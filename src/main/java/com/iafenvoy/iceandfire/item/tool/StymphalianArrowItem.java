package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.entity.StymphalianArrowEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StymphalianArrowItem extends ArrowItem {
    public StymphalianArrowItem() {
        super(new Item.Properties());
    }

    @Override
    public @NotNull AbstractArrow createArrow(@NotNull Level world, @NotNull ItemStack stack, @NotNull LivingEntity shooter, @Nullable ItemStack shotFrom) {
        return new StymphalianArrowEntity(IafEntities.STYMPHALIAN_ARROW.get(), world, shooter, shotFrom);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.iceandfire.stymphalian_arrow.desc").withStyle(ChatFormatting.GRAY));
    }
}
