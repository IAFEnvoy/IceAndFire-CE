package com.iafenvoy.iceandfire.entity.util;

import com.google.common.collect.ImmutableMap;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafItems;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;

import java.util.Optional;

public class MyrmexTrades {
    public static final Int2ObjectMap<TradeOffers.Factory[]> DESERT_WORKER;
    public static final Int2ObjectMap<TradeOffers.Factory[]> JUNGLE_WORKER;
    public static final Int2ObjectMap<TradeOffers.Factory[]> DESERT_SOLDIER;
    public static final Int2ObjectMap<TradeOffers.Factory[]> JUNGLE_SOLDIER;
    public static final Int2ObjectMap<TradeOffers.Factory[]> DESERT_SENTINEL;
    public static final Int2ObjectMap<TradeOffers.Factory[]> JUNGLE_SENTINEL;
    public static final Int2ObjectMap<TradeOffers.Factory[]> DESERT_ROYAL;
    public static final Int2ObjectMap<TradeOffers.Factory[]> JUNGLE_ROYAL;
    public static final Int2ObjectMap<TradeOffers.Factory[]> DESERT_QUEEN;
    public static final Int2ObjectMap<TradeOffers.Factory[]> JUNGLE_QUEEN;

    static {
        DESERT_WORKER = createTrades(ImmutableMap.of(1,
                new TradeOffers.Factory[]{
                        new DesertResinForItemsTrade(Items.DIRT, 64, 1, 5),
                        new DesertResinForItemsTrade(Items.SAND, 64, 1, 5),
                        new ItemsForDesertResinTrade(Items.DEAD_BUSH, 2, 8, 5, 2),
                        new DesertResinForItemsTrade(Items.BONE, 10, 1, 1),
                },
                //Only 3 of these appears per myrmex
                2, new TradeOffers.Factory[]{
                        new ItemsForDesertResinTrade(Items.IRON_ORE, 1, 6, 3, 2),
                        new DesertResinForItemsTrade(Items.SUGAR, 15, 2, 1),
                        new ItemsForDesertResinTrade(Items.STICK, 1, 64, 5, 2),
                        new ItemsForDesertResinTrade(IafItems.COPPER_NUGGET.get(), 1, 4, 10),
                }));
        JUNGLE_WORKER = createTrades(ImmutableMap.of(1,
                new TradeOffers.Factory[]{
                        new JungleResinForItemsTrade(Items.DIRT, 64, 1, 5),
                        new ItemsForJungleResinTrade(Items.MELON_SLICE, 1, 20, 3, 1),
                        new ItemsForJungleResinTrade(Items.JUNGLE_LEAVES, 1, 64, 5, 1),
                        new JungleResinForItemsTrade(Items.BONE, 10, 1, 5),
                },
                //Only 3 of these appears per myrmex
                2, new TradeOffers.Factory[]{
                        new ItemsForJungleResinTrade(Items.GOLD_ORE, 2, 15, 3, 2),
                        new JungleResinForItemsTrade(Items.SUGAR, 15, 2, 3),
                        new ItemsForJungleResinTrade(Items.STICK, 1, 64, 5, 2),
                        new ItemsForJungleResinTrade(IafItems.COPPER_NUGGET.get(), 1, 4, 10),
                }));
        DESERT_SOLDIER = createTrades(ImmutableMap.of(1,
                new TradeOffers.Factory[]{
                        new DesertResinForItemsTrade(Items.BONE, 7, 1, 3),
                        new DesertResinForItemsTrade(Items.FEATHER, 16, 3, 3),
                        new DesertResinForItemsTrade(Items.GUNPOWDER, 5, 1, 4),
                        new ItemsForDesertResinTrade(Items.RABBIT, 1, 3, 6, 2),
                        new DesertResinForItemsTrade(Items.IRON_NUGGET, 4, 1, 4),
                        new ItemsForDesertResinTrade(Items.CHICKEN, 2, 2, 7),
                        new ItemsForDesertResinTrade(IafItems.SILVER_NUGGET.get(), 4, 1, 15),
                },
                //Only 3 of these appears per myrmex
                2, new TradeOffers.Factory[]{
                        new ItemsForDesertResinTrade(Items.CACTUS, 1, 15, 6, 2),
                        new ItemsForDesertResinTrade(Items.GOLD_NUGGET, 1, 4, 6, 2),
                        new ItemsForDesertResinTrade(IafItems.TROLL_TUSK.get(), 6, 1, 4, 2),
                        new DesertResinForItemsTrade(IafItems.DRAGON_BONE.get(), 6, 2, 3),
                }));
        JUNGLE_SOLDIER = createTrades(ImmutableMap.of(1,
                new TradeOffers.Factory[]{
                        new JungleResinForItemsTrade(Items.BONE, 7, 1, 3),
                        new JungleResinForItemsTrade(Items.FEATHER, 16, 3, 3),
                        new JungleResinForItemsTrade(Items.GUNPOWDER, 5, 1, 4),
                        new ItemsForJungleResinTrade(Items.EGG, 1, 4, 6, 2),
                        new JungleResinForItemsTrade(Items.IRON_NUGGET, 4, 1, 4),
                        new ItemsForJungleResinTrade(Items.CHICKEN, 2, 2, 7),
                        new ItemsForJungleResinTrade(IafItems.SILVER_NUGGET.get(), 1, 4, 15),
                },
                //Only 3 of these appears per myrmex
                2, new TradeOffers.Factory[]{
                        new ItemsForJungleResinTrade(Items.ROTTEN_FLESH, 1, 15, 6, 2),
                        new ItemsForJungleResinTrade(Items.GOLD_NUGGET, 1, 4, 6, 2),
                        new ItemsForJungleResinTrade(IafItems.TROLL_TUSK.get(), 6, 1, 4, 2),
                        new JungleResinForItemsTrade(IafItems.DRAGON_BONE.get(), 6, 2, 3),
                }));
        DESERT_SENTINEL = createTrades(ImmutableMap.of(1,
                new TradeOffers.Factory[]{
                        new DesertResinForItemsTrade(Items.SPIDER_EYE, 10, 2, 3),
                        new DesertResinForItemsTrade(Items.POISONOUS_POTATO, 2, 1, 2),
                        new DesertResinForItemsTrade(Items.PUFFERFISH, 4, 2, 4),
                },
                //Only 3 of these appears per myrmex
                2, new TradeOffers.Factory[]{
                        new ItemsForDesertResinTrade(Items.REDSTONE, 2, 5, 5, 1),
                        new ItemsForDesertResinTrade(Items.PORKCHOP, 2, 3, 4),
                        new ItemsForDesertResinTrade(Items.BEEF, 2, 3, 4),
                        new ItemsForDesertResinTrade(Items.MUTTON, 2, 3, 4),
                        new ItemsForDesertResinTrade(Items.SKELETON_SKULL, 15, 1, 2, 1),
                }));
        JUNGLE_SENTINEL = createTrades(ImmutableMap.of(1,
                new TradeOffers.Factory[]{
                        new JungleResinForItemsTrade(Items.SPIDER_EYE, 10, 2, 3),
                        new JungleResinForItemsTrade(Items.POISONOUS_POTATO, 2, 1, 2),
                        new JungleResinForItemsTrade(Items.PUFFERFISH, 4, 2, 4),
                },
                //Only 3 of these appears per myrmex
                2, new TradeOffers.Factory[]{
                        new ItemsForJungleResinTrade(Items.REDSTONE, 2, 5, 5, 1),
                        new ItemsForJungleResinTrade(Items.PORKCHOP, 2, 3, 4),
                        new ItemsForJungleResinTrade(Items.BEEF, 2, 3, 4),
                        new ItemsForJungleResinTrade(Items.MUTTON, 2, 3, 4),
                        new ItemsForJungleResinTrade(Items.SKELETON_SKULL, 15, 1, 2, 1),
                }));
        DESERT_ROYAL = createTrades(ImmutableMap.of(1,
                new TradeOffers.Factory[]{
                        new ItemsForDesertResinTrade(IafItems.MANUSCRIPT.get(), 1, 3, 5, 1),
                        new ItemsForDesertResinTrade(IafItems.WITHER_SHARD.get(), 3, 1, 3, 1),
                        new ItemsForDesertResinTrade(Items.EMERALD, 10, 1, 3, 1),
                        new ItemsForDesertResinTrade(Items.QUARTZ, 2, 4, 3, 1),
                },
                //Only 3 of these appears per myrmex
                2, new TradeOffers.Factory[]{
                        new ItemsForDesertResinTrade(Items.GOLDEN_CARROT, 3, 1, 2, 1),
                        new ItemsForDesertResinTrade(Items.MAGMA_CREAM, 5, 1, 3, 1),
                        new ItemsForDesertResinTrade(Items.GOLD_INGOT, 3, 1, 5, 1),
                        new ItemsForDesertResinTrade(IafItems.SILVER_INGOT.get(), 3, 1, 5, 1),
                        new ItemsForDesertResinTrade(Items.COPPER_INGOT, 2, 2, 3, 1),
                        new ItemsForDesertResinTrade(Items.ENDER_PEARL, 8, 1, 5, 1),
                        new ItemsForDesertResinTrade(Items.RABBIT_FOOT, 3, 1, 5, 1),
                }));
        JUNGLE_ROYAL = createTrades(ImmutableMap.of(1,
                new TradeOffers.Factory[]{
                        new ItemsForJungleResinTrade(IafItems.MANUSCRIPT.get(), 1, 3, 5, 1),
                        new ItemsForJungleResinTrade(IafItems.WITHER_SHARD.get(), 3, 1, 3, 1),
                        new ItemsForJungleResinTrade(Items.EMERALD, 10, 1, 3, 1),
                        new ItemsForJungleResinTrade(Items.QUARTZ, 2, 4, 3, 1),
                },
                //Only 3 of these appears per myrmex
                2, new TradeOffers.Factory[]{
                        new ItemsForJungleResinTrade(Items.GOLDEN_CARROT, 3, 1, 2, 1),
                        new ItemsForJungleResinTrade(Items.MAGMA_CREAM, 5, 1, 3, 1),
                        new ItemsForJungleResinTrade(Items.GOLD_INGOT, 3, 1, 5, 1),
                        new ItemsForJungleResinTrade(IafItems.SILVER_INGOT.get(), 3, 1, 5, 1),
                        new ItemsForJungleResinTrade(Items.COPPER_INGOT, 2, 2, 3, 1),
                        new ItemsForJungleResinTrade(Items.ENDER_PEARL, 8, 1, 5, 1),
                        new ItemsForJungleResinTrade(Items.RABBIT_FOOT, 3, 1, 5, 1),
                }));

        DESERT_QUEEN = createTrades(ImmutableMap.of(1,
                new TradeOffers.Factory[]{
                        new ItemsForDesertResinTrade(createEgg(false, 0), 10, 1, 10, 1),
                        new ItemsForDesertResinTrade(createEgg(false, 1), 20, 1, 8, 1),
                        new ItemsForDesertResinTrade(createEgg(false, 2), 30, 1, 5, 1),
                        new ItemsForDesertResinTrade(createEgg(false, 3), 40, 1, 3, 1),
                },
                //Only 3 of these appears per myrmex
                2, new TradeOffers.Factory[]{
                        new ItemsForDesertResinTrade(createEgg(false, 4), 60, 1, 2, 1),
                        new ItemsForDesertResinTrade(Items.EMERALD, 15, 1, 9, 1),
                        new ItemsForDesertResinTrade(Items.DIAMOND, 25, 1, 9, 1),
                }));
        JUNGLE_QUEEN = createTrades(ImmutableMap.of(1,
                new TradeOffers.Factory[]{
                        new ItemsForJungleResinTrade(createEgg(true, 0), 10, 1, 10, 1),
                        new ItemsForJungleResinTrade(createEgg(true, 1), 20, 1, 8, 1),
                        new ItemsForJungleResinTrade(createEgg(true, 2), 30, 1, 5, 1),
                        new ItemsForJungleResinTrade(createEgg(true, 3), 40, 1, 3, 1),
                },
                //Only 3 of these appears per myrmex
                2, new TradeOffers.Factory[]{
                        new ItemsForJungleResinTrade(createEgg(true, 4), 60, 1, 2, 1),
                        new ItemsForDesertResinTrade(Items.EMERALD, 15, 1, 9, 1),
                        new ItemsForDesertResinTrade(Items.DIAMOND, 25, 1, 9, 1),
                }));
    }

