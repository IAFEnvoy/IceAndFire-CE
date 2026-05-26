package com.iafenvoy.iceandfire.data;

import net.minecraft.world.entity.EquipmentSlot;

import java.util.Locale;

public enum DragonArmorPart {
    HEAD, NECK, BODY, TAIL;

    public String getId() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static DragonArmorPart fromSlot(EquipmentSlot slot) {
        return switch (slot) {
            case MAINHAND, OFFHAND, BODY -> null;
            case FEET -> TAIL;
            case LEGS -> BODY;
            case CHEST -> NECK;
            case HEAD -> HEAD;
        };
    }
}
