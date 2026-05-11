package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.iceandfire.world.feature.config.StymphalianBirdFeatureConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class StymphalianBirdSpawnFeature extends Feature<StymphalianBirdFeatureConfig> implements DangerousGeneration {
    public StymphalianBirdSpawnFeature() {
        super(StymphalianBirdFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<StymphalianBirdFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        StymphalianBirdFeatureConfig config = context.getConfig();
        BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, context.getOrigin().add(8, 0, 8));
        if (this.isFarEnoughFromSpawn(world, pos) && random.nextDouble() < config.spawnChance())
            for (int i = 0; i < config.flockMin() + random.nextInt(config.flockMax() - config.flockMin() + 1); i++) {
                BlockPos spawnPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, pos.add(random.nextInt(10) - 5, 0, random.nextInt(10) - 5));
                if (world.getBlockState(spawnPos.down()).isOpaque()) {
                    Entity entity = IafEntities.STYMPHALIAN_BIRD.get().create(world.toServerWorld());
                    if (entity != null) {
                        entity.refreshPositionAndAngles(spawnPos.getX() + 0.5F, spawnPos.getY() + 1.5F, spawnPos.getZ() + 0.5F, 0, 0);
                        world.spawnEntity(entity);
                    }
                }
            }
        return true;
    }
}
