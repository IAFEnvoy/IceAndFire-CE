package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.entity.GorgonEntity;
import com.iafenvoy.iceandfire.entity.TrollEntity;
import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.TrollEntityRenderer;
import com.iafenvoy.iceandfire.render.model.TrollModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class TrollEyesFeatureRenderer implements LegacyEntityFeature<TrollEntity> {
    private final TrollModel model;

    public TrollEyesFeatureRenderer(TrollEntityRenderer renderer) {
        this.model = renderer.getLegacyModel();
    }

    @Override
    public void submit(TrollEntity troll, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int lightCoords, int outlineColor) {
        if (!GorgonEntity.isStoneMob(troll)) {
            collector.submitCustomGeometry(poseStack, RenderTypes.eyes(troll.getTrollType().getEyesTexture()), (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                this.model.renderToBuffer(modelStack, buffer, lightCoords, OverlayTexture.NO_OVERLAY, -1);
            });
        }
    }
}
