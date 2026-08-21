package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;

public final class IafBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IceAndFire.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EggInIceBlockEntity>> EGG_IN_ICE = register("egginice", EggInIceBlockEntity::new, IafBlocks.EGG_IN_ICE);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PixieHouseBlockEntity>> PIXIE_HOUSE = register("pixie_house", PixieHouseBlockEntity::new, IafBlocks.PIXIE_HOUSE_MUSHROOM_RED, IafBlocks.PIXIE_HOUSE_MUSHROOM_BROWN, IafBlocks.PIXIE_HOUSE_OAK, IafBlocks.PIXIE_HOUSE_BIRCH, IafBlocks.PIXIE_HOUSE_SPRUCE, IafBlocks.PIXIE_HOUSE_DARK_OAK);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<JarBlockEntity>> PIXIE_JAR = register("pixie_jar", JarBlockEntity::new, IafBlocks.JAR_EMPTY, IafBlocks.JAR_PIXIE_0, IafBlocks.JAR_PIXIE_1, IafBlocks.JAR_PIXIE_2, IafBlocks.JAR_PIXIE_3, IafBlocks.JAR_PIXIE_4);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DragonForgeBlockEntity>> DRAGONFORGE_CORE = register("dragonforge_core", DragonForgeBlockEntity::new, IafBlocks.DRAGONFORGE_FIRE_CORE, IafBlocks.DRAGONFORGE_ICE_CORE, IafBlocks.DRAGONFORGE_FIRE_CORE_DISABLED, IafBlocks.DRAGONFORGE_ICE_CORE_DISABLED, IafBlocks.DRAGONFORGE_LIGHTNING_CORE, IafBlocks.DRAGONFORGE_LIGHTNING_CORE_DISABLED);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DragonForgeBrickBlockEntity>> DRAGONFORGE_BRICK = register("dragonforge_brick", DragonForgeBrickBlockEntity::new, IafBlocks.DRAGONFORGE_FIRE_BRICK, IafBlocks.DRAGONFORGE_ICE_BRICK, IafBlocks.DRAGONFORGE_LIGHTNING_BRICK);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DragonForgeInputBlockEntity>> DRAGONFORGE_INPUT = register("dragonforge_input", DragonForgeInputBlockEntity::new, IafBlocks.DRAGONFORGE_FIRE_INPUT, IafBlocks.DRAGONFORGE_ICE_INPUT, IafBlocks.DRAGONFORGE_LIGHTNING_INPUT);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DreadSpawnerBlockEntity>> DREAD_SPAWNER = register("dread_spawner", DreadSpawnerBlockEntity::new, IafBlocks.DREAD_SPAWNER);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GhostChestBlockEntity>> GHOST_CHEST = register("ghost_chest", GhostChestBlockEntity::new, IafBlocks.GHOST_CHEST);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LecternBlockEntity>> IAF_LECTERN = register("lectern", LecternBlockEntity::new, IafBlocks.LECTERN);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PodiumBlockEntity>> PODIUM = register("podium", PodiumBlockEntity::new, IafBlocks.PODIUM_OAK, IafBlocks.PODIUM_BIRCH, IafBlocks.PODIUM_SPRUCE, IafBlocks.PODIUM_JUNGLE, IafBlocks.PODIUM_DARK_OAK, IafBlocks.PODIUM_ACACIA);

    @SafeVarargs
    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String entityName, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block>... validBlocks) {
        return REGISTRY.register(entityName, () -> new BlockEntityType<>(factory, Set.of(Arrays.stream(validBlocks).map(Supplier::get).toArray(Block[]::new))));
    }
}
