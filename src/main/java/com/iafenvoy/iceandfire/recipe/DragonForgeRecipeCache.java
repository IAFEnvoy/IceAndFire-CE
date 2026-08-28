package com.iafenvoy.iceandfire.recipe;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.List;

/**
 * Client copy of dragon-forge recipes delivered by
 * {@link net.neoforged.neoforge.event.OnDatapackSyncEvent#sendRecipes}.
 */
@EventBusSubscriber(Dist.CLIENT)
public final class DragonForgeRecipeCache {
    private static volatile List<DragonForgeRecipe> RECIPES = List.of();

    private DragonForgeRecipeCache() {
    }

    public static List<DragonForgeRecipe> get() {
        return RECIPES;
    }

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        RECIPES = event.getRecipeMap().byType(IafRecipes.DRAGON_FORGE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        IceAndFire.LOGGER.info("Received {} dragon forge recipes from NeoForge sync", RECIPES.size());
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        RECIPES = List.of();
    }
}
