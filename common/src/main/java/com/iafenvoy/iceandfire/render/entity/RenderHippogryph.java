package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityHippogryph;
import com.iafenvoy.iceandfire.render.model.ModelHippogryph;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RenderHippogryph extends MobEntityRenderer<EntityHippogryph, ModelHippogryph> {
    public RenderHippogryph(EntityRendererFactory.Context context) {
        super(context, new ModelHippogryph(), 0.8F);
        this.features.add(new LayerHippogriffSaddle(this));
    }

    @Override
    protected void scale(EntityHippogryph entity, MatrixStack matrix, float partialTickTime) {
        matrix.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public Identifier getTexture(EntityHippogryph entity) {
        return entity.getEnumVariant().getTexture(entity.isBlinking());
    }

    private static class LayerHippogriffSaddle extends FeatureRenderer<EntityHippogryph, ModelHippogryph> {
        private final RenderLayer SADDLE_TEXTURE = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippogryph/saddle.png"));
        private final RenderLayer BRIDLE = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippogryph/bridle.png"));
        private final RenderLayer CHEST = RenderLayer.getEntityTranslucent(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippogryph/chest.png"));
        private final RenderLayer TEXTURE_IRON = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippogryph/armor_iron.png"));
        private final RenderLayer TEXTURE_GOLD = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippogryph/armor_gold.png"));
        private final RenderLayer TEXTURE_DIAMOND = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippogryph/armor_diamond.png"));
        private final RenderLayer TEXTURE_NETHERITE = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippogryph/armor_netherite.png"));

        public LayerHippogriffSaddle(RenderHippogryph renderer) {
            super(renderer);
        }

        @Override
        public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, EntityHippogryph hippo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (hippo.getArmorTier() != 0) {
                RenderLayer type = switch (hippo.getArmorTier()) {
                    case 1 -> this.TEXTURE_IRON;
                    case 2 -> this.TEXTURE_GOLD;
                    case 3 -> this.TEXTURE_DIAMOND;
                    case 4 -> this.TEXTURE_NETHERITE;
                    default -> null;
                };
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(type);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (hippo.isSaddled()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.SADDLE_TEXTURE);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (hippo.isSaddled() && hippo.getControllingPassenger() != null) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.BRIDLE);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (hippo.isChested()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.CHEST);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
