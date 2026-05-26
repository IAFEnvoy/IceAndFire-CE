package com.iafenvoy.iceandfire.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class DreadTorchParticle extends TextureSheetParticle {
    protected DreadTorchParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.setPos(x, y, z);
        this.yd += 0.01D;
        this.pickSprite(spriteProvider);
    }

    public static ParticleProvider<SimpleParticleType> factory(SpriteSet spriteProvider) {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> new DreadTorchParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
    }

    @Override
    public void render(@NotNull VertexConsumer consumer, @NotNull Camera camera, float tickDelta) {
        this.quadSize = 0.125F * (this.lifetime - (this.age));
        this.quadSize = this.quadSize * 0.09F;
        this.xd *= 0.75D;
        this.yd *= 0.75D;
        this.zd *= 0.75D;
        super.render(consumer, camera, tickDelta);
    }

    @Override
    public int getLightColor(float tint) {
        return 240;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }
}
