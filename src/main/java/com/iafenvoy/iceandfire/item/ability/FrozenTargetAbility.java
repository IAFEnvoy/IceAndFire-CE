package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.registry.IafMobEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record FrozenTargetAbility(int duration) implements PostHitAbility {
    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, this.duration, 2));
        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, this.duration, 2));
        target.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(IafMobEffects.FROZEN.get()), this.duration));
    }
}
