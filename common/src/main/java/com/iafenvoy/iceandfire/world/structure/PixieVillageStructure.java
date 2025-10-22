package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.PixieEntity;
import com.iafenvoy.iceandfire.item.block.PixieHouseBlock;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class PixieVillageStructure extends Structure {
    public static final MapCodec<PixieVillageStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(configCodecBuilder(instance)).apply(instance, PixieVillageStructure::new));

    protected PixieVillageStructure(Config config) {
        super(config);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        if (context.random().nextDouble() >= IafCommonConfig.INSTANCE.worldGen.generatePixieVillageChance.getValue())
            return Optional.empty();
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        return Optional.of(new StructurePosition(blockPos, collector -> collector.addPiece(new PixieVillagePiece(0, new BlockBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ())))));
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.PIXIE_VILLAGE.get();
    }

    public static class PixieVillagePiece extends StructurePiece {

        protected PixieVillagePiece(int length, BlockBox boundingBox) {
            super(IafStructurePieces.PIXIE_VILLAGE.get(), length, boundingBox);
        }

        public PixieVillagePiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.PIXIE_VILLAGE.get(), nbt);
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            int maxRoads = IafCommonConfig.INSTANCE.pixie.size.getValue() + random.nextInt(5);
            BlockPos buildPosition = pivot;
            int placedRoads = 0;
            while (placedRoads < maxRoads) {
                int roadLength = 10 + random.nextInt(15);
                Direction buildingDirection = Direction.fromHorizontal(random.nextInt(3));
                for (int i = 0; i < roadLength; i++) {
                    BlockPos buildPosition2 = buildPosition.offset(buildingDirection, i);
                    buildPosition2 = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, buildPosition2).down();
                    if (world.getBlockState(buildPosition2).getFluidState().isEmpty()) {
                        world.setBlockState(buildPosition2, Blocks.DIRT_PATH.getDefaultState(), 2);
                    } else {
                        world.setBlockState(buildPosition2, Blocks.SPRUCE_PLANKS.getDefaultState(), 2);
                    }
                    if (random.nextInt(8) == 0) {
                        Direction houseDir = random.nextBoolean() ? buildingDirection.rotateYClockwise() : buildingDirection.rotateYCounterclockwise();
                        int houseColor = random.nextInt(5);
                        BlockState houseState = switch (houseColor) {
                            case 0 ->
                                    IafBlocks.PIXIE_HOUSE_MUSHROOM_RED.get().getDefaultState().with(PixieHouseBlock.FACING, houseDir.getOpposite());
                            case 1 ->
                                    IafBlocks.PIXIE_HOUSE_MUSHROOM_BROWN.get().getDefaultState().with(PixieHouseBlock.FACING, houseDir.getOpposite());
                            case 2 ->
                                    IafBlocks.PIXIE_HOUSE_OAK.get().getDefaultState().with(PixieHouseBlock.FACING, houseDir.getOpposite());
                            case 3 ->
                                    IafBlocks.PIXIE_HOUSE_BIRCH.get().getDefaultState().with(PixieHouseBlock.FACING, houseDir.getOpposite());
                            case 4 ->
                                    IafBlocks.PIXIE_HOUSE_SPRUCE.get().getDefaultState().with(PixieHouseBlock.FACING, houseDir.getOpposite());
                            case 5 ->
                                    IafBlocks.PIXIE_HOUSE_DARK_OAK.get().getDefaultState().with(PixieHouseBlock.FACING, houseDir.getOpposite());
                            default -> IafBlocks.PIXIE_HOUSE_OAK.get().getDefaultState();
                        };
                        PixieEntity pixie = IafEntities.PIXIE.get().create(world.toServerWorld());
                        assert pixie != null;
                        pixie.initialize(world, world.getLocalDifficulty(buildPosition2.up()), SpawnReason.SPAWNER, null);
                        pixie.setPosition(buildPosition2.getX(), buildPosition2.getY() + 2, buildPosition2.getZ());
                        pixie.setPersistent();
                        world.spawnEntity(pixie);

                        world.setBlockState(buildPosition2.offset(houseDir).up(), houseState, 2);
                        if (!world.getBlockState(buildPosition2.offset(houseDir)).isOpaque()) {
                            world.setBlockState(buildPosition2.offset(houseDir), Blocks.COARSE_DIRT.getDefaultState(), 2);
                            world.setBlockState(buildPosition2.offset(houseDir).down(), Blocks.COARSE_DIRT.getDefaultState(), 2);
                        }
                    }
                }
                buildPosition = buildPosition.offset(buildingDirection, random.nextInt(roadLength));
                placedRoads++;
            }
        }
    }
}
