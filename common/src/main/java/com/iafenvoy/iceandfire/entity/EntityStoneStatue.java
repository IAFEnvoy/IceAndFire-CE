package com.iafenvoy.iceandfire.entity;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.util.IBlacklistedFromStatues;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

public class EntityStoneStatue extends LivingEntity implements IBlacklistedFromStatues {
    private static final TrackedData<String> TRAPPED_ENTITY_TYPE = DataTracker.registerData(EntityStoneStatue.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<NbtCompound> TRAPPED_ENTITY_DATA = DataTracker.registerData(EntityStoneStatue.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<Float> TRAPPED_ENTITY_WIDTH = DataTracker.registerData(EntityStoneStatue.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> TRAPPED_ENTITY_HEIGHT = DataTracker.registerData(EntityStoneStatue.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> TRAPPED_ENTITY_SCALE = DataTracker.registerData(EntityStoneStatue.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> CRACK_AMOUNT = DataTracker.registerData(EntityStoneStatue.class, TrackedDataHandlerRegistry.INTEGER);
    private EntityDimensions stoneStatueSize = EntityDimensions.fixed(0.5F, 0.5F);

    private Entity renderEntity;
    private EntityType<?> renderType;
    private NbtCompound renderTag;

    public Entity getOrCreateRenderEntity() {
        EntityType<?> type = this.getTrappedEntityType();
        NbtCompound tag = this.getTrappedTag();
        if (this.renderTag == null || this.renderType != type || !tag.equals(this.renderTag)) {
            this.renderType = type;
            this.renderEntity = type.create(this.getWorld());
            this.renderTag = tag.copy();
            if (this.renderEntity != null) {
                try { this.renderEntity.readNbt(this.renderTag); }
                catch (RuntimeException e) { IceAndFire.LOGGER.warn("Invalid statue data for {}", type, e); }
            }
        }
        return this.renderEntity;
    }

    public EntityStoneStatue(EntityType<? extends LivingEntity> t, World worldIn) {
        super(t, worldIn);
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0D)
                //ATTACK
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D);
    }

    public static EntityStoneStatue buildStatueEntity(LivingEntity parent) {
        EntityStoneStatue statue = IafEntities.STONE_STATUE.get().create(parent.getWorld());
        NbtCompound entityTag = new NbtCompound();
        try {
            if (!(parent instanceof PlayerEntity)) {
                parent.writeNbt(entityTag);
            }
        } catch (Exception e) {
            IceAndFire.LOGGER.debug("Encountered issue creating stone statue from {}", parent);
        }
        assert statue != null;
        statue.setTrappedTag(entityTag);
        statue.setTrappedEntityTypeString(Registries.ENTITY_TYPE.getId(parent.getType()).toString());
        statue.setTrappedEntityWidth(parent.getWidth());
        statue.setTrappedHeight(parent.getHeight());
        statue.setTrappedScale(parent.getScaleFactor());
        return statue;
    }

    @Override
    public void pushAwayFrom(Entity entityIn) {
    }

    @Override
    public void baseTick() {
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TRAPPED_ENTITY_TYPE, "minecraft:pig");
        this.dataTracker.startTracking(TRAPPED_ENTITY_DATA, new NbtCompound());
        this.dataTracker.startTracking(TRAPPED_ENTITY_WIDTH, 0.5F);
        this.dataTracker.startTracking(TRAPPED_ENTITY_HEIGHT, 0.5F);
        this.dataTracker.startTracking(TRAPPED_ENTITY_SCALE, 1F);
        this.dataTracker.startTracking(CRACK_AMOUNT, 0);
    }

    public EntityType<?> getTrappedEntityType() {
        return EntityType.get(this.getTrappedEntityTypeString()).orElse(EntityType.PIG);
    }

    public String getTrappedEntityTypeString() {
        return this.dataTracker.get(TRAPPED_ENTITY_TYPE);
    }

    public void setTrappedEntityTypeString(String string) {
        this.dataTracker.set(TRAPPED_ENTITY_TYPE, string);
    }

    public NbtCompound getTrappedTag() {
        return this.dataTracker.get(TRAPPED_ENTITY_DATA);
    }

    public void setTrappedTag(NbtCompound tag) {
        this.dataTracker.set(TRAPPED_ENTITY_DATA, tag);
    }

    public float getTrappedWidth() {
        return this.dataTracker.get(TRAPPED_ENTITY_WIDTH);
    }

    public void setTrappedEntityWidth(float size) {
        this.dataTracker.set(TRAPPED_ENTITY_WIDTH, size);
    }

    public float getTrappedHeight() {
        return this.dataTracker.get(TRAPPED_ENTITY_HEIGHT);
    }

    public void setTrappedHeight(float size) {
        this.dataTracker.set(TRAPPED_ENTITY_HEIGHT, size);
    }

    public float getTrappedScale() {
        return this.dataTracker.get(TRAPPED_ENTITY_SCALE);
    }

    public void setTrappedScale(float size) {
        this.dataTracker.set(TRAPPED_ENTITY_SCALE, size);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putInt("CrackAmount", this.getCrackAmount());
        tag.putFloat("StatueWidth", this.getTrappedWidth());
        tag.putFloat("StatueHeight", this.getTrappedHeight());
        tag.putFloat("StatueScale", this.getTrappedScale());
        tag.putString("StatueEntityType", this.getTrappedEntityTypeString());
        tag.put("StatueEntityTag", this.getTrappedTag());
    }

    @Override
    public float getScaleFactor() {
        return this.getTrappedScale();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        this.setCrackAmount(tag.getByte("CrackAmount"));
        this.setTrappedEntityWidth(tag.getFloat("StatueWidth"));
        this.setTrappedHeight(tag.getFloat("StatueHeight"));
        this.setTrappedScale(tag.getFloat("StatueScale"));
        this.setTrappedEntityTypeString(tag.getString("StatueEntityType"));
        if (tag.contains("StatueEntityTag")) {
            this.setTrappedTag(tag.getCompound("StatueEntityTag"));

        }
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose poseIn) {
        return this.stoneStatueSize;
    }

    @Override
    public void tick() {
        super.tick();
        this.setYaw(this.bodyYaw);
        this.headYaw = this.getYaw();
        if (Math.abs(this.getWidth() - this.getTrappedWidth()) > 0.01 || Math.abs(this.getHeight() - this.getTrappedHeight()) > 0.01) {
            double prevX = this.getX();
            double prevZ = this.getZ();
            this.stoneStatueSize = EntityDimensions.changing(this.getTrappedWidth(), this.getTrappedHeight());
            this.calculateDimensions();
            this.setPosition(prevX, this.getY(), prevZ);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.IS_PROJECTILE) && amount > 0) {
            if (this.getWorld() instanceof ServerWorld serverWorld && this.getTrappedEntityType().create(serverWorld) instanceof LivingEntity livingEntity)
                ExperienceOrbEntity.spawn(serverWorld, this.getPos(), livingEntity.getXpToDrop());
            this.remove(RemovalReason.KILLED);
            return true;
        }
        return super.damage(source, amount);
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return ImmutableList.of();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slotIn, ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    public int getCrackAmount() {
        return this.dataTracker.get(CRACK_AMOUNT);
    }

    public void setCrackAmount(int crackAmount) {
        this.dataTracker.set(CRACK_AMOUNT, crackAmount);
    }


    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }
}
