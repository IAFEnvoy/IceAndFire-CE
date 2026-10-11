package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityHippocampus;
import com.iafenvoy.iceandfire.render.model.ModelHippocampus;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Locale;


public class RenderHippocampus extends MobEntityRenderer<EntityHippocampus, ModelHippocampus> {
    private static final Identifier VARIANT_0 = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_0.png");
    private static final Identifier VARIANT_0_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_0_blinking.png");
    private static final Identifier VARIANT_1 = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_1.png");
    private static final Identifier VARIANT_1_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_1_blinking.png");
    private static final Identifier VARIANT_2 = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_2.png");
    private static final Identifier VARIANT_2_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_2_blinking.png");
    private static final Identifier VARIANT_3 = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_3.png");
    private static final Identifier VARIANT_3_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_3_blinking.png");
    private static final Identifier VARIANT_4 = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_4.png");
    private static final Identifier VARIANT_4_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_4_blinking.png");
    private static final Identifier VARIANT_5 = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_5.png");
    private static final Identifier VARIANT_5_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/hippocampus_5_blinking.png");

    public RenderHippocampus(EntityRendererFactory.Context context) {
        super(context, new ModelHippocampus(), 0.8F);
        this.features.add(new LayerHippocampusRainbow(this));
        this.features.add(new LayerHippocampusSaddle(this));
    }

    @Override
    public Identifier getTexture(EntityHippocampus entity) {
        return switch (entity.getVariant()) {
            case 1 -> entity.isBlinking() ? VARIANT_1_BLINK : VARIANT_1;
            case 2 -> entity.isBlinking() ? VARIANT_2_BLINK : VARIANT_2;
            case 3 -> entity.isBlinking() ? VARIANT_3_BLINK : VARIANT_3;
            case 4 -> entity.isBlinking() ? VARIANT_4_BLINK : VARIANT_4;
            case 5 -> entity.isBlinking() ? VARIANT_5_BLINK : VARIANT_5;
            default -> entity.isBlinking() ? VARIANT_0_BLINK : VARIANT_0;
        };
    }

    private static class LayerHippocampusSaddle extends FeatureRenderer<EntityHippocampus, ModelHippocampus> {
        private final RenderLayer SADDLE_TEXTURE = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/saddle.png"));
        private final RenderLayer BRIDLE = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/bridle.png"));
        private final RenderLayer CHEST = RenderLayer.getEntityTranslucent(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/chest.png"));
        private final RenderLayer TEXTURE_DIAMOND = RenderLayer.getEntityCutout(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/armor_diamond.png"));
        private final RenderLayer TEXTURE_GOLD = RenderLayer.getEntityCutout(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/armor_gold.png"));
        private final RenderLayer TEXTURE_IRON = RenderLayer.getEntityCutout(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/armor_iron.png"));

        public LayerHippocampusSaddle(RenderHippocampus renderer) {
            super(renderer);
        }

        @Override
        public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, EntityHippocampus hippo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
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
            if (hippo.getArmorTier() != 0) {
                RenderLayer type = switch (hippo.getArmorTier()) {
                    case 1 -> this.TEXTURE_IRON;
                    case 2 -> this.TEXTURE_GOLD;
                    case 3 -> this.TEXTURE_DIAMOND;
                    default -> null;
                };
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(type);
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    private static class LayerHippocampusRainbow extends FeatureRenderer<EntityHippocampus, ModelHippocampus> {
        private final RenderLayer TEXTURE = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/rainbow.png"));
        private final RenderLayer TEXTURE_BLINK = RenderLayer.getEntityNoOutline(Identifier.of(IceAndFire.MOD_ID, "textures/models/hippocampus/rainbow_blink.png"));

        public LayerHippocampusRainbow(RenderHippocampus renderer) {
            super(renderer);
        }

        @Override
        public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, EntityHippocampus hippo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            assert hippo.getCustomName() != null;
            if (hippo.hasCustomName() && hippo.getCustomName().toString().toLowerCase(Locale.ROOT).contains("rainbow")) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(hippo.isBlinking() ? this.TEXTURE_BLINK : this.TEXTURE);
                int i = hippo.age / 25 + hippo.getId();
                int j = DyeColor.values().length;
                int k = i % j;
                int l = (i + 1) % j;
                float f = ((float) (hippo.age % 25) + partialTicks) / 25.0F;
                float[] afloat1 = SheepEntity.getRgbColor(DyeColor.byId(k));
                float[] afloat2 = SheepEntity.getRgbColor(DyeColor.byId(l));
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlay(hippo, 0.0F), afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f, 1.0F);
            }
        }
    }
}
