package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DreadPortalBlockEntity extends BlockEntity {
    public DreadPortalBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.DREAD_PORTAL.get(), pos, state);
    }
}
