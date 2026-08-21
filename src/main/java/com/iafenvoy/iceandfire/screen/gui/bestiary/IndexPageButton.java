package com.iafenvoy.iceandfire.screen.gui.bestiary;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class IndexPageButton extends Button {
    public IndexPageButton(int x, int y, Component buttonText, OnPress butn) {
        super(x, y, 160, 32, buttonText, butn, DEFAULT_NARRATION);
        this.width = 160;
        this.height = 32;
    }

    @Override
    protected void extractContents(@NotNull GuiGraphicsExtractor pGuiGraphics, int mouseX, int mouseY, float partial) {
        if (this.active) {
            Font font = Minecraft.getInstance().font;
            boolean flag = this.isHoveredOrFocused();
            pGuiGraphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/bestiary/widgets.png"), this.getX(), this.getY(), 0, flag ? 32 : 0, this.width, this.height, 256, 256);
            pGuiGraphics.text(font, this.getMessage(), this.getX() + (this.width - font.width(this.getMessage())) / 2, this.getY() + (this.height - 8) / 2, -1);
        }
    }
}
