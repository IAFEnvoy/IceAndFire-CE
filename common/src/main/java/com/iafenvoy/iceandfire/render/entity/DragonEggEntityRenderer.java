package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.DragonEggEntity;
import com.iafenvoy.iceandfire.render.model.DragonEggModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

public class DragonEggEntityRenderer extends LivingEntityRenderer<DragonEggEntity, DragonEggModel<DragonEggEntity>> {
    public DragonEggEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DragonEggModel<>(), 0.3F);
    }

    @Override
    public Identifier getTexture(DragonEggEntity entity) {
        return entity.getEggType().getEggTexture();
    }
}
