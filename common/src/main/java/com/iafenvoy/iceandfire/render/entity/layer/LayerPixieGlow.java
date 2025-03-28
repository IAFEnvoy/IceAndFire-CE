package com.iafenvoy.iceandfire.render.entity.layer;

import com.iafenvoy.iceandfire.entity.EntityPixie;
import com.iafenvoy.iceandfire.render.entity.RenderPixie;
import com.iafenvoy.iceandfire.render.model.ModelPixie;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class LayerPixieGlow extends FeatureRenderer<EntityPixie, ModelPixie> {
    public LayerPixieGlow(RenderPixie renderIn) {
        super(renderIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, EntityPixie pixie, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Identifier texture = switch (pixie.getColor()) {
            case 1 -> RenderPixie.TEXTURE_1;
            case 2 -> RenderPixie.TEXTURE_2;
            case 3 -> RenderPixie.TEXTURE_3;
            case 4 -> RenderPixie.TEXTURE_4;
            case 5 -> RenderPixie.TEXTURE_5;
            default -> RenderPixie.TEXTURE_0;
        };
        RenderLayer eyes = RenderLayer.getEyes(texture);
        VertexConsumer vertexConsumer = bufferIn.getBuffer(eyes);
        this.getContextModel().render(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
    }
}