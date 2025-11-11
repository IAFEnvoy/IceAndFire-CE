package com.iafenvoy.iceandfire.util;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

public class ItemRandomizer {
    private static final int INTERVAL = 1000;

    public static Item random(TagKey<Item> tag) {
        List<Item> items = Registries.ITEM.getOrCreateEntryList(tag).stream().map(RegistryEntry::value).toList();
        return items.get((int) (System.currentTimeMillis() / INTERVAL % items.size()));
    }
}