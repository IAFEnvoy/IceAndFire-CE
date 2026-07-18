package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.MobSkullEntity;
import com.iafenvoy.iceandfire.render.model.SeaSerpentGeoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** GeckoLib replacement for the sea-serpent mob skull. */
public class SeaSerpentSkullEntityRenderer extends GeoEntityRenderer<MobSkullEntity> {
    public SeaSerpentSkullEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SeaSerpentGeoModel<>(MobSkullEntityRenderer::getSkullTexture));
    }

    @Override
    protected void applyRotations(MobSkullEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
    }

    @Override
    public void preRender(PoseStack poseStack, MobSkullEntity skull, BakedGeoModel model, MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        poseStack.mulPose(Axis.XP.rotationDegrees(-180.0F));
        poseStack.mulPose(Axis.YN.rotationDegrees(180.0F - skull.getYRot()));
        poseStack.translate(0, skull.isOnWall() ? -0.59F : -0.47F, 1.3F);
        poseStack.scale(2.5F, 2.5F, 2.5F);
        super.preRender(poseStack, skull, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
        if (bone.getName().equals("Head")) super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, renderColor);
    }

    @Override
    public ResourceLocation getTextureLocation(MobSkullEntity skull) {
        return MobSkullEntityRenderer.getSkullTexture(skull);
    }
}
