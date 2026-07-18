package com.iafenvoy.iceandfire.network;

import com.iafenvoy.iceandfire.entity.*;
import com.iafenvoy.iceandfire.entity.util.ISyncMount;
import com.iafenvoy.iceandfire.event.handler.ServerEvents;
import com.iafenvoy.iceandfire.network.payload.DragonControlC2SPayload;
import com.iafenvoy.iceandfire.network.payload.MultipartInteractC2SPayload;
import com.iafenvoy.iceandfire.network.payload.PlayerHitMultipartC2SPayload;
import com.iafenvoy.iceandfire.network.payload.StartRidingMobPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
            if (ServerEvents.isRidingOrBeingRiddenBy(entity, player)) {
                BlockPos pos = payload.pos();
                /*
                    For some of these entities the `setPos` is handled in `Entity#move`
                    Doing it here would cause server-side movement checks to fail (resulting in "moved wrongly" messages)
                */
                switch (entity) {
                    case DragonBaseEntity dragon -> {
                        if (dragon.isOwnedBy(player))
                            dragon.setControlState(payload.controlState());
                    }
                    case HippogryphEntity hippogryph -> {
                        if (hippogryph.isOwnedBy(player))
                            hippogryph.setControlState(payload.controlState());
                    }
                    case HippocampusEntity hippo -> {
                        if (hippo.isOwnedBy(player))
                            hippo.setControlState(payload.controlState());
                        hippo.setPos(pos.getX(), pos.getY(), pos.getZ());
                    }
                    case DeathWormEntity deathWorm -> {
                        deathWorm.setControlState(payload.controlState());
                        deathWorm.setPos(pos.getX(), pos.getY(), pos.getZ());
                    }
                    case AmphithereEntity amphithere -> {
                        if (amphithere.isOwnedBy(player))
                            amphithere.setControlState(payload.controlState());
                        // TODO :: Is this handled by Entity#move due to recent changes?
                        amphithere.setPos(pos.getX(), pos.getY(), pos.getZ());
                    }
                    default -> {
                    }
                }
            }
        });
    }

    public static void handleMultipartInteract(MultipartInteractC2SPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player.level() instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(payload.creatureID());
                if (entity instanceof LivingEntity livingEntity) {
                    double dist = player.distanceTo(livingEntity);
                    if (dist < 100) {
                        float dmg = payload.dmg();
                        if (dmg > 0F) livingEntity.hurt(serverLevel.damageSources().mobAttack(player), dmg);
                        else livingEntity.interact(player, InteractionHand.MAIN_HAND);
                    }
                }
            }
        });
    }

    public static void handlePlayerHitMultipart(PlayerHitMultipartC2SPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Entity entity = player.level().getEntity(payload.entityId());
            if (entity instanceof LivingEntity livingEntity) {
                double dist = player.distanceTo(livingEntity);
                if (dist < 100) {
                    player.attack(livingEntity);
                    if (livingEntity instanceof HydraEntity hydra)
                        hydra.triggerHeadFlags(payload.index());
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
