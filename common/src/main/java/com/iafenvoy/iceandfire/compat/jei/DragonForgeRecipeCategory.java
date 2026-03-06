package com.iafenvoy.iceandfire.compat.jei;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

//By jdkdigital
abstract public class DragonForgeRecipeCategory extends AbstractRecipeCategory<DragonForgeRecipe> {
    private final IDrawable background;
    private final IDrawable overlay;

    public DragonForgeRecipeCategory(IGuiHelper guiHelper, RecipeType<DragonForgeRecipe> recipeType, String forgeType, IDrawable icon) {
        super(
                recipeType,
                Text.translatable("jei." + IceAndFire.MOD_ID + ".dragon_forge_" + forgeType),
                icon,
                170, 79
        );
        this.background = guiHelper.drawableBuilder(Identifier.of(IceAndFire.MOD_ID, "textures/gui/dragonforge_" + forgeType + ".png"), 3, 4, 170, 79).setTextureSize(256, 256).build();
        this.overlay = guiHelper.createAnimatedDrawable(
                guiHelper.drawableBuilder(Identifier.of(IceAndFire.MOD_ID, "textures/gui/dragonforge_" + forgeType + ".png"), 0, 166, 126, 38).setTextureSize(256, 256).build(),
                50, IDrawableAnimated.StartDirection.LEFT, false
        );
    }

    @Override
    public void draw(@NotNull DragonForgeRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull DrawContext guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        this.overlay.draw(guiGraphics, 9, 19);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DragonForgeRecipe recipe, @NotNull IFocusGroup focuses) {
        builder
                .addSlot(RecipeIngredientRole.INPUT, 65, 30)
                .addIngredients(recipe.getInput())
                .setStandardSlotBackground();

        builder
                .addSlot(RecipeIngredientRole.INPUT, 83, 30)
                .addIngredients(recipe.getBlood())
                .setStandardSlotBackground();

        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 145, 31)
                .addItemStack(recipe.getResultItem());
    }
}
