package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.HomePosition;
import com.iafenvoy.iceandfire.item.block.GoldPileBlock;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.uranus.util.RandomHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

import java.util.Optional;
import java.util.stream.Collectors;

public abstract class DragonRoostStructure extends Structure implements DangerousGeneration {
    protected DragonRoostStructure(Config config) {
        super(config);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        if (context.random().nextDouble() >= this.getGenerateChance())
            return Optional.empty();
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (!this.isFarEnoughFromSpawn(blockPos) || blockPos.getY() <= context.world().getBottomY() + 2)
            return Optional.empty();
        return Optional.of(new StructurePosition(blockPos, collector -> collector.addPiece(this.createPiece(new BlockBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ()), context.random().nextBoolean()))));
    }

    protected abstract DragonRoostPiece createPiece(BlockBox boundingBox, boolean isMale);

    protected abstract double getGenerateChance();

    protected static abstract class DragonRoostPiece extends StructurePiece {
        protected final Block treasureBlock;
        private final boolean isMale;

        protected DragonRoostPiece(StructurePieceType type, int length, BlockBox boundingBox, Block treasureBlock, boolean isMale) {
            super(type, length, boundingBox);
            this.treasureBlock = treasureBlock;
            this.isMale = isMale;
        }

        public DragonRoostPiece(StructurePieceType type, NbtCompound nbt) {
            super(type, nbt);
            this.treasureBlock = Registries.BLOCK.get(Identifier.tryParse(nbt.getString("treasureBlock")));
            this.isMale = nbt.getBoolean("isMale");
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            nbt.putString("treasureBlock", Registries.BLOCK.getId(this.treasureBlock).toString());
            nbt.putBoolean("isMale", this.isMale);
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            int radius = 12 + random.nextInt(8);
            this.spawnDragon(world, pivot, random, radius, this.isMale);
            this.generateSurface(world, pivot, random, radius);
            this.generateShell(world, pivot, random, radius);
            radius -= 2;
            this.hollowOut(world, pivot, radius);
            radius += 15;
            this.generateDecoration(world, pivot, random, radius, this.isMale);
        }


        protected void generateRoostPile(StructureWorldAccess level, Random random, BlockPos position, Block block) {
            int radius = random.nextInt(4);

            for (int i = 0; i < radius; i++) {
                int layeredRadius = radius - i;
                double circularArea = this.getCircularArea(radius);
                BlockPos up = position.up(i);

                for (BlockPos blockpos : BlockPos.stream(up.add(-layeredRadius, 0, -layeredRadius), up.add(layeredRadius, 0, layeredRadius)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                    if (blockpos.getSquaredDistance(position) <= circularArea) {
                        level.setBlockState(blockpos, block.getDefaultState(), Block.NOTIFY_LISTENERS);
                    }
                }
            }
        }

        protected double getCircularArea(int radius, int height) {
            double area = (radius + height + radius) * 0.333F + 0.5F;
            return MathHelper.floor(area * area);
        }

        protected double getCircularArea(int radius) {
            double area = (radius + radius) * 0.333F + 0.5F;
            return MathHelper.floor(area * area);
        }

        protected BlockPos getSurfacePosition(StructureWorldAccess level, BlockPos position) {
            return level.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, position);
        }

        protected BlockState transform(Block block) {
            return this.transform(block.getDefaultState());
        }

        private void generateDecoration(StructureWorldAccess world, BlockPos origin, Random random, int radius, boolean isMale) {
            int height = (radius / 5);
            double circularArea = this.getCircularArea(radius, height);

            BlockPos.stream(origin.add(-radius, -height, -radius), origin.add(radius, height, radius)).map(BlockPos::toImmutable).forEach(position -> {
                if (position.getSquaredDistance(origin) <= circularArea) {
                    double distance = position.getSquaredDistance(origin) / circularArea;

                    if (!world.isAir(origin) && random.nextDouble() > distance * 0.5) {
                        BlockState state = world.getBlockState(position);

                        if (!(state.getBlock() instanceof BlockWithEntity) && state.getHardness(world, position) >= 0) {
                            BlockState transformed = this.transform(state);

                            if (transformed != state) {
                                world.setBlockState(position, transformed, Block.NOTIFY_LISTENERS);
                            }
                        }
                    }

                    this.handleCustomGeneration(world, origin, random, position, distance);
                    if (distance > 0.5 && random.nextInt(1000) == 0)
                        this.generateBoulder(world, random, this.getSurfacePosition(world, position), this.transform(Blocks.COBBLESTONE).getBlock(), random.nextInt(3), true);
                    if (distance < 0.3 && random.nextInt(isMale ? 200 : 300) == 0)
                        this.generateTreasurePile(world, random, position);

                    if (distance < 0.3D && random.nextInt(isMale ? 500 : 700) == 0) {
                        BlockPos surfacePosition = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, position);
                        boolean wasPlaced = world.setBlockState(surfacePosition, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.Type.HORIZONTAL.random(random)), Block.NOTIFY_LISTENERS);

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

        public void generateBoulder(WorldAccess worldIn, Random rand, BlockPos position, Block block, int startRadius, boolean replaceAir) {
            while (true) {
                if (position.getY() > 3) {
                    if (worldIn.isAir(position.down())) {
                        position = position.down();
                        continue;
                    }
                    BlockState b = worldIn.getBlockState(position.down());
                    if (!b.isIn(IafBlockTags.GRASSES) && !b.isOf(Blocks.DIRT) && !b.isOf(Blocks.STONE)) {
                        position = position.down();
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
                    for (BlockPos blockpos : BlockPos.stream(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet()))
                        if (blockpos.getSquaredDistance(position) <= (double) (f * f) && (replaceAir || worldIn.getBlockState(blockpos).isOpaque()))
                            worldIn.setBlockState(blockpos, block.getDefaultState(), 2);
                    position = position.add(-(startRadius + 1) + rand.nextInt(2 + startRadius * 2), -rand.nextInt(2), -(startRadius + 1) + rand.nextInt(2 + startRadius * 2));
                }
                break;
            }
        }

        private void generateArch(WorldAccess worldIn, Random random, BlockPos position, Block block) {
            int height = 3 + random.nextInt(3);
            int width = Math.min(3, height - 2);
            Direction direction = Direction.Type.HORIZONTAL.random(random);
            boolean diagonal = random.nextBoolean();
            for (int i = 0; i < height; i++)
                worldIn.setBlockState(position.up(i), block.getDefaultState(), 2);
            BlockPos offsetPos = position;
            int placedWidths = 0;
            for (int i = 0; i < width; i++) {
                offsetPos = position.up(height).offset(direction, i);
                if (diagonal)
                    offsetPos = position.up(height).offset(direction, i).offset(direction.rotateYClockwise(), i);
                if (placedWidths < width - 1 || random.nextBoolean())
                    worldIn.setBlockState(offsetPos, block.getDefaultState(), 2);
                placedWidths++;
            }
            while (worldIn.isAir(offsetPos.down()) && offsetPos.getY() > 0) {
                worldIn.setBlockState(offsetPos.down(), block.getDefaultState(), 2);
                offsetPos = offsetPos.down();
            }
        }

        private void hollowOut(StructureWorldAccess world, BlockPos origin, int radius) {
            int height = 2;
            double circularArea = this.getCircularArea(radius, height);
            BlockPos up = origin.up(height - 1);

            BlockPos.stream(up.add(-radius, 0, -radius), up.add(radius, height, radius)).map(BlockPos::toImmutable).forEach(position -> {
                if (position.getSquaredDistance(origin) <= circularArea)
                    world.setBlockState(position, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
            });
        }

        private void generateShell(StructureWorldAccess world, BlockPos origin, Random random, int radius) {
            int height = (radius / 5);
            double circularArea = this.getCircularArea(radius, height);

            BlockPos.stream(origin.add(-radius, -height, -radius), origin.add(radius, 1, radius)).map(BlockPos::toImmutable).forEach(position -> {
                if (position.getSquaredDistance(origin) < circularArea)
                    world.setBlockState(position, random.nextBoolean() ? this.transform(Blocks.GRAVEL) : this.transform(Blocks.DIRT), Block.NOTIFY_LISTENERS);
                else if (position.getSquaredDistance(origin) == circularArea)
                    world.setBlockState(position, this.transform(Blocks.COBBLESTONE), Block.NOTIFY_LISTENERS);
            });
        }

        private void generateSurface(StructureWorldAccess world, BlockPos origin, Random random, int radius) {
            int height = 2;
            double circularArea = this.getCircularArea(radius, height);

            BlockPos.stream(origin.add(-radius, height, -radius), origin.add(radius, 0, radius)).map(BlockPos::toImmutable).forEach(position -> {
                int heightDifference = position.getY() - origin.getY();

                if (position.getSquaredDistance(origin) <= circularArea && heightDifference < 2 + random.nextInt(height) && !world.isAir(position.down())) {
                    if (world.isAir(position.up()))
                        world.setBlockState(position, this.transform(Blocks.SHORT_GRASS), Block.NOTIFY_LISTENERS);
                    else
                        world.setBlockState(position, this.transform(Blocks.DIRT), Block.NOTIFY_LISTENERS);
                }
            });
        }

        private void generateTreasurePile(StructureWorldAccess world, Random random, BlockPos origin) {
            int layers = random.nextInt(3);

            for (int i = 0; i < layers; i++) {
                int radius = layers - i;
                double circularArea = this.getCircularArea(radius);

                for (BlockPos position : BlockPos.stream(origin.add(-radius, i, -radius), origin.add(radius, i, radius)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                    if (position.getSquaredDistance(origin) <= circularArea) {
                        position = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, position);

                        if (this.treasureBlock instanceof GoldPileBlock) {
                            BlockState state = world.getBlockState(position);
                            boolean placed = false;
                            if (state.isAir()) {
                                world.setBlockState(position, this.treasureBlock.getDefaultState().with(GoldPileBlock.LAYERS, 1 + random.nextInt(7)), Block.NOTIFY_LISTENERS);
                                placed = true;
                            } else if (state.getBlock() instanceof SnowBlock) {
                                world.setBlockState(position.down(), this.treasureBlock.getDefaultState().with(GoldPileBlock.LAYERS, state.get(SnowBlock.LAYERS)), Block.NOTIFY_LISTENERS);
                                placed = true;
                            }
                            if (placed && world.getBlockState(position.down()).getBlock() instanceof GoldPileBlock)
                                world.setBlockState(position.down(), this.treasureBlock.getDefaultState().with(GoldPileBlock.LAYERS, 8), Block.NOTIFY_LISTENERS);
                        }
                    }
                }
            }
        }

        private void spawnDragon(StructureWorldAccess world, BlockPos origin, Random random, int ageOffset, boolean isMale) {
            DragonBaseEntity dragon = this.getDragonType().create(world.toServerWorld());
            assert dragon != null;
            dragon.setGender(isMale);
            dragon.growDragon(40 + ageOffset);
            dragon.setAgingDisabled(true);
            dragon.setHealth(dragon.getMaxHealth());
            dragon.setVariant(RandomHelper.randomOne(dragon.dragonType.colors()).getName());
            dragon.updatePositionAndAngles(origin.getX() + 0.5, world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, origin).getY() + 1.5, origin.getZ() + 0.5, random.nextFloat() * 360, 0);
            dragon.homePos = new HomePosition(origin, world.toServerWorld());
            dragon.hasHomePosition = true;
            dragon.setHunger(50);
            world.spawnEntity(dragon);
        }

        protected abstract EntityType<? extends DragonBaseEntity> getDragonType();

        protected abstract RegistryKey<LootTable> getRoostLootTable();

        protected abstract BlockState transform(BlockState block);

        protected abstract void handleCustomGeneration(StructureWorldAccess world, BlockPos origin, Random random, BlockPos position, double distance);
    }
}
