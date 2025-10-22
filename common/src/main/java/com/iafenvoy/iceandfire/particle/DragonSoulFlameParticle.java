package com.iafenvoy.iceandfire.particle;

import com.iafenvoy.uranus.object.VecUtil;
import com.iafenvoy.uranus.util.RandomHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.NotNull;

public class DragonSoulFlameParticle extends SpriteBillboardParticle {
    protected DragonSoulFlameParticle(DragonSoulFlameParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        float size = parameters.getScale();
        this.scale *= (float) RandomHelper.nextDouble(size, size * 2);
        this.maxAge = 30;
        this.gravityStrength = 0.0F;
        this.collidesWithWorld = false;
        this.setVelocity(RandomHelper.randomize(velocityX, 0.5), RandomHelper.randomize(velocityY, 0.5), RandomHelper.randomize(velocityZ, 0.5));
        this.setSprite(spriteProvider);
    }

    public static ParticleFactory<DragonSoulFlameParticleType> factory(SpriteProvider spriteProvider) {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> new DragonSoulFlameParticle(parameters, world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
    }

    @Override
    public int getBrightness(float partialTick) {
        return 240;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick() {
        super.tick();
        BlockState state = this.world.getBlockState(VecUtil.createBlockPos(this.x, this.y, this.z));
        if (state != null && state.isSolid())
            this.markDead();
    }

    @Override
    public @NotNull ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }
}
