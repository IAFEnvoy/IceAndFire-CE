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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PixieVillageStructure extends Structure {
    public static final MapCodec<PixieVillageStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(settingsCodec(instance)).apply(instance, PixieVillageStructure::new));

    protected PixieVillageStructure(StructureSettings config) {
        super(config);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (context.random().nextDouble() >= IafCommonConfig.INSTANCE.worldGen.generatePixieVillageChance.getValue())
            return Optional.empty();
        Rotation blockRotation = Rotation.getRandom(context.random());
        BlockPos blockPos = this.getLowestYIn5by5BoxOffset7Blocks(context, blockRotation);
        return Optional.of(new GenerationStub(blockPos, collector -> collector.addPiece(new PixieVillagePiece(0, new BoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ())))));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return IafStructureTypes.PIXIE_VILLAGE.get();
    }

    public static class PixieVillagePiece extends StructurePiece {

        protected PixieVillagePiece(int length, BoundingBox boundingBox) {
            super(IafStructurePieces.PIXIE_VILLAGE.get(), length, boundingBox);
        }

        public PixieVillagePiece(StructurePieceSerializationContext context, CompoundTag nbt) {
            super(IafStructurePieces.PIXIE_VILLAGE.get(), nbt);
        }

        @Override
        protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag nbt) {
        }

        @Override
        public void postProcess(@NotNull WorldGenLevel world, @NotNull StructureManager structureAccessor, @NotNull ChunkGenerator chunkGenerator, @NotNull RandomSource random, BoundingBox chunkBox, @NotNull ChunkPos chunkPos, @NotNull BlockPos pivot) {
            if (!chunkBox.isInside(pivot))
                return;

            int maxRoads = IafCommonConfig.INSTANCE.pixie.size.getValue() + random.nextInt(5);
            BlockPos buildPosition = pivot;
            int placedRoads = 0;
            List<BlockPos> posesInBB = new ArrayList<>();
            while (placedRoads < maxRoads) {
                int roadLength = 10 + random.nextInt(15);
                Direction buildingDirection = Direction.from2DDataValue(random.nextInt(3));
                for (int i = 0; i < roadLength; i++) {
                    BlockPos buildPosition2 = buildPosition.relative(buildingDirection, i);
                    buildPosition2 = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, buildPosition2).below();
                    if (chunkBox.inflatedBy(16, 0, 16).isInside(buildPosition2))
                        posesInBB.add(buildPosition2);
                    if (world.getBlockState(buildPosition2).getFluidState().isEmpty()) {
                        world.setBlock(buildPosition2, Blocks.DIRT_PATH.defaultBlockState(), 2);
                    } else {
                        world.setBlock(buildPosition2, Blocks.SPRUCE_PLANKS.defaultBlockState(), 2);
                    }
                    if (random.nextInt(8) == 0) {
                        Direction houseDir = random.nextBoolean() ? buildingDirection.getClockWise() : buildingDirection.getCounterClockWise();
                        int houseColor = random.nextInt(5);
                        BlockState houseState = switch (houseColor) {
                            case 0 ->
                                    IafBlocks.PIXIE_HOUSE_MUSHROOM_RED.get().defaultBlockState().setValue(PixieHouseBlock.FACING, houseDir.getOpposite());
                            case 1 ->
                                    IafBlocks.PIXIE_HOUSE_MUSHROOM_BROWN.get().defaultBlockState().setValue(PixieHouseBlock.FACING, houseDir.getOpposite());
                            case 2 ->
                                    IafBlocks.PIXIE_HOUSE_OAK.get().defaultBlockState().setValue(PixieHouseBlock.FACING, houseDir.getOpposite());
                            case 3 ->
                                    IafBlocks.PIXIE_HOUSE_BIRCH.get().defaultBlockState().setValue(PixieHouseBlock.FACING, houseDir.getOpposite());
                            case 4 ->
                                    IafBlocks.PIXIE_HOUSE_SPRUCE.get().defaultBlockState().setValue(PixieHouseBlock.FACING, houseDir.getOpposite());
                            case 5 ->
                                    IafBlocks.PIXIE_HOUSE_DARK_OAK.get().defaultBlockState().setValue(PixieHouseBlock.FACING, houseDir.getOpposite());
                            default -> IafBlocks.PIXIE_HOUSE_OAK.get().defaultBlockState();
                        };
                        PixieEntity pixie = IafEntities.PIXIE.get().create(world.getLevel());
                        assert pixie != null;
                        pixie.finalizeSpawn(world, world.getCurrentDifficultyAt(buildPosition2.above()), MobSpawnType.SPAWNER, null);
                        pixie.setPos(buildPosition2.getX(), buildPosition2.getY() + 2, buildPosition2.getZ());
                        pixie.setPersistenceRequired();
                        world.addFreshEntity(pixie);

                        world.setBlock(buildPosition2.relative(houseDir).above(), houseState, 2);
                        if (!world.getBlockState(buildPosition2.relative(houseDir)).canOcclude()) {
                            world.setBlock(buildPosition2.relative(houseDir), Blocks.COARSE_DIRT.defaultBlockState(), 2);
                            world.setBlock(buildPosition2.relative(houseDir).below(), Blocks.COARSE_DIRT.defaultBlockState(), 2);
                        }
                    }
                }
                buildPosition = buildPosition.relative(buildingDirection, random.nextInt(roadLength));
                placedRoads++;
            }
            super.boundingBox = BoundingBox.encapsulatingPositions(posesInBB).orElseGet(super::getBoundingBox).inflatedBy(0, 2, 0);
        }
    }
}
