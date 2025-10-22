package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.block.entity.DragonForgeBlockEntity;
import com.iafenvoy.iceandfire.item.block.util.DragonProof;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.util.DragonTypeProvider;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

//FIXME::Introduce a base block class for all dragon forge blocks
public class DragonForgeCoreBlock extends BlockWithEntity implements DragonProof, DragonTypeProvider {
    private static final Map<DragonType, Block> ACTIVATED_MAP = new HashMap<>();
    private final DragonType dragonType;

    public DragonForgeCoreBlock(DragonType dragonType, boolean activated) {
        super(Settings.create().mapColor(MapColor.IRON_GRAY).dynamicBounds().strength(40, 500).sounds(BlockSoundGroup.METAL).luminance((state) -> activated ? 15 : 0));
        this.dragonType = dragonType;
        if (activated) ACTIVATED_MAP.put(dragonType, this);
    }

    public static String name(DragonType dragonType, boolean activated) {
        return "dragonforge_%s_core%s".formatted(dragonType.name(), activated ? "" : "_disabled");
    }

    public static void setState(DragonType dragonType, World worldIn, BlockPos pos) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        worldIn.setBlockState(pos, ACTIVATED_MAP.getOrDefault(dragonType, IafBlocks.DRAGONFORGE_FIRE_CORE.get()).getDefaultState(), 3);
        if (blockEntity != null) {
            blockEntity.cancelRemoval();
            worldIn.addBlockEntity(blockEntity);
        }
    }

    @Override
    public DragonType getDragonType() {
        return this.dragonType;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.isSneaking()) {
            if (player instanceof ServerPlayerEntity serverPlayer && world.getBlockEntity(pos) instanceof DragonForgeBlockEntity forge)
                MenuRegistry.openExtendedMenu(serverPlayer, forge);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DragonForgeBlockEntity) {
            ItemScatterer.spawn(world, pos, (DragonForgeBlockEntity) blockEntity);
            world.updateComparators(pos, this);
            world.removeBlockEntity(pos);
        }
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> entityType) {
        return validateTicker(entityType, IafBlockEntities.DRAGONFORGE_CORE.get(), DragonForgeBlockEntity::tick);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DragonForgeBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return MapCodec.unit(this);
    }
}
