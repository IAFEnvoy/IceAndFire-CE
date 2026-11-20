package com.iafenvoy.iceandfire.compat.curios;

import com.iafenvoy.iceandfire.item.HydraHeartItem;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CuriosRegistry {
    public static void registerItems() {
        CuriosApi.registerCurio(IafItems.HYDRA_HEART.get(), new ICurioItem() {
            @Override
            public void curioTick(SlotContext ctx, ItemStack stack) {
                if (ctx.entity() instanceof Player player && player.level() instanceof ServerLevel)
                    HydraHeartItem.applyRegeneration(player);
            }
        });
    }
}
