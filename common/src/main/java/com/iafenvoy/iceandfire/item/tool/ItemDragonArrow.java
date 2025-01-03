package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.entity.EntityDragonArrow;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDragonArrow extends ArrowItem {
    public ItemDragonArrow() {
        super(new Settings());
    }

    @Override
    public PersistentProjectileEntity createArrow(final World level, final ItemStack arrow, final LivingEntity shooter) {
        return new EntityDragonArrow(IafEntities.DRAGON_ARROW.get(), shooter, level);
    }
}
