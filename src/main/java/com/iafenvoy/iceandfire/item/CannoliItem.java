package com.iafenvoy.iceandfire.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CannoliItem extends Item {
    public CannoliItem() {
        super(new Properties().food(new FoodProperties.Builder().nutrition(20).saturationModifier(2).alwaysEdible().effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 2), 1).build()));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.iceandfire.cannoli.desc").withStyle(ChatFormatting.GRAY));
    }
}
