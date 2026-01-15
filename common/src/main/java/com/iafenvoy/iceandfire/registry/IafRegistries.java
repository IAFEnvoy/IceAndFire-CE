package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.*;
import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleDefaultedRegistry;

import java.util.Locale;

public final class IafRegistries {
    public static final DefaultedRegistry<BestiaryPage> BESTIARY_PAGE = create(String.format(Locale.ROOT, "%s:introduction", IceAndFire.MOD_ID), IafRegistryKeys.BESTIARY_PAGE);
    public static final DefaultedRegistry<DragonColor> DRAGON_COLOR = create(String.format(Locale.ROOT, "%s:red", IceAndFire.MOD_ID), IafRegistryKeys.DRAGON_COLOR);
    public static final DefaultedRegistry<DragonType> DRAGON_TYPE = create(String.format(Locale.ROOT, "%s:fire", IceAndFire.MOD_ID), IafRegistryKeys.DRAGON_TYPE);
    public static final DefaultedRegistry<HippogryphType> HIPPOGRYPH_TYPE = create(String.format(Locale.ROOT, "%s:black", IceAndFire.MOD_ID), IafRegistryKeys.HIPPOGRYPH_TYPE);
    public static final DefaultedRegistry<SeaSerpentType> SEA_SERPENT_TYPE = create(String.format(Locale.ROOT, "%s:blue", IceAndFire.MOD_ID), IafRegistryKeys.SEA_SERPENT_TYPE);
    public static final DefaultedRegistry<TrollType> TROLL_TYPE = create(String.format(Locale.ROOT, "%s:forest", IceAndFire.MOD_ID), IafRegistryKeys.TROLL_TYPE);

    private static <T> DefaultedRegistry<T> create(String defaultId, RegistryKey<Registry<T>> key) {
        return new SimpleDefaultedRegistry<>(defaultId, key, Lifecycle.stable(), false);
    }
}
