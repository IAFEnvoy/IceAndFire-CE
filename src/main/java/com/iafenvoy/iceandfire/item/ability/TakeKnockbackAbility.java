package com.iafenvoy.iceandfire.item.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TakeKnockbackAbility implements PostHitAbility {
    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.knockback(1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
    }
}
