package com.iafenvoy.iceandfire.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class SerpentBubbleParticle extends SingleQuadParticle {
    protected SerpentBubbleParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, TextureAtlasSprite sprite) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, sprite);
        this.setPos(x, y, z);
        this.quadSize = 0.3F;
    }

    public static ParticleProvider<SimpleParticleType> factory(SpriteSet spriteProvider) {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ, random) -> new SerpentBubbleParticle(world, x, y, z, 1, 1, 1, spriteProvider.get(random));
    }

    @Override
    public int getLightCoords(float partialTick) {
        return 15728880;
    }

    @Override
    public @NotNull Layer getLayer() {
        return Layer.TRANSLUCENT;
    }
}
