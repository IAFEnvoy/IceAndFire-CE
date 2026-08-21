package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class DragonMaleOverlayFeatureRenderer<T extends DragonBaseEntity> implements LegacyEntityFeature<T> {
    private final TabulaModel<T> model;

    public DragonMaleOverlayFeatureRenderer(LegacyMobRenderer<T, TabulaModel<T>> renderer) {
        this.model = renderer.getLegacyModel();
    }

    @Override
    public void submit(T dragon, float partialTick, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState camera, int packedLightIn, int outlineColor) {
        Identifier texture = DragonColor.getById(dragon.getVariant()).getTextureProvider().getMaleOverlay();
        if (dragon.isMale() && !dragon.isSkeletal() && texture != null)
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucent(texture), (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                this.model.renderToBuffer(modelStack, buffer, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            });
    }
}
