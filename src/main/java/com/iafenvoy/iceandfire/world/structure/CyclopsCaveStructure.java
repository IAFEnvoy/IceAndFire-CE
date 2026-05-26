package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.CyclopsEntity;
import com.iafenvoy.iceandfire.item.block.PileBlock;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Collectors;

public class CyclopsCaveStructure extends Structure implements DangerousGeneration {
    public static final MapCodec<CyclopsCaveStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(settingsCodec(instance)).apply(instance, CyclopsCaveStructure::new));

    protected CyclopsCaveStructure(StructureSettings config) {
        super(config);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (context.random().nextDouble() >= IafCommonConfig.INSTANCE.worldGen.generateCyclopsCaveChance.getValue())
            return Optional.empty();
        Rotation blockRotation = Rotation.getRandom(context.random());
        BlockPos blockPos = this.getLowestYIn5by5BoxOffset7Blocks(context, blockRotation);
        if (!this.isFarEnoughFromSpawn(blockPos)) return Optional.empty();
        return Optional.of(new GenerationStub(blockPos, collector -> collector.addPiece(new CyclopsCavePiece(0, new BoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ())))));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return IafStructureTypes.CYCLOPS_CAVE.get();
    }

    public static class CyclopsCavePiece extends StructurePiece {
        public static final ResourceKey<LootTable> CYCLOPS_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "chest/cyclops_cave"));

        protected CyclopsCavePiece(int length, BoundingBox boundingBox) {
            super(IafStructurePieces.CYCLOPS_CAVE.get(), length, boundingBox);
        }

        public CyclopsCavePiece(StructurePieceSerializationContext context, CompoundTag nbt) {
            super(IafStructurePieces.CYCLOPS_CAVE.get(), nbt);
        }

        @Override
        protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag nbt) {
        }

        @Override
        public void postProcess(@NotNull WorldGenLevel world, @NotNull StructureManager structureAccessor, @NotNull ChunkGenerator chunkGenerator, @NotNull RandomSource random, BoundingBox chunkBox, @NotNull ChunkPos chunkPos, @NotNull BlockPos pivot) {
            if (!chunkBox.isInside(pivot))
                return;

            int size = 16;
            this.generateShell(world, pivot, random, size);

            int innerSize = size - 2;
            int x = innerSize + random.nextInt(2);
            int y = 10 + random.nextInt(2);
            int z = innerSize + random.nextInt(2);
            float radius = (x + y + z) * 0.333F + 0.5F;

            int sheepPenCount = 0;

            // Clear out the area
            for (BlockPos position : BlockPos.betweenClosedStream(pivot.offset(-x, -y, -z), pivot.offset(x, y, z)).map(BlockPos::immutable).collect(Collectors.toSet()))
                if (position.distSqr(pivot) <= radius * radius && position.getY() > pivot.getY())
                    if (!(world.getBlockState(pivot).getBlock() instanceof AbstractChestBlock))
                        world.setBlock(position, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

            // Set up the actual content
            for (BlockPos position : BlockPos.betweenClosedStream(pivot.offset(-x, -y, -z), pivot.offset(x, y, z)).map(BlockPos::immutable).collect(Collectors.toSet())) {
                if (position.distSqr(pivot) <= radius * radius && position.getY() == pivot.getY()) {
                    if (random.nextInt(130) == 0 && this.isTouchingAir(world, position.above()))
                        this.generateSkeleton(world, position.above(), random, pivot, radius);
                    if (random.nextInt(130) == 0 && position.distSqr(pivot) <= (double) (radius * radius) * 0.8F && sheepPenCount < 2) {
                        this.generateSheepPen(world, position.above(), random, pivot, radius);
                        sheepPenCount++;
                    }

                    if (random.nextInt(80) == 0 && this.isTouchingAir(world, position.above())) {
                        world.setBlock(position.above(), IafBlocks.GOLD_PILE.get().defaultBlockState().setValue(PileBlock.LAYERS, 8), 3);
                        world.setBlock(position.above().north(), IafBlocks.GOLD_PILE.get().defaultBlockState().setValue(PileBlock.LAYERS, 1 + random.nextInt(7)), 3);
                        world.setBlock(position.above().south(), IafBlocks.GOLD_PILE.get().defaultBlockState().setValue(PileBlock.LAYERS, 1 + random.nextInt(7)), 3);
                        world.setBlock(position.above().west(), IafBlocks.GOLD_PILE.get().defaultBlockState().setValue(PileBlock.LAYERS, 1 + random.nextInt(7)), 3);
                        world.setBlock(position.above().east(), IafBlocks.GOLD_PILE.get().defaultBlockState().setValue(PileBlock.LAYERS, 1 + random.nextInt(7)), 3);
                        world.setBlock(position.above(2), Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)), 2);

                        if (world.getBlockState(position.above(2)).getBlock() instanceof AbstractChestBlock) {
                            BlockEntity blockEntity = world.getBlockEntity(position.above(2));
                            if (blockEntity instanceof ChestBlockEntity chestBlockEntity)
                                chestBlockEntity.setLootTable(CYCLOPS_CHEST, random.nextLong());
                        }
                    }

                    if (random.nextInt(50) == 0 && this.isTouchingAir(world, position.above())) {
                        int torchHeight = random.nextInt(2) + 1;
                        for (int fence = 0; fence < torchHeight; fence++)
                            world.setBlock(position.above(1 + fence), this.getFenceState(world, position.above(1 + fence)), 3);
                        world.setBlock(position.above(1 + torchHeight), Blocks.TORCH.defaultBlockState(), 2);
                    }
                }
            }

            CyclopsEntity cyclops = IafEntities.CYCLOPS.get().create(world.getLevel());
            if (cyclops != null) {
                cyclops.absMoveTo(pivot.getX() + 0.5, pivot.getY() + 1.5, pivot.getZ() + 0.5, random.nextFloat() * 360, 0);
                world.addFreshEntity(cyclops);
            }
        }

        private void generateSheepPen(ServerLevelAccessor level, BlockPos position, RandomSource random, BlockPos origin, float radius) {
            int width = 5 + random.nextInt(3);
            int sheepAmount = 2 + random.nextInt(3);
            Direction direction = Direction.NORTH;
            BlockPos end = position;

            for (int sideCount = 0; sideCount < 4; sideCount++) {
                for (int side = 0; side < width; side++) {
                    BlockPos relativePosition = end.relative(direction, side);

                    if (origin.distSqr(relativePosition) <= radius * radius) {
                        level.setBlock(relativePosition, this.getFenceState(level, relativePosition), Block.UPDATE_ALL);
                        if (level.isEmptyBlock(relativePosition.relative(direction.getClockWise())) && sheepAmount > 0) {
                            BlockPos sheepPos = relativePosition.relative(direction.getClockWise());
                            Sheep sheep = new Sheep(EntityType.SHEEP, level.getLevel());
                            sheep.setPos(sheepPos.getX() + 0.5F, sheepPos.getY() + 0.5F, sheepPos.getZ() + 0.5F);
                            sheep.setColor(random.nextInt(4) == 0 ? DyeColor.YELLOW : DyeColor.WHITE);
                            level.addFreshEntity(sheep);
                            sheepAmount--;
                        }
                    }
                }
                end = end.relative(direction, width);
                direction = direction.getClockWise();
            }

            // Go through the fence blocks again and make sure they're properly connected to each other
            for (int sideCount = 0; sideCount < 4; sideCount++) {
                for (int side = 0; side < width; side++) {
                    BlockPos relativePosition = end.relative(direction, side);
                    if (origin.distSqr(relativePosition) <= radius * radius)
                        level.setBlock(relativePosition, this.getFenceState(level, relativePosition), Block.UPDATE_ALL);
                }

                end = end.relative(direction, width);
                direction = direction.getClockWise();
            }
        }

        private void generateSkeleton(LevelAccessor level, BlockPos position, RandomSource random, BlockPos origin, float radius) {
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            Direction.Axis oppositeAxis = direction.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            int maxRibHeight = random.nextInt(2);

            for (int spine = 0; spine < 5 + random.nextInt(2) * 2; spine++) {
                BlockPos segment = position.relative(direction, spine);

                if (origin.distSqr(segment) <= radius * radius)
                    level.setBlock(segment, Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, direction.getAxis()), 2);

                if (spine % 2 != 0) {
                    BlockPos rightRib = segment.relative(direction.getCounterClockWise());
                    BlockPos leftRib = segment.relative(direction.getClockWise());

                    if (origin.distSqr(rightRib) <= radius * radius)
                        level.setBlock(rightRib, Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, oppositeAxis), 2);

                    if (origin.distSqr(leftRib) <= radius * radius)
                        level.setBlock(leftRib, Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, oppositeAxis), 2);

                    for (int ribHeight = 1; ribHeight < maxRibHeight + 2; ribHeight++) {
                        if (origin.distSqr(rightRib.above(ribHeight).relative(direction.getCounterClockWise())) <= radius * radius)
                            level.setBlock(rightRib.above(ribHeight).relative(direction.getCounterClockWise()), Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y), Block.UPDATE_CLIENTS);
                        if (origin.distSqr(leftRib.above(ribHeight).relative(direction.getClockWise())) <= radius * radius)
                            level.setBlock(leftRib.above(ribHeight).relative(direction.getClockWise()), Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y), Block.UPDATE_CLIENTS);
                    }
                    if (origin.distSqr(rightRib.above(maxRibHeight + 2)) <= radius * radius)
                        level.setBlock(rightRib.above(maxRibHeight + 2), Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, oppositeAxis), Block.UPDATE_CLIENTS);
                    if (origin.distSqr(leftRib.above(maxRibHeight + 2)) <= radius * radius)
                        level.setBlock(leftRib.above(maxRibHeight + 2), Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, oppositeAxis), Block.UPDATE_CLIENTS);
                }

            }
        }

        private boolean isTouchingAir(LevelAccessor level, BlockPos position) {
            for (Direction direction : Direction.Plane.HORIZONTAL)
                if (!level.isEmptyBlock(position.relative(direction)))
                    return false;
            return true;
        }

        private BlockState getFenceState(LevelAccessor level, BlockPos position) {
            boolean east = level.getBlockState(position.east()).getBlock() == Blocks.OAK_FENCE;
            boolean west = level.getBlockState(position.west()).getBlock() == Blocks.OAK_FENCE;
            boolean north = level.getBlockState(position.north()).getBlock() == Blocks.OAK_FENCE;
            boolean south = level.getBlockState(position.south()).getBlock() == Blocks.OAK_FENCE;

            return Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, east).setValue(FenceBlock.WEST, west).setValue(FenceBlock.NORTH, north).setValue(FenceBlock.SOUTH, south);
        }

        private void generateShell(WorldGenLevel world, BlockPos origin, RandomSource random, int size) {
            int x = size + random.nextInt(2);
            int y = 12 + random.nextInt(2);
            int z = size + random.nextInt(2);
            super.boundingBox = new BoundingBox(origin.getX() - x + 2, origin.getY(), origin.getZ() - z + 2, origin.getX() + x - 2, origin.getY() + y, origin.getZ() + z - 2);
            float radius = (x + y + z) * 0.333F + 0.5F;

            for (BlockPos position : BlockPos.betweenClosedStream(origin.offset(-x, -y, -z), origin.offset(x, y, z)).map(BlockPos::immutable).collect(Collectors.toSet())) {
                boolean doorwayX = position.getX() >= origin.getX() - 2 + random.nextInt(2) && position.getX() <= origin.getX() + 2 + random.nextInt(2);
                boolean doorwayZ = position.getZ() >= origin.getZ() - 2 + random.nextInt(2) && position.getZ() <= origin.getZ() + 2 + random.nextInt(2);
                boolean isNotInDoorway = !doorwayX && !doorwayZ && position.getY() > origin.getY() || position.getY() > origin.getY() + y - (3 + random.nextInt(2));

                if (position.distSqr(origin) <= radius * radius) {
                    BlockState state = world.getBlockState(position);
                    if (!(state.getBlock() instanceof AbstractChestBlock) && state.getDestroySpeed(world, position) >= 0 && isNotInDoorway)
                        world.setBlock(position, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
                    if (position.getY() == origin.getY())
                        world.setBlock(position, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), Block.UPDATE_ALL);
                    if (position.getY() <= origin.getY() - 1 && !state.canOcclude())
                        world.setBlock(position, Blocks.COBBLESTONE.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }
}
