package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.component.ChainData;
import com.iafenvoy.iceandfire.data.component.ChickenData;
import com.iafenvoy.iceandfire.data.component.MiscData;
import com.iafenvoy.iceandfire.data.component.PortalData;
import com.iafenvoy.iceandfire.util.attachment.IafEntityAttachment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@EventBusSubscriber
public final class IafAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, IceAndFire.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ChainData>> CHAIN_DATA = register("chain_data", () -> AttachmentType.builder(ChainData::new).serialize(ChainData.CODEC).sync(ChainData.PACKET_CODEC).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ChickenData>> CHICKEN_DATA = register("chicken_data", () -> AttachmentType.builder(ChickenData::new).serialize(ChickenData.CODEC).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MiscData>> MISC_DATA = register("misc_data", () -> AttachmentType.builder(MiscData::new).serialize(MiscData.CODEC).sync(MiscData.PACKET_CODEC).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PortalData>> PORTAL_DATA = register("portal_data", () -> AttachmentType.builder(PortalData::new).serialize(PortalData.CODEC).sync(PortalData.PACKET_CODEC).copyOnDeath().build());

    private static <T> DeferredHolder<AttachmentType<?>, AttachmentType<T>> register(String id, Supplier<AttachmentType<T>> type) {
        return REGISTRY.register(id, type);
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity living) {
            tickAndSync(IafAttachments.CHAIN_DATA, living);
            tickAndSync(IafAttachments.CHICKEN_DATA, living);
            tickAndSync(IafAttachments.MISC_DATA, living);
            tickAndSync(IafAttachments.PORTAL_DATA, living);
        }
    }

    private static <T extends Entity, A extends IafEntityAttachment<T>> void tickAndSync(Supplier<AttachmentType<A>> type, T entity) {
        A attachment = entity.getData(type);
        attachment.tick(entity);
        if (attachment.isDirty()) entity.syncData(type);
    }
}
