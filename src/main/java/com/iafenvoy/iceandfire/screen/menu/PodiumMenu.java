package com.iafenvoy.iceandfire.screen.menu;

import com.iafenvoy.iceandfire.registry.IafMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PodiumMenu extends AbstractContainerMenu {
    public final Container podium;

    public PodiumMenu(int i, Inventory playerInventory) {
        this(i, new SimpleContainer(1), playerInventory);
    }

    public PodiumMenu(int id, Container furnaceInventory, Inventory playerInventory) {
        super(IafMenus.PODIUM_SCREEN.get(), id);
        this.podium = furnaceInventory;
        furnaceInventory.startOpen(playerInventory.player);
        byte b0 = 51;
        this.addSlot(new Slot(furnaceInventory, 0, 80, 20));
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, i * 18 + b0));
        for (int i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 58 + b0));
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return this.podium.stillValid(playerIn);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.podium.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.podium.getContainerSize(), this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(itemstack1, 0, this.podium.getContainerSize(), false))
                return ItemStack.EMPTY;
            if (itemstack1.isEmpty())
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();
        }
        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        this.podium.stopOpen(playerIn);
    }
}