package com.iafenvoy.iceandfire.screen.gui;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.BestiaryPage;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.screen.menu.LecternMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Random;

public class LecternScreen extends AbstractContainerScreen<LecternMenu> {
    private static final Identifier ENCHANTMENT_TABLE_GUI_TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/lectern.png");
    private static final Identifier ENCHANTMENT_TABLE_BOOK_TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/lectern_book.png");
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
    private int flapTimer;

    public LecternScreen(LecternMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.nameable = name;
    }

    @Override
    protected void init() {
        super.init();
        bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.nameable, 12, 4, 4210752, false);
        graphics.text(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, 4210752, false);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.menu.onUpdate();
        this.tickBook();
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        for (int index = 0; index < 3; ++index) {
            double x = event.x() - (left + 60);
            double y = event.y() - (top + 14 + 19 * index);
            if (x >= 0.0 && y >= 0.0 && x < 108.0 && y < 19.0 && this.menu.clickMenuButton(this.minecraft.player, index)) {
                this.flapTimer = 5;
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, index);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, left, top, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        this.extractBook(graphics, left, top, partialTick);

        for (int index = 0; index < 3; ++index) {
            int buttonX = left + 60;
            int textX = buttonX + 20;
            BestiaryPage page = this.menu.getPossiblePages()[index];
            int pageId = page == null ? -1 : IafRegistries.BESTIARY_PAGE.getId(page);
            if (pageId == -1) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, buttonX, top + 14 + 19 * index, 0.0F, 185.0F, 108, 19, 256, 256);
                continue;
            }

            Font font = this.font;
            String pageName = I18n.get("bestiary." + page.name());
            float textScale = font.width(pageName) > 80 ? 1.0F - (font.width(pageName) - 80) * 0.01F : 1.0F;
            int textColor = 6839882;
            int costColor = 0x9F988C;
            if (this.menu.getSlot(0).getItem().getItem() == IafItems.BESTIARY.get()) {
                int x = mouseX - buttonX;
                int y = mouseY - (top + 14 + 19 * index);
                if (x >= 0 && y >= 0 && x < 108 && y < 19) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, buttonX, top + 14 + 19 * index, 0.0F, 204.0F, 108, 19, 256, 256);
                    textColor = 16777088;
                    costColor = 16777088;
                } else {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, buttonX, top + 14 + 19 * index, 0.0F, 166.0F, 108, 19, 256, 256);
                }
                graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, buttonX + 1, top + 15 + 19 * index, 16.0F * index, 223.0F, 16, 16, 256, 256);
                graphics.pose().pushMatrix();
                graphics.pose().translate(this.width / 2.0F - 10.0F, this.height / 2.0F - 83.0F + (1.0F - textScale) * 55.0F);
                graphics.pose().scale(textScale, textScale);
                graphics.text(font, pageName, 0, 20 + 19 * index, textColor, false);
                graphics.pose().popMatrix();
                graphics.text(this.font, "3", textX + 84 - this.font.width("3"), top + 20 + 19 * index, costColor, true);
            } else {
                graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, buttonX, top + 14 + 19 * index, 0.0F, 185.0F, 108, 19, 256, 256);
                graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, buttonX + 1, top + 15 + 19 * index, 16.0F * index, 239.0F, 16, 16, 256, 256);
            }
        }
    }

    private void extractBook(GuiGraphicsExtractor graphics, int left, int top, float partialTick) {
        float bookOpen = Mth.lerp(partialTick, this.oOpen, this.open);
        float bookFlip = Mth.lerp(partialTick, this.oFlip, this.flip);
        graphics.book(bookModel, ENCHANTMENT_TABLE_BOOK_TEXTURE, 40.0F, bookOpen, bookFlip, left + 14, top + 14, left + 52, top + 45);
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
        boolean shouldOpen = false;
        for (int index = 0; index < 3; ++index) if (this.menu.getPossiblePages()[index] != null) shouldOpen = true;
        this.open += shouldOpen ? 0.2F : -0.2F;
        this.open = Mth.clamp(this.open, 0.0F, 1.0F);
        float difference = (this.flipT - this.flip) * 0.4F;
        if (this.flapTimer > 0) {
            difference = (this.ticks + this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false)) * 0.5F;
            this.flapTimer--;
        }
        difference = Mth.clamp(difference, -0.2F, 0.2F);
        this.flipA += (difference - this.flipA) * 0.9F;
        this.flip += this.flipA;
    }
}
