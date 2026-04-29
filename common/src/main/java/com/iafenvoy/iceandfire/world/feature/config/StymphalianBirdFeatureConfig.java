package com.iafenvoy.iceandfire.world.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public record StymphalianBirdFeatureConfig(float spawnChance, int flockMin, int flockMax) implements FeatureConfig {
    public StymphalianBirdFeatureConfig {
        if (flockMax < flockMin)
            throw new IllegalArgumentException("flock_max (" + flockMax + ") must be >= flock_min (" + flockMin + ")");
    }

    public static final Codec<StymphalianBirdFeatureConfig> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    Codec.floatRange(0f, 1f).optionalFieldOf("spawn_chance", 1f / 80f).forGetter(StymphalianBirdFeatureConfig::spawnChance),
                    Codec.intRange(1, 32).optionalFieldOf("flock_min", 4).forGetter(StymphalianBirdFeatureConfig::flockMin),
                    Codec.intRange(1, 32).optionalFieldOf("flock_max", 7).forGetter(StymphalianBirdFeatureConfig::flockMax)
            ).apply(i, StymphalianBirdFeatureConfig::new));
}
