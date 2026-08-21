package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.registry.IafArmorMaterials;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.NotNull;

public class BlindfoldItem extends Item {
    public BlindfoldItem() {
        super(new Properties().humanoidArmor(IafArmorMaterials.BLINDFOLD.value(), ArmorType.HELMET));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel world, @NotNull Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (entity instanceof Player player && player.getItemBySlot(ArmorType.HELMET.getSlot()) == stack)
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50, 0, false, false));
    }
}
