package com.iafenvoy.iceandfire.screen.gui;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.screen.handler.DragonScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DragonScreen extends AbstractContainerScreen<DragonScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/dragon.png");

    public DragonScreen(DragonScreenHandler dragonInv, Inventory playerInv, Component name) {
        super(dragonInv, playerInv, name);
        this.imageHeight = 214;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY) {
    }

    @Override
    public void render(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        matrixStack.blit(TEXTURE, k, l, 0, 0, this.imageWidth, this.imageHeight);
        assert Minecraft.getInstance().level != null;
        DragonBaseEntity dragon = this.menu.getDragon();
        float dragonScale = 1F / Math.max(0.0001F, dragon.getAgeScale());
        Quaternionf quaternionf = (new Quaternionf()).rotateY((float) Mth.lerp((float) mouseX / this.width, 0, Math.PI)).rotateZ((float) Mth.lerp((float) mouseY / this.width, Math.PI, Math.PI + 0.2));
        InventoryScreen.renderEntityInInventory(matrixStack, k + 88, l + (int) (0.5F * (dragon.flyProgress)) + 55, (int) (dragonScale * 23F), new Vector3f(0), quaternionf, null, dragon);
        assert this.minecraft != null;
        Font textRenderer = this.minecraft.font;
        String s3 = dragon.getCustomName() == null ? I18n.get("dragon.unnamed") : I18n.get("dragon.name") + " " + dragon.getCustomName().getString();
        textRenderer.drawInBatch(s3, k + (float) this.imageWidth / 2 - (float) textRenderer.width(s3) / 2, l + 75, 0XFFFFFF, false, matrixStack.pose().last().pose(), matrixStack.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        String s2 = I18n.get("dragon.health") + " " + Math.floor(Math.min(dragon.getHealth(), dragon.getMaxHealth())) + " / " + dragon.getMaxHealth();
        textRenderer.drawInBatch(s2, k + (float) this.imageWidth / 2 - (float) textRenderer.width(s2) / 2, l + 84, 0XFFFFFF, false, matrixStack.pose().last().pose(), matrixStack.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        String s = (dragon.isMale() ? "dragon.gender.male" : "dragon.gender.female");
        String s5 = I18n.get("dragon.gender") + I18n.get(s);
        textRenderer.drawInBatch(s5, k + (float) this.imageWidth / 2 - (float) textRenderer.width(s5) / 2, l + 93, 0XFFFFFF, false, matrixStack.pose().last().pose(), matrixStack.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        String s6 = I18n.get("dragon.hunger") + dragon.getHunger() + "/100";
        textRenderer.drawInBatch(s6, k + (float) this.imageWidth / 2 - (float) textRenderer.width(s6) / 2, l + 102, 0XFFFFFF, false, matrixStack.pose().last().pose(), matrixStack.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        String s4 = I18n.get("dragon.stage") + " " + dragon.getDragonStage() + " " + I18n.get("dragon.days.front") + dragon.getAgeInDays() + " " + I18n.get("dragon.days.back");
        textRenderer.drawInBatch(s4, k + (float) this.imageWidth / 2 - (float) textRenderer.width(s4) / 2, l + 111, 0XFFFFFF, false, matrixStack.pose().last().pose(), matrixStack.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        String s7 = dragon.getOwner() != null ? I18n.get("dragon.owner") + dragon.getOwner().getName().getString() : I18n.get("dragon.untamed");
        textRenderer.drawInBatch(s7, k + (float) this.imageWidth / 2 - (float) textRenderer.width(s7) / 2, l + 120, 0XFFFFFF, false, matrixStack.pose().last().pose(), matrixStack.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
    }
}