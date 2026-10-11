package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.compat.IafClientCompat;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.AmphithereEntity;
import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer;
import com.iafenvoy.iceandfire.render.model.AmphithereModel;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Flight rework: draws the controlling player as part of the amphithere model instead of letting the player renderer
 * place it. {@link com.iafenvoy.iceandfire.client.FlightClientEvents} keeps the standalone player render disabled
 * while the entity is being drawn here.
 */
public class AmphithereRiderFeatureRenderer implements LegacyEntityFeature<AmphithereEntity> {
    private static final float AMPHITHERE_SCALE = 2.0F;
    public static final List<Entity> RENDERING_RIDERS = new ArrayList<>();
    private final AmphithereModel model;

    public AmphithereRiderFeatureRenderer(LegacyMobRenderer<AmphithereEntity, AmphithereModel> renderer) {
        this.model = renderer.getLegacyModel();
    }

    @Override
    public void submit(AmphithereEntity amphithere, float partialTicks, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState camera, int packedLightIn, int outlineColor) {
        if (!IafCommonConfig.INSTANCE.amphithere.flightRework.getValue()) return;
        // Sodium skips the nested extract/submit below, so the vanilla player render has to stay enabled there.
        if (IafClientCompat.isSodiumLoaded()) return;
        Entity controller = null;
        for (Entity passenger : amphithere.getPassengers()) {
            if (passenger instanceof Player && amphithere.getTarget() != passenger) {
                controller = passenger;
                break;
            }
        }
        if (controller == null) return;

        matrixStackIn.pushPose();
        this.translateToBody(matrixStackIn);
        matrixStackIn.translate(0, -0.01F * AMPHITHERE_SCALE, -0.035F * AMPHITHERE_SCALE);

        float riderRot = controller.yRotO + (controller.getYRot() - controller.yRotO) * partialTicks;

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(riderRot + 180));
        matrixStackIn.scale(1 / AMPHITHERE_SCALE, 1 / AMPHITHERE_SCALE, 1 / AMPHITHERE_SCALE);
        matrixStackIn.translate(0, -0.25F, 0);
        RENDERING_RIDERS.add(controller);
        EntityRenderState passengerState = Minecraft.getInstance().getEntityRenderDispatcher().extractEntity(controller, partialTicks);
        Minecraft.getInstance().getEntityRenderDispatcher().submit(passengerState, camera, 0.0D, 0.0D, 0.0D, matrixStackIn, collector);
        RENDERING_RIDERS.remove(controller);
        matrixStackIn.popPose();

        matrixStackIn.popPose();
    }

    protected void translateToBody(PoseStack stack) {
        this.postRender(this.model.BodyUpper, stack);
        this.postRender(this.model.Neck1, stack);
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
}
