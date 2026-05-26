package com.iafenvoy.iceandfire.network.payload;

import com.iafenvoy.iceandfire.IceAndFire;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record UpdatePixieJarS2CPayload(BlockPos blockPos, boolean isProducing) implements CustomPacketPayload {
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "update_pixie_jar");
    public static final Type<UpdatePixieJarS2CPayload> ID = new Type<>(IDENTIFIER);
    public static final StreamCodec<ByteBuf, UpdatePixieJarS2CPayload> CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            BlockPos.CODEC.fieldOf("blockPos").forGetter(UpdatePixieJarS2CPayload::blockPos),
            Codec.BOOL.fieldOf("isProducing").forGetter(UpdatePixieJarS2CPayload::isProducing)
    ).apply(i, UpdatePixieJarS2CPayload::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
