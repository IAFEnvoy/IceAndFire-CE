package com.iafenvoy.iceandfire.fabric;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.fabric.compat.trinkets.TrinketsRegistry;
import com.iafenvoy.iceandfire.registry.IafTrades;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.world.poi.PointOfInterestTypes;

public final class IceAndFireFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        IceAndFire.init();
        IceAndFire.process();
        //Special Patch
        for (BlockState state : IafTrades.SCRIBE_WORKSTATION.apply(IafTrades.SCRIBE_BLOCK.get()))
            PointOfInterestTypes.POI_STATES_TO_TYPE.put(state, Registries.POINT_OF_INTEREST_TYPE.getEntry(IafTrades.SCRIBE_POI.get()));
        if (FabricLoader.getInstance().isModLoaded("trinkets")) TrinketsRegistry.registerItems();
    }
}
