package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.data.DragonType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public final class IafDragonColors {
    public static final DragonColor RED = register("red", ChatFormatting.DARK_RED, IafDragonTypes.FIRE, IafItems.DRAGONEGG_RED, IafItems.DRAGONSCALES_RED);
    public static final DragonColor GREEN = register("green", ChatFormatting.DARK_GREEN, IafDragonTypes.FIRE, IafItems.DRAGONEGG_GREEN, IafItems.DRAGONSCALES_GREEN);
    public static final DragonColor BRONZE = register("bronze", ChatFormatting.GOLD, IafDragonTypes.FIRE, IafItems.DRAGONEGG_BRONZE, IafItems.DRAGONSCALES_BRONZE);
    public static final DragonColor GRAY = register("gray", ChatFormatting.GRAY, IafDragonTypes.FIRE, IafItems.DRAGONEGG_GRAY, IafItems.DRAGONSCALES_GRAY);
    public static final DragonColor BLUE = register("blue", ChatFormatting.AQUA, IafDragonTypes.ICE, IafItems.DRAGONEGG_BLUE, IafItems.DRAGONSCALES_BLUE);
    public static final DragonColor WHITE = register("white", ChatFormatting.WHITE, IafDragonTypes.ICE, IafItems.DRAGONEGG_WHITE, IafItems.DRAGONSCALES_WHITE);
    public static final DragonColor SAPPHIRE = register("sapphire", ChatFormatting.BLUE, IafDragonTypes.ICE, IafItems.DRAGONEGG_SAPPHIRE, IafItems.DRAGONSCALES_SAPPHIRE);
    public static final DragonColor SILVER = register("silver", ChatFormatting.DARK_GRAY, IafDragonTypes.ICE, IafItems.DRAGONEGG_SILVER, IafItems.DRAGONSCALES_SILVER);
    public static final DragonColor ELECTRIC = register("electric", ChatFormatting.DARK_BLUE, IafDragonTypes.LIGHTNING, IafItems.DRAGONEGG_ELECTRIC, IafItems.DRAGONSCALES_ELECTRIC);
    public static final DragonColor AMETHYST = register("amethyst", ChatFormatting.LIGHT_PURPLE, IafDragonTypes.LIGHTNING, IafItems.DRAGONEGG_AMETHYST, IafItems.DRAGONSCALES_AMETHYST);
    public static final DragonColor COPPER = register("copper", ChatFormatting.GOLD, IafDragonTypes.LIGHTNING, IafItems.DRAGONEGG_COPPER, IafItems.DRAGONSCALES_COPPER);
    public static final DragonColor BLACK = register("black", ChatFormatting.DARK_GRAY, IafDragonTypes.LIGHTNING, IafItems.DRAGONEGG_BLACK, IafItems.DRAGONSCALES_BLACK);

    private static DragonColor register(String name, ChatFormatting color, DragonType dragonType, Supplier<Item> eggItem, Supplier<Item> scaleItem) {
        return Registry.register(IafRegistries.DRAGON_COLOR, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, name), new DragonColor(name, color, dragonType, eggItem, scaleItem));
    }

    public static void init() {
    }
}
