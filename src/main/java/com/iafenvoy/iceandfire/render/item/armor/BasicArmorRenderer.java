package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BasicArmorRenderer implements IArmorRendererBase<LivingEntity> {
    private final Boolean2ObjectFunction<HumanoidModel<LivingEntity>> modelProvider;

    public BasicArmorRenderer(Boolean2ObjectFunction<HumanoidModel<LivingEntity>> modelProvider) {
        this.modelProvider = modelProvider;
    }

    @Override
    public HumanoidModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<LivingEntity> bipedEntityModel) {
        return this.modelProvider.get(armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.HEAD);
    }
}
