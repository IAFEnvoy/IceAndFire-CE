package com.iafenvoy.iceandfire.screen.gui;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.screen.menu.DragonMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class DragonScreen extends AbstractContainerScreen<DragonMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/dragon.png");

    public DragonScreen(DragonMenu dragonInv, Inventory playerInv, Component name) {
        super(dragonInv, playerInv, name, 176, 214);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        matrixStack.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, k, l, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        assert Minecraft.getInstance().level != null;
        DragonBaseEntity dragon = this.menu.getDragon();
        float dragonScale = 1F / Math.max(0.0001F, dragon.getAgeScale());
        InventoryScreen.extractEntityInInventoryFollowsMouse(matrixStack, k + 64, l + 28, k + 112, l + 84, (int) (dragonScale * 23F), 0.25F, mouseX, mouseY, dragon);
        assert this.minecraft != null;
        Font textRenderer = this.minecraft.font;
        String s3 = dragon.getCustomName() == null ? I18n.get("dragon.unnamed") : I18n.get("dragon.name") + " " + dragon.getCustomName().getString();
        matrixStack.text(textRenderer, s3, k + this.imageWidth / 2 - textRenderer.width(s3) / 2, l + 75, 0XFFFFFF, false);
        String s2 = I18n.get("dragon.health") + " " + Math.floor(Math.min(dragon.getHealth(), dragon.getMaxHealth())) + " / " + dragon.getMaxHealth();
        matrixStack.text(textRenderer, s2, k + this.imageWidth / 2 - textRenderer.width(s2) / 2, l + 84, 0XFFFFFF, false);
        String s = (dragon.isMale() ? "dragon.gender.male" : "dragon.gender.female");
        String s5 = I18n.get("dragon.gender") + I18n.get(s);
        matrixStack.text(textRenderer, s5, k + this.imageWidth / 2 - textRenderer.width(s5) / 2, l + 93, 0XFFFFFF, false);
        String s6 = I18n.get("dragon.hunger") + dragon.getHunger() + "/100";
        matrixStack.text(textRenderer, s6, k + this.imageWidth / 2 - textRenderer.width(s6) / 2, l + 102, 0XFFFFFF, false);
        String s4 = I18n.get("dragon.stage") + " " + dragon.getDragonStage() + " " + I18n.get("dragon.days.front") + dragon.getAgeInDays() + " " + I18n.get("dragon.days.back");
        matrixStack.text(textRenderer, s4, k + this.imageWidth / 2 - textRenderer.width(s4) / 2, l + 111, 0XFFFFFF, false);
        String s7 = dragon.getOwner() != null ? I18n.get("dragon.owner") + dragon.getOwner().getName().getString() : I18n.get("dragon.untamed");
        matrixStack.text(textRenderer, s7, k + this.imageWidth / 2 - textRenderer.width(s7) / 2, l + 120, 0XFFFFFF, false);
    }
}
