package com.iafenvoy.iceandfire.render.entity;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class NothingEntityRenderer<T extends Entity> extends EntityRenderer<T> {
    public NothingEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    // Only render if the debug bboxes are enabled
    @Override
    public boolean shouldRender(T livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (!this.dispatcher.shouldRenderHitboxes()) return false;
        return super.shouldRender(livingEntityIn, camera, camX, camY, camZ);
    }

    @Override
    public Identifier getTexture(Entity entity) {
        return null;
    }
}
