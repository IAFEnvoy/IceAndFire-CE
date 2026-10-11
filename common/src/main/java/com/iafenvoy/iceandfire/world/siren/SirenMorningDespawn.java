package com.iafenvoy.iceandfire.world.siren;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.EntitySiren;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;

public final class SirenMorningDespawn {
    private SirenMorningDespawn() {
    }

    public static void init() {
        EntityEvent.LIVING_CHECK_SPAWN.register((entity, world, x, y, z, reason, spawnData) -> {
            if (entity instanceof EntitySiren siren && (reason == SpawnReason.NATURAL || reason == SpawnReason.CHUNK_GENERATION))
                siren.addCommandTag(SirenSpawnPredicate.NIGHT_SPAWN_TAG);
            return EventResult.pass();
        });
        TickEvent.SERVER_LEVEL_POST.register(SirenMorningDespawn::onLevelTick);
    }

    private static void onLevelTick(ServerWorld world) {
        if (!IafCommonConfig.INSTANCE.siren.despawnAtMorning.getValue()) return;
        if (!world.isDay() || world.getTime() % 20 != 0) return;
        for (Entity entity : world.iterateEntities())
            if (entity instanceof EntitySiren siren
                    && siren.getCommandTags().contains(SirenSpawnPredicate.NIGHT_SPAWN_TAG)
                    && !siren.hasCustomName()
                    && !siren.cannotDespawn())
                siren.discard();
    }
}
