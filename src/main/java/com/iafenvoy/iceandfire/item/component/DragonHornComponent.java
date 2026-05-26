package com.iafenvoy.iceandfire.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record DragonHornComponent(ResourceLocation entityType, UUID entityUuid, CompoundTag entityData) {
    public static final Codec<DragonHornComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.optionalFieldOf("entityType", ResourceLocation.withDefaultNamespace("empty")).forGetter(DragonHornComponent::entityType),
            UUIDUtil.AUTHLIB_CODEC.optionalFieldOf("entityUuid", new UUID(0, 0)).forGetter(DragonHornComponent::entityUuid),
            CompoundTag.CODEC.optionalFieldOf("entityData", new CompoundTag()).forGetter(DragonHornComponent::entityData)
    ).apply(i, DragonHornComponent::new));
}
