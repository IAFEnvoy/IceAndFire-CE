package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.entity.block.BlockEntityEggInIce;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEggInIce extends BlockWithEntity {
    public BlockEggInIce() {
        super(Settings.create().mapColor(MapColor.PALE_PURPLE).nonOpaque().dynamicBounds().strength(0.5F).dynamicBounds().sounds(BlockSoundGroup.GLASS));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityEggInIce(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> entityType) {
        return checkType(entityType, IafBlockEntities.EGG_IN_ICE.get(), BlockEntityEggInIce::tickEgg);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity te, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) != null)
            if (world.getBlockEntity(pos) instanceof BlockEntityEggInIce tile)
                tile.spawnEgg();
    }
}
