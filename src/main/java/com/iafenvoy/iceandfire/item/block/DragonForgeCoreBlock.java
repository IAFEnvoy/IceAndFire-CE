package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.block.entity.DragonForgeBlockEntity;
import com.iafenvoy.iceandfire.item.block.util.DragonProof;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.util.DragonTypeProvider;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//FIXME::Introduce a base block class for all dragon forge blocks
public class DragonForgeCoreBlock extends BaseEntityBlock implements DragonProof, DragonTypeProvider {
    private static final Map<DragonType, Block> ACTIVATED_MAP = new HashMap<>();
    private static final Map<DragonType, Block> DISABLED_MAP = new HashMap<>();
    private final DragonType dragonType;

    public DragonForgeCoreBlock(DragonType dragonType, boolean activated) {
        super(Properties.of().mapColor(MapColor.METAL).dynamicShape().strength(40, 500).sound(SoundType.METAL).lightLevel((state) -> activated ? 15 : 0));
        this.dragonType = dragonType;
        if (activated) ACTIVATED_MAP.put(dragonType, this);
        else DISABLED_MAP.put(dragonType, this);
    }

    public static String name(DragonType dragonType, boolean activated) {
        return String.format(Locale.ROOT, "dragonforge_%s_core%s", dragonType.name(), activated ? "" : "_disabled");
    }

    public static void setState(DragonType dragonType, Level worldIn, BlockPos pos) {
        setState(dragonType, worldIn, pos, true);
    }

    public static void setState(DragonType dragonType, Level worldIn, BlockPos pos, boolean assembled) {
        Block target = (assembled ? ACTIVATED_MAP : DISABLED_MAP).get(dragonType);
        if (target == null || worldIn.getBlockState(pos).is(target)) return;
        // Both states are valid for the same BlockEntityType. LevelChunk retains the entity,
        // updates its block state and rebinds its ticker after onRemove preserves the inventory.
        worldIn.setBlock(pos, target.defaultBlockState(), 3);
    }

    @Override
    public DragonType getDragonType() {
        return this.dragonType;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player, @NotNull BlockHitResult hit) {
        if (!player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer serverPlayer && world.getBlockEntity(pos) instanceof DragonForgeBlockEntity forge)
                serverPlayer.openMenu(forge);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (newState.getBlock() instanceof DragonForgeCoreBlock core && core.dragonType == this.dragonType) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!world.isClientSide && blockEntity instanceof DragonForgeBlockEntity) {
            Containers.dropContents(world, pos, (DragonForgeBlockEntity) blockEntity);
            world.updateNeighbourForOutputSignal(pos, this);
            world.removeBlockEntity(pos);
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, Level world, @NotNull BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> entityType) {
        return createTickerHelper(entityType, IafBlockEntities.DRAGONFORGE_CORE.get(), DragonForgeBlockEntity::tick);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new DragonForgeBlockEntity(pos, state);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(this);
    }
}
