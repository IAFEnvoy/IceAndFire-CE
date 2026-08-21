package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.entity.PixieEntity;
import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.PixieEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PixieItemFeatureRenderer implements LegacyEntityFeature<PixieEntity> {
    public PixieItemFeatureRenderer(PixieEntityRenderer renderer) {
    }

    @Override
    public void submit(PixieEntity entity, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int light, int outlineColor) {
        ItemStack itemstack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (!itemstack.isEmpty()) {
            ItemStackRenderState itemState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForLiving(itemState, itemstack, ItemDisplayContext.FIXED, entity);
            poseStack.pushPose();
            poseStack.translate(-0.0625F, 0.53125F, 0.21875F);
            poseStack.translate(-0.075F, 0.0F, -0.05F);
            poseStack.translate(0.05F, 0.55F, -0.4F);
            poseStack.mulPose(Axis.XP.rotationDegrees(200.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            itemState.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, outlineColor == 0 ? -1 : outlineColor);
            poseStack.popPose();
        }
    }
}
