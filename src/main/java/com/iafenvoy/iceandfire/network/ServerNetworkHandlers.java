package com.iafenvoy.iceandfire.network;

import com.iafenvoy.iceandfire.entity.*;
import com.iafenvoy.iceandfire.entity.util.ISyncMount;
import com.iafenvoy.iceandfire.network.payload.DragonControlC2SPayload;
import com.iafenvoy.iceandfire.network.payload.StartRidingMobPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ServerNetworkHandlers {
    private ServerNetworkHandlers() {
    }

    public static void handleDragonControl(DragonControlC2SPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Entity entity = player.level().getEntity(payload.dragonId());
            // Either the player rides the creature (adult), or the creature rides the player
            // (baby dragon on the shoulder).
            if (entity != null && (entity.getControllingPassenger() == player || entity.getVehicle() == player)) {
                // The payload's legacy position field is deliberately ignored. Movement is server validated.
                switch (entity) {
                    case DragonBaseEntity dragon -> {
                        if (dragon.isOwnedBy(player)) {
                            dragon.setControlState(payload.controlState());
                            if (dragon.getVehicle() == player && dragon.isDismounting()) {
                                dragon.stopRiding();
                                dragon.dismount(false);
                            }
                        }
                    }
                    case HippogryphEntity hippogryph -> {
                        if (hippogryph.isOwnedBy(player))
                            hippogryph.setControlState(payload.controlState());
                    }
                    case HippocampusEntity hippo -> {
                        if (hippo.isOwnedBy(player))
                            hippo.setControlState(payload.controlState());
                    }
                    case DeathWormEntity deathWorm -> {
                        deathWorm.setControlState(payload.controlState());
                    }
                    case AmphithereEntity amphithere -> {
                        if (amphithere.isOwnedBy(player))
                            amphithere.setControlState(payload.controlState());
                    }
                    default -> {
                    }
                }
            }
        });
    }


    public static void handleStartRidingMob(StartRidingMobPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Entity entity = player.level().getEntity(payload.dragonId());
            if (entity instanceof ISyncMount && entity instanceof TamableAnimal tamable)
                if (tamable.isOwnedBy(player) && tamable.distanceTo(player) < 14)
                    if (payload.ride()) {
                        if (payload.baby()) tamable.startRiding(player, true);
                        else player.startRiding(tamable, true);
                    } else {
                        if (payload.baby()) tamable.stopRiding();
                        else player.stopRiding();
                    }
        });
    }
}
