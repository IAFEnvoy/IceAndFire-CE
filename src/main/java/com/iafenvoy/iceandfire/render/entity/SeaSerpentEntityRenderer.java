package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.SeaSerpentEntity;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.render.entity.feature.SeaSerpentAncientGeoLayer;
import com.iafenvoy.iceandfire.render.model.SeaSerpentGeoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SeaSerpentEntityRenderer extends GeoEntityRenderer<SeaSerpentEntity> {
    private static final float MODEL_VERTICAL_OFFSET = -0.50F;

    public SeaSerpentEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SeaSerpentGeoModel<>(serpent -> IafRegistries.SEA_SERPENT_TYPE.get(IceAndFire.id(serpent.getVariant())).getTexture(serpent.isBlinking())));
        this.shadowRadius = 1.6F;
        this.addRenderLayer(new SeaSerpentAncientGeoLayer(this));
    }

    @Override
    public void preRender(PoseStack poseStack, SeaSerpentEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        this.shadowRadius = entity.getSeaSerpentScale();
        if (!isReRender) {
            poseStack.scale(this.shadowRadius, this.shadowRadius, this.shadowRadius);
            // Align the Geo root with the scaled multipart hitbox centre.
            poseStack.translate(0.0F, MODEL_VERTICAL_OFFSET, 0.0F);
        }
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(SeaSerpentEntity serpent) {
        return IafRegistries.SEA_SERPENT_TYPE.get(IceAndFire.id(serpent.getVariant())).getTexture(serpent.isBlinking());
    }
}