    private static ItemStack createEgg(boolean jungle, int caste) {
        ItemStack egg = new ItemStack(jungle ? IafItems.MYRMEX_JUNGLE_EGG.get() : IafItems.MYRMEX_DESERT_EGG.get());
        egg.set(IafDataComponents.INT.get(), caste);
        return egg;
    }

    private static Int2ObjectMap<TradeOffers.Factory[]> createTrades(ImmutableMap<Integer, TradeOffers.Factory[]> p_221238_0_) {
        return new Int2ObjectOpenHashMap<>(p_221238_0_);
    }

    static class ItemsForDesertResinTrade implements TradeOffers.Factory {
        private final ItemStack stack;
        private final int emeraldCount;
        private final int itemCount;
        private final int maxUses;
        private final int exp;
        private final float multiplier;

        public ItemsForDesertResinTrade(Block sellingItem, int emeraldCount, int sellingItemCount, int maxUses, int xpValue) {
            this(new ItemStack(sellingItem), emeraldCount, sellingItemCount, maxUses, xpValue);
        }

        public ItemsForDesertResinTrade(Item sellingItem, int emeraldCount, int sellingItemCount, int xpValue) {
            this(new ItemStack(sellingItem), emeraldCount, sellingItemCount, 12, xpValue);
        }

