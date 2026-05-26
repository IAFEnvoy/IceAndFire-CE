package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.HomePosition;
import com.iafenvoy.iceandfire.item.block.PileBlock;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.uranus.util.RandomHelper;
import com.iafenvoy.uranus.util.ShapeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class DragonCaveStructure extends Structure implements DangerousGeneration {
    protected DragonCaveStructure(StructureSettings config) {
        super(config);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (context.random().nextDouble() >= this.getGenerateChance())
            return Optional.empty();
        Rotation blockRotation = Rotation.getRandom(context.random());
        BlockPos blockPos = this.getLowestYIn5by5BoxOffset7Blocks(context, blockRotation);
        if (!this.isFarEnoughFromSpawn(blockPos) || blockPos.getY() <= context.heightAccessor().getMinBuildHeight() + 2)
            return Optional.empty();
        return Optional.of(new GenerationStub(blockPos, collector -> this.addPieces(collector, blockPos, context, context.random().nextBoolean())));
    }

    private void addPieces(StructurePiecesBuilder collector, BlockPos pos, GenerationContext context, boolean male) {
        int y = context.heightAccessor().getMinBuildHeight() + 40 + context.random().nextInt(30);
        long seed = context.random().nextLong();
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                collector.addPiece(this.createPiece(new BoundingBox(pos.getX() + i * 32, y - 12, pos.getZ() + j * 32, pos.getX() + i * 32, y + 12, pos.getZ() + j * 32), male, new BlockPos(i * 32, 0, j * 32), y, seed));
    }

    protected abstract DragonCavePiece createPiece(BoundingBox boundingBox, boolean male, BlockPos offset, int y, long seed);

    protected abstract double getGenerateChance();

    protected abstract static class DragonCavePiece extends StructurePiece {
        private final boolean male;
        private final BlockPos offset;
        private final int y;
        private final long seed;

        protected DragonCavePiece(StructurePieceType type, int length, BoundingBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
            super(type, length, boundingBox);
            this.male = male;
            this.offset = offset;
            this.y = y;
            this.seed = seed;
        }

        public DragonCavePiece(StructurePieceType type, CompoundTag nbt) {
            super(type, nbt);
            this.male = nbt.getBoolean("male");
            this.offset = BlockPos.of(nbt.getLong("offset"));
            this.y = nbt.getInt("down");
            this.seed = nbt.getLong("seed");
        }

        @Override
        protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, CompoundTag nbt) {
            nbt.putBoolean("male", this.male);
            nbt.putLong("offset", this.offset.asLong());
            nbt.putInt("down", this.y);
            nbt.putLong("seed", this.seed);
        }

        @Override
        public void postProcess(@NotNull WorldGenLevel world, @NotNull StructureManager structureAccessor, @NotNull ChunkGenerator chunkGenerator, @NotNull RandomSource random, @NotNull BoundingBox chunkBox, @NotNull ChunkPos chunkPos, @NotNull BlockPos pivot) {
            if (super.boundingBox.getXSpan() > 1)
                return;
            BlockPos center = new BlockPos(super.boundingBox.minX(), super.boundingBox.minY(), super.boundingBox.minZ()).subtract(this.offset);
            BlockPos bb_pos = center.offset(new BlockPos((this.offset.getX() * 24) / 32, 0, (this.offset.getZ() * 24) / 32));
            super.boundingBox = new BoundingBox(bb_pos.getX() - 12, super.boundingBox.minY(), bb_pos.getZ() - 12,
                    bb_pos.getX() + 11, super.boundingBox.maxY(), bb_pos.getZ() + 11);

            random = new LegacyRandomSource(this.seed);
            // Center the position at the "middle" of the chunk
            BlockPos position = new BlockPos((chunkPos.x << 4) + 8, this.y, (chunkPos.z << 4) + 8).subtract(this.offset);
            int dragonAge = 75 + random.nextInt(50);
            int radius = (int) (dragonAge * 0.2F) + random.nextInt(4);
            this.generateCave(world, radius, 3, position, random);
            if (this.offset.equals(new BlockPos(0, 0, 0))) {
                DragonBaseEntity dragon = this.createDragon(world, random, position, dragonAge);
                world.addFreshEntity(dragon);
            }
        }

        private boolean isOutOfRange(ChunkPos chunkPos, BlockPos blockPos) {
            return chunkPos.getMinBlockX() - 16 > blockPos.getX() || blockPos.getX() > chunkPos.getMaxBlockX() + 16 ||
                    chunkPos.getMinBlockZ() - 16 > blockPos.getZ() || blockPos.getZ() > chunkPos.getMaxBlockZ() + 16;
        }

        public void generateCave(LevelAccessor worldIn, int radius, int amount, BlockPos center, RandomSource random) {
            List<SphereInfo> sphereList = new ArrayList<>();
            sphereList.add(new SphereInfo(radius, center.immutable()));
            Stream<BlockPos> sphereBlocks = ShapeBuilder.start().getAllInCutOffSphereMutable(radius, radius / 2, center).toStream(false);
            Stream<BlockPos> hollowBlocks = ShapeBuilder.start().getAllInRandomlyDistributedRangeYCutOffSphereMutable(radius - 2, (int) ((radius - 2) * 0.75), (radius - 2) / 2, random, center).toStream(false);
            //Get shells
            //Get hollows
            for (int i = 0; i < amount + random.nextInt(2); i++) {
                Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                int r = 2 * (int) (radius / 3F) + random.nextInt(8);
                BlockPos centerOffset = center.relative(direction, radius - 2);
                sphereBlocks = Stream.concat(sphereBlocks, ShapeBuilder.start().getAllInCutOffSphereMutable(r, r, centerOffset).toStream(false));
                hollowBlocks = Stream.concat(hollowBlocks, ShapeBuilder.start().getAllInRandomlyDistributedRangeYCutOffSphereMutable(r - 2, (int) ((r - 2) * 0.75), (r - 2) / 2, random, centerOffset).toStream(false));
                sphereList.add(new SphereInfo(r, centerOffset));
            }
            Set<BlockPos> shellBlocksSet = sphereBlocks.map(BlockPos::immutable).collect(Collectors.toSet());
            Set<BlockPos> hollowBlocksSet = hollowBlocks.map(BlockPos::immutable).collect(Collectors.toSet());
            shellBlocksSet.removeAll(hollowBlocksSet);

            //Remove blocks that is not belong to this piece
            ChunkPos chunkPos = new ChunkPos(center.offset(this.offset));
            shellBlocksSet.removeIf(x -> this.isOutOfRange(chunkPos, x));
            hollowBlocksSet.removeIf(x -> this.isOutOfRange(chunkPos, x));

            //setBlocks
            this.createShell(worldIn, random, shellBlocksSet);
            //removeBlocks
            this.hollowOut(worldIn, hollowBlocksSet);
            //decorate
            this.decorateCave(worldIn, random, hollowBlocksSet, sphereList, center);
            sphereList.clear();
        }

        public void createShell(LevelAccessor worldIn, RandomSource rand, Set<BlockPos> positions) {
            List<Block> rareOres = this.getBlockList(IafBlockTags.DRAGON_CAVE_RARE_ORES);
            List<Block> uncommonOres = this.getBlockList(IafBlockTags.DRAGON_CAVE_UNCOMMON_ORES);
            List<Block> commonOres = this.getBlockList(IafBlockTags.DRAGON_CAVE_COMMON_ORES);
            List<Block> dragonTypeOres = this.getBlockList(this.getOreTag());
            positions.forEach(blockPos -> {
                if (!(worldIn.getBlockState(blockPos).getBlock() instanceof BaseEntityBlock) && worldIn.getBlockState(blockPos).getDestroySpeed(worldIn, blockPos) >= 0) {
                    boolean doOres = rand.nextDouble() < IafCommonConfig.INSTANCE.dragon.generateOreRatio.getValue();
                    if (doOres) {
                        Block toPlace = null;
                        if (rand.nextBoolean())
                            toPlace = !dragonTypeOres.isEmpty() ? dragonTypeOres.get(rand.nextInt(dragonTypeOres.size())) : null;
                        else {
                            double chance = rand.nextDouble();
                            if (!rareOres.isEmpty() && chance <= 0.15)
                                toPlace = rareOres.get(rand.nextInt(rareOres.size()));
                            else if (!uncommonOres.isEmpty() && chance <= 0.45)
                                toPlace = uncommonOres.get(rand.nextInt(uncommonOres.size()));
                            else if (!commonOres.isEmpty())
                                toPlace = commonOres.get(rand.nextInt(commonOres.size()));
                        }
                        if (toPlace != null)
                            worldIn.setBlock(blockPos, toPlace.defaultBlockState(), Block.UPDATE_CLIENTS);
                        else
                            worldIn.setBlock(blockPos, this.getPaletteBlock(rand), Block.UPDATE_CLIENTS);
                    } else
                        worldIn.setBlock(blockPos, this.getPaletteBlock(rand), Block.UPDATE_CLIENTS);
                }
            });
        }

        private List<Block> getBlockList(final TagKey<Block> tagKey) {
            return BuiltInRegistries.BLOCK.getTag(tagKey).map(holders -> holders.stream().map(Holder::value).toList()).orElse(Collections.emptyList());
        }

        public void hollowOut(LevelAccessor worldIn, Set<BlockPos> positions) {
            positions.forEach(blockPos -> {
                if (!(worldIn.getBlockState(blockPos).getBlock() instanceof BaseEntityBlock))
                    worldIn.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            });
        }

        public void decorateCave(LevelAccessor worldIn, RandomSource random, Set<BlockPos> positions, List<SphereInfo> spheres, BlockPos center) {
            for (SphereInfo sphere : spheres) {
                BlockPos pos = sphere.pos();
                int radius = sphere.radius();
                for (int i = 0; i < 15 + random.nextInt(10); i++)
                    this.getCeilingDecoration().generate(worldIn, random, pos.above(radius / 2 - 1).offset(random.nextInt(radius) - radius / 2, 0, random.nextInt(radius) - radius / 2));
            }

            positions.forEach(blockPos -> {
                if (blockPos.getY() < center.getY()) {
                    BlockState stateBelow = worldIn.getBlockState(blockPos.below());
                    if ((stateBelow.is(BlockTags.BASE_STONE_OVERWORLD) || stateBelow.is(IafBlockTags.DRAGON_ENVIRONMENT_BLOCKS)) && worldIn.getBlockState(blockPos).isAir())
                        this.setGoldPile(worldIn, blockPos, random);
                }
            });
        }

        public void setGoldPile(LevelAccessor world, BlockPos pos, RandomSource random) {
            if (!(world.getBlockState(pos).getBlock() instanceof BaseEntityBlock)) {
                int chance = random.nextInt(99) + 1;
                if (chance < 60) {
                    boolean generateGold = random.nextDouble() < IafCommonConfig.INSTANCE.dragon.generateDenGoldChance.getValue() * (this.male ? 1 : 2);
                    world.setBlock(pos, generateGold ? this.getTreasurePile().setValue(PileBlock.LAYERS, 1 + random.nextInt(7)) : Blocks.AIR.defaultBlockState(), 3);
                } else if (chance == 61) {
                    world.setBlock(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)), Block.UPDATE_CLIENTS);
                    if (world.getBlockState(pos).getBlock() instanceof ChestBlock) {
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        if (blockEntity instanceof ChestBlockEntity chestBlockEntity)
                            chestBlockEntity.setLootTable(this.getChestTable(this.male), random.nextLong());
                    }
                }
            }
        }

        private DragonBaseEntity createDragon(final WorldGenLevel worldGen, final RandomSource random, final BlockPos position, int dragonAge) {
            DragonBaseEntity dragon = this.getDragonType().create(worldGen.getLevel());
            assert dragon != null;
            dragon.setGender(this.male);
            dragon.growDragon(dragonAge);
            dragon.setAgingDisabled(true);
            dragon.setHealth(dragon.getMaxHealth());
            dragon.setVariant(RandomHelper.randomOne(dragon.dragonType.colors()).getName());
            dragon.absMoveTo(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, random.nextFloat() * 360, 0);
            dragon.setInSittingPose(true);
            dragon.homePos = new HomePosition(position, worldGen.getLevel());
            dragon.setHunger(50);
            return dragon;
        }

        protected abstract TagKey<Block> getOreTag();

        protected abstract WorldGenCaveStalactites getCeilingDecoration();

        protected abstract BlockState getTreasurePile();

        protected abstract BlockState getPaletteBlock(RandomSource random);

        protected abstract ResourceKey<LootTable> getChestTable(boolean male);

        protected abstract EntityType<? extends DragonBaseEntity> getDragonType();
    }

    public record SphereInfo(int radius, BlockPos pos) {
    }

    protected static class WorldGenCaveStalactites {
        private final Block block;
        private final int maxHeight;

        public WorldGenCaveStalactites(Block block, int maxHeight) {
            this.block = block;
            this.maxHeight = maxHeight;
        }

        public void generate(LevelAccessor worldIn, RandomSource rand, BlockPos position) {
            int height = this.maxHeight + rand.nextInt(3);
            for (int i = 0; i < height; i++) {
                if (i < height / 2) {
                    worldIn.setBlock(position.below(i).north(), this.block.defaultBlockState(), 2);
                    worldIn.setBlock(position.below(i).east(), this.block.defaultBlockState(), 2);
                    worldIn.setBlock(position.below(i).south(), this.block.defaultBlockState(), 2);
                    worldIn.setBlock(position.below(i).west(), this.block.defaultBlockState(), 2);
                }
                worldIn.setBlock(position.below(i), this.block.defaultBlockState(), 2);
            }
        }
    }
}
