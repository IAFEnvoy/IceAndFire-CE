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
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;
import java.util.stream.Collectors;

public class HydraCaveStructure extends Structure implements DangerousGeneration {
    public static final MapCodec<HydraCaveStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(configCodecBuilder(instance)).apply(instance, HydraCaveStructure::new));

    protected HydraCaveStructure(Config config) {
        super(config);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        if (context.random().nextDouble() >= IafCommonConfig.INSTANCE.worldGen.generateHydraCaveChance.getValue())
            return Optional.empty();
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (!this.isFarEnoughFromSpawn(blockPos)) return Optional.empty();
        return Optional.of(new StructurePosition(blockPos, collector -> collector.addPiece(new HydraCavePiece(0, new BlockBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ())))));
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.HYDRA_CAVE.get();
    }

    public static class HydraCavePiece extends StructurePiece {
        public static final RegistryKey<LootTable> HYDRA_CHEST = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(IceAndFire.MOD_ID, "chest/hydra_cave"));

        protected HydraCavePiece(int length, BlockBox boundingBox) {
            super(IafStructurePieces.HYDRA_CAVE.get(), length, boundingBox);
        }

        public HydraCavePiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.HYDRA_CAVE.get(), nbt);
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            int i1 = 8;
            int i2 = i1 - 2;
            {
                int ySize = random.nextInt(2);
                int j = i1 + random.nextInt(2);
                int k = 5 + ySize;
                int l = i1 + random.nextInt(2);
                float f = (j + k + l) * 0.333F + 0.5F;

                for (BlockPos blockpos : BlockPos.stream(pivot.add(-j, -k, -l), pivot.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                    boolean doorwayX = blockpos.getX() >= pivot.getX() - 2 + random.nextInt(2) && blockpos.getX() <= pivot.getX() + 2 + random.nextInt(2);
                    boolean doorwayZ = blockpos.getZ() >= pivot.getZ() - 2 + random.nextInt(2) && blockpos.getZ() <= pivot.getZ() + 2 + random.nextInt(2);
                    boolean isNotInDoorway = !doorwayX && !doorwayZ && blockpos.getY() > pivot.getY() || blockpos.getY() > pivot.getY() + k - (1 + random.nextInt(2));
                    if (blockpos.getSquaredDistance(pivot) <= f * f) {
                        if (!(world.getBlockState(pivot).getBlock() instanceof ChestBlock) && isNotInDoorway) {
                            world.setBlockState(blockpos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
                            if (world.getBlockState(pivot.down()).getBlock() == Blocks.GRASS_BLOCK)
                                world.setBlockState(blockpos.down(), Blocks.DIRT.getDefaultState(), 3);
                            if (random.nextInt(4) == 0)
                                world.setBlockState(blockpos.up(), Blocks.SHORT_GRASS.getDefaultState(), 2);
                            if (random.nextInt(9) == 0)
                                world.getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).getEntry(TreeConfiguredFeatures.SWAMP_OAK).ifPresent(holder -> holder.value().generate(world, chunkGenerator, random, blockpos.up()));
                        }
                        if (blockpos.getY() == pivot.getY())
                            world.setBlockState(blockpos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
                        if (blockpos.getY() <= pivot.getY() - 1 && !world.getBlockState(blockpos).isOpaque())
                            world.setBlockState(blockpos, Blocks.STONE.getDefaultState(), 3);
                    }
                }
            }
            {
                int ySize = random.nextInt(2);
                int j = i2 + random.nextInt(2);
                int k = 4 + ySize;
                int l = i2 + random.nextInt(2);
                float f = (j + k + l) * 0.333F + 0.5F;
                for (BlockPos blockpos : BlockPos.stream(pivot.add(-j, -k, -l), pivot.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet()))
                    if (blockpos.getSquaredDistance(pivot) <= f * f && blockpos.getY() > pivot.getY())
                        if (!(world.getBlockState(pivot).getBlock() instanceof ChestBlock))
                            world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 3);
                for (BlockPos blockpos : BlockPos.stream(pivot.add(-j, -k, -l), pivot.add(j, k + 8, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                    if (blockpos.getSquaredDistance(pivot) <= f * f && blockpos.getY() == pivot.getY()) {
                        if (random.nextInt(30) == 0 && this.isTouchingAir(world, blockpos.up())) {
                            world.setBlockState(blockpos.up(1), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.Type.HORIZONTAL.random(random)), 2);
                            if (world.getBlockState(blockpos.up(1)).getBlock() instanceof ChestBlock)
                                if (world.getBlockEntity(blockpos.up(1)) instanceof ChestBlockEntity chest)
                                    chest.setLootTable(HYDRA_CHEST, random.nextLong());
                            continue;
                        }
                        if (random.nextInt(45) == 0 && this.isTouchingAir(world, blockpos.up())) {
                            world.setBlockState(blockpos.up(), Blocks.SKELETON_SKULL.getDefaultState().with(SkullBlock.ROTATION, random.nextInt(15)), 2);
                            continue;
                        }
                        if (random.nextInt(35) == 0 && this.isTouchingAir(world, blockpos.up())) {
                            world.setBlockState(blockpos.up(), Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true), 2);
                            for (Direction facing : Direction.values())
                                if (random.nextFloat() < 0.3F && facing != Direction.DOWN)
                                    world.setBlockState(blockpos.up().offset(facing), Blocks.OAK_LEAVES.getDefaultState(), 2);
                            continue;
                        }
                        if (random.nextInt(15) == 0 && this.isTouchingAir(world, blockpos.up())) {
                            world.setBlockState(blockpos.up(), Blocks.TALL_GRASS.getDefaultState(), 2);
                            continue;
                        }
                        if (random.nextInt(15) == 0 && this.isTouchingAir(world, blockpos.up()))
                            world.setBlockState(blockpos.up(), random.nextBoolean() ? Blocks.BROWN_MUSHROOM.getDefaultState() : Blocks.RED_MUSHROOM.getDefaultState(), 2);
                    }
                }
            }
            HydraEntity hydra = new HydraEntity(IafEntities.HYDRA.get(), world.toServerWorld());
            hydra.setVariant(random.nextInt(3));
            hydra.setPositionTarget(pivot, 15);
            hydra.updatePositionAndAngles(pivot.getX() + 0.5, pivot.getY() + 1.5, pivot.getZ() + 0.5, random.nextFloat() * 360, 0);
            world.spawnEntity(hydra);
        }

        private boolean isTouchingAir(WorldAccess worldIn, BlockPos pos) {
            for (Direction direction : Direction.Type.HORIZONTAL)
                if (!worldIn.isAir(pos.offset(direction)))
                    return false;
            return true;
        }
    }
}
