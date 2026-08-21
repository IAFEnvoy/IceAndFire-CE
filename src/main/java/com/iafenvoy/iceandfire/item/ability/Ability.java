package com.iafenvoy.iceandfire.item.ability;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public interface Ability {
    default boolean isEnable() {
        return true;
    }

    default void addDescription(Consumer<Component> tooltip) {
    }
}
