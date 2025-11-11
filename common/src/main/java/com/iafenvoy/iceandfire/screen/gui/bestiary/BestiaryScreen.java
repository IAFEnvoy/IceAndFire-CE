package com.iafenvoy.iceandfire.screen.gui.bestiary;

import com.google.common.collect.Maps;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.BestiaryPage;
import com.iafenvoy.iceandfire.data.SeaSerpentType;
import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.item.component.BestiaryPageComponent;
import com.iafenvoy.iceandfire.registry.*;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.iafenvoy.iceandfire.screen.handler.BestiaryScreenHandler;
import com.iafenvoy.iceandfire.util.ItemRandomizer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class BestiaryScreen extends HandledScreen<BestiaryScreenHandler> {
    protected static final int X = 390;
    protected static final int Y = 245;
    private static final Identifier TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/gui/bestiary/bestiary.png");
    private static final Identifier DRAWINGS_0 = Identifier.of(IceAndFire.MOD_ID, "textures/gui/bestiary/drawings_0.png");
    private static final Identifier DRAWINGS_1 = Identifier.of(IceAndFire.MOD_ID, "textures/gui/bestiary/drawings_1.png");
    private static final Map<String, Identifier> PICTURE_LOCATION_CACHE = Maps.newHashMap();
    public final List<BestiaryPage> allPageTypes = new ArrayList<>();
    public final List<IndexPageButton> indexButtons = new ArrayList<>();
    protected final ItemStack book;
    public BestiaryPage pageType;
    public ChangePageButton previousPage;
    public ChangePageButton nextPage;
    public int bookPages;
    public int indexPages;
    public int indexPagesTotal = 1;
    protected boolean index;

    public BestiaryScreen(BestiaryScreenHandler container, PlayerInventory inv, Text name) {
        super(container, inv, name);
        this.book = container.getBook();
        if (!this.book.isEmpty() && this.book.getItem() != null && this.book.getItem() == IafItems.BESTIARY.get())
            if (this.book.contains(IafDataComponents.BESTIARY_PAGES.get())) {
                BestiaryPageComponent component = this.book.get(IafDataComponents.BESTIARY_PAGES.get());
                if (component == null) component = new BestiaryPageComponent(List.of());
                this.allPageTypes.addAll(component.pages());
                // Make sure the pageCount are sorted according to the enum
                this.allPageTypes.sort(Comparator.comparingInt(IafRegistries.BESTIARY_PAGE::getRawId));
                this.indexPagesTotal = (int) Math.ceil(component.pages().size() / 10D);
            }
        this.index = true;
    }

    private static Item getItemByRegistryName(String registryName) {
        return Registries.ITEM.get(Identifier.tryParse(registryName));
    }

    @Override
    protected void init() {
        super.init();
        this.clearChildren();
        this.indexButtons.clear();
        int centerX = (this.width - X) / 2;
        int centerY = (this.height - Y) / 2;
        this.previousPage = new ChangePageButton(centerX + 15, centerY + 215, false, 0, (p_214132_1_) -> {
            if ((this.index ? this.indexPages > 0 : this.pageType != null)) {
                if (this.index) {
                    this.indexPages--;
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(IafSounds.BESTIARY_PAGE.get(), 1.0F));
                } else if (this.bookPages > 0) {
                    this.bookPages--;
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(IafSounds.BESTIARY_PAGE.get(), 1.0F));
                } else this.index = true;
            }
        });
        this.addDrawableChild(this.previousPage);
        this.nextPage = new ChangePageButton(centerX + 357, centerY + 215, true, 0, (p_214132_1_) -> {
            if (this.index ? this.indexPages < this.indexPagesTotal - 1 : this.pageType != null && this.bookPages < this.pageType.pageCount()) {
                if (this.index) this.indexPages++;
                else this.bookPages++;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(IafSounds.BESTIARY_PAGE.get(), 1.0F));
            }
        });
        this.addDrawableChild(this.nextPage);
        if (!this.allPageTypes.isEmpty()) {
            for (int i = 0; i < this.allPageTypes.size(); i++) {
                //noinspection ExtractMethodRecommender
                int xIndex = i % -2;
                int yIndex = i % 10;
                int id = 2 + i;
                IndexPageButton button = new IndexPageButton(centerX + 15 + (xIndex * 200), centerY + 10 + (yIndex * 20) - (xIndex == 1 ? 20 : 0), Text.translatable("bestiary." + this.allPageTypes.get(i).name().toLowerCase(Locale.ROOT)), widget -> {
                    if (this.indexButtons.get(id - 2) != null && this.allPageTypes.get(id - 2) != null) {
                        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(IafSounds.BESTIARY_PAGE.get(), 1.0F));
                        this.index = false;
                        this.bookPages = 0;
                        this.pageType = this.allPageTypes.get(id - 2);
                    }
                });
                this.indexButtons.add(button);
                this.addDrawableChild(button);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(context, mouseX, mouseY, partialTicks);
        for (Drawable widget : this.drawables)
            if (widget instanceof IndexPageButton button) {
                button.active = this.index;
                button.visible = this.index;
            }
        for (int i = 0; i < this.indexButtons.size(); i++)
            this.indexButtons.get(i).active = i < 10 * (this.indexPages + 1) && i >= 10 * (this.indexPages) && this.index;
        RenderSystem.setShaderColor(1, 1, 1, 1);
        super.render(context, mouseX, mouseY, partialTicks);
        int cornerX = (this.width - X) / 2;
        int cornerY = (this.height - Y) / 2;
        context.getMatrices().push();
        context.getMatrices().translate(cornerX, cornerY, 0.0F);
        RenderSystem.disableDepthTest();
        if (!this.index) {
            this.drawPerPage(context, this.bookPages);
            int pageLeft = this.bookPages * 2 + 1;
            int pageRight = pageLeft + 1;
            context.drawText(this.textRenderer, String.valueOf(pageLeft), X / 4, Y - 32, 0X303030, false);
            context.drawText(this.textRenderer, String.valueOf(pageRight), X * 3 / 4, Y - 32, 0X303030, false);
        }
        context.getMatrices().pop();
        this.drawables.forEach((widget -> widget.render(context, mouseX, mouseY, partialTicks)));
        RenderSystem.enableDepthTest();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int cornerX = (this.width - X) / 2;
        int cornerY = (this.height - Y) / 2;
        context.drawTexture(TEXTURE, cornerX, cornerY, 0, 0, X, Y, 390, 390);
    }

    public void drawPerPage(DrawContext ms, int bookPages) {
        this.imageFromTxt(ms);
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        if (this.pageType.equals(IafBestiaryPages.INTRODUCTION)) {
            if (bookPages == 1) {
                this.drawItemStack(ms, new ItemStack(IafBlocks.SAPPHIRE_ORE.get()), 30, 20, 2.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.SAPPHIRE_GEM.get()), 40, 55, 2F);
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                boolean drawGold = player.age % 20 < 10;
                this.drawItemStack(ms, new ItemStack(drawGold ? Items.GOLD_NUGGET : IafItems.SILVER_NUGGET.get()), 144, 34, 1.5F);
                this.drawItemStack(ms, new ItemStack(drawGold ? Items.GOLD_NUGGET : IafItems.SILVER_NUGGET.get()), 161, 34, 1.5F);
                this.drawItemStack(ms, new ItemStack(drawGold ? IafBlocks.GOLD_PILE.get() : IafBlocks.SILVER_PILE.get()), 151, 7, 2F);
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 90, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(Blocks.OAK_PLANKS), 161, 124, 1.5F);
                this.drawItemStack(ms, new ItemStack(Blocks.OAK_PLANKS), 161, 107, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.MANUSCRIPT.get()), 161, 91, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafBlocks.LECTERN.get()), 151, 78, 2F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.TAMED_DRAGONS)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 90, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(Items.BONE), 145, 124, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.PORKCHOP), 145, 107, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.BONE), 145, 91, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.PORKCHOP), 161, 124, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.BONE), 161, 107, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.PORKCHOP), 161, 91, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.BONE), 177, 124, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.PORKCHOP), 177, 107, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.BONE), 177, 91, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_MEAL.get()), 151, 78, 2F);
            }
            if (bookPages == 1) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_SKULL_FIRE.get()), 161, 17, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.STICK), 161, 32, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_STAFF.get()), 151, 10, 2F);
            }
            if (bookPages == 2) {
                ms.getMatrices().push();
                this.drawItemStack(ms, new ItemStack(IafBlocks.FIRE_LILY.get()), 5, 14, 3.75F);
                this.drawItemStack(ms, new ItemStack(IafBlocks.FROST_LILY.get()), 17, 14, 3.75F);
                this.drawItemStack(ms, new ItemStack(IafBlocks.LIGHTNING_LILY.get()), 30, 14, 3.75F);
                ms.getMatrices().pop();
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                int type = (player.age / 20) % 3;
                this.drawItemStack(ms, new ItemStack(type == 0 ? IafBlocks.FIRE_LILY.get() : type == 1 ? IafBlocks.FROST_LILY.get() : IafBlocks.LIGHTNING_LILY.get()), 161, 17, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.BOWL), 161, 32, 1.5F);
                this.drawItemStack(ms, new ItemStack(type == 0 ? Items.BLAZE_ROD : type == 1 ? Items.PRISMARINE_CRYSTALS : Items.CHORUS_FRUIT), 177, 17, 1.5F);
                this.drawItemStack(ms, new ItemStack(type == 0 ? IafItems.FIRE_STEW.get() : type == 1 ? IafItems.FROST_STEW.get() : IafItems.LIGHTNING_STEW.get()), 151, 10, 2F);

                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 65, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();

                this.drawItemStack(ms, new ItemStack(Items.STICK), 144, 97, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_BONE.get()), 180, 110, 1.35f);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_BONE.get()), 180, 92, 1.35f);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_BONE.get()), 198, 92, 1.35f);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_BONE.get()), 198, 74, 1.35f);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_HORN.get()), 151, 60, 2F);

            }
            if (bookPages == 3) {
                int j = 18;
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGONARMOR_IRON_HEAD.get(), 1), j += 16, 60, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGONARMOR_IRON_NECK.get(), 1), j += 16, 60, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGONARMOR_IRON_BODY.get(), 1), j += 16, 60, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGONARMOR_IRON_TAIL.get(), 1), j + 16, 60, 1.5F);

                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 10, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_BONE.get()), 160, 12, 1.35f);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_BONE.get()), 180, 31, 1.35f);
                this.drawItemStack(ms, new ItemStack(Items.IRON_INGOT), 199, 50, 1.35f);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_FLUTE.get()), 151, 18, 2F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.MATERIALS)) {
            if (bookPages == 0) {
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGONSCALES_RED.get()), 18, 14, 3.75F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_BONE.get()), 70, 8, 3.75F);
                this.drawItemStack(ms, new ItemStack(IafItems.WITHERBONE.get()), 112, 65, 2.5F);

                int j = 18;
                this.drawItemStack(ms, new ItemStack(IafDragonColors.RED.helmet.get()), j += 16, 110, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafDragonColors.RED.chestplate.get()), j += 16, 110, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafDragonColors.RED.leggings.get()), j += 16, 110, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafDragonColors.RED.boots.get()), j + 16, 110, 1.5F);
            }
            if (bookPages == 1) {
                int j = 1;
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGONBONE_SWORD.get()), j += 16, 14, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGONBONE_PICKAXE.get()), j += 16, 14, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGONBONE_AXE.get()), j += 16, 14, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGONBONE_SHOVEL.get()), j += 16, 14, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGONBONE_HOE.get()), j += 16, 14, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_BOW.get()), j + 16, 14, 1.5F);

                this.drawItemStack(ms, new ItemStack(IafItems.FIRE_DRAGON_FLESH.get()), 18, 24, 3.75F);
                this.drawItemStack(ms, new ItemStack(IafItems.FIRE_DRAGON_HEART.get()), 70, 14, 3.75F);
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGON_SKULL_FIRE.get()), 70, 39, 3.75F);
            }
            if (bookPages == 2)
                this.drawItemStack(ms, new ItemStack(IafItems.FIRE_DRAGON_BLOOD.get()), 18, 20, 3.75F);
        } else if (this.pageType.equals(IafBestiaryPages.ALCHEMY)) {
            if (bookPages == 0) {
                this.drawItemStack(ms, new ItemStack(IafItems.FIRE_DRAGON_BLOOD.get()), 2, 24, 3.75F);
                this.drawItemStack(ms, new ItemStack(IafItems.ICE_DRAGON_BLOOD.get()), 18, 24, 3.75F);
                this.drawItemStack(ms, new ItemStack(IafItems.LIGHTNING_DRAGON_BLOOD.get()), 34, 24, 3.75F);
                int type = (player.age / 20) % 3;
                this.drawItemStack(ms, new ItemStack(IafItems.DRAGONBONE_SWORD.get()), 161, 17, 1.5F);
                this.drawItemStack(ms, new ItemStack(type == 0 ? IafItems.FIRE_DRAGON_BLOOD.get() : type == 1 ? IafItems.ICE_DRAGON_BLOOD.get() : IafItems.LIGHTNING_DRAGON_BLOOD.get()), 161, 32, 1.5F);
                this.drawItemStack(ms, new ItemStack(type == 0 ? IafItems.DRAGONBONE_SWORD_FIRE.get() : type == 1 ? IafItems.DRAGONBONE_SWORD_ICE.get() : IafItems.DRAGONBONE_SWORD_LIGHTNING.get()), 151, 10, 2F);
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
            }
        } else if (this.pageType.equals(IafBestiaryPages.HIPPOGRYPH)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                ms.getMatrices().scale(0.8F, 0.8F, 1F);
                this.drawImage(ms, DRAWINGS_0, 29, 150, 303, 151, 61, 36, 512F);
                this.drawImage(ms, DRAWINGS_0, 91, 150, 364, 151, 61, 36, 512F);
                this.drawImage(ms, DRAWINGS_0, 151, 150, 425, 151, 61, 36, 512F);
                this.drawImage(ms, DRAWINGS_0, 29, 190, 303, 187, 61, 36, 512F);
                this.drawImage(ms, DRAWINGS_0, 91, 190, 364, 187, 61, 36, 512F);
                this.drawImage(ms, DRAWINGS_0, 151, 190, 425, 187, 61, 36, 512F);
                this.drawImage(ms, DRAWINGS_0, 90, 230, 425, 223, 61, 35, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(ItemRandomizer.random(IafItemTags.TAME_HIPPOGRYPH)), 70, 20, 3.75F);
            }
            if (bookPages == 1) {
                this.drawItemStack(ms, new ItemStack(Items.STICK), 16, 24, 3.75F);

                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 10, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(Items.FEATHER), 160, 31, 1.35F);
                int drawType = player.age % 60 > 40 ? 2 : player.age % 60 > 20 ? 1 : 0;
                this.drawItemStack(ms, new ItemStack(drawType == 0 ? Items.IRON_HORSE_ARMOR : drawType == 1 ? Items.GOLDEN_HORSE_ARMOR : Items.DIAMOND_HORSE_ARMOR), 180, 31, 1.35F);
                this.drawItemStack(ms, new ItemStack(Items.FEATHER), 199, 31, 1.35F);
                this.drawItemStack(ms, new ItemStack(drawType == 0 ? IafItems.IRON_HIPPOGRYPH_ARMOR.get() : drawType == 1 ? IafItems.GOLD_HIPPOGRYPH_ARMOR.get() : IafItems.DIAMOND_HIPPOGRYPH_ARMOR.get()), 151, 18, 2F);
                this.drawItemStack(ms, new ItemStack(ItemRandomizer.random(IafItemTags.BREED_HIPPOGRYPH)), 70, 23, 3.75F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.GORGON)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 10, 89, 473, 117, 19, 34, 512F);
                this.drawImage(ms, DRAWINGS_0, 50, 78, 399, 106, 28, 45, 512F);
                this.drawImage(ms, DRAWINGS_0, 100, 89, 455, 117, 18, 34, 512F);
                ms.getMatrices().pop();

                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 70, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();

                this.drawItemStack(ms, new ItemStack(Items.STRING), 160, 97, 1.35F);
                this.drawItemStack(ms, new ItemStack(Items.LEATHER), 180, 97, 1.35F);
                this.drawItemStack(ms, new ItemStack(Items.STRING), 199, 97, 1.35F);
                this.drawItemStack(ms, new ItemStack(IafItems.BLINDFOLD.get()), 171, 65, 2F);
            }
            if (bookPages == 1) {
                this.drawItemStack(ms, new ItemStack(IafItems.GORGON_HEAD.get()), 16, 12, 3.75F);
                ms.getMatrices().push();
                ms.getMatrices().scale(1.7F, 1.7F, 1F);
                this.drawImage(ms, DRAWINGS_0, 37, 95, 473, 117, 19, 34, 512F);
                this.drawImage(ms, DRAWINGS_0, 60, 95, 455, 117, 18, 34, 512F);
                ms.getMatrices().pop();
            }
        } else if (this.pageType.equals(IafBestiaryPages.PIXIE)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                this.drawImage(ms, DRAWINGS_0, 20, 60, 371, 258, 47, 35, 512F);
                this.drawImage(ms, DRAWINGS_0, 42, 95, 416, 258, 45, 35, 512F);
                this.drawImage(ms, DRAWINGS_0, 67, 60, 462, 258, 47, 35, 512F);
                this.drawImage(ms, DRAWINGS_0, 88, 95, 370, 293, 47, 35, 512F);
                this.drawImage(ms, DRAWINGS_0, 110, 60, 416, 293, 47, 35, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(IafItems.PIXIE_DUST.get()), 70, 10, 3.75F);
                ms.getMatrices().push();
                ms.getMatrices().scale(0.9F, 0.9F, 1F);
                ms.getMatrices().translate(20, 24, 0);
                ms.getMatrices().push();
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 150, 100, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(Blocks.GLASS), 160, 113, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.GLASS), 199, 113, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.OAK_PLANKS), 180, 113, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.GLASS), 160, 131, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.GLASS), 199, 131, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.GLASS), 180, 150, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.GLASS), 160, 150, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.GLASS), 199, 150, 1.35F);
                this.drawItemStack(ms, new ItemStack(IafBlocks.JAR_EMPTY.get()), 171, 85, 2F);
                ms.getMatrices().pop();
                ms.getMatrices().pop();

            }
            if (bookPages == 1) {
                this.drawItemStack(ms, new ItemStack(IafItems.AMBROSIA.get()), 14, 22, 3.75F);
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 100, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();

                this.drawItemStack(ms, new ItemStack(IafItems.PIXIE_DUST.get()), 180, 131, 1.35F);
                this.drawItemStack(ms, new ItemStack(Items.BOWL), 180, 150, 1.35F);
                this.drawItemStack(ms, new ItemStack(IafItems.AMBROSIA.get()), 171, 85, 2F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.CYCLOPS)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1.5F);
                this.drawImage(ms, DRAWINGS_0, 185, 8, 399, 328, 24, 63, 512F);
                ms.getMatrices().pop();
            }
            if (bookPages == 1) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1.5F);
                this.drawImage(ms, DRAWINGS_0, 50, 35, 423, 328, 24, 63, 512F);
                //drawImage(ms, DRAWINGS_0, 68, 60, 447, 328, 24, 63, 512F);
                ms.getMatrices().pop();

                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 50, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();

                this.drawItemStack(ms, new ItemStack(Items.LEATHER_HELMET), 180, 76, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 160, 76, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 199, 76, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 160, 57, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 180, 57, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 199, 57, 1.35F);
                this.drawItemStack(ms, new ItemStack(IafItems.SHEEP_HELMET.get()), 165, 45, 2F);

                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 144, 95, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(Items.LEATHER_CHESTPLATE), 180, 126, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 160, 126, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 199, 126, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 160, 107, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 199, 107, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 160, 145, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 180, 145, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 199, 145, 1.35F);
                this.drawItemStack(ms, new ItemStack(IafItems.SHEEP_CHESTPLATE.get()), 165, 95, 2F);
            }
            if (bookPages == 2) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1.5F);
                this.drawImage(ms, DRAWINGS_0, 185, 30, 447, 328, 24, 63, 512F);
                ms.getMatrices().pop();

                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 13, 24, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(Items.LEATHER_LEGGINGS), 34, 46, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 14, 46, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 53, 46, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 14, 27, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 34, 27, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 53, 27, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 14, 65, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 53, 65, 1.35F);

                this.drawItemStack(ms, new ItemStack(IafItems.SHEEP_LEGGINGS.get()), 64, 27, 2F);
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 13, 84, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(Items.LEATHER_BOOTS), 34, 94, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 14, 113, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 53, 113, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 14, 94, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 53, 94, 1.35F);
                this.drawItemStack(ms, new ItemStack(IafItems.SHEEP_BOOTS.get()), 64, 73, 2F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.SIREN)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.25F, 1.25F, 1.25F);
                this.drawImage(ms, DRAWINGS_1, 190, 25, 0, 0, 25, 42, 512F);
                this.drawImage(ms, DRAWINGS_1, 220, 15, 25, 0, 25, 42, 512F);
                this.drawImage(ms, DRAWINGS_1, 255, 25, 50, 0, 25, 42, 512F);

                this.drawImage(ms, DRAWINGS_1, 190, 135, 0, 42, 26, 28, 512F);
                this.drawImage(ms, DRAWINGS_1, 220, 125, 26, 42, 26, 28, 512F);
                this.drawImage(ms, DRAWINGS_1, 255, 135, 52, 42, 26, 28, 512F);
                ms.getMatrices().pop();
            }
            if (bookPages == 1) {
                this.drawItemStack(ms, new ItemStack(IafItems.EARPLUGS.get()), 18, 40, 3.75F);

                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 160, 0, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();


                this.drawItemStack(ms, new ItemStack(Blocks.OAK_BUTTON), 180, 20, 1.35F);
                this.drawItemStack(ms, new ItemStack(Blocks.OAK_BUTTON), 215, 20, 1.35F);
                this.drawItemStack(ms, new ItemStack(IafItems.EARPLUGS.get()), 170, 10, 2F);
                this.drawItemStack(ms, new ItemStack(IafItems.SHINY_SCALES.get()), 123, 75, 2.25F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.HIPPOCAMPUS)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                this.drawImage(ms, DRAWINGS_1, 210, 25, 0, 70, 57, 49, 512F);
                this.drawImage(ms, DRAWINGS_1, 265, 25, 57, 70, 57, 49, 512F);
                this.drawImage(ms, DRAWINGS_1, 320, 25, 0, 119, 57, 49, 512F);
                this.drawImage(ms, DRAWINGS_1, 210, 80, 57, 119, 57, 49, 512F);
                this.drawImage(ms, DRAWINGS_1, 265, 80, 0, 168, 57, 49, 512F);
                this.drawImage(ms, DRAWINGS_1, 320, 80, 57, 168, 57, 49, 512F);
                ms.getMatrices().pop();
            }
            if (bookPages == 1) {
                this.drawItemStack(ms, new ItemStack(ItemRandomizer.random(IafItemTags.TEMPT_HIPPOCAMPUS)), 37, 33, 2.25F);
                this.drawItemStack(ms, new ItemStack(Items.PRISMARINE_CRYSTALS), 37, 73, 2.25F);
            }
            if (bookPages == 2) {
                this.drawItemStack(ms, new ItemStack(Items.STICK), 35, 25, 2.25F);
                this.drawItemStack(ms, new ItemStack(IafItems.SHINY_SCALES.get()), 35, 75, 2.25F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.DEATHWORM)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                this.drawImage(ms, DRAWINGS_1, 230, 25, 0, 217, 133, 16, 512F);
                this.drawImage(ms, DRAWINGS_1, 230, 50, 0, 233, 133, 16, 512F);
                this.drawImage(ms, DRAWINGS_1, 230, 75, 0, 249, 133, 16, 512F);
                ms.getMatrices().pop();
            }
            if (bookPages == 1) {
                ms.getMatrices().push();
                this.drawImage(ms, DRAWINGS_1, 25, 95, 0, 265, 148, 44, 512F);
                this.drawImage(ms, DRAWINGS_1, 250, 5, 0, 309, 81, 162, 512F);
                ms.getMatrices().pop();
            }
            if (bookPages == 2) {
                int drawType = player.age % 60 > 40 ? 2 : player.age % 60 > 20 ? 1 : 0;
                Item chitin = switch (drawType) {
                    case 2 -> IafItems.DEATH_WORM_CHITIN_RED.get();
                    case 1 -> IafItems.DEATH_WORM_CHITIN_WHITE.get();
                    default -> IafItems.DEATH_WORM_CHITIN_YELLOW.get();
                };
                this.drawItemStack(ms, new ItemStack(chitin, 1), 17, 30, 3.75F);
                this.drawItemStack(ms, new ItemStack(drawType == 2 ? IafItems.DEATHWORM_RED_HELMET.get() : drawType == 1 ? IafItems.DEATHWORM_WHITE_HELMET.get() : IafItems.DEATHWORM_YELLOW_HELMET.get()), 92, 8, 2.25F);
                this.drawItemStack(ms, new ItemStack(drawType == 2 ? IafItems.DEATHWORM_RED_CHESTPLATE.get() : drawType == 1 ? IafItems.DEATHWORM_WHITE_CHESTPLATE.get() : IafItems.DEATHWORM_YELLOW_CHESTPLATE.get()), 112, 8, 2.25F);
                this.drawItemStack(ms, new ItemStack(drawType == 2 ? IafItems.DEATHWORM_RED_LEGGINGS.get() : drawType == 1 ? IafItems.DEATHWORM_WHITE_LEGGINGS.get() : IafItems.DEATHWORM_YELLOW_LEGGINGS.get()), 132, 8, 2.25F);
                this.drawItemStack(ms, new ItemStack(drawType == 2 ? IafItems.DEATHWORM_RED_BOOTS.get() : drawType == 1 ? IafItems.DEATHWORM_WHITE_BOOTS.get() : IafItems.DEATHWORM_YELLOW_BOOTS.get()), 152, 8, 2.25F);
                this.drawItemStack(ms, new ItemStack(IafItems.DEATHWORM_EGG.get()), 125, 42, 2.25F);
            }
            if (bookPages == 3) {
                this.drawItemStack(ms, new ItemStack(IafItems.DEATHWORM_EGG_GIGANTIC.get(), 1), 125, 4, 2.25F);
                this.drawItemStack(ms, new ItemStack(Items.FISHING_ROD), 115, 55, 2.25F);
                this.drawItemStack(ms, new ItemStack(Items.FISHING_ROD), 135, 55, 2.25F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.COCKATRICE)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_1, 155, 10, 114, 0, 88, 36, 512F);
                this.drawImage(ms, DRAWINGS_1, 155, 45, 114, 36, 88, 36, 512F);
                ms.getMatrices().pop();
            }
            if (bookPages == 1) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 18, 10, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();

                this.drawItemStack(ms, new ItemStack(Items.STRING), 20, 30, 1.35F);
                this.drawItemStack(ms, new ItemStack(Items.LEATHER), 40, 30, 1.35F);
                this.drawItemStack(ms, new ItemStack(Items.STRING), 59, 30, 1.35F);
                this.drawItemStack(ms, new ItemStack(IafItems.BLINDFOLD.get()), 60, 18, 2F);
                this.drawItemStack(ms, new ItemStack(IafItems.WITHERBONE.get()), 30, 58, 2.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.ROTTEN_EGG.get()), 109, 18, 2.5F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.STYMPHALIAN_BIRD)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_1, 34, 46, 114, 72, 59, 37, 512F);
                this.drawImage(ms, DRAWINGS_1, 155, 35, 114, 109, 67, 35, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(IafItems.STYMPHALIAN_BIRD_FEATHER.get()), 109, 60, 2.5F);
            }
            if (bookPages == 1) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 18, 10, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();

                this.drawItemStack(ms, new ItemStack(Items.FLINT), 40, 13, 1.35F);
                this.drawItemStack(ms, new ItemStack(Items.STICK), 40, 30, 1.35F);
                this.drawItemStack(ms, new ItemStack(IafItems.STYMPHALIAN_BIRD_FEATHER.get()), 40, 49, 1.35F);
                this.drawItemStack(ms, new ItemStack(IafItems.STYMPHALIAN_ARROW.get()), 60, 18, 2F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.TROLL)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_1, 15, 60, 156, 211, 25, 58, 512F);
                this.drawImage(ms, DRAWINGS_1, 50, 55, 181, 211, 25, 58, 512F);
                this.drawImage(ms, DRAWINGS_1, 85, 60, 206, 211, 25, 58, 512F);
                this.drawImage(ms, DRAWINGS_1, 155, 22, 114, 145, 24, 66, 512F);
                this.drawImage(ms, DRAWINGS_1, 190, 19, 188, 142, 47, 69, 512F);
                ms.getMatrices().pop();
            }
            if (bookPages == 1) {
                int i = (player.age % (TrollType.BuiltinWeapon.values().length * 20)) / 20;
                this.drawItemStack(ms, new ItemStack(TrollType.BuiltinWeapon.values()[i].getItem()), 30, 7, 2.5F);
                int j = (player.age % (TrollType.values().size() * 20)) / 20;
                this.drawItemStack(ms, new ItemStack(TrollType.values().get(j).leather.get()), 100, 30, 2.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.TROLL_TUSK.get()), 120, 30, 2.5F);
            }
            if (bookPages == 2) {
                int j = (player.age % (TrollType.values().size() * 20)) / 20;
                this.drawItemStack(ms, new ItemStack(TrollType.values().get(j).helmet.get()), 27, 15, 1.5F);
                this.drawItemStack(ms, new ItemStack(TrollType.values().get(j).chestplate.get()), 47, 15, 1.5F);
                this.drawItemStack(ms, new ItemStack(TrollType.values().get(j).leggings.get()), 67, 15, 1.5F);
                this.drawItemStack(ms, new ItemStack(TrollType.values().get(j).boots.get()), 87, 15, 1.5F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.AMPHITHERE)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                ms.getMatrices().scale(0.75F, 0.75F, 0.75F);
                this.drawImage(ms, DRAWINGS_1, 70, 97, 257, 163, 136, 93, 512F);
                this.drawImage(ms, DRAWINGS_1, 270, 50, 148, 267, 120, 51, 512F);
                this.drawImage(ms, DRAWINGS_1, 380, 50, 148, 318, 120, 51, 512F);
                this.drawImage(ms, DRAWINGS_1, 270, 100, 148, 369, 120, 51, 512F);
                this.drawImage(ms, DRAWINGS_1, 380, 100, 148, 420, 120, 51, 512F);
                this.drawImage(ms, DRAWINGS_1, 330, 150, 268, 267, 120, 51, 512F);
                ms.getMatrices().pop();
            }
            if (bookPages == 2) {
                this.drawItemStack(ms, new ItemStack(IafItems.AMPHITHERE_FEATHER.get()), 30, 20, 2.5F);
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 19, 71, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                this.drawItemStack(ms, new ItemStack(Items.FLINT), 36, 73, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.STICK), 36, 89, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.AMPHITHERE_FEATHER.get()), 36, 106, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.AMPHITHERE_ARROW.get()), 60, 65, 2F);
            }
        } else if (this.pageType.equals(IafBestiaryPages.SEA_SERPENT)) {
            if (bookPages == 0) {
                ms.getMatrices().push();
                ms.getMatrices().scale(0.75F, 0.75F, 0.75F);
                this.drawImage(ms, DRAWINGS_1, 290, 5, 422, 0, 90, 64, 512F);
                this.drawImage(ms, DRAWINGS_1, 380, 5, 422, 64, 90, 64, 512F);
                this.drawImage(ms, DRAWINGS_1, 290, 70, 422, 128, 90, 64, 512F);
                this.drawImage(ms, DRAWINGS_1, 380, 70, 422, 192, 90, 64, 512F);
                this.drawImage(ms, DRAWINGS_1, 290, 140, 422, 256, 90, 64, 512F);
                this.drawImage(ms, DRAWINGS_1, 380, 140, 422, 320, 90, 64, 512F);
                this.drawImage(ms, DRAWINGS_1, 345, 210, 422, 384, 90, 64, 512F);
                ms.getMatrices().pop();
            }
            if (bookPages == 1) {
                this.drawImage(ms, DRAWINGS_1, 60, 90, 337, 0, 70, 83, 512F);
                int j = (player.age % (SeaSerpentType.values().size() * 20)) / 20;
                this.drawItemStack(ms, new ItemStack(SeaSerpentType.values().get(j).scale.get()), 130, 40, 2.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.SERPENT_FANG.get()), 90, 40, 2.5F);
            }
            if (bookPages == 2) {
                ms.getMatrices().push();
                ms.getMatrices().scale(1.5F, 1.5F, 1F);
                this.drawImage(ms, DRAWINGS_0, 19, 31, 389, 1, 50, 50, 512F);
                ms.getMatrices().pop();
                int j = (player.age % (SeaSerpentType.values().size() * 20)) / 20;
                this.drawItemStack(ms, new ItemStack(IafItems.SERPENT_FANG.get()), 36, 32, 1.5F);
                this.drawItemStack(ms, new ItemStack(Items.STICK), 36, 48, 1.5F);
                this.drawItemStack(ms, new ItemStack(SeaSerpentType.values().get(j).scale.get()), 36, 66, 1.5F);
                this.drawItemStack(ms, new ItemStack(SeaSerpentType.values().get(j).helmet.get()), 34, 125, 1.5F);
                this.drawItemStack(ms, new ItemStack(SeaSerpentType.values().get(j).chestplate.get()), 50, 125, 1.5F);
                this.drawItemStack(ms, new ItemStack(SeaSerpentType.values().get(j).leggings.get()), 66, 125, 1.5F);
                this.drawItemStack(ms, new ItemStack(SeaSerpentType.values().get(j).boots.get()), 82, 125, 1.5F);
                this.drawItemStack(ms, new ItemStack(IafItems.SEA_SERPENT_ARROW.get()), 60, 33, 2F);
            }
        }
        this.writeFromTxt(ms);
    }

    public void imageFromTxt(DrawContext ms) {
        String fileName = this.pageType.name() + "_" + this.bookPages + ".txt";
        String languageName = MinecraftClient.getInstance().options.language.toLowerCase(Locale.ROOT);
        Identifier fileLoc = Identifier.of(IceAndFire.MOD_ID, "lang/bestiary/" + languageName + "_0/" + fileName);
        Identifier backupLoc = Identifier.of(IceAndFire.MOD_ID, "lang/bestiary/en_us_0/" + fileName);
        Optional<Resource> resource;

        resource = MinecraftClient.getInstance().getResourceManager().getResource(fileLoc);
        if (resource.isEmpty())
            resource = MinecraftClient.getInstance().getResourceManager().getResource(backupLoc);
        try {
            if (resource.isPresent()) {
                final List<String> lines = IOUtils.readLines(resource.get().getInputStream(), StandardCharsets.UTF_8);
                int zLevelAdd = 0;
                for (String line : lines) {
                    line = line.trim();
                    if (line.contains("<") || line.contains(">"))
                        if (line.contains("<image>")) {
                            line = line.substring(8, line.length() - 1);
                            String[] split = line.split(" ");
                            Identifier id = Identifier.of(IceAndFire.MOD_ID, "textures/gui/bestiary/" + split[0]);
                            Identifier resourcelocation = PICTURE_LOCATION_CACHE.computeIfAbsent(id.toString(), k -> id);
                            ms.getMatrices().push();
                            this.drawImage(ms, resourcelocation, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]), Integer.parseInt(split[6]), Float.parseFloat(split[7]) * 512F);
                            ms.getMatrices().pop();
                        }
                    if (line.contains("<item>")) {
                        line = line.substring(7, line.length() - 1);
                        String[] split = line.split(" ");
                        RenderSystem.enableDepthTest();
                        this.drawItemStack(ms, new ItemStack(getItemByRegistryName(split[0]), 1), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Float.parseFloat(split[4]) * 2F);
                    }
                    if (line.contains("<block>")) {
                        zLevelAdd += 1;
                        line = line.substring(8, line.length() - 1);
                        String[] split = line.split(" ");
                        RenderSystem.enableDepthTest();
                        this.drawBlockStack(ms, new ItemStack(getItemByRegistryName(split[0]), 1), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Float.parseFloat(split[4]) * 2F, zLevelAdd);
                    }
                    if (line.contains("<recipe>")) {
                        line = line.substring(9, line.length() - 1);
                        String[] split = line.split(" ");
                        RenderSystem.enableDepthTest();
                        float scale = Float.parseFloat(split[split.length - 1]);
                        int x = Integer.parseInt(split[split.length - 3]);
                        int y = Integer.parseInt(split[split.length - 2]);
                        ItemStack result = new ItemStack(getItemByRegistryName(split[0]), 1);
                        ItemStack[] ingredients = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
                        int j = 8;
                        for (int i = split.length - 5; i >= 2; i -= 2) {
                            ingredients[j] = new ItemStack(getItemByRegistryName(split[i]), 1);
                            j--;
                        }
                        RenderSystem.enableDepthTest();
                        ms.getMatrices().push();
                        this.drawRecipe(ms, result, ingredients, x, y, scale);
                        ms.getMatrices().pop();
                    }
                }
            }
        } catch (Exception e) {
            IceAndFire.LOGGER.error(e);
        }
    }

    private void drawRecipe(DrawContext ms, ItemStack result, ItemStack[] ingredients, int x, int y, float scale) {
        ms.getMatrices().push();
        ms.getMatrices().translate(x, y, 0.0D);
        ms.getMatrices().scale(scale, scale, scale);
        ms.getMatrices().pop();
        for (int i = 0; i < 9; i++) {
            ms.getMatrices().push();
            ms.getMatrices().translate(44, 20, 32.0D);
            ms.getMatrices().translate(x + i % 3 * 22 * scale, y + Math.floor((double) i / 3) * 22 * scale, 0.0D);
            ms.drawItem(ingredients[i], 0, 0);
            ms.getMatrices().pop();
        }
        ms.getMatrices().push();
        ms.getMatrices().translate(40, 20, 32.0D);
        float finScale = scale * 1.5F;
        ms.getMatrices().translate((x + 70.0F * finScale), (y + 10.0F * finScale), 0.0D);
        ms.getMatrices().scale(finScale, finScale, finScale);
        ms.drawItem(result, 0, 0);
        ms.getMatrices().pop();

        ms.getMatrices().push();
        ms.getMatrices().translate(x, y, 0);
        ms.getMatrices().scale(scale, scale, 0);
        ms.getMatrices().translate(37F, 13, 1F);
        ms.getMatrices().scale(1.5F, 1.5F, 1F);
        this.drawImage(ms, DRAWINGS_0, 0, 0, 389, 1, 50, 50, 512F);
        ms.getMatrices().pop();
    }

    public void writeFromTxt(DrawContext ms) {
        String fileName = this.pageType.name() + "_" + this.bookPages + ".txt";
        String languageName = MinecraftClient.getInstance().options.language.toLowerCase(Locale.ROOT);
        Identifier fileLoc = Identifier.of(IceAndFire.MOD_ID, "lang/bestiary/" + languageName + "_0/" + fileName);
        Identifier backupLoc = Identifier.of(IceAndFire.MOD_ID, "lang/bestiary/en_us_0/" + fileName);
        Optional<Resource> resource;

        resource = MinecraftClient.getInstance().getResourceManager().getResource(fileLoc);
        if (resource.isEmpty())
            resource = MinecraftClient.getInstance().getResourceManager().getResource(backupLoc);
        try {
            assert resource.isPresent();
            final List<String> lines = IOUtils.readLines(resource.get().getInputStream(), "UTF-8");
            int linenumber = 0;
            for (String line : lines) {
                line = line.trim();
                if (line.contains("<") || line.contains(">")) continue;
                ms.getMatrices().push();
                if (this.usingVanillaFont()) {
                    ms.getMatrices().scale(0.945F, 0.945F, 0.945F);
                    ms.getMatrices().translate(0, 5.5F, 0);
                }
                if (linenumber <= 19)
                    this.textRenderer.draw(line, 15, 20 + linenumber * 10, 0X303030, false, ms.getMatrices().peek().getPositionMatrix(), ms.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
                else
                    this.textRenderer.draw(line, 220, (linenumber - 19) * 10, 0X303030, false, ms.getMatrices().peek().getPositionMatrix(), ms.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
                linenumber++;
                ms.getMatrices().pop();
            }
        } catch (Exception e) {
            IceAndFire.LOGGER.error(e);
        }
        ms.getMatrices().push();
        String s = I18n.translate("bestiary." + this.pageType.name());
        float scale = this.textRenderer.getWidth(s) <= 100 ? 2 : this.textRenderer.getWidth(s) * 0.0125F;
        ms.getMatrices().scale(scale, scale, scale);
        this.textRenderer.draw(s, 10, 2, 0x7a756a, false, ms.getMatrices().peek().getPositionMatrix(), ms.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        ms.getMatrices().pop();
    }

    private boolean usingVanillaFont() {
        return this.textRenderer == MinecraftClient.getInstance().textRenderer;
    }

    public void drawImage(DrawContext ms, Identifier texture, int x, int y, int u, int v, int width, int height, float scale) {
        ms.getMatrices().push();
        RenderSystem.setShaderTexture(0, texture);
        ms.getMatrices().scale(scale / 512F, scale / 512F, scale / 512F);
        ms.drawTexture(texture, x, y, u, v, width, height, 512, 512);
        ms.getMatrices().pop();
    }

    private void drawItemStack(DrawContext ms, ItemStack stack, int x, int y, float scale) {
        ms.getMatrices().push();
        ms.getMatrices().scale(scale, scale, scale);
        ms.drawItem(stack, x, y);
        ms.getMatrices().pop();
    }

    private void drawBlockStack(DrawContext ms, ItemStack stack, int x, int y, float scale, int zScale) {
        ms.getMatrices().push();
        ms.getMatrices().scale(scale, scale, scale);
        ms.getMatrices().translate(0, 0, zScale * 10);
        ms.drawItem(stack, x, y);
        ms.getMatrices().pop();
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        //Remove texts.
    }
}
