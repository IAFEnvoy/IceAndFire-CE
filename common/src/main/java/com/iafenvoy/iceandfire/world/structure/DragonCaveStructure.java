package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.HomePosition;
import com.iafenvoy.iceandfire.item.block.GoldPileBlock;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.iafenvoy.uranus.util.RandomHelper;
import com.iafenvoy.uranus.util.ShapeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class DragonCaveStructure extends Structure implements DangerousGeneration {
    protected DragonCaveStructure(Config config) {
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
        return Optional.of(new StructurePosition(blockPos, collector -> this.addPieces(collector, blockPos, context, context.random().nextBoolean())));
    }

    private void addPieces(StructurePiecesCollector collector, BlockPos pos, Context context, boolean male) {
        int y = context.world().getBottomY() + 40 + context.random().nextInt(30);
        long seed = context.random().nextLong();
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                collector.addPiece(this.createPiece(new BlockBox(pos.getX() + i * 32, y - 12, pos.getZ() + j * 32, pos.getX() + i * 32, y + 12, pos.getZ() + j * 32), male, new BlockPos(i * 32, 0, j * 32), y, seed));
    }

    protected abstract DragonCavePiece createPiece(BlockBox boundingBox, boolean male, BlockPos offset, int y, long seed);

    protected abstract double getGenerateChance();

    protected abstract static class DragonCavePiece extends StructurePiece {
        private final boolean male;
        private final BlockPos offset;
        private final int y;
        private final long seed;

        protected DragonCavePiece(StructurePieceType type, int length, BlockBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
            super(type, length, boundingBox);
            this.male = male;
            this.offset = offset;
            this.y = y;
            this.seed = seed;
        }

        public DragonCavePiece(StructurePieceType type, NbtCompound nbt) {
            super(type, nbt);
            this.male = nbt.getBoolean("male");
            this.offset = BlockPos.fromLong(nbt.getLong("offset"));
            this.y = nbt.getInt("down");
            this.seed = nbt.getLong("seed");
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            nbt.putBoolean("male", this.male);
            nbt.putLong("offset", this.offset.asLong());
            nbt.putInt("down", this.y);
            nbt.putLong("seed", this.seed);
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            random = new CheckedRandom(this.seed);
            // Center the position at the "middle" of the chunk
            BlockPos position = new BlockPos((chunkPos.x << 4) + 8, this.y, (chunkPos.z << 4) + 8).subtract(this.offset);
            int dragonAge = 75 + random.nextInt(50);
            int radius = (int) (dragonAge * 0.2F) + random.nextInt(4);
            this.generateCave(world, radius, 3, position, random);
            if (this.offset.equals(new BlockPos(0, 0, 0))) {
                DragonBaseEntity dragon = this.createDragon(world, random, position, dragonAge);
                world.spawnEntity(dragon);
            }
        }

        private boolean isOutOfRange(ChunkPos chunkPos, BlockPos blockPos) {
            return chunkPos.getStartX() - 16 > blockPos.getX() || blockPos.getX() > chunkPos.getEndX() + 16 ||
                    chunkPos.getStartZ() - 16 > blockPos.getZ() || blockPos.getZ() > chunkPos.getEndZ() + 16;
        }

        public void generateCave(WorldAccess worldIn, int radius, int amount, BlockPos center, Random random) {
            List<SphereInfo> sphereList = new ArrayList<>();
            sphereList.add(new SphereInfo(radius, center.toImmutable()));
            Stream<BlockPos> sphereBlocks = ShapeBuilder.start().getAllInCutOffSphereMutable(radius, radius / 2, center).toStream(false);
            Stream<BlockPos> hollowBlocks = ShapeBuilder.start().getAllInRandomlyDistributedRangeYCutOffSphereMutable(radius - 2, (int) ((radius - 2) * 0.75), (radius - 2) / 2, random, center).toStream(false);
            //Get shells
            //Get hollows
            for (int i = 0; i < amount + random.nextInt(2); i++) {
                Direction direction = Direction.Type.HORIZONTAL.random(random);
                int r = 2 * (int) (radius / 3F) + random.nextInt(8);
                BlockPos centerOffset = center.offset(direction, radius - 2);
                sphereBlocks = Stream.concat(sphereBlocks, ShapeBuilder.start().getAllInCutOffSphereMutable(r, r, centerOffset).toStream(false));
                hollowBlocks = Stream.concat(hollowBlocks, ShapeBuilder.start().getAllInRandomlyDistributedRangeYCutOffSphereMutable(r - 2, (int) ((r - 2) * 0.75), (r - 2) / 2, random, centerOffset).toStream(false));
                sphereList.add(new SphereInfo(r, centerOffset));
            }
            Set<BlockPos> shellBlocksSet = sphereBlocks.map(BlockPos::toImmutable).collect(Collectors.toSet());
            Set<BlockPos> hollowBlocksSet = hollowBlocks.map(BlockPos::toImmutable).collect(Collectors.toSet());
            shellBlocksSet.removeAll(hollowBlocksSet);

            //Remove blocks that is not belong to this piece
            ChunkPos chunkPos = new ChunkPos(center.add(this.offset));
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

        public void createShell(WorldAccess worldIn, Random rand, Set<BlockPos> positions) {
            List<Block> rareOres = this.getBlockList(IafBlockTags.DRAGON_CAVE_RARE_ORES);
            List<Block> uncommonOres = this.getBlockList(IafBlockTags.DRAGON_CAVE_UNCOMMON_ORES);
            List<Block> commonOres = this.getBlockList(IafBlockTags.DRAGON_CAVE_COMMON_ORES);
            List<Block> dragonTypeOres = this.getBlockList(this.getOreTag());
            positions.forEach(blockPos -> {
                if (!(worldIn.getBlockState(blockPos).getBlock() instanceof BlockWithEntity) && worldIn.getBlockState(blockPos).getHardness(worldIn, blockPos) >= 0) {
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
                            worldIn.setBlockState(blockPos, toPlace.getDefaultState(), Block.NOTIFY_LISTENERS);
                        else
                            worldIn.setBlockState(blockPos, this.getPaletteBlock(rand), Block.NOTIFY_LISTENERS);
                    } else
                        worldIn.setBlockState(blockPos, this.getPaletteBlock(rand), Block.NOTIFY_LISTENERS);
                }
            });
        }

        private List<Block> getBlockList(final TagKey<Block> tagKey) {
            return Registries.BLOCK.getEntryList(tagKey).map(holders -> holders.stream().map(RegistryEntry::value).toList()).orElse(Collections.emptyList());
        }

        public void hollowOut(WorldAccess worldIn, Set<BlockPos> positions) {
            positions.forEach(blockPos -> {
                if (!(worldIn.getBlockState(blockPos).getBlock() instanceof BlockWithEntity))
                    worldIn.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            });
        }

        public void decorateCave(WorldAccess worldIn, Random random, Set<BlockPos> positions, List<SphereInfo> spheres, BlockPos center) {
            for (SphereInfo sphere : spheres) {
                BlockPos pos = sphere.pos();
                int radius = sphere.radius();
                for (int i = 0; i < 15 + random.nextInt(10); i++)
                    this.getCeilingDecoration().generate(worldIn, random, pos.up(radius / 2 - 1).add(random.nextInt(radius) - radius / 2, 0, random.nextInt(radius) - radius / 2));
            }

            positions.forEach(blockPos -> {
                if (blockPos.getY() < center.getY()) {
                    BlockState stateBelow = worldIn.getBlockState(blockPos.down());
                    if ((stateBelow.isIn(BlockTags.BASE_STONE_OVERWORLD) || stateBelow.isIn(IafBlockTags.DRAGON_ENVIRONMENT_BLOCKS)) && worldIn.getBlockState(blockPos).isAir())
                        this.setGoldPile(worldIn, blockPos, random);
                }
            });
        }

        public void setGoldPile(WorldAccess world, BlockPos pos, Random random) {
            if (!(world.getBlockState(pos).getBlock() instanceof BlockWithEntity)) {
                int chance = random.nextInt(99) + 1;
                if (chance < 60) {
                    boolean generateGold = random.nextDouble() < IafCommonConfig.INSTANCE.dragon.generateDenGoldChance.getValue() * (this.male ? 1 : 2);
                    world.setBlockState(pos, generateGold ? this.getTreasurePile().with(GoldPileBlock.LAYERS, 1 + random.nextInt(7)) : Blocks.AIR.getDefaultState(), 3);
                } else if (chance == 61) {
                    world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.Type.HORIZONTAL.random(random)), Block.NOTIFY_LISTENERS);
                    if (world.getBlockState(pos).getBlock() instanceof ChestBlock) {
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        if (blockEntity instanceof ChestBlockEntity chestBlockEntity)
                            chestBlockEntity.setLootTable(this.getChestTable(this.male), random.nextLong());
                    }
                }
            }
        }

        private DragonBaseEntity createDragon(final StructureWorldAccess worldGen, final Random random, final BlockPos position, int dragonAge) {
            DragonBaseEntity dragon = this.getDragonType().create(worldGen.toServerWorld());
            assert dragon != null;
            dragon.setGender(this.male);
            dragon.growDragon(dragonAge);
            dragon.setAgingDisabled(true);
            dragon.setHealth(dragon.getMaxHealth());
            dragon.setVariant(RandomHelper.randomOne(dragon.dragonType.colors()).getName());
            dragon.updatePositionAndAngles(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, random.nextFloat() * 360, 0);
            dragon.setInSittingPose(true);
            dragon.homePos = new HomePosition(position, worldGen.toServerWorld());
            dragon.setHunger(50);
            return dragon;
        }

        protected abstract TagKey<Block> getOreTag();

        protected abstract WorldGenCaveStalactites getCeilingDecoration();

        protected abstract BlockState getTreasurePile();

        protected abstract BlockState getPaletteBlock(Random random);

        protected abstract RegistryKey<LootTable> getChestTable(boolean male);

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

        public void generate(WorldAccess worldIn, Random rand, BlockPos position) {
            int height = this.maxHeight + rand.nextInt(3);
            for (int i = 0; i < height; i++) {
                if (i < height / 2) {
                    worldIn.setBlockState(position.down(i).north(), this.block.getDefaultState(), 2);
                    worldIn.setBlockState(position.down(i).east(), this.block.getDefaultState(), 2);
                    worldIn.setBlockState(position.down(i).south(), this.block.getDefaultState(), 2);
                    worldIn.setBlockState(position.down(i).west(), this.block.getDefaultState(), 2);
                }
                worldIn.setBlockState(position.down(i), this.block.getDefaultState(), 2);
            }
        }
    }
}
