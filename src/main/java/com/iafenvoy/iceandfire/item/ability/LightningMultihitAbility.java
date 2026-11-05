package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.network.payload.LightningBoltS2CPayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LightningMultihitAbility implements PostHitAbility {
    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.isEnable()) {
            if (IafCommonConfig.INSTANCE.tools.dragonLightningAbility.getValue() && attacker.level() instanceof ServerLevel world && target instanceof Mob mob) {
                Vec3 pos = attacker.position();
                double searchRange = IafCommonConfig.INSTANCE.tools.dragonLightningSearchRange.getValue();
                List<Tuple<Vec3, Vec3>> lightnings = new LinkedList<>();
                int budget = IafCommonConfig.INSTANCE.tools.dragonLightningMaxSearchCount.getValue();
                Set<Mob> selected = new HashSet<>();
                selected.add(mob);
                double damage = getStackAttackDamage(stack) * IafCommonConfig.INSTANCE.tools.dragonLightningDamageReduction.getValue();
                if (budget > 1) {
                    // Every old BFS expansion searched this same box; one selection preserves its first-hop targets.
                    List<Mob> targets = world.getEntitiesOfClass(Mob.class, new AABB(
                            pos.x() - searchRange, pos.y() - searchRange, pos.z() - searchRange,
                            pos.x() + searchRange, pos.y() + searchRange, pos.z() + searchRange));
                    for (Mob next : targets) {
                        if (selected.size() >= budget) break;
                        if (next == attacker || !next.isAlive() || !attacker.hasLineOfSight(next) || !selected.add(next)) continue;
                        next.hurt(world.damageSources().mobAttack(attacker), (float) damage);
                        lightnings.add(new Tuple<>(mob.getBoundingBox().getCenter(), next.getBoundingBox().getCenter()));
                    }
                }
                for (ServerPlayer player : world.getPlayers(player1 -> player1.distanceTo(attacker) < 64))
                    PacketDistributor.sendToPlayer(player, new LightningBoltS2CPayload(lightnings));
            }
        }
    }

    /**
     * Base damage of the chain, taken from the weapon that started it, so the first link deals the same damage the
     * 1.20.1 implementation did (the stack's attack damage modifier, e.g. dragonSteelBaseDamage - 1) instead of a
     * flat 1. Each selected secondary target receives one dragonLightningDamageReduction factor.
     */
    private static double getStackAttackDamage(ItemStack stack) {
        final ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) return 1.0D;
        double damage = 0.0D;
        for (ItemAttributeModifiers.Entry modifier : modifiers.modifiers())
            if (modifier.attribute().equals(Attributes.ATTACK_DAMAGE))
                damage += modifier.modifier().amount();
        return damage > 0.0D ? damage : 1.0D;
    }
}
