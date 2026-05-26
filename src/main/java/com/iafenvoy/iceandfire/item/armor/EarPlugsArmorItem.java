package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafArmorMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EarPlugsArmorItem extends ArmorItem {
    public EarPlugsArmorItem() {
        super(IafArmorMaterials.EARPLUGS, ArmorItem.Type.HELMET, new Properties().durability(55));
    }

    private static boolean isAprilFool() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1;
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
        return isAprilFool() ? String.format(Locale.ROOT, "item.%s.air_pods", IceAndFire.MOD_ID) : super.getDescriptionId(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        if (isAprilFool())
            tooltip.add(Component.translatable("item.iceandfire.air_pods.desc").withStyle(ChatFormatting.GREEN));
    }
}
