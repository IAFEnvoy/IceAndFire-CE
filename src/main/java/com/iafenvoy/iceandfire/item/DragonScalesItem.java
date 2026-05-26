package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.DragonColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class DragonScalesItem extends Item {
    final DragonColor type;

    public DragonScalesItem(DragonColor type) {
        super(new Properties());
        this.type = type;
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "item.iceandfire.dragonscales";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("dragon." + this.type.getName().toLowerCase(Locale.ROOT)).withStyle(this.type.getColorFormatting()));
    }
}
