package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DragonSteelArmorItem extends ArmorItem {
    public DragonSteelArmorItem(Holder<ArmorMaterial> material, Type slot) {
        super(material, slot, new Properties().durability(switch (slot) {
            case HELMET -> IafCommonConfig.INSTANCE.armors.dragonsteelHelmetDurability.getValue();
            case CHESTPLATE -> IafCommonConfig.INSTANCE.armors.dragonsteelChestplateDurability.getValue();
            case LEGGINGS -> IafCommonConfig.INSTANCE.armors.dragonsteelLeggingsDurability.getValue();
            case BOOTS -> IafCommonConfig.INSTANCE.armors.dragonsteelBootsDurability.getValue();
            case BODY -> 0;
        }));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.dragonscales_armor.desc").withStyle(ChatFormatting.GRAY));
    }
}
