package com.iafenvoy.iceandfire.network;

import com.iafenvoy.iceandfire.network.payload.DragonControlC2SPayload;
import com.iafenvoy.iceandfire.network.payload.DragonSetBurnBlockS2CPayload;
import com.iafenvoy.iceandfire.network.payload.LightningBoltS2CPayload;
import com.iafenvoy.iceandfire.network.payload.MultipartInteractC2SPayload;
import com.iafenvoy.iceandfire.network.payload.PlayerHitMultipartC2SPayload;
import com.iafenvoy.iceandfire.network.payload.StartRidingMobPayload;
import com.iafenvoy.iceandfire.network.payload.UpdatePixieHouseS2CPayload;
import com.iafenvoy.iceandfire.network.payload.UpdatePixieJarS2CPayload;
import com.iafenvoy.iceandfire.network.payload.UpdatePodiumS2CPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public final class NetworkManager {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(DragonSetBurnBlockS2CPayload.ID, DragonSetBurnBlockS2CPayload.CODEC, ClientNetworkHandlers::handleDragonSetBurnBlock);
        registrar.playToClient(LightningBoltS2CPayload.ID, LightningBoltS2CPayload.CODEC, ClientNetworkHandlers::handleLightningBolt);
        registrar.playToClient(UpdatePixieHouseS2CPayload.ID, UpdatePixieHouseS2CPayload.CODEC, ClientNetworkHandlers::handleUpdatePixieHouse);
        registrar.playToClient(UpdatePixieJarS2CPayload.ID, UpdatePixieJarS2CPayload.CODEC, ClientNetworkHandlers::handleUpdatePixieJar);
        registrar.playToClient(UpdatePodiumS2CPayload.ID, UpdatePodiumS2CPayload.CODEC, ClientNetworkHandlers::handleUpdatePodium);

        registrar.playToServer(DragonControlC2SPayload.ID, DragonControlC2SPayload.CODEC, ServerNetworkHandlers::handleDragonControl);
        registrar.playToServer(MultipartInteractC2SPayload.ID, MultipartInteractC2SPayload.CODEC, ServerNetworkHandlers::handleMultipartInteract);
        registrar.playToServer(PlayerHitMultipartC2SPayload.ID, PlayerHitMultipartC2SPayload.CODEC, ServerNetworkHandlers::handlePlayerHitMultipart);

        registrar.playBidirectional(StartRidingMobPayload.ID, StartRidingMobPayload.CODEC, new DirectionalPayloadHandler<>(ClientNetworkHandlers::handleStartRidingMob, ServerNetworkHandlers::handleStartRidingMob));
    }
}
