package com.iafenvoy.iceandfire.registry.tag;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class IafBlockTags {
    public static final TagKey<Block> DRAGON_ENVIRONMENT_BLOCKS = createKey("dragon_environment_blocks");

    public static final TagKey<Block> DRAGON_CAVE_RARE_ORES = createKey("dragon_cave_rare_ores");
    public static final TagKey<Block> DRAGON_CAVE_UNCOMMON_ORES = createKey("dragon_cave_uncommon_ores");
    public static final TagKey<Block> DRAGON_CAVE_COMMON_ORES = createKey("dragon_cave_common_ores");

    public static final TagKey<Block> FIRE_DRAGON_CAVE_ORES = createKey("fire_dragon_cave_ores");
    public static final TagKey<Block> ICE_DRAGON_CAVE_ORES = createKey("ice_dragon_cave_ores");
    public static final TagKey<Block> LIGHTNING_DRAGON_CAVE_ORES = createKey("lightning_dragon_cave_ores");

    public static final TagKey<Block> DRAGON_BLOCK_BREAK_BLACKLIST = createKey("dragon_block_break_blacklist");
    public static final TagKey<Block> DRAGON_BLOCK_BREAK_NO_DROPS = createKey("dragon_block_break_no_drops");

    private static TagKey<Block> createKey(final String name) {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of(IceAndFire.MOD_ID, name));
    }
}

