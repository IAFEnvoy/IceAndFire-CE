package com.iafenvoy.iceandfire.item.food;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class ItemAmbrosia extends ItemGenericFood {
    public ItemAmbrosia() {
        super(5, 0.6F, false, false, true, 1);
    }

    @Override
    public void onFoodEaten(ItemStack stack, World worldIn, LivingEntity livingEntity) {
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 3600, 2));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 3600, 2));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 3600, 2));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 3600, 2));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack itemStack = super.finishUsing(stack, world, user);
        ItemStack remainder = new ItemStack(Items.BOWL);
        if (itemStack.isEmpty()) return remainder;
        if (user instanceof PlayerEntity player && !player.isCreative()) player.getInventory().offerOrDrop(remainder);
        return itemStack;
    }
}
