package com.iafenvoy.iceandfire.screen.menu;

import com.iafenvoy.iceandfire.data.BestiaryPage;
import com.iafenvoy.iceandfire.item.BestiaryItem;
import com.iafenvoy.iceandfire.item.block.entity.LecternBlockEntity;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.registry.IafMenus;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.screen.slot.LecternSlot;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LecternMenu extends AbstractContainerMenu {
    private final Container tileFurnace;
    private final int[] possiblePagesInt = new int[3];
    private final ContainerData propertyDelegate;

    public LecternMenu(int i, Inventory playerInventory) {
        this(i, new SimpleContainer(2), playerInventory, new SimpleContainerData(3));
    }

    public LecternMenu(int id, Container furnaceInventory, Inventory playerInventory, ContainerData propertyDelegate) {
        super(IafMenus.IAF_LECTERN_SCREEN.get(), id);
        this.tileFurnace = furnaceInventory;
        this.propertyDelegate = propertyDelegate;
        this.addDataSlots(propertyDelegate);
        this.addSlot(new LecternSlot(furnaceInventory, 0, 15, 47) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return super.mayPlace(stack) && !stack.isEmpty() && stack.getItem() instanceof BestiaryItem;
            }
        });
        this.addSlot(new Slot(furnaceInventory, 1, 35, 47) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return super.mayPlace(stack) && !stack.isEmpty() && stack.getItem() == IafItems.MANUSCRIPT.get();
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
    public boolean stillValid(@NotNull Player playerIn) {
        return this.tileFurnace.stillValid(playerIn);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.tileFurnace.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.tileFurnace.getContainerSize(), this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(0).mayPlace(itemstack1) && !this.getSlot(0).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false))
                    return ItemStack.EMPTY;
            } else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, false))
                    return ItemStack.EMPTY;
            } else if (this.tileFurnace.getContainerSize() <= 5 || !this.moveItemStackTo(itemstack1, 5, this.tileFurnace.getContainerSize(), false))
                return ItemStack.EMPTY;
            if (itemstack1.isEmpty())
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();
        }
        return itemstack;
    }

    public BestiaryPage[] getPossiblePages() {
        this.possiblePagesInt[0] = this.getPageField(0);
        this.possiblePagesInt[1] = this.getPageField(1);
        this.possiblePagesInt[2] = this.getPageField(2);
        BestiaryPage[] pages = new BestiaryPage[3];
        List<BestiaryPage> allPages = IafRegistries.BESTIARY_PAGE.stream().toList();
        if (this.tileFurnace.getItem(0).getItem() == IafItems.BESTIARY.get()) {
            if (this.possiblePagesInt[0] < 0) pages[0] = null;
            else pages[0] = allPages.get(Math.min(allPages.size(), this.possiblePagesInt[0]));
            if (this.possiblePagesInt[1] < 0) pages[1] = null;
            else pages[1] = allPages.get(Math.min(allPages.size(), this.possiblePagesInt[1]));
            if (this.possiblePagesInt[2] < 0) pages[2] = null;
            else pages[2] = allPages.get(Math.min(allPages.size(), this.possiblePagesInt[2]));
        }
        return pages;
    }

    @Override
    public boolean clickMenuButton(@NotNull Player playerIn, int id) {
        this.onUpdate();
        ItemStack bookStack = this.tileFurnace.getItem(0);
        ItemStack manuscriptStack = this.tileFurnace.getItem(1);
        int i = 3;
        if ((manuscriptStack.isEmpty() || manuscriptStack.getCount() < i || manuscriptStack.getItem() != IafItems.MANUSCRIPT.get()))
            return false;
        else if (this.possiblePagesInt[id] > 0 && !bookStack.isEmpty() && bookStack.getItem() == IafItems.BESTIARY.get()) {
            BestiaryPage page = this.getPossiblePages()[Mth.clamp(id, 0, 2)];
            if (page != null) {
                if (!playerIn.level().isClientSide) {
                    manuscriptStack.shrink(i);
                    if (manuscriptStack.isEmpty())
                        this.tileFurnace.setItem(1, ItemStack.EMPTY);
                    BestiaryPage.addPage(page, bookStack);
                    if (this.tileFurnace instanceof LecternBlockEntity entityLectern)
                        entityLectern.randomizePages(bookStack, manuscriptStack);
                }
                this.tileFurnace.setItem(0, bookStack);
                this.tileFurnace.setChanged();
                this.slotsChanged(this.tileFurnace);
                playerIn.level().playSound(null, playerIn.blockPosition(), IafSounds.BESTIARY_PAGE.get(), SoundSource.BLOCKS, 1.0F, playerIn.level().random.nextFloat() * 0.1F + 0.9F);
            }
            this.onUpdate();
            return true;
        } else
            return false;
    }
}