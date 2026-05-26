package com.iafenvoy.iceandfire.screen.handler;

import com.iafenvoy.iceandfire.entity.HippocampusEntity;
import com.iafenvoy.iceandfire.registry.IafScreenHandlers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class HippocampusScreenHandler extends AbstractContainerMenu {
    private final Container hippocampusInventory;
    private final HippocampusEntity hippocampus;

    public HippocampusScreenHandler(int i, Inventory playerInventory, FriendlyByteBuf buf) {
        this(i, new SimpleContainer(18), playerInventory, (HippocampusEntity) Minecraft.getInstance().level.getEntity(buf.readInt()));
    }

    public HippocampusScreenHandler(int id, Container hippoInventory, Inventory playerInventory, HippocampusEntity hippocampus) {
        super(IafScreenHandlers.HIPPOCAMPUS_SCREEN.get(), id);
        this.hippocampusInventory = hippoInventory;
        this.hippocampus = hippocampus;
        Player player = playerInventory.player;
        this.hippocampusInventory.startOpen(player);

        // Saddle slot
        this.addSlot(new Slot(this.hippocampusInventory, 0, 8, 18) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() == Items.SADDLE && !this.hasItem();
            }

            @Override
            public boolean isActive() {
                return true;
            }
        });

        // Chest slot
        this.addSlot(new Slot(this.hippocampusInventory, 1, 8, 36) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() == Blocks.CHEST.asItem() && !this.hasItem();
            }

            @Override
            public boolean isActive() {
                return true;
            }
        });

        // Armor slot
        this.addSlot(new Slot(this.hippocampusInventory, 2, 8, 52) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return HippocampusEntity.getIntFromArmor(stack) != 0;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean isActive() {
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
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            int i = this.hippocampusInventory.getContainerSize() + 1;
            if (slot < i) {
                if (!this.moveItemStackTo(itemStack2, i, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(1).mayPlace(itemStack2) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo(itemStack2, 1, 2, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(0).mayPlace(itemStack2)) {
                if (!this.moveItemStackTo(itemStack2, 0, 1, false))
                    return ItemStack.EMPTY;
            } else if (i <= 1 || !this.moveItemStackTo(itemStack2, 2, i, false)) {
                int k = i + 27;
                int m = k + 9;
                if (slot >= k && slot < m) {
                    if (!this.moveItemStackTo(itemStack2, i, k, false))
                        return ItemStack.EMPTY;
                } else if (slot < k) {
                    if (!this.moveItemStackTo(itemStack2, k, m, false))
                        return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(itemStack2, k, k, false))
                    return ItemStack.EMPTY;
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) slot2.setByPlayer(ItemStack.EMPTY);
            else slot2.setChanged();
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return !this.hippocampus.hasInventoryChanged(this.hippocampusInventory) && this.hippocampusInventory.stillValid(playerIn) && this.hippocampus.isAlive() && this.hippocampus.distanceTo(playerIn) < 8.0F;
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        this.hippocampusInventory.stopOpen(playerIn);
    }

    public HippocampusEntity getHippocampus() {
        return this.hippocampus;
    }
}