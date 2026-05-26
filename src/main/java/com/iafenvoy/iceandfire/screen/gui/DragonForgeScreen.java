package com.iafenvoy.iceandfire.screen.gui;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.screen.handler.DragonForgeScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class DragonForgeScreen extends AbstractContainerScreen<DragonForgeScreenHandler> {
    public DragonForgeScreen(DragonForgeScreenHandler container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {
        assert this.minecraft != null;
        Font textRenderer = this.minecraft.font;
        String s = I18n.get("block.iceandfire.dragonforge_" + this.menu.getDragonType().name() + "_core");
        pGuiGraphics.drawString(this.font, s, this.imageWidth / 2 - textRenderer.width(s) / 2, 6, 4210752, false);
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, String.format(Locale.ROOT, "textures/gui/dragonforge_%s.png", this.menu.getDragonType().name()));

        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(texture, k, l, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.getMaxCookTime() > 0)
            pGuiGraphics.blit(texture, k + 12, l + 23, 0, 166, 125 * this.menu.getCookTime() / this.menu.getMaxCookTime(), 38);
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(context, mouseX, mouseY, partialTicks);
        super.render(context, mouseX, mouseY, partialTicks);
        this.renderTooltip(context, mouseX, mouseY);
    }
}