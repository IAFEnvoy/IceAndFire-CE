package com.iafenvoy.iceandfire.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class DreadTorchParticle extends SingleQuadParticle {
    protected DreadTorchParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, TextureAtlasSprite sprite) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, sprite);
        this.setPos(x, y, z);
        this.yd += 0.01D;
    }

    public static ParticleProvider<SimpleParticleType> factory(SpriteSet spriteProvider) {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ, random) -> new DreadTorchParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.get(random));
    }

    @Override
    public float getQuadSize(float tickDelta) {
        return 0.01125F * (this.lifetime - this.age);
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
