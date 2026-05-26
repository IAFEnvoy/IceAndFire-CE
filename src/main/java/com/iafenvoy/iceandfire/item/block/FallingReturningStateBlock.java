package com.iafenvoy.iceandfire.item.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class FallingReturningStateBlock extends FallingBlock {
    public static final BooleanProperty REVERTS = BooleanProperty.create("revert");
    private final BlockState returnState;

    public FallingReturningStateBlock(float hardness, float resistance, SoundType sound, MapColor color, BlockState revertState) {
        super(Properties.of().mapColor(color).sound(sound).strength(hardness, resistance).randomTicks());

        this.returnState = revertState;
        this.registerDefaultState(this.stateDefinition.any().setValue(REVERTS, Boolean.FALSE));
    }

    public FallingReturningStateBlock(float hardness, float resistance, SoundType sound, boolean slippery, MapColor color, BlockState revertState) {
        super(Properties.of().mapColor(color).sound(sound).strength(hardness, resistance).randomTicks());

        this.returnState = revertState;
        this.registerDefaultState(this.stateDefinition.any().setValue(REVERTS, Boolean.FALSE));
    }

    @Override
    protected @NotNull MapCodec<? extends FallingBlock> codec() {
        return simpleCodec(s -> this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        super.tick(state, world, pos, rand);
        if (!world.isClientSide) {
            if (!world.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) return;
            if (state.getValue(REVERTS) && rand.nextInt(3) == 0)
                world.setBlockAndUpdate(pos, this.returnState);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(REVERTS);
    }
}
