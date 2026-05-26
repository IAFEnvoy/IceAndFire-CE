package com.iafenvoy.iceandfire.item.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface PostHitAbility extends Ability {
    void active(ItemStack stack, LivingEntity target, LivingEntity attacker);
}
