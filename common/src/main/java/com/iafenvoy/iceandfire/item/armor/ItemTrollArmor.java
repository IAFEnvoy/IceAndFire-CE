package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.data.TrollType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;

public class ItemTrollArmor extends ArmorItem {
    public final TrollType troll;

    public ItemTrollArmor(TrollType troll, Type slot) {
        super(troll.getMaterial(), slot, new Settings());
        this.troll = troll;
    }

    public static String getName(TrollType troll, EquipmentSlot slot) {
        return String.format(Locale.ROOT, "%s_troll_leather_%s", troll.getName(), getArmorPart(slot));
    }

    private static String getArmorPart(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> "";
        };
    }

    @Override
    public ArmorMaterial getMaterial() {
        return this.troll.getMaterial();
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        tooltip.add(Text.translatable("item.iceandfire.troll_leather_armor_" + getArmorPart(this.type.getEquipmentSlot()) + ".desc").formatted(Formatting.GREEN));
    }
}
