package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.ai.StymphalianBirdAIAirTarget;
import com.iafenvoy.iceandfire.entity.ai.StymphalianBirdAIFlee;
import com.iafenvoy.iceandfire.entity.ai.StymphalianBirdAITarget;
import com.iafenvoy.iceandfire.entity.util.IAnimalFear;
import com.iafenvoy.iceandfire.entity.util.IVillagerFear;
import com.iafenvoy.iceandfire.entity.util.StymphalianBirdFlock;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityStymphalianBird extends HostileEntity implements IAnimatedEntity, Monster, IVillagerFear, IAnimalFear {
    public static final Predicate<Entity> STYMPHALIAN_PREDICATE = entity -> entity instanceof EntityStymphalianBird;
    public static final Animation ANIMATION_PECK = Animation.create(20);
    public static final Animation ANIMATION_SHOOT_ARROWS = Animation.create(30);
    public static final Animation ANIMATION_SPEAK = Animation.create(10);
    private static final int FLIGHT_CHANCE_PER_TICK = 100;
    private static final TrackedData<Optional<UUID>> VICTOR_ENTITY = DataTracker.registerData(EntityStymphalianBird.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Boolean> FLYING = DataTracker.registerData(EntityStymphalianBird.class, TrackedDataHandlerRegistry.BOOLEAN);
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

    public EntityStymphalianBird(EntityType<? extends HostileEntity> t, World worldIn) {
        super(t, worldIn);
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0D)
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6)
                //FOLLOW RANGE
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, Math.min(2048, IafCommonConfig.INSTANCE.stymphalianBird.targetSearchLength.getValue()))
                //ARMOR
                .add(EntityAttributes.GENERIC_ARMOR, 4.0D);
    }

    @Override
    protected void fall(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new StymphalianBirdAIFlee(this, 10));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.5D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new StymphalianBirdAIAirTarget(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, LivingEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new StymphalianBirdAITarget(this, LivingEntity.class, true));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VICTOR_ENTITY, Optional.empty());
        this.dataTracker.startTracking(FLYING, Boolean.FALSE);
    }

    @Override
    public int getXpToDrop() {
        return 10;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && this.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        if (this.getVictorId() != null) {
            tag.putUuid("VictorUUID", this.getVictorId());
        }
        tag.putBoolean("Flying", this.isFlying());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        UUID s;

        if (tag.containsUuid("VictorUUID")) {
            s = tag.getUuid("VictorUUID");
        } else {
            String s1 = tag.getString("VictorUUID");
            s = ServerConfigHandler.getPlayerUuidByName(this.getServer(), s1);
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
        if (this.getWorld().isClient) {
            return this.isFlying = this.dataTracker.get(FLYING);
        }
        return this.isFlying;
    }

    public void setFlying(boolean flying) {
        this.dataTracker.set(FLYING, flying);
        if (!this.getWorld().isClient) {
            this.isFlying = flying;
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (cause.getAttacker() != null && cause.getAttacker() instanceof LivingEntity && !this.getWorld().isClient) {
            this.setVictorId(cause.getAttacker().getUuid());
            if (this.flock != null) {
                this.flock.setFearTarget((LivingEntity) cause.getAttacker());
            }
        }
        super.onDeath(cause);
    }

    public UUID getVictorId() {
        return this.dataTracker.get(VICTOR_ENTITY).orElse(null);
    }

    public void setVictorId(UUID uuid) {
        this.dataTracker.set(VICTOR_ENTITY, Optional.ofNullable(uuid));
    }

    public LivingEntity getVictor() {
        try {
            UUID uuid = this.getVictorId();
            return uuid == null ? null : this.getWorld().getPlayerByUuid(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public void setVictor(LivingEntity player) {
        this.setVictorId(player.getUuid());
    }

    public boolean isVictor(LivingEntity entityIn) {
        return entityIn == this.getVictor();
    }

    public boolean isTargetBlocked(Vec3d target) {
        return this.getWorld().raycast(new RaycastContext(target, this.getCameraPosVec(1.0F), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_PECK);
        }
        return true;
    }


    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof PlayerEntity) {
            this.setTarget(null);
        }
        if (this.getTarget() != null && (this.getTarget() instanceof PlayerEntity && ((PlayerEntity) this.getTarget()).isCreative() || this.getVictor() != null && this.isVictor(this.getTarget()))) {
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
                double dist = this.squaredDistanceTo(this.flock.getLeader());
                if (dist > 360) {
                    this.setFlying(true);
                    this.navigation.stop();
                    this.airTarget = StymphalianBirdAIAirTarget.getNearbyAirTarget(this.flock.getLeader());
                    this.aiFlightLaunch = false;
                } else if (!this.flock.getLeader().isFlying()) {
                    this.setFlying(false);
                    this.airTarget = null;
                    this.aiFlightLaunch = false;
                }
                if (this.isOnGround() && dist < 40 && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
                    this.setFlying(false);
                }
            }
            this.flock.update();
        }
        if (!this.getWorld().isClient && this.getTarget() != null && this.getTarget().isAlive()) {
            double dist = this.squaredDistanceTo(this.getTarget());
            if (this.getAnimation() == ANIMATION_PECK && this.getAnimationTick() == 7) {
                if (dist < 1.5F) {
                    this.getTarget().damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
                }
                if (this.isOnGround()) {
                    this.setFlying(false);
                }
            }
            if (this.getAnimation() != ANIMATION_PECK && this.getAnimation() != ANIMATION_SHOOT_ARROWS && dist > 3 && dist < 225) {
                this.setAnimation(ANIMATION_SHOOT_ARROWS);
            }
            if (this.getAnimation() == ANIMATION_SHOOT_ARROWS) {
                LivingEntity target = this.getTarget();
                this.lookAtEntity(target, 360, 360);
                if (this.isFlying()) {
                    this.setYaw(this.bodyYaw);
                    if ((this.getAnimationTick() == 7 || this.getAnimationTick() == 14) && this.isDirectPathBetweenPoints(this, this.getPos(), target.getPos())) {
                        this.playSound(IafSounds.STYMPHALIAN_BIRD_ATTACK.get(), 1, 1);
                        for (int i = 0; i < 4; i++) {
                            float wingX = (float) (this.getX() + 1.8F * 0.5F * MathHelper.cos((float) ((this.getYaw() + 180 * (i % 2)) * Math.PI / 180)));
                            float wingZ = (float) (this.getZ() + 1.8F * 0.5F * MathHelper.sin((float) ((this.getYaw() + 180 * (i % 2)) * Math.PI / 180)));
                            float wingY = (float) (this.getY() + 1F);
                            double d0 = target.getX() - wingX;
                            double d1 = target.getBoundingBox().minY - wingY;
                            double d2 = target.getZ() - wingZ;
                            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                            EntityStymphalianFeather entityarrow = new EntityStymphalianFeather(IafEntities.STYMPHALIAN_FEATHER.get(), this.getWorld(), this);
                            entityarrow.setPosition(wingX, wingY, wingZ);
                            entityarrow.setVelocity(d0, d1 + d3 * 0.10000000298023224D, d2, 1.6F,
                                    14 - this.getWorld().getDifficulty().getId() * 4);
                            this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                            this.getWorld().spawnEntity(entityarrow);
                        }
                    }
                } else {
                    this.setFlying(true);
                }
            }
        }
        boolean flying = this.isFlying() && !this.isOnGround() || this.airBorneCounter > 10 || this.getAnimation() == ANIMATION_SHOOT_ARROWS;
        if (flying && this.flyProgress < 20.0F) {
            this.flyProgress += 1F;
        } else if (!flying && this.flyProgress > 0.0F) {
            this.flyProgress -= 1F;
        }
        if (!this.isFlying() && this.airTarget != null && this.isOnGround() && !this.getWorld().isClient) {
            this.airTarget = null;
        }
        if (this.isFlying() && this.getTarget() == null) {
            this.flyAround();
        } else if (this.getTarget() != null) {
            this.flyTowardsTarget();
        }
        if (!this.getWorld().isClient && this.doesWantToLand() && !this.aiFlightLaunch && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
            this.setFlying(false);
            this.airTarget = null;
        }
        if (!this.getWorld().isClient && this.doesNotCollide(0, 0, 0) && !this.isFlying()) {
            this.setFlying(true);
            this.launchTicks = 0;
            this.flyTicks = 0;
            this.aiFlightLaunch = true;
        }
        if (!this.getWorld().isClient && this.isOnGround() && this.isFlying() && !this.aiFlightLaunch && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
            this.setFlying(false);
            this.airTarget = null;
        }
        if (!this.getWorld().isClient && (this.flock == null || this.flock.isLeader(this)) && this.getRandom().nextInt(FLIGHT_CHANCE_PER_TICK) == 0 && !this.isFlying() && this.getPassengerList().isEmpty() && !this.isBaby() && this.isOnGround()) {
            this.setFlying(true);
            this.launchTicks = 0;
            this.flyTicks = 0;
            this.aiFlightLaunch = true;
        }
        if (!this.getWorld().isClient) {
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
        if (!this.isOnGround()) {
            this.airBorneCounter++;
        } else {
            this.airBorneCounter = 0;
        }
        if (this.getAnimation() == ANIMATION_SHOOT_ARROWS && !this.isFlying() && !this.getWorld().isClient) {
            this.setFlying(true);
            this.aiFlightLaunch = true;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean isDirectPathBetweenPoints(Entity entity, Vec3d vec1, Vec3d vec2) {
        return this.getWorld().raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)).getType() == HitResult.Type.MISS;
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
        if (this.airTarget != null && this.isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3d(this.airTarget.getX(), this.getY(), this.airTarget.getZ())) > 3) {
            double targetX = this.airTarget.getX() + 0.5D - this.getX();
            double targetY = Math.min(this.airTarget.getY(), 256) + 1D - this.getY();
            double targetZ = this.airTarget.getZ() + 0.5D - this.getZ();
            double motionX = (Math.signum(targetX) * 0.5D - this.getVelocity().x) * 0.100000000372529 * this.getFlySpeed(false);
            double motionY = (Math.signum(targetY) * 0.5D - this.getVelocity().y) * 0.100000000372529 * this.getFlySpeed(true);
            double motionZ = (Math.signum(targetZ) * 0.5D - this.getVelocity().z) * 0.100000000372529 * this.getFlySpeed(false);
            this.setVelocity(this.getVelocity().add(motionX, motionY, motionZ));
            float angle = (float) (Math.atan2(this.getVelocity().z, this.getVelocity().x) * 180.0D / Math.PI) - 90.0F;
            float rotation = MathHelper.wrapDegrees(angle - this.getYaw());
            this.forwardSpeed = 0.5F;
            this.prevYaw = this.getYaw();
            this.setYaw(this.getYaw() + rotation);
            if (!this.isFlying()) {
                this.setFlying(true);
            }
        } else {
            this.airTarget = null;
        }
        if (this.airTarget != null && this.isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3d(this.airTarget.getX(), this.getY(), this.airTarget.getZ())) < 3 && this.doesWantToLand()) {
            this.setFlying(false);
        }
    }

    private float getFlySpeed(boolean y) {
        float speed = 2;
        if (this.flock != null && !this.flock.isLeader(this) && this.squaredDistanceTo(this.flock.getLeader()) > 10) {
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
    protected void playHurtSound(DamageSource source) {
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
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSounds.STYMPHALIAN_BIRD_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return IafSounds.STYMPHALIAN_BIRD_DIE.get();
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        spawnDataIn = super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(IafCommonConfig.INSTANCE.stymphalianBird.targetSearchLength.getValue());
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

    public float getDistanceSquared(Vec3d Vector3d) {
        float f = (float) (this.getX() - Vector3d.x);
        float f1 = (float) (this.getY() - Vector3d.y);
        float f2 = (float) (this.getZ() - Vector3d.z);
        return f * f + f1 * f1 + f2 * f2;
    }

    protected boolean isTargetInAir() {
        return this.airTarget != null && ((this.getWorld().getBlockState(this.airTarget).isAir()) || this.getWorld().getBlockState(this.airTarget).isAir());
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
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceToClosestPlayer) {
        return false;
    }
}
