package com.iafenvoy.iceandfire.item.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeaSerpentScalesBlock extends Block {
    final ChatFormatting color;
    final String name;

    public SeaSerpentScalesBlock(String name, ChatFormatting color) {
        super(Properties.of().mapColor(MapColor.STONE).strength(30F, 500F).sound(SoundType.STONE).requiresCorrectToolForDrops());
        this.color = color;
        this.name = name;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag options) {
        super.appendHoverText(stack, context, tooltip, options);
        tooltip.add(Component.translatable("sea_serpent." + this.name).withStyle(this.color));
    }
}
