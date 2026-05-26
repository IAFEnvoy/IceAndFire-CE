package com.iafenvoy.iceandfire.compat.emi;

import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ForgeRecipeHolder {
    private final String dragonType;
    private final EmiTexture texture;
    private final EmiStack workstation;
    private final EmiRecipeCategory category;

    public ForgeRecipeHolder(ResourceLocation id, String dragonType, EmiTexture texture, EmiStack workstation) {
        this.dragonType = dragonType;
        this.texture = texture;
        this.workstation = workstation;
        this.category = new EmiRecipeCategory(id, workstation, texture);
    }

    public void register(EmiRegistry registry) {
        registry.addCategory(this.category);
        registry.addWorkstation(this.category, this.workstation);
        List<RecipeHolder<DragonForgeRecipe>> forgeRecipeList = registry.getRecipeManager().getAllRecipesFor(IafRecipes.DRAGON_FORGE_TYPE.get());
        for (RecipeHolder<DragonForgeRecipe> recipeEntry : forgeRecipeList.stream().filter(entry -> entry.value().getDragonType().equals(this.dragonType)).toList())
            registry.addRecipe(new DragonForgeEmiRecipe(recipeEntry, this.category));
    }

    public class DragonForgeEmiRecipe implements EmiRecipe {
        private final RecipeHolder<DragonForgeRecipe> entry;
        private final EmiRecipeCategory category;

        public DragonForgeEmiRecipe(RecipeHolder<DragonForgeRecipe> entry, EmiRecipeCategory category) {
            this.entry = entry;
            this.category = category;
        }

        @Override
        public EmiRecipeCategory getCategory() {
            return this.category;
        }

        @Override
        public @Nullable ResourceLocation getId() {
            return this.entry.id();
        }

        @Override
        public List<EmiIngredient> getInputs() {
            return List.of(EmiIngredient.of(this.entry.value().getInput()), EmiIngredient.of(this.entry.value().getBlood()));
        }

        @Override
        public List<EmiStack> getOutputs() {
            return List.of(EmiStack.of(this.entry.value().getResultItem()));
        }

        @Override
        public int getDisplayWidth() {
            return 176;
        }

        @Override
        public int getDisplayHeight() {
            return 1120;
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            widgets.addTexture(ForgeRecipeHolder.this.texture, 3, 4);
            widgets.addSlot(EmiIngredient.of(this.entry.value().getInput()), 67, 33);
            widgets.addSlot(EmiIngredient.of(this.entry.value().getBlood()), 85, 33);
            widgets.addSlot(EmiStack.of(this.entry.value().getResultItem()), 143, 30).large(true).recipeContext(this);
        }
    }
}
