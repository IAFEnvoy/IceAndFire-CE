package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.SirenEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class SirenIslandStructure extends Structure implements DangerousGeneration {
    public static final MapCodec<SirenIslandStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(configCodecBuilder(instance)).apply(instance, SirenIslandStructure::new));

    protected SirenIslandStructure(Config config) {
        super(config);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        if (context.random().nextDouble() >= IafCommonConfig.INSTANCE.worldGen.generateSirenIslandChance.getValue())
            return Optional.empty();
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        if (!this.isFarEnoughFromSpawn(blockPos)) return Optional.empty();
        return Optional.of(new StructurePosition(blockPos, collector -> collector.addPiece(new SirenIslandPiece(0, new BlockBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ())))));
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.SIREN_ISLAND.get();
    }

    public static class SirenIslandPiece extends StructurePiece {
        protected SirenIslandPiece(int length, BlockBox boundingBox) {
            super(IafStructurePieces.SIREN_ISLAND.get(), length, boundingBox);
        }

        public SirenIslandPiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.SIREN_ISLAND.get(), nbt);
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            int up = random.nextInt(4) + 1;
            BlockPos center = pivot.up(up);
            int layer = 0;
            int sirens = 1 + random.nextInt(3);

            while (!world.getBlockState(center).isOpaque() && center.getY() >= world.getBottomY()) {
                layer++;
                for (float i = 0; i < this.getRadius(layer, up); i += 0.5f) {
                    for (float j = 0; j < 2 * Math.PI * i + random.nextInt(2); j += 0.5f) {
                        BlockPos stonePos = BlockPos.ofFloored(Math.floor(center.getX() + MathHelper.sin(j) * i + random.nextInt(2)), center.getY(), Math.floor(center.getZ() + MathHelper.cos(j) * i + random.nextInt(2)));
                        world.setBlockState(stonePos, this.getStone(random), Block.NOTIFY_ALL);
                        BlockPos upPos = stonePos.up();
                        if (world.isAir(upPos) && world.isAir(upPos.east()) && world.isAir(upPos.north()) && world.isAir(upPos.north().east()) && random.nextInt(3) == 0 && sirens > 0) {
                            this.spawnSiren(world, random, upPos.north().east());
                            sirens--;
                        }
                    }
                }
                center = center.down();
            }
            layer++;
            for (float i = 0; i < this.getRadius(layer, up); i += 0.5f)
                for (float j = 0; j < 2 * Math.PI * i + random.nextInt(2); j += 0.5f) {
                    BlockPos stonePos = BlockPos.ofFloored(Math.floor(center.getX() + MathHelper.sin(j) * i + random.nextInt(2)), center.getY(), Math.floor(center.getZ() + MathHelper.cos(j) * i + random.nextInt(2)));
                    while (!world.getBlockState(stonePos).isOpaque() && stonePos.getY() >= 0) {
                        world.setBlockState(stonePos, this.getStone(random), Block.NOTIFY_ALL);
                        stonePos = stonePos.down();
                    }
                }
        }

        private int getRadius(int layer, int up) {
            int MAX_ISLAND_RADIUS = 10;
            return layer > up ? (int) (layer * 0.25) + up : Math.min(layer, MAX_ISLAND_RADIUS);
        }

        private BlockState getStone(Random random) {
            int chance = random.nextInt(100);
            if (chance > 90) return Blocks.MOSSY_COBBLESTONE.getDefaultState();
            else if (chance > 70) return Blocks.GRAVEL.getDefaultState();
            else if (chance > 45) return Blocks.COBBLESTONE.getDefaultState();
            else return Blocks.STONE.getDefaultState();
        }

        private void spawnSiren(ServerWorldAccess worldIn, Random rand, BlockPos position) {
            SirenEntity siren = new SirenEntity(IafEntities.SIREN.get(), worldIn.toServerWorld());
            siren.setSinging(true);
            siren.setHairColor(rand.nextInt(2));
            siren.setSingingPose(rand.nextInt(2));
            siren.updatePositionAndAngles(position.getX() + 0.5D, position.getY() + 1, position.getZ() + 0.5D, rand.nextFloat() * 360, 0);
            worldIn.spawnEntity(siren);
        }
    }
}
