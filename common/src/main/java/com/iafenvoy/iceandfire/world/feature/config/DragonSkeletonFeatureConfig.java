package com.iafenvoy.iceandfire.world.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.world.gen.feature.FeatureConfig;

public record DragonSkeletonFeatureConfig(
        float spawnChance,
        boolean enabled,
        EntityType<?> entityType,
        int ageMin,
        int ageMax
) implements FeatureConfig {
    public static final Codec<DragonSkeletonFeatureConfig> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    Codec.floatRange(0f, 1f).optionalFieldOf("spawn_chance", 1f / 300f).forGetter(DragonSkeletonFeatureConfig::spawnChance),
                    Codec.BOOL.optionalFieldOf("enabled", true).forGetter(DragonSkeletonFeatureConfig::enabled),
                    Registries.ENTITY_TYPE.getCodec().fieldOf("entity_type").forGetter(DragonSkeletonFeatureConfig::entityType),
                    Codec.intRange(1, 500).optionalFieldOf("age_min", 10).forGetter(DragonSkeletonFeatureConfig::ageMin),
                    Codec.intRange(1, 500).optionalFieldOf("age_max", 110).forGetter(DragonSkeletonFeatureConfig::ageMax)
            ).apply(i, DragonSkeletonFeatureConfig::new));
}
