package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockElementalFlower extends PlantBlock {
    private static final MapCodec<? extends PlantBlock> CODEC = createCodec(s -> new BlockElementalFlower());

    public BlockElementalFlower() {
        super(Settings.create().mapColor(MapColor.DARK_GREEN).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offset(OffsetType.XZ).pistonBehavior(PistonBehavior.DESTROY));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
    }

    @Override
    protected MapCodec<? extends PlantBlock> getCodec() {
        return CODEC;
    }

    @Override
    public boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
        return state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.DIRT) || state.isOf(Blocks.COARSE_DIRT) || state.isOf(Blocks.PODZOL) || state.isOf(Blocks.FARMLAND) || state.isIn(BlockTags.SAND) || this.canStay(world, pos);
    }

    public boolean canStay(BlockView world, BlockPos pos) {
        BlockState soil = world.getBlockState(pos.down());
        if (this == IafBlocks.FIRE_LILY.get())
            return soil.isIn(BlockTags.SAND) || soil.isOf(Blocks.NETHERRACK);
        else if (this == IafBlocks.LIGHTNING_LILY.get())
            return soil.isIn(BlockTags.DIRT) || soil.isIn(IafBlockTags.GRASSES);
        else
            return soil.isIn(BlockTags.ICE) || soil.isIn(BlockTags.SNOW) || soil.isIn(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON);
    }
}
