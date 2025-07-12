package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.api.IafEvents;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.entity.ai.TrollAIFleeSun;
import com.iafenvoy.iceandfire.entity.util.IHasCustomizableAttributes;
import com.iafenvoy.iceandfire.entity.util.IHumanoid;
import com.iafenvoy.iceandfire.entity.util.IVillagerFear;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
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
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.explosion.Explosion;

import java.util.ArrayList;

public class EntityTroll extends HostileEntity implements IAnimatedEntity, IVillagerFear, IHumanoid, IHasCustomizableAttributes {
    public static final Animation ANIMATION_STRIKE_HORIZONTAL = Animation.create(20);
    public static final Animation ANIMATION_STRIKE_VERTICAL = Animation.create(20);
    public static final Animation ANIMATION_SPEAK = Animation.create(10);
    public static final Animation ANIMATION_ROAR = Animation.create(25);
    private static final TrackedData<String> VARIANT = DataTracker.registerData(EntityTroll.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> WEAPON = DataTracker.registerData(EntityTroll.class, TrackedDataHandlerRegistry.STRING);
    public float stoneProgress;
    private int animationTick;
    private Animation currentAnimation;
    private boolean avoidSun = true;

    public EntityTroll(EntityType<EntityTroll> t, World worldIn) {
        super(t, worldIn);
    }

    public static boolean canTrollSpawnOn(EntityType<? extends MobEntity> typeIn, ServerWorldAccess worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && isSpawnDark(worldIn, pos, randomIn) && canMobSpawn(IafEntities.TROLL.get(), worldIn, reason, pos, randomIn);
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, IafCommonConfig.INSTANCE.troll.maxHealth.getValue())
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, IafCommonConfig.INSTANCE.troll.attackDamage.getValue())
                //KNOCKBACK RESIST
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
                //ARMOR
                .add(EntityAttributes.GENERIC_ARMOR, 9.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(IafCommonConfig.INSTANCE.troll.maxHealth.getValue());
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(IafCommonConfig.INSTANCE.troll.attackDamage.getValue());
    }

    private void setAvoidSun(boolean day) {
        if (day && !this.avoidSun) {
            ((MobNavigation) this.getNavigation()).setAvoidSunlight(true);
            this.avoidSun = true;
        }
        if (!day && this.avoidSun) {
            ((MobNavigation) this.getNavigation()).setAvoidSunlight(false);
            this.avoidSun = false;
        }
    }

    @Override
    public boolean canSpawn(WorldView worldIn) {
        return worldIn.doesNotIntersectEntities(this);
    }

