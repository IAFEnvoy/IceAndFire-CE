package com.iafenvoy.iceandfire.entity;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.ai.AquaticAIGetInWaterGoal;
import com.iafenvoy.iceandfire.entity.ai.AquaticAIGetOutOfWaterGoal;
import com.iafenvoy.iceandfire.entity.ai.SirenAIFindWaterTargetGoal;
import com.iafenvoy.iceandfire.entity.ai.SirenAIWanderGoal;
import com.iafenvoy.iceandfire.entity.util.ChainBuffer;
import com.iafenvoy.iceandfire.entity.util.IHasCustomizableAttributes;
import com.iafenvoy.iceandfire.entity.util.IVillagerFear;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafMobEffects;
import com.iafenvoy.iceandfire.registry.IafParticles;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SirenEntity extends Monster implements IAnimatedEntity, IVillagerFear, IHasCustomizableAttributes {
    public static final int SEARCH_RANGE = 32;
    public static final Predicate<Entity> SIREN_PREY = entity -> (entity instanceof Player player && !player.isCreative() && !entity.isSpectator()) || entity.getType().is(IafEntityTags.SIREN_CHARMABLE);
    public static final Animation ANIMATION_BITE = Animation.create(20);
    public static final Animation ANIMATION_PULL = Animation.create(20);
    private static final EntityDataAccessor<Integer> HAIR_COLOR = SynchedEntityData.defineId(SirenEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> AGGRESSIVE = SynchedEntityData.defineId(SirenEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SING_POSE = SynchedEntityData.defineId(SirenEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SINGING = SynchedEntityData.defineId(SirenEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SWIMMING = SynchedEntityData.defineId(SirenEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHARMED = SynchedEntityData.defineId(SirenEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(SirenEntity.class, EntityDataSerializers.BYTE);
    private final Object2IntMap<LivingEntity> charmingEntities = new Object2IntOpenHashMap<>();
    public ChainBuffer tail_buffer;
    public float singProgress;
    public float swimProgress;
    public int singCooldown;
    private int animationTick;
    private Animation currentAnimation;
    private boolean isSwimming;
    private boolean isLandNavigator;
    private int ticksAgressive;

    public SirenEntity(EntityType<SirenEntity> t, Level worldIn) {
        super(t, worldIn);
        this.switchNavigator(true);
        if (worldIn.isClientSide) this.tail_buffer = new ChainBuffer();
    }

    public static boolean isWearingEarplugs(LivingEntity entity) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
        return !stack.isEmpty() && stack.is(IafItems.EARPLUGS.get());
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, IafCommonConfig.INSTANCE.siren.maxHealth.getValue())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.STEP_HEIGHT, 1);
    }

    public static float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = Mth.wrapDegrees(targetAngle - angle);
        return angle + Mth.clamp(f, -maxIncrease, maxIncrease);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SirenAIFindWaterTargetGoal(this));
        this.goalSelector.addGoal(1, new AquaticAIGetInWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new AquaticAIGetOutOfWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new SirenAIWanderGoal(this, 1));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F, 1.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, entity -> entity instanceof Player player && SirenEntity.this.isAgressive() && !(player.isCreative() || player.isSpectator())));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, 10, true, false, entity -> SirenEntity.this.isAgressive()));
    }

    @Override
    public int getBaseExperienceReward() {
        return 8;
    }

    @Override
    public float getWalkTargetValue(@NotNull BlockPos pos) {
        return this.level().getBlockState(pos).is(Blocks.WATER) ? 10F : super.getWalkTargetValue(pos);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        if (this.getRandom().nextInt(2) == 0) {
            if (this.getAnimation() != ANIMATION_PULL) {
                this.setAnimation(ANIMATION_PULL);
                this.playSound(IafSounds.NAGA_ATTACK.get(), 1, 1);
            }
        } else {
            if (this.getAnimation() != ANIMATION_BITE) {
                this.setAnimation(ANIMATION_BITE);
                this.playSound(IafSounds.NAGA_ATTACK.get(), 1, 1);
            }
        }
        return true;
    }

    public boolean isDirectPathBetweenPoints(Vec3 vec1, Vec3 pos) {
        Vec3 Vector3d1 = new Vec3(pos.x() + 0.5D, pos.y() + 0.5D, pos.z() + 0.5D);
        return this.level().clip(new ClipContext(vec1, Vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public float getPathfindingMalus(@NotNull PathType nodeType) {
        return nodeType == PathType.WATER ? 0F : super.getPathfindingMalus(nodeType);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new AmphibiousPathNavigation(this, this.level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new SwimmingMoveHelper();
            this.navigation = new WaterBoundPathNavigation(this, this.level());
            this.isLandNavigator = false;
        }
    }

    private boolean isPathOnHighGround() {
        if (this.navigation.getPath() != null && this.navigation.getPath().getEndNode() != null) {
            BlockPos target = new BlockPos(this.navigation.getPath().getEndNode().x, this.navigation.getPath().getEndNode().y, this.navigation.getPath().getEndNode().z);
            BlockPos siren = this.blockPosition();
            return this.level().isEmptyBlock(siren.above()) && this.level().isEmptyBlock(target.above()) && target.getY() >= siren.getY();
        }
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.yBodyRot = this.getYRot();

        LivingEntity attackTarget = this.getTarget();
        if (this.singCooldown > 0) {
            this.singCooldown--;
            this.setSinging(false);
        }
        if (!this.level().isClientSide && attackTarget == null && !this.isAgressive())
            this.setSinging(true);
        if (this.getAnimation() == ANIMATION_BITE && attackTarget != null && this.distanceToSqr(attackTarget) < 7D && this.getAnimationTick() == 5)
            attackTarget.hurt(this.level().damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
        if (this.getAnimation() == ANIMATION_PULL && attackTarget != null && this.distanceToSqr(attackTarget) < 16D && this.getAnimationTick() == 5) {
            attackTarget.hurt(this.level().damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            double attackmotionX = (Math.signum(this.getX() - attackTarget.getX()) * 0.5D - attackTarget.getDeltaMovement().z) * 0.100000000372529 * 5;
            double attackmotionY = (Math.signum(this.getY() - attackTarget.getY() + 1) * 0.5D - attackTarget.getDeltaMovement().y) * 0.100000000372529 * 5;
            double attackmotionZ = (Math.signum(this.getZ() - attackTarget.getZ()) * 0.5D - attackTarget.getDeltaMovement().z) * 0.100000000372529 * 5;

            attackTarget.setDeltaMovement(attackTarget.getDeltaMovement().add(attackmotionX, attackmotionY, attackmotionZ));
            double d0 = this.getX() - attackTarget.getX();
            double d2 = this.getZ() - attackTarget.getZ();
            double d1 = this.getY() - 1 - attackTarget.getY();
            double d3 = Math.sqrt((float) (d0 * d0 + d2 * d2));
            float f = (float) (Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
            float f1 = (float) (-(Mth.atan2(d1, d3) * (180D / Math.PI)));
            attackTarget.setXRot(updateRotation(attackTarget.getXRot(), f1, 30F));
            attackTarget.setYRot(updateRotation(attackTarget.getYRot(), f, 30F));
        }
        if (this.level().isClientSide)
            this.tail_buffer.calculateChainSwingBuffer(40, 10, 2.5F, this);
        if (this.isAgressive()) this.ticksAgressive++;
        else this.ticksAgressive = 0;

        if (this.ticksAgressive > 300 && this.isAgressive() && attackTarget == null && !this.level().isClientSide) {
            this.setAggressive(false);
            this.ticksAgressive = 0;
            this.setSinging(false);
        }

        if (this.isInWater() && !this.isSwimming()) {
            this.setSwimming(true);
        }
        if (!this.isInWater() && this.isSwimming()) {
            this.setSwimming(false);
        }
        LivingEntity target = this.getTarget();
        boolean pathOnHighGround = this.isPathOnHighGround() || !this.level().isClientSide && target != null && !target.isInWater();
        if (target == null || !target.isInWater()) {
            if (pathOnHighGround && this.isInWater()) {
                this.jumpFromGround();
                this.doWaterSplashEffect();
            }
        }
        if ((this.isInWater() && !pathOnHighGround) && this.isLandNavigator) {
            this.switchNavigator(false);
        }
        if ((!this.isInWater() || pathOnHighGround) && !this.isLandNavigator) {
            this.switchNavigator(true);
        }
        if (target instanceof Player player && player.isCreative()) {
            this.setTarget(null);
            this.setAggressive(false);
        }
        if (target != null && !this.isAgressive()) {
            this.setAggressive(true);
        }
        boolean singing = this.isActuallySinging() && !this.isAgressive() && !this.isInWater() && this.onGround();
        if (singing && this.singProgress < 20.0F) {
            this.singProgress += 1F;
        } else if (!singing && this.singProgress > 0.0F) {
            this.singProgress -= 1F;
        }
        boolean swimming = this.isSwimming();
        if (swimming && this.swimProgress < 20.0F) {
            this.swimProgress += 1F;
        } else if (!swimming && this.swimProgress > 0.0F) {
            this.swimProgress -= 0.5F;
        }
        if (!this.level().isClientSide && !GorgonEntity.isStoneMob(this) && this.isActuallySinging()) {
            if (this.tickCount % 20 == 0) {
                List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(50, 12, 50), SIREN_PREY)
                        .stream().filter(x -> !isWearingEarplugs(x)).filter(x -> x.distanceTo(this) >= 5).toList();
                this.charmingEntities.keySet().removeIf(x -> !targets.contains(x));
                targets.forEach(x -> this.charmingEntities.computeIfAbsent(x, e -> 0));
                this.charmingEntities.keySet().forEach(x -> x.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(IafMobEffects.SIREN_CHARM.get()), 30), this));
            }
            this.setSinging(true);
            this.tickCharm();
        }
        if (!this.level().isClientSide && GorgonEntity.isStoneMob(this) && this.isSinging()) {
            this.setSinging(false);
        }
        if (this.isActuallySinging() && !this.isInWater()) {
            if (this.getRandom().nextInt(3) == 0) {
                this.yBodyRot = this.getYRot();
                if (this.level().isClientSide) {
                    float radius = -0.9F;
                    float angle = (0.01745329251F * this.yBodyRot) - 3F;
                    double extraX = radius * Mth.sin((float) (Math.PI + angle));
                    double extraY = 1.2F;
                    double extraZ = radius * Mth.cos(angle);
                    this.level().addParticle(IafParticles.SIREN_MUSIC.get(), this.getX() + extraX + this.random.nextFloat() - 0.5, this.getY() + extraY + this.random.nextFloat() - 0.5, this.getZ() + extraZ + this.random.nextFloat() - 0.5, 0, 0, 0);
                }
            }
        }
        if (this.isActuallySinging() && !this.isInWater() && this.tickCount % 200 == 0)
            this.playSound(IafSounds.SIREN_SONG.get(), 2, 1);
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void tickCharm() {
        for (LivingEntity charmingEntity : this.charmingEntities.keySet()) {
            if (this.charmingEntities.getInt(charmingEntity) > IafCommonConfig.INSTANCE.siren.maxSingTime.getValue())
                this.stopCharm(charmingEntity);
            else if (!this.isAlive() || this.distanceTo(charmingEntity) > SirenEntity.SEARCH_RANGE * 2 || this.charmingEntities instanceof Player player && (player.isCreative() || player.isSpectator())) {
                this.stopCharm(charmingEntity);
                this.setAggressive(false);
            } else if (this.distanceTo(charmingEntity) < 5) {
                this.singCooldown = IafCommonConfig.INSTANCE.siren.timeBetweenSongs.getValue();
                this.setSinging(false);
                this.setTarget(charmingEntity);
                this.setAggressive(true);
                this.triggerOtherSirens(charmingEntity);
                this.stopCharm(charmingEntity);
            } else {
                this.charmingEntities.computeIntIfPresent(charmingEntity, (e, charmTime) -> charmTime + 1);
                if (charmingEntity.horizontalCollision) charmingEntity.setJumping(true);
                Vec3 velocity = charmingEntity.getDeltaMovement();
                double vx = (Math.signum(this.getX() - charmingEntity.getX()) * 0.5D - velocity.x) * 0.1;
                double vy = (Math.signum(this.getY() - charmingEntity.getY() + 1) * 0.5D - velocity.y) * 0.1;
                double vz = (Math.signum(this.getZ() - charmingEntity.getZ()) * 0.5D - velocity.z) * 0.1;
                charmingEntity.setDeltaMovement(velocity.add(vx, vy, vz));
                charmingEntity.hurtMarked = true;
                if (charmingEntity.isPassenger()) charmingEntity.stopRiding();
                if (!(this.charmingEntities instanceof Player)) {
                    Vec3 delta = this.position().subtract(charmingEntity.position()).subtract(0, 1, 0);
                    double x = delta.x();
                    double y = delta.y();
                    double z = delta.z();
                    double radius = Math.sqrt(x * x + z * z);
                    float xRot = (float) -Math.toDegrees(Mth.atan2(y, radius));
                    float yRot = (float) Math.toDegrees(Mth.atan2(z, x)) - 90.0F;
                    charmingEntity.setXRot(this.updateCharmedEntityRotation(charmingEntity.getXRot(), xRot));
                    charmingEntity.setYRot(this.updateCharmedEntityRotation(charmingEntity.getYRot(), yRot));
                }
            }
        }
    }

    private float updateCharmedEntityRotation(float angle, float targetAngle) {
        float f = Mth.wrapDegrees(targetAngle - angle);
        if (f > 30) f = 30f;
        if (f < -30) f = -30f;
        return angle + f;
    }

    public void stopCharm(LivingEntity living) {
        this.charmingEntities.removeInt(living);
        this.singCooldown = IafCommonConfig.INSTANCE.siren.timeBetweenSongs.getValue();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && source.getEntity() instanceof LivingEntity)
            this.triggerOtherSirens((LivingEntity) source.getEntity());
        return super.hurt(source, amount);
    }

    public void triggerOtherSirens(LivingEntity aggressor) {
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().inflate(12, 12, 12));
        for (Entity entity : entities) {
            if (entity instanceof SirenEntity siren) {
                siren.setTarget(aggressor);
                siren.setAggressive(true);
                siren.setSinging(false);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag list = new ListTag();
        for (Object2IntMap.Entry<LivingEntity> entry : this.charmingEntities.object2IntEntrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID("Uuid", entry.getKey().getUUID());
            nbt.putInt("CharmTime", entry.getIntValue());
            list.add(nbt);
        }
        tag.put("CharmingEntities", list);
        tag.putInt("HairColor", this.getHairColor());
        tag.putBoolean("Aggressive", this.isAgressive());
        tag.putInt("SingingPose", this.getSingingPose());
        tag.putBoolean("Singing", this.isSinging());
        tag.putBoolean("Swimming", this.isSwimming());
        tag.putBoolean("Passive", this.isCharmed());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.charmingEntities.clear();
        if (tag.contains("CharmingEntities", Tag.TAG_LIST) && this.level() instanceof ServerLevel world) {
            ListTag list = tag.getList("CharmingEntities", Tag.TAG_COMPOUND);
            for (Tag element : list)
                if (element instanceof CompoundTag nbt) {
                    Entity entity = world.getEntity(nbt.getUUID("Uuid"));
                    if (entity instanceof LivingEntity living)
                        this.charmingEntities.put(living, nbt.getInt("CharmTime"));
                }
        }
        this.setHairColor(tag.getInt("HairColor"));
        this.setAggressive(tag.getBoolean("Aggressive"));
        this.setSingingPose(tag.getInt("SingingPose"));
        this.setSinging(tag.getBoolean("Singing"));
        this.setSwimming(tag.getBoolean("Swimming"));
        this.setCharmed(tag.getBoolean("Passive"));
        this.setConfigurableAttributes();
    }

    public boolean isSinging() {
        return this.entityData.get(SINGING);
    }

    public void setSinging(boolean singing) {
        if (this.singCooldown > 0) singing = false;
        this.entityData.set(SINGING, singing);
    }

    public boolean wantsToSing() {
        return this.isSinging() && this.isInWater() && !this.isAgressive();
    }

    public boolean isActuallySinging() {
        return this.isSinging() && !this.wantsToSing();
    }

    @Override
    public boolean isSwimming() {
        if (this.level().isClientSide) {
            return this.isSwimming = this.entityData.get(SWIMMING);
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
    public void setAggressive(boolean aggressive) {
        this.entityData.set(AGGRESSIVE, aggressive);
    }

    public boolean isAgressive() {
        return this.entityData.get(AGGRESSIVE);
    }

    public boolean isCharmed() {
        return this.entityData.get(CHARMED);
    }

    public void setCharmed(boolean aggressive) {
        this.entityData.set(CHARMED, aggressive);
    }

    public int getHairColor() {
        return this.entityData.get(HAIR_COLOR);
    }

    public void setHairColor(int hairColor) {
        this.entityData.set(HAIR_COLOR, hairColor);
    }

    public int getSingingPose() {
        return this.entityData.get(SING_POSE);
    }

    public void setSingingPose(int pose) {
        this.entityData.set(SING_POSE, Mth.clamp(pose, 0, 2));
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(IafCommonConfig.INSTANCE.siren.maxHealth.getValue());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAIR_COLOR, 0);
        builder.define(SING_POSE, 0);
        builder.define(AGGRESSIVE, Boolean.FALSE);
        builder.define(SINGING, Boolean.FALSE);
        builder.define(SWIMMING, Boolean.FALSE);
        builder.define(CHARMED, Boolean.FALSE);
        builder.define(CLIMBING, (byte) 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull MobSpawnType reason, SpawnGroupData spawnDataIn) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
        this.setHairColor(this.getRandom().nextInt(3));
        this.setSingingPose(this.getRandom().nextInt(3));
        return spawnDataIn;
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
        return new Animation[]{NO_ANIMATION, ANIMATION_BITE, ANIMATION_PULL};
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isAgressive() ? IafSounds.NAGA_IDLE.get() : IafSounds.MERMAID_IDLE.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return this.isAgressive() ? IafSounds.NAGA_HURT.get() : IafSounds.MERMAID_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return this.isAgressive() ? IafSounds.NAGA_DIE.get() : IafSounds.MERMAID_DIE.get();
    }

    @Override
    public void travel(@NotNull Vec3 motion) {
        super.travel(motion);
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean shouldFear() {
        return this.isAgressive();
    }

    class SwimmingMoveHelper extends MoveControl {
        private final SirenEntity siren = SirenEntity.this;

        public SwimmingMoveHelper() {
            super(SirenEntity.this);
        }

        @Override
        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                double distanceX = this.wantedX - this.siren.getX();
                double distanceY = this.wantedY - this.siren.getY();
                double distanceZ = this.wantedZ - this.siren.getZ();
                double distance = Math.abs(distanceX * distanceX + distanceZ * distanceZ);
                double distanceWithY = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
                distanceY = distanceY / distanceWithY;
                float angle = (float) (Math.atan2(distanceZ, distanceX) * 180.0D / Math.PI) - 90.0F;
                this.siren.setYRot(this.rotlerp(this.siren.getYRot(), angle, 30.0F));
                this.siren.setSpeed(1F);
                float f1 = 0;
                float f2 = 0;
                if (distance < (double) Math.max(1.0F, this.siren.getBbWidth())) {
                    float f = this.siren.getYRot() * 0.017453292F;
                    f1 -= Mth.sin(f) * 0.35F;
                    f2 += Mth.cos(f) * 0.35F;
                }
                this.siren.setDeltaMovement(this.siren.getDeltaMovement().add(f1, this.siren.getSpeed() * distanceY * 0.1D, f2));
            } else if (this.operation == Operation.JUMPING) {
                this.siren.setSpeed((float) (this.speedModifier * this.siren.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));
                if (this.siren.onGround()) {
                    this.operation = Operation.WAIT;
                }
            } else {
                this.siren.setSpeed(0.0F);
            }
        }
    }
}
