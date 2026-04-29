package com.iafenvoy.iceandfire.world.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.world.gen.feature.FeatureConfig;

public record EntitySpawnFeatureConfig(float spawnChance, EntityType<?> entityType) implements FeatureConfig {
    public static final Codec<EntitySpawnFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.floatRange(0f, 1f).fieldOf("spawn_chance").forGetter(EntitySpawnFeatureConfig::spawnChance),
                    Registries.ENTITY_TYPE.getCodec().fieldOf("entity_type").forGetter(EntitySpawnFeatureConfig::entityType)
            ).apply(instance, EntitySpawnFeatureConfig::new));
}
