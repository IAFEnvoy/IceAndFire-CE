package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.api.IafEvents;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.entity.util.dragon.IafDragonAttacks;
import com.iafenvoy.iceandfire.entity.util.dragon.IafDragonDestructionManager;
import com.iafenvoy.iceandfire.particle.DragonFrostParticleType;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Random;

public class EntityIceDragon extends EntityDragonBase {
    public static final Identifier FEMALE_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/dragon/ice_dragon_female");
    public static final Identifier MALE_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/dragon/ice_dragon_male");
    public static final Identifier SKELETON_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/dragon/ice_dragon_skeleton");

    public EntityIceDragon(EntityType<? extends EntityIceDragon> t, World worldIn) {
        super(t, worldIn, DragonType.ICE, 1, 1 + IafCommonConfig.INSTANCE.dragon.attackDamage.getValue(), IafCommonConfig.INSTANCE.dragon.maxHealth.getValue() * 0.04, IafCommonConfig.INSTANCE.dragon.maxHealth.getValue(), 0.15F, 0.4F);
        ANIMATION_SPEAK = Animation.create(20);
        ANIMATION_BITE = Animation.create(35);
        ANIMATION_SHAKEPREY = Animation.create(65);
        ANIMATION_TAILWHACK = Animation.create(40);
        ANIMATION_FIRECHARGE = Animation.create(25);
        ANIMATION_WINGBLAST = Animation.create(50);
        ANIMATION_ROAR = Animation.create(40);
        ANIMATION_EPIC_ROAR = Animation.create(60);
    }

