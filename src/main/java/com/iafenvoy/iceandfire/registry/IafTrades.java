package com.iafenvoy.iceandfire.registry;

import com.google.common.collect.ImmutableSet;
import com.iafenvoy.iceandfire.IceAndFire;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public final class IafTrades {
    public static final DeferredRegister<PoiType> POI_REGISTRY = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, IceAndFire.MOD_ID);
    public static final DeferredRegister<VillagerProfession> PROFESSION_REGISTRY = DeferredRegister.create(Registries.VILLAGER_PROFESSION, IceAndFire.MOD_ID);

    private static final String SCRIBE = "scribe";
    private static final DeferredHolder<Block, Block> SCRIBE_BLOCK = IafBlocks.LECTERN;
    public static final Function<Block, Set<BlockState>> SCRIBE_WORKSTATION = block -> new HashSet<>(block.getStateDefinition().getPossibleStates());
    public static final DeferredHolder<PoiType, PoiType> SCRIBE_POI = POI_REGISTRY.register(SCRIBE, () -> new PoiType(SCRIBE_WORKSTATION.apply(SCRIBE_BLOCK.get()), 1, 1));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> SCRIBE_PROFESSION = PROFESSION_REGISTRY.register(SCRIBE, () -> new VillagerProfession(
            Component.translatable("entity.iceandfire.villager.scribe"),
            e -> e.is(SCRIBE_POI.getKey()), e -> e.is(SCRIBE_POI.getKey()), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_LIBRARIAN,
            Int2ObjectMap.ofEntries(
                    Int2ObjectMap.entry(1, tradeSet(1)), Int2ObjectMap.entry(2, tradeSet(2)), Int2ObjectMap.entry(3, tradeSet(3)),
                    Int2ObjectMap.entry(4, tradeSet(4)), Int2ObjectMap.entry(5, tradeSet(5))
            )
    ));

    private static ResourceKey<TradeSet> tradeSet(int level) {
        return ResourceKey.create(Registries.TRADE_SET, IceAndFire.id(SCRIBE + "/level_" + level));
    }

    public static void registerPoiStates() {
        for (BlockState state : SCRIBE_WORKSTATION.apply(SCRIBE_BLOCK.get()))
            PoiTypes.TYPE_BY_STATE.put(state, BuiltInRegistries.POINT_OF_INTEREST_TYPE.wrapAsHolder(SCRIBE_POI.get()));
    }
}
