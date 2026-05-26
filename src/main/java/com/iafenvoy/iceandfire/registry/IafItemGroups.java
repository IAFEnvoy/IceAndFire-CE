package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public final class IafItemGroups {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IceAndFire.MOD_ID);

    public static final List<Holder<Item>> BLOCKS_LIST = new LinkedList<>(), ITEMS_LIST = new LinkedList<>(), TOOLS_WEAPONS_LIST = new LinkedList<>(), ARMORS_LIST = new LinkedList<>();

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCKS = register("blocks", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + IceAndFire.MOD_ID + ".blocks")).icon(() -> new ItemStack(IafBlocks.DRAGON_SCALE_RED.get())).displayItems(BLOCKS_LIST).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEMS = register("items", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + IceAndFire.MOD_ID + ".items")).icon(() -> new ItemStack(IafItems.DRAGON_SKULL_FIRE.get())).displayItems(ITEMS_LIST).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TOOLS_WEAPONS = register("tools_weapons", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + IceAndFire.MOD_ID + ".tools_weapons")).icon(() -> new ItemStack(IafItems.DRAGONSTEEL_LIGHTNING_SWORD.get())).displayItems(TOOLS_WEAPONS_LIST).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARMORS = register("armors", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + IceAndFire.MOD_ID + ".armors")).icon(() -> new ItemStack(IafItems.DRAGONSTEEL_FIRE_HELMET.get())).displayItems(ARMORS_LIST).build());

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> register(String name, Supplier<CreativeModeTab> group) {
        return REGISTRY.register(name, group);
    }
}
