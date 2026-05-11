package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.DimensionPadding;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class DreadRuinStructure extends IafJigsawStructure {
    public static final MapCodec<DreadRuinStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    configCodecBuilder(instance),
                    StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(s -> s.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(s -> s.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(s -> s.startHeight),
                    Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(s -> s.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(s -> s.maxDistanceFromCenter)
            ).apply(instance, DreadRuinStructure::new));

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public DreadRuinStructure(Config config, RegistryEntry<StructurePool> startPool, Optional<Identifier> startJigsawName,
                               int size, HeightProvider startHeight, Optional<Heightmap.Type> projectStartToHeightmap,
                               int maxDistanceFromCenter) {
        super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter);
    }

    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        if (context.random().nextDouble() >= IafCommonConfig.INSTANCE.worldGen.generateDreadRuinChance.getValue())
            return Optional.empty();
        BlockPos blockPos = context.chunkPos().getCenterAtY(1);
        return StructurePoolBasedGenerator.generate(
                context,
                this.startPool,
                this.startJigsawName,
                this.size,
                blockPos,
                false,
                this.projectStartToHeightmap,
                this.maxDistanceFromCenter,
                StructurePoolAliasLookup.EMPTY,
                DimensionPadding.NONE,
                StructureLiquidSettings.IGNORE_WATERLOGGING);
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.DREAD_RUIN.get();
    }
}
