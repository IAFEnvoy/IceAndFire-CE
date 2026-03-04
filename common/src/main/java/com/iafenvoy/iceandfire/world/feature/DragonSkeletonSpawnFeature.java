package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.uranus.util.RandomHelper;
import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DragonSkeletonSpawnFeature extends Feature<DefaultFeatureConfig> {
    protected final EntityType<? extends DragonBaseEntity> dragonType;

    public DragonSkeletonSpawnFeature(EntityType<? extends DragonBaseEntity> dt, Codec<DefaultFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
        this.dragonType = dt;
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        BlockPos pos = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, context.getOrigin().add(8, 0, 8));
        if (IafCommonConfig.INSTANCE.dragon.generateSkeletons.getValue() && random.nextDouble() < IafCommonConfig.INSTANCE.dragon.generateSkeletonChance.getValue()) {
            DragonBaseEntity dragon = this.dragonType.create(world.toServerWorld());
            assert dragon != null;
            dragon.setPosition(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F);
            int age = 10 + random.nextInt(100);
            dragon.growDragon(age);
            dragon.modelDeadProgress = 20;
            dragon.setModelDead(true);
            dragon.setDeathStage(age / 10);
            dragon.setYaw(random.nextInt(360));
            dragon.setVariant(RandomHelper.randomOne(dragon.dragonType.colors()).getName());
            world.spawnEntity(dragon);
        }
        return true;
    }
}
