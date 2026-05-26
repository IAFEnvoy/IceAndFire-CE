package com.iafenvoy.iceandfire.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SirenMusicParticle extends TextureSheetParticle {
    private float colorScale;

    protected SirenMusicParticle(ClientLevel world, double x, double y, double z, double motX, double motY, double motZ, SpriteSet spriteProvider) {
        super(world, x, y, z, motX, motY, motZ);
        this.setPos(x, y, z);
        this.colorScale = (float) 1;
        this.rCol = Math.max(0, Mth.sin((this.colorScale + 0.0F) * 6.2831855F) * 0.65F + 0.35F);
        this.gCol = Math.max(0, Mth.sin((this.colorScale + 0.33333334F) * 6.2831855F) * 0.65F + 0.35F);
        this.bCol = Math.max(0, Mth.sin((this.colorScale + 0.6666667F) * 6.2831855F) * 0.65F + 0.35F);
        this.pickSprite(spriteProvider);
    }

    public static ParticleProvider<SimpleParticleType> factory(SpriteSet spriteProvider) {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> new SirenMusicParticle(world, x, y, z, 1, 1, 1, spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.colorScale += 0.015F;
        if (this.colorScale > 25) this.colorScale = 0;
        this.rCol = Math.max(0.0F, Mth.sin((this.colorScale + 0.0F) * 6.2831855F) * 0.65F + 0.35F);
        this.gCol = Math.max(0.0F, Mth.sin((this.colorScale + 0.33333334F) * 6.2831855F) * 0.65F + 0.35F);
        this.bCol = Math.max(0.0F, Mth.sin((this.colorScale + 0.6666667F) * 6.2831855F) * 0.65F + 0.35F);
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