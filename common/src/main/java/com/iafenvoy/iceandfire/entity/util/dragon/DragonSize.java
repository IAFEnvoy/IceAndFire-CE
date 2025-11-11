package com.iafenvoy.iceandfire.entity.util.dragon;

import net.minecraft.util.math.MathHelper;

import java.util.List;

public record DragonSize(float x0, float x1) {
    private static final List<DragonSize> GROWTH_STAGES = List.of(
            new DragonSize(1F, 3F),
            new DragonSize(3F, 7F),
            new DragonSize(7F, 12.5F),
            new DragonSize(12.5F, 20F),
            new DragonSize(20F, 30F)
    );

    public float step() {
        return this.x1 - this.x0;
    }

    public static DragonSize getSize(int stage) {
        return GROWTH_STAGES.get(MathHelper.clamp(stage - 1, 0, GROWTH_STAGES.size()));
    }
}
