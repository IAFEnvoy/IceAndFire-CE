package com.iafenvoy.iceandfire.render.entity.feature;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public interface IHasArmorVariantResource {
    ResourceLocation getArmorResource(int variant, EquipmentSlot slotType);
}
