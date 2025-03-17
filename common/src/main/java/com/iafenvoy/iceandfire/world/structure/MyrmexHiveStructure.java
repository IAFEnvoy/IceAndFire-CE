package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.*;
import com.iafenvoy.iceandfire.entity.util.MyrmexHive;
import com.iafenvoy.iceandfire.item.block.BlockGoldPile;
import com.iafenvoy.iceandfire.item.block.BlockMyrmexBiolight;
import com.iafenvoy.iceandfire.item.block.BlockMyrmexConnectedResin;
import com.iafenvoy.iceandfire.item.block.BlockMyrmexResin;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.util.RestrictWorldAccess;
import com.iafenvoy.iceandfire.world.GenerationConstants;
import com.iafenvoy.iceandfire.world.MyrmexWorldData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyrmexHiveStructure extends Structure {
    public static final Codec<MyrmexHiveStructure> CODEC = RecordCodecBuilder.<MyrmexHiveStructure>mapCodec(instance -> instance.group(
            configCodecBuilder(instance),
            Codec.BOOL.fieldOf("jungle").forGetter(structure -> structure.jungle)
    ).apply(instance, MyrmexHiveStructure::new)).codec();
    private final boolean jungle;

    protected MyrmexHiveStructure(Config config, boolean jungle) {
        super(config);
        this.jungle = jungle;
    }

    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        if (context.random().nextDouble() >= (this.jungle ? IafCommonConfig.INSTANCE.worldGen.generateMyemexHiveJungleChance : IafCommonConfig.INSTANCE.worldGen.generateMyemexHiveDesertChance).getValue())
            return Optional.empty();
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (!GenerationConstants.isFarEnoughFromSpawn(blockPos)) return Optional.empty();
        return Optional.of(new StructurePosition(blockPos, collector -> this.addPieces(collector, blockPos, context)));
    }

    private void addPieces(StructurePiecesCollector collector, BlockPos pos, Context context) {
        int down = Math.max(15, pos.getY() - 16 + context.random().nextInt(5));
        long seed = context.random().nextLong();
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                collector.addPiece(new MyrmexHivePiece(0, new BlockBox(pos.getX() + i * 32, pos.getY(), pos.getZ() + j * 32, pos.getX() + i * 32, pos.getY(), pos.getZ() + j * 32), new BlockPos(i * 32, 0, j * 32), down, seed, false, this.jungle));
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.MYRMEX_HIVE.get();
    }

    public static MyrmexHivePiece placeSmallGen(boolean isJungle, StructureWorldAccess world, Random random, BlockPos pos) {
        int down = Math.max(15, pos.getY() - 20 + random.nextInt(10));
        MyrmexHivePiece piece = new MyrmexHivePiece(0, new BlockBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ()), new BlockPos(0, 0, 0), down, random.nextLong(), true, isJungle);
        piece.hasFoodRoom = false;
        piece.hasNursery = false;
        piece.totalRooms = 0;
        piece.entrances = 0;
        piece.generateMainRoom(world, random, pos);
        return piece;
    }

    public static class MyrmexHivePiece extends StructurePiece {
        public static final Identifier MYRMEX_GOLD_CHEST = Identifier.of(IceAndFire.MOD_ID, "chest/myrmex_loot_chest");
        public static final Identifier DESERT_MYRMEX_FOOD_CHEST = Identifier.of(IceAndFire.MOD_ID, "chest/myrmex_desert_food_chest");
        public static final Identifier JUNGLE_MYRMEX_FOOD_CHEST = Identifier.of(IceAndFire.MOD_ID, "chest/myrmex_jungle_food_chest");
        public static final Identifier MYRMEX_TRASH_CHEST = Identifier.of(IceAndFire.MOD_ID, "chest/myrmex_trash_chest");
        private static final BlockState DESERT_RESIN = IafBlocks.MYRMEX_DESERT_RESIN.get().getDefaultState();
        private static final BlockState STICKY_DESERT_RESIN = IafBlocks.MYRMEX_DESERT_RESIN_STICKY.get().getDefaultState();
        private static final BlockState JUNGLE_RESIN = IafBlocks.MYRMEX_JUNGLE_RESIN.get().getDefaultState();
        private static final BlockState STICKY_JUNGLE_RESIN = IafBlocks.MYRMEX_JUNGLE_RESIN_STICKY.get().getDefaultState();
        private final BlockPos offset;
        private final int y;
        private final long seed;

        public MyrmexHive hive;
        private int entrances = 0;
        private int totalRooms;
        private boolean hasFoodRoom;
        private boolean hasNursery;
        private final boolean small, jungle;

        protected MyrmexHivePiece(int length, BlockBox boundingBox, BlockPos offset, int y, long seed, boolean small, boolean jungle) {
            super(IafStructurePieces.MYRMEX_HIVE.get(), length, boundingBox);
            this.offset = offset;
            this.y = y;
            this.seed = seed;
            this.small = small;
            this.jungle = jungle;
        }

        public MyrmexHivePiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.MYRMEX_HIVE.get(), nbt);
            this.offset = BlockPos.fromLong(nbt.getLong("offset"));
            this.y = nbt.getInt("down");
            this.seed = nbt.getLong("seed");
            this.small = nbt.getBoolean("small");
            this.jungle = nbt.getBoolean("jungle");
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            nbt.putLong("offset", this.offset.asLong());
            nbt.putInt("down", this.y);
            nbt.putLong("seed", this.seed);
            nbt.putBoolean("small", this.small);
            nbt.putBoolean("jungle", this.jungle);
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            Random rand = new CheckedRandom(this.seed);
            this.hasFoodRoom = false;
            this.hasNursery = false;
            this.totalRooms = 0;
            BlockPos undergroundPos = new BlockPos(pivot.getX(), this.y, pivot.getZ());
            this.entrances = 0;
            this.generateMainRoom(new RestrictWorldAccess(world, pos -> this.isIn(chunkPos, pos)), rand, undergroundPos);
        }

        private boolean isIn(ChunkPos chunkPos, BlockPos blockPos) {
            return chunkPos.getStartX() - 16 <= blockPos.getX() && blockPos.getX() <= chunkPos.getEndX() + 16 &&
                    chunkPos.getStartZ() - 16 <= blockPos.getZ() && blockPos.getZ() <= chunkPos.getEndZ() + 16;
        }

        private void generateMainRoom(ServerWorldAccess world, Random rand, BlockPos position) {
            this.hive = new MyrmexHive(world.toServerWorld(), position, 100);
            MyrmexWorldData.addHive(world.toServerWorld(), this.hive);
            BlockState resin = this.jungle ? JUNGLE_RESIN : DESERT_RESIN;
            BlockState sticky_resin = this.jungle ? STICKY_JUNGLE_RESIN : STICKY_DESERT_RESIN;
            this.generateSphere(world, rand, position, 14, 7, resin, sticky_resin);
            this.generateSphere(world, rand, position, 12, 5, Blocks.AIR.getDefaultState());
            this.decorateSphere(world, rand, position, 12, 5, RoomType.QUEEN);
            this.generatePath(world, rand, position.offset(Direction.NORTH, 7).down(), 8 + rand.nextInt(10), Direction.NORTH, 100);
            this.generatePath(world, rand, position.offset(Direction.SOUTH, 7).down(), 8 + rand.nextInt(10), Direction.SOUTH, 100);
            this.generatePath(world, rand, position.offset(Direction.WEST, 7).down(), 8 + rand.nextInt(10), Direction.WEST, 100);
            this.generatePath(world, rand, position.offset(Direction.EAST, 7).down(), 8 + rand.nextInt(10), Direction.EAST, 100);
            if (!this.small) {
                EntityMyrmexQueen queen = new EntityMyrmexQueen(IafEntities.MYRMEX_QUEEN.get(), world.toServerWorld());
                BlockPos ground = MyrmexHive.getGroundedPos(world, position);
                queen.initialize(world, world.getLocalDifficulty(ground), SpawnReason.CHUNK_GENERATION, null, null);
                queen.setHive(this.hive);
                queen.setJungleVariant(this.jungle);
                queen.updatePositionAndAngles(ground.getX() + 0.5D, ground.getY() + 1D, ground.getZ() + 0.5D, 0, 0);
                world.spawnEntity(queen);

                for (int i = 0; i < 4 + rand.nextInt(3); i++) {
                    EntityMyrmexBase myrmex = new EntityMyrmexWorker(IafEntities.MYRMEX_WORKER.get(), world.toServerWorld());
                    myrmex.initialize(world, world.getLocalDifficulty(ground), SpawnReason.CHUNK_GENERATION, null, null);
                    myrmex.setHive(this.hive);
                    myrmex.updatePositionAndAngles(ground.getX() + 0.5D, ground.getY() + 1D, ground.getZ() + 0.5D, 0, 0);
                    myrmex.setJungleVariant(this.jungle);
                    world.spawnEntity(myrmex);
                }
                for (int i = 0; i < 2 + rand.nextInt(2); i++) {
                    EntityMyrmexBase myrmex = new EntityMyrmexSoldier(IafEntities.MYRMEX_SOLDIER.get(), world.toServerWorld());
                    myrmex.initialize(world, world.getLocalDifficulty(ground), SpawnReason.CHUNK_GENERATION, null, null);
                    myrmex.setHive(this.hive);
                    myrmex.updatePositionAndAngles(ground.getX() + 0.5D, ground.getY() + 1D, ground.getZ() + 0.5D, 0, 0);
                    myrmex.setJungleVariant(this.jungle);
                    world.spawnEntity(myrmex);
                }
                for (int i = 0; i < rand.nextInt(2); i++) {
                    EntityMyrmexBase myrmex = new EntityMyrmexSentinel(IafEntities.MYRMEX_SENTINEL.get(), world.toServerWorld());
                    myrmex.initialize(world, world.getLocalDifficulty(ground), SpawnReason.CHUNK_GENERATION, null, null);
                    myrmex.setHive(this.hive);
                    myrmex.updatePositionAndAngles(ground.getX() + 0.5D, ground.getY() + 1D, ground.getZ() + 0.5D, 0, 0);
                    myrmex.setJungleVariant(this.jungle);
                    world.spawnEntity(myrmex);
                }
            }
        }

        private void generatePath(WorldAccess world, Random rand, BlockPos offset, int length, Direction direction, int roomChance) {
            if (roomChance == 0) return;
            if (this.small) {
                length /= 2;
                if (this.entrances < 1) {
                    for (int i = 0; i < length; i++)
                        this.generateCircle(world, rand, offset.offset(direction, i), direction);
                    this.generateEntrance(world, rand, offset.offset(direction, length), direction);
                } else if (this.totalRooms < 2) {
                    for (int i = 0; i < length; i++)
                        this.generateCircle(world, rand, offset.offset(direction, i), direction);
                    this.generateRoom(world, rand, offset.offset(direction, length), 6, roomChance / 2, direction);
                    for (int i = -3; i < 3; i++) {
                        this.generateCircleAir(world, rand, offset.offset(direction, i), direction);
                        this.generateCircleAir(world, rand, offset.offset(direction, length + i), direction);
                    }
                    this.totalRooms++;
                }
            } else if (rand.nextInt(100) < roomChance)
                if (this.entrances < 3 && rand.nextInt(1 + this.entrances * 2) == 0 && this.hasFoodRoom && this.hasNursery && this.totalRooms > 3 || this.entrances == 0)
                    this.generateEntrance(world, rand, offset.offset(direction, 1), direction);
                else {
                    for (int i = 0; i < length; i++)
                        this.generateCircle(world, rand, offset.offset(direction, i), direction);
                    for (int i = -3; i < 3; i++)
                        this.generateCircleAir(world, rand, offset.offset(direction, length + i), direction);
                    this.totalRooms++;
                    this.generateRoom(world, rand, offset.offset(direction, length), 7, roomChance / 2, direction);
                }
        }

        private void generateRoom(WorldAccess world, Random rand, BlockPos position, int size, int roomChance, Direction direction) {
            BlockState resin = this.jungle ? JUNGLE_RESIN : DESERT_RESIN;
            BlockState sticky_resin = this.jungle ? STICKY_JUNGLE_RESIN : STICKY_DESERT_RESIN;
            RoomType type = RoomType.random(rand);
            if (!this.hasFoodRoom) {
                type = RoomType.FOOD;
                this.hasFoodRoom = true;
            } else if (!this.hasNursery) {
                type = RoomType.NURSERY;
                this.hasNursery = true;
            }
            this.generateSphereRespectResin(world, rand, position, size + 2, 4 + 2, resin, sticky_resin);
            this.generateSphere(world, rand, position, size, 4 - 1, Blocks.AIR.getDefaultState());
            this.decorateSphere(world, rand, position, size, 4 - 1, type);
            this.hive.addRoom(position, type);
            if (!this.small) {
                if (rand.nextInt(3) == 0 && direction.getOpposite() != Direction.NORTH)
                    this.generatePath(world, rand, position.offset(Direction.NORTH, size - 2), 5 + rand.nextInt(20), Direction.NORTH, roomChance);
                if (rand.nextInt(3) == 0 && direction.getOpposite() != Direction.SOUTH)
                    this.generatePath(world, rand, position.offset(Direction.SOUTH, size - 2), 5 + rand.nextInt(20), Direction.SOUTH, roomChance);
                if (rand.nextInt(3) == 0 && direction.getOpposite() != Direction.WEST)
                    this.generatePath(world, rand, position.offset(Direction.WEST, size - 2), 5 + rand.nextInt(20), Direction.WEST, roomChance);
                if (rand.nextInt(3) == 0 && direction.getOpposite() != Direction.EAST)
                    this.generatePath(world, rand, position.offset(Direction.EAST, size - 2), 5 + rand.nextInt(20), Direction.EAST, roomChance);
            }
        }

        private void generateEntrance(WorldAccess world, Random rand, BlockPos position, Direction direction) {
            BlockPos up = position.up();
            this.hive.getEntranceBottoms().put(up, direction);
            while (up.getY() < world.getTopPosition(this.small ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : Heightmap.Type.WORLD_SURFACE_WG, up).getY() && !world.getBlockState(up).isIn(BlockTags.LOGS)) {
                this.generateCircleRespectSky(world, rand, up, direction);
                up = up.up().offset(direction);
            }
            BlockState resin = this.jungle ? JUNGLE_RESIN : DESERT_RESIN;
            BlockState sticky_resin = this.jungle ? STICKY_JUNGLE_RESIN : STICKY_DESERT_RESIN;
            this.generateSphereRespectAir(world, rand, up, 4 + 4, 4 + 2, resin, sticky_resin);
            this.generateSphere(world, rand, up.up(), 4, 4, Blocks.AIR.getDefaultState());
            this.decorateSphere(world, rand, up.up(), 4, 4 - 1, RoomType.ENTERANCE);
            this.hive.getEntrances().put(up, direction);
            this.entrances++;
        }

        private void generateCircle(WorldAccess world, Random rand, BlockPos position, Direction direction) {
            BlockState resin = this.jungle ? JUNGLE_RESIN : DESERT_RESIN;
            BlockState sticky_resin = this.jungle ? STICKY_JUNGLE_RESIN : STICKY_DESERT_RESIN;
            int radius = 3 + 2;
            for (float i = 0; i < radius; i += 0.5F)
                for (float j = 0; j < 2 * Math.PI * i; j += 0.5F) {
                    int x = (int) Math.floor(MathHelper.sin(j) * i);
                    int z = (int) Math.floor(MathHelper.cos(j) * i);
                    if (direction == Direction.WEST || direction == Direction.EAST)
                        world.setBlockState(position.add(0, x, z), rand.nextInt(3) == 0 ? sticky_resin : resin, 2);
                    else
                        world.setBlockState(position.add(x, z, 0), rand.nextInt(3) == 0 ? sticky_resin : resin, 2);
                }
            radius -= 2;
            for (float i = 0; i < radius; i += 0.5F)
                for (float j = 0; j < 2 * Math.PI * i; j += 0.5F) {
                    int x = (int) Math.floor(MathHelper.sin(j) * i * MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
                    int z = (int) Math.floor(MathHelper.cos(j) * i * MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
                    if (direction == Direction.WEST || direction == Direction.EAST)
                        world.setBlockState(position.add(0, x, z), Blocks.AIR.getDefaultState(), 2);
                    else
                        world.setBlockState(position.add(x, z, 0), Blocks.AIR.getDefaultState(), 2);
                }
            this.decorateCircle(world, rand, position, 3, 5, direction);
        }

        private void generateCircleRespectSky(WorldAccess world, Random rand, BlockPos position, Direction direction) {
            BlockState resin = this.jungle ? JUNGLE_RESIN : DESERT_RESIN;
            BlockState sticky_resin = this.jungle ? STICKY_JUNGLE_RESIN : STICKY_DESERT_RESIN;
            int radius = 4 + 2;
            for (float i = 0; i < radius; i += 0.5F)
                for (float j = 0; j < 2 * Math.PI * i; j += 0.5F) {
                    int x = (int) Math.floor(MathHelper.sin(j) * i);
                    int z = (int) Math.floor(MathHelper.cos(j) * i);
                    if (direction == Direction.WEST || direction == Direction.EAST) {
                        if (!world.isSkyVisibleAllowingSea(position.add(0, x, z)))
                            world.setBlockState(position.add(0, x, z), rand.nextInt(3) == 0 ? sticky_resin : resin, 3);
                    } else if (!world.isSkyVisibleAllowingSea(position.add(x, z, 0)))
                        world.setBlockState(position.add(x, z, 0), rand.nextInt(3) == 0 ? sticky_resin : resin, 3);
                }
            radius -= 2;
            for (float i = 0; i < radius; i += 0.5F)
                for (float j = 0; j < 2 * Math.PI * i; j += 0.5F) {
                    int x = (int) Math.floor(MathHelper.sin(j) * i * MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
                    int z = (int) Math.floor(MathHelper.cos(j) * i * MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
                    if (direction == Direction.WEST || direction == Direction.EAST)
                        world.setBlockState(position.add(0, x, z), Blocks.AIR.getDefaultState(), 3);
                    else
                        world.setBlockState(position.add(x, z, 0), Blocks.AIR.getDefaultState(), 3);
                }
            this.decorateCircle(world, rand, position, 4, 4, direction);
        }


        private void generateCircleAir(WorldAccess world, Random rand, BlockPos position, Direction direction) {
            for (float i = 0; i < 3; i += 0.5F)
                for (float j = 0; j < 2 * Math.PI * i; j += 0.5F) {
                    int x = (int) Math.floor(MathHelper.sin(j) * i * MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
                    int z = (int) Math.floor(MathHelper.cos(j) * i * MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
                    if (direction == Direction.WEST || direction == Direction.EAST)
                        world.setBlockState(position.add(0, x, z), Blocks.AIR.getDefaultState(), 2);
                    else
                        world.setBlockState(position.add(x, z, 0), Blocks.AIR.getDefaultState(), 2);
                }
            this.decorateCircle(world, rand, position, 3, 5, direction);
        }

        public void generateSphere(WorldAccess world, Random rand, BlockPos position, int size, int height, BlockState fill) {
            int ySize = rand.nextInt(2);
            int j = size + rand.nextInt(2);
            int k = height + ySize;
            int l = size + rand.nextInt(2);
            float f = (j + k + l) * 0.333F;
            for (BlockPos blockpos : BlockPos.stream(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet()))
                if (blockpos.getSquaredDistance(position) <= f * f * MathHelper.clamp(rand.nextFloat(), 0.75F, 1.0F) && !world.isAir(blockpos))
                    world.setBlockState(blockpos, fill, 3);
        }

        public void generateSphere(WorldAccess world, Random rand, BlockPos position, int size, int height, BlockState fill, BlockState fill2) {
            int ySize = rand.nextInt(2);
            int j = size + rand.nextInt(2);
            int k = height + ySize;
            int l = size + rand.nextInt(2);
            float f = (j + k + l) * 0.333F;
            for (BlockPos blockpos : BlockPos.stream(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet()))
                if (blockpos.getSquaredDistance(position) <= f * f * MathHelper.clamp(rand.nextFloat(), 0.75F, 1.0F))
                    world.setBlockState(blockpos, rand.nextInt(3) == 0 ? fill2 : fill, 2);
        }

        public void generateSphereRespectResin(WorldAccess world, Random rand, BlockPos position, int size, int height, BlockState fill, BlockState fill2) {
            int ySize = rand.nextInt(2);
            int j = size + rand.nextInt(2);
            int k = height + ySize;
            int l = size + rand.nextInt(2);
            float f = (j + k + l) * 0.333F;
            for (BlockPos blockpos : BlockPos.stream(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet()))
                if (blockpos.getSquaredDistance(position) <= f * f * MathHelper.clamp(rand.nextFloat(), 0.75F, 1.0F) && (!world.isAir(blockpos) || world.isAir(blockpos) && !this.hasResinUnder(blockpos, world)))
                    world.setBlockState(blockpos, rand.nextInt(3) == 0 ? fill2 : fill, 2);
        }

        public void generateSphereRespectAir(WorldAccess world, Random rand, BlockPos position, int size, int height, BlockState fill, BlockState fill2) {
            int ySize = rand.nextInt(2);
            int j = size + rand.nextInt(2);
            int k = height + ySize;
            int l = size + rand.nextInt(2);
            float f = (j + k + l) * 0.333F;
            for (BlockPos blockpos : BlockPos.stream(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet()))
                if (blockpos.getSquaredDistance(position) <= f * f * MathHelper.clamp(rand.nextFloat(), 0.75F, 1.0F) && !world.isAir(blockpos))
                    world.setBlockState(blockpos, rand.nextInt(3) == 0 ? fill2 : fill, 2);
        }

        private boolean hasResinUnder(BlockPos pos, WorldAccess world) {
            BlockPos copy = pos.down();
            while (world.isAir(copy) && copy.getY() > 1) copy = copy.down();
            return world.getBlockState(copy).getBlock() instanceof BlockMyrmexResin || world.getBlockState(copy).getBlock() instanceof BlockMyrmexConnectedResin;
        }

        private void decorateCircle(WorldAccess world, Random rand, BlockPos position, int size, int height, Direction direction) {
            int radius = size + 2;
            for (float i = 0; i < radius; i += 0.5F) {
                for (float j = 0; j < 2 * Math.PI * i; j += 0.5F) {
                    int x = (int) Math.floor(MathHelper.sin(j) * i);
                    int z = (int) Math.floor(MathHelper.cos(j) * i);
                    if (direction == Direction.WEST || direction == Direction.EAST) {
                        if (world.isAir(position.add(0, x, z)))
                            this.decorate(world, position.add(0, x, z), position, size, rand, RoomType.TUNNEL);
                    } else if (world.isAir(position.add(x, z, 0)))
                        this.decorate(world, position.add(x, z, 0), position, size, rand, RoomType.TUNNEL);
                    if (world.isAir(position.add(0, x, z)))
                        this.decorateTubers(world, position.add(0, x, z), rand, RoomType.TUNNEL);
                }
            }
        }

        private void decorateSphere(WorldAccess world, Random rand, BlockPos position, int size, int height, RoomType roomType) {
            int ySize = rand.nextInt(2);
            int j = size + rand.nextInt(2);
            int k = height + ySize;
            int l = size + rand.nextInt(2);
            float f = (j + k + l) * 0.333F;
            for (BlockPos blockpos : BlockPos.stream(position.add(-j, -k, -l), position.add(j, k + 1, l)).map(BlockPos::toImmutable).collect(Collectors.toSet()))
                if (blockpos.getSquaredDistance(position) <= f * f) {
                    if (world.getBlockState(blockpos.down()).isOpaque() && world.isAir(blockpos))
                        this.decorate(world, blockpos, position, size, rand, roomType);
                    if (world.isAir(blockpos))
                        this.decorateTubers(world, blockpos, rand, roomType);
                }
        }

        private void decorate(WorldAccess world, BlockPos blockpos, BlockPos center, int size, Random random, RoomType roomType) {
            switch (roomType) {
                case FOOD -> {
                    if (random.nextInt(45) == 0 && world.getBlockState(blockpos.down()).getBlock() instanceof BlockMyrmexResin)
                        generateSkeleton(world, blockpos, center, size, random);
                    if (random.nextInt(13) == 0)
                        generateLeaves(world, blockpos, center, size, random, this.jungle);
                    if (random.nextInt(12) == 0)
                        generatePumpkins(world, blockpos, center, size, random, this.jungle);
                    if (random.nextInt(6) == 0)
                        generateMushrooms(world, blockpos, center, size, random);
                    if (random.nextInt(12) == 0)
                        generateCocoon(world, blockpos, random, this.jungle, this.jungle ? JUNGLE_MYRMEX_FOOD_CHEST : DESERT_MYRMEX_FOOD_CHEST);
                }
                case SHINY -> {
                    if (random.nextInt(12) == 0)
                        generateGold(world, blockpos, center, size, random);
                }
                case TRASH -> {
                    if (random.nextInt(24) == 0)
                        generateTrashHeap(world, blockpos, center, size, random);
                    if (random.nextBoolean())
                        generateTrashOre(world, blockpos, center, size, random);
                    if (random.nextInt(12) == 0)
                        generateCocoon(world, blockpos, random, this.jungle, MYRMEX_TRASH_CHEST);
                }
                default -> {
                }
            }

        }

        private void decorateTubers(WorldAccess world, BlockPos blockpos, Random random, RoomType roomType) {
            if (world.getBlockState(blockpos.up()).isOpaque() && random.nextInt(roomType == RoomType.ENTERANCE || roomType == RoomType.TUNNEL ? 20 : 6) == 0) {
                int tuberLength = roomType == RoomType.ENTERANCE || roomType == RoomType.TUNNEL ? 1 : roomType == RoomType.QUEEN ? 1 + random.nextInt(5) : 1 + random.nextInt(3);
                for (int i = 0; i < tuberLength; i++)
                    if (world.isAir(blockpos.down(i))) {
                        boolean connected = i != tuberLength - 1;
                        world.setBlockState(blockpos.down(i), this.jungle ? IafBlocks.MYRMEX_JUNGLE_BIOLIGHT.get().getDefaultState().with(BlockMyrmexBiolight.CONNECTED_DOWN, connected) : IafBlocks.MYRMEX_DESERT_BIOLIGHT.get().getDefaultState().with(BlockMyrmexBiolight.CONNECTED_DOWN, connected), 2);
                    }
            }
        }

        public static void generateSkeleton(WorldAccess worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand) {
            if (worldIn.getBlockState(blockpos.down()).isSideSolidFullSquare(worldIn, blockpos.down(), Direction.UP)) {
                Direction direction = Direction.fromHorizontal(rand.nextInt(3));
                Direction.Axis oppositeAxis = direction.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
                int maxRibHeight = rand.nextInt(2);
                for (int spine = 0; spine < 5 + rand.nextInt(2) * 2; spine++) {
                    BlockPos segment = blockpos.offset(direction, spine);
                    if (origin.getSquaredDistance(segment) <= (double) (radius * radius))
                        worldIn.setBlockState(segment, Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, direction.getAxis()), 2);
                    if (spine % 2 != 0) {
                        BlockPos rightRib = segment.offset(direction.rotateYCounterclockwise());
                        BlockPos leftRib = segment.offset(direction.rotateYClockwise());
                        if (origin.getSquaredDistance(rightRib) <= (double) (radius * radius))
                            worldIn.setBlockState(rightRib, Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, oppositeAxis), 2);
                        if (origin.getSquaredDistance(leftRib) <= (double) (radius * radius))
                            worldIn.setBlockState(leftRib, Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, oppositeAxis), 2);
                        for (int ribHeight = 1; ribHeight < maxRibHeight + 2; ribHeight++) {
                            if (origin.getSquaredDistance(rightRib.up(ribHeight).offset(direction.rotateYCounterclockwise())) <= (double) (radius * radius))
                                worldIn.setBlockState(rightRib.up(ribHeight).offset(direction.rotateYCounterclockwise()), Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y), 2);
                            if (origin.getSquaredDistance(leftRib.up(ribHeight).offset(direction.rotateYClockwise())) <= (double) (radius * radius))
                                worldIn.setBlockState(leftRib.up(ribHeight).offset(direction.rotateYClockwise()), Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y), 2);
                        }
                        if (origin.getSquaredDistance(rightRib.up(maxRibHeight + 2)) <= (double) (radius * radius))
                            worldIn.setBlockState(rightRib.up(maxRibHeight + 2), Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, oppositeAxis), 2);
                        if (origin.getSquaredDistance(leftRib.up(maxRibHeight + 2)) <= (double) (radius * radius))
                            worldIn.setBlockState(leftRib.up(maxRibHeight + 2), Blocks.BONE_BLOCK.getDefaultState().with(PillarBlock.AXIS, oppositeAxis), 2);
                    }
                }
            }
        }

        public static void generateLeaves(WorldAccess worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand, boolean jungle) {
            if (worldIn.getBlockState(blockpos.down()).isSideSolidFullSquare(worldIn, blockpos.down(), Direction.UP)) {
                BlockState leaf = Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, Boolean.TRUE);
                if (jungle) leaf = Blocks.JUNGLE_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, Boolean.TRUE);
                int i1 = 0;
                for (int i = 0; i < 3; ++i) {
                    int j = i1 + rand.nextInt(2);
                    int k = i1 + rand.nextInt(2);
                    int l = i1 + rand.nextInt(2);
                    float f = (float) (j + k + l) * 0.333F + 0.5F;
                    for (BlockPos pos : BlockPos.stream(blockpos.add(-j, -k, -l), blockpos.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet()))
                        if (pos.getSquaredDistance(blockpos) <= (double) (f * f) && worldIn.isAir(pos))
                            worldIn.setBlockState(pos, leaf, 4);
                    blockpos = blockpos.add(-(i1 + 1) + rand.nextInt(2), -rand.nextInt(2), -(i1 + 1) + rand.nextInt(2));
                }
            }
        }

        public static void generatePumpkins(WorldAccess worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand, boolean jungle) {
            if (worldIn.getBlockState(blockpos.down()).isSideSolidFullSquare(worldIn, blockpos.down(), Direction.UP))
                worldIn.setBlockState(blockpos, jungle ? Blocks.MELON.getDefaultState() : Blocks.PUMPKIN.getDefaultState(), 2);
        }

        public static void generateCocoon(WorldAccess worldIn, BlockPos blockpos, Random rand, boolean jungle, Identifier lootTable) {
            if (worldIn.getBlockState(blockpos.down()).isSideSolidFullSquare(worldIn, blockpos.down(), Direction.UP)) {
                worldIn.setBlockState(blockpos, jungle ? IafBlocks.JUNGLE_MYRMEX_COCOON.get().getDefaultState() : IafBlocks.DESERT_MYRMEX_COCOON.get().getDefaultState(), 3);

                if (worldIn.getBlockEntity(blockpos) != null && worldIn.getBlockEntity(blockpos) instanceof LootableContainerBlockEntity lootable) {
                    BlockEntity tileentity1 = worldIn.getBlockEntity(blockpos);
                    assert tileentity1 != null;
                    lootable.setLootTable(lootTable, rand.nextLong());
                }
            }
        }

        public static void generateMushrooms(WorldAccess worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand) {
            if (worldIn.getBlockState(blockpos.down()).isSideSolidFullSquare(worldIn, blockpos.down(), Direction.UP))
                worldIn.setBlockState(blockpos, rand.nextBoolean() ? Blocks.BROWN_MUSHROOM.getDefaultState() : Blocks.RED_MUSHROOM.getDefaultState(), 2);
        }

        public static void generateGold(WorldAccess worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand) {
            BlockState gold = IafBlocks.GOLD_PILE.get().getDefaultState();
            int choice = rand.nextInt(2);
            if (choice == 1) gold = IafBlocks.SILVER_PILE.get().getDefaultState();
            else if (choice == 2) gold = IafBlocks.COPPER_PILE.get().getDefaultState();
            if (worldIn.getBlockState(blockpos.down()).isSideSolidFullSquare(worldIn, blockpos.down(), Direction.UP)) {
                worldIn.setBlockState(blockpos, gold.with(BlockGoldPile.LAYERS, 8), 3);
                worldIn.setBlockState(MyrmexHive.getGroundedPos(worldIn, blockpos.north()), gold.with(BlockGoldPile.LAYERS, 1 + Random.create().nextInt(7)), 3);
                worldIn.setBlockState(MyrmexHive.getGroundedPos(worldIn, blockpos.south()), gold.with(BlockGoldPile.LAYERS, 1 + Random.create().nextInt(7)), 3);
                worldIn.setBlockState(MyrmexHive.getGroundedPos(worldIn, blockpos.west()), gold.with(BlockGoldPile.LAYERS, 1 + Random.create().nextInt(7)), 3);
                worldIn.setBlockState(MyrmexHive.getGroundedPos(worldIn, blockpos.east()), gold.with(BlockGoldPile.LAYERS, 1 + Random.create().nextInt(7)), 3);
                if (rand.nextInt(3) == 0) {
                    worldIn.setBlockState(blockpos.up(), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, GenerationConstants.HORIZONTALS[Random.create().nextInt(3)]), 2);
                    if (worldIn.getBlockState(blockpos.up()).getBlock() instanceof ChestBlock) {
                        BlockEntity tileentity1 = worldIn.getBlockEntity(blockpos.up());
                        if (tileentity1 instanceof ChestBlockEntity chest)
                            chest.setLootTable(MYRMEX_GOLD_CHEST, rand.nextLong());
                    }
                }
            }
        }

        public static void generateTrashHeap(WorldAccess worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand) {
            if (worldIn.getBlockState(blockpos.down()).isSideSolidFullSquare(worldIn, blockpos.down(), Direction.UP)) {
                Block blob = switch (rand.nextInt(3)) {
                    case 0 -> Blocks.DIRT;
                    case 1 -> Blocks.SAND;
                    case 2 -> Blocks.COBBLESTONE;
                    case 3 -> Blocks.GRAVEL;
                    default -> Blocks.AIR;
                };
                int i1 = 0;
                for (int i = 0; i < 3; ++i) {
                    int j = i1 + rand.nextInt(2);
                    int k = i1 + rand.nextInt(2);
                    int l = i1 + rand.nextInt(2);
                    float f = (float) (j + k + l) * 0.333F + 0.5F;
                    for (BlockPos pos : BlockPos.stream(blockpos.add(-j, -k, -l), blockpos.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet()))
                        if (pos.getSquaredDistance(blockpos) <= (double) (f * f))
                            worldIn.setBlockState(pos, blob.getDefaultState(), 4);
                    blockpos = blockpos.add(-(i1 + 1) + rand.nextInt(2), -rand.nextInt(2), -(i1 + 1) + rand.nextInt(2));
                }
            }
        }

        public static void generateTrashOre(WorldAccess worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand) {
            Block current = worldIn.getBlockState(blockpos).getBlock();
            if (origin.getSquaredDistance(blockpos) <= (double) (radius * radius)) {
                if (current == Blocks.DIRT || current == Blocks.SAND || current == Blocks.COBBLESTONE || current == Blocks.GRAVEL) {
                    Block ore = Blocks.REDSTONE_ORE;
                    if (rand.nextInt(3) == 0) {
                        ore = rand.nextBoolean() ? Blocks.GOLD_ORE : IafBlocks.SILVER_ORE.get();
                        if (rand.nextInt(2) == 0)
                            ore = Blocks.COPPER_ORE;
                    } else if (rand.nextInt(3) == 0)
                        ore = Blocks.DIAMOND_ORE;
                    else if (rand.nextInt(2) == 0) {
                        ore = rand.nextBoolean() ? Blocks.EMERALD_ORE : IafBlocks.SAPPHIRE_ORE.get();
                        if (rand.nextInt(2) == 0)
                            ore = Blocks.AMETHYST_CLUSTER;
                    }
                    worldIn.setBlockState(blockpos, ore.getDefaultState(), 2);
                }
            }
        }
    }

    public enum RoomType {
        DEFAULT(false),
        TUNNEL(false),
        ENTERANCE(false),
        QUEEN(false),
        FOOD(true),
        EMPTY(true),
        NURSERY(true),
        SHINY(true),
        TRASH(true);
        final boolean random;

        RoomType(boolean random) {
            this.random = random;
        }

        public static RoomType random(Random rand) {
            List<RoomType> list = new ArrayList<>();
            for (RoomType type : RoomType.values()) {
                if (type.random) {
                    list.add(type);
                }
            }
            return list.get(rand.nextInt(list.size()));
        }
    }
}
