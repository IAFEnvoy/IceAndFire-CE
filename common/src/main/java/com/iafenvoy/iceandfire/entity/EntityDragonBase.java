package com.iafenvoy.iceandfire.entity;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.StaticVariables;
import com.iafenvoy.iceandfire.api.IafEvents;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonArmor;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.iceandfire.entity.ai.*;
import com.iafenvoy.iceandfire.entity.block.BlockEntityDragonForgeInput;
import com.iafenvoy.iceandfire.entity.util.*;
import com.iafenvoy.iceandfire.entity.util.dragon.*;
import com.iafenvoy.iceandfire.item.ItemSummoningCrystal;
import com.iafenvoy.iceandfire.item.block.util.IDragonProof;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.iafenvoy.iceandfire.render.model.IFChainBuffer;
import com.iafenvoy.iceandfire.render.model.util.LegSolverQuadruped;
import com.iafenvoy.iceandfire.screen.handler.DragonScreenHandler;
import com.iafenvoy.iceandfire.world.DragonPosWorldData;
import com.iafenvoy.uranus.ServerHelper;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.data.EntityPropertyDelegate;
import com.iafenvoy.uranus.network.PacketBufferUtils;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.AdvancedPathNavigate;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.IPassabilityNavigator;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.PathingStuckHandler;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.pathjobs.ICustomSizeNavigator;
import com.iafenvoy.uranus.object.item.FoodUtils;
import dev.architectury.networking.NetworkManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class EntityDragonBase extends TameableEntity implements NamedScreenHandlerFactory, IPassabilityNavigator, ISyncMount, IFlyingMount, IMultipartEntity, IAnimatedEntity, IDragonFlute, IDeadMob, IVillagerFear, IAnimalFear, IDropArmor, IHasCustomizableAttributes, ICustomSizeNavigator, ICustomMoveController, InventoryChangedListener {
    public static final int FLIGHT_CHANCE_PER_TICK = 1500;
    public static final float[] growth_stage_1 = new float[]{1F, 3F};
    public static final float[] growth_stage_2 = new float[]{3F, 7F};
    public static final float[] growth_stage_3 = new float[]{7F, 12.5F};
    public static final float[] growth_stage_4 = new float[]{12.5F, 20F};
    public static final float[] growth_stage_5 = new float[]{20F, 30F};
    protected static final TrackedData<Boolean> SWIMMING = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final TrackedData<Integer> HUNGER = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> AGE_TICKS = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> GENDER = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> VARIANT = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FIREBREATHING = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HOVERING = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FLYING = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> MODEL_DEAD = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> DEATH_STAGE = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> CONTROL_STATE = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> TACKLE = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> AGINGDISABLED = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> COMMAND = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> DRAGON_PITCH = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> CRYSTAL_BOUND = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> CUSTOM_POSE = DataTracker.registerData(EntityDragonBase.class, TrackedDataHandlerRegistry.STRING);
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
    public final float[][] growth_stages = new float[][]{growth_stage_1, growth_stage_2, growth_stage_3, growth_stage_4, growth_stage_5};
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
    public SimpleInventory dragonInventory;
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
    private EntityDragonPart headPart;
    private EntityDragonPart neckPart;
    private EntityDragonPart rightWingUpperPart;
    private EntityDragonPart rightWingLowerPart;
    private EntityDragonPart leftWingUpperPart;
    private EntityDragonPart leftWingLowerPart;
    private EntityDragonPart tail1Part;
    private EntityDragonPart tail2Part;
    private EntityDragonPart tail3Part;
    private EntityDragonPart tail4Part;
    private boolean isOverAir;

    public EntityDragonBase(EntityType<? extends EntityDragonBase> t, World world, DragonType type, double minimumDamage, double maximumDamage, double minimumHealth, double maximumHealth, double minimumSpeed, double maximumSpeed) {
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
        if (world.isClient) {
            this.roll_buffer = new IFChainBuffer();
            this.pitch_buffer = new IFChainBuffer();
            this.pitch_buffer_body = new IFChainBuffer();
            this.turn_buffer = new ReversedBuffer();
            this.tail_buffer = new ChainBuffer();
        }
        this.legSolver = new LegSolverQuadruped(0.3F, 0.35F, 0.2F, 1.45F, 1.0F);
        this.flightManager = new IafDragonFlightManager(this);
        this.logic = this.createDragonLogic();
        this.ignoreCameraFrustum = true;
        this.switchNavigator(0);
        this.randomizeAttacks();
        this.lastScale = 0;//Ensure scale will be updated so that multipart can generate correctly
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1)
                //FOLLOW RANGE
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, Math.min(2048, IafCommonConfig.INSTANCE.dragon.targetSearchLength.getValue()))
                //ARMOR
                .add(EntityAttributes.GENERIC_ARMOR, 4);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(Math.min(2048, IafCommonConfig.INSTANCE.dragon.targetSearchLength.getValue()));
    }

    @Override
    public BlockPos getPositionTarget() {
        return this.homePos == null ? super.getPositionTarget() : this.homePos.getPosition();
    }

    @Override
    public float getPositionTargetRange() {
        return IafCommonConfig.INSTANCE.dragon.wanderFromHomeDistance.getValue();
    }

    public String getHomeDimensionName() {
        return this.homePos == null ? "" : this.homePos.getDimension();
    }

    @Override
    public boolean hasPositionTarget() {
        return this.hasHomePosition &&
                this.getHomeDimensionName().equals(DragonUtils.getDimensionName(this.getWorld()))
                || super.hasPositionTarget();
    }

    @Override
    protected void initGoals() {
//        this.goalSelector.addGoal(0, new DragonAIRide<>(this));
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new DragonAIMate(this, 1.0D));
        this.goalSelector.add(3, new DragonAIReturnToRoost(this, 1.0D));
        this.goalSelector.add(4, new DragonAIEscort(this, 1.0D));
        this.goalSelector.add(5, new DragonAIAttackMelee(this, 1.5D, false));
        this.goalSelector.add(6, new TemptGoal(this, 1.0D, Ingredient.fromTag(IafItemTags.TEMPT_DRAGON), false));
        this.goalSelector.add(7, new DragonAIWander(this, 1.0D));
        this.goalSelector.add(8, new DragonAIWatchClosest(this, LivingEntity.class, 6.0F));
        this.goalSelector.add(8, new DragonAILookIdle(this));
        this.targetSelector.add(1, new AttackWithOwnerGoal(this));
        this.targetSelector.add(2, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this));
        this.targetSelector.add(4, new DragonAITargetItems<>(this, 60, false, false, true));
        this.targetSelector.add(5, new DragonAITargetNonTamed<>(this, LivingEntity.class, false, (Predicate<LivingEntity>) entity -> {
            if (entity instanceof PlayerEntity player) return !player.isCreative();
            if (this.getRandom().nextInt(100) > this.getHunger())
                return entity.getType() != this.getType() && DragonUtils.canHostilesTarget(entity) && DragonUtils.isAlive(entity) && this.shouldTarget(entity);
            return false;
        }));
        this.targetSelector.add(6, new DragonAITarget<>(this, LivingEntity.class, true, (Predicate<LivingEntity>) entity -> DragonUtils.canHostilesTarget(entity) && entity.getType() != this.getType() && this.shouldTarget(entity) && DragonUtils.isAlive(entity)));
        this.targetSelector.add(7, new DragonAITargetItems<>(this, false));
    }

    protected abstract boolean shouldTarget(Entity entity);

    public void updateScale(float scale) {
        if (this.headPart == null || this.headPart.isRemoved()) {
            this.headPart = new EntityDragonPart(this, 1.55F, 0, 0.6F, 0.5F, 0.35F, 1.5F);
            this.getWorld().spawnEntity(this.headPart);
        }
        this.headPart.updateScale(scale);

        if (this.neckPart == null || this.neckPart.isRemoved()) {
            this.neckPart = new EntityDragonPart(this, 0.85F, 0, 0.7F, 0.5F, 0.2F, 1);
            this.getWorld().spawnEntity(this.neckPart);
        }
        this.neckPart.updateScale(scale);

        if (this.rightWingUpperPart == null || this.rightWingUpperPart.isRemoved()) {
            this.rightWingUpperPart = new EntityDragonPart(this, 1, 90, 0.5F, 0.85F, 0.3F, 0.5F);
            this.getWorld().spawnEntity(this.rightWingUpperPart);
        }
        this.rightWingUpperPart.updateScale(scale);

        if (this.rightWingLowerPart == null || this.rightWingLowerPart.isRemoved()) {
            this.rightWingLowerPart = new EntityDragonPart(this, 1.4F, 100, 0.3F, 0.85F, 0.2F, 0.5F);
            this.getWorld().spawnEntity(this.rightWingLowerPart);
        }
        this.rightWingLowerPart.updateScale(scale);

        if (this.leftWingUpperPart == null || this.leftWingUpperPart.isRemoved()) {
            this.leftWingUpperPart = new EntityDragonPart(this, 1, -90, 0.5F, 0.85F, 0.3F, 0.5F);
            this.getWorld().spawnEntity(this.leftWingUpperPart);
        }
        this.leftWingUpperPart.updateScale(scale);

        if (this.leftWingLowerPart == null || this.leftWingLowerPart.isRemoved()) {
            this.leftWingLowerPart = new EntityDragonPart(this, 1.4F, -100, 0.3F, 0.85F, 0.2F, 0.5F);
            this.getWorld().spawnEntity(this.leftWingLowerPart);
        }
        this.leftWingLowerPart.updateScale(scale);

        if (this.tail1Part == null || this.tail1Part.isRemoved()) {
            this.tail1Part = new EntityDragonPart(this, -0.75F, 0, 0.6F, 0.35F, 0.35F, 1);
            this.getWorld().spawnEntity(this.tail1Part);
        }
        this.tail1Part.updateScale(scale);

        if (this.tail2Part == null || this.tail2Part.isRemoved()) {
            this.tail2Part = new EntityDragonPart(this, -1.15F, 0, 0.45F, 0.35F, 0.35F, 1);
            this.getWorld().spawnEntity(this.tail2Part);
        }
        this.tail2Part.updateScale(scale);

        if (this.tail3Part == null || this.tail3Part.isRemoved()) {
            this.tail3Part = new EntityDragonPart(this, -1.5F, 0, 0.35F, 0.35F, 0.35F, 1);
            this.getWorld().spawnEntity(this.tail3Part);
        }
        this.tail3Part.updateScale(scale);

        if (this.tail4Part == null || this.tail4Part.isRemoved()) {
            this.tail4Part = new EntityDragonPart(this, -1.95F, 0, 0.25F, 0.45F, 0.3F, 1.5F);
            this.getWorld().spawnEntity(this.tail4Part);
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
        this.headPart.copyPositionAndRotation(this);
        this.neckPart.copyPositionAndRotation(this);
        this.rightWingUpperPart.copyPositionAndRotation(this);
        this.rightWingLowerPart.copyPositionAndRotation(this);
        this.leftWingUpperPart.copyPositionAndRotation(this);
        this.leftWingLowerPart.copyPositionAndRotation(this);
        this.tail1Part.copyPositionAndRotation(this);
        this.tail2Part.copyPositionAndRotation(this);
        this.tail3Part.copyPositionAndRotation(this);
        this.tail4Part.copyPositionAndRotation(this);

        EntityUtil.updatePart(this.headPart, this);
        EntityUtil.updatePart(this.neckPart, this);
        EntityUtil.updatePart(this.rightWingUpperPart, this);
        EntityUtil.updatePart(this.rightWingLowerPart, this);
        EntityUtil.updatePart(this.leftWingUpperPart, this);
        EntityUtil.updatePart(this.leftWingLowerPart, this);
        EntityUtil.updatePart(this.tail1Part, this);
        EntityUtil.updatePart(this.tail2Part, this);
        EntityUtil.updatePart(this.tail3Part, this);
        EntityUtil.updatePart(this.tail4Part, this);
    }

    public void updateBurnTarget() {
        if (this.burningTarget != null && !this.isSleeping() && !this.isModelDead() && !this.isBaby()) {
            float maxDist = 115 * this.getDragonStage();
            if (this.getWorld().getBlockEntity(this.burningTarget) instanceof BlockEntityDragonForgeInput forge && forge.isAssembled() && this.squaredDistanceTo(this.burningTarget.getX() + 0.5D, this.burningTarget.getY() + 0.5D, this.burningTarget.getZ() + 0.5D) < maxDist && this.canPositionBeSeen(this.burningTarget.getX() + 0.5D, this.burningTarget.getY() + 0.5D, this.burningTarget.getZ() + 0.5D)) {
                this.getLookControl().lookAt(this.burningTarget.getX() + 0.5D, this.burningTarget.getY() + 0.5D, this.burningTarget.getZ() + 0.5D, 180F, 180F);
                this.breathFireAtPos(this.burningTarget);
                this.setBreathingFire(true);
            } else {
                if (!this.getWorld().isClient) {
                    PacketByteBuf buf = PacketBufferUtils.create();
                    buf.writeInt(this.getId()).writeBoolean(true);
                    buf.writeBlockPos(this.burningTarget);
                    ServerHelper.sendToAll(StaticVariables.DRAGON_SET_BURN_BLOCK, buf);
                }
                this.burningTarget = null;
            }
        }
    }

    protected abstract void breathFireAtPos(BlockPos burningTarget);

    protected PathingStuckHandler createStuckHandler() {
        return PathingStuckHandler.createStuckHandler();
    }

    @Override
    protected EntityNavigation createNavigation(World worldIn) {
        return this.createNavigator(worldIn, AdvancedPathNavigate.MovementType.WALKING);
    }

    protected EntityNavigation createNavigator(World worldIn, AdvancedPathNavigate.MovementType type) {
        return this.createNavigator(worldIn, type, this.createStuckHandler());
    }

    protected EntityNavigation createNavigator(World worldIn, AdvancedPathNavigate.MovementType type, PathingStuckHandler stuckHandler) {
        return this.createNavigator(worldIn, type, stuckHandler, 4f);
    }

    protected EntityNavigation createNavigator(World worldIn, AdvancedPathNavigate.MovementType type, PathingStuckHandler stuckHandler, float width) {
        AdvancedPathNavigate newNavigator = new AdvancedPathNavigate(this, this.getWorld(), type, width, (float) 4.0);
        this.navigation = newNavigator;
        newNavigator.setCanSwim(true);
        newNavigator.getNodeMaker().setCanOpenDoors(true);
        return newNavigator;
    }

    public void switchNavigator(int navigatorType) {
        if (navigatorType == 0) {
            this.moveControl = new IafDragonFlightManager.GroundMoveHelper(this);
            this.navigation = this.createNavigator(this.getWorld(), AdvancedPathNavigate.MovementType.WALKING, this.createStuckHandler().withTeleportSteps(5));
            this.navigatorType = 0;
            this.setFlying(false);
            this.setHovering(false);
        } else if (navigatorType == 1) {
            this.moveControl = new IafDragonFlightManager.FlightMoveHelper(this);
            this.navigation = this.createNavigator(this.getWorld(), AdvancedPathNavigate.MovementType.FLYING);
            this.navigatorType = 1;
        } else {
            this.moveControl = new IafDragonFlightManager.PlayerFlightMoveHelper<>(this);
            this.navigation = this.createNavigator(this.getWorld(), AdvancedPathNavigate.MovementType.FLYING);
            this.navigatorType = 2;
        }
    }

    @Override
    public boolean canStartRiding(Entity rider) {
        return true;
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        this.breakBlocks(false);
    }

    @Override
    public void checkDespawn() {
        if (IafCommonConfig.INSTANCE.dragon.canDespawn.getValue()) {
            super.checkDespawn();
        }
    }

    public boolean canDestroyBlock(BlockPos pos, BlockState state) {
        return state.getBlock().getHardness() <= 100;
    }

    @Override
    public boolean isMobDead() {
        return this.isModelDead();
    }

    @Override
    public int getMaxHeadRotation() {
        return 30 * this.getDragonStage() / 5;
    }

    public void openInventory(PlayerEntity player) {
        player.openHandledScreen(this);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new DragonScreenHandler(syncId, this.dragonInventory, player.getInventory(), new EntityPropertyDelegate(this.getId()));
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 90;
    }

    @Override
    protected void updatePostDeath() {
        this.deathTime = 0;
        this.setModelDead(true);
        this.removeAllPassengers();
        if (this.getDeathStage() >= this.getAgeInDays() / 5) {
            this.remove(RemovalReason.KILLED);
            for (int k = 0; k < 40; ++k) {
                double d2 = this.random.nextGaussian() * 0.02D;
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                if (this.getWorld().isClient) {
                    this.getWorld().addParticle(ParticleTypes.CLOUD, this.getX() + this.random.nextFloat() * this.getWidth() * 2.0F - this.getWidth(), this.getY() + this.random.nextFloat() * this.getHeight(), this.getZ() + this.random.nextFloat() * this.getWidth() * 2.0F - this.getWidth(), d2, d0, d1);
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
    public void remove(RemovalReason reason) {
        this.removeParts();
        super.remove(reason);
    }

    @Override
    public int getXpToDrop() {
        return switch (this.getDragonStage()) {
            case 2 -> 20;
            case 3 -> 150;
            case 4 -> 300;
            case 5 -> 650;
            default -> 5;
        };
    }

    @Override
    public boolean isAiDisabled() {
        return this.isModelDead() || super.isAiDisabled();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HUNGER, 0);
        this.dataTracker.startTracking(AGE_TICKS, 0);
        this.dataTracker.startTracking(GENDER, false);
        this.dataTracker.startTracking(VARIANT, DragonColor.RED.name());
        this.dataTracker.startTracking(SLEEPING, false);
        this.dataTracker.startTracking(FIREBREATHING, false);
        this.dataTracker.startTracking(HOVERING, false);
        this.dataTracker.startTracking(FLYING, false);
        this.dataTracker.startTracking(DEATH_STAGE, 0);
        this.dataTracker.startTracking(MODEL_DEAD, false);
        this.dataTracker.startTracking(CONTROL_STATE, (byte) 0);
        this.dataTracker.startTracking(TACKLE, false);
        this.dataTracker.startTracking(AGINGDISABLED, false);
        this.dataTracker.startTracking(COMMAND, 0);
        this.dataTracker.startTracking(DRAGON_PITCH, 0F);
        this.dataTracker.startTracking(CRYSTAL_BOUND, false);
        this.dataTracker.startTracking(CUSTOM_POSE, "");
    }

    @Override
    public boolean isGoingUp() {
        return (this.dataTracker.get(CONTROL_STATE) & 1) == 1;
    }

    @Override
    public boolean isGoingDown() {
        return (this.dataTracker.get(CONTROL_STATE) >> 1 & 1) == 1;
    }

    @Override
    public boolean isAttacking() {
        return (this.dataTracker.get(CONTROL_STATE) >> 2 & 1) == 1;
    }

    public boolean isStriking() {
        return (this.dataTracker.get(CONTROL_STATE) >> 3 & 1) == 1;
    }

    public boolean isDismounting() {
        return (this.dataTracker.get(CONTROL_STATE) >> 4 & 1) == 1;
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
        byte prevState = this.dataTracker.get(CONTROL_STATE);
        if (newState) {
            this.dataTracker.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        } else {
            this.dataTracker.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
        }
    }

    @Override
    public byte getControlState() {
        return this.dataTracker.get(CONTROL_STATE);
    }

    @Override
    public void setControlState(byte state) {
        this.dataTracker.set(CONTROL_STATE, state);
    }

    public int getCommand() {
        return this.dataTracker.get(COMMAND);
    }

    public void setCommand(int command) {
        this.dataTracker.set(COMMAND, command);
        this.setSitting(command == 1);
    }

    public float getDragonPitch() {
        return this.dataTracker.get(DRAGON_PITCH);
    }

    public void setDragonPitch(float pitch) {
        this.dataTracker.set(DRAGON_PITCH, pitch);
    }

    public void incrementDragonPitch(float pitch) {
        this.dataTracker.set(DRAGON_PITCH, this.getDragonPitch() + pitch);
    }

    public void decrementDragonPitch(float pitch) {
        this.dataTracker.set(DRAGON_PITCH, this.getDragonPitch() - pitch);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putInt("Hunger", this.getHunger());
        compound.putInt("AgeTicks", this.getAgeInTicks());
        compound.putBoolean("Gender", this.isMale());
        compound.putString("Variant", this.getVariant());
        compound.putBoolean("Sleeping", this.isSleeping());
        compound.putBoolean("TamedDragon", this.isTamed());
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
        if (this.homePos != null && this.hasHomePosition) {
            this.homePos.write(compound);
        }
        compound.putBoolean("AgingDisabled", this.isAgingDisabled());
        compound.putInt("Command", this.getCommand());
        if (this.dragonInventory != null) {
            NbtList nbttaglist = new NbtList();
            for (int i = 0; i < this.dragonInventory.size(); ++i) {
                ItemStack itemstack = this.dragonInventory.getStack(i);
                if (!itemstack.isEmpty()) {
                    NbtCompound CompoundNBT = new NbtCompound();
                    CompoundNBT.putByte("Slot", (byte) i);
                    itemstack.writeNbt(CompoundNBT);
                    nbttaglist.add(CompoundNBT);
                }
            }
            compound.put("Items", nbttaglist);
        }
        compound.putBoolean("CrystalBound", this.isBoundToCrystal());
        if (this.hasCustomName()) {
            compound.putString("CustomName", Text.Serializer.toJson(this.getCustomName()));
        }
        this.removeParts();
        this.lastScale = 0;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setHunger(compound.getInt("Hunger"));
        this.setAgeInTicks(compound.getInt("AgeTicks"));
        this.setGender(compound.getBoolean("Gender"));
        //FIXME: Compat for old version should be removed in 0.7
        if (compound.contains("Variant") && compound.get("Variant").getType() == NbtElement.STRING_TYPE)
            this.setVariant(compound.getString("Variant"));
        else {
            List<DragonColor> colors = DragonColor.getColorsByType(this.dragonType);
            this.setVariant(colors.get(compound.getInt("Variant")).name());
        }
        this.setInSittingPose(compound.getBoolean("Sleeping"));
        this.setTamed(compound.getBoolean("TamedDragon"));
        this.setBreathingFire(compound.getBoolean("FireBreathing"));
        this.usingGroundAttack = compound.getBoolean("AttackDecision");
        this.setHovering(compound.getBoolean("Hovering"));
        this.setFlying(compound.getBoolean("Flying"));
        this.setDeathStage(compound.getInt("DeathStage"));
        this.setModelDead(compound.getBoolean("ModelDead"));
        this.modelDeadProgress = compound.getFloat("DeadProg");
        this.setCustomPose(compound.getString("CustomPose"));
        this.hasHomePosition = compound.getBoolean("HasHomePosition");
        if (this.hasHomePosition && compound.getInt("HomeAreaX") != 0 && compound.getInt("HomeAreaY") != 0 && compound.getInt("HomeAreaZ") != 0) {
            this.homePos = new HomePosition(compound, this.getWorld());
        }
        this.setTackling(compound.getBoolean("Tackle"));
        this.setAgingDisabled(compound.getBoolean("AgingDisabled"));
        this.setCommand(compound.getInt("Command"));
        if (this.dragonInventory != null) {
            NbtList nbttaglist = compound.getList("Items", 10);
            this.createInventory();
            for (NbtElement inbt : nbttaglist) {
                NbtCompound CompoundNBT = (NbtCompound) inbt;
                int j = CompoundNBT.getByte("Slot") & 255;
                if (j <= 4) {
                    this.dragonInventory.setStack(j, ItemStack.fromNbt(CompoundNBT));
                }
            }
        } else {
            NbtList nbttaglist = compound.getList("Items", 10);
            this.createInventory();
            for (NbtElement inbt : nbttaglist) {
                NbtCompound CompoundNBT = (NbtCompound) inbt;
                int j = CompoundNBT.getByte("Slot") & 255;
                this.dragonInventory.setStack(j, ItemStack.fromNbt(CompoundNBT));
            }
        }
        this.setCrystalBound(compound.getBoolean("CrystalBound"));
        if (compound.contains("CustomName", 8) && !compound.getString("CustomName").startsWith("TextComponent")) {
            this.setCustomName(Text.Serializer.fromJson(compound.getString("CustomName")));
        }

        this.setConfigurableAttributes();

        this.updateAttributes();
    }

    public int getContainerSize() {
        return 5;
    }

    protected void createInventory() {
        SimpleInventory tempInventory = this.dragonInventory;
        this.dragonInventory = new SimpleInventory(this.getContainerSize());
        if (tempInventory != null) {
            tempInventory.removeListener(this);
            int i = Math.min(tempInventory.size(), this.dragonInventory.size());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = tempInventory.getStack(j);
                if (!itemstack.isEmpty())
                    this.dragonInventory.setStack(j, itemstack.copy());
            }
        }

        this.dragonInventory.addListener(this);
        this.updateContainerEquipment();
    }

    protected void updateContainerEquipment() {
        if (!this.getWorld().isClient)
            this.updateAttributes();
    }

    public boolean hasInventoryChanged(Inventory pInventory) {
        return this.dragonInventory != pInventory;
    }

    @Override // TODO :: This only returns player - is that correct?
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengerList())
            if (passenger instanceof PlayerEntity player && this.getTarget() != passenger)
                if (this.isTamed() && this.getOwnerUuid() != null && this.getOwnerUuid().equals(player.getUuid()))
                    return player;
        return null;
    }

    @Override
    public PlayerEntity getRidingPlayer() {
        if (this.getControllingPassenger() instanceof PlayerEntity player)
            return player;
        return null;
    }

    public void updateAttributes() {
        double age = Math.min(this.getAgeInDays(), 125);
        final double healthStep = (this.maximumHealth - this.minimumHealth) / 125F;
        final double attackStep = (this.maximumDamage - this.minimumDamage) / 125F;
        final double speedStep = (this.maximumSpeed - this.minimumSpeed) / 125F;
        final double armorStep = (this.maximumArmor - this.minimumArmor) / 125F;

        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.round(this.minimumHealth + (healthStep * age)));
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.round(this.minimumDamage + (attackStep * age)));
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this.minimumSpeed + (speedStep * age));
        final double baseValue = this.minimumArmor + (armorStep * this.getAgeInDays());
        this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(baseValue);
        if (!this.getWorld().isClient) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(new EntityAttributeModifier(ARMOR_MODIFIER_UUID, "Dragon armor bonus", this.calculateArmorModifier(), EntityAttributeModifier.Operation.ADDITION));
        }
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(Math.min(2048, IafCommonConfig.INSTANCE.dragon.targetSearchLength.getValue()));
    }

    public int getHunger() {
        return this.dataTracker.get(HUNGER);
    }

    public void setHunger(int hunger) {
        this.dataTracker.set(HUNGER, MathHelper.clamp(hunger, 0, 100));
    }

    public String getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public void setVariant(String variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public int getAgeInDays() {
        return MathHelper.clamp(this.dataTracker.get(AGE_TICKS) / 24000, 0, this.isTamed() ? IafCommonConfig.INSTANCE.dragon.maxTamedDragonAge.getValue() : 128);
    }

    public void setAgeInDays(int age) {
        this.dataTracker.set(AGE_TICKS, age * 24000);
    }

    public int getAgeInTicks() {
        return this.dataTracker.get(AGE_TICKS);
    }

    public void setAgeInTicks(int age) {
        this.dataTracker.set(AGE_TICKS, age);
    }

    public int getDeathStage() {
        return this.dataTracker.get(DEATH_STAGE);
    }

    public void setDeathStage(int stage) {
        this.dataTracker.set(DEATH_STAGE, stage);
    }

    public boolean isMale() {
        return this.dataTracker.get(GENDER);
    }

    public boolean isModelDead() {
        if (this.getWorld().isClient) {
            return this.isModelDead = this.dataTracker.get(MODEL_DEAD);
        }
        return this.isModelDead;
    }

    public void setModelDead(boolean modeldead) {
        this.dataTracker.set(MODEL_DEAD, modeldead);
        if (!this.getWorld().isClient) {
            this.isModelDead = modeldead;
        }
    }

    @Override
    public boolean isHovering() {
        return this.dataTracker.get(HOVERING);
    }

    public void setHovering(boolean hovering) {
        this.dataTracker.set(HOVERING, hovering);
    }

    @Override
    public boolean isFlying() {
        return this.dataTracker.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.dataTracker.set(FLYING, flying);
    }

    public boolean useFlyingPathFinder() {
        return this.isFlying() && this.getControllingPassenger() == null;
    }

    public void setGender(boolean male) {
        this.dataTracker.set(GENDER, male);
    }

    @Override
    public boolean isSleeping() {
        return this.dataTracker.get(SLEEPING);
    }

    public boolean isBlinking() {
        return this.age % 50 > 43;
    }

    public boolean isBreathingFire() {
        return this.dataTracker.get(FIREBREATHING);
    }

    public void setBreathingFire(boolean breathing) {
        this.dataTracker.set(FIREBREATHING, breathing);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < 2;
    }

    @Override
    public boolean isSitting() {
        return (this.dataTracker.get(TAMEABLE_FLAGS) & 1) != 0;
    }

    @Override
    public void setSitting(boolean sitting) {
        byte b0 = this.dataTracker.get(TAMEABLE_FLAGS);
        if (sitting) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b0 | 1));
            this.getNavigation().stop();
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b0 & -2));
        }
    }

    @Override
    public void setInSittingPose(boolean sleeping) {
        this.dataTracker.set(SLEEPING, sleeping);
        if (sleeping)
            this.getNavigation().stop();
    }

    public String getCustomPose() {
        return this.dataTracker.get(CUSTOM_POSE);
    }

    public void setCustomPose(String customPose) {
        this.dataTracker.set(CUSTOM_POSE, customPose);
        this.modelDeadProgress = 20f;
    }

    public void riderShootFire(Entity controller) {
    }

    private double calculateArmorModifier() {
        double val = 1D;
        final EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (EquipmentSlot slot : slots) {
            switch (DragonArmor.getArmorOrdinal(this.getEquippedStack(slot))) {
                case 1 -> val += 2D;
                case 2, 4 -> val += 3D;
                case 3 -> val += 5D;
                case 5, 6, 8 -> val += 10D;
                case 7 -> val += 1.5D;
            }
        }
        return val;
    }

    public boolean canMove() {
        return !this.isSitting() && !this.isSleeping() &&
                this.getControllingPassenger() == null && !this.hasVehicle() &&
                !this.isModelDead() && this.sleepProgress == 0 &&
                this.getAnimation() != ANIMATION_SHAKEPREY;
    }

    public boolean isFuelingForge() {
        return this.burningTarget != null && this.getWorld().getBlockEntity(this.burningTarget) instanceof BlockEntityDragonForgeInput;
    }

    @Override
    public boolean isAlive() {
        if (this.isModelDead())
            return !this.isRemoved();
        return super.isAlive();
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d vec, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() == IafItems.DRAGON_DEBUG_STICK.get()) {
            this.logic.debug();
            return ActionResult.SUCCESS;
        }
        return super.interactAt(player, vec, hand);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        // Interaction usually means right-click but the relevant item is often in the main hand
        ItemStack stack = player.getMainHandStack();
        int lastDeathStage = Math.min(this.getAgeInDays() / 5, 25);

        if (stack == ItemStack.EMPTY) {
            stack = player.getStackInHand(hand);
        }

        if (stack.getItem() == IafItems.DRAGON_DEBUG_STICK.get()) {
            this.logic.debug();
            return ActionResult.SUCCESS;
        }
        if (!this.isModelDead()) {
            if (stack.getItem() == IafItems.CREATIVE_DRAGON_MEAL.get()) {
                this.setTamed(true);
                this.setOwner(player);
                this.setHunger(this.getHunger() + 20);
                this.heal(Math.min(this.getHealth(), (int) (this.getMaxHealth() / 2)));
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
                this.spawnItemCrackParticles(stack.getItem());
                this.spawnItemCrackParticles(Items.BONE);
                this.spawnItemCrackParticles(Items.BONE_MEAL);
                if (!player.isCreative())
                    stack.decrement(1);
                return ActionResult.SUCCESS;
            }
            if (this.isBreedingItem(stack) && this.shouldDropLoot()) {
                this.setBreedingAge(0);
                this.eat(player, Hand.MAIN_HAND, stack);
                this.lovePlayer(player);
                return ActionResult.SUCCESS;
            }
            if (this.isOwner(player)) {
                if (stack.getItem() == this.dragonType.getCrystalItem() && !ItemSummoningCrystal.hasDragon(stack)) {
                    this.setCrystalBound(true);
                    NbtCompound compound = stack.getOrCreateNbt();
                    NbtCompound dragonTag = new NbtCompound();
                    dragonTag.putUuid("DragonUUID", this.getUuid());
                    if (this.getCustomName() != null) {
                        dragonTag.putString("CustomName", this.getCustomName().getString());
                    }
                    compound.put("Dragon", dragonTag);
                    this.playSound(SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, 1, 1);
                    player.swingHand(hand);
                    return ActionResult.SUCCESS;
                }
                this.setOwner(player);
                if (stack.getItem() == IafItems.DRAGON_HORN.get())
                    return super.interactMob(player, hand);
                if (stack.isEmpty() && !player.isSneaking()) {
                    if (!this.getWorld().isClient) {
                        final int dragonStage = this.getDragonStage();
                        if (dragonStage < 2) {
                            if (player.getPassengerList().size() >= 3)
                                return ActionResult.FAIL;
                            this.startRiding(player, true);
                            PacketByteBuf buf = PacketBufferUtils.create();
                            buf.writeInt(this.getId()).writeBoolean(true).writeBoolean(true);
                            ServerHelper.sendToAll(StaticVariables.START_RIDING_MOB_S2C, buf);
                        } else if (dragonStage > 2 && !player.hasVehicle()) {
                            player.setSneaking(false);
                            player.startRiding(this, true);
                            PacketByteBuf buf = PacketBufferUtils.create();
                            buf.writeInt(this.getId()).writeBoolean(true).writeBoolean(false);
                            ServerHelper.sendToAll(StaticVariables.START_RIDING_MOB_S2C, buf);
                            this.setInSittingPose(false);
                        }
                        this.getNavigation().stop();
                    }
                    return ActionResult.SUCCESS;
                } else if (stack.isEmpty() && player.isSneaking()) {
                    this.openInventory(player);
                    return ActionResult.SUCCESS;
                } else {
                    int itemFoodAmount = FoodUtils.getFoodPoints(stack, true, this.dragonType.isPiscivore());
                    if (itemFoodAmount > 0 && (this.getHunger() < 100 || this.getHealth() < this.getMaxHealth())) {
                        this.setHunger(this.getHunger() + itemFoodAmount);
                        this.setHealth(Math.min(this.getMaxHealth(), (int) (this.getHealth() + ((float) itemFoodAmount / 10))));
                        this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
                        this.spawnItemCrackParticles(stack.getItem());
                        if (!player.isCreative())
                            stack.decrement(1);
                        return ActionResult.SUCCESS;
                    }
                    final Item stackItem = stack.getItem();
                    if (stackItem == IafItems.DRAGON_MEAL.get() && this.getAgeInDays() < (this.isTamed() ? IafCommonConfig.INSTANCE.dragon.maxTamedDragonAge.getValue() : 128)) {
                        this.growDragon(1);
                        this.setHunger(this.getHunger() + 20);
                        this.heal(Math.min(this.getHealth(), (int) (this.getMaxHealth() / 2)));
                        this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
                        this.spawnItemCrackParticles(stackItem);
                        this.spawnItemCrackParticles(Items.BONE);
                        this.spawnItemCrackParticles(Items.BONE_MEAL);
                        if (!player.isCreative())
                            stack.decrement(1);
                        return ActionResult.SUCCESS;
                    } else if (stackItem == IafItems.SICKLY_DRAGON_MEAL.get() && !this.isAgingDisabled()) {
                        this.setHunger(this.getHunger() + 20);
                        this.heal(this.getMaxHealth());
                        this.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundVolume(), this.getSoundPitch());
                        this.spawnItemCrackParticles(stackItem);
                        this.spawnItemCrackParticles(Items.BONE);
                        this.spawnItemCrackParticles(Items.BONE_MEAL);
                        this.spawnItemCrackParticles(Items.POISONOUS_POTATO);
                        this.spawnItemCrackParticles(Items.POISONOUS_POTATO);
                        this.setAgingDisabled(true);
                        if (!player.isCreative())
                            stack.decrement(1);
                        return ActionResult.SUCCESS;
                    } else if (stackItem == IafItems.DRAGON_STAFF.get()) {
                        if (player.isSneaking()) {
                            if (this.hasHomePosition) {
                                this.hasHomePosition = false;
                                player.sendMessage(Text.translatable("dragon.command.remove_home"), true);
                            } else {
                                BlockPos pos = this.getBlockPos();
                                this.homePos = new HomePosition(pos, this.getWorld());
                                this.hasHomePosition = true;
                                player.sendMessage(Text.translatable("dragon.command.new_home", pos.getX(), pos.getY(), pos.getZ(), this.homePos.getDimension()), true);
                            }
                        } else {
                            this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, this.getSoundVolume(), this.getSoundPitch());
                            if (!this.getWorld().isClient) {
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
                            player.sendMessage(Text.translatable("dragon.command." + commandText), true);
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }
        } else if (this.getDeathStage() < lastDeathStage && player.canModifyBlocks()) {
            if (!this.getWorld().isClient && !stack.isEmpty() && stack.getItem() != null && stack.getItem() == Items.GLASS_BOTTLE && this.getDeathStage() < lastDeathStage / 2 && IafCommonConfig.INSTANCE.dragon.lootBlood.getValue()) {
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                this.setDeathStage(this.getDeathStage() + 1);
                player.getInventory().insertStack(new ItemStack(this.getBloodItem(), 1));
                return ActionResult.SUCCESS;
            } else {
                if (!this.getWorld().isClient && stack.isEmpty()) {
                    if (IafCommonConfig.INSTANCE.dragon.lootSkull.getValue()) {
                        if (this.getDeathStage() >= lastDeathStage - 1) {
                            ItemStack skull = this.getSkull().copy();
                            skull.setNbt(new NbtCompound());
                            assert skull.getNbt() != null;
                            skull.getNbt().putInt("Stage", this.getDragonStage());
                            skull.getNbt().putInt("DragonType", 0);
                            skull.getNbt().putInt("DragonAge", this.getAgeInDays());
                            this.setDeathStage(this.getDeathStage() + 1);
                            if (!this.getWorld().isClient) {
                                this.dropStack(skull, 1);
                            }
                            this.remove(RemovalReason.DISCARDED);
                        } else if (this.getDeathStage() == (lastDeathStage / 2) - 1 && IafCommonConfig.INSTANCE.dragon.lootHeart.getValue()) {
                            ItemStack heart = new ItemStack(this.getHeartItem(), 1);
                            if (!this.getWorld().isClient) {
                                this.dropStack(heart, 1);
                            }
                            this.setDeathStage(this.getDeathStage() + 1);
                        } else {
                            this.setDeathStage(this.getDeathStage() + 1);
                            ItemStack drop = this.getRandomDrop();
                            if (!drop.isEmpty() && !this.getWorld().isClient) {
                                this.dropStack(drop, 1);
                            }
                        }
                    }
                }
            }
            return ActionResult.SUCCESS;
        } else return super.interactMob(player, hand);
        return ActionResult.PASS;
    }

    public abstract ItemConvertible getHeartItem();

    public abstract Item getBloodItem();

    public abstract Item getFleshItem();

    public ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    private ItemStack getRandomDrop() {
        ItemStack stack = this.getItemFromLootTable();
        if (stack.getItem() == IafItems.DRAGON_BONE.get()) {
            this.playSound(SoundEvents.ENTITY_SKELETON_AMBIENT, 1, 1);
        } else {
            this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
        }
        return stack;
    }

    public boolean canPositionBeSeen(final double x, final double y, final double z) {
        final HitResult result = this.getWorld().raycast(new RaycastContext(new Vec3d(this.getX(), this.getY() + (double) this.getStandingEyeHeight(), this.getZ()), new Vec3d(x, y, z), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        final double dist = result.getPos().squaredDistanceTo(x, y, z);
        return dist <= 1.0D || result.getType() == HitResult.Type.MISS;
    }

    public abstract Identifier getDeadLootTable();

    public ItemStack getItemFromLootTable() {
        LootTable loottable = this.getWorld().getServer().getLootManager().getLootTable(this.getDeadLootTable());
        LootContextParameterSet.Builder lootparams$builder = (new LootContextParameterSet.Builder((ServerWorld) this.getWorld())).add(LootContextParameters.THIS_ENTITY, this).add(LootContextParameters.ORIGIN, this.getPos()).add(LootContextParameters.DAMAGE_SOURCE, this.getWorld().getDamageSources().generic());
        for (ItemStack itemstack : loottable.generateLoot(lootparams$builder.build(LootContextTypes.ENTITY))) {
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    public void growDragon(final int ageInDays) {
        if (this.isAgingDisabled())
            return;
        this.setAgeInDays(this.getAgeInDays() + ageInDays);
        //TODO: Probably brakes bounding boxes
        this.setBoundingBox(this.getBoundingBox());
        if (this.getWorld().isClient) {
            if (this.getAgeInDays() % 25 == 0) {
                for (int i = 0; i < this.getRenderSize() * 4; i++) {
                    final float f = (float) (this.getRandom().nextFloat() * (this.getBoundingBox().maxX - this.getBoundingBox().minX) + this.getBoundingBox().minX);
                    final float f1 = (float) (this.getRandom().nextFloat() * (this.getBoundingBox().maxY - this.getBoundingBox().minY) + this.getBoundingBox().minY);
                    final float f2 = (float) (this.getRandom().nextFloat() * (this.getBoundingBox().maxZ - this.getBoundingBox().minZ) + this.getBoundingBox().minZ);
                    final double motionX = this.getRandom().nextGaussian() * 0.07D;
                    final double motionY = this.getRandom().nextGaussian() * 0.07D;
                    final double motionZ = this.getRandom().nextGaussian() * 0.07D;

                    this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, f, f1, f2, motionX, motionY, motionZ);
                }
            }
        }
        if (this.getDragonStage() >= 2)
            this.dismountVehicle();
        this.updateAttributes();
    }

    public void spawnItemCrackParticles(Item item) {
        for (int i = 0; i < 15; i++) {
            final double motionX = this.getRandom().nextGaussian() * 0.07D;
            final double motionY = this.getRandom().nextGaussian() * 0.07D;
            final double motionZ = this.getRandom().nextGaussian() * 0.07D;
            final Vec3d headVec = this.getHeadPosition();
            if (!this.getWorld().isClient) {
                ((ServerWorld) this.getWorld()).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(item)), headVec.x, headVec.y, headVec.z, 1, motionX, motionY, motionZ, 0.1);
            } else {
                this.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(item)), headVec.x, headVec.y, headVec.z, motionX, motionY, motionZ);
            }
        }
    }

    public boolean isTimeToWake() {
        return this.getWorld().isDay() || this.getCommand() == 2;
    }

    private boolean isStuck() {
        boolean skip = this.isChained() || this.isTamed();

        if (skip) {
            return false;
        }

        boolean checkNavigation = this.ticksStill > 80 && this.canMove() && !this.isHovering();

        if (checkNavigation) {
            EntityNavigation navigation = this.getNavigation();
            Path path = navigation.getCurrentPath();

            return !navigation.isIdle() && (path == null || path.getEnd() != null || this.getBlockPos().getSquaredDistance(path.getEnd().getBlockPos()) > 15);
        }

        return false;
    }

    public boolean isOverAir() {
        return this.isOverAir;
    }

    private boolean isOverAirLogic() {
        return this.getWorld().isAir(BlockPos.ofFloored(this.getBlockX(), this.getBoundingBox().minY - 1, this.getBlockZ()));
    }

    public boolean isDiving() {
        return false;//isFlying() && motionY < -0.2;
    }

    public boolean isBeyondHeight() {
        if (this.getY() > this.getWorld().getTopY()) {
            return true;
        }
        return this.getY() > IafCommonConfig.INSTANCE.dragon.maxFlight.getValue();
    }

    private int calculateDownY() {
        if (this.getNavigation().getCurrentPath() != null) {
            Path path = this.getNavigation().getCurrentPath();
            Vec3d p = path.getNodePosition(this, Math.min(path.getLength() - 1, path.getCurrentNodeIndex() + 1));
            if (p.y < this.getY() - 1) {
                return -1;
            }
        }
        return 1;
    }

    public void breakBlock(final BlockPos position) {
        if (IafEvents.ON_GRIEF_BREAK_BLOCK.invoker().onBreakBlock(this, position.getX(), position.getY(), position.getZ()))
            return;

        final BlockState state = this.getWorld().getBlockState(position);
        final float hardness = IafCommonConfig.INSTANCE.dragon.griefing.getValue() || this.getDragonStage() <= 3 ? 2.0F : 5.0F;
        if (this.isBreakable(position, state, hardness, this)) {
            this.setVelocity(this.getVelocity().multiply(0.6F, 1, 0.6F));
            if (!this.getWorld().isClient()) {
                this.getWorld().breakBlock(position, !state.isIn(IafBlockTags.DRAGON_BLOCK_BREAK_NO_DROPS) && this.random.nextFloat() <= IafCommonConfig.INSTANCE.dragon.blockBreakingDropChance.getValue());
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
            if (this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                if (DragonUtils.canGrief(this)) {
                    // TODO :: make `force` ignore the dragon stage?
                    if (!this.isModelDead() && this.getDragonStage() >= 3 && (this.canMove() || this.getControllingPassenger() != null)) {
                        final int bounds = 1;
                        final int flightModifier = this.isFlying() && this.getTarget() != null ? -1 : 1;
                        final int yMinus = this.calculateDownY();
                        BlockPos.stream(
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

    protected boolean isBreakable(BlockPos pos, BlockState state, float hardness, EntityDragonBase entity) {
        return state.blocksMovement() && !state.isAir() &&
                state.getFluidState().isEmpty() && !state.getOutlineShape(this.getWorld(), pos).isEmpty() &&
                state.getHardness(this.getWorld(), pos) >= 0F &&
                state.getHardness(this.getWorld(), pos) <= hardness &&
                DragonUtils.canDragonBreak(state, entity) && this.canDestroyBlock(pos, state);
    }

    public void spawnGroundEffects() {
        if (this.getWorld().isClient) {
            for (int i = 0; i < this.getRenderSize(); i++) {
                for (int i1 = 0; i1 < 20; i1++) {
                    final float radius = 0.75F * (0.7F * this.getRenderSize() / 3) * -3;
                    final float angle = (0.01745329251F * this.bodyYaw) + i1 * 1F;
                    final double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    final double extraY = 0.8F;
                    final double extraZ = radius * MathHelper.cos(angle);
                    final BlockPos ground = this.getGround(BlockPos.ofFloored(this.getX() + extraX, this.getY() + extraY - 1, this.getZ() + extraZ));
                    final BlockState BlockState = this.getWorld().getBlockState(ground);
                    if (BlockState.isAir()) {
                        final double motionX = this.getRandom().nextGaussian() * 0.07D;
                        final double motionY = this.getRandom().nextGaussian() * 0.07D;
                        final double motionZ = this.getRandom().nextGaussian() * 0.07D;

                        this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, BlockState), true, this.getX() + extraX, ground.getY() + extraY, this.getZ() + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
    }

    private BlockPos getGround(BlockPos blockPos) {
        while (this.getWorld().isAir(blockPos) && blockPos.getY() > 1) {
            blockPos = blockPos.down();
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
    public void updatePassengerPosition(Entity passenger, PositionUpdater callback) {
        super.updatePassengerPosition(passenger, callback);
        if (this.hasPassenger(passenger)) {
            if (this.getControllingPassenger() == null || !this.getControllingPassenger().getUuid().equals(passenger.getUuid())) {
                this.updatePreyInMouth(passenger);
            } else {
                if (this.isModelDead()) {
                    passenger.stopRiding();
                }

                this.setYaw(passenger.getYaw());
                this.setHeadYaw(passenger.getHeadYaw());
                this.setPitch(passenger.getPitch());

                Vec3d riderPos = this.getRiderPosition();
                passenger.setPosition(riderPos.x, riderPos.y + passenger.getHeight(), riderPos.z);
            }
        }
    }

    private float bob(float speed, float degree, boolean bounce, float f, float f1) {
        final double a = MathHelper.sin(f * speed) * f1 * degree;
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
            float baseDamage = (float) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue();
            float damage = baseDamage * 2;
            boolean didDamage = prey.damage(this.getWorld().getDamageSources().mobAttack(this), damage);

            if (didDamage) {
                if (IafCommonConfig.INSTANCE.dragon.canHealFromBiting.getValue()) {
                    this.heal(damage * 0.5f);
                }
            }

            if (!(prey instanceof PlayerEntity)) {
                this.setHunger(this.getHunger() + 1);
            }

            prey.stopRiding();
        } else {
            this.bodyYaw = this.getYaw();
            final float modTick_0 = this.getAnimationTick() - 25;
            final float modTick_1 = this.getAnimationTick() > 25 && this.getAnimationTick() < 55 ? 8 * MathHelper.clamp(MathHelper.sin((float) (Math.PI + modTick_0 * 0.25)), -0.8F, 0.8F) : 0;
            final float modTick_2 = this.getAnimationTick() > 30 ? 10 : Math.max(0, this.getAnimationTick() - 20);
            final float radius = 0.75F * (0.6F * this.getRenderSize() / 3) * -3;
            final float angle = (0.01745329251F * this.bodyYaw) + 3.15F + (modTick_1 * 2F) * 0.015F;
            final double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            final double extraZ = radius * MathHelper.cos(angle);
            final double extraY = modTick_2 == 0 ? 0 : 0.035F * ((this.getRenderSize() / 3) + (modTick_2 * 0.5 * (this.getRenderSize() / 3)));
            assert prey != null;
            prey.setPosition(this.getX() + extraX, this.getY() + extraY, this.getZ() + extraZ);
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

    @Override
    public boolean shouldDropLoot() {
        return this.getDragonStage() >= 4;
    }

    @Override
    public boolean isBaby() {
        return this.getDragonStage() < 2;
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        spawnDataIn = super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setGender(this.getRandom().nextBoolean());
        final int age = this.getRandom().nextInt(80) + 1;
        this.growDragon(age);
        List<DragonColor> colors = DragonColor.getColorsByType(this.dragonType);
        this.setVariant(colors.get(new Random().nextInt(colors.size())).name());
        this.setInSittingPose(false);
        final double healthStep = (this.maximumHealth - this.minimumHealth) / 125;
        this.heal((Math.round(this.minimumHealth + (healthStep * age))));
        this.usingGroundAttack = true;
        this.setHunger(50);
        return spawnDataIn;
    }

    @Override
    public boolean damage(DamageSource dmg, float i) {
        if (this.isModelDead() && dmg != this.getWorld().getDamageSources().outOfWorld()) {
            return false;
        }
        if (this.hasPassengers() && dmg.getAttacker() != null && this.getControllingPassenger() != null && dmg.getAttacker() == this.getControllingPassenger()) {
            return false;
        }

        if ((dmg.getType().msgId().contains("arrow") || this.getVehicle() != null && dmg.getAttacker() != null && dmg.getAttacker().isPartOf(this.getVehicle())) && this.hasVehicle()) {
            return false;
        }

        if (dmg.isOf(DamageTypes.IN_WALL) || dmg.isOf(DamageTypes.FALLING_BLOCK) || dmg.isOf(DamageTypes.CRAMMING)) {
            return false;
        }
        if (!this.getWorld().isClient && dmg.getAttacker() != null && this.getRandom().nextInt(4) == 0) {
            this.roar();
        }
        if (i > 0) {
            if (this.isSleeping()) {
                this.setInSittingPose(false);
                if (!this.isTamed()) {
                    if (dmg.getAttacker() instanceof PlayerEntity) {
                        this.setTarget((PlayerEntity) dmg.getAttacker());
                    }
                }
            }
        }
        return super.damage(dmg, i);

    }

    @Override
    public void calculateDimensions() {
        super.calculateDimensions();
        final float scale = Math.min(this.getRenderSize() * 0.35F, 7F);
//        double prevX = getPosX();
//        double prevY = getPosY();
//        double prevZ = getPosZ();
//        float localWidth = this.getWidth();
//        if (this.getWidth() > localWidth && !this.firstUpdate && !this.world.isRemote) {
//            this.setPosition(prevX, prevY, prevZ);
//        }
        if (scale != this.lastScale) {
            this.updateScale(this.getRenderSize() / 3);
        }
        this.lastScale = scale;
    }

    @Override
    public float getStepHeight() {
        return Math.max(1.2F, 1.2F + (Math.min(this.getAgeInDays(), 125) - 25) * 1.8F / 100F);
    }

    @Override
    public void tick() {
        super.tick();
        this.calculateDimensions();
        this.updateParts();
        this.prevDragonPitch = this.getDragonPitch();
        this.getWorld().getProfiler().push("dragonLogic");
        this.setStepHeight(this.getStepHeight());
        this.isOverAir = this.isOverAirLogic();
        this.logic.updateDragonCommon();
        if (this.isModelDead()) {
            if (!this.getWorld().isClient && this.getWorld().isAir(BlockPos.ofFloored(this.getBlockX(), this.getBoundingBox().minY, this.getBlockZ())) && this.getY() > -1) {
                this.move(MovementType.SELF, new Vec3d(0, -0.2F, 0));
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
            if (this.getWorld().isClient) {
                this.logic.updateDragonClient();
            } else {
                this.logic.updateDragonServer();
                this.logic.updateDragonAttack();
            }
        }
        this.getWorld().getProfiler().pop();
        this.getWorld().getProfiler().push("dragonFlight");
        if (this.useFlyingPathFinder() && !this.getWorld().isClient /*&& isControlledByLocalInstance()*/) {
            this.flightManager.update();
        }
        this.getWorld().getProfiler().pop();
        this.getWorld().getProfiler().pop();

        if (!this.getWorld().isClient()) {
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
    public void tickMovement() {
        super.tickMovement();
        this.prevModelDeadProgress = this.modelDeadProgress;
        this.prevDiveProgress = this.diveProgress;
        this.prevAnimationProgresses[0] = this.sitProgress;
        this.prevAnimationProgresses[1] = this.sleepProgress;
        this.prevAnimationProgresses[2] = this.hoverProgress;
        this.prevAnimationProgresses[3] = this.flyProgress;
        this.prevAnimationProgresses[4] = this.fireBreathProgress;
        this.prevAnimationProgresses[5] = this.ridingProgress;
        this.prevAnimationProgresses[6] = this.tackleProgress;
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof PlayerEntity) {
            this.setTarget(null);
        }
        if (this.isModelDead()) {
            if (this.hasPassengers()) {
                this.removeAllPassengers();
            }

            this.setHovering(false);
            this.setFlying(false);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        if (this.animationTick > this.getAnimation().getDuration() && !this.getWorld().isClient) {
            this.animationTick = 0;
        }
    }

    @Override
    public EntityDimensions getDimensions(EntityPose poseIn) {
        return this.getType().getDimensions().scaled(this.getScaleFactor());
    }

    @Override
    public float getScaleFactor() {
        return Math.min(this.getRenderSize() * 0.35F, 7F);
    }

    @Override
    protected void fall(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public float getRenderSize() {
        final int stage = this.getDragonStage() - 1;
        final float step = (this.growth_stages[stage][1] - this.growth_stages[stage][0]) / 25;
        if (this.getAgeInDays() > 125) {
            return this.growth_stages[stage][0] + (step * 25);
        }
        return this.growth_stages[stage][0] + (step * this.getAgeFactor());
    }

    private int getAgeFactor() {
        return (this.getDragonStage() > 1 ? this.getAgeInDays() - (25 * (this.getDragonStage() - 1)) : this.getAgeInDays());
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
        this.getLookControl().lookAt(entityIn, 30.0F, 30.0F);
        if (this.isTackling() || this.isModelDead()) {
            return false;
        }

        final boolean flag = entityIn.damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));

        if (flag) {
            this.applyDamageEffects(this, entityIn);
        }

        return flag;
    }

    @Override
    public void tickRiding() {
        Entity entity = this.getVehicle();
        if (this.hasVehicle() && !entity.isAlive()) {
            this.stopRiding();
        } else {
            this.setVelocity(0, 0, 0);
            this.tick();
            if (this.hasVehicle()) {
                this.updateRiding(entity);
            }
        }
    }

    public void updateRiding(Entity riding) {
        if (riding != null && riding.hasPassenger(this) && riding instanceof PlayerEntity player) {
            final int i = riding.getPassengerList().indexOf(this);
            final float radius = (i == 2 ? -0.2F : 0.5F) + (player.isFallFlying() ? 2 : 0);
            final float angle = (0.01745329251F * player.bodyYaw) + (i == 1 ? 90 : i == 0 ? -90 : 0);
            final double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            final double extraZ = radius * MathHelper.cos(angle);
            final double extraY = (riding.isSneaking() ? 1.2D : 1.4D) + (i == 2 ? 0.4D : 0D);
            this.headYaw = player.headYaw;
            this.setYaw(this.headYaw);
            this.setPosition(riding.getX() + extraX, riding.getY() + extraY, riding.getZ() + extraZ);
            if ((this.getControlState() == 1 << 4 || player.isFallFlying()) && !riding.hasVehicle()) {
                this.stopRiding();
                if (this.getWorld().isClient) {
                    PacketByteBuf buf = PacketBufferUtils.create();
                    buf.writeInt(this.getId()).writeBoolean(false).writeBoolean(true);
                    NetworkManager.sendToServer(StaticVariables.START_RIDING_MOB_C2S, buf);
                }
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
        if (this.isModelDead()) {
            return NO_ANIMATION;
        }
        return this.currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (this.isModelDead()) {
            return;
        }
        this.currentAnimation = animation;
    }

    @Override
    public void playAmbientSound() {
        if (!this.isSleeping() && !this.isModelDead() && !this.getWorld().isClient) {
            if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                this.setAnimation(ANIMATION_SPEAK);
            }
            super.playAmbientSound();
        }
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        if (!this.isModelDead()) {
            if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION && !this.getWorld().isClient) {
                this.setAnimation(ANIMATION_SPEAK);
            }
            super.playHurtSound(source);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, EntityDragonBase.ANIMATION_EAT};
    }

    @Override
    public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity ageable) {
        return null;
    }

    @Override
    public boolean canBreedWith(AnimalEntity otherAnimal) {
        if (otherAnimal instanceof EntityDragonBase dragon && otherAnimal != this && otherAnimal.getClass() == this.getClass()) {
            return this.isMale() && !dragon.isMale() || !this.isMale() && dragon.isMale();
        }
        return false;
    }

    public EntityDragonEgg createEgg(EntityDragonBase ageable) { // FIXME :: Unused parameter
        EntityDragonEgg dragon = new EntityDragonEgg(IafEntities.DRAGON_EGG.get(), this.getWorld());
        dragon.setEggType(DragonColor.byMetadata(new Random().nextInt(4) + this.getStartMetaForType()));
        dragon.setPosition(MathHelper.floor(this.getX()) + 0.5, MathHelper.floor(this.getY()) + 1, MathHelper.floor(this.getZ()) + 0.5);
        return dragon;
    }

    public int getStartMetaForType() {
        return 0;
    }

    public boolean isTargetBlocked(Vec3d target) {
        if (target != null) {
            final BlockHitResult rayTrace = this.getWorld().raycast(new RaycastContext(this.getPos().add(0, this.getStandingEyeHeight(), 0), target, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
            final BlockPos sidePos = rayTrace.getBlockPos();
            if (!this.getWorld().isAir(sidePos)) {
                return true;
            }
            return rayTrace.getType() == HitResult.Type.BLOCK;
        }
        return false;
    }

    // FIXME :: Unused
    private double getFlySpeed() {
        return (2 + ((double) this.getAgeInDays() / 125) * 2) * (this.isTackling() ? 2 : 1);
    }

    public boolean isTackling() {
        return this.dataTracker.get(TACKLE);
    }

    public void setTackling(boolean tackling) {
        this.dataTracker.set(TACKLE, tackling);
    }

    public boolean isAgingDisabled() {
        return this.dataTracker.get(AGINGDISABLED);
    }

    public void setAgingDisabled(boolean isAgingDisabled) {
        this.dataTracker.set(AGINGDISABLED, isAgingDisabled);
    }

    public boolean isBoundToCrystal() {
        return this.dataTracker.get(CRYSTAL_BOUND);
    }

    public void setCrystalBound(boolean crystalBound) {
        this.dataTracker.set(CRYSTAL_BOUND, crystalBound);
    }

    public float getDistanceSquared(Vec3d Vector3d) {
        final float f = (float) (this.getX() - Vector3d.x);
        final float f1 = (float) (this.getY() - Vector3d.y);
        final float f2 = (float) (this.getZ() - Vector3d.z);
        return f * f + f1 * f1 + f2 * f2;
    }

    @Override
    public boolean isImmobile() {
        return this.getHealth() <= 0.0F || this.isSitting() && !this.hasPassengers() || this.isModelDead() || this.hasVehicle();
    }

    @Override
    public boolean isTouchingWater() {
        return super.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > MathHelper.floor(this.getDragonStage() / 2.0f);
    }

    @Override
    public void travel(Vec3d pTravelVector) {
        if (this.getAnimation() == ANIMATION_SHAKEPREY || !this.canMove() && !this.hasPassengers() || this.isSitting()) {
            if (this.getNavigation().getCurrentPath() != null) {
                this.getNavigation().stop();
            }
            pTravelVector = new Vec3d(0, 0, 0);
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
                double forward = rider.forwardSpeed;
                double strafing = rider.sidewaysSpeed;
                double vertical = 0;
                float speed = (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                // Bigger difference in speed for young and elder dragons
//                float airSpeedModifier = (float) (5.2f + 1.0f * Mth.map(Math.min(this.getAgeInDays(), 125), 0, 125, 0f, 1.5f));
                float airSpeedModifier = (float) (5.2f + 1.0f * MathHelper.map(speed, this.minimumSpeed, this.maximumSpeed, 0f, 1.5f));
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
                this.setTackling(this.isAttacking() && this.getPitch() > -5 && this.getVelocity().length() > 1.0d);

                this.gliding = this.allowMousePitchControl && rider.isSprinting();
                if (!this.gliding) {
                    // Mouse controlled yaw
                    speed += this.glidingSpeedBonus;
                    // Slower on going astern
                    forward *= rider.forwardSpeed > 0 ? 1.0f : 0.5f;
                    // Slower on going sideways
                    strafing *= 0.4f;
                    if (this.isGoingUp() && !this.isGoingDown()) {
                        vertical = 1f;
                    } else if (this.isGoingDown() && !this.isGoingUp()) {
                        vertical = -1f;
                    }
                    // Damp the vertical motion so the dragon's head is more responsive to the control
                    else {
                        this.isLogicalSideForUpdatingMovement();//                        this.setDeltaMovement(this.getDeltaMovement().multiply(1.0f, 0.8f, 1.0f));
                    }
                } else {
                    // Mouse controlled yaw and pitch
                    speed *= 1.5f;
                    strafing *= 0.1f;
                    // Diving is faster
                    // Todo: a new and better algorithm much like elytra flying
                    this.glidingSpeedBonus = (float) MathHelper.clamp(this.glidingSpeedBonus + this.getVelocity().y * -0.05d, -0.8d, 1.5d);
                    speed += this.glidingSpeedBonus;
                    // Try to match the moving vector to the rider's look vector
                    forward = MathHelper.abs(MathHelper.cos(this.getPitch() * ((float) Math.PI / 180F)));
                    vertical = MathHelper.abs(MathHelper.sin(this.getPitch() * ((float) Math.PI / 180F)));
                    // Pitch is still responsive to spacebar and x key
                    if (this.isGoingUp() && !this.isGoingDown()) {
                        vertical = Math.max(vertical, 0.5);
                    } else if (this.isGoingDown() && !this.isGoingUp()) {
                        vertical = Math.min(vertical, -0.5);
                    } else if (this.isGoingUp() && this.isGoingDown()) {
                        vertical = 0;
                    }
                    // X rotation takes minus on looking upward
                    else if (this.getPitch() < 0) {
                        vertical *= 1;
                    } else if (this.getPitch() > 0) {
                        vertical *= -1;
                    } else {
                        this.isLogicalSideForUpdatingMovement();//                        this.setDeltaMovement(this.getDeltaMovement().multiply(1.0f, 0.8f, 1.0f));
                    }
                }
                // Speed bonus damping
                this.glidingSpeedBonus -= (float) (this.glidingSpeedBonus * 0.01d);

                if (this.isLogicalSideForUpdatingMovement()) {
                    // Vanilla friction on Y axis is smaller, which will influence terminal speed for climbing and diving
                    // use same friction coefficient on all axis simplifies how travel vector is computed
                    flyingSpeed = speed * 0.1F;
                    this.setMovementSpeed(flyingSpeed);

                    this.updateVelocity(flyingSpeed, new Vec3d(strafing, vertical, forward));
                    this.move(MovementType.SELF, this.getVelocity());
                    this.setVelocity(this.getVelocity().multiply(new Vec3d(0.9, 0.9, 0.9)));

                    Vec3d currentMotion = this.getVelocity();
                    if (this.horizontalCollision) {
                        currentMotion = new Vec3d(currentMotion.x, 0.1D, currentMotion.z);
                    }
                    this.setVelocity(currentMotion);

                    this.updateLimbs(false);
                } else {
                    this.setVelocity(Vec3d.ZERO);
                }
                this.tryCheckBlockCollision();
                this.updatePitch(this.lastRenderY - this.getY());
            }
            // In water move control, for those that can't swim
            else if (this.isTouchingWater() || this.isInLava()) {
                double forward = rider.forwardSpeed;
                double strafing = rider.sidewaysSpeed;
                double vertical = 0;
                float speed = (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);

                if (this.isGoingUp() && !this.isGoingDown()) {
                    vertical = 0.5f;
                } else if (this.isGoingDown() && !this.isGoingUp()) {
                    vertical = -0.5f;
                }

                flyingSpeed = speed;
                // Float in water for those can't swim is done in LivingEntity#aiStep on server side
                // Leave this handled by both side before we have a better solution
                this.setMovementSpeed(flyingSpeed);
                // Overwrite the zza in setSpeed
                this.setForwardSpeed((float) forward);
                // Vanilla in water behavior includes float on water and moving very slow
                // in lava behavior includes moving slow and sink
                super.travel(pTravelVector.add(strafing, vertical, forward));

            }
            // Walking control
            else {
                double forward = rider.forwardSpeed;
                double strafing = rider.sidewaysSpeed * 0.5f;
                // Inherit y motion for dropping
                double vertical = pTravelVector.y;
                float speed = (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);

                float groundSpeedModifier = (float) (1.8F * this.getFlightSpeedModifier());
                speed *= groundSpeedModifier;
                // Try to match the original riding speed
                forward *= speed;
                // Faster sprint
                forward *= rider.isSprinting() ? 1.2f : 1.0f;
                // Slower going back
                forward *= rider.forwardSpeed > 0 ? 1.0f : 0.2f;

                if (this.isLogicalSideForUpdatingMovement()) {
                    this.setMovementSpeed(speed);
                    // Vanilla walking behavior includes going up steps
                    super.travel(new Vec3d(strafing, vertical, forward));
                } else {
                    this.setVelocity(Vec3d.ZERO);
                }
                this.tryCheckBlockCollision();
                this.updatePitch(this.lastRenderY - this.getY());
            }
        }
        // No rider move control
        else {
            super.travel(pTravelVector);
        }
    }

    /**
     * Update dragon pitch for the server on {@link IafDragonLogic#updateDragonServer()} <br>
     * For some reason the {@link LivingEntity#prevY} failed to update the pitch properly when the movement is handled by client.
     * Use {@link LivingEntity#lastRenderY} instead will properly update the pitch on server.
     *
     * @param verticalDelta vertical distance from last update
     */
    public void updatePitch(final double verticalDelta) {
        if (this.isOverAir() && !this.hasVehicle()) {
            // Update the pitch when in air, and stepping up many blocks
            if (!this.isHovering()) {
                this.incrementDragonPitch((float) (verticalDelta) * 10);
            }
            this.setDragonPitch(MathHelper.clamp(this.getDragonPitch(), -60, 40));
            final float plateau = 2;
            final float planeDist = (float) ((Math.abs(this.getVelocity().x) + Math.abs(this.getVelocity().z)) * 6F);
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
            if (MathHelper.abs(this.getDragonPitch()) < 1) {
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

        if (controllingPassenger instanceof PlayerEntity rider) {
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
            if (this.spacebarTicks > 20 && this.getOwner() != null && this.getPassengerList().contains(this.getOwner()) && !this.isFlying() && !this.isHovering()) {
                if (!this.isTouchingWater()) {
                    this.setHovering(true);
                    this.spacebarTicks = 0;

                    this.glidingSpeedBonus = 0;
                }
            }
            if (this.isFlying() || this.isHovering()) {
                if (rider.forwardSpeed > 0) {
                    this.setFlying(true);
                    this.setHovering(false);
                } else {
                    this.setFlying(false);
                    this.setHovering(true);
                }
                // Hitting terrain with big angle of attack
                if (!this.isOverAir() && this.isFlying() && rider.getPitch() > 10 && !this.isTouchingWater()) {
                    this.setHovering(false);
                    this.setFlying(false);
                }
                // Dragon landing
                if (!this.isOverAir() && this.isGoingDown() && !this.isTouchingWater()) {
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
                if (!this.isFlying() && this.isOnGround()) {
                    this.tacklingTicks = 0;
                    this.setTackling(false);
                }
                // Todo: problem with friendly fire to tamed horses
                List<Entity> victims = this.getWorld().getOtherEntities(this, this.getBoundingBox().stretch(2.0D, 2.0D, 2.0D), potentialVictim -> (
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
            if (this.isAttacking() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity) {
                LivingEntity target = DragonUtils.riderLookingAtEntity(this, this.getControllingPassenger(), this.getDragonStage() + (this.getBoundingBox().maxX - this.getBoundingBox().minX));
                if (this.getAnimation() != EntityDragonBase.ANIMATION_BITE) {
                    this.setAnimation(EntityDragonBase.ANIMATION_BITE);
                }
                if (target != null && !DragonUtils.hasSameOwner(this, target)) {
                    int damage = (int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue();
                    boolean didDamage = this.logic.attackTarget(target, rider, damage);

                    if (didDamage) {
                        if (IafCommonConfig.INSTANCE.dragon.canHealFromBiting.getValue()) {
                            this.heal(damage * 0.1f);
                        }
                    }
                }
            }
            // Shift key to dismount
            if (this.getControllingPassenger() != null && this.getControllingPassenger().isSneaking()) {
                IafEntityData data = IafEntityData.get(this.getControllingPassenger());
                data.miscData.setDismounted(true);
                this.getControllingPassenger().stopRiding();
            }
            // Reset attack target when being ridden
            if (this.getTarget() != null && !this.getPassengerList().isEmpty() && this.getOwner() != null && this.getPassengerList().contains(this.getOwner())) {
                this.setTarget(null);
            }
            // Stop flying when hit the water, but waterfalls do not block flying
            if (this.getBlockStateAtPos().getFluidState().isStill() && this.isTouchingWater() && !this.isGoingUp()) {
                this.setFlying(false);
                this.setHovering(false);
            }
        } else if (controllingPassenger instanceof EntityDreadQueen) {
            // Original logic involves riding
            PlayerEntity ridingPlayer = this.getRidingPlayer();
            if (ridingPlayer != null) {
                if (this.isGoingUp()) {
                    if (!this.isFlying() && !this.isHovering()) {
                        this.spacebarTicks += 2;
                    }
                } else if (this.isDismounting()) {
                    if (this.isFlying() || this.isHovering()) {
                        this.setVelocity(this.getVelocity().add(0, -0.04, 0));
                        this.setFlying(false);
                        this.setHovering(false);
                    }
                }
            }
            if (!this.isDismounting() && (this.isFlying() || this.isHovering())) {
                this.setVelocity(this.getVelocity().add(0, 0.01, 0));
            }
            if (this.isStriking() && this.getControllingPassenger() != null && this.getDragonStage() > 1) {
                this.setBreathingFire(true);
                this.riderShootFire(this.getControllingPassenger());
                this.fireStopTicks = 10;
            }
            if (this.isAttacking() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity) {
                LivingEntity target = DragonUtils.riderLookingAtEntity(this, this.getControllingPassenger(), this.getDragonStage() + (this.getBoundingBox().maxX - this.getBoundingBox().minX));
                if (this.getAnimation() != EntityDragonBase.ANIMATION_BITE) {
                    this.setAnimation(EntityDragonBase.ANIMATION_BITE);
                }
                if (target != null && !DragonUtils.hasSameOwner(this, target)) {
                    this.logic.attackTarget(target, ridingPlayer, (int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue());
                }
            }
            if (this.getControllingPassenger() != null && this.getControllingPassenger().isSneaking()) {
                IafEntityData data = IafEntityData.get(this.getControllingPassenger());
                data.miscData.setDismounted(true);
                this.getControllingPassenger().stopRiding();
            }
            if (this.isFlying()) {
                if (!this.isHovering() && this.getControllingPassenger() != null && !this.isOnGround() && Math.max(Math.abs(this.getVelocity().getX()), Math.abs(this.getVelocity().getZ())) < 0.1F) {
                    this.setHovering(true);
                    this.setFlying(false);
                }
            } else {
                if (this.isHovering() && this.getControllingPassenger() != null && !this.isOnGround() && Math.max(Math.abs(this.getVelocity().getX()), Math.abs(this.getVelocity().getZ())) > 0.1F) {
                    this.setFlying(true);
                    this.usingGroundAttack = false;
                    this.setHovering(false);
                }
            }
            if (this.spacebarTicks > 0) {
                this.spacebarTicks--;
            }
            if (this.spacebarTicks > 20 && this.getOwner() != null && this.getPassengerList().contains(this.getOwner()) && !this.isFlying() && !this.isHovering()) {
                this.setHovering(true);
            }

            if (this.hasPassengers() && !this.isOverAir() && this.isFlying() && !this.isHovering() && this.flyTicks > 40) {
                this.setFlying(false);
            }
        }
    }

    @Override
    public void move(MovementType pType, Vec3d pPos) {
        if (this.isSitting() && !this.hasPassengers()) {
            pPos = new Vec3d(0, pPos.getY(), 0);
        }

        if (this.hasPassengers()) {
            // When riding, the server side movement check is performed in ServerGamePacketListenerImpl#handleMoveVehicle
            // verticalCollide tag might get inconsistent due to dragon's large bounding box and causes move wrongly msg
            if (this.isLogicalSideForUpdatingMovement()) {
                // This is how EntityDragonBase#breakBlock handles movement when breaking blocks
                // it's done by server, however client does not fire server side events, so breakBlock() here won't work
                if (this.horizontalCollision) {
                    this.setVelocity(this.getVelocity().multiply(0.6F, 1, 0.6F));
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
        final double checkLength = this.getBoundingBox().getAverageSideLength() * 3;
        final PlayerEntity player = this.getWorld().getClosestPlayer(this, checkLength);
        if (this.isSleeping()) {
            if (player != null && !this.isOwner(player) && !player.isCreative()) {
                this.setInSittingPose(false);
                this.setSitting(false);
                this.setTarget(player);
            }
        }
    }

    public boolean isDirectPathBetweenPoints(Vec3d vec1, Vec3d vec2) {
        final BlockHitResult rayTrace = this.getWorld().raycast(new RaycastContext(vec1, new Vec3d(vec2.x, vec2.y + (double) this.getHeight() * 0.5D, vec2.z), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        return rayTrace.getType() != HitResult.Type.BLOCK;
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        this.setHunger(this.getHunger() + FoodUtils.getFoodPoints(this));
    }

    @Override
    public void onHearFlute(PlayerEntity player) {
        if (this.isTamed() && this.isOwner(player)) {
            if (this.isFlying() || this.isHovering()) {
                this.setFlying(false);
                this.setHovering(false);
            }
        }
    }

    public abstract SoundEvent getRoarSound();

    public void roar() {
        if (EntityGorgon.isStoneMob(this) || this.isModelDead()) {
            return;
        }
        if (this.random.nextBoolean()) {
            if (this.getAnimation() != ANIMATION_EPIC_ROAR) {
                this.setAnimation(ANIMATION_EPIC_ROAR);
                this.playSound(this.getRoarSound(), this.getSoundVolume() + 3 + Math.max(0, this.getDragonStage() - 2), this.getSoundPitch() * 0.7F);
            }
            if (this.getDragonStage() > 3) {
                final int size = (this.getDragonStage() - 3) * 30;
                final List<Entity> entities = this.getWorld().getOtherEntities(this, this.getBoundingBox().stretch(size, size, size));
                for (final Entity entity : entities) {
                    final boolean isStrongerDragon = entity instanceof EntityDragonBase && ((EntityDragonBase) entity).getDragonStage() >= this.getDragonStage();
                    if (entity instanceof LivingEntity living && !isStrongerDragon) {
                        if (this.isOwner(living) || this.isOwnersPet(living))
                            living.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 50 * size));
                        else if (living.getEquippedStack(EquipmentSlot.HEAD).getItem() != IafItems.EARPLUGS.get())
                            living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 50 * size));
                    }
                }
            }
        } else {
            if (this.getAnimation() != ANIMATION_ROAR) {
                this.setAnimation(ANIMATION_ROAR);
                this.playSound(this.getRoarSound(), this.getSoundVolume() + 2 + Math.max(0, this.getDragonStage() - 3), this.getSoundPitch());
            }
            if (this.getDragonStage() > 3) {
                final int size = (this.getDragonStage() - 3) * 30;
                final List<Entity> entities = this.getWorld().getOtherEntities(this, this.getBoundingBox().stretch(size, size, size));
                for (final Entity entity : entities) {
                    final boolean isStrongerDragon = entity instanceof EntityDragonBase && ((EntityDragonBase) entity).getDragonStage() >= this.getDragonStage();
                    if (entity instanceof LivingEntity living && !isStrongerDragon)
                        if (this.isOwner(living) || this.isOwnersPet(living))
                            living.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 30 * size));
                        else
                            living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 30 * size));
                }
            }
        }
    }

    private boolean isOwnersPet(LivingEntity living) {
        return this.isTamed() && this.getOwner() != null && living instanceof TameableEntity && ((TameableEntity) living).getOwner() != null && this.getOwner().isPartOf(((TameableEntity) living).getOwner());
    }

    public boolean isDirectPathBetweenPoints(Entity entity, Vec3d vec1, Vec3d vec2) {

        HitResult movingobjectposition = this.getWorld().raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        return movingobjectposition.getType() != HitResult.Type.BLOCK;
    }

    public boolean shouldRenderEyes() {
        return !this.isSleeping() && !this.isModelDead() && !this.isBlinking() && !EntityGorgon.isStoneMob(this);
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return DragonUtils.canTameDragonAttack(this, entity);
    }

    @Override
    public void dropArmor() {

    }

    public boolean isChained() {
        AtomicBoolean isChained = new AtomicBoolean(false);
        IafEntityData data = IafEntityData.get(this);
        isChained.set(data.chainData.getChainedTo().isEmpty());
        return isChained.get();
    }

    @Override
    protected void dropLoot(DamageSource damageSourceIn, boolean attackedRecently) {
    }

    public HitResult rayTraceRider(Entity rider, double blockReachDistance, float partialTicks) {
        Vec3d Vector3d = rider.getCameraPosVec(partialTicks);
        Vec3d Vector3d1 = rider.getRotationVec(partialTicks);
        Vec3d Vector3d2 = Vector3d.add(Vector3d1.x * blockReachDistance, Vector3d1.y * blockReachDistance, Vector3d1.z * blockReachDistance);
        return this.getWorld().raycast(new RaycastContext(Vector3d, Vector3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
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
     * @see EntityDragonBase#getRideHorizontalBase()
     */
    protected float getRideHeightBase() {
        return 0.002237889819f * MathHelper.square(this.getRenderSize()) + 0.233137174f * this.getRenderSize() - 1.717904311f;
    }

    /**
     * Provide a rough horizontal distance of rider's hitbox
     *
     * @return rider's horizontal position delta, in blocks
     * @see EntityDragonBase#getRideHeightBase()
     */
    protected float getRideHorizontalBase() {
        return 0.00336283f * MathHelper.square(this.getRenderSize()) + 0.1934242516f * this.getRenderSize() - 0.02622133882f;
    }

    public Vec3d getRiderPosition() {
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
        final float linearFactor = MathHelper.map(Math.max(this.getAgeInDays() - 50, 0), 0, 75, 0, 1);
        LivingEntity rider = this.getControllingPassenger();
        // Extra height when rider and the dragon look upwards, this will reduce model clipping
        if (rider != null && rider.getPitch() < 0) {
            extraY += (float) MathHelper.map(rider.getPitch(), 60, -40, -0.1, 0.1);
        }
        if (this.isHovering() || this.isFlying()) {
            // Extra height when flying, reduces model clipping since dragon has a bigger amplitude when flying/hovering
            extraY += 1.1f * linearFactor;
            extraY += this.getRideHeightBase() * 0.6f;
        } else {
            // Extra height when walking, reduces model clipping
            if (rider != null && rider.forwardSpeed > 0) {
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
        final float headPosX = (float) (this.getX() + xzMod * MathHelper.cos((float) ((this.getYaw() + 90) * Math.PI / 180)));
//        final float headPosY = (float) (getY() + (0.7F + sitProg + hoverProg + deadProg + sleepProg + flyProg + pitchY) * getRenderSize() * 0.3F + this.getScale() * 0.2F);
        final float headPosY = (float) (this.getY() + yMod);
        final float headPosZ = (float) (this.getZ() + xzMod * MathHelper.sin((float) ((this.getYaw() + 90) * Math.PI / 180)));
        return new Vec3d(headPosX, headPosY, headPosZ);
    }

    @Override
    public Vec3d updatePassengerForDismount(final LivingEntity passenger) {
        if (passenger.isInsideWall()) {
            return this.getPos().add(0, 1, 0);
        }

        return this.getRiderPosition().add(0, passenger.getHeight(), 0);
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
        this.setDeathStage(this.getAgeInDays() / 5);
        this.setModelDead(false);
    }

    @Override
    public boolean isTeammate(Entity entityIn) {
        // Workaround to make sure dragons won't be attacked when dead
        if (this.isModelDead())
            return true;
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity)
                return true;
            if (entityIn instanceof TameableEntity entity)
                return entity.isOwner(livingentity);
            if (livingentity != null)
                return livingentity.isTeammate(entityIn);
        }

        return super.isTeammate(entityIn);
    }

    public Vec3d getHeadPosition() {
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
            pitchMulti = MathHelper.sin((float) Math.toRadians(dragonPitch));
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
        final float xzMod = (1.7F * this.getRenderSize() * 0.3F * flightXz) + this.getRenderSize() * (0.3F * MathHelper.sin((float) ((dragonPitch + 90) * Math.PI / 180)) * pitchAdjustment - pitchMinus - hoverProg * 0.45F);
        final float headPosX = (float) (this.getX() + (xzMod) * MathHelper.cos((float) ((this.getYaw() + 90) * Math.PI / 180)));
        final float headPosY = (float) (this.getY() + (0.7F + sitProg + hoverProg + deadProg + epicRoarProg + sleepProg + flyProg + pitchMulti) * this.getRenderSize() * 0.3F);
        final float headPosZ = (float) (this.getZ() + (xzMod) * MathHelper.sin((float) ((this.getYaw() + 90) * Math.PI / 180)));
        return new Vec3d(headPosX, headPosY, headPosZ);
    }

    public abstract void stimulateFire(double burnX, double burnY, double burnZ, int syncType);

    public void randomizeAttacks() {
        this.airAttack = IafDragonAttacks.Air.values()[this.getRandom().nextInt(IafDragonAttacks.Air.values().length)];
        this.groundAttack = IafDragonAttacks.Ground.values()[this.getRandom().nextInt(IafDragonAttacks.Ground.values().length)];

    }

    @Override
    public boolean canExplosionDestroyBlock(Explosion explosionIn, BlockView worldIn, BlockPos pos, BlockState blockStateIn, float explosionPower) {
        return !(blockStateIn.getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(blockStateIn, this);
    }

    public void tryScorchTarget() {
        LivingEntity entity = this.getTarget();
        if (entity != null) {
            final float distX = (float) (entity.getX() - this.getX());
            final float distZ = (float) (entity.getZ() - this.getZ());
            if (this.isBreathingFire()) {
                if (this.isActuallyBreathingFire()) {
                    this.setYaw(this.bodyYaw);
                    if (this.age % 5 == 0)
                        this.playSound(IafSounds.FIREDRAGON_BREATH.get(), 4, 1);
                    int breathTicks = MathHelper.clamp(this.fireBreathTicks, 0, 40);
                    this.stimulateFire(this.getX() + distX * breathTicks / 40, entity.getY(), this.getZ() + distZ * breathTicks / 40, 1);
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
    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (this.isTamed() && target instanceof TameableEntity tamableTarget) {
            UUID targetOwner = tamableTarget.getOwnerUuid();
            if (targetOwner != null && targetOwner.equals(this.getOwnerUuid())) {
                return false;
            }
        }
        return super.canAttackWithOwner(target, owner);
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        return super.canTarget(target) && DragonUtils.isAlive(target);
    }

    public boolean isPart(Entity entityHit) {
        return this.headPart != null && this.headPart.isPartOf(entityHit) || this.neckPart != null && this.neckPart.isPartOf(entityHit) ||
                this.leftWingLowerPart != null && this.leftWingLowerPart.isPartOf(entityHit) || this.rightWingLowerPart != null && this.rightWingLowerPart.isPartOf(entityHit) ||
                this.leftWingUpperPart != null && this.leftWingUpperPart.isPartOf(entityHit) || this.rightWingUpperPart != null && this.rightWingUpperPart.isPartOf(entityHit) ||
                this.tail1Part != null && this.tail1Part.isPartOf(entityHit) || this.tail2Part != null && this.tail2Part.isPartOf(entityHit) ||
                this.tail3Part != null && this.tail3Part.isPartOf(entityHit) || this.tail4Part != null && this.tail4Part.isPartOf(entityHit);
    }

    @Override
    public double getFlightSpeedModifier() {
        return IafCommonConfig.INSTANCE.dragon.dragonFlightSpeedMod.getValue();
    }

    public boolean isAllowedToTriggerFlight() {
        return (this.hasFlightClearance() && this.isOnGround() || this.isTouchingWater()) && !this.isSitting() && this.getPassengerList().isEmpty() && !this.isBaby() && !this.isSleeping() && this.canMove();
    }

    public BlockPos getEscortPosition() {
        return this.getOwner() != null ? new BlockPos(this.getOwner().getBlockPos()) : this.getBlockPos();
    }

    public boolean shouldTPtoOwner() {
        return this.getOwner() != null && this.distanceTo(this.getOwner()) > 10;
    }

    public boolean isSkeletal() {
        return this.getDeathStage() >= (this.getAgeInDays() / 5) / 2;
    }

    @Override
    public boolean saveNbt(NbtCompound compound) {
        return this.saveSelfNbt(compound);
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        if (soundIn == SoundEvents.ENTITY_GENERIC_EAT || soundIn == this.getAmbientSound() || soundIn == this.getHurtSound(this.getWorld().getDamageSources().generic()) || soundIn == this.getDeathSound() || soundIn == this.getRoarSound()) {
            if (!this.isSilent() && this.headPart != null) {
                this.getWorld().playSound(null, this.headPart.getX(), this.headPart.getY(), this.headPart.getZ(), soundIn, this.getSoundCategory(), volume, pitch);
            }
        } else {
            super.playSound(soundIn, volume, pitch);
        }
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    public boolean hasFlightClearance() {
        BlockPos topOfBB = BlockPos.ofFloored(this.getBlockX(), this.getBoundingBox().maxY, this.getBlockZ());
        for (int i = 1; i < 4; i++) {
            if (!this.getWorld().isAir(topOfBB.up(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getEquippedStack(final EquipmentSlot slotIn) {
        return switch (slotIn) {
            case OFFHAND -> this.dragonInventory.getStack(0);
            case HEAD -> this.dragonInventory.getStack(1);
            case CHEST -> this.dragonInventory.getStack(2);
            case LEGS -> this.dragonInventory.getStack(3);
            case FEET -> this.dragonInventory.getStack(4);
            default -> super.getEquippedStack(slotIn);
        };
    }

    @Override
    public void equipStack(final EquipmentSlot slotIn, final ItemStack stack) {
        switch (slotIn) {
            case OFFHAND -> this.dragonInventory.setStack(0, stack);
            case HEAD -> this.dragonInventory.setStack(1, stack);
            case CHEST -> this.dragonInventory.setStack(2, stack);
            case LEGS -> this.dragonInventory.setStack(3, stack);
            case FEET -> this.dragonInventory.setStack(4, stack);
            default -> {
                super.getEquippedStack(slotIn);
                return;
            }
        }
        this.dragonInventory.markDirty();
    }

    public SoundEvent getBabyFireSound() {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
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
    public void onRemoved() {
        if (IafCommonConfig.INSTANCE.dragon.chunkLoadSummonCrystal.getValue()) {
            if (this.isBoundToCrystal()) {
                DragonPosWorldData data = DragonPosWorldData.get(this.getWorld());
                if (data != null) {
                    data.addDragon(this.getUuid(), this.getBlockPos());
                }
            }
        }
        super.onRemoved();
    }

    @Override
    public int maxSearchNodes() {
        return (int) this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).getValue();
    }

    @Override
    public boolean isSmallerThanBlock() {
        return false;
    }

    @Override
    public float getXZNavSize() {
        return Math.max(1.4F, this.getWidth() / 2.0F);
    }

    @Override
    public int getYNavSize() {
        return MathHelper.ceil(this.getHeight());
    }

    @Override
    public void onInventoryChanged(Inventory invBasic) {
        if (!this.getWorld().isClient) {
            this.updateAttributes();
        }
    }

    @Override // TODO :: Block collision performance impact (due to the multi-part entity)?
    public Vec3d applyMovementInput(Vec3d pDeltaMovement, float pFriction) {
        if (this.moveControl instanceof IafDragonFlightManager.PlayerFlightMoveHelper)
            return pDeltaMovement;
        return super.applyMovementInput(pDeltaMovement, pFriction);
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
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
