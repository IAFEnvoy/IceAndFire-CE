package com.iafenvoy.iceandfire.entity;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.ai.DreadAITargetNonDreadGoal;
import com.iafenvoy.iceandfire.entity.ai.DreadLichAIStrifeGoal;
import com.iafenvoy.iceandfire.entity.util.IAnimalFear;
import com.iafenvoy.iceandfire.entity.util.IDreadMob;
import com.iafenvoy.iceandfire.entity.util.IVillagerFear;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafParticles;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DreadLichEntity extends DreadMobEntity implements IAnimatedEntity, IVillagerFear, IAnimalFear, RangedAttackMob {
    public static final Animation ANIMATION_SPAWN = Animation.create(40);
    public static final Animation ANIMATION_SUMMON = Animation.create(15);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(DreadLichEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MINION_COUNT = SynchedEntityData.defineId(DreadLichEntity.class, EntityDataSerializers.INT);
    private final DreadLichAIStrifeGoal aiArrowAttack = new DreadLichAIStrifeGoal(this, 1.0D, 20, 15.0F);
    private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 1.0D, false);
    private int animationTick;
    private Animation currentAnimation;
    private int fireCooldown = 0;
    private int minionCooldown = 0;

    public DreadLichEntity(EntityType<? extends DreadMobEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public static boolean canLichSpawnOn(EntityType<? extends Mob> typeIn, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        BlockPos blockpos = pos.below();
        if (reason == MobSpawnType.SPAWNER) return true;
        if (!new DangerousGeneration() {
        }.isFarEnoughFromSpawn(worldIn, pos)) return false;
        if (!worldIn.getBlockState(blockpos).isValidSpawn(worldIn, blockpos, typeIn)) return false;
        return randomIn.nextDouble() < IafCommonConfig.INSTANCE.lich.spawnChance.getValue();
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, 50.0D)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                //FOLLOW RANGE
                .add(Attributes.FOLLOW_RANGE, 128.0D)
                //ARMOR
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, IDreadMob.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (Predicate<LivingEntity>) DragonUtils::canHostilesTarget));
        this.targetSelector.addGoal(3, new DreadAITargetNonDreadGoal(this, LivingEntity.class, false, (Predicate<LivingEntity>) entity -> entity instanceof LivingEntity && DragonUtils.canHostilesTarget(entity)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(MINION_COUNT, 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getAnimation() == ANIMATION_SPAWN && this.getAnimationTick() < 30) {
            BlockState belowBlock = this.level().getBlockState(this.blockPosition().below());
            if (belowBlock.getBlock() != Blocks.AIR) {
                for (int i = 0; i < 5; i++) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, belowBlock), this.getX() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(), this.getBoundingBox().minY, this.getZ() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
                }
            }
            this.setDeltaMovement(0, this.getDeltaMovement().y, this.getDeltaMovement().z);

        }
        if (this.level().isClientSide && this.getAnimation() == ANIMATION_SUMMON) {
            double d0 = 0;
            double d1 = 0;
            double d2 = 0;
            float f = this.yBodyRot * 0.017453292F + Mth.cos(this.tickCount * 0.6662F) * 0.25F;
            float f1 = Mth.cos(f);
            float f2 = Mth.sin(f);
            this.level().addParticle(IafParticles.DREAD_TORCH.get(), this.getX() + (double) f1 * 0.6D, this.getY() + 1.8D, this.getZ() + (double) f2 * 0.6D, d0, d1, d2);
            this.level().addParticle(IafParticles.DREAD_TORCH.get(), this.getX() - (double) f1 * 0.6D, this.getY() + 1.8D, this.getZ() - (double) f2 * 0.6D, d0, d1, d2);
        }
        if (this.fireCooldown > 0) {
            this.fireCooldown--;
        }
        if (this.minionCooldown > 0) {
            this.minionCooldown--;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    protected void populateDefaultEquipmentSlots(@NotNull RandomSource pRandom, @NotNull DifficultyInstance pDifficulty) {
        super.populateDefaultEquipmentSlots(pRandom, pDifficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(IafItems.LICH_STAFF.get()));
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull MobSpawnType reason, SpawnGroupData spawnDataIn) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
        this.setAnimation(ANIMATION_SPAWN);
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), difficultyIn);
        this.setVariant(this.random.nextInt(5));
        this.setCombatTask();
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
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("MinionCount", this.getMinionCount());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setMinionCount(compound.getInt("MinionCount"));
        this.setCombatTask();
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public int getMinionCount() {
        return this.entityData.get(MINION_COUNT);
    }

    public void setMinionCount(int minions) {
        this.entityData.set(MINION_COUNT, minions);
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
        return new Animation[]{ANIMATION_SPAWN, ANIMATION_SUMMON};
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
    public void setItemSlot(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {
        super.setItemSlot(slotIn, stack);

        if (!this.level().isClientSide && slotIn == EquipmentSlot.MAINHAND) {
            this.setCombatTask();
        }
    }

    public void setCombatTask() {
        if (!this.level().isClientSide) {
            this.goalSelector.removeGoal(this.aiAttackOnCollide);
            this.goalSelector.removeGoal(this.aiArrowAttack);
            ItemStack itemstack = this.getMainHandItem();
            if (itemstack.getItem() == IafItems.LICH_STAFF.get()) {
                int i = 100;
                this.aiArrowAttack.setAttackCooldown(i);
                this.goalSelector.addGoal(4, this.aiArrowAttack);
            } else {
                this.goalSelector.addGoal(4, this.aiAttackOnCollide);
            }
        }
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float distanceFactor) {
        boolean flag = false;
        if (this.getMinionCount() < 5 && this.minionCooldown == 0) {
            this.setAnimation(ANIMATION_SUMMON);
            this.playSound(IafSounds.DREAD_LICH_SUMMON.get(), this.getSoundVolume(), this.getVoicePitch());
            Mob minion = this.getRandomNewMinion();
            int x = (int) (this.getX()) - 5 + this.random.nextInt(10);
            int z = (int) (this.getZ()) - 5 + this.random.nextInt(10);
            double y = this.getHeightFromXZ(x, z);
            minion.moveTo(x + 0.5D, y, z + 0.5D, this.getYRot(), this.getXRot());
            minion.setTarget(target);
            Level currentLevel = this.level();
            if (currentLevel instanceof ServerLevelAccessor serverWorldAccess)
                minion.finalizeSpawn(serverWorldAccess, currentLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
            if (minion instanceof DreadMobEntity mob)
                mob.setCommanderId(this.getUUID());
            if (!currentLevel.isClientSide)
                currentLevel.addFreshEntity(minion);
            this.minionCooldown = 100;
            this.setMinionCount(this.getMinionCount() + 1);
            flag = true;
        }
        if (this.fireCooldown == 0 && !flag) {
            this.swing(InteractionHand.MAIN_HAND);
            this.playSound(SoundEvents.ZOMBIE_INFECT, this.getSoundVolume(), this.getVoicePitch());
            DreadLichSkullEntity skull = new DreadLichSkullEntity(IafEntities.DREAD_LICH_SKULL.get(), this.level(), this, 6);
            double d0 = target.getX() - this.getX();
            double d1 = target.getBoundingBox().minY + target.getBbHeight() * 2 - skull.getY();
            double d2 = target.getZ() - this.getZ();
            double d3 = Math.sqrt((float) (d0 * d0 + d2 * d2));
            skull.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 0.0F, 14 - this.level().getDifficulty().getId() * 4);
            this.level().addFreshEntity(skull);
            this.fireCooldown = 100;
        }
    }

    private Mob getRandomNewMinion() {
        float chance = this.random.nextFloat();
        if (chance > 0.5F) {
            return new DreadThrallEntity(IafEntities.DREAD_THRALL.get(), this.level());
        } else if (chance > 0.35F) {
            return new DreadGhoulEntity(IafEntities.DREAD_GHOUL.get(), this.level());
        } else if (chance > 0.15F) {
            return new DreadBeastEntity(IafEntities.DREAD_BEAST.get(), this.level());
        } else {
            return new DreadScuttlerEntity(IafEntities.DREAD_SCUTTLER.get(), this.level());
        }
    }

    private double getHeightFromXZ(int x, int z) {
        BlockPos thisPos = new BlockPos(x, (int) (this.getY() + 7), z);
        while (this.level().isEmptyBlock(thisPos) && thisPos.getY() > 2) {
            thisPos = thisPos.below();
        }
        return thisPos.getY() + 1.0D;
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity entityIn) {
        return entityIn instanceof IDreadMob || super.isAlliedTo(entityIn);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.STRAY_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.STRAY_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.STRAY_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.STRAY_STEP, 0.15F, 1.0F);
    }

}