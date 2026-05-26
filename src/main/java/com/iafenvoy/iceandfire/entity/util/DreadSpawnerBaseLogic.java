package com.iafenvoy.iceandfire.entity.util;

import com.iafenvoy.iceandfire.registry.IafParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

// This class only servers the point of changing the particles spawned
public abstract class DreadSpawnerBaseLogic extends BaseSpawner {
    private short spawnDelay = 20;
    private double spin;
    private double oSpin;

    @Override
    public void clientTick(@NotNull Level world, @NotNull BlockPos pos) {
        if (!this.isNearPlayer(world, pos))
            this.oSpin = this.spin;
        else {
            double d0 = (double) pos.getX() + world.random.nextDouble();
            double d1 = (double) pos.getY() + world.random.nextDouble();
            double d2 = (double) pos.getZ() + world.random.nextDouble();
            world.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            world.addParticle(IafParticles.DREAD_TORCH.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
            if (this.spawnDelay > 0) --this.spawnDelay;

            this.oSpin = this.spin;
            this.spin = (this.spin + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
        }
    }

    private boolean isNearPlayer(Level world, BlockPos pos) {
        return world.hasNearbyAlivePlayer((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 20);
    }

    @Override
    public double getSpin() {
        return this.spin;
    }

    @Override
    public double getoSpin() {
        return this.oSpin;
    }
}
