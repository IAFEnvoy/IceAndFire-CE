package com.iafenvoy.iceandfire.client;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.compat.IafClientCompat;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.AmphithereEntity;
import com.iafenvoy.iceandfire.render.entity.feature.AmphithereRiderFeatureRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(Dist.CLIENT)
public final class FlightClientEvents {
    private FlightClientEvents() {
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR,
                Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "flight_stamina"),
                new FlightStaminaOverlay());
    }

    /**
     * Flight rework: the rider of an amphithere is drawn by {@link AmphithereRiderFeatureRenderer}, so the standalone player
     * render has to be skipped unless the camera is in first person or a rider is currently being rendered.
     */
    @SubscribeEvent
    public static void disablePlayerRenderWhenNeed(RenderPlayerEvent.Pre<?> event) {
        if (!IafCommonConfig.INSTANCE.amphithere.flightRework.getValue()) return;
        if (Minecraft.getInstance().level == null) return;
        Entity entity = Minecraft.getInstance().level.getEntity(event.getRenderState().id);
        if (!(entity instanceof Player player) || !(player.getVehicle() instanceof AmphithereEntity)) return;
        if (player instanceof LocalPlayer && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            event.setCanceled(true);
            return;
        }
        // Sodium skips the nested extract/submit used by AmphithereRiderFeatureRenderer, so cancelling here would make the
        // rider vanish. Leave vanilla/Sodium passenger rendering.
        if (IafClientCompat.isSodiumLoaded()) return;
        if (player instanceof LocalPlayer && !AmphithereRiderFeatureRenderer.RENDERING_RIDERS.contains(player))
            event.setCanceled(true);
        if (player instanceof RemotePlayer && !AmphithereRiderFeatureRenderer.RENDERING_RIDERS.contains(player))
            event.setCanceled(true);
    }
}
