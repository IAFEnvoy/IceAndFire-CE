package com.iafenvoy.iceandfire;

import com.iafenvoy.iceandfire.compat.ponder.IceAndFirePonderPlugin;
import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.IafRenderers;
import com.iafenvoy.integration.IntegrationExecutor;
import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.render.screen.ConfigSelectScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@Mod(value = IceAndFire.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class IceAndFireClient {
    public IceAndFireClient() {
        ConfigManager.getInstance().registerConfigHandler(IafClientConfig.INSTANCE);

        IntegrationExecutor.runWhenLoad("ponder", () -> IceAndFirePonderPlugin::init);
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        IafRenderers.registerModelPredicates();
        IafRenderers.registerArmorRenderers();
        IafRenderers.registerItemRenderers();


        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (container, parent) -> ConfigSelectScreen.builder(Component.translatable("config.iceandfire.title"), parent).server(IafCommonConfig.INSTANCE).client(IafClientConfig.INSTANCE).build());
    }

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        event.addPackFinders(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "resourcepacks/iaf_legacy"), PackType.CLIENT_RESOURCES, Component.translatable("resourcePack.iceandfire.legacy.name"), PackSource.BUILT_IN, false, Pack.Position.TOP);
    }
}
