package com.iafenvoy.iceandfire.registry.tag;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class IafBannerPatternTags {
    public static final TagKey<BannerPattern> FIRE_BANNER_PATTERN = create("pattern_item/fire");
    public static final TagKey<BannerPattern> ICE_BANNER_PATTERN = create("pattern_item/ice");
    public static final TagKey<BannerPattern> LIGHTNING_BANNER_PATTERN = create("pattern_item/lightning");
    public static final TagKey<BannerPattern> FIRE_HEAD_BANNER_PATTERN = create("pattern_item/fire_head");
    public static final TagKey<BannerPattern> ICE_HEAD_BANNER_PATTERN = create("pattern_item/ice_head");
    public static final TagKey<BannerPattern> LIGHTNING_HEAD_BANNER_PATTERN = create("pattern_item/lightning_head");
    public static final TagKey<BannerPattern> NETHER_BANNER_PATTERN = create("pattern_item/nether");
    public static final TagKey<BannerPattern> NETHER_HEAD_BANNER_PATTERN = create("pattern_item/nether_head");
    public static final TagKey<BannerPattern> AMPHITHERE_BANNER_PATTERN = create("pattern_item/amphithere");
    public static final TagKey<BannerPattern> BIRD_BANNER_PATTERN = create("pattern_item/bird");
    public static final TagKey<BannerPattern> EYE_BANNER_PATTERN = create("pattern_item/eye");
    public static final TagKey<BannerPattern> FAE_BANNER_PATTERN = create("pattern_item/fae");
    public static final TagKey<BannerPattern> FEATHER_BANNER_PATTERN = create("pattern_item/feather");
    public static final TagKey<BannerPattern> GORGON_BANNER_PATTERN = create("pattern_item/gorgon");
    public static final TagKey<BannerPattern> HIPPOCAMPUS_BANNER_PATTERN = create("pattern_item/hippocampus");
    public static final TagKey<BannerPattern> HIPPOGRYPH_HEAD_BANNER_PATTERN = create("pattern_item/hippogryph_head");
    public static final TagKey<BannerPattern> MERMAID_BANNER_PATTERN = create("pattern_item/mermaid");
    public static final TagKey<BannerPattern> SEA_SERPENT_BANNER_PATTERN = create("pattern_item/sea_serpent");
    public static final TagKey<BannerPattern> TROLL_BANNER_PATTERN = create("pattern_item/troll");
    public static final TagKey<BannerPattern> WEEZER_BANNER_PATTERN = create("pattern_item/weezer");
    public static final TagKey<BannerPattern> DREAD_BANNER_PATTERN = create("pattern_item/dread");

    private static TagKey<BannerPattern> create(String name) {
        return TagKey.of(RegistryKeys.BANNER_PATTERN, Identifier.of(IceAndFire.MOD_ID, name));
    }
}

