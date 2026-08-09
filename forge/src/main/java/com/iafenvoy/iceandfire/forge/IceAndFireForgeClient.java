package com.iafenvoy.iceandfire.forge;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.IceAndFireClient;
import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.jupiter.render.screen.ConfigSelectScreen;
import dev.architectury.platform.Platform;
import net.minecraft.resource.*;
import net.minecraft.text.Text;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.resource.PathPackResources;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class IceAndFireForgeClient {
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(IceAndFireClient::process);
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigSelectScreen.builder(Text.translatable("config.iceandfire.title"), parent).server(IafCommonConfig.INSTANCE).client(IafClientConfig.INSTANCE).build()));
    }

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (!Platform.isDevelopmentEnvironment() && event.getPackType() == ResourceType.CLIENT_RESOURCES)
            event.addRepositorySource(consumer -> consumer.accept(ResourcePackProfile.create(
                    "resourcepacks/iaf_legacy",
                    Text.translatable("resourcePack.iceandfire.legacy.name"),
                    false,
                    name -> new PathPackResources(IceAndFire.MOD_ID, true, ModList.get().getModFileById(IceAndFire.MOD_ID).getFile().findResource(name)),
                    ResourceType.CLIENT_RESOURCES,
                    ResourcePackProfile.InsertionPosition.TOP,
                    ResourcePackSource.BUILTIN
            )));
    }
}
