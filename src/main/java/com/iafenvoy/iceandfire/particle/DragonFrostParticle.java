package com.iafenvoy.iceandfire.particle;

import com.iafenvoy.uranus.object.VecUtil;
import com.iafenvoy.uranus.util.RandomHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DragonFrostParticle extends TextureSheetParticle {
    protected DragonFrostParticle(DragonFrostParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
        super(world, x, y, z);
        float size = parameters.getScale();
        this.quadSize *= (float) RandomHelper.nextDouble(size, size * 2);
        this.lifetime = 30;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.pickSprite(spriteProvider);
        this.setParticleSpeed(RandomHelper.randomize(velocityX, 0.5), RandomHelper.randomize(velocityY, 0.5), RandomHelper.randomize(velocityZ, 0.5));
    }

    public static ParticleProvider<DragonFrostParticleType> factory(SpriteSet spriteProvider) {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> new DragonFrostParticle(parameters, world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
    }

    @Override
    public int getLightColor(float partialTick) {
        int i = super.getLightColor(partialTick);
        int j = i & 255;
        int k = i >> 16 & 255;
        if (j > 240) j = 240;
        return j | k << 16;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick() {
        super.tick();
        BlockState state = this.level.getBlockState(VecUtil.createBlockPos(this.x, this.y, this.z));
        if (state.isSolid())
            this.remove();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    protected record Provider(SpriteSet spriteProvider) implements ParticleProvider<DragonFrostParticleType> {
        @Override
        public Particle createParticle(@NotNull DragonFrostParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new DragonFrostParticle(typeIn, worldIn, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}
