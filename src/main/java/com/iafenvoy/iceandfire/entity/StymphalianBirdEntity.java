package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.ai.StymphalianBirdAIAirTargetGoal;
import com.iafenvoy.iceandfire.entity.ai.StymphalianBirdAIFleeGoal;
import com.iafenvoy.iceandfire.entity.ai.StymphalianBirdAITargetGoal;
import com.iafenvoy.iceandfire.entity.util.IAnimalFear;
import com.iafenvoy.iceandfire.entity.util.IVillagerFear;
import com.iafenvoy.iceandfire.entity.util.StymphalianBirdFlock;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class StymphalianBirdEntity extends Monster implements IAnimatedEntity, Enemy, IVillagerFear, IAnimalFear {
    public static final Predicate<Entity> STYMPHALIAN_PREDICATE = entity -> entity instanceof StymphalianBirdEntity;
    public static final Animation ANIMATION_PECK = Animation.create(20);
    public static final Animation ANIMATION_SHOOT_ARROWS = Animation.create(30);
    public static final Animation ANIMATION_SPEAK = Animation.create(10);
    private static final int FLIGHT_CHANCE_PER_TICK = 100;
    private static final EntityDataAccessor<Optional<UUID>> VICTOR_ENTITY = SynchedEntityData.defineId(StymphalianBirdEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(StymphalianBirdEntity.class, EntityDataSerializers.BOOLEAN);
    public float flyProgress;
    public BlockPos airTarget;
    public StymphalianBirdFlock flock;
    private int animationTick;
    private Animation currentAnimation;
    private boolean isFlying;
    private int flyTicks;
    private int launchTicks;
    private boolean aiFlightLaunch = false;
    private int airBorneCounter;

    public StymphalianBirdEntity(EntityType<? extends Monster> t, Level worldIn) {
        super(t, worldIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, 24.0)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, 6)
                //FOLLOW RANGE
                .add(Attributes.FOLLOW_RANGE, Math.min(2048, IafCommonConfig.INSTANCE.stymphalianBird.targetSearchLength.getValue()))
                //ARMOR
                .add(Attributes.ARMOR, 4.0);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new StymphalianBirdAIFleeGoal(this, 10));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.5D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new StymphalianBirdAIAirTargetGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new StymphalianBirdAITargetGoal(this, LivingEntity.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VICTOR_ENTITY, Optional.empty());
        builder.define(FLYING, Boolean.FALSE);
    }

    @Override
    public int getBaseExperienceReward() {
        return 10;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.getVictorId() != null) {
            tag.putUUID("VictorUUID", this.getVictorId());
        }
        tag.putBoolean("Flying", this.isFlying());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        UUID s;

        if (tag.hasUUID("VictorUUID")) {
            s = tag.getUUID("VictorUUID");
        } else {
            String s1 = tag.getString("VictorUUID");
            s = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s1);
        }

        if (s != null) {
            try {
                this.setVictorId(s);
            } catch (Throwable ignored) {
            }
        }
        this.setFlying(tag.getBoolean("Flying"));
    }

    public boolean isFlying() {
        if (this.level().isClientSide) {
            return this.isFlying = this.entityData.get(FLYING);
        }
        return this.isFlying;
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
        if (!this.level().isClientSide) {
            this.isFlying = flying;
        }
    }

    @Override
    public void die(DamageSource cause) {
        if (cause.getEntity() != null && cause.getEntity() instanceof LivingEntity && !this.level().isClientSide) {
            this.setVictorId(cause.getEntity().getUUID());
            if (this.flock != null) {
                this.flock.setFearTarget((LivingEntity) cause.getEntity());
            }
        }
        super.die(cause);
    }

    public UUID getVictorId() {
        return this.entityData.get(VICTOR_ENTITY).orElse(null);
    }

    public void setVictorId(UUID uuid) {
        this.entityData.set(VICTOR_ENTITY, Optional.ofNullable(uuid));
    }

    public LivingEntity getVictor() {
        try {
            UUID uuid = this.getVictorId();
            return uuid == null ? null : this.level().getPlayerByUUID(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public void setVictor(LivingEntity player) {
        this.setVictorId(player.getUUID());
    }

    public boolean isVictor(LivingEntity entityIn) {
        return entityIn == this.getVictor();
    }

    public boolean isTargetBlocked(Vec3 target) {
        return this.level().clip(new ClipContext(target, this.getEyePosition(1.0F), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_PECK);
        }
        return true;
    }


    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof Player) {
            this.setTarget(null);
        }
        if (this.getTarget() != null && (this.getTarget() instanceof Player && ((Player) this.getTarget()).isCreative() || this.getVictor() != null && this.isVictor(this.getTarget()))) {
            this.setTarget(null);
        }
        if (this.flock == null) {
            StymphalianBirdFlock otherFlock = StymphalianBirdFlock.getNearbyFlock(this);
            if (otherFlock == null) {
                this.flock = StymphalianBirdFlock.createFlock(this);
            } else {
                this.flock = otherFlock;
                this.flock.addToFlock(this);
            }
        } else {
            if (!this.flock.isLeader(this)) {
                double dist = this.distanceToSqr(this.flock.getLeader());
                if (dist > 360) {
                    this.setFlying(true);
                    this.navigation.stop();
                    this.airTarget = StymphalianBirdAIAirTargetGoal.getNearbyAirTarget(this.flock.getLeader());
                    this.aiFlightLaunch = false;
                } else if (!this.flock.getLeader().isFlying()) {
                    this.setFlying(false);
                    this.airTarget = null;
                    this.aiFlightLaunch = false;
                }
                if (this.onGround() && dist < 40 && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
                    this.setFlying(false);
                }
            }
            this.flock.update();
        }
        if (!this.level().isClientSide && this.getTarget() != null && this.getTarget().isAlive()) {
            double dist = this.distanceToSqr(this.getTarget());
            if (this.getAnimation() == ANIMATION_PECK && this.getAnimationTick() == 7) {
                if (dist < 1.5F) {
                    this.getTarget().hurt(this.level().damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                }
                if (this.onGround()) {
                    this.setFlying(false);
                }
            }
            if (this.getAnimation() != ANIMATION_PECK && this.getAnimation() != ANIMATION_SHOOT_ARROWS && dist > 3 && dist < 225) {
                this.setAnimation(ANIMATION_SHOOT_ARROWS);
            }
            if (this.getAnimation() == ANIMATION_SHOOT_ARROWS) {
                LivingEntity target = this.getTarget();
                this.lookAt(target, 360, 360);
                if (this.isFlying()) {
                    this.setYRot(this.yBodyRot);
                    if ((this.getAnimationTick() == 7 || this.getAnimationTick() == 14) && this.isDirectPathBetweenPoints(this, this.position(), target.position())) {
                        this.playSound(IafSounds.STYMPHALIAN_BIRD_ATTACK.get(), 1, 1);
                        for (int i = 0; i < 4; i++) {
                            float wingX = (float) (this.getX() + 1.8F * 0.5F * Mth.cos((float) ((this.getYRot() + 180 * (i % 2)) * Math.PI / 180)));
                            float wingZ = (float) (this.getZ() + 1.8F * 0.5F * Mth.sin((float) ((this.getYRot() + 180 * (i % 2)) * Math.PI / 180)));
                            float wingY = (float) (this.getY() + 1F);
                            double d0 = target.getX() - wingX;
                            double d1 = target.getBoundingBox().minY - wingY;
                            double d2 = target.getZ() - wingZ;
                            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                            StymphalianFeatherEntity entityarrow = new StymphalianFeatherEntity(IafEntities.STYMPHALIAN_FEATHER.get(), this.level(), this);
                            entityarrow.setPos(wingX, wingY, wingZ);
                            entityarrow.shoot(d0, d1 + d3 * 0.10000000298023224D, d2, 1.6F,
                                    14 - this.level().getDifficulty().getId() * 4);
                            this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                            this.level().addFreshEntity(entityarrow);
                        }
                    }
                } else {
                    this.setFlying(true);
                }
            }
        }
        boolean flying = this.isFlying() && !this.onGround() || this.airBorneCounter > 10 || this.getAnimation() == ANIMATION_SHOOT_ARROWS;
        if (flying && this.flyProgress < 20.0F) {
            this.flyProgress += 1F;
        } else if (!flying && this.flyProgress > 0.0F) {
            this.flyProgress -= 1F;
        }
        if (!this.isFlying() && this.airTarget != null && this.onGround() && !this.level().isClientSide) {
            this.airTarget = null;
        }
        if (this.isFlying() && this.getTarget() == null) {
            this.flyAround();
        } else if (this.getTarget() != null) {
            this.flyTowardsTarget();
        }
        if (!this.level().isClientSide && this.doesWantToLand() && !this.aiFlightLaunch && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
            this.setFlying(false);
            this.airTarget = null;
        }
        if (!this.level().isClientSide && this.isFree(0, 0, 0) && !this.isFlying()) {
            this.setFlying(true);
            this.launchTicks = 0;
            this.flyTicks = 0;
            this.aiFlightLaunch = true;
        }
        if (!this.level().isClientSide && this.onGround() && this.isFlying() && !this.aiFlightLaunch && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
            this.setFlying(false);
            this.airTarget = null;
        }
        if (!this.level().isClientSide && (this.flock == null || this.flock.isLeader(this)) && this.getRandom().nextInt(FLIGHT_CHANCE_PER_TICK) == 0 && !this.isFlying() && this.getPassengers().isEmpty() && !this.isBaby() && this.onGround()) {
            this.setFlying(true);
            this.launchTicks = 0;
            this.flyTicks = 0;
            this.aiFlightLaunch = true;
        }
        if (!this.level().isClientSide) {
            if (this.aiFlightLaunch && this.launchTicks < 40) {
                this.launchTicks++;
            } else {
                this.launchTicks = 0;
                this.aiFlightLaunch = false;
            }
            if (this.isFlying()) {
                this.flyTicks++;
            } else {
                this.flyTicks = 0;
            }
        }
        if (!this.onGround()) {
            this.airBorneCounter++;
        } else {
            this.airBorneCounter = 0;
        }
        if (this.getAnimation() == ANIMATION_SHOOT_ARROWS && !this.isFlying() && !this.level().isClientSide) {
            this.setFlying(true);
            this.aiFlightLaunch = true;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean isDirectPathBetweenPoints(Entity entity, Vec3 vec1, Vec3 vec2) {
        return this.level().clip(new ClipContext(vec1, vec2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    public void flyAround() {
        if (this.airTarget != null && this.isFlying()) {
            if (!this.isTargetInAir() || this.flyTicks > 6000 || !this.isFlying()) {
                this.airTarget = null;
            }
            this.flyTowardsTarget();
        }
    }

    public void flyTowardsTarget() {
        if (this.airTarget != null && this.isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3(this.airTarget.getX(), this.getY(), this.airTarget.getZ())) > 3) {
            double targetX = this.airTarget.getX() + 0.5D - this.getX();
            double targetY = Math.min(this.airTarget.getY(), 256) + 1D - this.getY();
            double targetZ = this.airTarget.getZ() + 0.5D - this.getZ();
            double motionX = (Math.signum(targetX) * 0.5D - this.getDeltaMovement().x) * 0.100000000372529 * this.getFlySpeed(false);
            double motionY = (Math.signum(targetY) * 0.5D - this.getDeltaMovement().y) * 0.100000000372529 * this.getFlySpeed(true);
            double motionZ = (Math.signum(targetZ) * 0.5D - this.getDeltaMovement().z) * 0.100000000372529 * this.getFlySpeed(false);
            this.setDeltaMovement(this.getDeltaMovement().add(motionX, motionY, motionZ));
            float angle = (float) (Math.atan2(this.getDeltaMovement().z, this.getDeltaMovement().x) * 180.0D / Math.PI) - 90.0F;
            float rotation = Mth.wrapDegrees(angle - this.getYRot());
            this.zza = 0.5F;
            this.yRotO = this.getYRot();
            this.setYRot(this.getYRot() + rotation);
            if (!this.isFlying()) {
                this.setFlying(true);
            }
        } else {
            this.airTarget = null;
        }
        if (this.airTarget != null && this.isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3(this.airTarget.getX(), this.getY(), this.airTarget.getZ())) < 3 && this.doesWantToLand()) {
            this.setFlying(false);
        }
    }

    private float getFlySpeed(boolean y) {
        float speed = 2;
        if (this.flock != null && !this.flock.isLeader(this) && this.distanceToSqr(this.flock.getLeader()) > 10) {
            speed = 4;
        }
        if (this.getAnimation() == ANIMATION_SHOOT_ARROWS && !y) {
            speed *= 0.05F;
        }
        return speed;
    }

    @Override
    public void playAmbientSound() {
        if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
        super.playAmbientSound();
    }

    @Override
    protected void playHurtSound(@NotNull DamageSource source) {
        if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
        super.playHurtSound(source);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return IafSounds.STYMPHALIAN_BIRD_IDLE.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return IafSounds.STYMPHALIAN_BIRD_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return IafSounds.STYMPHALIAN_BIRD_DIE.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull MobSpawnType reason, SpawnGroupData spawnDataIn) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(IafCommonConfig.INSTANCE.stymphalianBird.targetSearchLength.getValue());
        return spawnDataIn;
    }

    @Override
    public void setTarget(LivingEntity entity) {
        if (this.isVictor(entity) && entity != null) {
            return;
        }
        super.setTarget(entity);
        if (this.flock != null && this.flock.isLeader(this) && entity != null) {
            this.flock.onLeaderAttack(entity);
        }
    }

    public float getDistanceSquared(Vec3 Vector3d) {
        float f = (float) (this.getX() - Vector3d.x);
        float f1 = (float) (this.getY() - Vector3d.y);
        float f2 = (float) (this.getZ() - Vector3d.z);
        return f * f + f1 * f1 + f2 * f2;
    }

    protected boolean isTargetInAir() {
        return this.airTarget != null && ((this.level().getBlockState(this.airTarget).isAir()) || this.level().getBlockState(this.airTarget).isAir());
    }

    public boolean doesWantToLand() {
        if (this.flock != null) {
            if (!this.flock.isLeader(this) && this.flock.getLeader() != null) {
                return this.flock.getLeader().doesWantToLand();
            }
        }
        return this.flyTicks > 500 || this.flyTicks > 40 && this.flyProgress == 0;
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
        return new Animation[]{NO_ANIMATION, ANIMATION_PECK, ANIMATION_SHOOT_ARROWS, ANIMATION_SPEAK};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return IafCommonConfig.INSTANCE.stymphalianBird.attackAnimals.getValue();
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
