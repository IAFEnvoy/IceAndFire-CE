package com.iafenvoy.iceandfire.data.component;

public class NeedUpdateData {
    private boolean dirty;

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        if (!this.dirty) return false;
        this.dirty = false;
        return true;
    }
}
