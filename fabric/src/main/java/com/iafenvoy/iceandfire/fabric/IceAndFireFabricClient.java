package com.iafenvoy.iceandfire.fabric;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.IceAndFireClient;
import com.iafenvoy.iceandfire.registry.IafRenderers;
import dev.architectury.platform.Platform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class IceAndFireFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IceAndFireClient.init();
        IceAndFireClient.process();
        IafRenderers.registerParticleRenderers(holder -> holder.applyRegister(ParticleFactoryRegistry.getInstance()::register, (t, f) -> ParticleFactoryRegistry.getInstance().register(t, f::create)));
        if (!Platform.isDevelopmentEnvironment())
            FabricLoader.getInstance().getModContainer(IceAndFire.MOD_ID).ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(IceAndFire.MOD_ID, "legacy"), container, Text.translatable("resourcePack.iceandfire.legacy.name"), ResourcePackActivationType.NORMAL));
    }
}
