package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.SeaSerpentType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class IafSeaSerpentTypes {
    public static final SeaSerpentType BLUE = register("blue", ChatFormatting.BLUE);
    public static final SeaSerpentType BRONZE = register("bronze", ChatFormatting.GOLD);
    public static final SeaSerpentType DEEPBLUE = register("deepblue", ChatFormatting.DARK_BLUE);
    public static final SeaSerpentType GREEN = register("green", ChatFormatting.DARK_GREEN);
    public static final SeaSerpentType PURPLE = register("purple", ChatFormatting.DARK_PURPLE);
    public static final SeaSerpentType RED = register("red", ChatFormatting.DARK_RED);
    public static final SeaSerpentType TEAL = register("teal", ChatFormatting.AQUA);

    private static SeaSerpentType register(String name, ChatFormatting color) {
        return Registry.register(IafRegistries.SEA_SERPENT_TYPE, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, name), new SeaSerpentType(name, color));
    }

    public static void init() {
    }
}
