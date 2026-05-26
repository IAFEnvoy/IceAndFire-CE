package com.iafenvoy.iceandfire.world;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.uranus.ServerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

import java.util.Optional;

public interface DangerousGeneration {
    default boolean isFarEnoughFromSpawn(LevelAccessor world, BlockPos pos) {
        return !this.getOrigin(world, pos).closerThan(pos, this.getDangerousRadius());
    }

    default boolean isFarEnoughFromSpawn(BlockPos pos) {
        return Optional.ofNullable(ServerHelper.server).map(server -> this.isFarEnoughFromSpawn(server.overworld(), pos)).orElse(true);
    }

    default float getDangerousRadius() {
        return IafCommonConfig.INSTANCE.worldGen.dangerousDistanceLimit.getValue().floatValue();
    }

    default BlockPos getOrigin(LevelAccessor world, BlockPos pos) {
        BlockPos spawn = world.getLevelData().getSpawnPos();
        return new BlockPos(spawn.getX(), pos.getY(), spawn.getZ());
    }
}
