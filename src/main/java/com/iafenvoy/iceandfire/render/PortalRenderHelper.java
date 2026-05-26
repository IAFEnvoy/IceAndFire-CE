package com.iafenvoy.iceandfire.render;

import com.iafenvoy.iceandfire.registry.IafBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class PortalRenderHelper {
    private static int TICK = 0;

    public static int getTick() {
        return TICK;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Level world = minecraft.level;
        Player player = minecraft.player;
        if (world == null || player == null) return;
        if (world.getBlockState(player.blockPosition()).is(IafBlocks.DREAD_PORTAL.get())) TICK++;
        else if (TICK > 0) TICK--;
    }
}
