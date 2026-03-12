package com.iafenvoy.iceandfire.data.component;

import com.iafenvoy.iceandfire.entity.EntityIceDragon;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;

public class FrozenData extends NeedUpdateData {
    public int frozenTicks;
    public boolean isFrozen;

    public void tickFrozen(final LivingEntity entity) {
        if (!this.isFrozen) return;

        if (entity instanceof EntityIceDragon) {
            this.clearFrozen(entity);
            return;
        }

        if (entity.isOnFire()) {
            this.clearFrozen(entity);
            entity.extinguish();
            return;
        }

        if (entity.isDead()) {
            this.clearFrozen(entity);
            return;
        }

        if (this.frozenTicks > 0) {
            this.frozenTicks--;
        } else
            this.clearFrozen(entity);

        if (this.isFrozen && !(entity instanceof PlayerEntity player && player.isCreative())) {
            entity.setVelocity(entity.getVelocity().multiply(0.25F, 1, 0.25F));

            if (!(entity instanceof EnderDragonEntity) && !entity.isOnGround())
                entity.setVelocity(entity.getVelocity().add(0, -0.2, 0));
        }
    }

    public void setFrozen(final LivingEntity target, int duration) {
        if (!this.isFrozen)
            target.playSound(SoundEvents.BLOCK_GLASS_PLACE, 1, 1);

        this.frozenTicks = duration;
        this.isFrozen = true;
        this.markDirty();
    }

    private void clearFrozen(final LivingEntity entity) {
        for (int i = 0; i < 15; i++) {
            entity.getWorld().addParticle(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, IafBlocks.DRAGON_ICE.get().getDefaultState()),
                    entity.getX() + ((entity.getRandom().nextDouble() - 0.5D) * entity.getWidth()),
                    entity.getY() + ((entity.getRandom().nextDouble()) * entity.getHeight()),
                    entity.getZ() + ((entity.getRandom().nextDouble() - 0.5D) * entity.getWidth()),
                    0, 0, 0);
        }

        entity.playSound(SoundEvents.BLOCK_GLASS_BREAK, 3, 1);

        this.isFrozen = false;
        this.frozenTicks = 0;
        this.markDirty();
    }

    public void serialize(final NbtCompound tag) {
        NbtCompound frozenData = new NbtCompound();
        frozenData.putInt("frozenTicks", this.frozenTicks);
        frozenData.putBoolean("isFrozen", this.isFrozen);

        tag.put("frozenData", frozenData);
    }

    public void deserialize(final NbtCompound tag) {
        NbtCompound frozenData = tag.getCompound("frozenData");
        this.frozenTicks = frozenData.getInt("frozenTicks");
        this.isFrozen = frozenData.getBoolean("isFrozen");
    }
}
