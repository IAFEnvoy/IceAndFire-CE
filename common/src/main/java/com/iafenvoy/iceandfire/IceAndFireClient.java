package com.iafenvoy.iceandfire;

import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.event.ClientEvents;
import com.iafenvoy.iceandfire.network.ClientNetworkHelper;
import com.iafenvoy.iceandfire.registry.IafKeybindings;
import com.iafenvoy.iceandfire.registry.IafRenderers;
import com.iafenvoy.iceandfire.registry.IafScreenHandlers;
import com.iafenvoy.iceandfire.render.PortalRenderTick;
import com.iafenvoy.jupiter.ConfigManager;
import dev.architectury.event.events.common.InteractionEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class IceAndFireClient {
    public static void init() {
        ConfigManager.getInstance().registerConfigHandler(IafClientConfig.INSTANCE);

        IafRenderers.registerEntityRenderers();
        IafRenderers.registerParticleRenderers();
        IafKeybindings.init();
    }

    public static void process() {
        IafScreenHandlers.registerGui();
        IafRenderers.registerRenderLayers();
        IafRenderers.registerModelPredicates();
        IafRenderers.registerBlockEntityRenderers();
        IafRenderers.registerArmorRenderers();
        IafRenderers.registerItemRenderers();
        PortalRenderTick.init();

        InteractionEvent.INTERACT_ENTITY.register(ClientEvents::onEntityInteract);
        ClientEvents.registerFlightStaminaOverlay();

        ClientNetworkHelper.registerReceivers();
    }
}
