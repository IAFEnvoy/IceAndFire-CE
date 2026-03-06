package com.iafenvoy.iceandfire.fabric;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.fabric.compat.trinkets.TrinketsRegistry;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.integration.IntegrationExecutor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.potion.Potions;

public final class IceAndFireFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        IceAndFire.init();
        IceAndFire.process();
        IafAttachments.init();
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> builder.registerPotionRecipe(Potions.WATER, IafItems.SHINY_SCALES.get(), Potions.WATER_BREATHING));
        IntegrationExecutor.runWhenLoad("trinkets", () -> TrinketsRegistry::registerItems);
    }
}
