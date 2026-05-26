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

public record DragonControlC2SPayload(int dragonId, byte controlState, BlockPos pos) implements CustomPacketPayload {
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "dragon_control");
    public static final Type<DragonControlC2SPayload> ID = new Type<>(IDENTIFIER);
    public static final StreamCodec<ByteBuf, DragonControlC2SPayload> CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("dragonId").forGetter(DragonControlC2SPayload::dragonId),
            Codec.BYTE.fieldOf("controlState").forGetter(DragonControlC2SPayload::controlState),
            BlockPos.CODEC.fieldOf("pos").forGetter(DragonControlC2SPayload::pos)
    ).apply(i, DragonControlC2SPayload::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
