package com.iafenvoy.iceandfire.world.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.world.gen.feature.FeatureConfig;

public record DeathWormFeatureConfig(float spawnChance, EntityType<?> entityType) implements FeatureConfig {
    public static final Codec<DeathWormFeatureConfig> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    Codec.floatRange(0f, 1f)
                            .optionalFieldOf("spawn_chance", 1f / 30f)
                            .forGetter(DeathWormFeatureConfig::spawnChance),
                    Registries.ENTITY_TYPE.getCodec()
                            .fieldOf("entity_type")
                            .forGetter(DeathWormFeatureConfig::entityType)
            ).apply(i, DeathWormFeatureConfig::new));
}
