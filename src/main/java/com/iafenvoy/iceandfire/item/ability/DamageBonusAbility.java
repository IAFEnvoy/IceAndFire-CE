package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.registry.IafDamageTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record DamageBonusAbility(float bonus, TagKey<EntityType<?>> targetType,
                                 @Nullable Component tooltip) implements PostHitAbility {
    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player && player.getAttackStrengthScale(0) != 1.0F) return;
        if (target.getType().builtInRegistryHolder().is(this.targetType))
            target.hurt(IafDamageTypes.bonusDamage(attacker), this.bonus);
    }

    @Override
    public void addDescription(Consumer<Component> tooltip) {
        if (this.tooltip != null) tooltip.accept(this.tooltip);
    }
}
