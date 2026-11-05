package com.iafenvoy.iceandfire.world;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.iafenvoy.iceandfire.registry.IafTicketTypes;
import net.minecraft.core.UUIDUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public final class DragonSummoning {
    private static final Map<UUID, Pending> PENDING = new HashMap<>();

    private record Pending(ServerPlayer player, InteractionHand hand, ItemStack crystal, UUID dragonId,
                           ServerLevel source, ChunkPos chunk, ServerLevel destination, BlockPos target,
                           float yaw, int deadline) {
        boolean isValid() {
            return !this.player.hasDisconnected() && this.player.isAlive() && this.player.level() == this.destination
                    && this.player.getItemInHand(this.hand) == this.crystal
                    && this.dragonId.equals(boundDragon(this.crystal));
        }

        void release() {
            // 26.1 tickets are keyed by type and chunk. Keep a shared ticket until its final request ends.
            if (PENDING.values().stream().noneMatch(other -> other != this && other.source == this.source && other.chunk.equals(this.chunk)))
                this.source.getChunkSource().removeTicketWithRadius(IafTicketTypes.DRAGON_SUMMON.get(), this.chunk, 2);
        }
    }

    private DragonSummoning() { }

    public static UUID boundDragon(ItemStack stack) {
        CompoundTag data = stack.get(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
        if (data != null) for (String key : data.keySet()) {
            CompoundTag dragon = data.getCompoundOrEmpty(key);
            if (key.contains("Dragon")) {
                UUID id = dragon.read("DragonUUID", UUIDUtil.AUTHLIB_CODEC)
                        .or(() -> dragon.read("DragonUUID", UUIDUtil.CODEC)).orElse(null);
                if (id != null) return id;
            }
        }
        return null;
    }

    public static void request(ServerPlayer player, InteractionHand hand, BlockPos target) {
        ItemStack crystal = player.getItemInHand(hand);
        UUID id = boundDragon(crystal);
        if (id == null || PENDING.containsKey(id)) return;
        MinecraftServer server = player.level().getServer();
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(id);
            if (entity != null) {
                if (!summon(player, hand, crystal, entity, player.level(), target, player.getYRot())) fail(player);
                return;
            }
        }
        if (IafCommonConfig.INSTANCE.dragon.chunkLoadSummonCrystal.getValue()) {
            for (ServerLevel level : server.getAllLevels()) {
                BlockPos pos = DragonPosWorldData.get(level).getDragonPos(id);
                if (pos == null) continue;
                // Bound concurrent requests per player as well as per dragon.
                if (PENDING.values().stream().anyMatch(p -> p.player == player)) return;
                ChunkPos chunk = ChunkPos.containing(pos);
                level.getChunkSource().addTicketWithRadius(IafTicketTypes.DRAGON_SUMMON.get(), chunk, 2);
                PENDING.put(id, new Pending(player, hand, crystal, id, level, chunk,
                        player.level(), target.immutable(), player.getYRot(), server.getTickCount() + 200));
                return;
            }
        }
        fail(player);
    }

    private static boolean summon(ServerPlayer player, InteractionHand hand, ItemStack crystal, Entity entity,
                                  ServerLevel destination, BlockPos target, float yaw) {
        if (!(entity instanceof DragonBaseEntity dragon) || !dragon.isAlive() || dragon.isModelDead()
                || !dragon.isBoundToCrystal() || !dragon.isTame()
                || dragon.getOwnerReference() == null || !dragon.getOwnerReference().matches(player)
                || crystal.getItem() != dragon.dragonType.getCrystalItem()
                || !dragon.getUUID().equals(boundDragon(crystal))
                || !destination.getWorldBorder().isWithinBounds(target) || destination.isOutsideBuildHeight(target)) return false;
        Entity moved;
        if (dragon.level() == destination) {
            dragon.teleportTo(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
            dragon.setYRot(yaw);
            moved = dragon;
        } else {
            moved = dragon.teleport(new TeleportTransition(destination, Vec3.atCenterOf(target),
                    Vec3.ZERO, yaw, 0, TeleportTransition.DO_NOTHING));
        }
        if (!(moved instanceof DragonBaseEntity summoned)) return false;
        summoned.setCrystalBound(false);
        summoned.setDeltaMovement(Vec3.ZERO);
        for (ServerLevel level : player.level().getServer().getAllLevels()) DragonPosWorldData.get(level).removeDragon(dragon.getUUID());
        crystal.remove(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
        player.playSound(SoundEvents.GLASS_BREAK, 1, 1);
        player.swing(hand, true);
        player.sendSystemMessage(Component.translatable("message.iceandfire.dragonTeleport"), true);
        return true;
    }

    private static void fail(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.iceandfire.noDragonTeleport"), true);
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        for (Iterator<Pending> it = PENDING.values().iterator(); it.hasNext();) {
            Pending pending = it.next();
            if (pending.player.level().getServer() != event.getServer()) continue;
            boolean valid = pending.isValid();
            Entity entity = valid ? pending.source.getEntity(pending.dragonId) : null;
            if (entity == null && valid && event.getServer().getTickCount() < pending.deadline) continue;
            try {
                if (valid && (entity == null || !summon(pending.player, pending.hand, pending.crystal, entity,
                        pending.destination, pending.target, pending.yaw))) fail(pending.player);
            } finally {
                pending.release();
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public static void joined(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof DragonBaseEntity dragon
                && dragon.isBoundToCrystal()) DragonPosWorldData.get(level).addDragon(dragon.getUUID(), dragon.blockPosition());
    }

    @SubscribeEvent
    public static void left(EntityLeaveLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof DragonBaseEntity dragon) {
            DragonPosWorldData data = DragonPosWorldData.get(level);
            if ((dragon.getRemovalReason() == null || dragon.getRemovalReason() == Entity.RemovalReason.UNLOADED_TO_CHUNK)
                    && dragon.isBoundToCrystal() && !dragon.isModelDead())
                data.addDragon(dragon.getUUID(), dragon.blockPosition());
            else data.removeDragon(dragon.getUUID());
        }
    }

    @SubscribeEvent
    public static void stopped(ServerStoppingEvent event) {
        PENDING.values().removeIf(pending -> {
            if (pending.player.level().getServer() != event.getServer()) return false;
            pending.release();
            return true;
        });
    }
}
