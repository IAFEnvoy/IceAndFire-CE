package com.iafenvoy.iceandfire.entity.util;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityMultipartPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;

public class IafEntityUtil {
    public static void updatePart(final EntityMultipartPart part, final LivingEntity parent) {
        if (part == null || !(parent.getWorld() instanceof ServerWorld serverLevel) || parent.isRemoved())
            return;
        if (!part.shouldContinuePersisting()) {
            UUID uuid = part.getUuid();
            Entity existing = serverLevel.getEntity(uuid);
            // Update UUID if a different entity with the same UUID exists already
            if (existing != null && existing != part) {
                while (serverLevel.getEntity(uuid) != null)
                    uuid = MathHelper.randomUuid(parent.getRandom());
                IceAndFire.LOGGER.debug("Updated the UUID of [{}] due to a clash with [{}]", part, existing);
            }
            part.setUuid(uuid);
            serverLevel.spawnEntity(part);
        }
        part.setParent(parent);
    }
}
