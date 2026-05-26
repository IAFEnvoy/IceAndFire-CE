package com.iafenvoy.iceandfire.entity;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.entity.ai.*;
import com.iafenvoy.iceandfire.item.block.entity.PixieHouseBlockEntity;
import com.iafenvoy.iceandfire.network.payload.UpdatePixieHouseS2CPayload;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafParticles;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

@SuppressWarnings("ALL")
public class PixieEntity extends TamableAnimal {
    public static final float[][] PARTICLE_RGB = new float[][]{new float[]{1F, 0.752F, 0.792F}, new float[]{0.831F, 0.662F, 1F}, new float[]{0.513F, 0.843F, 1F}, new float[]{0.654F, 0.909F, 0.615F}, new float[]{0.996F, 0.788F, 0.407F}};
    public static final int STEAL_COOLDOWN = 3000;
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(PixieEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(PixieEntity.class, EntityDataSerializers.INT);
    public final Holder<MobEffect>[] positivePotions = new Holder[]{MobEffects.DAMAGE_BOOST, MobEffects.JUMP, MobEffects.MOVEMENT_SPEED, MobEffects.LUCK, MobEffects.DIG_SPEED};
    public final Holder<MobEffect>[] negativePotions = new Holder[]{MobEffects.WEAKNESS, MobEffects.CONFUSION, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.UNLUCK, MobEffects.DIG_SLOWDOWN};
    public boolean slowSpeed = false;
    public int ticksUntilHouseAI;
    public int ticksHeldItemFor;
    public int stealCooldown = 0;
    private BlockPos housePos;
    private boolean isSitting;

    public PixieEntity(EntityType<? extends PixieEntity> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.xpReward = 3;
        this.setDropChance(EquipmentSlot.MAINHAND, 0F);
    }

    public static BlockPos getPositionRelativetoGround(Entity entity, Level world, double x, double z, RandomSource rand) {
        BlockPos pos = BlockPos.containing(x, entity.getBlockY(), z);
        for (int yDown = 0; yDown < 3; yDown++) {
            if (!world.isEmptyBlock(pos.below(yDown))) {
                return pos.above(yDown);
            }
        }
        return pos;
    }

    public static BlockPos findAHouse(Entity entity, Level world) {
        for (int xSearch = -10; xSearch < 10; xSearch++) {
            for (int ySearch = -10; ySearch < 10; ySearch++) {
                for (int zSearch = -10; zSearch < 10; zSearch++) {
                    if (world.getBlockEntity(entity.blockPosition().offset(xSearch, ySearch, zSearch)) != null && world.getBlockEntity(entity.blockPosition().offset(xSearch, ySearch, zSearch)) instanceof PixieHouseBlockEntity house) {
                        if (!house.hasPixie) {
                            return entity.blockPosition().offset(xSearch, ySearch, zSearch);
                        }
                    }
                }
            }
        }
        return entity.blockPosition();
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, 10D)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public boolean isPixieSitting() {
        if (this.level().isClientSide) {
            boolean isSitting = (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
            this.isSitting = isSitting;
            this.setOrderedToSit(isSitting);
            return isSitting;
        }
        return this.isSitting;
    }

    public void setPixieSitting(boolean sitting) {
        if (!this.level().isClientSide) {
            this.isSitting = sitting;
            this.setInSittingPose(sitting);
        }
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (sitting) {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 | 1));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 & -2));
        }
    }

    @Override
    public boolean isOrderedToSit() {
        return this.isPixieSitting();
    }

    @Override
    public int getBaseExperienceReward() {
        return 3;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.SUGAR);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && this.getRandom().nextInt(3) == 0 && !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0);
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            this.stealCooldown = STEAL_COOLDOWN;
            return true;
        }
        if (this.isOwnerClose() && ((source.getEntity() != null && source == this.level().damageSources().fallingBlock(source.getEntity())) || source == this.level().damageSources().inWall() || this.getOwner() != null && source.getEntity() == this.getOwner())) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        boolean invulnerable = super.isInvulnerableTo(source);
        if (!invulnerable) {
            Entity owner = this.getOwner();
            if (owner != null && source.getEntity() == owner) {
                return true;
            }
        }
        return invulnerable;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide && !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0);
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        super.die(cause);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COLOR, 0);
        builder.define(COMMAND, 0);
    }

    @Override
    protected void doPush(Entity entityIn) {
        if (this.getOwner() != entityIn) {
            entityIn.push(this);
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isOwnedBy(player)) {
            if (player.getItemInHand(hand).is(IafItemTags.HEAL_PIXIE) && this.getHealth() < this.getMaxHealth()) {
                this.heal(5);
                player.getItemInHand(hand).shrink(1);
                this.playSound(IafSounds.PIXIE_TAUNT.get(), 1F, 1F);
                return InteractionResult.SUCCESS;
            } else {
                this.setCommand(this.getCommand() + 1);
                if (this.getCommand() > 1) this.setCommand(0);
                return InteractionResult.SUCCESS;
            }
        } else if (player.getItemInHand(hand).getItem() == IafBlocks.JAR_EMPTY.get().asItem() && !this.isTame()) {
            if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
            Block jar = switch (this.getColor()) {
                case 0 -> IafBlocks.JAR_PIXIE_0.get();
                case 1 -> IafBlocks.JAR_PIXIE_1.get();
                case 2 -> IafBlocks.JAR_PIXIE_2.get();
                case 3 -> IafBlocks.JAR_PIXIE_3.get();
                case 4 -> IafBlocks.JAR_PIXIE_4.get();
                default -> Blocks.AIR;
            };
            ItemStack stack = new ItemStack(jar, 1);
            if (!this.level().isClientSide) {
                if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                    this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
                    this.stealCooldown = STEAL_COOLDOWN;
                }

                this.spawnAtLocation(stack, 0.0F);
            }
            this.remove(RemovalReason.DISCARDED);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PixieAIFollowOwnerGoal(this, 1.0D, 2.0F, 4.0F));
        this.goalSelector.addGoal(2, new PixieAIPickupItemGoal<>(this, false));
        this.goalSelector.addGoal(2, new PixieAIFleeGoal<>(this, Player.class, 10, (Predicate<Player>) entity -> true));
        this.goalSelector.addGoal(2, new PixieAIStealGoal(this));
        this.goalSelector.addGoal(3, new PixieAIMoveRandomGoal(this));
        this.goalSelector.addGoal(4, new PixieAIEnterHouseGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, SpawnGroupData spawnDataIn) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
        this.setColor(this.random.nextInt(5));
        this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        return spawnDataIn;
    }

    private boolean isBeyondHeight() {
        if (this.getY() > this.level().getMaxBuildHeight()) return true;
        BlockPos height = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.blockPosition());
        int maxY = 20 + height.getY();
        return this.getY() > maxY;
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, command);
        this.setPixieSitting(command == 1);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            // NOTE: This code was taken from HippogryphEntity basically same idea
            if (this.isPixieSitting() && this.getCommand() != 1)
                this.setPixieSitting(false);
            if (!this.isPixieSitting() && this.getCommand() == 1)
                this.setPixieSitting(true);
            if (this.isPixieSitting())
                this.getNavigation().stop();
        }
        if (this.stealCooldown > 0)
            this.stealCooldown--;
        if (!this.getMainHandItem().isEmpty() && !this.isTame())
            this.ticksHeldItemFor++;
        else
            this.ticksHeldItemFor = 0;

        if (!this.isPixieSitting() && !this.isBeyondHeight())
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.08, 0));
        if (this.level().isClientSide)
            this.level().addParticle(IafParticles.PIXIE_DUST.get(), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2F) - (double) this.getBbWidth(), this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2F) - (double) this.getBbWidth(), PARTICLE_RGB[this.getColor()][0], PARTICLE_RGB[this.getColor()][1], PARTICLE_RGB[this.getColor()][2]);
        if (this.ticksUntilHouseAI > 0)
            this.ticksUntilHouseAI--;
        if (!this.level().isClientSide) {
            if (this.housePos != null && this.distanceToSqr(Vec3.atCenterOf(this.housePos)) < 1.5F && this.level().getBlockEntity(this.housePos) != null && this.level().getBlockEntity(this.housePos) instanceof PixieHouseBlockEntity house) {
                if (house.hasPixie) this.housePos = null;
                else {
                    house.hasPixie = true;
                    house.pixieType = this.getColor();
                    house.pixieItems.set(0, this.getItemInHand(InteractionHand.MAIN_HAND));
                    house.tamedPixie = this.isTame();
                    house.pixieOwnerUUID = this.getOwnerUUID();
                    PacketDistributor.sendToAllPlayers(new UpdatePixieHouseS2CPayload(this.housePos, true, this.getColor()));
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
        if (this.getOwner() != null && this.isOwnerClose() && this.tickCount % 80 == 0) {
            this.getOwner().addEffect(new MobEffectInstance(this.positivePotions[this.getColor()], 100, 0, false, false));
        }
    }

    public int getColor() {
        return Mth.clamp(this.getEntityData().get(COLOR), 0, 4);
    }

    public void setColor(int color) {
        this.getEntityData().set(COLOR, color);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setColor(compound.getInt("Color"));

        this.stealCooldown = compound.getInt("StealCooldown");
        this.ticksHeldItemFor = compound.getInt("HoldingTicks");

        this.setPixieSitting(compound.getBoolean("PixieSitting"));
        this.setCommand(compound.getInt("Command"));

        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Color", this.getColor());
        compound.putInt("Command", this.getCommand());
        compound.putInt("StealCooldown", this.stealCooldown);
        compound.putInt("HoldingTicks", this.ticksHeldItemFor);
        compound.putBoolean("PixieSitting", this.isPixieSitting());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        return null;
    }

    public void setHousePosition(BlockPos blockPos) {
        this.housePos = blockPos;
    }

    public BlockPos getHousePos() {
        return this.housePos;
    }

    public boolean isOwnerClose() {
        return this.isTame() && this.getOwner() != null && this.distanceToSqr(this.getOwner()) < 100;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return IafSounds.PIXIE_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return IafSounds.PIXIE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return IafSounds.PIXIE_DIE.get();
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity)
                return true;
            if (entityIn instanceof TamableAnimal tameable)
                return tameable.isOwnedBy(livingentity);
            if (livingentity != null)
                return livingentity.isAlliedTo(entityIn);
        }
        return super.isAlliedTo(entityIn);
    }

    class AIMoveControl extends MoveControl {
        public AIMoveControl(PixieEntity pixie) {
            super(pixie);
        }

        @Override
        public void tick() {
            float speedMod = 1;
            if (PixieEntity.this.slowSpeed) speedMod = 2F;
            if (this.operation == Operation.MOVE_TO) {
                if (PixieEntity.this.horizontalCollision) {
                    PixieEntity.this.setYRot(this.mob.getYRot() + 180.0F);
                    speedMod = 0.1F;
                    BlockPos target = PixieEntity.getPositionRelativetoGround(PixieEntity.this, PixieEntity.this.level(), PixieEntity.this.getX() + PixieEntity.this.random.nextInt(15) - 7, PixieEntity.this.getZ() + PixieEntity.this.random.nextInt(15) - 7, PixieEntity.this.random);
                    this.wantedX = target.getX();
                    this.wantedY = target.getY();
                    this.wantedZ = target.getZ();
                }
                double d0 = this.wantedX - PixieEntity.this.getX();
                double d1 = this.wantedY - PixieEntity.this.getY();
                double d2 = this.wantedZ - PixieEntity.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = Math.sqrt(d3);

                if (d3 < PixieEntity.this.getBoundingBox().getSize()) {
                    this.operation = Operation.WAIT;
                    PixieEntity.this.setDeltaMovement(PixieEntity.this.getDeltaMovement().multiply(0.5D, 0.5D, 0.5D));
                } else {
                    PixieEntity.this.setDeltaMovement(PixieEntity.this.getDeltaMovement().add(d0 / d3 * 0.05D * this.speedModifier * speedMod, d1 / d3 * 0.05D * this.speedModifier * speedMod, d2 / d3 * 0.05D * this.speedModifier * speedMod));

                    if (PixieEntity.this.getTarget() == null) {
                        PixieEntity.this.setYRot(-((float) Mth.atan2(PixieEntity.this.getDeltaMovement().x, PixieEntity.this.getDeltaMovement().z)) * (180F / (float) Math.PI));
                        PixieEntity.this.yBodyRot = PixieEntity.this.getYRot();
                    } else {
                        double d4 = PixieEntity.this.getTarget().getX() - PixieEntity.this.getX();
                        double d5 = PixieEntity.this.getTarget().getZ() - PixieEntity.this.getZ();
                        PixieEntity.this.setYRot(-((float) Mth.atan2(d4, d5)) * (180F / (float) Math.PI));
                        PixieEntity.this.yBodyRot = PixieEntity.this.getYRot();
                    }
                }
            }
        }
    }


}