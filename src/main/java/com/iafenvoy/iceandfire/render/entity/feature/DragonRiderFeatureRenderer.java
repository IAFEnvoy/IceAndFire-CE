package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.DreadQueenEntity;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class DragonRiderFeatureRenderer<T extends DragonBaseEntity> implements LegacyEntityFeature<T> {
    public static final List<Entity> RENDERING_RIDERS = new ArrayList<>();
    private final boolean excludeDreadQueenMob;
    private final TabulaModel<T> model;

    public DragonRiderFeatureRenderer(LegacyMobRenderer<T, TabulaModel<T>> renderer, boolean excludeDreadQueenMob) {
        this.model = renderer.getLegacyModel();
        this.excludeDreadQueenMob = excludeDreadQueenMob;
    }

    @Override
    public void submit(T dragon, float partialTicks, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState camera, int packedLightIn, int outlineColor) {
        matrixStackIn.pushPose();
        if (!dragon.getPassengers().isEmpty()) {
            float dragonScale = dragon.getRenderSize() / 3;
            for (Entity passenger : dragon.getPassengers()) {
                boolean prey = dragon.getControllingPassenger() == null || dragon.getControllingPassenger().getId() != passenger.getId();
                if (this.excludeDreadQueenMob && passenger instanceof DreadQueenEntity) prey = false;
                float riderRot = passenger.yRotO + (passenger.getYRot() - passenger.yRotO) * partialTicks;
                int animationTicks = 0;
                if (dragon.getAnimation() == DragonBaseEntity.ANIMATION_SHAKEPREY)
                    animationTicks = dragon.getAnimationTick();
                if (animationTicks == 0 || animationTicks >= 15) this.translateToBody(matrixStackIn);
                if (prey) {
                    if (animationTicks == 0 || animationTicks >= 15 || dragon.isFlying()) {
                        this.translateToHead(matrixStackIn);
                        this.offsetPerDragonType(dragon.dragonType, matrixStackIn);
                        if (passenger.getBbHeight() > passenger.getBbWidth()) {
                            matrixStackIn.translate(-0.15F * passenger.getBbHeight(), 0.1F * dragonScale - 0.1F * passenger.getBbHeight(), -0.1F * dragonScale - 0.1F * passenger.getBbWidth());
                            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90.0F));
                            matrixStackIn.mulPose(Axis.YP.rotationDegrees(45.0F));
                        } else {
                            matrixStackIn.translate(-0.15F * passenger.getBbWidth(), 0.1F * dragonScale - 0.15F * passenger.getBbWidth(), -0.1F * dragonScale - 0.1F * passenger.getBbWidth());
                            matrixStackIn.mulPose(Axis.XN.rotationDegrees(90.0F));
                        }
                    } else matrixStackIn.translate(0, 0.555F * dragonScale, -0.5F * dragonScale);
                } else matrixStackIn.translate(0, -0.01F * dragonScale, -0.035F * dragonScale);
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(riderRot + 180));
                matrixStackIn.scale(1 / dragonScale, 1 / dragonScale, 1 / dragonScale);
                matrixStackIn.translate(0, -0.25F, 0);
                RENDERING_RIDERS.add(passenger);
                EntityRenderState passengerState = Minecraft.getInstance().getEntityRenderDispatcher().extractEntity(passenger, partialTicks);
                Minecraft.getInstance().getEntityRenderDispatcher().submit(passengerState, camera, 0.0D, 0.0D, 0.0D, matrixStackIn, collector);
                RENDERING_RIDERS.remove(passenger);
                matrixStackIn.popPose();
            }
        }
        matrixStackIn.popPose();
    }

    protected void translateToBody(PoseStack stack) {
        this.postRender(this.model.getCube("BodyUpper"), stack);
        this.postRender(this.model.getCube("Neck1"), stack);
    }

    protected void translateToHead(PoseStack stack) {
        this.postRender(this.model.getCube("Neck2"), stack);
        this.postRender(this.model.getCube("Neck3"), stack);
        this.postRender(this.model.getCube("Head"), stack);
    }

    protected void postRender(AdvancedModelBox renderer, PoseStack matrixStackIn) {
        if (renderer.rotateAngleX == 0.0F && renderer.rotateAngleY == 0.0F && renderer.rotateAngleZ == 0.0F) {
            if (renderer.rotationPointX != 0.0F || renderer.rotationPointY != 0.0F || renderer.rotationPointZ != 0.0F)
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

    private void offsetPerDragonType(DragonType dragonType, PoseStack stackIn) {
        if (dragonType == IafDragonTypes.LIGHTNING)
            stackIn.translate(0.1F, -0.2F, -0.1F);
    }

}
