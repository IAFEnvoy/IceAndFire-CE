package com.iafenvoy.iceandfire.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class HydraBreathParticle extends SingleQuadParticle {
    private final float newScale;

    protected HydraBreathParticle(ClientLevel world, double x, double y, double z, TextureAtlasSprite sprite) {
        super(world, x, y, z, 0, 0, 0, sprite);
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;
        this.newScale = this.quadSize;
    }

    public static ParticleProvider<SimpleParticleType> factory(SpriteSet spriteProvider) {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ, random) -> new HydraBreathParticle(world, x, y, z, spriteProvider.get(random));
    }

    @Override
    public float getQuadSize(float tickDelta) {
        float scaley = ((float) this.age + tickDelta) / (float) this.lifetime * 32.0F;
        scaley = Mth.clamp(scaley, 0.0F, 1.0F);
        return this.newScale * scaley;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return super.getLightCoords(partialTick);
    }

    @Override
    public @NotNull Layer getLayer() {
        return Layer.TRANSLUCENT;
    }
}
