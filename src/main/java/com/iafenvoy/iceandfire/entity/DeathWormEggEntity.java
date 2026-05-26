package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class DeathWormEggEntity extends ThrowableItemProjectile {
    private boolean giant;

    public DeathWormEggEntity(EntityType<? extends ThrowableItemProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    public DeathWormEggEntity(EntityType<? extends ThrowableItemProjectile> type, LivingEntity throwerIn, Level worldIn, boolean giant) {
        super(type, throwerIn, worldIn);
        this.giant = giant;
    }

    public DeathWormEggEntity(EntityType<? extends ThrowableItemProjectile> type, double x, double y, double z,
                              Level worldIn, boolean giant) {
        super(type, x, y, z, worldIn);
        this.giant = giant;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), (this.random.nextFloat() - 0.5D) * 0.08D, (this.random.nextFloat() - 0.5D) * 0.08D, (this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onHit(HitResult result) {
        Entity thrower = this.getOwner();
        if (result.getType() == HitResult.Type.ENTITY)
            ((EntityHitResult) result).getEntity().hurt(this.level().damageSources().thrown(this, thrower), 0.0F);

        if (!this.level().isClientSide) {
            float wormSize = 0.25F + (float) (Math.random() * 0.35F);

            DeathWormEntity deathworm = new DeathWormEntity(IafEntities.DEATH_WORM.get(), this.level());
            deathworm.setVariant(this.random.nextInt(3));
            deathworm.setTame(true, false);
            deathworm.setWormHome(this.blockPosition());
            deathworm.setWormAge(1);
            deathworm.setDeathWormScale(this.giant ? (wormSize * 4) : wormSize);
            deathworm.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            if (thrower instanceof Player) deathworm.setOwnerUUID(thrower.getUUID());
            this.level().addFreshEntity(deathworm);
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return this.giant ? IafItems.DEATHWORM_EGG_GIGANTIC.get() : IafItems.DEATHWORM_EGG.get();
    }
}
