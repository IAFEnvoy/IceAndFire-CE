package com.iafenvoy.iceandfire.item.block;

import com.hollingsworth.arsnouveau.setup.registry.MenuRegistry;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.block.entity.DragonForgeBlockEntity;
import com.iafenvoy.iceandfire.item.block.util.DragonProof;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafBlocks;
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
    private final DragonType dragonType;

    public DragonForgeCoreBlock(DragonType dragonType, boolean activated) {
        super(Properties.of().mapColor(MapColor.METAL).dynamicShape().strength(40, 500).sound(SoundType.METAL).lightLevel((state) -> activated ? 15 : 0));
        this.dragonType = dragonType;
        if (activated) ACTIVATED_MAP.put(dragonType, this);
    }

    public static String name(DragonType dragonType, boolean activated) {
        return String.format(Locale.ROOT, "dragonforge_%s_core%s", dragonType.name(), activated ? "" : "_disabled");
    }

    public static void setState(DragonType dragonType, Level worldIn, BlockPos pos) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        worldIn.setBlock(pos, ACTIVATED_MAP.getOrDefault(dragonType, IafBlocks.DRAGONFORGE_FIRE_CORE.get()).defaultBlockState(), 3);
        if (blockEntity != null) {
            blockEntity.clearRemoved();
            worldIn.setBlockEntity(blockEntity);
        }
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
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DragonForgeBlockEntity) {
            Containers.dropContents(world, pos, (DragonForgeBlockEntity) blockEntity);
            world.updateNeighbourForOutputSignal(pos, this);
            world.removeBlockEntity(pos);
        }
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
