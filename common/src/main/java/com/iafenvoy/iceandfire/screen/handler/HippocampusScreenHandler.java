package com.iafenvoy.iceandfire.screen.handler;

import com.iafenvoy.iceandfire.entity.EntityHippocampus;
import com.iafenvoy.iceandfire.registry.IafScreenHandlers;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class HippocampusScreenHandler extends ScreenHandler {
    private final Inventory hippocampusInventory;
    private final EntityHippocampus hippocampus;

    public HippocampusScreenHandler(int i, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(i, new SimpleInventory(18), playerInventory, (EntityHippocampus) MinecraftClient.getInstance().world.getEntityById(buf.readInt()));
    }

    public HippocampusScreenHandler(int id, Inventory hippoInventory, PlayerInventory playerInventory, EntityHippocampus hippocampus) {
        super(IafScreenHandlers.HIPPOCAMPUS_SCREEN.get(), id);
        this.hippocampusInventory = hippoInventory;
        this.hippocampus = hippocampus;
        PlayerEntity player = playerInventory.player;
        this.hippocampusInventory.onOpen(player);

        // Saddle slot
        this.addSlot(new Slot(this.hippocampusInventory, 0, 8, 18) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.SADDLE && !this.hasStack();
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        });

        // Chest slot
        this.addSlot(new Slot(this.hippocampusInventory, 1, 8, 36) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Blocks.CHEST.asItem() && !this.hasStack();
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        });

        // Armor slot
        this.addSlot(new Slot(this.hippocampusInventory, 2, 8, 52) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return EntityHippocampus.getIntFromArmor(stack) != 0;
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        });

        // Create the slots for the inventory
        if (this.hippocampus != null && this.hippocampus.isChested())
            for (int k = 0; k < 3; ++k)
                for (int l = 0; l < this.hippocampus.getInventoryColumns(); ++l)
                    this.addSlot(new Slot(hippoInventory, 3 + l + k * this.hippocampus.getInventoryColumns(), 80 + l * 18, 18 + k * 18));
        for (int i1 = 0; i1 < 3; ++i1)
            for (int k1 = 0; k1 < 9; ++k1)
                this.addSlot(new Slot(player.getInventory(), k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 - 18));
        for (int j1 = 0; j1 < 9; ++j1)
            this.addSlot(new Slot(player.getInventory(), j1, 8 + j1 * 18, 142));
    }

    @Override
    public ItemStack quickMove(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            int containerSize = this.hippocampusInventory.size();
            if (index < containerSize) {
                if (!this.insertItem(itemstack1, containerSize, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(1).canInsert(itemstack1) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(itemstack1, 1, 2, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(2).canInsert(itemstack1) && !this.getSlot(2).hasStack()) {
                if (!this.insertItem(itemstack1, 2, 3, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(0).canInsert(itemstack1)) {
                if (!this.insertItem(itemstack1, 0, 1, false))
                    return ItemStack.EMPTY;
            } else if (containerSize <= 3 || !this.insertItem(itemstack1, 3, containerSize, false)) {
                int j = containerSize + 27;
                int k = j + 9;
                if (index >= j && index < k) {
                    if (!this.insertItem(itemstack1, containerSize, j, false))
                        return ItemStack.EMPTY;
                } else if (index < j) {
                    if (!this.insertItem(itemstack1, j, k, false))
                        return ItemStack.EMPTY;
                } else if (!this.insertItem(itemstack1, j, j, false))
                    return ItemStack.EMPTY;
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty())
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            else
                slot.markDirty();
        }
        return itemstack;
    }

    @Override
    public boolean canUse(PlayerEntity playerIn) {
        return !this.hippocampus.hasInventoryChanged(this.hippocampusInventory) && this.hippocampusInventory.canPlayerUse(playerIn) && this.hippocampus.isAlive() && this.hippocampus.distanceTo(playerIn) < 8.0F;
    }

    @Override
    public void onClosed(PlayerEntity playerIn) {
        super.onClosed(playerIn);
        this.hippocampusInventory.onClose(playerIn);
    }

    public EntityHippocampus getHippocampus() {
        return this.hippocampus;
    }
}