    @Override
    public boolean canSpawn(WorldAccess worldIn, SpawnReason spawnReasonIn) {
        BlockPos pos = this.getBlockPos();
        BlockPos heightAt = worldIn.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
        boolean rngCheck = true;
        return pos.getY() < heightAt.getY() - 10 && super.canSpawn(worldIn, spawnReasonIn);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new TrollAIFleeSun(this, 1.0D));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F, 1.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
        this.setAvoidSun(true);
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
        if (this.getRandom().nextBoolean()) {
            this.setAnimation(ANIMATION_STRIKE_VERTICAL);

        } else {
            this.setAnimation(ANIMATION_STRIKE_HORIZONTAL);
        }
        return true;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, TrollType.FOREST.getName());
        this.dataTracker.startTracking(WEAPON, TrollType.BuiltinWeapon.AXE.getName());
    }

    private String getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    private void setVariant(String variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public TrollType getTrollType() {
        return TrollType.getByName(this.getVariant());
    }

    public void setTrollType(TrollType variant) {
        this.setVariant(variant.getName());
    }

    private String getWeapon() {
        return this.dataTracker.get(WEAPON);
    }

    private void setWeapon(String variant) {
        this.dataTracker.set(WEAPON, variant);
    }

    public TrollType.ITrollWeapon getWeaponType() {
        return TrollType.ITrollWeapon.getByName(this.getWeapon());
    }

    public void setWeaponType(TrollType.ITrollWeapon variant) {
        this.setWeapon(variant.getName());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putString("Variant", this.getVariant());
        compound.putString("Weapon", this.getWeapon());
        compound.putFloat("StoneProgress", this.stoneProgress);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setVariant(compound.getString("Variant"));
        this.setWeapon(compound.getString("Weapon"));
        this.stoneProgress = compound.getFloat("StoneProgress");
        this.setConfigurableAttributes();
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        spawnDataIn = super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setTrollType(TrollType.getBiomeType(this.getWorld().getBiome(this.getBlockPos())));
        this.setWeaponType(TrollType.getWeaponForType(this.getTrollType()));
        return spawnDataIn;
    }

    @Override
    public boolean damage(DamageSource source, float damage) {
        if (source.getName().contains("arrow")) {
            return false;
        }
        return super.damage(source, damage);
    }

    @Override
    protected Identifier getLootTableId() {
        return this.getTrollType().getLootTable();
    }

    @Override
    public int getXpToDrop() {
        return 15;
    }

    @Override
    protected void updatePostDeath() {
        super.updatePostDeath();
        if (this.deathTime == 20 && !this.getWorld().isClient) {
            if (IafCommonConfig.INSTANCE.troll.dropWeapon.getValue()) {
                if (this.getRandom().nextInt(3) == 0) {
                    ItemStack weaponStack = new ItemStack(this.getWeaponType().getItem(), 1);
                    weaponStack.damage(this.getRandom().nextInt(250), this.getRandom(), null);
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
            ItemEntity entityitem = new ItemEntity(this.getWorld(), x, y, z, stack);
            entityitem.setToDefaultPickupDelay();
            this.getWorld().spawnEntity(entityitem);
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof PlayerEntity)
            this.setTarget(null);
        boolean stone = EntityGorgon.isStoneMob(this);
        if (stone && this.stoneProgress < 20.0F)
            this.stoneProgress += 2F;
        else if (!stone && this.stoneProgress > 0.0F)
            this.stoneProgress -= 2F;
        if (!stone && this.getAnimation() == NO_ANIMATION && this.getTarget() != null && this.getRandom().nextInt(100) == 0)
            this.setAnimation(ANIMATION_ROAR);
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 5)
            this.playSound(IafSounds.TROLL_ROAR.get(), 1, 1);
        if (!stone && this.getHealth() < this.getMaxHealth() && this.age % 30 == 0)
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 30, 1, false, false));
        this.setAvoidSun(this.getWorld().isDay());
        if (this.getWorld().isDay() && !this.getWorld().isClient) {
            float f = this.getWorld().getLightLevel(LightType.SKY, this.getBlockPos());
            BlockPos blockpos = this.getVehicle() instanceof BoatEntity ? (new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ())).up() : new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ());
            if (f > 0.5F && this.getWorld().isSkyVisible(blockpos)) {
                this.setVelocity(0, 0, 0);
                this.setAnimation(NO_ANIMATION);
                this.playSound(IafSounds.TURN_STONE.get(), 1, 1);
                this.stoneProgress = 20;
                EntityStoneStatue statue = EntityStoneStatue.buildStatueEntity(this);
                statue.getTrappedTag().putFloat("StoneProgress", 20);
                statue.updatePositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
                if (!this.getWorld().isClient) this.getWorld().spawnEntity(statue);
                statue.prevYaw = this.getYaw();
                statue.setYaw(this.getYaw());
                statue.headYaw = this.getYaw();
                statue.bodyYaw = this.getYaw();
                statue.prevBodyYaw = this.getYaw();
                this.remove(RemovalReason.KILLED);
            }
        }
        if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10) {
            float weaponX = (float) (this.getX() + 1.9F * MathHelper.cos((float) ((this.bodyYaw + 90) * Math.PI / 180)));
            float weaponZ = (float) (this.getZ() + 1.9F * MathHelper.sin((float) ((this.bodyYaw + 90) * Math.PI / 180)));
            float weaponY = (float) (this.getY() + (0.2F));
            BlockState state = this.getWorld().getBlockState(BlockPos.ofFloored(weaponX, weaponY - 1, weaponZ));
            for (int i = 0; i < 20; i++) {
                double motionX = this.getRandom().nextGaussian() * 0.07D;
                double motionY = this.getRandom().nextGaussian() * 0.07D;
                double motionZ = this.getRandom().nextGaussian() * 0.07D;
                if (state.isSolid() && this.getWorld().isClient)
                    this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), weaponX + (this.getRandom().nextFloat() - 0.5F), weaponY + (this.getRandom().nextFloat() - 0.5F), weaponZ + (this.getRandom().nextFloat() - 0.5F), motionX, motionY, motionZ);
            }
        }
        if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getTarget() != null && this.squaredDistanceTo(this.getTarget()) < 4D && this.getAnimationTick() == 10 && this.deathTime <= 0)
            this.getTarget().damage(this.getWorld().getDamageSources().mobAttack(this), (float) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue());
        if (this.getAnimation() == ANIMATION_STRIKE_HORIZONTAL && this.getTarget() != null && this.squaredDistanceTo(this.getTarget()) < 4D && this.getAnimationTick() == 10 && this.deathTime <= 0) {
            LivingEntity target = this.getTarget();
            target.damage(this.getWorld().getDamageSources().mobAttack(this), (float) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue());
            float f5 = MathHelper.sin(this.getYaw() * 0.017453292F);
            float f6 = MathHelper.cos(this.getYaw() * 0.017453292F);
            target.setVelocity(f5, f6, 0.4F);
        }
        if (this.getNavigation().isIdle() && this.getTarget() != null && this.squaredDistanceTo(this.getTarget()) > 3 && this.squaredDistanceTo(this.getTarget()) < 30 && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            this.lookAtEntity(this.getTarget(), 30, 30);
            if (this.getAnimation() == NO_ANIMATION && this.random.nextInt(15) == 0)
                this.setAnimation(ANIMATION_STRIKE_VERTICAL);
            if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10) {
                float weaponX = (float) (this.getX() + 1.9F * MathHelper.cos((float) ((this.bodyYaw + 90) * Math.PI / 180)));
                float weaponZ = (float) (this.getZ() + 1.9F * MathHelper.sin((float) ((this.bodyYaw + 90) * Math.PI / 180)));
                float weaponY = (float) (this.getY() + (this.getStandingEyeHeight() / 2));
                Explosion explosion = new Explosion(this.getWorld(), this, weaponX, weaponY, weaponZ, 1F + this.getRandom().nextFloat(), new ArrayList<>());
                if (!IafEvents.ON_GRIEF_BREAK_BLOCK.invoker().onBreakBlock(this, weaponX, weaponY, weaponZ)) {
                    explosion.collectBlocksAndDamageEntities();
                    explosion.affectWorld(true);
                }
                this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1, 1);
            }
        }
        if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10)
            this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 2.5F, 0.5F);
        if (this.getAnimation() == ANIMATION_STRIKE_HORIZONTAL && this.getAnimationTick() == 10)
            this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 2.5F, 0.5F);
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public void playAmbientSound() {
        if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION)
            this.setAnimation(ANIMATION_SPEAK);
        super.playAmbientSound();
    }

    @Override
    protected void playHurtSound(DamageSource source) {
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
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSounds.TROLL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return IafSounds.TROLL_DIE.get();
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{NO_ANIMATION, ANIMATION_STRIKE_HORIZONTAL, ANIMATION_STRIKE_VERTICAL, ANIMATION_SPEAK, ANIMATION_ROAR};
    }
}
