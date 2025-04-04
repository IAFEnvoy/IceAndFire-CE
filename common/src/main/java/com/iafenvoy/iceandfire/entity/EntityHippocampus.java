package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.ai.AquaticAIFindWaterTarget;
import com.iafenvoy.iceandfire.entity.ai.AquaticAIGetInWater;
import com.iafenvoy.iceandfire.entity.ai.HippocampusAIWander;
import com.iafenvoy.iceandfire.entity.util.ChainBuffer;
import com.iafenvoy.iceandfire.entity.util.ICustomMoveController;
import com.iafenvoy.iceandfire.entity.util.ISyncMount;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.iafenvoy.iceandfire.screen.handler.HippocampusScreenHandler;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.AnimationHandler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.data.EntityPropertyDelegate;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EntityHippocampus extends TameableEntity implements NamedScreenHandlerFactory, ISyncMount, IAnimatedEntity, ICustomMoveController, InventoryChangedListener, Saddleable {
    public static final int INV_SLOT_SADDLE = 0;
    public static final int INV_SLOT_CHEST = 1;
    public static final int INV_SLOT_ARMOR = 2;
    public static final int INV_BASE_COUNT = 3;
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(EntityHippocampus.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SADDLE = DataTracker.registerData(EntityHippocampus.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> ARMOR = DataTracker.registerData(EntityHippocampus.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CHESTED = DataTracker.registerData(EntityHippocampus.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Byte> CONTROL_STATE = DataTracker.registerData(EntityHippocampus.class, TrackedDataHandlerRegistry.BYTE);
    // These are from TamableAnimal
    private static final int FLAG_SITTING = 1;
    private static final int FLAG_TAME = 4;
    private static final Text CONTAINER_TITLE = Text.translatable("entity.iceandfire.hippocampus");

    public static Animation ANIMATION_SPEAK;
    public float onLandProgress;
    public ChainBuffer tail_buffer;
    public SimpleInventory inventory;
    public float sitProgress;
    private int animationTick;
    private Animation currentAnimation;

    public EntityHippocampus(EntityType<? extends EntityHippocampus> entityType, World worldIn) {
        super(entityType, worldIn);
        ANIMATION_SPEAK = Animation.create(15);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.moveControl = new HippoMoveControl(this);
        this.setStepHeight(1F);
        if (worldIn.isClient)
            this.tail_buffer = new ChainBuffer();
        this.createInventory();
    }

    public static int getIntFromArmor(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() == Items.IRON_HORSE_ARMOR)
            return 1;
        if (!stack.isEmpty() && stack.getItem() == Items.GOLDEN_HORSE_ARMOR)
            return 2;
        if (!stack.isEmpty() && stack.getItem() == Items.DIAMOND_HORSE_ARMOR)
            return 3;
        return 0;
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected EntityNavigation createNavigation(World level) {
        return new AmphibiousSwimNavigation(this, level);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new AquaticAIFindWaterTarget(this, 10, true));
        this.goalSelector.add(2, new AquaticAIGetInWater(this, 1.0D));
        this.goalSelector.add(3, new HippocampusAIWander(this, 1));
        this.goalSelector.add(4, new AnimalMateGoal(this, 1.0D));

        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.add(0, new TemptGoal(this, 1.0D, Ingredient.fromTag(IafItemTags.TEMPT_HIPPOCAMPUS), false));
    }

    @Override
    public int getXpToDrop() {
        return 2;
    }

    @Override
    public float getPathfindingFavor(BlockPos pos) {
        return this.getWorld().getBlockState(pos.down()).isOf(Blocks.WATER) ? 10.0F : this.getWorld().getLightLevel(pos) - 0.5F;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }

    @Override
    public boolean isTeammate(Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity)
                return true;
            if (entityIn instanceof TameableEntity tameable)
                return tameable.isOwner(livingentity);
            if (livingentity != null)
                return livingentity.isTeammate(entityIn);
        }

        return super.isTeammate(entityIn);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(ARMOR, 0);
        this.dataTracker.startTracking(SADDLE, Boolean.FALSE);
        this.dataTracker.startTracking(CHESTED, Boolean.FALSE);
        this.dataTracker.startTracking(CONTROL_STATE, (byte) 0);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof MobEntity mob)
            return mob;
        if (this.isSaddled()) {
            entity = this.getFirstPassenger();
            if (entity instanceof PlayerEntity player)
                return player;
        }
        return null;
    }

    @Override
    public ItemStack tryEquip(ItemStack itemStackIn) {
        if (itemStackIn == null)
            return ItemStack.EMPTY;
        EquipmentSlot equipmentSlot = getPreferredEquipmentSlot(itemStackIn);
        int j = equipmentSlot.getEntitySlotId() - 500 + 2;
        if (j >= 0 && j < this.inventory.size()) {
            this.inventory.setStack(j, itemStackIn);
            return itemStackIn;
        } else
            return ItemStack.EMPTY;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.inventory != null && !this.getWorld().isClient) {
            for (int i = 0; i < this.inventory.size(); ++i) {
                ItemStack itemstack = this.inventory.getStack(i);
                if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack))
                    this.dropStack(itemstack);
            }
        }
        if (this.isChested()) {
            if (!this.getWorld().isClient) {
                this.dropItem(Blocks.CHEST);
            }
            this.setChested(false);
        }
    }

    protected void dropChestItems() {
        for (int i = 3; i < 18; i++)
            if (!this.inventory.getStack(i).isEmpty()) {
                if (!this.getWorld().isClient)
                    this.dropStack(this.inventory.getStack(i), 1);
                this.inventory.removeStack(i);
            }
    }

    private void updateControlState(int i, boolean newState) {
        byte prevState = this.dataTracker.get(CONTROL_STATE);
        if (newState)
            this.dataTracker.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        else
            this.dataTracker.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
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
    public boolean canStartRiding(Entity rider) {
        return true;
    }

    @Override
    public void updatePassengerPosition(Entity passenger, PositionUpdater callback) {
        super.updatePassengerPosition(passenger, callback);
        if (this.hasPassenger(passenger)) {
            this.bodyYaw = this.getYaw();
            this.setBodyYaw(passenger.getYaw());
        }
        double ymod1 = this.onLandProgress * -0.02;
        passenger.setPosition(this.getX(), this.getY() + 0.6F + ymod1, this.getZ());
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.getWorld().isClient)
            if (this.random.nextInt(900) == 0 && this.deathTime == 0)
                this.heal(1.0F);
        AnimationHandler.INSTANCE.updateAnimations(this);
        if (this.getControllingPassenger() != null && this.age % 20 == 0)
            (this.getControllingPassenger()).addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 30, 0, true, false));

        if (this.getWorld().isClient)
            this.tail_buffer.calculateChainSwingBuffer(40, 10, 1F, this);
        boolean inWater = this.isTouchingWater();
        if (!inWater && this.onLandProgress < 20.0F)
            this.onLandProgress += 1F;
        else if (inWater && this.onLandProgress > 0.0F)
            this.onLandProgress -= 1F;
        boolean sitting = this.isSitting();
        if (sitting && this.sitProgress < 20.0F)
            this.sitProgress += 0.5F;
        else if (!sitting && this.sitProgress > 0.0F)
            this.sitProgress -= 0.5F;
    }

    @Override
    protected void tickControlled(PlayerEntity player, Vec3d travelVector) {
        super.tickControlled(player, travelVector);
        Vec2f vec2 = this.getRiddenRotation(player);
        this.setRotation(vec2.y, vec2.x);
        this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();
        if (this.isLogicalSideForUpdatingMovement()) {
            Vec3d vec3 = this.getVelocity();

            if (this.isGoingUp()) {
                if (!this.isTouchingWater() && this.isOnGround())
                    this.jump();
                else if (this.isTouchingWater())
                    this.setVelocity(vec3.add(0, 0.04F, 0));
            }
            if (this.isGoingDown() && this.isTouchingWater())
                this.setVelocity(vec3.add(0, -0.025F, 0));
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

    @Override
    protected float getSaddledSpeed(PlayerEntity player) {
        float speed = (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.6F;
        if (this.isTouchingWater())
            speed *= IafCommonConfig.INSTANCE.hippocampus.swimSpeedMod.getValue().floatValue();
        else speed *= 0.2F;
        return speed;
    }


    public boolean isGoingUp() {
        return (this.dataTracker.get(CONTROL_STATE) & 1) == 1;
    }

    public boolean isGoingDown() {
        return (this.dataTracker.get(CONTROL_STATE) >> 1 & 1) == 1;
    }

    public boolean isBlinking() {
        return this.age % 50 > 43;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putBoolean("Chested", this.isChested());
        compound.putBoolean("Saddled", this.isSaddled());
        compound.putInt("Armor", this.getArmor());
        NbtList nbttaglist = new NbtList();
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemstack = this.inventory.getStack(i);
            if (!itemstack.isEmpty()) {
                NbtCompound CompoundNBT = new NbtCompound();
                CompoundNBT.putByte("Slot", (byte) i);
                itemstack.writeNbt(CompoundNBT);
                nbttaglist.add(CompoundNBT);
            }
        }
        compound.put("Items", nbttaglist);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setChested(compound.getBoolean("Chested"));
        this.setSaddled(compound.getBoolean("Saddled"));
        this.setArmor(compound.getInt("Armor"));
        if (this.inventory != null) {
            NbtList nbttaglist = compound.getList("Items", 10);
            this.createInventory();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                NbtCompound CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.inventory.setStack(j, ItemStack.fromNbt(CompoundNBT));
            }
        }
    }

    protected int getInventorySize() {
        return this.isChested() ? 18 : 3;
    }

    protected void createInventory() {
        SimpleInventory simplecontainer = this.inventory;
        this.inventory = new SimpleInventory(this.getInventorySize());
        if (simplecontainer != null) {
            simplecontainer.removeListener(this);
            int i = Math.min(simplecontainer.size(), this.inventory.size());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = simplecontainer.getStack(j);
                if (!itemstack.isEmpty())
                    this.inventory.setStack(j, itemstack.copy());
            }
        }

        this.inventory.addListener(this);
        this.updateContainerEquipment();
    }

    protected void updateContainerEquipment() {
        if (!this.getWorld().isClient) {
            this.setSaddled(!this.inventory.getStack(INV_SLOT_SADDLE).isEmpty());
            this.setChested(!this.inventory.getStack(INV_SLOT_CHEST).isEmpty());
            this.setArmor(getIntFromArmor(this.inventory.getStack(INV_SLOT_ARMOR)));
        }
    }

    public boolean hasInventoryChanged(Inventory pInventory) {
        return this.inventory != pInventory;
    }

    @Override
    public boolean canBeSaddled() {
        return this.isAlive() && !this.isBaby() && this.isTamed();
    }

    @Override
    public void saddle(SoundCategory pSource) {
        this.inventory.setStack(0, new ItemStack(Items.SADDLE));
    }

    @Override
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
        if (!chested)
            this.dropChestItems();
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

    public int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        EntityData data = super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVariant(this.getRandom().nextInt(6));
        return data;
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
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, ANIMATION_SPEAK};
    }

    @Override
    public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity ageable) {
        if (ageable instanceof EntityHippocampus) {
            EntityHippocampus hippo = new EntityHippocampus(IafEntities.HIPPOCAMPUS.get(), this.getWorld());
            hippo.setVariant(this.getRandom().nextBoolean() ? this.getVariant() : ((EntityHippocampus) ageable).getVariant());
            return hippo;
        }
        return null;
    }

    @Override
    public void travel(Vec3d pTravelVector) {
        if (this.isLogicalSideForUpdatingMovement() && this.isTouchingWater()) {
            this.updateVelocity(0.1F, pTravelVector);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9D));
        } else
            super.travel(pTravelVector);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(IafItemTags.BREED_HIPPOCAMPUS);
    }

    @Override
    public void playAmbientSound() {
        if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION)
            this.setAnimation(ANIMATION_SPEAK);
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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        // Breed item
        if (itemstack.isIn(IafItemTags.BREED_HIPPOCAMPUS) && this.getBreedingAge() == 0 && !this.isInLove()) {
            this.setSitting(false);
            this.lovePlayer(player);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
            if (!player.isCreative())
                itemstack.decrement(1);
            return ActionResult.SUCCESS;
        }
        // Food item
        if (itemstack.isIn(IafItemTags.HEAL_HIPPOCAMPUS)) {
            if (!this.getWorld().isClient) {
                this.heal(5);
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
                for (int i = 0; i < 3; i++)
                    this.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, itemstack), this.getX() + this.random.nextFloat() * this.getWidth() * 2.0F - this.getWidth(), this.getY() + this.random.nextFloat() * this.getHeight(), this.getZ() + this.random.nextFloat() * this.getWidth() * 2.0F - this.getWidth(), 0, 0, 0);
                if (!player.isCreative())
                    itemstack.decrement(1);
            }
            if (!this.isTamed() && this.getRandom().nextInt(3) == 0) {
                this.setOwner(player);
                for (int i = 0; i < 6; i++)
                    this.getWorld().addParticle(ParticleTypes.HEART, this.getX() + this.random.nextFloat() * this.getWidth() * 2.0F - this.getWidth(), this.getY() + this.random.nextFloat() * this.getHeight(), this.getZ() + this.random.nextFloat() * this.getWidth() * 2.0F - this.getWidth(), 0, 0, 0);
            }
            return ActionResult.SUCCESS;

        }
        // Owner
        if (this.isOwner(player) && itemstack.getItem() == Items.STICK) {
            this.setSitting(!this.isSitting());
            return ActionResult.SUCCESS;
        }
        // Inventory
        if (this.isOwner(player) && itemstack.isEmpty() && player.isSneaking()) {
            this.openInventory(player);
            return ActionResult.success(this.getWorld().isClient);
        }
        // Riding
        if (this.isOwner(player) && this.isSaddled() && !this.isBaby() && !player.hasVehicle()) {
            this.doPlayerRide(player);
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    protected void doPlayerRide(PlayerEntity pPlayer) {
        this.setSitting(false);
        if (!this.getWorld().isClient) {
            pPlayer.setYaw(this.getYaw());
            pPlayer.setPitch(this.getPitch());
            pPlayer.startRiding(this);
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new HippocampusScreenHandler(syncId, this.inventory, inv, this, new EntityPropertyDelegate(this.getId()));
    }

    public void openInventory(PlayerEntity player) {
        player.openHandledScreen(this);
    }

    @Override
    public void up(boolean up) {
        this.updateControlState(0, up);
    }

    @Override
    public void down(boolean down) {
        this.updateControlState(1, down);
    }

    @Override
    public void attack(boolean attack) {
    }

    @Override
    public void strike(boolean strike) {

    }

    @Override
    public void dismount(boolean dismount) {
        this.updateControlState(2, dismount);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return IafSounds.HIPPOCAMPUS_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return IafSounds.HIPPOCAMPUS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return IafSounds.HIPPOCAMPUS_DIE.get();
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    public PlayerEntity getRidingPlayer() {
        if (this.getControllingPassenger() instanceof PlayerEntity player) {
            return player;
        }
        return null;
    }

    public int getInventoryColumns() {
        return 5; // TODO :: Introduce upgrade item?
    }

    @Override
    public void onInventoryChanged(Inventory pInvBasic) {
        boolean flag = this.isSaddled();
        this.updateContainerEquipment();
        if (this.age > 20 && !flag && this.isSaddled())
            this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    @Override
    public void tick() {
        super.tick();
        this.setAir(this.getMaxAir());
    }

    /**
     * Only called Server side
     */
    class HippoMoveControl extends MoveControl {
        private final EntityHippocampus hippo = EntityHippocampus.this;

        public HippoMoveControl(EntityHippocampus entityHippocampus) {
            super(entityHippocampus);
        }

        private void updateSpeed() {
            if (this.hippo.isTouchingWater())
                this.hippo.setVelocity(this.hippo.getVelocity().add(0.0D, 0.005D, 0.0D));
            else if (this.hippo.isOnGround())
                this.hippo.setMovementSpeed(Math.max(this.hippo.getMovementSpeed() / 4.0F, 0.06F));
        }

        @Override
        public void tick() {
            this.updateSpeed();
            if (this.state == State.MOVE_TO && !this.hippo.getNavigation().isIdle()) {
                double d0 = this.targetX - this.hippo.getX();
                double d1 = this.targetY - this.hippo.getY();
                double d2 = this.targetZ - this.hippo.getZ();
                double distance = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                if (distance < (double) 1.0E-5F)
                    this.entity.setMovementSpeed(0.0F);
                else {
                    d1 /= distance;
                    float minRotation = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                    this.hippo.setYaw(this.wrapDegrees(this.hippo.getYaw(), minRotation, 90.0F));
                    this.hippo.bodyYaw = this.hippo.getYaw();
                    float maxSpeed = (float) (this.speed * this.hippo.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                    maxSpeed *= 0.6F;
                    if (this.hippo.isTouchingWater()) {
                        maxSpeed *= IafCommonConfig.INSTANCE.hippocampus.swimSpeedMod.getValue().floatValue();
                    } else
                        maxSpeed *= 0.2F;
                    this.hippo.setMovementSpeed(MathHelper.lerp(0.125F, this.hippo.getMovementSpeed(), maxSpeed));
                    this.hippo.setVelocity(this.hippo.getVelocity().add(0.0D, (double) this.hippo.getMovementSpeed() * d1 * 0.1D, 0.0D));
                }
            } else
                this.hippo.setMovementSpeed(0.0F);
        }
    }
}
