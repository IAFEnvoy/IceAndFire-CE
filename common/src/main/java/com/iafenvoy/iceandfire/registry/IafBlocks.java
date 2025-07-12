package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.item.block.*;
import com.iafenvoy.iceandfire.item.block.util.IWallBlock;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class IafBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.BLOCK);

    public static final RegistrySupplier<Block> LECTERN = register("lectern", BlockLectern::new);
    public static final RegistrySupplier<Block> PODIUM_OAK = register("podium_oak", BlockPodium::new);
    public static final RegistrySupplier<Block> PODIUM_BIRCH = register("podium_birch", BlockPodium::new);
    public static final RegistrySupplier<Block> PODIUM_SPRUCE = register("podium_spruce", BlockPodium::new);
    public static final RegistrySupplier<Block> PODIUM_JUNGLE = register("podium_jungle", BlockPodium::new);
    public static final RegistrySupplier<Block> PODIUM_DARK_OAK = register("podium_dark_oak", BlockPodium::new);
    public static final RegistrySupplier<Block> PODIUM_ACACIA = register("podium_acacia", BlockPodium::new);
    public static final RegistrySupplier<Block> FIRE_LILY = register("fire_lily", BlockElementalFlower::new);
    public static final RegistrySupplier<Block> FROST_LILY = register("frost_lily", BlockElementalFlower::new);
    public static final RegistrySupplier<Block> LIGHTNING_LILY = register("lightning_lily", BlockElementalFlower::new);
    public static final RegistrySupplier<Block> GOLD_PILE = register("gold_pile", BlockGoldPile::new);
    public static final RegistrySupplier<Block> SILVER_PILE = register("silver_pile", BlockGoldPile::new);
    public static final RegistrySupplier<Block> COPPER_PILE = register("copper_pile", BlockGoldPile::new);
    public static final RegistrySupplier<Block> SILVER_ORE = register("silver_ore", () -> new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(3, 3).requiresTool()));
    public static final RegistrySupplier<Block> DEEPSLATE_SILVER_ORE = register("deepslate_silver_ore", () -> new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).strength(3, 3).requiresTool()));
    public static final RegistrySupplier<Block> SILVER_BLOCK = register("silver_block", () -> BlockGeneric.builder(3.0F, 5.0F, BlockSoundGroup.METAL, MapColor.IRON_GRAY, null, null, false));
    public static final RegistrySupplier<Block> SAPPHIRE_ORE = register("sapphire_ore", () -> new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(4, 3).requiresTool(), UniformIntProvider.create(3, 7)));
    public static final RegistrySupplier<Block> SAPPHIRE_BLOCK = register("sapphire_block", () -> BlockGeneric.builder(3.0F, 6.0F, BlockSoundGroup.METAL, MapColor.IRON_GRAY, null, null, false));
    public static final RegistrySupplier<Block> RAW_SILVER_BLOCK = register("raw_silver_block", () -> BlockGeneric.builder(3.0F, 5.0F, BlockSoundGroup.STONE, MapColor.IRON_GRAY, Instrument.BASEDRUM, null, false));
    public static final RegistrySupplier<Block> CHARRED_DIRT = register("chared_dirt", () -> BlockReturningState.builder(0.5F, 0.0F, BlockSoundGroup.GRAVEL, MapColor.DIRT_BROWN, null, null, false, Blocks.DIRT.getDefaultState()));
    public static final RegistrySupplier<Block> CHARRED_GRASS = register("chared_grass", () -> BlockReturningState.builder(0.6F, 0.0F, BlockSoundGroup.GRAVEL, MapColor.PALE_GREEN, null, null, false, Blocks.GRASS_BLOCK.getDefaultState()));
    public static final RegistrySupplier<Block> CHARRED_STONE = register("chared_stone", () -> BlockReturningState.builder(1.5F, 10.0F, BlockSoundGroup.STONE, MapColor.STONE_GRAY, Instrument.BASEDRUM, null, false, Blocks.STONE.getDefaultState()));
    public static final RegistrySupplier<Block> CHARRED_COBBLESTONE = register("chared_cobblestone", () -> BlockReturningState.builder(2F, 10.0F, BlockSoundGroup.STONE, MapColor.STONE_GRAY, Instrument.BASEDRUM, null, false, Blocks.COBBLESTONE.getDefaultState()));
    public static final RegistrySupplier<Block> CHARRED_GRAVEL = register("chared_gravel", () -> new BlockFallingReturningState(0.6F, 0F, BlockSoundGroup.GRAVEL, MapColor.DIRT_BROWN, Blocks.GRAVEL.getDefaultState()));
    public static final RegistrySupplier<Block> CHARRED_DIRT_PATH = register(BlockCharedPath.getNameFromType(0), () -> new BlockCharedPath(0));
    public static final RegistrySupplier<Block> ASH = register("ash", () -> BlockFallingGeneric.builder(0.5F, 0F, BlockSoundGroup.SAND, MapColor.PALE_YELLOW, Instrument.SNARE));
    public static final RegistrySupplier<Block> FROZEN_DIRT = register("frozen_dirt", () -> BlockReturningState.builder(0.5F, 0.0F, BlockSoundGroup.GLASS, true, MapColor.DIRT_BROWN, null, null, false, Blocks.DIRT.getDefaultState()));
    public static final RegistrySupplier<Block> FROZEN_GRASS = register("frozen_grass", () -> BlockReturningState.builder(0.6F, 0.0F, BlockSoundGroup.GLASS, true, MapColor.PALE_GREEN, null, null, false, Blocks.GRASS_BLOCK.getDefaultState()));
    public static final RegistrySupplier<Block> FROZEN_STONE = register("frozen_stone", () -> BlockReturningState.builder(1.5F, 1.0F, BlockSoundGroup.GLASS, true, MapColor.STONE_GRAY, Instrument.BASEDRUM, null, false, Blocks.STONE.getDefaultState()));
    public static final RegistrySupplier<Block> FROZEN_COBBLESTONE = register("frozen_cobblestone", () -> BlockReturningState.builder(2F, 2.0F, BlockSoundGroup.GLASS, true, MapColor.STONE_GRAY, Instrument.BASEDRUM, null, false, Blocks.COBBLESTONE.getDefaultState()));
    public static final RegistrySupplier<Block> FROZEN_GRAVEL = register("frozen_gravel", () -> new BlockFallingReturningState(0.6F, 0F, BlockSoundGroup.GLASS, true, MapColor.DIRT_BROWN, Blocks.GRAVEL.getDefaultState()));
    public static final RegistrySupplier<Block> FROZEN_DIRT_PATH = register(BlockCharedPath.getNameFromType(1), () -> new BlockCharedPath(1));
    public static final RegistrySupplier<Block> FROZEN_SPLINTERS = register("frozen_splinters", () -> BlockGeneric.builder(2.0F, 1.0F, BlockSoundGroup.GLASS, true, MapColor.OAK_TAN, Instrument.BASS, null, true));
    public static final RegistrySupplier<Block> DRAGON_ICE = register("dragon_ice", () -> BlockGeneric.builder(0.5F, 0F, BlockSoundGroup.GLASS, true, MapColor.PALE_PURPLE, null, null, false));
    public static final RegistrySupplier<Block> DRAGON_ICE_SPIKES = register("dragon_ice_spikes", BlockIceSpikes::new);
    public static final RegistrySupplier<Block> CRACKLED_DIRT = register("crackled_dirt", () -> BlockReturningState.builder(0.5F, 0.0F, BlockSoundGroup.GRAVEL, MapColor.DIRT_BROWN, null, null, false, Blocks.DIRT.getDefaultState()));
    public static final RegistrySupplier<Block> CRACKLED_GRASS = register("crackled_grass", () -> BlockReturningState.builder(0.6F, 0.0F, BlockSoundGroup.GRAVEL, MapColor.PALE_GREEN, null, null, false, Blocks.GRASS_BLOCK.getDefaultState()));
    public static final RegistrySupplier<Block> CRACKLED_STONE = register("crackled_stone", () -> BlockReturningState.builder(1.5F, 1.0F, BlockSoundGroup.STONE, MapColor.STONE_GRAY, Instrument.BASEDRUM, null, false, Blocks.STONE.getDefaultState()));
    public static final RegistrySupplier<Block> CRACKLED_COBBLESTONE = register("crackled_cobblestone", () -> BlockReturningState.builder(2F, 2F, BlockSoundGroup.STONE, MapColor.STONE_GRAY, Instrument.BASEDRUM, null, false, Blocks.COBBLESTONE.getDefaultState()));
    public static final RegistrySupplier<Block> CRACKLED_GRAVEL = register("crackled_gravel", () -> new BlockFallingReturningState(0.6F, 0F, BlockSoundGroup.GRAVEL, MapColor.DIRT_BROWN, Blocks.GRAVEL.getDefaultState()));
    public static final RegistrySupplier<Block> CRACKLED_DIRT_PATH = register(BlockCharedPath.getNameFromType(2), () -> new BlockCharedPath(2));

    public static final RegistrySupplier<Block> NEST = register("nest", () -> BlockGeneric.builder(0.5F, 0F, BlockSoundGroup.GRAVEL, false, MapColor.DARK_GREEN, null, PistonBehavior.DESTROY, false));

    public static final RegistrySupplier<Block> DRAGON_SCALE_RED = register("dragonscale_red", () -> new BlockDragonScales(DragonColor.RED));
    public static final RegistrySupplier<Block> DRAGON_SCALE_GREEN = register("dragonscale_green", () -> new BlockDragonScales(DragonColor.GREEN));
    public static final RegistrySupplier<Block> DRAGON_SCALE_BRONZE = register("dragonscale_bronze", () -> new BlockDragonScales(DragonColor.BRONZE));
    public static final RegistrySupplier<Block> DRAGON_SCALE_GRAY = register("dragonscale_gray", () -> new BlockDragonScales(DragonColor.GRAY));
    public static final RegistrySupplier<Block> DRAGON_SCALE_BLUE = register("dragonscale_blue", () -> new BlockDragonScales(DragonColor.BLUE));
    public static final RegistrySupplier<Block> DRAGON_SCALE_WHITE = register("dragonscale_white", () -> new BlockDragonScales(DragonColor.WHITE));
    public static final RegistrySupplier<Block> DRAGON_SCALE_SAPPHIRE = register("dragonscale_sapphire", () -> new BlockDragonScales(DragonColor.SAPPHIRE));
    public static final RegistrySupplier<Block> DRAGON_SCALE_SILVER = register("dragonscale_silver", () -> new BlockDragonScales(DragonColor.SILVER));
    public static final RegistrySupplier<Block> DRAGON_SCALE_ELECTRIC = register("dragonscale_electric", () -> new BlockDragonScales(DragonColor.ELECTRIC));
    public static final RegistrySupplier<Block> DRAGON_SCALE_amethyst = register("dragonscale_amethyst", () -> new BlockDragonScales(DragonColor.AMETHYST));
    public static final RegistrySupplier<Block> DRAGON_SCALE_COPPER = register("dragonscale_copper", () -> new BlockDragonScales(DragonColor.COPPER));
    public static final RegistrySupplier<Block> DRAGON_SCALE_BLACK = register("dragonscale_black", () -> new BlockDragonScales(DragonColor.BLACK));

    public static final RegistrySupplier<Block> DRAGON_BONE_BLOCK = register("dragon_bone_block", BlockDragonBone::new);
    public static final RegistrySupplier<Block> DRAGON_BONE_BLOCK_WALL = register("dragon_bone_wall", () -> new BlockDragonBoneWall(AbstractBlock.Settings.copy(IafBlocks.DRAGON_BONE_BLOCK.get())));
    public static final RegistrySupplier<Block> DRAGONFORGE_FIRE_BRICK = register(BlockDragonForgeBricks.name(0), () -> new BlockDragonForgeBricks(0));
    public static final RegistrySupplier<Block> DRAGONFORGE_ICE_BRICK = register(BlockDragonForgeBricks.name(1), () -> new BlockDragonForgeBricks(1));
    public static final RegistrySupplier<Block> DRAGONFORGE_LIGHTNING_BRICK = register(BlockDragonForgeBricks.name(2), () -> new BlockDragonForgeBricks(2));
    public static final RegistrySupplier<Block> DRAGONFORGE_FIRE_INPUT = register(BlockDragonForgeInput.name(0), () -> new BlockDragonForgeInput(0));
    public static final RegistrySupplier<Block> DRAGONFORGE_ICE_INPUT = register(BlockDragonForgeInput.name(1), () -> new BlockDragonForgeInput(1));
    public static final RegistrySupplier<Block> DRAGONFORGE_LIGHTNING_INPUT = register(BlockDragonForgeInput.name(2), () -> new BlockDragonForgeInput(2));
    public static final RegistrySupplier<Block> DRAGONFORGE_FIRE_CORE = register(BlockDragonForgeCore.name(0, true), () -> new BlockDragonForgeCore(0, true));
    public static final RegistrySupplier<Block> DRAGONFORGE_ICE_CORE = register(BlockDragonForgeCore.name(1, true), () -> new BlockDragonForgeCore(1, true));
    public static final RegistrySupplier<Block> DRAGONFORGE_LIGHTNING_CORE = register(BlockDragonForgeCore.name(2, true), () -> new BlockDragonForgeCore(2, true));
    public static final RegistrySupplier<Block> DRAGONFORGE_FIRE_CORE_DISABLED = register(BlockDragonForgeCore.name(0, false), () -> new BlockDragonForgeCore(0, false));
    public static final RegistrySupplier<Block> DRAGONFORGE_ICE_CORE_DISABLED = register(BlockDragonForgeCore.name(1, false), () -> new BlockDragonForgeCore(1, false));
    public static final RegistrySupplier<Block> DRAGONFORGE_LIGHTNING_CORE_DISABLED = register(BlockDragonForgeCore.name(2, false), () -> new BlockDragonForgeCore(2, false));
    public static final RegistrySupplier<Block> EGG_IN_ICE = register("egginice", BlockEggInIce::new);
    public static final RegistrySupplier<Block> PIXIE_HOUSE_MUSHROOM_RED = register(BlockPixieHouse.name("mushroom_red"), BlockPixieHouse::new);
    public static final RegistrySupplier<Block> PIXIE_HOUSE_MUSHROOM_BROWN = register(BlockPixieHouse.name("mushroom_brown"), BlockPixieHouse::new);
    public static final RegistrySupplier<Block> PIXIE_HOUSE_OAK = register(BlockPixieHouse.name("oak"), BlockPixieHouse::new);
    public static final RegistrySupplier<Block> PIXIE_HOUSE_BIRCH = register(BlockPixieHouse.name("birch"), BlockPixieHouse::new);
    public static final RegistrySupplier<Block> PIXIE_HOUSE_SPRUCE = register(BlockPixieHouse.name("spruce"), BlockPixieHouse::new);
    public static final RegistrySupplier<Block> PIXIE_HOUSE_DARK_OAK = register(BlockPixieHouse.name("dark_oak"), BlockPixieHouse::new);
    public static final RegistrySupplier<Block> JAR_EMPTY = register(BlockJar.name(-1), () -> new BlockJar(-1));
    public static final RegistrySupplier<Block> JAR_PIXIE_0 = register(BlockJar.name(0), () -> new BlockJar(0));
    public static final RegistrySupplier<Block> JAR_PIXIE_1 = register(BlockJar.name(1), () -> new BlockJar(1));
    public static final RegistrySupplier<Block> JAR_PIXIE_2 = register(BlockJar.name(2), () -> new BlockJar(2));
    public static final RegistrySupplier<Block> JAR_PIXIE_3 = register(BlockJar.name(3), () -> new BlockJar(3));
    public static final RegistrySupplier<Block> JAR_PIXIE_4 = register(BlockJar.name(4), () -> new BlockJar(4));
    public static final RegistrySupplier<Block> DRAGONSTEEL_FIRE_BLOCK = register("dragonsteel_fire_block", () -> BlockGeneric.builder(10.0F, 1000.0F, BlockSoundGroup.METAL, MapColor.IRON_GRAY, null, null, false));
    public static final RegistrySupplier<Block> DRAGONSTEEL_ICE_BLOCK = register("dragonsteel_ice_block", () -> BlockGeneric.builder(10.0F, 1000.0F, BlockSoundGroup.METAL, MapColor.IRON_GRAY, null, null, false));
    public static final RegistrySupplier<Block> DRAGONSTEEL_LIGHTNING_BLOCK = register("dragonsteel_lightning_block", () -> BlockGeneric.builder(10.0F, 1000.0F, BlockSoundGroup.METAL, MapColor.IRON_GRAY, null, null, false));
    public static final RegistrySupplier<BlockDreadBase> DREAD_STONE = register("dread_stone", () -> new BlockDreadBase(false));
    public static final RegistrySupplier<BlockDreadBase> DREAD_STONE_BRICKS = register("dread_stone_bricks", () -> new BlockDreadBase(false));
    public static final RegistrySupplier<Block> DREAD_STONE_BRICKS_STAIRS = register("dread_stone_stairs", () -> new BlockDreadStairs(DREAD_STONE_BRICKS.get().getDefaultState(), AbstractBlock.Settings.create().strength(-1.0F, 3600000.0F)));
    public static final RegistrySupplier<BlockDreadBase> DREAD_STONE_BRICKS_CHISELED = register("dread_stone_bricks_chiseled", () -> new BlockDreadBase(false));
    public static final RegistrySupplier<BlockDreadBase> DREAD_STONE_BRICKS_CRACKED = register("dread_stone_bricks_cracked", () -> new BlockDreadBase(false));
    public static final RegistrySupplier<BlockDreadBase> DREAD_STONE_BRICKS_MOSSY = register("dread_stone_bricks_mossy", () -> new BlockDreadBase(false));
    public static final RegistrySupplier<BlockDreadBase> DREAD_STONE_TILE = register("dread_stone_tile", () -> new BlockDreadBase(false));
    public static final RegistrySupplier<Block> DREAD_STONE_FACE = register("dread_stone_face", BlockDreadStoneFace::new);
    public static final RegistrySupplier<BlockDreadTorchWall> DREAD_TORCH_WALL = registerWallTorch("dread_torch_wall", BlockDreadTorchWall::new);
    public static final RegistrySupplier<TorchBlock> DREAD_TORCH = registerWallBlock("dread_torch", BlockDreadTorch::new);
    public static final RegistrySupplier<Block> DREAD_STONE_BRICKS_SLAB = register("dread_stone_slab", () -> new BlockDreadSlab(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(10F, 10000F)));
    public static final RegistrySupplier<Block> DREADWOOD_LOG = register("dreadwood_log", BlockDreadWoodLog::new);
    public static final RegistrySupplier<BlockDreadBase> DREADWOOD_PLANKS = register("dreadwood_planks", () -> new BlockDreadBase(true));
    public static final RegistrySupplier<Block> DREADWOOD_LEAVES = register("dreadwood_leaves", () -> Blocks.createLeavesBlock(BlockSoundGroup.GRASS));
    public static final RegistrySupplier<Block> DREADWOOD_SAPLING = register("dreadwood_sapling", DreadwoodSaplingBlock::new);
    public static final RegistrySupplier<Block> DREADWOOD_PLANKS_LOCK = register("dreadwood_planks_lock", BlockDreadWoodLock::new);
    public static final RegistrySupplier<Block> DREAD_PORTAL = register("dread_portal", BlockDreadPortal::new);
    public static final RegistrySupplier<Block> DREAD_SPAWNER = register("dread_spawner", BlockDreadSpawner::new);
    public static final RegistrySupplier<BlockBurntTorchWall> BURNT_TORCH_WALL = registerWallTorch("burnt_torch_wall", BlockBurntTorchWall::new);
    public static final RegistrySupplier<TorchBlock> BURNT_TORCH = registerWallBlock("burnt_torch", BlockBurntTorch::new);
    public static final RegistrySupplier<Block> GHOST_CHEST = register("ghost_chest", BlockGhostChest::new);
    public static final RegistrySupplier<Block> GRAVEYARD_SOIL = register("graveyard_soil", BlockGraveyardSoil::new);

    public static <T extends Block> RegistrySupplier<T> register(String name, Supplier<T> block) {
        RegistrySupplier<T> r = REGISTRY.register(name, block);
        IafItems.register(name, () -> new BlockItem(r.get(), new Item.Settings()), false);
        IafItemGroups.TAB_BLOCKS_LIST.add(r::get);
        return r;
    }

    private static <T extends TorchBlock> RegistrySupplier<T> registerWallBlock(String name, Supplier<T> block) {
        RegistrySupplier<T> r = REGISTRY.register(name, block);
        IafItems.register(name, () -> new VerticallyAttachableBlockItem(r.get(), ((IWallBlock) r.get()).wallBlock(), new Item.Settings(), Direction.DOWN), false);
        IafItemGroups.TAB_BLOCKS_LIST.add(r::get);
        return r;
    }

    private static <T extends WallTorchBlock> RegistrySupplier<T> registerWallTorch(String name, Supplier<T> block) {
        return REGISTRY.register(name, block);
    }
}
