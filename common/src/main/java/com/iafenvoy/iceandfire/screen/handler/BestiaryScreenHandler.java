package com.iafenvoy.iceandfire.screen.handler;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Hand;

public class BestiaryScreenHandler extends ScreenHandler {
    private ItemStack bookStack = ItemStack.EMPTY;

    public BestiaryScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(IafScreenHandlers.BESTIARY_SCREEN.get(), syncId);
    }

    public BestiaryScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory);
        NbtCompound nbt = buf.readNbt();
        if (nbt != null)
            this.bookStack = ItemStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, nbt.get("data")).resultOrPartial(IceAndFire.LOGGER::error).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.getStackInHand(Hand.MAIN_HAND).isOf(IafItems.BESTIARY.get()) || player.getStackInHand(Hand.OFF_HAND).isOf(IafItems.BESTIARY.get());
    }

    public ItemStack getBook() {
        return this.bookStack;
    }
}
