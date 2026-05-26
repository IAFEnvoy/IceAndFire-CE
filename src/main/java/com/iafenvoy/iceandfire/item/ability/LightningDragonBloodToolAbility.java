package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LightningDragonBloodToolAbility implements PostHitAbility {
    private final DamageBonusAbility damageBonusFire = new DamageBonusAbility(4.0F, IafEntityTags.FIRE_DRAGON, null);
    private final DamageBonusAbility damageBonusIce = new DamageBonusAbility(4.0F, IafEntityTags.ICE_DRAGON, null);

    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        this.damageBonusFire.active(stack, target, attacker);
        this.damageBonusIce.active(stack, target, attacker);
        if (this.isEnable()) {
            BuiltinAbilities.SUMMON_LIGHTNING.active(stack, target, attacker);
        }
    }

    @Override
    public boolean isEnable() {
        return IafCommonConfig.INSTANCE.tools.dragonLightningAbility.getValue();
    }

    @Override
    public void addDescription(List<Component> tooltip) {
        tooltip.add(Component.translatable("dragon_sword_lightning.hurt1").withStyle(ChatFormatting.GREEN));
        if (this.isEnable()) {
            tooltip.add(Component.translatable("dragon_sword_lightning.hurt2").withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
}
