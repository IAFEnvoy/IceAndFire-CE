package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.util.*;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class EntityHydra extends HostileEntity implements IAnimatedEntity, IMultipartEntity, IVillagerFear, IAnimalFear, IHasCustomizableAttributes {
    public static final int HEADS = 9;
    public static final double HEAD_HEALTH_THRESHOLD = 20;
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(EntityHydra.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> HEAD_COUNT = DataTracker.registerData(EntityHydra.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SEVERED_HEAD = DataTracker.registerData(EntityHydra.class, TrackedDataHandlerRegistry.INTEGER);
    private static final float[][] ROTATE = new float[][]{
            {0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 1 total heads
            {10F, -10F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 2 total heads
            {10F, 0F, -10F, 0F, 0F, 0F, 0F, 0F, 0F},// 3 total heads
            {25F, 10F, -10F, -25F, 0F, 0F, 0F, 0F, 0F},//etc...
            {30F, 15F, 0F, -15F, -30F, 0F, 0F, 0F, 0F},
            {40F, 25F, 5F, -5F, -25F, -40F, 0F, 0F, 0F},
            {40F, 30F, 15F, 0F, -15F, -30F, -40F, 0F, 0F},
            {45F, 30F, 20F, 5F, -5F, -20F, -30F, -45F, 0F},
            {50F, 37F, 25F, 15F, 0, -15F, -25F, -37F, -50F},
    };
    public final boolean[] isStriking = new boolean[HEADS];
    public final float[] strikingProgress = new float[HEADS];
    public final float[] prevStrikeProgress = new float[HEADS];
    public final boolean[] isBreathing = new boolean[HEADS];
    public final float[] speakingProgress = new float[HEADS];
    public final float[] prevSpeakingProgress = new float[HEADS];
    public final float[] breathProgress = new float[HEADS];
    public final float[] prevBreathProgress = new float[HEADS];
    public final int[] breathTicks = new int[HEADS];
    public final float[] headDamageTracker = new float[HEADS];
    private final float headDamageThreshold;
    private int animationTick;
    private Animation currentAnimation;
    private EntityHydraHead[] headBoxes = new EntityHydraHead[HEADS * 9];
    private int strikeCooldown = 0;
    private int breathCooldown = 0;
    private int lastHitHead = 0;
    private int prevHeadCount = -1;
    private int regrowHeadCooldown = 0;
    private boolean onlyRegrowOneHeadNotTwo = false;
    private boolean multipartLoaded;

    public EntityHydra(EntityType<EntityHydra> type, World worldIn) {
        super(type, worldIn);
        this.multipartLoaded = false;
        this.headDamageThreshold = Math.max(5, IafCommonConfig.INSTANCE.hydra.maxHealth.getValue().floatValue() * 0.08F);
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, IafCommonConfig.INSTANCE.hydra.maxHealth.getValue())
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D)
                //ARMOR
                .add(EntityAttributes.GENERIC_ARMOR, 1.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(IafCommonConfig.INSTANCE.hydra.maxHealth.getValue());
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, entity -> DragonUtils.isAlive(entity) && !(entity instanceof Monster) || entity instanceof IBlacklistedFromStatues blacklisted && blacklisted.canBeTurnedToStone()));
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
        return false;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        LivingEntity attackTarget = this.getTarget();
        if (attackTarget != null && this.canSee(attackTarget)) {
            int index = this.random.nextInt(this.getHeadCount());
            if (!this.isBreathing[index] && !this.isStriking[index]) {
                if (this.distanceTo(attackTarget) < 6) {
                    if (this.strikeCooldown == 0 && this.strikingProgress[index] == 0) {
                        this.isBreathing[index] = false;
                        this.isStriking[index] = true;
                        this.getWorld().sendEntityStatus(this, (byte) (40 + index));
                        this.strikeCooldown = 3;
                    }
                } else if (this.random.nextBoolean() && this.breathCooldown == 0) {
                    this.isBreathing[index] = true;
                    this.isStriking[index] = false;
                    this.getWorld().sendEntityStatus(this, (byte) (50 + index));
                    this.breathCooldown = 15;
                }
            }
        }
        for (int i = 0; i < HEADS; i++) {
            boolean striking = this.isStriking[i];
            boolean breathing = this.isBreathing[i];
            this.prevStrikeProgress[i] = this.strikingProgress[i];
            if (striking && this.strikingProgress[i] > 9) {
                this.isStriking[i] = false;
                if (attackTarget != null && this.distanceTo(attackTarget) < 6) {
                    attackTarget.damage(this.getWorld().getDamageSources().mobAttack(this), (float) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue());
                    attackTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 3, false, false));
                    attackTarget.takeKnockback(0.25F, this.getX() - attackTarget.getX(), this.getZ() - attackTarget.getZ());
                }
            }
            if (breathing) {
                if (this.age % 7 == 0 && attackTarget != null && i < this.getHeadCount()) {
                    Vec3d Vector3d = this.getRotationVec(1.0F);
                    if (this.random.nextFloat() < 0.2F)
                        this.playSound(IafSounds.HYDRA_SPIT.get(), this.getSoundVolume(), this.getSoundPitch());
                    double headPosX = this.headBoxes[i].getX() + Vector3d.x;
                    double headPosY = this.headBoxes[i].getY() + 1.3F;
                    double headPosZ = this.headBoxes[i].getZ() + Vector3d.z;
                    double d2 = attackTarget.getX() - headPosX + this.random.nextGaussian() * 0.4D;
                    double d3 = attackTarget.getY() + attackTarget.getStandingEyeHeight() - headPosY + this.random.nextGaussian() * 0.4D;
                    double d4 = attackTarget.getZ() - headPosZ + this.random.nextGaussian() * 0.4D;
                    EntityHydraBreath entitylargefireball = new EntityHydraBreath(IafEntities.HYDRA_BREATH.get(), this.getWorld(), this, d2, d3, d4);
                    entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
                    if (!this.getWorld().isClient)
                        this.getWorld().spawnEntity(entitylargefireball);
                }
                if (this.isBreathing[i] && (attackTarget == null || !attackTarget.isAlive() || this.breathTicks[i] > 60) && !this.getWorld().isClient) {
                    this.isBreathing[i] = false;
                    this.breathTicks[i] = 0;
                    this.breathCooldown = 15;
                    this.getWorld().sendEntityStatus(this, (byte) (60 + i));
                }
                this.breathTicks[i]++;
            } else
                this.breathTicks[i] = 0;
            if (striking && this.strikingProgress[i] < 10.0F)
                this.strikingProgress[i] += 2.5F;
            else if (!striking && this.strikingProgress[i] > 0.0F)
                this.strikingProgress[i] -= 2.5F;
            this.prevSpeakingProgress[i] = this.speakingProgress[i];
            if (this.speakingProgress[i] > 0.0F)
                this.speakingProgress[i] -= 0.1F;
            this.prevBreathProgress[i] = this.breathProgress[i];
            if (breathing && this.breathProgress[i] < 10.0F)
                this.breathProgress[i] += 1.0F;
            else if (!breathing && this.breathProgress[i] > 0.0F)
                this.breathProgress[i] -= 1.0F;
        }
        if (this.strikeCooldown > 0)
            this.strikeCooldown--;
        if (this.breathCooldown > 0)
            this.breathCooldown--;
        if (this.getHeadCount() == 1 && this.getSeveredHead() != -1)
            this.setSeveredHead(-1);
        if (this.getHeadCount() == 1 && !this.isOnFire()) {
            this.setHeadCount(2);
            this.setSeveredHead(1);
            this.onlyRegrowOneHeadNotTwo = true;
        }

        if (this.getSeveredHead() != -1 && this.getSeveredHead() < this.getHeadCount()) {
            this.setSeveredHead(MathHelper.clamp(this.getSeveredHead(), 0, this.getHeadCount() - 1));
            this.regrowHeadCooldown++;
            if (this.regrowHeadCooldown >= 100) {
                this.headDamageTracker[this.getSeveredHead()] = 0;
                this.setSeveredHead(-1);
                if (this.isOnFire())
                    this.setHeadCount(this.getHeadCount() - 1);
                else {
                    this.playSound(IafSounds.HYDRA_REGEN_HEAD.get(), this.getSoundVolume(), this.getSoundPitch());
                    if (!this.onlyRegrowOneHeadNotTwo)
                        this.setHeadCount(this.getHeadCount() + 1);
                }
                this.onlyRegrowOneHeadNotTwo = false;
                this.regrowHeadCooldown = 0;
            }
        } else this.regrowHeadCooldown = 0;
    }

    public void resetParts() {
        this.clearParts();
        this.headBoxes = new EntityHydraHead[HEADS * 2];
        for (int i = 0; i < this.getHeadCount(); i++) {
            this.headBoxes[i] = new EntityHydraHead(this, 3.2F, ROTATE[this.getHeadCount() - 1][i] * 1.1F, 1.0F, 0.75F, 1.75F, 1, i, false);
            this.headBoxes[HEADS + i] = new EntityHydraHead(this, 2.1F, ROTATE[this.getHeadCount() - 1][i] * 1.1F, 1.0F, 0.75F, 0.75F, 1, i, true);
            this.headBoxes[i].copyPositionAndRotation(this);
            this.headBoxes[HEADS + i].copyPositionAndRotation(this);
            this.headBoxes[i].setParent(this);
            this.headBoxes[HEADS + i].setParent(this);
            if (!this.getWorld().isClient) {
                this.getWorld().spawnEntity(this.headBoxes[i]);
                this.getWorld().spawnEntity(this.headBoxes[HEADS + i]);
            }
        }
        this.multipartLoaded = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.multipartLoaded || this.prevHeadCount != this.getHeadCount())
            this.resetParts();

        float partY = 1.0F - this.limbAnimator.getSpeed() * 0.5F;

        for (int i = 0; i < this.getHeadCount(); i++) {
            this.headBoxes[i].setPosition(this.headBoxes[i].getX(), this.getY() + partY, this.headBoxes[i].getZ());
            EntityUtil.updatePart(this.headBoxes[i], this);

            this.headBoxes[HEADS + i].setPosition(this.headBoxes[HEADS + i].getX(), this.getY() + partY, this.headBoxes[HEADS + i].getZ());
            EntityUtil.updatePart(this.headBoxes[HEADS + i], this);
        }

        if (this.getHeadCount() > 1 && !this.isOnFire())
            if (this.getHealth() < this.getMaxHealth() && this.age % 30 == 0) {
                int level = this.getHeadCount() - 1;
                if (this.getSeveredHead() != -1) level--;
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 30, level, false, false));
            }

        if (this.isOnFire())
            this.removeStatusEffect(StatusEffects.REGENERATION);

        this.prevHeadCount = this.getHeadCount();
    }

    private void clearParts() {
        for (Entity entity : this.headBoxes)
            if (entity != null)
                entity.remove(RemovalReason.DISCARDED);
        this.multipartLoaded = false;
    }

    @Override
    public void remove(RemovalReason reason) {
        this.clearParts();
        super.remove(reason);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.speakingProgress[this.random.nextInt(this.getHeadCount())] = 1F;
        super.playHurtSound(source);
    }

    @Override
    public void playAmbientSound() {
        this.speakingProgress[this.random.nextInt(this.getHeadCount())] = 1F;
        super.playAmbientSound();
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 100 / this.getHeadCount();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("HeadCount", this.getHeadCount());
        compound.putInt("SeveredHead", this.getSeveredHead());
        for (int i = 0; i < HEADS; i++)
            compound.putFloat("HeadDamage" + i, this.headDamageTracker[i]);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setHeadCount(compound.getInt("HeadCount"));
        this.setSeveredHead(compound.getInt("SeveredHead"));
        for (int i = 0; i < HEADS; i++)
            this.headDamageTracker[i] = compound.getFloat("HeadDamage" + i);
        this.setConfigurableAttributes();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(HEAD_COUNT, 3);
        this.dataTracker.startTracking(SEVERED_HEAD, -1);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.lastHitHead < 0 || this.lastHitHead >= this.getHeadCount())
            this.lastHitHead = this.getHeadCount() - 1;
        int headIndex = this.lastHitHead;
        this.headDamageTracker[headIndex] += amount;

        if (this.headDamageTracker[headIndex] > this.headDamageThreshold && (this.getSeveredHead() == -1 || this.getSeveredHead() >= this.getHeadCount())) {
            this.headDamageTracker[headIndex] = 0;
            this.regrowHeadCooldown = 0;
            this.setSeveredHead(headIndex);
            this.playSound(SoundEvents.ENTITY_GUARDIAN_FLOP, this.getSoundVolume(), this.getSoundPitch());
        }
        if (this.getHealth() <= amount + 5 && this.getHeadCount() > 1 && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY))
            amount = 0;
        return super.damage(source, amount);
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        EntityData data = super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
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
        return new Animation[]{};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceToClosestPlayer) {
        return false;
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public int getHeadCount() {
        return MathHelper.clamp(this.dataTracker.get(HEAD_COUNT), 1, HEADS);
    }

    public void setHeadCount(int count) {
        this.dataTracker.set(HEAD_COUNT, MathHelper.clamp(count, 1, HEADS));
    }

    public int getSeveredHead() {
        return MathHelper.clamp(this.dataTracker.get(SEVERED_HEAD), -1, HEADS);
    }

    public void setSeveredHead(int count) {
        this.dataTracker.set(SEVERED_HEAD, MathHelper.clamp(count, -1, HEADS));
    }

    @Override
    public void handleStatus(byte id) {
        if (id >= 40 && id <= 48) {
            int index = id - 40;
            this.isStriking[MathHelper.clamp(index, 0, 8)] = true;
        } else if (id >= 50 && id <= 58) {
            int index = id - 50;
            this.isBreathing[MathHelper.clamp(index, 0, 8)] = true;
        } else if (id >= 60 && id <= 68) {
            int index = id - 60;
            this.isBreathing[MathHelper.clamp(index, 0, 8)] = false;
        } else {
            super.handleStatus(id);
        }
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance potioneffectIn) {
        return potioneffectIn.getEffectType() != StatusEffects.POISON && super.canHaveStatusEffect(potioneffectIn);
    }

    public void onHitHead(float damage, int headIndex) {
        this.triggerHeadFlags(headIndex);
    }

    public void triggerHeadFlags(int index) {
        this.lastHitHead = MathHelper.clamp(index, 0, Math.min(HEADS, this.getHeadCount()) - 1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return IafSounds.HYDRA_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSounds.HYDRA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return IafSounds.HYDRA_DIE.get();
    }

}
