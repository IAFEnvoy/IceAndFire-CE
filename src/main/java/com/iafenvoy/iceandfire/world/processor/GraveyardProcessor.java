package com.iafenvoy.iceandfire.world.processor;

import com.iafenvoy.iceandfire.registry.IafProcessors;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

public class GraveyardProcessor extends StructureProcessor {
    public static final GraveyardProcessor INSTANCE = new GraveyardProcessor();
    public static final MapCodec<GraveyardProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

    public static BlockState getRandomCobblestone(RandomSource random) {
        float rand = random.nextFloat();
        if (rand < 0.5) return Blocks.COBBLESTONE.defaultBlockState();
        else if (rand < 0.9) return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        else return Blocks.INFESTED_COBBLESTONE.defaultBlockState();
    }

    public static BlockState getRandomCrackedBlock(RandomSource random) {
        float rand = random.nextFloat();
        if (rand < 0.5) return Blocks.STONE_BRICKS.defaultBlockState();
        else if (rand < 0.9) return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        else return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(@NotNull LevelReader world, @NotNull BlockPos pos, @NotNull BlockPos pivot, StructureTemplate.@NotNull StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlaceSettings data) {
        RandomSource random = data.getRandom(currentBlockInfo.pos());
        if (currentBlockInfo.state().getBlock() == Blocks.STONE_BRICKS) {
            BlockState state = getRandomCrackedBlock(random);
            return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), state, null);
        }
        if (currentBlockInfo.state().getBlock() == Blocks.COBBLESTONE) {
            BlockState state = getRandomCobblestone(random);
            return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), state, null);
        }
        return currentBlockInfo;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return IafProcessors.GRAVEYARD_PROCESSOR.get();
    }
}
