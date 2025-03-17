package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.registry.tag.CommonTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.StructureContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.structure.StructureType;

public class FireDragonRoostStructure extends DragonRoostStructure {
    public static final Codec<FireDragonRoostStructure> CODEC = RecordCodecBuilder.<FireDragonRoostStructure>mapCodec(instance ->
            instance.group(configCodecBuilder(instance)).apply(instance, FireDragonRoostStructure::new)).codec();

    protected FireDragonRoostStructure(Config config) {
        super(config);
    }

    @Override
    protected DragonRoostPiece createPiece(BlockBox boundingBox, boolean isMale) {
        return new FireDragonRoostPiece(0, boundingBox, IafBlocks.GOLD_PILE.get(), isMale);
    }

    @Override
    protected double getGenerateChance() {
        return IafCommonConfig.INSTANCE.worldGen.generateFireDragonRoostChance.getValue();
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.FIRE_DRAGON_ROOST.get();
    }

    public static class FireDragonRoostPiece extends DragonRoostPiece {
        private static final Identifier DRAGON_CHEST = Identifier.of(IceAndFire.MOD_ID, "chest/fire_dragon_roost");

        protected FireDragonRoostPiece(int length, BlockBox boundingBox, Block treasureBlock, boolean isMale) {
            super(IafStructurePieces.FIRE_DRAGON_ROOST.get(), length, boundingBox, treasureBlock, isMale);
        }

        public FireDragonRoostPiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.FIRE_DRAGON_ROOST.get(), nbt);
        }

        @Override
        protected EntityType<? extends EntityDragonBase> getDragonType() {
            return IafEntities.FIRE_DRAGON.get();
        }

        @Override
        protected Identifier getRoostLootTable() {
            return DRAGON_CHEST;
        }

        @Override
        protected BlockState transform(final BlockState state) {
            Block block = null;
            if (state.isOf(Blocks.GRASS_BLOCK))
                block = IafBlocks.CHARRED_GRASS.get();
            else if (state.isOf(Blocks.DIRT_PATH))
                block = IafBlocks.CHARRED_DIRT_PATH.get();
            else if (state.isIn(CommonTags.Blocks.GRAVEL))
                block = IafBlocks.CHARRED_GRAVEL.get();
            else if (state.isIn(BlockTags.DIRT))
                block = IafBlocks.CHARRED_DIRT.get();
            else if (state.isIn(CommonTags.Blocks.STONE))
                block = IafBlocks.CHARRED_STONE.get();
            else if (state.isIn(CommonTags.Blocks.COBBLESTONE))
                block = IafBlocks.CHARRED_COBBLESTONE.get();
            else if (state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.PLANKS))
                block = IafBlocks.ASH.get();
            else if (state.isOf(Blocks.GRASS) || state.isIn(BlockTags.LEAVES) || state.isIn(BlockTags.FLOWERS) || state.isIn(BlockTags.CROPS))
                block = Blocks.AIR;
            if (block != null) return block.getDefaultState();
            return state;
        }

        @Override
        protected void handleCustomGeneration(StructureWorldAccess world, BlockPos origin, Random random, BlockPos position, double distance) {
            if (random.nextInt(1000) == 0)
                this.generateRoostPile(world, random, this.getSurfacePosition(world, position), IafBlocks.ASH.get());
        }
    }
}
