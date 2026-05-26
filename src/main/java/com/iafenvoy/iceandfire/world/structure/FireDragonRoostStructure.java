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
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class FireDragonRoostStructure extends DragonRoostStructure {
    public static final MapCodec<FireDragonRoostStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(settingsCodec(instance)).apply(instance, FireDragonRoostStructure::new));

    protected FireDragonRoostStructure(StructureSettings config) {
        super(config);
    }

    @Override
    protected DragonRoostPiece createPiece(BoundingBox boundingBox, boolean isMale) {
        return new FireDragonRoostPiece(0, boundingBox, IafBlocks.GOLD_PILE.get(), isMale);
    }

    @Override
    protected double getGenerateChance() {
        return IafCommonConfig.INSTANCE.worldGen.generateFireDragonRoostChance.getValue();
    }

    @Override
    public @NotNull StructureType<?> type() {
        return IafStructureTypes.FIRE_DRAGON_ROOST.get();
    }

    public static class FireDragonRoostPiece extends DragonRoostPiece {
        private static final ResourceLocation DRAGON_CHEST = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "chest/fire_dragon_roost");

        protected FireDragonRoostPiece(int length, BoundingBox boundingBox, Block treasureBlock, boolean isMale) {
            super(IafStructurePieces.FIRE_DRAGON_ROOST.get(), length, boundingBox, treasureBlock, isMale);
        }

        public FireDragonRoostPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
            super(IafStructurePieces.FIRE_DRAGON_ROOST.get(), nbt);
        }

        @Override
        protected EntityType<? extends DragonBaseEntity> getDragonType() {
            return IafEntities.FIRE_DRAGON.get();
        }

        @Override
        protected ResourceKey<LootTable> getRoostLootTable() {
            return ResourceKey.create(Registries.LOOT_TABLE, DRAGON_CHEST);
        }

        @Override
        protected BlockState transform(final BlockState state) {
            Block block = null;
            if (state.is(Blocks.GRASS_BLOCK))
                block = IafBlocks.CHARRED_GRASS.get();
            else if (state.is(Blocks.DIRT_PATH))
                block = IafBlocks.CHARRED_DIRT_PATH.get();
            else if (state.is(CommonBlockTags.GRAVELS))
                block = IafBlocks.CHARRED_GRAVEL.get();
            else if (state.is(BlockTags.DIRT))
                block = IafBlocks.CHARRED_DIRT.get();
            else if (state.is(CommonBlockTags.STONES))
                block = IafBlocks.CHARRED_STONE.get();
            else if (state.is(CommonBlockTags.COBBLESTONES))
                block = IafBlocks.CHARRED_COBBLESTONE.get();
            else if (state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS))
                block = IafBlocks.ASH.get();
            else if (state.is(IafBlockTags.GRASSES) || state.is(BlockTags.LEAVES) || state.is(BlockTags.FLOWERS) || state.is(BlockTags.CROPS))
                block = Blocks.AIR;
            if (block != null) return block.defaultBlockState();
            return state;
        }

        @Override
        protected void handleCustomGeneration(WorldGenLevel world, BlockPos origin, RandomSource random, BlockPos position, double distance) {
            if (random.nextInt(1000) == 0)
                this.generateRoostPile(world, random, this.getSurfacePosition(world, position), IafBlocks.ASH.get());
        }
    }
}
