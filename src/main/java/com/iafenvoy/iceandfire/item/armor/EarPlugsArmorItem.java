package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.registry.IafArmorMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

public class EarPlugsArmorItem extends Item {
    public EarPlugsArmorItem() {
        super(new Properties().humanoidArmor(IafArmorMaterials.EARPLUGS.value(), ArmorType.HELMET));
    }

    private static boolean isAprilFool() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay display, @NonNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        if (isAprilFool())
            tooltip.accept(Component.translatable("item.iceandfire.air_pods.desc").withStyle(ChatFormatting.GREEN));
    }
}
