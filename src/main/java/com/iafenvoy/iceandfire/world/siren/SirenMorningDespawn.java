package com.iafenvoy.iceandfire.world.siren;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.SirenEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public final class SirenMorningDespawn {
    private static final String NIGHT_SPAWN_TAG = "iceandfire_night_siren";

    private SirenMorningDespawn() {
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        EntitySpawnReason reason = event.getSpawnType();
        boolean worldSpawn = reason == EntitySpawnReason.NATURAL || reason == EntitySpawnReason.CHUNK_GENERATION;
        if (worldSpawn && event.getEntity() instanceof SirenEntity siren) siren.addTag(NIGHT_SPAWN_TAG);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!IafCommonConfig.INSTANCE.siren.despawnAtMorning.getValue()) return;
        if (!(event.getEntity() instanceof SirenEntity siren) || siren.level().isClientSide() || siren.tickCount % 20 != 0) return;
        if (siren.entityTags().contains(NIGHT_SPAWN_TAG)
                && !siren.hasCustomName()
                && !siren.requiresCustomPersistence()
                && isDay(siren)) {
            siren.discard();
        }
    }

    /**
     * {@code Level#isDay()} was removed in 26.1.2 in favour of the world clock system, so the day
     * window is derived from the dimension's default clock. The thresholds match the removed vanilla
     * implementation (0.25 to 0.75 of the day-night cycle).
     */
    private static boolean isDay(SirenEntity siren) {
        long time = siren.level().getDefaultClockTime() % 24000L;
        return time >= 6000L && time < 18000L;
    }
}
