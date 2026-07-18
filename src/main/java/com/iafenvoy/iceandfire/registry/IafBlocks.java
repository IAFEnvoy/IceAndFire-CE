package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.block.*;
import com.iafenvoy.iceandfire.item.block.LecternBlock;
import com.iafenvoy.iceandfire.item.block.util.WallBlock;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

//FIXME::Specific vararg and remove .get()
@SuppressWarnings("unused")
public final class IafBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(IceAndFire.MOD_ID);

    public static final DeferredBlock<Block> LECTERN = register("lectern", LecternBlock::new);
    public static final DeferredBlock<Block> PODIUM_OAK = register("podium_oak", PodiumBlock::new);
    public static final DeferredBlock<Block> PODIUM_BIRCH = register("podium_birch", PodiumBlock::new);
    public static final DeferredBlock<Block> PODIUM_SPRUCE = register("podium_spruce", PodiumBlock::new);
    public static final DeferredBlock<Block> PODIUM_JUNGLE = register("podium_jungle", PodiumBlock::new);
    public static final DeferredBlock<Block> PODIUM_DARK_OAK = register("podium_dark_oak", PodiumBlock::new);
    public static final DeferredBlock<Block> PODIUM_ACACIA = register("podium_acacia", PodiumBlock::new);
    public static final DeferredBlock<Block> FIRE_LILY = register("fire_lily", ElementalFlowerBlock::new);
    public static final DeferredBlock<Block> FROST_LILY = register("frost_lily", ElementalFlowerBlock::new);
    public static final DeferredBlock<Block> LIGHTNING_LILY = register("lightning_lily", ElementalFlowerBlock::new);
    public static final DeferredBlock<Block> GOLD_PILE = register("gold_pile", PileBlock::new);
    public static final DeferredBlock<Block> SILVER_PILE = register("silver_pile", PileBlock::new);
    public static final DeferredBlock<Block> COPPER_PILE = register("copper_pile", PileBlock::new);
    public static final DeferredBlock<Block> SILVER_ORE = register("silver_ore", () -> new DropExperienceBlock(ConstantInt.of(2), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3, 3).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DEEPSLATE_SILVER_ORE = register("deepslate_silver_ore", () -> new DropExperienceBlock(ConstantInt.of(2), BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).strength(3, 3).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SILVER_BLOCK = register("silver_block", () -> GenericBlock.builder(3.0F, 5.0F, SoundType.METAL, MapColor.METAL, null, null, false));
    public static final DeferredBlock<Block> SAPPHIRE_ORE = register("sapphire_ore", () -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(4, 3).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SAPPHIRE_BLOCK = register("sapphire_block", () -> GenericBlock.builder(3.0F, 6.0F, SoundType.METAL, MapColor.METAL, null, null, false));
    public static final DeferredBlock<Block> RAW_SILVER_BLOCK = register("raw_silver_block", () -> GenericBlock.builder(3.0F, 5.0F, SoundType.STONE, MapColor.METAL, NoteBlockInstrument.BASEDRUM, null, false));
    public static final DeferredBlock<Block> CHARRED_DIRT = register("chared_dirt", () -> ReturningStateBlock.builder(0.5F, 0.0F, SoundType.GRAVEL, MapColor.DIRT, null, null, false, Blocks.DIRT.defaultBlockState()));
    public static final DeferredBlock<Block> CHARRED_GRASS = register("chared_grass", () -> ReturningStateBlock.builder(0.6F, 0.0F, SoundType.GRAVEL, MapColor.GRASS, null, null, false, Blocks.GRASS_BLOCK.defaultBlockState()));
    public static final DeferredBlock<Block> CHARRED_STONE = register("chared_stone", () -> ReturningStateBlock.builder(1.5F, 10.0F, SoundType.STONE, MapColor.STONE, NoteBlockInstrument.BASEDRUM, null, false, Blocks.STONE.defaultBlockState()));
    public static final DeferredBlock<Block> CHARRED_COBBLESTONE = register("chared_cobblestone", () -> ReturningStateBlock.builder(2F, 10.0F, SoundType.STONE, MapColor.STONE, NoteBlockInstrument.BASEDRUM, null, false, Blocks.COBBLESTONE.defaultBlockState()));
    public static final DeferredBlock<Block> CHARRED_GRAVEL = register("chared_gravel", () -> new FallingReturningStateBlock(0.6F, 0F, SoundType.GRAVEL, MapColor.DIRT, Blocks.GRAVEL.defaultBlockState()));
    public static final DeferredBlock<Block> CHARRED_DIRT_PATH = register(CharedPathBlock.getNameFromType(0), () -> new CharedPathBlock(0));
    public static final DeferredBlock<Block> ASH = register("ash", () -> FallingGenericBlock.builder(0.5F, 0F, SoundType.SAND, MapColor.SAND, NoteBlockInstrument.SNARE));
    public static final DeferredBlock<Block> FROZEN_DIRT = register("frozen_dirt", () -> ReturningStateBlock.builder(0.5F, 0.0F, SoundType.GLASS, true, MapColor.DIRT, null, null, false, Blocks.DIRT.defaultBlockState()));
    public static final DeferredBlock<Block> FROZEN_GRASS = register("frozen_grass", () -> ReturningStateBlock.builder(0.6F, 0.0F, SoundType.GLASS, true, MapColor.GRASS, null, null, false, Blocks.GRASS_BLOCK.defaultBlockState()));
    public static final DeferredBlock<Block> FROZEN_STONE = register("frozen_stone", () -> ReturningStateBlock.builder(1.5F, 1.0F, SoundType.GLASS, true, MapColor.STONE, NoteBlockInstrument.BASEDRUM, null, false, Blocks.STONE.defaultBlockState()));
    public static final DeferredBlock<Block> FROZEN_COBBLESTONE = register("frozen_cobblestone", () -> ReturningStateBlock.builder(2F, 2.0F, SoundType.GLASS, true, MapColor.STONE, NoteBlockInstrument.BASEDRUM, null, false, Blocks.COBBLESTONE.defaultBlockState()));
    public static final DeferredBlock<Block> FROZEN_GRAVEL = register("frozen_gravel", () -> new FallingReturningStateBlock(0.6F, 0F, SoundType.GLASS, true, MapColor.DIRT, Blocks.GRAVEL.defaultBlockState()));
    public static final DeferredBlock<Block> FROZEN_DIRT_PATH = register(CharedPathBlock.getNameFromType(1), () -> new CharedPathBlock(1));
    public static final DeferredBlock<Block> FROZEN_SPLINTERS = register("frozen_splinters", () -> GenericBlock.builder(2.0F, 1.0F, SoundType.GLASS, true, MapColor.WOOD, NoteBlockInstrument.BASS, null, true));
    public static final DeferredBlock<Block> DRAGON_ICE = register("dragon_ice", () -> GenericBlock.builder(0.5F, 0F, SoundType.GLASS, true, MapColor.ICE, null, null, false));
    public static final DeferredBlock<Block> DRAGON_ICE_SPIKES = register("dragon_ice_spikes", IceSpikesBlock::new);
    public static final DeferredBlock<Block> CRACKLED_DIRT = register("crackled_dirt", () -> ReturningStateBlock.builder(0.5F, 0.0F, SoundType.GRAVEL, MapColor.DIRT, null, null, false, Blocks.DIRT.defaultBlockState()));
    public static final DeferredBlock<Block> CRACKLED_GRASS = register("crackled_grass", () -> ReturningStateBlock.builder(0.6F, 0.0F, SoundType.GRAVEL, MapColor.GRASS, null, null, false, Blocks.GRASS_BLOCK.defaultBlockState()));
    public static final DeferredBlock<Block> CRACKLED_STONE = register("crackled_stone", () -> ReturningStateBlock.builder(1.5F, 1.0F, SoundType.STONE, MapColor.STONE, NoteBlockInstrument.BASEDRUM, null, false, Blocks.STONE.defaultBlockState()));
    public static final DeferredBlock<Block> CRACKLED_COBBLESTONE = register("crackled_cobblestone", () -> ReturningStateBlock.builder(2F, 2F, SoundType.STONE, MapColor.STONE, NoteBlockInstrument.BASEDRUM, null, false, Blocks.COBBLESTONE.defaultBlockState()));
    public static final DeferredBlock<Block> CRACKLED_GRAVEL = register("crackled_gravel", () -> new FallingReturningStateBlock(0.6F, 0F, SoundType.GRAVEL, MapColor.DIRT, Blocks.GRAVEL.defaultBlockState()));
    public static final DeferredBlock<Block> CRACKLED_DIRT_PATH = register(CharedPathBlock.getNameFromType(2), () -> new CharedPathBlock(2));

    public static final DeferredBlock<Block> NEST = register("nest", () -> GenericBlock.builder(0.5F, 0F, SoundType.GRAVEL, false, MapColor.PLANT, null, PushReaction.DESTROY, false));

    public static final DeferredBlock<Block> DRAGON_SCALE_RED = register("dragonscale_red", () -> new DragonScalesBlock(IafDragonColors.RED));
    public static final DeferredBlock<Block> DRAGON_SCALE_GREEN = register("dragonscale_green", () -> new DragonScalesBlock(IafDragonColors.GREEN));
    public static final DeferredBlock<Block> DRAGON_SCALE_BRONZE = register("dragonscale_bronze", () -> new DragonScalesBlock(IafDragonColors.BRONZE));
    public static final DeferredBlock<Block> DRAGON_SCALE_GRAY = register("dragonscale_gray", () -> new DragonScalesBlock(IafDragonColors.GRAY));
    public static final DeferredBlock<Block> DRAGON_SCALE_BLUE = register("dragonscale_blue", () -> new DragonScalesBlock(IafDragonColors.BLUE));
    public static final DeferredBlock<Block> DRAGON_SCALE_WHITE = register("dragonscale_white", () -> new DragonScalesBlock(IafDragonColors.WHITE));
    public static final DeferredBlock<Block> DRAGON_SCALE_SAPPHIRE = register("dragonscale_sapphire", () -> new DragonScalesBlock(IafDragonColors.SAPPHIRE));
    public static final DeferredBlock<Block> DRAGON_SCALE_SILVER = register("dragonscale_silver", () -> new DragonScalesBlock(IafDragonColors.SILVER));
    public static final DeferredBlock<Block> DRAGON_SCALE_ELECTRIC = register("dragonscale_electric", () -> new DragonScalesBlock(IafDragonColors.ELECTRIC));
    public static final DeferredBlock<Block> DRAGON_SCALE_amethyst = register("dragonscale_amethyst", () -> new DragonScalesBlock(IafDragonColors.AMETHYST));
    public static final DeferredBlock<Block> DRAGON_SCALE_COPPER = register("dragonscale_copper", () -> new DragonScalesBlock(IafDragonColors.COPPER));
    public static final DeferredBlock<Block> DRAGON_SCALE_BLACK = register("dragonscale_black", () -> new DragonScalesBlock(IafDragonColors.BLACK));

    public static final DeferredBlock<Block> DRAGON_BONE_BLOCK = register("dragon_bone_block", DragonBoneBlock::new);
    public static final DeferredBlock<Block> DRAGON_BONE_BLOCK_WALL = register("dragon_bone_wall", () -> new DragonBoneWallBlock(BlockBehaviour.Properties.ofFullCopy(IafBlocks.DRAGON_BONE_BLOCK.get())));
    public static final DeferredBlock<Block> DRAGONFORGE_FIRE_BRICK = forgeBrick(IafDragonTypes.FIRE);
    public static final DeferredBlock<Block> DRAGONFORGE_ICE_BRICK = forgeBrick(IafDragonTypes.ICE);
    public static final DeferredBlock<Block> DRAGONFORGE_LIGHTNING_BRICK = forgeBrick(IafDragonTypes.LIGHTNING);
    public static final DeferredBlock<Block> DRAGONFORGE_FIRE_INPUT = forgeInput(IafDragonTypes.FIRE);
    public static final DeferredBlock<Block> DRAGONFORGE_ICE_INPUT = forgeInput(IafDragonTypes.ICE);
    public static final DeferredBlock<Block> DRAGONFORGE_LIGHTNING_INPUT = forgeInput(IafDragonTypes.LIGHTNING);
    public static final DeferredBlock<Block> DRAGONFORGE_FIRE_CORE = forgeCore(IafDragonTypes.FIRE, true);
    public static final DeferredBlock<Block> DRAGONFORGE_ICE_CORE = forgeCore(IafDragonTypes.ICE, true);
    public static final DeferredBlock<Block> DRAGONFORGE_LIGHTNING_CORE = forgeCore(IafDragonTypes.LIGHTNING, true);
    public static final DeferredBlock<Block> DRAGONFORGE_FIRE_CORE_DISABLED = forgeCore(IafDragonTypes.FIRE, false);
    public static final DeferredBlock<Block> DRAGONFORGE_ICE_CORE_DISABLED = forgeCore(IafDragonTypes.ICE, false);
    public static final DeferredBlock<Block> DRAGONFORGE_LIGHTNING_CORE_DISABLED = forgeCore(IafDragonTypes.LIGHTNING, false);
    public static final DeferredBlock<Block> EGG_IN_ICE = register("egginice", EggInIceBlock::new);
    public static final DeferredBlock<Block> PIXIE_HOUSE_MUSHROOM_RED = register(PixieHouseBlock.name("mushroom_red"), PixieHouseBlock::new);
    public static final DeferredBlock<Block> PIXIE_HOUSE_MUSHROOM_BROWN = register(PixieHouseBlock.name("mushroom_brown"), PixieHouseBlock::new);
    public static final DeferredBlock<Block> PIXIE_HOUSE_OAK = register(PixieHouseBlock.name("oak"), PixieHouseBlock::new);
    public static final DeferredBlock<Block> PIXIE_HOUSE_BIRCH = register(PixieHouseBlock.name("birch"), PixieHouseBlock::new);
    public static final DeferredBlock<Block> PIXIE_HOUSE_SPRUCE = register(PixieHouseBlock.name("spruce"), PixieHouseBlock::new);
    public static final DeferredBlock<Block> PIXIE_HOUSE_DARK_OAK = register(PixieHouseBlock.name("dark_oak"), PixieHouseBlock::new);
    public static final DeferredBlock<Block> JAR_EMPTY = register(JarBlock.name(-1), () -> new JarBlock(-1));
    public static final DeferredBlock<Block> JAR_PIXIE_0 = register(JarBlock.name(0), () -> new JarBlock(0));
    public static final DeferredBlock<Block> JAR_PIXIE_1 = register(JarBlock.name(1), () -> new JarBlock(1));
    public static final DeferredBlock<Block> JAR_PIXIE_2 = register(JarBlock.name(2), () -> new JarBlock(2));
    public static final DeferredBlock<Block> JAR_PIXIE_3 = register(JarBlock.name(3), () -> new JarBlock(3));
    public static final DeferredBlock<Block> JAR_PIXIE_4 = register(JarBlock.name(4), () -> new JarBlock(4));
    public static final DeferredBlock<Block> DRAGONSTEEL_FIRE_BLOCK = register("dragonsteel_fire_block", () -> GenericBlock.builder(10.0F, 1000.0F, SoundType.METAL, MapColor.METAL, null, null, false));
    public static final DeferredBlock<Block> DRAGONSTEEL_ICE_BLOCK = register("dragonsteel_ice_block", () -> GenericBlock.builder(10.0F, 1000.0F, SoundType.METAL, MapColor.METAL, null, null, false));
    public static final DeferredBlock<Block> DRAGONSTEEL_LIGHTNING_BLOCK = register("dragonsteel_lightning_block", () -> GenericBlock.builder(10.0F, 1000.0F, SoundType.METAL, MapColor.METAL, null, null, false));
    public static final DeferredBlock<DreadBaseBlock> DREAD_STONE = register("dread_stone", () -> new DreadBaseBlock(false));
    public static final DeferredBlock<DreadBaseBlock> DREAD_STONE_BRICKS = register("dread_stone_bricks", () -> new DreadBaseBlock(false));
    public static final DeferredBlock<Block> DREAD_STONE_BRICKS_STAIRS = register("dread_stone_stairs", () -> new DreadStairsBlock(DREAD_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F)));
    public static final DeferredBlock<DreadBaseBlock> DREAD_STONE_BRICKS_CHISELED = register("dread_stone_bricks_chiseled", () -> new DreadBaseBlock(false));
    public static final DeferredBlock<DreadBaseBlock> DREAD_STONE_BRICKS_CRACKED = register("dread_stone_bricks_cracked", () -> new DreadBaseBlock(false));
    public static final DeferredBlock<DreadBaseBlock> DREAD_STONE_BRICKS_MOSSY = register("dread_stone_bricks_mossy", () -> new DreadBaseBlock(false));
    public static final DeferredBlock<DreadBaseBlock> DREAD_STONE_TILE = register("dread_stone_tile", () -> new DreadBaseBlock(false));
    public static final DeferredBlock<Block> DREAD_STONE_FACE = register("dread_stone_face", DreadStoneFaceBlock::new);
    public static final DeferredBlock<DreadTorchWallBlock> DREAD_TORCH_WALL = registerWallTorch("dread_torch_wall", DreadTorchWallBlock::new);
    public static final DeferredBlock<TorchBlock> DREAD_TORCH = registerWallBlock("dread_torch", DreadTorchBlock::new);
    public static final DeferredBlock<Block> DREAD_STONE_BRICKS_SLAB = register("dread_stone_slab", () -> new DreadSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10F, 10000F)));
    public static final DeferredBlock<Block> DREADWOOD_LOG = register("dreadwood_log", DreadWoodLogBlock::new);
    public static final DeferredBlock<DreadBaseBlock> DREADWOOD_PLANKS = register("dreadwood_planks", () -> new DreadBaseBlock(true));
    public static final DeferredBlock<Block> DREADWOOD_LEAVES = register("dreadwood_leaves", () -> Blocks.leaves(SoundType.GRASS));
    public static final DeferredBlock<Block> DREADWOOD_SAPLING = register("dreadwood_sapling", DreadwoodSaplingBlock::new);
    public static final DeferredBlock<Block> DREADWOOD_PLANKS_LOCK = register("dreadwood_planks_lock", DreadWoodLockBlock::new);
    public static final DeferredBlock<Block> DREAD_SPAWNER = register("dread_spawner", DreadSpawnerBlock::new);
    public static final DeferredBlock<BurntTorchWallBlock> BURNT_TORCH_WALL = registerWallTorch("burnt_torch_wall", BurntTorchWallBlock::new);
    public static final DeferredBlock<TorchBlock> BURNT_TORCH = registerWallBlock("burnt_torch", BurntTorchBlock::new);
    public static final DeferredBlock<Block> GHOST_CHEST = register("ghost_chest", GhostChestBlock::new);
    public static final DeferredBlock<Block> GRAVEYARD_SOIL = register("graveyard_soil", GraveyardSoilBlock::new);

    public static DeferredBlock<Block> forgeBrick(DragonType type) {
        return register(DragonForgeBrickBlock.name(type), () -> new DragonForgeBrickBlock(type));
    }

    public static DeferredBlock<Block> forgeInput(DragonType type) {
        return register(DragonForgeInputBlock.name(type), () -> new DragonForgeInputBlock(type));
    }

    public static DeferredBlock<Block> forgeCore(DragonType type, boolean activated) {
        return register(DragonForgeCoreBlock.name(type, activated), () -> new DragonForgeCoreBlock(type, activated));
    }

    public static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> block) {
        DeferredBlock<T> r = REGISTRY.register(name, block);
        IafItems.registerBlock(name, () -> new BlockItem(r.get(), new Item.Properties()));
        return r;
    }

    private static <T extends TorchBlock> DeferredBlock<T> registerWallBlock(String name, Supplier<T> block) {
        DeferredBlock<T> r = REGISTRY.register(name, block);
        IafItems.registerBlock(name, () -> new StandingAndWallBlockItem(r.get(), ((WallBlock) r.get()).wallBlock(), new Item.Properties(), Direction.DOWN));
        return r;
    }

    private static <T extends WallTorchBlock> DeferredBlock<T> registerWallTorch(String name, Supplier<T> block) {
        return REGISTRY.register(name, block);
    }
}
