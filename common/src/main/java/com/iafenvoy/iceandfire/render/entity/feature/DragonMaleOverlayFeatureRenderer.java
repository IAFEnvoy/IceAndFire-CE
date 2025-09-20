package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.uranus.client.model.TabulaModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class DragonMaleOverlayFeatureRenderer<T extends DragonBaseEntity> extends FeatureRenderer<T, TabulaModel<T>> {
    public DragonMaleOverlayFeatureRenderer(MobEntityRenderer<T, TabulaModel<T>> renderIn) {
        super(renderIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, T dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (dragon.isMale() && !dragon.isSkeletal()) {
            Identifier texture = this.getTexture(dragon);
            if (texture != null) {
                this.getContextModel().render(matrixStackIn, bufferIn.getBuffer(RenderLayer.getEntityTranslucent(texture)), packedLightIn, OverlayTexture.DEFAULT_UV, -1);
            }
        }
    }

    @Override
    protected Identifier getTexture(T dragon) {
        return DragonColor.getById(dragon.getVariant()).getMaleOverlay();
    }
}