package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;

public final class IafBannerPatterns {
    public static final ResourceKey<BannerPattern> PATTERN_DREAD = ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "dread"));
}
