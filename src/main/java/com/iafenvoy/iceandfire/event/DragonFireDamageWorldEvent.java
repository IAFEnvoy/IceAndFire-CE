package com.iafenvoy.iceandfire.event;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public final class DragonFireDamageWorldEvent extends Event implements ICancellableEvent {
    private final DragonBaseEntity dragon;
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public DragonFireDamageWorldEvent(DragonBaseEntity dragon, double targetX, double targetY, double targetZ) {
        this.dragon = dragon;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public DragonBaseEntity getDragon() {
        return this.dragon;
    }

    public double getTargetX() {
        return this.targetX;
    }

    public double getTargetY() {
        return this.targetY;
    }

    public double getTargetZ() {
        return this.targetZ;
    }
}

