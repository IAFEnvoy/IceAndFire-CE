package com.iafenvoy.iceandfire.entity.block;

import com.iafenvoy.iceandfire.item.ItemDragonEgg;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.screen.handler.PodiumScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BlockEntityPodium extends LockableContainerBlockEntity implements SidedInventory {
    private static final int[] slotsTop = new int[]{0};
    public int ticksExisted;
    public int prevTicksExisted;
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public BlockEntityPodium(BlockPos pos, BlockState state) {
        super(IafBlockEntities.PODIUM.get(), pos, state);
    }

    //TODO: This must be easier to do
    public static void tick(World level, BlockPos pos, BlockState state, BlockEntityPodium entityPodium) {
        entityPodium.prevTicksExisted = entityPodium.ticksExisted;
        entityPodium.ticksExisted++;
    }

    @Override
    public int size() {
        return this.stacks.size();
    }

    @Override
    public ItemStack getStack(int index) {
        return this.stacks.get(index);
    }

    @Override
    public ItemStack removeStack(int index, int count) {
        ItemStack removed = Inventories.splitStack(this.stacks, index, count);
        if (!removed.isEmpty()) BlockEntitySync.changed(this);
        return removed;
    }

    public ItemStack getStackInSlotOnClosing(int index) {
        return this.removeStack(index);
    }

    @Override
    public void setStack(int index, ItemStack stack) {
        this.stacks.set(index, stack);
        if (stack.getCount() > Math.min(this.getMaxCountPerStack(), stack.getMaxCount()))
            stack.setCount(Math.min(this.getMaxCountPerStack(), stack.getMaxCount()));
        BlockEntitySync.changed(this);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.stacks = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(compound, this.stacks);
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        Inventories.writeNbt(compound, this.stacks);
    }

    @Override
    public void onOpen(PlayerEntity player) {
    }

    @Override
    public void onClose(PlayerEntity player) {
    }

    @Override
    public boolean canInsert(int index, ItemStack stack, Direction direction) {
        return index != 0 || (stack.getItem() instanceof ItemDragonEgg);
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.getWorld().getBlockEntity(this.pos) == this
                && player.squaredDistanceTo(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        this.stacks.clear();
        BlockEntitySync.changed(this);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return slotsTop;
    }

    @Override
    public boolean canExtract(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean isValid(int index, ItemStack stack) {
        return false;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbtWithIdentifyingData();
    }

    @Override
    public ItemStack removeStack(int index) {
        ItemStack removed = Inventories.removeStack(this.stacks, index);
        if (!removed.isEmpty()) BlockEntitySync.changed(this);
        return removed;
    }

    @Override
    public Text getDisplayName() {
        return this.getContainerName();
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("block.iceandfire.podium");
    }

    @Override
    protected ScreenHandler createScreenHandler(int id, PlayerInventory player) {
        return new PodiumScreenHandler(id, this, player);
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.size(); i++)
            if (!this.getStack(i).isEmpty())
                return false;
        return true;
    }

    @Override
    public ScreenHandler createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new PodiumScreenHandler(id, this, playerInventory);
    }
}
