package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.entity.DragonArrowEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DragonArrowItem extends ArrowItem {
    public DragonArrowItem() {
        super(new Item.Properties());
    }

    @Override
    public @NotNull AbstractArrow createArrow(@NotNull Level world, @NotNull ItemStack stack, @NotNull LivingEntity shooter, @Nullable ItemStack shotFrom) {
        return new DragonArrowEntity(IafEntities.DRAGON_ARROW.get(), shooter, world, shotFrom);
    }

    @Override
    public @NotNull Projectile asProjectile(@NotNull Level world, Position pos, ItemStack stack, @NotNull Direction direction) {
        DragonArrowEntity arrowEntity = new DragonArrowEntity(IafEntities.DRAGON_ARROW.get(), pos.x(), pos.y(), pos.z(), world, stack.copyWithCount(1), null);
        arrowEntity.pickup = AbstractArrow.Pickup.ALLOWED;
        return arrowEntity;
    }
}
