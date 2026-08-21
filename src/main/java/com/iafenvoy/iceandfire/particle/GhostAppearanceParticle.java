package com.iafenvoy.iceandfire.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.SimpleParticleType;
import org.jspecify.annotations.NonNull;

public class GhostAppearanceParticle extends Particle {
    private final GhostModel model = new GhostModel(0.0F);
    private final int ghost;
    private final boolean fromLeft;

    protected GhostAppearanceParticle(ClientLevel world, double x, double y, double z, int ghost) {
        super(world, x, y, z);
        this.gravity = 0.0F;
        this.lifetime = 15;
        this.ghost = ghost;
        this.fromLeft = this.random.nextBoolean();
    }

    public static ParticleProvider<SimpleParticleType> factory() {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ, random) -> new GhostAppearanceParticle(world, x, y, z, 1);
    }

    @Override
    public @NonNull ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}

