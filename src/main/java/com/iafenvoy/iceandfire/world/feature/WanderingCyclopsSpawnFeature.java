package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.CyclopsEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class WanderingCyclopsSpawnFeature extends Feature<NoneFeatureConfiguration> implements DangerousGeneration {
    public WanderingCyclopsSpawnFeature(Codec<NoneFeatureConfiguration> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        RandomSource random = context.random();
        BlockPos pos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, context.origin().offset(8, 0, 8));
        if (this.isFarEnoughFromSpawn(world, pos) && random.nextDouble() < IafCommonConfig.INSTANCE.cyclops.spawnWanderingChance.getValue() && random.nextInt(12) == 0) {
            CyclopsEntity cyclops = IafEntities.CYCLOPS.get().create(world.getLevel());
            assert cyclops != null;
            cyclops.setPos(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F);
            cyclops.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), MobSpawnType.SPAWNER, null);
            world.addFreshEntity(cyclops);
            for (int i = 0; i < 3 + random.nextInt(3); i++) {
                Sheep sheep = EntityType.SHEEP.create(world.getLevel());
                assert sheep != null;
                sheep.setPos(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F);
                sheep.setColor(Sheep.getRandomSheepColor(random));
                world.addFreshEntity(sheep);
            }
        }
        return true;
    }
}
