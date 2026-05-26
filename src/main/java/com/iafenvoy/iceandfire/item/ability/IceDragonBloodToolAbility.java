package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class IceDragonBloodToolAbility implements PostHitAbility {
    private final DamageBonusAbility damageBonus = new DamageBonusAbility(8.0F, IafEntityTags.FIRE_DRAGON, null);
    private final PostHitAbility frozen = new FrozenTargetAbility(IafCommonConfig.INSTANCE.tools.dragonBloodFrozenDuration.getValue());

    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        this.damageBonus.active(stack, target, attacker);
        if (this.isEnable()) {
            this.frozen.active(stack, target, attacker);
        }
    }

    @Override
    public boolean isEnable() {
        return IafCommonConfig.INSTANCE.tools.dragonIceAbility.getValue();
    }

    @Override
    public void addDescription(List<Component> tooltip) {
        tooltip.add(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("dragon_sword_ice.hurt1").withStyle(ChatFormatting.GREEN));
        if (this.isEnable()) {
            tooltip.add(Component.translatable("dragon_sword_ice.hurt2").withStyle(ChatFormatting.AQUA));
        }
    }
}
