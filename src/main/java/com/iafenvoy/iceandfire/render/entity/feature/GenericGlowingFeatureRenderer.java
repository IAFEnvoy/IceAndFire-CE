package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

public class GenericGlowingFeatureRenderer<T extends Mob, M extends AdvancedEntityModel<T>> implements LegacyEntityFeature<T> {
    private final M model;
    private final Identifier texture;

    public GenericGlowingFeatureRenderer(LegacyMobRenderer<T, M> renderer, Identifier texture) {
        this.model = renderer.getLegacyModel();
        this.texture = texture;
    }

    @Override
    public void submit(T entity, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int lightCoords, int outlineColor) {
        collector.submitCustomGeometry(poseStack, RenderTypes.eyes(this.texture), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            this.model.renderToBuffer(modelStack, buffer, lightCoords, OverlayTexture.NO_OVERLAY, -1);
        });
    }
}
