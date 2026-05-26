package com.iafenvoy.iceandfire.registry.tag;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class IafBlockTags {
    public static final TagKey<Block> DRAGON_ENVIRONMENT_BLOCKS = create("dragon_environment_blocks");

    public static final TagKey<Block> DRAGON_CAVE_RARE_ORES = create("dragon_cave_rare_ores");
    public static final TagKey<Block> DRAGON_CAVE_UNCOMMON_ORES = create("dragon_cave_uncommon_ores");
    public static final TagKey<Block> DRAGON_CAVE_COMMON_ORES = create("dragon_cave_common_ores");

    public static final TagKey<Block> FIRE_DRAGON_CAVE_ORES = create("fire_dragon_cave_ores");
    public static final TagKey<Block> ICE_DRAGON_CAVE_ORES = create("ice_dragon_cave_ores");
    public static final TagKey<Block> LIGHTNING_DRAGON_CAVE_ORES = create("lightning_dragon_cave_ores");

    public static final TagKey<Block> DRAGON_BLOCK_BREAK_BLACKLIST = create("dragon_block_break_blacklist");
    public static final TagKey<Block> DRAGON_BLOCK_BREAK_NO_DROPS = create("dragon_block_break_no_drops");
    public static final TagKey<Block> GRASSES = create("grasses");

    private static TagKey<Block> create(final String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, name));
    }
}

