package com.iafenvoy.iceandfire.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class BloodParticle extends SingleQuadParticle {
    protected BloodParticle(ClientLevel world, double x, double y, double z, TextureAtlasSprite sprite) {
        super(world, x, y, z, 0, Math.random() * (double) 0.2F + 0.1, 0, sprite);
        this.setPos(x, y, z);
        this.yd += 0.01D;
    }

    public static ParticleProvider<SimpleParticleType> factory(SpriteSet spriteProvider) {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ, random) -> new BloodParticle(world, x, y, z, spriteProvider.get(random));
    }

    @Override
    public int getLightCoords(float tint) {
        return 15728880;
    }

    @Override
    public @NotNull Layer getLayer() {
        return Layer.TRANSLUCENT;
    }
}
