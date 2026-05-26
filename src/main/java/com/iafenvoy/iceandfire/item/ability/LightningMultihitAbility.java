package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.network.payload.LightningBoltS2CPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LightningMultihitAbility implements PostHitAbility {
    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.isEnable()) {
            if (IafCommonConfig.INSTANCE.tools.dragonLightningAbility.getValue() && attacker.level() instanceof ServerLevel world && target instanceof Mob mob) {
                Vec3 pos = attacker.position();
                double searchRange = IafCommonConfig.INSTANCE.tools.dragonLightningSearchRange.getValue();
                List<Tuple<Vec3, Vec3>> lightnings = new LinkedList<>();
                //Cache for BFS
                Queue<Tuple<Mob, Double>> bfsQueue = new LinkedList<>();
                bfsQueue.add(new Tuple<>(mob, (double) EnchantmentHelper.modifyDamage(world, stack, target, world.damageSources.mobAttack(attacker), 1)));
                List<Mob> attacked = new LinkedList<>();
                while (!bfsQueue.isEmpty()) {
                    Tuple<Mob, Double> pair = bfsQueue.poll();
                    Mob mobEntity = pair.getA();
                    double damage = pair.getB();
                    if (mobEntity != target)//Don't hit the source entity again
                        mobEntity.hurt(world.damageSources().mobAttack(attacker), (float) damage);
                    attacked.add(mobEntity);
                    //Search for more targets
                    List<Mob> targets = world.getEntitiesOfClass(Mob.class, new AABB(
                            pos.x() - searchRange,
                            pos.y() - searchRange,
                            pos.z() - searchRange,
                            pos.x() + searchRange,
                            pos.y() + searchRange,
                            pos.z() + searchRange
                    )).stream().filter(attacker::hasLineOfSight).filter(x -> !attacked.contains(x)).toList();
                    for (Mob m : targets) {
                        if (attacked.size() + bfsQueue.size() >= IafCommonConfig.INSTANCE.tools.dragonLightningMaxSearchCount.getValue())
                            break;
                        bfsQueue.add(new Tuple<>(m, damage * IafCommonConfig.INSTANCE.tools.dragonLightningDamageReduction.getValue()));
                        lightnings.add(new Tuple<>(mobEntity.getBoundingBox().getCenter(), m.getBoundingBox().getCenter()));
                    }
                }
                for (ServerPlayer player : world.getPlayers(player1 -> player1.distanceTo(attacker) < 64))
                    PacketDistributor.sendToPlayer(player, new LightningBoltS2CPayload(lightnings));
            }
        }
    }
}
