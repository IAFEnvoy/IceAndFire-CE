package com.iafenvoy.iceandfire.neoforge;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.IceAndFireClient;
import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.IafRenderers;
import com.iafenvoy.jupiter.render.screen.ConfigSelectScreen;
import dev.architectury.platform.Platform;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class IceAndFireNeoForgeClient {
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(IceAndFireClient::process);
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (container, parent) -> ConfigSelectScreen.builder(Text.translatable("config.iceandfire.title"), parent).server(IafCommonConfig.INSTANCE).client(IafClientConfig.INSTANCE).build());
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        IafRenderers.registerParticleRenderers(holder -> holder.applyRegister(event::registerSpecial, event::registerSpriteSet));
    }

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (!Platform.isDevelopmentEnvironment())
            event.addPackFinders(Identifier.of(IceAndFire.MOD_ID, "resourcepacks/legacy"), ResourceType.CLIENT_RESOURCES, Text.translatable("resourcePack.iceandfire.legacy.name"), ResourcePackSource.BUILTIN, false, ResourcePackProfile.InsertionPosition.TOP);
    }
}
