package com.iafenvoy.iceandfire.world.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public record WanderingCyclopsFeatureConfig(float spawnChance, int sheepMin, int sheepMax) implements FeatureConfig {
    public WanderingCyclopsFeatureConfig {
        if (sheepMin > sheepMax)
            throw new IllegalArgumentException("sheep_min (" + sheepMin + ") must not be greater than sheep_max (" + sheepMax + ")");
    }

    public static final Codec<WanderingCyclopsFeatureConfig> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    Codec.floatRange(0f, 1f).optionalFieldOf("spawn_chance", 1f / 900f).forGetter(WanderingCyclopsFeatureConfig::spawnChance),
                    Codec.intRange(0, 16).optionalFieldOf("sheep_min", 3).forGetter(WanderingCyclopsFeatureConfig::sheepMin),
                    Codec.intRange(0, 16).optionalFieldOf("sheep_max", 5).forGetter(WanderingCyclopsFeatureConfig::sheepMax)
            ).apply(i, WanderingCyclopsFeatureConfig::new));
}
