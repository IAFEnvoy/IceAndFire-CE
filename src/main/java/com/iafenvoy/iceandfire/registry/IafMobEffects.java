package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.effect.FrozenStatusEffect;
import com.iafenvoy.iceandfire.effect.SirenCharmStatusEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class IafMobEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, IceAndFire.MOD_ID);

    public static final DeferredHolder<MobEffect, FrozenStatusEffect> FROZEN = register("frozen", FrozenStatusEffect::new);
    public static final DeferredHolder<MobEffect, SirenCharmStatusEffect> SIREN_CHARM = register("siren_charm", SirenCharmStatusEffect::new);

    private static <T extends MobEffect> DeferredHolder<MobEffect, T> register(String name, Supplier<T> obj) {
        return REGISTRY.register(name, obj);
    }
}
