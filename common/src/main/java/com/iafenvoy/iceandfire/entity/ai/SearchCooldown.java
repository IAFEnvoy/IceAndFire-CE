package com.iafenvoy.iceandfire.entity.ai;

/** Deadlines remain reachable even when a goal selector only runs every other tick. */
public final class SearchCooldown {
    private long nextSearch = Long.MIN_VALUE;
    private long lastTime = Long.MIN_VALUE;

    public boolean shouldSearch(long now, int entityId) {
        if (this.nextSearch == Long.MIN_VALUE || now < this.lastTime)
            this.nextSearch = now + Math.floorMod(entityId, 4);
        this.lastTime = now;
        if (now < this.nextSearch) return false;
        this.nextSearch = now + 4;
        return true;
    }
}
