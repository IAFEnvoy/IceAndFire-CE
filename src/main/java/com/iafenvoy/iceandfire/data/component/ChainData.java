package com.iafenvoy.iceandfire.data.component;

import com.iafenvoy.iceandfire.impl.ComponentManager;
import com.iafenvoy.iceandfire.util.attachment.NeedUpdateData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ChainData extends NeedUpdateData<LivingEntity> {
    public static final Codec<ChainData> CODEC = RecordCodecBuilder.create(i -> i.group(
            UUIDUtil.AUTHLIB_CODEC.listOf().fieldOf("chainedTo").forGetter(ChainData::getChainedTo)
    ).apply(i, ChainData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChainData> PACKET_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    @NotNull
    private final List<UUID> chainedTo = new LinkedList<>();

    public ChainData() {
    }

    private ChainData(List<UUID> chainedTo) {
        this.chainedTo.addAll(chainedTo);
    }

    @Override
    public void tick(LivingEntity entity) {
        if (entity.level() instanceof ServerLevel world)
            for (UUID uuid : this.chainedTo) {
                Entity chain = world.getEntity(uuid);
                if (chain == null) continue;
                double distance = chain.distanceTo(entity);
                if (distance > 7) {
                    double x = (chain.getX() - entity.getX()) / distance;
                    double y = (chain.getY() - entity.getY()) / distance;
                    double z = (chain.getZ() - entity.getZ()) / distance;
                    entity.setDeltaMovement(entity.getDeltaMovement().add(x * Math.abs(x) * 0.4D, y * Math.abs(y) * 0.2D, z * Math.abs(z) * 0.4D));
                }
            }
    }

    public @NotNull List<UUID> getChainedTo() {
        return List.copyOf(this.chainedTo);
    }

    public void clearChains() {
        this.chainedTo.clear();
        this.markDirty();
    }

    public void attachChain(final UUID chain) {
        if (this.chainedTo.contains(chain)) return;
        this.chainedTo.add(chain);
        this.markDirty();
    }

    public void removeChain(final UUID chain) {
        this.chainedTo.remove(chain);
        this.markDirty();
    }

    public boolean isChainedTo(final UUID toCheck) {
        return this.chainedTo.contains(toCheck);
    }

    public static ChainData get(LivingEntity living) {
        return ComponentManager.getChainData(living);
    }
}
