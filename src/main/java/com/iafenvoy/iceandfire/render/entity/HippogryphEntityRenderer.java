package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.HippogryphEntity;
import com.iafenvoy.iceandfire.render.model.HippogryphModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HippogryphEntityRenderer extends MobRenderer<HippogryphEntity, HippogryphModel> {
    public HippogryphEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HippogryphModel(), 0.8F);
        this.layers.add(new LayerHippogriffSaddle(this));
    }

    @Override
    protected void scale(@NotNull HippogryphEntity entity, PoseStack matrix, float partialTickTime) {
        matrix.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(HippogryphEntity entity) {
        return entity.getEnumVariant().getTexture(entity.isBlinking());
    }

    private static class LayerHippogriffSaddle extends RenderLayer<HippogryphEntity, HippogryphModel> {
        private final RenderType SADDLE_TEXTURE = RenderType.entityNoOutline(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/saddle.png"));
        private final RenderType BRIDLE = RenderType.entityNoOutline(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/bridle.png"));
        private final RenderType CHEST = RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/chest.png"));
        private final RenderType TEXTURE_IRON = RenderType.entityNoOutline(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_iron.png"));
        private final RenderType TEXTURE_GOLD = RenderType.entityNoOutline(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_gold.png"));
        private final RenderType TEXTURE_DIAMOND = RenderType.entityNoOutline(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_diamond.png"));
        private final RenderType TEXTURE_NETHERITE = RenderType.entityNoOutline(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hippogryph/armor_netherite.png"));

        public LayerHippogriffSaddle(HippogryphEntityRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, HippogryphEntity hippo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (hippo.getArmorValue() != 0) {
                RenderType type = switch (hippo.getArmorValue()) {
                    case 1 -> this.TEXTURE_IRON;
                    case 2 -> this.TEXTURE_GOLD;
                    case 3 -> this.TEXTURE_DIAMOND;
                    case 4 -> this.TEXTURE_NETHERITE;
                    default -> null;
                };
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(type);
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            }
            if (hippo.isSaddled()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.SADDLE_TEXTURE);
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            }
            if (hippo.isSaddled() && hippo.getControllingPassenger() != null) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.BRIDLE);
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            }
            if (hippo.isChested()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.CHEST);
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            }
        }
    }
}
