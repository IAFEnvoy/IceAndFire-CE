package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.registry.IafTiers;
import com.iafenvoy.uranus.object.RegistryHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class HippogryphSwordItem extends Item {
    public HippogryphSwordItem() {
        super(new Properties().sword(IafTiers.HIPPOGRYPH_SWORD_TOOL_MATERIAL, 3, -2.4F));
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity targetEntity, LivingEntity attacker) {
        float f = (float) attacker.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        float f3 = 1.0F + getMultiplier(EnchantmentHelper.getEnchantmentLevel(RegistryHelper.getEnchantment(attacker.registryAccess(), Enchantments.SWEEPING_EDGE), attacker)) * f;
        if (attacker instanceof Player player) {
            for (LivingEntity LivingEntity : attacker.level().getEntitiesOfClass(LivingEntity.class, targetEntity.getBoundingBox().inflate(1.0D, 0.25D, 1.0D)))
                if (LivingEntity != player && LivingEntity != targetEntity && !attacker.isAlliedTo(LivingEntity) && attacker.distanceToSqr(LivingEntity) < 9.0D) {
                    LivingEntity.knockback(0.4F, Mth.sin(attacker.getYRot() * 0.017453292F), -Mth.cos(attacker.getYRot() * 0.017453292F));
                    LivingEntity.hurt(attacker.level().damageSources().playerAttack(player), f3);
                }
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
        }
        super.hurtEnemy(stack, targetEntity, attacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NonNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.hippogryph_sword.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.hippogryph_sword.desc_1").withStyle(ChatFormatting.GRAY));
    }

    public static float getMultiplier(int level) {
        return 1.0F - 1.0F / (float) (level + 1);
    }
}
