package com.iafenvoy.iceandfire.screen.handler;

import com.iafenvoy.iceandfire.entity.HippocampusEntity;
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
    private final HippocampusEntity hippocampus;

    public HippocampusScreenHandler(int i, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(i, new SimpleInventory(18), playerInventory, (HippocampusEntity) MinecraftClient.getInstance().world.getEntityById(buf.readInt()));
    }

    public HippocampusScreenHandler(int id, Inventory hippoInventory, PlayerInventory playerInventory, HippocampusEntity hippocampus) {
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
                return HippocampusEntity.getIntFromArmor(stack) != 0;
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
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            int i = this.hippocampusInventory.size() + 1;
            if (slot < i) {
                if (!this.insertItem(itemStack2, i, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(1).canInsert(itemStack2) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(itemStack2, 1, 2, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(0).canInsert(itemStack2)) {
                if (!this.insertItem(itemStack2, 0, 1, false))
                    return ItemStack.EMPTY;
            } else if (i <= 1 || !this.insertItem(itemStack2, 2, i, false)) {
                int k = i + 27;
                int m = k + 9;
                if (slot >= k && slot < m) {
                    if (!this.insertItem(itemStack2, i, k, false))
                        return ItemStack.EMPTY;
                } else if (slot < k) {
                    if (!this.insertItem(itemStack2, k, m, false))
                        return ItemStack.EMPTY;
                } else if (!this.insertItem(itemStack2, k, k, false))
                    return ItemStack.EMPTY;
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) slot2.setStack(ItemStack.EMPTY);
            else slot2.markDirty();
        }

        return itemStack;
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

    public HippocampusEntity getHippocampus() {
        return this.hippocampus;
    }
}