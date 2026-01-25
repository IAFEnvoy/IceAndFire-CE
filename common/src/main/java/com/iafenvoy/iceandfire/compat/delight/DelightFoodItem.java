package com.iafenvoy.iceandfire.compat.delight;

import dev.architectury.platform.Platform;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class DelightFoodItem extends Item {
    public DelightFoodItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (!Platform.isModLoaded("farmersdelight"))
            tooltip.add(Text.translatable("item.iceandfire.tooltip.require.delight"));
    }
}
