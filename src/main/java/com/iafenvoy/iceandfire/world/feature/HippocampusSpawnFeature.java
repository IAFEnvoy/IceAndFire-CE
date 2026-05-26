package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.HippocampusEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class HippocampusSpawnFeature extends Feature<NoneFeatureConfiguration> {
    public HippocampusSpawnFeature(Codec<NoneFeatureConfiguration> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        RandomSource random = context.random();
        BlockPos pos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, context.origin().offset(8, 0, 8));
        BlockPos oceanPos = world.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, pos.offset(8, 0, 8));
        if (random.nextDouble() < IafCommonConfig.INSTANCE.hippocampus.spawnChance.getValue()) {
            for (int i = 0; i < random.nextInt(5); i++) {
                BlockPos spawnPos = oceanPos.offset(random.nextInt(10) - 5, random.nextInt(30), random.nextInt(10) - 5);
                if (world.getFluidState(spawnPos).getType() == Fluids.WATER) {
                    HippocampusEntity campus = IafEntities.HIPPOCAMPUS.get().create(world.getLevel());
                    assert campus != null;
                    campus.setVariant(random.nextInt(6));
                    campus.moveTo(spawnPos.getX() + 0.5F, spawnPos.getY() + 0.5F, spawnPos.getZ() + 0.5F, 0, 0);
                    world.addFreshEntity(campus);
                }
            }
        }
        return true;
    }
}
