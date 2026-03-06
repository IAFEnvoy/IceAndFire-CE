package com.iafenvoy.iceandfire.forge;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.IceAndFireClient;
import com.iafenvoy.iceandfire.forge.compat.curios.CuriosRegistry;
import com.iafenvoy.iceandfire.forge.component.EntityDataProvider;
import com.iafenvoy.iceandfire.forge.component.PortalDataProvider;
import com.iafenvoy.uranus.forge.component.CapabilitySyncHelper;
import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(IceAndFire.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class IceAndFireForge {
    public IceAndFireForge() {
        EventBuses.registerModEventBus(IceAndFire.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CapabilitySyncHelper.registerForLiving(Identifier.of(IceAndFire.MOD_ID, "entity_data"), EntityDataProvider.CAPABILITY, EntityDataProvider::new);
        CapabilitySyncHelper.registerForPlayer(Identifier.of(IceAndFire.MOD_ID, "portal_data"), PortalDataProvider.CAPABILITY, PortalDataProvider::new, CapabilitySyncHelper.CopyPolicy.NEVER);

        IceAndFire.init();
        if (Platform.getEnv() == Dist.CLIENT)
            IceAndFireClient.init();
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(IceAndFire::process);
        if (ModList.get().isLoaded("curios")) CuriosRegistry.registerItems();
    }
}
