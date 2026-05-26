package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.event.handler.ServerEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SummonLightningAbility implements PostHitAbility {
    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.isEnable()) {
            if (attacker instanceof Player && attacker.attackAnim > 0.2) {
                return;
            }
            if (!attacker.level().isClientSide) {
                LightningBolt lightningEntity = EntityType.LIGHTNING_BOLT.create(target.level());
                assert lightningEntity != null;
                lightningEntity.getTags().add(ServerEvents.BOLT_DONT_DESTROY_LOOT);
                lightningEntity.getTags().add(attacker.getStringUUID());
                lightningEntity.moveTo(target.position());
                if (!target.level().isClientSide) {
                    target.level().addFreshEntity(lightningEntity);
                }
            }
        }
    }
}
