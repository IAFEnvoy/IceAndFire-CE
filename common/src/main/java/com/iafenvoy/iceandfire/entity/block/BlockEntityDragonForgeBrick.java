package com.iafenvoy.iceandfire.entity.block;

import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockEntityDragonForgeBrick extends BlockEntity implements SidedInventory {
    @Nullable
    private BlockEntityDragonForge core = null;

    public BlockEntityDragonForgeBrick(BlockPos pos, BlockState state) {
        super(IafBlockEntities.DRAGONFORGE_BRICK.get(), pos, state);
    }

    public static void tick(final World level, final BlockPos position, final BlockState state, final BlockEntityDragonForgeBrick forgeInput) {
        forgeInput.core = forgeInput.getConnectedTileEntity(position);
    }

    private BlockEntityDragonForge getConnectedTileEntity(final BlockPos position) {
        assert this.world != null;
        for (Direction facing : Direction.values())
            if (this.world.getBlockEntity(position.offset(facing)) instanceof BlockEntityDragonForge forge)
                return forge;
        return null;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return this.core == null ? new int[0] : this.core.getAvailableSlots(side);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.core != null && this.core.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return this.core != null && this.core.canExtract(slot, stack, dir);
    }

    @Override
    public int size() {
        return this.core == null ? 0 : this.core.size();
    }

    @Override
    public boolean isEmpty() {
        return this.core == null || this.core.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.core == null ? ItemStack.EMPTY : this.core.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.core == null ? ItemStack.EMPTY : this.core.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.core == null ? ItemStack.EMPTY : this.core.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (this.core != null) this.core.setStack(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.core != null && this.core.canPlayerUse(player);
    }

    @Override
    public void clear() {
        if (this.core != null) this.core.clear();
    }
}
