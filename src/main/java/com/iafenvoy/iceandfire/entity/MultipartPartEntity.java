package com.iafenvoy.iceandfire.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Shared collision-only implementation for the mod's multipart creatures.
 * Parts are owned and tracked by their parent through {@link Entity#getParts()}.
 */
public abstract class MultipartPartEntity<T extends LivingEntity> extends PartEntity<T> implements OwnableEntity {
    protected float radius;
    protected float angleYaw;
    protected float offsetY;
    protected float damageMultiplier;
    private float scaleWidth;
    private float scaleHeight;
    private float partYaw;

    protected MultipartPartEntity(T parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(parent);
        this.radius = radius;
        this.angleYaw = (angleYaw + 90.0F) * ((float) Math.PI / 180.0F);
        this.offsetY = offsetY;
        this.damageMultiplier = damageMultiplier;
        this.setScaleX(sizeX);
        this.setScaleY(sizeY);
        this.refreshDimensions();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
    }

    public static boolean sharesRider(Entity parent, Entity entityIn) {
        for (Entity entity : parent.getPassengers()) {
            if (entity.equals(entityIn)) return true;
            if (sharesRider(entity, entityIn)) return true;
        }
        return false;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void doWaterSplashEffect() {
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose poseIn) {
        return EntityDimensions.scalable(this.scaleWidth, this.scaleHeight);
    }

    public float getPartYaw() {
        return this.partYaw;
    }

    protected void setScaleX(float scale) {
        this.scaleWidth = scale;
        this.refreshDimensions();
    }

    protected void setScaleY(float scale) {
        this.scaleHeight = scale;
        this.refreshDimensions();
    }

    private void setPartYaw(float yaw) {
        this.partYaw = yaw % 360;
    }

    /** Updates this part from its parent's current state. Called by the parent every tick on both sides. */
    public void updatePosition() {
        T parent = this.getParent();
        if (parent.isRemoved()) return;

        // PartEntity is not ticked by the level, so advance interpolation state manually.
        this.setOldPosAndRot();
        float renderYawOffset = parent.yBodyRot;
        if (this.isSlowFollow()) {
            this.setPos(parent.xo + this.radius * Mth.cos((float) (renderYawOffset * (Math.PI / 180.0F) + this.angleYaw)), parent.yo + this.offsetY, parent.zo + this.radius * Mth.sin((float) (renderYawOffset * (Math.PI / 180.0F) + this.angleYaw)));
            double d0 = parent.getX() - this.getX();
            double d1 = parent.getY() - this.getY();
            double d2 = parent.getZ() - this.getZ();
            float f2 = -((float) (Mth.atan2(d1, Mth.sqrt((float) (d0 * d0 + d2 * d2))) * (180F / (float) Math.PI)));
            this.setXRot(this.limitAngle(this.getXRot(), f2));
            this.setYRot(renderYawOffset);
            this.setPartYaw(this.getYRot());
        } else {
            this.setPos(parent.getX() + this.radius * Mth.cos((float) (renderYawOffset * (Math.PI / 180.0F) + this.angleYaw)), parent.getY() + this.offsetY, parent.getZ() + this.radius * Mth.sin((float) (renderYawOffset * (Math.PI / 180.0F) + this.angleYaw)));
        }
        if (!this.level().isClientSide)
            this.collideWithNearbyEntities();
    }

    protected boolean isSlowFollow() {
        return false;
    }

    protected float limitAngle(float sourceAngle, float targetAngle) {
        float f = Mth.wrapDegrees(targetAngle - sourceAngle);
        if (f > 5.0F) f = 5.0F;
        if (f < -5.0F) f = -5.0F;

        float f1 = sourceAngle + f;
        if (f1 < 0.0F) f1 += 360.0F;
        else if (f1 > 360.0F) f1 -= 360.0F;

        return f1;
    }

    @Override
    public boolean is(@NotNull Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        return this.getParent().getPickResult();
    }

    public void collideWithNearbyEntities() {
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        T parent = this.getParent();
        entities.stream().filter(entity -> entity != parent && !sharesRider(parent, entity) && !(entity instanceof MultipartPartEntity<?>) && entity.isPushable()).forEach(entity -> entity.push(parent));
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        return this.getParent().hurt(source, damage * this.damageMultiplier);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FALLING_BLOCK) || source.is(DamageTypes.LAVA) || source.is(DamageTypeTags.IS_FIRE) || super.isInvulnerableTo(source);
    }

    @Override
    public void copyPosition(@NotNull Entity entity) {
        super.copyPosition(entity);
        this.setDeltaMovement(entity.getDeltaMovement());
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return this.getParent() instanceof OwnableEntity tameable ? tameable.getOwnerUUID() : null;
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        return this.getParent().interact(player, hand);
    }

    @Override
    public @NotNull InteractionResult interactAt(@NotNull Player player, @NotNull Vec3 hitPos, @NotNull InteractionHand hand) {
        return this.getParent().interactAt(player, hitPos, hand);
    }
}
