package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.DragonSkullEntity;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonSize;
import com.iafenvoy.iceandfire.render.model.DragonSkullGeoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** Draws only the dragon head while retaining its GeckoLib bone transforms. */
public class DragonSkullEntityRenderer extends GeoEntityRenderer<DragonSkullEntity> {
    private static final float VERTICAL_OFFSET = 0.15F;

    public DragonSkullEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DragonSkullGeoModel());
    }

    @Override
    public void preRender(PoseStack poseStack, DragonSkullEntity skull, BakedGeoModel model, MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        float size = this.getRenderSize(skull) / 3;
        poseStack.scale(size, size, size);
        super.preRender(poseStack, skull, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, DragonSkullEntity skull, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        model.getBone("Head").ifPresent(head -> {
            head.updateRotation(skull.isOnWall() ? (float) Math.toRadians(-50.0D) : 0.0F, 0.0F, 0.0F);
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-skull.getYRot()));
            poseStack.translate(-head.getPivotX() / 16.0F, -head.getPivotY() / 16.0F, -head.getPivotZ() / 16.0F);
            poseStack.translate(0.0F, VERTICAL_OFFSET, 0.0F);
            this.renderRecursively(poseStack, skull, head, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
            poseStack.popPose();
        });
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
