package com.iafenvoy.iceandfire.screen.gui;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.screen.menu.PodiumMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class PodiumScreen extends AbstractContainerScreen<PodiumMenu> {
    public static final Identifier PODIUM_TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/podium.png");

    public PodiumScreen(PodiumMenu container, Inventory inv, Component name) {
        super(container, inv, name, 176, 133);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor pGuiGraphics, int x, int y) {
        Component s = Component.translatable("block.iceandfire.podium");
        assert this.minecraft != null;
        pGuiGraphics.text(this.font, s, this.imageWidth / 2 - this.font.width(s) / 2, 6, 4210752);
        pGuiGraphics.text(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor pGuiGraphics, int x, int y, float partialTicks) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(RenderPipelines.GUI_TEXTURED, PODIUM_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
