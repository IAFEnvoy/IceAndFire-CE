package com.iafenvoy.iceandfire.screen.handler;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.registry.IafRegistryKeys;
import com.iafenvoy.iceandfire.registry.IafScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class DragonForgeScreenHandler extends ScreenHandler {
    protected final World world;
    private final Inventory tileFurnace;
    private final DragonType dragonType;
    private final PropertyDelegate propertyDelegate;

    public DragonForgeScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, new SimpleInventory(3), playerInventory, IafRegistries.DRAGON_TYPE.get(buf.readRegistryKey(IafRegistryKeys.DRAGON_TYPE)), new ArrayPropertyDelegate(1));
    }

    public DragonForgeScreenHandler(int syncId, Inventory furnaceInventory, PlayerInventory playerInventory, DragonType dragonType, PropertyDelegate delegate) {
        super(IafScreenHandlers.DRAGON_FORGE_SCREEN.get(), syncId);
        this.tileFurnace = furnaceInventory;
        this.world = playerInventory.player.getWorld();
        this.dragonType = dragonType;
        checkDataCount(delegate, 1);
        this.propertyDelegate = delegate;
        this.addProperties(this.propertyDelegate);
        this.addSlot(new Slot(furnaceInventory, 0, 68, 34));
        this.addSlot(new Slot(furnaceInventory, 1, 86, 34));
        this.addSlot(new FurnaceOutputSlot(playerInventory.player, furnaceInventory, 2, 148, 35));
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for (int k = 0; k < 9; ++k)
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
    }

    @Override
    public boolean canUse(PlayerEntity playerIn) {
        return this.tileFurnace.canPlayerUse(playerIn);
    }

    @Override
    public ItemStack quickMove(PlayerEntity playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (index == 2) {
                if (!this.insertItem(slotStack, 3, 39, true))
                    return ItemStack.EMPTY;
                slot.onQuickTransfer(slotStack, stack);
            } else if (index != 1 && index != 0) {
                if (!this.insertItem(slotStack, 0, 1, false))
                    return ItemStack.EMPTY;
                else if (index < 30) {
                    if (!this.insertItem(slotStack, 30, 39, false))
                        return ItemStack.EMPTY;
                } else if (index < 39 && !this.insertItem(slotStack, 3, 30, false))
                    return ItemStack.EMPTY;
            } else if (!this.insertItem(slotStack, 3, 39, false))
                return ItemStack.EMPTY;

            if (slotStack.isEmpty()) slot.setStackNoCallbacks(ItemStack.EMPTY);
            else slot.markDirty();

            if (slotStack.getCount() == stack.getCount())
                return ItemStack.EMPTY;

            slot.onTakeItem(playerIn, slotStack);
        }

        return stack;
    }

    public PropertyDelegate getPropertyDelegate() {
        return this.propertyDelegate;
    }

    public DragonType getDragonType() {
        return this.dragonType;
    }
}
