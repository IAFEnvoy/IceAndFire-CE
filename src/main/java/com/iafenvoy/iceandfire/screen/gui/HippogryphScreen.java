package com.iafenvoy.iceandfire.screen.gui;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.HippogryphEntity;
import com.iafenvoy.iceandfire.screen.handler.HippogryphScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class HippogryphScreen extends AbstractContainerScreen<HippogryphScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/hippogryph.png");

    public HippogryphScreen(HippogryphScreenHandler handler, Inventory playerInv, Component name) {
        super(handler, playerInv, name);
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        context.drawString(this.font, this.menu.getHippogryph().getDisplayName().getString(), 8, 6, 4210752, false);
        context.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        this.renderBackground(context, mouseX, mouseY, tickDelta);
        super.render(context, mouseX, mouseY, tickDelta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics context, float tickDelta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
        HippogryphEntity hippo = this.menu.getHippogryph();
        if (hippo.isChested()) context.blit(TEXTURE, i + 79, j + 17, 0, this.imageHeight, 5 * 18, 54);
        InventoryScreen.renderEntityInInventoryFollowsMouse(context, i + 26, j + 18, i + 77, j + 69, 17, 0.25F, mouseX, mouseY, hippo);
    }
}