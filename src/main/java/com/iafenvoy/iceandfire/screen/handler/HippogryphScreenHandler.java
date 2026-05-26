package com.iafenvoy.iceandfire.screen.handler;

import com.iafenvoy.iceandfire.entity.HippogryphEntity;
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
import org.jetbrains.annotations.NotNull;

public class HippogryphScreenHandler extends AbstractContainerMenu {
    private final Container hippogryphInventory;
    private final HippogryphEntity hippogryph;

    public HippogryphScreenHandler(int i, Inventory playerInventory, FriendlyByteBuf buf) {
        this(i, new SimpleContainer(18), playerInventory, (HippogryphEntity) Minecraft.getInstance().level.getEntity(buf.readInt()));
    }

    public HippogryphScreenHandler(int id, Container hippogryphInventory, Inventory playerInventory, HippogryphEntity hippogryph) {
        super(IafScreenHandlers.HIPPOGRYPH_SCREEN.get(), id);
        this.hippogryphInventory = hippogryphInventory;
        this.hippogryph = hippogryph;
        Player player = playerInventory.player;
        this.hippogryphInventory.startOpen(player);
        this.addSlot(new Slot(this.hippogryphInventory, 0, 8, 18) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() == Items.SADDLE && !this.hasItem();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                if (HippogryphScreenHandler.this.hippogryph != null)
                    HippogryphScreenHandler.this.hippogryph.setSaddled(this.hasItem() && this.getItem().is(Items.SADDLE));
            }

            @Override
            public boolean isActive() {
                return true;
            }
        });
        this.addSlot(new Slot(this.hippogryphInventory, 1, 8, 36) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.CHEST) && !this.hasItem();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                if (HippogryphScreenHandler.this.hippogryph != null)
                    HippogryphScreenHandler.this.hippogryph.setChested(this.hasItem() && this.getItem().is(Items.CHEST));
            }

            @Override
            public boolean isActive() {
                return true;
            }
        });
        this.addSlot(new Slot(this.hippogryphInventory, 2, 8, 52) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return HippogryphEntity.getIntFromArmor(stack) != 0;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                if (HippogryphScreenHandler.this.hippogryph != null)
                    HippogryphScreenHandler.this.hippogryph.setArmor(this.hasItem() ? HippogryphEntity.getIntFromArmor(this.getItem()) : 0);
            }

            @Override
            public boolean isActive() {
                return true;
            }
        });

        for (int k = 0; k < 3; ++k)
            for (int l = 0; l < 5; ++l)
                this.addSlot(new Slot(this.hippogryphInventory, 3 + l + k * 5, 80 + l * 18, 18 + k * 18) {
                    @Override
                    public boolean isActive() {
                        return HippogryphScreenHandler.this.hippogryph != null && HippogryphScreenHandler.this.hippogryph.isChested();
                    }

                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return HippogryphScreenHandler.this.hippogryph != null && HippogryphScreenHandler.this.hippogryph.isChested();
                    }
                });

        for (int i1 = 0; i1 < 3; ++i1)
            for (int k1 = 0; k1 < 9; ++k1)
                this.addSlot(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 - 18));

        for (int j1 = 0; j1 < 9; ++j1)
            this.addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 142));
    }


    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.hippogryphInventory.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.hippogryphInventory.getContainerSize(), this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(2).mayPlace(itemstack1) && !this.getSlot(2).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 2, 3, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(0).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false))
                    return ItemStack.EMPTY;
            } else if (this.hippogryphInventory.getContainerSize() <= 3 || !this.moveItemStackTo(itemstack1, 3, this.hippogryphInventory.getContainerSize(), false))
                return ItemStack.EMPTY;
            if (itemstack1.isEmpty())
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return this.hippogryphInventory.stillValid(playerIn) && this.hippogryph.isAlive() && this.hippogryph.distanceTo(playerIn) < 8.0F;
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        this.hippogryphInventory.stopOpen(playerIn);
    }

    public HippogryphEntity getHippogryph() {
        return this.hippogryph;
    }
}