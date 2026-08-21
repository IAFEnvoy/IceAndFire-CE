package com.iafenvoy.iceandfire.screen.gui;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.screen.menu.DragonForgeMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

public class DragonForgeScreen extends AbstractContainerScreen<DragonForgeMenu> {
    public DragonForgeScreen(DragonForgeMenu container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor pGuiGraphics, int mouseX, int mouseY) {
        assert this.minecraft != null;
        Font textRenderer = this.minecraft.font;
        String s = I18n.get("block.iceandfire.dragonforge_" + this.menu.getDragonType().name() + "_core");
        pGuiGraphics.text(this.font, s, this.imageWidth / 2 - textRenderer.width(s) / 2, 6, 4210752, false);
        pGuiGraphics.text(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Identifier texture = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, String.format(Locale.ROOT, "textures/gui/dragonforge_%s.png", this.menu.getDragonType().name()));

        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, k, l, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if (this.menu.getMaxCookTime() > 0)
            pGuiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, k + 12, l + 23, 0, 166, 125 * this.menu.getCookTime() / this.menu.getMaxCookTime(), 38, 256, 256);
    }
}
