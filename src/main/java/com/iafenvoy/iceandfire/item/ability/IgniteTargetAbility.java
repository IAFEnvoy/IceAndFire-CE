package com.iafenvoy.iceandfire.item.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record IgniteTargetAbility(int fireTime) implements PostHitAbility {
    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.isEnable()) {
            target.igniteForSeconds(this.fireTime);
        }
    }
}
