package com.iafenvoy.iceandfire.item.tool;

import com.google.common.collect.Multimap;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.iceandfire.event.ServerEvents;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;


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
            if (IafCommonConfig.INSTANCE.armors.dragonLightningAbility.getValue()) {
                boolean flag = true;
                if (attacker instanceof PlayerEntity)
                    if (attacker.handSwingProgress > 0.2)
                        flag = false;
                if (!attacker.getWorld().isClient && flag) {
                    LightningEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(target.getWorld());
                    assert lightningboltentity != null;
                    lightningboltentity.getCommandTags().add(ServerEvents.BOLT_DONT_DESTROY_LOOT);
                    lightningboltentity.getCommandTags().add(attacker.getUuidAsString());
                    lightningboltentity.refreshPositionAfterTeleport(target.getPos());
                    if (!target.getWorld().isClient)
                        target.getWorld().spawnEntity(lightningboltentity);
                }
                target.takeKnockback(1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
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
