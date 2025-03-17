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

public class LightningDragonCaveStructure extends DragonCaveStructure {
    public static final Codec<LightningDragonCaveStructure> CODEC = RecordCodecBuilder.<LightningDragonCaveStructure>mapCodec(instance ->
            instance.group(configCodecBuilder(instance)).apply(instance, LightningDragonCaveStructure::new)).codec();

    protected LightningDragonCaveStructure(Config config) {
        super(config);
    }

    @Override
    protected DragonCavePiece createPiece(BlockBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
        return new LightningDragonCavePiece(0, boundingBox, male, offset, y, seed);
    }

    @Override
    protected double getGenerateChance() {
        return IafCommonConfig.INSTANCE.worldGen.generateLightningDragonCaveChance.getValue();
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.LIGHTNING_DRAGON_CAVE.get();
    }

    public static class LightningDragonCavePiece extends DragonCavePiece {
        public static final Identifier LIGHTNING_DRAGON_CHEST = Identifier.of(IceAndFire.MOD_ID, "chest/lightning_dragon_female_cave");
        public static final Identifier LIGHTNING_DRAGON_CHEST_MALE = Identifier.of(IceAndFire.MOD_ID, "chest/lightning_dragon_male_cave");

        protected LightningDragonCavePiece(int length, BlockBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
            super(IafStructurePieces.LIGHTNING_DRAGON_CAVE.get(), length, boundingBox, male, offset, y, seed);
        }

        public LightningDragonCavePiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.LIGHTNING_DRAGON_CAVE.get(), nbt);
        }

        @Override
        protected TagKey<Block> getOreTag() {
            return IafBlockTags.LIGHTNING_DRAGON_CAVE_ORES;
        }

        @Override
        protected WorldGenCaveStalactites getCeilingDecoration() {
            return new WorldGenCaveStalactites(IafBlocks.CRACKLED_STONE.get(), 6);
        }

        @Override
        protected BlockState getTreasurePile() {
            return IafBlocks.COPPER_PILE.get().getDefaultState();
        }

        @Override
        protected BlockState getPaletteBlock(Random random) {
            return (random.nextBoolean() ? IafBlocks.CRACKLED_STONE : IafBlocks.CRACKLED_COBBLESTONE).get().getDefaultState();
        }

        @Override
        protected Identifier getChestTable(boolean male) {
            return male ? LIGHTNING_DRAGON_CHEST_MALE : LIGHTNING_DRAGON_CHEST;
        }

        @Override
        protected EntityType<? extends EntityDragonBase> getDragonType() {
            return IafEntities.LIGHTNING_DRAGON.get();
        }
    }
}
