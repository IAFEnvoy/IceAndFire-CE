package com.iafenvoy.iceandfire.util.trade.factory;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BuyWithPrice implements VillagerTrades.ItemListing {
    private final ItemStack input1;
    private final ItemStack input2;
    private final ItemStack output;
    private final int maxUses;
    private final int experience;
    private final float multiplier;

    public BuyWithPrice(ItemStack input, ItemStack output, int maxUses, int experience, float priceMultiplier) {
        this(input, null, output, maxUses, experience, priceMultiplier);
    }

    public BuyWithPrice(ItemStack input1, ItemStack input2, ItemStack output, int maxUses, int experience, float priceMultiplier) {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.maxUses = maxUses;
        this.experience = experience;
        this.multiplier = priceMultiplier;
    }

    @Nullable
    @Override
    public MerchantOffer getOffer(@NotNull Entity entity, @NotNull RandomSource random) {
        if (this.input2 == null)
            return new MerchantOffer(new ItemCost(this.input1.getItem(), this.input1.getCount()), this.output, this.maxUses, this.experience, this.multiplier);
        return new MerchantOffer(new ItemCost(this.input1.getItem(), this.input1.getCount()), Optional.of(new ItemCost(this.input2.getItem(), this.input2.getCount())), this.output, this.maxUses, this.experience, this.multiplier);
    }
}
