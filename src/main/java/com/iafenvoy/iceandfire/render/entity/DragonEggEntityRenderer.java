package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.DragonEggEntity;
import com.iafenvoy.iceandfire.render.model.DragonEggModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DragonEggEntityRenderer extends LivingEntityRenderer<DragonEggEntity, DragonEggModel> {
    public DragonEggEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DragonEggModel(), 0.3F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DragonEggEntity entity) {
        return entity.getEggType().getTextureProvider().getEggTexture();
    }
}
