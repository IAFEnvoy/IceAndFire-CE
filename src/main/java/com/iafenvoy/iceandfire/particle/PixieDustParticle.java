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

public class PixieDustParticle extends TextureSheetParticle {
    private final float newScale;

    protected PixieDustParticle(ClientLevel world, double x, double y, double z, float scale, float red, float green, float blue, SpriteSet spriteProvider) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;
        float f = (float) Math.random() * 0.4F + 0.6F;
        this.rCol = ((float) (Math.random() * 0.2) + 0.8F) * red * f;
        this.gCol = ((float) (Math.random() * 0.2) + 0.8F) * green * f;
        this.bCol = ((float) (Math.random() * 0.2) + 0.8F) * blue * f;
        this.quadSize *= scale;
        this.newScale = this.quadSize;
        this.lifetime = (int) (this.lifetime * scale);
        this.pickSprite(spriteProvider);
    }

    public static ParticleProvider<SimpleParticleType> factory(SpriteSet spriteProvider) {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> new PixieDustParticle(world, x, y, z, 1, 1, 1, 1, spriteProvider);
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
        return 240;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }
}
