package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer;
import com.iafenvoy.iceandfire.render.model.BipedBaseModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Item-in-hand feature for legacy biped models submitted through the 26.1 render-state pipeline.
 */
public final class DreadItemFeatureRenderer<T extends Mob> implements LegacyEntityFeature<T> {
    private final LegacyMobRenderer<T, ? extends BipedBaseModel<T>> renderer;
    public boolean hidden;

    public DreadItemFeatureRenderer(LegacyMobRenderer<T, ? extends BipedBaseModel<T>> renderer) {
        this.renderer = renderer;
    }

    @Override
    public void submit(T entity, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int light, int outlineColor) {
        if (this.hidden) return;
        this.submitHand(entity, InteractionHand.MAIN_HAND, entity.getMainArm(), poseStack, collector, light, outlineColor);
        this.submitHand(entity, InteractionHand.OFF_HAND, entity.getMainArm().getOpposite(), poseStack, collector, light, outlineColor);
    }

    private void submitHand(T entity, InteractionHand hand, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int light, int outlineColor) {
        ItemStack stack = entity.getItemInHand(hand);
        if (stack.isEmpty()) return;
        ItemStackRenderState itemState = new ItemStackRenderState();
        ItemDisplayContext context = arm == HumanoidArm.LEFT ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        Minecraft.getInstance().getItemModelResolver().updateForLiving(itemState, stack, context, entity);
        poseStack.pushPose();
        this.renderer.getLegacyModel().translateToHand(new EntityRenderState(), arm, poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.translate(arm == HumanoidArm.LEFT ? -0.0625F : 0.0625F, 0.125F, -0.625F);
        itemState.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, outlineColor);
        poseStack.popPose();
    }
}
