package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class FireDragonCaveStructure extends DragonCaveStructure {
    public static final MapCodec<FireDragonCaveStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(settingsCodec(instance)).apply(instance, FireDragonCaveStructure::new));

    protected FireDragonCaveStructure(StructureSettings config) {
        super(config);
    }

    @Override
    protected DragonCavePiece createPiece(BoundingBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
        return new FireDragonCavePiece(0, boundingBox, male, offset, y, seed);
    }

    @Override
    protected double getGenerateChance() {
        return IafCommonConfig.INSTANCE.worldGen.generateFireDragonCaveChance.getValue();
    }

    @Override
    public @NotNull StructureType<?> type() {
        return IafStructureTypes.FIRE_DRAGON_CAVE.get();
    }

    public static class FireDragonCavePiece extends DragonCavePiece {
        public static final ResourceLocation FIRE_DRAGON_CHEST = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "chest/fire_dragon_female_cave");
        public static final ResourceLocation FIRE_DRAGON_CHEST_MALE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "chest/fire_dragon_male_cave");

        protected FireDragonCavePiece(int length, BoundingBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
            super(IafStructurePieces.FIRE_DRAGON_CAVE.get(), length, boundingBox, male, offset, y, seed);
        }

        public FireDragonCavePiece(StructurePieceSerializationContext context, CompoundTag nbt) {
            super(IafStructurePieces.FIRE_DRAGON_CAVE.get(), nbt);
        }

        @Override
        protected TagKey<Block> getOreTag() {
            return IafBlockTags.FIRE_DRAGON_CAVE_ORES;
        }

        @Override
        protected WorldGenCaveStalactites getCeilingDecoration() {
            return new WorldGenCaveStalactites(IafBlocks.CHARRED_STONE.get(), 3);
        }

        @Override
        protected BlockState getTreasurePile() {
            return IafBlocks.GOLD_PILE.get().defaultBlockState();
        }

        @Override
        protected BlockState getPaletteBlock(RandomSource random) {
            return (random.nextBoolean() ? IafBlocks.CHARRED_STONE : IafBlocks.CHARRED_COBBLESTONE).get().defaultBlockState();
        }

        @Override
        protected ResourceKey<LootTable> getChestTable(boolean male) {
            return ResourceKey.create(Registries.LOOT_TABLE, male ? FIRE_DRAGON_CHEST_MALE : FIRE_DRAGON_CHEST);
        }

        @Override
        protected EntityType<? extends DragonBaseEntity> getDragonType() {
            return IafEntities.FIRE_DRAGON.get();
        }
    }
}