        public ItemsForDesertResinTrade(Item item, int DesertResin, int items, int maxUses, int exp) {
            this(new ItemStack(item), DesertResin, items, maxUses, exp);
        }

        public ItemsForDesertResinTrade(ItemStack stack, int DesertResin, int items, int maxUses, int exp) {
            this(stack, DesertResin, items, maxUses, exp, 0.05F);
        }

        public ItemsForDesertResinTrade(ItemStack stack, int DesertResin, int items, int maxUses, int exp, float multi) {
            this.stack = stack;
            this.emeraldCount = DesertResin;
            this.itemCount = items;
            this.maxUses = maxUses;
            this.exp = exp;
            this.multiplier = multi;
        }

        @Override
        public TradeOffer create(Entity trader, Random rand) {
            ItemStack cloneStack = this.stack.copyComponentsToNewStack(this.stack.getItem(), this.itemCount);
            return new TradeOffer(new TradedItem(IafItems.MYRMEX_DESERT_RESIN.get(), this.emeraldCount), cloneStack, this.maxUses, this.exp, this.multiplier);
        }
    }

    static class DesertResinForItemsTrade implements TradeOffers.Factory {
        private final Item tradeItem;
        private final int count;
        private final int maxUses;
        private final int xpValue;
        private final float priceMultiplier;

