package net.minecraft.world;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ContainerHelper {
    public static final String TAG_ITEMS = "Items";

    public static ItemStack removeItem(List<ItemStack> itemStacks, int slot, int count) {
        return slot >= 0 && slot < itemStacks.size() && !itemStacks.get(slot).isEmpty() && count > 0 ? itemStacks.get(slot).split(count) : ItemStack.EMPTY;
    }

    public static ItemStack takeItem(List<ItemStack> itemStacks, int slot) {
        return slot >= 0 && slot < itemStacks.size() ? itemStacks.set(slot, ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    public static void saveAllItems(ValueOutput output, NonNullList<ItemStack> itemStacks) {
        saveAllItems(output, itemStacks, true);
    }

    public static void saveAllItems(ValueOutput output, NonNullList<ItemStack> itemStacks, boolean alsoWhenEmpty) {
        ValueOutput.TypedOutputList<ItemStackWithSlot> itemsOutput = output.list("Items", ItemStackWithSlot.CODEC);

        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack itemStack = itemStacks.get(i);
            if (!itemStack.isEmpty()) {
                itemsOutput.add(new ItemStackWithSlot(i, itemStack));
            }
        }

        if (itemsOutput.isEmpty() && !alsoWhenEmpty) {
            output.discard("Items");
        }
    }

    public static void loadAllItems(ValueInput input, NonNullList<ItemStack> itemStacks) {
        for (ItemStackWithSlot item : input.listOrEmpty("Items", ItemStackWithSlot.CODEC)) {
            if (item.isValidInContainer(itemStacks.size())) {
                itemStacks.set(item.slot(), item.stack());
            }
        }
    }

    public static int clearOrCountMatchingItems(Container container, Predicate<ItemStack> predicate, int amountToRemove, boolean countingOnly) {
        int count = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            int amountRemoved = clearOrCountMatchingItems(itemStack, predicate, amountToRemove - count, countingOnly);
            if (amountRemoved > 0 && !countingOnly && itemStack.isEmpty()) {
                container.setItem(i, ItemStack.EMPTY);
            }

            count += amountRemoved;
        }

        return count;
    }

    public static int clearOrCountMatchingItems(ItemStack itemStack, Predicate<ItemStack> predicate, int amountToRemove, boolean countingOnly) {
        if (itemStack.isEmpty() || !predicate.test(itemStack)) {
            return 0;
        } else if (countingOnly) {
            return itemStack.getCount();
        } else {
            int amountRemoved = amountToRemove < 0 ? itemStack.getCount() : Math.min(amountToRemove, itemStack.getCount());
            itemStack.shrink(amountRemoved);
            return amountRemoved;
        }
    }
}
