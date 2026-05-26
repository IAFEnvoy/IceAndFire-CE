package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.GhostSwordEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

public class SummonGhostSwordAbility implements SwingHandAbility {
    @Override
    public boolean isEnable() {
        return IafCommonConfig.INSTANCE.tools.phantasmalBladeAbility.getValue();
    }

    @Override
    public void active(LivingEntity attacker) {
        if (attacker instanceof Player playerEntity) {
            ItemStack stack = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);
            if (playerEntity.getCooldowns().isOnCooldown(stack.getItem())) {
                return;
            }
            final ItemAttributeModifiers dmg = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            double totalDmg = 0D;
            for (ItemAttributeModifiers.Entry modifier : dmg.modifiers())
                if (modifier.attribute().equals(Attributes.ATTACK_DAMAGE))
                    totalDmg += modifier.modifier().amount();
            playerEntity.playSound(SoundEvents.ZOMBIE_INFECT, 1, 1);
            GhostSwordEntity shot = new GhostSwordEntity(IafEntities.GHOST_SWORD.get(), playerEntity.level(), playerEntity, totalDmg * 0.5F, stack);
            shot.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, 1, 0.5f);
            playerEntity.level().addFreshEntity(shot);
            stack.hurtAndBreak(1, playerEntity, EquipmentSlot.MAINHAND);
            playerEntity.getCooldowns().addCooldown(stack.getItem(), 10);
        }
    }

    @Override
    public void addDescription(List<Component> tooltip) {
        tooltip.add(Component.translatable("item.iceandfire.ghost_sword.desc_0").withStyle(ChatFormatting.GRAY));
    }
}
