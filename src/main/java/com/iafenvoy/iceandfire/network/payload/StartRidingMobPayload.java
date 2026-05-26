package com.iafenvoy.iceandfire.network.payload;

import com.iafenvoy.iceandfire.IceAndFire;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record StartRidingMobPayload(int dragonId, boolean ride, boolean baby) implements CustomPacketPayload {
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "start_riding_mob");
    public static final Type<StartRidingMobPayload> ID = new Type<>(IDENTIFIER);
    public static final StreamCodec<ByteBuf, StartRidingMobPayload> CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("dragonId").forGetter(StartRidingMobPayload::dragonId),
            Codec.BOOL.fieldOf("ride").forGetter(StartRidingMobPayload::ride),
            Codec.BOOL.fieldOf("baby").forGetter(StartRidingMobPayload::baby)
    ).apply(i, StartRidingMobPayload::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
