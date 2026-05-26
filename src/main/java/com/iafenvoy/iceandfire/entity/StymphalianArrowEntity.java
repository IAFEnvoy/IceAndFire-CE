package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class StymphalianArrowEntity extends AbstractArrow {
    public StymphalianArrowEntity(EntityType<? extends AbstractArrow> t, Level worldIn) {
        super(t, worldIn);
        this.setBaseDamage(3.5F);
    }

    public StymphalianArrowEntity(EntityType<? extends AbstractArrow> t, Level worldIn, double x, double y, double z) {
        this(t, worldIn);
        this.setPos(x, y, z);
        this.setBaseDamage(3.5F);
    }

    public StymphalianArrowEntity(EntityType<? extends AbstractArrow> t, Level worldIn, LivingEntity shooter, ItemStack from) {
        super(t, shooter, worldIn, new ItemStack(IafItems.STYMPHALIAN_ARROW.get()), from);
        this.setBaseDamage(3.5F);
    }

    @Override
    public void tick() {
        super.tick();
        float sqrt = Mth.sqrt((float) (this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z));
        if (sqrt < 0.1F) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.01F, 0));
        }
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return new ItemStack(IafItems.STYMPHALIAN_ARROW.get());
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }
}
