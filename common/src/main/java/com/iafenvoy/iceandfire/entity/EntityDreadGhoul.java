package com.iafenvoy.iceandfire.entity;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.entity.ai.DreadAITargetNonDread;
import com.iafenvoy.iceandfire.entity.util.IAnimalFear;
import com.iafenvoy.iceandfire.entity.util.IDreadMob;
import com.iafenvoy.iceandfire.entity.util.IVillagerFear;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.block.Block;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class EntityDreadGhoul extends EntityDreadMob implements IAnimatedEntity, IVillagerFear, IAnimalFear {
    public static final Animation ANIMATION_SPAWN = Animation.create(40);
    public static final Animation ANIMATION_SLASH = Animation.create(25);
    private static final TrackedData<Float> SCALE = DataTracker.registerData(EntityDreadGhoul.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(EntityDreadGhoul.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SCREAMS = DataTracker.registerData(EntityDreadGhoul.class, TrackedDataHandlerRegistry.INTEGER);
    private static final float INITIAL_WIDTH = 0.6F;
    private static final float INITIAL_HEIGHT = 1.8F;
    private int animationTick;
    private Animation currentAnimation;
    private int hostileTicks = 0;
    private float firstWidth = 1.0F;
    private float firstHeight = 1.0F;

    public EntityDreadGhoul(EntityType<? extends EntityDreadGhoul> type, World worldIn) {
        super(type, worldIn);
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D)
                //FOLLOW RANGE
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 128.0D)
                //ARMOR
                .add(EntityAttributes.GENERIC_ARMOR, 4.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.5D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, IDreadMob.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, DragonUtils::canHostilesTarget));
        this.targetSelector.add(3, new DreadAITargetNonDread(this, LivingEntity.class, false, (Predicate<LivingEntity>) DragonUtils::canHostilesTarget));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, 0);
        builder.add(SCREAMS, 0);
        builder.add(SCALE, 1F);
    }

    public float getSize() {
        return this.dataTracker.get(SCALE);
    }

    public void setSize(float scale) {
        this.dataTracker.set(SCALE, scale);
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_SLASH);
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
        if (this.getAnimation() == ANIMATION_SPAWN && this.getAnimationTick() < 30) {
            BlockState belowBlock = this.getWorld().getBlockState(this.getBlockPos().down());
            if (belowBlock.getBlock() != Blocks.AIR) {
                for (int i = 0; i < 5; i++) {
                    this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, belowBlock), this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getBoundingBox().minY, this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
                }
            }
            this.setVelocity(0, this.getVelocity().y, this.getVelocity().z);
        }
        if (attackTarget != null && this.distanceTo(attackTarget) < 4 && this.canSee(attackTarget)) {
            if (this.getAnimation() == NO_ANIMATION) {
                this.setAnimation(ANIMATION_SLASH);
            }
            this.lookAtEntity(attackTarget, 360, 80);
            if (this.getAnimation() == ANIMATION_SLASH && (this.getAnimationTick() == 9 || this.getAnimationTick() == 19)) {
                attackTarget.damage(this.getWorld().getDamageSources().mobAttack(this), (float) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue());
                attackTarget.takeKnockback(0.25F, this.getX() - attackTarget.getX(), this.getZ() - attackTarget.getZ());
            }
        }
        if (!this.getWorld().isClient) {
            if (this.getTarget() != null) {
                this.hostileTicks++;
                if (this.getScreamStage() == 0) {
                    if (this.hostileTicks > 20) {
                        this.setScreamStage(1);
                    }
                } else {
                    if (this.age % 20 < 10) {
                        this.setScreamStage(1);
                    } else {
                        this.setScreamStage(2);
                    }
                }
            } else {
                if (this.getScreamStage() > 0) {
                    if (this.age % 20 < 10 && this.getScreamStage() == 2) {
                        this.setScreamStage(1);
                    } else {
                        this.setScreamStage(0);
                    }
                }
                this.hostileTicks = 0;
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("ScreamStage", this.getScreamStage());
        compound.putFloat("DreadScale", this.getSize());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setScreamStage(compound.getInt("ScreamStage"));
        this.setSize(compound.getFloat("DreadScale"));
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public int getScreamStage() {
        return this.dataTracker.get(SCREAMS);
    }

    public void setScreamStage(int screamStage) {
        this.dataTracker.set(SCREAMS, screamStage);
    }

    @Override
    public float getScaleFactor() {
        return this.getSize();
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn) {
        EntityData data = super.initialize(worldIn, difficultyIn, reason, spawnDataIn);
        this.setAnimation(ANIMATION_SPAWN);
        this.setVariant(this.random.nextInt(3));
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
        return new Animation[]{ANIMATION_SPAWN, ANIMATION_SLASH};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return IafSounds.DREAD_GHOUL_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_STEP, 0.15F, 1.0F);
    }

    @Override
    public float getSoundPitch() {
        return super.getSoundPitch() * 0.70F;
    }
}