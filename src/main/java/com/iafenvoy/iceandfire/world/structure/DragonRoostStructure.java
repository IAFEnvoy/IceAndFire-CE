package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.HomePosition;
import com.iafenvoy.iceandfire.item.block.PileBlock;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.uranus.util.RandomHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Collectors;

public abstract class DragonRoostStructure extends Structure implements DangerousGeneration {
    protected DragonRoostStructure(StructureSettings config) {
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
        return Optional.of(new GenerationStub(blockPos, collector -> collector.addPiece(this.createPiece(new BoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ()), context.random().nextBoolean()))));
    }

    protected abstract DragonRoostPiece createPiece(BoundingBox boundingBox, boolean isMale);

    protected abstract double getGenerateChance();

    protected static abstract class DragonRoostPiece extends StructurePiece {
        protected final Block treasureBlock;
        private final boolean isMale;

        protected DragonRoostPiece(StructurePieceType type, int length, BoundingBox boundingBox, Block treasureBlock, boolean isMale) {
            super(type, length, boundingBox);
            this.treasureBlock = treasureBlock;
            this.isMale = isMale;
        }

        public DragonRoostPiece(StructurePieceType type, CompoundTag nbt) {
            super(type, nbt);
            this.treasureBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(nbt.getString("treasureBlock")));
            this.isMale = nbt.getBoolean("isMale");
        }

        @Override
        protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, CompoundTag nbt) {
            nbt.putString("treasureBlock", BuiltInRegistries.BLOCK.getKey(this.treasureBlock).toString());
            nbt.putBoolean("isMale", this.isMale);
        }

        @Override
        public void postProcess(@NotNull WorldGenLevel world, @NotNull StructureManager structureAccessor, @NotNull ChunkGenerator chunkGenerator, @NotNull RandomSource random, BoundingBox chunkBox, @NotNull ChunkPos chunkPos, @NotNull BlockPos pivot) {
            if (!chunkBox.isInside(pivot))
                return;

            int radius = 12 + random.nextInt(8);
            this.spawnDragon(world, pivot, random, radius, this.isMale);
            this.generateSurface(world, pivot, random, radius);
            this.generateShell(world, pivot, random, radius);
            radius -= 2;
            this.hollowOut(world, pivot, radius);
            radius += 15;
            this.generateDecoration(world, pivot, random, radius, this.isMale);
        }


        protected void generateRoostPile(WorldGenLevel level, RandomSource random, BlockPos position, Block block) {
            int radius = random.nextInt(4);

            for (int i = 0; i < radius; i++) {
                int layeredRadius = radius - i;
                double circularArea = this.getCircularArea(radius);
                BlockPos up = position.above(i);

                for (BlockPos blockpos : BlockPos.betweenClosedStream(up.offset(-layeredRadius, 0, -layeredRadius), up.offset(layeredRadius, 0, layeredRadius)).map(BlockPos::immutable).collect(Collectors.toSet())) {
                    if (blockpos.distSqr(position) <= circularArea) {
                        level.setBlock(blockpos, block.defaultBlockState(), Block.UPDATE_CLIENTS);
                    }
                }
            }
        }

        protected double getCircularArea(int radius, int height) {
            double area = (radius + height + radius) * 0.333F + 0.5F;
            return Mth.floor(area * area);
        }

        protected double getCircularArea(int radius) {
            double area = (radius + radius) * 0.333F + 0.5F;
            return Mth.floor(area * area);
        }

        protected BlockPos getSurfacePosition(WorldGenLevel level, BlockPos position) {
            return level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, position);
        }

        protected BlockState transform(Block block) {
            return this.transform(block.defaultBlockState());
        }

        private void generateDecoration(WorldGenLevel world, BlockPos origin, RandomSource random, int radius, boolean isMale) {
            int height = (radius / 5);
            double circularArea = this.getCircularArea(radius, height);

            BlockPos.betweenClosedStream(origin.offset(-radius, -height, -radius), origin.offset(radius, height, radius)).map(BlockPos::immutable).forEach(position -> {
                if (position.distSqr(origin) <= circularArea) {
                    double distance = position.distSqr(origin) / circularArea;

                    if (!world.isEmptyBlock(origin) && random.nextDouble() > distance * 0.5) {
                        BlockState state = world.getBlockState(position);

                        if (!(state.getBlock() instanceof BaseEntityBlock) && state.getDestroySpeed(world, position) >= 0) {
                            BlockState transformed = this.transform(state);

                            if (transformed != state) {
                                world.setBlock(position, transformed, Block.UPDATE_CLIENTS);
                            }
                        }
                    }

                    this.handleCustomGeneration(world, origin, random, position, distance);
                    if (distance > 0.5 && random.nextInt(1000) == 0)
                        this.generateBoulder(world, random, this.getSurfacePosition(world, position), this.transform(Blocks.COBBLESTONE).getBlock(), random.nextInt(3), true);
                    if (distance < 0.3 && random.nextInt(isMale ? 200 : 300) == 0)
                        this.generateTreasurePile(world, random, position);

                    if (distance < 0.3D && random.nextInt(isMale ? 500 : 700) == 0) {
                        BlockPos surfacePosition = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, position);
                        boolean wasPlaced = world.setBlock(surfacePosition, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)), Block.UPDATE_CLIENTS);

                        if (wasPlaced) {
                            BlockEntity blockEntity = world.getBlockEntity(surfacePosition);
                            if (blockEntity instanceof ChestBlockEntity chest)
                                chest.setLootTable(this.getRoostLootTable(), random.nextLong());
                        }
                    }
                    if (random.nextInt(5000) == 0)
                        this.generateArch(world, random, this.getSurfacePosition(world, position), this.transform(Blocks.COBBLESTONE).getBlock());
                }
            });
        }

        public void generateBoulder(LevelAccessor worldIn, RandomSource rand, BlockPos position, Block block, int startRadius, boolean replaceAir) {
            while (true) {
                if (position.getY() > 3) {
                    if (worldIn.isEmptyBlock(position.below())) {
                        position = position.below();
                        continue;
                    }
                    BlockState b = worldIn.getBlockState(position.below());
                    if (!b.is(IafBlockTags.GRASSES) && !b.is(Blocks.DIRT) && !b.is(Blocks.STONE)) {
                        position = position.below();
                        continue;
                    }
                }
                if (position.getY() <= 3)
                    break;

                for (int i = 0; startRadius >= 0 && i < 3; ++i) {
                    int j = startRadius + rand.nextInt(2);
                    int k = startRadius + rand.nextInt(2);
                    int l = startRadius + rand.nextInt(2);
                    float f = (float) (j + k + l) * 0.333F + 0.5F;
                    for (BlockPos blockpos : BlockPos.betweenClosedStream(position.offset(-j, -k, -l), position.offset(j, k, l)).map(BlockPos::immutable).collect(Collectors.toSet()))
                        if (blockpos.distSqr(position) <= (double) (f * f) && (replaceAir || worldIn.getBlockState(blockpos).canOcclude()))
                            worldIn.setBlock(blockpos, block.defaultBlockState(), 2);
                    position = position.offset(-(startRadius + 1) + rand.nextInt(2 + startRadius * 2), -rand.nextInt(2), -(startRadius + 1) + rand.nextInt(2 + startRadius * 2));
                }
                break;
            }
        }

        private void generateArch(LevelAccessor worldIn, RandomSource random, BlockPos position, Block block) {
            int height = 3 + random.nextInt(3);
            int width = Math.min(3, height - 2);
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            boolean diagonal = random.nextBoolean();
            for (int i = 0; i < height; i++)
                worldIn.setBlock(position.above(i), block.defaultBlockState(), 2);
            BlockPos offsetPos = position;
            int placedWidths = 0;
            for (int i = 0; i < width; i++) {
                offsetPos = position.above(height).relative(direction, i);
                if (diagonal)
                    offsetPos = position.above(height).relative(direction, i).relative(direction.getClockWise(), i);
                if (placedWidths < width - 1 || random.nextBoolean())
                    worldIn.setBlock(offsetPos, block.defaultBlockState(), 2);
                placedWidths++;
            }
            while (worldIn.isEmptyBlock(offsetPos.below()) && offsetPos.getY() > 0) {
                worldIn.setBlock(offsetPos.below(), block.defaultBlockState(), 2);
                offsetPos = offsetPos.below();
            }
        }

        private void hollowOut(WorldGenLevel world, BlockPos origin, int radius) {
            int height = 2;
            double circularArea = this.getCircularArea(radius, height);
            BlockPos up = origin.above(height - 1);

            BlockPos.betweenClosedStream(up.offset(-radius, 0, -radius), up.offset(radius, height, radius)).map(BlockPos::immutable).forEach(position -> {
                if (position.distSqr(origin) <= circularArea)
                    world.setBlock(position, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            });
        }

        private void generateShell(WorldGenLevel world, BlockPos origin, RandomSource random, int radius) {
            int height = (radius / 5);
            double circularArea = this.getCircularArea(radius, height);

            int real_radius = (int) Math.sqrt(circularArea);
            super.boundingBox = new BoundingBox(origin.getX() - real_radius, origin.getY(), origin.getZ() - real_radius, origin.getX() + real_radius, origin.getY() + 3, origin.getZ() + real_radius);

            BlockPos.betweenClosedStream(origin.offset(-radius, -height, -radius), origin.offset(radius, 1, radius)).map(BlockPos::immutable).forEach(position -> {
                if (position.distSqr(origin) < circularArea)
                    world.setBlock(position, random.nextBoolean() ? this.transform(Blocks.GRAVEL) : this.transform(Blocks.DIRT), Block.UPDATE_CLIENTS);
                else if (position.distSqr(origin) == circularArea)
                    world.setBlock(position, this.transform(Blocks.COBBLESTONE), Block.UPDATE_CLIENTS);
            });
        }

        private void generateSurface(WorldGenLevel world, BlockPos origin, RandomSource random, int radius) {
            int height = 2;
            double circularArea = this.getCircularArea(radius, height);

            BlockPos.betweenClosedStream(origin.offset(-radius, height, -radius), origin.offset(radius, 0, radius)).map(BlockPos::immutable).forEach(position -> {
                int heightDifference = position.getY() - origin.getY();

                if (position.distSqr(origin) <= circularArea && heightDifference < 2 + random.nextInt(height) && !world.isEmptyBlock(position.below())) {
                    if (world.isEmptyBlock(position.above()))
                        world.setBlock(position, this.transform(Blocks.SHORT_GRASS), Block.UPDATE_CLIENTS);
                    else
                        world.setBlock(position, this.transform(Blocks.DIRT), Block.UPDATE_CLIENTS);
                }
            });
        }

        private void generateTreasurePile(WorldGenLevel world, RandomSource random, BlockPos origin) {
            int layers = random.nextInt(3);

            for (int i = 0; i < layers; i++) {
                int radius = layers - i;
                double circularArea = this.getCircularArea(radius);

                for (BlockPos position : BlockPos.betweenClosedStream(origin.offset(-radius, i, -radius), origin.offset(radius, i, radius)).map(BlockPos::immutable).collect(Collectors.toSet())) {
                    if (position.distSqr(origin) <= circularArea) {
                        position = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, position);

                        if (this.treasureBlock instanceof PileBlock) {
                            BlockState state = world.getBlockState(position);
                            boolean placed = false;
                            if (state.isAir()) {
                                world.setBlock(position, this.treasureBlock.defaultBlockState().setValue(PileBlock.LAYERS, 1 + random.nextInt(7)), Block.UPDATE_CLIENTS);
                                placed = true;
                            } else if (state.getBlock() instanceof SnowLayerBlock) {
                                world.setBlock(position.below(), this.treasureBlock.defaultBlockState().setValue(PileBlock.LAYERS, state.getValue(SnowLayerBlock.LAYERS)), Block.UPDATE_CLIENTS);
                                placed = true;
                            }
                            if (placed && world.getBlockState(position.below()).getBlock() instanceof PileBlock)
                                world.setBlock(position.below(), this.treasureBlock.defaultBlockState().setValue(PileBlock.LAYERS, 8), Block.UPDATE_CLIENTS);
                        }
                    }
                }
            }
        }

        private void spawnDragon(WorldGenLevel world, BlockPos origin, RandomSource random, int ageOffset, boolean isMale) {
            DragonBaseEntity dragon = this.getDragonType().create(world.getLevel());
            assert dragon != null;
            dragon.setGender(isMale);
            dragon.growDragon(40 + ageOffset);
            dragon.setAgingDisabled(true);
            dragon.setHealth(dragon.getMaxHealth());
            dragon.setVariant(RandomHelper.randomOne(dragon.dragonType.colors()).getName());
            dragon.absMoveTo(origin.getX() + 0.5, world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, origin).getY() + 1.5, origin.getZ() + 0.5, random.nextFloat() * 360, 0);
            dragon.homePos = new HomePosition(origin, world.getLevel());
            dragon.hasHomePosition = true;
            dragon.setHunger(50);
            world.addFreshEntity(dragon);
        }

        protected abstract EntityType<? extends DragonBaseEntity> getDragonType();

        protected abstract ResourceKey<LootTable> getRoostLootTable();

        protected abstract BlockState transform(BlockState block);

        protected abstract void handleCustomGeneration(WorldGenLevel world, BlockPos origin, RandomSource random, BlockPos position, double distance);
    }
}
