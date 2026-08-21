package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.SeaSerpentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class SeaSerpentScaleItem extends Item {
    private final SeaSerpentType type;

    public SeaSerpentScaleItem(SeaSerpentType type) {
        super(new Properties());
        this.type = type;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull net.minecraft.world.item.component.TooltipDisplay display, java.util.function.@NonNull @NonNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        tooltip.accept(Component.translatable("sea_serpent." + this.type.getName()).withStyle(this.type.getColor()));
    }
}
