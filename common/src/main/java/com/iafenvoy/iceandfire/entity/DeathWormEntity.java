package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.ai.*;
import com.iafenvoy.iceandfire.entity.pathfinding.DeathWormLandNavigation;
import com.iafenvoy.iceandfire.entity.pathfinding.DeathWormSandNavigation;
import com.iafenvoy.iceandfire.entity.util.*;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.event.IafEvents;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.object.entity.collision.ICustomCollisions;
import com.iafenvoy.uranus.util.RandomHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;

@SuppressWarnings("ALL")
public class DeathWormEntity extends TameableEntity implements ISyncMount, ICustomCollisions, BlacklistedFromStatues, IAnimatedEntity, IVillagerFear, IAnimalFear, IGroundMount, IHasCustomizableAttributes, ICustomMoveController {
    public static final Identifier TAN_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/deathworm_tan");
    public static final Identifier WHITE_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/deathworm_white");
    public static final Identifier RED_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/deathworm_red");
    public static final Identifier TAN_GIANT_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/deathworm_tan_giant");
    public static final Identifier WHITE_GIANT_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/deathworm_white_giant");
    public static final Identifier RED_GIANT_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/deathworm_red_giant");
    public static final Animation ANIMATION_BITE = Animation.create(10);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(DeathWormEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SCALE = DataTracker.registerData(DeathWormEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> JUMP_TICKS = DataTracker.registerData(DeathWormEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> CONTROL_STATE = DataTracker.registerData(DeathWormEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> WORM_AGE = DataTracker.registerData(DeathWormEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<BlockPos> HOME = DataTracker.registerData(DeathWormEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private final LookControl lookHelper;
    public ChainBuffer tail_buffer;
    public float jumpProgress;
    public float prevJumpProgress;
    public DeathwormAITargetItemsGoal<?> targetItemsGoal;
    private int animationTick;
    private boolean willExplode = false;
    private int ticksTillExplosion = 60;
    private Animation currentAnimation;
    private final SlowPartEntity[] segments = new SlowPartEntity[7];
    private boolean isSandNavigator;
    private int growthCounter = 0;
    private PlayerEntity thrower;

    public DeathWormEntity(EntityType<DeathWormEntity> type, World worldIn) {
        super(type, worldIn);
        this.setPathfindingPenalty(PathNodeType.OPEN, 2.0f); // FIXME :: Death worms are trying to go upwards -> figure out why (or if this really helps)
        this.setPathfindingPenalty(PathNodeType.WATER, 4.0f);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 4.0f);
        this.lookHelper = new IAFLookControl(this);
        this.ignoreCameraFrustum = true;
        if (worldIn.isClient) {
            this.tail_buffer = new ChainBuffer();
        }
        this.switchNavigator(false);
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, IafCommonConfig.INSTANCE.deathworm.maxHealth.getValue())
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, IafCommonConfig.INSTANCE.deathworm.attackDamage.getValue())
                //FOLLOW RANGE
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, IafCommonConfig.INSTANCE.deathworm.targetSearchLength.getValue())
                //ARMOR
                .add(EntityAttributes.GENERIC_ARMOR, 3)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new EntityGroundAIRideGoal<>(this));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new DeathWormAIAttackGoal(this));
        this.goalSelector.add(3, new DeathWormAIJumpGoal(this, 12));
        this.goalSelector.add(4, new DeathWormAIFindSandTargetGoal(this, 10));
        this.goalSelector.add(5, new DeathWormAIGetInSandGoal(this, 1.0D));
        this.goalSelector.add(6, new DeathWormAIWanderGoal(this, 1));
        this.targetSelector.add(2, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(3, new AttackWithOwnerGoal(this));
        this.targetSelector.add(4, new RevengeGoal(this));
        this.targetSelector.add(4, this.targetItemsGoal = new DeathwormAITargetItemsGoal<>(this, false, false));
        this.targetSelector.add(5, new DeathWormAITargetGoal<>(this, LivingEntity.class, false, input -> {
            if (DeathWormEntity.this.isTamed()) {
                return input instanceof HostileEntity;
            } else if (input != null) {
                if (input.isTouchingWater() || !DragonUtils.isAlive(input) || DeathWormEntity.this.isOwner(input)) {
                    return false;
                }

                if (input instanceof PlayerEntity || input instanceof AnimalEntity) {
                    return true;
                }

                return IafCommonConfig.INSTANCE.deathworm.attackMonsters.getValue();
            }

            return false;
        }));
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.max(6, IafCommonConfig.INSTANCE.deathworm.maxHealth.getValue()));
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.max(1, IafCommonConfig.INSTANCE.deathworm.attackDamage.getValue()));
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(IafCommonConfig.INSTANCE.deathworm.targetSearchLength.getValue());
    }

    @Override
    public LookControl getLookControl() {
        return this.lookHelper;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    public boolean getCanSpawnHere() {
        int i = MathHelper.floor(this.getX());
        int j = MathHelper.floor(this.getBoundingBox().minY);
        int k = MathHelper.floor(this.getZ());
        BlockPos blockpos = new BlockPos(i, j, k);
        this.getWorld().getBlockState(blockpos.down()).isIn(BlockTags.SAND);
        return this.getWorld().getBlockState(blockpos.down()).isIn(BlockTags.SAND)
                && this.getWorld().getLightLevel(blockpos) > 8;
    }

    public void onUpdateParts() {
        if (this.isRemoved()) return;
        for (int i = 0; i < this.segments.length; i++) {
            if (this.segments[i] != null && !this.segments[i].isRemoved()) continue;
            this.segments[i] = new SlowPartEntity(this, (-0.8F - (i * 0.8F)), 0, 0, 0.7F, 0.7F, 1);
            this.segments[i].copyPositionAndRotation(this);
            this.segments[i].setParent(this);
            this.segments[i].updateScale(this.getScaleFactor());
            this.getWorld().spawnEntity(this.segments[i]);
        }
        for (MultipartPartEntity entity : this.segments)
            IafEntityUtil.updatePart(entity, this);
    }

    public void updateScale(float scale) {
        for (SlowPartEntity entity : this.segments)
            if (entity != null && !entity.isRemoved())
                entity.updateScale(scale);
    }

    @Override
    public int getXpToDrop() {
        return this.getScaleFactor() > 3 ? 20 : 10;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    private void clearSegments() {
        for (Entity entity : this.segments)
            if (entity != null && !entity.isRemoved())
                entity.remove(RemovalReason.DISCARDED);
    }

    public void setExplosive(boolean explosive, PlayerEntity thrower) {
        this.willExplode = true;
        this.ticksTillExplosion = 60;
        this.thrower = thrower;
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
        if (this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(ANIMATION_BITE);
            this.playSound(this.getScaleFactor() > 3 ? IafSounds.DEATHWORM_GIANT_ATTACK.get() : IafSounds.DEATHWORM_ATTACK.get(), 1, 1);
        }
        if (this.getRandom().nextInt(3) == 0 && this.getScaleFactor() > 1 && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            if (!IafEvents.ON_GRIEF_BREAK_BLOCK.invoker().onBreakBlock(this, entityIn.getX(), entityIn.getY(), entityIn.getZ())) {
                BlockLaunchExplosion explosion = new BlockLaunchExplosion(this.getWorld(), this, entityIn.getX(), entityIn.getY(), entityIn.getZ(), this.getScaleFactor());
                explosion.collectBlocksAndDamageEntities();
                explosion.affectWorld(true);
            }
        }
        return false;
    }

    @Override
    public void onDeath(DamageSource cause) {
        this.clearSegments();
        super.onDeath(cause);
    }

    @Override
    protected void fall(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected RegistryKey<LootTable> getLootTableId() {
        return switch (this.getVariant()) {
            case 0 -> RegistryKey.of(RegistryKeys.LOOT_TABLE, this.getScaleFactor() > 3 ? TAN_GIANT_LOOT : TAN_LOOT);
            case 1 -> RegistryKey.of(RegistryKeys.LOOT_TABLE, this.getScaleFactor() > 3 ? RED_GIANT_LOOT : RED_LOOT);
            case 2 ->
                    RegistryKey.of(RegistryKeys.LOOT_TABLE, this.getScaleFactor() > 3 ? WHITE_GIANT_LOOT : WHITE_LOOT);
            default -> null;
        };
    }

    @Override
    public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity ageable) {
        return null;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, 0);
        builder.add(SCALE, 1F);
        builder.add(CONTROL_STATE, (byte) 0);
        builder.add(WORM_AGE, 10);
        builder.add(HOME, BlockPos.ORIGIN);
        builder.add(JUMP_TICKS, 0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("GrowthCounter", this.growthCounter);
        compound.putFloat("Scale", this.getDeathwormScale());
        compound.putInt("WormAge", this.getWormAge());
        compound.putLong("WormHome", this.getWormHome().asLong());
        compound.putBoolean("WillExplode", this.willExplode);
        this.clearSegments();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setVariant(compound.getInt("Variant"));
        this.growthCounter = compound.getInt("GrowthCounter");
        this.setDeathWormScale(compound.getFloat("Scale"));
        this.setWormAge(compound.getInt("WormAge"));
        this.setWormHome(BlockPos.fromLong(compound.getLong("WormHome")));
        this.willExplode = compound.getBoolean("WillExplode");
        this.setConfigurableAttributes();
    }

    private void setStateField(int i, boolean newState) {
        byte prevState = this.dataTracker.get(CONTROL_STATE);
        if (newState) {
            this.dataTracker.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        } else {
            this.dataTracker.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
        }
    }

    @Override
    public byte getControlState() {
        return this.dataTracker.get(CONTROL_STATE);
    }

    @Override
    public void setControlState(byte state) {
        this.dataTracker.set(CONTROL_STATE, state);
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public int getWormJumping() {
        return this.dataTracker.get(JUMP_TICKS);
    }

    public void setWormJumping(int jump) {
        this.dataTracker.set(JUMP_TICKS, jump);
    }

    public BlockPos getWormHome() {
        return this.dataTracker.get(HOME);
    }

    public void setWormHome(BlockPos home) {
        if (home instanceof BlockPos) {
            this.dataTracker.set(HOME, home);
        }
    }

    public int getWormAge() {
        return Math.max(1, this.dataTracker.get(WORM_AGE));
    }

    public void setWormAge(int age) {
        this.dataTracker.set(WORM_AGE, age);
    }

    @Override
    public float getScaleFactor() {
        return Math.min(this.getDeathwormScale() * (this.getWormAge() / 5F), 7F);
    }

    public float getDeathwormScale() {
        float scale = this.dataTracker.get(SCALE);
        if (scale == 0) {
            scale = (float) RandomHelper.nextDouble(1, 7);
            this.setDeathWormScale(scale);
        }
        return scale;
    }

    public void setDeathWormScale(float scale) {
        this.dataTracker.set(SCALE, scale);
        this.updateAttributes();
        this.updateScale(scale * (this.getWormAge() / 5F));
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn) {
        spawnDataIn = super.initialize(worldIn, difficultyIn, reason, spawnDataIn);
        this.setVariant(this.getRandom().nextInt(3));
        float size = 0.25F + (float) (Math.random() * 0.35F);
        this.setDeathWormScale(this.getRandom().nextInt(20) == 0 ? size * 4 : size);
        return spawnDataIn;
    }

    @Override
    public void updatePassengerPosition(Entity passenger, PositionUpdater callback) {
        super.updatePassengerPosition(passenger, callback);
        if (this.hasPassenger(passenger)) {
            this.setBodyYaw(passenger.getYaw());
            float radius = -0.5F * this.getScaleFactor();
            float angle = (0.01745329251F * this.bodyYaw);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            passenger.setPosition(this.getX() + extraX, this.getY() + this.getStandingEyeHeight() - 0.55F, this.getZ() + extraZ);
        }
    }

    @Override
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengerList())
            if (passenger instanceof PlayerEntity player)
                return player;
        return null;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWormAge() > 4 && player.getVehicle() == null && (player.getMainHandStack().isOf(Items.FISHING_ROD) || player.getOffHandStack().isOf(Items.FISHING_ROD))) {
            player.startRiding(this);
            return ActionResult.success(this.getWorld().isClient);
        }
        return super.interactMob(player, hand);
    }

    private void switchNavigator(boolean inSand) {
        if (inSand) {
            this.moveControl = new SandMoveHelper();
            this.navigation = new DeathWormSandNavigation(this, this.getWorld());
            this.isSandNavigator = true;
        } else {
            this.moveControl = new MoveControl(this);
            this.navigation = new DeathWormLandNavigation(this, this.getWorld());
            this.isSandNavigator = false;
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.IN_WALL) || source.isOf(DamageTypes.FALLING_BLOCK)) return false;
        if (this.hasPassengers() && source.getAttacker() != null && this.getControllingPassenger() != null && source.getAttacker() == this.getControllingPassenger())
            return false;
        return super.damage(source, amount);
    }

    @Override
    public void move(MovementType typeIn, Vec3d pos) {
        super.move(typeIn, pos);
    }

    @Override
    public boolean isInsideWall() {
        return !this.isInSand() && super.isInsideWall();
    }

    @Override
    protected void pushOutOfBlocks(double x, double y, double z) {
        Vec3d blockpos = new Vec3d(x, y, z);
        Vec3i vec3i = new Vec3i((int) Math.round(blockpos.getX()), (int) Math.round(blockpos.getY()), (int) Math.round(blockpos.getZ()));
        Vec3d vector3d = new Vec3d(x - blockpos.getX(), y - blockpos.getY(), z - blockpos.getZ());
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        Direction direction = Direction.UP;
        double d0 = Double.MAX_VALUE;

        for (Direction dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
            blockpos$mutable.set(vec3i, dir);
            if (!this.getWorld().getBlockState(blockpos$mutable).isFullCube(this.getWorld(), blockpos$mutable)
                    || this.getWorld().getBlockState(blockpos$mutable).isIn(BlockTags.SAND)) {
                double d1 = vector3d.getComponentAlongAxis(dir.getAxis());
                double d2 = dir.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - d1 : d1;
                if (d2 < d0) {
                    d0 = d2;
                    direction = dir;
                }
            }
        }

        float f = this.random.nextFloat() * 0.2F + 0.1F;
        float f1 = (float) direction.getDirection().offset();
        Vec3d vector3d1 = this.getVelocity().multiply(0.75D);
        if (direction.getAxis() == Direction.Axis.X) {
            this.setVelocity(f1 * f, vector3d1.y, vector3d1.z);
        } else if (direction.getAxis() == Direction.Axis.Y) {
            this.setVelocity(vector3d1.x, f1 * f, vector3d1.z);
        } else if (direction.getAxis() == Direction.Axis.Z) {
            this.setVelocity(vector3d1.x, vector3d1.y, f1 * f);
        }
    }

    private void updateAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(Math.min(0.2D, 0.15D * this.getScaleFactor()));
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.max(1, IafCommonConfig.INSTANCE.deathworm.attackDamage.getValue() * this.getScaleFactor()));
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.max(6, IafCommonConfig.INSTANCE.deathworm.maxHealth.getValue() * this.getScaleFactor()));
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(IafCommonConfig.INSTANCE.deathworm.targetSearchLength.getValue());
        this.setHealth((float) this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).getBaseValue());
    }

    @Override
    public boolean onKilledOther(ServerWorld world, LivingEntity entity) {
        if (this.isTamed()) {
            this.heal(14);
            return false;
        }
        return true;
    }

    @Override
    public boolean isTeammate(Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) return true;
            if (entityIn instanceof TameableEntity tameable) return tameable.isOwner(livingentity);
            if (livingentity != null) return livingentity.isTeammate(entityIn);
        }
        return super.isTeammate(entityIn);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.prevJumpProgress = this.jumpProgress;
        if (this.getWormJumping() > 0 && this.jumpProgress < 5F) this.jumpProgress++;
        if (this.getWormJumping() == 0 && this.jumpProgress > 0F) this.jumpProgress--;
        if (this.isInSand() && this.horizontalCollision) this.setVelocity(this.getVelocity().add(0, 0.05, 0));
        if (this.getWormJumping() > 0) {
            float f2 = (float) -((float) this.getVelocity().y * (double) (180F / (float) Math.PI));
            this.setPitch(f2);
            if (this.isInSand() || this.isOnGround()) this.setWormJumping(this.getWormJumping() - 1);
        }
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof PlayerEntity)
            this.setTarget(null);
        if (this.getTarget() != null && (!this.getTarget().isAlive() || !DragonUtils.isAlive(this.getTarget())))
            this.setTarget(null);
        if (this.willExplode) {
            if (this.ticksTillExplosion == 0) {
                if (!IafEvents.ON_GRIEF_BREAK_BLOCK.invoker().onBreakBlock(this, this.getX(), this.getY(), this.getZ()))
                    this.getWorld().createExplosion(this.thrower, this.getX(), this.getY(), this.getZ(), 2.5F * this.getScaleFactor(), false, World.ExplosionSourceType.MOB);
                this.thrower = null;
            } else this.ticksTillExplosion--;
        }
        if (this.isInSandStrict()) this.setVelocity(this.getVelocity().add(0, 0.08D, 0));
        if (this.growthCounter > 1000 && this.getWormAge() < 5) {
            this.growthCounter = 0;
            this.setWormAge(Math.min(5, this.getWormAge() + 1));
            this.clearSegments();
            this.heal(15);
            this.setDeathWormScale(this.getDeathwormScale());
            if (this.getWorld().isClient)
                for (int i = 0; i < 10 * this.getScaleFactor(); i++)
                    this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getSurface((int) Math.floor(this.getX()), (int) Math.floor(this.getY()), (int) Math.floor(this.getZ())) + 0.5F, this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
        }
        if (this.getWormAge() < 5) this.growthCounter++;
        if (this.getControllingPassenger() != null && this.getTarget() != null) {
            this.getNavigation().stop();
            this.setTarget(null);
        }
        //this.faceEntity(this.getAttackTarget(), 10.0F, 10.0F);
           /* if (dist >= 4.0D * getRenderScale() && dist <= 16.0D * getRenderScale() && (this.isInSand() || this.onGround)) {
                this.setWormJumping(true);
                double d0 = this.getAttackTarget().getPosX() - this.getPosX();
                double d1 = this.getAttackTarget().getPosZ() - this.getPosZ();
                float leap = MathHelper.sqrt(d0 * d0 + d1 * d1);
                if ((double) leap >= 1.0E-4D) {
                    this.setMotion(this.getMotion().add(d0 / (double) leap * 0.5D, 0.15F, d1 / (double) leap * 0.5D));
                }
                this.setAnimation(ANIMATION_BITE);
            }*/
        if (this.getTarget() != null && this.distanceTo(this.getTarget()) < Math.min(4, 4D * this.getScaleFactor()) && this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 5) {
            float f = (float) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue();
            this.getTarget().damage(this.getWorld().getDamageSources().mobAttack(this), f);
            this.setVelocity(this.getVelocity().add(0, -0.4F, 0));
        }

    }

    public int getWormBrightness(boolean sky) {
        Vec3d vec3 = this.getCameraPosVec(1.0F);
        BlockPos eyePos = BlockPos.ofFloored(vec3);
        while (eyePos.getY() < 256 && !this.getWorld().isAir(eyePos)) {
            eyePos = eyePos.up();
        }
        return this.getWorld().getLightLevel(sky ? LightType.SKY : LightType.BLOCK, eyePos.up());
    }

    public int getSurface(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        while (!this.getWorld().isAir(pos)) {
            pos = pos.up();
        }
        return pos.getY();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.getScaleFactor() > 3 ? IafSounds.DEATHWORM_GIANT_IDLE.get() : IafSounds.DEATHWORM_IDLE.get();
    }


    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.getScaleFactor() > 3 ? IafSounds.DEATHWORM_GIANT_HURT.get() : IafSounds.DEATHWORM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.getScaleFactor() > 3 ? IafSounds.DEATHWORM_GIANT_DIE.get() : IafSounds.DEATHWORM_DIE.get();
    }

    @Override
    public void tick() {
        super.tick();
        this.calculateDimensions();
        this.onUpdateParts();
        if (this.age == 1)
            this.updateScale(this.getScaleFactor());
        if (this.attack() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity) {
            LivingEntity target = DragonUtils.riderLookingAtEntity(this, this.getControllingPassenger(), 3);
            if (this.getAnimation() != ANIMATION_BITE) {
                this.setAnimation(ANIMATION_BITE);
                this.playSound(this.getScaleFactor() > 3 ? IafSounds.DEATHWORM_GIANT_ATTACK.get() : IafSounds.DEATHWORM_ATTACK.get(), 1, 1);
                if (this.getRandom().nextInt(3) == 0 && this.getScaleFactor() > 1) {
                    float radius = 1.5F * this.getScaleFactor();
                    float angle = (0.01745329251F * this.bodyYaw);
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraZ = radius * MathHelper.cos(angle);
                    BlockLaunchExplosion explosion = new BlockLaunchExplosion(this.getWorld(), this, this.getX() + extraX, this.getY() - this.getStandingEyeHeight(), this.getZ() + extraZ, this.getScaleFactor() * 0.75F);
                    explosion.collectBlocksAndDamageEntities();
                    explosion.affectWorld(true);
                }
            }
            if (target != null) {
                target.damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.isInSand()) {
            BlockPos pos = new BlockPos(this.getBlockX(), this.getSurface(this.getBlockX(), this.getBlockY(), this.getBlockZ()), this.getBlockZ()).down();
            BlockState state = this.getWorld().getBlockState(pos);
            if (state.isOpaqueFullCube(this.getWorld(), pos)) {
                if (this.getWorld().isClient) {
                    this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getSurface((int) Math.floor(this.getX()), (int) Math.floor(this.getY()), (int) Math.floor(this.getZ())) + 0.5F, this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
                }
            }
            if (this.age % 10 == 0) {
                this.playSound(SoundEvents.BLOCK_SAND_BREAK, 1, 0.5F);
            }
        }
        if (this.up() && this.isOnGround()) {
            this.jump();
        }
        boolean inSand = this.isInSand() || this.getControllingPassenger() == null;
        if (inSand && !this.isSandNavigator) {
            this.switchNavigator(true);
        }
        if (!inSand && this.isSandNavigator) {
            this.switchNavigator(false);
        }
        if (this.getWorld().isClient) {
            this.tail_buffer.calculateChainSwingBuffer(90, 20, 5F, this);
        }

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean up() {
        return (this.dataTracker.get(CONTROL_STATE) & 1) == 1;
    }

    public boolean dismountIAF() {
        return (this.dataTracker.get(CONTROL_STATE) >> 1 & 1) == 1;
    }

    public boolean attack() {
        return (this.dataTracker.get(CONTROL_STATE) >> 2 & 1) == 1;
    }

    @Override
    public void up(boolean up) {
        this.setStateField(0, up);
    }

    @Override
    public void down(boolean down) {

    }

    @Override
    public void dismount(boolean dismount) {
        this.setStateField(1, dismount);
    }

    @Override
    public void attack(boolean attack) {
        this.setStateField(2, attack);
    }

    @Override
    public void strike(boolean strike) {
    }

    public boolean isSandBelow() {
        int i = MathHelper.floor(this.getX());
        int j = MathHelper.floor(this.getY() + 1);
        int k = MathHelper.floor(this.getZ());
        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState BlockState = this.getWorld().getBlockState(blockpos);
        return BlockState.isIn(BlockTags.SAND);
    }

    public boolean isInSand() {
        return this.getControllingPassenger() == null && this.isInSandStrict();
    }

    public boolean isInSandStrict() {
        return this.getWorld().getBlockState(this.getBlockPos()).isIn(BlockTags.SAND);
    }

    @Override
    public int getAnimationTick() {
        return this.animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return this.currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        this.currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_BITE};
    }

    public Entity[] getWormParts() {
        return this.segments;
    }

    @Override
    public int getMaxHeadRotation() {
        return 10;
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    @Override
    public boolean canPassThrough(BlockPos pos, BlockState state, VoxelShape shape) {
        return this.getWorld().getBlockState(pos).isIn(BlockTags.SAND);
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceToClosestPlayer) {
        return false;
    }

    public boolean isRidingPlayer(PlayerEntity player) {
        return this.getRidingPlayer() != null && player != null && this.getRidingPlayer().getUuid().equals(player.getUuid());
    }

    @Override
    public PlayerEntity getRidingPlayer() {
        if (this.getControllingPassenger() instanceof PlayerEntity player)
            return player;
        return null;
    }

    @Override
    public double getRideSpeedModifier() {
        return this.isInSand() ? 1.5F : 1F;
    }

    public double processRiderY(double y) {
        return this.isInSand() ? y + 0.2F : y;
    }

    public class SandMoveHelper extends MoveControl {
        private final DeathWormEntity worm = DeathWormEntity.this;

        public SandMoveHelper() {
            super(DeathWormEntity.this);
        }

        @Override
        public void tick() {
            if (this.state == State.MOVE_TO) {
                double d1 = this.targetY - this.worm.getY();
                double d2 = this.targetZ - this.worm.getZ();
                Vec3d Vector3d = new Vec3d(this.targetX - this.worm.getX(), this.targetY - this.worm.getY(), this.targetZ - this.worm.getZ());
                double d0 = Vector3d.length();
                if (d0 < (double) 2.5000003E-7F) {
                    this.entity.setForwardSpeed(0.0F);
                } else {
                    this.speed = 1.0F;
                    this.worm.setVelocity(this.worm.getVelocity().add(Vector3d.multiply(this.speed * 0.05D / d0)));
                    Vec3d Vector3d1 = this.worm.getVelocity();
                    this.worm.setYaw(-((float) MathHelper.atan2(Vector3d1.x, Vector3d1.z)) * (180F / (float) Math.PI));
                }
            }
        }
    }
}
