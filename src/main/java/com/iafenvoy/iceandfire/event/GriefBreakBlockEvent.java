package com.iafenvoy.iceandfire.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public final class GriefBreakBlockEvent extends Event implements ICancellableEvent {
    private final LivingEntity griefer;
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public GriefBreakBlockEvent(LivingEntity griefer, double targetX, double targetY, double targetZ) {
        this.griefer = griefer;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public LivingEntity getGriefer() {
        return this.griefer;
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

