package com.iafenvoy.iceandfire.item.ability;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface Ability {
    default boolean isEnable() {
        return true;
    }

    default void addDescription(List<Component> tooltip) {
    }
}
