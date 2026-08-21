package com.iafenvoy.iceandfire.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class DragonSteelArmorItem extends Item {
    public DragonSteelArmorItem(Holder<ArmorMaterial> material, ArmorType type) {
        super(new Properties().humanoidArmor(material.value(), type));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull net.minecraft.world.item.component.TooltipDisplay display, java.util.function.@NonNull @NonNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        tooltip.accept(Component.translatable("item.dragonscales_armor.desc").withStyle(ChatFormatting.GRAY));
    }
}
