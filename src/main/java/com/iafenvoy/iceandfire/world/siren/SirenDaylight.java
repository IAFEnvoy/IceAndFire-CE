package com.iafenvoy.iceandfire.world.siren;

import net.minecraft.core.BlockPos;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;

public final class SirenDaylight {
    private SirenDaylight() { }

    /** Follow the dimension's daylight timeline, including data pack overrides. */
    public static boolean isDay(Level level, BlockPos pos) {
        return level.environmentAttributes().getValue(EnvironmentAttributes.MONSTERS_BURN, pos);
    }
}
