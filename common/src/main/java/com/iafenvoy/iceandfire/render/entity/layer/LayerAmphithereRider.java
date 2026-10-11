package com.iafenvoy.iceandfire.render.entity.layer;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.EntityAmphithere;
import com.iafenvoy.iceandfire.render.model.ModelAmphithere;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import java.util.ArrayList;
import java.util.List;

/**
 * Flight rework: draws the controlling player as part of the amphithere model instead of letting the player renderer
 * place it. {@link com.iafenvoy.iceandfire.mixin.PlayerEntityRendererMixin} keeps the standalone player render disabled
 * while the entity is being drawn here.
 */
public class LayerAmphithereRider extends FeatureRenderer<EntityAmphithere, ModelAmphithere> {
    private static final float AMPHITHERE_SCALE = 2.0F;
    public static final List<Entity> renderingRiders = new ArrayList<>();
    private final MobEntityRenderer<EntityAmphithere, ModelAmphithere> render;

    public LayerAmphithereRider(MobEntityRenderer<EntityAmphithere, ModelAmphithere> renderIn) {
        super(renderIn);
        this.render = renderIn;
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, EntityAmphithere amphithere, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!IafCommonConfig.INSTANCE.amphithere.flightRework.getValue()) return;
        Entity controller = null;
        for (Entity passenger : amphithere.getPassengerList()) {
            if (passenger instanceof PlayerEntity && amphithere.getTarget() != passenger) {
                controller = passenger;
                break;
            }
        }
        if (controller == null) return;

        matrixStackIn.push();
        this.translateToBody(matrixStackIn);
        matrixStackIn.translate(0, -0.01F * AMPHITHERE_SCALE, -0.035F * AMPHITHERE_SCALE);

        float riderRot = controller.prevYaw + (controller.getYaw() - controller.prevYaw) * partialTicks;

        matrixStackIn.push();
        matrixStackIn.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
        matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(riderRot + 180));
        matrixStackIn.scale(1 / AMPHITHERE_SCALE, 1 / AMPHITHERE_SCALE, 1 / AMPHITHERE_SCALE);
        matrixStackIn.translate(0, -0.25F, 0);
        renderingRiders.add(controller);
        this.renderEntity(controller, 0, 0, 0, 0.0F, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        renderingRiders.remove(controller);
        matrixStackIn.pop();

        matrixStackIn.pop();
    }

    protected void translateToBody(MatrixStack stack) {
        this.postRender(this.render.getModel().BodyUpper, stack);
        this.postRender(this.render.getModel().Neck1, stack);
    }

    protected void postRender(AdvancedModelBox renderer, MatrixStack matrixStackIn) {
        if (renderer.rotateAngleX == 0.0F && renderer.rotateAngleY == 0.0F && renderer.rotateAngleZ == 0.0F) {
            if (renderer.rotationPointX != 0.0F || renderer.rotationPointY != 0.0F || renderer.rotationPointZ != 0.0F)
                matrixStackIn.translate(renderer.rotationPointX * (float) 0.0625, renderer.rotationPointY * (float) 0.0625, renderer.rotationPointZ * (float) 0.0625);
        } else {
            matrixStackIn.translate(renderer.rotationPointX * (float) 0.0625, renderer.rotationPointY * (float) 0.0625, renderer.rotationPointZ * (float) 0.0625);
            if (renderer.rotateAngleZ != 0.0F)
                matrixStackIn.multiply(RotationAxis.POSITIVE_Z.rotation(renderer.rotateAngleZ));
            if (renderer.rotateAngleY != 0.0F)
                matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotation(renderer.rotateAngleY));
            if (renderer.rotateAngleX != 0.0F)
                matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotation(renderer.rotateAngleX));
        }
    }

    public <E extends Entity> void renderEntity(E entityIn, int x, int y, int z, float yaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLight) {
        try {
            MinecraftClient.getInstance().getEntityRenderDispatcher().render(entityIn, x, y, z, yaw, partialTicks, matrixStack, bufferIn, packedLight);
        } catch (Throwable throwable3) {
            CrashReport crashreport = CrashReport.create(throwable3, "Rendering entity in world");
            CrashReportSection crashreportcategory = crashreport.addElement("Entity being rendered");
            entityIn.populateCrashReport(crashreportcategory);
            CrashReportSection crashreportcategory1 = crashreport.addElement("Renderer details");
            crashreportcategory1.add("Assigned renderer", this.render);
            crashreportcategory1.add("Location", new BlockPos(x, y, z));
            crashreportcategory1.add("Rotation", yaw);
            crashreportcategory1.add("Delta", partialTicks);
            throw new CrashException(crashreport);
        }
    }
}
