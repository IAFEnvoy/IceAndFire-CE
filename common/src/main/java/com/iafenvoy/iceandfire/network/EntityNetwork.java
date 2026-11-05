package com.iafenvoy.iceandfire.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public final class EntityNetwork {
    private EntityNetwork() {}

    public static void sendToTracking(Entity entity, Identifier channel, PacketByteBuf data) {
        if (entity.getWorld() instanceof ServerWorld world)
            for (var packet : NetworkManager.toPackets(NetworkManager.Side.S2C, channel, data))
                world.getChunkManager().sendToNearbyPlayers(entity, packet);
    }
}
