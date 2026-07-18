package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/** Attaches dragon passengers and banners to the animated upper-body bone. */
public class DragonGeoAttachmentLayer<T extends DragonBaseEntity> extends GeoRenderLayer<T> {
    public DragonGeoAttachmentLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void renderForBone(PoseStack poseStack, T dragon, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!bone.getName().equals("BodyUpper")) return;
        this.renderBanner(poseStack, dragon, bufferSource, packedLight);
        this.renderPassengers(poseStack, dragon, bufferSource, packedLight, partialTick);
    }

    private void renderBanner(PoseStack poseStack, T dragon, MultiBufferSource bufferSource, int packedLight) {
        ItemStack banner = dragon.getItemInHand(InteractionHand.OFF_HAND);
        if (!(banner.getItem() instanceof BannerItem)) return;
        float dragonScale = dragon.getRenderSize() / 3F;
        poseStack.pushPose();
        poseStack.translate(0, -0.2F, 0.4F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.scale(1F / dragonScale, 1F / dragonScale, 1F / dragonScale);
        Minecraft.getInstance().getItemRenderer().renderStatic(banner, ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, Minecraft.getInstance().level, 0);
        poseStack.popPose();
    }

    private void renderPassengers(PoseStack poseStack, T dragon, MultiBufferSource bufferSource, int packedLight, float partialTick) {
        float dragonScale = dragon.getRenderSize() / 3F;
        for (Entity passenger : dragon.getPassengers()) {
            poseStack.pushPose();
            boolean prey = dragon.getControllingPassenger() == null || dragon.getControllingPassenger().getId() != passenger.getId();
            poseStack.translate(0, prey ? -0.12F : -0.01F, prey ? -0.55F : -0.04F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            float riderYaw = passenger.yRotO + (passenger.getYRot() - passenger.yRotO) * partialTick;
            poseStack.mulPose(Axis.YP.rotationDegrees(riderYaw + 180.0F));
            poseStack.scale(1F / dragonScale, 1F / dragonScale, 1F / dragonScale);
            DragonRiderRenderState.RENDERING_RIDERS.add(passenger);
            Minecraft.getInstance().getEntityRenderDispatcher().render(passenger, 0, 0, 0, 0.0F, partialTick, poseStack, bufferSource, packedLight);
            DragonRiderRenderState.RENDERING_RIDERS.remove(passenger);
            poseStack.popPose();
        }
    }
}
