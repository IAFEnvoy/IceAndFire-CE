package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.item.DragonEggItem;
import com.iafenvoy.iceandfire.item.block.entity.PodiumBlockEntity;
import com.iafenvoy.iceandfire.render.model.DragonEggModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class PodiumBlockEntityRenderer<T extends PodiumBlockEntity> implements BlockEntityRenderer<T> {
    public PodiumBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    protected static RenderType getEggTexture(DragonColor type) {
        return RenderType.entityCutout(type.getTextureProvider().getEggTexture());
    }

    @Override
    public void render(T entity, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        DragonEggModel model = new DragonEggModel();
        if (!entity.getItem(0).isEmpty()) {
            if (entity.getItem(0).getItem() instanceof DragonEggItem item) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.5F, 0.475F, 0.5F);
                matrixStackIn.pushPose();
                matrixStackIn.pushPose();
                model.renderPodium();
                model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(PodiumBlockEntityRenderer.getEggTexture(item.type)), combinedLightIn, combinedOverlayIn, -1);
                matrixStackIn.popPose();
                matrixStackIn.popPose();
                matrixStackIn.popPose();
            } else if (!entity.getItem(0).isEmpty()) {
                matrixStackIn.pushPose();
                float f2 = ((float) entity.prevTicksExisted + (entity.ticksExisted - entity.prevTicksExisted) * partialTicks);
                float f3 = Mth.sin(f2 / 10.0F) * 0.1F + 0.1F;
                matrixStackIn.translate(0.5F, 1.55F + f3, 0.5F);
                float f4 = (f2 / 20.0F);
                matrixStackIn.mulPose(Axis.YP.rotation(f4));
                matrixStackIn.pushPose();
                matrixStackIn.translate(0, 0.2F, 0);
                matrixStackIn.scale(0.65F, 0.65F, 0.65F);
                Minecraft.getInstance().getItemRenderer().renderStatic(entity.getItem(0), ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, entity.getLevel(), 0);
                matrixStackIn.popPose();
                matrixStackIn.popPose();
            }
        }
    }
}
