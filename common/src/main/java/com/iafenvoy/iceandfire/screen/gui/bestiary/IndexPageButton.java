package com.iafenvoy.iceandfire.screen.gui.bestiary;

import com.iafenvoy.iceandfire.IceAndFire;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class IndexPageButton extends ButtonWidget {
    public IndexPageButton(int x, int y, Text buttonText, PressAction butn) {
        super(x, y, 160, 32, buttonText, butn, DEFAULT_NARRATION_SUPPLIER);
        this.width = 160;
        this.height = 32;
    }

    @Override
    public void renderWidget(DrawContext pGuiGraphics, int mouseX, int mouseY, float partial) {
        if (this.active) {
            pGuiGraphics.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            boolean flag = this.isSelected();
            pGuiGraphics.drawTexture(Identifier.of(IceAndFire.MOD_ID, "textures/gui/bestiary/widgets.png"), this.getX(), this.getY(), 0, flag ? 32 : 0, this.width, this.height);
            pGuiGraphics.setShaderColor(1, 1, 1, 1);
            int i = -1;
            this.drawMessage(pGuiGraphics, font, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
