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

public record PlayerHitMultipartC2SPayload(int entityId, int index) implements CustomPacketPayload {
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "player_hit_multipart");
    public static final Type<PlayerHitMultipartC2SPayload> ID = new Type<>(IDENTIFIER);
    public static final StreamCodec<ByteBuf, PlayerHitMultipartC2SPayload> CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("blockPos").forGetter(PlayerHitMultipartC2SPayload::entityId),
            Codec.INT.fieldOf("isProducing").forGetter(PlayerHitMultipartC2SPayload::index)
    ).apply(i, PlayerHitMultipartC2SPayload::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
