package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.entity.util.dragon.IafDragonAttacks;
import com.iafenvoy.iceandfire.entity.util.dragon.IafDragonDestructionManager;
import com.iafenvoy.iceandfire.event.IafEvents;
import com.iafenvoy.iceandfire.particle.DragonFlameParticleType;
import com.iafenvoy.iceandfire.particle.DragonSoulFlameParticleType;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Random;

public class NetherDragonEntity extends DragonBaseEntity {
    public static final Identifier FEMALE_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/dragon/nether_dragon_female");
    public static final Identifier MALE_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/dragon/nether_dragon_male");
    public static final Identifier SKELETON_LOOT = Identifier.of(IceAndFire.MOD_ID, "entities/dragon/nether_dragon_skeleton");

    public NetherDragonEntity(EntityType<? extends NetherDragonEntity> t, World worldIn) {
        super(t, worldIn, IafDragonTypes.NETHER, 1, 1 + IafCommonConfig.INSTANCE.dragon.attackDamage.getValue(), IafCommonConfig.INSTANCE.dragon.netherDragonMaxHealth.getValue() * 0.04, IafCommonConfig.INSTANCE.dragon.netherDragonMaxHealth.getValue(), 0.15F, 0.4F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
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
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
    }

    @Override
    protected boolean shouldTarget(Entity entity) {
        if (entity instanceof DragonBaseEntity && !this.isTamed()) {
            return entity.getType() != this.getType() && this.getWidth() >= entity.getWidth() && !((DragonBaseEntity) entity).isMobDead();
        }
        return entity instanceof PlayerEntity || DragonUtils.isDragonTargetable(entity, IafEntityTags.FIRE_DRAGON_TARGETS) || !this.isTamed() && DragonUtils.isVillager(entity);
    }

    @Override
    public String getVariantName(int variant) {
        return "nether_";
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
        this.getLookControl().lookAt(entityIn, 30.0F, 30.0F);
        if (!this.isPlayingAttackAnimation()) {
            switch (this.groundAttack) {
                case BITE -> this.setAnimation(ANIMATION_BITE);
                case TAIL_WHIP -> this.setAnimation(ANIMATION_TAILWHACK);
                case SHAKE_PREY -> {
                    boolean flag = false;
                    if (new Random().nextInt(2) == 0 && this.isDirectPathBetweenPoints(this, this.getPos().add(0, this.getHeight() / 2, 0), entityIn.getPos().add(0, entityIn.getHeight() / 2, 0)) &&
                            entityIn.getWidth() < this.getWidth() * 0.5F && this.getControllingPassenger() == null && this.getDragonStage() > 1 && !(entityIn instanceof DragonBaseEntity) && !DragonUtils.isAnimaniaMob(entityIn)) {
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
    public void tickMovement() {
        super.tickMovement();
        LivingEntity attackTarget = this.getTarget();
        if (!this.getWorld().isClient && attackTarget != null) {
            if (this.getBoundingBox().expand(2.5F + this.getRenderSize() * 0.33F, 2.5F + this.getRenderSize() * 0.33F, 2.5F + this.getRenderSize() * 0.33F).intersects(attackTarget.getBoundingBox())) {
                this.tryAttack(attackTarget);
            }
            if (this.groundAttack == IafDragonAttacks.Ground.FIRE && (this.usingGroundAttack || this.isOnGround())) {
                this.shootSoulFireAtMob(attackTarget);
            }
            if (this.airAttack == IafDragonAttacks.Air.TACKLE && !this.usingGroundAttack && this.squaredDistanceTo(attackTarget) < 100) {
                double difX = attackTarget.getX() - this.getX();
                double difY = attackTarget.getY() + attackTarget.getHeight() - this.getY();
                double difZ = attackTarget.getZ() - this.getZ();
                this.setVelocity(this.getVelocity().add(difX * 0.1D, difY * 0.1D, difZ * 0.1D));
                if (this.getBoundingBox().expand(1 + this.getRenderSize() * 0.5F, 1 + this.getRenderSize() * 0.5F, 1 + this.getRenderSize() * 0.5F).intersects(attackTarget.getBoundingBox())) {
                    this.tryAttack(attackTarget);
                    this.usingGroundAttack = true;
                    this.randomizeAttacks();
                    this.setFlying(false);
                    this.setHovering(false);
                }
            }
        }
    }

    @Override
    protected void breathFireAtPos(BlockPos burningTarget) {
        if (this.isBreathingFire()) {
            if (this.isActuallyBreathingFire()) {
                this.setYaw(this.bodyYaw);
                if (this.age % 5 == 0) {
                    this.playSound(IafSounds.FIREDRAGON_BREATH.get(), 4, 1);
                }
                this.stimulateSoulFire(burningTarget.getX() + 0.5F, burningTarget.getY() + 0.5F, burningTarget.getZ() + 0.5F, 1);
            }
        } else {
            this.setBreathingFire(true);
        }
    }

    @Override
    public void riderShootFire(Entity controller) {
        if (this.getRandom().nextInt(5) == 0 && !this.isBaby()) {
            if (this.getAnimation() != ANIMATION_FIRECHARGE) {
                this.setAnimation(ANIMATION_FIRECHARGE);
            } else if (this.getAnimationTick() == 20) {
                this.setYaw(this.bodyYaw);
                Vec3d headVec = this.getHeadPosition();
                this.playSound(IafSounds.FIREDRAGON_BREATH.get(), 4, 1);
                double d2 = controller.getRotationVector().x;
                double d3 = controller.getRotationVector().y;
                double d4 = controller.getRotationVector().z;
                float inaccuracy = 1.0F;
                d2 = d2 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d3 = d3 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d4 = d4 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                NetherDragonChargeEntity entitylargefireball = new NetherDragonChargeEntity(
                        IafEntities.NETHER_DRAGON_CHARGE.get(), this.getWorld(), this, d2, d3, d4);

                entitylargefireball.setPosition(headVec.x, headVec.y, headVec.z);
                if (!this.getWorld().isClient) {
                    this.getWorld().spawnEntity(entitylargefireball);
                }
            }
        } else {
            if (this.isBreathingFire()) {
                if (this.isActuallyBreathingFire()) {
                    this.setYaw(this.bodyYaw);
                    if (this.age % 5 == 0) {
                        this.playSound(IafSounds.FIREDRAGON_BREATH.get(), 4, 1);
                    }
                    HitResult mop = this.rayTraceRider(controller, 10 * this.getDragonStage(), 1.0F);
                    if (mop != null) {
                        this.stimulateSoulFire(mop.getPos().x, mop.getPos().y, mop.getPos().z, 1);
                    }
                }
            } else {
                this.setBreathingFire(true);
            }
        }
    }

    private void shootSoulFireAtMob(LivingEntity entity) {
        if (this.usingGroundAttack && this.groundAttack == IafDragonAttacks.Ground.FIRE || !this.usingGroundAttack && (this.airAttack == IafDragonAttacks.Air.SCORCH_STREAM || this.airAttack == IafDragonAttacks.Air.HOVER_BLAST)) {
            if (this.usingGroundAttack && this.getRandom().nextInt(5) == 0 || !this.usingGroundAttack && this.airAttack == IafDragonAttacks.Air.HOVER_BLAST) {
                if (this.getAnimation() != ANIMATION_FIRECHARGE) {
                    this.setAnimation(ANIMATION_FIRECHARGE);
                } else if (this.getAnimationTick() == 20) {
                    this.setYaw(this.bodyYaw);
                    Vec3d headVec = this.getHeadPosition();
                    double d2 = entity.getX() - headVec.x;
                    double d3 = entity.getY() - headVec.y;
                    double d4 = entity.getZ() - headVec.z;
                    float inaccuracy = 1.0F;
                    d2 = d2 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    d3 = d3 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    d4 = d4 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                    this.playSound(IafSounds.FIREDRAGON_BREATH.get(), 4, 1);
                    NetherDragonChargeEntity entitylargefireball = new NetherDragonChargeEntity(IafEntities.NETHER_DRAGON_CHARGE.get(), this.getWorld(), this, d2, d3, d4);

                    entitylargefireball.setPosition(headVec.x, headVec.y, headVec.z);
                    if (!this.getWorld().isClient)
                        this.getWorld().spawnEntity(entitylargefireball);
                    if (!entity.isAlive())
                        this.setBreathingFire(false);
                    this.randomizeAttacks();
                }
            } else {
                if (this.isBreathingFire()) {
                    if (this.isActuallyBreathingFire()) {
                        this.setYaw(this.bodyYaw);
                        if (this.age % 5 == 0)
                            this.playSound(IafSounds.FIREDRAGON_BREATH.get(), 4, 1);
                        this.stimulateSoulFire(entity.getX(), entity.getY(), entity.getZ(), 1);
                        if (!entity.isAlive()) {
                            this.setBreathingFire(false);
                            this.randomizeAttacks();
                        }
                    }
                } else {
                    this.setBreathingFire(true);
                }
            }
        }
        this.lookAtEntity(entity, 360, 360);
    }

    private void stimulateSoulFire(double burnX, double burnY, double burnZ, int syncType) {
        if (IafEvents.ON_DRAGON_FIRE_BLOCK.invoker().onFireBlock(this, burnX, burnY, burnZ)) return;
        if (syncType > 2 && syncType < 6) {
            if (this.getAnimation() != ANIMATION_FIRECHARGE) {
                this.setAnimation(ANIMATION_FIRECHARGE);
            } else if (this.getAnimationTick() == 20) {
                this.setYaw(this.bodyYaw);
                Vec3d headVec = this.getHeadPosition();
                double d2 = burnX - headVec.x;
                double d3 = burnY - headVec.y;
                double d4 = burnZ - headVec.z;
                float inaccuracy = 1.0F;
                d2 = d2 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d3 = d3 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                d4 = d4 + this.random.nextGaussian() * 0.007499999832361937D * inaccuracy;
                this.playSound(IafSounds.FIREDRAGON_BREATH.get(), 4, 1);
                NetherDragonChargeEntity entitylargefireball = new NetherDragonChargeEntity(IafEntities.NETHER_DRAGON_CHARGE.get(), this.getWorld(), this, d2, d3, d4);
                entitylargefireball.setPosition(headVec.x, headVec.y, headVec.z);
                if (!this.getWorld().isClient)
                    this.getWorld().spawnEntity(entitylargefireball);
                this.randomizeAttacks();
            }
            return;
        }
        this.getNavigation().stop();
        this.burnParticleX = burnX;
        this.burnParticleY = burnY;
        this.burnParticleZ = burnZ;
        Vec3d headPos = this.getHeadPosition();
        double d2 = burnX - headPos.x;
        double d3 = burnY - headPos.y;
        double d4 = burnZ - headPos.z;
        double distance = Math.max(2.5F * Math.sqrt(this.squaredDistanceTo(burnX, burnY, burnZ)), 0);
        double conqueredDistance = this.burnProgress / 40D * distance;
        int increment = (int) Math.ceil(conqueredDistance / 100);
        int particleCount = this.getDragonStage() <= 3 ? 6 : 3;
        for (int i = 0; i < conqueredDistance; i += increment) {
            double progressX = headPos.x + d2 * (i / (float) distance);
            double progressY = headPos.y + d3 * (i / (float) distance);
            double progressZ = headPos.z + d4 * (i / (float) distance);
            if (this.canPositionBeSeen(progressX, progressY, progressZ)) {
                if (this.random.nextInt(particleCount) == 0) {
                    Vec3d velocity = new Vec3d(progressX, progressY, progressZ).subtract(headPos);
                    if (this.getWorld() instanceof ServerWorld serverWorld)
                        serverWorld.spawnParticles(new DragonSoulFlameParticleType(this.getScaleFactor()), headPos.x, headPos.y, headPos.z, 0, velocity.x, velocity.y, velocity.z, 1);
                }
            } else if (!this.getWorld().isClient) {
                HitResult result = this.getWorld().raycast(new RaycastContext(new Vec3d(this.getX(), this.getY() + this.getStandingEyeHeight(), this.getZ()), new Vec3d(progressX, progressY, progressZ), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
                Vec3d vec3 = result.getPos();
                BlockPos pos = BlockPos.ofFloored(vec3);
                IafDragonDestructionManager.destroyAreaBreath(this.getWorld(), pos, this);
            }
        }
        if (this.burnProgress >= 40D && this.canPositionBeSeen(burnX, burnY, burnZ)) {
            double spawnX = burnX + (this.random.nextFloat() * 3.0) - 1.5;
            double spawnY = burnY + (this.random.nextFloat() * 3.0) - 1.5;
            double spawnZ = burnZ + (this.random.nextFloat() * 3.0) - 1.5;
            if (!this.getWorld().isClient) {
                BlockPos blockpos = BlockPos.ofFloored(spawnX, spawnY, spawnZ);
                BlockState blockState = this.getWorld().getBlockState(blockpos);
                BlockState blockStateDown = this.getWorld().getBlockState(blockpos.down());
                
                // Place soul fire on any flammable block or soul fire base blocks
                if (blockState.isAir() && (blockStateDown.isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS) || 
                    blockStateDown.isIn(BlockTags.LOGS) || 
                    blockStateDown.isIn(BlockTags.PLANKS) || 
                    blockStateDown.isIn(BlockTags.WOOL) || 
                    blockStateDown.isIn(BlockTags.LEAVES))) {
                    this.getWorld().setBlockState(blockpos, net.minecraft.block.Blocks.SOUL_FIRE.getDefaultState());
                }
                IafDragonDestructionManager.destroyAreaBreath(this.getWorld(), blockpos, this);
            }
        }
    }

    @Override
    public void stimulateFire(double burnX, double burnY, double burnZ, int syncType) {
        // Call our soul fire version instead
        this.stimulateSoulFire(burnX, burnY, burnZ, syncType);
    }

    @Override
    protected float getVelocityMultiplier() {
        // Disable soul sand slow down
        if (this.getWorld().getBlockState(this.getVelocityAffectingPos()).isIn(BlockTags.SOUL_SPEED_BLOCKS))
            return this.getDragonStage() >= 2 ? 1.0f : 0.8f;
        return super.getVelocityMultiplier();
    }

    @Override
    public Identifier getDeadLootTable() {
        if (this.getDeathStage() >= (this.getAgeInDays() / 5) / 2) {
            return SKELETON_LOOT;
        } else {
            return this.isMale() ? MALE_LOOT : FEMALE_LOOT;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isTeen() ? IafSounds.FIREDRAGON_TEEN_IDLE.get() : this.isMature() ? IafSounds.FIREDRAGON_ADULT_IDLE.get() : IafSounds.FIREDRAGON_CHILD_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isTeen() ? IafSounds.FIREDRAGON_TEEN_HURT.get() : this.isMature() ? IafSounds.FIREDRAGON_ADULT_HURT.get() : IafSounds.FIREDRAGON_CHILD_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isTeen() ? IafSounds.FIREDRAGON_TEEN_DEATH.get() : this.isMature() ? IafSounds.FIREDRAGON_ADULT_DEATH.get() : IafSounds.FIREDRAGON_CHILD_DEATH.get();
    }

    @Override
    public SoundEvent getRoarSound() {
        return this.isTeen() ? IafSounds.FIREDRAGON_TEEN_ROAR.get() : this.isMature() ? IafSounds.FIREDRAGON_ADULT_ROAR.get() : IafSounds.FIREDRAGON_CHILD_ROAR.get();
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, ANIMATION_EAT, ANIMATION_SPEAK, ANIMATION_BITE, ANIMATION_SHAKEPREY, ANIMATION_TAILWHACK, ANIMATION_FIRECHARGE, ANIMATION_WINGBLAST, ANIMATION_ROAR, ANIMATION_EPIC_ROAR};
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() != null && stack.getItem() == IafItems.FIRE_STEW.get();
    }

    @Override
    protected void spawnDeathParticles() {
        for (int i = 0; i < 20; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getParticleX(1.0D), this.getRandomBodyY(), this.getParticleZ(1.0D), d0, d1, d2);
        }
    }

    @Override
    public void spawnBabyParticles() {
        for (int i = 0; i < 20; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getParticleX(1.0D), this.getRandomBodyY(), this.getParticleZ(1.0D), d0, d1, d2);
        }
    }

    @Override
    public Item getSkull() {
        return IafItems.DRAGON_SKULL_NETHER.get();
    }

    @Override
    public Item getBloodItem() {
        return IafItems.NETHER_DRAGON_BLOOD.get();
    }

    @Override
    public Item getFleshItem() {
        return IafItems.FIRE_DRAGON_FLESH.get();
    }

    @Override
    public ItemConvertible getHeartItem() {
        return IafItems.FIRE_DRAGON_HEART.get();
    }

    public static net.minecraft.entity.attribute.DefaultAttributeContainer.Builder bakeAttributes() {
        return DragonBaseEntity.bakeAttributes();
    }
}
