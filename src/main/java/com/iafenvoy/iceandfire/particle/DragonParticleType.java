package com.iafenvoy.iceandfire.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public abstract class DragonParticleType<T extends DragonParticleType<T>> extends ParticleType<T> implements ParticleOptions {
    protected final float scale;

    public DragonParticleType(float scale) {
        super(false);
        this.scale = scale;
    }

    public float getScale() {
        return this.scale;
    }
}
