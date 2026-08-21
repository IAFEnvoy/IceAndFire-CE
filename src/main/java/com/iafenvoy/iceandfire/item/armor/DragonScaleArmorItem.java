package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.data.DragonColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.function.Consumer;

public class DragonScaleArmorItem extends Item {
    private final DragonColor color;
    private final ArmorType type;

    public DragonScaleArmorItem(DragonColor color, ArmorType type) {
        super(new Properties().humanoidArmor(color.getMaterial().value(), type));
        this.color = color;
        this.type = type;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NonNull TooltipDisplay display, Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        tooltip.accept(Component.translatable("dragon." + this.color.getName().toLowerCase(Locale.ROOT)).withStyle(this.color.getColorFormatting()));
        tooltip.accept(Component.translatable("item.dragonscales_armor.desc").withStyle(ChatFormatting.GRAY));
    }

    public DragonColor getColor() {
        return this.color;
    }
}
