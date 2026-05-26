package com.iafenvoy.iceandfire.screen.gui;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.screen.menu.PodiumMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class PodiumScreen extends AbstractContainerScreen<PodiumMenu> {
    public static final ResourceLocation PODIUM_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/podium.png");

    public PodiumScreen(PodiumMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.imageHeight = 133;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int x, int y) {
        Component s = Component.translatable("block.iceandfire.podium");
        assert this.minecraft != null;
        pGuiGraphics.drawCenteredString(this.font, s, this.imageWidth / 2, 6, 4210752);
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(context, mouseX, mouseY, partialTicks);
        super.render(context, mouseX, mouseY, partialTicks);
        this.renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(PODIUM_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}