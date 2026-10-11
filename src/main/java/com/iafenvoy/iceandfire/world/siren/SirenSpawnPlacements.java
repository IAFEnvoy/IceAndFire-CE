package com.iafenvoy.iceandfire.world.siren;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber
public final class SirenSpawnPlacements {
    private static final int SURFACE_TOLERANCE = 1;
    private static final DangerousGeneration DANGER_RADIUS = new DangerousGeneration() {
    };

    private SirenSpawnPlacements() {
    }

    @SubscribeEvent
    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                IafEntities.SIREN.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SirenSpawnPlacements::canSirenSpawn,
                RegisterSpawnPlacementsEvent.Operation.OR
        );
    }

    private static boolean canSirenSpawn(EntityType<? extends Monster> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getLevel().isNight()
                && DANGER_RADIUS.isFarEnoughFromSpawn(level, pos)
                && isOnSurface(level, pos)
                && isAwayFromPlayers(level, reason, pos)
                && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    private static boolean isOnSurface(ServerLevelAccessor level, BlockPos pos) {
        return pos.getY() >= level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos).getY() - SURFACE_TOLERANCE;
    }

    private static boolean isAwayFromPlayers(ServerLevelAccessor level, MobSpawnType reason, BlockPos pos) {
        double minDistance = IafCommonConfig.INSTANCE.siren.beachMinPlayerDistance.getValue();
        if (reason != MobSpawnType.NATURAL || minDistance <= 0) return true;
        return !level.hasNearbyAlivePlayer(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, minDistance);
    }
}
