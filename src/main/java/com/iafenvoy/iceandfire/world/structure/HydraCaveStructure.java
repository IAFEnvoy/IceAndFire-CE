package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.HydraEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
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

public class HydraCaveStructure extends Structure implements DangerousGeneration {
    public static final MapCodec<HydraCaveStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(settingsCodec(instance)).apply(instance, HydraCaveStructure::new));

    protected HydraCaveStructure(StructureSettings config) {
        super(config);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (context.random().nextDouble() >= IafCommonConfig.INSTANCE.worldGen.generateHydraCaveChance.getValue())
            return Optional.empty();
        Rotation blockRotation = Rotation.getRandom(context.random());
        BlockPos blockPos = this.getLowestYIn5by5BoxOffset7Blocks(context, blockRotation);
        if (!this.isFarEnoughFromSpawn(blockPos)) return Optional.empty();
        return Optional.of(new GenerationStub(blockPos, collector -> collector.addPiece(new HydraCavePiece(0, new BoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ())))));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return IafStructureTypes.HYDRA_CAVE.get();
    }

    public static class HydraCavePiece extends StructurePiece {
        public static final ResourceKey<LootTable> HYDRA_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "chest/hydra_cave"));

        protected HydraCavePiece(int length, BoundingBox boundingBox) {
            super(IafStructurePieces.HYDRA_CAVE.get(), length, boundingBox);
        }

        public HydraCavePiece(StructurePieceSerializationContext context, CompoundTag nbt) {
            super(IafStructurePieces.HYDRA_CAVE.get(), nbt);
        }

        @Override
        protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag nbt) {
        }

        @Override
        public void postProcess(@NotNull WorldGenLevel world, @NotNull StructureManager structureAccessor, @NotNull ChunkGenerator chunkGenerator, @NotNull RandomSource random, BoundingBox chunkBox, @NotNull ChunkPos chunkPos, @NotNull BlockPos pivot) {
            if (!chunkBox.isInside(pivot))
                return;

            int i1 = 8;
            int i2 = i1 - 2;
            {
                int ySize = random.nextInt(2);
                int j = i1 + random.nextInt(2);
                int k = 5 + ySize;
                int l = i1 + random.nextInt(2);
                float f = (j + k + l) * 0.333F + 0.5F;
                super.boundingBox = new BoundingBox(pivot.getX() - j + 2, pivot.getY(), pivot.getZ() - l + 2, pivot.getX() + j - 2, pivot.getY() + k, pivot.getZ() + l - 2);

                for (BlockPos blockpos : BlockPos.betweenClosedStream(pivot.offset(-j, -k, -l), pivot.offset(j, k, l)).map(BlockPos::immutable).collect(Collectors.toSet())) {
                    boolean doorwayX = blockpos.getX() >= pivot.getX() - 2 + random.nextInt(2) && blockpos.getX() <= pivot.getX() + 2 + random.nextInt(2);
                    boolean doorwayZ = blockpos.getZ() >= pivot.getZ() - 2 + random.nextInt(2) && blockpos.getZ() <= pivot.getZ() + 2 + random.nextInt(2);
                    boolean isNotInDoorway = !doorwayX && !doorwayZ && blockpos.getY() > pivot.getY() || blockpos.getY() > pivot.getY() + k - (1 + random.nextInt(2));
                    if (blockpos.distSqr(pivot) <= f * f) {
                        if (!(world.getBlockState(pivot).getBlock() instanceof ChestBlock) && isNotInDoorway) {
                            world.setBlock(blockpos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                            if (world.getBlockState(pivot.below()).getBlock() == Blocks.GRASS_BLOCK)
                                world.setBlock(blockpos.below(), Blocks.DIRT.defaultBlockState(), 3);
                            if (random.nextInt(4) == 0)
                                world.setBlock(blockpos.above(), Blocks.SHORT_GRASS.defaultBlockState(), 2);
                            if (random.nextInt(9) == 0)
                                world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(TreeFeatures.SWAMP_OAK).ifPresent(holder -> holder.value().place(world, chunkGenerator, random, blockpos.above()));
                        }
                        if (blockpos.getY() == pivot.getY())
                            world.setBlock(blockpos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                        if (blockpos.getY() <= pivot.getY() - 1 && !world.getBlockState(blockpos).canOcclude())
                            world.setBlock(blockpos, Blocks.STONE.defaultBlockState(), 3);
                    }
                }
            }
            {
                int ySize = random.nextInt(2);
                int j = i2 + random.nextInt(2);
                int k = 4 + ySize;
                int l = i2 + random.nextInt(2);
                float f = (j + k + l) * 0.333F + 0.5F;
                for (BlockPos blockpos : BlockPos.betweenClosedStream(pivot.offset(-j, -k, -l), pivot.offset(j, k, l)).map(BlockPos::immutable).collect(Collectors.toSet()))
                    if (blockpos.distSqr(pivot) <= f * f && blockpos.getY() > pivot.getY())
                        if (!(world.getBlockState(pivot).getBlock() instanceof ChestBlock))
                            world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 3);
                for (BlockPos blockpos : BlockPos.betweenClosedStream(pivot.offset(-j, -k, -l), pivot.offset(j, k + 8, l)).map(BlockPos::immutable).collect(Collectors.toSet())) {
                    if (blockpos.distSqr(pivot) <= f * f && blockpos.getY() == pivot.getY()) {
                        if (random.nextInt(30) == 0 && this.isTouchingAir(world, blockpos.above())) {
                            world.setBlock(blockpos.above(1), Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)), 2);
                            if (world.getBlockState(blockpos.above(1)).getBlock() instanceof ChestBlock)
                                if (world.getBlockEntity(blockpos.above(1)) instanceof ChestBlockEntity chest)
                                    chest.setLootTable(HYDRA_CHEST, random.nextLong());
                            continue;
                        }
                        if (random.nextInt(45) == 0 && this.isTouchingAir(world, blockpos.above())) {
                            world.setBlock(blockpos.above(), Blocks.SKELETON_SKULL.defaultBlockState().setValue(SkullBlock.ROTATION, random.nextInt(15)), 2);
                            continue;
                        }
                        if (random.nextInt(35) == 0 && this.isTouchingAir(world, blockpos.above())) {
                            world.setBlock(blockpos.above(), Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 2);
                            for (Direction facing : Direction.values())
                                if (random.nextFloat() < 0.3F && facing != Direction.DOWN)
                                    world.setBlock(blockpos.above().relative(facing), Blocks.OAK_LEAVES.defaultBlockState(), 2);
                            continue;
                        }
                        if (random.nextInt(15) == 0 && this.isTouchingAir(world, blockpos.above())) {
                            world.setBlock(blockpos.above(), Blocks.TALL_GRASS.defaultBlockState(), 2);
                            continue;
                        }
                        if (random.nextInt(15) == 0 && this.isTouchingAir(world, blockpos.above()))
                            world.setBlock(blockpos.above(), random.nextBoolean() ? Blocks.BROWN_MUSHROOM.defaultBlockState() : Blocks.RED_MUSHROOM.defaultBlockState(), 2);
                    }
                }
            }
            HydraEntity hydra = new HydraEntity(IafEntities.HYDRA.get(), world.getLevel());
            hydra.setVariant(random.nextInt(3));
            hydra.restrictTo(pivot, 15);
            hydra.absMoveTo(pivot.getX() + 0.5, pivot.getY() + 1.5, pivot.getZ() + 0.5, random.nextFloat() * 360, 0);
            world.addFreshEntity(hydra);
        }

        private boolean isTouchingAir(LevelAccessor worldIn, BlockPos pos) {
            for (Direction direction : Direction.Plane.HORIZONTAL)
                if (!worldIn.isEmptyBlock(pos.relative(direction)))
                    return false;
            return true;
        }
    }
}
