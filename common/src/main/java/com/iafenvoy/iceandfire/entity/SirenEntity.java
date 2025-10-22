package com.iafenvoy.iceandfire.entity;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.component.SirenData;
import com.iafenvoy.iceandfire.entity.ai.AquaticAIGetInWaterGoal;
import com.iafenvoy.iceandfire.entity.ai.AquaticAIGetOutOfWaterGoal;
import com.iafenvoy.iceandfire.entity.ai.SirenAIFindWaterTargetGoal;
import com.iafenvoy.iceandfire.entity.ai.SirenAIWanderGoal;
import com.iafenvoy.iceandfire.entity.util.ChainBuffer;
import com.iafenvoy.iceandfire.entity.util.IHasCustomizableAttributes;
import com.iafenvoy.iceandfire.entity.util.IVillagerFear;
import com.iafenvoy.iceandfire.entity.util.SirenAffectable;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafParticles;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.util.List;

public class SirenEntity extends HostileEntity implements IAnimatedEntity, IVillagerFear, IHasCustomizableAttributes {
    public static final int SEARCH_RANGE = 32;
    public static final Predicate<Entity> SIREN_PREY = entity -> (entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative() && !entity.isSpectator()) || entity instanceof MerchantEntity || entity instanceof SirenAffectable;
    public static final Animation ANIMATION_BITE = Animation.create(20);
    public static final Animation ANIMATION_PULL = Animation.create(20);
    private static final TrackedData<Integer> HAIR_COLOR = DataTracker.registerData(SirenEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> AGGRESSIVE = DataTracker.registerData(SirenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> SING_POSE = DataTracker.registerData(SirenEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SINGING = DataTracker.registerData(SirenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SWIMMING = DataTracker.registerData(SirenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CHARMED = DataTracker.registerData(SirenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Byte> CLIMBING = DataTracker.registerData(SirenEntity.class, TrackedDataHandlerRegistry.BYTE);
    public ChainBuffer tail_buffer;
    public float singProgress;
    public float swimProgress;
    public int singCooldown;
    private int animationTick;
    private Animation currentAnimation;
    private boolean isSinging;
    private boolean isSwimming;
    private boolean isLandNavigator;
    private int ticksAgressive;

    public SirenEntity(EntityType<SirenEntity> t, World worldIn) {
        super(t, worldIn);
        this.switchNavigator(true);
        if (worldIn.isClient) this.tail_buffer = new ChainBuffer();
    }

    public static boolean isWearingEarplugs(LivingEntity entity) {
        return entity.getEquippedStack(EquipmentSlot.HEAD).isOf(IafItems.EARPLUGS.get());
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, IafCommonConfig.INSTANCE.siren.maxHealth.getValue())
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1);
    }

    public static float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);
        return angle + MathHelper.clamp(f, -maxIncrease, maxIncrease);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SirenAIFindWaterTargetGoal(this));
        this.goalSelector.add(1, new AquaticAIGetInWaterGoal(this, 1.0D));
        this.goalSelector.add(1, new AquaticAIGetOutOfWaterGoal(this, 1.0D));
        this.goalSelector.add(2, new SirenAIWanderGoal(this, 1));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F, 1.0F));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, entity -> entity instanceof PlayerEntity player && SirenEntity.this.isAgressive() && !(player.isCreative() || player.isSpectator())));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, MerchantEntity.class, 10, true, false, entity -> SirenEntity.this.isAgressive()));
    }

    @Override
    public int getXpToDrop() {
        return 8;
    }

    @Override
    public float getPathfindingFavor(BlockPos pos) {
        return this.getWorld().getBlockState(pos).isOf(Blocks.WATER) ? 10F : super.getPathfindingFavor(pos);
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
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

    public boolean isDirectPathBetweenPoints(Vec3d vec1, Vec3d pos) {
        Vec3d Vector3d1 = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        return this.getWorld().raycast(new RaycastContext(vec1, Vector3d1, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public float getPathfindingPenalty(PathNodeType nodeType) {
        return nodeType == PathNodeType.WATER ? 0F : super.getPathfindingPenalty(nodeType);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new AmphibiousSwimNavigation(this, this.getWorld());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new SwimmingMoveHelper();
            this.navigation = new SwimNavigation(this, this.getWorld());
            this.isLandNavigator = false;
        }
    }

    private boolean isPathOnHighGround() {
        if (this.navigation != null && this.navigation.getCurrentPath() != null && this.navigation.getCurrentPath().getEnd() != null) {
            BlockPos target = new BlockPos(this.navigation.getCurrentPath().getEnd().x, this.navigation.getCurrentPath().getEnd().y, this.navigation.getCurrentPath().getEnd().z);
            BlockPos siren = this.getBlockPos();
            return this.getWorld().isAir(siren.up()) && this.getWorld().isAir(target.up()) && target.getY() >= siren.getY();
        }
        return false;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.bodyYaw = this.getYaw();

        LivingEntity attackTarget = this.getTarget();
        if (this.singCooldown > 0) {
            this.singCooldown--;
            this.setSinging(false);
        }
        if (!this.getWorld().isClient && attackTarget == null && !this.isAgressive())
            this.setSinging(true);
        if (this.getAnimation() == ANIMATION_BITE && attackTarget != null && this.squaredDistanceTo(attackTarget) < 7D && this.getAnimationTick() == 5)
            attackTarget.damage(this.getWorld().getDamageSources().mobAttack(this), (float) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue());
        if (this.getAnimation() == ANIMATION_PULL && attackTarget != null && this.squaredDistanceTo(attackTarget) < 16D && this.getAnimationTick() == 5) {
            attackTarget.damage(this.getWorld().getDamageSources().mobAttack(this), (float) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue());
            double attackmotionX = (Math.signum(this.getX() - attackTarget.getX()) * 0.5D - attackTarget.getVelocity().z) * 0.100000000372529 * 5;
            double attackmotionY = (Math.signum(this.getY() - attackTarget.getY() + 1) * 0.5D - attackTarget.getVelocity().y) * 0.100000000372529 * 5;
            double attackmotionZ = (Math.signum(this.getZ() - attackTarget.getZ()) * 0.5D - attackTarget.getVelocity().z) * 0.100000000372529 * 5;

            attackTarget.setVelocity(attackTarget.getVelocity().add(attackmotionX, attackmotionY, attackmotionZ));
            double d0 = this.getX() - attackTarget.getX();
            double d2 = this.getZ() - attackTarget.getZ();
            double d1 = this.getY() - 1 - attackTarget.getY();
            double d3 = Math.sqrt((float) (d0 * d0 + d2 * d2));
            float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
            float f1 = (float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
            attackTarget.setPitch(updateRotation(attackTarget.getPitch(), f1, 30F));
            attackTarget.setYaw(updateRotation(attackTarget.getYaw(), f, 30F));
        }
        if (this.getWorld().isClient)
            this.tail_buffer.calculateChainSwingBuffer(40, 10, 2.5F, this);
        if (this.isAgressive()) this.ticksAgressive++;
        else this.ticksAgressive = 0;

        if (this.ticksAgressive > 300 && this.isAgressive() && attackTarget == null && !this.getWorld().isClient) {
            this.setAttacking(false);
            this.ticksAgressive = 0;
            this.setSinging(false);
        }

        if (this.isTouchingWater() && !this.isSwimming()) {
            this.setSwimming(true);
        }
        if (!this.isTouchingWater() && this.isSwimming()) {
            this.setSwimming(false);
        }
        LivingEntity target = this.getTarget();
        boolean pathOnHighGround = this.isPathOnHighGround() || !this.getWorld().isClient && target != null && !target.isTouchingWater();
        if (target == null || !target.isTouchingWater()) {
            if (pathOnHighGround && this.isTouchingWater()) {
                this.jump();
                this.onSwimmingStart();
            }
        }
        if ((this.isTouchingWater() && !pathOnHighGround) && this.isLandNavigator) {
            this.switchNavigator(false);
        }
        if ((!this.isTouchingWater() || pathOnHighGround) && !this.isLandNavigator) {
            this.switchNavigator(true);
        }
        if (target instanceof PlayerEntity player && player.isCreative()) {
            this.setTarget(null);
            this.setAttacking(false);
        }
        if (target != null && !this.isAgressive()) {
            this.setAttacking(true);
        }
        boolean singing = this.isActuallySinging() && !this.isAgressive() && !this.isTouchingWater() && this.isOnGround();
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
        if (!this.getWorld().isClient && !GorgonEntity.isStoneMob(this) && this.isActuallySinging()) {
            this.updateLure();
            this.checkForPrey();

        }
        if (!this.getWorld().isClient && GorgonEntity.isStoneMob(this) && this.isSinging()) {
            this.setSinging(false);
        }
        if (this.isActuallySinging() && !this.isTouchingWater()) {
            if (this.getRandom().nextInt(3) == 0) {
                this.bodyYaw = this.getYaw();
                if (this.getWorld().isClient) {
                    float radius = -0.9F;
                    float angle = (0.01745329251F * this.bodyYaw) - 3F;
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraY = 1.2F;
                    double extraZ = radius * MathHelper.cos(angle);
                    this.getWorld().addParticle(IafParticles.SIREN_MUSIC.get(), this.getX() + extraX + this.random.nextFloat() - 0.5, this.getY() + extraY + this.random.nextFloat() - 0.5, this.getZ() + extraZ + this.random.nextFloat() - 0.5, 0, 0, 0);
                }
            }
        }
        if (this.isActuallySinging() && !this.isTouchingWater() && this.age % 200 == 0) {
            this.playSound(IafSounds.SIREN_SONG.get(), 2, 1);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void checkForPrey() {
        this.setSinging(true);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() != null && source.getAttacker() instanceof LivingEntity) {
            this.triggerOtherSirens((LivingEntity) source.getAttacker());
        }
        return super.damage(source, amount);
    }

    public void triggerOtherSirens(LivingEntity aggressor) {
        List<Entity> entities = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(12, 12, 12));
        for (Entity entity : entities) {
            if (entity instanceof SirenEntity siren) {
                siren.setTarget(aggressor);
                siren.setAttacking(true);
                siren.setSinging(false);
            }
        }
    }

    public void updateLure() {
        if (this.age % 20 == 0) {
            List<LivingEntity> entities = this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(50, 12, 50), SIREN_PREY);
            for (LivingEntity entity : entities) {
                if (isWearingEarplugs(entity)) continue;
                SirenData sirenData = SirenData.get(entity);
                if (!sirenData.isCharmed()) sirenData.setCharmed(this);
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putInt("HairColor", this.getHairColor());
        tag.putBoolean("Aggressive", this.isAgressive());
        tag.putInt("SingingPose", this.getSingingPose());
        tag.putBoolean("Singing", this.isSinging());
        tag.putBoolean("Swimming", this.isSwimming());
        tag.putBoolean("Passive", this.isCharmed());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        this.setHairColor(tag.getInt("HairColor"));
        this.setAttacking(tag.getBoolean("Aggressive"));
        this.setSingingPose(tag.getInt("SingingPose"));
        this.setSinging(tag.getBoolean("Singing"));
        this.setSwimming(tag.getBoolean("Swimming"));
        this.setCharmed(tag.getBoolean("Passive"));
        this.setConfigurableAttributes();
    }

    public boolean isSinging() {
        return this.isSinging = this.dataTracker.get(SINGING);
    }

    public void setSinging(boolean singing) {
        if (this.singCooldown > 0)
            singing = false;
        this.dataTracker.set(SINGING, singing);
        this.isSinging = singing;
    }

    public boolean wantsToSing() {
        return this.isSinging() && this.isTouchingWater() && !this.isAgressive();
    }

    public boolean isActuallySinging() {
        return this.isSinging() && !this.wantsToSing();
    }

    @Override
    public boolean isSwimming() {
        if (this.getWorld().isClient) {
            return this.isSwimming = this.dataTracker.get(SWIMMING);
        }
        return this.isSwimming;
    }

    @Override
    public void setSwimming(boolean swimming) {
        this.dataTracker.set(SWIMMING, swimming);
        if (!this.getWorld().isClient) {
            this.isSwimming = swimming;
        }
    }

    @Override
    public void setAttacking(boolean aggressive) {
        this.dataTracker.set(AGGRESSIVE, aggressive);
    }

    public boolean isAgressive() {
        return this.dataTracker.get(AGGRESSIVE);
    }

    public boolean isCharmed() {
        return this.dataTracker.get(CHARMED);
    }

    public void setCharmed(boolean aggressive) {
        this.dataTracker.set(CHARMED, aggressive);
    }

    public int getHairColor() {
        return this.dataTracker.get(HAIR_COLOR);
    }

    public void setHairColor(int hairColor) {
        this.dataTracker.set(HAIR_COLOR, hairColor);
    }

    public int getSingingPose() {
        return this.dataTracker.get(SING_POSE);
    }

    public void setSingingPose(int pose) {
        this.dataTracker.set(SING_POSE, MathHelper.clamp(pose, 0, 2));
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(IafCommonConfig.INSTANCE.siren.maxHealth.getValue());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAIR_COLOR, 0);
        builder.add(SING_POSE, 0);
        builder.add(AGGRESSIVE, Boolean.FALSE);
        builder.add(SINGING, Boolean.FALSE);
        builder.add(SWIMMING, Boolean.FALSE);
        builder.add(CHARMED, Boolean.FALSE);
        builder.add(CLIMBING, (byte) 0);
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn) {
        spawnDataIn = super.initialize(worldIn, difficultyIn, reason, spawnDataIn);
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
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isAgressive() ? IafSounds.NAGA_HURT.get() : IafSounds.MERMAID_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isAgressive() ? IafSounds.NAGA_DIE.get() : IafSounds.MERMAID_DIE.get();
    }

    @Override
    public void travel(Vec3d motion) {
        super.travel(motion);
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceToClosestPlayer) {
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
            if (this.state == State.MOVE_TO) {
                double distanceX = this.targetX - this.siren.getX();
                double distanceY = this.targetY - this.siren.getY();
                double distanceZ = this.targetZ - this.siren.getZ();
                double distance = Math.abs(distanceX * distanceX + distanceZ * distanceZ);
                double distanceWithY = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
                distanceY = distanceY / distanceWithY;
                float angle = (float) (Math.atan2(distanceZ, distanceX) * 180.0D / Math.PI) - 90.0F;
                this.siren.setYaw(this.wrapDegrees(this.siren.getYaw(), angle, 30.0F));
                this.siren.setMovementSpeed(1F);
                float f1 = 0;
                float f2 = 0;
                if (distance < (double) Math.max(1.0F, this.siren.getWidth())) {
                    float f = this.siren.getYaw() * 0.017453292F;
                    f1 -= MathHelper.sin(f) * 0.35F;
                    f2 += MathHelper.cos(f) * 0.35F;
                }
                this.siren.setVelocity(this.siren.getVelocity().add(f1, this.siren.getMovementSpeed() * distanceY * 0.1D, f2));
            } else if (this.state == State.JUMPING) {
                this.siren.setMovementSpeed((float) (this.speed * this.siren.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue()));
                if (this.siren.isOnGround()) {
                    this.state = State.WAIT;
                }
            } else {
                this.siren.setMovementSpeed(0.0F);
            }
        }
    }
}
