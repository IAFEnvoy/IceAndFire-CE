package com.iafenvoy.iceandfire.effect;

import com.iafenvoy.iceandfire.entity.IceDragonEntity;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FrozenStatusEffect extends MobEffect {
    public FrozenStatusEffect() {
        super(MobEffectCategory.NEUTRAL, 0xFFB9CDF6);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity instanceof IceDragonEntity || entity.isDeadOrDying()) return false;
        else if (entity.isOnFire()) {
            entity.clearFire();
            return false;
        } else if (!(entity instanceof Player player && player.isCreative())) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.25F, 1, 0.25F));
            if (!(entity instanceof EnderDragon) && !entity.onGround())
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.2, 0));
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onEffectStarted(@NotNull LivingEntity entity, int amplifier) {
        playSound(entity, SoundEvents.GLASS_PLACE, 1);
    }

    //Vanilla don't have this, so we need to do with mixin
    public void onRemoved(LivingEntity entity) {
        if (entity.level() instanceof ServerLevel serverWorld)
            for (int i = 0; i < 15; i++)
                serverWorld.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, IafBlocks.DRAGON_ICE.get().defaultBlockState()),
                        entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * entity.getBbWidth(),
                        entity.getY() + entity.getRandom().nextDouble() * entity.getBbHeight(),
                        entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * entity.getBbWidth(),
                        0, 0, 0, 0, 0);
        playSound(entity, SoundEvents.GLASS_BREAK, 3);
    }

    private static void playSound(LivingEntity entity, SoundEvent sound, int volume) {
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundSource.NEUTRAL, volume, 1);
    }
}
