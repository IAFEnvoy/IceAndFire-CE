package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class IafResourceKeys {
    public static final ResourceKey<Registry<BestiaryPage>> BESTIARY_PAGE = createKey("bestiary_page");
    public static final ResourceKey<Registry<DragonColor>> DRAGON_COLOR = createKey("dragon_color");
    public static final ResourceKey<Registry<DragonType>> DRAGON_TYPE = createKey("dragon_type");
    public static final ResourceKey<Registry<HippogryphType>> HIPPOGRYPH_TYPE = createKey("hippogryph_type");
    public static final ResourceKey<Registry<SeaSerpentType>> SEA_SERPENT_TYPE = createKey("sea_serpent_type");
    public static final ResourceKey<Registry<TrollType>> TROLL_TYPE = createKey("troll_type");

    private static <T> ResourceKey<Registry<T>> createKey(String id) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, id));
    }
}
