package com.iafenvoy.iceandfire.item.ability;

import net.minecraft.entity.LivingEntity;

public interface IgniteTargetAbility extends PostHitAbility {
    int getFireTime();

    @Override
    default void active(LivingEntity target, LivingEntity attacker) {
        if (isEnable()) {
            target.setOnFireFor(getFireTime());
        }
    }
}
