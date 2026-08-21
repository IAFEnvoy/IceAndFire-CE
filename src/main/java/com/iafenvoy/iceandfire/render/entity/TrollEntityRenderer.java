package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.TrollEntity;
import com.iafenvoy.iceandfire.render.entity.feature.TrollEyesFeatureRenderer;
import com.iafenvoy.iceandfire.render.entity.feature.TrollWeaponFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.TrollModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class TrollEntityRenderer extends LegacyMobRenderer<TrollEntity, TrollModel> {
    public TrollEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new TrollModel(), 0.9F);
        this.addLayer(new TrollEyesFeatureRenderer(this));
        this.addLayer(new TrollWeaponFeatureRenderer(this));
    }

    @Override
    public @NotNull Identifier getTextureLocation(TrollEntity troll) {
        return troll.getTrollType().getTexture();
    }
}
