package com.iafenvoy.iceandfire.entity.util;

public interface IVillagerFear {
    default boolean shouldFear() {
        return true;
    }
}
