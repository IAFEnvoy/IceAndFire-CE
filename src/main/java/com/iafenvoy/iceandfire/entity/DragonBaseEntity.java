package com.iafenvoy.iceandfire.entity;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.data.component.ChainData;
import com.iafenvoy.iceandfire.entity.ai.*;
import com.iafenvoy.iceandfire.entity.util.*;
import com.iafenvoy.iceandfire.entity.util.dragon.*;
import com.iafenvoy.iceandfire.event.IafEvents;
import com.iafenvoy.iceandfire.item.DragonArmorItem;
import com.iafenvoy.iceandfire.item.SummoningCrystalItem;
import com.iafenvoy.iceandfire.item.block.entity.DragonForgeInputBlockEntity;
import com.iafenvoy.iceandfire.item.block.util.DragonProof;
import com.iafenvoy.iceandfire.item.component.DragonSkullComponent;
import com.iafenvoy.iceandfire.network.payload.DragonSetBurnBlockS2CPayload;
import com.iafenvoy.iceandfire.network.payload.StartRidingMobPayload;
import com.iafenvoy.iceandfire.registry.*;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.iafenvoy.iceandfire.render.model.IFChainBuffer;
import com.iafenvoy.iceandfire.render.model.util.LegSolverQuadruped;
import com.iafenvoy.iceandfire.screen.handler.DragonScreenHandler;
import com.iafenvoy.iceandfire.world.DragonPosWorldData;
import com.iafenvoy.integration.IntegrationExecutor;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.object.EntityUtil;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.AdvancedPathNavigate;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.IPassabilityNavigator;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.PathingStuckHandler;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.pathjobs.ICustomSizeNavigator;
import com.iafenvoy.uranus.object.item.FoodUtils;
import com.iafenvoy.uranus.util.RandomHelper;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class DragonBaseEntity extends TamableAnimal implements MenuProvider, IPassabilityNavigator, ISyncMount, IFlyingMount, IMultipartEntity, IAnimatedEntity, IDragonFlute, IDeadMob, IVillagerFear, IAnimalFear, IHasCustomizableAttributes, ICustomSizeNavigator, ICustomMoveController, ContainerListener {
    public static final int FLIGHT_CHANCE_PER_TICK = 1500;
    private static final ResourceLocation ARMOR_MODIFIER = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "armor_modifier");
    private static final EntityDataAccessor<Integer> HUNGER = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> AGE_TICKS = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> GENDER = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FIREBREATHING = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HOVERING = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MODEL_DEAD = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DEATH_STAGE = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> CONTROL_STATE = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> TACKLE = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AGINGDISABLED = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DRAGON_PITCH = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> CRYSTAL_BOUND = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> CUSTOM_POSE = SynchedEntityData.defineId(DragonBaseEntity.class, EntityDataSerializers.STRING);
    public static Animation ANIMATION_FIRECHARGE;
    public static Animation ANIMATION_EAT;
    public static Animation ANIMATION_SPEAK;
    public static Animation ANIMATION_BITE;
    public static Animation ANIMATION_SHAKEPREY;
    public static Animation ANIMATION_WINGBLAST;
    public static Animation ANIMATION_ROAR;
    public static Animation ANIMATION_EPIC_ROAR;
    public static Animation ANIMATION_TAILWHACK;
    public final DragonType dragonType;
    public final double minimumDamage;
    public final double maximumDamage;
    public final double minimumHealth;
    public final double maximumHealth;
    public final double minimumSpeed;
    public final double maximumSpeed;
    public final double minimumArmor;
    public final double maximumArmor;
    public final float[] prevAnimationProgresses = new float[10];
    public final LegSolverQuadruped legSolver;
    public final IafDragonLogic logic;
    public final IafDragonFlightManager flightManager;
    public final boolean allowLocalMotionControl = true;
    public final boolean allowMousePitchControl = true;
    public float sitProgress;
    public float sleepProgress;
    public float hoverProgress;
    public float flyProgress;
    public float fireBreathProgress;
    public float diveProgress;
    public float prevDiveProgress;
    public float prevFireBreathProgress;
    public int fireStopTicks;
    public int flyTicks;
    public float modelDeadProgress;
    public float prevModelDeadProgress;
    public float ridingProgress;
    public float tackleProgress;
    /*
    0 = sit
    1 = sleep
    2 = hover
    3 = fly
    4 = fireBreath
    5 = riding
    6 = tackle
     */
    public boolean isSwimming;
    public float prevSwimProgress;
    public float swimProgress;
    public int ticksSwiming;
    public int swimCycle;
    public boolean isDaytime;
    public int flightCycle;
    public HomePosition homePos;
    public boolean hasHomePosition = false;
    public IFChainBuffer roll_buffer;
    public IFChainBuffer pitch_buffer;
    public IFChainBuffer pitch_buffer_body;
    public ReversedBuffer turn_buffer;
    public ChainBuffer tail_buffer;
    public int spacebarTicks;
    public int walkCycle;
    public BlockPos burningTarget;
    public int burnProgress;
    public double burnParticleX;
    public double burnParticleY;
    public double burnParticleZ;
    public float prevDragonPitch;
    public IafDragonAttacks.Air airAttack;
    public IafDragonAttacks.Ground groundAttack;
    public boolean usingGroundAttack = true;
    public int hoverTicks;
    public int tacklingTicks;
    public int ticksStill;
    /*
        0 = ground/walking
        1 = ai flight
        2 = controlled flight
     */
    public int navigatorType;
    public SimpleContainer dragonInventory;
    public boolean lookingForRoostAIFlag = false;
    public int flyHovering;
    public boolean hasHadHornUse = false;
    public int blockBreakCounter;
    public int fireBreathTicks;
    protected boolean gliding = false;
    protected float glidingSpeedBonus = 0;
    // For slowly raise rider position
    protected float riderWalkingExtraY = 0;
    private int prevFlightCycle;
    private boolean isModelDead;
    private int animationTick;
    private Animation currentAnimation;
    private float lastScale;
    private DragonPartEntity headPart;
    private DragonPartEntity neckPart;
    private DragonPartEntity rightWingUpperPart;
    private DragonPartEntity rightWingLowerPart;
    private DragonPartEntity leftWingUpperPart;
    private DragonPartEntity leftWingLowerPart;
    private DragonPartEntity tail1Part;
    private DragonPartEntity tail2Part;
    private DragonPartEntity tail3Part;
    private DragonPartEntity tail4Part;
    private boolean isOverAir;
    private int brushedTime;

    public DragonBaseEntity(EntityType<? extends DragonBaseEntity> t, Level world, DragonType type, double minimumDamage, double maximumDamage, double minimumHealth, double maximumHealth, double minimumSpeed, double maximumSpeed) {
        super(t, world);
        this.dragonType = type;
        this.minimumDamage = minimumDamage;
        this.maximumDamage = maximumDamage;
        this.minimumHealth = minimumHealth;
        this.maximumHealth = maximumHealth;
        this.minimumSpeed = minimumSpeed;
        this.maximumSpeed = maximumSpeed;
        this.minimumArmor = 1D;
        this.maximumArmor = 20D;
        ANIMATION_EAT = Animation.create(20);
        this.createInventory();
        if (world.isClientSide) {
            this.roll_buffer = new IFChainBuffer();
            this.pitch_buffer = new IFChainBuffer();
            this.pitch_buffer_body = new IFChainBuffer();
            this.turn_buffer = new ReversedBuffer();
            this.tail_buffer = new ChainBuffer();
        }
        this.legSolver = new LegSolverQuadruped(0.3F, 0.35F, 0.2F, 1.45F, 1.0F);
        this.flightManager = new IafDragonFlightManager(this);
        this.logic = this.createDragonLogic();
        this.noCulling = true;
        this.switchNavigator(0);
        this.randomizeAttacks();
        this.lastScale = 0;//Ensure scale will be updated so that multipart can generate correctly
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, 20.0D)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, 1)
                //FOLLOW RANGE
                .add(Attributes.FOLLOW_RANGE, Math.min(2048, IafCommonConfig.INSTANCE.dragon.targetSearchLength.getValue()))
                //ARMOR
                .add(Attributes.ARMOR, 4);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(Math.min(2048, IafCommonConfig.INSTANCE.dragon.targetSearchLength.getValue()));
    }

    @Override
    public @NotNull BlockPos getRestrictCenter() {
        return this.homePos == null ? super.getRestrictCenter() : this.homePos.getPosition();
    }

    @Override
    public float getRestrictRadius() {
        return IafCommonConfig.INSTANCE.dragon.wanderFromHomeDistance.getValue();
    }

    public String getHomeDimensionName() {
        return this.homePos == null ? "" : this.homePos.getDimension();
    }

    @Override
    public boolean hasRestriction() {
        return this.hasHomePosition &&
                this.getHomeDimensionName().equals(DragonUtils.getDimensionName(this.level()))
                || super.hasRestriction();
    }

    @Override
    protected void registerGoals() {
//        this.goalSelector.addGoal(0, new DragonAIRide<>(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new DragonAIMateGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new DragonAIReturnToRoostGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new DragonAIEscortGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new DragonAIAttackMeleeGoal(this, 1.5D, false));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.0D, Ingredient.of(IafItemTags.TEMPT_DRAGON), false));
        this.goalSelector.addGoal(7, new DragonAIWanderGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new DragonAIWatchClosestGoal(this, LivingEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new DragonAILookIdleGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new DragonAITargetItemsGoal(this, 60, false, false, true));
        this.targetSelector.addGoal(5, new DragonAITargetNonTamedGoal<>(this, LivingEntity.class, false, (Predicate<LivingEntity>) entity -> {
            if (entity instanceof Player player)
                return !player.isCreative() && !IafCommonConfig.INSTANCE.dragon.neutralToPlayer.getValue();
            if (this.getRandom().nextInt(100) > this.getHunger())
                return entity.getType() != this.getType() && DragonUtils.canHostilesTarget(entity) && DragonUtils.isAlive(entity) && this.shouldTarget(entity);
            return false;
        }));
        this.targetSelector.addGoal(6, new DragonAITargetGoal<>(this, LivingEntity.class, true, (Predicate<LivingEntity>) entity -> entity instanceof Player ? !IafCommonConfig.INSTANCE.dragon.neutralToPlayer.getValue() : DragonUtils.canHostilesTarget(entity) && entity.getType() != this.getType() && this.shouldTarget(entity) && DragonUtils.isAlive(entity)));
        this.targetSelector.addGoal(7, new DragonAITargetItemsGoal(this, false));
    }

    protected abstract boolean shouldTarget(Entity entity);

    public void updateScale(float scale) {
        if (this.headPart == null || this.headPart.isRemoved()) {
            this.headPart = new DragonPartEntity(this, 1.55F, 0, 0.6F, 0.5F, 0.35F, 1.5F);
            this.level().addFreshEntity(this.headPart);
        }
        this.headPart.updateScale(scale);

        if (this.neckPart == null || this.neckPart.isRemoved()) {
            this.neckPart = new DragonPartEntity(this, 0.85F, 0, 0.7F, 0.5F, 0.2F, 1);
            this.level().addFreshEntity(this.neckPart);
        }
        this.neckPart.updateScale(scale);

        if (this.rightWingUpperPart == null || this.rightWingUpperPart.isRemoved()) {
            this.rightWingUpperPart = new DragonPartEntity(this, 1, 90, 0.5F, 0.85F, 0.3F, 0.5F);
            this.level().addFreshEntity(this.rightWingUpperPart);
        }
        this.rightWingUpperPart.updateScale(scale);

        if (this.rightWingLowerPart == null || this.rightWingLowerPart.isRemoved()) {
            this.rightWingLowerPart = new DragonPartEntity(this, 1.4F, 100, 0.3F, 0.85F, 0.2F, 0.5F);
            this.level().addFreshEntity(this.rightWingLowerPart);
        }
        this.rightWingLowerPart.updateScale(scale);

        if (this.leftWingUpperPart == null || this.leftWingUpperPart.isRemoved()) {
            this.leftWingUpperPart = new DragonPartEntity(this, 1, -90, 0.5F, 0.85F, 0.3F, 0.5F);
            this.level().addFreshEntity(this.leftWingUpperPart);
        }
        this.leftWingUpperPart.updateScale(scale);

        if (this.leftWingLowerPart == null || this.leftWingLowerPart.isRemoved()) {
            this.leftWingLowerPart = new DragonPartEntity(this, 1.4F, -100, 0.3F, 0.85F, 0.2F, 0.5F);
            this.level().addFreshEntity(this.leftWingLowerPart);
        }
        this.leftWingLowerPart.updateScale(scale);

        if (this.tail1Part == null || this.tail1Part.isRemoved()) {
            this.tail1Part = new DragonPartEntity(this, -0.75F, 0, 0.6F, 0.35F, 0.35F, 1);
            this.level().addFreshEntity(this.tail1Part);
        }
        this.tail1Part.updateScale(scale);

        if (this.tail2Part == null || this.tail2Part.isRemoved()) {
            this.tail2Part = new DragonPartEntity(this, -1.15F, 0, 0.45F, 0.35F, 0.35F, 1);
            this.level().addFreshEntity(this.tail2Part);
        }
        this.tail2Part.updateScale(scale);

        if (this.tail3Part == null || this.tail3Part.isRemoved()) {
            this.tail3Part = new DragonPartEntity(this, -1.5F, 0, 0.35F, 0.35F, 0.35F, 1);
            this.level().addFreshEntity(this.tail3Part);
        }
        this.tail3Part.updateScale(scale);

        if (this.tail4Part == null || this.tail4Part.isRemoved()) {
            this.tail4Part = new DragonPartEntity(this, -1.95F, 0, 0.25F, 0.45F, 0.3F, 1.5F);
            this.level().addFreshEntity(this.tail4Part);
        }
        this.tail4Part.updateScale(scale);
    }

    public void removeParts() {
        if (this.headPart != null) {
            this.headPart.remove(RemovalReason.DISCARDED);
            this.headPart = null;
        }
        if (this.neckPart != null) {
            this.neckPart.remove(RemovalReason.DISCARDED);
            this.neckPart = null;
        }
        if (this.rightWingUpperPart != null) {
            this.rightWingUpperPart.remove(RemovalReason.DISCARDED);
            this.rightWingUpperPart = null;
        }
        if (this.rightWingLowerPart != null) {
            this.rightWingLowerPart.remove(RemovalReason.DISCARDED);
            this.rightWingLowerPart = null;
        }
        if (this.leftWingUpperPart != null) {
            this.leftWingUpperPart.remove(RemovalReason.DISCARDED);
            this.leftWingUpperPart = null;
        }
        if (this.leftWingLowerPart != null) {
            this.leftWingLowerPart.remove(RemovalReason.DISCARDED);
            this.leftWingLowerPart = null;
        }
        if (this.tail1Part != null) {
            this.tail1Part.remove(RemovalReason.DISCARDED);
            this.tail1Part = null;
        }
        if (this.tail2Part != null) {
            this.tail2Part.remove(RemovalReason.DISCARDED);
            this.tail2Part = null;
        }
        if (this.tail3Part != null) {
            this.tail3Part.remove(RemovalReason.DISCARDED);
            this.tail3Part = null;
        }
        if (this.tail4Part != null) {
            this.tail4Part.remove(RemovalReason.DISCARDED);
            this.tail4Part = null;
        }
    }

    public void updateParts() {
        if (this.isRemoved()) return;
        this.headPart.copyPosition(this);
        this.neckPart.copyPosition(this);
        this.rightWingUpperPart.copyPosition(this);
        this.rightWingLowerPart.copyPosition(this);
        this.leftWingUpperPart.copyPosition(this);
        this.leftWingLowerPart.copyPosition(this);
        this.tail1Part.copyPosition(this);
        this.tail2Part.copyPosition(this);
        this.tail3Part.copyPosition(this);
        this.tail4Part.copyPosition(this);

        IafEntityUtil.updatePart(this.headPart, this);
        IafEntityUtil.updatePart(this.neckPart, this);
        IafEntityUtil.updatePart(this.rightWingUpperPart, this);
        IafEntityUtil.updatePart(this.rightWingLowerPart, this);
        IafEntityUtil.updatePart(this.leftWingUpperPart, this);
        IafEntityUtil.updatePart(this.leftWingLowerPart, this);
        IafEntityUtil.updatePart(this.tail1Part, this);
        IafEntityUtil.updatePart(this.tail2Part, this);
        IafEntityUtil.updatePart(this.tail3Part, this);
        IafEntityUtil.updatePart(this.tail4Part, this);
    }

    public void updateBurnTarget() {
        if (this.burningTarget != null && !this.isSleeping() && !this.isModelDead() && !this.isBaby()) {
            float maxDist = 115 * this.getDragonStage();
            if (this.level().getBlockEntity(this.burningTarget) instanceof DragonForgeInputBlockEntity forge && forge.isAssembled() && this.distanceToSqr(this.burningTarget.getX() + 0.5D, this.burningTarget.getY() + 0.5D, this.burningTarget.getZ() + 0.5D) < maxDist && this.canPositionBeSeen(this.burningTarget.getX() + 0.5D, this.burningTarget.getY() + 0.5D, this.burningTarget.getZ() + 0.5D)) {
                this.getLookControl().setLookAt(this.burningTarget.getX() + 0.5D, this.burningTarget.getY() + 0.5D, this.burningTarget.getZ() + 0.5D, 180F, 180F);
                this.breathFireAtPos(this.burningTarget);
                this.setBreathingFire(true);
            } else {
                if (!this.level().isClientSide)
                    PacketDistributor.sendToAllPlayers(new DragonSetBurnBlockS2CPayload(this.getId(), true, this.burningTarget));
                this.burningTarget = null;
            }
        }
    }

    protected abstract void breathFireAtPos(BlockPos burningTarget);

    protected PathingStuckHandler createStuckHandler() {
        return PathingStuckHandler.createStuckHandler();
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level worldIn) {
        return this.createNavigator(worldIn, AdvancedPathNavigate.MovementType.WALKING);
    }

    protected PathNavigation createNavigator(Level worldIn, AdvancedPathNavigate.MovementType type) {
        return this.createNavigator(worldIn, type, this.createStuckHandler());
    }

    protected PathNavigation createNavigator(Level worldIn, AdvancedPathNavigate.MovementType type, PathingStuckHandler stuckHandler) {
        return this.createNavigator(worldIn, type, stuckHandler, 4f);
    }

    protected PathNavigation createNavigator(Level worldIn, AdvancedPathNavigate.MovementType type, PathingStuckHandler stuckHandler, float width) {
        AdvancedPathNavigate newNavigator = new AdvancedPathNavigate(this, this.level(), type, width, (float) 4.0);
        this.navigation = newNavigator;
        newNavigator.setCanFloat(true);
        newNavigator.getNodeEvaluator().setCanOpenDoors(true);
        return newNavigator;
    }

    public void switchNavigator(int navigatorType) {
        if (navigatorType == 0) {
            this.moveControl = new IafDragonFlightManager.GroundMoveHelper(this);
            this.navigation = this.createNavigator(this.level(), AdvancedPathNavigate.MovementType.WALKING, this.createStuckHandler().withTeleportSteps(5));
            this.navigatorType = 0;
            this.setFlying(false);
            this.setHovering(false);
        } else if (navigatorType == 1) {
            this.moveControl = new IafDragonFlightManager.FlightMoveHelper(this);
            this.navigation = this.createNavigator(this.level(), AdvancedPathNavigate.MovementType.FLYING);
            this.navigatorType = 1;
        } else {
            this.moveControl = new IafDragonFlightManager.PlayerFlightMoveHelper<>(this);
            this.navigation = this.createNavigator(this.level(), AdvancedPathNavigate.MovementType.FLYING);
            this.navigatorType = 2;
        }
    }

    @Override
    public boolean canRide(@NotNull Entity rider) {
        return true;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.breakBlocks(false);
    }

    @Override
    public void checkDespawn() {
        if (IafCommonConfig.INSTANCE.dragon.canDespawn.getValue()) super.checkDespawn();
    }

    public boolean canDestroyBlock(BlockPos pos, BlockState state) {
        return state.getBlock().defaultDestroyTime() <= 100;
    }

    @Override
    public boolean isMobDead() {
        return this.isModelDead();
    }

    @Override
    public int getMaxHeadYRot() {
        return 30 * this.getDragonStage() / 5;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inv, Player player) {
        return new DragonScreenHandler(syncId, this.dragonInventory, player.getInventory(), this);
    }

    @Override
    public void writeClientSideData(@NotNull AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.getId());
    }

    @Override
    public int getAmbientSoundInterval() {
        return 90;
    }

    @Override
    protected void tickDeath() {
        this.deathTime = 0;
        this.setModelDead(true);
        this.ejectPassengers();
        if (this.getDeathStage() >= this.getAgeInDays() / 5) {
            this.remove(RemovalReason.KILLED);
            for (int k = 0; k < 40; ++k) {
                double d2 = this.random.nextGaussian() * 0.02D;
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                if (this.level().isClientSide) {
                    this.level().addParticle(ParticleTypes.CLOUD, this.getX() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(), this.getY() + this.random.nextFloat() * this.getBbHeight(), this.getZ() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(), d2, d0, d1);
                }
            }
            this.spawnDeathParticles();
        }
    }

    protected void spawnDeathParticles() {
    }

    public void spawnBabyParticles() {
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        this.removeParts();
        super.remove(reason);
    }

    @Override
    public int getBaseExperienceReward() {
        return switch (this.getDragonStage()) {
            case 2 -> 20;
            case 3 -> 150;
            case 4 -> 300;
            case 5 -> 650;
            default -> 5;
        };
    }

    @Override
    public boolean isNoAi() {
        return this.isModelDead() || super.isNoAi();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HUNGER, 0);
        builder.define(AGE_TICKS, 0);
        builder.define(GENDER, false);
        builder.define(VARIANT, IafDragonColors.RED.getName());
        builder.define(SLEEPING, false);
        builder.define(FIREBREATHING, false);
        builder.define(HOVERING, false);
        builder.define(FLYING, false);
        builder.define(DEATH_STAGE, 0);
        builder.define(MODEL_DEAD, false);
        builder.define(CONTROL_STATE, (byte) 0);
        builder.define(TACKLE, false);
        builder.define(AGINGDISABLED, false);
        builder.define(COMMAND, 0);
        builder.define(DRAGON_PITCH, 0F);
        builder.define(CRYSTAL_BOUND, false);
        builder.define(CUSTOM_POSE, "");
    }

    @Override
    public boolean isGoingUp() {
        return (this.entityData.get(CONTROL_STATE) & 1) == 1;
    }

    @Override
    public boolean isGoingDown() {
        return (this.entityData.get(CONTROL_STATE) >> 1 & 1) == 1;
    }

    @Override
    public boolean isAggressive() {
        return (this.entityData.get(CONTROL_STATE) >> 2 & 1) == 1;
    }

    public boolean isStriking() {
        return (this.entityData.get(CONTROL_STATE) >> 3 & 1) == 1;
    }

    public boolean isDismounting() {
        return (this.entityData.get(CONTROL_STATE) >> 4 & 1) == 1;
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
        this.setStateField(3, strike);
    }

    @Override
    public void dismount(boolean dismount) {
        this.setStateField(4, dismount);
    }

    private void setStateField(int i, boolean newState) {
        byte prevState = this.entityData.get(CONTROL_STATE);
        if (newState) {
            this.entityData.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        } else {
            this.entityData.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
        }
    }

    @Override
    public byte getControlState() {
        return this.entityData.get(CONTROL_STATE);
    }

    @Override
    public void setControlState(byte state) {
        this.entityData.set(CONTROL_STATE, state);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, command);
        this.setOrderedToSit(command == 1);
    }

    public float getDragonPitch() {
        return this.entityData.get(DRAGON_PITCH);
    }

    public void setDragonPitch(float pitch) {
        this.entityData.set(DRAGON_PITCH, pitch);
    }

    public void incrementDragonPitch(float pitch) {
        this.entityData.set(DRAGON_PITCH, this.getDragonPitch() + pitch);
    }

    public void decrementDragonPitch(float pitch) {
        this.entityData.set(DRAGON_PITCH, this.getDragonPitch() - pitch);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Hunger", this.getHunger());
        compound.putInt("AgeTicks", this.getAgeInTicks());
        compound.putBoolean("Gender", this.isMale());
        compound.putString("Variant", this.getVariant());
        compound.putBoolean("Sleeping", this.isSleeping());
        compound.putBoolean("TamedDragon", this.isTame());
        compound.putBoolean("FireBreathing", this.isBreathingFire());
        compound.putBoolean("AttackDecision", this.usingGroundAttack);
        compound.putBoolean("Hovering", this.isHovering());
        compound.putBoolean("Flying", this.isFlying());
        compound.putInt("DeathStage", this.getDeathStage());
        compound.putBoolean("ModelDead", this.isModelDead());
        compound.putFloat("DeadProg", this.modelDeadProgress);
        compound.putBoolean("Tackle", this.isTackling());
        compound.putBoolean("HasHomePosition", this.hasHomePosition);
        compound.putString("CustomPose", this.getCustomPose());
        if (this.homePos != null && this.hasHomePosition) this.homePos.write(compound);
        compound.putBoolean("AgingDisabled", this.isAgingDisabled());
        compound.putInt("Command", this.getCommand());
        if (this.dragonInventory != null)
            compound.put("Items", ItemStack.OPTIONAL_CODEC.listOf().encodeStart(RegistryOps.create(NbtOps.INSTANCE, this.level().registryAccess()), this.dragonInventory.getItems()).resultOrPartial(IceAndFire.LOGGER::error).orElse(new ListTag()));
        compound.putBoolean("CrystalBound", this.isBoundToCrystal());
        this.removeParts();
        this.lastScale = 0;
        compound.putInt("BrushedTime", this.brushedTime);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHunger(compound.getInt("Hunger"));
        this.setAgeInTicks(compound.getInt("AgeTicks"));
        this.setGender(compound.getBoolean("Gender"));
        this.setVariant(compound.getString("Variant"));
        this.setInSittingPose(compound.getBoolean("Sleeping"));
        this.setTame(compound.getBoolean("TamedDragon"), true);
        this.setBreathingFire(compound.getBoolean("FireBreathing"));
        this.usingGroundAttack = compound.getBoolean("AttackDecision");
        this.setHovering(compound.getBoolean("Hovering"));
        this.setFlying(compound.getBoolean("Flying"));
        this.setDeathStage(compound.getInt("DeathStage"));
        this.setModelDead(compound.getBoolean("ModelDead"));
        this.modelDeadProgress = compound.getFloat("DeadProg");
        this.setCustomPose(compound.getString("CustomPose"));
        this.hasHomePosition = compound.getBoolean("HasHomePosition");
        if (this.hasHomePosition && compound.getInt("HomeAreaX") != 0 && compound.getInt("HomeAreaY") != 0 && compound.getInt("HomeAreaZ") != 0)
            this.homePos = new HomePosition(compound, this.level());
        this.setTackling(compound.getBoolean("Tackle"));
        this.setAgingDisabled(compound.getBoolean("AgingDisabled"));
        this.setCommand(compound.getInt("Command"));

        this.createInventory();
        List<ItemStack> stacks = ItemStack.OPTIONAL_CODEC.listOf().parse(RegistryOps.create(NbtOps.INSTANCE, this.level().registryAccess()), compound.get("Items")).resultOrPartial(IceAndFire.LOGGER::error).orElse(List.of());
        for (int i = 0; i < stacks.size() && i < this.dragonInventory.getContainerSize(); i++)
            this.dragonInventory.setItem(i, stacks.get(i));

        this.setCrystalBound(compound.getBoolean("CrystalBound"));
        this.setConfigurableAttributes();
        this.refreshDirtyAttributes();
        this.brushedTime = compound.getInt("BrushedTime");
    }

    public int getContainerSize() {
        return 5;
    }

    protected void createInventory() {
        SimpleContainer tempInventory = this.dragonInventory;
        this.dragonInventory = new SimpleContainer(this.getContainerSize());
        if (tempInventory != null) {
            tempInventory.removeListener(this);
            int i = Math.min(tempInventory.getContainerSize(), this.dragonInventory.getContainerSize());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = tempInventory.getItem(j);
                if (!itemstack.isEmpty())
                    this.dragonInventory.setItem(j, itemstack.copy());
            }
        }

        this.dragonInventory.addListener(this);
        this.updateContainerEquipment();
    }

    protected void updateContainerEquipment() {
        if (!this.level().isClientSide)
            this.refreshDirtyAttributes();
    }

    public boolean hasInventoryChanged(Container pInventory) {
        return this.dragonInventory != pInventory;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengers())
            if (passenger instanceof LivingEntity living && this.getTarget() != living)
                if (this.isTame() && this.getOwnerUUID() != null && this.getOwnerUUID().equals(living.getUUID()))
                    return living;
        return null;
    }

    @Override
    public Player getRidingPlayer() {
        if (this.getControllingPassenger() instanceof Player player)
            return player;
        return null;
    }

    public void refreshDirtyAttributes() {
        double age = Math.min(this.getAgeInDays(), 125);
        final double healthStep = (this.maximumHealth - this.minimumHealth) / 125F;
        final double attackStep = (this.maximumDamage - this.minimumDamage) / 125F;
        final double speedStep = (this.maximumSpeed - this.minimumSpeed) / 125F;
        final double armorStep = (this.maximumArmor - this.minimumArmor) / 125F;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.round(this.minimumHealth + (healthStep * age)));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(Math.round(this.minimumDamage + (attackStep * age)));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.minimumSpeed + (speedStep * age));
        final double baseValue = this.minimumArmor + (armorStep * this.getAgeInDays());
        this.getAttribute(Attributes.ARMOR).setBaseValue(baseValue);
        if (!this.level().isClientSide) {
            this.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER);
            this.getAttribute(Attributes.ARMOR).addPermanentModifier(new AttributeModifier(ARMOR_MODIFIER, this.calculateArmorModifier(), AttributeModifier.Operation.ADD_VALUE));
        }
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(Math.min(2048, IafCommonConfig.INSTANCE.dragon.targetSearchLength.getValue()));
    }

    public int getHunger() {
        return this.entityData.get(HUNGER);
    }

    public void setHunger(int hunger) {
        this.entityData.set(HUNGER, Mth.clamp(hunger, 0, 100));
    }

    public String getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(String variant) {
        this.entityData.set(VARIANT, variant);
    }

    public int getAgeInDays() {
        return Mth.clamp(this.entityData.get(AGE_TICKS) / 24000, 0, this.isTame() ? IafCommonConfig.INSTANCE.dragon.maxTamedDragonAge.getValue() : 128);
    }

    public void setAgeInDays(int age) {
        this.entityData.set(AGE_TICKS, age * 24000);
    }

    public int getAgeInTicks() {
        return this.entityData.get(AGE_TICKS);
    }

    public void setAgeInTicks(int age) {
        this.entityData.set(AGE_TICKS, age);
    }

    public int getDeathStage() {
        return this.entityData.get(DEATH_STAGE);
    }

    public void setDeathStage(int stage) {
        this.entityData.set(DEATH_STAGE, stage);
    }

    public boolean isMale() {
        return this.entityData.get(GENDER);
    }

    public boolean isModelDead() {
        if (this.level().isClientSide) {
            return this.isModelDead = this.entityData.get(MODEL_DEAD);
        }
        return this.isModelDead;
    }

    public void setModelDead(boolean modeldead) {
        this.entityData.set(MODEL_DEAD, modeldead);
        if (!this.level().isClientSide) {
            this.isModelDead = modeldead;
        }
    }

    @Override
    public boolean isHovering() {
        return this.entityData.get(HOVERING);
    }

    public void setHovering(boolean hovering) {
        this.entityData.set(HOVERING, hovering);
    }

    @Override
    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public boolean useFlyingPathFinder() {
        return this.isFlying() && this.getControllingPassenger() == null;
    }

    public void setGender(boolean male) {
        this.entityData.set(GENDER, male);
    }

    @Override
    public boolean isSleeping() {
        return this.entityData.get(SLEEPING);
    }

    public boolean isBlinking() {
        return this.tickCount % 50 > 43;
    }

    public boolean isBreathingFire() {
        return this.entityData.get(FIREBREATHING);
    }

    public void setBreathingFire(boolean breathing) {
        this.entityData.set(FIREBREATHING, breathing);
    }

    @Override
    protected boolean canAddPassenger(@NotNull Entity passenger) {
        return this.getPassengers().size() < 2;
    }

    @Override
    public boolean isOrderedToSit() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    @Override
    public void setOrderedToSit(boolean sitting) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (sitting) {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 | 1));
            this.getNavigation().stop();
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 & -2));
        }
    }

    @Override
    public void setInSittingPose(boolean sleeping) {
        this.entityData.set(SLEEPING, sleeping);
        if (sleeping)
            this.getNavigation().stop();
    }

    public String getCustomPose() {
        return this.entityData.get(CUSTOM_POSE);
    }

    public void setCustomPose(String customPose) {
        this.entityData.set(CUSTOM_POSE, customPose);
        this.modelDeadProgress = 20f;
    }

    public void riderShootFire(Entity controller) {
    }

    private double calculateArmorModifier() {
        double val = 1D;
        final EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (EquipmentSlot slot : slots)
            if (this.getItemBySlot(slot).getItem() instanceof DragonArmorItem dragonArmor)
                val += dragonArmor.type.protection();
        return val;
    }

    public boolean canMove() {
        return !this.isOrderedToSit() && !this.isSleeping() &&
                this.getControllingPassenger() == null && !this.isPassenger() &&
                !this.isModelDead() && this.sleepProgress == 0 &&
                this.getAnimation() != ANIMATION_SHAKEPREY;
    }

    public boolean isFuelingForge() {
        return this.burningTarget != null && this.level().getBlockEntity(this.burningTarget) instanceof DragonForgeInputBlockEntity;
    }

    @Override
    public boolean isAlive() {
        if (this.isModelDead())
            return !this.isRemoved();
        return super.isAlive();
    }

    @Override
    public @NotNull InteractionResult interactAt(Player player, @NotNull Vec3 vec, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == IafItems.DRAGON_DEBUG_STICK.get()) {
            this.logic.debug();
            return InteractionResult.SUCCESS;
        }
        return super.interactAt(player, vec, hand);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        // Interaction usually means right-click but the relevant item is often in the main hand
        ItemStack stack = player.getMainHandItem();
        int lastDeathStage = Math.min(this.getAgeInDays() / 5, 25);

        if (stack == ItemStack.EMPTY) {
            stack = player.getItemInHand(hand);
        }

        if (stack.getItem() == IafItems.DRAGON_DEBUG_STICK.get()) {
            this.logic.debug();
            return InteractionResult.SUCCESS;
        }
        if (!this.isModelDead()) {
            if (stack.getItem() == IafItems.CREATIVE_DRAGON_MEAL.get()) {
                this.setTame(true, true);
                this.tame(player);
                this.setHunger(this.getHunger() + 20);
                this.heal(Math.min(this.getHealth(), (int) (this.getMaxHealth() / 2)));
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                this.spawnItemCrackParticles(stack.getItem());
                this.spawnItemCrackParticles(Items.BONE);
                this.spawnItemCrackParticles(Items.BONE_MEAL);
                if (!player.isCreative()) stack.shrink(1);
                this.brushedTime = 0;
                return InteractionResult.SUCCESS;
            }
            if (this.isFood(stack) && this.isMature()) {
                this.setAge(0);
                this.usePlayerItem(player, InteractionHand.MAIN_HAND, stack);
                this.setInLove(player);
                return InteractionResult.SUCCESS;
            }
            if (this.isOwnedBy(player)) {
                if (stack.is(Items.BRUSH) && IafCommonConfig.INSTANCE.dragon.enableBrushDragonScales.getValue() && this.getDragonStage() >= 3 && this.brushedTime < this.getDragonStage() * IafCommonConfig.INSTANCE.dragon.brushTimesMul.getValue()) {
                    if (this.level() instanceof ServerLevel serverWorld) {
                        DragonColor color = DragonColor.getById(this.getVariant());
                        Vec3 pos = this.position();
                        EntityUtil.item(serverWorld, pos.x, pos.y, pos.z, new ItemStack(color.getScaleItem(), new Random().nextInt(IafCommonConfig.INSTANCE.dragon.maxBrushScalesDropPerTime.getValue()) + 1), 0);
                    }
                    this.brushedTime++;
                    return InteractionResult.SUCCESS;
                }
                if (stack.getItem() == this.dragonType.getCrystalItem() && !SummoningCrystalItem.hasDragon(stack)) {
                    this.setCrystalBound(true);
                    CompoundTag compound = new CompoundTag(), dragonTag = new CompoundTag();
                    dragonTag.putUUID("DragonUUID", this.getUUID());
                    if (this.getCustomName() != null)
                        dragonTag.putString("CustomName", this.getCustomName().getString());
                    compound.put("Dragon", dragonTag);
                    stack.set(IafDataComponents.CRYSTAL_DRAGON_DATA.get(), compound);
                    this.playSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 1, 1);
                    player.swing(hand);
                    return InteractionResult.SUCCESS;
                }
                this.tame(player);
                if (stack.getItem() == IafItems.DRAGON_HORN.get())
                    return super.mobInteract(player, hand);
                if (stack.isEmpty() && !player.isShiftKeyDown()) {
                    if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                        final int dragonStage = this.getDragonStage();
                        if (dragonStage < 2) {
                            if (player.getPassengers().size() >= 3)
                                return InteractionResult.FAIL;
                            this.startRiding(player, true);
                            PacketDistributor.sendToPlayer(serverPlayer, new StartRidingMobPayload(this.getId(), true, true));
                        } else if (dragonStage > 2 && !player.isPassenger()) {
                            player.setShiftKeyDown(false);
                            player.startRiding(this, true);
                            PacketDistributor.sendToPlayer(serverPlayer, new StartRidingMobPayload(this.getId(), true, false));
                            this.setInSittingPose(false);
                        }
                        this.getNavigation().stop();
                    }
                    return InteractionResult.SUCCESS;
                } else if (stack.isEmpty() && player.isShiftKeyDown()) {
                    if (player instanceof ServerPlayer serverPlayer)
                        serverPlayer.openMenu(this);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                } else {
                    int itemFoodAmount = FoodUtils.getFoodPoints(stack, true, this.dragonType.piscivore());
                    if (itemFoodAmount > 0 && (this.getHunger() < 100 || this.getHealth() < this.getMaxHealth())) {
                        this.setHunger(this.getHunger() + itemFoodAmount);
                        this.setHealth(Math.min(this.getMaxHealth(), (int) (this.getHealth() + ((float) itemFoodAmount / 10))));
                        this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                        this.spawnItemCrackParticles(stack.getItem());
                        if (!player.isCreative())
                            stack.shrink(1);
                        return InteractionResult.SUCCESS;
                    }
                    final Item stackItem = stack.getItem();
                    if (stackItem == IafItems.DRAGON_MEAL.get() && this.getAgeInDays() < (this.isTame() ? IafCommonConfig.INSTANCE.dragon.maxTamedDragonAge.getValue() : 128)) {
                        this.setAgingDisabled(false);
                        this.growDragon(1);
                        this.setHunger(this.getHunger() + 20);
                        this.heal(Math.min(this.getHealth(), (int) (this.getMaxHealth() / 2)));
                        this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                        this.spawnItemCrackParticles(stackItem);
                        this.spawnItemCrackParticles(Items.BONE);
                        this.spawnItemCrackParticles(Items.BONE_MEAL);
                        if (!player.isCreative()) stack.shrink(1);
                        if (this.brushedTime > 0) this.brushedTime--;
                        return InteractionResult.SUCCESS;
                    } else if (stackItem == IafItems.SICKLY_DRAGON_MEAL.get() && !this.isAgingDisabled()) {
                        this.setHunger(this.getHunger() + 20);
                        this.heal(this.getMaxHealth());
                        this.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundVolume(), this.getVoicePitch());
                        this.spawnItemCrackParticles(stackItem);
                        this.spawnItemCrackParticles(Items.BONE);
                        this.spawnItemCrackParticles(Items.BONE_MEAL);
                        this.spawnItemCrackParticles(Items.POISONOUS_POTATO);
                        this.spawnItemCrackParticles(Items.POISONOUS_POTATO);
                        this.setAgingDisabled(true);
                        if (!player.isCreative())
                            stack.shrink(1);
                        return InteractionResult.SUCCESS;
                    } else if (stackItem == IafItems.DRAGON_STAFF.get()) {
                        if (player.isShiftKeyDown()) {
                            if (this.hasHomePosition) {
                                this.hasHomePosition = false;
                                player.displayClientMessage(Component.translatable("dragon.command.remove_home"), true);
                            } else {
                                BlockPos pos = this.blockPosition();
                                this.homePos = new HomePosition(pos, this.level());
                                this.hasHomePosition = true;
                                player.displayClientMessage(Component.translatable("dragon.command.new_home", pos.getX(), pos.getY(), pos.getZ(), this.homePos.getDimension()), true);
                            }
                        } else {
                            this.playSound(SoundEvents.ZOMBIE_INFECT, this.getSoundVolume(), this.getVoicePitch());
                            if (!this.level().isClientSide) {
                                this.setCommand(this.getCommand() + 1);
                                if (this.getCommand() > 2) {
                                    this.setCommand(0);
                                }
                            }
                            String commandText = "stand";
                            if (this.getCommand() == 1) {
                                commandText = "sit";
                            } else if (this.getCommand() == 2) {
                                commandText = "escort";
                            }
                            player.displayClientMessage(Component.translatable("dragon.command." + commandText), true);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        } else if (!this.level().isClientSide && this.getDeathStage() < lastDeathStage && player.mayBuild()) {
            if (!stack.isEmpty() && stack.getItem() == Items.GLASS_BOTTLE && this.getDeathStage() < lastDeathStage / 2 && IafCommonConfig.INSTANCE.dragon.lootBlood.getValue()) {
                if (!player.isCreative()) stack.shrink(1);
                this.setDeathStage(this.getDeathStage() + 1);
                player.getInventory().add(new ItemStack(this.getBloodItem(), 1));
                return InteractionResult.SUCCESS;
            } else if (stack.isEmpty()) {
                if (this.getDeathStage() >= lastDeathStage - 1 && IafCommonConfig.INSTANCE.dragon.lootSkull.getValue()) {
                    ItemStack skull = new ItemStack(this.getSkull());
                    skull.set(IafDataComponents.DRAGON_SKULL.get(), new DragonSkullComponent(this.getDragonStage(), this.getAgeInDays()));
                    this.spawnAtLocation(skull, 1);
                    this.remove(RemovalReason.DISCARDED);
                } else if (this.getDeathStage() == (lastDeathStage / 2) - 1 && IafCommonConfig.INSTANCE.dragon.lootHeart.getValue()) {
                    ItemStack heart = new ItemStack(this.getHeartItem(), 1);
                    ItemStack egg = new ItemStack(RandomHelper.randomOne(this.dragonType.colors()).getEggItem(), 1);
                    this.spawnAtLocation(heart, 1);
                    if (!this.isMale() && this.getDragonStage() > 3)
                        this.spawnAtLocation(egg, 1);
                } else {
                    ItemStack drop = this.getRandomDrop();
                    if (!drop.isEmpty()) this.spawnAtLocation(drop, 1);
                }
                this.setDeathStage(this.getDeathStage() + 1);
            } else return InteractionResult.PASS;
            return InteractionResult.SUCCESS;
        } else return super.mobInteract(player, hand);
        return InteractionResult.PASS;
    }

    @Override
    public boolean canFallInLove() {
        return this.isMature() && super.canFallInLove();
    }

    public abstract ItemLike getHeartItem();

    public abstract Item getBloodItem();

    public abstract Item getFleshItem();

    public abstract Item getSkull();

    private ItemStack getRandomDrop() {
        ItemStack stack = this.getItemFromLootTable();
        if (stack.getItem() == IafItems.DRAGON_BONE.get())
            this.playSound(SoundEvents.SKELETON_AMBIENT, 1, 1);
        else
            this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1, 1);
        return stack;
    }

    public boolean canPositionBeSeen(final double x, final double y, final double z) {
        final HitResult result = this.level().clip(new ClipContext(new Vec3(this.getX(), this.getY() + (double) this.getEyeHeight(), this.getZ()), new Vec3(x, y, z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        final double dist = result.getLocation().distanceToSqr(x, y, z);
        return dist <= 1.0D || result.getType() == HitResult.Type.MISS;
    }

    public abstract ResourceLocation getDeadLootTable();

    public ItemStack getItemFromLootTable() {
        assert this.getServer() != null;
        LootTable lootTable = this.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, this.getDeadLootTable()));
        LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) this.level())).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.DAMAGE_SOURCE, this.level().damageSources().generic());
        for (ItemStack itemstack : lootTable.getRandomItems(lootparams$builder.create(LootContextParamSets.ENTITY)))
            return itemstack;
        return ItemStack.EMPTY;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    public void growDragon(final int ageInDays) {
        if (this.isAgingDisabled())
            return;
        this.setAgeInDays(this.getAgeInDays() + ageInDays);
        //TODO: Probably brakes bounding boxes
        this.setBoundingBox(this.getBoundingBox());
        if (this.level().isClientSide) {
            if (this.getAgeInDays() % 25 == 0) {
                for (int i = 0; i < this.getRenderSize() * 4; i++) {
                    final float f = (float) (this.getRandom().nextFloat() * (this.getBoundingBox().maxX - this.getBoundingBox().minX) + this.getBoundingBox().minX);
                    final float f1 = (float) (this.getRandom().nextFloat() * (this.getBoundingBox().maxY - this.getBoundingBox().minY) + this.getBoundingBox().minY);
                    final float f2 = (float) (this.getRandom().nextFloat() * (this.getBoundingBox().maxZ - this.getBoundingBox().minZ) + this.getBoundingBox().minZ);
                    final double motionX = this.getRandom().nextGaussian() * 0.07D;
                    final double motionY = this.getRandom().nextGaussian() * 0.07D;
                    final double motionZ = this.getRandom().nextGaussian() * 0.07D;

                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, f, f1, f2, motionX, motionY, motionZ);
                }
            }
        }
        if (this.getDragonStage() >= 2)
            this.removeVehicle();
        this.refreshDirtyAttributes();
    }

    public void spawnItemCrackParticles(Item item) {
        for (int i = 0; i < 15; i++) {
            final double motionX = this.getRandom().nextGaussian() * 0.07D;
            final double motionY = this.getRandom().nextGaussian() * 0.07D;
            final double motionZ = this.getRandom().nextGaussian() * 0.07D;
            final Vec3 headVec = this.getHeadPosition();
            if (!this.level().isClientSide) {
                ((ServerLevel) this.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(item)), headVec.x, headVec.y, headVec.z, 1, motionX, motionY, motionZ, 0.1);
            } else {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(item)), headVec.x, headVec.y, headVec.z, motionX, motionY, motionZ);
            }
        }
    }

    public boolean isTimeToWake() {
        return this.level().isDay() || this.getCommand() == 2;
    }

    private boolean isStuck() {
        boolean skip = this.isChained() || this.isTame();

        if (skip) {
            return false;
        }

        boolean checkNavigation = this.ticksStill > 80 && this.canMove() && !this.isHovering();

        if (checkNavigation) {
            PathNavigation navigation = this.getNavigation();
            Path path = navigation.getPath();

            return !navigation.isDone() && (path == null || path.getEndNode() != null || this.blockPosition().distSqr(path.getEndNode().asBlockPos()) > 15);
        }

        return false;
    }

    public boolean isOverAir() {
        return this.isOverAir;
    }

    private boolean isOverAirLogic() {
        return this.level().isEmptyBlock(BlockPos.containing(this.getBlockX(), this.getBoundingBox().minY - 1, this.getBlockZ()));
    }

    public boolean isDiving() {
        return false;//isFlying() && motionY < -0.2;
    }

    public boolean isBeyondHeight() {
        if (this.getY() > this.level().getMaxBuildHeight()) {
            return true;
        }
        return this.getY() > IafCommonConfig.INSTANCE.dragon.maxFlight.getValue();
    }

    private int calculateDownY() {
        if (this.getNavigation().getPath() != null) {
            Path path = this.getNavigation().getPath();
            Vec3 p = path.getEntityPosAtNode(this, Math.min(path.getNodeCount() - 1, path.getNextNodeIndex() + 1));
            if (p.y < this.getY() - 1) {
                return -1;
            }
        }
        return 1;
    }

    public void breakBlock(final BlockPos position) {
        if (IafEvents.ON_GRIEF_BREAK_BLOCK.invoker().onBreakBlock(this, position.getX(), position.getY(), position.getZ()))
            return;

        final BlockState state = this.level().getBlockState(position);
        final float hardness = IafCommonConfig.INSTANCE.dragon.griefing.getValue() || this.getDragonStage() <= 3 ? 2.0F : 5.0F;
        if (this.isBreakable(position, state, hardness, this)) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6F, 1, 0.6F));
            if (!this.level().isClientSide()) {
                this.level().destroyBlock(position, !state.is(IafBlockTags.DRAGON_BLOCK_BREAK_NO_DROPS) && this.random.nextFloat() <= IafCommonConfig.INSTANCE.dragon.blockBreakingDropChance.getValue());
            }
        }
    }

    public void breakBlocks(boolean force) {
        boolean doBreak = force;

        if (this.blockBreakCounter > 0 || IafCommonConfig.INSTANCE.dragon.breakBlockCooldown.getValue() == 0) {
            --this.blockBreakCounter;
            if (this.blockBreakCounter == 0 || IafCommonConfig.INSTANCE.dragon.breakBlockCooldown.getValue() == 0)
                doBreak = true;
        }

        if (doBreak) {
            if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                if (DragonUtils.canGrief(this)) {
                    // TODO :: make `force` ignore the dragon stage?
                    if (!this.isModelDead() && this.getDragonStage() >= 3 && (this.canMove() || this.getControllingPassenger() != null)) {
                        final int bounds = 1;
                        final int flightModifier = this.isFlying() && this.getTarget() != null ? -1 : 1;
                        final int yMinus = this.calculateDownY();
                        BlockPos.betweenClosedStream(
                                (int) Math.floor(this.getBoundingBox().minX) - bounds,
                                (int) Math.floor(this.getBoundingBox().minY) + yMinus,
                                (int) Math.floor(this.getBoundingBox().minZ) - bounds,
                                (int) Math.floor(this.getBoundingBox().maxX) + bounds,
                                (int) Math.floor(this.getBoundingBox().maxY) + bounds + flightModifier,
                                (int) Math.floor(this.getBoundingBox().maxZ) + bounds
                        ).forEach(this::breakBlock);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    protected boolean isBreakable(BlockPos pos, BlockState state, float hardness, DragonBaseEntity entity) {
        return state.blocksMotion() && !state.isAir() &&
                state.getFluidState().isEmpty() && !state.getShape(this.level(), pos).isEmpty() &&
                state.getDestroySpeed(this.level(), pos) >= 0F &&
                state.getDestroySpeed(this.level(), pos) <= hardness &&
                DragonUtils.canDragonBreak(state, entity) && this.canDestroyBlock(pos, state);
    }

    public void spawnGroundEffects() {
        if (this.level().isClientSide) {
            for (int i = 0; i < this.getRenderSize(); i++) {
                for (int i1 = 0; i1 < 20; i1++) {
                    final float radius = 0.75F * (0.7F * this.getRenderSize() / 3) * -3;
                    final float angle = (0.01745329251F * this.yBodyRot) + i1 * 1F;
                    final double extraX = radius * Mth.sin((float) (Math.PI + angle));
                    final double extraY = 0.8F;
                    final double extraZ = radius * Mth.cos(angle);
                    final BlockPos ground = this.getGround(BlockPos.containing(this.getX() + extraX, this.getY() + extraY - 1, this.getZ() + extraZ));
                    final BlockState BlockState = this.level().getBlockState(ground);
                    if (BlockState.isAir()) {
                        final double motionX = this.getRandom().nextGaussian() * 0.07D;
                        final double motionY = this.getRandom().nextGaussian() * 0.07D;
                        final double motionZ = this.getRandom().nextGaussian() * 0.07D;

                        this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockState), true, this.getX() + extraX, ground.getY() + extraY, this.getZ() + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
    }

    private BlockPos getGround(BlockPos blockPos) {
        while (this.level().isEmptyBlock(blockPos) && blockPos.getY() > 1) {
            blockPos = blockPos.below();
        }
        return blockPos;
    }

    public boolean isActuallyBreathingFire() {
        return this.fireBreathTicks > 20 && this.isBreathingFire();
    }

    public boolean doesWantToLand() {
        return this.flyTicks > 6000 || this.isGoingDown() || this.flyTicks > 40 && this.flyProgress == 0 || this.isChained() && this.flyTicks > 100;
    }

    public abstract String getVariantName(int variant);

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (this.hasPassenger(passenger)) {
            if (this.getControllingPassenger() == null || !this.getControllingPassenger().getUUID().equals(passenger.getUUID())) {
                this.updatePreyInMouth(passenger);
            } else {
                if (this.isModelDead()) passenger.stopRiding();
                this.setYRot(passenger.getYRot());
                this.setYHeadRot(passenger.getYHeadRot());
                this.setXRot(passenger.getXRot());

                Vec3 riderPos = this.getRiderPosition();
                passenger.setPos(riderPos.x, riderPos.y + passenger.getBbHeight(), riderPos.z);
            }
        }
    }

    private float bob(float speed, float degree, boolean bounce, float f, float f1) {
        final double a = Mth.sin(f * speed) * f1 * degree;
        float bob = (float) (a - f1 * degree);
        if (bounce) {
            bob = (float) -Math.abs(a);
        }
        return bob * this.getRenderSize() / 3;
    }

    protected void updatePreyInMouth(final Entity prey) {
        if (this.getAnimation() != ANIMATION_SHAKEPREY) {
            this.setAnimation(ANIMATION_SHAKEPREY);
        }

        if (this.getAnimation() == ANIMATION_SHAKEPREY && this.getAnimationTick() > 55 && prey != null) {
            float baseDamage = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            float damage = baseDamage * 2;
            boolean didDamage = prey.hurt(this.level().damageSources().mobAttack(this), damage);

            if (didDamage) {
                if (IafCommonConfig.INSTANCE.dragon.canHealFromBiting.getValue()) {
                    this.heal(damage * 0.5f);
                }
            }

            if (!(prey instanceof Player)) {
                this.setHunger(this.getHunger() + 1);
            }

            prey.stopRiding();
        } else {
            this.yBodyRot = this.getYRot();
            final float modTick_0 = this.getAnimationTick() - 25;
            final float modTick_1 = this.getAnimationTick() > 25 && this.getAnimationTick() < 55 ? 8 * Mth.clamp(Mth.sin((float) (Math.PI + modTick_0 * 0.25)), -0.8F, 0.8F) : 0;
            final float modTick_2 = this.getAnimationTick() > 30 ? 10 : Math.max(0, this.getAnimationTick() - 20);
            final float radius = 0.75F * (0.6F * this.getRenderSize() / 3) * -3;
            final float angle = (0.01745329251F * this.yBodyRot) + 3.15F + (modTick_1 * 2F) * 0.015F;
            final double extraX = radius * Mth.sin((float) (Math.PI + angle));
            final double extraZ = radius * Mth.cos(angle);
            final double extraY = modTick_2 == 0 ? 0 : 0.035F * ((this.getRenderSize() / 3) + (modTick_2 * 0.5 * (this.getRenderSize() / 3)));
            assert prey != null;
            prey.setPos(this.getX() + extraX, this.getY() + extraY, this.getZ() + extraZ);
        }
    }

    public int getDragonStage() {
        final int age = this.getAgeInDays();
        if (age >= 100) return 5;
        else if (age >= 75) return 4;
        else if (age >= 50) return 3;
        else if (age >= 25) return 2;
        else return 1;
    }

    public boolean isTeen() {
        return this.getDragonStage() == 3;
    }

    public boolean isMature() {
        return this.getDragonStage() >= 4;
    }

    @Override
    public boolean shouldDropLoot() {
        return this.isMature();
    }

    @Override
    public boolean isBaby() {
        return this.getDragonStage() < 2;
    }

    @Override
    public @NotNull SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull MobSpawnType reason, SpawnGroupData spawnDataIn) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
        this.setGender(this.getRandom().nextBoolean());
        final int age = this.getRandom().nextInt(80) + 1;
        this.growDragon(age);
        this.setVariant(RandomHelper.randomOne(this.dragonType.colors()).getName());
        this.setInSittingPose(false);
        final double healthStep = (this.maximumHealth - this.minimumHealth) / 125;
        this.heal((Math.round(this.minimumHealth + (healthStep * age))));
        this.usingGroundAttack = true;
        this.setHunger(50);
        return spawnDataIn;
    }

    @Override
    public boolean hurt(@NotNull DamageSource dmg, float i) {
        if (this.isModelDead() && dmg != this.level().damageSources().fellOutOfWorld()) {
            return false;
        }
        if (this.isVehicle() && dmg.getEntity() != null && this.getControllingPassenger() != null && dmg.getEntity() == this.getControllingPassenger()) {
            return false;
        }

        if ((dmg.type().msgId().contains("arrow") || this.getVehicle() != null && dmg.getEntity() != null && dmg.getEntity().is(this.getVehicle())) && this.isPassenger()) {
            return false;
        }

        if (dmg.is(DamageTypes.IN_WALL) || dmg.is(DamageTypes.FALLING_BLOCK) || dmg.is(DamageTypes.CRAMMING)) {
            return false;
        }
        if (!this.level().isClientSide && dmg.getEntity() != null && this.getRandom().nextInt(4) == 0) {
            this.roar();
        }
        if (i > 0) {
            if (this.isSleeping()) {
                this.setInSittingPose(false);
                if (!this.isTame()) {
                    if (dmg.getEntity() instanceof Player) {
                        this.setTarget((Player) dmg.getEntity());
                    }
                }
            }
        }
        return super.hurt(dmg, i);

    }

    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        final float scale = Math.min(this.getRenderSize() * 0.35F, 7F);
        if (scale != this.lastScale)
            this.updateScale(this.getRenderSize() / 3);
        this.lastScale = scale;
    }

    @Override
    public float maxUpStep() {
        return Math.max(1.2F, 1.2F + (Math.min(this.getAgeInDays(), 125) - 25) * 1.8F / 100F);
    }

    @Override
    public void tick() {
        super.tick();
        // Capture previous animation progress BEFORE they are updated this tick,
        // so the renderer can properly interpolate between frames.
        this.prevModelDeadProgress = this.modelDeadProgress;
        this.prevDiveProgress = this.diveProgress;
        this.prevAnimationProgresses[0] = this.sitProgress;
        this.prevAnimationProgresses[1] = this.sleepProgress;
        this.prevAnimationProgresses[2] = this.hoverProgress;
        this.prevAnimationProgresses[3] = this.flyProgress;
        this.prevAnimationProgresses[4] = this.fireBreathProgress;
        this.prevAnimationProgresses[5] = this.ridingProgress;
        this.prevAnimationProgresses[6] = this.tackleProgress;
        //TODO: Better detect logic
        if (!IntegrationExecutor.getWhenLoad("ponder", () -> () -> this.level() instanceof SchematicLevel, () -> false)) {
            this.refreshDimensions();
            this.updateParts();
        }
        this.prevDragonPitch = this.getDragonPitch();
        this.level().getProfiler().push("dragonLogic");
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(this.maxUpStep());
        this.isOverAir = this.isOverAirLogic();
        this.logic.updateDragonCommon();
        if (this.isModelDead()) {
            if (!this.level().isClientSide && this.level().isEmptyBlock(BlockPos.containing(this.getBlockX(), this.getBoundingBox().minY, this.getBlockZ())) && this.getY() > -1) {
                this.move(MoverType.SELF, new Vec3(0, -0.2F, 0));
            }
            this.setBreathingFire(false);

            float dragonPitch = this.getDragonPitch();
            if (dragonPitch > 0) {
                dragonPitch = Math.min(0, dragonPitch - 5);
                this.setDragonPitch(dragonPitch);
            }
            if (dragonPitch < 0) {
                this.setDragonPitch(Math.max(0, dragonPitch + 5));
            }
        } else {
            if (this.level().isClientSide) {
                this.logic.updateDragonClient();
            } else {
                this.logic.updateDragonServer();
                this.logic.updateDragonAttack();
            }
        }
        this.level().getProfiler().pop();
        this.level().getProfiler().push("dragonFlight");
        if (this.useFlyingPathFinder() && !this.level().isClientSide /*&& isControlledByLocalInstance()*/) {
            this.flightManager.update();
        }
        this.level().getProfiler().pop();
        this.level().getProfiler().pop();

        if (!this.level().isClientSide()) {
            if (IafCommonConfig.INSTANCE.dragon.digWhenStuck.getValue() && this.isStuck()) {
                this.breakBlocks(true);
                this.resetStuck();
            }
        }
    }


    private void resetStuck() {
        this.ticksStill = 0;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof Player) {
            this.setTarget(null);
        }
        if (this.isModelDead()) {
            if (this.isVehicle()) {
                this.ejectPassengers();
            }

            this.setHovering(false);
            this.setFlying(false);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        if (this.animationTick > this.getAnimation().getDuration() && !this.level().isClientSide)
            this.animationTick = 0;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose poseIn) {
        return this.getType().getDimensions().scale(this.getAgeScale());
    }

    @Override
    public float getAgeScale() {
        return Math.min(this.getRenderSize() * 0.35F, 7F);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    public float getRenderSize() {
        DragonSize size = DragonSize.getSize(this.getDragonStage());
        final float step = size.step() / 25;
        if (this.getAgeInDays() > 125) return size.x0() + (step * 25);
        return size.x0() + (step * this.getAgeFactor());
    }

    private int getAgeFactor() {
        return (this.getDragonStage() > 1 ? this.getAgeInDays() - (25 * (this.getDragonStage() - 1)) : this.getAgeInDays());
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        this.getLookControl().setLookAt(entityIn, 30.0F, 30.0F);
        if (this.isTackling() || this.isModelDead()) return false;
        return entityIn.hurt(this.level().damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
    }

    @Override
    public void rideTick() {
        Entity entity = this.getVehicle();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                this.updateRiding(entity);
            }
        }
    }

    public void updateRiding(Entity riding) {
        if (riding != null && riding.hasPassenger(this) && riding instanceof Player player) {
            final int i = riding.getPassengers().indexOf(this);
            final float radius = (i == 2 ? -0.2F : 0.5F) + (player.isFallFlying() ? 2 : 0);
            final float angle = (0.01745329251F * player.yBodyRot) + (i == 1 ? 90 : i == 0 ? -90 : 0);
            final double extraX = radius * Mth.sin((float) (Math.PI + angle));
            final double extraZ = radius * Mth.cos(angle);
            final double extraY = (riding.isShiftKeyDown() ? 1.2D : 1.4D) + (i == 2 ? 0.4D : 0D);
            this.yHeadRot = player.yHeadRot;
            this.setYRot(this.yHeadRot);
            this.setPos(riding.getX() + extraX, riding.getY() + extraY, riding.getZ() + extraZ);
            if ((this.getControlState() == 1 << 4 || player.isFallFlying()) && !riding.isPassenger()) {
                this.stopRiding();
                if (this.level().isClientSide)
                    PacketDistributor.sendToServer(new StartRidingMobPayload(this.getId(), false, true));
            }
        }
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
        if (this.isModelDead()) return NO_ANIMATION;
        return this.currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (this.isModelDead()) return;
        this.currentAnimation = animation;
    }

    @Override
    public void playAmbientSound() {
        if (!this.isSleeping() && !this.isModelDead() && !this.level().isClientSide) {
            if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION)
                this.setAnimation(ANIMATION_SPEAK);
            super.playAmbientSound();
        }
    }

    @Override
    protected void playHurtSound(@NotNull DamageSource source) {
        if (!this.isModelDead()) {
            if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION && !this.level().isClientSide)
                this.setAnimation(ANIMATION_SPEAK);
            super.playHurtSound(source);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, DragonBaseEntity.ANIMATION_EAT};
    }

    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverWorld, @NotNull AgeableMob ageable) {
        return null;
    }

    @Override
    public boolean canMate(@NotNull Animal otherAnimal) {
        return otherAnimal instanceof DragonBaseEntity dragon && otherAnimal != this && otherAnimal.getClass() == this.getClass() && (this.isMale() && !dragon.isMale() || !this.isMale() && dragon.isMale());
    }

    //FIXME::Do not use id to find types
    public DragonEggEntity createEgg() {
        DragonEggEntity dragon = new DragonEggEntity(IafEntities.DRAGON_EGG.get(), this.level());
        dragon.setEggType(IafRegistries.DRAGON_COLOR.byId(new Random().nextInt(4) + this.getStartMetaForType()));
        dragon.setPos(Mth.floor(this.getX()) + 0.5, Mth.floor(this.getY()) + 1, Mth.floor(this.getZ()) + 0.5);
        return dragon;
    }

    public int getStartMetaForType() {
        return 0;
    }

    public boolean isTargetBlocked(Vec3 target) {
        if (target != null) {
            final BlockHitResult rayTrace = this.level().clip(new ClipContext(this.position().add(0, this.getEyeHeight(), 0), target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            final BlockPos sidePos = rayTrace.getBlockPos();
            if (!this.level().isEmptyBlock(sidePos))
                return true;
            return rayTrace.getType() == HitResult.Type.BLOCK;
        }
        return false;
    }

    // FIXME :: Unused
    private double getFlySpeed() {
        return (2 + ((double) this.getAgeInDays() / 125) * 2) * (this.isTackling() ? 2 : 1);
    }

    public boolean isTackling() {
        return this.entityData.get(TACKLE);
    }

    public void setTackling(boolean tackling) {
        this.entityData.set(TACKLE, tackling);
    }

    public boolean isAgingDisabled() {
        return this.entityData.get(AGINGDISABLED);
    }

    public void setAgingDisabled(boolean isAgingDisabled) {
        this.entityData.set(AGINGDISABLED, isAgingDisabled);
    }

    public boolean isBoundToCrystal() {
        return this.entityData.get(CRYSTAL_BOUND);
    }

    public void setCrystalBound(boolean crystalBound) {
        this.entityData.set(CRYSTAL_BOUND, crystalBound);
    }

    public float getDistanceSquared(Vec3 Vector3d) {
        final float f = (float) (this.getX() - Vector3d.x);
        final float f1 = (float) (this.getY() - Vector3d.y);
        final float f2 = (float) (this.getZ() - Vector3d.z);
        return f * f + f1 * f1 + f2 * f2;
    }

    @Override
    public boolean isImmobile() {
        return this.getHealth() <= 0.0F || this.isOrderedToSit() && !this.isVehicle() || this.isModelDead() || this.isPassenger();
    }

    @Override
    public boolean isInWater() {
        return super.isInWater() && this.getFluidHeight(FluidTags.WATER) > Mth.floor(this.getDragonStage() / 2.0f);
    }

    @Override
    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.getAnimation() == ANIMATION_SHAKEPREY || !this.canMove() && !this.isVehicle() || this.isOrderedToSit()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            pTravelVector = new Vec3(0, 0, 0);
        }
        // Player riding controls
        // Note: when motion is handled by the client no server side setDeltaMovement() should be called
        // otherwise the movement will halt
        // Todo: move wrongly fix
        float flyingSpeed; // FIXME :: Why overlay the flyingSpeed variable from LivingEntity
        if (this.allowLocalMotionControl && this.getControllingPassenger() != null) {
            LivingEntity rider = this.getControllingPassenger();
            if (rider == null) {
                super.travel(pTravelVector);
                return;
            }

            // Flying control, include flying through waterfalls
            if (this.isHovering() || this.isFlying()) {
                double forward = rider.zza;
                double strafing = rider.xxa;
                double vertical = 0;
                float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
                // Bigger difference in speed for young and elder dragons
//                float airSpeedModifier = (float) (5.2f + 1.0f * Mth.map(Math.min(this.getAgeInDays(), 125), 0, 125, 0f, 1.5f));
                float airSpeedModifier = (float) (5.2f + 1.0f * Mth.map(speed, this.minimumSpeed, this.maximumSpeed, 0f, 1.5f));
                // Apply speed mod
                speed *= airSpeedModifier;
                // Set flag for logic and animation
                if (forward > 0) {
                    this.setFlying(true);
                    this.setHovering(false);
                }
                // Rider controlled tackling
                //                } else if (this.getXRot() > 10 && this.getDeltaMovement().length() > 1.0d) {
                //                    this.setDiving(true);
                // Todo: diving animation here
                this.setTackling(this.isAggressive() && this.getXRot() > -5 && this.getDeltaMovement().length() > 1.0d);

                this.gliding = this.allowMousePitchControl && rider.isSprinting();
                if (!this.gliding) {
                    // Mouse controlled yaw
                    speed += this.glidingSpeedBonus;
                    // Slower on going astern
                    forward *= rider.zza > 0 ? 1.0f : 0.5f;
                    // Slower on going sideways
                    strafing *= 0.4f;
                    if (this.isGoingUp() && !this.isGoingDown())
                        vertical = 1f;
                    else if (this.isGoingDown() && !this.isGoingUp())
                        vertical = -1f;
                        // Damp the vertical motion so the dragon's head is more responsive to the control
                    else
                        this.isControlledByLocalInstance();
                    // this.setDeltaMovement(this.getDeltaMovement().multiply(1.0f, 0.8f, 1.0f));
                } else {
                    // Mouse controlled yaw and pitch
                    speed *= 1.5f;
                    strafing *= 0.1f;
                    // Diving is faster
                    // Todo: a new and better algorithm much like elytra flying
                    this.glidingSpeedBonus = (float) Mth.clamp(this.glidingSpeedBonus + this.getDeltaMovement().y * -0.05d, -0.8d, 1.5d);
                    speed += this.glidingSpeedBonus;
                    // Try to match the moving vector to the rider's look vector
                    forward = Mth.abs(Mth.cos(this.getXRot() * ((float) Math.PI / 180F)));
                    vertical = Mth.abs(Mth.sin(this.getXRot() * ((float) Math.PI / 180F)));
                    // Pitch is still responsive to spacebar and x key
                    if (this.isGoingUp() && !this.isGoingDown())
                        vertical = Math.max(vertical, 0.5);
                    else if (this.isGoingDown() && !this.isGoingUp())
                        vertical = Math.min(vertical, -0.5);
                    else if (this.isGoingUp() && this.isGoingDown())
                        vertical = 0;
                        // X rotation takes minus on looking upward
                    else if (this.getXRot() < 0)
                        vertical *= 1;
                    else if (this.getXRot() > 0)
                        vertical *= -1;
                    else
                        this.isControlledByLocalInstance();
                    // this.setDeltaMovement(this.getDeltaMovement().multiply(1.0f, 0.8f, 1.0f));

                }
                // Speed bonus damping
                this.glidingSpeedBonus -= (float) (this.glidingSpeedBonus * 0.01d);

                if (this.isControlledByLocalInstance()) {
                    // Vanilla friction on Y axis is smaller, which will influence terminal speed for climbing and diving
                    // use same friction coefficient on all axis simplifies how travel vector is computed
                    flyingSpeed = speed * 0.1F;
                    this.setSpeed(flyingSpeed);

                    this.moveRelative(flyingSpeed, new Vec3(strafing, vertical, forward));
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    this.setDeltaMovement(this.getDeltaMovement().multiply(new Vec3(0.9, 0.9, 0.9)));

                    Vec3 currentMotion = this.getDeltaMovement();
                    if (this.horizontalCollision)
                        currentMotion = new Vec3(currentMotion.x, 0.1D, currentMotion.z);
                    this.setDeltaMovement(currentMotion);
                    this.calculateEntityAnimation(false);
                } else
                    this.setDeltaMovement(Vec3.ZERO);
                this.tryCheckInsideBlocks();
                this.updatePitch(this.yOld - this.getY());
            }
            // In water move control, for those that can't swim
            else if (this.isInWater() || this.isInLava()) {
                double forward = rider.zza;
                double strafing = rider.xxa;
                double vertical = 0;
                float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);

                if (this.isGoingUp() && !this.isGoingDown())
                    vertical = 0.5f;
                else if (this.isGoingDown() && !this.isGoingUp())
                    vertical = -0.5f;

                flyingSpeed = speed;
                // Float in water for those can't swim is done in LivingEntity#aiStep on server side
                // Leave this handled by both side before we have a better solution
                this.setSpeed(flyingSpeed);
                // Overwrite the zza in setSpeed
                this.setZza((float) forward);
                // Vanilla in water behavior includes float on water and moving very slow
                // in lava behavior includes moving slow and sink
                super.travel(pTravelVector.add(strafing, vertical, forward));

            }
            // Walking control
            else {
                double forward = rider.zza;
                double strafing = rider.xxa * 0.5f;
                // Inherit y motion for dropping
                double vertical = pTravelVector.y;
                float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);

                float groundSpeedModifier = (float) (1.8F * this.getFlightSpeedModifier());
                speed *= groundSpeedModifier;
                // Try to match the original riding speed
                forward *= speed;
                // Faster sprint
                forward *= rider.isSprinting() ? 1.2f : 1.0f;
                // Slower going back
                forward *= rider.zza > 0 ? 1.0f : 0.2f;

                if (this.isControlledByLocalInstance()) {
                    this.setSpeed(speed);
                    // Vanilla walking behavior includes going up steps
                    super.travel(new Vec3(strafing, vertical, forward));
                } else {
                    this.setDeltaMovement(Vec3.ZERO);
                }
                this.tryCheckInsideBlocks();
                this.updatePitch(this.yOld - this.getY());
            }
        }
        // No rider move control
        else {
            super.travel(pTravelVector);
        }
    }

    /**
     * Update dragon pitch for the server on {@link IafDragonLogic#updateDragonServer()} <br>
     * For some reason the {@link LivingEntity#yo} failed to update the pitch properly when the movement is handled by client.
     * Use {@link LivingEntity#yOld} instead will properly update the pitch on server.
     *
     * @param verticalDelta vertical distance from last update
     */
    public void updatePitch(final double verticalDelta) {
        if (this.isOverAir() && !this.isPassenger()) {
            // Update the pitch when in air, and stepping up many blocks
            if (!this.isHovering()) {
                this.incrementDragonPitch((float) (verticalDelta) * 10);
            }
            this.setDragonPitch(Mth.clamp(this.getDragonPitch(), -60, 40));
            final float plateau = 2;
            final float planeDist = (float) ((Math.abs(this.getDeltaMovement().x) + Math.abs(this.getDeltaMovement().z)) * 6F);
            if (this.getDragonPitch() > plateau) {
                //down
                //this.motionY -= 0.2D;
                this.decrementDragonPitch(planeDist * Math.abs(this.getDragonPitch()) / 90);
            }
            if (this.getDragonPitch() < -plateau) {//-2
                //up
                this.incrementDragonPitch(planeDist * Math.abs(this.getDragonPitch()) / 90);
            }
            if (this.getDragonPitch() > 2F) {
                this.decrementDragonPitch(1);
            } else if (this.getDragonPitch() < -2F) {
                this.incrementDragonPitch(1);
            }
            if (this.getControllingPassenger() == null && this.getDragonPitch() < -45 && planeDist < 3) {
                if (this.isFlying() && !this.isHovering()) {
                    this.setHovering(true);
                }
            }
        } else {
            // Damp the pitch once on ground
            if (Mth.abs(this.getDragonPitch()) < 1) {
                this.setDragonPitch(0);
            } else {
                this.setDragonPitch(this.getDragonPitch() / 1.5f);
            }
        }
    }

    /**
     * Rider logic from {@link IafDragonLogic#updateDragonServer()} <br>
     * Updates when rider is onboard
     */
    public void updateRider() {
        Entity controllingPassenger = this.getControllingPassenger();

        if (controllingPassenger instanceof Player rider) {
            this.ticksStill = 0;
            this.hoverTicks = 0;
            this.flyTicks = 0;

            if (this.isGoingUp()) {
                if (!this.isFlying() && !this.isHovering()) {
                    // Update spacebar tick for take off
                    this.spacebarTicks += 2;
                }
            } else if (this.isDismounting()) {
                if (this.isFlying() || this.isHovering()) {
                    // If the rider decided to dismount in air, try to follow
                    this.setCommand(2);
                }
            }
            // Update spacebar ticks and take off
            if (this.spacebarTicks > 0) {
                this.spacebarTicks--;
            }
            // Hold spacebar 1 sec to take off
            if (this.spacebarTicks > 20 && this.getOwner() != null && this.getPassengers().contains(this.getOwner()) && !this.isFlying() && !this.isHovering()) {
                if (!this.isInWater()) {
                    this.setHovering(true);
                    this.spacebarTicks = 0;

                    this.glidingSpeedBonus = 0;
                }
            }
            if (this.isFlying() || this.isHovering()) {
                if (rider.zza > 0) {
                    this.setFlying(true);
                    this.setHovering(false);
                } else {
                    this.setFlying(false);
                    this.setHovering(true);
                }
                // Hitting terrain with big angle of attack
                if (!this.isOverAir() && this.isFlying() && rider.getXRot() > 10 && !this.isInWater()) {
                    this.setHovering(false);
                    this.setFlying(false);
                }
                // Dragon landing
                if (!this.isOverAir() && this.isGoingDown() && !this.isInWater()) {
                    this.setFlying(false);
                    this.setHovering(false);
                }
            }

            // Dragon tackle attack
            if (this.isTackling()) {
                // Todo: tackling too low will cause animation to disappear
                this.tacklingTicks++;
                if (this.tacklingTicks == 40) {
                    this.tacklingTicks = 0;
                }
                if (!this.isFlying() && this.onGround()) {
                    this.tacklingTicks = 0;
                    this.setTackling(false);
                }
                // Todo: problem with friendly fire to tamed horses
                List<Entity> victims = this.level().getEntities(this, this.getBoundingBox().expandTowards(2.0D, 2.0D, 2.0D), potentialVictim -> (
                        potentialVictim != rider
                                && potentialVictim instanceof LivingEntity
                ));
                victims.forEach(victim -> this.logic.attackTarget(victim, rider, this.getDragonStage() * 3));
            }
            // Dragon breathe attack
            if (this.isStriking() && this.getControllingPassenger() != null && this.getDragonStage() > 1) {
                this.setBreathingFire(true);
                this.riderShootFire(this.getControllingPassenger());
                this.fireStopTicks = 10;
            }
            // Dragon bite attack
            if (this.isAggressive() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof Player) {
                LivingEntity target = DragonUtils.riderLookingAtEntity(this, this.getControllingPassenger(), this.getDragonStage() + (this.getBoundingBox().maxX - this.getBoundingBox().minX));
                if (this.getAnimation() != DragonBaseEntity.ANIMATION_BITE) {
                    this.setAnimation(DragonBaseEntity.ANIMATION_BITE);
                }
                if (target != null && !DragonUtils.hasSameOwner(this, target)) {
                    int damage = (int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                    boolean didDamage = this.logic.attackTarget(target, rider, damage);

                    if (didDamage) {
                        if (IafCommonConfig.INSTANCE.dragon.canHealFromBiting.getValue()) {
                            this.heal(damage * 0.1f);
                        }
                    }
                }
            }
            // Reset attack target when being ridden
            if (this.getTarget() != null && !this.getPassengers().isEmpty() && this.getOwner() != null && this.getPassengers().contains(this.getOwner())) {
                this.setTarget(null);
            }
            // Stop flying when hit the water, but waterfalls do not block flying
            if (this.getInBlockState().getFluidState().isSource() && this.isInWater() && !this.isGoingUp()) {
                this.setFlying(false);
                this.setHovering(false);
            }
        } else if (controllingPassenger instanceof DreadQueenEntity) {
            // Original logic involves riding
            Player ridingPlayer = this.getRidingPlayer();
            if (ridingPlayer != null) {
                if (this.isGoingUp()) {
                    if (!this.isFlying() && !this.isHovering()) {
                        this.spacebarTicks += 2;
                    }
                } else if (this.isDismounting()) {
                    if (this.isFlying() || this.isHovering()) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04, 0));
                        this.setFlying(false);
                        this.setHovering(false);
                    }
                }
            }
            if (!this.isDismounting() && (this.isFlying() || this.isHovering())) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.01, 0));
            }
            if (this.isStriking() && this.getControllingPassenger() != null && this.getDragonStage() > 1) {
                this.setBreathingFire(true);
                this.riderShootFire(this.getControllingPassenger());
                this.fireStopTicks = 10;
            }
            if (this.isAggressive() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof Player) {
                LivingEntity target = DragonUtils.riderLookingAtEntity(this, this.getControllingPassenger(), this.getDragonStage() + (this.getBoundingBox().maxX - this.getBoundingBox().minX));
                if (this.getAnimation() != DragonBaseEntity.ANIMATION_BITE) {
                    this.setAnimation(DragonBaseEntity.ANIMATION_BITE);
                }
                if (target != null && !DragonUtils.hasSameOwner(this, target)) {
                    this.logic.attackTarget(target, ridingPlayer, (int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                }
            }
            if (this.isFlying()) {
                if (!this.isHovering() && this.getControllingPassenger() != null && !this.onGround() && Math.max(Math.abs(this.getDeltaMovement().x()), Math.abs(this.getDeltaMovement().z())) < 0.1F) {
                    this.setHovering(true);
                    this.setFlying(false);
                }
            } else {
                if (this.isHovering() && this.getControllingPassenger() != null && !this.onGround() && Math.max(Math.abs(this.getDeltaMovement().x()), Math.abs(this.getDeltaMovement().z())) > 0.1F) {
                    this.setFlying(true);
                    this.usingGroundAttack = false;
                    this.setHovering(false);
                }
            }
            if (this.spacebarTicks > 0) {
                this.spacebarTicks--;
            }
            if (this.spacebarTicks > 20 && this.getOwner() != null && this.getPassengers().contains(this.getOwner()) && !this.isFlying() && !this.isHovering()) {
                this.setHovering(true);
            }

            if (this.isVehicle() && !this.isOverAir() && this.isFlying() && !this.isHovering() && this.flyTicks > 40) {
                this.setFlying(false);
            }
        }
    }

    @Override
    public void move(@NotNull MoverType pType, @NotNull Vec3 pPos) {
        if (this.isOrderedToSit() && !this.isVehicle()) {
            pPos = new Vec3(0, pPos.y(), 0);
        }

        if (this.isVehicle()) {
            // When riding, the server side movement check is performed in ServerGamePacketListenerImpl#handleMoveVehicle
            // verticalCollide tag might get inconsistent due to dragon's large bounding box and causes move wrongly msg
            if (this.isControlledByLocalInstance()) {
                // This is how DragonBaseEntity#breakBlock handles movement when breaking blocks
                // it's done by server, however client does not fire server side events, so breakBlock() here won't work
                if (this.horizontalCollision) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.6F, 1, 0.6F));
                }
                super.move(pType, pPos);
            } else {
                super.move(pType, pPos);
            }

            // Set no gravity flag to prevent getting kicked by flight disabled servers
            this.setNoGravity(this.isHovering() || this.isFlying());
        } else {
            // The flight mgr is not ready for noGravity
            this.setNoGravity(false);
            super.move(pType, pPos);
        }
    }

    public void updateCheckPlayer() {
        final double checkLength = this.getBoundingBox().getSize() * 3;
        final Player player = this.level().getNearestPlayer(this, checkLength);
        if (this.isSleeping()) {
            if (player != null && !this.isOwnedBy(player) && !player.isCreative()) {
                this.setInSittingPose(false);
                this.setOrderedToSit(false);
                this.setTarget(player);
            }
        }
    }

    public boolean isDirectPathBetweenPoints(Vec3 vec1, Vec3 vec2) {
        final BlockHitResult rayTrace = this.level().clip(new ClipContext(vec1, new Vec3(vec2.x, vec2.y + (double) this.getBbHeight() * 0.5D, vec2.z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return rayTrace.getType() != HitResult.Type.BLOCK;
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);
        this.setHunger(this.getHunger() + FoodUtils.getFoodPoints(this));
    }

    @Override
    protected void removePassenger(@NotNull Entity passenger) {
        super.removePassenger(passenger);
        if (passenger instanceof Player player)
            this.onHearFlute(player);
    }

    @Override
    public void onHearFlute(Player player) {
        if (this.isTame() && this.isOwnedBy(player)) {
            if (this.isFlying() || this.isHovering()) {
                this.setFlying(false);
                this.setHovering(false);
            }
            this.navigation.stop();
        }
    }

    public abstract SoundEvent getRoarSound();

    public void roar() {
        if (GorgonEntity.isStoneMob(this) || this.isModelDead()) {
            return;
        }
        if (this.random.nextBoolean()) {
            if (this.getAnimation() != ANIMATION_EPIC_ROAR) {
                this.setAnimation(ANIMATION_EPIC_ROAR);
                this.playSound(this.getRoarSound(), this.getSoundVolume() + 3 + Math.max(0, this.getDragonStage() - 2), this.getVoicePitch() * 0.7F);
            }
            if (this.getDragonStage() > 3) {
                final int size = (this.getDragonStage() - 3) * 30;
                final List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(size, size, size));
                for (final Entity entity : entities) {
                    final boolean isStrongerDragon = entity instanceof DragonBaseEntity && ((DragonBaseEntity) entity).getDragonStage() >= this.getDragonStage();
                    if (entity instanceof LivingEntity living && !isStrongerDragon) {
                        if (this.isOwnedBy(living) || this.isOwnersPet(living))
                            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 50 * size));
                        else if (living.getItemBySlot(EquipmentSlot.HEAD).getItem() != IafItems.EARPLUGS.get())
                            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 50 * size));
                    }
                }
            }
        } else {
            if (this.getAnimation() != ANIMATION_ROAR) {
                this.setAnimation(ANIMATION_ROAR);
                this.playSound(this.getRoarSound(), this.getSoundVolume() + 2 + Math.max(0, this.getDragonStage() - 3), this.getVoicePitch());
            }
            if (this.getDragonStage() > 3) {
                final int size = (this.getDragonStage() - 3) * 30;
                final List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(size, size, size));
                for (final Entity entity : entities) {
                    final boolean isStrongerDragon = entity instanceof DragonBaseEntity && ((DragonBaseEntity) entity).getDragonStage() >= this.getDragonStage();
                    if (entity instanceof LivingEntity living && !isStrongerDragon)
                        if (this.isOwnedBy(living) || this.isOwnersPet(living))
                            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * size));
                        else
                            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30 * size));
                }
            }
        }
    }

    private boolean isOwnersPet(LivingEntity living) {
        return this.isTame() && this.getOwner() != null && living instanceof TamableAnimal && ((TamableAnimal) living).getOwner() != null && this.getOwner().is(((TamableAnimal) living).getOwner());
    }

    public boolean isDirectPathBetweenPoints(Entity entity, Vec3 vec1, Vec3 vec2) {

        HitResult movingobjectposition = this.level().clip(new ClipContext(vec1, vec2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return movingobjectposition.getType() != HitResult.Type.BLOCK;
    }

    public boolean shouldRenderEyes() {
        return !this.isSleeping() && !this.isModelDead() && !this.isBlinking() && !GorgonEntity.isStoneMob(this);
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return DragonUtils.canTameDragonAttack(this, entity);
    }

    public boolean isChained() {
        return ChainData.get(this).getChainedTo().isEmpty();
    }

    @Override
    protected void dropFromLootTable(@NotNull DamageSource damageSourceIn, boolean attackedRecently) {
    }

    public HitResult rayTraceRider(Entity rider, double blockReachDistance, float partialTicks) {
        Vec3 Vector3d = rider.getEyePosition(partialTicks);
        Vec3 Vector3d1 = rider.getViewVector(partialTicks);
        Vec3 Vector3d2 = Vector3d.add(Vector3d1.x * blockReachDistance, Vector3d1.y * blockReachDistance, Vector3d1.z * blockReachDistance);
        return this.level().clip(new ClipContext(Vector3d, Vector3d2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
    }

    /**
     * Provide rider position deltas for a dragon of certain size <br>
     * These methods use quadratic regression on data below to provide a smooth transition on the position <br>
     * Stage 3 / 50 Days / 1200000 Ticks / Render size 7.0 / Scale 2.45      | XZ 1.5    Y 0.0 <br>
     * Stage 4 / 75 Days / 1800000 Ticks / Render size 12.5 / Scale 4.375    | XZ 2.9    Y 1.6 <br>
     * Stage 5 / 100 Days / 2400000 Ticks / Render size 20.0 / Scale 7.0     | XZ 5.2    Y 3.8 <br>
     * Stage 5 / 125 Days / 3000000 Ticks / Render size 30.0 / Scale 7.0     | XZ 8.8    Y 7.4 <br>
     *
     * @return rider's vertical position delta, in blocks
     * @see DragonBaseEntity#getRideHorizontalBase()
     */
    protected float getRideHeightBase() {
        return 0.002237889819f * Mth.square(this.getRenderSize()) + 0.233137174f * this.getRenderSize() - 1.717904311f;
    }

    /**
     * Provide a rough horizontal distance of rider's hitbox
     *
     * @return rider's horizontal position delta, in blocks
     * @see DragonBaseEntity#getRideHeightBase()
     */
    protected float getRideHorizontalBase() {
        return 0.00336283f * Mth.square(this.getRenderSize()) + 0.1934242516f * this.getRenderSize() - 0.02622133882f;
    }

    public Vec3 getRiderPosition() {
        // The old position is seems to be given by a series compute of magic numbers
        // So I replace the number with an even more magical yet better one I tuned
        // The rider's hitbox and pov is now closer to its model, and less model clipping in first person
        // Todo: a better way of computing rider position, and a more dynamic one that changes according to dragon's animation

        float extraXZ = 0;
        float extraY = 0;

        // Extra delta when going up and down
        float pitchXZ = 0F;
        float pitchY = 0F;
        final float dragonPitch = this.getDragonPitch();
        if (dragonPitch > 0) {
            pitchXZ = Math.min(dragonPitch / 90, 0.2F);
            pitchY = -(dragonPitch / 90) * 0.6F;
        } else if (dragonPitch < 0) {//going up
            pitchXZ = Math.max(dragonPitch / 90, -0.5F);
            pitchY = (dragonPitch / 90) * 0.03F;
        }
//        float extraY = (pitchY + sitProg + hoverProg + deadProg + sleepProg + flyProg) * getRenderSize();
        extraXZ += pitchXZ * this.getRenderSize();
        extraY += pitchY * this.getRenderSize();

        // Extra delta when moving
        // The linear part of the tuning
        final float linearFactor = Mth.map(Math.max(this.getAgeInDays() - 50, 0), 0, 75, 0, 1);
        LivingEntity rider = this.getControllingPassenger();
        // Extra height when rider and the dragon look upwards, this will reduce model clipping
        if (rider != null && rider.getXRot() < 0) {
            extraY += (float) Mth.map(rider.getXRot(), 60, -40, -0.1, 0.1);
        }
        if (this.isHovering() || this.isFlying()) {
            // Extra height when flying, reduces model clipping since dragon has a bigger amplitude when flying/hovering
            extraY += 1.1f * linearFactor;
            extraY += this.getRideHeightBase() * 0.6f;
        } else {
            // Extra height when walking, reduces model clipping
            if (rider != null && rider.zza > 0) {
                final float MAX_RAISE_HEIGHT = 1.1f * linearFactor + this.getRideHeightBase() * 0.1f;
                this.riderWalkingExtraY = Math.min(MAX_RAISE_HEIGHT, this.riderWalkingExtraY + 0.1f);
            } else {
                this.riderWalkingExtraY = Math.max(0, this.riderWalkingExtraY - 0.15f);
            }
            extraY += this.riderWalkingExtraY;
        }

        final float xzMod = this.getRideHorizontalBase() + extraXZ;
//        final float xzMod = (0.15F + pitchXZ) * getRenderSize() + extraAgeScale;
        final float yMod = this.getRideHeightBase() + extraY;
        final float headPosX = (float) (this.getX() + xzMod * Mth.cos((float) ((this.getYRot() + 90) * Math.PI / 180)));
//        final float headPosY = (float) (getY() + (0.7F + sitProg + hoverProg + deadProg + sleepProg + flyProg + pitchY) * getRenderSize() * 0.3F + this.getScale() * 0.2F);
        final float headPosY = (float) (this.getY() + yMod);
        final float headPosZ = (float) (this.getZ() + xzMod * Mth.sin((float) ((this.getYRot() + 90) * Math.PI / 180)));
        return new Vec3(headPosX, headPosY, headPosZ);
    }

    @Override
    public @NotNull Vec3 getDismountLocationForPassenger(final LivingEntity passenger) {
        if (passenger.isInWall()) {
            return this.position().add(0, 1, 0);
        }

        return this.getRiderPosition().add(0, passenger.getBbHeight(), 0);
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
        this.setDeathStage(this.getAgeInDays() / 5);
        this.setModelDead(false);
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity entityIn) {
        // Workaround to make sure dragons won't be attacked when dead
        if (this.isModelDead())
            return true;
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity)
                return true;
            if (entityIn instanceof TamableAnimal entity)
                return entity.isOwnedBy(livingentity);
            if (livingentity != null)
                return livingentity.isAlliedTo(entityIn);
        }

        return super.isAlliedTo(entityIn);
    }

    public Vec3 getHeadPosition() {
        final float sitProg = this.sitProgress * 0.015F;
        final float deadProg = this.modelDeadProgress * -0.02F;
        final float hoverProg = this.hoverProgress * 0.03F;
        final float flyProg = this.flyProgress * 0.01F;
        int tick;
        if (this.getAnimationTick() < 10) {
            tick = this.getAnimationTick();
        } else if (this.getAnimationTick() > 50) {
            tick = 60 - this.getAnimationTick();
        } else {
            tick = 10;
        }
        final float epicRoarProg = this.getAnimation() == ANIMATION_EPIC_ROAR ? tick * 0.1F : 0;
        final float sleepProg = this.sleepProgress * -0.025F;
        float pitchMulti = 0F;
        float pitchAdjustment = 0F;
        float pitchMinus = 0F;
        final float dragonPitch = -this.getDragonPitch();
        if (this.isFlying() || this.isHovering()) {
            pitchMulti = Mth.sin((float) Math.toRadians(dragonPitch));
            pitchAdjustment = 1.2F;
            pitchMulti *= 2.1F * Math.abs(dragonPitch) / 90;
            if (pitchMulti > 0) {
                pitchMulti *= 1.5F - pitchMulti * 0.5F;
            }
            if (pitchMulti < 0) {
                pitchMulti *= 1.3F - pitchMulti * 0.1F;
            }
            pitchMinus = 0.3F * Math.abs(dragonPitch / 90);
            if (dragonPitch >= 0) {
                pitchAdjustment = 0.6F * Math.abs(dragonPitch / 90);
                pitchMinus = 0.95F * Math.abs(dragonPitch / 90);
            }
        }
        final float flightXz = 1.0F + flyProg + hoverProg;
        final float xzMod = (1.7F * this.getRenderSize() * 0.3F * flightXz) + this.getRenderSize() * (0.3F * Mth.sin((float) ((dragonPitch + 90) * Math.PI / 180)) * pitchAdjustment - pitchMinus - hoverProg * 0.45F);
        final float headPosX = (float) (this.getX() + (xzMod) * Mth.cos((float) ((this.getYRot() + 90) * Math.PI / 180)));
        final float headPosY = (float) (this.getY() + (0.7F + sitProg + hoverProg + deadProg + epicRoarProg + sleepProg + flyProg + pitchMulti) * this.getRenderSize() * 0.3F);
        final float headPosZ = (float) (this.getZ() + (xzMod) * Mth.sin((float) ((this.getYRot() + 90) * Math.PI / 180)));
        return new Vec3(headPosX, headPosY, headPosZ);
    }

    public final void breathAttack(double burnX, double burnY, double burnZ, boolean useCharge) {
        if (IafEvents.ON_DRAGON_FIRE_BLOCK.invoker().onFireBlock(this, burnX, burnY, burnZ)) return;
        if (useCharge) this.performChargeAttack(burnX, burnY, burnZ);
        else this.performNormalBreathAttack(burnX, burnY, burnZ);
    }

    protected void performNormalBreathAttack(double burnX, double burnY, double burnZ) {
        this.getNavigation().stop();
        this.burnParticleX = burnX;
        this.burnParticleY = burnY;
        this.burnParticleZ = burnZ;
        Vec3 headPos = this.getHeadPosition();
        double d2 = burnX - headPos.x;
        double d3 = burnY - headPos.y;
        double d4 = burnZ - headPos.z;
        double distance = Math.max(2.5F * Math.sqrt(this.distanceToSqr(burnX, burnY, burnZ)), 0);
        double conqueredDistance = this.burnProgress / 40D * distance;
        int increment = (int) Math.ceil(conqueredDistance / 100);
        int particleCount = this.getDragonStage() <= 3 ? 6 : 3;
        for (int i = 0; i < conqueredDistance; i += increment) {
            double progressX = headPos.x + d2 * i / distance;
            double progressY = headPos.y + d3 * i / distance;
            double progressZ = headPos.z + d4 * i / distance;
            if (this.canPositionBeSeen(progressX, progressY, progressZ)) {
                if (this.random.nextInt(particleCount) == 0) {
                    Vec3 velocity = new Vec3(progressX, progressY, progressZ).subtract(headPos);
                    if (this.level() instanceof ServerLevel serverWorld)
                        serverWorld.sendParticles(this.createBreathParticle(), headPos.x, headPos.y, headPos.z, 0, velocity.x, velocity.y, velocity.z, 1);
                }
            } else if (!this.level().isClientSide) {
                HitResult result = this.level().clip(new ClipContext(new Vec3(this.getX(), this.getY() + this.getEyeHeight(), this.getZ()), new Vec3(progressX, progressY, progressZ), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                Vec3 vec3 = result.getLocation();
                BlockPos pos = BlockPos.containing(vec3);
                IafDragonDestructionManager.destroyAreaBreath(this.level(), pos, this);
            }
        }
        if (this.burnProgress >= 40D && this.canPositionBeSeen(burnX, burnY, burnZ)) {
            double spawnX = burnX + (this.random.nextFloat() * 3.0) - 1.5;
            double spawnY = burnY + (this.random.nextFloat() * 3.0) - 1.5;
            double spawnZ = burnZ + (this.random.nextFloat() * 3.0) - 1.5;
            if (!this.level().isClientSide)
                IafDragonDestructionManager.destroyAreaBreath(this.level(), BlockPos.containing(spawnX, spawnY, spawnZ), this);
        }
    }

    protected void performChargeAttack(double burnX, double burnY, double burnZ) {
        if (this.getAnimation() != ANIMATION_FIRECHARGE)
            this.setAnimation(ANIMATION_FIRECHARGE);
        else if (this.getAnimationTick() == 20) {
            this.setYRot(this.yBodyRot);
            Vec3 headVec = this.getHeadPosition();
            double d2 = burnX - headVec.x;
            double d3 = burnY - headVec.y;
            double d4 = burnZ - headVec.z;
            float inaccuracy = 1.0F;
            d2 = d2 + this.random.nextGaussian() * 0.0075 * inaccuracy;
            d3 = d3 + this.random.nextGaussian() * 0.0075 * inaccuracy;
            d4 = d4 + this.random.nextGaussian() * 0.0075 * inaccuracy;
            this.playSound(IafSounds.FIREDRAGON_BREATH.get(), 4, 1);
            Entity charge = this.createCharge(d2, d3, d4);
            charge.setPos(headVec.x, headVec.y, headVec.z);
            if (!this.level().isClientSide) this.level().addFreshEntity(charge);
            this.randomizeAttacks();
        }
    }

    public abstract Entity createCharge(double velocityX, double velocityY, double velocityZ);

    public abstract ParticleOptions createBreathParticle();

    public void randomizeAttacks() {
        this.airAttack = IafDragonAttacks.Air.values()[this.getRandom().nextInt(IafDragonAttacks.Air.values().length)];
        this.groundAttack = IafDragonAttacks.Ground.values()[this.getRandom().nextInt(IafDragonAttacks.Ground.values().length)];
    }

    @Override
    public boolean shouldBlockExplode(@NotNull Explosion explosionIn, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, BlockState blockStateIn, float explosionPower) {
        return !(blockStateIn.getBlock() instanceof DragonProof) && DragonUtils.canDragonBreak(blockStateIn, this);
    }

    public void tryScorchTarget() {
        LivingEntity entity = this.getTarget();
        if (entity != null) {
            final float distX = (float) (entity.getX() - this.getX());
            final float distZ = (float) (entity.getZ() - this.getZ());
            if (this.isBreathingFire()) {
                if (this.isActuallyBreathingFire()) {
                    this.setYRot(this.yBodyRot);
                    if (this.tickCount % 5 == 0)
                        this.playSound(IafSounds.FIREDRAGON_BREATH.get(), 4, 1);
                    int breathTicks = Mth.clamp(this.fireBreathTicks, 0, 40);
                    this.breathAttack(this.getX() + distX * breathTicks / 40, entity.getY(), this.getZ() + distZ * breathTicks / 40, false);
                }
            } else {
                this.setBreathingFire(true);
            }
        }
    }

    @Override
    public void setTarget(LivingEntity LivingEntityIn) {
        super.setTarget(LivingEntityIn);
        this.flightManager.onSetAttackTarget(LivingEntityIn);
    }

    @Override
    public boolean wantsToAttack(@NotNull LivingEntity target, @NotNull LivingEntity owner) {
        if (this.isTame() && target instanceof TamableAnimal tamableTarget) {
            UUID targetOwner = tamableTarget.getOwnerUUID();
            if (targetOwner != null && targetOwner.equals(this.getOwnerUUID())) {
                return false;
            }
        }
        return super.wantsToAttack(target, owner);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        return super.canAttack(target) && DragonUtils.isAlive(target);
    }

    public boolean isPart(Entity entityHit) {
        return this.headPart != null && this.headPart.is(entityHit) || this.neckPart != null && this.neckPart.is(entityHit) ||
                this.leftWingLowerPart != null && this.leftWingLowerPart.is(entityHit) || this.rightWingLowerPart != null && this.rightWingLowerPart.is(entityHit) ||
                this.leftWingUpperPart != null && this.leftWingUpperPart.is(entityHit) || this.rightWingUpperPart != null && this.rightWingUpperPart.is(entityHit) ||
                this.tail1Part != null && this.tail1Part.is(entityHit) || this.tail2Part != null && this.tail2Part.is(entityHit) ||
                this.tail3Part != null && this.tail3Part.is(entityHit) || this.tail4Part != null && this.tail4Part.is(entityHit);
    }

    @Override
    public double getFlightSpeedModifier() {
        return IafCommonConfig.INSTANCE.dragon.dragonFlightSpeedMod.getValue();
    }

    public boolean isAllowedToTriggerFlight() {
        return (this.hasFlightClearance() && this.onGround() || this.isInWater()) && !this.isOrderedToSit() && this.getPassengers().isEmpty() && !this.isBaby() && !this.isSleeping() && this.canMove();
    }

    public BlockPos getEscortPosition() {
        return this.getOwner() != null ? new BlockPos(this.getOwner().blockPosition()) : this.blockPosition();
    }

    public boolean shouldTPtoOwner() {
        return this.getOwner() != null && this.distanceTo(this.getOwner()) > 10;
    }

    public boolean isSkeletal() {
        return this.getDeathStage() >= (this.getAgeInDays() / 5) / 2;
    }

    @Override
    public boolean save(@NotNull CompoundTag compound) {
        return this.saveAsPassenger(compound);
    }

    @Override
    public void playSound(@NotNull SoundEvent soundIn, float volume, float pitch) {
        if (soundIn == SoundEvents.GENERIC_EAT || soundIn == this.getAmbientSound() || soundIn == this.getHurtSound(this.level().damageSources().generic()) || soundIn == this.getDeathSound() || soundIn == this.getRoarSound()) {
            if (!this.isSilent() && this.headPart != null) {
                this.level().playSound(null, this.headPart.getX(), this.headPart.getY(), this.headPart.getZ(), soundIn, this.getSoundSource(), volume, pitch);
            }
        } else {
            super.playSound(soundIn, volume, pitch);
        }
    }

    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    public boolean hasFlightClearance() {
        BlockPos topOfBB = BlockPos.containing(this.getBlockX(), this.getBoundingBox().maxY, this.getBlockZ());
        for (int i = 1; i < 4; i++) {
            if (!this.level().isEmptyBlock(topOfBB.above(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItemBySlot(final EquipmentSlot slotIn) {
        return switch (slotIn) {
            case OFFHAND -> this.dragonInventory.getItem(0);
            case HEAD -> this.dragonInventory.getItem(1);
            case CHEST -> this.dragonInventory.getItem(2);
            case LEGS -> this.dragonInventory.getItem(3);
            case FEET -> this.dragonInventory.getItem(4);
            default -> super.getItemBySlot(slotIn);
        };
    }

    @Override
    public void setItemSlot(final EquipmentSlot slotIn, final @NotNull ItemStack stack) {
        switch (slotIn) {
            case OFFHAND -> this.dragonInventory.setItem(0, stack);
            case HEAD -> this.dragonInventory.setItem(1, stack);
            case CHEST -> this.dragonInventory.setItem(2, stack);
            case LEGS -> this.dragonInventory.setItem(3, stack);
            case FEET -> this.dragonInventory.setItem(4, stack);
            default -> {
                super.getItemBySlot(slotIn);
                return;
            }
        }
        this.dragonInventory.setChanged();
    }

    public SoundEvent getBabyFireSound() {
        return SoundEvents.FIRE_EXTINGUISH;
    }

    public boolean isPlayingAttackAnimation() {
        return this.getAnimation() == ANIMATION_BITE || this.getAnimation() == ANIMATION_SHAKEPREY || this.getAnimation() == ANIMATION_WINGBLAST ||
                this.getAnimation() == ANIMATION_TAILWHACK;
    }

    protected IafDragonLogic createDragonLogic() {
        return new IafDragonLogic(this);
    }

    public int getFlightChancePerTick() {
        return FLIGHT_CHANCE_PER_TICK;
    }

    @Override
    public void onClientRemoval() {
        if (IafCommonConfig.INSTANCE.dragon.chunkLoadSummonCrystal.getValue()) {
            if (this.isBoundToCrystal()) {
                DragonPosWorldData data = DragonPosWorldData.get(this.level());
                if (data != null)
                    data.addDragon(this.getUUID(), this.blockPosition());
            }
        }
        super.onClientRemoval();
    }

    @Override
    public int maxSearchNodes() {
        return (int) this.getAttribute(Attributes.FOLLOW_RANGE).getValue();
    }

    @Override
    public boolean isSmallerThanBlock() {
        return false;
    }

    @Override
    public float getXZNavSize() {
        return Math.max(1.4F, this.getBbWidth() / 2.0F);
    }

    @Override
    public int getYNavSize() {
        return Mth.ceil(this.getBbHeight());
    }

    @Override
    public void containerChanged(@NotNull Container invBasic) {
        if (!this.level().isClientSide) {
            this.refreshDirtyAttributes();
        }
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    @Override // TODO :: Block collision performance impact (due to the multi-part entity)?
    public @NotNull Vec3 handleRelativeFrictionAndCalculateMovement(@NotNull Vec3 pDeltaMovement, float pFriction) {
        if (this.moveControl instanceof IafDragonFlightManager.PlayerFlightMoveHelper)
            return pDeltaMovement;
        return super.handleRelativeFrictionAndCalculateMovement(pDeltaMovement, pFriction);
    }

    @Override
    public boolean isBlockExplicitlyPassable(BlockState state, BlockPos pos, BlockPos entityPos) {
        return false;
    }

    @Override
    public boolean isBlockExplicitlyNotPassable(BlockState state, BlockPos pos, BlockPos entityPos) {
        return false;
    }
}
