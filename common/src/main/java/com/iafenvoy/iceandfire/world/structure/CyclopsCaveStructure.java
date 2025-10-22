package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.CyclopsEntity;
import com.iafenvoy.iceandfire.item.block.GoldPileBlock;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;
import java.util.stream.Collectors;

public class CyclopsCaveStructure extends Structure implements DangerousGeneration {
    public static final MapCodec<CyclopsCaveStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(configCodecBuilder(instance)).apply(instance, CyclopsCaveStructure::new));

    protected CyclopsCaveStructure(Config config) {
        super(config);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        if (context.random().nextDouble() >= IafCommonConfig.INSTANCE.worldGen.generateCyclopsCaveChance.getValue())
            return Optional.empty();
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (!this.isFarEnoughFromSpawn(blockPos)) return Optional.empty();
        return Optional.of(new StructurePosition(blockPos, collector -> collector.addPiece(new CyclopsCavePiece(0, new BlockBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ())))));
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.CYCLOPS_CAVE.get();
    }

    public static class CyclopsCavePiece extends StructurePiece {
        public static final RegistryKey<LootTable> CYCLOPS_CHEST = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(IceAndFire.MOD_ID, "chest/cyclops_cave"));

        protected CyclopsCavePiece(int length, BlockBox boundingBox) {
            super(IafStructurePieces.CYCLOPS_CAVE.get(), length, boundingBox);
        }

        public CyclopsCavePiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.CYCLOPS_CAVE.get(), nbt);
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            int size = 16;
            generateShell(world, pivot, random, size);

            int innerSize = size - 2;
            int x = innerSize + random.nextInt(2);
            int y = 10 + random.nextInt(2);
            int z = innerSize + random.nextInt(2);
            float radius = (x + y + z) * 0.333F + 0.5F;

            int sheepPenCount = 0;

            // Clear out the area
            for (BlockPos position : BlockPos.stream(pivot.add(-x, -y, -z), pivot.add(x, y, z)).map(BlockPos::toImmutable).collect(Collectors.toSet()))
                if (position.getSquaredDistance(pivot) <= radius * radius && position.getY() > pivot.getY())
                    if (!(world.getBlockState(pivot).getBlock() instanceof AbstractChestBlock))
                        world.setBlockState(position, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);

            // Set up the actual content
            for (BlockPos position : BlockPos.stream(pivot.add(-x, -y, -z), pivot.add(x, y, z)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                if (position.getSquaredDistance(pivot) <= radius * radius && position.getY() == pivot.getY()) {
                    if (random.nextInt(130) == 0 && this.isTouchingAir(world, position.up()))
                        this.generateSkeleton(world, position.up(), random, pivot, radius);
                    if (random.nextInt(130) == 0 && position.getSquaredDistance(pivot) <= (double) (radius * radius) * 0.8F && sheepPenCount < 2) {
                        this.generateSheepPen(world, position.up(), random, pivot, radius);
                        sheepPenCount++;
                    }

                    if (random.nextInt(80) == 0 && this.isTouchingAir(world, position.up())) {
                        world.setBlockState(position.up(), IafBlocks.GOLD_PILE.get().getDefaultState().with(GoldPileBlock.LAYERS, 8), 3);
                        world.setBlockState(position.up().north(), IafBlocks.GOLD_PILE.get().getDefaultState().with(GoldPileBlock.LAYERS, 1 + random.nextInt(7)), 3);
                        world.setBlockState(position.up().south(), IafBlocks.GOLD_PILE.get().getDefaultState().with(GoldPileBlock.LAYERS, 1 + random.nextInt(7)), 3);
                        world.setBlockState(position.up().west(), IafBlocks.GOLD_PILE.get().getDefaultState().with(GoldPileBlock.LAYERS, 1 + random.nextInt(7)), 3);
                        world.setBlockState(position.up().east(), IafBlocks.GOLD_PILE.get().getDefaultState().with(GoldPileBlock.LAYERS, 1 + random.nextInt(7)), 3);
                        world.setBlockState(position.up(2), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.Type.HORIZONTAL.random(random)), 2);

                        if (world.getBlockState(position.up(2)).getBlock() instanceof AbstractChestBlock) {
                            BlockEntity blockEntity = world.getBlockEntity(position.up(2));
                            if (blockEntity instanceof ChestBlockEntity chestBlockEntity)
                                chestBlockEntity.setLootTable(CYCLOPS_CHEST, random.nextLong());
                        }
                    }

                    if (random.nextInt(50) == 0 && this.isTouchingAir(world, position.up())) {
                        int torchHeight = random.nextInt(2) + 1;
                        for (int fence = 0; fence < torchHeight; fence++)
                            world.setBlockState(position.up(1 + fence), this.getFenceState(world, position.up(1 + fence)), 3);
                        world.setBlockState(position.up(1 + torchHeight), Blocks.TORCH.getDefaultState(), 2);
                    }
                }
            }

            CyclopsEntity cyclops = IafEntities.CYCLOPS.get().create(world.toServerWorld());
            if (cyclops != null) {
                cyclops.updatePositionAndAngles(pivot.getX() + 0.5, pivot.getY() + 1.5, pivot.getZ() + 0.5, random.nextFloat() * 360, 0);
                world.spawnEntity(cyclops);
            }
        }

        private void generateSheepPen(ServerWorldAccess level, BlockPos position, Random random, BlockPos origin, float radius) {
            int width = 5 + random.nextInt(3);
            int sheepAmount = 2 + random.nextInt(3);
            Direction direction = Direction.NORTH;
            BlockPos end = position;

            for (int sideCount = 0; sideCount < 4; sideCount++) {
                for (int side = 0; side < width; side++) {
                    BlockPos relativePosition = end.offset(direction, side);

                    if (origin.getSquaredDistance(relativePosition) <= radius * radius) {
                        level.setBlockState(relativePosition, this.getFenceState(level, relativePosition), Block.NOTIFY_ALL);
                        if (level.isAir(relativePosition.offset(direction.rotateYClockwise())) && sheepAmount > 0) {
                            BlockPos sheepPos = relativePosition.offset(direction.rotateYClockwise());
                            SheepEntity sheep = new SheepEntity(EntityType.SHEEP, level.toServerWorld());
                            sheep.setPosition(sheepPos.getX() + 0.5F, sheepPos.getY() + 0.5F, sheepPos.getZ() + 0.5F);
                            sheep.setColor(random.nextInt(4) == 0 ? DyeColor.YELLOW : DyeColor.WHITE);
                            level.spawnEntity(sheep);
                            sheepAmount--;
                        }
                    }
                }
                end = end.offset(direction, width);
                direction = direction.rotateYClockwise();
            }

            // Go through the fence blocks again and make sure they're properly connected to each other
            for (int sideCount = 0; sideCount < 4; sideCount++) {
                for (int side = 0; side < width; side++) {
                    BlockPos relativePosition = end.offset(direction, side);
                    if (origin.getSquaredDistance(relativePosition) <= radius * radius)
                        level.setBlockState(relativePosition, this.getFenceState(level, relativePosition), Block.NOTIFY_ALL);
                }

                end = end.offset(direction, width);
                direction = direction.rotateYClockwise();
            }
        }

        private void generateSkeleton(WorldAccess level, BlockPos position, Random random, BlockPos origin, float radius) {
            Direction direction = Direction.Type.HORIZONTAL.random(random);
            Direction.Axis oppositeAxis = direction.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            int maxRibHeight = random.nextInt(2);

            for (int spine = 0; spine < 5 + random.nextInt(2) * 2; spine++) {
                BlockPos segment = position.offset(direction, spine);

                if (origin.getSquaredDistance(segment) <= radius * radius)
                    level.setBlockState(segment, Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, direction.getAxis()), 2);

                if (spine % 2 != 0) {
                    BlockPos rightRib = segment.offset(direction.rotateYCounterclockwise());
                    BlockPos leftRib = segment.offset(direction.rotateYClockwise());

                    if (origin.getSquaredDistance(rightRib) <= radius * radius)
                        level.setBlockState(rightRib, Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, oppositeAxis), 2);

                    if (origin.getSquaredDistance(leftRib) <= radius * radius)
                        level.setBlockState(leftRib, Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, oppositeAxis), 2);

                    for (int ribHeight = 1; ribHeight < maxRibHeight + 2; ribHeight++) {
                        if (origin.getSquaredDistance(rightRib.up(ribHeight).offset(direction.rotateYCounterclockwise())) <= radius * radius)
                            level.setBlockState(rightRib.up(ribHeight).offset(direction.rotateYCounterclockwise()), Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y), Block.NOTIFY_LISTENERS);
                        if (origin.getSquaredDistance(leftRib.up(ribHeight).offset(direction.rotateYClockwise())) <= radius * radius)
                            level.setBlockState(leftRib.up(ribHeight).offset(direction.rotateYClockwise()), Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y), Block.NOTIFY_LISTENERS);
                    }
                    if (origin.getSquaredDistance(rightRib.up(maxRibHeight + 2)) <= radius * radius)
                        level.setBlockState(rightRib.up(maxRibHeight + 2), Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, oppositeAxis), Block.NOTIFY_LISTENERS);
                    if (origin.getSquaredDistance(leftRib.up(maxRibHeight + 2)) <= radius * radius)
                        level.setBlockState(leftRib.up(maxRibHeight + 2), Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, oppositeAxis), Block.NOTIFY_LISTENERS);
                }

            }
        }

        private boolean isTouchingAir(WorldAccess level, BlockPos position) {
            for (Direction direction : Direction.Type.HORIZONTAL)
                if (!level.isAir(position.offset(direction)))
                    return false;
            return true;
        }

        private BlockState getFenceState(WorldAccess level, BlockPos position) {
            boolean east = level.getBlockState(position.east()).getBlock() == Blocks.OAK_FENCE;
            boolean west = level.getBlockState(position.west()).getBlock() == Blocks.OAK_FENCE;
            boolean north = level.getBlockState(position.north()).getBlock() == Blocks.OAK_FENCE;
            boolean south = level.getBlockState(position.south()).getBlock() == Blocks.OAK_FENCE;

            return Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.EAST, east).with(FenceBlock.WEST, west).with(FenceBlock.NORTH, north).with(FenceBlock.SOUTH, south);
        }

        private static void generateShell(StructureWorldAccess world, BlockPos origin, Random random, int size) {
            int x = size + random.nextInt(2);
            int y = 12 + random.nextInt(2);
            int z = size + random.nextInt(2);
            float radius = (x + y + z) * 0.333F + 0.5F;

            for (BlockPos position : BlockPos.stream(origin.add(-x, -y, -z), origin.add(x, y, z)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                boolean doorwayX = position.getX() >= origin.getX() - 2 + random.nextInt(2) && position.getX() <= origin.getX() + 2 + random.nextInt(2);
                boolean doorwayZ = position.getZ() >= origin.getZ() - 2 + random.nextInt(2) && position.getZ() <= origin.getZ() + 2 + random.nextInt(2);
                boolean isNotInDoorway = !doorwayX && !doorwayZ && position.getY() > origin.getY() || position.getY() > origin.getY() + y - (3 + random.nextInt(2));

                if (position.getSquaredDistance(origin) <= radius * radius) {
                    BlockState state = world.getBlockState(position);
                    if (!(state.getBlock() instanceof AbstractChestBlock) && state.getHardness(world, position) >= 0 && isNotInDoorway)
                        world.setBlockState(position, Blocks.STONE.getDefaultState(), Block.NOTIFY_ALL);
                    if (position.getY() == origin.getY())
                        world.setBlockState(position, Blocks.MOSSY_COBBLESTONE.getDefaultState(), Block.NOTIFY_ALL);
                    if (position.getY() <= origin.getY() - 1 && !state.isOpaque())
                        world.setBlockState(position, Blocks.COBBLESTONE.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }
    }
}
