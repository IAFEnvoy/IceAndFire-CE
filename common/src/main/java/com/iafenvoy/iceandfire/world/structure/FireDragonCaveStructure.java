package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.StructureContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.structure.StructureType;

public class FireDragonCaveStructure extends DragonCaveStructure {
    public static final Codec<FireDragonCaveStructure> CODEC = RecordCodecBuilder.<FireDragonCaveStructure>mapCodec(instance ->
            instance.group(configCodecBuilder(instance)).apply(instance, FireDragonCaveStructure::new)).codec();

    protected FireDragonCaveStructure(Config config) {
        super(config);
    }

    @Override
    protected DragonCavePiece createPiece(BlockBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
        return new FireDragonCavePiece(0, boundingBox, male, offset, y, seed);
    }

    @Override
    protected double getGenerateChance() {
        return IafCommonConfig.INSTANCE.worldGen.generateFireDragonCaveChance.getValue();
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.FIRE_DRAGON_CAVE.get();
    }

    public static class FireDragonCavePiece extends DragonCavePiece {
        public static final Identifier FIRE_DRAGON_CHEST = Identifier.of(IceAndFire.MOD_ID, "chest/fire_dragon_female_cave");
        public static final Identifier FIRE_DRAGON_CHEST_MALE = Identifier.of(IceAndFire.MOD_ID, "chest/fire_dragon_male_cave");

        protected FireDragonCavePiece(int length, BlockBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
            super(IafStructurePieces.FIRE_DRAGON_CAVE.get(), length, boundingBox, male, offset, y, seed);
        }

        public FireDragonCavePiece(StructureContext context, NbtCompound nbt) {
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
            return IafBlocks.GOLD_PILE.get().getDefaultState();
        }

        @Override
        protected BlockState getPaletteBlock(Random random) {
            return (random.nextBoolean() ? IafBlocks.CHARRED_STONE : IafBlocks.CHARRED_COBBLESTONE).get().getDefaultState();
        }

        @Override
        protected Identifier getChestTable(boolean male) {
            return male ? FIRE_DRAGON_CHEST_MALE : FIRE_DRAGON_CHEST;
        }

        @Override
        protected EntityType<? extends EntityDragonBase> getDragonType() {
            return IafEntities.FIRE_DRAGON.get();
        }
    }
}
