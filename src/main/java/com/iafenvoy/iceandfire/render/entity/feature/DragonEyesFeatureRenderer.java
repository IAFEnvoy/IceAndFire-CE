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

public class DragonEyesFeatureRenderer<T extends DragonBaseEntity> implements LegacyEntityFeature<T> {
    private final TabulaModel<T> model;

    public DragonEyesFeatureRenderer(LegacyMobRenderer<T, TabulaModel<T>> renderer) {
        this.model = renderer.getLegacyModel();
    }

    @Override
    public void submit(T entity, float partialTick, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState camera, int light, int outlineColor) {
        if (!entity.shouldRenderEyes()) return;
        Identifier eyeTexture = DragonColor.getById(entity.getVariant()).getTextureProvider().getEyesTexture(entity.getDragonStage());
        if (eyeTexture == null) return;
        collector.submitCustomGeometry(matrices, RenderTypes.eyes(eyeTexture), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            this.model.renderToBuffer(modelStack, buffer, light, OverlayTexture.NO_OVERLAY, -1);
        });
    }
}
