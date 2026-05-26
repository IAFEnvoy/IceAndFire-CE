package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.ai.GorgonAIStareAttackGoal;
import com.iafenvoy.iceandfire.entity.util.*;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.registry.IafDamageTypes;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafParticles;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class GorgonEntity extends Monster implements IAnimatedEntity, IVillagerFear, IAnimalFear, IHumanoid, IHasCustomizableAttributes {
    public static Animation ANIMATION_SCARE;
    public static Animation ANIMATION_HIT;
    private int animationTick;
    private Animation currentAnimation;
    private GorgonAIStareAttackGoal aiStare;
    private MeleeAttackGoal aiMelee;
    private int playerStatueCooldown;

    public GorgonEntity(EntityType<GorgonEntity> type, Level worldIn) {
        super(type, worldIn);
        ANIMATION_SCARE = Animation.create(30);
        ANIMATION_HIT = Animation.create(10);
    }

    public static boolean isStoneMob(LivingEntity mob) {
        return mob instanceof StoneStatueEntity;
    }

    public static boolean isBlindfolded(LivingEntity attackTarget) {
        if (attackTarget == null) return false;
        if (attackTarget.getItemBySlot(EquipmentSlot.HEAD).getItem() == IafItems.BLINDFOLD.get() || attackTarget.hasEffect(MobEffects.BLINDNESS))
            return true;
        return attackTarget.getType().is(IafEntityTags.BLINDED);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, IafCommonConfig.INSTANCE.gorgon.maxHealth.getValue())
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                //ARMOR
                .add(Attributes.ARMOR, 1.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(IafCommonConfig.INSTANCE.gorgon.maxHealth.getValue());
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        HitResult result = this.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return result.getType() != HitResult.Type.MISS;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, this.aiStare = new GorgonAIStareAttackGoal(this, 1.0D, 0, 15.0F));
        this.goalSelector.addGoal(3, this.aiMelee = new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                this.interval = 20;
                return super.canUse();
            }
        });
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F, 1.0F) {
            @Override
            public boolean canContinueToUse() {
                if (this.lookAt != null && this.lookAt instanceof Player && ((Player) this.lookAt).isCreative()) {
                    return false;
                }
                return super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false, LivingEntity::isAlive));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, entity -> entity instanceof LivingEntity && DragonUtils.isAlive(entity) || (entity instanceof BlacklistedFromStatues blacklisted && blacklisted.canBeTurnedToStone())));
        this.goalSelector.removeGoal(this.aiMelee);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        boolean blindness = this.hasEffect(MobEffects.BLINDNESS) || this.getTarget() != null && this.getTarget().hasEffect(MobEffects.BLINDNESS) || this.getTarget() != null && this.getTarget() instanceof BlacklistedFromStatues blacklisted && !blacklisted.canBeTurnedToStone();
        if (blindness && this.deathTime == 0) {
            if (this.getAnimation() != ANIMATION_HIT)
                this.setAnimation(ANIMATION_HIT);
            if (entityIn instanceof LivingEntity living)
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2, false, true));
        }
        return super.doHurtTarget(entityIn);
    }

    @Override
    public void setTarget(LivingEntity LivingEntityIn) {
        super.setTarget(LivingEntityIn);
        if (LivingEntityIn != null && !this.level().isClientSide) {


            boolean blindness = this.hasEffect(MobEffects.BLINDNESS) || LivingEntityIn.hasEffect(MobEffects.BLINDNESS) || LivingEntityIn instanceof BlacklistedFromStatues && !((BlacklistedFromStatues) LivingEntityIn).canBeTurnedToStone() || isBlindfolded(LivingEntityIn);
            if (blindness && this.deathTime == 0) {
                this.goalSelector.addGoal(3, this.aiMelee);
                this.goalSelector.removeGoal(this.aiStare);
            } else {
                this.goalSelector.addGoal(3, this.aiStare);
                this.goalSelector.removeGoal(this.aiMelee);
            }
        }
    }

    @Override
    public int getBaseExperienceReward() {
        return 30;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.ambientSoundTime = 20;
        if (this.level().isClientSide) {
            for (int k = 0; k < 5; ++k) {
                double d2 = 0.4;
                double d0 = 0.1;
                double d1 = 0.1;
                this.level().addParticle(IafParticles.BLOOD.get(), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY(), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d2, d0, d1);
            }
        }
        if (this.deathTime >= 200) {
            if (!this.level().isClientSide && (this.isAlwaysExperienceDropper() || this.lastHurtByPlayerTime > 0 && this.shouldDropExperience() && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))) {
                int i = this.getBaseExperienceReward();
                while (i > 0) {
                    int j = ExperienceOrb.getExperienceValue(i);
                    i -= j;
                    this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY(), this.getZ(), j));
                }
            }
            this.remove(RemovalReason.KILLED);

            for (int k = 0; k < 20; ++k) {
                double d2 = this.random.nextGaussian() * 0.02D;
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(ParticleTypes.CLOUD, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d2, d0, d1);
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.playerStatueCooldown > 0) {
            this.playerStatueCooldown--;
        }
        LivingEntity attackTarget = this.getTarget();
        if (attackTarget != null) {
            boolean blindness = this.hasEffect(MobEffects.BLINDNESS) || attackTarget.hasEffect(MobEffects.BLINDNESS);
            if (!blindness && this.deathTime == 0 && attackTarget instanceof Mob) {
                this.forcePreyToLook(attackTarget);
            }
            if (IafEntityUtil.isEntityLookingAt(attackTarget, this, 0.4)) {
                this.getLookControl().setLookAt(attackTarget.getX(), attackTarget.getY() + (double) attackTarget.getEyeHeight(), attackTarget.getZ(), (float) this.getMaxHeadYRot(), (float) this.getMaxHeadXRot());
            }
        }


        if (attackTarget != null && IafEntityUtil.isEntityLookingAt(this, attackTarget, 0.4) && IafEntityUtil.isEntityLookingAt(attackTarget, this, 0.4) && !isBlindfolded(attackTarget)) {
            boolean blindness = this.hasEffect(MobEffects.BLINDNESS) || attackTarget.hasEffect(MobEffects.BLINDNESS) || attackTarget instanceof BlacklistedFromStatues blacklisted && !blacklisted.canBeTurnedToStone();
            if (!blindness && this.deathTime == 0) {
                if (this.getAnimation() != ANIMATION_SCARE) {
                    this.playSound(IafSounds.GORGON_ATTACK.get(), 1, 1);
                    this.setAnimation(ANIMATION_SCARE);
                }
                if (this.getAnimation() == ANIMATION_SCARE) {
                    if (this.getAnimationTick() > 10) {
                        if (!this.level().isClientSide) {
                            if (this.playerStatueCooldown == 0) {
                                StoneStatueEntity statue = StoneStatueEntity.buildStatueEntity(attackTarget);
                                statue.absMoveTo(attackTarget.getX(), attackTarget.getY(), attackTarget.getZ(), attackTarget.getYRot(), attackTarget.getXRot());
                                if (!this.level().isClientSide)
                                    this.level().addFreshEntity(statue);
                                statue.setYRot(attackTarget.getYRot());
                                statue.setYRot(attackTarget.getYRot());
                                statue.yHeadRot = attackTarget.getYRot();
                                statue.yBodyRot = attackTarget.getYRot();
                                statue.yBodyRotO = attackTarget.getYRot();
                                this.playerStatueCooldown = 40;
                                if (attackTarget instanceof Player)
                                    attackTarget.hurt(IafDamageTypes.causeGorgonDamage(this), Integer.MAX_VALUE);
                                else attackTarget.remove(RemovalReason.KILLED);
                                this.setTarget(null);
                            }
                        }
                    }
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public int getMaxHeadXRot() {
        return 10;
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    public void forcePreyToLook(LivingEntity mob) {
        if (mob instanceof Mob mobEntity)
            mobEntity.getLookControl().setLookAt(this.getX(), this.getY() + (double) this.getEyeHeight(), this.getZ(), (float) mobEntity.getMaxHeadYRot(), (float) mobEntity.getMaxHeadXRot());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setConfigurableAttributes();
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
        return new Animation[]{ANIMATION_SCARE, ANIMATION_HIT};
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return IafSounds.GORGON_IDLE.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return IafSounds.GORGON_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return IafSounds.GORGON_DIE.get();
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
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
