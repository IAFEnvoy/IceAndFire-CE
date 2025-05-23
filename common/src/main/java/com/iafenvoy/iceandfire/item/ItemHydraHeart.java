package com.iafenvoy.iceandfire.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemHydraHeart extends Item {
    public ItemHydraHeart() {
        super(new Settings().maxCount(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof PlayerEntity player && itemSlot >= 0 && itemSlot <= 8) {
            double healthPercentage = player.getHealth() / Math.max(1, player.getMaxHealth());
            if (healthPercentage < 1.0D) {
                int level = 0;
                if (healthPercentage < 0.25D) level = 3;
                else if (healthPercentage < 0.5D) level = 2;
                else if (healthPercentage < 0.75D) level = 1;
                //Consider using EffectInstance.combine
                if (!player.hasStatusEffect(StatusEffects.REGENERATION) || player.getStatusEffect(StatusEffects.REGENERATION).getAmplifier() < level)
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, level, true, false));
            }
            //In hotbar
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.iceandfire.legendary_weapon.desc").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.hydra_heart.desc_0").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.hydra_heart.desc_1").formatted(Formatting.GRAY));
    }
}
