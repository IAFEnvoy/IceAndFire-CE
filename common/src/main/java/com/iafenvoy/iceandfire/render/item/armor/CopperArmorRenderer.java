package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.iceandfire.render.model.armor.CopperArmorModel;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class CopperArmorRenderer implements IArmorRendererBase<LivingEntity> {
    @Override
    public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot armorSlot, BipedEntityModel<LivingEntity> bipedEntityModel) {
        return new CopperArmorModel(armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.HEAD);
    }
}
