package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.iceandfire.world.feature.config.EntitySpawnFeatureConfig;
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

public class WanderingCyclopsSpawnFeature extends Feature<EntitySpawnFeatureConfig> implements DangerousGeneration {
    public WanderingCyclopsSpawnFeature() {
        super(EntitySpawnFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<EntitySpawnFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, context.getOrigin().add(8, 0, 8));
        if (this.isFarEnoughFromSpawn(world, pos) && random.nextDouble() < context.getConfig().spawnChance() && random.nextInt(12) == 0) {
            Entity entity = context.getConfig().entityType().create(world.toServerWorld());
            if (entity != null) {
                entity.setPosition(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F);
                if (entity instanceof MobEntity mob)
                    mob.initialize(world, world.getLocalDifficulty(pos), SpawnReason.SPAWNER, null);
                world.spawnEntity(entity);
                for (int i = 0; i < 3 + random.nextInt(3); i++) {
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
