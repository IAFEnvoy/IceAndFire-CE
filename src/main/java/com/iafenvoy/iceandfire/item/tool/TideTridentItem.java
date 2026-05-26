package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.entity.TideTridentEntity;
import com.iafenvoy.uranus.object.RegistryHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TideTridentItem extends TridentItem {
    public TideTridentItem() {
        super(new Item.Properties().durability(400).component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes()));
    }

    public static @NotNull ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 12, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.9F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity user, int timeLeft) {
        if (user instanceof Player player) {
            int time = this.getUseDuration(stack, user) - timeLeft;
            if (time >= 10) {
                int riptideLevel = EnchantmentHelper.getItemEnchantmentLevel(RegistryHelper.getEnchantment(worldIn.registryAccess(), Enchantments.RIPTIDE), stack);
                if (riptideLevel <= 0 || player.isInWaterOrRain()) {
                    if (!worldIn.isClientSide) {
                        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(user.getUsedItemHand()));
                        if (riptideLevel == 0) {
                            TideTridentEntity tideTrident = new TideTridentEntity(worldIn, player, stack);
                            tideTrident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F + (float) riptideLevel * 0.5F, 1.0F);
                            if (player.getAbilities().instabuild)
                                tideTrident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            worldIn.addFreshEntity(tideTrident);
                            worldIn.playSound(null, tideTrident, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                            if (!player.getAbilities().instabuild)
                                player.getInventory().removeItem(stack);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    if (riptideLevel > 0) {
                        float yaw = player.getYRot();
                        float pitch = player.getXRot();
                        float velocityX = -Mth.sin(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);
                        float velocityY = -Mth.sin(pitch * 0.017453292F);
                        float velocityZ = Mth.cos(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);
                        float speed = Mth.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
                        float targetSpeed = 3.0F * ((1.0F + (float) riptideLevel) / 4.0F);
                        velocityX *= targetSpeed / speed;
                        velocityY *= targetSpeed / speed;
                        velocityZ *= targetSpeed / speed;
                        player.push(velocityX, velocityY, velocityZ);
                        player.startAutoSpinAttack(20, 8.0F, stack);
                        if (player.onGround())
                            player.move(MoverType.SELF, new Vec3(0.0D, 1.1999999284744263D, 0.0D));

                        Holder<SoundEvent> sound;
                        if (riptideLevel >= 3) sound = SoundEvents.TRIDENT_RIPTIDE_3;
                        else if (riptideLevel == 2) sound = SoundEvents.TRIDENT_RIPTIDE_2;
                        else sound = SoundEvents.TRIDENT_RIPTIDE_1;

                        worldIn.playSound(null, player, sound.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.iceandfire.tide_trident.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.iceandfire.tide_trident.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
