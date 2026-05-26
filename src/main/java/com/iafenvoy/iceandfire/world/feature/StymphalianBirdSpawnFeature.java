package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.StymphalianBirdEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class StymphalianBirdSpawnFeature extends Feature<NoneFeatureConfiguration> implements DangerousGeneration {
    public StymphalianBirdSpawnFeature(Codec<NoneFeatureConfiguration> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        RandomSource random = context.random();
        BlockPos pos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, context.origin().offset(8, 0, 8));
        if (this.isFarEnoughFromSpawn(world, pos) && random.nextDouble() < IafCommonConfig.INSTANCE.stymphalianBird.spawnChance.getValue())
            for (int i = 0; i < 4 + random.nextInt(4); i++) {
                BlockPos spawnPos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos.offset(random.nextInt(10) - 5, 0, random.nextInt(10) - 5));
                if (world.getBlockState(spawnPos.below()).canOcclude()) {
                    StymphalianBirdEntity bird = IafEntities.STYMPHALIAN_BIRD.get().create(world.getLevel());
                    assert bird != null;
                    bird.moveTo(spawnPos.getX() + 0.5F, spawnPos.getY() + 1.5F, spawnPos.getZ() + 0.5F, 0, 0);
                    world.addFreshEntity(bird);
                }
            }
        return true;
    }
}
