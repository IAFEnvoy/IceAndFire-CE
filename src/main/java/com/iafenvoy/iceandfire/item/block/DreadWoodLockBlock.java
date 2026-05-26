package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.item.block.util.DragonProof;
import com.iafenvoy.iceandfire.item.block.util.DreadBlock;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class DreadWoodLockBlock extends Block implements DragonProof, DreadBlock {
    public static final BooleanProperty PLAYER_PLACED = BooleanProperty.create("player_placed");

    public DreadWoodLockBlock() {
        super(Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(-1.0F, 1000000F).sound(SoundType.WOOD));
        this.registerDefaultState(this.getStateDefinition().any().setValue(PLAYER_PLACED, Boolean.FALSE));
    }

    @Override
    public float getDestroyProgress(BlockState state, @NotNull Player player, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        if (state.getValue(PLAYER_PLACED)) {
            float f = 8f;
            //Code from super method
            return player.getDestroySpeed(state) / f / (float) 30;
        }
        return super.getDestroyProgress(state, player, worldIn, pos);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, @NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (stack.is(IafItems.DREAD_KEY.get())) {
            if (!player.isCreative())
                stack.shrink(1);
            this.deleteNearbyWood(world, pos, pos);
            world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.BLOCKS, 1, 1, false);
            world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1, 2, false);
        }
        return ItemInteractionResult.SUCCESS;
    }

    private void deleteNearbyWood(Level world, BlockPos pos, BlockPos startPos) {
        if (pos.distSqr(startPos) < 32)
            if (world.getBlockState(pos).is(IafBlocks.DREADWOOD_PLANKS.get()) || world.getBlockState(pos).is(IafBlocks.DREADWOOD_PLANKS_LOCK.get())) {
                world.destroyBlock(pos, false);
                for (Direction facing : Direction.values())
                    this.deleteNearbyWood(world, pos.relative(facing), startPos);
            }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PLAYER_PLACED);
    }
}
