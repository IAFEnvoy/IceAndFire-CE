package com.iafenvoy.iceandfire.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

public record StoneStatusComponent(boolean isPlayer, String entityType, CompoundTag nbt) {
    public static final Codec<StoneStatusComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.fieldOf("isPlayer").forGetter(StoneStatusComponent::isPlayer),
            Codec.STRING.fieldOf("hatchEntityCreator").forGetter(StoneStatusComponent::entityType),
            CompoundTag.CODEC.fieldOf("nbt").forGetter(StoneStatusComponent::nbt)
    ).apply(i, StoneStatusComponent::new));
}
