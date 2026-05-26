package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class StymphalianFeatherEntity extends AbstractArrow {
    public StymphalianFeatherEntity(EntityType<? extends AbstractArrow> t, Level worldIn) {
        super(t, worldIn);
    }

    public StymphalianFeatherEntity(EntityType<? extends AbstractArrow> t, Level worldIn, LivingEntity shooter) {
        super(t, worldIn);
        this.setOwner(shooter);
        this.setBaseDamage(IafCommonConfig.INSTANCE.stymphalianBird.featherAttackDamage.getValue());
        this.setPos(shooter.position());
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        if (IafCommonConfig.INSTANCE.stymphalianBird.featherDropChance.getValue() > 0)
            if (this.level().isClientSide)
                if (this.random.nextDouble() < IafCommonConfig.INSTANCE.stymphalianBird.featherDropChance.getValue())
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 100) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHit) {
        Entity shootingEntity = this.getOwner();
        if (!(shootingEntity instanceof StymphalianBirdEntity) || !(entityHit.getEntity() instanceof StymphalianBirdEntity)) {
            super.onHitEntity(entityHit);
            if (entityHit.getEntity() instanceof StymphalianBirdEntity bird)
                bird.setArrowCount(bird.getArrowCount() - 1);
        }
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return new ItemStack(IafItems.STYMPHALIAN_BIRD_FEATHER.get());
    }
}
