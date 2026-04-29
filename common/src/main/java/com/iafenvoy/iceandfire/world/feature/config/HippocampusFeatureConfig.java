package com.iafenvoy.iceandfire.world.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public record HippocampusFeatureConfig(
        float spawnChance,
        int maxCount,
        int yScatter,
        int variantCount
) implements FeatureConfig {
    public static final Codec<HippocampusFeatureConfig> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    Codec.floatRange(0f, 1f).optionalFieldOf("spawn_chance", 1f / 40f).forGetter(HippocampusFeatureConfig::spawnChance),
                    Codec.intRange(0, 20).optionalFieldOf("max_count", 5).forGetter(HippocampusFeatureConfig::maxCount),
                    Codec.intRange(0, 64).optionalFieldOf("y_scatter", 30).forGetter(HippocampusFeatureConfig::yScatter),
                    Codec.intRange(1, 16).optionalFieldOf("variant_count", 6).forGetter(HippocampusFeatureConfig::variantCount)
            ).apply(i, HippocampusFeatureConfig::new));
}
