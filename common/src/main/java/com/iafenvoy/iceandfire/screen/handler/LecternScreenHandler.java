package com.iafenvoy.iceandfire.screen.handler;

import com.iafenvoy.iceandfire.data.BestiaryPages;
import com.iafenvoy.iceandfire.entity.block.BlockEntityLectern;
import com.iafenvoy.iceandfire.item.ItemBestiary;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafScreenHandlers;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.screen.slot.LecternSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;

import java.util.List;

public class LecternScreenHandler extends ScreenHandler {
    private final Inventory tileFurnace;
    private final int[] possiblePagesInt = new int[3];
    private final PropertyDelegate propertyDelegate;

    public LecternScreenHandler(int i, PlayerInventory playerInventory) {
        this(i, new SimpleInventory(2), playerInventory, new ArrayPropertyDelegate(3));
    }

    public LecternScreenHandler(int id, Inventory furnaceInventory, PlayerInventory playerInventory, PropertyDelegate propertyDelegate) {
        super(IafScreenHandlers.IAF_LECTERN_SCREEN.get(), id);
        this.tileFurnace = furnaceInventory;
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
        this.addSlot(new LecternSlot(furnaceInventory, 0, 15, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return super.canInsert(stack) && !stack.isEmpty() && stack.getItem() instanceof ItemBestiary;
            }
        });
        this.addSlot(new Slot(furnaceInventory, 1, 35, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return super.canInsert(stack) && !stack.isEmpty() && stack.getItem() == IafItems.MANUSCRIPT.get();
            }
        });
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for (int k = 0; k < 9; ++k)
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
    }

    private int getPageField(int i) {
        return this.propertyDelegate.get(i);
    }

    public void onUpdate() {
        this.possiblePagesInt[0] = this.getPageField(0);
        this.possiblePagesInt[1] = this.getPageField(1);
        this.possiblePagesInt[2] = this.getPageField(2);
    }

    @Override
    public boolean canUse(PlayerEntity playerIn) {
        return this.tileFurnace.canPlayerUse(playerIn);
    }

    @Override
    public ItemStack quickMove(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.tileFurnace.size()) {
                if (!this.insertItem(itemstack1, this.tileFurnace.size(), this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(0).canInsert(itemstack1) && !this.getSlot(0).hasStack()) {
                if (!this.insertItem(itemstack1, 0, 1, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(1).canInsert(itemstack1) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(itemstack1, 1, 2, false))
                    return ItemStack.EMPTY;
            } else if (this.tileFurnace.size() <= 5 || !this.insertItem(itemstack1, 5, this.tileFurnace.size(), false))
                return ItemStack.EMPTY;
            if (itemstack1.isEmpty())
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            else
                slot.markDirty();
        }
        return itemstack;
    }

    public BestiaryPages[] getPossiblePages() {
        this.onUpdate();
        BestiaryPages[] pages = new BestiaryPages[3];
        List<BestiaryPages> allPages = BestiaryPages.values();
        if (this.tileFurnace.getStack(0).getItem() == IafItems.BESTIARY.get())
            for (int i = 0; i < pages.length; i++) {
                int index = this.possiblePagesInt[i];
                if (index >= 0 && index < allPages.size()) pages[i] = allPages.get(index);
            }
        return pages;
    }

    @Override
    public boolean onButtonClick(PlayerEntity playerIn, int id) {
        if (id < 0 || id >= this.possiblePagesInt.length || !this.canUse(playerIn)) return false;
        this.onUpdate();
        ItemStack bookStack = this.tileFurnace.getStack(0);
        ItemStack manuscriptStack = this.tileFurnace.getStack(1);
        int i = 3;
        if ((manuscriptStack.isEmpty() || manuscriptStack.getCount() < i || manuscriptStack.getItem() != IafItems.MANUSCRIPT.get()))
            return false;
        else if (this.possiblePagesInt[id] >= 0 && !bookStack.isEmpty() && bookStack.getItem() == IafItems.BESTIARY.get()) {
            BestiaryPages page = this.getPossiblePages()[id];
            if (page != null) {
                if (!playerIn.getWorld().isClient) {
                    manuscriptStack.decrement(i);
                    if (manuscriptStack.isEmpty())
                        this.tileFurnace.setStack(1, ItemStack.EMPTY);
                    BestiaryPages.addPage(page, bookStack);
                    if (this.tileFurnace instanceof BlockEntityLectern entityLectern)
                        entityLectern.randomizePages(bookStack, manuscriptStack);
                }
                this.tileFurnace.setStack(0, bookStack);
                this.tileFurnace.markDirty();
                this.onContentChanged(this.tileFurnace);
                playerIn.getWorld().playSound(null, playerIn.getBlockPos(), IafSounds.BESTIARY_PAGE.get(), SoundCategory.BLOCKS, 1.0F, playerIn.getWorld().random.nextFloat() * 0.1F + 0.9F);
            }
            this.onUpdate();
            return true;
        } else
            return false;
    }
}
