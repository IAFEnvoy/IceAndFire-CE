package com.iafenvoy.iceandfire.network.payload;

import com.iafenvoy.iceandfire.IceAndFire;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record UpdatePodiumS2CPayload(BlockPos blockPos, ItemStack heldStack) implements CustomPacketPayload {
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "update_podium");
    public static final Type<UpdatePodiumS2CPayload> ID = new Type<>(IDENTIFIER);
    public static final StreamCodec<ByteBuf, UpdatePodiumS2CPayload> CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            BlockPos.CODEC.fieldOf("blockPos").forGetter(UpdatePodiumS2CPayload::blockPos),
            ItemStack.OPTIONAL_CODEC.fieldOf("heldStack").forGetter(UpdatePodiumS2CPayload::heldStack)
    ).apply(i, UpdatePodiumS2CPayload::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
