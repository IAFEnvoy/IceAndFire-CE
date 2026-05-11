package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.world.structure.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructurePieceType;

import java.util.function.Supplier;

public final class IafStructurePieces {
    public static final DeferredRegister<StructurePieceType> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.STRUCTURE_PIECE);

    public static final RegistrySupplier<StructurePieceType> DRAGON_ROOST = register("dragon_roost", () -> DragonRoostStructure.DragonRoostPiece::new);
    public static final RegistrySupplier<StructurePieceType> DRAGON_CAVE = register("dragon_cave", () -> DragonCaveStructure.DragonCavePiece::new);
    public static final RegistrySupplier<StructurePieceType> CYCLOPS_CAVE = register("cyclops_cave", () -> CyclopsCaveStructure.CyclopsCavePiece::new);
    public static final RegistrySupplier<StructurePieceType> HYDRA_CAVE = register("hydra_cave", () -> HydraCaveStructure.HydraCavePiece::new);
    public static final RegistrySupplier<StructurePieceType> SIREN_ISLAND = register("siren_island", () -> SirenIslandStructure.SirenIslandPiece::new);
    public static final RegistrySupplier<StructurePieceType> PIXIE_VILLAGE = register("pixie_village", () -> PixieVillageStructure.PixieVillagePiece::new);

    private static RegistrySupplier<StructurePieceType> register(String id, Supplier<StructurePieceType> type) {
        return REGISTRY.register(id, type);
    }
}
