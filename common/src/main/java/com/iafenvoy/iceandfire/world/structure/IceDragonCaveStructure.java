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

public class IceDragonCaveStructure extends DragonCaveStructure {
    public static final Codec<IceDragonCaveStructure> CODEC = RecordCodecBuilder.<IceDragonCaveStructure>mapCodec(instance ->
            instance.group(configCodecBuilder(instance)).apply(instance, IceDragonCaveStructure::new)).codec();

    protected IceDragonCaveStructure(Config config) {
        super(config);
    }

    @Override
    protected DragonCavePiece createPiece(BlockBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
        return new IceDragonCavePiece(0, boundingBox, male, offset, y, seed);
    }

    @Override
    protected double getGenerateChance() {
        return IafCommonConfig.INSTANCE.worldGen.generateIceDragonCaveChance.getValue();
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.ICE_DRAGON_CAVE.get();
    }

    public static class IceDragonCavePiece extends DragonCavePiece {
        public static final Identifier ICE_DRAGON_CHEST = Identifier.of(IceAndFire.MOD_ID, "chest/ice_dragon_female_cave");
        public static final Identifier ICE_DRAGON_CHEST_MALE = Identifier.of(IceAndFire.MOD_ID, "chest/ice_dragon_male_cave");

        protected IceDragonCavePiece(int length, BlockBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
            super(IafStructurePieces.ICE_DRAGON_CAVE.get(), length, boundingBox, male, offset, y, seed);
        }

        public IceDragonCavePiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.ICE_DRAGON_CAVE.get(), nbt);
        }

        @Override
        protected TagKey<Block> getOreTag() {
            return IafBlockTags.ICE_DRAGON_CAVE_ORES;
        }

        @Override
        protected WorldGenCaveStalactites getCeilingDecoration() {
            return new WorldGenCaveStalactites(IafBlocks.FROZEN_STONE.get(), 3);
        }

        @Override
        protected BlockState getTreasurePile() {
            return IafBlocks.SILVER_PILE.get().getDefaultState();
        }

        @Override
        protected BlockState getPaletteBlock(Random random) {
            return (random.nextBoolean() ? IafBlocks.FROZEN_STONE : IafBlocks.FROZEN_COBBLESTONE).get().getDefaultState();
        }

        @Override
        protected Identifier getChestTable(boolean male) {
            return male ? ICE_DRAGON_CHEST_MALE : ICE_DRAGON_CHEST;
        }

        @Override
        protected EntityType<? extends EntityDragonBase> getDragonType() {
            return IafEntities.ICE_DRAGON.get();
        }
    }
}
