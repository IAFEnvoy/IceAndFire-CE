package com.iafenvoy.iceandfire.screen;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.uranus.util.RandomHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TitleScreenRenderManager {
    public static final ResourceLocation splash = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "splashes.txt");
    public static final ResourceLocation[] pageFlipTextures;
    public static final ResourceLocation[] drawingTextures = new ResourceLocation[23];
    private static final ResourceLocation BESTIARY_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/main_menu/bestiary_menu.png");
    private static final ResourceLocation TABLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/main_menu/table.png");
    private static final Font textRenderer = Minecraft.getInstance().font;
    private static int layerTick;
    private static List<String> splashText;
    private static boolean isFlippingPage = false;
    private static int pageFlip = 0;
    private static Picture[] drawnPictures;
    private static float globalAlpha = 1F;

    static {
        pageFlipTextures = new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/main_menu/page_1.png"),
                ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/main_menu/page_2.png"),
                ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/main_menu/page_3.png"),
                ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/main_menu/page_4.png"),
                ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/main_menu/page_5.png"),
                ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/main_menu/page_6.png")};
        for (int i = 0; i < drawingTextures.length; i++)
            drawingTextures[i] = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/gui/main_menu/drawing_" + i + ".png");
        resetDrawnImages();
    }

    public static SplashRenderer getSplash() {
        if (splashText == null)
            try {
                BufferedReader bufferedReader = Minecraft.getInstance().getResourceManager().openAsReader(splash);
                splashText = bufferedReader.lines().map(String::trim).filter((splashText) -> splashText.hashCode() != 125780783).toList();
                bufferedReader.close();
            } catch (IOException var8) {
                splashText = new ArrayList<>();
            }
        if (splashText.isEmpty()) return null;
        return new SplashRenderer(splashText.get(RandomHelper.nextInt(0, splashText.size() - 1)));
    }

    private static void resetDrawnImages() {
        globalAlpha = 0;
        Random random = ThreadLocalRandom.current();
        drawnPictures = new Picture[2];
        boolean left = random.nextBoolean();
        for (int i = 0; i < drawnPictures.length; i++) {
            left = !left;
            int x = left ? -15 - random.nextInt(20) - 128 : 30 + random.nextInt(20);
            int y = random.nextInt(25);
            drawnPictures[i] = new Picture(random.nextInt(drawingTextures.length), x, y, 0.5F, random.nextFloat() * 0.5F + 0.5F);
        }
    }

    public static void tick() {
        float flipTick = layerTick % 40;
        if (globalAlpha < 1 && !isFlippingPage && flipTick < 30)
            globalAlpha += 0.1F;
        if (globalAlpha > 0 && flipTick > 30)
            globalAlpha -= 0.1F;
        if (flipTick == 0 && !isFlippingPage)
            isFlippingPage = true;
        if (isFlippingPage) {
            if (layerTick % 2 == 0)
                pageFlip++;
            if (pageFlip == 6) {
                pageFlip = 0;
                isFlippingPage = false;
                resetDrawnImages();
            }
        }
        layerTick++;
    }

    public static void renderBackground(GuiGraphics ms, int width, int height) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        ms.blit(TABLE_TEXTURE, 0, 0, 0, 0, width, height, width, height);
        ms.blit(BESTIARY_TEXTURE, 50, 0, 0, 0, width - 100, height, width - 100, height);
        if (isFlippingPage)
            ms.blit(pageFlipTextures[Math.min(5, pageFlip)], 50, 0, 0, 0, width - 100, height, width - 100, height);
        else {
            int middleX = width / 2;
            int middleY = height / 5;
            float widthScale = width / 427F;
            float heightScale = height / 427F;
            float imageScale = Math.min(widthScale, heightScale) * 192;
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, globalAlpha);
            for (Picture picture : drawnPictures) {
                int x = (int) (picture.x * widthScale) + middleX;
                int y = (int) ((picture.y * heightScale) + middleY);
                ms.blit(drawingTextures[picture.image], x, y, 0, 0, (int) imageScale, (int) imageScale, (int) imageScale, (int) imageScale);
            }
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
    }

    public static void drawModName(GuiGraphics ms, int width, int height, int alphaFormatted) {
        int textColor = 0x00FFFFFF | alphaFormatted;
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        ms.drawString(textRenderer, "Ice and Fire CE-" + ChatFormatting.GOLD + IceAndFire.VERSION, 2, height - 30, textColor, false);
    }

    private static class Picture {
        final int image;
        final int x;
        final int y;
        final float alpha;

        public Picture(int image, int x, int y, float alpha, float scale) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.alpha = alpha;
        }
    }
}

