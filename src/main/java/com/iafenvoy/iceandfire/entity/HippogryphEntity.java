package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.HippogryphType;
import com.iafenvoy.iceandfire.entity.ai.HippogryphAIMateGoal;
import com.iafenvoy.iceandfire.entity.ai.HippogryphAITargetGoal;
import com.iafenvoy.iceandfire.entity.ai.HippogryphAITargetItemsGoal;
import com.iafenvoy.iceandfire.entity.ai.HippogryphAIWanderGoal;
import com.iafenvoy.iceandfire.entity.util.*;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.entity.util.dragon.IDragonFlute;
import com.iafenvoy.iceandfire.registry.IafHippogryphTypes;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.iafenvoy.iceandfire.screen.handler.HippogryphScreenHandler;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.AdvancedPathNavigate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HippogryphEntity extends TamableAnimal implements MenuProvider, ISyncMount, IAnimatedEntity, IDragonFlute, IVillagerFear, IAnimalFear, IFlyingMount, ICustomMoveController, IHasCustomizableAttributes {
    private static final int FLIGHT_CHANCE_PER_TICK = 1200;
    private static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(HippogryphEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> SADDLE = SynchedEntityData.defineId(HippogryphEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ARMOR = SynchedEntityData.defineId(HippogryphEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CHESTED = SynchedEntityData.defineId(HippogryphEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HOVERING = SynchedEntityData.defineId(HippogryphEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(HippogryphEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> CONTROL_STATE = SynchedEntityData.defineId(HippogryphEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(HippogryphEntity.class, EntityDataSerializers.INT);
    public static Animation ANIMATION_EAT;
    public static Animation ANIMATION_SPEAK;
    public static Animation ANIMATION_SCRATCH;
    public static Animation ANIMATION_BITE;
    public SimpleContainer hippogryphInventory;

    public float sitProgress;
    public float hoverProgress;
    public float flyProgress;
    public int spacebarTicks;
    public int airBorneCounter;
    public BlockPos homePos;
    public boolean hasHomePosition = false;
    public int feedings = 0;
    private boolean isLandNavigator;
    private boolean isSitting;
    private boolean isHovering;
    private boolean isFlying;
    private int animationTick;
    private Animation currentAnimation;
    private int flyTicks;
    private int hoverTicks;
    private boolean hasChestVarChanged = false;
    private boolean isOverAir;

    public HippogryphEntity(EntityType<? extends TamableAnimal> type, Level worldIn) {
        super(type, worldIn);
        this.switchNavigator(true);
        ANIMATION_EAT = Animation.create(25);
        ANIMATION_SPEAK = Animation.create(15);
        ANIMATION_SCRATCH = Animation.create(25);
        ANIMATION_BITE = Animation.create(20);
        this.initHippogryphInv();
    }

    public static int getIntFromArmor(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (stack.getItem() == IafItems.IRON_HIPPOGRYPH_ARMOR.get()) return 1;
        if (stack.getItem() == IafItems.GOLD_HIPPOGRYPH_ARMOR.get()) return 2;
        if (stack.getItem() == IafItems.DIAMOND_HIPPOGRYPH_ARMOR.get()) return 3;
        if (stack.getItem() == IafItems.NETHERITE_HIPPOGRYPH_ARMOR.get()) return 4;
        return 0;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, IafCommonConfig.INSTANCE.hippogryphs.fightSpeedMod.getValue())
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.STEP_HEIGHT, 1);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(IafCommonConfig.INSTANCE.hippogryphs.fightSpeedMod.getValue());
    }

    protected boolean isOverAir() {
        return this.isOverAir;
    }

    private boolean isOverAirLogic() {
        return this.level().isEmptyBlock(BlockPos.containing(this.getBlockX(), this.getBoundingBox().minY - 1, this.getBlockZ()));
    }

    @Override
    public int getBaseExperienceReward() {
        return 10;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.KELP);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, LivingEntity.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new HippogryphAIMateGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.0D, Ingredient.of(IafItemTags.TEMPT_HIPPOGRYPH), false));
        this.goalSelector.addGoal(8, new HippogryphAIWanderGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new HippogryphAITargetItemsGoal<>(this, false));
        this.targetSelector.addGoal(5, new HippogryphAITargetGoal<>(this, LivingEntity.class, false, entity -> !(entity instanceof AbstractHorse) && DragonUtils.isAlive(entity)));
        this.targetSelector.addGoal(5, new HippogryphAITargetGoal<>(this, Player.class, 350, false, entity -> entity instanceof Player player && !player.isCreative()));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, IafHippogryphTypes.BLACK.name());
        builder.define(ARMOR, 0);
        builder.define(SADDLE, Boolean.FALSE);
        builder.define(CHESTED, Boolean.FALSE);
        builder.define(HOVERING, Boolean.FALSE);
        builder.define(FLYING, Boolean.FALSE);
        builder.define(CONTROL_STATE, (byte) 0);
        builder.define(COMMAND, 0);
    }

    @Override
    public double getYSpeedMod() {
        return 4;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (this.hasPassenger(passenger)) {
            this.yBodyRot = this.getYRot();
            this.setYHeadRot(passenger.getYHeadRot());
            this.setYBodyRot(passenger.getYRot());
        }
        passenger.setPos(this.getX(), this.getY() + 1.05F, this.getZ());
    }

    private void initHippogryphInv() {
        SimpleContainer animalchest = this.hippogryphInventory;
        this.hippogryphInventory = new SimpleContainer(18);
        if (animalchest != null) {
            int i = Math.min(animalchest.getContainerSize(), this.hippogryphInventory.getContainerSize());
            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = animalchest.getItem(j);
                if (!itemstack.isEmpty())
                    this.hippogryphInventory.setItem(j, itemstack.copy());
            }
        }
    }

    @Override
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengers())
            if (passenger instanceof Player player && this.getTarget() != passenger)
                if (this.isTame() && this.getOwnerUUID() != null && this.getOwnerUUID().equals(player.getUUID()))
                    return player;
        return null;
    }

    public boolean isBlinking() {
        return this.tickCount % 50 > 43;
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        String s = ChatFormatting.stripFormatting(player.getName().getString());
        boolean isDev = s.equals("Alexthe666") || s.equals("Raptorfarian") || s.equals("tweakbsd");
        if (this.isTame() && this.isOwnedBy(player)) {
            if (itemstack.getItem() == Items.RED_DYE && this.getEnumVariant() != IafHippogryphTypes.ALEX && isDev) {
                this.setVariant(IafHippogryphTypes.ALEX);
                if (!player.isCreative())
                    itemstack.shrink(1);
                this.playSound(SoundEvents.ZOMBIE_INFECT, 1, 1);
                for (int i = 0; i < 20; i++)
                    this.level().addParticle(ParticleTypes.CLOUD, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), 0, 0, 0);
                return InteractionResult.SUCCESS;
            }
            if (itemstack.getItem() == Items.LIGHT_GRAY_DYE && this.getEnumVariant() != IafHippogryphTypes.RAPTOR && isDev) {
                this.setVariant(IafHippogryphTypes.RAPTOR);
                if (!player.isCreative())
                    itemstack.shrink(1);
                this.playSound(SoundEvents.ZOMBIE_INFECT, 1, 1);
                for (int i = 0; i < 20; i++)
                    this.level().addParticle(ParticleTypes.CLOUD, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), 0, 0, 0);
                return InteractionResult.SUCCESS;
            }
            if (itemstack.is(IafItemTags.BREED_HIPPOGRYPH) && this.getAge() == 0 && !this.isInLove()) {
                this.setInLove(player);
                this.playSound(SoundEvents.GENERIC_EAT, 1, 1);
                if (!player.isCreative())
                    itemstack.shrink(1);
                return InteractionResult.SUCCESS;
            }
            if (itemstack.getItem() == Items.STICK) {
                if (player.isShiftKeyDown()) {
                    if (this.hasHomePosition) {
                        this.hasHomePosition = false;
                        player.displayClientMessage(Component.translatable("hippogryph.command.remove_home"), true);
                    } else {
                        this.homePos = this.blockPosition();
                        this.hasHomePosition = true;
                        player.displayClientMessage(Component.translatable("hippogryph.command.new_home", this.homePos.getX(), this.homePos.getY(), this.homePos.getZ()), true);
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    this.setCommand(this.getCommand() + 1);
                    if (this.getCommand() > 1)
                        this.setCommand(0);
                    player.displayClientMessage(Component.translatable("hippogryph.command." + (this.getCommand() == 1 ? "sit" : "stand")), true);
                }
                return InteractionResult.SUCCESS;
            }
            if (itemstack.getItem() == Items.GLISTERING_MELON_SLICE && this.getEnumVariant() != IafHippogryphTypes.DODO) {
                this.setVariant(IafHippogryphTypes.DODO);
                if (!player.isCreative())
                    itemstack.shrink(1);
                this.playSound(SoundEvents.ZOMBIE_INFECT, 1, 1);
                for (int i = 0; i < 20; i++)
                    this.level().addParticle(ParticleTypes.ENCHANT, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), 0, 0, 0);
                return InteractionResult.SUCCESS;
            }
            if (itemstack.has(DataComponents.FOOD) && itemstack.is(ItemTags.MEAT) && this.getHealth() < this.getMaxHealth()) {
                this.heal(5);
                this.playSound(SoundEvents.GENERIC_EAT, 1, 1);
                for (int i = 0; i < 3; i++)
                    this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemstack), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), 0, 0, 0);
                if (!player.isCreative())
                    itemstack.shrink(1);
                return InteractionResult.SUCCESS;
            }
            if (itemstack.isEmpty())
                if (player.isShiftKeyDown()) {
                    if (player instanceof ServerPlayer serverPlayer)
                        serverPlayer.openMenu(this);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                } else if (this.isSaddled() && !this.isBaby() && !player.isPassenger()) {
                    player.startRiding(this, true);
                    return InteractionResult.SUCCESS;
                }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isGoingUp() {
        return (this.entityData.get(CONTROL_STATE) & 1) == 1;
    }

    @Override
    public boolean isGoingDown() {
        return (this.entityData.get(CONTROL_STATE) >> 1 & 1) == 1;
    }

    public boolean attack() {
        return (this.entityData.get(CONTROL_STATE) >> 2 & 1) == 1;
    }

    public boolean dismountIAF() {
        return (this.entityData.get(CONTROL_STATE) >> 3 & 1) == 1;
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
        byte prevState = this.entityData.get(CONTROL_STATE);
        if (newState) this.entityData.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        else this.entityData.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
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

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Variant", this.getVariant());
        compound.putBoolean("Chested", this.isChested());
        compound.putBoolean("Saddled", this.isSaddled());
        compound.putBoolean("Hovering", this.isHovering());
        compound.putBoolean("Flying", this.isFlying());
        compound.putInt("Armor", this.getArmorValue());
        compound.putInt("Feedings", this.feedings);
        if (this.hippogryphInventory != null)
            compound.put("Items", ItemStack.OPTIONAL_CODEC.listOf().encodeStart(RegistryOps.create(NbtOps.INSTANCE, this.level().registryAccess()), this.hippogryphInventory.getItems()).resultOrPartial(IceAndFire.LOGGER::error).orElse(new ListTag()));
        compound.putBoolean("HasHomePosition", this.hasHomePosition);
        if (this.homePos != null && this.hasHomePosition) {
            compound.putInt("HomeAreaX", this.homePos.getX());
            compound.putInt("HomeAreaY", this.homePos.getY());
            compound.putInt("HomeAreaZ", this.homePos.getZ());
        }
        compound.putInt("Command", this.getCommand());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getString("Variant"));
        this.setChested(compound.getBoolean("Chested"));
        this.setSaddled(compound.getBoolean("Saddled"));
        this.setHovering(compound.getBoolean("Hovering"));
        this.setFlying(compound.getBoolean("Flying"));
        this.setArmor(compound.getInt("Armor"));
        this.feedings = compound.getInt("Feedings");

        this.initHippogryphInv();
        List<ItemStack> inv = ItemStack.OPTIONAL_CODEC.listOf().parse(RegistryOps.create(NbtOps.INSTANCE, this.level().registryAccess()), compound.get("Items")).resultOrPartial(IceAndFire.LOGGER::error).orElse(List.of());
        for (int i = 0; i < inv.size() && i < this.hippogryphInventory.getContainerSize(); i++)
            this.hippogryphInventory.setItem(i, inv.get(i));

        this.hasHomePosition = compound.getBoolean("HasHomePosition");
        if (this.hasHomePosition && compound.getInt("HomeAreaX") != 0 && compound.getInt("HomeAreaY") != 0 && compound.getInt("HomeAreaZ") != 0) {
            this.homePos = new BlockPos(compound.getInt("HomeAreaX"), compound.getInt("HomeAreaY"), compound.getInt("HomeAreaZ"));
        }
        this.setCommand(compound.getInt("Command"));

        if (this.isOrderedToSit())
            this.sitProgress = 20.0F;

        this.setConfigurableAttributes();
    }

    public String getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(String variant) {
        this.entityData.set(VARIANT, variant);
    }

    public HippogryphType getEnumVariant() {
        return IafRegistries.HIPPOGRYPH_TYPE.get(IceAndFire.id(this.getVariant()));
    }

    public void setVariant(HippogryphType variant) {
        this.setVariant(variant.name());
    }

    public boolean isSaddled() {
        return this.entityData.get(SADDLE);
    }

    public void setSaddled(boolean saddle) {
        this.entityData.set(SADDLE, saddle);
    }

    public boolean isChested() {
        return this.entityData.get(CHESTED);
    }

    public void setChested(boolean chested) {
        this.entityData.set(CHESTED, chested);
        this.hasChestVarChanged = true;
    }

    @Override
    public boolean isOrderedToSit() {
        if (this.level().isClientSide) {
            boolean isSitting = (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
            this.isSitting = isSitting;
            return isSitting;
        }
        return this.isSitting;
    }

    @Override
    public void setOrderedToSit(boolean sitting) {
        if (!this.level().isClientSide) {
            this.isSitting = sitting;
        }
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (sitting) {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 | 1));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 & -2));
        }
    }

    @Override
    public boolean isHovering() {
        if (this.level().isClientSide) {
            return this.isHovering = this.entityData.get(HOVERING);
        }
        return this.isHovering;
    }

    public void setHovering(boolean hovering) {
        this.entityData.set(HOVERING, hovering);
        if (!this.level().isClientSide) {
            this.isHovering = hovering;
        }
    }

    @Override
    public Player getRidingPlayer() {
        if (this.getControllingPassenger() instanceof Player) {
            return (Player) this.getControllingPassenger();
        }
        return null;
    }

    @Override
    public double getFlightSpeedModifier() {
        return IafCommonConfig.INSTANCE.hippogryphs.fightSpeedMod.getValue() * 0.9F;
    }

    @Override
    public boolean isFlying() {
        if (this.level().isClientSide) {
            return this.isFlying = this.entityData.get(FLYING);
        }
        return this.isFlying;
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
        if (!this.level().isClientSide) {
            this.isFlying = flying;
        }
    }

    @Override
    public int getArmorValue() {
        return this.entityData.get(ARMOR);
    }

    public void setArmor(int armorType) {
        this.entityData.set(ARMOR, armorType);
        double armorValue = switch (armorType) {
            case 1 -> 10;
            case 2 -> 20;
            case 3 -> 30;
            case 4 -> 35;
            default -> 0;
        };
        this.getAttribute(Attributes.ARMOR).setBaseValue(armorValue);
    }

    public boolean canMove() {
        return !this.isOrderedToSit() && this.getControllingPassenger() == null && this.sitProgress == 0;
    }

    @Override
    public @NotNull SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull MobSpawnType reason, SpawnGroupData spawnDataIn) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
        this.setVariant(HippogryphType.getBiomeType(worldIn.getBiome(this.blockPosition())));
        return data;
    }

    @Override
    public boolean hurt(@NotNull DamageSource dmg, float i) {
        if (this.isVehicle() && dmg.getEntity() != null && this.getControllingPassenger() != null && dmg.getEntity() == this.getControllingPassenger()) {
            return false;
        }
        return super.hurt(dmg, i);
    }

    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverWorld, @NotNull AgeableMob ageable) {
        return null;
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
    public void playAmbientSound() {
        if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
        super.playAmbientSound();
    }

    @Override
    protected void playHurtSound(@NotNull DamageSource source) {
        if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
        super.playHurtSound(source);
    }

    @Override
    protected float getRiddenSpeed(@NotNull Player pPlayer) {
        return (this.isFlying() || this.isHovering()) ? (float) this.getAttributeValue(Attributes.FLYING_SPEED) : (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.75F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return IafSounds.HIPPOGRYPH_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return IafSounds.HIPPOGRYPH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return IafSounds.HIPPOGRYPH_DIE.get();
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, HippogryphEntity.ANIMATION_EAT, HippogryphEntity.ANIMATION_BITE, HippogryphEntity.ANIMATION_SPEAK, HippogryphEntity.ANIMATION_SCRATCH};
    }

    @Override
    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else if (this.isFlying() || this.isHovering()) {
                this.moveRelative(0.1F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            } else {
                super.travel(pTravelVector);
            }
        } else {
            super.travel(pTravelVector);
        }
    }

    @Override
    protected void tickRidden(@NotNull Player player, @NotNull Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        Vec2 vec2 = this.getRiddenRotation(player);
        this.setRot(vec2.y, vec2.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        if (this.isControlledByLocalInstance()) {
            Vec3 vec3 = this.getDeltaMovement();
            float vertical = this.isGoingUp() ? 0.2F : this.isGoingDown() ? -0.2F : 0F;
            if (!this.isFlying() && !this.isHovering()) {
                vertical = (float) travelVector.y;
            }
            this.setDeltaMovement(vec3.add(0, vertical, 0));
        }
    }

    @Override
    protected @NotNull Vec3 getRiddenInput(Player player, @NotNull Vec3 travelVector) {
        float f = player.xxa * 0.5F;
        float f1 = player.zza;
        if (f1 <= 0.0F) {
            f1 *= 0.25F;
        }

        return new Vec3(f, 0.0D, f1);

    }

    protected Vec2 getRiddenRotation(LivingEntity entity) {
        return new Vec2(entity.getXRot() * 0.5F, entity.getYRot());
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        if (this.getAnimation() != ANIMATION_SCRATCH && this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_SCRATCH : ANIMATION_BITE);
        } else {
            return true;
        }
        return false;
    }

    // FIXME: There's something majorly wrong with hovering/flying logic. Results in Hippogryphs not landing and other animation issues
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof Player)
            this.setTarget(null);
        if (!this.level().isClientSide) {
            if (this.isOrderedToSit() && (this.getCommand() != 1 || this.getControllingPassenger() != null))
                this.setOrderedToSit(false);
            if (!this.isOrderedToSit() && this.getCommand() == 1 && this.getControllingPassenger() == null)
                this.setOrderedToSit(true);
            if (this.isOrderedToSit()) this.getNavigation().stop();
            if (this.random.nextInt(900) == 0 && this.deathTime == 0) this.heal(1.0F);
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getTarget() != null && this.getAnimationTick() == 6) {
            double dist = this.distanceToSqr(this.getTarget());
            if (dist < 8)
                this.getTarget().hurt(this.level().damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
        }
        LivingEntity attackTarget = this.getTarget();
        if (this.getAnimation() == ANIMATION_SCRATCH && attackTarget != null && this.getAnimationTick() == 6) {
            double dist = this.distanceToSqr(attackTarget);

            if (dist < 8) {
                attackTarget.hurt(this.level().damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                attackTarget.hasImpulse = true;
                float f = Mth.sqrt((float) (0.5 * 0.5 + 0.5 * 0.5));
                attackTarget.setDeltaMovement(attackTarget.getDeltaMovement().add(-0.5 / (double) f, 1, -0.5 / (double) f));
                attackTarget.setDeltaMovement(attackTarget.getDeltaMovement().multiply(0.5D, 1, 0.5D));

                if (attackTarget.onGround()) {
                    attackTarget.setDeltaMovement(attackTarget.getDeltaMovement().add(0, 0.3, 0));
                }
            }
        }
        if (!this.level().isClientSide && !this.isOverAir() && this.getNavigation().isDone() && attackTarget != null && attackTarget.getY() - 3 > this.getY() && this.getRandom().nextInt(15) == 0 && this.canMove() && !this.isHovering() && !this.isFlying()) {
            this.setHovering(true);
            this.hoverTicks = 0;
            this.flyTicks = 0;
        }
        if (this.isOverAir()) {
            this.airBorneCounter++;
        } else {
            this.airBorneCounter = 0;
        }
        if (this.hasChestVarChanged && this.hippogryphInventory != null && !this.isChested()) {
            for (int i = 3; i < 18; i++) {
                if (!this.hippogryphInventory.getItem(i).isEmpty()) {
                    if (!this.level().isClientSide) {
                        this.spawnAtLocation(this.hippogryphInventory.getItem(i), 1);
                    }
                    this.hippogryphInventory.removeItemNoUpdate(i);
                }
            }
            this.hasChestVarChanged = false;
        }
        if (this.isFlying() && this.tickCount % 40 == 0 || this.isFlying() && this.isOrderedToSit()) {
            this.setFlying(true);
        }
        if (!this.canMove() && attackTarget != null) {
            this.setTarget(null);
        }
        if (!this.canMove()) {
            this.getNavigation().stop();

        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        boolean sitting = this.isOrderedToSit() && !this.isHovering() && !this.isFlying();
        if (sitting && this.sitProgress < 20.0F) {
            this.sitProgress += 0.5F;
        } else if (!sitting && this.sitProgress > 0.0F) {
            this.sitProgress -= 0.5F;
        }

        boolean hovering = this.isHovering();
        if (hovering && this.hoverProgress < 20.0F) {
            this.hoverProgress += 0.5F;
        } else if (!hovering && this.hoverProgress > 0.0F) {
            this.hoverProgress -= 0.5F;
        }
        boolean flying = this.isFlying() || this.isHovering() && this.airBorneCounter > 10;
        if (flying && this.flyProgress < 20.0F) {
            this.flyProgress += 0.5F;
        } else if (!flying && this.flyProgress > 0.0F) {
            this.flyProgress -= 0.5F;
        }
        if (flying && this.isLandNavigator) {
            this.switchNavigator(false);
        }
        if (!flying && !this.isLandNavigator) {
            this.switchNavigator(true);
        }
        if ((flying || hovering) && !this.doesWantToLand() && this.getControllingPassenger() == null) {
            double up = this.isInWater() ? 0.16D : 0.08D;
            this.setDeltaMovement(this.getDeltaMovement().add(0, up, 0));
        }
        if ((flying || hovering) && this.tickCount % 20 == 0 && this.isOverAir()) {
            this.playSound(SoundEvents.ENDER_DRAGON_FLAP, this.getSoundVolume() * ((float) IafCommonConfig.INSTANCE.dragon.flapNoiseDistance.getValue() / 2), 0.6F + this.random.nextFloat() * 0.6F * this.getVoicePitch());
        }
        if (this.onGround() && this.doesWantToLand() && (this.isFlying() || this.isHovering())) {
            this.setFlying(false);
            this.setHovering(false);
        }
        if (this.isHovering()) {
            if (this.isOrderedToSit()) {
                this.setHovering(false);
            }
            this.hoverTicks++;
            if (this.doesWantToLand()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.05D, 0));
            } else {
                if (this.getControllingPassenger() == null) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.08D, 0));
                }
                if (this.hoverTicks > 40) {
                    if (!this.isBaby()) {
                        this.setFlying(true);
                    }
                    this.setHovering(false);
                    this.hoverTicks = 0;
                    this.flyTicks = 0;
                }
            }
        }
        if (this.isOrderedToSit()) {
            this.getNavigation().stop();
        }
        if (this.onGround() && this.flyTicks != 0) {
            this.flyTicks = 0;
        }
        if (this.isFlying() && this.doesWantToLand() && this.getControllingPassenger() == null) {
            this.setHovering(false);
            if (this.onGround()) {
                this.flyTicks = 0;
            }
            this.setFlying(false);
        }
        if (this.isFlying()) {
            this.flyTicks++;
        }
        if ((this.isHovering() || this.isFlying()) && this.isOrderedToSit()) {
            this.setFlying(false);
            this.setHovering(false);
        }
        if (this.isVehicle() && this.isGoingDown() && this.onGround()) {
            this.setHovering(false);
            this.setFlying(false);
        }
        if ((!this.level().isClientSide && this.getRandom().nextInt(FLIGHT_CHANCE_PER_TICK) == 0 && !this.isOrderedToSit() && !this.isFlying() && this.getPassengers().isEmpty() && !this.isBaby() && !this.isHovering() && !this.isOrderedToSit() && this.canMove() && !this.isOverAir() || this.getY() < -1)) {
            this.setHovering(true);
            this.hoverTicks = 0;
            this.flyTicks = 0;
        }
        if (this.getTarget() != null && !this.getPassengers().isEmpty() && this.getOwner() != null && this.getPassengers().contains(this.getOwner())) {
            this.setTarget(null);
        }
    }

    public boolean doesWantToLand() {
        return (this.flyTicks > 200 || this.flyTicks > 40 && this.flyProgress == 0) && !this.isVehicle();
    }

    @Override
    public void tick() {
        super.tick();
        this.isOverAir = this.isOverAirLogic();
        if (this.isGoingUp()) {
            if (this.airBorneCounter == 0) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.02F, 0));
            }
            if (!this.isFlying() && !this.isHovering()) {
                this.spacebarTicks += 2;
            }
        } else if (this.dismountIAF()) {
            if (this.isFlying() || this.isHovering()) {
                this.setFlying(false);
                this.setHovering(false);
            }
        }
        if (this.attack() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof Player) {

            LivingEntity target = DragonUtils.riderLookingAtEntity(this, this.getControllingPassenger(), 3);
            if (this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_SCRATCH) {
                this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_SCRATCH : ANIMATION_BITE);
            }
            if (target != null && this.getAnimationTick() >= 10 && this.getAnimationTick() < 13) {
                target.hurt(this.level().damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getControllingPassenger() != null && this.getControllingPassenger().isShiftKeyDown()) {
            this.getControllingPassenger().stopRiding();
        }

        double motion = this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z;//Use squared norm2

        if (this.isFlying() && !this.isHovering() && this.getControllingPassenger() != null && this.isOverAir() && motion < 0.01F) {
            this.setHovering(true);
            this.setFlying(false);
        }
        if (this.isHovering() && !this.isFlying() && this.getControllingPassenger() != null && this.isOverAir() && motion > 0.01F) {
            this.setFlying(true);
            this.setHovering(false);
        }
        if (this.spacebarTicks > 0) this.spacebarTicks--;
        if (this.spacebarTicks > 10 && this.getOwner() != null && this.getPassengers().contains(this.getOwner()) && !this.isFlying() && !this.isHovering())
            this.setHovering(true);
        if (this.getTarget() != null && this.getVehicle() == null && !this.getTarget().isAlive() || this.getTarget() != null && this.getTarget() instanceof DragonBaseEntity && !this.getTarget().isAlive())
            this.setTarget(null);
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);
        if (this.hippogryphInventory != null && !this.level().isClientSide)
            for (int i = 0; i < this.hippogryphInventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.hippogryphInventory.getItem(i);
                if (!itemstack.isEmpty())
                    this.spawnAtLocation(itemstack, 0.0F);
            }
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = this.createNavigator(this.level(), AdvancedPathNavigate.MovementType.CLIMBING);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlyingMoveControl(this, 10, true);
            this.navigation = this.createNavigator(this.level(), AdvancedPathNavigate.MovementType.FLYING);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level worldIn) {
        return this.createNavigator(worldIn, AdvancedPathNavigate.MovementType.CLIMBING);
    }

    protected PathNavigation createNavigator(Level worldIn, AdvancedPathNavigate.MovementType type) {
        return this.createNavigator(worldIn, type, 2);
    }

    protected PathNavigation createNavigator(Level worldIn, AdvancedPathNavigate.MovementType type, float width) {
        AdvancedPathNavigate newNavigator = new AdvancedPathNavigate(this, this.level(), type, width, (float) 2);
        this.navigation = newNavigator;
        newNavigator.setCanFloat(true);
        newNavigator.getNodeEvaluator().setCanOpenDoors(true);
        return newNavigator;
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) return true;
            if (entityIn instanceof TamableAnimal tameable) return tameable.isOwnedBy(livingentity);
            if (livingentity != null) return livingentity.isAlliedTo(entityIn);
        }
        return super.isAlliedTo(entityIn);
    }

    @Override
    public void onHearFlute(Player player) {
        if (this.isTame() && this.isOwnedBy(player))
            if (this.isFlying() || this.isHovering()) {
                this.setFlying(false);
                this.setHovering(false);
            }
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return DragonUtils.canTameDragonAttack(this, entity);
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new HippogryphScreenHandler(syncId, this.hippogryphInventory, playerInventory, this);
    }

    @Override
    public void writeClientSideData(@NotNull AbstractContainerMenu menu, RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.getId());
    }
}
