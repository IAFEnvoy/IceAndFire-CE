package com.iafenvoy.iceandfire.item.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class FallingGenericBlock extends FallingBlock {
    private static final MapCodec<? extends FallingBlock> CODEC = simpleCodec(FallingGenericBlock::new);

    public FallingGenericBlock(Properties props) {
        super(props);
    }

    @Override
    protected @NotNull MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    @Override
    public int getDustColor(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos) {
        return state.getMapColor(level, pos).col;
    }

    public static FallingGenericBlock builder(float hardness, float resistance, SoundType sound, MapColor color, NoteBlockInstrument instrument) {
        Properties props = Properties.of().mapColor(color).instrument(instrument).sound(sound).strength(hardness, resistance);
        return new FallingGenericBlock(props);
    }
}
