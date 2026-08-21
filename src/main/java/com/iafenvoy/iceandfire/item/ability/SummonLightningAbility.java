package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.event.handler.ServerEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
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
            if (target.level() instanceof ServerLevel level) {
                LightningBolt lightningEntity = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.EVENT);
                assert lightningEntity != null;
                lightningEntity.addTag(ServerEvents.BOLT_DONT_DESTROY_LOOT);
                lightningEntity.addTag(attacker.getStringUUID());
                lightningEntity.snapTo(target.position());
                level.addFreshEntity(lightningEntity);
            }
        }
    }
}
