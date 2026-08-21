package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.GorgonEntity;
import com.iafenvoy.iceandfire.render.entity.GorgonEntityRenderer;
import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.model.GorgonModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class GorgonEyesFeatureRenderer implements LegacyEntityFeature<GorgonEntity> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/gorgon/gorgon_eyes.png");
    private final GorgonModel model;

    public GorgonEyesFeatureRenderer(GorgonEntityRenderer renderIn) {
        this.model = renderIn.getLegacyModel();
    }

    @Override
    public void submit(GorgonEntity entity, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int lightCoords, int outlineColor) {
        if (entity.getAnimation() == GorgonEntity.ANIMATION_SCARE || entity.getAnimation() == GorgonEntity.ANIMATION_HIT) {
            collector.submitCustomGeometry(poseStack, RenderTypes.eyes(TEXTURE), (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                this.model.renderToBuffer(modelStack, buffer, lightCoords, OverlayTexture.NO_OVERLAY, -1);
            });
        }
    }
}
