package com.iafenvoy.iceandfire.entity;

public class DragonPartEntity extends MultipartPartEntity<DragonBaseEntity> {
    private final float baseRadius, baseOffsetY, baseSizeX, baseSizeY;

    public DragonPartEntity(DragonBaseEntity parent, float baseRadius, float angleYaw, float baseOffsetY, float baseSizeX, float baseSizeY, float damageMultiplier) {
        super(parent, baseRadius, angleYaw, baseOffsetY, baseSizeX, baseSizeY, damageMultiplier);
        this.baseRadius = baseRadius;
        this.baseOffsetY = baseOffsetY;
        this.baseSizeX = baseSizeX;
        this.baseSizeY = baseSizeY;
    }

    public void updateScale(float scale) {
        this.radius = this.baseRadius * scale;
        this.offsetY = this.baseOffsetY * scale;
        this.setScaleX(this.baseSizeX * scale);
        this.setScaleY(this.baseSizeY * scale);
    }

    @Override
    public void collideWithNearbyEntities() {
    }
}
