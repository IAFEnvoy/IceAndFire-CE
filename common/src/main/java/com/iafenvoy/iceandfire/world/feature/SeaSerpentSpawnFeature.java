package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.entity.SeaSerpentEntity;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.iceandfire.world.feature.config.EntitySpawnFeatureConfig;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SeaSerpentSpawnFeature extends Feature<EntitySpawnFeatureConfig> implements DangerousGeneration {
    public SeaSerpentSpawnFeature() {
        super(EntitySpawnFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<EntitySpawnFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, context.getOrigin().add(8, 0, 8));
        BlockPos oceanPos = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, pos.add(8, 0, 8));
        if (this.isFarEnoughFromSpawn(world, pos) && random.nextDouble() < context.getConfig().spawnChance()) {
            BlockPos spawnPos = oceanPos.add(random.nextInt(10) - 5, random.nextInt(30), random.nextInt(10) - 5);
            if (world.getFluidState(spawnPos).getFluid() == Fluids.WATER) {
                Entity entity = context.getConfig().entityType().create(world.toServerWorld());
                if (entity != null) {
                    if (entity instanceof SeaSerpentEntity serpent)
                        serpent.onWorldSpawn(random);
                    entity.refreshPositionAndAngles(spawnPos.getX() + 0.5F, spawnPos.getY() + 0.5F, spawnPos.getZ() + 0.5F, 0, 0);
                    world.spawnEntity(entity);
                }
            }
        }
        return true;
    }
}
