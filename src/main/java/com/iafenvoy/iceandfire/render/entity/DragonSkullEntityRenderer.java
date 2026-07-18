package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.DragonSkullEntity;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonSize;
import com.iafenvoy.iceandfire.render.model.DragonSkullGeoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** Draws only the dragon head while retaining its GeckoLib bone transforms. */
public class DragonSkullEntityRenderer extends GeoEntityRenderer<DragonSkullEntity> {
    public DragonSkullEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DragonSkullGeoModel());
    }

    @Override
    protected void applyRotations(DragonSkullEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
    }

    @Override
    public void preRender(PoseStack poseStack, DragonSkullEntity skull, software.bernie.geckolib.cache.object.BakedGeoModel model, MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        poseStack.mulPose(Axis.XP.rotationDegrees(-180.0F));
        poseStack.mulPose(Axis.YN.rotationDegrees(-180.0F - skull.getYRot()));
        float size = this.getRenderSize(skull) / 3;
        poseStack.scale(size, size, size);
        poseStack.translate(0, skull.isOnWall() ? -0.24F : -0.12F, skull.isOnWall() ? 0.4F : 0.5F);
        super.preRender(poseStack, skull, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
        if (bone.getName().equals("Head")) super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, renderColor);
    }

    @Override
    public ResourceLocation getTextureLocation(DragonSkullEntity skull) {
        return this.getGeoModel().getTextureResource(skull);
    }

    private float getRenderSize(DragonSkullEntity skull) {
        DragonSize size = DragonSize.getSize(skull.getDragonStage());
        float step = size.step() / 25;
        return skull.getDragonAge() > 125 ? size.x0() + step * 25 : size.x0() + step * this.getAgeFactor(skull);
    }

    private int getAgeFactor(DragonSkullEntity skull) {
        return skull.getDragonStage() > 1 ? skull.getDragonAge() - 25 * (skull.getDragonStage() - 1) : skull.getDragonAge();
    }
}
