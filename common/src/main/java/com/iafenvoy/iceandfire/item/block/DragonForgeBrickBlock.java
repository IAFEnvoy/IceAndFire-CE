package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.block.entity.DragonForgeBlockEntity;
import com.iafenvoy.iceandfire.item.block.entity.DragonForgeBrickBlockEntity;
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
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

//FIXME::Introduce a base block class for all dragon forge blocks
public class DragonForgeBrickBlock extends BlockWithEntity implements DragonProof, DragonTypeProvider {
    private static final Map<DragonType, Block> TYPE_MAP = new HashMap<>();
    public static final BooleanProperty GRILL = BooleanProperty.of("grill");
    private final DragonType dragonType;

    public DragonForgeBrickBlock(DragonType dragonType) {
        super(Settings.create().mapColor(MapColor.STONE_GRAY).instrument(NoteBlockInstrument.BASEDRUM).dynamicBounds().strength(40, 500).sounds(BlockSoundGroup.METAL));
        this.dragonType = dragonType;
        this.setDefaultState(this.getStateManager().getDefaultState().with(GRILL, Boolean.FALSE));
        TYPE_MAP.put(dragonType, this);
    }

    public static String name(DragonType dragonType) {
        return "dragonforge_%s_brick".formatted(dragonType.name());
    }

    public static Block getBlockByType(DragonType type) {
        return TYPE_MAP.getOrDefault(type, IafBlocks.DRAGONFORGE_FIRE_BRICK.get());
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        DragonForgeBlockEntity forge = this.getConnectedBlockEntity(world, pos);
        if (forge != null && forge.getDragonType() == this.dragonType && player instanceof ServerPlayerEntity serverPlayer) {
            MenuRegistry.openExtendedMenu(serverPlayer, forge);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    private DragonForgeBlockEntity getConnectedBlockEntity(World worldIn, BlockPos pos) {
        for (Direction facing : Direction.values())
            if (worldIn.getBlockEntity(pos.offset(facing)) != null && worldIn.getBlockEntity(pos.offset(facing)) instanceof DragonForgeBlockEntity forge)
                if (forge.assembled())
                    return forge;
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(GRILL);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return MapCodec.unit(this);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DragonForgeBrickBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, IafBlockEntities.DRAGONFORGE_BRICK.get(), DragonForgeBrickBlockEntity::tick);
    }

    @Override
    public DragonType getDragonType() {
        return this.dragonType;
    }
}
