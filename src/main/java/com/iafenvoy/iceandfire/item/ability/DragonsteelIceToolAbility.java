package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DragonsteelIceToolAbility implements PostHitAbility {
    private final PostHitAbility frozen = new FrozenTargetAbility(IafCommonConfig.INSTANCE.tools.dragonsteelFrozenDuration.getValue());

    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
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
        if (this.isEnable()) {
            tooltip.add(Component.translatable("dragon_sword_ice.hurt2").withStyle(ChatFormatting.AQUA));
        }
    }
}
