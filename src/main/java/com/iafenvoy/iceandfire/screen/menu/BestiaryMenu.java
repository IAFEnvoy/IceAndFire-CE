package com.iafenvoy.iceandfire.screen.menu;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafMenus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BestiaryMenu extends AbstractContainerMenu {
    private ItemStack bookStack = ItemStack.EMPTY;

    public BestiaryMenu(int syncId, Inventory playerInventory) {
        super(IafMenus.BESTIARY_SCREEN.get(), syncId);
    }

    public BestiaryMenu(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, playerInventory);
        CompoundTag nbt = buf.readNbt();
        if (nbt != null)
            this.bookStack = ItemStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, nbt.get("data")).resultOrPartial(IceAndFire.LOGGER::error).orElse(ItemStack.EMPTY);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).is(IafItems.BESTIARY.get()) || player.getItemInHand(InteractionHand.OFF_HAND).is(IafItems.BESTIARY.get());
    }

    public ItemStack getBook() {
        return this.bookStack;
    }
}
