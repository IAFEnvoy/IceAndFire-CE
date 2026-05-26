package com.iafenvoy.iceandfire.screen.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BannerSlot extends Slot {
    public BannerSlot(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return super.mayPlace(stack) && stack.getItem() instanceof BannerItem;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
