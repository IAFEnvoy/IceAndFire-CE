package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.iceandfire.entity.ai.*;
import com.iafenvoy.iceandfire.entity.pathfinding.PathNavigateFlyingCreature;
import com.iafenvoy.iceandfire.entity.util.*;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.entity.util.dragon.IDragonFlute;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.iafenvoy.iceandfire.render.model.IFChainBuffer;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;

@SuppressWarnings("ALL")
public class EntityAmphithere extends TameableEntity implements ISyncMount, IAnimatedEntity, IPhasesThroughBlock, IFlapable, IDragonFlute, IFlyingMount, IHasCustomizableAttributes, ICustomMoveController {
    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_BITE_RIDER = Animation.create(15);
    public static final Animation ANIMATION_WING_BLAST = Animation.create(30);
    public static final Animation ANIMATION_TAIL_WHIP = Animation.create(30);
    public static final Animation ANIMATION_SPEAK = Animation.create(10);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(EntityAmphithere.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> FLYING = DataTracker.registerData(EntityAmphithere.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> FLAP_TICKS = DataTracker.registerData(EntityAmphithere.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> CONTROL_STATE = DataTracker.registerData(EntityAmphithere.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> COMMAND = DataTracker.registerData(EntityAmphithere.class, TrackedDataHandlerRegistry.INTEGER);
    public float flapProgress;
    public float groundProgress = 0;
    public float sitProgress = 0;
    public float diveProgress = 0;

    public IFChainBuffer roll_buffer;
    public IFChainBuffer tail_buffer;
    public IFChainBuffer pitch_buffer;
    public BlockPos orbitPos = null;
    public float orbitRadius = 0.0F;
    public boolean isFallen;
    public BlockPos homePos;
    public boolean hasHomePosition = false;
    protected FlightBehavior flightBehavior = FlightBehavior.WANDER;
    protected int ticksCircling = 0;
    private int animationTick;
    private Animation currentAnimation;
    private int flapTicks = 0;
    private int flightCooldown = 0;
    private int ticksFlying = 0;
    private boolean isFlying;
    private boolean changedFlightBehavior = false;
    private int ticksStill = 0;
    private int ridingTime = 0;
    private boolean isSitting;
    /*
          0 = ground/walking
          1 = ai flight
          2 = controlled flight
       */
    private int navigatorType = 0;

    public EntityAmphithere(EntityType<EntityAmphithere> type, World worldIn) {
        super(type, worldIn);
        if (worldIn.isClient) {
            this.roll_buffer = new IFChainBuffer();
            this.pitch_buffer = new IFChainBuffer();
            this.tail_buffer = new IFChainBuffer();
        }
        this.setStepHeight(1F);
        this.switchNavigator(0);
    }

    public static BlockPos getPositionRelativetoGround(Entity entity, World world, int x, int z, Random rand) {
        BlockPos pos = new BlockPos(x, entity.getBlockY(), z);
        for (int yDown = 0; yDown < 6 + rand.nextInt(6); yDown++) {
            if (!world.isAir(pos.down(yDown))) {
                return pos.up(yDown);
            }
        }
        return pos;
    }

    public static boolean canAmphithereSpawnOn(EntityType<EntityAmphithere> parrotIn, ServerWorldAccess worldIn, SpawnReason reason, BlockPos p_223317_3_, Random random) {
        BlockState blockState = worldIn.getBlockState(p_223317_3_.down());
        Block block = blockState.getBlock();
        return (blockState.isIn(BlockTags.LEAVES)
                || block == Blocks.GRASS_BLOCK
                || blockState.isIn(BlockTags.LOGS)
                || block == Blocks.AIR);
    }

    public static BlockPos getPositionInOrbit(EntityAmphithere entity, World world, BlockPos orbit, Random rand) {
        float possibleOrbitRadius = (entity.orbitRadius + 10.0F);
        float radius = 10;
        if (entity.getCommand() == 2) {
            if (entity.getOwner() != null) {
                orbit = entity.getOwner().getBlockPos().up(7);
                radius = 5;
            }
        } else if (entity.hasHomePosition) {
            orbit = entity.homePos.up(30);
            radius = 30;
        }
        float angle = (0.01745329251F * possibleOrbitRadius);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = BlockPos.ofFloored(orbit.getX() + extraX, orbit.getY(), orbit.getZ() + extraZ);
        entity.orbitRadius = possibleOrbitRadius;
        return radialPos;
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, IafCommonConfig.INSTANCE.amphithere.maxHealth.getValue())
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, IafCommonConfig.INSTANCE.amphithere.attackDamage.getValue())
                .add(EntityAttributes.GENERIC_FLYING_SPEED, IafCommonConfig.INSTANCE.amphithere.flightSpeed.getValue())
                //FOLLOW RANGE
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }

    @Override
    public boolean canSpawn(WorldView worldIn) {
        if (worldIn.doesNotIntersectEntities(this) && !worldIn.containsFluid(this.getBoundingBox())) {
            BlockPos blockpos = this.getBlockPos();
            if (blockpos.getY() < worldIn.getSeaLevel())
                return false;

            BlockState blockstate = worldIn.getBlockState(blockpos.down());
            return blockstate.isOf(Blocks.GRASS_BLOCK) || blockstate.isIn(BlockTags.LEAVES);
        }

        return false;
    }

    @Override
    protected void fall(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public float getPathfindingFavor(BlockPos pos) {
        if (this.isFlying()) return this.getWorld().isAir(pos) ? 10F : 0F;
        else return super.getPathfindingFavor(pos);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);

        if (itemstack != null && itemstack.isIn(IafItemTags.BREED_AMPITHERE)) {
            if (this.getBreedingAge() == 0 && !this.isInLove()) {
                this.setSitting(false);
                this.lovePlayer(player);
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
                if (!player.isCreative())
                    itemstack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }
        if (itemstack != null && itemstack.isIn(IafItemTags.HEAL_AMPITHERE) && this.getHealth() < this.getMaxHealth()) {
            this.heal(5);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
            if (!player.isCreative())
                itemstack.decrement(1);
            return ActionResult.SUCCESS;
        }
        if (super.interactMob(player, hand) == ActionResult.PASS) {
            if (itemstack != null && itemstack.getItem() == IafItems.DRAGON_STAFF.get() && this.isOwner(player)) {
                if (player.isSneaking()) {
                    this.homePos = this.getBlockPos();
                    this.hasHomePosition = true;
                    player.sendMessage(Text.translatable("amphithere.command.new_home", this.homePos.getX(), this.homePos.getY(), this.homePos.getZ()), true);
                    return ActionResult.SUCCESS;
                }
                return ActionResult.SUCCESS;
            }
            if (player.isSneaking() && this.isOwner(player)) {
                if (player.getStackInHand(hand).isEmpty()) {
                    this.setCommand(this.getCommand() + 1);
                    if (this.getCommand() > 2)
                        this.setCommand(0);
                    player.sendMessage(Text.translatable("amphithere.command." + this.getCommand()), true);
                    this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1, 1);
                    return ActionResult.SUCCESS;
                }
                return ActionResult.SUCCESS;
            } else {
                if ((!this.isTamed() || this.isOwner(player)) && !this.isBaby()) {
                    assert itemstack != null;
                    if (itemstack.isEmpty()) {
                        player.startRiding(this);
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new AmphithereAIAttackMelee(this, 1.0D, true));
        this.goalSelector.add(2, new AmphithereAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.add(3, new AmphithereAIFleePlayer(this, 32.0F, 0.8D, 1.8D));
        this.goalSelector.add(3, new AIFlyWander());
        this.goalSelector.add(3, new AIFlyCircle());
        this.goalSelector.add(3, new AILandWander(this, 1.0D));
        this.goalSelector.add(4, new EntityAIWatchClosestIgnoreRider(this, LivingEntity.class, 6.0F));
        this.goalSelector.add(4, new AnimalMateGoal(this, 1.0D));
        this.targetSelector.add(1, new AttackWithOwnerGoal(this));
        this.targetSelector.add(2, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(3, new AmphithereAIHurtByTarget(this, false, new Class[0]));
        this.targetSelector.add(3, new AmphithereAITargetItems<>(this, false));
    }

    public boolean isStill() {
        return Math.abs(this.getVelocity().x) < 0.05 && Math.abs(this.getVelocity().z) < 0.05;
    }

    protected void switchNavigator(int navigatorType) {
        if (navigatorType == 0) {
            this.moveControl = new MoveControl(this);
            this.navigation = new SpiderNavigation(this, this.getWorld());
            this.navigatorType = 0;
        } else if (navigatorType == 1) {
            this.moveControl = new FlyMoveHelper(this);
            this.navigation = new PathNavigateFlyingCreature(this, this.getWorld());
            this.navigatorType = 1;
        } else {
            this.moveControl = new FlightMoveControl(this, 20, false);
            this.navigation = new PathNavigateFlyingCreature(this, this.getWorld());
            this.navigatorType = 2;
        }
    }

    public boolean onLeaves() {
        BlockState state = this.getWorld().getBlockState(this.getBlockPos().down());
        return state.getBlock() instanceof LeavesBlock;
    }

    @Override
    public boolean damage(DamageSource source, float damage) {
        if (!this.isTamed() && this.isFlying() && !this.isOnGround() && source.isIn(DamageTypeTags.IS_PROJECTILE) && !this.getWorld().isClient)
            this.isFallen = true;
        if (source.getAttacker() instanceof LivingEntity && source.getAttacker().isConnectedThroughVehicle(this) && this.isTamed() && this.isOwner((LivingEntity) source.getAttacker()))
            return false;
        return super.damage(source, damage);
    }

    @Override
    public void updatePassengerPosition(Entity passenger, PositionUpdater callback) {
        super.updatePassengerPosition(passenger, callback);
        if (this.hasPassenger(passenger) && this.isTamed()) {
            this.setBodyYaw(passenger.getYaw());
            this.setHeadYaw(passenger.getHeadYaw());
        }
        if (!this.getWorld().isClient && !this.isTamed() && passenger instanceof PlayerEntity && this.getAnimation() == NO_ANIMATION && this.random.nextInt(15) == 0)
            this.setAnimation(ANIMATION_BITE_RIDER);
        if (!this.getWorld().isClient && this.getAnimation() == ANIMATION_BITE_RIDER && this.getAnimationTick() == 6 && !this.isTamed())
            passenger.damage(this.getWorld().getDamageSources().mobAttack(this), 1);
        float pitch_forward = this.getPitch() > 0 && this.isFlying() ? (this.getPitch() / 45F) * 0.45F : 0;
        float scaled_ground = this.groundProgress * 0.1F;
        float radius = (this.isTamed() ? 0.5F : 0.3F) - scaled_ground * 0.5F + pitch_forward;
        float angle = (0.01745329251F * this.bodyYaw);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        passenger.setPosition(this.getX() + extraX, this.getY() + 0.7F - scaled_ground * 0.14F + pitch_forward, this.getZ() + extraZ);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(IafItemTags.BREED_AMPITHERE);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof PlayerEntity)
            this.setTarget(null);
        if (this.isTouchingWater() && this.jumping)
            this.setVelocity(this.getVelocity().x, this.getVelocity().y + 0.1D, this.getVelocity().z);
        if (this.isBaby() && this.getTarget() != null)
            this.setTarget(null);
        if (this.isInLove())
            this.setFlying(false);
        if (this.isSitting() && this.getTarget() != null)
            this.setTarget(null);
        boolean flapping = this.isFlappingWings();
        boolean flying = this.isFlying() && this.isOverAir() || (this.isOverAir() && !this.onLeaves());
        boolean diving = flying && this.getVelocity().y <= -0.1F || this.isFallen;
        boolean sitting = this.isSitting() && !this.isFlying();
        boolean notGrounded = flying || this.getAnimation() == ANIMATION_WING_BLAST;
        if (!this.getWorld().isClient) {
            if (this.isSitting() && (this.getCommand() != 1 || this.getControllingPassenger() != null))
                this.setSitting(false);
            if (!this.isSitting() && this.getCommand() == 1 && this.getControllingPassenger() == null)
                this.setSitting(true);
            if (this.isSitting())
                this.getNavigation().stop();
            //TODO
//                this.getMoveHelper().action = MovementController.Action.WAIT;
            if (flying) this.ticksFlying++;
            else this.ticksFlying = 0;
        }
        if (this.isFlying() && this.isOnGround())
            this.setFlying(false);
        if (sitting && this.sitProgress < 20.0F)
            this.sitProgress += 0.5F;
        else if (!sitting && this.sitProgress > 0.0F)
            this.sitProgress -= 0.5F;
        if (this.flightCooldown > 0) this.flightCooldown--;
        if (!this.getWorld().isClient) {
            if (this.flightBehavior == FlightBehavior.CIRCLE)
                this.ticksCircling++;
            else
                this.ticksCircling = 0;
        }
        if (this.getUntamedRider() != null && !this.isTamed())
            this.ridingTime++;
        if (this.getUntamedRider() == null)
            this.ridingTime = 0;
        if (!this.isTamed()) {
            if (this.ridingTime > IafCommonConfig.INSTANCE.amphithere.tameTime.getValue() && this.getUntamedRider() != null && this.getUntamedRider() instanceof PlayerEntity) {
                this.getWorld().sendEntityStatus(this, (byte) 45);
                this.setOwner((PlayerEntity) this.getUntamedRider());
                if (this.getTarget() == this.getUntamedRider())
                    this.setTarget(null);
            }
        }
        if (this.isStill())
            this.ticksStill++;
        else
            this.ticksStill = 0;
        if (!this.isFlying() && !this.isBaby() && ((this.isOnGround() && this.random.nextInt(200) == 0 && this.flightCooldown == 0 && this.getPassengerList().isEmpty() && !this.isAiDisabled() && this.canMove()) || this.getY() < -1)) {
            this.setVelocity(this.getVelocity().x, this.getVelocity().y + 0.5D, this.getVelocity().z);
            this.setFlying(true);
        }
        if (this.getControllingPassenger() != null && this.isFlying() && !this.isOnGround()) {
            if (this.getControllingPassenger().getPitch() > 25 && this.getVelocity().y > -1.0F)
                this.setVelocity(this.getVelocity().x, this.getVelocity().y - 0.1D, this.getVelocity().z);
            if (this.getControllingPassenger().getPitch() < -25 && this.getVelocity().y < 1.0F)
                this.setVelocity(this.getVelocity().x, this.getVelocity().y + 0.1D, this.getVelocity().z);
        }
        if (notGrounded && this.groundProgress > 0.0F)
            this.groundProgress -= 2F;
        else if (!notGrounded && this.groundProgress < 20.0F)
            this.groundProgress += 2F;
        if (diving && this.diveProgress < 20.0F)
            this.diveProgress += 1F;
        else if (!diving && this.diveProgress > 0.0F)
            this.diveProgress -= 1F;

        if (this.isFallen && this.flightBehavior != FlightBehavior.NONE)
            this.flightBehavior = FlightBehavior.NONE;
        if (this.flightBehavior == FlightBehavior.NONE && this.getControllingPassenger() == null && this.isFlying())
            this.setVelocity(this.getVelocity().x, this.getVelocity().y - 0.3D, this.getVelocity().z);
        if (this.isFlying() && !this.isOnGround() && this.isFallen && this.getControllingPassenger() == null) {
            this.setVelocity(this.getVelocity().x, this.getVelocity().y - 0.2D, this.getVelocity().z);
            this.setPitch(Math.max(this.getPitch() + 5, 75));
        }
        if (this.isFallen && this.isOnGround()) {
            this.setFlying(false);
            this.flightCooldown = this.isTamed() ? 50 : 12000;
            this.isFallen = false;
        }
        if (flying && this.isOverAir()) {
            if (this.getRidingPlayer() == null && this.navigatorType != 1)
                this.switchNavigator(1);
            if (this.getRidingPlayer() != null && this.navigatorType != 2)
                this.switchNavigator(2);
        }
        if (!flying && this.navigatorType != 0)
            this.switchNavigator(0);
        if ((this.hasHomePosition || this.getCommand() == 2) && this.flightBehavior == FlightBehavior.WANDER)
            this.flightBehavior = FlightBehavior.CIRCLE;
        if (flapping && this.flapProgress < 10.0F)
            this.flapProgress += 1F;
        else if (!flapping && this.flapProgress > 0.0F)
            this.flapProgress -= 1F;
        if (this.flapTicks > 0)
            this.flapTicks--;
        if (this.getWorld().isClient) {
            if (!this.isOnGround()) {
                if (this.hasPassengers())
                    this.roll_buffer.calculateChainFlapBufferHead(40, 1, 2F, 0.5F, this);
                else {
                    this.bodyYaw = this.getYaw();
                    this.roll_buffer.calculateChainFlapBuffer(70, 1, 2F, 0.5F, this);
                }
                this.pitch_buffer.calculateChainPitchBuffer(90, 10, 10F, 0.5F, this);
            }
            this.tail_buffer.calculateChainSwingBuffer(70, 20, 5F, this);
        }
        if (this.changedFlightBehavior)
            this.changedFlightBehavior = false;
        if (!flapping && (this.getVelocity().y > 0.15F || this.getVelocity().y > 0 && this.age % 200 == 0) && this.isOverAir())
            this.flapWings();
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public boolean isFlappingWings() {
        return this.flapTicks > 0;
    }

    public int getCommand() {
        return this.dataTracker.get(COMMAND);
    }

    public void setCommand(int command) {
        this.dataTracker.set(COMMAND, command);
        this.setSitting(command == 1);
    }

    @Override
    public void flapWings() {
        this.flapTicks = 20;
    }

    @Override
    public boolean isSitting() {
        if (this.getWorld().isClient) {
            boolean isSitting = (this.dataTracker.get(TAMEABLE_FLAGS) & 1) != 0;
            this.isSitting = isSitting;
            return isSitting;
        }
        return this.isSitting;
    }

    @Override
    public void setSitting(boolean sitting) {
        if (!this.getWorld().isClient) this.isSitting = sitting;
        byte b0 = this.dataTracker.get(TAMEABLE_FLAGS);
        if (sitting) this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b0 | 1));
        else this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b0 & -2));
    }

    @Override
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengerList())
            if (passenger instanceof PlayerEntity player && this.getTarget() != passenger)
                if (this.isTamed() && this.getOwnerUuid() != null && this.getOwnerUuid().equals(player.getUuid()))
                    return player;
        return null;
    }

    public Entity getUntamedRider() {
        for (Entity passenger : this.getPassengerList())
            if (passenger instanceof PlayerEntity)
                return passenger;
        return null;
    }

    @Override
    public boolean isTeammate(Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) return true;
            if (entityIn instanceof TameableEntity tameable)
                return tameable.isOwner(livingentity);
            if (livingentity != null)
                return livingentity.isTeammate(entityIn);
        }
        return super.isTeammate(entityIn);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(IafCommonConfig.INSTANCE.amphithere.maxHealth.getValue());
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(IafCommonConfig.INSTANCE.amphithere.attackDamage.getValue());
        this.getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED).setBaseValue(IafCommonConfig.INSTANCE.amphithere.flightSpeed.getValue());
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(FLYING, false);
        this.dataTracker.startTracking(FLAP_TICKS, 0);
        this.dataTracker.startTracking(CONTROL_STATE, (byte) 0);
        this.dataTracker.startTracking(COMMAND, 0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putBoolean("Flying", this.isFlying());
        compound.putInt("FlightCooldown", this.flightCooldown);
        compound.putInt("RidingTime", this.ridingTime);
        compound.putBoolean("HasHomePosition", this.hasHomePosition);
        if (this.homePos != null && this.hasHomePosition) {
            compound.putInt("HomeAreaX", this.homePos.getX());
            compound.putInt("HomeAreaY", this.homePos.getY());
            compound.putInt("HomeAreaZ", this.homePos.getZ());
        }
        compound.putInt("Command", this.getCommand());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setFlying(compound.getBoolean("Flying"));
        this.flightCooldown = compound.getInt("FlightCooldown");
        this.ridingTime = compound.getInt("RidingTime");
        this.hasHomePosition = compound.getBoolean("HasHomePosition");
        if (this.hasHomePosition && compound.getInt("HomeAreaX") != 0 && compound.getInt("HomeAreaY") != 0 && compound.getInt("HomeAreaZ") != 0)
            this.homePos = new BlockPos(compound.getInt("HomeAreaX"), compound.getInt("HomeAreaY"), compound.getInt("HomeAreaZ"));
        this.setCommand(compound.getInt("Command"));
        this.setConfigurableAttributes();
    }

    //TODO: Create entity placements
    public boolean getCanSpawnHere() {
        int i = MathHelper.floor(this.getX());
        int j = MathHelper.floor(this.getBoundingBox().minY);
        int k = MathHelper.floor(this.getZ());
        BlockPos blockpos = new BlockPos(i, j, k);
        Block block = this.getWorld().getBlockState(blockpos.down()).getBlock();
        return this.getWorld().isSkyVisibleAllowingSea(blockpos.up());
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = this.getTarget();
        if (target != null && this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 7) {
            double dist = this.squaredDistanceTo(target);
            if (dist < 10) {
                target.takeKnockback(0.6F, MathHelper.sin(this.getYaw() * 0.017453292F), -MathHelper.cos(this.getYaw() * 0.017453292F));
                target.damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getAnimation() == ANIMATION_WING_BLAST && this.getAnimationTick() == 5)
            this.playSound(IafSounds.AMPHITHERE_GUST.get(), 1, 1);
        if ((this.getAnimation() == ANIMATION_BITE || this.getAnimation() == ANIMATION_BITE_RIDER) && this.getAnimationTick() == 1)
            this.playSound(IafSounds.AMPHITHERE_BITE.get(), 1, 1);
        if (target != null && this.getAnimation() == ANIMATION_WING_BLAST && this.getAnimationTick() > 5 && this.getAnimationTick() < 22) {
            double dist = this.squaredDistanceTo(target);
            if (dist < 25) {
                target.damage(this.getWorld().getDamageSources().mobAttack(this), ((float) (int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue() / 2));
                target.velocityDirty = true;
                if (!(this.random.nextDouble() < this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).getValue())) {
                    this.velocityDirty = true;
                    double d1 = target.getX() - this.getX();

                    double d0;
                    for (d0 = target.getZ() - this.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
                        d1 = (Math.random() - Math.random()) * 0.01D;
                    Vec3d Vector3d = this.getVelocity();
                    Vec3d Vector3d1 = new Vec3d(d0, 0.0D, d1).normalize().multiply(0.5);
                    this.setVelocity(Vector3d.x / 2.0D - Vector3d1.x, this.isOnGround() ? Math.min(0.4D, Vector3d.y / 2.0D + 0.5) : Vector3d.y, Vector3d.z / 2.0D - Vector3d1.z);
                }
            }
        }
        if (this.getAnimation() == ANIMATION_TAIL_WHIP && target != null && this.getAnimationTick() == 7) {
            double dist = this.squaredDistanceTo(target);
            if (dist < 10) {
                target.damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
                target.velocityDirty = true;
                float f = MathHelper.sqrt((float) (0.5 * 0.5 + 0.5 * 0.5));
                double d0;
                double d1 = target.getX() - this.getX();
                for (d0 = target.getZ() - this.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
                    d1 = (Math.random() - Math.random()) * 0.01D;
                Vec3d Vector3d = this.getVelocity();
                Vec3d Vector3d1 = new Vec3d(d0, 0.0D, d1).normalize().multiply(0.5);
                this.setVelocity(Vector3d.x / 2.0D - Vector3d1.x, this.isOnGround() ? Math.min(0.4D, Vector3d.y / 2.0D + 0.5) : Vector3d.y, Vector3d.z / 2.0D - Vector3d1.z);

            }
        }
        if (this.isGoingUp() && !this.getWorld().isClient)
            if (!this.isFlying()) {
                this.setVelocity(this.getVelocity().add(0, 1, 0));
                this.setFlying(true);
            }
        if (!this.isOverAir() && this.isFlying() && this.ticksFlying > 25)
            this.setFlying(false);
        if (this.dismountIAF() && this.isFlying() && this.isOnGround())
            this.setFlying(false);
        if (this.getUntamedRider() != null && this.getUntamedRider().isSneaking()) {
            if (this.getUntamedRider() instanceof LivingEntity rider) {
                IafEntityData data = IafEntityData.get(rider);
                data.miscData.setDismounted(true);
            }
            this.getUntamedRider().stopRiding();
        }
        if (this.attack() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity) {
            LivingEntity riderTarget = DragonUtils.riderLookingAtEntity(this, this.getControllingPassenger(), 2.5D);
            if (this.getAnimation() != ANIMATION_BITE)
                this.setAnimation(ANIMATION_BITE);
            if (riderTarget != null)
                riderTarget.damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
        }
        if (target != null && this.isOwner(target))
            this.setTarget(null);
        if (target != null && this.isOnGround() && this.isFlying() && this.ticksFlying > 40)
            this.setFlying(false);
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
        if (this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_TAIL_WHIP && this.getAnimation() != ANIMATION_WING_BLAST && this.getControllingPassenger() == null) {
            if (this.random.nextBoolean()) this.setAnimation(ANIMATION_BITE);
            else
                this.setAnimation(this.getRandom().nextBoolean() || this.isFlying() ? ANIMATION_WING_BLAST : ANIMATION_TAIL_WHIP);
            return true;
        }
        return false;
    }

    @Override
    public PlayerEntity getRidingPlayer() {
        if (this.getControllingPassenger() instanceof PlayerEntity player)
            return player;
        return null;
    }

    @Override
    public boolean isFlying() {
        if (this.getWorld().isClient)
            return this.isFlying = this.dataTracker.get(FLYING);
        return this.isFlying;
    }

    public void setFlying(boolean flying) {
        this.dataTracker.set(FLYING, flying);
        if (!this.getWorld().isClient)
            this.isFlying = flying;
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    @Override
    public boolean isGoingUp() {
        return (this.dataTracker.get(CONTROL_STATE) & 1) == 1;
    }

    @Override
    public boolean isGoingDown() {
        return (this.dataTracker.get(CONTROL_STATE) >> 1 & 1) == 1;
    }

    public boolean attack() {
        return (this.dataTracker.get(CONTROL_STATE) >> 2 & 1) == 1;
    }

    public boolean dismountIAF() {
        return (this.dataTracker.get(CONTROL_STATE) >> 3 & 1) == 1;
    }

    @Override
    public void up(boolean up) {
        this.setStateField(0, up);
    }

    @Override
    public void down(boolean down) {
        this.setStateField(1, down);
    }

    @Override
    public void attack(boolean attack) {
        this.setStateField(2, attack);
    }

    @Override
    public void strike(boolean strike) {

    }

    @Override
    public void dismount(boolean dismount) {
        this.setStateField(3, dismount);
    }

    private void setStateField(int i, boolean newState) {
        byte prevState = this.dataTracker.get(CONTROL_STATE);
        if (newState) this.dataTracker.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        else this.dataTracker.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
    }

    @Override
    public byte getControlState() {
        return this.dataTracker.get(CONTROL_STATE);
    }

    @Override
    public void setControlState(byte state) {
        this.dataTracker.set(CONTROL_STATE, state);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return IafSounds.AMPHITHERE_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSounds.AMPHITHERE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return IafSounds.AMPHITHERE_DIE.get();
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
        return new Animation[]{ANIMATION_BITE, ANIMATION_BITE_RIDER, ANIMATION_WING_BLAST, ANIMATION_TAIL_WHIP, ANIMATION_SPEAK};
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

    public boolean isBlinking() {
        return this.age % 50 > 40;
    }

    @Override
    public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity ageableEntity) {
        EntityAmphithere amphithere = new EntityAmphithere(IafEntities.AMPHITHERE.get(), this.getWorld());
        amphithere.setVariant(this.getVariant());
        return amphithere;
    }

    @Override
    public int getXpToDrop() {
        return 10;
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        spawnDataIn = super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVariant(this.getRandom().nextInt(5));
        return spawnDataIn;
    }

    @Override
    public boolean canPhaseThroughBlock(WorldAccess world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof LeavesBlock;
    }

    // FIXME: I don't know what's is overriding the flight speed (I assume it's on the server side)
    @Override
    protected float getSaddledSpeed(PlayerEntity pPlayer) {
        return (this.isFlying() || this.isHovering()) ? (float) this.getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED) * 2F : (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.5F;
    }

    @Override
    public void travel(Vec3d travelVector) {
        if (this.isLogicalSideForUpdatingMovement()) {
            if (this.isTouchingWater()) {
                this.updateVelocity(0.02F, travelVector);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.8F));
            } else if (this.isInLava()) {
                this.updateVelocity(0.02F, travelVector);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5D));
            } else if (this.isFlying() || this.isHovering()) {
                this.updateVelocity(0.1F, travelVector);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.9D));
            } else super.travel(travelVector);
        } else super.travel(travelVector);

    }

    @Override
    protected void tickControlled(PlayerEntity player, Vec3d travelVector) {
        super.tickControlled(player, travelVector);
        Vec2f vec2 = this.getRiddenRotation(player);
        this.setRotation(vec2.y, vec2.x);
        this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();
        if (this.isLogicalSideForUpdatingMovement()) {
            Vec3d vec3 = this.getVelocity();
            float vertical = this.isGoingUp() ? 0.2F : this.isGoingDown() ? -0.2F : 0F;
            if (!this.isFlying() && !this.isHovering())
                vertical = (float) travelVector.y;
            this.setVelocity(vec3.add(0, vertical, 0));
        }
    }

    @Override
    protected Vec3d getControlledMovementInput(PlayerEntity player, Vec3d travelVector) {
        float f = player.sidewaysSpeed * 0.5F;
        float f1 = player.forwardSpeed;
        if (f1 <= 0.0F) f1 *= 0.25F;
        return new Vec3d(f, 0.0D, f1);
    }

    protected Vec2f getRiddenRotation(LivingEntity entity) {
        return new Vec2f(entity.getPitch() * 0.5F, entity.getYaw());
    }

    public boolean canMove() {
        return this.getControllingPassenger() == null && this.sitProgress == 0 && !this.isSitting();
    }

    @Override
    public void handleStatus(byte id) {
        if (id == 45) this.playEffect();
        else super.handleStatus(id);
    }

    protected void playEffect() {
        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.getWorld().addParticle(ParticleTypes.HEART, this.getX() + this.random.nextFloat() * this.getWidth() * 2.0F - this.getWidth(), this.getY() + 0.5D + (this.random.nextFloat() * this.getHeight()), this.getZ() + this.random.nextFloat() * this.getWidth() * 2.0F - this.getWidth(), d0, d1, d2);
        }
    }

    @Override
    public void onHearFlute(PlayerEntity player) {
        if (!this.isOnGround() && this.isTamed())
            this.isFallen = true;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public double getFlightSpeedModifier() {
        return 0.555D;
    }

    @Override
    public boolean fliesLikeElytra() {
        return !this.isOnGround();
    }

    private boolean isOverAir() {
        return this.getWorld().isAir(this.getBlockPos().down());
    }

    public boolean canBlockPosBeSeen(BlockPos pos) {
        Vec3d Vector3d = new Vec3d(this.getX(), this.getEyeY(), this.getZ());
        Vec3d Vector3d1 = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        return this.getWorld().raycast(new RaycastContext(Vector3d, Vector3d1, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    public enum FlightBehavior {
        CIRCLE,
        WANDER,
        NONE
    }

    static class AILandWander extends WanderAroundFarGoal {
        public AILandWander(PathAwareEntity creature, double speed) {
            super(creature, speed, 10);
        }

        @Override
        public boolean canStart() {
            return this.mob.isOnGround() && super.canStart() && ((EntityAmphithere) this.mob).canMove();
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }
    }

    class AIFlyWander extends Goal {
        BlockPos target;

        public AIFlyWander() {
        }

        @Override
        public boolean canStart() {
            if (EntityAmphithere.this.flightBehavior != FlightBehavior.WANDER || !EntityAmphithere.this.canMove())
                return false;
            if (EntityAmphithere.this.isFlying()) {
                this.target = EntityAmphithere.getPositionRelativetoGround(EntityAmphithere.this, EntityAmphithere.this.getWorld(), EntityAmphithere.this.getBlockX() + EntityAmphithere.this.random.nextInt(30) - 15, EntityAmphithere.this.getBlockZ() + EntityAmphithere.this.random.nextInt(30) - 15, EntityAmphithere.this.random);
                EntityAmphithere.this.orbitPos = null;
                return (!EntityAmphithere.this.getMoveControl().isMoving() || EntityAmphithere.this.ticksStill >= 50);
            } else return false;
        }

        protected boolean isDirectPathBetweenPoints(Entity e) {
            return EntityAmphithere.this.canBlockPosBeSeen(this.target);
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void tick() {
            if (!this.isDirectPathBetweenPoints(EntityAmphithere.this))
                this.target = EntityAmphithere.getPositionRelativetoGround(EntityAmphithere.this, EntityAmphithere.this.getWorld(), EntityAmphithere.this.getBlockX() + EntityAmphithere.this.random.nextInt(30) - 15, EntityAmphithere.this.getBlockZ() + EntityAmphithere.this.random.nextInt(30) - 15, EntityAmphithere.this.random);
            if (EntityAmphithere.this.getWorld().isAir(this.target)) {
                EntityAmphithere.this.moveControl.moveTo(this.target.getX() + 0.5D, this.target.getY() + 0.5D, this.target.getZ() + 0.5D, 0.25D);
                if (EntityAmphithere.this.getTarget() == null)
                    EntityAmphithere.this.getLookControl().lookAt(this.target.getX() + 0.5D, this.target.getY() + 0.5D, this.target.getZ() + 0.5D, 180.0F, 20.0F);
            }
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }
    }

    class AIFlyCircle extends Goal {
        BlockPos target;

        public AIFlyCircle() {
        }

        @Override
        public boolean canStart() {
            if (EntityAmphithere.this.flightBehavior != FlightBehavior.CIRCLE || !EntityAmphithere.this.canMove())
                return false;
            if (EntityAmphithere.this.isFlying()) {
                EntityAmphithere.this.orbitPos = EntityAmphithere.getPositionRelativetoGround(EntityAmphithere.this, EntityAmphithere.this.getWorld(), EntityAmphithere.this.getBlockX() + EntityAmphithere.this.random.nextInt(30) - 15, EntityAmphithere.this.getBlockZ() + EntityAmphithere.this.random.nextInt(30) - 15, EntityAmphithere.this.random);
                this.target = EntityAmphithere.getPositionInOrbit(EntityAmphithere.this, EntityAmphithere.this.getWorld(), EntityAmphithere.this.orbitPos, EntityAmphithere.this.random);
                return true;
            } else return false;
        }

        protected boolean isDirectPathBetweenPoints() {
            return EntityAmphithere.this.canBlockPosBeSeen(this.target);
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void tick() {
            if (!this.isDirectPathBetweenPoints())
                this.target = EntityAmphithere.getPositionInOrbit(EntityAmphithere.this, EntityAmphithere.this.getWorld(), EntityAmphithere.this.orbitPos, EntityAmphithere.this.random);

            if (EntityAmphithere.this.getWorld().isAir(this.target)) {
                EntityAmphithere.this.moveControl.moveTo(this.target.getX() + 0.5D, this.target.getY() + 0.5D, this.target.getZ() + 0.5D, 0.25D);
                if (EntityAmphithere.this.getTarget() == null)
                    EntityAmphithere.this.getLookControl().lookAt(this.target.getX() + 0.5D, this.target.getY() + 0.5D, this.target.getZ() + 0.5D, 180.0F, 20.0F);
            }
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }
    }

    class FlyMoveHelper extends MoveControl {
        public FlyMoveHelper(EntityAmphithere entity) {
            super(entity);
            this.speed = 1.75F;
        }

        @Override
        public void tick() {
            if (!EntityAmphithere.this.canMove())
                return;
            if (EntityAmphithere.this.horizontalCollision) {
                EntityAmphithere.this.setYaw(EntityAmphithere.this.getYaw() + 180.0F);
                this.speed = 0.1F;
                BlockPos target = EntityAmphithere.getPositionRelativetoGround(EntityAmphithere.this, EntityAmphithere.this.getWorld(), EntityAmphithere.this.getBlockX() + EntityAmphithere.this.random.nextInt(15) - 7, EntityAmphithere.this.getBlockZ() + EntityAmphithere.this.random.nextInt(15) - 7, EntityAmphithere.this.random);
                this.targetX = target.getX();
                this.targetY = target.getY();
                this.targetZ = target.getZ();
            }
            if (this.state == State.MOVE_TO) {
                double d0 = this.targetX - EntityAmphithere.this.getX();
                double d1 = this.targetY - EntityAmphithere.this.getY();
                double d2 = this.targetZ - EntityAmphithere.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = MathHelper.sqrt((float) d3);
                if (d3 < 6 && EntityAmphithere.this.getTarget() == null) {
                    if (!EntityAmphithere.this.changedFlightBehavior && EntityAmphithere.this.flightBehavior == FlightBehavior.WANDER && EntityAmphithere.this.random.nextInt(30) == 0) {
                        EntityAmphithere.this.flightBehavior = FlightBehavior.CIRCLE;
                        EntityAmphithere.this.changedFlightBehavior = true;
                    }
                    if (!EntityAmphithere.this.changedFlightBehavior && EntityAmphithere.this.flightBehavior == FlightBehavior.CIRCLE && EntityAmphithere.this.random.nextInt(5) == 0 && EntityAmphithere.this.ticksCircling > 150) {
                        EntityAmphithere.this.flightBehavior = FlightBehavior.WANDER;
                        EntityAmphithere.this.changedFlightBehavior = true;
                    }
                    if (EntityAmphithere.this.hasHomePosition && EntityAmphithere.this.flightBehavior != FlightBehavior.NONE || EntityAmphithere.this.getCommand() == 2)
                        EntityAmphithere.this.flightBehavior = FlightBehavior.CIRCLE;
                }
                if (d3 < 1 && EntityAmphithere.this.getTarget() == null) {
                    this.state = State.WAIT;
                    EntityAmphithere.this.setVelocity(EntityAmphithere.this.getVelocity().multiply(0.5D, 0.5D, 0.5D));
                } else {
                    EntityAmphithere.this.setVelocity(EntityAmphithere.this.getVelocity().add(d0 / d3 * 0.5D * this.speed, d1 / d3 * 0.5D * this.speed, d2 / d3 * 0.5D * this.speed));
                    float f1 = (float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
                    EntityAmphithere.this.setPitch(f1);
                    if (EntityAmphithere.this.getTarget() == null)
                        EntityAmphithere.this.setYaw(-((float) MathHelper.atan2(EntityAmphithere.this.getVelocity().x, EntityAmphithere.this.getVelocity().z)) * (180F / (float) Math.PI));
                    else {
                        double d4 = EntityAmphithere.this.getTarget().getX() - EntityAmphithere.this.getX();
                        double d5 = EntityAmphithere.this.getTarget().getZ() - EntityAmphithere.this.getZ();
                        EntityAmphithere.this.setYaw(-((float) MathHelper.atan2(d4, d5)) * (180F / (float) Math.PI));
                    }
                    EntityAmphithere.this.bodyYaw = EntityAmphithere.this.getYaw();
                }
            }
        }
    }
}
