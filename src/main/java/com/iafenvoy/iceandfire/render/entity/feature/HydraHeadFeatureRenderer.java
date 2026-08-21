package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.HydraEntity;
import com.iafenvoy.iceandfire.render.entity.HydraEntityRenderer;
import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.model.HydraBodyModel;
import com.iafenvoy.iceandfire.render.model.HydraHeadModel;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class HydraHeadFeatureRenderer implements LegacyEntityFeature<HydraEntity> {
    public static final Identifier TEXTURE_STONE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hydra/stone.png");
    private static final float[][] TRANSLATE = new float[][]{{0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F}, {-0.15F, 0.15F, 0F, 0F, 0F, 0F, 0F, 0F, 0F}, {-0.3F, 0F, 0.3F, 0F, 0F, 0F, 0F, 0F, 0F}, {-0.4F, -0.1F, 0.1F, 0.4F, 0F, 0F, 0F, 0F, 0F}, {-0.5F, -0.2F, 0F, 0.2F, 0.5F, 0F, 0F, 0F, 0F}, {-0.7F, -0.4F, -0.2F, 0.2F, 0.4F, 0.7F, 0F, 0F, 0F}, {-0.7F, -0.4F, -0.2F, 0F, 0.2F, 0.4F, 0.7F, 0F, 0F}, {-0.6F, -0.4F, -0.2F, -0.1F, 0.1F, 0.2F, 0.4F, 0.6F, 0F}, {-0.6F, -0.4F, -0.2F, -0.1F, 0.0F, 0.1F, 0.2F, 0.4F, 0.6F}};
    private static final float[][] ROTATE = new float[][]{{0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F}, {10F, -10F, 0F, 0F, 0F, 0F, 0F, 0F, 0F}, {10F, 0F, -10F, 0F, 0F, 0F, 0F, 0F, 0F}, {25F, 10F, -10F, -25F, 0F, 0F, 0F, 0F, 0F}, {30F, 15F, 0F, -15F, -30F, 0F, 0F, 0F, 0F}, {40F, 25F, 5F, -5F, -25F, -40F, 0F, 0F, 0F}, {40F, 30F, 15F, 0F, -15F, -30F, -40F, 0F, 0F}, {45F, 30F, 20F, 5F, -5F, 0F, -20F, -30F, -45F}, {50F, 37F, 25F, 15F, 0F, -15F, -25F, -37F, -50F}};
    private static final HydraHeadModel[] MODELS = new HydraHeadModel[HydraEntity.HEADS];

    static {
        for (int index = 0; index < MODELS.length; index++) MODELS[index] = new HydraHeadModel(index);
    }

    private final HydraEntityRenderer renderer;

    public HydraHeadFeatureRenderer(HydraEntityRenderer renderer) {
        this.renderer = renderer;
    }

    public static void submitHydraHeads(HydraBodyModel body, boolean stone, PoseStack poseStack, SubmitNodeCollector collector, int light, HydraEntity hydra, float ageInTicks, int outlineColor) {
        int heads = hydra.getHeadCount();
        if (heads < 1 || heads > MODELS.length) return;
        poseStack.pushPose();
        translateToBody(body, poseStack);
        RenderType type = RenderTypes.entityCutout(stone ? TEXTURE_STONE : getHeadTexture(hydra));
        for (int head = 1; head <= heads; head++) {
            HydraHeadModel model = MODELS[head - 1];
            poseStack.pushPose();
            poseStack.translate(TRANSLATE[heads - 1][head - 1] * 0.5F, 0.0F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(ROTATE[heads - 1][head - 1]));
            model.setupAnim(hydra, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F);
            collector.submitCustomGeometry(poseStack, type, (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                model.renderToBuffer(modelStack, buffer, light, OverlayTexture.NO_OVERLAY, outlineColor == 0 ? -1 : outlineColor);
            });
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    public static Identifier getHeadTexture(HydraEntity hydra) {
        return switch (hydra.getVariant()) {
            case 1 -> HydraEntityRenderer.TEXUTURE_1;
            case 2 -> HydraEntityRenderer.TEXUTURE_2;
            default -> HydraEntityRenderer.TEXUTURE_0;
        };
    }

    private static void translateToBody(HydraBodyModel model, PoseStack stack) {
        postRender(model.BodyUpper, stack);
    }

    private static void postRender(AdvancedModelBox part, PoseStack stack) {
        stack.translate(part.rotationPointX * 0.0625F, part.rotationPointY * 0.0625F, part.rotationPointZ * 0.0625F);
        if (part.rotateAngleZ != 0.0F) stack.mulPose(Axis.ZP.rotation(part.rotateAngleZ));
        if (part.rotateAngleY != 0.0F) stack.mulPose(Axis.YP.rotation(part.rotateAngleY));
        if (part.rotateAngleX != 0.0F) stack.mulPose(Axis.XP.rotation(part.rotateAngleX));
    }

    @Override
    public void submit(HydraEntity entity, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int lightCoords, int outlineColor) {
        if (!entity.isInvisible())
            submitHydraHeads(this.renderer.getLegacyModel(), false, poseStack, collector, lightCoords, entity, entity.tickCount + partialTick, outlineColor);
    }
}
