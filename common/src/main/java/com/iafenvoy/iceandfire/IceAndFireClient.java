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
        dev.architectury.registry.ReloadListenerRegistry.register(net.minecraft.resource.ResourceType.CLIENT_RESOURCES,
                new com.iafenvoy.iceandfire.screen.gui.bestiary.BestiaryPageCache(),
                net.minecraft.util.Identifier.of(IceAndFire.MOD_ID, "bestiary_pages"));
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
