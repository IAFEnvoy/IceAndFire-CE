package com.iafenvoy.iceandfire.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record StructureGenerationConfig(float generateChance) {
    public static final StructureGenerationConfig DEFAULT = new StructureGenerationConfig(0.5f);

    public static final Codec<StructureGenerationConfig> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    Codec.floatRange(0f, 1f)
                            .optionalFieldOf("generate_chance", 0.5f)
                            .forGetter(StructureGenerationConfig::generateChance)
            ).apply(i, StructureGenerationConfig::new));
}
