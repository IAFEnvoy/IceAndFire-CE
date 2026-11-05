package com.iafenvoy.iceandfire.item.ability;

import net.minecraft.world.entity.LivingEntity;

public interface SwingHandAbility extends Ability {
    void active(LivingEntity entity);

    default void active(LivingEntity entity, net.minecraft.world.InteractionHand hand) {
        this.active(entity);
    }
}
