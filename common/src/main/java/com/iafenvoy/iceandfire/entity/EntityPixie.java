package com.iafenvoy.iceandfire.entity;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.entity.ai.*;
import com.iafenvoy.iceandfire.entity.block.BlockEntityPixieHouse;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafParticles;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;

@SuppressWarnings("ALL")
public class EntityPixie extends TameableEntity {
    public static final float[][] PARTICLE_RGB = new float[][]{new float[]{1F, 0.752F, 0.792F}, new float[]{0.831F, 0.662F, 1F}, new float[]{0.513F, 0.843F, 1F}, new float[]{0.654F, 0.909F, 0.615F}, new float[]{0.996F, 0.788F, 0.407F}};
    public static final int STEAL_COOLDOWN = 3000;
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(EntityPixie.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> COMMAND = DataTracker.registerData(EntityPixie.class, TrackedDataHandlerRegistry.INTEGER);
    public final StatusEffect[] positivePotions = new StatusEffect[]{StatusEffects.STRENGTH, StatusEffects.JUMP_BOOST, StatusEffects.SPEED, StatusEffects.LUCK, StatusEffects.HASTE};
    public final StatusEffect[] negativePotions = new StatusEffect[]{StatusEffects.WEAKNESS, StatusEffects.NAUSEA, StatusEffects.SLOWNESS, StatusEffects.UNLUCK, StatusEffects.MINING_FATIGUE};
    public boolean slowSpeed = false;
    public int ticksUntilHouseAI;
    public int ticksHeldItemFor;
    public int stealCooldown = 0;
    private BlockPos housePos;
    private boolean isSitting;

    public EntityPixie(EntityType<? extends EntityPixie> type, World worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.experiencePoints = 3;
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0F);
    }

    public static BlockPos getPositionRelativetoGround(Entity entity, World world, double x, double z, Random rand) {
        BlockPos pos = BlockPos.ofFloored(x, entity.getBlockY(), z);
        for (int yDown = 0; yDown < 3; yDown++) {
            if (!world.isAir(pos.down(yDown))) {
                return pos.up(yDown);
            }
        }
        return pos;
    }

