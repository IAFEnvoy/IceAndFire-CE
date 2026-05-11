package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.world.StructureGenerationConfig;
import com.iafenvoy.iceandfire.world.DangerousGeneration;
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
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.DimensionPadding;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class GorgonTempleStructure extends IafJigsawStructure implements DangerousGeneration {
    public static final MapCodec<GorgonTempleStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(configCodecBuilder(instance),
                    StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    StructureGenerationConfig.CODEC.optionalFieldOf("generation", StructureGenerationConfig.DEFAULT)
                            .forGetter(s -> s.generationConfig)
            ).apply(instance, GorgonTempleStructure::new));

    private final StructureGenerationConfig generationConfig;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public GorgonTempleStructure(Config config, RegistryEntry<StructurePool> startPool, Optional<Identifier> startJigsawName, int size, HeightProvider startHeight, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter, StructureGenerationConfig generationConfig) {
        super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter);
        this.generationConfig = generationConfig;
    }

    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        if (context.random().nextDouble() >= this.generationConfig.generateChance())
            return Optional.empty();
        ChunkPos pos = context.chunkPos();
        BlockPos blockpos = pos.getCenterAtY(1);
        if (!this.isFarEnoughFromSpawn(blockpos)) return Optional.empty();
        return StructurePoolBasedGenerator.generate(
                context, // Used for JigsawPlacement to get all the proper behaviors done.
                this.startPool, // The starting pool to use to create the structure layout from
                this.startJigsawName, // Can be used to only spawn from one Jigsaw block. But we don't need to worry about this.
                this.size, // How deep a branch of pieces can go away from center piece. (5 means branches cannot be longer than 5 pieces from center piece)
                blockpos, // Where to spawn the structure.
                false, // "useExpansionHack" This is for legacy villages to generate properly. You should keep this false always.
                this.projectStartToHeightmap, // Adds the terrain height's y value to the passed in blockpos's y value. (This uses WORLD_SURFACE_WG heightmap which stops at top water too)
                // Here, blockpos's y value is 60 which means the structure spawn 60 blocks above terrain height.
                // Set this to false for structure to be place only at the passed in blockpos's Y value instead.
                // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                this.maxDistanceFromCenter,
                StructurePoolAliasLookup.EMPTY,
                DimensionPadding.NONE,
                StructureLiquidSettings.IGNORE_WATERLOGGING);
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.GORGON_TEMPLE.get();
    }
}
