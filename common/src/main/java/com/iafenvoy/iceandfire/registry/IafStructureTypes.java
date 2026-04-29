package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.world.structure.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.function.Supplier;

public final class IafStructureTypes {
    public static final DeferredRegister<StructureType<?>> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.STRUCTURE_TYPE);

    public static final RegistrySupplier<StructureType<GraveyardStructure>> GRAVEYARD = registerType("graveyard", () -> () -> GraveyardStructure.CODEC);
    public static final RegistrySupplier<StructureType<MausoleumStructure>> MAUSOLEUM = registerType("mausoleum", () -> () -> MausoleumStructure.CODEC);
    public static final RegistrySupplier<StructureType<GorgonTempleStructure>> GORGON_TEMPLE = registerType("gorgon_temple", () -> () -> GorgonTempleStructure.CODEC);
    public static final RegistrySupplier<StructureType<DragonRoostStructure>> DRAGON_ROOST = registerType("dragon_roost", () -> () -> DragonRoostStructure.CODEC);
    public static final RegistrySupplier<StructureType<FireDragonCaveStructure>> FIRE_DRAGON_CAVE = registerType("fire_dragon_cave", () -> () -> FireDragonCaveStructure.CODEC);
    public static final RegistrySupplier<StructureType<IceDragonCaveStructure>> ICE_DRAGON_CAVE = registerType("ice_dragon_cave", () -> () -> IceDragonCaveStructure.CODEC);
    public static final RegistrySupplier<StructureType<LightningDragonCaveStructure>> LIGHTNING_DRAGON_CAVE = registerType("lightning_dragon_cave", () -> () -> LightningDragonCaveStructure.CODEC);
    public static final RegistrySupplier<StructureType<CyclopsCaveStructure>> CYCLOPS_CAVE = registerType("cyclops_cave", () -> () -> CyclopsCaveStructure.CODEC);
    public static final RegistrySupplier<StructureType<HydraCaveStructure>> HYDRA_CAVE = registerType("hydra_cave", () -> () -> HydraCaveStructure.CODEC);
    public static final RegistrySupplier<StructureType<SirenIslandStructure>> SIREN_ISLAND = registerType("siren_island", () -> () -> SirenIslandStructure.CODEC);
    public static final RegistrySupplier<StructureType<PixieVillageStructure>> PIXIE_VILLAGE = registerType("pixie_village", () -> () -> PixieVillageStructure.CODEC);

    private static <P extends Structure> RegistrySupplier<StructureType<P>> registerType(String name, Supplier<StructureType<P>> factory) {
        return REGISTRY.register(name, factory);
    }
}
