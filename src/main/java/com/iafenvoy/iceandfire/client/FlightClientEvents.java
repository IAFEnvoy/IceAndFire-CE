package com.iafenvoy.iceandfire.client;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.AmphithereEntity;
import com.iafenvoy.iceandfire.render.entity.layer.AmphithereRiderLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(value = Dist.CLIENT, modid = IceAndFire.MOD_ID)
public final class FlightClientEvents {
    private FlightClientEvents() {
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "flight_stamina"),
                new FlightStaminaOverlay());
    }

    /**
     * Flight rework: the rider of an amphithere is drawn by {@link AmphithereRiderLayer}, so the standalone player
     * render has to be skipped unless the camera is in first person or a rider is currently being rendered.
     */
    @SubscribeEvent
    public static void disablePlayerRenderWhenNeed(RenderPlayerEvent.Pre event) {
        if (!IafCommonConfig.INSTANCE.amphithere.flightRework.getValue()) return;
        Player player = event.getEntity();
        if (player.getVehicle() instanceof AmphithereEntity && player instanceof LocalPlayer
                && (Minecraft.getInstance().options.getCameraType().isFirstPerson() || !AmphithereRiderLayer.RENDERING_RIDERS.contains(player)))
            event.setCanceled(true);
        if (player instanceof RemotePlayer && player.getVehicle() instanceof AmphithereEntity
                && !AmphithereRiderLayer.RENDERING_RIDERS.contains(player))
            event.setCanceled(true);
    }
}
