package com.iafenvoy.iceandfire.util.attachment;

import net.minecraft.world.entity.Entity;

public interface IafEntityAttachment<T extends Entity> {
    default void tick(T entity) {
    }

    default boolean isDirty() {
        return false;
    }

    default void markDirty() {
    }
}
