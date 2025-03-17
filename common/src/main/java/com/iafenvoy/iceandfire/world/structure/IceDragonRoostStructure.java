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

public class IceDragonRoostStructure extends DragonRoostStructure {
    public static final Codec<IceDragonRoostStructure> CODEC = RecordCodecBuilder.<IceDragonRoostStructure>mapCodec(instance ->
            instance.group(configCodecBuilder(instance)).apply(instance, IceDragonRoostStructure::new)).codec();

    protected IceDragonRoostStructure(Config config) {
        super(config);
    }

    @Override
    protected DragonRoostPiece createPiece(BlockBox boundingBox, boolean isMale) {
        return new IceDragonRoostPiece(0, boundingBox, IafBlocks.SILVER_PILE.get(), isMale);
    }

    @Override
    protected double getGenerateChance() {
        return IafCommonConfig.INSTANCE.worldGen.generateIceDragonRoostChance.getValue();
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.ICE_DRAGON_ROOST.get();
    }

    public static class IceDragonRoostPiece extends DragonRoostPiece {
        private static final Identifier DRAGON_CHEST = Identifier.of(IceAndFire.MOD_ID, "chest/ice_dragon_roost");

        protected IceDragonRoostPiece(int length, BlockBox boundingBox, Block treasureBlock, boolean isMale) {
            super(IafStructurePieces.ICE_DRAGON_ROOST.get(), length, boundingBox, treasureBlock, isMale);
        }

        public IceDragonRoostPiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.ICE_DRAGON_ROOST.get(), nbt);
        }

        @Override
        protected EntityType<? extends EntityDragonBase> getDragonType() {
            return IafEntities.ICE_DRAGON.get();
        }

        @Override
        protected Identifier getRoostLootTable() {
            return DRAGON_CHEST;
        }

        @Override
        protected BlockState transform(final BlockState state) {
            Block block = null;
            if (state.isOf(Blocks.GRASS_BLOCK))
                block = IafBlocks.FROZEN_GRASS.get();
            else if (state.isOf(Blocks.DIRT_PATH))
                block = IafBlocks.FROZEN_DIRT_PATH.get();
            else if (state.isIn(CommonTags.Blocks.GRAVEL))
                block = IafBlocks.FROZEN_GRAVEL.get();
            else if (state.isIn(BlockTags.DIRT))
                block = IafBlocks.FROZEN_DIRT.get();
            else if (state.isIn(CommonTags.Blocks.STONE))
                block = IafBlocks.FROZEN_STONE.get();
            else if (state.isIn(CommonTags.Blocks.COBBLESTONE))
                block = IafBlocks.FROZEN_COBBLESTONE.get();
            else if (state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.PLANKS))
                block = IafBlocks.FROZEN_SPLINTERS.get();
            else if (state.isOf(Blocks.GRASS) || state.isIn(BlockTags.LEAVES) || state.isIn(BlockTags.FLOWERS) || state.isIn(BlockTags.CROPS))
                block = Blocks.AIR;
            if (block != null) return block.getDefaultState();
            return state;
        }

        @Override
        protected void handleCustomGeneration(StructureWorldAccess world, BlockPos origin, Random random, BlockPos position, double distance) {
            if (random.nextInt(1000) == 0)
                this.generateRoostPile(world, random, this.getSurfacePosition(world, position), IafBlocks.DRAGON_ICE.get());
        }
    }
}