        public DesertResinForItemsTrade(ItemConvertible tradeItemIn, int countIn, int maxUsesIn, int xpValueIn) {
            this.tradeItem = tradeItemIn.asItem();
            this.count = countIn;
            this.maxUses = maxUsesIn;
            this.xpValue = xpValueIn;
            this.priceMultiplier = 0.05F;
        }

        @Override
        public TradeOffer create(Entity trader, Random rand) {
            return new TradeOffer(new TradedItem(this.tradeItem, this.count), new ItemStack(IafItems.MYRMEX_DESERT_RESIN.get()), this.maxUses, this.xpValue, this.priceMultiplier);
        }
    }

    static class ItemsForJungleResinAndItemsTrade implements TradeOffers.Factory {
        private final ItemStack buyingItem;
        private final int buyingItemCount;
        private final int emeraldCount;
        private final ItemStack sellingItem;
        private final int sellingItemCount;
        private final int maxUses;
        private final int xpValue;
        private final float priceMultiplier;

        public ItemsForJungleResinAndItemsTrade(ItemConvertible buyingItem, int buyingItemCount, Item sellingItem, int sellingItemCount, int maxUses, int xpValue) {
            this(buyingItem, buyingItemCount, 1, sellingItem, sellingItemCount, maxUses, xpValue);
        }

