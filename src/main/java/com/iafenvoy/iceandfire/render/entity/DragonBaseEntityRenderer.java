package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.render.entity.feature.*;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DragonBaseEntityRenderer<T extends DragonBaseEntity> extends LegacyMobRenderer<T, TabulaModel<T>> {
    private boolean layersAdded;
    public DragonBaseEntityRenderer(EntityRendererProvider.Context context, TabulaModel<T> model) {
        super(context, model, 0.0025F);
        if (model != null) this.addDragonLayers();
    }

    public DragonBaseEntityRenderer(EntityRendererProvider.Context context, Supplier<TabulaModel<T>> modelSupplier) {
        super(context, modelSupplier, 0.0025F);
    }

    @Override
    protected void onModelAvailable() {
        this.addDragonLayers();
    }

    private void addDragonLayers() {
        if (this.layersAdded) return;
        this.layersAdded = true;
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
    public @NotNull Identifier getTextureLocation(DragonBaseEntity entity) {
        return DragonColor.getById(entity.getVariant()).getTextureProvider().getTextureByEntity(entity);
    }
}
