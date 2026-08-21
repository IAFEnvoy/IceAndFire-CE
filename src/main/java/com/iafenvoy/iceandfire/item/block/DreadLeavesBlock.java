package com.iafenvoy.iceandfire.item.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.NonNull;

public class DreadLeavesBlock extends LeavesBlock {
    public static final MapCodec<DreadLeavesBlock> CODEC = simpleCodec(DreadLeavesBlock::new);

    public DreadLeavesBlock(BlockBehaviour.Properties properties) {
        super(0.0F, properties);
    }

    @Override
    public @NonNull MapCodec<DreadLeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected void spawnFallingLeavesParticle(@NonNull Level level, @NonNull BlockPos pos, @NonNull RandomSource random) {
    }
}
