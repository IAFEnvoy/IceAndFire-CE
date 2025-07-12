package com.iafenvoy.iceandfire.network;

import com.iafenvoy.iceandfire.StaticVariables;
import com.iafenvoy.iceandfire.entity.*;
import com.iafenvoy.iceandfire.entity.util.ISyncMount;
import com.iafenvoy.iceandfire.event.ServerEvents;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class ServerNetworkHelper {
    public static void registerReceivers() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, StaticVariables.DRAGON_CONTROL, (buf, ctx) -> {
            int dragonId = buf.readInt();
            byte controlState = buf.readByte();
            BlockPos pos = buf.readBlockPos();
            PlayerEntity player = ctx.getPlayer();

            if (player != null) {
                Entity entity = player.getWorld().getEntityById(dragonId);
                if (ServerEvents.isRidingOrBeingRiddenBy(entity, player)) {
                        /*
                            For some of these entities the `setPos` is handled in `Entity#move`
                            Doing it here would cause server-side movement checks to fail (resulting in "moved wrongly" messages)
                        */
                    if (entity instanceof EntityDragonBase dragon) {
                        if (dragon.isOwner(player))
                            dragon.setControlState(controlState);
                    } else if (entity instanceof EntityHippogryph hippogryph) {
                        if (hippogryph.isOwner(player))
                            hippogryph.setControlState(controlState);
                    } else if (entity instanceof EntityHippocampus hippo) {
                        if (hippo.isOwner(player))
                            hippo.setControlState(controlState);
                        hippo.setPos(pos.getX(), pos.getY(), pos.getZ());
                    } else if (entity instanceof EntityDeathWorm deathWorm) {
                        deathWorm.setControlState(controlState);
                        deathWorm.setPos(pos.getX(), pos.getY(), pos.getZ());
                    } else if (entity instanceof EntityAmphithere amphithere) {
                        if (amphithere.isOwner(player))
                            amphithere.setControlState(controlState);
                        // TODO :: Is this handled by Entity#move due to recent changes?
                        amphithere.setPos(pos.getX(), pos.getY(), pos.getZ());
                    }
                }
            }
        });
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, StaticVariables.MULTIPART_INTERACT, (buf, ctx) -> {
            UUID creatureID = buf.readUuid();
            float dmg = buf.readFloat();
            PlayerEntity player = ctx.getPlayer();
            ctx.queue(() -> {
                if (player != null && player.getWorld() instanceof ServerWorld serverWorld) {
                    Entity entity = serverWorld.getEntity(creatureID);
                    if (entity instanceof LivingEntity livingEntity) {
                        double dist = player.distanceTo(livingEntity);
                        if (dist < 100) {
                            if (dmg > 0F) livingEntity.damage(player.getWorld().damageSources.mobAttack(player), dmg);
                            else livingEntity.interact(player, Hand.MAIN_HAND);
                        }
                    }
                }
            });
        });
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, StaticVariables.PLAYER_HIT_MULTIPART, (buf, ctx) -> {
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                Entity entity = player.getWorld().getEntityById(buf.readInt());
                if (entity instanceof LivingEntity livingEntity) {
                    double dist = player.distanceTo(livingEntity);
                    if (dist < 100) {
                        player.attack(livingEntity);
                        if (livingEntity instanceof EntityHydra hydra)
                            hydra.triggerHeadFlags(buf.readInt());
                    }
                }
            }
        });
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, StaticVariables.START_RIDING_MOB_C2S, (buf, ctx) -> {
            int dragonId = buf.readInt();
            boolean ride = buf.readBoolean();
            boolean baby = buf.readBoolean();
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                Entity entity = player.getWorld().getEntityById(dragonId);
                if (entity instanceof ISyncMount && entity instanceof TameableEntity tamable)
                    if (tamable.isOwner(player) && tamable.distanceTo(player) < 14)
                        if (ride) {
                            if (baby) tamable.startRiding(player, true);
                            else player.startRiding(tamable, true);
                        } else {
                            if (baby) tamable.stopRiding();
                            else player.stopRiding();
                        }
            }
        });
    }
}
