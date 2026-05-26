package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.entity.PixieEntity;
import com.iafenvoy.iceandfire.render.entity.PixieEntityRenderer;
import com.iafenvoy.iceandfire.render.model.PixieModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PixieGlowFeatureRenderer extends RenderLayer<PixieEntity, PixieModel> {
    public PixieGlowFeatureRenderer(PixieEntityRenderer renderIn) {
        super(renderIn);
    }

    @Override
    public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, PixieEntity pixie, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation texture = switch (pixie.getColor()) {
            case 1 -> PixieEntityRenderer.TEXTURE_1;
            case 2 -> PixieEntityRenderer.TEXTURE_2;
            case 3 -> PixieEntityRenderer.TEXTURE_3;
            case 4 -> PixieEntityRenderer.TEXTURE_4;
            case 5 -> PixieEntityRenderer.TEXTURE_5;
            default -> PixieEntityRenderer.TEXTURE_0;
        };
        RenderType eyes = RenderType.eyes(texture);
        VertexConsumer vertexConsumer = bufferIn.getBuffer(eyes);
        this.getParentModel().renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
    }
}