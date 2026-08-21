package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.DragonColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.function.Consumer;

public class DragonScalesItem extends Item {
    final DragonColor type;

    public DragonScalesItem(DragonColor type) {
        super(new Properties());
        this.type = type;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay display, @NonNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        tooltip.accept(Component.translatable("dragon." + this.type.getName().toLowerCase(Locale.ROOT)).withStyle(this.type.getColorFormatting()));
    }
}
