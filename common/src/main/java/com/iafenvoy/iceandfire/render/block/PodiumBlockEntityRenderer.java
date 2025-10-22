package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.item.block.entity.PodiumBlockEntity;
import com.iafenvoy.iceandfire.item.DragonEggItem;
import com.iafenvoy.iceandfire.render.model.DragonEggModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class PodiumBlockEntityRenderer<T extends PodiumBlockEntity> implements BlockEntityRenderer<T> {
    public PodiumBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    protected static RenderLayer getEggTexture(DragonColor type) {
        return RenderLayer.getEntityCutout(type.getEggTexture());
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        DragonEggModel model = new DragonEggModel();
        if (!entity.getStackInSlot(0).isEmpty()) {
            if (entity.getStackInSlot(0).getItem() instanceof DragonEggItem item) {
                matrixStackIn.push();
                matrixStackIn.translate(0.5F, 0.475F, 0.5F);
                matrixStackIn.push();
                matrixStackIn.push();
                model.renderPodium();
                model.render(matrixStackIn, bufferIn.getBuffer(PodiumBlockEntityRenderer.getEggTexture(item.type)), combinedLightIn, combinedOverlayIn, -1);
                matrixStackIn.pop();
                matrixStackIn.pop();
                matrixStackIn.pop();
            } else if (!entity.getStackInSlot(0).isEmpty()) {
                matrixStackIn.push();
                float f2 = ((float) entity.prevTicksExisted + (entity.ticksExisted - entity.prevTicksExisted) * partialTicks);
                float f3 = MathHelper.sin(f2 / 10.0F) * 0.1F + 0.1F;
                matrixStackIn.translate(0.5F, 1.55F + f3, 0.5F);
                float f4 = (f2 / 20.0F);
                matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotation(f4));
                matrixStackIn.push();
                matrixStackIn.translate(0, 0.2F, 0);
                matrixStackIn.scale(0.65F, 0.65F, 0.65F);
                MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getStackInSlot(0), ModelTransformationMode.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, entity.getWorld(), 0);
                matrixStackIn.pop();
                matrixStackIn.pop();
            }
        }
    }
}
