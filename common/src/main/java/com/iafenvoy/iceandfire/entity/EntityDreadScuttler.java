package com.iafenvoy.iceandfire.entity;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.entity.ai.DreadAITargetNonDread;
import com.iafenvoy.iceandfire.entity.util.IAnimalFear;
import com.iafenvoy.iceandfire.entity.util.IDreadMob;
import com.iafenvoy.iceandfire.entity.util.IVillagerFear;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class EntityDreadScuttler extends EntityDreadMob implements IAnimatedEntity, IVillagerFear, IAnimalFear {
    public static final Animation ANIMATION_SPAWN = Animation.create(40);
    public static final Animation ANIMATION_BITE = Animation.create(15);
    private static final TrackedData<Float> SCALE = DataTracker.registerData(EntityDreadScuttler.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Byte> CLIMBING = DataTracker.registerData(EntityDreadScuttler.class, TrackedDataHandlerRegistry.BYTE);
    private static final float INITIAL_WIDTH = 1.5F;
    private static final float INITIAL_HEIGHT = 1.3F;
    private int animationTick;
    private Animation currentAnimation;
    private float firstWidth = -1.0F;
    private float firstHeight = -1.0F;

    public EntityDreadScuttler(EntityType<? extends EntityDreadScuttler> type, World worldIn) {
        super(type, worldIn);
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.34D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0D)
                //FOLLOW RANGE
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0D)
                //ARMOR
                .add(EntityAttributes.GENERIC_ARMOR, 10.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, IDreadMob.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, (Predicate<LivingEntity>) DragonUtils::canHostilesTarget));
        this.targetSelector.add(3, new DreadAITargetNonDread(this, LivingEntity.class, false, (Predicate<LivingEntity>) entity -> entity instanceof LivingEntity && DragonUtils.canHostilesTarget(entity)));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CLIMBING, (byte) 0);
        this.dataTracker.startTracking(SCALE, 1F);
    }

    public float getSize() {
        return this.dataTracker.get(SCALE);
    }

    public void setSize(float scale) {
        this.dataTracker.set(SCALE, scale);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putFloat("Scale", this.getSize());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setSize(compound.getFloat("Scale"));
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_BITE);
        }
        return true;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        LivingEntity attackTarget = this.getTarget();
        if (Math.abs(this.firstWidth - INITIAL_WIDTH * this.getSize()) > 0.01F || Math.abs(this.firstHeight - INITIAL_HEIGHT * this.getSize()) > 0.01F) {
            this.firstWidth = INITIAL_WIDTH * this.getSize();
            this.firstHeight = INITIAL_HEIGHT * this.getSize();
        }
        if (!this.getWorld().isClient) {
            this.setBesideClimbableBlock(this.horizontalCollision);
        }
        if (this.getAnimation() == ANIMATION_SPAWN && this.getAnimationTick() < 30) {
            BlockState belowBlock = this.getWorld().getBlockState(this.getBlockPos().down());
            if (belowBlock.getBlock() != Blocks.AIR) {
                for (int i = 0; i < 5; i++) {
                    this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, belowBlock), this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getBoundingBox().minY, this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
                }
            }
            this.setVelocity(0, this.getVelocity().y, 0);
        }
        if (attackTarget != null && this.distanceTo(attackTarget) < 4 && this.canSee(attackTarget)) {
            if (this.getAnimation() == NO_ANIMATION) {
                this.setAnimation(ANIMATION_BITE);
            }
            this.lookAtEntity(attackTarget, 360, 80);
            if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 6) {
                attackTarget.damage(this.getWorld().getDamageSources().mobAttack(this), (float) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue());
                attackTarget.takeKnockback(0.25F, this.getX() - attackTarget.getX(), this.getZ() - attackTarget.getZ());
            }
        }

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public boolean isClimbing() {
        return this.isBesideClimbableBlock();
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance potioneffectIn) {
        return potioneffectIn.getEffectType() != StatusEffects.POISON && super.canHaveStatusEffect(potioneffectIn);
    }

    public boolean isBesideClimbableBlock() {
        return (this.dataTracker.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.dataTracker.get(CLIMBING);

        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.dataTracker.set(CLIMBING, b0);
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        EntityData data = super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setAnimation(ANIMATION_SPAWN);
        this.setSize(0.5F + this.random.nextFloat() * 1.15F);
        return data;
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
        return new Animation[]{ANIMATION_SPAWN, ANIMATION_BITE};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    public Entity getCommander() {
        return null;
    }

    @Override
    public boolean isTeammate(Entity entityIn) {
        return entityIn instanceof IDreadMob || super.isTeammate(entityIn);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SPIDER_DEATH;
    }

    @Override
    public float getSoundPitch() {
        return super.getSoundPitch() * 0.70F;
    }

    @Override
    public float getScaleFactor() {
        return this.getSize();
    }

}