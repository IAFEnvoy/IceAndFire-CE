package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.block.BlockEntityDragonForge;
import com.iafenvoy.iceandfire.entity.block.BlockEntityDragonForgeInput;
import com.iafenvoy.iceandfire.item.block.util.IDragonProof;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Locale;

public class BlockDragonForgeInput extends BlockWithEntity implements IDragonProof {
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
    private final int dragonType;

    public BlockDragonForgeInput(int dragonType) {
        super(Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).dynamicBounds().strength(40, 500).sounds(BlockSoundGroup.METAL));
        this.dragonType = dragonType;
        this.setDefaultState(this.getStateManager().getDefaultState().with(ACTIVE, Boolean.FALSE));
    }

    public static String name(int dragonType) {
        return String.format(Locale.ROOT, "dragonforge_%s_input", DragonType.getNameFromInt(dragonType));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (this.getConnectedTileEntity(world, hitResult.getBlockPos()) != null) {
            BlockEntityDragonForge forge = this.getConnectedTileEntity(world, hitResult.getBlockPos());
            if (forge != null && forge.getPropertyDelegate().fireType == this.dragonType) {
                if (!world.isClient) {
                    NamedScreenHandlerFactory inamedcontainerprovider = this.createScreenHandlerFactory(forge.getCachedState(), world, forge.getPos());
                    if (inamedcontainerprovider != null) {
                        player.openHandledScreen(inamedcontainerprovider);
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.SUCCESS;
    }

    private BlockEntityDragonForge getConnectedTileEntity(World world, BlockPos pos) {
        for (Direction facing : Direction.values())
            if (world.getBlockEntity(pos.offset(facing)) != null && world.getBlockEntity(pos.offset(facing)) instanceof BlockEntityDragonForge)
                return (BlockEntityDragonForge) world.getBlockEntity(pos.offset(facing));
        return null;
    }

    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().with(ACTIVE, meta > 0);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public int getMetaFromState(BlockState state) {
        return state.get(ACTIVE) ? 1 : 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> entityType) {
        return world.isClient ? null : checkType(entityType, IafBlockEntities.DRAGONFORGE_INPUT.get(), BlockEntityDragonForgeInput::tick);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityDragonForgeInput(pos, state);
    }
}
