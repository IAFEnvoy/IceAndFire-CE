package com.iafenvoy.iceandfire.entity;

import net.minecraft.world.entity.LivingEntity;

public class SlowPartEntity<T extends LivingEntity> extends MultipartPartEntity<T> {
    private final float baseRadius, baseOffsetY, baseSizeX, baseSizeY;

    public SlowPartEntity(T parent, float baseRadius, float angleYaw, float baseOffsetY, float baseSizeX, float baseSizeY, float damageMultiplier) {
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
    protected boolean isSlowFollow() {
        return true;
    }
}
