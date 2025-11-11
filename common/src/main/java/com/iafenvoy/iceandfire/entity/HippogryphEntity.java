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
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HippogryphEntity extends TameableEntity implements ExtendedMenuProvider, ISyncMount, IAnimatedEntity, IDragonFlute, IVillagerFear, IAnimalFear, IFlyingMount, ICustomMoveController, IHasCustomizableAttributes {
    private static final int FLIGHT_CHANCE_PER_TICK = 1200;
    private static final TrackedData<String> VARIANT = DataTracker.registerData(HippogryphEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> SADDLE = DataTracker.registerData(HippogryphEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> ARMOR = DataTracker.registerData(HippogryphEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CHESTED = DataTracker.registerData(HippogryphEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HOVERING = DataTracker.registerData(HippogryphEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FLYING = DataTracker.registerData(HippogryphEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Byte> CONTROL_STATE = DataTracker.registerData(HippogryphEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> COMMAND = DataTracker.registerData(HippogryphEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static Animation ANIMATION_EAT;
    public static Animation ANIMATION_SPEAK;
    public static Animation ANIMATION_SCRATCH;
    public static Animation ANIMATION_BITE;
    public SimpleInventory hippogryphInventory;

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

    public HippogryphEntity(EntityType<? extends TameableEntity> type, World worldIn) {
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

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, IafCommonConfig.INSTANCE.hippogryphs.fightSpeedMod.getValue())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED).setBaseValue(IafCommonConfig.INSTANCE.hippogryphs.fightSpeedMod.getValue());
    }

    protected boolean isOverAir() {
        return this.isOverAir;
    }

    private boolean isOverAirLogic() {
        return this.getWorld().isAir(BlockPos.ofFloored(this.getBlockX(), this.getBoundingBox().minY - 1, this.getBlockZ()));
    }

    @Override
    public int getXpToDrop() {
        return 10;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.KELP);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.add(4, new LookAtEntityGoal(this, LivingEntity.class, 6.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new HippogryphAIMateGoal(this, 1.0D));
        this.goalSelector.add(6, new TemptGoal(this, 1.0D, Ingredient.fromTag(IafItemTags.TEMPT_HIPPOGRYPH), false));
        this.goalSelector.add(8, new HippogryphAIWanderGoal(this, 1.0D));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this));
        this.targetSelector.add(4, new HippogryphAITargetItemsGoal<>(this, false));
        this.targetSelector.add(5, new HippogryphAITargetGoal<>(this, LivingEntity.class, false, entity -> !(entity instanceof AbstractHorseEntity) && DragonUtils.isAlive(entity)));
        this.targetSelector.add(5, new HippogryphAITargetGoal<>(this, PlayerEntity.class, 350, false, entity -> entity instanceof PlayerEntity player && !player.isCreative()));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, IafHippogryphTypes.BLACK.name());
        builder.add(ARMOR, 0);
        builder.add(SADDLE, Boolean.FALSE);
        builder.add(CHESTED, Boolean.FALSE);
        builder.add(HOVERING, Boolean.FALSE);
        builder.add(FLYING, Boolean.FALSE);
        builder.add(CONTROL_STATE, (byte) 0);
        builder.add(COMMAND, 0);
    }

    @Override
    public double getYSpeedMod() {
        return 4;
    }

    @Override
    protected void fall(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void updatePassengerPosition(Entity passenger, PositionUpdater callback) {
        super.updatePassengerPosition(passenger, callback);
        if (this.hasPassenger(passenger)) {
            this.bodyYaw = this.getYaw();
            this.setHeadYaw(passenger.getHeadYaw());
            this.setBodyYaw(passenger.getYaw());
        }
        passenger.setPosition(this.getX(), this.getY() + 1.05F, this.getZ());
    }

    private void initHippogryphInv() {
        SimpleInventory animalchest = this.hippogryphInventory;
        this.hippogryphInventory = new SimpleInventory(18);
        if (animalchest != null) {
            int i = Math.min(animalchest.size(), this.hippogryphInventory.size());
            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = animalchest.getStackInSlot(j);
                if (!itemstack.isEmpty())
                    this.hippogryphInventory.setStack(j, itemstack.copy());
            }
        }
    }

    @Override
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengerList())
            if (passenger instanceof PlayerEntity player && this.getTarget() != passenger)
                if (this.isTamed() && this.getOwnerUuid() != null && this.getOwnerUuid().equals(player.getUuid()))
                    return player;
        return null;
    }

    public boolean isBlinking() {
        return this.age % 50 > 43;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        String s = Formatting.strip(player.getName().getString());
        assert s != null;
        boolean isDev = s.equals("Alexthe666") || s.equals("Raptorfarian") || s.equals("tweakbsd");
        if (this.isTamed() && this.isOwner(player)) {
            if (itemstack.getItem() == Items.RED_DYE && this.getEnumVariant() != IafHippogryphTypes.ALEX && isDev) {
                this.setVariant(IafHippogryphTypes.ALEX);
                if (!player.isCreative())
                    itemstack.decrement(1);
                this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1, 1);
                for (int i = 0; i < 20; i++)
                    this.getWorld().addParticle(ParticleTypes.CLOUD, this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getY() + (double) (this.random.nextFloat() * this.getHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), 0, 0, 0);
                return ActionResult.SUCCESS;
            }
            if (itemstack.getItem() == Items.LIGHT_GRAY_DYE && this.getEnumVariant() != IafHippogryphTypes.RAPTOR && isDev) {
                this.setVariant(IafHippogryphTypes.RAPTOR);
                if (!player.isCreative())
                    itemstack.decrement(1);
                this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1, 1);
                for (int i = 0; i < 20; i++)
                    this.getWorld().addParticle(ParticleTypes.CLOUD, this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getY() + (double) (this.random.nextFloat() * this.getHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), 0, 0, 0);
                return ActionResult.SUCCESS;
            }
            if (itemstack.isIn(IafItemTags.BREED_HIPPOGRYPH) && this.getBreedingAge() == 0 && !this.isInLove()) {
                this.lovePlayer(player);
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
                if (!player.isCreative())
                    itemstack.decrement(1);
                return ActionResult.SUCCESS;
            }
            if (itemstack.getItem() == Items.STICK) {
                if (player.isSneaking()) {
                    if (this.hasHomePosition) {
                        this.hasHomePosition = false;
                        player.sendMessage(Text.translatable("hippogryph.command.remove_home"), true);
                    } else {
                        this.homePos = this.getBlockPos();
                        this.hasHomePosition = true;
                        player.sendMessage(Text.translatable("hippogryph.command.new_home", this.homePos.getX(), this.homePos.getY(), this.homePos.getZ()), true);
                    }
                    return ActionResult.SUCCESS;
                } else {
                    this.setCommand(this.getCommand() + 1);
                    if (this.getCommand() > 1)
                        this.setCommand(0);
                    player.sendMessage(Text.translatable("hippogryph.command." + (this.getCommand() == 1 ? "sit" : "stand")), true);
                }
                return ActionResult.SUCCESS;
            }
            if (itemstack.getItem() == Items.GLISTERING_MELON_SLICE && this.getEnumVariant() != IafHippogryphTypes.DODO) {
                this.setVariant(IafHippogryphTypes.DODO);
                if (!player.isCreative())
                    itemstack.decrement(1);
                this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1, 1);
                for (int i = 0; i < 20; i++)
                    this.getWorld().addParticle(ParticleTypes.ENCHANT, this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getY() + (double) (this.random.nextFloat() * this.getHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), 0, 0, 0);
                return ActionResult.SUCCESS;
            }
            if (itemstack.contains(DataComponentTypes.FOOD) && itemstack.isIn(ItemTags.MEAT) && this.getHealth() < this.getMaxHealth()) {
                this.heal(5);
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
                for (int i = 0; i < 3; i++)
                    this.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, itemstack), this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getY() + (double) (this.random.nextFloat() * this.getHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), 0, 0, 0);
                if (!player.isCreative())
                    itemstack.decrement(1);
                return ActionResult.SUCCESS;
            }
            if (itemstack.isEmpty())
                if (player.isSneaking()) {
                    if (player instanceof ServerPlayerEntity serverPlayer)
                        MenuRegistry.openExtendedMenu(serverPlayer, this);
                    return ActionResult.success(this.getWorld().isClient);
                } else if (this.isSaddled() && !this.isBaby() && !player.hasVehicle()) {
                    player.startRiding(this, true);
                    return ActionResult.SUCCESS;
                }
        }
        return super.interactMob(player, hand);
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

    public int getCommand() {
        return this.dataTracker.get(COMMAND);
    }

    public void setCommand(int command) {
        this.dataTracker.set(COMMAND, command);
        this.setSitting(command == 1);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putString("Variant", this.getVariant());
        compound.putBoolean("Chested", this.isChested());
        compound.putBoolean("Saddled", this.isSaddled());
        compound.putBoolean("Hovering", this.isHovering());
        compound.putBoolean("Flying", this.isFlying());
        compound.putInt("Armor", this.getArmor());
        compound.putInt("Feedings", this.feedings);
        if (this.hippogryphInventory != null)
            compound.put("Items", ItemStack.OPTIONAL_CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.hippogryphInventory.getHeldStacks()).resultOrPartial(IceAndFire.LOGGER::error).orElse(new NbtList()));
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
        this.setVariant(compound.getString("Variant"));
        this.setChested(compound.getBoolean("Chested"));
        this.setSaddled(compound.getBoolean("Saddled"));
        this.setHovering(compound.getBoolean("Hovering"));
        this.setFlying(compound.getBoolean("Flying"));
        this.setArmor(compound.getInt("Armor"));
        this.feedings = compound.getInt("Feedings");

        this.initHippogryphInv();
        List<ItemStack> inv = ItemStack.OPTIONAL_CODEC.listOf().parse(NbtOps.INSTANCE, compound.get("Items")).resultOrPartial(IceAndFire.LOGGER::error).orElse(List.of());
        for (int i = 0; i < inv.size() && i < this.hippogryphInventory.size(); i++)
            this.hippogryphInventory.setStack(i, inv.get(i));

        this.hasHomePosition = compound.getBoolean("HasHomePosition");
        if (this.hasHomePosition && compound.getInt("HomeAreaX") != 0 && compound.getInt("HomeAreaY") != 0 && compound.getInt("HomeAreaZ") != 0) {
            this.homePos = new BlockPos(compound.getInt("HomeAreaX"), compound.getInt("HomeAreaY"), compound.getInt("HomeAreaZ"));
        }
        this.setCommand(compound.getInt("Command"));

        if (this.isSitting())
            this.sitProgress = 20.0F;

        this.setConfigurableAttributes();
    }

    public String getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public void setVariant(String variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public HippogryphType getEnumVariant() {
        return IafRegistries.HIPPOGRYPH_TYPE.get(IceAndFire.id(this.getVariant()));
    }

    public void setVariant(HippogryphType variant) {
        this.setVariant(variant.name());
    }

    public boolean isSaddled() {
        return this.dataTracker.get(SADDLE);
    }

    public void setSaddled(boolean saddle) {
        this.dataTracker.set(SADDLE, saddle);
    }

    public boolean isChested() {
        return this.dataTracker.get(CHESTED);
    }

    public void setChested(boolean chested) {
        this.dataTracker.set(CHESTED, chested);
        this.hasChestVarChanged = true;
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
        if (!this.getWorld().isClient) {
            this.isSitting = sitting;
        }
        byte b0 = this.dataTracker.get(TAMEABLE_FLAGS);
        if (sitting) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b0 | 1));
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte) (b0 & -2));
        }
    }

    @Override
    public boolean isHovering() {
        if (this.getWorld().isClient) {
            return this.isHovering = this.dataTracker.get(HOVERING);
        }
        return this.isHovering;
    }

    public void setHovering(boolean hovering) {
        this.dataTracker.set(HOVERING, hovering);
        if (!this.getWorld().isClient) {
            this.isHovering = hovering;
        }
    }

    @Override
    public PlayerEntity getRidingPlayer() {
        if (this.getControllingPassenger() instanceof PlayerEntity) {
            return (PlayerEntity) this.getControllingPassenger();
        }
        return null;
    }

    @Override
    public double getFlightSpeedModifier() {
        return IafCommonConfig.INSTANCE.hippogryphs.fightSpeedMod.getValue() * 0.9F;
    }

    @Override
    public boolean isFlying() {
        if (this.getWorld().isClient) {
            return this.isFlying = this.dataTracker.get(FLYING);
        }
        return this.isFlying;
    }

    public void setFlying(boolean flying) {
        this.dataTracker.set(FLYING, flying);
        if (!this.getWorld().isClient) {
            this.isFlying = flying;
        }
    }

    @Override
    public int getArmor() {
        return this.dataTracker.get(ARMOR);
    }

    public void setArmor(int armorType) {
        this.dataTracker.set(ARMOR, armorType);
        double armorValue = switch (armorType) {
            case 1 -> 10;
            case 2 -> 20;
            case 3 -> 30;
            case 4 -> 35;
            default -> 0;
        };
        this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(armorValue);
    }

    public boolean canMove() {
        return !this.isSitting() && this.getControllingPassenger() == null && this.sitProgress == 0;
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn) {
        EntityData data = super.initialize(worldIn, difficultyIn, reason, spawnDataIn);
        this.setVariant(HippogryphType.getBiomeType(worldIn.getBiome(this.getBlockPos())));
        return data;
    }

    @Override
    public boolean damage(DamageSource dmg, float i) {
        if (this.hasPassengers() && dmg.getAttacker() != null && this.getControllingPassenger() != null && dmg.getAttacker() == this.getControllingPassenger()) {
            return false;
        }
        return super.damage(dmg, i);
    }

    @Override
    public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity ageable) {
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
    protected void playHurtSound(DamageSource source) {
        if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
        super.playHurtSound(source);
    }

    @Override
    protected float getSaddledSpeed(PlayerEntity pPlayer) {
        return (this.isFlying() || this.isHovering()) ? (float) this.getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED) : (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.75F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return IafSounds.HIPPOGRYPH_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
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
    public void travel(Vec3d pTravelVector) {
        if (this.isLogicalSideForUpdatingMovement()) {
            if (this.isTouchingWater()) {
                this.updateVelocity(0.02F, pTravelVector);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.8F));
            } else if (this.isInLava()) {
                this.updateVelocity(0.02F, pTravelVector);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5D));
            } else if (this.isFlying() || this.isHovering()) {
                this.updateVelocity(0.1F, pTravelVector);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.9D));
            } else {
                super.travel(pTravelVector);
            }
        } else {
            super.travel(pTravelVector);
        }
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
            if (!this.isFlying() && !this.isHovering()) {
                vertical = (float) travelVector.y;
            }
            this.setVelocity(vec3.add(0, vertical, 0));
        }
    }

    @Override
    protected Vec3d getControlledMovementInput(PlayerEntity player, Vec3d travelVector) {
        float f = player.sidewaysSpeed * 0.5F;
        float f1 = player.forwardSpeed;
        if (f1 <= 0.0F) {
            f1 *= 0.25F;
        }

        return new Vec3d(f, 0.0D, f1);

    }

    protected Vec2f getRiddenRotation(LivingEntity entity) {
        return new Vec2f(entity.getPitch() * 0.5F, entity.getYaw());
    }

    @Override
    public boolean tryAttack(Entity entityIn) {
        if (this.getAnimation() != ANIMATION_SCRATCH && this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_SCRATCH : ANIMATION_BITE);
        } else {
            return true;
        }
        return false;
    }

    // FIXME: There's something majorly wrong with hovering/flying logic. Results in Hippogryphs not landing and other animation issues
    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof PlayerEntity)
            this.setTarget(null);
        if (!this.getWorld().isClient) {
            if (this.isSitting() && (this.getCommand() != 1 || this.getControllingPassenger() != null))
                this.setSitting(false);
            if (!this.isSitting() && this.getCommand() == 1 && this.getControllingPassenger() == null)
                this.setSitting(true);
            if (this.isSitting()) this.getNavigation().stop();
            if (this.random.nextInt(900) == 0 && this.deathTime == 0) this.heal(1.0F);
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getTarget() != null && this.getAnimationTick() == 6) {
            double dist = this.squaredDistanceTo(this.getTarget());
            if (dist < 8)
                this.getTarget().damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
        }
        LivingEntity attackTarget = this.getTarget();
        if (this.getAnimation() == ANIMATION_SCRATCH && attackTarget != null && this.getAnimationTick() == 6) {
            double dist = this.squaredDistanceTo(attackTarget);

            if (dist < 8) {
                attackTarget.damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
                attackTarget.velocityDirty = true;
                float f = MathHelper.sqrt((float) (0.5 * 0.5 + 0.5 * 0.5));
                attackTarget.setVelocity(attackTarget.getVelocity().add(-0.5 / (double) f, 1, -0.5 / (double) f));
                attackTarget.setVelocity(attackTarget.getVelocity().multiply(0.5D, 1, 0.5D));

                if (attackTarget.isOnGround()) {
                    attackTarget.setVelocity(attackTarget.getVelocity().add(0, 0.3, 0));
                }
            }
        }
        if (!this.getWorld().isClient && !this.isOverAir() && this.getNavigation().isIdle() && attackTarget != null && attackTarget.getY() - 3 > this.getY() && this.getRandom().nextInt(15) == 0 && this.canMove() && !this.isHovering() && !this.isFlying()) {
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
                if (!this.hippogryphInventory.getStackInSlot(i).isEmpty()) {
                    if (!this.getWorld().isClient) {
                        this.dropStack(this.hippogryphInventory.getStackInSlot(i), 1);
                    }
                    this.hippogryphInventory.removeStack(i);
                }
            }
            this.hasChestVarChanged = false;
        }
        if (this.isFlying() && this.age % 40 == 0 || this.isFlying() && this.isSitting()) {
            this.setFlying(true);
        }
        if (!this.canMove() && attackTarget != null) {
            this.setTarget(null);
        }
        if (!this.canMove()) {
            this.getNavigation().stop();

        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        boolean sitting = this.isSitting() && !this.isHovering() && !this.isFlying();
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
            double up = this.isTouchingWater() ? 0.16D : 0.08D;
            this.setVelocity(this.getVelocity().add(0, up, 0));
        }
        if ((flying || hovering) && this.age % 20 == 0 && this.isOverAir()) {
            this.playSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, this.getSoundVolume() * ((float) IafCommonConfig.INSTANCE.dragon.flapNoiseDistance.getValue() / 2), 0.6F + this.random.nextFloat() * 0.6F * this.getSoundPitch());
        }
        if (this.isOnGround() && this.doesWantToLand() && (this.isFlying() || this.isHovering())) {
            this.setFlying(false);
            this.setHovering(false);
        }
        if (this.isHovering()) {
            if (this.isSitting()) {
                this.setHovering(false);
            }
            this.hoverTicks++;
            if (this.doesWantToLand()) {
                this.setVelocity(this.getVelocity().add(0, -0.05D, 0));
            } else {
                if (this.getControllingPassenger() == null) {
                    this.setVelocity(this.getVelocity().add(0, 0.08D, 0));
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
        if (this.isSitting()) {
            this.getNavigation().stop();
        }
        if (this.isOnGround() && this.flyTicks != 0) {
            this.flyTicks = 0;
        }
        if (this.isFlying() && this.doesWantToLand() && this.getControllingPassenger() == null) {
            this.setHovering(false);
            if (this.isOnGround()) {
                this.flyTicks = 0;
            }
            this.setFlying(false);
        }
        if (this.isFlying()) {
            this.flyTicks++;
        }
        if ((this.isHovering() || this.isFlying()) && this.isSitting()) {
            this.setFlying(false);
            this.setHovering(false);
        }
        if (this.hasPassengers() && this.isGoingDown() && this.isOnGround()) {
            this.setHovering(false);
            this.setFlying(false);
        }
        if ((!this.getWorld().isClient && this.getRandom().nextInt(FLIGHT_CHANCE_PER_TICK) == 0 && !this.isSitting() && !this.isFlying() && this.getPassengerList().isEmpty() && !this.isBaby() && !this.isHovering() && !this.isSitting() && this.canMove() && !this.isOverAir() || this.getY() < -1)) {
            this.setHovering(true);
            this.hoverTicks = 0;
            this.flyTicks = 0;
        }
        if (this.getTarget() != null && !this.getPassengerList().isEmpty() && this.getOwner() != null && this.getPassengerList().contains(this.getOwner())) {
            this.setTarget(null);
        }
    }

    public boolean doesWantToLand() {
        return (this.flyTicks > 200 || this.flyTicks > 40 && this.flyProgress == 0) && !this.hasPassengers();
    }

    @Override
    public void tick() {
        super.tick();
        this.isOverAir = this.isOverAirLogic();
        if (this.isGoingUp()) {
            if (this.airBorneCounter == 0) {
                this.setVelocity(this.getVelocity().add(0, 0.02F, 0));
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
        if (this.attack() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity) {

            LivingEntity target = DragonUtils.riderLookingAtEntity(this, this.getControllingPassenger(), 3);
            if (this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_SCRATCH) {
                this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_SCRATCH : ANIMATION_BITE);
            }
            if (target != null && this.getAnimationTick() >= 10 && this.getAnimationTick() < 13) {
                target.damage(this.getWorld().getDamageSources().mobAttack(this), ((int) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getControllingPassenger() != null && this.getControllingPassenger().isSneaking()) {
            this.getControllingPassenger().stopRiding();
        }

        double motion = this.getVelocity().x * this.getVelocity().x + this.getVelocity().z * this.getVelocity().z;//Use squared norm2

        if (this.isFlying() && !this.isHovering() && this.getControllingPassenger() != null && this.isOverAir() && motion < 0.01F) {
            this.setHovering(true);
            this.setFlying(false);
        }
        if (this.isHovering() && !this.isFlying() && this.getControllingPassenger() != null && this.isOverAir() && motion > 0.01F) {
            this.setFlying(true);
            this.setHovering(false);
        }
        if (this.spacebarTicks > 0) this.spacebarTicks--;
        if (this.spacebarTicks > 10 && this.getOwner() != null && this.getPassengerList().contains(this.getOwner()) && !this.isFlying() && !this.isHovering())
            this.setHovering(true);
        if (this.getTarget() != null && this.getVehicle() == null && !this.getTarget().isAlive() || this.getTarget() != null && this.getTarget() instanceof DragonBaseEntity && !this.getTarget().isAlive())
            this.setTarget(null);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (this.hippogryphInventory != null && !this.getWorld().isClient)
            for (int i = 0; i < this.hippogryphInventory.size(); ++i) {
                ItemStack itemstack = this.hippogryphInventory.getStackInSlot(i);
                if (!itemstack.isEmpty())
                    this.dropStack(itemstack, 0.0F);
            }
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = this.createNavigator(this.getWorld(), AdvancedPathNavigate.MovementType.CLIMBING);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveControl(this, 10, true);
            this.navigation = this.createNavigator(this.getWorld(), AdvancedPathNavigate.MovementType.FLYING);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected EntityNavigation createNavigation(World worldIn) {
        return this.createNavigator(worldIn, AdvancedPathNavigate.MovementType.CLIMBING);
    }

    protected EntityNavigation createNavigator(World worldIn, AdvancedPathNavigate.MovementType type) {
        return this.createNavigator(worldIn, type, 2);
    }

    protected EntityNavigation createNavigator(World worldIn, AdvancedPathNavigate.MovementType type, float width) {
        AdvancedPathNavigate newNavigator = new AdvancedPathNavigate(this, this.getWorld(), type, width, (float) 2);
        this.navigation = newNavigator;
        newNavigator.setCanSwim(true);
        newNavigator.getNodeMaker().setCanOpenDoors(true);
        return newNavigator;
    }

    @Override
    public boolean isTeammate(Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) return true;
            if (entityIn instanceof TameableEntity tameable) return tameable.isOwner(livingentity);
            if (livingentity != null) return livingentity.isTeammate(entityIn);
        }
        return super.isTeammate(entityIn);
    }

    @Override
    public void onHearFlute(PlayerEntity player) {
        if (this.isTamed() && this.isOwner(player))
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
    public boolean isPersistent() {
        return true;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new HippogryphScreenHandler(syncId, this.hippogryphInventory, playerInventory, this);
    }

    @Override
    public void saveExtraData(PacketByteBuf buf) {
        buf.writeInt(this.getId());
    }
}
