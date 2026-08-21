package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.registry.IafTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class TrollWeaponItem extends Item {
    public final TrollType.ITrollWeapon weapon;

    public TrollWeaponItem(TrollType.ITrollWeapon weapon) {
        super(new Properties().sword(IafTiers.TROLL_WEAPON_TOOL_MATERIAL, 15, -3.5F));
        this.weapon = weapon;
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (attacker instanceof Player player)
            return;
        super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel world, @NotNull Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player player && slot == EquipmentSlot.MAINHAND)
            if (player.getAttackStrengthScale(0) < 0.95 && player.attackAnim > 0)
                player.swingTime--;
    }

    public boolean onEntitySwing(LivingEntity LivingEntity, ItemStack stack) {
        if (LivingEntity instanceof Player player)
            if (player.getAttackStrengthScale(0) < 1 && player.attackAnim > 0)
                return true;
            else
                player.swingTime = -1;
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull net.minecraft.world.item.component.TooltipDisplay display, java.util.function.@NonNull @NonNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
    }
}
