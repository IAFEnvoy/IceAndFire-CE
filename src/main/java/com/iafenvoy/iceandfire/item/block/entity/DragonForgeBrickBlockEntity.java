package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DragonForgeBrickBlockEntity extends BlockEntity implements WorldlyContainer {
    @Nullable
    private DragonForgeBlockEntity core = null;

    public DragonForgeBrickBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.DRAGONFORGE_BRICK.get(), pos, state);
    }

    public static void tick(final Level level, final BlockPos position, final BlockState state, final DragonForgeBrickBlockEntity forgeInput) {
        forgeInput.core = forgeInput.getConnectedTileEntity(position);
    }

    private DragonForgeBlockEntity getConnectedTileEntity(final BlockPos position) {
        assert this.level != null;
        for (Direction facing : Direction.values())
            if (this.level.getBlockEntity(position.relative(facing)) instanceof DragonForgeBlockEntity forge)
                return forge;
        return null;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return this.core == null ? new int[0] : this.core.getSlotsForFace(side);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction dir) {
        return this.core != null && this.core.canPlaceItemThroughFace(slot, stack, dir);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction dir) {
        return this.core != null && this.core.canTakeItemThroughFace(slot, stack, dir);
    }

    @Override
    public int getContainerSize() {
        return this.core == null ? 0 : this.core.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return this.core == null || this.core.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.core == null ? ItemStack.EMPTY : this.core.getItem(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return this.core == null ? ItemStack.EMPTY : this.core.removeItem(slot, amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return this.core == null ? ItemStack.EMPTY : this.core.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (this.core != null) this.core.setItem(slot, stack);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.core != null && this.core.stillValid(player);
    }

    @Override
    public void clearContent() {
        if (this.core != null) this.core.clearContent();
    }
}
