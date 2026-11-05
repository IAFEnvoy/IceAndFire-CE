package com.iafenvoy.iceandfire.entity.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;

public final class BlockEntitySync {
    private BlockEntitySync() {}

    public static void changed(BlockEntity entity) {
        entity.markDirty();
        if (entity.getWorld() instanceof ServerWorld world && !entity.isRemoved())
            world.getChunkManager().markForUpdate(entity.getPos());
    }
}
