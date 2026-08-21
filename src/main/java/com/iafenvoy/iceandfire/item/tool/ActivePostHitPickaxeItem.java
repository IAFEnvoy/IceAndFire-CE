package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.item.ability.PostHitAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class ActivePostHitPickaxeItem extends Item {
    private final PostHitAbility ability;

    public ActivePostHitPickaxeItem(ToolMaterial material, Properties settings, PostHitAbility ability) {
        super(settings.pickaxe(material, 1.0F, -2.8F));
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
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull TooltipDisplay display, @NonNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        if (this.ability.isEnable()) {
            this.ability.addDescription(tooltip);
        }
    }
}
