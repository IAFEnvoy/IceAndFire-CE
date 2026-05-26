package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.item.ability.BuiltinAbilities;
import com.iafenvoy.iceandfire.registry.IafTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GhostSwordItem extends SwordItem {
    public GhostSwordItem() {
        super(IafTiers.GHOST_SWORD_TOOL_MATERIAL, new Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.GHOST_SWORD_TOOL_MATERIAL, 5, -1.0F)));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag type) {
        tooltip.add(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, type);
        BuiltinAbilities.SUMMON_GHOST_SWORD.addDescription(tooltip);
    }
}
