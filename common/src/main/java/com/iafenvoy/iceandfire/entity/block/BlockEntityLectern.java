package com.iafenvoy.iceandfire.entity.block;

import com.iafenvoy.iceandfire.data.BestiaryPages;
import com.iafenvoy.iceandfire.item.ItemBestiary;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.screen.handler.LecternScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockEntityLectern extends LockableContainerBlockEntity implements SidedInventory {
    private static final int[] slotsTop = new int[]{0};
    private static final int[] slotsSides = new int[]{1};
    private static final int[] slotsBottom = new int[]{0};
    private static final Random RANDOM = new Random();
    private static final ArrayList<BestiaryPages> EMPTY_LIST = new ArrayList<>();
    private final Random localRand = new Random();
    public float pageFlip;
    public float pageFlipPrev;
    public float pageHelp1;
    public float pageHelp2;
    public BestiaryPages[] selectedPages = new BestiaryPages[3];
    public final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            BestiaryPages page = BlockEntityLectern.this.selectedPages[index];
            return page == null ? -1 : page.getId();
        }

        @Override
        public void set(int index, int value) {
            BlockEntityLectern.this.selectedPages[index] = BestiaryPages.fromInt(value);
        }

        @Override
        public int size() {
            return 3;
        }
    };
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(3, ItemStack.EMPTY);

    public BlockEntityLectern(BlockPos pos, BlockState state) {
        super(IafBlockEntities.IAF_LECTERN.get(), pos, state);
    }

    public static void bookAnimationTick(World world, BlockPos pos, BlockState state, BlockEntityLectern lectern) {
        float f1 = lectern.pageHelp1;
        do lectern.pageHelp1 += RANDOM.nextInt(4) - RANDOM.nextInt(4); while (f1 == lectern.pageHelp1);
        lectern.pageFlipPrev = lectern.pageFlip;
        float f = (lectern.pageHelp1 - lectern.pageFlip) * 0.04F;
        float f3 = 0.02F;
        f = MathHelper.clamp(f, -f3, f3);
        lectern.pageHelp2 += (f - lectern.pageHelp2) * 0.9F;
        lectern.pageFlip += lectern.pageHelp2;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public ItemStack getStack(int index) {
        return this.stacks.get(index);
    }

    private List<BestiaryPages> getPossiblePages() {
        final List<BestiaryPages> list = BestiaryPages.possiblePages(this.stacks.get(0));
        if (!list.isEmpty()) return list;
        return EMPTY_LIST;
    }

    @Override
    public ItemStack removeStack(int index, int count) {
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
    public void setStack(int index, ItemStack stack) {
        this.stacks.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack())
            stack.setCount(this.getMaxCountPerStack());

        this.markDirty();

        if (this.stacks.get(0).isEmpty() || this.stacks.get(1).isEmpty()) {
            this.selectedPages[0] = null;
            this.selectedPages[1] = null;
            this.selectedPages[2] = null;
        } else this.randomizePages(this.getStack(0), this.getStack(1));
    }

    public void randomizePages(ItemStack bestiary, ItemStack manuscript) {
        assert this.world != null;
        if (!this.world.isClient && bestiary.getItem() == IafItems.BESTIARY.get()) {
            List<BestiaryPages> possibleList = this.getPossiblePages();
            this.localRand.setSeed(this.world.getTime());
            Collections.shuffle(possibleList, this.localRand);
            this.selectedPages[0] = !possibleList.isEmpty() ? possibleList.get(0) : null;
            this.selectedPages[1] = possibleList.size() > 1 ? possibleList.get(1) : null;
            this.selectedPages[2] = possibleList.size() > 2 ? possibleList.get(2) : null;
        }
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
    public boolean isValid(int index, ItemStack stack) {
        if (stack.isEmpty())
            return false;
        if (index == 0)
            return stack.getItem() instanceof ItemBestiary;
        if (index == 1)
            return stack.getItem() == IafItems.MANUSCRIPT.get();
        return false;
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.world != null && player.getWorld() == this.world && this.world.getBlockEntity(this.pos) == this
                && player.squaredDistanceTo(Vec3d.ofCenter(this.pos)) <= 64;
    }

    @Override
    public void clear() {
        this.stacks.clear();
    }

    @Override
    public Text getName() {
        return Text.translatable("block.iceandfire.lectern");
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
    public int[] getAvailableSlots(Direction side) {
        return side == Direction.DOWN ? slotsBottom : (side == Direction.UP ? slotsTop : slotsSides);
    }

    @Override
    public boolean canInsert(int index, ItemStack itemStackIn, Direction direction) {
        return this.isValid(index, itemStackIn);
    }

    @Override
    public ItemStack removeStack(int index) {
        return ItemStack.EMPTY;
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
    protected Text getContainerName() {
        return this.getName();
    }

    @Override
    protected ScreenHandler createScreenHandler(int id, PlayerInventory player) {
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
    public ScreenHandler createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new LecternScreenHandler(id, this, playerInventory, this.propertyDelegate);
    }
}
