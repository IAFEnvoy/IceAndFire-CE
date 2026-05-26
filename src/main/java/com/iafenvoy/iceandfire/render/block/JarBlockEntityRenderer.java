package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.item.block.JarBlock;
import com.iafenvoy.iceandfire.item.block.entity.JarBlockEntity;
import com.iafenvoy.iceandfire.render.entity.PixieEntityRenderer;
import com.iafenvoy.iceandfire.render.model.PixieModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class JarBlockEntityRenderer<T extends JarBlockEntity> implements BlockEntityRenderer<T> {
    public static final RenderType TEXTURE_0 = RenderType.entityCutoutNoCull(PixieEntityRenderer.TEXTURE_0, false);
    public static final RenderType TEXTURE_1 = RenderType.entityCutoutNoCull(PixieEntityRenderer.TEXTURE_1, false);
    public static final RenderType TEXTURE_2 = RenderType.entityCutoutNoCull(PixieEntityRenderer.TEXTURE_2, false);
    public static final RenderType TEXTURE_3 = RenderType.entityCutoutNoCull(PixieEntityRenderer.TEXTURE_3, false);
    public static final RenderType TEXTURE_4 = RenderType.entityCutoutNoCull(PixieEntityRenderer.TEXTURE_4, false);
    public static final RenderType TEXTURE_5 = RenderType.entityCutoutNoCull(PixieEntityRenderer.TEXTURE_5, false);
    public static final RenderType TEXTURE_0_GLO = RenderType.eyes(PixieEntityRenderer.TEXTURE_0);
    public static final RenderType TEXTURE_1_GLO = RenderType.eyes(PixieEntityRenderer.TEXTURE_1);
    public static final RenderType TEXTURE_2_GLO = RenderType.eyes(PixieEntityRenderer.TEXTURE_2);
    public static final RenderType TEXTURE_3_GLO = RenderType.eyes(PixieEntityRenderer.TEXTURE_3);
    public static final RenderType TEXTURE_4_GLO = RenderType.eyes(PixieEntityRenderer.TEXTURE_4);
    public static final RenderType TEXTURE_5_GLO = RenderType.eyes(PixieEntityRenderer.TEXTURE_5);
    private static final Supplier<PixieModel> MODEL_PIXIE = PixieModel::new;

    public JarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(T entity, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        int meta = 0;
        boolean hasPixie = false;
        if (entity.getLevel() != null) {
            if (entity.getBlockState().getBlock() instanceof JarBlock jar) {
                meta = jar.getPixieType();
                hasPixie = !jar.isEmpty();
            } else {
                meta = entity.pixieType;
                hasPixie = entity.hasPixie;
            }
        }
        if (hasPixie) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 1.501F, 0.5F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
            matrixStackIn.pushPose();
            RenderType type = switch (meta) {
                case 1 -> TEXTURE_1;
                case 2 -> TEXTURE_2;
                case 3 -> TEXTURE_3;
                case 4 -> TEXTURE_4;
                default -> TEXTURE_0;
            };
            RenderType typeGlow = switch (meta) {
                case 1 -> TEXTURE_1_GLO;
                case 2 -> TEXTURE_2_GLO;
                case 3 -> TEXTURE_3_GLO;
                case 4 -> TEXTURE_4_GLO;
                default -> TEXTURE_0_GLO;
            };
            VertexConsumer buffer = bufferIn.getBuffer(type);
            if (entity.getLevel() != null) {
                if (entity.hasProduced) matrixStackIn.translate(0F, 0.90F, 0F);
                else matrixStackIn.translate(0F, 0.60F, 0F);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(this.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks)));
                matrixStackIn.scale(0.50F, 0.50F, 0.50F);
                PixieModel model = MODEL_PIXIE.get();
                model.animateInJar(entity.hasProduced, entity, 0);
                model.renderToBuffer(matrixStackIn, buffer, combinedLightIn, combinedOverlayIn, -1);
                model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(typeGlow), combinedLightIn, combinedOverlayIn, -1);
            }
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
    }

    protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
        float f = yawOffset - prevYawOffset;
        while (f < -180) f += 360;
        while (f >= 180.0F) f -= 360.0F;
        return prevYawOffset + partialTicks * f;
    }
}