        public ItemsForJungleResinAndItemsTrade(ItemConvertible buyingItem, int buyingItemCount, int emeraldCount, Item sellingItem, int sellingItemCount, int maxUses, int xpValue) {
            this.buyingItem = new ItemStack(buyingItem);
            this.buyingItemCount = buyingItemCount;
            this.emeraldCount = emeraldCount;
            this.sellingItem = new ItemStack(sellingItem);
            this.sellingItemCount = sellingItemCount;
            this.maxUses = maxUses;
            this.xpValue = xpValue;
            this.priceMultiplier = 0.05F;
        }

        @Override
        public TradeOffer create(Entity trader, Random rand) {
            return new TradeOffer(new TradedItem(IafItems.MYRMEX_JUNGLE_RESIN.get(), this.emeraldCount), Optional.of(new TradedItem(this.buyingItem.getItem(), this.buyingItemCount)), new ItemStack(this.sellingItem.getItem(), this.sellingItemCount), this.maxUses, this.xpValue, this.priceMultiplier);
        }
    }

    static class ItemsForJungleResinTrade implements TradeOffers.Factory {
        private final ItemStack stack;
        private final int emeraldCount;
        private final int itemCount;
        private final int maxUses;
        private final int exp;
        private final float multiplier;

        public ItemsForJungleResinTrade(Block sellingItem, int emeraldCount, int sellingItemCount, int maxUses, int xpValue) {
            this(new ItemStack(sellingItem), emeraldCount, sellingItemCount, maxUses, xpValue);
        }

        public ItemsForJungleResinTrade(Item sellingItem, int emeraldCount, int sellingItemCount, int xpValue) {
            this(new ItemStack(sellingItem), emeraldCount, sellingItemCount, 12, xpValue);
        }

        public ItemsForJungleResinTrade(Item item, int JungleResin, int items, int maxUses, int exp) {
            this(new ItemStack(item), JungleResin, items, maxUses, exp);
        }

        public ItemsForJungleResinTrade(ItemStack stack, int JungleResin, int items, int maxUses, int exp) {
            this(stack, JungleResin, items, maxUses, exp, 0.05F);
        }

        public ItemsForJungleResinTrade(ItemStack stack, int JungleResin, int items, int maxUses, int exp, float multi) {
            this.stack = stack;
            this.emeraldCount = JungleResin;
            this.itemCount = items;
            this.maxUses = maxUses;
            this.exp = exp;
            this.multiplier = multi;
        }

        @Override
        public TradeOffer create(Entity trader, Random rand) {
            ItemStack cloneStack = this.stack.copyComponentsToNewStack(this.stack.getItem(), this.itemCount);
            return new TradeOffer(new TradedItem(IafItems.MYRMEX_JUNGLE_RESIN.get(), this.emeraldCount), cloneStack, this.maxUses, this.exp, this.multiplier);
        }
    }

    static class JungleResinForItemsTrade implements TradeOffers.Factory {
        private final Item tradeItem;
        private final int count;
        private final int maxUses;
        private final int xpValue;
        private final float priceMultiplier;

        public JungleResinForItemsTrade(ItemConvertible tradeItemIn, int countIn, int maxUsesIn, int xpValueIn) {
            this.tradeItem = tradeItemIn.asItem();
            this.count = countIn;
            this.maxUses = maxUsesIn;
            this.xpValue = xpValueIn;
            this.priceMultiplier = 0.05F;
        }

        @Override
        public TradeOffer create(Entity trader, Random rand) {
            TradedItem lvt_3_1_ = new TradedItem(this.tradeItem, this.count);
            return new TradeOffer(lvt_3_1_, new ItemStack(IafItems.MYRMEX_JUNGLE_RESIN.get()), this.maxUses, this.xpValue, this.priceMultiplier);
        }
    }
}

