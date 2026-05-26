package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DragonsteelFireToolAbility implements PostHitAbility {
    private final PostHitAbility ignite = new IgniteTargetAbility(IafCommonConfig.INSTANCE.tools.dragonsteelFireDuration.getValue());

    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
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
        if (this.isEnable()) {
            tooltip.add(Component.translatable("dragon_sword_fire.hurt2").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
