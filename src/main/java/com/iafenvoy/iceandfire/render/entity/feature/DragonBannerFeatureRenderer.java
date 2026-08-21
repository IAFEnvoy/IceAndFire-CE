package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.stream.StreamSupport;

public class DragonBannerFeatureRenderer<T extends DragonBaseEntity> implements LegacyEntityFeature<T> {
    private final TabulaModel<T> model;

    public DragonBannerFeatureRenderer(LegacyMobRenderer<T, TabulaModel<T>> renderer) {
        this.model = renderer.getLegacyModel();
    }

    @Override
    public void submit(T entity, float partialTick, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState camera, int packedLightIn, int outlineColor) {
        ItemStack itemstack = entity.getItemInHand(InteractionHand.OFF_HAND);
        matrixStackIn.pushPose();
        if (!itemstack.isEmpty() && itemstack.getItem() instanceof BannerItem) {
            float f = (entity.getRenderSize() / 3F);
            float f2 = 1F / f;
            matrixStackIn.pushPose();
            Optional<AdvancedModelBox> optional = StreamSupport.stream(this.model.getAllParts().spliterator(), false).filter(cube -> cube.boxName.equals("BodyUpper")).findFirst();
            optional.ifPresent(box -> this.postRender(box, matrixStackIn));
            matrixStackIn.translate(0, -0.2F, 0.4F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
            matrixStackIn.pushPose();
            matrixStackIn.scale(f2, f2, f2);
            ItemStackRenderState itemState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForLiving(itemState, itemstack, ItemDisplayContext.NONE, entity);
            itemState.submit(matrixStackIn, collector, packedLightIn, OverlayTexture.NO_OVERLAY, outlineColor);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();
    }

    protected void postRender(AdvancedModelBox renderer, PoseStack matrixStackIn) {
        if (renderer.rotateAngleX == 0.0F && renderer.rotateAngleY == 0.0F && renderer.rotateAngleZ == 0.0F) {
            if (renderer.rotationPointX != 0.0F || renderer.rotationPointY != 0.0F || renderer.offsetZ != 0.0F)
                matrixStackIn.translate(renderer.rotationPointX * (float) 0.0625, renderer.rotationPointY * (float) 0.0625, renderer.rotationPointZ * (float) 0.0625);
        } else {
            matrixStackIn.translate(renderer.rotationPointX * (float) 0.0625, renderer.rotationPointY * (float) 0.0625, renderer.rotationPointZ * (float) 0.0625);
            if (renderer.rotateAngleZ != 0.0F)
                matrixStackIn.mulPose(Axis.ZP.rotation(renderer.rotateAngleZ));
            if (renderer.rotateAngleY != 0.0F)
                matrixStackIn.mulPose(Axis.YP.rotation(renderer.rotateAngleY));
            if (renderer.rotateAngleX != 0.0F)
                matrixStackIn.mulPose(Axis.XP.rotation(renderer.rotateAngleX));
        }
    }
}
