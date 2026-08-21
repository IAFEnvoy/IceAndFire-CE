package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.util.BlacklistedFromStatues;
import com.iafenvoy.iceandfire.mixin.LivingEntityAccessor;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafMobEffects;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import com.iafenvoy.iceandfire.util.EntityDataHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class StoneStatueEntity extends LivingEntity implements BlacklistedFromStatues {
    private static final EntityDataAccessor<String> TRAPPED_ENTITY_TYPE = SynchedEntityData.defineId(StoneStatueEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TRAPPED_ENTITY_DATA = SynchedEntityData.defineId(StoneStatueEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_WIDTH = SynchedEntityData.defineId(StoneStatueEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_HEIGHT = SynchedEntityData.defineId(StoneStatueEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_SCALE = SynchedEntityData.defineId(StoneStatueEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> CRACK_AMOUNT = SynchedEntityData.defineId(StoneStatueEntity.class, EntityDataSerializers.INT);
    private EntityDimensions stoneStatueSize = EntityDimensions.fixed(0.5F, 0.5F);

    public StoneStatueEntity(EntityType<? extends LivingEntity> t, Level worldIn) {
        super(t, worldIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, 20)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    public static StoneStatueEntity buildStatueEntity(LivingEntity parent) {
        StoneStatueEntity statue = IafEntities.STONE_STATUE.get().create(parent.level(), EntitySpawnReason.CONVERSION);
        if (statue == null) throw new IllegalStateException("Stone statue entity type is disabled");
        CompoundTag entityTag = new CompoundTag();
        try {
            if (!(parent instanceof Player)) {
                entityTag = EntityDataHelper.saveWithoutId(parent);
                trimRestorationData(entityTag);
            }
        } catch (Exception e) {
            IceAndFire.LOGGER.debug("Encountered issue creating stone statue from {}", parent);
        }
        statue.setTrappedTag(entityTag);
        statue.setTrappedEntityTypeString(BuiltInRegistries.ENTITY_TYPE.getKey(parent.getType()).toString());
        statue.setTrappedEntityWidth(parent.getBbWidth());
        statue.setTrappedHeight(parent.getBbHeight());
        statue.setTrappedScale(parent.getAgeScale());
        return statue;
    }

    private static void trimRestorationData(CompoundTag entityTag) {
        entityTag.remove("UUID");
        entityTag.remove("Pos");
        entityTag.remove("Motion");
        entityTag.remove("Rotation");
        entityTag.remove("FallDistance");
        entityTag.remove("Fire");
        entityTag.remove("Air");
        entityTag.remove("OnGround");
        entityTag.remove("PortalCooldown");
        entityTag.remove("Leash");
        entityTag.remove("Passengers");
    }

    @Override
    public void push(@NotNull Entity entityIn) {
    }

    @Override
    public void baseTick() {
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TRAPPED_ENTITY_TYPE, "minecraft:pig");
        builder.define(TRAPPED_ENTITY_DATA, "{}");
        builder.define(TRAPPED_ENTITY_WIDTH, 0.5F);
        builder.define(TRAPPED_ENTITY_HEIGHT, 0.5F);
        builder.define(TRAPPED_ENTITY_SCALE, 1F);
        builder.define(CRACK_AMOUNT, 0);
    }

    public EntityType<?> getTrappedEntityType() {
        return EntityType.byString(this.getTrappedEntityTypeString()).orElse(EntityType.PIG);
    }

    public String getTrappedEntityTypeString() {
        return this.entityData.get(TRAPPED_ENTITY_TYPE);
    }

    public void setTrappedEntityTypeString(String string) {
        this.entityData.set(TRAPPED_ENTITY_TYPE, string);
    }

    public CompoundTag getTrappedTag() {
        try {
            return TagParser.parseCompoundFully(this.entityData.get(TRAPPED_ENTITY_DATA));
        } catch (Exception exception) {
            IceAndFire.LOGGER.warn("Invalid trapped entity data on stone statue", exception);
            return new CompoundTag();
        }
    }

    public void setTrappedTag(CompoundTag tag) {
        this.entityData.set(TRAPPED_ENTITY_DATA, tag.toString());
    }

    public float getTrappedWidth() {
        return this.entityData.get(TRAPPED_ENTITY_WIDTH);
    }

    public void setTrappedEntityWidth(float size) {
        this.entityData.set(TRAPPED_ENTITY_WIDTH, size);
    }

    public float getTrappedHeight() {
        return this.entityData.get(TRAPPED_ENTITY_HEIGHT);
    }

    public void setTrappedHeight(float size) {
        this.entityData.set(TRAPPED_ENTITY_HEIGHT, size);
    }

    public float getTrappedScale() {
        return this.entityData.get(TRAPPED_ENTITY_SCALE);
    }

    public void setTrappedScale(float size) {
        this.entityData.set(TRAPPED_ENTITY_SCALE, size);
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("CrackAmount", this.getCrackAmount());
        output.putFloat("StatueWidth", this.getTrappedWidth());
        output.putFloat("StatueHeight", this.getTrappedHeight());
        output.putFloat("StatueScale", this.getTrappedScale());
        output.putString("StatueEntityType", this.getTrappedEntityTypeString());
        output.store("StatueEntityTag", CompoundTag.CODEC, this.getTrappedTag());
    }

    @Override
    public float getAgeScale() {
        return this.getTrappedScale();
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setCrackAmount(input.getIntOr("CrackAmount", 0));
        this.setTrappedEntityWidth(input.getFloatOr("StatueWidth", 0.5F));
        this.setTrappedHeight(input.getFloatOr("StatueHeight", 0.5F));
        this.setTrappedScale(input.getFloatOr("StatueScale", 1.0F));
        this.setTrappedEntityTypeString(input.getStringOr("StatueEntityType", "minecraft:pig"));
        input.read("StatueEntityTag", CompoundTag.CODEC).ifPresent(this::setTrappedTag);
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose poseIn) {
        return this.stoneStatueSize;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel && this.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(IafMobEffects.DEPETRIFICATION.get()))) {
            this.tryDepetrify(serverLevel);
        }
        this.setYRot(this.yBodyRot);
        this.yHeadRot = this.getYRot();
        if (Math.abs(this.getBbWidth() - this.getTrappedWidth()) > 0.01 || Math.abs(this.getBbHeight() - this.getTrappedHeight()) > 0.01) {
            double prevX = this.getX();
            double prevZ = this.getZ();
            this.stoneStatueSize = EntityDimensions.scalable(this.getTrappedWidth(), this.getTrappedHeight());
            this.refreshDimensions();
            this.setPos(prevX, this.getY(), prevZ);
        }
    }

    private void tryDepetrify(ServerLevel level) {
        EntityType<?> entityType = this.getTrappedEntityType();
        if (entityType == EntityType.PLAYER || entityType.builtInRegistryHolder().is(IafEntityTags.NO_DEPETRIFY))
            return;

        CompoundTag entityTag = this.getTrappedTag();
        if (entityTag.isEmpty()) return;

        Entity entity = entityType.create(level, EntitySpawnReason.CONVERSION);
        if (!(entity instanceof LivingEntity livingEntity)) return;

        try {
            CompoundTag restorationData = entityTag.copy();
            trimRestorationData(restorationData);
            EntityDataHelper.load(livingEntity, restorationData);
            livingEntity.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            if (level.addFreshEntity(livingEntity)) {
                this.remove(RemovalReason.DISCARDED);
            }
        } catch (Exception e) {
            IceAndFire.LOGGER.warn("Could not depetrify {}", this.getTrappedEntityTypeString(), e);
        }
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel level, DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_PROJECTILE) && amount > 0) {
            if (this.getTrappedEntityType().create(level, EntitySpawnReason.CONVERSION) instanceof LivingEntity livingEntity)
                ExperienceOrb.award(level, this.position(), ((LivingEntityAccessor) livingEntity).expReward(level));
            this.remove(RemovalReason.KILLED);
            return true;
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    public void kill(@NonNull ServerLevel level) {
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {

    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public int getCrackAmount() {
        return this.entityData.get(CRACK_AMOUNT);
    }

    public void setCrackAmount(int crackAmount) {
        this.entityData.set(CRACK_AMOUNT, crackAmount);
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }
}
