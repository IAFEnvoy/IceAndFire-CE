package com.iafenvoy.iceandfire.recipe;

import com.iafenvoy.iceandfire.registry.IafRecipes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

/**
 * Asks NeoForge to send {@code iceandfire:dragonforge} recipes to the client.
 * The client stores them in {@link DragonForgeRecipeCache}.
 */
@EventBusSubscriber
public final class DragonForgeRecipeSync {
    private DragonForgeRecipeSync() {
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(IafRecipes.DRAGON_FORGE_TYPE.get());
    }
}
