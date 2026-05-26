package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.world.structure.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class IafStructurePieces {
    public static final DeferredRegister<StructurePieceType> REGISTRY = DeferredRegister.create(Registries.STRUCTURE_PIECE, IceAndFire.MOD_ID);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> FIRE_DRAGON_ROOST = register("fire_dragon_roost", () -> FireDragonRoostStructure.FireDragonRoostPiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> ICE_DRAGON_ROOST = register("ice_dragon_roost", () -> IceDragonRoostStructure.IceDragonRoostPiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> LIGHTNING_DRAGON_ROOST = register("lightning_dragon_roost", () -> LightningDragonRoostStructure.LightningDragonRoostPiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> FIRE_DRAGON_CAVE = register("fire_dragon_cave", () -> FireDragonCaveStructure.FireDragonCavePiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> ICE_DRAGON_CAVE = register("ice_dragon_cave", () -> IceDragonCaveStructure.IceDragonCavePiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> LIGHTNING_DRAGON_CAVE = register("lightning_dragon_cave", () -> LightningDragonCaveStructure.LightningDragonCavePiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> CYCLOPS_CAVE = register("cyclops_cave", () -> CyclopsCaveStructure.CyclopsCavePiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> HYDRA_CAVE = register("hydra_cave", () -> HydraCaveStructure.HydraCavePiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> SIREN_ISLAND = register("siren_island", () -> SirenIslandStructure.SirenIslandPiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> PIXIE_VILLAGE = register("pixie_village", () -> PixieVillageStructure.PixieVillagePiece::new);

    private static DeferredHolder<StructurePieceType, StructurePieceType> register(String id, Supplier<StructurePieceType> type) {
        return REGISTRY.register(id, type);
    }
}
