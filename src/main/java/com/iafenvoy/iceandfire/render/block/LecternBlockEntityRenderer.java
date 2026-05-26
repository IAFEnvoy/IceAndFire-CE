package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.block.LecternBlock;
import com.iafenvoy.iceandfire.item.block.entity.LecternBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class LecternBlockEntityRenderer<T extends LecternBlockEntity> implements BlockEntityRenderer<T> {
    private static final RenderType ENCHANTMENT_TABLE_BOOK_TEXTURE = RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/lectern_book.png"));
    private final BookModel bookModel;

    public LecternBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void render(@NotNull T entity, float partialTicks, PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5F, 1.1F, 0.5F);
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(this.getRotation(entity)));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(112.0F));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
        float f4 = entity.pageFlipPrev + (entity.pageFlip - entity.pageFlipPrev) * partialTicks + 0.25F;
        float f5 = entity.pageFlipPrev + (entity.pageFlip - entity.pageFlipPrev) * partialTicks + 0.75F;
        f4 = (f4 - Mth.floor(f4)) * 1.6F - 0.3F;
        f5 = (f5 - Mth.floor(f5)) * 1.6F - 0.3F;

        if (f4 < 0.0F) f4 = 0.0F;
        if (f5 < 0.0F) f5 = 0.0F;
        if (f4 > 1.0F) f4 = 1.0F;
        if (f5 > 1.0F) f5 = 1.0F;

        float f6 = 1.29F;

        this.bookModel.setupAnim(partialTicks, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), f6);
        this.bookModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(ENCHANTMENT_TABLE_BOOK_TEXTURE), combinedLightIn, combinedOverlayIn, -1);
        matrixStackIn.popPose();
    }

    private float getRotation(LecternBlockEntity lectern) {
        return switch (lectern.getBlockState().getValue(LecternBlock.FACING)) {
            case EAST -> 90;
            case WEST -> -90;
            case SOUTH -> 0;
            default -> 180;
        };
    }
}
