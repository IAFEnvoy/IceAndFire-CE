package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.render.entity.feature.*;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DragonBaseEntityRenderer<T extends DragonBaseEntity> extends MobRenderer<T, TabulaModel<T>> {
    public DragonBaseEntityRenderer(EntityRendererProvider.Context context, TabulaModel<T> model) {
        super(context, model, 0.0025F);
        this.addLayer(new DragonMaleOverlayFeatureRenderer<>(this));
        this.addLayer(new DragonEyesFeatureRenderer<>(this));
        this.addLayer(new DragonRiderFeatureRenderer<>(this, false));
        this.addLayer(new DragonBannerFeatureRenderer<>(this));
        this.addLayer(new DragonArmorFeatureRenderer<>(this));
    }

    @Override
    protected void scale(DragonBaseEntity entity, PoseStack matrixStackIn, float partialTickTime) {
        this.shadowRadius = entity.getRenderSize() / 3;
        float f7 = entity.prevDragonPitch + (entity.getDragonPitch() - entity.prevDragonPitch) * partialTickTime;
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(f7));
        matrixStackIn.scale(this.shadowRadius, this.shadowRadius, this.shadowRadius);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DragonBaseEntity entity) {
        return DragonColor.getById(entity.getVariant()).getTextureProvider().getTextureByEntity(entity);
    }
}
