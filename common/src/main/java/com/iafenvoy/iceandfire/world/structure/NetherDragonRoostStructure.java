package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.HomePosition;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.registry.tag.CommonBlockTags;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.iafenvoy.uranus.util.RandomHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.StructureContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class NetherDragonRoostStructure extends DragonRoostStructure {
    public static final MapCodec<NetherDragonRoostStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(configCodecBuilder(instance)).apply(instance, NetherDragonRoostStructure::new));

    protected NetherDragonRoostStructure(Config config) {
        super(config);
    }

    @Override
    protected DragonRoostPiece createPiece(BlockBox boundingBox, boolean isMale) {
        return new NetherDragonRoostPiece(0, boundingBox, IafBlocks.GOLD_PILE.get(), isMale);
    }

    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        if (context.random().nextDouble() >= this.getGenerateChance())
            return Optional.empty();
        
        // For Nether structures, we need to avoid heightmap placement which can place structures on bedrock ceiling
        // Use fixed Y levels within the nether's buildable range (Y=0 to Y=127)
        BlockPos chunkPos = context.chunkPos().getStartPos();
        int x = chunkPos.getX() + context.random().nextInt(16);
        int z = chunkPos.getZ() + context.random().nextInt(16);
        
        // Generate Y level between 32 and 96 in the nether (good range for structures)
        // This avoids the bedrock ceiling (Y=127) and provides good terrain
        int y = 32 + context.random().nextInt(64); // 32 to 95
        
        BlockPos blockPos = new BlockPos(x, y, z);
        
        if (!this.isFarEnoughFromSpawn(blockPos) || blockPos.getY() <= context.world().getBottomY() + 2)
            return Optional.empty();
        
        return Optional.of(new StructurePosition(blockPos, collector -> collector.addPiece(this.createPiece(new BlockBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ()), context.random().nextBoolean()))));
    }

    @Override
    protected double getGenerateChance() {
        return IafCommonConfig.INSTANCE.worldGen.generateFireDragonRoostChance.getValue(); // Use fire dragon chance as base
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.NETHER_DRAGON_ROOST.get();
    }

    public static class NetherDragonRoostPiece extends DragonRoostPiece {
        private static final Identifier DRAGON_CHEST = Identifier.of(IceAndFire.MOD_ID, "chest/nether_dragon_roost");

        protected NetherDragonRoostPiece(int length, BlockBox boundingBox, Block treasureBlock, boolean isMale) {
            super(IafStructurePieces.NETHER_DRAGON_ROOST.get(), length, boundingBox, treasureBlock, isMale);
        }

        public NetherDragonRoostPiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.NETHER_DRAGON_ROOST.get(), nbt);
        }

        @Override
        protected EntityType<? extends DragonBaseEntity> getDragonType() {
            return IafEntities.NETHER_DRAGON.get();
        }

        @Override
        protected RegistryKey<LootTable> getRoostLootTable() {
            return RegistryKey.of(RegistryKeys.LOOT_TABLE, DRAGON_CHEST);
        }

        @Override
        protected BlockState transform(final BlockState state) {
            Block block = null;
            if (state.isOf(Blocks.GRASS_BLOCK))
                block = Blocks.NETHERRACK;
            else if (state.isOf(Blocks.DIRT_PATH))
                block = Blocks.NETHERRACK;
            else if (state.isIn(CommonBlockTags.GRAVELS))
                block = Blocks.GRAVEL;
            else if (state.isIn(BlockTags.DIRT))
                block = Blocks.NETHERRACK;
            else if (state.isIn(CommonBlockTags.STONES))
                block = Blocks.BLACKSTONE;
            else if (state.isIn(CommonBlockTags.COBBLESTONES))
                block = Blocks.BLACKSTONE;
            else if (state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.PLANKS))
                block = Blocks.CRIMSON_STEM;
            else if (state.isIn(IafBlockTags.GRASSES) || state.isIn(BlockTags.LEAVES) || state.isIn(BlockTags.FLOWERS) || state.isIn(BlockTags.CROPS))
                block = Blocks.AIR;
            if (block != null) return block.getDefaultState();
            return state;
        }

        @Override
        protected BlockPos getSurfacePosition(StructureWorldAccess level, BlockPos position) {
            // For nether structures, use the structure's origin Y level instead of heightmap
            // This prevents decorations from spawning on the bedrock ceiling
            // The surface should be at the origin Y level (top of the roost), not the bottom
            return new BlockPos(position.getX(), this.boundingBox.getMinY() + 1, position.getZ());
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            int radius = 12 + random.nextInt(8);
            
            // Clear terrain around the roost to prevent it from being covered
            this.clearTerrainAroundRoost(world, pivot, random, radius);
            
            // Spawn dragon at structure level instead of using heightmap
            this.spawnDragonAtStructureLevel(world, pivot, random, radius);
            // Call the individual generation methods (these are private in parent, so we need to duplicate them)
            this.generateSurface(world, pivot, random, radius);
            this.generateShell(world, pivot, random, radius);
            radius -= 2;
            this.hollowOut(world, pivot, radius);
            radius += 15;
            this.generateDecoration(world, pivot, random, radius, this.getIsMale());
        }

        private boolean getIsMale() {
            try {
                java.lang.reflect.Field isMaleField = this.getClass().getSuperclass().getDeclaredField("isMale");
                isMaleField.setAccessible(true);
                return isMaleField.getBoolean(this);
            } catch (Exception e) {
                return false; // Fallback
            }
        }

        private void spawnDragonAtStructureLevel(StructureWorldAccess world, BlockPos origin, Random random, int ageOffset) {
            DragonBaseEntity dragon = this.getDragonType().create(world.toServerWorld());
            assert dragon != null;
            dragon.setGender(this.getIsMale());
            dragon.growDragon(40 + ageOffset);
            dragon.setAgingDisabled(true);
            dragon.setHealth(dragon.getMaxHealth());
            dragon.setVariant(RandomHelper.randomOne(dragon.dragonType.colors()).getName());
            // Use the structure's origin Y level instead of heightmap for nether dragons
            dragon.updatePositionAndAngles(origin.getX() + 0.5, origin.getY() + 1.5, origin.getZ() + 0.5, random.nextFloat() * 360, 0);
            dragon.homePos = new HomePosition(origin, world.toServerWorld());
            dragon.hasHomePosition = true;
            dragon.setHunger(50);
            world.spawnEntity(dragon);
        }

        // Copy the private methods from the parent class so we can call them
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

        private void generateShell(StructureWorldAccess world, BlockPos origin, Random random, int radius) {
            int height = (radius / 5);
            double circularArea = this.getCircularArea(radius, height);

            BlockPos.stream(origin.add(-radius, -height, -radius), origin.add(radius, 1, radius)).map(BlockPos::toImmutable).forEach(position -> {
                if (position.getSquaredDistance(origin) < circularArea)
                    world.setBlockState(position, random.nextBoolean() ? Blocks.SOUL_SOIL.getDefaultState() : this.transform(Blocks.DIRT), Block.NOTIFY_LISTENERS);
                else if (position.getSquaredDistance(origin) == circularArea)
                    world.setBlockState(position, this.transform(Blocks.COBBLESTONE), Block.NOTIFY_LISTENERS);
            });
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
                        BlockPos surfacePosition = this.getSurfacePosition(world, position);
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

        @Override
        protected void handleCustomGeneration(StructureWorldAccess world, BlockPos origin, Random random, BlockPos position, double distance) {
            // Generate soul fire more frequently, especially on soul soil
            BlockPos surfacePos = this.getSurfacePosition(world, position);
            BlockState stateBelow = world.getBlockState(surfacePos.down());
            
            if (world.isAir(surfacePos)) {
                if (stateBelow.isOf(Blocks.SOUL_SOIL)) {
                    // Much more frequent on soul soil (1 in 200 chance)
                    if (random.nextInt(200) == 0) {
                        world.setBlockState(surfacePos, Blocks.SOUL_FIRE.getDefaultState(), Block.NOTIFY_LISTENERS);
                    }
                } else if (stateBelow.isOf(Blocks.NETHERRACK)) {
                    // Less frequent on netherrack (1 in 800 chance)
                    if (random.nextInt(800) == 0) {
                        world.setBlockState(surfacePos, Blocks.SOUL_FIRE.getDefaultState(), Block.NOTIFY_LISTENERS);
                    }
                }
            }
            
            // Generate nether-specific decorations
            if (random.nextInt(1000) == 0) {
                this.generateRoostPile(world, random, this.getSurfacePosition(world, position), Blocks.OBSIDIAN);
            }
            
            // Generate netherrack piles
            if (random.nextInt(1500) == 0) {
                this.generateRoostPile(world, random, this.getSurfacePosition(world, position), Blocks.NETHERRACK);
            }
            
            // Generate soulgma blocks - 3-5 per roost (separate from ore system)
            if (random.nextInt(800) == 0) { // Reduced frequency to ensure 3-5 total per roost
                if (world.isAir(surfacePos) && this.isValidSoulgmaSurface(world, surfacePos)) {
                    world.setBlockState(surfacePos, IafBlocks.SOULGMA_BLOCK.get().getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }
        }

        // Clear terrain around the roost to prevent it from being covered by terrain generation
        private void clearTerrainAroundRoost(StructureWorldAccess world, BlockPos origin, Random random, int radius) {
            // Clear a larger area around the roost to ensure it's not covered by terrain
            int clearRadius = radius + 5; // Clear 5 extra blocks around the roost
            int clearHeight = 10; // Clear 10 blocks high to ensure the roost area is clear
            
            BlockPos.stream(origin.add(-clearRadius, -clearHeight, -clearRadius), 
                           origin.add(clearRadius, clearHeight, clearRadius))
                .map(BlockPos::toImmutable)
                .forEach(position -> {
                    // Only clear blocks that are within the clear radius
                    if (position.getSquaredDistance(origin) <= clearRadius * clearRadius) {
                        BlockState currentState = world.getBlockState(position);
                        // Only clear solid blocks, leave air and important blocks alone
                        if (currentState.isSolid() && !currentState.isOf(Blocks.BEDROCK) && 
                            !currentState.isOf(Blocks.NETHER_PORTAL) && !currentState.isOf(Blocks.END_PORTAL)) {
                            world.setBlockState(position, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                        }
                    }
                });
        }

        // Copy additional methods from parent class
        private void generateTreasurePile(StructureWorldAccess world, Random random, BlockPos origin) {
            int layers = random.nextInt(3);

            for (int i = 0; i < layers; i++) {
                int radius = layers - i;
                double circularArea = this.getCircularArea(radius);

                for (BlockPos position : BlockPos.stream(origin.add(-radius, i, -radius), origin.add(radius, i, radius)).map(BlockPos::toImmutable).collect(java.util.stream.Collectors.toSet())) {
                    if (position.getSquaredDistance(origin) <= circularArea) {
                        position = this.getSurfacePosition(world, position);

                        if (this.treasureBlock instanceof com.iafenvoy.iceandfire.item.block.GoldPileBlock) {
                            BlockState state = world.getBlockState(position);
                            boolean placed = false;
                            // More permissive placement - allow on air, netherrack, or soul soil
                            if (state.isAir() || state.isOf(Blocks.NETHERRACK) || state.isOf(Blocks.SOUL_SOIL)) {
                                world.setBlockState(position, this.treasureBlock.getDefaultState().with(com.iafenvoy.iceandfire.item.block.GoldPileBlock.LAYERS, 1 + random.nextInt(7)), Block.NOTIFY_LISTENERS);
                                placed = true;
                            } else if (state.getBlock() instanceof net.minecraft.block.SnowBlock) {
                                world.setBlockState(position.down(), this.treasureBlock.getDefaultState().with(com.iafenvoy.iceandfire.item.block.GoldPileBlock.LAYERS, state.get(net.minecraft.block.SnowBlock.LAYERS)), Block.NOTIFY_LISTENERS);
                                placed = true;
                            }
                            if (placed && world.getBlockState(position.down()).getBlock() instanceof com.iafenvoy.iceandfire.item.block.GoldPileBlock)
                                world.setBlockState(position.down(), this.treasureBlock.getDefaultState().with(com.iafenvoy.iceandfire.item.block.GoldPileBlock.LAYERS, 8), Block.NOTIFY_LISTENERS);
                        }
                    }
                }
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
        
        private boolean isValidSoulgmaSurface(StructureWorldAccess world, BlockPos pos) {
            BlockState stateBelow = world.getBlockState(pos.down());
            return stateBelow.isOf(Blocks.SOUL_SOIL) || 
                   stateBelow.isOf(Blocks.NETHERRACK) || 
                   stateBelow.isOf(Blocks.BLACKSTONE) || 
                   stateBelow.isOf(Blocks.BASALT) ||
                   stateBelow.isIn(IafBlockTags.DRAGON_ENVIRONMENT_BLOCKS);
        }
    }
}
