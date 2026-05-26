package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.loot.DragonLootFunction;
import com.iafenvoy.iceandfire.loot.SeaSerpentLootFunction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class IafLoots {
    public static final DeferredRegister<LootItemFunctionType<?>> REGISTRY = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, IceAndFire.MOD_ID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<DragonLootFunction>> DRAGON_LOOT = register("dragon_loot", () -> DragonLootFunction.CODEC);
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SeaSerpentLootFunction>> SEA_SERPENT_LOOT = register("sea_serpent_loot", () -> SeaSerpentLootFunction.CODEC);

    private static <T extends LootItemFunction> DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<T>> register(String id, Supplier<MapCodec<T>> obj) {
        return REGISTRY.register(id, () -> new LootItemFunctionType<>(obj.get()));
    }
}
