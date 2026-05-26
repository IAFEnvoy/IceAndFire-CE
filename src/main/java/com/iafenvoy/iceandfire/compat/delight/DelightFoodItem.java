package com.iafenvoy.iceandfire.compat.delight;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DelightFoodItem extends Item {
    public DelightFoodItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        if (!ModList.get().isLoaded("farmersdelight"))
            tooltip.add(Component.translatable("item.iceandfire.tooltip.require.delight"));
    }
}
