package com.iafenvoy.iceandfire.world;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.item.ItemSummoningCrystal;
import com.iafenvoy.iceandfire.world.util.Teleportable;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Server-thread requests; tickets never become persistent forced chunks. */
public final class SummoningCrystalManager {
    private static final ChunkTicketType<UUID> TICKET = ChunkTicketType.create(
            "iceandfire_summoning", Comparator.comparing(UUID::toString), 100);
    private static final Map<UUID, Request> PENDING = new HashMap<>();

    private SummoningCrystalManager() {
    }

    public static void register() {
        TickEvent.SERVER_POST.register(SummoningCrystalManager::tick);
        LifecycleEvent.SERVER_STOPPING.register(server -> {
            PENDING.values().forEach(Request::release);
            PENDING.clear();
        });
    }

    public static void request(ServerPlayerEntity player, Hand hand, BlockPos destination) {
        Request previous = PENDING.remove(player.getUuid());
        if (previous != null) previous.release();
        ItemStack stack = player.getStackInHand(hand);
        if (!ItemSummoningCrystal.hasDragon(stack)
                || !stack.getNbt().getCompound("Dragon").containsUuid("DragonUUID")
                || !player.isAlive() || player.isSpectator()
                || !player.getWorld().getWorldBorder().contains(destination)
                || !player.getWorld().isInBuildLimit(destination)) {
            fail(player);
            return;
        }
        UUID id = stack.getNbt().getCompound("Dragon").getUuid("DragonUUID");
        Request request = new Request(player, hand, stack, id, destination.toImmutable());
        Entity loaded = findLoaded(player.getServer(), id);
        if (loaded != null) {
            if (!request.trySummon(player, loaded)) fail(player);
            return;
        }
        if (IafCommonConfig.INSTANCE.dragon.chunkLoadSummonCrystal.getValue()) {
            // Positions come exclusively from server persistence, never from item/client coordinates.
            for (ServerWorld world : player.getServer().getWorlds()) {
                BlockPos pos = DragonPosWorldData.get(world).getDragonPos(id);
                if (pos != null) {
                    ChunkPos chunk = new ChunkPos(pos);
                    world.getChunkManager().addTicket(TICKET, chunk, 2, request.ticketId);
                    request.tickets.add(new Ticket(world, chunk));
                }
            }
        }
        if (request.tickets.isEmpty()) fail(player);
        else PENDING.put(player.getUuid(), request);
    }

    private static Entity findLoaded(MinecraftServer server, UUID id) {
        for (ServerWorld world : server.getWorlds()) {
            Entity entity = world.getEntity(id);
            if (entity != null) return entity;
        }
        return null;
    }

    private static void tick(MinecraftServer server) {
        var iterator = PENDING.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            Request request = entry.getValue();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            boolean done = !request.isValid(player);
            if (!done) {
                Entity entity = findLoaded(server, request.dragonId);
                if (entity != null) {
                    if (!request.trySummon(player, entity)) fail(player);
                    done = true;
                } else if (--request.remaining <= 0) {
                    fail(player);
                    done = true;
                }
            }
            if (done) {
                request.release();
                iterator.remove();
            }
        }
    }

    private static void fail(ServerPlayerEntity player) {
        player.sendMessage(Text.translatable("message.iceandfire.noDragonTeleport"), true);
    }

    private record Ticket(ServerWorld world, ChunkPos chunk) {
    }

    private static final class Request {
        private final UUID ticketId = UUID.randomUUID();
        private final Hand hand;
        private final ItemStack stack;
        private final UUID dragonId;
        private final ServerWorld destinationWorld;
        private final BlockPos destination;
        private final float yaw;
        private final List<Ticket> tickets = new ArrayList<>();
        private int remaining = 100;

        private Request(ServerPlayerEntity player, Hand hand, ItemStack stack, UUID dragonId, BlockPos destination) {
            this.hand = hand;
            this.stack = stack;
            this.dragonId = dragonId;
            this.destinationWorld = player.getServerWorld();
            this.destination = destination;
            this.yaw = player.getYaw();
        }

        private boolean isValid(ServerPlayerEntity player) {
            return player != null && player.isAlive() && !player.isSpectator()
                    && player.getServerWorld() == this.destinationWorld
                    && player.getStackInHand(this.hand) == this.stack
                    && ItemSummoningCrystal.hasDragon(this.stack)
                    && this.stack.getNbt().getCompound("Dragon").containsUuid("DragonUUID")
                    && this.dragonId.equals(this.stack.getNbt().getCompound("Dragon").getUuid("DragonUUID"));
        }

        private boolean summon(ServerPlayerEntity player, Entity entity) {
            if (!this.isValid(player) || !(entity instanceof EntityDragonBase dragon)
                    || dragon.isRemoved() || !dragon.isAlive() || dragon.isModelDead()
                    || !dragon.isTamed() || !player.getUuid().equals(dragon.getOwnerUuid()) || !dragon.isBoundToCrystal()
                    || this.stack.getItem() != dragon.dragonType.getCrystalItem()) return false;
            Vec3d position = Vec3d.ofCenter(this.destination);
            Entity moved = dragon;
            if (dragon.getWorld() != this.destinationWorld) {
                Teleportable teleportable = (Teleportable) dragon;
                try {
                    teleportable.iceandfire$setCustomTeleportTarget(new TeleportTarget(position, Vec3d.ZERO, this.yaw, 0));
                    moved = dragon.moveToWorld(this.destinationWorld);
                } finally {
                    teleportable.iceandfire$setCustomTeleportTarget(null);
                }
            } else dragon.refreshPositionAndAngles(position.x, position.y, position.z, this.yaw, 0);
            if (!(moved instanceof EntityDragonBase summoned)) return false;
            summoned.setCrystalBound(false);
            summoned.setVelocity(Vec3d.ZERO);
            for (ServerWorld world : player.getServer().getWorlds())
                DragonPosWorldData.get(world).removeDragon(this.dragonId);
            this.stack.getNbt().remove("Dragon");
            player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            player.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1, 1);
            player.swingHand(this.hand);
            player.sendMessage(Text.translatable("message.iceandfire.dragonTeleport"), true);
            return true;
        }

        private boolean trySummon(ServerPlayerEntity player, Entity entity) {
            try {
                return this.summon(player, entity);
            } catch (RuntimeException e) {
                com.iafenvoy.iceandfire.IceAndFire.LOGGER.warn("Could not summon dragon {}", this.dragonId, e);
                return false;
            }
        }

        private void release() {
            for (Ticket ticket : this.tickets)
                ticket.world.getChunkManager().removeTicket(TICKET, ticket.chunk, 2, this.ticketId);
            this.tickets.clear();
        }
    }
}
