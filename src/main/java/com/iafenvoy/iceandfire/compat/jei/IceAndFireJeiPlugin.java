package com.iafenvoy.iceandfire.compat.jei;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipeCache;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import com.iafenvoy.iceandfire.screen.gui.bestiary.BestiaryScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//By jdkdigital
@JeiPlugin
public class IceAndFireJeiPlugin implements IModPlugin {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, IceAndFire.MOD_ID);

    public static final IRecipeType<DragonForgeRecipe> FIRE = IRecipeType.create(IceAndFire.MOD_ID, "firedragonforge", DragonForgeRecipe.class);
    public static final IRecipeType<DragonForgeRecipe> ICE = IRecipeType.create(IceAndFire.MOD_ID, "icedragonforge", DragonForgeRecipe.class);
    public static final IRecipeType<DragonForgeRecipe> LIGHTNING = IRecipeType.create(IceAndFire.MOD_ID, "lightningdragonforge", DragonForgeRecipe.class);

    @Override
    public @NotNull Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new FireDragonForgeRecipeCategory(guiHelper));
        registration.addRecipeCategories(new IceDragonForgeRecipeCategory(guiHelper));
        registration.addRecipeCategories(new LightningDragonForgeRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(FIRE, IafBlocks.DRAGONFORGE_FIRE_CORE.get());
        registration.addCraftingStation(ICE, IafBlocks.DRAGONFORGE_ICE_CORE.get());
        registration.addCraftingStation(LIGHTNING, IafBlocks.DRAGONFORGE_LIGHTNING_CORE.get());
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        List<DragonForgeRecipe> recipes = new ArrayList<>(DragonForgeRecipeCache.get());
        if (recipes.isEmpty()) {
            RecipeManager recipeManager = serverRecipeManager();
            if (recipeManager != null) {
                for (RecipeHolder<DragonForgeRecipe> holder : recipeManager.recipeMap().byType(IafRecipes.DRAGON_FORGE_TYPE.get())) {
                    recipes.add(holder.value());
                }
            }
        }
        if (recipes.isEmpty()) {
            IceAndFire.LOGGER.warn("Skipping Dragon Forge JEI recipes because no server recipes are available");
            return;
        }

        List<DragonForgeRecipe> fireRecipes = new ArrayList<>();
        List<DragonForgeRecipe> iceRecipes = new ArrayList<>();
        List<DragonForgeRecipe> lightningRecipes = new ArrayList<>();

        for (DragonForgeRecipe recipe : recipes) {
            switch (recipe.getDragonType()) {
                case "fire" -> fireRecipes.add(recipe);
                case "ice" -> iceRecipes.add(recipe);
                case "lightning" -> lightningRecipes.add(recipe);
            }
        }

        IceAndFire.LOGGER.info("Registering Dragon Forge JEI recipes: fire={}, ice={}, lightning={}", fireRecipes.size(), iceRecipes.size(), lightningRecipes.size());
        registration.addRecipes(FIRE, fireRecipes);
        registration.addRecipes(ICE, iceRecipes);
        registration.addRecipes(LIGHTNING, lightningRecipes);
    }

    /**
     * 26.1 keeps full recipes on the server. {@link net.minecraft.client.multiplayer.ClientLevel#recipeAccess()}
     * is only a display container, so reading it never finds {@code iceandfire:dragonforge}.
     */
    private static @Nullable RecipeManager serverRecipeManager() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            Minecraft minecraft = Minecraft.getInstance();
            server = minecraft.getSingleplayerServer();
        }
        return server != null ? server.getRecipeManager() : null;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(BestiaryScreen.class, screen -> new IGuiProperties() {
            @Override
            public @NotNull Class<? extends Screen> screenClass() {
                return BestiaryScreen.class;
            }

            @Override
            public int guiLeft() {
                return 0;
            }

            @Override
            public int guiTop() {
                return 0;
            }

            @Override
            public int guiXSize() {
                return screen.width;
            }

            @Override
            public int guiYSize() {
                return screen.height;
            }

            @Override
            public int screenWidth() {
                return screen.width;
            }

            @Override
            public int screenHeight() {
                return screen.height;
            }
        });
    }
}
