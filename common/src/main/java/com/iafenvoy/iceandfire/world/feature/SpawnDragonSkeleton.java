package com.iafenvoy.iceandfire.world.feature;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.uranus.util.RandomHelper;
import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.List;

public class SpawnDragonSkeleton extends Feature<DefaultFeatureConfig> {
    protected final EntityType<? extends EntityDragonBase> dragonType;

    public SpawnDragonSkeleton(EntityType<? extends EntityDragonBase> dt, Codec<DefaultFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
        this.dragonType = dt;
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess worldIn = context.getWorld();
        Random rand = context.getRandom();
        BlockPos position = context.getOrigin();
        position = worldIn.getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, position.add(8, 0, 8));
        if (IafCommonConfig.INSTANCE.dragon.generateSkeletons.getValue()) {
            if (rand.nextDouble() < IafCommonConfig.INSTANCE.dragon.generateSkeletonChance.getValue()) {
                EntityDragonBase dragon = this.dragonType.create(worldIn.toServerWorld());
                assert dragon != null;
                dragon.setPosition(position.getX() + 0.5F, position.getY() + 1, position.getZ() + 0.5F);
                int dragonage = 10 + rand.nextInt(100);
                dragon.growDragon(dragonage);
                dragon.modelDeadProgress = 20;
                dragon.setModelDead(true);
                dragon.setDeathStage((dragonage / 5) / 2);
                dragon.setYaw(rand.nextInt(360));
                List<DragonColor> colors = DragonColor.getColorsByType(dragon.dragonType);
                dragon.setVariant(colors.get(new java.util.Random().nextInt(colors.size())).name());
                worldIn.spawnEntity(dragon);
            }
        }
        return true;
    }
}
