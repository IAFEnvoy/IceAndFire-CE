package com.iafenvoy.iceandfire.registry.tag;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

/**
 * Tags owned by this mod which union the common ({@code c:}) and forge ({@code forge:}) conventions.
 * Code must look these up instead of a foreign namespace: on 1.20.1 forge only provides the
 * {@code forge:} tags and fabric only provides the {@code c:} ones.
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
