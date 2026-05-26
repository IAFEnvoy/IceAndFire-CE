package com.iafenvoy.iceandfire.util.trade;

import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.List;
import java.util.function.Consumer;

// From object builder api v1

/**
 * Utilities to help with registration of trade offers.
 */
public final class TradeOfferHelper {
    /**
     * Registers trade offer factories for use by villagers.
     *
     * @param profession the villager profession to assign the trades to
     * @param level      the profession level the villager must be to offer the trades
     * @param factories  a list of trade factory you want to add
     */
    public static void registerVillagerOffers(VillagerProfession profession, int level, VillagerTrades.ItemListing... factories) {
        registerVillagerOffers(profession, level, List.of(factories));
    }

    /**
     * Registers trade offer factories for use by villagers.
     *
     * @param profession the villager profession to assign the trades to
     * @param level      the profession level the villager must be to offer the trades
     * @param factories  a list of trade factory you want to add
     */
    public static void registerVillagerOffers(VillagerProfession profession, int level, List<VillagerTrades.ItemListing> factories) {
        registerVillagerOffers(profession, level, factory -> factory.addAll(factories));
    }

    /**
     * Registers trade offer factories for use by villagers.
     *
     * <p>Below is an example, of registering a trade offer factory to be added a blacksmith with a profession level of 3:
     * <blockquote><pre>
     * TradeOfferHelper.registerVillagerOffers(VillagerProfession.BLACKSMITH, 3, factories -> {
     * 	factories.add(new CustomTradeFactory(...);
     * });
     * </pre></blockquote>
     *
     * @param profession the villager profession to assign the trades to
     * @param level      the profession level the villager must be to offer the trades
     * @param factories  a consumer to provide the factories
     */
    public static void registerVillagerOffers(VillagerProfession profession, int level, Consumer<List<VillagerTrades.ItemListing>> factories) {
        TradeOfferInternals.registerVillagerOffers(profession, level, factories);
    }

    /**
     * Registers trade offer factories for use by wandering trades.
     *
     * @param level     the level the trades
     * @param factories a list of trade factory you want to add
     */
    public static void registerWanderingTraderOffers(int level, VillagerTrades.ItemListing... factories) {
        registerWanderingTraderOffers(level, List.of(factories));
    }

    /**
     * Registers trade offer factories for use by wandering trades.
     *
     * @param level     the level the trades
     * @param factories a list of trade factory you want to add
     */
    public static void registerWanderingTraderOffers(int level, List<VillagerTrades.ItemListing> factories) {
        registerWanderingTraderOffers(level, factory -> factory.addAll(factories));
    }

    /**
     * Registers trade offer factories for use by wandering trades.
     *
     * @param level   the level the trades
     * @param factory a consumer to provide the factories
     */
    public static void registerWanderingTraderOffers(int level, Consumer<List<VillagerTrades.ItemListing>> factory) {
        TradeOfferInternals.registerWanderingTraderOffers(level, factory);
    }

    /**
     * @deprecated This never did anything useful.
     */
    @Deprecated(forRemoval = true)
    public static void refreshOffers() {
        TradeOfferInternals.printRefreshOffersWarning();
    }

    private TradeOfferHelper() {
    }
}