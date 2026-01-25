package com.iafenvoy.iceandfire.item.ability;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.network.payload.LightningBoltS2CPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LightningMultihitAbility implements PostHitAbility {
    @Override
    public void active(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.isEnable()) {
            if (IafCommonConfig.INSTANCE.tools.dragonLightningAbility.getValue() && attacker.getWorld() instanceof ServerWorld world && target instanceof MobEntity mob) {
                Vec3d pos = attacker.getPos();
                double searchRange = IafCommonConfig.INSTANCE.tools.dragonLightningSearchRange.getValue();
                List<Pair<Vec3d, Vec3d>> lightnings = new LinkedList<>();
                //Cache for BFS
                Queue<Pair<MobEntity, Double>> bfsQueue = new LinkedList<>();
                bfsQueue.add(new Pair<>(mob, (double) EnchantmentHelper.getDamage(world, stack, target, world.damageSources.mobAttack(attacker), 1)));
                List<MobEntity> attacked = new LinkedList<>();
                while (!bfsQueue.isEmpty()) {
                    Pair<MobEntity, Double> pair = bfsQueue.poll();
                    MobEntity mobEntity = pair.getLeft();
                    double damage = pair.getRight();
                    if (mobEntity != target)//Don't hit the source entity again
                        mobEntity.damage(world.getDamageSources().mobAttack(attacker), (float) damage);
                    attacked.add(mobEntity);
                    //Search for more targets
                    List<MobEntity> targets = world.getNonSpectatingEntities(MobEntity.class, new Box(
                            pos.getX() - searchRange,
                            pos.getY() - searchRange,
                            pos.getZ() - searchRange,
                            pos.getX() + searchRange,
                            pos.getY() + searchRange,
                            pos.getZ() + searchRange
                    )).stream().filter(attacker::canSee).filter(x -> !attacked.contains(x)).toList();
                    for (MobEntity m : targets) {
                        if (attacked.size() + bfsQueue.size() >= IafCommonConfig.INSTANCE.tools.dragonLightningMaxSearchCount.getValue())
                            break;
                        bfsQueue.add(new Pair<>(m, damage * IafCommonConfig.INSTANCE.tools.dragonLightningDamageReduction.getValue()));
                        lightnings.add(new Pair<>(mobEntity.getBoundingBox().getCenter(), m.getBoundingBox().getCenter()));
                    }
                }
                for (ServerPlayerEntity player : world.getPlayers(player1 -> player1.distanceTo(attacker) < 64))
                    NetworkManager.sendToPlayer(player, new LightningBoltS2CPayload(lightnings));
            }
        }
    }
}
