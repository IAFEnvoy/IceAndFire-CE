package com.iafenvoy.iceandfire.forge.compat.curios;

import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.item.Item;
import top.theillusivec4.curios.api.CuriosApi;

public class CuriosRegistry {
    public static void registerItems() {
        registerSingle(IafItems.HYDRA_HEART.get());
    }

    private static void registerSingle(Item item) {
        CuriosApi.registerCurio(item, new SimpleTickItemWrapper(item));
    }
}
