package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DeathWormEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DeathWormSpawnFeature extends Feature<NoneFeatureConfiguration> implements DangerousGeneration {
    public DeathWormSpawnFeature(Codec<NoneFeatureConfiguration> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos pos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, context.origin().offset(8, 0, 8));
        if (this.isFarEnoughFromSpawn(world, pos) && context.random().nextDouble() < IafCommonConfig.INSTANCE.deathworm.spawnChance.getValue()) {
            DeathWormEntity deathWorm = IafEntities.DEATH_WORM.get().create(world.getLevel());
            assert deathWorm != null;
            deathWorm.setPos(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F);
            deathWorm.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), MobSpawnType.CHUNK_GENERATION, null);
            world.addFreshEntity(deathWorm);
        }
        return true;
    }
}
