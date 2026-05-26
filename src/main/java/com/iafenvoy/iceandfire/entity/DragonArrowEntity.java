package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DragonArrowEntity extends AbstractArrow {
    public DragonArrowEntity(EntityType<? extends AbstractArrow> typeIn, Level worldIn) {
        super(typeIn, worldIn);
        this.setBaseDamage(10);
    }

    public DragonArrowEntity(EntityType<? extends AbstractArrow> typeIn, double x, double y, double z, Level world, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(typeIn, x, y, z, world, stack, shotFrom);
        this.setBaseDamage(10);
    }

    public DragonArrowEntity(EntityType<? extends AbstractArrow> typeIn, LivingEntity shooter, Level worldIn, ItemStack from) {
        super(typeIn, shooter, worldIn, new ItemStack(IafItems.DRAGONBONE_ARROW.get()), from);
        this.setBaseDamage(10);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tagCompound) {
        super.addAdditionalSaveData(tagCompound);
        tagCompound.putDouble("damage", 10);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tagCompund) {
        super.readAdditionalSaveData(tagCompund);
        this.setBaseDamage(tagCompund.getDouble("damage"));
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return new ItemStack(IafItems.DRAGONBONE_ARROW.get());
    }
}