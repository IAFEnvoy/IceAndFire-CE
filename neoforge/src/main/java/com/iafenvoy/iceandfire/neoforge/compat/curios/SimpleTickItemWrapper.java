package com.iafenvoy.iceandfire.neoforge.compat.curios;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SimpleTickItemWrapper implements ICurioItem {
    private final Item item;

    public SimpleTickItemWrapper(Item item) {
        this.item = item;
    }

    @Override
    public void curioTick(SlotContext ctx, ItemStack stack) {
        this.item.inventoryTick(stack, ctx.entity().getWorld(), ctx.entity(), 0, false);
    }
}
