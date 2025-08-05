package com.iafenvoy.iceandfire.registry;

import com.google.common.collect.ImmutableSet;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.util.trade.TradeOfferHelper;
import com.iafenvoy.iceandfire.util.trade.factory.BuyWithPrice;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public final class IafTrades {
    public static final DeferredRegister<PointOfInterestType> POI_REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.POINT_OF_INTEREST_TYPE);
    public static final DeferredRegister<VillagerProfession> PROFESSION_REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.VILLAGER_PROFESSION);

    private static final String SCRIBE = "scribe";
    private static final RegistrySupplier<Block> SCRIBE_BLOCK = IafBlocks.LECTERN;
    public static final Function<Block, Set<BlockState>> SCRIBE_WORKSTATION = block -> new HashSet<>(block.getStateManager().getStates());
    public static final RegistrySupplier<PointOfInterestType> SCRIBE_POI = POI_REGISTRY.register(SCRIBE, () -> new PointOfInterestType(SCRIBE_WORKSTATION.apply(SCRIBE_BLOCK.get()), 1, 1));
    public static final RegistrySupplier<VillagerProfession> SCRIBE_PROFESSION = PROFESSION_REGISTRY.register(SCRIBE, () -> new VillagerProfession(SCRIBE, e -> e.matchesKey(SCRIBE_POI.getKey()), e -> e.matchesKey(SCRIBE_POI.getKey()), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN));

    public static void init() {
        for (BlockState state : SCRIBE_WORKSTATION.apply(SCRIBE_BLOCK.get()))
            PointOfInterestTypes.POI_STATES_TO_TYPE.put(state, Registries.POINT_OF_INTEREST_TYPE.getEntry(SCRIBE_POI.get()));
        VillagerProfession profession = SCRIBE_PROFESSION.get();
        final float emeraldForItemsMultiplier = 0.05F; //Values taken from VillagerTrades.java
        final float itemForEmeraldMultiplier = 0.05F;
        final float rareItemForEmeraldMultiplier = 0.2F;
        TradeOfferHelper.registerVillagerOffers(profession, 1,
                new BuyWithPrice(new ItemStack(Items.EMERALD, 1), new ItemStack(IafItems.MANUSCRIPT.get(), 4), 25, 2, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(Items.BOOKSHELF, 3), new ItemStack(Items.EMERALD, 1), 8, 3, itemForEmeraldMultiplier),
                new BuyWithPrice(new ItemStack(Items.PAPER, 15), new ItemStack(Items.EMERALD, 2), 4, 4, itemForEmeraldMultiplier),
                new BuyWithPrice(new ItemStack(IafBlocks.ASH.get(), 10), new ItemStack(Items.EMERALD, 1), 8, 4, itemForEmeraldMultiplier));
        TradeOfferHelper.registerVillagerOffers(profession, 2,
                new BuyWithPrice(new ItemStack(IafItems.SILVER_INGOT.get(), 5), new ItemStack(Items.EMERALD, 1), 3, 5, itemForEmeraldMultiplier),
                new BuyWithPrice(new ItemStack(IafBlocks.FIRE_LILY.get(), 8), new ItemStack(Items.EMERALD, 1), 3, 5, itemForEmeraldMultiplier),
                new BuyWithPrice(new ItemStack(IafBlocks.LIGHTNING_LILY.get(), 7), new ItemStack(Items.EMERALD, 3), 2, 5, itemForEmeraldMultiplier),
                new BuyWithPrice(new ItemStack(Items.EMERALD, 3), new ItemStack(IafBlocks.FROST_LILY.get(), 4), 3, 3, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(Items.EMERALD, 2), new ItemStack(IafBlocks.DRAGON_ICE_SPIKES.get(), 7), 2, 3, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(IafItems.SAPPHIRE_GEM.get()), new ItemStack(Items.EMERALD, 2), 30, 3, rareItemForEmeraldMultiplier),
                new BuyWithPrice(new ItemStack(Items.EMERALD, 2), new ItemStack(IafBlocks.JAR_EMPTY.get(), 1), 3, 4, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(Items.AMETHYST_SHARD), new ItemStack(Items.EMERALD, 3), 20, 3, rareItemForEmeraldMultiplier));
        TradeOfferHelper.registerVillagerOffers(profession, 3,
                new BuyWithPrice(new ItemStack(IafItems.DRAGON_BONE.get(), 6), new ItemStack(Items.EMERALD, 1), 7, 4, itemForEmeraldMultiplier),
                new BuyWithPrice(new ItemStack(IafItems.CHAIN.get(), 2), new ItemStack(Items.EMERALD, 3), 4, 2, itemForEmeraldMultiplier),
                new BuyWithPrice(new ItemStack(Items.EMERALD, 6), new ItemStack(IafItems.PIXIE_DUST.get(), 2), 8, 3, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(Items.EMERALD, 6), new ItemStack(IafItems.FIRE_DRAGON_FLESH.get(), 2), 8, 3, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(Items.EMERALD, 7), new ItemStack(IafItems.ICE_DRAGON_FLESH.get(), 1), 8, 3, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(Items.EMERALD, 8), new ItemStack(IafItems.LIGHTNING_DRAGON_FLESH.get(), 1), 8, 3, emeraldForItemsMultiplier));
        TradeOfferHelper.registerVillagerOffers(profession, 4,
                new BuyWithPrice(new ItemStack(Items.EMERALD, 10), new ItemStack(IafItems.DRAGON_BONE.get(), 2), 20, 5, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(Items.EMERALD, 4), new ItemStack(IafItems.SHINY_SCALES.get(), 1), 5, 2, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(IafItems.DREAD_SHARD.get(), 5), new ItemStack(Items.EMERALD, 1), 10, 4, itemForEmeraldMultiplier),
                new BuyWithPrice(new ItemStack(Items.EMERALD, 8), new ItemStack(IafItems.STYMPHALIAN_BIRD_FEATHER.get(), 12), 3, 6, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(Items.EMERALD, 4), new ItemStack(IafItems.TROLL_TUSK.get(), 12), 7, 3, emeraldForItemsMultiplier));
        TradeOfferHelper.registerVillagerOffers(profession, 5,
                new BuyWithPrice(new ItemStack(Items.EMERALD, 15), new ItemStack(IafItems.SERPENT_FANG.get(), 3), 20, 3, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(Items.EMERALD, 12), new ItemStack(IafItems.HYDRA_FANG.get(), 1), 20, 3, emeraldForItemsMultiplier),
                new BuyWithPrice(new ItemStack(IafItems.ECTOPLASM.get(), 6), new ItemStack(Items.EMERALD, 1), 7, 3, itemForEmeraldMultiplier));
    }
}
