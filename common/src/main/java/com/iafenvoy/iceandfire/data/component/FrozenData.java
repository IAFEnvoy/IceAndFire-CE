package com.iafenvoy.iceandfire.data.component;

import com.iafenvoy.iceandfire.entity.IceDragonEntity;
import com.iafenvoy.iceandfire.impl.ComponentManager;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.util.attachment.NeedUpdateData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;

public class FrozenData extends NeedUpdateData<LivingEntity> {
    public static final Codec<FrozenData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.fieldOf("isFrozen").forGetter(FrozenData::isFrozen),
            Codec.INT.fieldOf("frozenTicks").forGetter(FrozenData::getFrozenTicks)
    ).apply(i, FrozenData::new));
    public static final PacketCodec<RegistryByteBuf, FrozenData> PACKET_CODEC = PacketCodecs.registryCodec(CODEC);
    public boolean isFrozen;
    public int frozenTicks;

    public FrozenData() {
    }

    private FrozenData(boolean isFrozen, int frozenTicks) {
        this.isFrozen = isFrozen;
        this.frozenTicks = frozenTicks;
    }

    @Override
    public void tick(final LivingEntity entity) {
        if (!this.isFrozen) return;
        if (entity instanceof IceDragonEntity) this.clearFrozen(entity);
        else if (entity.isDead()) this.clearFrozen(entity);
        else if (entity.isOnFire()) {
            this.clearFrozen(entity);
            entity.extinguish();
        } else {
            if (this.frozenTicks > 0) this.frozenTicks--;
            else this.clearFrozen(entity);
            if (this.isFrozen && !(entity instanceof PlayerEntity player && player.isCreative())) {
                entity.setVelocity(entity.getVelocity().multiply(0.25F, 1, 0.25F));
                if (!(entity instanceof EnderDragonEntity) && !entity.isOnGround())
                    entity.setVelocity(entity.getVelocity().add(0, -0.2, 0));
            }
        }
    }

    public void setFrozen(final LivingEntity target, int duration) {
        if (!this.isFrozen) target.playSound(SoundEvents.BLOCK_GLASS_PLACE, 1, 1);
        this.frozenTicks = duration;
        this.isFrozen = true;
        this.markDirty();
    }

    private void clearFrozen(final LivingEntity entity) {
        for (int i = 0; i < 15; i++)
            entity.getWorld().addParticle(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, IafBlocks.DRAGON_ICE.get().getDefaultState()),
                    entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * entity.getWidth(),
                    entity.getY() + entity.getRandom().nextDouble() * entity.getHeight(),
                    entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * entity.getWidth(),
                    0, 0, 0);
        entity.playSound(SoundEvents.BLOCK_GLASS_BREAK, 3, 1);
        this.isFrozen = false;
        this.frozenTicks = 0;
        this.markDirty();
    }

    public boolean isFrozen() {
        return this.isFrozen;
    }

    public int getFrozenTicks() {
        return this.frozenTicks;
    }

    public static FrozenData get(LivingEntity living) {
        return ComponentManager.getFrozenData(living);
    }
}
