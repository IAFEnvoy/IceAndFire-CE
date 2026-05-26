package com.iafenvoy.iceandfire.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DragonSkullComponent(int stage, int dragonAge) {
    public static final Codec<DragonSkullComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("stage").forGetter(DragonSkullComponent::stage),
            Codec.INT.fieldOf("dragonAge").forGetter(DragonSkullComponent::dragonAge)
    ).apply(i, DragonSkullComponent::new));
}
