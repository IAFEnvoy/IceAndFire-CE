package com.iafenvoy.iceandfire.screen.gui;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import com.iafenvoy.iceandfire.screen.handler.DragonForgeScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class DragonForgeScreen extends HandledScreen<DragonForgeScreenHandler> {
    public DragonForgeScreen(DragonForgeScreenHandler container, PlayerInventory inv, Text name) {
        super(container, inv, name);
    }

    @Override
    protected void drawForeground(DrawContext pGuiGraphics, int mouseX, int mouseY) {
        assert this.client != null;
        TextRenderer textRenderer = this.client.textRenderer;
        String s = I18n.translate("block.iceandfire.dragonforge_" + this.handler.getDragonType().name() + "_core");
        pGuiGraphics.drawText(this.textRenderer, s, this.backgroundWidth / 2 - textRenderer.getWidth(s) / 2, 6, 4210752, false);
        pGuiGraphics.drawText(this.textRenderer, this.playerInventoryTitle, 8, this.backgroundHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void drawBackground(DrawContext pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        Identifier texture = Identifier.of(IceAndFire.MOD_ID, "textures/gui/dragonforge_%s.png".formatted(this.handler.getDragonType().name()));

        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        pGuiGraphics.drawTexture(texture, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int i1 = this.getCookTime(this.handler.getPropertyDelegate().get(0));
        pGuiGraphics.drawTexture(texture, k + 12, l + 23, 0, 166, i1, 38);
    }

    private int getCookTime(int time) {
        assert this.client != null;
        assert this.client.world != null;
        List<RecipeEntry<DragonForgeRecipe>> recipes = this.client.world.getRecipeManager().listAllOfType(IafRecipes.DRAGON_FORGE_TYPE.get()).stream().filter(item -> item.value().isValidInput(this.handler.getSlot(0).getStack()) && item.value().isValidBlood(this.handler.getSlot(1).getStack())).toList();
        int maxCookTime = recipes.isEmpty() ? 100 : recipes.getFirst().value().getCookTime();
        double scale = 125000.0 / maxCookTime;
        return (int) (scale * time / maxCookTime);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(context, mouseX, mouseY, partialTicks);
        super.render(context, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}