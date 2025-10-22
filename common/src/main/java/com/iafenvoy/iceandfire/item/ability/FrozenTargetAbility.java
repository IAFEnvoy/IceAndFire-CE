package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.data.component.FrozenData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;

public record FrozenTargetAbility(int duration) implements PostHitAbility {
    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        FrozenData.get(target).setFrozen(target, this.duration);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, this.duration, 2));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, this.duration, 2));
    }
}
