package com.iafenvoy.iceandfire.fabric.compat.trinkets;

import com.iafenvoy.iceandfire.registry.IafItems;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.item.Item;

public class TrinketsRegistry {
    public static void registerItems() {
        registerSingle(IafItems.HYDRA_HEART.get());
    }

    private static void registerSingle(Item item) {
        TrinketsApi.registerTrinket(item, new SimpleTickItemWrapper(item));
    }
}
