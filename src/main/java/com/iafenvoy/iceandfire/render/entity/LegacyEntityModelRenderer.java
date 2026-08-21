package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;

/** EntityRenderer bridge for Uranus models that animate from a live non-Mob entity. */
public abstract class LegacyEntityModelRenderer<T extends Entity, M extends AdvancedEntityModel<T>> extends EntityRenderer<T, LegacyEntityRenderState<T>> {
    protected final M model;

    protected LegacyEntityModelRenderer(EntityRendererProvider.Context context, M model, float shadowRadius) {
        super(context);
        this.model = model;
        this.shadowRadius = shadowRadius;
    }

    public abstract Identifier getTextureLocation(T entity);

    @Override
    public LegacyEntityRenderState<T> createRenderState() {
        return new LegacyEntityRenderState<>();
    }

    @Override
    public void extractRenderState(T entity, LegacyEntityRenderState<T> state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.entity = entity;
    }

    @Override
    public void submit(LegacyEntityRenderState<T> state, PoseStack poseStack, SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        T entity = state.entity;
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.model.setupAnim(entity, 0.0F, 0.0F, state.ageInTicks, 0.0F, 0.0F);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(this.getTextureLocation(entity)), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            this.model.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor == 0 ? -1 : state.outlineColor);
        });
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
