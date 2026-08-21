package com.iafenvoy.iceandfire.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/** Supplies the registry key required by the 26.1.2 property constructors. */
public final class IafRegistrationContext {
    private static final ThreadLocal<ResourceKey<Block>> BLOCK_ID = new ThreadLocal<>();
    private static final ThreadLocal<ResourceKey<Item>> ITEM_ID = new ThreadLocal<>();

    private IafRegistrationContext() {
    }

    public static <T extends Block> T createBlock(Identifier id, Supplier<? extends T> factory) {
        return with(BLOCK_ID, ResourceKey.create(Registries.BLOCK, id), factory);
    }

    public static <T extends Item> T createItem(Identifier id, Supplier<? extends T> factory) {
        return with(ITEM_ID, ResourceKey.create(Registries.ITEM, id), factory);
    }

    public static ResourceKey<Block> currentBlockId() {
        return BLOCK_ID.get();
    }

    public static ResourceKey<Item> currentItemId() {
        return ITEM_ID.get();
    }

    private static <T, V> T with(ThreadLocal<V> context, V value, Supplier<? extends T> factory) {
        V previous = context.get();
        context.set(value);
        try {
            return factory.get();
        } finally {
            if (previous == null) context.remove();
            else context.set(previous);
        }
    }
}
