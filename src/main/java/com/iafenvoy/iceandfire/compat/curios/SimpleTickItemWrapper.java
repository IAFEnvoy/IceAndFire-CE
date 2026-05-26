package com.iafenvoy.iceandfire.compat.curios;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SimpleTickItemWrapper implements ICurioItem {
    private final Item item;

    public SimpleTickItemWrapper(Item item) {
        this.item = item;
    }

    @Override
    public void curioTick(SlotContext ctx, ItemStack stack) {
        this.item.inventoryTick(stack, ctx.entity().level(), ctx.entity(), 0, false);
    }
}
