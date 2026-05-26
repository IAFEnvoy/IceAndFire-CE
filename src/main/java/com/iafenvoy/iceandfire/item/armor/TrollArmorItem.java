package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.TrollType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class TrollArmorItem extends ArmorItem {
    private final TrollType trollType;

    public TrollArmorItem(TrollType trollType, Type type) {
        super(trollType.getMaterial(), type, new Properties().durability(switch (type) {
            case HELMET -> 220;
            case CHESTPLATE -> 320;
            case LEGGINGS -> 300;
            case BOOTS -> 260;
            case BODY -> 0;
        }));
        this.trollType = trollType;
    }

    public static String getName(TrollType trollType, Type type) {
        return String.format(Locale.ROOT, "%s_troll_leather_%s", trollType.getName(), type.getName());
    }

    @Override
    public @NotNull Holder<ArmorMaterial> getMaterial() {
        return this.trollType.getMaterial();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable(String.format(Locale.ROOT, "item.%s.troll_leather_armor_%s.desc", IceAndFire.MOD_ID, this.type.getName())).withStyle(ChatFormatting.GREEN));
    }
}
