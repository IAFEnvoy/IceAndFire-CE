package com.iafenvoy.iceandfire.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class HydraBreathParticle extends TextureSheetParticle {
    private final float newScale;

    protected HydraBreathParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, 0, 0, 0);
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;
        this.newScale = this.quadSize;
        this.pickSprite(spriteProvider);
    }

    public static ParticleProvider<SimpleParticleType> factory(SpriteSet spriteProvider) {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> new HydraBreathParticle(world, x, y, z, spriteProvider);
    }

    @Override
    public void render(@NotNull VertexConsumer consumer, @NotNull Camera camera, float tickDelta) {
        float scaley = ((float) this.age + tickDelta) / (float) this.lifetime * 32.0F;
        scaley = Mth.clamp(scaley, 0.0F, 1.0F);
        this.quadSize = this.newScale * scaley;
        super.render(consumer, camera, tickDelta);
    }

    @Override
    public int getLightColor(float partialTick) {
        return super.getLightColor(partialTick);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }
}
