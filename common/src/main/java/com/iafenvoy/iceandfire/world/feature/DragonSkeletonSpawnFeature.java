package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.world.feature.config.DragonSkeletonFeatureConfig;
import com.iafenvoy.uranus.util.RandomHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DragonSkeletonSpawnFeature extends Feature<DragonSkeletonFeatureConfig> {
    public DragonSkeletonSpawnFeature() {
        super(DragonSkeletonFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<DragonSkeletonFeatureConfig> context) {
        DragonSkeletonFeatureConfig config = context.getConfig();
        if (!config.enabled()) return false;
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        BlockPos pos = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, context.getOrigin().add(8, 0, 8));
        if (random.nextDouble() < config.spawnChance()) {
            Entity entity = config.entityType().create(world.toServerWorld());
            if (entity instanceof DragonBaseEntity dragon) {
                dragon.setPosition(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F);
                int age = config.ageMin() + random.nextInt(config.ageMax() - config.ageMin());
                dragon.growDragon(age);
                dragon.modelDeadProgress = 20;
                dragon.setModelDead(true);
                dragon.setDeathStage(age / 10);
                dragon.setYaw(random.nextInt(360));
                dragon.setVariant(RandomHelper.randomOne(dragon.dragonType.colors()).getName());
                world.spawnEntity(dragon);
            }
        }
        return true;
    }
}
