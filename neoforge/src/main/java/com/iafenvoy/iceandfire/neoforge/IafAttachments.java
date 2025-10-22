package com.iafenvoy.iceandfire.neoforge;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.component.*;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class IafAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, IceAndFire.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ChainData>> CHAIN_DATA = register("chain_data", () -> AttachmentType.builder(ChainData::new).serialize(ChainData.CODEC).sync(ChainData.PACKET_CODEC).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ChickenData>> CHICKEN_DATA = register("chicken_data", () -> AttachmentType.builder(ChickenData::new).serialize(ChickenData.CODEC).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<FrozenData>> FROZEN_DATA = register("frozen_data", () -> AttachmentType.builder(FrozenData::new).serialize(FrozenData.CODEC).sync(FrozenData.PACKET_CODEC).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MiscData>> MISC_DATA = register("misc_data", () -> AttachmentType.builder(MiscData::new).serialize(MiscData.CODEC).sync(MiscData.PACKET_CODEC).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PortalData>> PORTAL_DATA = register("portal_data", () -> AttachmentType.builder(PortalData::new).serialize(PortalData.CODEC).sync(PortalData.PACKET_CODEC).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SirenData>> SIREN_DATA = register("siren_data", () -> AttachmentType.builder(SirenData::new).serialize(SirenData.CODEC).sync(SirenData.PACKET_CODEC).copyOnDeath().build());

    private static <T> DeferredHolder<AttachmentType<?>, AttachmentType<T>> register(String id, Supplier<AttachmentType<T>> type) {
        return REGISTRY.register(id, type);
    }
}
