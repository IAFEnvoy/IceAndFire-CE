package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.block.DragonForgeBrickBlock;
import com.iafenvoy.iceandfire.item.block.DragonForgeCoreBlock;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import com.iafenvoy.iceandfire.registry.*;
import com.iafenvoy.iceandfire.screen.handler.DragonForgeScreenHandler;
import com.iafenvoy.iceandfire.util.DragonTypeProvider;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class DragonForgeBlockEntity extends LockableContainerBlockEntity implements SidedInventory, ExtendedMenuProvider {
    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_SIDES = new int[]{1};
    private static final int[] SLOTS_BOTTOM = new int[]{2};
    public int lastDragonFlameTimer = 0;
    private DefaultedList<ItemStack> forgeItemStacks = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private boolean prevAssembled;
    private boolean canAddFlameAgain = true;
    private int cookTime = 0;
    //FIXME::Also add total cook time like what vanilla do
    private final PropertyDelegate delegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return DragonForgeBlockEntity.this.cookTime;
        }

        @Override
        public void set(int index, int value) {
            DragonForgeBlockEntity.this.cookTime = value;
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public DragonForgeBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.DRAGONFORGE_CORE.get(), pos, state);
    }

    //FIXME::Optimize logic and remove core replacement
    public static void tick(World level, BlockPos pos, BlockState state, DragonForgeBlockEntity blockEntity) {
        boolean flag = blockEntity.isBurning();
        boolean flag1 = false;
        if (blockEntity.lastDragonFlameTimer > 0) blockEntity.lastDragonFlameTimer--;
        blockEntity.updateGrills(blockEntity.assembled());
        if (!level.isClient) {
            if (blockEntity.prevAssembled != blockEntity.assembled() && state.getBlock() instanceof DragonForgeCoreBlock core)
                DragonForgeCoreBlock.setState(core.getDragonType(), level, pos);
            blockEntity.prevAssembled = blockEntity.assembled();
            if (!blockEntity.assembled()) return;
        }
        if (blockEntity.cookTime > 0 && blockEntity.canSmelt() && blockEntity.lastDragonFlameTimer == 0)
            blockEntity.cookTime--;
        if (blockEntity.getStackInSlot(0).isEmpty() && !level.isClient)
            blockEntity.cookTime = 0;
        assert blockEntity.world != null;
        if (!blockEntity.world.isClient) {
            if (blockEntity.isBurning()) {
                if (blockEntity.canSmelt()) {
                    ++blockEntity.cookTime;
                    if (blockEntity.cookTime >= blockEntity.getMaxCookTime()) {
                        blockEntity.cookTime = 0;
                        blockEntity.smeltItem();
                        flag1 = true;
                    }
                } else if (blockEntity.cookTime > 0) {
                    blockEntity.lastDragonFlameTimer = 40;
                    blockEntity.cookTime = 0;
                }
            } else if (!blockEntity.isBurning() && blockEntity.cookTime > 0)
                blockEntity.cookTime = MathHelper.clamp(blockEntity.cookTime - 2, 0, blockEntity.getMaxCookTime());

            if (flag != blockEntity.isBurning())
                flag1 = true;
        }

        if (flag1) blockEntity.markDirty();
        if (!blockEntity.canAddFlameAgain)
            blockEntity.canAddFlameAgain = true;
    }

    @Override
    public int size() {
        return this.forgeItemStacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.forgeItemStacks)
            if (!itemstack.isEmpty())
                return false;
        return true;
    }

    private void updateGrills(boolean grill) {
        for (Direction facing : Direction.Type.HORIZONTAL) {
            BlockPos pos = this.getPos().offset(facing);
            assert this.world != null;
            BlockState state = this.world.getBlockState(pos);
            if (state.contains(DragonForgeBrickBlock.GRILL) && state.get(DragonForgeBrickBlock.GRILL) != grill)
                this.world.setBlockState(pos, state.with(DragonForgeBrickBlock.GRILL, grill));
        }
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.forgeItemStacks.get(index);
    }

    @Override
    public ItemStack removeStack(int index, int count) {
        return Inventories.splitStack(this.forgeItemStacks, index, count);
    }

    @Override
    public ItemStack removeStack(int index) {
        return Inventories.removeStack(this.forgeItemStacks, index);
    }

    @Override
    public void setStack(int index, ItemStack stack) {
        ItemStack itemstack = this.forgeItemStacks.get(index);
        boolean flag = !stack.isEmpty() && ItemStack.areItemsEqual(stack, itemstack) && ItemStack.areEqual(stack, itemstack);
        this.forgeItemStacks.set(index, stack);

        if (stack.getCount() > this.getMaxCountPerStack())
            stack.setCount(this.getMaxCountPerStack());

        if (index == 0 && !flag || this.cookTime > this.getMaxCookTime()) {
            this.cookTime = 0;
            this.markDirty();
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.forgeItemStacks = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.forgeItemStacks, registryLookup);
        this.cookTime = nbt.getInt("CookTime");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("CookTime", (short) this.cookTime);
        Inventories.writeNbt(nbt, this.forgeItemStacks, registryLookup);
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    public boolean isBurning() {
        return this.cookTime > 0;
    }

    public DragonType getDragonType() {
        if (this.getCachedState().getBlock() instanceof DragonTypeProvider provider) return provider.getDragonType();
        return IafDragonTypes.FIRE;
    }

    public int getFireType(Block block) {
        if (block == IafBlocks.DRAGONFORGE_FIRE_CORE.get() || block == IafBlocks.DRAGONFORGE_FIRE_CORE_DISABLED.get())
            return 0;
        if (block == IafBlocks.DRAGONFORGE_ICE_CORE.get() || block == IafBlocks.DRAGONFORGE_ICE_CORE_DISABLED.get())
            return 1;
        if (block == IafBlocks.DRAGONFORGE_LIGHTNING_CORE.get() || block == IafBlocks.DRAGONFORGE_LIGHTNING_CORE_DISABLED.get())
            return 2;
        return 0;
    }

    public int getMaxCookTime() {
        return this.getCurrentRecipe().map(DragonForgeRecipe::getCookTime).orElse(100);
    }

    private Block getDefaultOutput() {
        return this.getDragonType() == IafDragonTypes.ICE ? IafBlocks.DRAGON_ICE.get() : IafBlocks.ASH.get();
    }

    private ItemStack getCurrentResult() {
        Optional<DragonForgeRecipe> recipe = this.getCurrentRecipe();
        return recipe.map(DragonForgeRecipe::getResultItem).orElseGet(() -> new ItemStack(this.getDefaultOutput()));
    }

    public Optional<DragonForgeRecipe> getCurrentRecipe() {
        assert this.world != null;
        return this.world.getRecipeManager().getFirstMatch(IafRecipes.DRAGON_FORGE_TYPE.get(), new DragonForgeRecipeInput(this), this.world).map(RecipeEntry::value);
    }

    public List<DragonForgeRecipe> getRecipes() {
        assert this.world != null;
        return this.world.getRecipeManager().listAllOfType(IafRecipes.DRAGON_FORGE_TYPE.get()).stream().map(RecipeEntry::value).toList();
    }

    public boolean canSmelt() {
        ItemStack cookStack = this.forgeItemStacks.getFirst();
        if (cookStack.isEmpty()) return false;

        ItemStack forgeRecipeOutput = this.getCurrentResult();

        if (forgeRecipeOutput.isEmpty()) return false;

        ItemStack outputStack = this.forgeItemStacks.get(2);
        if (!outputStack.isEmpty() && !ItemStack.areItemsEqual(outputStack, forgeRecipeOutput))
            return false;

        int calculatedOutputCount = outputStack.getCount() + forgeRecipeOutput.getCount();
        return (calculatedOutputCount <= this.getMaxCountPerStack() && calculatedOutputCount <= outputStack.getMaxCount());
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (player.getWorld().getBlockEntity(this.pos) != this) return false;
        else
            return player.squaredDistanceTo(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public void smeltItem() {
        if (!this.canSmelt()) return;

        ItemStack cookStack = this.forgeItemStacks.get(0);
        ItemStack bloodStack = this.forgeItemStacks.get(1);
        ItemStack outputStack = this.forgeItemStacks.get(2);

        ItemStack output = this.getCurrentResult();

        if (outputStack.isEmpty()) this.forgeItemStacks.set(2, output.copy());
        else outputStack.increment(output.getCount());

        cookStack.decrement(1);
        bloodStack.decrement(1);
    }

    @Override
    public boolean isValid(int index, ItemStack stack) {
        return switch (index) {
            case 1 -> this.getRecipes().stream().anyMatch(item -> item.isValidBlood(stack));
            case 0 -> true;//getRecipes().stream().anyMatch(item -> item.isValidInput(stack))
            default -> false;
        };
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) return SLOTS_BOTTOM;
        else
            return side == Direction.UP ? SLOTS_TOP : SLOTS_SIDES;
    }

    @Override
    public boolean canInsert(int index, ItemStack itemStackIn, Direction direction) {
        return this.isValid(index, itemStackIn);
    }

    @Override
    public boolean canExtract(int index, ItemStack stack, Direction direction) {
        if (direction == Direction.DOWN && index == 1) {
            Item item = stack.getItem();
            return item == Items.WATER_BUCKET || item == Items.BUCKET;
        }
        return true;
    }

    @Override
    public void clear() {
        this.forgeItemStacks.clear();
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.dragonforge_fire" + this.getDragonType().name());
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.forgeItemStacks;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.forgeItemStacks = inventory;
    }

    public void transferPower(int i) {
        assert this.world != null;
        if (!this.world.isClient) {
            if (this.canSmelt()) {
                if (this.canAddFlameAgain) {
                    this.cookTime = Math.min(this.getMaxCookTime() + 1, this.cookTime + i);
                    this.canAddFlameAgain = false;
                }
            } else this.cookTime = 0;
        }
        this.lastDragonFlameTimer = 40;
    }

    private boolean checkBoneCorners(BlockPos pos) {
        return this.doesBlockEqual(pos.north().east(), IafBlocks.DRAGON_BONE_BLOCK.get())
                && this.doesBlockEqual(pos.north().west(), IafBlocks.DRAGON_BONE_BLOCK.get())
                && this.doesBlockEqual(pos.south().east(), IafBlocks.DRAGON_BONE_BLOCK.get())
                && this.doesBlockEqual(pos.south().west(), IafBlocks.DRAGON_BONE_BLOCK.get());
    }

    private boolean checkBrickCorners(BlockPos pos) {
        return this.doesBlockEqual(pos.north().east(), this.getBrick()) && this.doesBlockEqual(pos.north().west(), this.getBrick())
                && this.doesBlockEqual(pos.south().east(), this.getBrick()) && this.doesBlockEqual(pos.south().west(), this.getBrick());
    }

    private boolean checkBrickSlots(BlockPos pos) {
        return this.doesBlockEqual(pos.north(), this.getBrick()) && this.doesBlockEqual(pos.east(), this.getBrick())
                && this.doesBlockEqual(pos.west(), this.getBrick()) && this.doesBlockEqual(pos.south(), this.getBrick());
    }

    private boolean checkY(BlockPos pos) {
        return this.doesBlockEqual(pos.up(), this.getBrick()) && this.doesBlockEqual(pos.down(), this.getBrick());
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbtWithIdentifyingData(registryLookup);
    }

    public boolean assembled() {
        return this.checkBoneCorners(this.pos.down()) && this.checkBrickSlots(this.pos.down()) && this.checkBrickCorners(this.pos)
                && this.atleastThreeAreBricks(this.pos) && this.checkY(this.pos) && this.checkBoneCorners(this.pos.up()) && this.checkBrickSlots(this.pos.up());
    }

    private Block getBrick() {
        return DragonForgeBrickBlock.getBlockByType(this.getDragonType());
    }

    private boolean doesBlockEqual(BlockPos pos, Block block) {
        assert this.world != null;
        return this.world.getBlockState(pos).getBlock() == block;
    }

    private boolean atleastThreeAreBricks(BlockPos pos) {
        int count = 0;
        for (Direction facing : Direction.Type.HORIZONTAL) {
            assert this.world != null;
            if (this.world.getBlockState(pos.offset(facing)).getBlock() == this.getBrick())
                count++;
        }
        return count > 2;
    }

    @Override
    protected ScreenHandler createScreenHandler(int id, PlayerInventory player) {
        return new DragonForgeScreenHandler(id, this, player, this.getDragonType(), this.delegate);
    }

    @Override
    public void saveExtraData(PacketByteBuf buf) {
        Optional<RegistryKey<DragonType>> key = IafRegistries.DRAGON_TYPE.getKey(this.getDragonType());
        key.ifPresent(buf::writeRegistryKey);
    }

    public static class DragonForgeRecipeInput implements RecipeInput {
        private final DragonForgeBlockEntity owner;

        public DragonForgeRecipeInput(DragonForgeBlockEntity owner) {
            this.owner = owner;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return this.owner.forgeItemStacks.get(slot);
        }

        @Override
        public int getSize() {
            return this.owner.forgeItemStacks.size();
        }

        public ItemStack getStack(int index) {
            return this.owner.forgeItemStacks.get(index);
        }

        public String getTypeID() {
            return switch (this.owner.getFireType(this.owner.getCachedState().getBlock())) {
                case 0 -> "fire";
                case 1 -> "ice";
                case 2 -> "lightning";
                default -> "";
            };
        }
    }
}
