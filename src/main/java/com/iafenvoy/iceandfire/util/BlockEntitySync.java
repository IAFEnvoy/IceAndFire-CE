package com.iafenvoy.iceandfire.util;

import net.minecraft.world.level.block.entity.BlockEntity;

public final class BlockEntitySync {
    private BlockEntitySync() { }

    /** Persist the transition and notify only clients tracking this chunk in this dimension. */
    public static void update(BlockEntity entity) {
        entity.setChanged();
        var level = entity.getLevel();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(entity.getBlockPos(), entity.getBlockState(), entity.getBlockState(), 3);
    }
}
