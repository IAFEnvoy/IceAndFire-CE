package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.item.DragonEggItem;
import com.iafenvoy.iceandfire.item.block.entity.PodiumBlockEntity;
import com.iafenvoy.iceandfire.render.model.DragonEggModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer.ModelPose;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class PodiumBlockEntityRenderer<T extends PodiumBlockEntity> implements BlockEntityRenderer<T, PodiumBlockEntityRenderer.State> {
    private final DragonEggModel model = new DragonEggModel();

    public static class State extends BlockEntityRenderState {
        private PodiumBlockEntity entity;
        private float partialTicks;
        private final ItemStackRenderState item = new ItemStackRenderState();
    }

    private final ItemModelResolver itemResolver;

    public PodiumBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemResolver = context.itemModelResolver();
    }

    protected static RenderType getEggTexture(DragonColor type) {
        return RenderTypes.entityCutout(type.getTextureProvider().getEggTexture());
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(T entity, State state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPosition, breakProgress);
        state.entity = entity;
        state.partialTicks = partialTicks;
        this.itemResolver.updateForTopItem(state.item, entity.getItem(0), ItemDisplayContext.GROUND,
                entity.getLevel(), null, entity.getBlockPos().hashCode());
    }

    @Override
    public void submit(State state, @NonNull PoseStack matrixStackIn, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        T entity = (T) state.entity;
        DragonEggModel model = this.model;
        if (!entity.getItem(0).isEmpty()) {
            if (entity.getItem(0).getItem() instanceof DragonEggItem item) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.5F, 0.475F, 0.5F);
                matrixStackIn.pushPose();
                matrixStackIn.pushPose();
                model.resetToDefaultPose();
                model.renderPodium();
                ModelPose.submit(submitNodeCollector, matrixStackIn, getEggTexture(item.type), model, (pose, buffer) -> {
                    PoseStack modelStack = new PoseStack();
                    modelStack.last().set(pose);
                    model.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
                });
                matrixStackIn.popPose();
                matrixStackIn.popPose();
                matrixStackIn.popPose();
            } else if (!entity.getItem(0).isEmpty()) {
                matrixStackIn.pushPose();
                float f2 = ((float) entity.prevTicksExisted + (entity.ticksExisted - entity.prevTicksExisted) * state.partialTicks);
                float f3 = Mth.sin(f2 / 10.0F) * 0.1F + 0.1F;
                matrixStackIn.translate(0.5F, 1.55F + f3, 0.5F);
                float f4 = (f2 / 20.0F);
                matrixStackIn.mulPose(Axis.YP.rotation(f4));
                matrixStackIn.pushPose();
                matrixStackIn.translate(0, 0.2F, 0);
                matrixStackIn.scale(0.65F, 0.65F, 0.65F);
                state.item.submit(matrixStackIn, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                matrixStackIn.popPose();
                matrixStackIn.popPose();
            }
        }
    }
}
