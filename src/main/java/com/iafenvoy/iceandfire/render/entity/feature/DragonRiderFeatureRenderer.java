package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.DreadQueenEntity;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DragonRiderFeatureRenderer<T extends DragonBaseEntity> extends RenderLayer<T, TabulaModel<T>> {
    public static final List<Entity> RENDERING_RIDERS = new ArrayList<>();
    private final boolean excludeDreadQueenMob;

    public DragonRiderFeatureRenderer(MobRenderer<T, TabulaModel<T>> renderIn, boolean excludeDreadQueenMob) {
        super(renderIn);
        this.excludeDreadQueenMob = excludeDreadQueenMob;
    }

    @Override
    public void render(PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, T dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
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
                        EntityRenderer<? super Entity> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(passenger);
                        EntityModel<?> modelBase = null;
                        if (render instanceof MobRenderer mobEntityRenderer)
                            modelBase = mobEntityRenderer.getModel();
                        if ((passenger.getBbHeight() > passenger.getBbWidth() || modelBase instanceof HumanoidModel) && !(modelBase instanceof QuadrupedModel) && !(modelBase instanceof HorseModel)) {
                            matrixStackIn.translate(-0.15F * passenger.getBbHeight(), 0.1F * dragonScale - 0.1F * passenger.getBbHeight(), -0.1F * dragonScale - 0.1F * passenger.getBbWidth());
                            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90.0F));
                            matrixStackIn.mulPose(Axis.YP.rotationDegrees(45.0F));
                        } else {
                            boolean horse = modelBase instanceof HorseModel;
                            matrixStackIn.translate((horse ? -0.08F : -0.15F) * passenger.getBbWidth(), 0.1F * dragonScale - 0.15F * passenger.getBbWidth(), -0.1F * dragonScale - 0.1F * passenger.getBbWidth());
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
                this.renderEntity(passenger, 0, 0, 0, 0.0F, partialTicks, matrixStackIn, bufferIn, packedLightIn);
                RENDERING_RIDERS.remove(passenger);
                matrixStackIn.popPose();
            }
        }
        matrixStackIn.popPose();
    }

    protected void translateToBody(PoseStack stack) {
        this.postRender(this.getParentModel().getCube("BodyUpper"), stack);
        this.postRender(this.getParentModel().getCube("Neck1"), stack);
    }

    protected void translateToHead(PoseStack stack) {
        this.postRender(this.getParentModel().getCube("Neck2"), stack);
        this.postRender(this.getParentModel().getCube("Neck3"), stack);
        this.postRender(this.getParentModel().getCube("Head"), stack);
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

    public <E extends Entity> void renderEntity(E entityIn, int x, int y, int z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
        try {
            Minecraft.getInstance().getEntityRenderDispatcher().render(entityIn, x, y, z, yaw, partialTicks, matrixStack, bufferIn, packedLight);
        } catch (Throwable throwable3) {
            CrashReport crashreport = CrashReport.forThrowable(throwable3, "Rendering entity in world");
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