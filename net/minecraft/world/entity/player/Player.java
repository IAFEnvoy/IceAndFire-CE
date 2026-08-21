package net.minecraft.world.entity.player;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.players.NameAndId;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.vehicle.minecart.MinecartCommandBlock;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.jspecify.annotations.Nullable;

public abstract class Player extends Avatar implements ContainerUser, net.neoforged.neoforge.common.extensions.IPlayerExtension {
    public static final String PERSISTED_NBT_TAG = "PlayerPersisted";
    public static final int MAX_HEALTH = 20;
    public static final int SLEEP_DURATION = 100;
    public static final int WAKE_UP_DURATION = 10;
    public static final int ENDER_SLOT_OFFSET = 200;
    public static final int HELD_ITEM_SLOT = 499;
    public static final int CRAFTING_SLOT_OFFSET = 500;
    public static final float DEFAULT_BLOCK_INTERACTION_RANGE = 4.5F;
    public static final float DEFAULT_ENTITY_INTERACTION_RANGE = 3.0F;
    private static final EntityDataAccessor<Float> DATA_PLAYER_ABSORPTION_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_SCORE_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<OptionalInt> DATA_SHOULDER_PARROT_LEFT = SynchedEntityData.defineId(
        Player.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT
    );
    private static final EntityDataAccessor<OptionalInt> DATA_SHOULDER_PARROT_RIGHT = SynchedEntityData.defineId(
        Player.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT
    );
    private static final short DEFAULT_SLEEP_TIMER = 0;
    private static final float DEFAULT_EXPERIENCE_PROGRESS = 0.0F;
    private static final int DEFAULT_EXPERIENCE_LEVEL = 0;
    private static final int DEFAULT_TOTAL_EXPERIENCE = 0;
    private static final int NO_ENCHANTMENT_SEED = 0;
    private static final int DEFAULT_SELECTED_SLOT = 0;
    private static final int DEFAULT_SCORE = 0;
    public static final float CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER_VALUE = 2.0F;
    private final Inventory inventory;
    protected PlayerEnderChestContainer enderChestInventory = new PlayerEnderChestContainer();
    public final InventoryMenu inventoryMenu;
    public AbstractContainerMenu containerMenu;
    protected FoodData foodData = new FoodData();
    protected int jumpTriggerTime;
    public int takeXpDelay;
    private int sleepCounter = 0;
    protected boolean wasUnderwater;
    private final Abilities abilities = new Abilities();
    public int experienceLevel = 0;
    public int totalExperience = 0;
    public float experienceProgress = 0.0F;
    protected int enchantmentSeed = 0;
    protected final float defaultFlySpeed = 0.02F;
    private int lastLevelUpTime;
    private final GameProfile gameProfile;
    private boolean reducedDebugInfo;
    private ItemStack lastItemInMainHand = ItemStack.EMPTY;
    private final ItemCooldowns cooldowns = this.createItemCooldowns();
    private Optional<GlobalPos> lastDeathLocation = Optional.empty();
    public @Nullable FishingHook fishing;
    protected float hurtDir;
    private final java.util.Collection<MutableComponent> prefixes = new java.util.LinkedList<>();
    private final java.util.Collection<MutableComponent> suffixes = new java.util.LinkedList<>();
    @Nullable
    private Pose forcedPose = null;

    public Player(Level level, GameProfile gameProfile) {
        super(EntityType.PLAYER, level);
        this.setUUID(gameProfile.id());
        this.gameProfile = gameProfile;
        this.inventory = new Inventory(this, this.equipment);
        this.inventoryMenu = new InventoryMenu(this.inventory, !level.isClientSide(), this);
        this.containerMenu = this.inventoryMenu;
    }

    @Override
    protected EntityEquipment createEquipment() {
        return new PlayerEquipment(this);
    }

    public boolean blockActionRestricted(Level level, BlockPos pos, GameType gameType) {
        if (!gameType.isBlockPlacingRestricted()) {
            return false;
        } else if (gameType == GameType.SPECTATOR) {
            return true;
        } else if (this.mayBuild()) {
            return false;
        } else {
            ItemStack itemStack = this.getMainHandItem();
            return itemStack.isEmpty() || !itemStack.canBreakBlockInAdventureMode(new BlockInWorld(level, pos, false));
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
            .add(Attributes.ATTACK_DAMAGE, 1.0)
            .add(Attributes.MOVEMENT_SPEED, 0.1F)
            .add(Attributes.ATTACK_SPEED)
            .add(Attributes.LUCK)
            .add(Attributes.BLOCK_INTERACTION_RANGE)
            .add(Attributes.BLOCK_BREAK_SPEED)
            .add(Attributes.SUBMERGED_MINING_SPEED)
            .add(Attributes.SNEAKING_SPEED)
            .add(Attributes.MINING_EFFICIENCY)
            .add(Attributes.SWEEPING_DAMAGE_RATIO)
            .add(Attributes.WAYPOINT_TRANSMIT_RANGE, 6.0E7)
            .add(Attributes.WAYPOINT_RECEIVE_RANGE, 6.0E7)
            .add(net.neoforged.neoforge.common.NeoForgeMod.CREATIVE_FLIGHT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_PLAYER_ABSORPTION_ID, 0.0F);
        entityData.define(DATA_SCORE_ID, 0);
        entityData.define(DATA_SHOULDER_PARROT_LEFT, OptionalInt.empty());
        entityData.define(DATA_SHOULDER_PARROT_RIGHT, OptionalInt.empty());
    }

    @Override
    public void tick() {
        net.neoforged.neoforge.event.EventHooks.firePlayerTickPre(this);
        this.noPhysics = this.isSpectator();
        if (this.isSpectator() || this.isPassenger()) {
            this.setOnGround(false);
        }

        if (this.takeXpDelay > 0) {
            this.takeXpDelay--;
        }

        if (this.isSleeping()) {
            this.sleepCounter++;
            if (this.sleepCounter > 100) {
                this.sleepCounter = 100;
            }

            if (!this.level().isClientSide()
                && !net.neoforged.neoforge.event.EventHooks.canEntityContinueSleeping(this, !this.level().environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, this.position()).canSleep(this.level()) ? BedSleepingProblem.OTHER_PROBLEM : null)) {
                this.stopSleepInBed(false, true);
            }
        } else if (this.sleepCounter > 0) {
            this.sleepCounter++;
            if (this.sleepCounter >= 110) {
                this.sleepCounter = 0;
            }
        }

        this.updateIsUnderwater();
        super.tick();
        int maxPositionOffset = 29999999;
        double nx = Mth.clamp(this.getX(), -2.9999999E7, 2.9999999E7);
        double nz = Mth.clamp(this.getZ(), -2.9999999E7, 2.9999999E7);
        if (nx != this.getX() || nz != this.getZ()) {
            this.setPos(nx, this.getY(), nz);
        }

        this.attackStrengthTicker++;
        this.itemSwapTicker++;
        ItemStack mainHandItemStack = this.getMainHandItem();
        if (!ItemStack.matches(this.lastItemInMainHand, mainHandItemStack)) {
            if (!ItemStack.isSameItem(this.lastItemInMainHand, mainHandItemStack)) {
                this.resetAttackStrengthTicker();
            }

            this.lastItemInMainHand = mainHandItemStack.copy();
        }

        if (!this.isEyeInFluid(FluidTags.WATER) && this.isEquipped(Items.TURTLE_HELMET)) {
            this.turtleHelmetTick();
        }

        this.cooldowns.tick();
        this.updatePlayerPose();
        net.neoforged.neoforge.event.EventHooks.firePlayerTickPost(this);
    }

    @Override
    protected float getMaxHeadRotationRelativeToBody() {
        return this.isBlocking() ? 15.0F : super.getMaxHeadRotationRelativeToBody();
    }

    public boolean isSecondaryUseActive() {
        return this.isShiftKeyDown();
    }

    protected boolean wantsToStopRiding() {
        return this.isShiftKeyDown();
    }

    protected boolean isStayingOnGroundSurface() {
        return this.isShiftKeyDown();
    }

    protected boolean updateIsUnderwater() {
        this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
        return this.wasUnderwater;
    }

    @Override
    public void onAboveBubbleColumn(boolean dragDown, BlockPos pos) {
        if (!this.getAbilities().flying) {
            super.onAboveBubbleColumn(dragDown, pos);
        }
    }

    @Override
    public void onInsideBubbleColumn(boolean dragDown) {
        if (!this.getAbilities().flying) {
            super.onInsideBubbleColumn(dragDown);
        }
    }

    private void turtleHelmetTick() {
        this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
    }

    private boolean isEquipped(Item item) {
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            ItemStack itemStack = this.getItemBySlot(slot);
            Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
            if (itemStack.is(item) && equippable != null && equippable.slot() == slot) {
                return true;
            }
        }

