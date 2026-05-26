package com.iafenvoy.iceandfire.impl;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class ParticleProviderHolder<T extends ParticleOptions> {
    private final ParticleType<T> type;
    @Nullable
    private final ParticleProvider<T> commonFactory;
    @Nullable
    private final ParticleEngine.SpriteParticleRegistration<T> extendedFactory;

    public ParticleProviderHolder(ParticleType<T> type, @NotNull ParticleProvider<T> factory) {
        this.type = type;
        this.commonFactory = factory;
        this.extendedFactory = null;
    }

    public ParticleProviderHolder(ParticleType<T> type, @NotNull ParticleEngine.SpriteParticleRegistration<T> factory) {
        this.type = type;
        this.commonFactory = null;
        this.extendedFactory = factory;
    }

    public void applyRegister(BiConsumer<ParticleType<T>, ParticleProvider<T>> common, BiConsumer<ParticleType<T>, ParticleEngine.SpriteParticleRegistration<T>> extended) {
        if (this.commonFactory != null) common.accept(this.type, this.commonFactory);
        if (this.extendedFactory != null) extended.accept(this.type, this.extendedFactory);
    }
}
