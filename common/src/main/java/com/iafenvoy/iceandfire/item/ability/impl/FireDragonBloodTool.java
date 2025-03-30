package com.iafenvoy.iceandfire.item.ability.impl;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.item.ability.DamageBonusAbility;
import com.iafenvoy.iceandfire.item.ability.PostHitAbility;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class FireDragonBloodTool implements PostHitAbility {
    private final DamageBonusAbility damageBonus = new DamageBonusAbilityImpl(8.0F, IafEntityTags.FIRE_DRAGON, null);
    @Override
    public void active(LivingEntity target, LivingEntity attacker) {
        damageBonus.active(target, attacker);
        if (isEnable()) {
            AbilityImpls.IGNITE_TARGET.active(target, attacker);
            AbilityImpls.TAKE_KNOCKBACK.active(target, attacker);
        }
    }

    @Override
    public boolean isEnable() {
        return IafCommonConfig.INSTANCE.armors.dragonFireAbility.getValue();
    }

    @Override
    public void addDescription(List<Text> tooltip) {
        tooltip.add(Text.translatable("item.iceandfire.legendary_weapon.desc").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("dragon_sword_fire.hurt1").formatted(Formatting.GREEN));
        if (isEnable()) {
            tooltip.add(Text.translatable("dragon_sword_fire.hurt2").formatted(Formatting.DARK_RED));
        }
    }
}
