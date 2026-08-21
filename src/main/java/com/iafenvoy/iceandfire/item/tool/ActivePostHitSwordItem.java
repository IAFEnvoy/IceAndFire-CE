package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.item.ability.PostHitAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class ActivePostHitSwordItem extends Item {
    private final PostHitAbility ability;

    public ActivePostHitSwordItem(ToolMaterial toolMaterial, Properties settings, PostHitAbility ability) {
        super(settings.sword(toolMaterial, 3.0F, -2.4F));
        this.ability = ability;
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (this.ability.isEnable()) {
            this.ability.active(stack, target, attacker);
        }
        super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull net.minecraft.world.item.component.TooltipDisplay display, java.util.function.@NonNull @NonNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        if (this.ability.isEnable()) {
            this.ability.addDescription(tooltip);
        }
    }
}
