package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.entity.PixieChargeEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.uranus.object.RegistryHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PixieWandItem extends Item {
    public PixieWandItem() {
        super(new Properties().stacksTo(1).durability(500));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player user, @NotNull InteractionHand hand) {
        ItemStack itemStackIn = user.getItemInHand(hand);
        boolean flag = user.isCreative() || EnchantmentHelper.getItemEnchantmentLevel(RegistryHelper.getEnchantment(user.registryAccess(), Enchantments.INFINITY), itemStackIn) > 0;
        ItemStack itemstack = this.findAmmo(user);
        user.startUsingItem(hand);
        user.swing(hand);
        if (!itemstack.isEmpty() || flag) {
            boolean flag1 = user.isCreative() || this.isInfinite(itemstack, itemStackIn, user);
            if (!flag1) {
                itemstack.shrink(1);
                if (itemstack.isEmpty())
                    user.getInventory().removeItem(itemstack);
            }
            if (!world.isClientSide) {
                double d2 = user.getLookAngle().x;
                double d3 = user.getLookAngle().y;
                double d4 = user.getLookAngle().z;
                float inaccuracy = 1.0F;
                d2 = d2 + user.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
                d3 = d3 + user.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
                d4 = d4 + user.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
                PixieChargeEntity charge = new PixieChargeEntity(IafEntities.PIXIE_CHARGE.get(), world, user, d2, d3, d4);
                charge.setPos(user.getX(), user.getY() + 1, user.getZ());
                world.addFreshEntity(charge);
            }
            user.playSound(IafSounds.PIXIE_WAND.get(), 1F, 0.75F + 0.5F * user.getRandom().nextFloat());
            itemstack.hurtAndBreak(1, user, LivingEntity.getSlotForHand(user.getUsedItemHand()));
            user.getCooldowns().addCooldown(this, 5);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStackIn);
    }

    public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
        int enchant = EnchantmentHelper.getItemEnchantmentLevel(RegistryHelper.getEnchantment(player.registryAccess(), Enchantments.INFINITY), bow);
        return enchant > 0 && stack.getItem() == IafItems.PIXIE_DUST.get();
    }

    private ItemStack findAmmo(Player player) {
        if (this.isAmmo(player.getItemInHand(InteractionHand.OFF_HAND)))
            return player.getItemInHand(InteractionHand.OFF_HAND);
        else if (this.isAmmo(player.getItemInHand(InteractionHand.MAIN_HAND)))
            return player.getItemInHand(InteractionHand.MAIN_HAND);
        else {
            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack = player.getInventory().getItem(i);
                if (this.isAmmo(itemstack))
                    return itemstack;
            }
            return ItemStack.EMPTY;
        }
    }

    protected boolean isAmmo(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == IafItems.PIXIE_DUST.get();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.iceandfire.pixie_wand.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.iceandfire.pixie_wand.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
