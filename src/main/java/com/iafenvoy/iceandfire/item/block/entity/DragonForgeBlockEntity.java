package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.block.DragonForgeBrickBlock;
import com.iafenvoy.iceandfire.item.block.DragonForgeCoreBlock;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import com.iafenvoy.iceandfire.registry.*;
import com.iafenvoy.iceandfire.screen.handler.DragonForgeScreenHandler;
import com.iafenvoy.iceandfire.util.DragonTypeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class DragonForgeBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, MenuProvider {
    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_SIDES = new int[]{1};
    private static final int[] SLOTS_BOTTOM = new int[]{2};
    public int lastDragonFlameTimer = 0;
    private NonNullList<ItemStack> forgeItemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
    private boolean prevAssembled;
    private double cookTime = 0;
    //FIXME::Also add total cook time like what vanilla do
    private final ContainerData delegate = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) DragonForgeBlockEntity.this.cookTime;
                case 1 -> (int) DragonForgeBlockEntity.this.getMaxCookTime();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) DragonForgeBlockEntity.this.cookTime = value;
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public DragonForgeBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.DRAGONFORGE_CORE.get(), pos, state);
    }

    //FIXME::Optimize logic and remove core replacement
    public static void tick(Level level, BlockPos pos, BlockState state, DragonForgeBlockEntity blockEntity) {
        boolean flag = blockEntity.isBurning();
        boolean flag1 = false;
        if (blockEntity.lastDragonFlameTimer > 0) blockEntity.lastDragonFlameTimer--;
        blockEntity.updateGrills(blockEntity.assembled());
        if (!level.isClientSide) {
            if (blockEntity.prevAssembled != blockEntity.assembled() && state.getBlock() instanceof DragonForgeCoreBlock core)
                DragonForgeCoreBlock.setState(core.getDragonType(), level, pos);
            blockEntity.prevAssembled = blockEntity.assembled();
            if (!blockEntity.assembled()) return;
        }
        if (blockEntity.cookTime > 0 && blockEntity.canSmelt() && blockEntity.lastDragonFlameTimer == 0)
            blockEntity.cookTime--;
        if (blockEntity.getItem(0).isEmpty() && !level.isClientSide)
            blockEntity.cookTime = 0;
        assert blockEntity.level != null;
        if (!blockEntity.level.isClientSide) {
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
                blockEntity.cookTime = Mth.clamp(blockEntity.cookTime - 2, 0, blockEntity.getMaxCookTime());

            if (flag != blockEntity.isBurning())
                flag1 = true;
        }

        if (flag1) blockEntity.setChanged();
    }

    @Override
    public int getContainerSize() {
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
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            BlockPos pos = this.getBlockPos().relative(facing);
            assert this.level != null;
            BlockState state = this.level.getBlockState(pos);
            if (state.hasProperty(DragonForgeBrickBlock.GRILL) && state.getValue(DragonForgeBrickBlock.GRILL) != grill)
                this.level.setBlockAndUpdate(pos, state.setValue(DragonForgeBrickBlock.GRILL, grill));
        }
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return this.forgeItemStacks.get(index);
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(this.forgeItemStacks, index, count);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.forgeItemStacks, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        ItemStack itemstack = this.forgeItemStacks.get(index);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItem(stack, itemstack) && ItemStack.matches(stack, itemstack);
        this.forgeItemStacks.set(index, stack);

        if (stack.getCount() > this.getMaxStackSize())
            stack.setCount(this.getMaxStackSize());

        if (index == 0 && !flag || this.cookTime > this.getMaxCookTime()) {
            this.cookTime = 0;
            this.setChanged();
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.forgeItemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.forgeItemStacks, registryLookup);
        this.cookTime = nbt.getInt("CookTime");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putInt("CookTime", (short) this.cookTime);
        ContainerHelper.saveAllItems(nbt, this.forgeItemStacks, registryLookup);
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    public boolean isBurning() {
        return this.cookTime > 0;
    }

    public DragonType getDragonType() {
        if (this.getBlockState().getBlock() instanceof DragonTypeProvider provider) return provider.getDragonType();
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

    public double getMaxCookTime() {
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
        assert this.level != null;
        return this.level.getRecipeManager().getRecipeFor(IafRecipes.DRAGON_FORGE_TYPE.get(), new DragonForgeRecipeInput(this), this.level).map(RecipeHolder::value);
    }

    public List<DragonForgeRecipe> getRecipes() {
        assert this.level != null;
        return this.level.getRecipeManager().getAllRecipesFor(IafRecipes.DRAGON_FORGE_TYPE.get()).stream().map(RecipeHolder::value).toList();
    }

    public boolean canSmelt() {
        ItemStack cookStack = this.forgeItemStacks.getFirst();
        if (cookStack.isEmpty()) return false;

        ItemStack forgeRecipeOutput = this.getCurrentResult();

        if (forgeRecipeOutput.isEmpty()) return false;

        ItemStack outputStack = this.forgeItemStacks.get(2);
        if (!outputStack.isEmpty() && !ItemStack.isSameItem(outputStack, forgeRecipeOutput))
            return false;

        int calculatedOutputCount = outputStack.getCount() + forgeRecipeOutput.getCount();
        return (calculatedOutputCount <= this.getMaxStackSize() && calculatedOutputCount <= outputStack.getMaxStackSize());
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.level().getBlockEntity(this.worldPosition) != this) return false;
        else
            return player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    public void smeltItem() {
        if (!this.canSmelt()) return;

        ItemStack cookStack = this.forgeItemStacks.get(0);
        ItemStack bloodStack = this.forgeItemStacks.get(1);
        ItemStack outputStack = this.forgeItemStacks.get(2);

        ItemStack output = this.getCurrentResult();

        if (outputStack.isEmpty()) this.forgeItemStacks.set(2, output.copy());
        else outputStack.grow(output.getCount());

        cookStack.shrink(1);
        bloodStack.shrink(1);
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        return switch (index) {
            case 1 -> this.getRecipes().stream().anyMatch(item -> item.isValidBlood(stack));
            case 0 -> true;//getRecipes().stream().anyMatch(item -> item.isValidInput(stack))
            default -> false;
        };
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.DOWN) return SLOTS_BOTTOM;
        else
            return side == Direction.UP ? SLOTS_TOP : SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStackIn, Direction direction) {
        return this.canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        if (direction == Direction.DOWN && index == 1) {
            Item item = stack.getItem();
            return item == Items.WATER_BUCKET || item == Items.BUCKET;
        }
        return true;
    }

    @Override
    public void clearContent() {
        this.forgeItemStacks.clear();
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.dragonforge_fire" + this.getDragonType().name());
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.forgeItemStacks;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> inventory) {
        this.forgeItemStacks = inventory;
    }

    public void transferPower(double i) {
        assert this.level != null;
        if (!this.level.isClientSide) {
            if (this.canSmelt()) this.cookTime = Math.min(this.getMaxCookTime() + 1, this.cookTime + i);
            else this.cookTime = 0;
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
        return this.doesBlockEqual(pos.above(), this.getBrick()) && this.doesBlockEqual(pos.below(), this.getBrick());
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registryLookup) {
        return this.saveWithFullMetadata(registryLookup);
    }

    public boolean assembled() {
        return this.checkBoneCorners(this.worldPosition.below()) && this.checkBrickSlots(this.worldPosition.below()) && this.checkBrickCorners(this.worldPosition)
                && this.atleastThreeAreBricks(this.worldPosition) && this.checkY(this.worldPosition) && this.checkBoneCorners(this.worldPosition.above()) && this.checkBrickSlots(this.worldPosition.above());
    }

    private Block getBrick() {
        return DragonForgeBrickBlock.getBlockByType(this.getDragonType());
    }

    private boolean doesBlockEqual(BlockPos pos, Block block) {
        assert this.level != null;
        return this.level.getBlockState(pos).getBlock() == block;
    }

    private boolean atleastThreeAreBricks(BlockPos pos) {
        int count = 0;
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            assert this.level != null;
            if (this.level.getBlockState(pos.relative(facing)).getBlock() == this.getBrick())
                count++;
        }
        return count > 2;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new DragonForgeScreenHandler(id, this, player, this.getDragonType(), this.delegate);
    }

    @Override
    public void writeClientSideData(@NotNull AbstractContainerMenu menu, RegistryFriendlyByteBuf buf) {
        Optional<ResourceKey<DragonType>> key = IafRegistries.DRAGON_TYPE.getResourceKey(this.getDragonType());
        key.ifPresent(buf::writeResourceKey);
    }

    public static class DragonForgeRecipeInput implements RecipeInput {
        private final DragonForgeBlockEntity owner;

        public DragonForgeRecipeInput(DragonForgeBlockEntity owner) {
            this.owner = owner;
        }

        @Override
        public @NotNull ItemStack getItem(int slot) {
            return this.owner.forgeItemStacks.get(slot);
        }

        @Override
        public int size() {
            return this.owner.forgeItemStacks.size();
        }

        public ItemStack getStack(int index) {
            return this.owner.forgeItemStacks.get(index);
        }

        public String getTypeID() {
            return switch (this.owner.getFireType(this.owner.getBlockState().getBlock())) {
                case 0 -> "fire";
                case 1 -> "ice";
                case 2 -> "lightning";
                default -> "";
            };
        }
    }
}
