package com.iafenvoy.iceandfire.entity;

import net.minecraft.world.entity.LivingEntity;

/**
 * Collision-only part that mirrors the parent's own bounding box.
 * <p>
 * The parent keeps its own hitbox; this part only makes that box reachable through
 * {@link net.minecraft.world.entity.Entity#getParts()}. Mods that treat the parts of a multipart entity as the
 * complete set of hitboxes of the creature (the same way the vanilla Ender Dragon is built) never test the
 * parent's own box, so without a body part they cannot hit anything but the extra parts.
 */
public class BodyPartEntity<T extends LivingEntity> extends MultipartPartEntity<T> {
    public BodyPartEntity(T parent) {
        super(parent, 0.0F, 0.0F, 0.0F, parent.getBbWidth(), parent.getBbHeight(), 1.0F);
    }

    @Override
    public void collideWithNearbyEntities() {
        // The parent already pushes entities out of this exact box, doing it a second time would double the push.
    }
}
