package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.EntityDragonEgg;
import com.iafenvoy.iceandfire.render.model.ModelDragonEgg;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

public class RenderDragonEgg extends LivingEntityRenderer<EntityDragonEgg, ModelDragonEgg<EntityDragonEgg>> {
    //TODO: Unused

    public RenderDragonEgg(EntityRendererFactory.Context context) {
        super(context, new ModelDragonEgg<>(), 0.3F);
    }

    @Override
    public Identifier getTexture(EntityDragonEgg entity) {
        return entity.getEggType().getEggTexture();
    }
}
