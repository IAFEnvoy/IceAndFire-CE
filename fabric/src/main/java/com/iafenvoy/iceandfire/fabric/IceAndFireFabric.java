package com.iafenvoy.iceandfire.fabric;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.fabric.compat.trinkets.TrinketsRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class IceAndFireFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        IceAndFire.init();
        IceAndFire.process();
        if (FabricLoader.getInstance().isModLoaded("trinkets")) TrinketsRegistry.registerItems();
    }
}
