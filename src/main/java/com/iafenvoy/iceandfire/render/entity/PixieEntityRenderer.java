package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.PixieEntity;
import com.iafenvoy.iceandfire.render.entity.feature.PixieGlowFeatureRenderer;
import com.iafenvoy.iceandfire.render.entity.feature.PixieItemFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.PixieModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PixieEntityRenderer extends MobRenderer<PixieEntity, PixieModel> {
    public static final ResourceLocation TEXTURE_0 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/pixie_0.png");
    public static final ResourceLocation TEXTURE_1 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/pixie_1.png");
    public static final ResourceLocation TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/pixie_2.png");
    public static final ResourceLocation TEXTURE_3 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/pixie_3.png");
    public static final ResourceLocation TEXTURE_4 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/pixie_4.png");
    public static final ResourceLocation TEXTURE_5 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/pixie_5.png");

    public PixieEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PixieModel(), 0.2F);
        this.layers.add(new PixieItemFeatureRenderer(this));
        this.layers.add(new PixieGlowFeatureRenderer(this));
    }

    @Override
    public void scale(PixieEntity LivingEntityIn, PoseStack stack, float partialTickTime) {
        stack.scale(0.55F, 0.55F, 0.55F);
        if (LivingEntityIn.isOrderedToSit()) {
            stack.translate(0F, 0.5F, 0F);

        }
    }

    @Override
    public ResourceLocation getTextureLocation(PixieEntity pixie) {
        return switch (pixie.getColor()) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            case 3 -> TEXTURE_3;
            case 4 -> TEXTURE_4;
            case 5 -> TEXTURE_5;
            default -> TEXTURE_0;
        };
    }
}
