package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.registry.IafDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CyclopsEyeItem extends Item {
    public CyclopsEyeItem() {
        super(new Properties().durability(500).component(IafDataComponents.TICK_COUNTER.get(), 0));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof LivingEntity living) {
            int tick = stack.getOrDefault(IafDataComponents.TICK_COUNTER.get(), 0);
            if (living.getMainHandItem() == stack || living.getOffhandItem() == stack) {
                double range = 15;
                boolean inflictedDamage = false;
                for (Mob LivingEntity : world.getEntitiesOfClass(Mob.class, new AABB(living.getX() - range, living.getY() - range, living.getZ() - range, living.getX() + range, living.getY() + range, living.getZ() + range)))
                    if (!LivingEntity.is(living) && !LivingEntity.isAlliedTo(living) && (LivingEntity.getTarget() == living || LivingEntity.getLastHurtByMob() == living || LivingEntity instanceof Enemy)) {
                        LivingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10, 1));
                        inflictedDamage = true;
                    }
                if (inflictedDamage)
                    tick++;
            }
            if (tick > 120) {
                stack.hurtAndBreak(1, living, LivingEntity.getSlotForHand(living.getUsedItemHand()));
                tick = 0;
            }
            stack.set(IafDataComponents.TICK_COUNTER.get(), tick);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.iceandfire.cyclops_eye.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.iceandfire.cyclops_eye.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
