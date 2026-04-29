package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.iceandfire.world.feature.config.WanderingCyclopsFeatureConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class WanderingCyclopsSpawnFeature extends Feature<WanderingCyclopsFeatureConfig> implements DangerousGeneration {
    public WanderingCyclopsSpawnFeature() {
        super(WanderingCyclopsFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<WanderingCyclopsFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, context.getOrigin().add(8, 0, 8));
        WanderingCyclopsFeatureConfig config = context.getConfig();
        if (this.isFarEnoughFromSpawn(world, pos) && random.nextFloat() < config.spawnChance()) {
            Entity entity = IafEntities.CYCLOPS.get().create(world.toServerWorld());
            if (entity != null) {
                entity.setPosition(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F);
                if (entity instanceof MobEntity mob)
                    mob.initialize(world, world.getLocalDifficulty(pos), SpawnReason.SPAWNER, null);
                world.spawnEntity(entity);
                int sheepCount = config.sheepMin() + random.nextInt(config.sheepMax() - config.sheepMin() + 1);
                for (int i = 0; i < sheepCount; i++) {
                    SheepEntity sheep = EntityType.SHEEP.create(world.toServerWorld());
                    if (sheep != null) {
                        sheep.setPosition(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F);
                        sheep.setColor(SheepEntity.generateDefaultColor(random));
                        world.spawnEntity(sheep);
                    }
                }
            }
        }
        return true;
    }
}
