package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.item.block.entity.EggInIceBlockEntity;
import com.iafenvoy.iceandfire.render.model.DragonEggModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class EggInIceBlockEntityRenderer<T extends EggInIceBlockEntity> implements BlockEntityRenderer<T, EggInIceBlockEntityRenderer.State> {
    public static class State extends BlockEntityRenderState {
        private EggInIceBlockEntity entity;
    }

    public EggInIceBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(T egg, State state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(egg, state, partialTicks, cameraPosition, breakProgress);
        state.entity = egg;
    }

    @Override
    public void submit(State state, @NonNull PoseStack matrixStackIn, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        T egg = (T) state.entity;
        DragonEggModel model = new DragonEggModel();
        if (egg.type != null) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5, -0.8F, 0.5F);
            matrixStackIn.pushPose();
            model.renderFrozen(egg);
            submitNodeCollector.submitCustomGeometry(matrixStackIn, PodiumBlockEntityRenderer.getEggTexture(egg.type), (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                model.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
            });
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
    }
}
