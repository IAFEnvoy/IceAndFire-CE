package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.registry.IafBlocks;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockElementalFlower extends PlantBlock {

    public BlockElementalFlower() {
        super(Settings.create().mapColor(MapColor.DARK_GREEN).replaceable().burnable().pistonBehavior(PistonBehavior.DESTROY).nonOpaque().noCollision().dynamicBounds().ticksRandomly().sounds(BlockSoundGroup.GRASS));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
    }

    @Override
    public boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
        return state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.DIRT) || state.isOf(Blocks.COARSE_DIRT) || state.isOf(Blocks.PODZOL) || state.isOf(Blocks.FARMLAND) || state.isIn(BlockTags.SAND) || this.canStay(state);
    }

    public boolean canStay(BlockState state) {
        if (this == IafBlocks.FIRE_LILY.get())
            return state.isIn(BlockTags.SAND) || state.isOf(Blocks.NETHERRACK);
        else if (this == IafBlocks.LIGHTNING_LILY.get())
            return state.isIn(BlockTags.DIRT) || state.isOf(Blocks.GRASS);
        else
            return state.isIn(BlockTags.ICE) || state.isIn(BlockTags.SNOW) || state.isIn(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON);
    }
}
