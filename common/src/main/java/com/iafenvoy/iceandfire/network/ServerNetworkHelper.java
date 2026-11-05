package com.iafenvoy.iceandfire.network;

import com.iafenvoy.iceandfire.StaticVariables;
import com.iafenvoy.iceandfire.entity.*;
import com.iafenvoy.iceandfire.entity.util.ICustomMoveController;
import com.iafenvoy.iceandfire.entity.util.ISyncMount;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ServerNetworkHelper {
    public static void registerReceivers() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, StaticVariables.DRAGON_CONTROL, (buf, ctx) -> {
            int entityId = buf.readInt();
            byte controls = buf.readByte();
            // Retain the wire field for compatibility; movement belongs to the server.
            buf.readBlockPos();
            ctx.queue(() -> {
                PlayerEntity player = ctx.getPlayer();
                if (player == null || !player.isAlive() || player.isSpectator()) return;
                Entity entity = player.getWorld().getEntityById(entityId);
                if (entity instanceof ICustomMoveController controller && canControl(player, entity))
                    controller.setControlState((byte) (controls & (entity.getVehicle() == player ? 16 : 31)));
            });
        });
        // Legacy channels cannot select damage, targets or hydra heads anymore.
        // Current clients use vanilla entity interaction packets for multipart entities.
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, StaticVariables.MULTIPART_INTERACT, (buf, ctx) -> {});
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, StaticVariables.PLAYER_HIT_MULTIPART, (buf, ctx) -> {});
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, StaticVariables.START_RIDING_MOB_C2S, (buf, ctx) -> {
            int entityId = buf.readInt();
            boolean ride = buf.readBoolean();
            boolean baby = buf.readBoolean();
            ctx.queue(() -> {
                PlayerEntity player = ctx.getPlayer();
                if (player == null || !player.isAlive() || player.isSpectator()) return;
                Entity entity = player.getWorld().getEntityById(entityId);
                if (!(entity instanceof ISyncMount) || !(entity instanceof TameableEntity mount)
                        || !mount.isAlive() || !mount.isOwner(player) || mount.squaredDistanceTo(player) > 64) return;
                if (ride) {
                    if (baby && mount instanceof EntityDragonBase dragon && dragon.isBaby())
                        mount.startRiding(player, true);
                    else if (!baby && !mount.hasVehicle() && !mount.hasPassengers())
                        player.startRiding(mount, true);
                } else if (baby && mount.getVehicle() == player)
                    mount.stopRiding();
                else if (!baby && player.getVehicle() == mount)
                    player.stopRiding();
            });
        });
    }

    public static boolean canControl(PlayerEntity player, Entity entity) {
        if (entity == null || entity.isRemoved() || entity.getWorld() != player.getWorld()
                || entity instanceof LivingEntity living && !living.isAlive()) return false;
        if (entity instanceof TameableEntity tameable && !tameable.isOwner(player)) return false;
        if (!(entity instanceof TameableEntity) && !(entity instanceof EntityDeathWorm)) return false;
        return entity.getControllingPassenger() == player
                || entity instanceof EntityDragonBase dragon && dragon.isBaby() && entity.getVehicle() == player;
    }
}
