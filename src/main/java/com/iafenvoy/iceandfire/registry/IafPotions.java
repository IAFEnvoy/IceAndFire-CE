package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IafPotions {
    public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(Registries.POTION, IceAndFire.MOD_ID);
    public static final DeferredHolder<Potion, Potion> DEPETRIFICATION = REGISTRY.register("depetrification", () -> new Potion("depetrification", new MobEffectInstance(IafMobEffects.DEPETRIFICATION, 20 * 60)));

    private IafPotions() {
    }
}
