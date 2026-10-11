package com.iafenvoy.iceandfire.render.entity.layer;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.AmphithereEntity;
import com.iafenvoy.iceandfire.render.model.AmphithereModel;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Flight rework: draws the controlling player as part of the amphithere model instead of letting the player renderer
 * place it. {@link com.iafenvoy.iceandfire.client.FlightClientEvents} keeps the standalone player render disabled
 * while the entity is being drawn here.
 */
public class AmphithereRiderLayer extends RenderLayer<AmphithereEntity, AmphithereModel> {
    private static final float AMPHITHERE_SCALE = 2.0F;
    public static final List<Entity> RENDERING_RIDERS = new ArrayList<>();

    public AmphithereRiderLayer(MobRenderer<AmphithereEntity, AmphithereModel> renderIn) {
        super(renderIn);
    }

    @Override
    public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, AmphithereEntity amphithere, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!IafCommonConfig.INSTANCE.amphithere.flightRework.getValue()) return;
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
        this.renderEntity(controller, 0, 0, 0, 0.0F, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        RENDERING_RIDERS.remove(controller);
        matrixStackIn.popPose();

        matrixStackIn.popPose();
    }

    protected void translateToBody(PoseStack stack) {
        this.postRender(this.getParentModel().BodyUpper, stack);
        this.postRender(this.getParentModel().Neck1, stack);
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

    public <E extends Entity> void renderEntity(E entityIn, int x, int y, int z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
        try {
            Minecraft.getInstance().getEntityRenderDispatcher().render(entityIn, x, y, z, yaw, partialTicks, matrixStack, bufferIn, packedLight);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being rendered");
            entityIn.fillCrashReportCategory(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.addCategory("Renderer details");
            crashreportcategory1.setDetail("Location", new BlockPos(x, y, z));
            crashreportcategory1.setDetail("Rotation", yaw);
            crashreportcategory1.setDetail("Delta", partialTicks);
            throw new ReportedException(crashreport);
        }
    }
}
