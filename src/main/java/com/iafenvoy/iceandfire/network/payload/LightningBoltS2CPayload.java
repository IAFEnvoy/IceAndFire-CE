package com.iafenvoy.iceandfire.network.payload;

import com.iafenvoy.iceandfire.IceAndFire;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record LightningBoltS2CPayload(List<Tuple<Vec3, Vec3>> lightnings) implements CustomPacketPayload {
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "lightning_bolt_s2c");
    public static final Type<LightningBoltS2CPayload> ID = new Type<>(IDENTIFIER);
    public static final StreamCodec<ByteBuf, LightningBoltS2CPayload> CODEC = ByteBufCodecs.fromCodec(RecordCodecBuilder.create(i -> i.group(
            RecordCodecBuilder.<Tuple<Vec3, Vec3>>create(i1 -> i1.group(
                    Vec3.CODEC.fieldOf("left").forGetter(Tuple::getA),
                    Vec3.CODEC.fieldOf("right").forGetter(Tuple::getB)
            ).apply(i1, Tuple::new)).listOf().fieldOf("lightnings").forGetter(LightningBoltS2CPayload::lightnings)
    ).apply(i, LightningBoltS2CPayload::new)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
