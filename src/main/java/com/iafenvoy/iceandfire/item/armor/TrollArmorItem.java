package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.TrollType;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public class TrollArmorItem extends Item {
    private final TrollType trollType;
    private final ArmorType type;

    public TrollArmorItem(TrollType trollType, ArmorType type) {
        super(new Properties().humanoidArmor(trollType.getMaterial().value(), type));
        this.trollType = trollType;
        this.type = type;
    }

    public static String getName(TrollType trollType, ArmorType type) {
        return String.format(Locale.ROOT, "%s_troll_leather_%s", trollType.getName(), type.getName());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull net.minecraft.world.item.component.TooltipDisplay display, java.util.function.@NonNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        tooltip.accept(Component.translatable(String.format(Locale.ROOT, "item.%s.troll_leather_armor_%s.desc", IceAndFire.MOD_ID, this.type.getName())).withStyle(ChatFormatting.GREEN));
    }
}
