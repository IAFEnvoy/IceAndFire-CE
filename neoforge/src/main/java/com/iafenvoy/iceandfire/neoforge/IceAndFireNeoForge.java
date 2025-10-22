package com.iafenvoy.iceandfire.neoforge;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.IceAndFireClient;
import com.iafenvoy.iceandfire.neoforge.compat.IceAndFireArsNouveauCompat;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.util.attachment.IafEntityAttachment;
import com.iafenvoy.integration.IntegrationExecutor;
import dev.architectury.platform.Platform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Potions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.function.Supplier;

@Mod(IceAndFire.MOD_ID)
@EventBusSubscriber
public final class IceAndFireNeoForge {
    public IceAndFireNeoForge(IEventBus modBus) {
        IafAttachments.REGISTRY.register(modBus);
        IceAndFire.init();
        if (Platform.getEnv() == Dist.CLIENT)
            IceAndFireClient.init();
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(IceAndFire::process);
        IntegrationExecutor.runWhenLoad("ars_nouveau", () -> IceAndFireArsNouveauCompat::init);
    }

    @SubscribeEvent
    public static void registerBrewing(RegisterBrewingRecipesEvent event) {
        event.getBuilder().registerPotionRecipe(Potions.WATER, IafItems.SHINY_SCALES.get(), Potions.WATER_BREATHING);
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity living) {
            tickAndSync(IafAttachments.CHAIN_DATA, living);
            tickAndSync(IafAttachments.CHICKEN_DATA, living);
            tickAndSync(IafAttachments.FROZEN_DATA, living);
            tickAndSync(IafAttachments.MISC_DATA, living);
            tickAndSync(IafAttachments.PORTAL_DATA, living);
            tickAndSync(IafAttachments.SIREN_DATA, living);
        }
    }

    private static <T extends Entity, A extends IafEntityAttachment<T>> void tickAndSync(Supplier<AttachmentType<A>> type, T entity) {
        A attachment = entity.getData(type);
        attachment.tick(entity);
        if (attachment.isDirty()) entity.syncData(type);
    }
}
