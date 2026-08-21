package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.item.ability.BuiltinAbilities;
import com.iafenvoy.iceandfire.registry.IafTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class GhostSwordItem extends Item {
    public GhostSwordItem() {
        super(new Properties().sword(IafTiers.GHOST_SWORD_TOOL_MATERIAL, 5, -1.0F));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NonNull TooltipDisplay display, Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, display, tooltip, type);
        BuiltinAbilities.SUMMON_GHOST_SWORD.addDescription(tooltip);
    }
}
