package com.iafenvoy.iceandfire.recipe;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.List;

/**
 * 26.1 clients no longer receive a full {@link RecipeManager}. JEI has to read
 * dragon-forge recipes from the integrated server (same JVM) instead.
 */
@EventBusSubscriber
public final class DragonForgeRecipeCache {
    private static volatile List<DragonForgeRecipe> RECIPES = List.of();

    private DragonForgeRecipeCache() {
    }

    public static List<DragonForgeRecipe> get() {
        return RECIPES;
    }

    public static void refresh(RecipeManager manager) {
        RECIPES = manager.recipeMap().byType(IafRecipes.DRAGON_FORGE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        IceAndFire.LOGGER.info("Cached {} dragon forge recipes for JEI", RECIPES.size());
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        refresh(event.getServer().getRecipeManager());
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        MinecraftServer server = event.getPlayerList().getServer();
        refresh(server.getRecipeManager());
    }
}
