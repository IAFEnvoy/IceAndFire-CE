package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.DragonEggEntity;
import com.iafenvoy.iceandfire.render.model.DragonEggModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class DragonEggEntityRenderer extends LegacyEntityModelRenderer<DragonEggEntity, DragonEggModel> {
    public DragonEggEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DragonEggModel(), 0.3F);
    }

    @Override
    public @NotNull Identifier getTextureLocation(DragonEggEntity entity) {
        return entity.getEggType().getTextureProvider().getEggTexture();
    }
}
