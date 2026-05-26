package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.entity.ai.TrollAIFleeSunGoal;
import com.iafenvoy.iceandfire.entity.util.IHasCustomizableAttributes;
import com.iafenvoy.iceandfire.entity.util.IHumanoid;
import com.iafenvoy.iceandfire.entity.util.IVillagerFear;
import com.iafenvoy.iceandfire.event.IafEvents;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.IafTrollTypes;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class TrollEntity extends Monster implements IAnimatedEntity, IVillagerFear, IHumanoid, IHasCustomizableAttributes {
    public static final Animation ANIMATION_STRIKE_HORIZONTAL = Animation.create(20);
    public static final Animation ANIMATION_STRIKE_VERTICAL = Animation.create(20);
    public static final Animation ANIMATION_SPEAK = Animation.create(10);
    public static final Animation ANIMATION_ROAR = Animation.create(25);
    private static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(TrollEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> WEAPON = SynchedEntityData.defineId(TrollEntity.class, EntityDataSerializers.STRING);
    public float stoneProgress;
    private int animationTick;
    private Animation currentAnimation;
    private boolean avoidSun = true;

    public TrollEntity(EntityType<TrollEntity> t, Level worldIn) {
        super(t, worldIn);
    }

    public static boolean canTrollSpawnOn(EntityType<? extends Mob> typeIn, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && new DangerousGeneration() {
        }.isFarEnoughFromSpawn(worldIn, pos) && isDarkEnoughToSpawn(worldIn, pos, randomIn) && checkMobSpawnRules(IafEntities.TROLL.get(), worldIn, reason, pos, randomIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, IafCommonConfig.INSTANCE.troll.maxHealth.getValue())
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, IafCommonConfig.INSTANCE.troll.attackDamage.getValue())
                //KNOCKBACK RESIST
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                //ARMOR
                .add(Attributes.ARMOR, 9.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(IafCommonConfig.INSTANCE.troll.maxHealth.getValue());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(IafCommonConfig.INSTANCE.troll.attackDamage.getValue());
    }

    private void setAvoidSun(boolean day) {
        if (day && !this.avoidSun) {
            ((GroundPathNavigation) this.getNavigation()).setAvoidSun(true);
            this.avoidSun = true;
        }
        if (!day && this.avoidSun) {
            ((GroundPathNavigation) this.getNavigation()).setAvoidSun(false);
            this.avoidSun = false;
        }
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor worldIn, @NotNull MobSpawnType spawnReasonIn) {
        BlockPos pos = this.blockPosition();
        BlockPos heightAt = worldIn.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
        boolean rngCheck = true;
        return pos.getY() < heightAt.getY() - 10 && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new TrollAIFleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F, 1.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.setAvoidSun(true);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        if (this.getRandom().nextBoolean()) {
            this.setAnimation(ANIMATION_STRIKE_VERTICAL);

        } else {
            this.setAnimation(ANIMATION_STRIKE_HORIZONTAL);
        }
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, IafTrollTypes.FOREST.getName());
        builder.define(WEAPON, TrollType.BuiltinWeapon.AXE.getName());
    }

    private String getVariant() {
        return this.entityData.get(VARIANT);
    }

    private void setVariant(String variant) {
        this.entityData.set(VARIANT, variant);
    }

    public TrollType getTrollType() {
        return TrollType.getByName(this.getVariant());
    }

    public void setTrollType(TrollType variant) {
        this.setVariant(variant.getName());
    }

    private String getWeapon() {
        return this.entityData.get(WEAPON);
    }

    private void setWeapon(String variant) {
        this.entityData.set(WEAPON, variant);
    }

    public TrollType.ITrollWeapon getWeaponType() {
        return TrollType.ITrollWeapon.getByName(this.getWeapon());
    }

    public void setWeaponType(TrollType.ITrollWeapon variant) {
        this.setWeapon(variant.getName());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Variant", this.getVariant());
        compound.putString("Weapon", this.getWeapon());
        compound.putFloat("StoneProgress", this.stoneProgress);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getString("Variant"));
        this.setWeapon(compound.getString("Weapon"));
        this.stoneProgress = compound.getFloat("StoneProgress");
        this.setConfigurableAttributes();
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull MobSpawnType reason, SpawnGroupData spawnDataIn) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
        this.setTrollType(TrollType.getBiomeType(this.level().getBiome(this.blockPosition())));
        this.setWeaponType(TrollType.getWeaponForType(this.getTrollType()));
        return spawnDataIn;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (source.getMsgId().contains("arrow")) {
            return false;
        }
        return super.hurt(source, damage);
    }

    @Override
    protected @NotNull ResourceKey<LootTable> getDefaultLootTable() {
        return ResourceKey.create(Registries.LOOT_TABLE, this.getTrollType().getLootTable());
    }

    @Override
    public int getBaseExperienceReward() {
        return 15;
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (this.deathTime == 20 && !this.level().isClientSide) {
            if (IafCommonConfig.INSTANCE.troll.dropWeapon.getValue()) {
                if (this.getRandom().nextInt(3) == 0) {
                    ItemStack weaponStack = new ItemStack(this.getWeaponType().getItem(), 1);
                    weaponStack.setDamageValue(this.getRandom().nextInt(250));
                    this.dropItemAt(weaponStack, this.getX(), this.getY(), this.getZ());
                } else {
                    ItemStack brokenDrop = new ItemStack(Blocks.STONE_BRICKS, this.getRandom().nextInt(2) + 1);
                    ItemStack brokenDrop2 = new ItemStack(Blocks.STONE_BRICKS, this.getRandom().nextInt(2) + 1);
                    if (this.getWeaponType() == TrollType.BuiltinWeapon.AXE) {
                        brokenDrop = new ItemStack(Items.STICK, this.getRandom().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Blocks.COBBLESTONE, this.getRandom().nextInt(2) + 1);
                    }
                    if (this.getWeaponType() == TrollType.BuiltinWeapon.COLUMN) {
                        brokenDrop = new ItemStack(Blocks.STONE_BRICKS, this.getRandom().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Blocks.STONE_BRICKS, this.getRandom().nextInt(2) + 1);
                    }
                    if (this.getWeaponType() == TrollType.BuiltinWeapon.COLUMN_FOREST) {
                        brokenDrop = new ItemStack(Blocks.STONE_BRICKS, this.getRandom().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Blocks.STONE_BRICKS, this.getRandom().nextInt(2) + 1);
                    }
                    if (this.getWeaponType() == TrollType.BuiltinWeapon.COLUMN_FROST) {
                        brokenDrop = new ItemStack(Blocks.STONE_BRICKS, this.getRandom().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Items.SNOWBALL, this.getRandom().nextInt(4) + 1);
                    }
                    if (this.getWeaponType() == TrollType.BuiltinWeapon.HAMMER) {
                        brokenDrop = new ItemStack(Items.BONE, this.getRandom().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Blocks.COBBLESTONE, this.getRandom().nextInt(2) + 1);
                    }
                    if (this.getWeaponType() == TrollType.BuiltinWeapon.TRUNK) {
                        brokenDrop = new ItemStack(Blocks.OAK_LOG, this.getRandom().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Blocks.OAK_LOG, this.getRandom().nextInt(2) + 1);
                    }
                    if (this.getWeaponType() == TrollType.BuiltinWeapon.TRUNK_FROST) {
                        brokenDrop = new ItemStack(Blocks.SPRUCE_LOG, this.getRandom().nextInt(4) + 1);
                        brokenDrop2 = new ItemStack(Items.SNOWBALL, this.getRandom().nextInt(4) + 1);
                    }
                    this.dropItemAt(brokenDrop, this.getX(), this.getY(), this.getZ());
                    this.dropItemAt(brokenDrop2, this.getX(), this.getY(), this.getZ());

                }
            }
        }
    }

    private void dropItemAt(ItemStack stack, double x, double y, double z) {
        if (stack.getCount() > 0) {
            ItemEntity entityitem = new ItemEntity(this.level(), x, y, z, stack);
            entityitem.setDefaultPickUpDelay();
            this.level().addFreshEntity(entityitem);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof Player)
            this.setTarget(null);
        boolean stone = GorgonEntity.isStoneMob(this);
        if (stone && this.stoneProgress < 20.0F)
            this.stoneProgress += 2F;
        else if (!stone && this.stoneProgress > 0.0F)
            this.stoneProgress -= 2F;
        if (!stone && this.getAnimation() == NO_ANIMATION && this.getTarget() != null && this.getRandom().nextInt(100) == 0)
            this.setAnimation(ANIMATION_ROAR);
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 5)
            this.playSound(IafSounds.TROLL_ROAR.get(), 1, 1);
        if (!stone && this.getHealth() < this.getMaxHealth() && this.tickCount % 30 == 0)
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30, 1, false, false));
        this.setAvoidSun(this.level().isDay());
        if (this.level().isDay() && !this.level().isClientSide) {
            float f = this.level().getBrightness(LightLayer.SKY, this.blockPosition());
            BlockPos blockpos = this.getVehicle() instanceof Boat ? (new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ())).above() : new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ());
            if (f > 0.5F && this.level().canSeeSky(blockpos)) {
                this.setDeltaMovement(0, 0, 0);
                this.setAnimation(NO_ANIMATION);
                this.playSound(IafSounds.TURN_STONE.get(), 1, 1);
                this.stoneProgress = 20;
                StoneStatueEntity statue = StoneStatueEntity.buildStatueEntity(this);
                statue.getTrappedTag().putFloat("StoneProgress", 20);
                statue.absMoveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                if (!this.level().isClientSide) this.level().addFreshEntity(statue);
                statue.yRotO = this.getYRot();
                statue.setYRot(this.getYRot());
                statue.yHeadRot = this.getYRot();
                statue.yBodyRot = this.getYRot();
                statue.yBodyRotO = this.getYRot();
                this.remove(RemovalReason.KILLED);
            }
        }
        if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10) {
            float weaponX = (float) (this.getX() + 1.9F * Mth.cos((float) ((this.yBodyRot + 90) * Math.PI / 180)));
            float weaponZ = (float) (this.getZ() + 1.9F * Mth.sin((float) ((this.yBodyRot + 90) * Math.PI / 180)));
            float weaponY = (float) (this.getY() + (0.2F));
            BlockState state = this.level().getBlockState(BlockPos.containing(weaponX, weaponY - 1, weaponZ));
            for (int i = 0; i < 20; i++) {
                double motionX = this.getRandom().nextGaussian() * 0.07D;
                double motionY = this.getRandom().nextGaussian() * 0.07D;
                double motionZ = this.getRandom().nextGaussian() * 0.07D;
                if (state.isSolid() && this.level().isClientSide)
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), weaponX + (this.getRandom().nextFloat() - 0.5F), weaponY + (this.getRandom().nextFloat() - 0.5F), weaponZ + (this.getRandom().nextFloat() - 0.5F), motionX, motionY, motionZ);
            }
        }
        if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getTarget() != null && this.distanceToSqr(this.getTarget()) < 4D && this.getAnimationTick() == 10 && this.deathTime <= 0)
            this.getTarget().hurt(this.level().damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
        if (this.getAnimation() == ANIMATION_STRIKE_HORIZONTAL && this.getTarget() != null && this.distanceToSqr(this.getTarget()) < 4D && this.getAnimationTick() == 10 && this.deathTime <= 0) {
            LivingEntity target = this.getTarget();
            target.hurt(this.level().damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            float f5 = Mth.sin(this.getYRot() * 0.017453292F);
            float f6 = Mth.cos(this.getYRot() * 0.017453292F);
            target.setDeltaMovement(f5, f6, 0.4F);
        }
        if (this.getNavigation().isDone() && this.getTarget() != null && this.distanceToSqr(this.getTarget()) > 3 && this.distanceToSqr(this.getTarget()) < 30 && this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            this.lookAt(this.getTarget(), 30, 30);
            if (this.getAnimation() == NO_ANIMATION && this.random.nextInt(15) == 0)
                this.setAnimation(ANIMATION_STRIKE_VERTICAL);
            if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10) {
                float weaponX = (float) (this.getX() + 1.9F * Mth.cos((float) ((this.yBodyRot + 90) * Math.PI / 180)));
                float weaponZ = (float) (this.getZ() + 1.9F * Mth.sin((float) ((this.yBodyRot + 90) * Math.PI / 180)));
                float weaponY = (float) (this.getY() + (this.getEyeHeight() / 2));
                //TODO: Recheck Explosion
                Explosion explosion = new Explosion(this.level(), this, weaponX, weaponY, weaponZ, 1F + this.getRandom().nextFloat(), false, Explosion.BlockInteraction.KEEP);
                if (!IafEvents.ON_GRIEF_BREAK_BLOCK.invoker().onBreakBlock(this, weaponX, weaponY, weaponZ)) {
                    explosion.explode();
                    explosion.finalizeExplosion(true);
                }
                this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1, 1);
            }
        }
        if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10)
            this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 2.5F, 0.5F);
        if (this.getAnimation() == ANIMATION_STRIKE_HORIZONTAL && this.getAnimationTick() == 10)
            this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 2.5F, 0.5F);
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public void playAmbientSound() {
        if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION)
            this.setAnimation(ANIMATION_SPEAK);
        super.playAmbientSound();
    }

    @Override
    protected void playHurtSound(@NotNull DamageSource source) {
        if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION)
            this.setAnimation(ANIMATION_SPEAK);
        super.playHurtSound(source);
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
    protected SoundEvent getAmbientSound() {
        return IafSounds.TROLL_IDLE.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return IafSounds.TROLL_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return IafSounds.TROLL_DIE.get();
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{NO_ANIMATION, ANIMATION_STRIKE_HORIZONTAL, ANIMATION_STRIKE_VERTICAL, ANIMATION_SPEAK, ANIMATION_ROAR};
    }
}
