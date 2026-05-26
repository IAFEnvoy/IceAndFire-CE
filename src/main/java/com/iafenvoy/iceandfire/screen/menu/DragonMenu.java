package com.iafenvoy.iceandfire.screen.menu;

import com.iafenvoy.iceandfire.data.DragonArmorPart;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.registry.IafMenus;
import com.iafenvoy.iceandfire.screen.slot.BannerSlot;
import com.iafenvoy.iceandfire.screen.slot.DragonArmorSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DragonMenu extends AbstractContainerMenu {
    private final Container dragonInventory;
    private final DragonBaseEntity dragon;

    public DragonMenu(int i, Inventory playerInventory, FriendlyByteBuf buf) {
        this(i, new SimpleContainer(5), playerInventory, (DragonBaseEntity) Minecraft.getInstance().level.getEntity(buf.readInt()));
    }

    public DragonMenu(int id, Container dragonInventory, Inventory playerInventory, DragonBaseEntity dragon) {
        super(IafMenus.DRAGON_SCREEN.get(), id);
        this.dragonInventory = dragonInventory;
        this.dragon = dragon;
        dragonInventory.startOpen(playerInventory.player);
        this.addSlot(new BannerSlot(dragonInventory, 0, 8, 54));
        this.addSlot(new DragonArmorSlot(dragonInventory, 1, 8, 18, DragonArmorPart.HEAD));
        this.addSlot(new DragonArmorSlot(dragonInventory, 2, 8, 36, DragonArmorPart.NECK));
        this.addSlot(new DragonArmorSlot(dragonInventory, 3, 153, 18, DragonArmorPart.BODY));
        this.addSlot(new DragonArmorSlot(dragonInventory, 4, 153, 36, DragonArmorPart.TAIL));
        for (int j = 0; j < 3; ++j)
            for (int k = 0; k < 9; ++k)
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 132 + j * 18));
        for (int j = 0; j < 9; ++j)
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 190));
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return !this.dragon.hasInventoryChanged(this.dragonInventory) && this.dragonInventory.stillValid(playerIn) && this.dragon.isAlive() && this.dragon.distanceTo(playerIn) < 8.0F;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.dragonInventory.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.dragonInventory.getContainerSize(), this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(2).mayPlace(itemstack1) && !this.getSlot(2).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 2, 3, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(3).mayPlace(itemstack1) && !this.getSlot(3).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 3, 4, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(4).mayPlace(itemstack1) && !this.getSlot(4).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 4, 5, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(0).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false))
                    return ItemStack.EMPTY;
            } else if (this.dragonInventory.getContainerSize() <= 5 || !this.moveItemStackTo(itemstack1, 5, this.dragonInventory.getContainerSize(), false))
                return ItemStack.EMPTY;
            if (itemstack1.isEmpty())
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();
        }
        return itemstack;
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        this.dragonInventory.stopOpen(playerIn);
    }

    public DragonBaseEntity getDragon() {
        return this.dragon;
    }
}