    @Override
    protected boolean shouldTarget(Entity entity) {
        if (entity instanceof EntityDragonBase && !this.isTamed()) {
            return entity.getType() != this.getType() && this.getWidth() >= entity.getWidth() && !((EntityDragonBase) entity).isMobDead();
        }
        return entity instanceof PlayerEntity || DragonUtils.isDragonTargetable(entity, IafEntityTags.ICE_DRAGON_TARGETS) || entity instanceof WaterCreatureEntity || !this.isTamed() && DragonUtils.isVillager(entity);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SWIMMING, Boolean.FALSE);
    }

    @Override
    public String getVariantName(int variant) {
        return switch (variant) {
            case 1 -> "white_";
            case 2 -> "sapphire_";
            case 3 -> "silver_";
            default -> "blue_";
        };
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }


    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putBoolean("Swimming", this.isSwimming());
        compound.putInt("SwimmingTicks", this.ticksSwiming);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setSwimming(compound.getBoolean("Swimming"));
        this.ticksSwiming = compound.getInt("SwimmingTicks");
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
        this.getLookControl().lookAt(entityIn, 30.0F, 30.0F);
        if (!this.isPlayingAttackAnimation()) {
            switch (this.groundAttack) {
                case BITE -> this.setAnimation(ANIMATION_BITE);
                case TAIL_WHIP -> this.setAnimation(ANIMATION_TAILWHACK);
                case SHAKE_PREY -> {
                    boolean flag = false;
                    if (new Random().nextInt(2) == 0 && this.isDirectPathBetweenPoints(this, this.getPos().add(0, this.getHeight() / 2, 0), entityIn.getPos().add(0, entityIn.getHeight() / 2, 0)) &&
                            entityIn.getWidth() < this.getWidth() * 0.5F && this.getControllingPassenger() == null && this.getDragonStage() > 1 && !(entityIn instanceof EntityDragonBase) && !DragonUtils.isAnimaniaMob(entityIn)) {
                        this.setAnimation(ANIMATION_SHAKEPREY);
                        flag = true;
                        entityIn.startRiding(this);
                    }
                    if (!flag) {
                        this.groundAttack = IafDragonAttacks.Ground.BITE;
                        this.setAnimation(ANIMATION_BITE);
                    }
                }
                case WING_BLAST -> this.setAnimation(ANIMATION_WINGBLAST);
            }
        }
        return false;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        LivingEntity attackTarget = this.getTarget();
        if (!this.getWorld().isClient && this.isInLava() && this.isAllowedToTriggerFlight() && !this.isModelDead()) {
            this.setHovering(true);
            this.setInSittingPose(false);
            this.setSitting(false);
            this.flyHovering = 0;
            this.flyTicks = 0;
        }
        if (!this.getWorld().isClient && attackTarget != null) {
            if (this.getBoundingBox().expand(0 + this.getRenderSize() * 0.33F, 0 + this.getRenderSize() * 0.33F, 0 + this.getRenderSize() * 0.33F).intersects(attackTarget.getBoundingBox())) {
                this.tryAttack(attackTarget);
            }
            if (this.groundAttack == IafDragonAttacks.Ground.FIRE && (this.usingGroundAttack || this.isOnGround())) {
                this.shootIceAtMob(attackTarget);
            }
            if (this.airAttack == IafDragonAttacks.Air.TACKLE && !this.usingGroundAttack && this.squaredDistanceTo(attackTarget) < 100) {
                double difX = attackTarget.getX() - this.getX();
                double difY = attackTarget.getY() + attackTarget.getHeight() - this.getY();
                double difZ = attackTarget.getZ() - this.getZ();
                this.setVelocity(this.getVelocity().add(difX * 0.1D, difY * 0.1D, difZ * 0.1D));

                if (this.getBoundingBox().expand(1 + this.getRenderSize() * 0.5F, 1 + this.getRenderSize() * 0.5F, 1 + this.getRenderSize() * 0.5F).intersects(attackTarget.getBoundingBox())) {
                    this.tryAttack(attackTarget);
                    this.usingGroundAttack = true;
                    this.randomizeAttacks();
                    this.setFlying(false);
                    this.setHovering(false);
                }
            }
        }
        boolean swimming = this.isTouchingWater();
        this.prevSwimProgress = this.swimProgress;
        if (swimming && this.swimProgress < 20.0F) {
            this.swimProgress += 0.5F;
        } else if (!swimming && this.swimProgress > 0.0F) {
            this.swimProgress -= 0.5F;
        }
        if (this.isTouchingWater() && !this.isSwimming() && (!this.isFlying() && !this.isHovering() || this.flyTicks > 100)) {
            this.setSwimming(true);
            this.setHovering(false);
            this.setFlying(false);
            this.flyTicks = 0;
            this.ticksSwiming = 0;
        }
        if ((!this.isTouchingWater() || this.isHovering() || this.isFlying()) && this.isSwimming()) {
            this.setSwimming(false);
            this.ticksSwiming = 0;
        }
        if (this.isSwimming() && !this.isModelDead()) {
            this.ticksSwiming++;
            if (this.isTouchingWater() && (this.ticksSwiming > 4000 || this.getTarget() != null && this.isTouchingWater() != this.getTarget().isTouchingWater()) && !this.isBaby() && !this.isHovering() && !this.isFlying()) {
                this.setHovering(true);
                this.jump();
                this.setVelocity(this.getVelocity().add(0.0D, 0.8D, 0.0D));
                this.setSwimming(false);
            }
        }
        if (!this.getWorld().isClient && this.getControllingPassenger() == null && (this.isHovering() && !this.isFlying() && this.isTouchingWater())) {
            this.setVelocity(this.getVelocity().add(0.0D, 0.2D, 0.0D));
        }
        if (this.swimCycle < 48) {
            this.swimCycle += 2;
        } else {
            this.swimCycle = 0;
        }
        if (this.isModelDead() && this.swimCycle != 0) {
            this.swimCycle = 0;
        }
    }

    @Override
    public void riderShootFire(Entity controller) {
        if (this.getRandom().nextInt(5) == 0 && !this.isBaby()) {
            if (this.getAnimation() != ANIMATION_FIRECHARGE) {
                this.setAnimation(ANIMATION_FIRECHARGE);
            } else if (this.getAnimationTick() == 15) {
                this.setYaw(this.bodyYaw);
                Vec3d headVec = this.getHeadPosition();
                this.playSound(IafSounds.ICEDRAGON_BREATH.get(), 4, 1);
                double d2 = controller.getRotationVector().x;
                double d3 = controller.getRotationVector().y;
                double d4 = controller.getRotationVector().z;
                float inaccuracy = 1.0F;
                d2 = d2 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d3 = d3 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d4 = d4 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                EntityDragonIceCharge entitylargefireball = new EntityDragonIceCharge(
                        IafEntities.ICE_DRAGON_CHARGE.get(), this.getWorld(), this, d2, d3, d4);
                float size = this.isBaby() ? 0.4F : this.shouldDropLoot() ? 1.3F : 0.8F;
                entitylargefireball.setPosition(headVec.x, headVec.y, headVec.z);
                if (!this.getWorld().isClient) {
                    this.getWorld().spawnEntity(entitylargefireball);
                }

            }
        } else {
            if (this.isBreathingFire()) {
                if (this.isActuallyBreathingFire()) {
                    this.setYaw(this.bodyYaw);
                    if (this.age % 5 == 0) {
                        this.playSound(IafSounds.ICEDRAGON_BREATH.get(), 4, 1);
                    }
                    HitResult mop = this.rayTraceRider(controller, 10 * this.getDragonStage(), 1.0F);
                    if (mop != null) {
                        this.stimulateFire(mop.getPos().x, mop.getPos().y, mop.getPos().z, 1);
                    }
                }
            } else {
                this.setBreathingFire(true);
            }
        }
    }

    @Override
    public boolean canSpawn(WorldView worldIn) {
        return worldIn.doesNotIntersectEntities(this);
    }

    @Override
    public void onBubbleColumnCollision(boolean pDownwards) {
        // Disable bubble column drag for elder dragons
        if (this.getDragonStage() < 2) {
            super.onBubbleColumnCollision(pDownwards);
        }
    }

    @Override
    public void onBubbleColumnSurfaceCollision(boolean pDownwards) {
        // Disable bubble column drag for elder dragons
        if (this.getDragonStage() < 2) {
            super.onBubbleColumnSurfaceCollision(pDownwards);
        }
    }

    @Override
    public void travel(Vec3d pTravelVector) {
        float flyingSpeed;
        if (/* Always false on the server */ this.isTouchingWater()) {
            // In water special
            if (this.canMoveVoluntarily() && this.getControllingPassenger() == null) {
                // Ice dragons swim faster
                this.updateVelocity(this.getMovementSpeed(), pTravelVector);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.9D));
                this.getTarget();//                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            } else if (this.allowLocalMotionControl && this.getControllingPassenger() != null && !this.isHovering() && !this.isFlying()) {
                LivingEntity rider = this.getControllingPassenger();

                float speed = (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                // Bigger difference in speed for young and elder dragons
                float waterSpeedMod = (float) (0.42f + 0.1 * MathHelper.map(speed, this.minimumSpeed, this.maximumSpeed, 0f, 1.5f));
                speed *= waterSpeedMod;
                speed *= rider.isSprinting() ? 1.5f : 1.0f;

                float vertical = 0f;
                if (this.isGoingUp() && !this.isGoingDown()) {
                    vertical = 1f;
                } else if (this.isGoingDown() && !this.isGoingUp()) {
                    vertical = -1f;
                } else if (this.isGoingUp() && this.isGoingDown() && this.isLogicalSideForUpdatingMovement()) {
                    // Try floating
                    this.setVelocity(this.getVelocity().multiply(1.0f, 0.5f, 1.0f));
                }

                Vec3d travelVector = new Vec3d(
                        rider.sidewaysSpeed,
                        vertical,
                        rider.forwardSpeed
                );
                if (this.isLogicalSideForUpdatingMovement()) {
                    this.setMovementSpeed(speed);

                    this.updateVelocity(this.getMovementSpeed(), travelVector);
                    this.move(MovementType.SELF, this.getVelocity());

                    Vec3d currentMotion = this.getVelocity();
                    if (this.horizontalCollision) {
                        currentMotion = new Vec3d(currentMotion.x, 0.2D, currentMotion.z);
                    }
                    this.setVelocity(currentMotion.multiply(0.9D));

                    this.updateLimbs(false);
                } else {
                    this.setVelocity(Vec3d.ZERO);
                }
                this.tryCheckBlockCollision();
            } else {
                super.travel(pTravelVector);
            }

        }
        // Over water special
        else if (this.allowLocalMotionControl && this.getControllingPassenger() != null && !this.isHovering() && !this.isFlying()
                && this.getWorld().getBlockState(this.getVelocityAffectingPos()).getFluidState().isIn(FluidTags.WATER)) {
            // Movement when walking on the water, mainly used for not slowing down when jumping out of water
            LivingEntity rider = this.getControllingPassenger();

            double forward = rider.forwardSpeed;
            double strafing = rider.sidewaysSpeed;
            // Inherit y motion for dropping
            double vertical = pTravelVector.y;
            float speed = (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);

            float groundSpeedModifier = (float) (1.8F * this.getFlightSpeedModifier());
            speed *= groundSpeedModifier;
            // Try to match the original riding speed
//            forward *= speed;
            // Faster sprint
            forward *= rider.isSprinting() ? 1.2f : 1.0f;
            // Slower going back
            forward *= rider.forwardSpeed > 0 ? 1.0f : 0.2f;
            // Slower going sideway
            strafing *= 0.05f;

            if (this.isLogicalSideForUpdatingMovement()) {
                this.setMovementSpeed(speed);

                // Vanilla walking behavior includes going up steps
                super.travel(new Vec3d(strafing, vertical, forward));

                Vec3d currentMotion = this.getVelocity();
                if (this.horizontalCollision) {
                    currentMotion = new Vec3d(currentMotion.x, 0.2D, currentMotion.z);
                }
                this.setVelocity(currentMotion.multiply(1.0D));
            } else {
                this.setVelocity(Vec3d.ZERO);
            }
            this.tryCheckBlockCollision();
//            this.updatePitch(this.yOld - this.getY());
        } else {
            super.travel(pTravelVector);
        }
    }

    @Override
    public Identifier getDeadLootTable() {
        if (this.getDeathStage() >= (this.getAgeInDays() / 5) / 2) {
            return SKELETON_LOOT;
        } else {
            return this.isMale() ? MALE_LOOT : FEMALE_LOOT;
        }
    }

    private void shootIceAtMob(LivingEntity entity) {
        if (this.usingGroundAttack && this.groundAttack == IafDragonAttacks.Ground.FIRE || !this.usingGroundAttack && (this.airAttack == IafDragonAttacks.Air.SCORCH_STREAM || this.airAttack == IafDragonAttacks.Air.HOVER_BLAST)) {
            if (this.usingGroundAttack && this.getRandom().nextInt(5) == 0 || !this.usingGroundAttack && this.airAttack == IafDragonAttacks.Air.HOVER_BLAST) {
                if (this.getAnimation() != ANIMATION_FIRECHARGE) {
                    this.setAnimation(ANIMATION_FIRECHARGE);
                } else if (this.getAnimationTick() == 15) {
                    this.setYaw(this.bodyYaw);
                    Vec3d headVec = this.getHeadPosition();
                    double d2 = entity.getX() - headVec.x;
                    double d3 = entity.getY() - headVec.y;
                    double d4 = entity.getZ() - headVec.z;
                    float inaccuracy = 1.0F;
                    d2 = d2 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    d3 = d3 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    d4 = d4 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    this.playSound(IafSounds.ICEDRAGON_BREATH.get(), 4, 1);
                    EntityDragonIceCharge entitylargefireball = new EntityDragonIceCharge(
                            IafEntities.ICE_DRAGON_CHARGE.get(), this.getWorld(), this, d2, d3, d4);
                    float size = this.isBaby() ? 0.4F : this.shouldDropLoot() ? 1.3F : 0.8F;
                    entitylargefireball.setPosition(headVec.x, headVec.y, headVec.z);
                    if (!this.getWorld().isClient) {
                        this.getWorld().spawnEntity(entitylargefireball);
                    }
                    if (!entity.isAlive()) {
                        this.setBreathingFire(false);
                        this.usingGroundAttack = true;
                    }
                }
            } else {
                if (this.isBreathingFire()) {
                    if (this.isActuallyBreathingFire()) {
                        this.setYaw(this.bodyYaw);
                        if (this.age % 5 == 0) {
                            this.playSound(IafSounds.ICEDRAGON_BREATH.get(), 4, 1);
                        }
                        this.stimulateFire(entity.getX(), entity.getY(), entity.getZ(), 1);
                        if (!entity.isAlive()) {
                            this.setBreathingFire(false);
                            this.usingGroundAttack = true;
                        }
                    }
                } else {
                    this.setBreathingFire(true);
                }
            }
        }
        this.lookAtEntity(entity, 360, 360);
    }

    @Override
    public void stimulateFire(double burnX, double burnY, double burnZ, int syncType) {
        if (IafEvents.ON_DRAGON_FIRE_BLOCK.invoker().onFireBlock(this, burnX, burnY, burnZ)) return;
        if (syncType > 2 && syncType < 6) {
            if (this.getAnimation() != ANIMATION_FIRECHARGE) {
                this.setAnimation(ANIMATION_FIRECHARGE);
            } else if (this.getAnimationTick() == 20) {
                this.setYaw(this.bodyYaw);
                Vec3d headVec = this.getHeadPosition();
                double d2 = burnX - headVec.x;
                double d3 = burnY - headVec.y;
                double d4 = burnZ - headVec.z;
                float inaccuracy = 1.0F;
                d2 = d2 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d3 = d3 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d4 = d4 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                this.playSound(IafSounds.FIREDRAGON_BREATH.get(), 4, 1);
                EntityDragonIceCharge entitylargefireball = new EntityDragonIceCharge(
                        IafEntities.ICE_DRAGON_CHARGE.get(), this.getWorld(), this, d2, d3, d4);
                // FIXME :: Unused
                // float size = this.isBaby() ? 0.4F : this.shouldDropLoot() ? 1.3F : 0.8F;
                entitylargefireball.setPosition(headVec.x, headVec.y, headVec.z);
                if (!this.getWorld().isClient) {
                    this.getWorld().spawnEntity(entitylargefireball);
                }
            }
            return;
        }
        this.getNavigation().stop();
        this.burnParticleX = burnX;
        this.burnParticleY = burnY;
        this.burnParticleZ = burnZ;
        Vec3d headPos = this.getHeadPosition();
        double d2 = burnX - headPos.x;
        double d3 = burnY - headPos.y;
        double d4 = burnZ - headPos.z;
        double distance = Math.max(2.5F * Math.sqrt(this.squaredDistanceTo(burnX, burnY, burnZ)), 0);
        double conqueredDistance = this.burnProgress / 40D * distance;
        int increment = (int) Math.ceil(conqueredDistance / 100);
        int particleCount = this.getDragonStage() <= 3 ? 6 : 3;
        for (int i = 0; i < conqueredDistance; i += increment) {
            double progressX = headPos.x + d2 * (i / (float) distance);
            double progressY = headPos.y + d3 * (i / (float) distance);
            double progressZ = headPos.z + d4 * (i / (float) distance);
            if (this.canPositionBeSeen(progressX, progressY, progressZ)) {
                if (this.random.nextInt(particleCount) == 0) {
                    Vec3d velocity = new Vec3d(progressX, progressY, progressZ).subtract(headPos);
                    if (this.getWorld() instanceof ServerWorld serverWorld)
                        serverWorld.spawnParticles(new DragonFrostParticleType(this.getScaleFactor()), headPos.x, headPos.y, headPos.z, 0, velocity.x, velocity.y, velocity.z, 1);
                }
            } else if (!this.getWorld().isClient) {
                HitResult result = this.getWorld().raycast(new RaycastContext(new Vec3d(this.getX(), this.getY() + this.getStandingEyeHeight(), this.getZ()), new Vec3d(progressX, progressY, progressZ), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
                Vec3d vec3 = result.getPos();
                BlockPos pos = BlockPos.ofFloored(vec3);
                IafDragonDestructionManager.destroyAreaBreath(this.getWorld(), pos, this);
            }
        }
        if (this.burnProgress >= 40D && this.canPositionBeSeen(burnX, burnY, burnZ)) {
            double spawnX = burnX + (this.random.nextFloat() * 3.0) - 1.5;
            double spawnY = burnY + (this.random.nextFloat() * 3.0) - 1.5;
            double spawnZ = burnZ + (this.random.nextFloat() * 3.0) - 1.5;
            if (!this.getWorld().isClient)
                IafDragonDestructionManager.destroyAreaBreath(this.getWorld(), BlockPos.ofFloored(spawnX, spawnY, spawnZ), this);
        }
    }

    @Override
    public boolean isSwimming() {
        if (this.getWorld().isClient) {
            boolean swimming = this.dataTracker.get(SWIMMING);
            this.isSwimming = swimming;
            return swimming;
        }
        return this.isSwimming;
    }

    @Override
    public void setSwimming(boolean swimming) {
        this.dataTracker.set(SWIMMING, swimming);
        if (!this.getWorld().isClient) {
            this.isSwimming = swimming;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isTeen() ? IafSounds.ICEDRAGON_TEEN_IDLE.get() : this.shouldDropLoot() ? IafSounds.ICEDRAGON_ADULT_IDLE.get() : IafSounds.ICEDRAGON_CHILD_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isTeen() ? IafSounds.ICEDRAGON_TEEN_HURT.get() : this.shouldDropLoot() ? IafSounds.ICEDRAGON_ADULT_HURT.get() : IafSounds.ICEDRAGON_CHILD_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isTeen() ? IafSounds.ICEDRAGON_TEEN_DEATH.get() : this.shouldDropLoot() ? IafSounds.ICEDRAGON_ADULT_DEATH.get() : IafSounds.ICEDRAGON_CHILD_DEATH.get();
    }

    @Override
    public SoundEvent getRoarSound() {
        return this.isTeen() ? IafSounds.ICEDRAGON_TEEN_ROAR.get() : this.shouldDropLoot() ? IafSounds.ICEDRAGON_ADULT_ROAR.get() : IafSounds.ICEDRAGON_CHILD_ROAR.get();
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, ANIMATION_EAT, ANIMATION_SPEAK, ANIMATION_BITE, ANIMATION_SHAKEPREY, ANIMATION_TAILWHACK, ANIMATION_FIRECHARGE, ANIMATION_WINGBLAST, ANIMATION_ROAR};
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() != null && stack.getItem() == IafItems.FROST_STEW.get();
    }

    @Override
    protected void breathFireAtPos(BlockPos burningTarget) {
        if (this.isBreathingFire()) {
            if (this.isActuallyBreathingFire()) {
                this.setYaw(this.bodyYaw);
                if (this.age % 5 == 0) {
                    this.playSound(IafSounds.ICEDRAGON_BREATH.get(), 4, 1);
                }
                this.stimulateFire(burningTarget.getX() + 0.5F, burningTarget.getY() + 0.5F, burningTarget.getZ() + 0.5F, 1);
            }
        } else {
            this.setBreathingFire(true);
        }
    }

    @Override
    public double getFlightSpeedModifier() {
        return super.getFlightSpeedModifier() * (this.isTouchingWater() ? 0.3F : 1F);
    }

    @Override
    public boolean isAllowedToTriggerFlight() {
        return super.isAllowedToTriggerFlight() && !this.isTouchingWater();
    }

    @Override
    public void spawnBabyParticles() {
        if (this.getWorld().isClient)
            for (int i = 0; i < 5; i++) {
                float radiusAdd = i * 0.15F;
                float headPosX = (float) (this.getX() + 1.8F * this.getRenderSize() * (0.3F + radiusAdd) * MathHelper.cos((float) ((this.getYaw() + 90) * Math.PI / 180)));
                float headPosZ = (float) (this.getZ() + 1.8F * this.getRenderSize() * (0.3F + radiusAdd) * MathHelper.sin((float) ((this.getYaw() + 90) * Math.PI / 180)));
                float headPosY = (float) (this.getY() + 0.5 * this.getRenderSize() * 0.3F);
                this.getWorld().addParticle(new DragonFrostParticleType(this.getScaleFactor()), headPosX, headPosY, headPosZ, 0, 0, 0);
            }
    }

    @Override
    public SoundEvent getBabyFireSound() {
        return SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH;
    }

    @Override
    public ItemStack getSkull() {
        return new ItemStack(IafItems.DRAGON_SKULL_ICE.get());
    }

    @Override
    public boolean useFlyingPathFinder() {
        return (this.isFlying() || this.isTouchingWater()) && this.getControllingPassenger() == null;
    }

    @Override
    public Item getBloodItem() {
        return IafItems.ICE_DRAGON_BLOOD.get();
    }

    @Override
    public Item getFleshItem() {
        return IafItems.ICE_DRAGON_FLESH.get();
    }

    @Override
    public ItemConvertible getHeartItem() {
        return IafItems.ICE_DRAGON_HEART.get();
    }
}
