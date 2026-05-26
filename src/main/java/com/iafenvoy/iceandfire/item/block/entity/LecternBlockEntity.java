package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.data.BestiaryPage;
import com.iafenvoy.iceandfire.item.BestiaryItem;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.screen.menu.LecternMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LecternBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    private static final int[] slotsTop = new int[]{0};
    private static final int[] slotsSides = new int[]{1};
    private static final int[] slotsBottom = new int[]{0};
    private static final Random RANDOM = new Random();
    private static final ArrayList<BestiaryPage> EMPTY_LIST = new ArrayList<>();
    private final Random localRand = new Random();
    public float pageFlip;
    public float pageFlipPrev;
    public float pageHelp1;
    public float pageHelp2;
    public final BestiaryPage[] selectedPages = new BestiaryPage[3];
    public final ContainerData propertyDelegate = new ContainerData() {
        @Override
        public int get(int index) {
            BestiaryPage page = LecternBlockEntity.this.selectedPages[index];
            return page == null ? -1 : IafRegistries.BESTIARY_PAGE.getId(page);
        }

        @Override
        public void set(int index, int value) {
            LecternBlockEntity.this.selectedPages[index] = IafRegistries.BESTIARY_PAGE.byId(value);
        }

        @Override
        public int getCount() {
            return 3;
        }
    };
    private NonNullList<ItemStack> stacks = NonNullList.withSize(3, ItemStack.EMPTY);

    public LecternBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.IAF_LECTERN.get(), pos, state);
    }

    public static void bookAnimationTick(Level world, BlockPos pos, BlockState state, LecternBlockEntity lectern) {
        float f1 = lectern.pageHelp1;
        do lectern.pageHelp1 += RANDOM.nextInt(4) - RANDOM.nextInt(4); while (f1 == lectern.pageHelp1);
        lectern.pageFlipPrev = lectern.pageFlip;
        float f = (lectern.pageHelp1 - lectern.pageFlip) * 0.04F;
        float f3 = 0.02F;
        f = Mth.clamp(f, -f3, f3);
        lectern.pageHelp2 += (f - lectern.pageHelp2) * 0.9F;
        lectern.pageFlip += lectern.pageHelp2;
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return this.stacks.get(index);
    }

    private List<BestiaryPage> getPossiblePages() {
        final List<BestiaryPage> list = BestiaryPage.possiblePages(this.stacks.getFirst());
        if (!list.isEmpty()) return list;
        return EMPTY_LIST;
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        if (!this.stacks.get(index).isEmpty()) {
            ItemStack itemstack;
            if (this.stacks.get(index).getCount() <= count) {
                itemstack = this.stacks.get(index);
                this.stacks.set(index, ItemStack.EMPTY);
            } else {
                itemstack = this.stacks.get(index).split(count);
                if (this.stacks.get(index).getCount() == 0)
                    this.stacks.set(index, ItemStack.EMPTY);
            }
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        this.stacks.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize())
            stack.setCount(this.getMaxStackSize());

        this.setChanged();

        if (this.stacks.get(0).isEmpty() || this.stacks.get(1).isEmpty()) {
            this.selectedPages[0] = null;
            this.selectedPages[1] = null;
            this.selectedPages[2] = null;
        } else this.randomizePages(this.getItem(0), this.getItem(1));
    }

    public void randomizePages(ItemStack bestiary, ItemStack manuscript) {
        assert this.level != null;
        if (!this.level.isClientSide && bestiary.getItem() == IafItems.BESTIARY.get()) {
            List<BestiaryPage> possibleList = this.getPossiblePages();
            this.localRand.setSeed(this.level.getGameTime());
            Collections.shuffle(possibleList, this.localRand);
            this.selectedPages[0] = !possibleList.isEmpty() ? possibleList.get(0) : null;
            this.selectedPages[1] = possibleList.size() > 1 ? possibleList.get(1) : null;
            this.selectedPages[2] = possibleList.size() > 2 ? possibleList.get(2) : null;
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.stacks, registryLookup);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        ContainerHelper.saveAllItems(nbt, this.stacks, registryLookup);
    }

    @Override
    public void startOpen(@NotNull Player player) {
    }

    @Override
    public void stopOpen(@NotNull Player player) {
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (stack.isEmpty())
            return false;
        if (index == 0)
            return stack.getItem() instanceof BestiaryItem;
        if (index == 1)
            return stack.getItem() == IafItems.MANUSCRIPT.get();
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.stacks.clear();
    }

    @Override
    public @NotNull Component getName() {
        return Component.translatable("block.iceandfire.lectern");
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return false;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return side == Direction.DOWN ? slotsBottom : (side == Direction.UP ? slotsTop : slotsSides);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStackIn, Direction direction) {
        return this.canPlaceItem(index, itemStackIn);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registryLookup) {
        return this.saveWithFullMetadata(registryLookup);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return this.getName();
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.stacks;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> inventory) {
        this.stacks = inventory;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.stacks)
            if (!itemstack.isEmpty())
                return false;
        return true;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new LecternMenu(id, this, playerInventory, this.propertyDelegate);
    }
}