package com.iafenvoy.iceandfire.util.trade;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

// From object builder api v1
public final class TradeOfferInternals {
    private static final Logger LOGGER = LoggerFactory.getLogger("fabric-villager-api-v1");

    private TradeOfferInternals() {
    }

    // synchronized guards against concurrent modifications - Vanilla does not mutate the underlying arrays (as of 1.16),
    // so reads will be fine without locking.
    public static synchronized void registerVillagerOffers(VillagerProfession profession, int level, Consumer<List<VillagerTrades.ItemListing>> factory) {
        Objects.requireNonNull(profession, "VillagerProfession may not be null.");
        registerOffers(VillagerTrades.TRADES.computeIfAbsent(profession, key -> new Int2ObjectOpenHashMap<>()), level, factory);
    }

    public static synchronized void registerWanderingTraderOffers(int level, Consumer<List<VillagerTrades.ItemListing>> factory) {
        registerOffers(VillagerTrades.WANDERING_TRADER_TRADES, level, factory);
    }

    // Shared code to register offers for both villagers and wandering traders.
    private static void registerOffers(Int2ObjectMap<VillagerTrades.ItemListing[]> leveledTradeMap, int level, Consumer<List<VillagerTrades.ItemListing>> factory) {
        final List<VillagerTrades.ItemListing> list = new ArrayList<>();
        factory.accept(list);

        final VillagerTrades.ItemListing[] originalEntries = leveledTradeMap.computeIfAbsent(level, key -> new VillagerTrades.ItemListing[0]);
        final VillagerTrades.ItemListing[] addedEntries = list.toArray(new VillagerTrades.ItemListing[0]);

        final VillagerTrades.ItemListing[] allEntries = ArrayUtils.addAll(originalEntries, addedEntries);
        leveledTradeMap.put(level, allEntries);
    }

    public static void printRefreshOffersWarning() {
        Throwable loggingThrowable = new Throwable();
        LOGGER.warn("TradeOfferHelper#refreshOffers does not do anything, yet it was called! Stack trace:", loggingThrowable);
    }
}