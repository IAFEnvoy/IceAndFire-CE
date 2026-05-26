package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.TrollEntity;
import com.iafenvoy.iceandfire.render.entity.feature.TrollEyesFeatureRenderer;
import com.iafenvoy.iceandfire.render.entity.feature.TrollWeaponFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.TrollModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TrollEntityRenderer extends MobRenderer<TrollEntity, TrollModel> {
    public TrollEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new TrollModel(), 0.9F);
        this.layers.add(new TrollWeaponFeatureRenderer(this));
        this.layers.add(new TrollEyesFeatureRenderer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(TrollEntity troll) {
        return troll.getTrollType().getTexture();
    }
}
