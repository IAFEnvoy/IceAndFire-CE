package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafArmorMaterials;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EarPlugsArmorItem extends ArmorItem {
    public EarPlugsArmorItem() {
        super(IafArmorMaterials.EARPLUGS, ArmorItem.Type.HELMET, new Settings().maxDamage(55));
    }

    private static boolean isAprilFool() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return isAprilFool() ? String.format(Locale.ROOT, "item.%s.air_pods", IceAndFire.MOD_ID) : super.getTranslationKey(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (isAprilFool()) tooltip.add(Text.translatable("item.iceandfire.air_pods.desc").formatted(Formatting.GREEN));
    }
}
