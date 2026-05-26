package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.particle.DragonFlameParticleType;
import com.iafenvoy.iceandfire.particle.DragonFrostParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class IafParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, IceAndFire.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD = register("blood", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, DragonFlameParticleType> DRAGON_FLAME = register("dragon_flame", DragonFlameParticleType::new);
    public static final DeferredHolder<ParticleType<?>, DragonFrostParticleType> DRAGON_FROST = register("dragon_frost", DragonFrostParticleType::new);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DREAD_PORTAL = register("dread_portal", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DREAD_TORCH = register("dread_torch", () -> new SimpleParticleType(true));
    //TODO::What is this?
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GHOST_APPEARANCE = register("ghost_appearance", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HYDRA_BREATH = register("hydra_breath", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PIXIE_DUST = register("pixie_dust", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SERPENT_BUBBLE = register("serpent_bubble", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SIREN_MUSIC = register("siren_music", () -> new SimpleParticleType(true));

    private static <T extends ParticleType<?>> DeferredHolder<ParticleType<?>, T> register(String name, Supplier<T> obj) {
        return REGISTRY.register(name, obj);
    }
}
