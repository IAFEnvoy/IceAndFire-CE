package com.iafenvoy.iceandfire.compat;

import net.neoforged.fml.ModList;

public final class IafClientCompat {
    private IafClientCompat() {
    }

    public static boolean isSodiumLoaded() {
        return ModList.get().isLoaded("sodium") || ModList.get().isLoaded("embeddium");
    }
}
