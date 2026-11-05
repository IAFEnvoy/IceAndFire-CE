package com.iafenvoy.iceandfire.util;

/** Spaces searches by elapsed world time, independently of the mob's AI scheduling phase. */
public final class SearchCooldown {
    private final int interval;
    private boolean initialized;
    private long lastSearch;

    public SearchCooldown(int interval) {
        if (interval < 1) throw new IllegalArgumentException("interval must be positive");
        this.interval = interval;
    }

    public boolean isReady(long gameTime) {
        if (this.initialized && gameTime >= this.lastSearch && gameTime - this.lastSearch < this.interval)
            return false;
        this.initialized = true;
        this.lastSearch = gameTime;
        return true;
    }
}
