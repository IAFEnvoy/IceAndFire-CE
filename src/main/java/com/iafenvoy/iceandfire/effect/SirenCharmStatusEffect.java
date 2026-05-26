package com.iafenvoy.iceandfire.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SirenCharmStatusEffect extends MobEffect {
    public SirenCharmStatusEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFC0CB, ParticleTypes.HEART);
    }
}
