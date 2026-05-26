package com.iafenvoy.iceandfire.registry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class CommonItemTags {
    public static final TagKey<Item> STRINGS = create("strings");
    public static final TagKey<Item> INGOTS_SILVER = create("ingots/silver");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
    }
}
