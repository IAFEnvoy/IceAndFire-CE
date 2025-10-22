package com.iafenvoy.iceandfire.particle;

import com.iafenvoy.iceandfire.registry.IafParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleType;

public class DragonSoulFlameParticleType extends DragonParticleType<DragonSoulFlameParticleType> {
    private static final MapCodec<DragonSoulFlameParticleType> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.fieldOf("scale").forGetter(DragonSoulFlameParticleType::getScale)
    ).apply(i, DragonSoulFlameParticleType::new));

    public DragonSoulFlameParticleType() {
        this(1);
    }

    public DragonSoulFlameParticleType(float scale) {
        super(scale);
    }

    @Override
    public ParticleType<?> getType() {
        return IafParticles.DRAGON_SOUL_FLAME.get();
    }

    @Override
    public MapCodec<DragonSoulFlameParticleType> getCodec() {
        return CODEC;
    }

    @Override
    public PacketCodec<? super RegistryByteBuf, DragonSoulFlameParticleType> getPacketCodec() {
        return PacketCodec.tuple(PacketCodecs.FLOAT, DragonSoulFlameParticleType::getScale, DragonSoulFlameParticleType::new);
    }
}
