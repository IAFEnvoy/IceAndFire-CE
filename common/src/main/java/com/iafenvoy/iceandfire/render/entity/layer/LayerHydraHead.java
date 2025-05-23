package com.iafenvoy.iceandfire.render.entity.layer;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityHydra;
import com.iafenvoy.iceandfire.render.entity.RenderHydra;
import com.iafenvoy.iceandfire.render.model.ModelHydraBody;
import com.iafenvoy.iceandfire.render.model.ModelHydraHead;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class LayerHydraHead extends FeatureRenderer<EntityHydra, ModelHydraBody> {
    public static final Identifier TEXTURE_STONE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/hydra/stone.png");
    private static final float[][] TRANSLATE = new float[][]{
            {0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 1 total heads
            {-0.15F, 0.15F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 2 total heads
            {-0.3F, 0F, 0.3F, 0F, 0F, 0F, 0F, 0F, 0F},// 3 total heads
            {-0.4F, -0.1F, 0.1F, 0.4F, 0F, 0F, 0F, 0F, 0F},//etc...
            {-0.5F, -0.2F, 0F, 0.2F, 0.5F, 0F, 0F, 0F, 0F},
            {-0.7F, -0.4F, -0.2F, 0.2F, 0.4F, 0.7F, 0F, 0F, 0F},
            {-0.7F, -0.4F, -0.2F, 0, 0.2F, 0.4F, 0.7F, 0F, 0F},
            {-0.6F, -0.4F, -0.2F, -0.1F, 0.1F, 0.2F, 0.4F, 0.6F, 0F},
            {-0.6F, -0.4F, -0.2F, -0.1F, 0.0F, 0.1F, 0.2F, 0.4F, 0.6F},
    };
    private static final float[][] ROTATE = new float[][]{
            {0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 1 total heads
            {10F, -10F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 2 total heads
            {10F, 0F, -10F, 0F, 0F, 0F, 0F, 0F, 0F},// 3 total heads
            {25F, 10F, -10F, -25F, 0F, 0F, 0F, 0F, 0F},//etc...
            {30F, 15F, 0F, -15F, -30F, 0F, 0F, 0F, 0F},
            {40F, 25F, 5F, -5F, -25F, -40F, 0F, 0F, 0F},
            {40F, 30F, 15F, 0F, -15F, -30F, -40F, 0F, 0F},
            {45F, 30F, 20F, 5F, -5F, -20F, -30F, -45F, 0F},
            {50F, 37F, 25F, 15F, 0, -15F, -25F, -37F, -50F},
    };
    private static final ModelHydraHead[] modelArr = new ModelHydraHead[EntityHydra.HEADS];

    static {
        for (int i = 0; i < modelArr.length; i++)
            modelArr[i] = new ModelHydraHead(i);
    }

    private final RenderHydra renderer;

    public LayerHydraHead(RenderHydra renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    public static void renderHydraHeads(ModelHydraBody model, boolean stone, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, EntityHydra hydra, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStackIn.push();
        int heads = hydra.getHeadCount();
        translateToBody(model, matrixStackIn);
        RenderLayer type = RenderLayer.getEntityCutout(stone ? TEXTURE_STONE : getHeadTexture(hydra));
        for (int head = 1; head <= heads; head++) {
            matrixStackIn.push();
            float bodyWidth = 0.5F;
            matrixStackIn.translate(TRANSLATE[heads - 1][head - 1] * bodyWidth, 0, 0);
            matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(ROTATE[heads - 1][head - 1]));
            modelArr[head - 1].setAngles(hydra, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            modelArr[head - 1].render(matrixStackIn, bufferIn.getBuffer(type), packedLightIn, LivingEntityRenderer.getOverlay(hydra, 0.0F), -1);
            matrixStackIn.pop();
        }
        matrixStackIn.pop();
    }

    public static Identifier getHeadTexture(EntityHydra gorgon) {
        return switch (gorgon.getVariant()) {
            case 1 -> RenderHydra.TEXUTURE_1;
            case 2 -> RenderHydra.TEXUTURE_2;
            default -> RenderHydra.TEXUTURE_0;
        };
    }

    protected static void translateToBody(ModelHydraBody model, MatrixStack stack) {
        postRender(model.BodyUpper, stack);
    }

    protected static void postRender(AdvancedModelBox renderer, MatrixStack matrixStackIn) {
        if (renderer.rotateAngleX == 0.0F && renderer.rotateAngleY == 0.0F && renderer.rotateAngleZ == 0.0F) {
            if (renderer.rotationPointX != 0.0F || renderer.rotationPointY != 0.0F)
                matrixStackIn.translate(renderer.rotationPointX * (float) 0.0625, renderer.rotationPointY * (float) 0.0625, renderer.rotateAngleZ * (float) 0.0625);
        } else {
            matrixStackIn.translate(renderer.rotationPointX * (float) 0.0625, renderer.rotationPointY * (float) 0.0625, renderer.rotateAngleZ * (float) 0.0625);
            if (renderer.rotateAngleZ != 0.0F)
                matrixStackIn.multiply(RotationAxis.POSITIVE_Z.rotation(renderer.rotateAngleZ));
            if (renderer.rotateAngleY != 0.0F)
                matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotation(renderer.rotateAngleY));
            if (renderer.rotateAngleX != 0.0F)
                matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotation(renderer.rotateAngleX));
        }
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, EntityHydra entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) return;
        renderHydraHeads(this.renderer.getModel(), false, matrixStackIn, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public Identifier getTexture(EntityHydra gorgon) {
        return switch (gorgon.getVariant()) {
            case 1 -> RenderHydra.TEXUTURE_1;
            case 2 -> RenderHydra.TEXUTURE_2;
            default -> RenderHydra.TEXUTURE_0;
        };
    }
}