        return false;
    }

    protected ItemCooldowns createItemCooldowns() {
        return new ItemCooldowns();
    }

    protected void updatePlayerPose() {
        if (forcedPose != null) {
            this.setPose(forcedPose);
            return;
        }
        if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.SWIMMING)) {
            Pose desiredPose = this.getDesiredPose();
            Pose actualPose;
            if (this.isSpectator() || this.isPassenger() || this.canPlayerFitWithinBlocksAndEntitiesWhen(desiredPose)) {
                actualPose = desiredPose;
            } else if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)) {
                actualPose = Pose.CROUCHING;
            } else {
                actualPose = Pose.SWIMMING;
            }

            this.setPose(actualPose);
        }
    }

    private Pose getDesiredPose() {
        if (this.isSleeping()) {
            return Pose.SLEEPING;
        } else if (this.isSwimming()) {
            return Pose.SWIMMING;
        } else if (this.isFallFlying()) {
            return Pose.FALL_FLYING;
        } else if (this.isAutoSpinAttack()) {
            return Pose.SPIN_ATTACK;
        } else {
            return this.isShiftKeyDown() && !this.abilities.flying ? Pose.CROUCHING : Pose.STANDING;
        }
    }

    protected boolean canPlayerFitWithinBlocksAndEntitiesWhen(Pose newPose) {
        return this.level().noCollision(this, this.getDimensions(newPose).makeBoundingBox(this.position()).deflate(1.0E-7));
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.PLAYER_SPLASH;
    }

    @Override
    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
    }

    @Override
    public int getDimensionChangingDelay() {
        return 10;
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        this.level().playSound(this, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), volume, pitch);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }

    @Override
    protected int getFireImmuneTicks() {
        return 20;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 9) {
            this.completeUsingItem();
        } else if (id == 23) {
            this.setReducedDebugInfo(false);
        } else if (id == 22) {
            this.setReducedDebugInfo(true);
        } else {
            super.handleEntityEvent(id);
        }
    }

    public void closeContainer() {
        this.containerMenu = this.inventoryMenu;
    }

    protected void doCloseContainer() {
    }

    @Override
    public void rideTick() {
        if (!this.level().isClientSide() && this.wantsToStopRiding() && this.isPassenger()) {
            this.stopRiding();
            this.setShiftKeyDown(false);
        } else {
            super.rideTick();
        }
    }

    @Override
    public void aiStep() {
        if (this.jumpTriggerTime > 0) {
            this.jumpTriggerTime--;
        }

        this.tickRegeneration();
        this.inventory.tick();
        if (this.abilities.flying && !this.isPassenger()) {
            this.resetFallDistance();
        }

        super.aiStep();
        this.updateSwingTime();
        this.yHeadRot = this.getYRot();
        this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        if (this.getHealth() > 0.0F && !this.isSpectator()) {
            AABB pickupArea;
            if (this.isPassenger() && !this.getVehicle().isRemoved()) {
                pickupArea = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0, 0.0, 1.0);
            } else {
                pickupArea = this.getBoundingBox().inflate(1.0, 0.5, 1.0);
            }

            List<Entity> entities = this.level().getEntities(this, pickupArea);
            List<Entity> orbs = Lists.newArrayList();

            for (Entity entity : entities) {
                if (entity.is(EntityType.EXPERIENCE_ORB)) {
                    orbs.add(entity);
                } else if (!entity.isRemoved()) {
                    this.touch(entity);
                }
            }

            if (!orbs.isEmpty()) {
                this.touch(Util.getRandom(orbs, this.random));
            }
        }

        this.handleShoulderEntities();
    }

    protected void tickRegeneration() {
    }

    public void handleShoulderEntities() {
    }

    protected void removeEntitiesOnShoulder() {
    }

    private void touch(Entity entity) {
        entity.playerTouch(this);
    }

    public int getScore() {
        return this.entityData.get(DATA_SCORE_ID);
    }

    public void setScore(int value) {
        this.entityData.set(DATA_SCORE_ID, value);
    }

    public void increaseScore(int amount) {
        int score = this.getScore();
        this.entityData.set(DATA_SCORE_ID, score + amount);
    }

    public void startAutoSpinAttack(int activationTicks, float dmg, ItemStack itemStackUsed) {
        this.autoSpinAttackTicks = activationTicks;
        this.autoSpinAttackDmg = dmg;
        this.autoSpinAttackItemStack = itemStackUsed;
        if (!this.level().isClientSide()) {
            this.removeEntitiesOnShoulder();
            this.setLivingEntityFlag(4, true);
        }
    }

    @Override
    public ItemStack getWeaponItem() {
        return this.isAutoSpinAttack() && this.autoSpinAttackItemStack != null ? this.autoSpinAttackItemStack : super.getWeaponItem();
    }

    @Override
    public void die(DamageSource source) {
        if (net.neoforged.neoforge.common.CommonHooks.onLivingDeath(this, source)) return;
        super.die(source);
        this.reapplyPosition();
        if (!this.isSpectator() && this.level() instanceof ServerLevel level) {
            this.dropAllDeathLoot(level, source);
        }

        if (source != null) {
            this.setDeltaMovement(
                -Mth.cos((this.getHurtDir() + this.getYRot()) * (float) (Math.PI / 180.0)) * 0.1F,
                0.1F,
                -Mth.sin((this.getHurtDir() + this.getYRot()) * (float) (Math.PI / 180.0)) * 0.1F
            );
        } else {
            this.setDeltaMovement(0.0, 0.1, 0.0);
        }

        this.awardStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        this.clearFire();
        this.setSharedFlagOnFire(false);
        this.setLastDeathLocation(Optional.of(GlobalPos.of(this.level().dimension(), this.blockPosition())));
    }

    @Override
    protected void dropEquipment(ServerLevel level) {
        super.dropEquipment(level);
        if (!level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
            this.destroyVanishingCursedItems();
            this.inventory.dropAll();
        }
    }

    protected void destroyVanishingCursedItems() {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack itemStack = this.inventory.getItem(i);
            if (!itemStack.isEmpty() && EnchantmentHelper.has(itemStack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                this.inventory.removeItemNoUpdate(i);
            }
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return source.type().effects().sound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    public void handleCreativeModeItemDrop(ItemStack stack) {
    }

    public @Nullable ItemEntity drop(ItemStack itemStack, boolean thrownFromHand) {
        return net.neoforged.neoforge.common.CommonHooks.onPlayerTossEvent(this, itemStack, false, thrownFromHand);
    }

    /**
     * @deprecated Neo: {@link #getDestroySpeed(BlockState, BlockPos)} instead
     */
    @Deprecated
    public float getDestroySpeed(BlockState state) {
        return getDestroySpeed(state, null);
    }

    public float getDestroySpeed(BlockState state, @Nullable BlockPos pos) {
        float speed = this.inventory.getSelectedItem().getDestroySpeed(state);
        if (speed > 1.0F) {
            speed += (float)this.getAttributeValue(Attributes.MINING_EFFICIENCY);
        }

        if (MobEffectUtil.hasDigSpeed(this)) {
            speed *= 1.0F + (MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2F;
        }

        if (this.hasEffect(MobEffects.MINING_FATIGUE)) {
            float scale = switch (this.getEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };
            speed *= scale;
        }

        speed *= (float)this.getAttributeValue(Attributes.BLOCK_BREAK_SPEED);
        if (this.isEyeInFluid(FluidTags.WATER)) {
            speed *= (float)this.getAttribute(Attributes.SUBMERGED_MINING_SPEED).getValue();
        }

        if (!this.onGround()) {
            speed /= 5.0F;
        }

        speed = net.neoforged.neoforge.event.EventHooks.getBreakSpeed(this, state, speed, pos);
        return speed;
    }

    @Deprecated // Neo: use position sensitive version below
    public boolean hasCorrectToolForDrops(BlockState state) {
        return !state.requiresCorrectToolForDrops() || this.inventory.getSelectedItem().isCorrectToolForDrops(state);
    }

    public boolean hasCorrectToolForDrops(BlockState state, Level level, BlockPos pos) {
        return net.neoforged.neoforge.event.EventHooks.doPlayerHarvestCheck(this, state, level, pos);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setUUID(this.gameProfile.id());
        this.inventory.load(input.listOrEmpty("Inventory", ItemStackWithSlot.CODEC));
        this.inventory.setSelectedSlot(input.getIntOr("SelectedItemSlot", 0));
        this.sleepCounter = input.getShortOr("SleepTimer", (short)0);
        this.experienceProgress = input.getFloatOr("XpP", 0.0F);
        this.experienceLevel = input.getIntOr("XpLevel", 0);
        this.totalExperience = input.getIntOr("XpTotal", 0);
        this.enchantmentSeed = input.getIntOr("XpSeed", 0);
        if (this.enchantmentSeed == 0) {
            this.enchantmentSeed = this.random.nextInt();
        }

        this.setScore(input.getIntOr("Score", 0));
        this.foodData.readAdditionalSaveData(input);
        input.read("abilities", Abilities.Packed.CODEC).ifPresent(this.abilities::apply);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.abilities.getWalkingSpeed());
        this.enderChestInventory.fromSlots(input.listOrEmpty("EnderItems", ItemStackWithSlot.CODEC));
        this.setLastDeathLocation(input.read("LastDeathLocation", GlobalPos.CODEC));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        NbtUtils.addCurrentDataVersion(output);
        this.inventory.save(output.list("Inventory", ItemStackWithSlot.CODEC));
        output.putInt("SelectedItemSlot", this.inventory.getSelectedSlot());
        output.putShort("SleepTimer", (short)this.sleepCounter);
        output.putFloat("XpP", this.experienceProgress);
        output.putInt("XpLevel", this.experienceLevel);
        output.putInt("XpTotal", this.totalExperience);
        output.putInt("XpSeed", this.enchantmentSeed);
        output.putInt("Score", this.getScore());
        this.foodData.addAdditionalSaveData(output);
        output.store("abilities", Abilities.Packed.CODEC, this.abilities.pack());
        this.enderChestInventory.storeAsSlots(output.list("EnderItems", ItemStackWithSlot.CODEC));
        this.lastDeathLocation.ifPresent(pos -> output.store("LastDeathLocation", GlobalPos.CODEC, pos));
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (super.isInvulnerableTo(level, source)) {
            return true;
        } else if (source.is(DamageTypeTags.IS_DROWNING)) {
            return !level.getGameRules().get(GameRules.DROWNING_DAMAGE);
        } else if (source.is(DamageTypeTags.IS_FALL)) {
            return !level.getGameRules().get(GameRules.FALL_DAMAGE);
        } else if (source.is(DamageTypeTags.IS_FIRE)) {
            return !level.getGameRules().get(GameRules.FIRE_DAMAGE);
        } else {
            return source.is(DamageTypeTags.IS_FREEZING) ? !level.getGameRules().get(GameRules.FREEZE_DAMAGE) : false;
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (this.isInvulnerableTo(level, source)) {
            return false;
        } else if (this.abilities.invulnerable && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        } else {
            this.noActionTime = 0;
            if (this.isDeadOrDying()) {
                return false;
            } else {
                this.removeEntitiesOnShoulder();
                damage = Math.max(0.0F, source.type().scaling().getScalingFunction().scaleDamage(source, this, damage, this.level().getDifficulty()));
                if (false && source.scalesWithDifficulty()) {
                    if (level.getDifficulty() == Difficulty.PEACEFUL) {
                        damage = 0.0F;
                    }

                    if (level.getDifficulty() == Difficulty.EASY) {
                        damage = Math.min(damage / 2.0F + 1.0F, damage);
                    }

                    if (level.getDifficulty() == Difficulty.HARD) {
                        damage = damage * 3.0F / 2.0F;
                    }
                }

                return damage == 0.0F ? false : super.hurtServer(level, source, damage);
            }
        }
    }

    @Override
    protected void blockUsingItem(ServerLevel level, LivingEntity attacker) {
        super.blockUsingItem(level, attacker);
        ItemStack itemBlockingWith = this.getItemBlockingWith();
        BlocksAttacks blocksAttacks = itemBlockingWith != null ? itemBlockingWith.get(DataComponents.BLOCKS_ATTACKS) : null;
        float secondsToDisableBlocking = attacker.getSecondsToDisableBlocking();
        if (secondsToDisableBlocking > 0.0F && blocksAttacks != null) {
            blocksAttacks.disable(level, this, secondsToDisableBlocking, itemBlockingWith);
        }
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.getAbilities().invulnerable && super.canBeSeenAsEnemy();
    }

    public boolean canHarmPlayer(Player target) {
        Team team = this.getTeam();
        Team otherTeam = target.getTeam();
        if (team == null) {
            return true;
        } else {
            return !team.isAlliedTo(otherTeam) ? true : team.isAllowFriendlyFire();
        }
    }

    @Override
    protected void hurtArmor(DamageSource damageSource, float damage) {
        this.doHurtEquipment(damageSource, damage, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);
    }

    @Override
    protected void hurtHelmet(DamageSource damageSource, float damage) {
        this.doHurtEquipment(damageSource, damage, EquipmentSlot.HEAD);
    }

    @Override
    protected void actuallyHurt(ServerLevel level, DamageSource source, float dmg) {
        if (!this.isInvulnerableTo(level, source)) {
            // Neo (ImplNote): Keep this in-sync with the equivalent patches in LivingEntity#actuallyHurt.
            // Neo: Primary damage pipeline hooks. Compute the armor and enchantment reductions against the "original" damage, and then fire LivingDamageEvent.Pre
            this.damageContainers.peek().setReduction(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.ARMOR, this.damageContainers.peek().getNewDamage() - this.getDamageAfterArmorAbsorb(source, this.damageContainers.peek().getNewDamage()));
            this.getDamageAfterMagicAbsorb(source, this.damageContainers.peek().getNewDamage());

            // LivingDamageEvent.Pre is then able to mutate the container to change the reductions and change the "real" damage that will be inflicted.
            float damage = net.neoforged.neoforge.common.CommonHooks.onLivingDamagePre(this, this.damageContainers.peek());
            // Throw an exception if someone kills the entity during Pre. Vanilla is going to kill any entity which isDeadOrDying() when it bubbles back up to hurtServer, so it dying early is invalid.
            com.google.common.base.Preconditions.checkArgument(!this.dead, "It is invalid to have killed an entity during LivingDamageEvent.Pre. Use LivingIncomingDamageEvent or LivingDamageEvent.Post instead.");

            // Store a copy of the "true" inflicted damage so we can propagate it back to hurtServer.
            this.damageContainers.peek().captureInflictedDamage();

            // Next we attribute damage to absorption hearts. We record the amount blocked by absorption in the container for later calculations.
            // Mods reduction functions may adjust the split of damage between absorption and health, so we need to first apply the reductions and then compute the amounts that go to each pool.
            this.damageContainers.peek().setReduction(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.ABSORPTION, Math.min(this.getAbsorptionAmount(), damage));
            float absorbed = Math.min(damage, this.damageContainers.peek().getReduction(net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction.ABSORPTION));
            this.setAbsorptionAmount(Math.max(0, this.getAbsorptionAmount() - absorbed));
            float var8 = this.damageContainers.peek().getNewDamage();
            float absorbedDamage = absorbed;
            if (absorbedDamage > 0.0F && absorbedDamage < 3.4028235E37F) {
                this.awardStat(Stats.DAMAGE_ABSORBED, Math.round(absorbedDamage * 10.0F));
            }

            // Last thing here, inflict damage to the entity's health, based on how much damage is remaining after absorption has been processes.
            if (var8 != 0.0F) {
                this.causeFoodExhaustion(source.getFoodExhaustion());
                this.getCombatTracker().recordDamage(source, var8);
                this.setHealth(this.getHealth() - var8);
                if (var8 < 3.4028235E37F) {
                    this.awardStat(Stats.DAMAGE_TAKEN, Math.round(var8 * 10.0F));
                }

                this.gameEvent(GameEvent.ENTITY_DAMAGE);
            }

            // Neo: Now we go back to hurtServer, where we'll update various metadata fields, process the side effects of damage (i.e. killing the entity), and firing LivingDamageEvent.Post
        }
    }

    public boolean isTextFilteringEnabled() {
        return false;
    }

    public void openTextEdit(SignBlockEntity sign, boolean isFrontText) {
    }

    public void openMinecartCommandBlock(MinecartCommandBlock commandBlock) {
    }

    public void openCommandBlock(CommandBlockEntity commandBlock) {
    }

    public void openStructureBlock(StructureBlockEntity structureBlock) {
    }

    public void openTestBlock(TestBlockEntity testBlock) {
    }

    public void openTestInstanceBlock(TestInstanceBlockEntity testInstanceBlock) {
    }

    public void openJigsawBlock(JigsawBlockEntity jigsawBlock) {
    }

    public void openHorseInventory(AbstractHorse horse, Container container) {
    }

    public void openNautilusInventory(AbstractNautilus nautilus, Container container) {
    }

    public OptionalInt openMenu(@Nullable MenuProvider provider) {
        return OptionalInt.empty();
    }

    public void openDialog(Holder<Dialog> dialog) {
    }

    public void sendMerchantOffers(int containerId, MerchantOffers offers, int merchantLevel, int merchantXp, boolean showProgressBar, boolean canRestock) {
    }

    public void openItemGui(ItemStack itemStack, InteractionHand hand) {
    }

    public InteractionResult interactOn(Entity entity, InteractionHand hand, Vec3 location) {
        if (this.isSpectator()) {
            if (entity instanceof MenuProvider) {
                this.openMenu((MenuProvider)entity);
            }

            return InteractionResult.PASS;
        } else {
            InteractionResult cancelResult = net.neoforged.neoforge.common.CommonHooks.onInteractEntity(this, entity, hand);
            if (cancelResult != null) return cancelResult;
            ItemStack itemStack = this.getItemInHand(hand);
            ItemStack itemStackClone = itemStack.copy();
            InteractionResult interact = entity.interact(this, hand, location);
            if (interact.consumesAction()) {
                if (this.hasInfiniteMaterials() && itemStack == this.getItemInHand(hand) && itemStack.getCount() < itemStackClone.getCount()) {
                    itemStack.setCount(itemStackClone.getCount());
                }

                if (!this.abilities.instabuild && itemStack.isEmpty()) {
                    net.neoforged.neoforge.event.EventHooks.onPlayerDestroyItem(this, itemStackClone, hand);
                }

                return interact;
            } else {
                if (!itemStack.isEmpty() && entity instanceof LivingEntity) {
                    if (this.hasInfiniteMaterials()) {
                        itemStack = itemStackClone;
                    }

                    InteractionResult interactionResult = itemStack.interactLivingEntity(this, (LivingEntity)entity, hand);
                    if (interactionResult.consumesAction()) {
                        this.level().gameEvent(GameEvent.ENTITY_INTERACT, entity.position(), GameEvent.Context.of(this));
                        if (itemStack.isEmpty() && !this.hasInfiniteMaterials()) {
                            net.neoforged.neoforge.event.EventHooks.onPlayerDestroyItem(this, itemStackClone, hand);
                            this.setItemInHand(hand, ItemStack.EMPTY);
                        }

                        return interactionResult;
                    }
                }

                return InteractionResult.PASS;
            }
        }
    }

    @Override
    public void removeVehicle() {
        super.removeVehicle();
        this.boardingCooldown = 0;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isSleeping();
    }

    @Override
    public boolean isAffectedByFluids() {
        return !this.abilities.flying;
    }

    @Override
    // Forge: Don't update this method to use IForgeEntity#getStepHeight() - https://github.com/MinecraftForge/MinecraftForge/issues/8922
    protected Vec3 maybeBackOffFromEdge(Vec3 delta, MoverType moverType) {
        float maxDownStep = this.maxUpStep();
        if (!this.abilities.flying
            && !(delta.y > 0.0)
            && (moverType == MoverType.SELF || moverType == MoverType.PLAYER)
            && this.isStayingOnGroundSurface()
            && this.isAboveGround(maxDownStep)) {
            double deltaX = delta.x;
            double deltaZ = delta.z;
            double step = 0.05;
            double stepX = Math.signum(deltaX) * 0.05;

            double stepZ;
            for (stepZ = Math.signum(deltaZ) * 0.05; deltaX != 0.0 && this.canFallAtLeast(deltaX, 0.0, maxDownStep); deltaX -= stepX) {
                if (Math.abs(deltaX) <= 0.05) {
                    deltaX = 0.0;
                    break;
                }
            }

            while (deltaZ != 0.0 && this.canFallAtLeast(0.0, deltaZ, maxDownStep)) {
                if (Math.abs(deltaZ) <= 0.05) {
                    deltaZ = 0.0;
                    break;
                }

                deltaZ -= stepZ;
            }

            while (deltaX != 0.0 && deltaZ != 0.0 && this.canFallAtLeast(deltaX, deltaZ, maxDownStep)) {
                if (Math.abs(deltaX) <= 0.05) {
                    deltaX = 0.0;
                } else {
                    deltaX -= stepX;
                }

                if (Math.abs(deltaZ) <= 0.05) {
                    deltaZ = 0.0;
                } else {
                    deltaZ -= stepZ;
                }
            }

            return new Vec3(deltaX, delta.y, deltaZ);
        } else {
            return delta;
        }
    }

    // Forge: Don't update this method to use IForgeEntity#getStepHeight() - https://github.com/MinecraftForge/MinecraftForge/issues/9376
    private boolean isAboveGround(float maxDownStep) {
        return this.onGround() || this.fallDistance < maxDownStep && !this.canFallAtLeast(0.0, 0.0, maxDownStep - this.fallDistance);
    }

    private boolean canFallAtLeast(double deltaX, double deltaZ, double minHeight) {
        AABB boundingBox = this.getBoundingBox();
        return this.level()
            .noCollision(
                this,
                new AABB(
                    boundingBox.minX + 1.0E-7 + deltaX,
                    boundingBox.minY - minHeight - 1.0E-7,
                    boundingBox.minZ + 1.0E-7 + deltaZ,
                    boundingBox.maxX - 1.0E-7 + deltaX,
                    boundingBox.minY,
                    boundingBox.maxZ - 1.0E-7 + deltaZ
                )
            );
    }

    public void attack(Entity entity) {
        if (!net.neoforged.neoforge.common.CommonHooks.onPlayerAttackTarget(this, entity)) return;
        if (!this.cannotAttack(entity)) {
            float baseDamage = this.isAutoSpinAttack() ? this.autoSpinAttackDmg : (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            ItemStack attackingItemStack = this.getWeaponItem();
            DamageSource damageSource = this.createAttackSource(attackingItemStack);
            float attackStrengthScale = this.getAttackStrengthScale(0.5F);
            float magicBoost = attackStrengthScale * (this.getEnchantedDamage(entity, baseDamage, damageSource) - baseDamage);
            baseDamage *= this.baseDamageScaleFactor();
            if (!this.deflectProjectile(entity)) {
                if (baseDamage > 0.0F || magicBoost > 0.0F) {
                    boolean fullStrengthAttack = attackStrengthScale > 0.9F;
                    boolean knockbackAttack;
                    if (this.isSprinting() && fullStrengthAttack) {
                        this.playServerSideSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK);
                        knockbackAttack = true;
                    } else {
                        knockbackAttack = false;
                    }

                    baseDamage += attackingItemStack.getItem().getAttackDamageBonus(entity, baseDamage, damageSource);
                    boolean criticalAttack = fullStrengthAttack && this.canCriticalAttack(entity);
                    // Neo: Fire the critical hit event and override the critical hit status and damage multiplier based on the event.
                    // The boolean local above (flag2) is the vanilla critical hit result.
                    var critEvent = net.neoforged.neoforge.common.CommonHooks.fireCriticalHit(this, entity, criticalAttack, criticalAttack ? 1.5F : 1.0F);
                    criticalAttack = critEvent.isCriticalHit();
                    if (criticalAttack) {
                        baseDamage *= critEvent.getDamageMultiplier();
                    }

                    float totalDamage = baseDamage + magicBoost;
                    // Neo: Replace !flag2 (!isCriticalHit) with the logic from the CriticalHitEvent.
                    boolean sweepAttack = this.isSweepAttack(fullStrengthAttack, critEvent.isCriticalHit() && critEvent.disableSweep(), knockbackAttack);

                    // Neo: Fire the SweepAttackEvent and overwrite the value of flag3 (the local controlling if a sweep will occur).
                    var sweepEvent = net.neoforged.neoforge.common.CommonHooks.fireSweepAttack(this, entity, sweepAttack);
                    sweepAttack = sweepEvent.isSweeping();

                    float oldLivingEntityHealth = 0.0F;
                    if (entity instanceof LivingEntity livingTarget) {
                        oldLivingEntityHealth = livingTarget.getHealth();
                    }

                    Vec3 oldMovement = entity.getDeltaMovement();
                    boolean wasHurt = entity.hurtOrSimulate(damageSource, totalDamage);
                    if (wasHurt) {
                        this.causeExtraKnockback(entity, this.getKnockback(entity, damageSource) + (knockbackAttack ? 0.5F : 0.0F), oldMovement);
                        if (sweepAttack) {
                            var sweepHitBox = attackingItemStack.getSweepHitBox(this, entity);
                            this.doSweepAttack(entity, baseDamage, damageSource, attackStrengthScale, sweepHitBox);
                        }

                        this.attackVisualEffects(entity, criticalAttack, sweepAttack, fullStrengthAttack, false, magicBoost);
                        this.setLastHurtMob(entity);
                        this.itemAttackInteraction(entity, attackingItemStack, damageSource, true);
                        this.damageStatsAndHearts(entity, oldLivingEntityHealth);
                        this.causeFoodExhaustion(0.1F);
                    } else {
                        this.playServerSideSound(SoundEvents.PLAYER_ATTACK_NODAMAGE);
                    }
                }

                this.postPiercingAttack();
            }
            // Neo: Moved from beginning of attack() so that getAttackStrengthScale() returns an accurate value during all attack events
            this.onAttack();
        }
    }

    private void playServerSideSound(SoundEvent sound) {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), 1.0F, 1.0F);
    }

    private DamageSource createAttackSource(ItemStack attackingItemStack) {
        return attackingItemStack.getDamageSource(this, () -> this.damageSources().playerAttack(this));
    }

    private boolean cannotAttack(Entity entity) {
        return !entity.isAttackable() ? true : entity.skipAttackInteraction(this);
    }

    private boolean deflectProjectile(Entity entity) {
        if (entity.is(EntityTypeTags.REDIRECTABLE_PROJECTILE)
            && entity instanceof Projectile projectile
            && projectile.deflect(ProjectileDeflection.AIM_DEFLECT, this, EntityReference.of(this), true)) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource());
            return true;
        } else {
            return false;
        }
    }

    private boolean canCriticalAttack(Entity entity) {
        return this.fallDistance > 0.0
            && !this.onGround()
            && !this.onClimbable()
            && !this.isInWater()
            && !this.isMobilityRestricted()
            && !this.isPassenger()
            && entity instanceof LivingEntity
            && !this.isSprinting();
    }

    private boolean isSweepAttack(boolean fullStrengthAttack, boolean criticalAttack, boolean knockbackAttack) {
        if (fullStrengthAttack && !criticalAttack && !knockbackAttack && this.onGround()) {
            double approximateSpeedSq = this.getKnownMovement().horizontalDistanceSqr();
            double maxSpeedForSweepAttack = this.getSpeed() * 2.5;
            if (approximateSpeedSq < Mth.square(maxSpeedForSweepAttack)) {
                // Neo: Make sweep attacks check SWORD_SWEEP instead of ItemTags.SWORDS.
                return this.getItemInHand(InteractionHand.MAIN_HAND).canPerformAction(net.neoforged.neoforge.common.ItemAbilities.SWORD_SWEEP);
            }
        }

        return false;
    }

    private void attackVisualEffects(
        Entity entity, boolean criticalAttack, boolean sweepAttack, boolean fullStrengthAttack, boolean stabAttack, float magicBoost
    ) {
        if (criticalAttack) {
            this.playServerSideSound(SoundEvents.PLAYER_ATTACK_CRIT);
            this.crit(entity);
        }

        if (!criticalAttack && !sweepAttack && !stabAttack) {
            this.playServerSideSound(fullStrengthAttack ? SoundEvents.PLAYER_ATTACK_STRONG : SoundEvents.PLAYER_ATTACK_WEAK);
        }

        if (magicBoost > 0.0F) {
            this.magicCrit(entity);
        }
    }

    private void damageStatsAndHearts(Entity entity, float oldLivingEntityHealth) {
        if (entity instanceof LivingEntity) {
            float actualDamage = oldLivingEntityHealth - ((LivingEntity)entity).getHealth();
            this.awardStat(Stats.DAMAGE_DEALT, Math.round(actualDamage * 10.0F));
            if (this.level() instanceof ServerLevel && actualDamage > 2.0F) {
                int count = (int)(actualDamage * 0.5);
                ((ServerLevel)this.level())
                    .sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(0.5), entity.getZ(), count, 0.1, 0.0, 0.1, 0.2);
            }
        }
    }

    private void itemAttackInteraction(Entity entity, ItemStack attackingItemStack, DamageSource damageSource, boolean applyToTarget) {
        Entity hurtTarget = entity;
        if (entity instanceof net.neoforged.neoforge.entity.PartEntity<?> partEntity) {
            hurtTarget = partEntity.getParent();
        }

        boolean itemHurtEnemy = false;
        ItemStack copy = attackingItemStack.copy();
        if (this.level() instanceof ServerLevel serverLevel) {
            if (hurtTarget instanceof LivingEntity livingTarget) {
                itemHurtEnemy = attackingItemStack.hurtEnemy(livingTarget, this);
            }

            if (applyToTarget) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, entity, damageSource, attackingItemStack);
            }
        }

        if (!this.level().isClientSide() && !attackingItemStack.isEmpty() && hurtTarget instanceof LivingEntity) {
            if (itemHurtEnemy) {
                attackingItemStack.postHurtEnemy((LivingEntity)hurtTarget, this);
            }

            if (attackingItemStack.isEmpty()) {
                net.neoforged.neoforge.event.EventHooks.onPlayerDestroyItem(this, copy, attackingItemStack == this.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
                if (attackingItemStack == this.getMainHandItem()) {
                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                } else {
                    this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public void causeExtraKnockback(Entity entity, float knockbackAmount, Vec3 oldMovement) {
        if (knockbackAmount > 0.0F) {
            if (entity instanceof LivingEntity livingTarget) {
                livingTarget.knockback(
                    knockbackAmount, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0))
                );
            } else {
                entity.push(
                    -Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)) * knockbackAmount,
                    0.1,
                    Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)) * knockbackAmount
                );
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            this.setSprinting(false);
        }

        if (entity instanceof ServerPlayer && entity.hurtMarked) {
            ((ServerPlayer)entity).connection.send(new ClientboundSetEntityMotionPacket(entity));
            entity.hurtMarked = false;
            entity.setDeltaMovement(oldMovement);
        }
    }

    @Override
    public float getVoicePitch() {
        return 1.0F;
    }

    // Neo: Use version that allows the sweep hitbox to be specified
    @Deprecated
    private void doSweepAttack(Entity entity, float baseDamage, DamageSource damageSource, float attackStrengthScale) {
        doSweepAttack(entity, baseDamage, damageSource, attackStrengthScale, entity.getBoundingBox().inflate(1.0, 0.25, 1.0));
    }

    private void doSweepAttack(Entity entity, float baseDamage, DamageSource damageSource, float attackStrengthScale, AABB sweepHitBox) {
        this.playServerSideSound(SoundEvents.PLAYER_ATTACK_SWEEP);
        if (this.level() instanceof ServerLevel serverLevel) {
            float var12 = 1.0F + (float)this.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * baseDamage;

            double entityReachSq = Mth.square(this.entityInteractionRange()); // Use entity reach instead of constant 9.0. Vanilla uses bottom center-to-center checks here, so don't update this to use canReach, since it uses closest-corner checks.
            for (LivingEntity nearby : this.level().getEntitiesOfClass(LivingEntity.class, sweepHitBox)) {
                if (nearby != this
                    && nearby != entity
                    && !this.isAlliedTo(nearby)
                    && !(nearby instanceof ArmorStand armorStand && armorStand.isMarker())
                    && this.distanceToSqr(nearby) < entityReachSq) {
                    float enchantedDamage = this.getEnchantedDamage(nearby, var12, damageSource) * attackStrengthScale;
                    if (nearby.hurtServer(serverLevel, damageSource, enchantedDamage)) {
                        nearby.knockback(0.4F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
                        EnchantmentHelper.doPostAttackEffects(serverLevel, nearby, damageSource);
                    }
                }
            }

            double dx = -Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
            double dz = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + dx, this.getY(0.5), this.getZ() + dz, 0, dx, 0.0, dz, 0.0);
        }
    }

    protected float getEnchantedDamage(Entity entity, float dmg, DamageSource damageSource) {
        return dmg;
    }

    @Override
    protected void doAutoAttackOnTouch(LivingEntity entity) {
        this.attack(entity);
    }

    public void crit(Entity entity) {
    }

    private float baseDamageScaleFactor() {
        float attackStrengthScale = this.getAttackStrengthScale(0.5F);
        return 0.2F + attackStrengthScale * attackStrengthScale * 0.8F;
    }

    @Override
    public boolean stabAttack(EquipmentSlot slot, Entity target, float baseDamage, boolean dealsDamage, boolean dealsKnockback, boolean dismounts) {
        if (this.cannotAttack(target)) {
            return false;
        } else {
            ItemStack weaponItem = this.getItemBySlot(slot);
            DamageSource damageSource = this.createAttackSource(weaponItem);
            float magicBoost = this.getEnchantedDamage(target, baseDamage, damageSource) - baseDamage;
            if (!this.isUsingItem() || this.getUsedItemHand().asEquipmentSlot() != slot) {
                magicBoost *= this.getAttackStrengthScale(0.5F);
                baseDamage *= this.baseDamageScaleFactor();
            }

            if (dealsKnockback && this.deflectProjectile(target)) {
                return true;
            } else {
                float totalDamage = dealsDamage ? baseDamage + magicBoost : 0.0F;
                float oldLivingEntityHealth = 0.0F;
                if (target instanceof LivingEntity livingTarget) {
                    oldLivingEntityHealth = livingTarget.getHealth();
                }

                Vec3 oldMovement = target.getDeltaMovement();
                boolean wasHurt = dealsDamage && target.hurtOrSimulate(damageSource, totalDamage);
                if (dealsKnockback) {
                    this.causeExtraKnockback(target, 0.4F + this.getKnockback(target, damageSource), oldMovement);
                }

                boolean dismounted = false;
                if (dismounts && target.isPassenger()) {
                    dismounted = true;
                    target.stopRiding();
                }

                if (!wasHurt && !dealsKnockback && !dismounted) {
                    return false;
                } else {
                    this.attackVisualEffects(target, false, false, dealsDamage, true, magicBoost);
                    this.setLastHurtMob(target);
                    this.itemAttackInteraction(target, weaponItem, damageSource, wasHurt);
                    this.damageStatsAndHearts(target, oldLivingEntityHealth);
                    this.causeFoodExhaustion(0.1F);
                    return true;
                }
            }
        }
    }

    public void magicCrit(Entity entity) {
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        this.inventoryMenu.removed(this);
        if (this.hasContainerOpen()) {
            this.doCloseContainer();
        }
    }

    @Override
    public boolean isClientAuthoritative() {
        return true;
    }

    @Override
    protected boolean isLocalClientAuthoritative() {
        return this.isLocalPlayer();
    }

    public boolean isLocalPlayer() {
        return false;
    }

    @Override
    public boolean canSimulateMovement() {
        return !this.level().isClientSide() || this.isLocalPlayer();
    }

    @Override
    public boolean isEffectiveAi() {
        return !this.level().isClientSide() || this.isLocalPlayer();
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public NameAndId nameAndId() {
        return new NameAndId(this.gameProfile);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Abilities getAbilities() {
        return this.abilities;
    }

    @Override
    public boolean hasInfiniteMaterials() {
        return this.abilities.instabuild;
    }

    public boolean preventsBlockDrops() {
        return this.abilities.instabuild;
    }

    public void updateTutorialInventoryAction(ItemStack itemCarried, ItemStack itemInSlot, ClickAction clickAction) {
    }

    public boolean hasContainerOpen() {
        return this.containerMenu != this.inventoryMenu;
    }

    public boolean canDropItems() {
        return true;
    }

    public Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos pos) {
        this.startSleeping(pos);
        this.sleepCounter = 0;
        return Either.right(Unit.INSTANCE);
    }

    public void stopSleepInBed(boolean forcefulWakeUp, boolean updateLevelList) {
        net.neoforged.neoforge.event.EventHooks.onPlayerWakeup(this, forcefulWakeUp, updateLevelList);
        super.stopSleeping();
        if (this.level() instanceof ServerLevel && updateLevelList) {
            ((ServerLevel)this.level()).updateSleepingPlayerList();
        }

        this.sleepCounter = forcefulWakeUp ? 0 : 100;
    }

    @Override
    public void stopSleeping() {
        this.stopSleepInBed(true, true);
    }

    public boolean isSleepingLongEnough() {
        return this.isSleeping() && this.sleepCounter >= 100;
    }

    public int getSleepTimer() {
        return this.sleepCounter;
    }

    public void sendSystemMessage(Component message) {
    }

    public void sendOverlayMessage(Component message) {
    }

    public void awardStat(Identifier location) {
        this.awardStat(Stats.CUSTOM.get(location));
    }

    public void awardStat(Identifier location, int count) {
        this.awardStat(Stats.CUSTOM.get(location), count);
    }

    public void awardStat(Stat<?> stat) {
        this.awardStat(stat, 1);
    }

    public void awardStat(Stat<?> stat, int count) {
    }

    public void resetStat(Stat<?> stat) {
    }

    public int awardRecipes(Collection<RecipeHolder<?>> recipes) {
        return 0;
    }

    public void triggerRecipeCrafted(RecipeHolder<?> recipe, List<ItemStack> itemStacks) {
    }

    public void awardRecipesByKey(List<ResourceKey<Recipe<?>>> recipeIds) {
    }

    public int resetRecipes(Collection<RecipeHolder<?>> recipe) {
        return 0;
    }

    @Override
    public void travel(Vec3 input) {
        if (this.isPassenger()) {
            super.travel(input);
        } else {
            if (this.isSwimming()) {
                double lookAngleY = this.getLookAngle().y;
                double multiplier = lookAngleY < -0.2 ? 0.085 : 0.06;
                if (lookAngleY <= 0.0
                    || this.jumping
                    || !this.level().getFluidState(BlockPos.containing(this.getX(), this.getY() + 1.0 - 0.1, this.getZ())).isEmpty()) {
                    Vec3 movement = this.getDeltaMovement();
                    this.setDeltaMovement(movement.add(0.0, (lookAngleY - movement.y) * multiplier, 0.0));
                }
            }

            if (this.getAbilities().flying) {
                double originalMovementY = this.getDeltaMovement().y;
                super.travel(input);
                this.setDeltaMovement(this.getDeltaMovement().with(Direction.Axis.Y, originalMovementY * 0.6));
            } else {
                super.travel(input);
            }
        }
    }

    @Override
    protected boolean canGlide() {
        return !this.abilities.flying && super.canGlide();
    }

    @Override
    public void updateSwimming() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        } else {
            super.updateSwimming();
        }
    }

    protected boolean freeAt(BlockPos pos) {
        return !this.level().getBlockState(pos).isSuffocating(this.level(), pos);
    }

    @Override
    public float getSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageModifier, DamageSource damageSource) {
        if (this.mayFly()) {
            net.neoforged.neoforge.event.EventHooks.onPlayerFall(this, damageModifier, damageModifier);
            return false;
        } else {
            if (fallDistance >= 2.0) {
                this.awardStat(Stats.FALL_ONE_CM, (int)Math.round(fallDistance * 100.0));
            }

            return super.causeFallDamage(fallDistance, damageModifier, damageSource);
        }
    }

    public boolean tryToStartFallFlying() {
        if (!this.isFallFlying() && this.canGlide() && !this.isInWater()) {
            this.startFallFlying();
            return true;
        } else {
            return false;
        }
    }

    public void startFallFlying() {
        this.setSharedFlag(7, true);
    }

    @Override
    protected void doWaterSplashEffect() {
        if (!this.isSpectator()) {
            super.doWaterSplashEffect();
        }
    }

    @Override
    protected void playStepSound(BlockPos onPos, BlockState onState) {
        if (this.isInWater()) {
            this.waterSwimSound();
            this.playMuffledStepSound(onState, onPos);
        } else {
            BlockPos primaryStepSoundPos = this.getPrimaryStepSoundBlockPos(onPos);
            if (!onPos.equals(primaryStepSoundPos)) {
                BlockState primaryStepState = this.level().getBlockState(primaryStepSoundPos);
                if (primaryStepState.is(BlockTags.COMBINATION_STEP_SOUND_BLOCKS)) {
                    this.playCombinationStepSounds(primaryStepState, onState, primaryStepSoundPos, onPos);
                } else {
                    super.playStepSound(primaryStepSoundPos, primaryStepState);
                }
            } else {
                super.playStepSound(onPos, onState);
            }
        }
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_BIG_FALL);
    }

    @Override
    public boolean killedEntity(ServerLevel level, LivingEntity entity, DamageSource source) {
        this.awardStat(Stats.ENTITY_KILLED.get(entity.getType()));
        return true;
    }

    @Override
    public void makeStuckInBlock(BlockState blockState, Vec3 speedMultiplier) {
        if (!this.abilities.flying) {
            super.makeStuckInBlock(blockState, speedMultiplier);
        }

        this.tryResetCurrentImpulseContext();
    }

    public void giveExperiencePoints(int i) {
        net.neoforged.neoforge.event.entity.player.PlayerXpEvent.XpChange event = new net.neoforged.neoforge.event.entity.player.PlayerXpEvent.XpChange(this, i);
        if (net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled()) return;
        i = event.getAmount();

        this.increaseScore(i);
        this.experienceProgress = this.experienceProgress + (float)i / this.getXpNeededForNextLevel();
        this.totalExperience = Mth.clamp(this.totalExperience + i, 0, Integer.MAX_VALUE);

        while (this.experienceProgress < 0.0F) {
            float remaining = this.experienceProgress * this.getXpNeededForNextLevel();
            if (this.experienceLevel > 0) {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 1.0F + remaining / this.getXpNeededForNextLevel();
            } else {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 0.0F;
            }
        }

        while (this.experienceProgress >= 1.0F) {
            this.experienceProgress = (this.experienceProgress - 1.0F) * this.getXpNeededForNextLevel();
            this.giveExperienceLevels(1);
            this.experienceProgress = this.experienceProgress / this.getXpNeededForNextLevel();
        }
    }

    public int getEnchantmentSeed() {
        return this.enchantmentSeed;
    }

    public void onEnchantmentPerformed(ItemStack itemStack, int enchantmentCost) {
        giveExperienceLevels(-enchantmentCost);
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        this.enchantmentSeed = this.random.nextInt();
    }

    public void giveExperienceLevels(int amount) {
        net.neoforged.neoforge.event.entity.player.PlayerXpEvent.LevelChange event = new net.neoforged.neoforge.event.entity.player.PlayerXpEvent.LevelChange(this, amount);
        if (net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled()) return;
        amount = event.getLevels();

        this.experienceLevel = IntMath.saturatedAdd(this.experienceLevel, amount);
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        if (amount > 0 && this.experienceLevel % 5 == 0 && this.lastLevelUpTime < this.tickCount - 100.0F) {
            float vol = this.experienceLevel > 30 ? 1.0F : this.experienceLevel / 30.0F;
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), vol * 0.75F, 1.0F);
            this.lastLevelUpTime = this.tickCount;
        }
    }

    public int getXpNeededForNextLevel() {
        if (this.experienceLevel >= 30) {
            return 112 + (this.experienceLevel - 30) * 9;
        } else {
            return this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2;
        }
    }

    public void causeFoodExhaustion(float amount) {
        if (!this.abilities.invulnerable) {
            if (!this.level().isClientSide()) {
                this.foodData.addExhaustion(amount);
            }
        }
    }

    protected boolean hasEnoughFoodToDoExhaustiveManoeuvres() {
        return this.getFoodData().hasEnoughFood() || this.mayFly();
    }

    public Optional<WardenSpawnTracker> getWardenSpawnTracker() {
        return Optional.empty();
    }

    public FoodData getFoodData() {
        return this.foodData;
    }

    public boolean canEat(boolean canAlwaysEat) {
        return this.abilities.invulnerable || canAlwaysEat || this.foodData.needsFood();
    }

    public boolean isHurt() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    public boolean mayBuild() {
        return this.abilities.mayBuild;
    }

    public boolean mayUseItemAt(BlockPos pos, Direction direction, ItemStack itemStack) {
        if (this.abilities.mayBuild) {
            return true;
        } else {
            BlockPos target = pos.relative(direction.getOpposite());
            BlockInWorld block = new BlockInWorld(this.level(), target, false);
            return itemStack.canPlaceOnBlockInAdventureMode(block);
        }
    }

    @Override
    protected int getBaseExperienceReward(ServerLevel level) {
        return !level.getGameRules().get(GameRules.KEEP_INVENTORY) && !this.isSpectator() ? Math.min(this.experienceLevel * 7, 100) : 0;
    }

    @Override
    protected boolean isAlwaysExperienceDropper() {
        return true;
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return this.abilities.flying || this.onGround() && this.isDiscrete() ? Entity.MovementEmission.NONE : Entity.MovementEmission.ALL;
    }

    public void onUpdateAbilities() {
    }

    @Override
    public Component getName() {
        return Component.literal(this.gameProfile.name());
    }

    @Override
    public String getPlainTextName() {
        return this.gameProfile.name();
    }

    public PlayerEnderChestContainer getEnderChestInventory() {
        return this.enderChestInventory;
    }

    @Override
    protected boolean doesEmitEquipEvent(EquipmentSlot slot) {
        return slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
    }

    public boolean addItem(ItemStack itemStack) {
        return this.inventory.add(itemStack);
    }

    public abstract @Nullable GameType gameMode();

    @Override
    public boolean isSpectator() {
        return this.gameMode() == GameType.SPECTATOR;
    }

    @Override
    public boolean isPickable() {
        return !this.isSpectator() && super.isPickable();
    }

    @Override
    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }

    public boolean isCreative() {
        return this.gameMode() == GameType.CREATIVE;
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.abilities.flying;
    }

    @Override
    public Component getDisplayName() {
        if (this.displayname == null) this.displayname = net.neoforged.neoforge.event.EventHooks.getPlayerDisplayName(this, this.getName());
        MutableComponent result = Component.literal("");
        result = prefixes.stream().reduce(result, MutableComponent::append);
        result = result.append(PlayerTeam.formatNameForTeam(this.getTeam(), this.displayname));
        result = suffixes.stream().reduce(result, MutableComponent::append);
        return this.decorateDisplayNameComponent(result);
    }

    private MutableComponent decorateDisplayNameComponent(MutableComponent nameComponent) {
        String name = this.getGameProfile().name();
        return nameComponent.withStyle(
            s -> s.withClickEvent(new ClickEvent.SuggestCommand("/tell " + name + " ")).withHoverEvent(this.createHoverEvent()).withInsertion(name)
        );
    }

    @Override
    public String getScoreboardName() {
        return this.getGameProfile().name();
    }

    @Override
    protected void internalSetAbsorptionAmount(float absorptionAmount) {
        this.getEntityData().set(DATA_PLAYER_ABSORPTION_ID, absorptionAmount);
    }

    @Override
    public float getAbsorptionAmount() {
        return this.getEntityData().get(DATA_PLAYER_ABSORPTION_ID);
    }

    @Override
    public @Nullable SlotAccess getSlot(int slot) {
        if (slot == 499) {
            return new SlotAccess() {
                {
                    Objects.requireNonNull(Player.this);
                }

                @Override
                public ItemStack get() {
                    return Player.this.containerMenu.getCarried();
                }

                @Override
                public boolean set(ItemStack itemStack) {
                    Player.this.containerMenu.setCarried(itemStack);
                    return true;
                }
            };
        } else {
            final int craftSlot = slot - 500;
            if (craftSlot >= 0 && craftSlot < 4) {
                return new SlotAccess() {
                    {
                        Objects.requireNonNull(Player.this);
                    }

                    @Override
                    public ItemStack get() {
                        return Player.this.inventoryMenu.getCraftSlots().getItem(craftSlot);
                    }

                    @Override
                    public boolean set(ItemStack itemStack) {
                        Player.this.inventoryMenu.getCraftSlots().setItem(craftSlot, itemStack);
                        Player.this.inventoryMenu.slotsChanged(Player.this.inventory);
                        return true;
                    }
                };
            } else if (slot >= 0 && slot < this.inventory.getNonEquipmentItems().size()) {
                return this.inventory.getSlot(slot);
            } else {
                int enderSlot = slot - 200;
                return enderSlot >= 0 && enderSlot < this.enderChestInventory.getContainerSize()
                    ? this.enderChestInventory.getSlot(enderSlot)
                    : super.getSlot(slot);
            }
        }
    }

    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public void setReducedDebugInfo(boolean reducedDebugInfo) {
        this.reducedDebugInfo = reducedDebugInfo;
    }

    @Override
    public void setRemainingFireTicks(int remainingTicks) {
        super.setRemainingFireTicks(this.abilities.invulnerable ? Math.min(remainingTicks, 1) : remainingTicks);
    }

    protected static Optional<Parrot.Variant> extractParrotVariant(CompoundTag tag) {
        if (!tag.isEmpty()) {
            EntityType<?> entityType = tag.read("id", EntityType.CODEC).orElse(null);
            if (entityType == EntityType.PARROT) {
                return tag.read("Variant", Parrot.Variant.LEGACY_CODEC);
            }
        }

        return Optional.empty();
    }

    protected static OptionalInt convertParrotVariant(Optional<Parrot.Variant> variant) {
        return variant.<OptionalInt>map(v -> OptionalInt.of(v.getId())).orElse(OptionalInt.empty());
    }

    private static Optional<Parrot.Variant> convertParrotVariant(OptionalInt variant) {
        return variant.isPresent() ? Optional.of(Parrot.Variant.byId(variant.getAsInt())) : Optional.empty();
    }

    public void setShoulderParrotLeft(Optional<Parrot.Variant> variant) {
        this.entityData.set(DATA_SHOULDER_PARROT_LEFT, convertParrotVariant(variant));
    }

    public Optional<Parrot.Variant> getShoulderParrotLeft() {
        return convertParrotVariant(this.entityData.get(DATA_SHOULDER_PARROT_LEFT));
    }

    public void setShoulderParrotRight(Optional<Parrot.Variant> variant) {
        this.entityData.set(DATA_SHOULDER_PARROT_RIGHT, convertParrotVariant(variant));
    }

    public Optional<Parrot.Variant> getShoulderParrotRight() {
        return convertParrotVariant(this.entityData.get(DATA_SHOULDER_PARROT_RIGHT));
    }

    public float getCurrentItemAttackStrengthDelay() {
        return (float)(1.0 / this.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0);
    }

    public boolean cannotAttackWithItem(ItemStack itemStack, int tolerance) {
        float requiredStrength = itemStack.getOrDefault(DataComponents.MINIMUM_ATTACK_CHARGE, 0.0F);
        float optimisticStrength = (this.attackStrengthTicker + tolerance) / this.getCurrentItemAttackStrengthDelay();
        return requiredStrength > 0.0F && optimisticStrength < requiredStrength;
    }

    public float getAttackStrengthScale(float a) {
        return Mth.clamp((this.attackStrengthTicker + a) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
    }

    public float getItemSwapScale(float a) {
        return Mth.clamp((this.itemSwapTicker + a) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
    }

    public void resetAttackStrengthTicker() {
        this.attackStrengthTicker = 0;
        this.itemSwapTicker = 0;
    }

    @Override
    public void onAttack() {
        this.resetOnlyAttackStrengthTicker();
        super.onAttack();
    }

    public void resetOnlyAttackStrengthTicker() {
        this.attackStrengthTicker = 0;
    }

    public ItemCooldowns getCooldowns() {
        return this.cooldowns;
    }

    @Override
    protected float getBlockSpeedFactor() {
        return !this.abilities.flying && !this.isFallFlying() ? super.getBlockSpeedFactor() : 1.0F;
    }

    @Override
    public float getLuck() {
        return (float)this.getAttributeValue(Attributes.LUCK);
    }

    public boolean canUseGameMasterBlocks() {
        return this.abilities.instabuild && this.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    public PermissionSet permissions() {
        return PermissionSet.NO_PERMISSIONS;
    }

    @Override
    public ImmutableList<Pose> getDismountPoses() {
        return ImmutableList.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING);
    }

    @Override
    public ItemStack getProjectile(ItemStack heldWeapon) {
        if (!(heldWeapon.getItem() instanceof ProjectileWeaponItem)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> supportedProjectiles = ((ProjectileWeaponItem)heldWeapon.getItem()).getSupportedHeldProjectiles(heldWeapon);
            ItemStack heldProjectile = ProjectileWeaponItem.getHeldProjectile(this, supportedProjectiles);
            if (!heldProjectile.isEmpty()) {
                return net.neoforged.neoforge.common.CommonHooks.getProjectile(this, heldWeapon, heldProjectile);
            } else {
                supportedProjectiles = ((ProjectileWeaponItem)heldWeapon.getItem()).getAllSupportedProjectiles(heldWeapon);

                for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                    ItemStack itemStack = this.inventory.getItem(i);
                    if (supportedProjectiles.test(itemStack)) {
                        return net.neoforged.neoforge.common.CommonHooks.getProjectile(this, heldWeapon, itemStack);
                    }
                }

                return net.neoforged.neoforge.common.CommonHooks.getProjectile(this, heldWeapon, this.abilities.instabuild ? ((ProjectileWeaponItem)heldWeapon.getItem()).getDefaultCreativeAmmo(this, heldWeapon) : ItemStack.EMPTY);
            }
        }
    }

    @Override
    public Vec3 getRopeHoldPosition(float partialTickTime) {
        double xOff = 0.22 * (this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0);
        float xRot = Mth.lerp(partialTickTime * 0.5F, this.getXRot(), this.xRotO) * (float) (Math.PI / 180.0);
        float yRot = Mth.lerp(partialTickTime, this.yBodyRotO, this.yBodyRot) * (float) (Math.PI / 180.0);
        if (this.isFallFlying() || this.isAutoSpinAttack()) {
            Vec3 lookAngle = this.getViewVector(partialTickTime);
            Vec3 movement = this.getDeltaMovement();
            double speedLen = movement.horizontalDistanceSqr();
            double lookLen = lookAngle.horizontalDistanceSqr();
            float zRot;
            if (speedLen > 0.0 && lookLen > 0.0) {
                double dot = (movement.x * lookAngle.x + movement.z * lookAngle.z) / Math.sqrt(speedLen * lookLen);
                double sign = movement.x * lookAngle.z - movement.z * lookAngle.x;
                zRot = (float)(Math.signum(sign) * Math.acos(dot));
            } else {
                zRot = 0.0F;
            }

            return this.getPosition(partialTickTime).add(new Vec3(xOff, -0.11, 0.85).zRot(-zRot).xRot(-xRot).yRot(-yRot));
        } else if (this.isVisuallySwimming()) {
            return this.getPosition(partialTickTime).add(new Vec3(xOff, 0.2, -0.15).xRot(-xRot).yRot(-yRot));
        } else {
            double yOff = this.getBoundingBox().getYsize() - 1.0;
            double zOff = this.isCrouching() ? -0.2 : 0.07;
            return this.getPosition(partialTickTime).add(new Vec3(xOff, yOff, zOff).yRot(-yRot));
        }
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    public boolean isScoping() {
        return this.isUsingItem() && this.getUseItem().canPerformAction(net.neoforged.neoforge.common.ItemAbilities.SPYGLASS_SCOPE);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public Optional<GlobalPos> getLastDeathLocation() {
        return this.lastDeathLocation;
    }

    public void setLastDeathLocation(Optional<GlobalPos> pos) {
        this.lastDeathLocation = pos;
    }

    @Override
    public float getHurtDir() {
        return this.hurtDir;
    }

    @Override
    public void animateHurt(float yaw) {
        super.animateHurt(yaw);
        this.hurtDir = yaw;
    }

    public boolean isMobilityRestricted() {
        return this.hasEffect(MobEffects.BLINDNESS);
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    @Override
    protected float getFlyingSpeed() {
        if (this.abilities.flying && !this.isPassenger()) {
            return this.isSprinting() ? this.abilities.getFlyingSpeed() * 2.0F : this.abilities.getFlyingSpeed();
        } else {
            return this.isSprinting() ? 0.025999999F : 0.02F;
        }
    }

    @Override
    public boolean hasContainerOpen(ContainerOpenersCounter container, BlockPos blockPos) {
        return container.isOwnContainer(this);
    }

    @Override
    public double getContainerInteractionRange() {
        return this.blockInteractionRange();
    }

    public double blockInteractionRange() {
        return this.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
    }

    public double entityInteractionRange() {
        return this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
    }

    public boolean isWithinEntityInteractionRange(Entity entity, double buffer) {
        return entity.isRemoved() ? false : this.isWithinEntityInteractionRange(entity.getBoundingBox(), buffer);
    }

    public boolean isWithinEntityInteractionRange(AABB aabb, double buffer) {
        double maxRange = this.entityInteractionRange() + buffer;
        double distanceToSq = aabb.distanceToSqr(this.getEyePosition());
        return distanceToSq < maxRange * maxRange;
    }

    public boolean isWithinAttackRange(ItemStack weaponItem, AABB aabb, double buffer) {
        return this.getAttackRangeWith(weaponItem).isInRange(this, aabb, buffer);
    }

    public boolean isWithinBlockInteractionRange(BlockPos pos, double buffer) {
        double maxRange = this.blockInteractionRange() + buffer;
        return new AABB(pos).distanceToSqr(this.getEyePosition()) < maxRange * maxRange;
    }

    public boolean shouldRotateWithMinecart() {
        return false;
    }

    @Override
    public boolean onClimbable() {
        return this.abilities.flying ? false : super.onClimbable();
    }

    public String debugInfo() {
        return MoreObjects.toStringHelper(this)
            .add("name", this.getPlainTextName())
            .add("id", this.getId())
            .add("pos", this.position())
            .add("mode", this.gameMode())
            .add("permission", printPlayerPermissions(this.permissions()))
            .toString();
    }

    private static String printPlayerPermissions(PermissionSet permissions) {
        if (permissions.hasPermission(Permissions.COMMANDS_OWNER)) {
            return "owner";
        } else if (permissions.hasPermission(Permissions.COMMANDS_ADMIN)) {
            return "admin";
        } else if (permissions.hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
            return "gamemaster";
        } else {
            return permissions.hasPermission(Permissions.COMMANDS_MODERATOR) ? "moderator" : "none";
        }
    }

    @Override
    public ResolvableProfile getProfile() {
        return ResolvableProfile.createResolved(this.gameProfile);
    }

    public record BedSleepingProblem(@Nullable Component message) {
        public static final Player.BedSleepingProblem TOO_FAR_AWAY = new Player.BedSleepingProblem(Component.translatable("block.minecraft.bed.too_far_away"));
        public static final Player.BedSleepingProblem OBSTRUCTED = new Player.BedSleepingProblem(Component.translatable("block.minecraft.bed.obstructed"));
        public static final Player.BedSleepingProblem OTHER_PROBLEM = new Player.BedSleepingProblem(null);
        public static final Player.BedSleepingProblem NOT_SAFE = new Player.BedSleepingProblem(Component.translatable("block.minecraft.bed.not_safe"));
    }

    // Neo: Getters for the Player's name prefixes and suffixes
    public Collection<MutableComponent> getPrefixes() {
         return this.prefixes;
    }

    public Collection<MutableComponent> getSuffixes() {
         return this.suffixes;
    }

    private Component displayname = null;

    /**
     * Neo: Force the displayed name to refresh, by firing {@link net.neoforged.neoforge.event.entity.player.PlayerEvent.NameFormat}, using the real player name as event parameter.
     */
    public void refreshDisplayName() {
        this.displayname = net.neoforged.neoforge.event.EventHooks.getPlayerDisplayName(this, this.getName());
    }

    /**
     * Neo: Force a pose for the player. If set, the vanilla pose determination and clearance check is skipped. Make sure the pose is clear yourself (e.g. in PlayerTick).
     * This has to be set just once, do not set it every tick.
     * Make sure to clear (null) the pose if not required anymore and only use if necessary.
     */
    public void setForcedPose(@Nullable Pose pose) {
        this.forcedPose = pose;
    }

    /**
     * Neo:
     * @return The forced pose if set, null otherwise
     */
    @Nullable
    public Pose getForcedPose() {
        return this.forcedPose;
    }
}
