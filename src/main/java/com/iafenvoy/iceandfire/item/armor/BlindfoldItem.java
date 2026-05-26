package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.registry.IafArmorMaterials;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BlindfoldItem extends ArmorItem {
    public BlindfoldItem() {
        super(IafArmorMaterials.BLINDFOLD, Type.HELMET, new Properties().durability(55));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (entity instanceof Player player && player.getItemBySlot(this.getEquipmentSlot()) == stack)
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50, 0, false, false));
    }
}
