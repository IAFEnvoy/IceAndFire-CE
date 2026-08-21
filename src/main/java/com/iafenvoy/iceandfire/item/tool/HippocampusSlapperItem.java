package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.registry.IafTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class HippocampusSlapperItem extends Item {
    public HippocampusSlapperItem() {
        super(new Properties().sword(IafTiers.HIPPOCAMPUS_SWORD_TOOL_MATERIAL, 3, -2.4F));
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, LivingEntity targetEntity, @NotNull LivingEntity attacker) {
        targetEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2));
        targetEntity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 2));
        targetEntity.playSound(SoundEvents.GUARDIAN_FLOP, 3, 1);

        super.hurtEnemy(stack, targetEntity, attacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull net.minecraft.world.item.component.TooltipDisplay display, java.util.function.@NonNull @NonNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.hippocampus_slapper.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.hippocampus_slapper.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
