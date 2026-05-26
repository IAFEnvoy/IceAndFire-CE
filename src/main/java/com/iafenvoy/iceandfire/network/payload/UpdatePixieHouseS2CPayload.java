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

public record UpdatePixieHouseS2CPayload(BlockPos blockPos, boolean hasPixie,
                                         int pixieType) implements CustomPacketPayload {
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "update_pixie_house");
    public static final Type<UpdatePixieHouseS2CPayload> ID = new Type<>(IDENTIFIER);
    public static final StreamCodec<ByteBuf, UpdatePixieHouseS2CPayload> CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            BlockPos.CODEC.fieldOf("blockPos").forGetter(UpdatePixieHouseS2CPayload::blockPos),
            Codec.BOOL.fieldOf("hasPixie").forGetter(UpdatePixieHouseS2CPayload::hasPixie),
            Codec.INT.fieldOf("pixieType").forGetter(UpdatePixieHouseS2CPayload::pixieType)
    ).apply(i, UpdatePixieHouseS2CPayload::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
