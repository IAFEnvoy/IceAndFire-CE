package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonColor;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Locale;

public class DragonScaleArmorItem extends ArmorItem {
    private final DragonColor color;

    public DragonScaleArmorItem(DragonColor color, Type slot) {
        super(color.getMaterial(), slot, new Settings().maxDamage(switch (slot) {
            case HELMET -> 397;
            case CHESTPLATE -> 577;
            case LEGGINGS -> 541;
            case BOOTS -> 469;
            case BODY -> 0;
        }));
        this.color = color;
    }

    @Override
    public String getTranslationKey() {
        return String.format(Locale.ROOT, "item.%s.dragon_%s", IceAndFire.MOD_ID, this.type.getName());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("dragon." + this.color.getName().toLowerCase(Locale.ROOT)).formatted(this.color.getColorFormatting()));
        tooltip.add(Text.translatable("item.dragonscales_armor.desc").formatted(Formatting.GRAY));
    }

    public DragonColor getColor() {
        return this.color;
    }
}
