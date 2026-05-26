package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.entity.StymphalianFeatherEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StymphalianFeatherBundleItem extends Item {
    public StymphalianFeatherBundleItem() {
        super(new Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStackIn = player.getItemInHand(hand);
        player.startUsingItem(hand);
        player.getCooldowns().addCooldown(this, 15);
        player.playSound(SoundEvents.EGG_THROW, 1, 1);
        if (!worldIn.isClientSide) {
            float rotation = player.yHeadRot;
            for (int i = 0; i < 8; i++) {
                StymphalianFeatherEntity feather = new StymphalianFeatherEntity(IafEntities.STYMPHALIAN_FEATHER.get(), worldIn, player);
                rotation += 45;
                feather.shootFromRotation(player, 0, rotation, 0.0F, 1.5F, 1.0F);
                worldIn.addFreshEntity(feather);
            }
        }
        if (!player.isCreative())
            itemStackIn.shrink(1);
        return new InteractionResultHolder<>(InteractionResult.PASS, itemStackIn);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.iceandfire.stymphalian_feather_bundle.desc_0").withStyle(ChatFormatting.GRAY));
    }
}