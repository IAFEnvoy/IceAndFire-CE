package com.iafenvoy.iceandfire.world.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public record SeaSerpentFeatureConfig(float spawnChance, double dangerousRadius) implements FeatureConfig {
    public static final Codec<SeaSerpentFeatureConfig> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    Codec.floatRange(0f, 1f).optionalFieldOf("spawn_chance", 1f / 250f).forGetter(SeaSerpentFeatureConfig::spawnChance),
                    Codec.doubleRange(0, 100_000).optionalFieldOf("dangerous_radius", 1000.0).forGetter(SeaSerpentFeatureConfig::dangerousRadius)
            ).apply(i, SeaSerpentFeatureConfig::new));
}
