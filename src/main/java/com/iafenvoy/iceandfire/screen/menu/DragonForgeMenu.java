package com.iafenvoy.iceandfire.screen.menu;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.registry.IafMenus;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.registry.IafResourceKeys;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DragonForgeMenu extends AbstractContainerMenu {
    protected final Level world;
    private final Container tileFurnace;
    private final DragonType dragonType;
    private final ContainerData propertyDelegate;

    public DragonForgeMenu(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, new SimpleContainer(3), playerInventory, IafRegistries.DRAGON_TYPE.get(buf.readResourceKey(IafResourceKeys.DRAGON_TYPE)), new SimpleContainerData(2));
    }

    public DragonForgeMenu(int syncId, Container furnaceInventory, Inventory playerInventory, DragonType dragonType, ContainerData delegate) {
        super(IafMenus.DRAGON_FORGE_SCREEN.get(), syncId);
        this.tileFurnace = furnaceInventory;
        this.world = playerInventory.player.level();
        this.dragonType = dragonType;
        checkContainerDataCount(delegate, 2);
        this.propertyDelegate = delegate;
        this.addDataSlots(this.propertyDelegate);
        this.addSlot(new Slot(furnaceInventory, 0, 68, 34));
        this.addSlot(new Slot(furnaceInventory, 1, 86, 34));
        this.addSlot(new FurnaceResultSlot(playerInventory.player, furnaceInventory, 2, 148, 35));
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for (int k = 0; k < 9; ++k)
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return this.tileFurnace.stillValid(playerIn);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            if (index == 2) {
                if (!this.moveItemStackTo(slotStack, 3, 39, true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(slotStack, stack);
            } else if (index != 1 && index != 0) {
                if (!this.moveItemStackTo(slotStack, 0, 1, false))
                    return ItemStack.EMPTY;
                else if (index < 30) {
                    if (!this.moveItemStackTo(slotStack, 30, 39, false))
                        return ItemStack.EMPTY;
                } else if (index < 39 && !this.moveItemStackTo(slotStack, 3, 30, false))
                    return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(slotStack, 3, 39, false))
                return ItemStack.EMPTY;

            if (slotStack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();

            if (slotStack.getCount() == stack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(playerIn, slotStack);
        }

        return stack;
    }

    public int getCookTime() {
        return this.propertyDelegate.get(0);
    }

    public int getMaxCookTime() {
        return this.propertyDelegate.get(1);
    }

    public DragonType getDragonType() {
        return this.dragonType;
    }
}
