package com.iafenvoy.iceandfire.screen.gui;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.BestiaryPage;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.screen.menu.LecternMenu;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Random;

public class LecternScreen extends AbstractContainerScreen<LecternMenu> {
    private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/lectern.png");
    private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/lectern_book.png");
    private static BookModel bookModel;
    private final Random random = new Random();
    private final Component nameable;
    public int ticks;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;
    private int flapTimer = 0;

    public LecternScreen(LecternMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.nameable = name;
    }

    @Override
    protected void init() {
        super.init();
        assert this.minecraft != null;
        bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Override
    protected void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
        assert this.minecraft != null;
        Font font = this.minecraft.font;
        font.drawInBatch(this.nameable.getString(), 12, 4, 4210752, false, ms.pose().last().pose(), ms.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        font.drawInBatch(this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false, ms.pose().last().pose(), ms.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.menu.onUpdate();
        this.tickBook();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        assert this.minecraft != null;
        assert this.minecraft.gameMode != null;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        for (int k = 0; k < 3; ++k) {
            double l = mouseX - (i + 60);
            double i1 = mouseY - (j + 14 + 19 * k);
            if (l >= 0 && i1 >= 0 && l < 108 && i1 < 19 && this.menu.clickMenuButton(this.minecraft.player, k)) {
                this.flapTimer = 5;
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, k);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        assert this.minecraft != null;
        Lighting.setupForFlatItems();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        ms.blit(ENCHANTMENT_TABLE_GUI_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
        int k = (int) this.minecraft.getWindow().getGuiScale();
        RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
        Matrix4f matrix4f = new Matrix4f().m03(-0.34F).m13(0.23F);
        matrix4f.mul(new Matrix4f().perspective(90.0F, 1.3333334F, 9.0F, 80.0F));
        ms.pose().pushPose();
        ms.pose().setIdentity();
        ms.pose().translate(0.0D, 3.3F, 1984.0D);
        ms.pose().scale(5.0F, 5.0F, 5.0F);
        ms.pose().mulPose(Axis.ZP.rotationDegrees(180.0F));
        ms.pose().mulPose(Axis.XP.rotationDegrees(20.0F));
        float f1 = Mth.lerp(partialTicks, this.oOpen, this.open);
        ms.pose().translate(((1.0F - f1) * 0.2F), ((1.0F - f1) * 0.1F), ((1.0F - f1) * 0.25F));
        float f2 = -(1.0F - f1) * 90.0F - 90.0F;
        ms.pose().mulPose(Axis.YP.rotationDegrees(f2));
        ms.pose().mulPose(Axis.XP.rotationDegrees(180.0F));
        float f3 = Mth.lerp(partialTicks, this.oFlip, this.flip) + 0.25F;
        float f4 = Mth.lerp(partialTicks, this.oFlip, this.flip) + 0.75F;
        f3 = (f3 - (float) Mth.floor(f3)) * 1.6F - 0.3F;
        f4 = (f4 - (float) Mth.floor(f4)) * 1.6F - 0.3F;
        if (f3 < 0.0F) f3 = 0.0F;
        if (f4 < 0.0F) f4 = 0.0F;
        if (f3 > 1.0F) f3 = 1.0F;
        if (f4 > 1.0F) f4 = 1.0F;
        bookModel.setupAnim(0, f3, f4, f1);
        VertexConsumer vertexconsumer = ms.bufferSource().getBuffer(bookModel.renderType(ENCHANTMENT_TABLE_BOOK_TEXTURE));
        bookModel.renderToBuffer(ms.pose(), vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, -1);
        ms.flush();
        ms.pose().popPose();
        RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
        Lighting.setupFor3DItems();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        for (int i1 = 0; i1 < 3; ++i1) {
            int j1 = i + 60;
            int k1 = j1 + 20;
            int l1 = this.menu.getPossiblePages()[i1] == null ? -1 : IafRegistries.BESTIARY_PAGE.getId(this.menu.getPossiblePages()[i1]);//enchantment level
            RenderSystem.setShaderColor(1, 1, 1, 1);
            if (l1 == -1)
                ms.blit(ENCHANTMENT_TABLE_GUI_TEXTURE, j1, j + 14 + 19 * i1, 0, 185, 108, 19);
            else {
                String s = "" + 3;
                Font fontrenderer = this.minecraft.font;
                String s1 = "";
                float textScale = 1.0F;
                BestiaryPage enchantment = this.menu.getPossiblePages()[i1];
                if (enchantment != null) {
                    s1 = I18n.get("bestiary." + enchantment.name());//EnchantmentNameParts.getInstance().generateNewRandomName(this.fontRenderer, l1);
                    if (fontrenderer.width(s1) > 80)
                        textScale = 1.0F - (fontrenderer.width(s1) - 80) * 0.01F;
                }
                int j2 = 6839882;
                if (this.menu.getSlot(0).getItem().getItem() == IafItems.BESTIARY.get()) { // Forge: render buttons as disabled when enchantable but enchantability not met on lower levels
                    int k2 = mouseX - (i + 60);
                    int l2 = mouseY - (j + 14 + 19 * i1);
                    int j3 = 0X9F988C;
                    if (k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19) {
                        ms.blit(ENCHANTMENT_TABLE_GUI_TEXTURE, j1, j + 14 + 19 * i1, 0, 204, 108, 19);
                        j2 = 16777088;
                        j3 = 16777088;
                    } else
                        ms.blit(ENCHANTMENT_TABLE_GUI_TEXTURE, j1, j + 14 + 19 * i1, 0, 166, 108, 19);

                    ms.blit(ENCHANTMENT_TABLE_GUI_TEXTURE, j1 + 1, j + 15 + 19 * i1, 16 * i1, 223, 16, 16);
                    ms.pose().pushPose();
                    ms.pose().translate(this.width / 2F - 10, this.height / 2F - 83 + (1.0F - textScale) * 55, 2);
                    ms.pose().scale(textScale, textScale, 1);
                    fontrenderer.drawInBatch(s1, 0, 20 + 19 * i1, j2, false, ms.pose().last().pose(), ms.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                    ms.pose().popPose();
                    fontrenderer = this.minecraft.font;
                    fontrenderer.drawInBatch(s, k1 + 84 - fontrenderer.width(s),
                            j + 13 + 19 * i1 + 7, j3, true, ms.pose().last().pose(), ms.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                } else {
                    ms.blit(ENCHANTMENT_TABLE_GUI_TEXTURE, j1, j + 14 + 19 * i1, 0, 185, 108, 19);
                    ms.blit(ENCHANTMENT_TABLE_GUI_TEXTURE, j1 + 1, j + 15 + 19 * i1, 16 * i1, 239, 16, 16);
                }
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms, mouseX, mouseY, partialTicks);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
    }

    public void tickBook() {
        ItemStack itemstack = this.menu.getSlot(0).getItem();

        if (!ItemStack.matches(itemstack, this.last)) {
            this.last = itemstack;
            do this.flipT += this.random.nextInt(4) - this.random.nextInt(4);
            while (this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
        }
        ++this.ticks;
        this.oFlip = this.flip;
        this.oOpen = this.open;

        boolean flag = false;
        for (int i = 0; i < 3; ++i)
            if (this.menu.getPossiblePages()[i] != null)
                flag = true;
        this.open += flag ? 0.2F : -0.2F;

        this.open = Mth.clamp(this.open, 0.0F, 1.0F);
        float f1 = (this.flipT - this.flip) * 0.4F;
        if (this.flapTimer > 0) {
            assert this.minecraft != null;
            f1 = (this.ticks + this.minecraft.getTimer().getGameTimeDeltaPartialTick(false)) * 0.5F;
            this.flapTimer--;
        }
        f1 = Mth.clamp(f1, -0.2F, 0.2F);
        this.flipA += (f1 - this.flipA) * 0.9F;
        this.flip += this.flipA;
    }
}