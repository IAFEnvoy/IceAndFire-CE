package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.registry.IafToolMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TrollWeaponItem extends SwordItem {
    public final TrollType.ITrollWeapon weapon;

    public TrollWeaponItem(TrollType.ITrollWeapon weapon) {
        super(IafToolMaterials.TROLL_WEAPON_TOOL_MATERIAL, new Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafToolMaterials.TROLL_WEAPON_TOOL_MATERIAL, 15, -3.5F)));
        this.weapon = weapon;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (attacker instanceof Player player)
            return player.getAttackStrengthScale(0) < 0.95 || player.attackAnim != 0;
        else return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        if (entity instanceof Player player && selected)
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
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
    }
}
