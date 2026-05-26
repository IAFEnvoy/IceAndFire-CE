package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.LightningDragonChargeEntity;
import com.iafenvoy.iceandfire.render.model.DreadLichSkullModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LightningDragonChargeEntityRenderer extends EntityRenderer<LightningDragonChargeEntity> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/lightningdragon/charge.png");
    public static final ResourceLocation TEXTURE_CORE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/lightningdragon/charge_core.png");
    private static final DreadLichSkullModel MODEL_SPIRIT = new DreadLichSkullModel();

    public LightningDragonChargeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(LightningDragonChargeEntity entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        float f = (float) entity.tickCount + partialTicks;
        float yaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTicks;

        VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(RenderType.eyes(TEXTURE_CORE));
        matrixStackIn.pushPose();
        matrixStackIn.translate(0F, 0.5F, 0F);
        matrixStackIn.translate(0F, -0.25F, 0F);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw - 180.0F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(f * 20.0F));
        matrixStackIn.translate(0F, 0.25F, 0F);
        MODEL_SPIRIT.renderToBuffer(matrixStackIn, ivertexbuilder2, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
        matrixStackIn.popPose();

        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.energySwirl(TEXTURE, f * 0.01F, f * 0.01F));
        matrixStackIn.pushPose();
        matrixStackIn.translate(0F, 0.5F, 0F);
        matrixStackIn.translate(0F, -0.25F, 0F);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw - 180.0F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(f * 15.0F));
        matrixStackIn.translate(0F, 0.25F, 0F);
        matrixStackIn.scale(1.5F, 1.5F, 1.5F);
        MODEL_SPIRIT.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0F, 0.75F, 0F);
        matrixStackIn.translate(0F, -0.25F, 0F);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw - 180.0F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(f * 10.0F));
        matrixStackIn.translate(0F, 0.75F, 0F);
        matrixStackIn.scale(2.5F, 2.5F, 2.5F);
        MODEL_SPIRIT.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
        matrixStackIn.popPose();

        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LightningDragonChargeEntity entity) {
        return TEXTURE;
    }
}
