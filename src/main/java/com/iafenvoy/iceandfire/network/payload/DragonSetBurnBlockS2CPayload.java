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

public record DragonSetBurnBlockS2CPayload(int entityId, boolean breathing,
                                           BlockPos target) implements CustomPacketPayload {
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "dragon_set_burn_block");
    public static final Type<DragonSetBurnBlockS2CPayload> ID = new Type<>(IDENTIFIER);
    public static final StreamCodec<ByteBuf, DragonSetBurnBlockS2CPayload> CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("entityId").forGetter(DragonSetBurnBlockS2CPayload::entityId),
            Codec.BOOL.fieldOf("breathing").forGetter(DragonSetBurnBlockS2CPayload::breathing),
            BlockPos.CODEC.fieldOf("target").forGetter(DragonSetBurnBlockS2CPayload::target)
    ).apply(i, DragonSetBurnBlockS2CPayload::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
