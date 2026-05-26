package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FireDragonBloodToolAbility implements PostHitAbility {
    private final DamageBonusAbility damageBonus = new DamageBonusAbility(8.0F, IafEntityTags.FIRE_DRAGON, null);
    private final PostHitAbility ignite = new IgniteTargetAbility(IafCommonConfig.INSTANCE.tools.dragonBloodFireDuration.getValue());

    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        this.damageBonus.active(stack, target, attacker);
        if (this.isEnable()) {
            this.ignite.active(stack, target, attacker);
            BuiltinAbilities.TAKE_KNOCKBACK.active(stack, target, attacker);
        }
    }

    @Override
    public boolean isEnable() {
        return IafCommonConfig.INSTANCE.tools.dragonFireAbility.getValue();
    }

    @Override
    public void addDescription(List<Component> tooltip) {
        tooltip.add(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("dragon_sword_fire.hurt1").withStyle(ChatFormatting.GREEN));
        if (this.isEnable()) {
            tooltip.add(Component.translatable("dragon_sword_fire.hurt2").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
