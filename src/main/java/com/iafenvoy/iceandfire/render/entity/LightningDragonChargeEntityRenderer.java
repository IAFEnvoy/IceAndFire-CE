package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.LightningDragonChargeEntity;
import com.iafenvoy.iceandfire.render.model.DreadLichSkullModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class LightningDragonChargeEntityRenderer extends EntityRenderer<LightningDragonChargeEntity, LegacyEntityRenderState<LightningDragonChargeEntity>> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/lightningdragon/charge.png");
    public static final Identifier TEXTURE_CORE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/lightningdragon/charge_core.png");
    private static final DreadLichSkullModel MODEL_SPIRIT = new DreadLichSkullModel();

    public LightningDragonChargeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public LegacyEntityRenderState<LightningDragonChargeEntity> createRenderState() {
        return new LegacyEntityRenderState<>();
    }

    @Override
    public void extractRenderState(LightningDragonChargeEntity entity, LegacyEntityRenderState<LightningDragonChargeEntity> state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.entity = entity;
    }

    @Override
    public void submit(LegacyEntityRenderState<LightningDragonChargeEntity> state, PoseStack matrixStackIn, SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        LightningDragonChargeEntity entity = state.entity;
        float f = state.ageInTicks;
        float yaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * state.partialTick;

        matrixStackIn.pushPose();
        matrixStackIn.translate(0F, 0.5F, 0F);
        matrixStackIn.translate(0F, -0.25F, 0F);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw - 180.0F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(f * 20.0F));
        matrixStackIn.translate(0F, 0.25F, 0F);
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(TEXTURE_CORE), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            MODEL_SPIRIT.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
        });
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0F, 0.5F, 0F);
        matrixStackIn.translate(0F, -0.25F, 0F);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw - 180.0F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(f * 15.0F));
        matrixStackIn.translate(0F, 0.25F, 0F);
        matrixStackIn.scale(1.5F, 1.5F, 1.5F);
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.energySwirl(TEXTURE, f * 0.01F, f * 0.01F), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            MODEL_SPIRIT.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
        });
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0F, 0.75F, 0F);
        matrixStackIn.translate(0F, -0.25F, 0F);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw - 180.0F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(f * 10.0F));
        matrixStackIn.translate(0F, 0.75F, 0F);
        matrixStackIn.scale(2.5F, 2.5F, 2.5F);
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.energySwirl(TEXTURE, f * 0.01F, f * 0.01F), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            MODEL_SPIRIT.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
        });
        matrixStackIn.popPose();

        super.submit(state, matrixStackIn, collector, camera);
    }

    public @NotNull Identifier getTextureLocation(@NotNull LightningDragonChargeEntity entity) {
        return TEXTURE;
    }
}
