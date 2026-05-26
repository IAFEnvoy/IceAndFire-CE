package com.iafenvoy.iceandfire.registry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class CommonBlockTags {
    public static final TagKey<Block> COBBLESTONES = create("cobblestones");
    public static final TagKey<Block> GRAVELS = create("gravels");
    public static final TagKey<Block> STONES = create("stones");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", name));
    }
}
