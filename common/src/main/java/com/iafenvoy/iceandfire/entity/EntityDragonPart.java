package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class EntityDragonPart extends EntityMultipartPart {
    private final float baseRadius, baseOffsetY, baseSizeX, baseSizeY;

    public EntityDragonPart(EntityType<?> t, World world) {
        super(t, world);
        this.baseRadius = 0;
        this.baseOffsetY = 0;
        this.baseSizeX = 0;
        this.baseSizeY = 0;
    }

    public EntityDragonPart(EntityType<?> type, EntityDragonBase dragon, float baseRadius, float angleYaw, float baseOffsetY, float baseSizeX, float baseSizeY, float damageMultiplier) {
        super(type, dragon, baseRadius, angleYaw, baseOffsetY, baseSizeX, baseSizeY, damageMultiplier);
        this.baseRadius = baseRadius;
        this.baseOffsetY = baseOffsetY;
        this.baseSizeX = baseSizeX;
        this.baseSizeY = baseSizeY;
    }

    public EntityDragonPart(EntityDragonBase parent, float baseRadius, float angleYaw, float baseOffsetY, float baseSizeX, float baseSizeY, float damageMultiplier) {
        super(IafEntities.DRAGON_MULTIPART.get(), parent, baseRadius, angleYaw, baseOffsetY, baseSizeX, baseSizeY, damageMultiplier);
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
