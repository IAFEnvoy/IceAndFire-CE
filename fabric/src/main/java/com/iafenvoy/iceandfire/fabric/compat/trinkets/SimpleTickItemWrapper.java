package com.iafenvoy.iceandfire.fabric.compat.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SimpleTickItemWrapper implements Trinket {
    private final Item item;

    public SimpleTickItemWrapper(Item item) {
        this.item = item;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        this.item.inventoryTick(stack, entity.getWorld(), entity, 0, false);
    }
}
