package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.entity.util.dragon.IafDragonAttacks;
import com.iafenvoy.iceandfire.particle.DragonFrostParticleType;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class IceDragonEntity extends DragonBaseEntity {
    public static final ResourceLocation FEMALE_LOOT = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "entities/dragon/ice_dragon_female");
    public static final ResourceLocation MALE_LOOT = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "entities/dragon/ice_dragon_male");
    public static final ResourceLocation SKELETON_LOOT = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "entities/dragon/ice_dragon_skeleton");
    private static final EntityDataAccessor<Boolean> SWIMMING = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.BOOLEAN);

    public IceDragonEntity(EntityType<? extends IceDragonEntity> t, Level worldIn) {
        super(t, worldIn, IafDragonTypes.ICE, 1, 1 + IafCommonConfig.INSTANCE.dragon.attackDamage.getValue(), IafCommonConfig.INSTANCE.dragon.maxHealth.getValue() * 0.04, IafCommonConfig.INSTANCE.dragon.maxHealth.getValue(), 0.15F, 0.4F);
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
        if (entity instanceof DragonBaseEntity && !this.isTame()) {
            return entity.getType() != this.getType() && this.getBbWidth() >= entity.getBbWidth() && !((DragonBaseEntity) entity).isMobDead();
        }
        return entity instanceof Player || DragonUtils.isDragonTargetable(entity, IafEntityTags.ICE_DRAGON_TARGETS) || !this.isTame() && DragonUtils.isVillager(entity);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SWIMMING, false);
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
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Swimming", this.isSwimming());
        compound.putInt("SwimmingTicks", this.ticksSwiming);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSwimming(compound.getBoolean("Swimming"));
        this.ticksSwiming = compound.getInt("SwimmingTicks");
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        this.getLookControl().setLookAt(entityIn, 30.0F, 30.0F);
        if (!this.isPlayingAttackAnimation()) {
            switch (this.groundAttack) {
                case BITE -> this.setAnimation(ANIMATION_BITE);
                case TAIL_WHIP -> this.setAnimation(ANIMATION_TAILWHACK);
                case SHAKE_PREY -> {
                    boolean flag = false;
                    if (new Random().nextInt(2) == 0 && this.isDirectPathBetweenPoints(this, this.position().add(0, this.getBbHeight() / 2, 0), entityIn.position().add(0, entityIn.getBbHeight() / 2, 0)) &&
                            entityIn.getBbWidth() < this.getBbWidth() * 0.5F && this.getControllingPassenger() == null && this.getDragonStage() > 1 && !(entityIn instanceof DragonBaseEntity) && !DragonUtils.isAnimaniaMob(entityIn)) {
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
    public void aiStep() {
        super.aiStep();
        LivingEntity attackTarget = this.getTarget();
        if (!this.level().isClientSide && this.isInLava() && this.isAllowedToTriggerFlight() && !this.isModelDead()) {
            this.setHovering(true);
            this.setInSittingPose(false);
            this.setOrderedToSit(false);
            this.flyHovering = 0;
            this.flyTicks = 0;
        }
        if (!this.level().isClientSide && attackTarget != null) {
            if (this.getBoundingBox().inflate(0 + this.getRenderSize() * 0.33F, 0 + this.getRenderSize() * 0.33F, 0 + this.getRenderSize() * 0.33F).intersects(attackTarget.getBoundingBox())) {
                this.doHurtTarget(attackTarget);
            }
            if (this.groundAttack == IafDragonAttacks.Ground.FIRE && (this.usingGroundAttack || this.onGround())) {
                this.shootIceAtMob(attackTarget);
            }
            if (this.airAttack == IafDragonAttacks.Air.TACKLE && !this.usingGroundAttack && this.distanceToSqr(attackTarget) < 100) {
                double difX = attackTarget.getX() - this.getX();
                double difY = attackTarget.getY() + attackTarget.getBbHeight() - this.getY();
                double difZ = attackTarget.getZ() - this.getZ();
                this.setDeltaMovement(this.getDeltaMovement().add(difX * 0.1D, difY * 0.1D, difZ * 0.1D));

                if (this.getBoundingBox().inflate(1 + this.getRenderSize() * 0.5F, 1 + this.getRenderSize() * 0.5F, 1 + this.getRenderSize() * 0.5F).intersects(attackTarget.getBoundingBox())) {
                    this.doHurtTarget(attackTarget);
                    this.usingGroundAttack = true;
                    this.randomizeAttacks();
                    this.setFlying(false);
                    this.setHovering(false);
                }
            }
        }
        boolean swimming = this.isInWater();
        this.prevSwimProgress = this.swimProgress;
        if (swimming && this.swimProgress < 20.0F) {
            this.swimProgress += 0.5F;
        } else if (!swimming && this.swimProgress > 0.0F) {
            this.swimProgress -= 0.5F;
        }
        if (this.isInWater() && !this.isSwimming() && (!this.isFlying() && !this.isHovering() || this.flyTicks > 100)) {
            this.setSwimming(true);
            this.setHovering(false);
            this.setFlying(false);
            this.flyTicks = 0;
            this.ticksSwiming = 0;
        }
        if ((!this.isInWater() || this.isHovering() || this.isFlying()) && this.isSwimming()) {
            this.setSwimming(false);
            this.ticksSwiming = 0;
        }
        if (this.isSwimming() && !this.isModelDead()) {
            this.ticksSwiming++;
            if (this.isInWater() && (this.ticksSwiming > 4000 || this.getTarget() != null && this.isInWater() != this.getTarget().isInWater()) && !this.isBaby() && !this.isHovering() && !this.isFlying()) {
                this.setHovering(true);
                this.jumpFromGround();
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.8D, 0.0D));
                this.setSwimming(false);
            }
        }
        if (!this.level().isClientSide && this.getControllingPassenger() == null && (this.isHovering() && !this.isFlying() && this.isInWater())) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.2D, 0.0D));
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
                this.setYRot(this.yBodyRot);
                Vec3 headVec = this.getHeadPosition();
                this.playSound(IafSounds.ICEDRAGON_BREATH.get(), 4, 1);
                double d2 = controller.getLookAngle().x;
                double d3 = controller.getLookAngle().y;
                double d4 = controller.getLookAngle().z;
                float inaccuracy = 1.0F;
                d2 = d2 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d3 = d3 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d4 = d4 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                IceDragonChargeEntity entitylargefireball = new IceDragonChargeEntity(
                        IafEntities.ICE_DRAGON_CHARGE.get(), this.level(), this, d2, d3, d4);
                float size;
                if (!this.isBaby()) {
                    this.isMature();
                }
                entitylargefireball.setPos(headVec.x, headVec.y, headVec.z);
                if (!this.level().isClientSide) {
                    this.level().addFreshEntity(entitylargefireball);
                }

            }
        } else {
            if (this.isBreathingFire()) {
                if (this.isActuallyBreathingFire()) {
                    this.setYRot(this.yBodyRot);
                    if (this.tickCount % 5 == 0) {
                        this.playSound(IafSounds.ICEDRAGON_BREATH.get(), 4, 1);
                    }
                    HitResult mop = this.rayTraceRider(controller, 10 * this.getDragonStage(), 1.0F);
                    if (mop != null) {
                        this.breathAttack(mop.getLocation().x, mop.getLocation().y, mop.getLocation().z, false);
                    }
                }
            } else {
                this.setBreathingFire(true);
            }
        }
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    @Override
    public void onInsideBubbleColumn(boolean pDownwards) {
        // Disable bubble column drag for elder dragons
        if (this.getDragonStage() < 2) {
            super.onInsideBubbleColumn(pDownwards);
        }
    }

    @Override
    public void onAboveBubbleCol(boolean pDownwards) {
        // Disable bubble column drag for elder dragons
        if (this.getDragonStage() < 2) {
            super.onAboveBubbleCol(pDownwards);
        }
    }

    @Override
    public void travel(@NotNull Vec3 pTravelVector) {
        if (/* Always false on the server */ this.isInWater()) {
            // In water special
            if (this.isEffectiveAi() && this.getControllingPassenger() == null) {
                // Ice dragons swim faster
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
//                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            } else if (this.allowLocalMotionControl && this.getControllingPassenger() != null && !this.isHovering() && !this.isFlying()) {
                LivingEntity rider = this.getControllingPassenger();

                float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
                // Bigger difference in speed for young and elder dragons
                float waterSpeedMod = (float) (0.42f + 0.1 * Mth.map(speed, this.minimumSpeed, this.maximumSpeed, 0f, 1.5f));
                speed *= waterSpeedMod;
                speed *= rider.isSprinting() ? 1.5f : 1.0f;

                float vertical = 0f;
                if (this.isGoingUp() && !this.isGoingDown()) {
                    vertical = 1f;
                } else if (this.isGoingDown() && !this.isGoingUp()) {
                    vertical = -1f;
                } else if (this.isGoingUp() && this.isGoingDown() && this.isControlledByLocalInstance()) {
                    // Try floating
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0f, 0.5f, 1.0f));
                }

                Vec3 travelVector = new Vec3(
                        rider.xxa,
                        vertical,
                        rider.zza
                );
                if (this.isControlledByLocalInstance()) {
                    this.setSpeed(speed);

                    this.moveRelative(this.getSpeed(), travelVector);
                    this.move(MoverType.SELF, this.getDeltaMovement());

                    Vec3 currentMotion = this.getDeltaMovement();
                    if (this.horizontalCollision) {
                        currentMotion = new Vec3(currentMotion.x, 0.2D, currentMotion.z);
                    }
                    this.setDeltaMovement(currentMotion.scale(0.9D));

                    this.calculateEntityAnimation(false);
                } else {
                    this.setDeltaMovement(Vec3.ZERO);
                }
                this.tryCheckInsideBlocks();
            } else {
                super.travel(pTravelVector);
            }

        }
        // Over water special
        else if (this.allowLocalMotionControl && this.getControllingPassenger() != null && !this.isHovering() && !this.isFlying()
                && this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getFluidState().is(FluidTags.WATER)) {
            // Movement when walking on the water, mainly used for not slowing down when jumping out of water
            LivingEntity rider = this.getControllingPassenger();

            double forward = rider.zza;
            double strafing = rider.xxa;
            // Inherit y motion for dropping
            double vertical = pTravelVector.y;
            float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);

            float groundSpeedModifier = (float) (1.8F * this.getFlightSpeedModifier());
            speed *= groundSpeedModifier;
            // Try to match the original riding speed
