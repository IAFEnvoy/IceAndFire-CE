package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.render.entity.feature.*;
import com.iafenvoy.uranus.client.model.TabulaModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class DragonBaseEntityRenderer<T extends DragonBaseEntity> extends MobEntityRenderer<T, TabulaModel<T>> {
    public DragonBaseEntityRenderer(EntityRendererFactory.Context context, TabulaModel<T> model) {
        super(context, model, 0.025F);
        this.addFeature(new DragonMaleOverlayFeatureRenderer<>(this));
        this.addFeature(new DragonEyesFeatureRenderer<>(this));
        this.addFeature(new DragonRiderFeatureRenderer<>(this, false));
        this.addFeature(new DragonBannerFeatureRenderer<>(this));
        this.addFeature(new DragonArmorFeatureRenderer<>(this));
    }

    @Override
    protected void scale(DragonBaseEntity entity, MatrixStack matrixStackIn, float partialTickTime) {
        this.shadowRadius = entity.getRenderSize() / 3;
        float f7 = entity.prevDragonPitch + (entity.getDragonPitch() - entity.prevDragonPitch) * partialTickTime;
        matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f7));
        matrixStackIn.scale(this.shadowRadius, this.shadowRadius, this.shadowRadius);
    }

    @Override
    public Identifier getTexture(DragonBaseEntity entity) {
        DragonColor color = DragonColor.getById(entity.getVariant());
        return color.getTextureByEntity(entity);
    }
}