    public static BlockPos findAHouse(Entity entity, World world) {
        for (int xSearch = -10; xSearch < 10; xSearch++) {
            for (int ySearch = -10; ySearch < 10; ySearch++) {
                for (int zSearch = -10; zSearch < 10; zSearch++) {
                    if (world.getBlockEntity(entity.getBlockPos().add(xSearch, ySearch, zSearch)) != null && world.getBlockEntity(entity.getBlockPos().add(xSearch, ySearch, zSearch)) instanceof BlockEntityPixieHouse house) {
                        if (!house.hasPixie) {
                            return entity.getBlockPos().add(xSearch, ySearch, zSearch);
                        }
                    }
                }
            }
        }
        return entity.getBlockPos();
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10D)
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D);
    }

    public boolean isPixieSitting() {
        if (this.getWorld().isClient) {
            boolean isSitting = (this.dataTracker.get(TAMEABLE_FLAGS) & 1) != 0;
            this.isSitting = isSitting;
            this.setSitting(isSitting);
            return isSitting;
        }
        return this.isSitting;
    }

    public void setPixieSitting(boolean sitting) {
        if (!this.getWorld().isClient) {
            this.isSitting = sitting;
            this.setInSittingPose(sitting);
        }
        byte b0 = this.dataTracker.get(TAMEABLE_FLAGS);
        if (sitting) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b0 | 1));
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b0 & -2));
        }
    }

    @Override
    public boolean isSitting() {
        return this.isPixieSitting();
    }

    @Override
    public int getXpToDrop() {
        return 3;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient && this.getRandom().nextInt(3) == 0 && !this.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
            this.dropStack(this.getStackInHand(Hand.MAIN_HAND), 0);
            this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            this.stealCooldown = STEAL_COOLDOWN;
            return true;
        }
        if (this.isOwnerClose() && ((source.getAttacker() != null && source == this.getWorld().getDamageSources().fallingBlock(source.getAttacker())) || source == this.getWorld().getDamageSources().inWall() || this.getOwner() != null && source.getAttacker() == this.getOwner())) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        boolean invulnerable = super.isInvulnerableTo(source);
        if (!invulnerable) {
            Entity owner = this.getOwner();
            if (owner != null && source.getAttacker() == owner) {
                return true;
            }
        }
        return invulnerable;
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (!this.getWorld().isClient && !this.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
            this.dropStack(this.getStackInHand(Hand.MAIN_HAND), 0);
            this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
        super.onDeath(cause);
        //if (cause.getTrueSource() instanceof PlayerEntity) {
        //	((PlayerEntity) cause.getTrueSource()).addStat(ModAchievements.killPixie);
        //}
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(COLOR, 0);
        this.dataTracker.startTracking(COMMAND, 0);
    }

    @Override
    protected void pushAway(Entity entityIn) {
        if (this.getOwner() != entityIn) {
            entityIn.pushAwayFrom(this);
        }
    }

    @Override
    protected void fall(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.isOwner(player)) {

            if (player.getStackInHand(hand).isIn(IafItemTags.HEAL_PIXIE) && this.getHealth() < this.getMaxHealth()) {
                this.heal(5);
                player.getStackInHand(hand).decrement(1);
                this.playSound(IafSounds.PIXIE_TAUNT.get(), 1F, 1F);
                return ActionResult.SUCCESS;
            } else {

                // make pixie sit via a check in livingTick() like Hippogryphs work
                this.setCommand(this.getCommand() + 1);
                if (this.getCommand() > 1) {
                    this.setCommand(0);
                }

                return ActionResult.SUCCESS;
            }
        } else if (player.getStackInHand(hand).getItem() == IafBlocks.JAR_EMPTY.get().asItem() && !this.isTamed()) {
            if (!player.isCreative()) {
                player.getStackInHand(hand).decrement(1);
            }
            Block jar = switch (this.getColor()) {
                case 0 -> IafBlocks.JAR_PIXIE_0.get();
                case 1 -> IafBlocks.JAR_PIXIE_1.get();
                case 2 -> IafBlocks.JAR_PIXIE_2.get();
                case 3 -> IafBlocks.JAR_PIXIE_3.get();
                case 4 -> IafBlocks.JAR_PIXIE_4.get();
                default -> Blocks.AIR;
            };
            ItemStack stack = new ItemStack(jar, 1);
            if (!this.getWorld().isClient) {
                if (!this.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
                    this.dropStack(this.getStackInHand(Hand.MAIN_HAND), 0.0F);
                    this.stealCooldown = STEAL_COOLDOWN;
                }

                this.dropStack(stack, 0.0F);
            }
            //player.addStat(ModAchievements.jarPixie);
            this.remove(RemovalReason.DISCARDED);
        }
        return super.interactMob(player, hand);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new PixieAIFollowOwner(this, 1.0D, 2.0F, 4.0F));
        this.goalSelector.add(2, new PixieAIPickupItem<>(this, false));
        this.goalSelector.add(2, new PixieAIFlee<>(this, PlayerEntity.class, 10, (Predicate<PlayerEntity>) entity -> true));
        this.goalSelector.add(2, new PixieAISteal(this, 1.0D));
        this.goalSelector.add(3, new PixieAIMoveRandom(this));
        this.goalSelector.add(4, new PixieAIEnterHouse(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        spawnDataIn = super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setColor(this.random.nextInt(5));
        this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);

        if (dataTag != null) {
            System.out.println("EntityPixie spawned with dataTag: " + dataTag);
        }

        return spawnDataIn;
    }

    private boolean isBeyondHeight() {
        if (this.getY() > this.getWorld().getTopY()) {
            return true;
        }
        BlockPos height = this.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this.getBlockPos());
        int maxY = 20 + height.getY();
        return this.getY() > maxY;
    }

    public int getCommand() {
        return this.dataTracker.get(COMMAND);
    }

    public void setCommand(int command) {
        this.dataTracker.set(COMMAND, command);
        this.setPixieSitting(command == 1);
    }


    @Override
    public void tickMovement() {
        super.tickMovement();

        if (!this.getWorld().isClient) {

            // NOTE: This code was taken from EntityHippogryph basically same idea
            if (this.isPixieSitting() && this.getCommand() != 1) {
                this.setPixieSitting(false);
            }
            if (!this.isPixieSitting() && this.getCommand() == 1) {
                this.setPixieSitting(true);
            }
            if (this.isPixieSitting()) {
                this.getNavigation().stop();
            }
        }

        if (this.stealCooldown > 0) {
            this.stealCooldown--;
        }
        if (!this.getMainHandStack().isEmpty() && !this.isTamed()) {
            this.ticksHeldItemFor++;
        } else {
            this.ticksHeldItemFor = 0;
        }


        if (!this.isPixieSitting() && !this.isBeyondHeight()) {
            this.setVelocity(this.getVelocity().add(0, 0.08, 0));
        }
        if (this.getWorld().isClient) {
            this.getWorld().addParticle(IafParticles.PIXIE_DUST.get(), this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2F) - (double) this.getWidth(), this.getY() + (double) (this.random.nextFloat() * this.getHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2F) - (double) this.getWidth(), PARTICLE_RGB[this.getColor()][0], PARTICLE_RGB[this.getColor()][1], PARTICLE_RGB[this.getColor()][2]);
        }
        if (this.ticksUntilHouseAI > 0) {
            this.ticksUntilHouseAI--;
        }
        if (!this.getWorld().isClient) {
            if (this.housePos != null && this.squaredDistanceTo(Vec3d.ofCenter(this.housePos)) < 1.5F && this.getWorld().getBlockEntity(this.housePos) != null && this.getWorld().getBlockEntity(this.housePos) instanceof BlockEntityPixieHouse house) {
                if (house.hasPixie) {
                    this.housePos = null;
                } else {
                    house.hasPixie = true;
                    house.pixieType = this.getColor();
                    house.pixieItems.set(0, this.getStackInHand(Hand.MAIN_HAND).copy());
                    this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    house.tamedPixie = this.isTamed();
                    house.pixieOwnerUUID = this.getOwnerUuid();
                    com.iafenvoy.iceandfire.entity.block.BlockEntitySync.changed(house);
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
        if (this.getOwner() != null && this.isOwnerClose() && this.age % 80 == 0) {
            this.getOwner().addStatusEffect(new StatusEffectInstance(this.positivePotions[this.getColor()], 100, 0, false, false));
        }
        //PlayerEntity player = world.getClosestPlayerToEntity(this, 25);
        //if (player != null) {
        //	player.addStat(ModAchievements.findPixie);
        //}
    }

    public int getColor() {
        return MathHelper.clamp(this.getDataTracker().get(COLOR), 0, 4);
    }

    public void setColor(int color) {
        this.getDataTracker().set(COLOR, color);
    }


    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        this.setColor(compound.getInt("Color"));

        this.stealCooldown = compound.getInt("StealCooldown");
        this.ticksHeldItemFor = compound.getInt("HoldingTicks");

        this.setPixieSitting(compound.getBoolean("PixieSitting"));
        this.setCommand(compound.getInt("Command"));

        super.readCustomDataFromNbt(compound);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        compound.putInt("Color", this.getColor());
        compound.putInt("Command", this.getCommand());
        compound.putInt("StealCooldown", this.stealCooldown);
        compound.putInt("HoldingTicks", this.ticksHeldItemFor);
        compound.putBoolean("PixieSitting", this.isPixieSitting());
        super.writeCustomDataToNbt(compound);
    }

    @Override
    public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity ageable) {
        return null;
    }

    public void setHousePosition(BlockPos blockPos) {
        this.housePos = blockPos;
    }

    public BlockPos getHousePos() {
        return this.housePos;
    }

    public boolean isOwnerClose() {
        return this.isTamed() && this.getOwner() != null && this.squaredDistanceTo(this.getOwner()) < 100;
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
    public boolean isTeammate(Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TameableEntity) {
                return ((TameableEntity) entityIn).isOwner(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isTeammate(entityIn);
            }
        }

        return super.isTeammate(entityIn);
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    class AIMoveControl extends MoveControl {
        public AIMoveControl(EntityPixie pixie) {
            super(pixie);
        }

        @Override
        public void tick() {
            float speedMod = 1;
            if (EntityPixie.this.slowSpeed) {
                speedMod = 2F;
            }
            if (this.state == State.MOVE_TO) {
                if (EntityPixie.this.horizontalCollision) {
                    EntityPixie.this.setYaw(this.entity.getYaw() + 180.0F);
                    speedMod = 0.1F;
                    BlockPos target = EntityPixie.getPositionRelativetoGround(EntityPixie.this, EntityPixie.this.getWorld(), EntityPixie.this.getX() + EntityPixie.this.random.nextInt(15) - 7, EntityPixie.this.getZ() + EntityPixie.this.random.nextInt(15) - 7, EntityPixie.this.random);
                    this.targetX = target.getX();
                    this.targetY = target.getY();
                    this.targetZ = target.getZ();
                }
                double d0 = this.targetX - EntityPixie.this.getX();
                double d1 = this.targetY - EntityPixie.this.getY();
                double d2 = this.targetZ - EntityPixie.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = Math.sqrt(d3);

                if (d3 < EntityPixie.this.getBoundingBox().getAverageSideLength()) {
                    this.state = State.WAIT;
                    EntityPixie.this.setVelocity(EntityPixie.this.getVelocity().multiply(0.5D, 0.5D, 0.5D));
                } else {
                    EntityPixie.this.setVelocity(EntityPixie.this.getVelocity().add(d0 / d3 * 0.05D * this.speed * speedMod, d1 / d3 * 0.05D * this.speed * speedMod, d2 / d3 * 0.05D * this.speed * speedMod));

                    if (EntityPixie.this.getTarget() == null) {
                        EntityPixie.this.setYaw(-((float) MathHelper.atan2(EntityPixie.this.getVelocity().x, EntityPixie.this.getVelocity().z)) * (180F / (float) Math.PI));
                        EntityPixie.this.bodyYaw = EntityPixie.this.getYaw();
                    } else {
                        double d4 = EntityPixie.this.getTarget().getX() - EntityPixie.this.getX();
                        double d5 = EntityPixie.this.getTarget().getZ() - EntityPixie.this.getZ();
                        EntityPixie.this.setYaw(-((float) MathHelper.atan2(d4, d5)) * (180F / (float) Math.PI));
                        EntityPixie.this.bodyYaw = EntityPixie.this.getYaw();
                    }
                }
            }
        }
    }


}
