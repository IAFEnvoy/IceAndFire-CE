package com.iafenvoy.iceandfire.item.component;

import com.iafenvoy.iceandfire.data.BestiaryPage;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record BestiaryPageComponent(List<BestiaryPage> pages) {
    public static final Codec<BestiaryPageComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            IafRegistries.BESTIARY_PAGE.byNameCodec().listOf().optionalFieldOf("pages", List.of()).forGetter(BestiaryPageComponent::pages)
    ).apply(i, BestiaryPageComponent::new));
}
