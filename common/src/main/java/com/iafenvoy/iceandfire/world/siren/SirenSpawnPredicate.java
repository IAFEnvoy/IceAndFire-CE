package com.iafenvoy.iceandfire.world.siren;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.EntitySiren;
import com.iafenvoy.iceandfire.world.GenerationConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;

public final class SirenSpawnPredicate {
    public static final String NIGHT_SPAWN_TAG = "iceandfire_night_siren";
    private static final int SURFACE_TOLERANCE = 1;

    private SirenSpawnPredicate() {
    }

    public static boolean canSirenSpawn(EntityType<EntitySiren> type, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        return world.toServerWorld().isNight()
                && GenerationConstants.isFarEnoughFromSpawn(pos)
                && isOnSurface(world, pos)
                && isAwayFromPlayers(world, reason, pos)
                && MobEntity.canMobSpawn(type, world, reason, pos, random);
    }

    private static boolean isOnSurface(ServerWorldAccess world, BlockPos pos) {
        return pos.getY() >= world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - SURFACE_TOLERANCE;
    }

    private static boolean isAwayFromPlayers(ServerWorldAccess world, SpawnReason reason, BlockPos pos) {
        double minDistance = IafCommonConfig.INSTANCE.siren.beachMinPlayerDistance.getValue();
        if (reason != SpawnReason.NATURAL || minDistance <= 0) return true;
        return !world.toServerWorld().isPlayerInRange(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, minDistance);
    }
}
