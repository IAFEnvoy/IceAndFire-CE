package com.iafenvoy.iceandfire.render.entity.feature;

import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/** Shared client-side state used to suppress duplicate passenger rendering. */
public final class DragonRiderRenderState {
    public static final List<Entity> RENDERING_RIDERS = new ArrayList<>();

    private DragonRiderRenderState() {
    }
}
