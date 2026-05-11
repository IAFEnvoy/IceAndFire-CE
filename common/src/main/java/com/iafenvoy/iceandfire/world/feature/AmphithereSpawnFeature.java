package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.iceandfire.world.feature.config.EntitySpawnFeatureConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class AmphithereSpawnFeature extends Feature<EntitySpawnFeatureConfig> implements DangerousGeneration {
    public AmphithereSpawnFeature() {
        super(EntitySpawnFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<EntitySpawnFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, context.getOrigin().add(8, 0, 8));
        if (this.isFarEnoughFromSpawn(world, pos) && context.getRandom().nextDouble() < context.getConfig().spawnChance()) {
            Entity entity = context.getConfig().entityType().create(world.toServerWorld());
            if (entity != null) {
                entity.setPosition(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F);
                if (entity instanceof MobEntity mob)
                    mob.initialize(world, world.getLocalDifficulty(pos), SpawnReason.CHUNK_GENERATION, null);
                world.spawnEntity(entity);
            }
        }
        return true;
    }
}
