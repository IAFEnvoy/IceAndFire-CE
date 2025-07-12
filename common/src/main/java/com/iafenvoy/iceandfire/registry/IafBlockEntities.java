package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.block.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Supplier;

public final class IafBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<BlockEntityEggInIce>> EGG_IN_ICE = register("egginice", () -> BlockEntityType.Builder.create(BlockEntityEggInIce::new, IafBlocks.EGG_IN_ICE.get()));
    public static final RegistrySupplier<BlockEntityType<BlockEntityPixieHouse>> PIXIE_HOUSE = register("pixie_house", () -> BlockEntityType.Builder.create(BlockEntityPixieHouse::new, IafBlocks.PIXIE_HOUSE_MUSHROOM_RED.get(), IafBlocks.PIXIE_HOUSE_MUSHROOM_BROWN.get(), IafBlocks.PIXIE_HOUSE_OAK.get(), IafBlocks.PIXIE_HOUSE_BIRCH.get(), IafBlocks.PIXIE_HOUSE_BIRCH.get(), IafBlocks.PIXIE_HOUSE_SPRUCE.get(), IafBlocks.PIXIE_HOUSE_DARK_OAK.get()));
    public static final RegistrySupplier<BlockEntityType<BlockEntityJar>> PIXIE_JAR = register("pixie_jar", () -> BlockEntityType.Builder.create(BlockEntityJar::new, IafBlocks.JAR_EMPTY.get(), IafBlocks.JAR_PIXIE_0.get(), IafBlocks.JAR_PIXIE_1.get(), IafBlocks.JAR_PIXIE_2.get(), IafBlocks.JAR_PIXIE_3.get(), IafBlocks.JAR_PIXIE_4.get()));
    public static final RegistrySupplier<BlockEntityType<BlockEntityDragonForge>> DRAGONFORGE_CORE = register("dragonforge_core", () -> BlockEntityType.Builder.create(BlockEntityDragonForge::new, IafBlocks.DRAGONFORGE_FIRE_CORE.get(), IafBlocks.DRAGONFORGE_ICE_CORE.get(), IafBlocks.DRAGONFORGE_FIRE_CORE_DISABLED.get(), IafBlocks.DRAGONFORGE_ICE_CORE_DISABLED.get(), IafBlocks.DRAGONFORGE_LIGHTNING_CORE.get(), IafBlocks.DRAGONFORGE_LIGHTNING_CORE_DISABLED.get()));
    public static final RegistrySupplier<BlockEntityType<BlockEntityDragonForgeBrick>> DRAGONFORGE_BRICK = register("dragonforge_brick", () -> BlockEntityType.Builder.create(BlockEntityDragonForgeBrick::new, IafBlocks.DRAGONFORGE_FIRE_BRICK.get(), IafBlocks.DRAGONFORGE_ICE_BRICK.get(), IafBlocks.DRAGONFORGE_LIGHTNING_BRICK.get()));
    public static final RegistrySupplier<BlockEntityType<BlockEntityDragonForgeInput>> DRAGONFORGE_INPUT = register("dragonforge_input", () -> BlockEntityType.Builder.create(BlockEntityDragonForgeInput::new, IafBlocks.DRAGONFORGE_FIRE_INPUT.get(), IafBlocks.DRAGONFORGE_ICE_INPUT.get(), IafBlocks.DRAGONFORGE_LIGHTNING_INPUT.get()));
    public static final RegistrySupplier<BlockEntityType<BlockEntityDreadPortal>> DREAD_PORTAL = register("dread_portal", () -> BlockEntityType.Builder.create(BlockEntityDreadPortal::new, IafBlocks.DREAD_PORTAL.get()));
    public static final RegistrySupplier<BlockEntityType<BlockEntityDreadSpawner>> DREAD_SPAWNER = register("dread_spawner", () -> BlockEntityType.Builder.create(BlockEntityDreadSpawner::new, IafBlocks.DREAD_SPAWNER.get()));
    public static final RegistrySupplier<BlockEntityType<BlockEntityGhostChest>> GHOST_CHEST = register("ghost_chest", () -> BlockEntityType.Builder.create(BlockEntityGhostChest::new, IafBlocks.GHOST_CHEST.get()));
    public static final RegistrySupplier<BlockEntityType<BlockEntityLectern>> IAF_LECTERN = register("lectern", () -> BlockEntityType.Builder.create(BlockEntityLectern::new, IafBlocks.LECTERN.get()));
    public static final RegistrySupplier<BlockEntityType<BlockEntityPodium>> PODIUM = register("podium", () -> BlockEntityType.Builder.create(BlockEntityPodium::new, IafBlocks.PODIUM_OAK.get(), IafBlocks.PODIUM_BIRCH.get(), IafBlocks.PODIUM_SPRUCE.get(), IafBlocks.PODIUM_JUNGLE.get(), IafBlocks.PODIUM_DARK_OAK.get(), IafBlocks.PODIUM_ACACIA.get()));

    private static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(String entityName, Supplier<BlockEntityType.Builder<T>> builder) {
        return REGISTRY.register(entityName, () -> builder.get().build(null));
    }
}
