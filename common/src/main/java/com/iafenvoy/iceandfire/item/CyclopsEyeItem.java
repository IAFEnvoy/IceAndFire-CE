package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.registry.IafDataComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class CyclopsEyeItem extends Item {
    public CyclopsEyeItem() {
        super(new Settings().maxDamage(500).component(IafDataComponents.TICK_COUNTER.get(), 0));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof LivingEntity living) {
            int tick = stack.getOrDefault(IafDataComponents.TICK_COUNTER.get(), 0);
            if (living.getMainHandStack() == stack || living.getOffHandStack() == stack) {
                double range = 15;
                boolean inflictedDamage = false;
                for (MobEntity LivingEntity : world.getNonSpectatingEntities(MobEntity.class, new Box(living.getX() - range, living.getY() - range, living.getZ() - range, living.getX() + range, living.getY() + range, living.getZ() + range)))
                    if (!LivingEntity.isPartOf(living) && !LivingEntity.isTeammate(living) && (LivingEntity.getTarget() == living || LivingEntity.getAttacker() == living || LivingEntity instanceof Monster)) {
                        LivingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 10, 1));
                        inflictedDamage = true;
                    }
                if (inflictedDamage)
                    tick++;
            }
            if (tick > 120) {
                stack.damage(1, living, LivingEntity.getSlotForHand(living.getActiveHand()));
                tick = 0;
            }
            stack.set(IafDataComponents.TICK_COUNTER.get(), tick);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.iceandfire.legendary_weapon.desc").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.cyclops_eye.desc_0").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.cyclops_eye.desc_1").formatted(Formatting.GRAY));
    }
}
