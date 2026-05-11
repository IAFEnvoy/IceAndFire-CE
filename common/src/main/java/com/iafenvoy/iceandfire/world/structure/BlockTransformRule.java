package com.iafenvoy.iceandfire.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

public record BlockTransformRule(Block from, Block to) {
    public static final Codec<BlockTransformRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Registries.BLOCK.getCodec().fieldOf("from").forGetter(BlockTransformRule::from),
                    Registries.BLOCK.getCodec().fieldOf("to").forGetter(BlockTransformRule::to)
            ).apply(instance, BlockTransformRule::new));
}