//            forward *= speed;
            // Faster sprint
            forward *= rider.isSprinting() ? 1.2f : 1.0f;
            // Slower going back
            forward *= rider.zza > 0 ? 1.0f : 0.2f;
            // Slower going sideway
            strafing *= 0.05f;

            if (this.isControlledByLocalInstance()) {
                this.setSpeed(speed);

                // Vanilla walking behavior includes going up steps
                super.travel(new Vec3(strafing, vertical, forward));

                Vec3 currentMotion = this.getDeltaMovement();
                if (this.horizontalCollision) {
                    currentMotion = new Vec3(currentMotion.x, 0.2D, currentMotion.z);
                }
                this.setDeltaMovement(currentMotion.scale(1.0D));
            } else {
                this.setDeltaMovement(Vec3.ZERO);
            }
            this.tryCheckInsideBlocks();
//            this.updatePitch(this.yOld - this.getY());
        } else {
            super.travel(pTravelVector);
        }
    }

    @Override
    public ResourceLocation getDeadLootTable() {
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
                    this.setYRot(this.yBodyRot);
                    Vec3 headVec = this.getHeadPosition();
                    double d2 = entity.getX() - headVec.x;
                    double d3 = entity.getY() - headVec.y;
                    double d4 = entity.getZ() - headVec.z;
                    float inaccuracy = 1.0F;
                    d2 = d2 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    d3 = d3 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    d4 = d4 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    this.playSound(IafSounds.ICEDRAGON_BREATH.get(), 4, 1);
                    IceDragonChargeEntity entitylargefireball = new IceDragonChargeEntity(
                            IafEntities.ICE_DRAGON_CHARGE.get(), this.level(), this, d2, d3, d4);
                    float size;
                    if (!this.isBaby()) {
                        this.isMature();
                    }
                    entitylargefireball.setPos(headVec.x, headVec.y, headVec.z);
                    if (!this.level().isClientSide) {
                        this.level().addFreshEntity(entitylargefireball);
                    }
                    if (!entity.isAlive()) {
                        this.setBreathingFire(false);
                        this.usingGroundAttack = true;
                    }
                }
            } else {
                if (this.isBreathingFire()) {
                    if (this.isActuallyBreathingFire()) {
                        this.setYRot(this.yBodyRot);
                        if (this.tickCount % 5 == 0) {
                            this.playSound(IafSounds.ICEDRAGON_BREATH.get(), 4, 1);
                        }
                        this.breathAttack(entity.getX(), entity.getY(), entity.getZ(), false);
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
        this.lookAt(entity, 360, 360);
    }

    @Override
    public Entity createCharge(double velocityX, double velocityY, double velocityZ) {
        this.playSound(IafSounds.ICEDRAGON_BREATH.get(), 4, 1);
        return new IceDragonChargeEntity(IafEntities.ICE_DRAGON_CHARGE.get(), this.level(), this, velocityX, velocityY, velocityZ);
    }

    @Override
    public ParticleOptions createBreathParticle() {
        return new DragonFrostParticleType(this.getAgeScale());
    }

    @Override
    public boolean isSwimming() {
        if (this.level().isClientSide) {
            boolean swimming = this.entityData.get(SWIMMING);
            this.isSwimming = swimming;
            return swimming;
        }
        return this.isSwimming;
    }

    @Override
    public void setSwimming(boolean swimming) {
        this.entityData.set(SWIMMING, swimming);
        if (!this.level().isClientSide) {
            this.isSwimming = swimming;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isTeen() ? IafSounds.ICEDRAGON_TEEN_IDLE.get() : this.isMature() ? IafSounds.ICEDRAGON_ADULT_IDLE.get() : IafSounds.ICEDRAGON_CHILD_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return this.isTeen() ? IafSounds.ICEDRAGON_TEEN_HURT.get() : this.isMature() ? IafSounds.ICEDRAGON_ADULT_HURT.get() : IafSounds.ICEDRAGON_CHILD_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isTeen() ? IafSounds.ICEDRAGON_TEEN_DEATH.get() : this.isMature() ? IafSounds.ICEDRAGON_ADULT_DEATH.get() : IafSounds.ICEDRAGON_CHILD_DEATH.get();
    }

    @Override
    public SoundEvent getRoarSound() {
        return this.isTeen() ? IafSounds.ICEDRAGON_TEEN_ROAR.get() : this.isMature() ? IafSounds.ICEDRAGON_ADULT_ROAR.get() : IafSounds.ICEDRAGON_CHILD_ROAR.get();
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, ANIMATION_EAT, ANIMATION_SPEAK, ANIMATION_BITE, ANIMATION_SHAKEPREY, ANIMATION_TAILWHACK, ANIMATION_FIRECHARGE, ANIMATION_WINGBLAST, ANIMATION_ROAR};
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == IafItems.FROST_STEW.get();
    }

    @Override
    protected void breathFireAtPos(BlockPos burningTarget) {
        if (this.isBreathingFire()) {
            if (this.isActuallyBreathingFire()) {
                this.setYRot(this.yBodyRot);
                if (this.tickCount % 5 == 0) {
                    this.playSound(IafSounds.ICEDRAGON_BREATH.get(), 4, 1);
                }
                this.breathAttack(burningTarget.getX() + 0.5F, burningTarget.getY() + 0.5F, burningTarget.getZ() + 0.5F, false);
            }
        } else {
            this.setBreathingFire(true);
        }
    }

    @Override
    public double getFlightSpeedModifier() {
        return super.getFlightSpeedModifier() * (this.isInWater() ? 0.3F : 1F);
    }

    @Override
    public boolean isAllowedToTriggerFlight() {
        return super.isAllowedToTriggerFlight() && !this.isInWater();
    }

    @Override
    public void spawnBabyParticles() {
        if (this.level().isClientSide)
            for (int i = 0; i < 5; i++) {
                float radiusAdd = i * 0.15F;
                float headPosX = (float) (this.getX() + 1.8F * this.getRenderSize() * (0.3F + radiusAdd) * Mth.cos((float) ((this.getYRot() + 90) * Math.PI / 180)));
                float headPosZ = (float) (this.getZ() + 1.8F * this.getRenderSize() * (0.3F + radiusAdd) * Mth.sin((float) ((this.getYRot() + 90) * Math.PI / 180)));
                float headPosY = (float) (this.getY() + 0.5 * this.getRenderSize() * 0.3F);
                this.level().addParticle(new DragonFrostParticleType(this.getAgeScale()), headPosX, headPosY, headPosZ, 0, 0, 0);
            }
    }

    //Required for proper egg drop
    @Override
    public int getStartMetaForType() {
        return 4;
    }

    @Override
    public SoundEvent getBabyFireSound() {
        return SoundEvents.BOTTLE_FILL_DRAGONBREATH;
    }

    @Override
    public Item getSkull() {
        return IafItems.DRAGON_SKULL_ICE.get();
    }

    @Override
    public boolean useFlyingPathFinder() {
        return (this.isFlying() || this.isInWater()) && this.getControllingPassenger() == null;
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
    public ItemLike getHeartItem() {
        return IafItems.ICE_DRAGON_HEART.get();
    }
}
