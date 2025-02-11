package com.iafenvoy.iceandfire.screen.handler;

import com.iafenvoy.iceandfire.registry.IafScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class PodiumScreenHandler extends ScreenHandler {
    public final Inventory podium;

    public PodiumScreenHandler(int i, PlayerInventory playerInventory) {
        this(i, new SimpleInventory(1), playerInventory);
    }

    public PodiumScreenHandler(int id, Inventory furnaceInventory, PlayerInventory playerInventory) {
        super(IafScreenHandlers.PODIUM_SCREEN.get(), id);
        this.podium = furnaceInventory;
        furnaceInventory.onOpen(playerInventory.player);
        byte b0 = 51;
        this.addSlot(new Slot(furnaceInventory, 0, 80, 20));
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, i * 18 + b0));
        for (int i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 58 + b0));
    }

    @Override
    public boolean canUse(PlayerEntity playerIn) {
        return this.podium.canPlayerUse(playerIn);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    @Override
    public ItemStack quickMove(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.podium.size()) {
                if (!this.insertItem(itemstack1, this.podium.size(), this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!this.insertItem(itemstack1, 0, this.podium.size(), false))
                return ItemStack.EMPTY;
            if (itemstack1.isEmpty())
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            else
                slot.markDirty();
        }
        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onClosed(PlayerEntity playerIn) {
        super.onClosed(playerIn);
        this.podium.onClose(playerIn);
    }
}