package com.iafenvoy.iceandfire.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;

public class ItemRandomizer {
    private static final int INTERVAL = 1000;

    public static Item random(TagKey<Item> tag) {
        List<Item> items = BuiltInRegistries.ITEM.getOrCreateTag(tag).stream().map(Holder::value).toList();
        return items.get((int) (System.currentTimeMillis() / INTERVAL % items.size()));
    }
}