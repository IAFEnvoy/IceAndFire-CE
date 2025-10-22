package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.data.DragonType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public final class IafDragonColors {
    public static final DragonColor RED = register("red", Formatting.DARK_RED, IafDragonTypes.FIRE, IafItems.DRAGONEGG_RED, IafItems.DRAGONSCALES_RED);
    public static final DragonColor GREEN = register("green", Formatting.DARK_GREEN, IafDragonTypes.FIRE, IafItems.DRAGONEGG_GREEN, IafItems.DRAGONSCALES_GREEN);
    public static final DragonColor BRONZE = register("bronze", Formatting.GOLD, IafDragonTypes.FIRE, IafItems.DRAGONEGG_BRONZE, IafItems.DRAGONSCALES_BRONZE);
    public static final DragonColor GRAY = register("gray", Formatting.GRAY, IafDragonTypes.FIRE, IafItems.DRAGONEGG_GRAY, IafItems.DRAGONSCALES_GRAY);
    public static final DragonColor BLUE = register("blue", Formatting.AQUA, IafDragonTypes.ICE, IafItems.DRAGONEGG_BLUE, IafItems.DRAGONSCALES_BLUE);
    public static final DragonColor WHITE = register("white", Formatting.WHITE, IafDragonTypes.ICE, IafItems.DRAGONEGG_WHITE, IafItems.DRAGONSCALES_WHITE);
    public static final DragonColor SAPPHIRE = register("sapphire", Formatting.BLUE, IafDragonTypes.ICE, IafItems.DRAGONEGG_SAPPHIRE, IafItems.DRAGONSCALES_SAPPHIRE);
    public static final DragonColor SILVER = register("silver", Formatting.DARK_GRAY, IafDragonTypes.ICE, IafItems.DRAGONEGG_SILVER, IafItems.DRAGONSCALES_SILVER);
    public static final DragonColor ELECTRIC = register("electric", Formatting.DARK_BLUE, IafDragonTypes.LIGHTNING, IafItems.DRAGONEGG_ELECTRIC, IafItems.DRAGONSCALES_ELECTRIC);
    public static final DragonColor AMETHYST = register("amethyst", Formatting.LIGHT_PURPLE, IafDragonTypes.LIGHTNING, IafItems.DRAGONEGG_AMETHYST, IafItems.DRAGONSCALES_AMETHYST);
    public static final DragonColor COPPER = register("copper", Formatting.GOLD, IafDragonTypes.LIGHTNING, IafItems.DRAGONEGG_COPPER, IafItems.DRAGONSCALES_COPPER);
    public static final DragonColor BLACK = register("black", Formatting.DARK_GRAY, IafDragonTypes.LIGHTNING, IafItems.DRAGONEGG_BLACK, IafItems.DRAGONSCALES_BLACK);
    public static final DragonColor NETHER = register("nether", Formatting.DARK_RED, IafDragonTypes.NETHER, IafItems.DRAGONEGG_NETHER, IafItems.DRAGONSCALES_NETHER);
    public static final DragonColor CRIMSON = register("crimson", Formatting.DARK_RED, IafDragonTypes.NETHER, IafItems.DRAGONEGG_CRIMSON, IafItems.DRAGONSCALES_CRIMSON);

    private static DragonColor register(String name, Formatting color, DragonType dragonType, Supplier<Item> eggItem, Supplier<Item> scaleItem) {
        return Registry.register(IafRegistries.DRAGON_COLOR, Identifier.of(IceAndFire.MOD_ID, name), new DragonColor(name, color, dragonType, eggItem, scaleItem));
    }

    public static void init() {
    }
}
