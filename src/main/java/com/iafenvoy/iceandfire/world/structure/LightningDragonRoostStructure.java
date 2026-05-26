package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.registry.tag.CommonBlockTags;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class LightningDragonRoostStructure extends DragonRoostStructure {
    public static final MapCodec<LightningDragonRoostStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(settingsCodec(instance)).apply(instance, LightningDragonRoostStructure::new));

    protected LightningDragonRoostStructure(StructureSettings config) {
        super(config);
    }

    @Override
    protected DragonRoostPiece createPiece(BoundingBox boundingBox, boolean isMale) {
        return new LightningDragonRoostPiece(0, boundingBox, IafBlocks.COPPER_PILE.get(), isMale);
    }

    @Override
    protected double getGenerateChance() {
        return IafCommonConfig.INSTANCE.worldGen.generateLightningDragonRoostChance.getValue();
    }

    @Override
    public @NotNull StructureType<?> type() {
        return IafStructureTypes.LIGHTNING_DRAGON_ROOST.get();
    }

    public static class LightningDragonRoostPiece extends DragonRoostPiece {
        private static final ResourceLocation DRAGON_CHEST = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "chest/lightning_dragon_roost");

        protected LightningDragonRoostPiece(int length, BoundingBox boundingBox, Block treasureBlock, boolean isMale) {
            super(IafStructurePieces.LIGHTNING_DRAGON_ROOST.get(), length, boundingBox, treasureBlock, isMale);
        }

        public LightningDragonRoostPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
            super(IafStructurePieces.LIGHTNING_DRAGON_ROOST.get(), nbt);
        }

        @Override
        protected EntityType<? extends DragonBaseEntity> getDragonType() {
            return IafEntities.LIGHTNING_DRAGON.get();
        }

        @Override
        protected ResourceKey<LootTable> getRoostLootTable() {
            return ResourceKey.create(Registries.LOOT_TABLE, DRAGON_CHEST);
        }

        @Override
        protected BlockState transform(final BlockState state) {
            Block block = null;
            if (state.is(Blocks.GRASS_BLOCK))
                block = IafBlocks.CRACKLED_GRASS.get();
            else if (state.is(Blocks.DIRT_PATH))
                block = IafBlocks.CRACKLED_DIRT_PATH.get();
            else if (state.is(CommonBlockTags.GRAVELS))
                block = IafBlocks.CRACKLED_GRAVEL.get();
            else if (state.is(BlockTags.DIRT))
                block = IafBlocks.CRACKLED_DIRT.get();
            else if (state.is(CommonBlockTags.STONES))
                block = IafBlocks.CRACKLED_STONE.get();
            else if (state.is(CommonBlockTags.COBBLESTONES))
                block = IafBlocks.CRACKLED_COBBLESTONE.get();
            else if (state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS))
                block = IafBlocks.ASH.get();
            else if (state.is(IafBlockTags.GRASSES) || state.is(BlockTags.LEAVES) || state.is(BlockTags.FLOWERS) || state.is(BlockTags.CROPS))
                block = Blocks.AIR;
            if (block != null) return block.defaultBlockState();
            return state;
        }

        @Override
        protected void handleCustomGeneration(WorldGenLevel world, BlockPos origin, RandomSource random, BlockPos position, double distance) {
            if (distance > 0.05D && random.nextInt(800) == 0)
                this.generateSpire(world, random, this.getSurfacePosition(world, position));
            if (distance > 0.05D && random.nextInt(1000) == 0)
                this.generateSpike(world, random, this.getSurfacePosition(world, position), Direction.Plane.HORIZONTAL.getRandomDirection(random));
        }

        private void generateSpike(LevelAccessor worldIn, RandomSource rand, BlockPos position, Direction direction) {
            int radius = 5;
            for (int i = 0; i < 5; i++) {
                int j = Math.max(0, radius - (int) (i * 1.75F));
                int l = radius - i;
                int k = Math.max(0, radius - (int) (i * 1.5F));
                float f = (float) (j + l) * 0.333F + 0.5F;
                BlockPos up = position.above().relative(direction, i);
                int xOrZero = direction.getAxis() == Direction.Axis.Z ? j : 0;
                int zOrZero = direction.getAxis() == Direction.Axis.Z ? 0 : k;
                for (BlockPos blockpos : BlockPos.betweenClosedStream(up.offset(-xOrZero, -l, -zOrZero), up.offset(xOrZero, l, zOrZero)).map(BlockPos::immutable).collect(Collectors.toSet())) {
                    if (blockpos.distSqr(position) <= (double) (f * f)) {
                        int height = Math.max(blockpos.getY() - up.getY(), 0);
                        if (i == 0) {
                            if (rand.nextFloat() < height * 0.3F)
                                worldIn.setBlock(blockpos, IafBlocks.CRACKLED_STONE.get().defaultBlockState(), 2);
                        } else worldIn.setBlock(blockpos, IafBlocks.CRACKLED_STONE.get().defaultBlockState(), 2);
                    }
                }
            }
        }

        private void generateSpire(LevelAccessor worldIn, RandomSource rand, BlockPos position) {
            int height = 5 + rand.nextInt(5);
            Direction bumpDirection = Direction.NORTH;
            for (int i = 0; i < height; i++) {
                worldIn.setBlock(position.above(i), IafBlocks.CRACKLED_STONE.get().defaultBlockState(), 2);
                if (rand.nextBoolean()) {
                    bumpDirection = bumpDirection.getClockWise();
                }
                int offset = 1;
                if (i < 4) {
                    worldIn.setBlock(position.above(i).north(), IafBlocks.CRACKLED_GRAVEL.get().defaultBlockState(), 2);
                    worldIn.setBlock(position.above(i).south(), IafBlocks.CRACKLED_GRAVEL.get().defaultBlockState(), 2);
                    worldIn.setBlock(position.above(i).east(), IafBlocks.CRACKLED_GRAVEL.get().defaultBlockState(), 2);
                    worldIn.setBlock(position.above(i).west(), IafBlocks.CRACKLED_GRAVEL.get().defaultBlockState(), 2);
                    offset = 2;
                }
                if (i < height - 2)
                    worldIn.setBlock(position.above(i).relative(bumpDirection, offset), IafBlocks.CRACKLED_COBBLESTONE.get().defaultBlockState(), 2);
            }
        }
    }
}
