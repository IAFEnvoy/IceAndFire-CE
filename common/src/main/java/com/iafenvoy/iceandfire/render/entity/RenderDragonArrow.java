package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityDragonArrow;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class RenderDragonArrow extends ProjectileEntityRenderer<EntityDragonArrow> {
    private static final Identifier TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/misc/dragonbone_arrow.png");

    public RenderDragonArrow(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(EntityDragonArrow entity) {
        return TEXTURE;
    }
}