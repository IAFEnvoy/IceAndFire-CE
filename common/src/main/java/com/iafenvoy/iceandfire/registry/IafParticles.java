package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.particle.DragonFlameParticleType;
import com.iafenvoy.iceandfire.particle.DragonFrostParticleType;
import com.iafenvoy.iceandfire.particle.DragonSoulFlameParticleType;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Supplier;

public final class IafParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.PARTICLE_TYPE);
    public static final RegistrySupplier<SimpleParticleType> BLOOD = register("blood", () -> new SimpleParticleType(true));
    public static final RegistrySupplier<DragonFlameParticleType> DRAGON_FLAME = register("dragon_flame", DragonFlameParticleType::new);
    public static final RegistrySupplier<DragonFrostParticleType> DRAGON_FROST = register("dragon_frost", DragonFrostParticleType::new);
    public static final RegistrySupplier<DragonSoulFlameParticleType> DRAGON_SOUL_FLAME = register("dragon_soul_flame", DragonSoulFlameParticleType::new);
    public static final RegistrySupplier<SimpleParticleType> DREAD_PORTAL = register("dread_portal", () -> new SimpleParticleType(true));
    public static final RegistrySupplier<SimpleParticleType> DREAD_TORCH = register("dread_torch", () -> new SimpleParticleType(true));
    public static final RegistrySupplier<SimpleParticleType> GHOST_APPEARANCE = register("ghost_appearance", () -> new SimpleParticleType(true));
    public static final RegistrySupplier<SimpleParticleType> HYDRA_BREATH = register("hydra_breath", () -> new SimpleParticleType(true));
    public static final RegistrySupplier<SimpleParticleType> PIXIE_DUST = register("pixie_dust", () -> new SimpleParticleType(true));
    public static final RegistrySupplier<SimpleParticleType> SERPENT_BUBBLE = register("serpent_bubble", () -> new SimpleParticleType(true));
    public static final RegistrySupplier<SimpleParticleType> SIREN_APPEARANCE = register("siren_appearance", () -> new SimpleParticleType(true));
    public static final RegistrySupplier<SimpleParticleType> SIREN_MUSIC = register("siren_music", () -> new SimpleParticleType(true));

    private static <T extends ParticleType<?>> RegistrySupplier<T> register(String name, Supplier<T> obj) {
        return REGISTRY.register(name, obj);
    }
}
