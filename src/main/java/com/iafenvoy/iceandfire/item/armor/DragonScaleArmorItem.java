package com.iafenvoy.iceandfire.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public class DragonScaleArmorItem extends Item {
    private final DragonColor color;
    private final ArmorType type;

    public DragonScaleArmorItem(DragonColor color, ArmorType type) {
        super(new Properties().humanoidArmor(color.getMaterial().value(), type));
        this.color = color;
        this.type = type;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, net.minecraft.world.item.component.@NonNull TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        tooltip.accept(Component.translatable("dragon." + this.color.getName().toLowerCase(Locale.ROOT)).withStyle(this.color.getColorFormatting()));
        tooltip.accept(Component.translatable("item.dragonscales_armor.desc").withStyle(ChatFormatting.GRAY));
    }

    public DragonColor getColor() {
        return this.color;
    }
}
