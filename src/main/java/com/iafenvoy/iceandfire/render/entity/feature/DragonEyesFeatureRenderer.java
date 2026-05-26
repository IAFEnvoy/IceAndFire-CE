package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DragonEyesFeatureRenderer<T extends DragonBaseEntity> extends RenderLayer<T, TabulaModel<T>> {
    public DragonEyesFeatureRenderer(MobRenderer<T, TabulaModel<T>> renderIn) {
        super(renderIn);
    }

    @Override
    public void render(@NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light, DragonBaseEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headpitch) {
        if (!entity.shouldRenderEyes()) return;
        ResourceLocation eyeTexture = DragonColor.getById(entity.getVariant()).getTextureProvider().getEyesTexture(entity.getDragonStage());
        if (eyeTexture == null) return;
        this.getParentModel().renderToBuffer(matrices, vertexConsumers.getBuffer(RenderType.eyes(eyeTexture)), light, OverlayTexture.NO_OVERLAY, -1);
    }
}