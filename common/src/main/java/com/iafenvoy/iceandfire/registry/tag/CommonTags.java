package com.iafenvoy.iceandfire.registry.tag;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

/**
 * Mod-owned tags containing local defaults and optional imports from {@code c:} and {@code forge:}.
 * Consumers use these tags; foreign tags only export concrete mod entries and never reference
 * these unions or the other convention. Keep imports one-way to avoid tag dependency cycles.
 */
public final class CommonTags {
    public static final class Blocks {
        public static final TagKey<Block> COBBLESTONE = compat("cobblestone");
        public static final TagKey<Block> GRAVEL = compat("gravel");
        public static final TagKey<Block> STONE = compat("stone");

        private static TagKey<Block> compat(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(IceAndFire.MOD_ID, "compat/" + name));
        }
    }

    public static final class Items {
        public static final TagKey<Item> STRING = compat("strings");

        private static TagKey<Item> compat(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(IceAndFire.MOD_ID, "compat/" + name));
        }
    }

}
