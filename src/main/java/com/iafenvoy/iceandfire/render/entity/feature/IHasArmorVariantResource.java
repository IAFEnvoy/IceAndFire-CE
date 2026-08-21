package com.iafenvoy.iceandfire.render.entity.feature;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;

public interface IHasArmorVariantResource {
    Identifier getArmorResource(int variant, EquipmentSlot slotType);
}
