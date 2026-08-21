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

public class TrollWeaponFeatureRenderer implements LegacyEntityFeature<TrollEntity> {
    private final TrollModel model;

    public TrollWeaponFeatureRenderer(TrollEntityRenderer renderer) {
        this.model = renderer.getLegacyModel();
    }

    @Override
    public void submit(TrollEntity troll, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int light, int outlineColor) {
        if (troll.getWeaponType() != null && !GorgonEntity.isStoneMob(troll)) {
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(troll.getWeaponType().getTexture()), (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                this.model.renderToBuffer(modelStack, buffer, light, OverlayTexture.NO_OVERLAY, outlineColor == 0 ? -1 : outlineColor);
            });
        }
    }
}
