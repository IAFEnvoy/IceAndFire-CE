package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.entity.PixieEntity;
import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.PixieEntityRenderer;
import com.iafenvoy.iceandfire.render.model.PixieModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class PixieGlowFeatureRenderer implements LegacyEntityFeature<PixieEntity> {
    private final PixieModel model;

    public PixieGlowFeatureRenderer(PixieEntityRenderer renderIn) {
        this.model = renderIn.getLegacyModel();
    }

    @Override
    public void submit(PixieEntity pixie, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int light, int outlineColor) {
        Identifier texture = switch (pixie.getColor()) {
            case 1 -> PixieEntityRenderer.TEXTURE_1;
            case 2 -> PixieEntityRenderer.TEXTURE_2;
            case 3 -> PixieEntityRenderer.TEXTURE_3;
            case 4 -> PixieEntityRenderer.TEXTURE_4;
            case 5 -> PixieEntityRenderer.TEXTURE_5;
            default -> PixieEntityRenderer.TEXTURE_0;
        };
        collector.submitCustomGeometry(poseStack, RenderTypes.eyes(texture), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            this.model.renderToBuffer(modelStack, buffer, light, OverlayTexture.NO_OVERLAY, outlineColor == 0 ? -1 : outlineColor);
        });
    }
}
