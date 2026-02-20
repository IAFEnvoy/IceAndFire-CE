package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.registry.IafSounds;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class PileBlock extends Block {
    public static final IntProperty LAYERS = IntProperty.of("layers", 1, 8);
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{VoxelShapes.empty(), Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    public PileBlock() {
        super(Settings.create().mapColor(MapColor.DIRT_BROWN).strength(0.3F, 1).ticksRandomly().sounds(new BlockSoundGroup(1.0F, 1.0F, IafSounds.GOLD_PILE_BREAK.get(), IafSounds.GOLD_PILE_STEP.get(), IafSounds.GOLD_PILE_BREAK.get(), IafSounds.GOLD_PILE_STEP.get(), IafSounds.GOLD_PILE_STEP.get())).pistonBehavior(PistonBehavior.DESTROY));
        this.setDefaultState(this.stateManager.getDefaultState().with(LAYERS, 1));
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return type == NavigationType.LAND && state.get(LAYERS) < 5;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(LAYERS)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(LAYERS) - 1];
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos.down());
        Block block = blockstate.getBlock();
        if (block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER) {
            if (block != Blocks.HONEY_BLOCK && block != Blocks.SOUL_SAND)
                return Block.isFaceFullSquare(blockstate.getCollisionShape(worldIn, pos.down()), Direction.UP) || block instanceof PileBlock && blockstate.get(LAYERS) == 8;
            else return true;
        } else return false;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos) {
        return !stateIn.canPlaceAt(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState blockstate = context.getWorld().getBlockState(context.getBlockPos());
        if (blockstate.getBlock() == this) {
            int i = blockstate.get(LAYERS);
            return blockstate.with(LAYERS, Math.min(8, i + 1));
        } else return super.getPlacementState(context);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack item = player.getInventory().getMainHandStack();

        if (!item.isEmpty() && item.getItem() != null && item.getItem() == this.asItem() && state.get(LAYERS) < 8) {
            world.setBlockState(pos, state.with(LAYERS, state.get(LAYERS) + 1), 3);
            if (!player.isCreative()) {
                item.decrement(1);
                PlayerInventory inventory = player.getInventory();
                if (item.isEmpty())
                    inventory.setStack(inventory.selectedSlot, ItemStack.EMPTY);
                else
                    inventory.setStack(inventory.selectedSlot, item);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}