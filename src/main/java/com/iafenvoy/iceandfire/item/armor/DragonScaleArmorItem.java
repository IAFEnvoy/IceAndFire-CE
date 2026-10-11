package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class DragonScaleArmorItem extends ArmorItem {
    private final DragonColor color;

    public DragonScaleArmorItem(DragonColor color, Type slot) {
        super(color.getMaterial(), slot, IafItems.cosmeticArmorProperties(new Properties().durability(switch (slot) {
            case HELMET -> 397;
            case CHESTPLATE -> 577;
            case LEGGINGS -> 541;
            case BOOTS -> 469;
            case BODY -> 0;
        })));
        this.color = color;
    }

    @Override
    public @NotNull String getDescriptionId() {
        return String.format(Locale.ROOT, "item.%s.dragon_%s", IceAndFire.MOD_ID, this.type.getName());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag type) {
        tooltip.add(Component.translatable("dragon." + this.color.getName().toLowerCase(Locale.ROOT)).withStyle(this.color.getColorFormatting()));
        if (IafCommonConfig.INSTANCE.armors.dragonScaleBreathReduction.getValue())
            tooltip.add(Component.translatable("item.dragonscales_armor.desc").withStyle(ChatFormatting.GRAY));
    }

    public DragonColor getColor() {
        return this.color;
    }
}
