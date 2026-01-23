package com.iafenvoy.iceandfire.compat.delight;

import dev.architectury.platform.Platform;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DelightFoodItem extends Item {
    private final Item remainder;

    public DelightFoodItem(Settings settings, Item remainder) {
        super(settings);
        this.remainder = remainder;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack itemStack = super.finishUsing(stack, world, user);
        ItemStack remainder = new ItemStack(this.remainder);
        if (itemStack.isEmpty()) return remainder;
        if (user instanceof PlayerEntity player && !player.isCreative()) player.getInventory().offerOrDrop(remainder);
        return itemStack;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (!Platform.isModLoaded("farmersdelight"))
            tooltip.add(Text.translatable("item.iceandfire.tooltip.require.delight"));
    }
}
