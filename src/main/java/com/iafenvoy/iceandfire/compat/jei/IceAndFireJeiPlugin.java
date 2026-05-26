package com.iafenvoy.iceandfire.compat.jei;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import com.iafenvoy.iceandfire.screen.gui.bestiary.BestiaryScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//By jdkdigital
@JeiPlugin
public class IceAndFireJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, IceAndFire.MOD_ID);

    public static final RecipeType<DragonForgeRecipe> FIRE = RecipeType.create(ResourceLocation.DEFAULT_NAMESPACE, "firedragonforge", DragonForgeRecipe.class);
    public static final RecipeType<DragonForgeRecipe> ICE = RecipeType.create(ResourceLocation.DEFAULT_NAMESPACE, "icedragonforge", DragonForgeRecipe.class);
    public static final RecipeType<DragonForgeRecipe> LIGHTNING = RecipeType.create(ResourceLocation.DEFAULT_NAMESPACE, "lightningdragonforge", DragonForgeRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
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
        registration.addRecipeCatalyst(IafBlocks.DRAGONFORGE_FIRE_CORE.get(), FIRE);
        registration.addRecipeCatalyst(IafBlocks.DRAGONFORGE_ICE_CORE.get(), ICE);
        registration.addRecipeCatalyst(IafBlocks.DRAGONFORGE_LIGHTNING_CORE.get(), LIGHTNING);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<RecipeHolder<DragonForgeRecipe>> recipeList = recipeManager.getAllRecipesFor(IafRecipes.DRAGON_FORGE_TYPE.get());

        List<DragonForgeRecipe> FIRE_RECIPES = new ArrayList<>();
        List<DragonForgeRecipe> ICE_RECIPES = new ArrayList<>();
        List<DragonForgeRecipe> LIGHTNING_RECIPES = new ArrayList<>();

        for (RecipeHolder<DragonForgeRecipe> recipe : recipeList) {
            switch (recipe.value().getDragonType()) {
                case "fire":
                    FIRE_RECIPES.add(recipe.value());
                    break;
                case "ice":
                    ICE_RECIPES.add(recipe.value());
                    break;
                case "lightning":
                    LIGHTNING_RECIPES.add(recipe.value());
                    break;
            }
        }

        registration.addRecipes(FIRE, FIRE_RECIPES);
        registration.addRecipes(ICE, ICE_RECIPES);
        registration.addRecipes(LIGHTNING, LIGHTNING_RECIPES);
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
