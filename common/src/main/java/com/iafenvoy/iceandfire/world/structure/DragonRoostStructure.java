package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.HomePosition;
import com.iafenvoy.iceandfire.item.block.PileBlock;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.uranus.util.RandomHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
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
import net.minecraft.world.gen.structure.StructureType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DragonRoostStructure extends Structure implements DangerousGeneration {
    public static final MapCodec<DragonRoostStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    configCodecBuilder(instance),
                    BlockTransformRule.CODEC.listOf().fieldOf("block_transform").forGetter(s -> s.blockTransform),
                    Registries.ENTITY_TYPE.getCodec().fieldOf("dragon_type").forGetter(s -> s.dragonType),
                    Identifier.CODEC.fieldOf("loot_table").forGetter(s -> s.lootTable),
                    Registries.BLOCK.getCodec().fieldOf("treasure_block").forGetter(s -> s.treasureBlock),
                    Registries.BLOCK.getCodec().optionalFieldOf("pile_block").forGetter(s -> Optional.ofNullable(s.pileBlock)),
                    Codec.BOOL.optionalFieldOf("generate_spires", false).forGetter(s -> s.generateSpires),
                    Codec.doubleRange(0.0, 1.0).optionalFieldOf("generate_chance", 0.5).forGetter(s -> s.generateChance)
            ).apply(instance, DragonRoostStructure::new));

    private final List<BlockTransformRule> blockTransform;
    private final EntityType<?> dragonType;
    private final Identifier lootTable;
    private final Block treasureBlock;
    private final Block pileBlock;
    private final boolean generateSpires;
    private final double generateChance;

    public DragonRoostStructure(Config config, List<BlockTransformRule> blockTransform, EntityType<?> dragonType,
                                 Identifier lootTable, Block treasureBlock, Optional<Block> pileBlock,
                                 boolean generateSpires, double generateChance) {
        super(config);
        this.blockTransform = blockTransform;
        this.dragonType = dragonType;
        this.lootTable = lootTable;
        this.treasureBlock = treasureBlock;
        this.pileBlock = pileBlock.orElse(null);
        this.generateSpires = generateSpires;
        this.generateChance = generateChance;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        if (context.random().nextDouble() >= this.generateChance)
            return Optional.empty();
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (!this.isFarEnoughFromSpawn(blockPos) || blockPos.getY() <= context.world().getBottomY() + 2)
            return Optional.empty();
        return Optional.of(new StructurePosition(blockPos, collector -> collector.addPiece(
                new DragonRoostPiece(0,
                        new BlockBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ()),
                        this.treasureBlock, context.random().nextBoolean(),
                        this.blockTransform, this.dragonType, this.lootTable, this.pileBlock, this.generateSpires))));
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.DRAGON_ROOST.get();
    }

    public static class DragonRoostPiece extends StructurePiece {
        private final Block treasureBlock;
        private final boolean isMale;
        private final List<BlockTransformRule> blockTransform;
        private final EntityType<?> dragonType;
        private final Identifier lootTable;
        private final Block pileBlock;
        private final boolean generateSpires;

        protected DragonRoostPiece(int length, BlockBox boundingBox, Block treasureBlock, boolean isMale,
                                    List<BlockTransformRule> blockTransform, EntityType<?> dragonType,
                                    Identifier lootTable, Block pileBlock, boolean generateSpires) {
            super(IafStructurePieces.DRAGON_ROOST.get(), length, boundingBox);
            this.treasureBlock = treasureBlock;
            this.isMale = isMale;
            this.blockTransform = blockTransform;
            this.dragonType = dragonType;
            this.lootTable = lootTable;
            this.pileBlock = pileBlock;
            this.generateSpires = generateSpires;
        }

        public DragonRoostPiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.DRAGON_ROOST.get(), nbt);
            this.treasureBlock = Registries.BLOCK.get(Identifier.tryParse(nbt.getString("treasureBlock")));
            this.isMale = nbt.getBoolean("isMale");
            this.dragonType = Registries.ENTITY_TYPE.get(Identifier.tryParse(nbt.getString("dragonType")));
            this.lootTable = Identifier.tryParse(nbt.getString("lootTable"));
            String pileBlockStr = nbt.getString("pileBlock");
            this.pileBlock = pileBlockStr.isEmpty() ? null : Registries.BLOCK.get(Identifier.tryParse(pileBlockStr));
            this.generateSpires = nbt.getBoolean("generateSpires");
            NbtElement transformNbt = nbt.get("blockTransform");
            this.blockTransform = transformNbt != null
                    ? BlockTransformRule.CODEC.listOf().parse(NbtOps.INSTANCE, transformNbt).result().orElse(List.of())
                    : List.of();
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            nbt.putString("treasureBlock", Registries.BLOCK.getId(this.treasureBlock).toString());
            nbt.putBoolean("isMale", this.isMale);
            nbt.putString("dragonType", Registries.ENTITY_TYPE.getId(this.dragonType).toString());
            nbt.putString("lootTable", this.lootTable.toString());
            nbt.putString("pileBlock", this.pileBlock != null ? Registries.BLOCK.getId(this.pileBlock).toString() : "");
            nbt.putBoolean("generateSpires", this.generateSpires);
            BlockTransformRule.CODEC.listOf()
                    .encodeStart(NbtOps.INSTANCE, this.blockTransform)
                    .result()
                    .ifPresent(encoded -> nbt.put("blockTransform", encoded));
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            if (!chunkBox.contains(pivot))
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

        private BlockState transform(BlockState state) {
            return this.blockTransform.stream()
                    .filter(r -> state.isOf(r.from()))
                    .findFirst()
                    .map(r -> r.to().getDefaultState())
                    .orElse(state);
        }

        private BlockState transform(Block block) {
            return this.transform(block.getDefaultState());
        }

        private void generateRoostPile(StructureWorldAccess level, Random random, BlockPos position, Block block) {
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

        private double getCircularArea(int radius, int height) {
            double area = (radius + height + radius) * 0.333F + 0.5F;
            return MathHelper.floor(area * area);
        }

        private double getCircularArea(int radius) {
            double area = (radius + radius) * 0.333F + 0.5F;
            return MathHelper.floor(area * area);
        }

        private BlockPos getSurfacePosition(StructureWorldAccess level, BlockPos position) {
            return level.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, position);
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
                                chest.setLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, this.lootTable), random.nextLong());
                        }
                    }
                    if (random.nextInt(5000) == 0)
                        this.generateArch(world, random, this.getSurfacePosition(world, position), this.transform(Blocks.COBBLESTONE).getBlock());
                }
            });
        }

        private void handleCustomGeneration(StructureWorldAccess world, BlockPos origin, Random random, BlockPos position, double distance) {
            if (this.pileBlock != null && random.nextInt(1000) == 0)
                this.generateRoostPile(world, random, this.getSurfacePosition(world, position), this.pileBlock);
            if (this.generateSpires) {
                if (distance > 0.05D && random.nextInt(800) == 0)
                    this.generateSpire(world, random, this.getSurfacePosition(world, position));
                if (distance > 0.05D && random.nextInt(1000) == 0)
                    this.generateSpike(world, random, this.getSurfacePosition(world, position), Direction.Type.HORIZONTAL.random(random));
            }
        }

        private void generateSpike(WorldAccess worldIn, Random rand, BlockPos position, Direction direction) {
            Block stoneBlock = this.transform(Blocks.STONE).getBlock();
            int radius = 5;
            for (int i = 0; i < 5; i++) {
                int j = Math.max(0, radius - (int) (i * 1.75F));
                int l = radius - i;
                int k = Math.max(0, radius - (int) (i * 1.5F));
                float f = (float) (j + l) * 0.333F + 0.5F;
                BlockPos up = position.up().offset(direction, i);
                int xOrZero = direction.getAxis() == Direction.Axis.Z ? j : 0;
                int zOrZero = direction.getAxis() == Direction.Axis.Z ? 0 : k;
                for (BlockPos blockpos : BlockPos.stream(up.add(-xOrZero, -l, -zOrZero), up.add(xOrZero, l, zOrZero)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                    if (blockpos.getSquaredDistance(position) <= (double) (f * f)) {
                        int height = Math.max(blockpos.getY() - up.getY(), 0);
                        if (i == 0) {
                            if (rand.nextFloat() < height * 0.3F)
                                worldIn.setBlockState(blockpos, stoneBlock.getDefaultState(), 2);
                        } else worldIn.setBlockState(blockpos, stoneBlock.getDefaultState(), 2);
                    }
                }
            }
        }

        private void generateSpire(WorldAccess worldIn, Random rand, BlockPos position) {
            Block stoneBlock = this.transform(Blocks.STONE).getBlock();
            Block gravelBlock = this.transform(Blocks.GRAVEL).getBlock();
            Block cobbleBlock = this.transform(Blocks.COBBLESTONE).getBlock();
            int height = 5 + rand.nextInt(5);
            Direction bumpDirection = Direction.NORTH;
            for (int i = 0; i < height; i++) {
                worldIn.setBlockState(position.up(i), stoneBlock.getDefaultState(), 2);
                if (rand.nextBoolean()) {
                    bumpDirection = bumpDirection.rotateYClockwise();
                }
                int offset = 1;
                if (i < 4) {
                    worldIn.setBlockState(position.up(i).north(), gravelBlock.getDefaultState(), 2);
                    worldIn.setBlockState(position.up(i).south(), gravelBlock.getDefaultState(), 2);
                    worldIn.setBlockState(position.up(i).east(), gravelBlock.getDefaultState(), 2);
                    worldIn.setBlockState(position.up(i).west(), gravelBlock.getDefaultState(), 2);
                    offset = 2;
                }
                if (i < height - 2)
                    worldIn.setBlockState(position.up(i).offset(bumpDirection, offset), cobbleBlock.getDefaultState(), 2);
            }
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

            int real_radius = (int) Math.sqrt(circularArea);
            super.boundingBox = new BlockBox(origin.getX() - real_radius, origin.getY(), origin.getZ() - real_radius, origin.getX() + real_radius, origin.getY() + 3, origin.getZ() + real_radius);

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

                        if (this.treasureBlock instanceof PileBlock) {
                            BlockState state = world.getBlockState(position);
                            boolean placed = false;
                            if (state.isAir()) {
                                world.setBlockState(position, this.treasureBlock.getDefaultState().with(PileBlock.LAYERS, 1 + random.nextInt(7)), Block.NOTIFY_LISTENERS);
                                placed = true;
                            } else if (state.getBlock() instanceof SnowBlock) {
                                world.setBlockState(position.down(), this.treasureBlock.getDefaultState().with(PileBlock.LAYERS, state.get(SnowBlock.LAYERS)), Block.NOTIFY_LISTENERS);
                                placed = true;
                            }
                            if (placed && world.getBlockState(position.down()).getBlock() instanceof PileBlock)
                                world.setBlockState(position.down(), this.treasureBlock.getDefaultState().with(PileBlock.LAYERS, 8), Block.NOTIFY_LISTENERS);
                        }
                    }
                }
            }
        }

        private void spawnDragon(StructureWorldAccess world, BlockPos origin, Random random, int ageOffset, boolean isMale) {
            Entity entity = this.dragonType.create(world.toServerWorld());
            if (entity instanceof DragonBaseEntity dragon) {
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
        }
    }
}
