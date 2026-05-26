package com.iafenvoy.iceandfire.util.attachment;

import net.minecraft.world.entity.Entity;

public class NeedUpdateData<T extends Entity> implements IafEntityAttachment<T> {
    private boolean dirty;

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public boolean isDirty() {
        if (!this.dirty) return false;
        this.dirty = false;
        return true;
    }
}
