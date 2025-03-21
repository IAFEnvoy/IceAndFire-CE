package com.iafenvoy.iceandfire.render.entity.layer;

import com.iafenvoy.iceandfire.entity.EntityGorgon;
import com.iafenvoy.iceandfire.entity.EntityTroll;
import com.iafenvoy.iceandfire.render.entity.RenderTroll;
import com.iafenvoy.iceandfire.render.model.ModelTroll;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class LayerTrollEyes extends FeatureRenderer<EntityTroll, ModelTroll> {
    public LayerTrollEyes(RenderTroll renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, EntityTroll troll, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!EntityGorgon.isStoneMob(troll)) {
            RenderLayer tex = RenderLayer.getEyes(troll.getTrollType().getEyesTexture());
            VertexConsumer vertexConsumer = bufferIn.getBuffer(tex);
            this.getContextModel().render(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
        }
    }
}
