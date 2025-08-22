package com.iafenvoy.iceandfire.item.tool;

import com.google.common.collect.Multimap;
import com.iafenvoy.iceandfire.StaticVariables;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.jupiter.network.ByteBufUtil;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public interface DragonSteelOverrides<T extends ToolItem> {
    /**
     * Kept for compatibility
     *
     * @deprecated use data pack overrides instead
     */
    @Deprecated
    Multimap<EntityAttribute, EntityAttributeModifier> bakeDragonsteel();

    default float getAttackDamage(T item) {
        if (item instanceof SwordItem swordItem)
            return swordItem.getAttackDamage();
        if (item instanceof MiningToolItem toolItem)
            return toolItem.getAttackDamage();
        return item.getMaterial().getAttackDamage();
    }

    default boolean isDragonSteel(ToolMaterial tier) {
        return this.isDragonSteelFire(tier) || this.isDragonSteelIce(tier) || this.isDragonSteelLightning(tier);
    }

    default boolean isDragonSteelFire(ToolMaterial tier) {
        return tier.getRepairIngredient().test(IafItems.DRAGONSTEEL_FIRE_INGOT.get().getDefaultStack());
    }

    default boolean isDragonSteelIce(ToolMaterial tier) {
        return tier.getRepairIngredient().test(IafItems.DRAGONSTEEL_ICE_INGOT.get().getDefaultStack());
    }

    default boolean isDragonSteelLightning(ToolMaterial tier) {
        return tier.getRepairIngredient().test(IafItems.DRAGONSTEEL_LIGHTNING_INGOT.get().getDefaultStack());
    }

    default void hurtEnemy(T item, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (item.getMaterial() == IafItems.SILVER_TOOL_MATERIAL)
            if (target.getGroup() == EntityGroup.UNDEAD)
                target.damage(attacker.getWorld().getDamageSources().magic(), this.getAttackDamage(item) + 3.0F);

        if (this.isDragonSteelFire(item.getMaterial())) {
            if (IafCommonConfig.INSTANCE.armors.dragonFireAbility.getValue()) {
                target.setOnFireFor(15);
                target.takeKnockback(1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
            }
        }
        if (this.isDragonSteelIce(item.getMaterial())) {
            if (IafCommonConfig.INSTANCE.armors.dragonIceAbility.getValue()) {
                IafEntityData data = IafEntityData.get(target);
                data.frozenData.setFrozen(target, 300);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 300, 2));
                target.takeKnockback(1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
            }
        }
        if (this.isDragonSteelLightning(item.getMaterial())) {
            if (IafCommonConfig.INSTANCE.armors.dragonLightningAbility.getValue() && attacker.getWorld() instanceof ServerWorld world && target instanceof MobEntity mob) {
                Vec3d pos = attacker.getPos();
                Collection<EntityAttributeModifier> damages = stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                //TODO: config
                double searchRange = IafCommonConfig.INSTANCE.armors.dragonLightningSearchRange.getValue();
                List<Pair<Vec3d, Vec3d>> lightnings = new LinkedList<>();
                //Cache for BFS
                Queue<Pair<MobEntity, Double>> bfsQueue = new LinkedList<>();
                bfsQueue.add(new Pair<>(mob, damages.isEmpty() ? 1 : damages.iterator().next().getValue()));
                List<MobEntity> attacked = new LinkedList<>();
                while (!bfsQueue.isEmpty()) {
                    Pair<MobEntity, Double> pair = bfsQueue.poll();
                    MobEntity mobEntity = pair.getLeft();
                    double damage = pair.getRight();
                    mobEntity.damage(world.getDamageSources().mobAttack(target), (float) damage);
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
                        if (attacked.size() + bfsQueue.size() >= IafCommonConfig.INSTANCE.armors.dragonLightningMaxSearchCount.getValue())
                            break;
                        bfsQueue.add(new Pair<>(m, damage * IafCommonConfig.INSTANCE.armors.dragonLightningDamageReduction.getValue()));
                        lightnings.add(new Pair<>(mobEntity.getPos(), m.getPos()));
                    }
                }
                for (ServerPlayerEntity player : world.getPlayers(player1 -> player1.distanceTo(attacker) < 64)) {
                    PacketByteBuf buf = ByteBufUtil.create();
                    buf.writeInt(lightnings.size());
                    for (Pair<Vec3d, Vec3d> pair : lightnings) {
                        Vec3d p1 = pair.getLeft(), p2 = pair.getRight();
                        buf.writeDouble(p1.x).writeDouble(p1.y).writeDouble(p1.z).writeDouble(p2.x).writeDouble(p2.y).writeDouble(p2.z);
                    }
                    NetworkManager.sendToPlayer(player, StaticVariables.LIGHTNING_BOLT_S2C, buf);
                }
            }
        }
    }

    default void appendHoverText(ToolMaterial tier, ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        if (tier == IafItems.SILVER_TOOL_MATERIAL)
            tooltip.add(Text.translatable("silvertools.hurt").formatted(Formatting.GREEN));
        if (this.isDragonSteelFire(tier)) {
            if (IafCommonConfig.INSTANCE.armors.dragonFireAbility.getValue())
                tooltip.add(Text.translatable("dragon_sword_fire.hurt2").formatted(Formatting.DARK_RED));
        }
        if (this.isDragonSteelIce(tier)) {
            if (IafCommonConfig.INSTANCE.armors.dragonIceAbility.getValue())
                tooltip.add(Text.translatable("dragon_sword_ice.hurt2").formatted(Formatting.AQUA));
        }
        if (this.isDragonSteelLightning(tier)) {
            if (IafCommonConfig.INSTANCE.armors.dragonLightningAbility.getValue())
                tooltip.add(Text.translatable("dragon_sword_lightning.hurt2").formatted(Formatting.DARK_PURPLE));
        }
    }
}
