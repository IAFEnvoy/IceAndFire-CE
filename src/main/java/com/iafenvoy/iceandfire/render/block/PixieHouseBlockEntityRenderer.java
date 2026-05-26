package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.block.PixieHouseBlock;
import com.iafenvoy.iceandfire.item.block.entity.PixieHouseBlockEntity;
import com.iafenvoy.iceandfire.render.model.PixieHouseModel;
import com.iafenvoy.iceandfire.render.model.PixieModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import org.jetbrains.annotations.NotNull;

public class PixieHouseBlockEntityRenderer<T extends PixieHouseBlockEntity> implements BlockEntityRenderer<T> {
    private static final PixieHouseModel MODEL = new PixieHouseModel();
    private static final RenderType TEXTURE_0 = RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_0.png"), false);
    private static final RenderType TEXTURE_1 = RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_1.png"), false);
    private static final RenderType TEXTURE_2 = RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_2.png"), false);
    private static final RenderType TEXTURE_3 = RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_3.png"), false);
    private static final RenderType TEXTURE_4 = RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_4.png"), false);
    private static final RenderType TEXTURE_5 = RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_5.png"), false);
    private final PixieModel pixieModel;
    public BlockItem metaOverride;

    public PixieHouseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.pixieModel = new PixieModel();
    }

    @Override
    public void render(@NotNull T entity, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        int rotation = 0;
        int meta = 0;
        if (entity != null && entity.getLevel() != null && entity.getBlockState().getBlock() instanceof PixieHouseBlock) {
            meta = PixieHouseBlockEntity.getHouseTypeFromBlock(entity.getBlockState().getBlock());
            rotation = entity.getBlockState().getValue(PixieHouseBlock.FACING).get2DDataValue() * 90;
        }
        if (entity == null) meta = PixieHouseBlockEntity.getHouseTypeFromBlock(this.metaOverride.getBlock());
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5F, 1.501F, 0.5F);
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
        if (entity != null && entity.getLevel() != null && entity.hasPixie) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0F, 0.95F, 0F);
            matrixStackIn.scale(0.55F, 0.55F, 0.55F);
            RenderType type = switch (entity.pixieType) {
                case 1 -> JarBlockEntityRenderer.TEXTURE_1;
                case 2 -> JarBlockEntityRenderer.TEXTURE_2;
                case 3 -> JarBlockEntityRenderer.TEXTURE_3;
                case 4 -> JarBlockEntityRenderer.TEXTURE_4;
                case 5 -> JarBlockEntityRenderer.TEXTURE_5;
                default -> JarBlockEntityRenderer.TEXTURE_0;
            };
            RenderType type2 = switch (entity.pixieType) {
                case 1 -> JarBlockEntityRenderer.TEXTURE_1_GLO;
                case 2 -> JarBlockEntityRenderer.TEXTURE_2_GLO;
                case 3 -> JarBlockEntityRenderer.TEXTURE_3_GLO;
                case 4 -> JarBlockEntityRenderer.TEXTURE_4_GLO;
                case 5 -> JarBlockEntityRenderer.TEXTURE_5_GLO;
                default -> JarBlockEntityRenderer.TEXTURE_0_GLO;
            };
            matrixStackIn.pushPose();
            this.pixieModel.animateInHouse(entity);
            this.pixieModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(type), combinedLightIn, combinedOverlayIn, -1);
            this.pixieModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(type2), combinedLightIn, combinedOverlayIn, -1);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
        RenderType pixieType = switch (meta) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            case 3 -> TEXTURE_3;
            case 4 -> TEXTURE_4;
            case 5 -> TEXTURE_5;
            default -> TEXTURE_0;
        };
        matrixStackIn.pushPose();
        MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(pixieType), combinedLightIn, combinedOverlayIn, -1);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }
}
