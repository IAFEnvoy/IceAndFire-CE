package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.render.entity.feature.DragonGeoOverlayLayer;
import com.iafenvoy.iceandfire.render.entity.feature.DragonGeoAttachmentLayer;
import com.iafenvoy.iceandfire.render.model.DragonGeoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DragonBaseEntityRenderer<T extends DragonBaseEntity> extends GeoEntityRenderer<T> {
    public DragonBaseEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DragonGeoModel<>());
        this.shadowRadius = 0.0025F;
        this.addRenderLayer(new DragonGeoOverlayLayer<>(this));
        this.addRenderLayer(new DragonGeoAttachmentLayer<>(this));
    }

    @Override
    public void preRender(PoseStack poseStack, T entity, BakedGeoModel model, MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        this.shadowRadius = entity.getRenderSize() / 3;
        if (!isReRender) {
            float pitch = entity.prevDragonPitch + (entity.getDragonPitch() - entity.prevDragonPitch) * partialTick;
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
            poseStack.scale(this.shadowRadius, this.shadowRadius, this.shadowRadius);
        }
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(T entity) {
        return DragonColor.getById(entity.getVariant()).getTextureProvider().getTextureByEntity(entity);
    }
}
