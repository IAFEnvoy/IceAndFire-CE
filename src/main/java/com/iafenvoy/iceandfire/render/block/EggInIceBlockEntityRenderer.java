package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.item.block.entity.EggInIceBlockEntity;
import com.iafenvoy.iceandfire.render.model.DragonEggModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

public class EggInIceBlockEntityRenderer<T extends EggInIceBlockEntity> implements BlockEntityRenderer<T> {
    public EggInIceBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(T egg, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        DragonEggModel model = new DragonEggModel();
        if (egg.type != null) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5, -0.8F, 0.5F);
            matrixStackIn.pushPose();
            model.renderFrozen(egg);
            model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(PodiumBlockEntityRenderer.getEggTexture(egg.type)), combinedLightIn, combinedOverlayIn, -1);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
    }
}
