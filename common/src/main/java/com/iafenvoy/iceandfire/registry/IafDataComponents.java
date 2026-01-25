package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.HippogryphType;
import com.iafenvoy.iceandfire.item.component.BestiaryPageComponent;
import com.iafenvoy.iceandfire.item.component.DragonHornComponent;
import com.iafenvoy.iceandfire.item.component.DragonSkullComponent;
import com.iafenvoy.iceandfire.item.component.StoneStatusComponent;
import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Unit;

public final class IafDataComponents {
    public static final DeferredRegister<ComponentType<?>> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.DATA_COMPONENT_TYPE);
    public static final RegistrySupplier<ComponentType<Integer>> TICK_COUNTER = register("tick_counter", ComponentType.<Integer>builder()
            .codec(Codec.INT)
            .packetCodec(PacketCodecs.INTEGER)
    );
    public static final RegistrySupplier<ComponentType<Integer>> USER_ID = register("user_id", ComponentType.<Integer>builder()
            .codec(Codec.INT)
            .packetCodec(PacketCodecs.INTEGER)
    );
    public static final RegistrySupplier<ComponentType<Unit>> ACTIVE = register("active", ComponentType.<Unit>builder()
            .codec(Unit.CODEC)
            .packetCodec(PacketCodec.unit(Unit.INSTANCE))
    );
    public static final RegistrySupplier<ComponentType<HippogryphType>> HIPPOGRYPH_EGG = register("hippogryph_egg", ComponentType.<HippogryphType>builder()
            .codec(IafRegistries.HIPPOGRYPH_TYPE.getCodec())
    );
    //FIXME::Data Fix For Crystal
    public static final RegistrySupplier<ComponentType<NbtCompound>> NBT_COMPOUND = register("nbt_compound", ComponentType.<NbtCompound>builder()
            .codec(NbtCompound.CODEC)
    );
    public static final RegistrySupplier<ComponentType<NbtCompound>> CRYSTAL_DRAGON_DATA = register("crystal_dragon_data", ComponentType.<NbtCompound>builder()
            .codec(NbtCompound.CODEC)
    );
    public static final RegistrySupplier<ComponentType<BestiaryPageComponent>> BESTIARY_PAGES = register("bestiary_pages", ComponentType.<BestiaryPageComponent>builder()
            .codec(BestiaryPageComponent.CODEC)
    );
    public static final RegistrySupplier<ComponentType<DragonHornComponent>> DRAGON_HORN = register("dragon_horn", ComponentType.<DragonHornComponent>builder()
            .codec(DragonHornComponent.CODEC)
    );
    public static final RegistrySupplier<ComponentType<DragonSkullComponent>> DRAGON_SKULL = register("dragon_skull", ComponentType.<DragonSkullComponent>builder()
            .codec(DragonSkullComponent.CODEC)
    );
    public static final RegistrySupplier<ComponentType<StoneStatusComponent>> STONE_STATUS = register("stone_status", ComponentType.<StoneStatusComponent>builder()
            .codec(StoneStatusComponent.CODEC)
    );

    public static <T> RegistrySupplier<ComponentType<T>> register(String id, ComponentType.Builder<T> builder) {
        return REGISTRY.register(id, builder::build);
    }
}
