package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.entity.util.dragon.IafDragonAttacks;
import com.iafenvoy.iceandfire.entity.util.dragon.IafDragonDestructionManager;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LightningDragonEntity extends DragonBaseEntity {
    public static final ResourceLocation FEMALE_LOOT = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "entities/dragon/lightning_dragon_female");
    public static final ResourceLocation MALE_LOOT = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "entities/dragon/lightning_dragon_male");
    public static final ResourceLocation SKELETON_LOOT = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "entities/dragon/lightning_dragon_skeleton");
    private static final EntityDataAccessor<Boolean> HAS_LIGHTNING_TARGET = SynchedEntityData.defineId(LightningDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> LIGHTNING_TARGET_X = SynchedEntityData.defineId(LightningDragonEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LIGHTNING_TARGET_Y = SynchedEntityData.defineId(LightningDragonEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LIGHTNING_TARGET_Z = SynchedEntityData.defineId(LightningDragonEntity.class, EntityDataSerializers.FLOAT);

    public LightningDragonEntity(EntityType<? extends LightningDragonEntity> t, Level worldIn) {
        super(t, worldIn, IafDragonTypes.LIGHTNING, 1, 1 + IafCommonConfig.INSTANCE.dragon.attackDamage.getValue(), IafCommonConfig.INSTANCE.dragon.maxHealth.getValue() * 0.04, IafCommonConfig.INSTANCE.dragon.maxHealth.getValue(), 0.15F, 0.4F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.LAVA, 8.0F);
        ANIMATION_SPEAK = Animation.create(20);
        ANIMATION_BITE = Animation.create(35);
        ANIMATION_SHAKEPREY = Animation.create(65);
        ANIMATION_TAILWHACK = Animation.create(40);
        ANIMATION_FIRECHARGE = Animation.create(30);
        ANIMATION_WINGBLAST = Animation.create(50);
        ANIMATION_ROAR = Animation.create(40);
        ANIMATION_EPIC_ROAR = Animation.create(60);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_LIGHTNING_TARGET, false);
        builder.define(LIGHTNING_TARGET_X, 0.0F);
        builder.define(LIGHTNING_TARGET_Y, 0.0F);
        builder.define(LIGHTNING_TARGET_Z, 0.0F);
    }

    @Override
    public int getStartMetaForType() {
        return 8;
    }

    @Override
    protected boolean shouldTarget(Entity entity) {
        if (entity instanceof DragonBaseEntity && !this.isTame())
            return entity.getType() != this.getType() && this.getBbWidth() >= entity.getBbWidth() && !((DragonBaseEntity) entity).isMobDead();
        return entity instanceof Player || DragonUtils.isDragonTargetable(entity, IafEntityTags.LIGHTNING_DRAGON_TARGETS) || !this.isTame() && DragonUtils.isVillager(entity);
    }

    @Override
    public boolean isTimeToWake() {
        return !this.level().isDay() || this.getCommand() == 2;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Override
    public String getVariantName(int variant) {
        return switch (variant) {
            case 1 -> "amethyst_";
            case 2 -> "copper_";
            case 3 -> "black_";
            default -> "electric_";
        };
    }

    @Override
    public boolean isInvulnerableTo(DamageSource i) {
        if (i.getMsgId().equals(this.level().damageSources().lightningBolt().getMsgId())) {
            this.heal(15F);
            this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 1));
            return true;
        }
        return super.isInvulnerableTo(i);
    }

    public void setHasLightningTarget(boolean lightning_target) {
        this.entityData.set(HAS_LIGHTNING_TARGET, lightning_target);
    }

    public boolean hasLightningTarget() {
        return this.entityData.get(HAS_LIGHTNING_TARGET);
    }

    public void setLightningTargetVec(float x, float y, float z) {
        this.entityData.set(LIGHTNING_TARGET_X, x);
        this.entityData.set(LIGHTNING_TARGET_Y, y);
        this.entityData.set(LIGHTNING_TARGET_Z, z);
    }

    public float getLightningTargetX() {
        return this.entityData.get(LIGHTNING_TARGET_X);
    }

    public float getLightningTargetY() {
        return this.entityData.get(LIGHTNING_TARGET_Y);
    }

    public float getLightningTargetZ() {
        return this.entityData.get(LIGHTNING_TARGET_Z);
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
        if (!this.level().isClientSide && attackTarget != null) {
            if (this.getBoundingBox().inflate(2.5F + this.getRenderSize() * 0.33F, 2.5F + this.getRenderSize() * 0.33F, 2.5F + this.getRenderSize() * 0.33F).intersects(attackTarget.getBoundingBox()))
                this.doHurtTarget(attackTarget);
            if (this.groundAttack == IafDragonAttacks.Ground.FIRE && (this.usingGroundAttack || this.onGround()))
                this.shootFireAtMob(attackTarget);
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
        if (!this.isBreathingFire())
            this.setHasLightningTarget(false);
    }


    @Override
    protected void breathFireAtPos(BlockPos burningTarget) {
        if (this.isBreathingFire()) {
            if (this.isActuallyBreathingFire()) {
                this.setYRot(this.yBodyRot);
                if (this.fireBreathTicks % 7 == 0)
                    this.playSound(IafSounds.LIGHTNINGDRAGON_BREATH.get(), 4, 1);
                this.breathAttack(burningTarget.getX() + 0.5F, burningTarget.getY() + 0.5F, burningTarget.getZ() + 0.5F, false);
            }
        } else
            this.setBreathingFire(true);
    }

    @Override
    public void riderShootFire(Entity controller) {
        if (this.getRandom().nextInt(5) == 0 && !this.isBaby()) {
            if (this.getAnimation() != ANIMATION_FIRECHARGE)
                this.setAnimation(ANIMATION_FIRECHARGE);
            else if (this.getAnimationTick() == 20) {
                this.setYRot(this.yBodyRot);
                Vec3 headVec = this.getHeadPosition();
                this.playSound(IafSounds.LIGHTNINGDRAGON_BREATH_CRACKLE.get(), 4, 1);
                double d2 = controller.getLookAngle().x;
                double d3 = controller.getLookAngle().y;
                double d4 = controller.getLookAngle().z;
                float inaccuracy = 1.0F;
                d2 = d2 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d3 = d3 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d4 = d4 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                LightningDragonChargeEntity entitylargefireball = new LightningDragonChargeEntity(IafEntities.LIGHTNING_DRAGON_CHARGE.get(), this.level(), this, d2, d3, d4);
                entitylargefireball.setPos(headVec.x, headVec.y, headVec.z);
                if (!this.level().isClientSide)
                    this.level().addFreshEntity(entitylargefireball);
            }
        } else {
            if (this.isBreathingFire()) {
                if (this.isActuallyBreathingFire()) {
                    this.setYRot(this.yBodyRot);
                    if (this.fireBreathTicks % 7 == 0)
                        this.playSound(IafSounds.LIGHTNINGDRAGON_BREATH.get(), 4, 1);
                    HitResult mop = this.rayTraceRider(controller, 10 * this.getDragonStage(), 1.0F);
                    if (mop != null)
                        this.breathAttack(mop.getLocation().x, mop.getLocation().y, mop.getLocation().z, false);
                }
            } else
                this.setBreathingFire(true);
        }
    }

    @Override
    public Item getBloodItem() {
        return IafItems.LIGHTNING_DRAGON_BLOOD.get();
    }

    @Override
    public Item getFleshItem() {
        return IafItems.LIGHTNING_DRAGON_FLESH.get();
    }

    @Override
    public ItemLike getHeartItem() {
        return IafItems.LIGHTNING_DRAGON_HEART.get();
    }

    @Override
    public ResourceLocation getDeadLootTable() {
        if (this.getDeathStage() >= (this.getAgeInDays() / 5) / 2)
            return SKELETON_LOOT;
        else
            return this.isMale() ? MALE_LOOT : FEMALE_LOOT;
    }

    private void shootFireAtMob(LivingEntity entity) {
        if (this.usingGroundAttack && this.groundAttack == IafDragonAttacks.Ground.FIRE || !this.usingGroundAttack && (this.airAttack == IafDragonAttacks.Air.SCORCH_STREAM || this.airAttack == IafDragonAttacks.Air.HOVER_BLAST)) {
            if (this.usingGroundAttack && this.getRandom().nextInt(5) == 0 || !this.usingGroundAttack && this.airAttack == IafDragonAttacks.Air.HOVER_BLAST) {
                if (this.getAnimation() != ANIMATION_FIRECHARGE)
                    this.setAnimation(ANIMATION_FIRECHARGE);
                else if (this.getAnimationTick() == 20) {
                    this.setYRot(this.yBodyRot);
                    Vec3 headVec = this.getHeadPosition();
                    double d2 = entity.getX() - headVec.x;
                    double d3 = entity.getY() - headVec.y;
                    double d4 = entity.getZ() - headVec.z;
                    float inaccuracy = 1.0F;
                    d2 = d2 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    d3 = d3 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    d4 = d4 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    this.playSound(IafSounds.LIGHTNINGDRAGON_BREATH.get(), 4, 1);
                    LightningDragonChargeEntity entitylargefireball = new LightningDragonChargeEntity(IafEntities.LIGHTNING_DRAGON_CHARGE.get(), this.level(), this, d2, d3, d4);
                    entitylargefireball.setPos(headVec.x, headVec.y, headVec.z);
                    if (!this.level().isClientSide) this.level().addFreshEntity(entitylargefireball);
                    if (!entity.isAlive()) this.setBreathingFire(false);
                    this.randomizeAttacks();
                }
            } else {
                if (this.isBreathingFire()) {
                    if (this.isActuallyBreathingFire()) {
                        this.setYRot(this.yBodyRot);
                        if (this.tickCount % 5 == 0)
                            this.playSound(IafSounds.LIGHTNINGDRAGON_BREATH.get(), 4, 1);
                        this.breathAttack(entity.getX(), entity.getY(), entity.getZ(), false);
                        if (!entity.isAlive()) {
                            this.setBreathingFire(false);
                            this.randomizeAttacks();
                        }
                    }
                } else
                    this.setBreathingFire(true);
            }
        }
        this.lookAt(entity, 360, 360);
    }

    @Override
    protected void performNormalBreathAttack(double burnX, double burnY, double burnZ) {
        this.burnParticleX = burnX;
        this.burnParticleY = burnY;
        this.burnParticleZ = burnZ;
        Vec3 headPos = this.getHeadPosition();
        double d2 = burnX - headPos.x;
        double d3 = burnY - headPos.y;
        double d4 = burnZ - headPos.z;
        double distance = Math.max(2.5F * Math.sqrt(this.distanceToSqr(burnX, burnY, burnZ)), 0);
        double conqueredDistance = this.burnProgress / 40D * distance;
        int increment = (int) Math.ceil(conqueredDistance / 100);
        for (int i = 0; i < conqueredDistance; i += increment) {
            double progressX = headPos.x + d2 * (i / (float) distance);
            double progressY = headPos.y + d3 * (i / (float) distance);
            double progressZ = headPos.z + d4 * (i / (float) distance);
            if (this.canPositionBeSeen(progressX, progressY, progressZ)) {
                this.setHasLightningTarget(true);
                this.setLightningTargetVec((float) burnX, (float) burnY, (float) burnZ);
            } else if (!this.level().isClientSide) {
                HitResult result = this.level().clip(new ClipContext(
                        new Vec3(this.getX(), this.getY() + this.getEyeHeight(), this.getZ()),
                        new Vec3(progressX, progressY, progressZ), ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE, this));
                Vec3 vec3 = result.getLocation();
                BlockPos pos = BlockPos.containing(vec3);
                IafDragonDestructionManager.destroyAreaBreath(this.level(), pos, this);
                this.setHasLightningTarget(true);
                this.setLightningTargetVec((float) result.getLocation().x, (float) result.getLocation().y, (float) result.getLocation().z);
            }
        }
        if (this.burnProgress >= 40D && this.canPositionBeSeen(burnX, burnY, burnZ)) {
            double spawnX = burnX + (this.random.nextFloat() * 3.0) - 1.5;
            double spawnY = burnY + (this.random.nextFloat() * 3.0) - 1.5;
            double spawnZ = burnZ + (this.random.nextFloat() * 3.0) - 1.5;
            this.setHasLightningTarget(true);
            this.setLightningTargetVec((float) spawnX, (float) spawnY, (float) spawnZ);
            if (!this.level().isClientSide)
                IafDragonDestructionManager.destroyAreaBreath(this.level(), BlockPos.containing(spawnX, spawnY, spawnZ), this);
        }
    }

    @Override
    public Entity createCharge(double velocityX, double velocityY, double velocityZ) {
        this.playSound(IafSounds.LIGHTNINGDRAGON_BREATH_CRACKLE.get(), 4, 1);
        return new LightningDragonChargeEntity(IafEntities.LIGHTNING_DRAGON_CHARGE.get(), this.level(), this, velocityX, velocityY, velocityZ);
    }

    @Override
    public ParticleOptions createBreathParticle() {
        return null;//Unused
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isTeen() ? IafSounds.LIGHTNINGDRAGON_TEEN_IDLE.get() : this.isMature() ? IafSounds.LIGHTNINGDRAGON_ADULT_IDLE.get() : IafSounds.LIGHTNINGDRAGON_CHILD_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return this.isTeen() ? IafSounds.LIGHTNINGDRAGON_TEEN_HURT.get() : this.isMature() ? IafSounds.LIGHTNINGDRAGON_ADULT_HURT.get() : IafSounds.LIGHTNINGDRAGON_CHILD_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isTeen() ? IafSounds.LIGHTNINGDRAGON_TEEN_DEATH.get() : this.isMature() ? IafSounds.LIGHTNINGDRAGON_ADULT_DEATH.get() : IafSounds.LIGHTNINGDRAGON_CHILD_DEATH.get();
    }

    @Override
    public SoundEvent getRoarSound() {
        return this.isTeen() ? IafSounds.LIGHTNINGDRAGON_TEEN_ROAR.get() : this.isMature() ? IafSounds.LIGHTNINGDRAGON_ADULT_ROAR.get() : IafSounds.LIGHTNINGDRAGON_CHILD_ROAR.get();
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, ANIMATION_EAT, ANIMATION_SPEAK, ANIMATION_BITE, ANIMATION_SHAKEPREY, ANIMATION_TAILWHACK, ANIMATION_FIRECHARGE, ANIMATION_WINGBLAST, ANIMATION_ROAR, ANIMATION_EPIC_ROAR};
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == IafItems.LIGHTNING_STEW.get();
    }

    @Override
    protected void spawnDeathParticles() {
        for (int k = 0; k < 3; ++k) {
            double d2 = this.random.nextGaussian() * 0.02D;
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            if (this.level().isClientSide) {
                this.level().addParticle(ParticleTypes.RAIN,
                        this.getX() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(),
                        this.getY() + this.random.nextFloat() * this.getBbHeight(),
                        this.getZ() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(), d2, d0, d1);
            }
        }
    }

    @Override
    public void spawnBabyParticles() {
        for (int i = 0; i < 5; i++) {
            float radiusAdd = i * 0.15F;
            float headPosX = (float) (this.getX() + 1.8F * this.getRenderSize() * (0.3F + radiusAdd) * Mth.cos((float) ((this.getYRot() + 90) * Math.PI / 180)));
            float headPosZ = (float) (this.getY() + 1.8F * this.getRenderSize() * (0.3F + radiusAdd) * Mth.sin((float) ((this.getYRot() + 90) * Math.PI / 180)));
            float headPosY = (float) (this.getZ() + 0.5 * this.getRenderSize() * 0.3F);
            this.level().addParticle(ParticleTypes.LARGE_SMOKE, headPosX, headPosY, headPosZ, 0, 0, 0);
        }
    }

    @Override
    public Item getSkull() {
        return IafItems.DRAGON_SKULL_LIGHTNING.get();
    }
}
