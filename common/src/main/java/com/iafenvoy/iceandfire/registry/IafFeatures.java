package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.tag.IafBiomeTags;
import com.iafenvoy.iceandfire.world.feature.*;
import com.iafenvoy.iceandfire.world.feature.config.DeathWormFeatureConfig;
import com.iafenvoy.iceandfire.world.feature.config.DragonSkeletonFeatureConfig;
import com.iafenvoy.iceandfire.world.feature.config.EntitySpawnFeatureConfig;
import com.iafenvoy.iceandfire.world.feature.config.SeaSerpentFeatureConfig;
import com.iafenvoy.iceandfire.world.feature.config.StymphalianBirdFeatureConfig;
import com.iafenvoy.iceandfire.world.feature.config.WanderingCyclopsFeatureConfig;
import dev.architectury.platform.Platform;
import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class IafFeatures {
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.FEATURE);

    public static final RegistrySupplier<Feature<DeathWormFeatureConfig>> SPAWN_DEATH_WORM = feature("spawn_death_worm", DeathWormSpawnFeature::new);
    public static final RegistrySupplier<Feature<DragonSkeletonFeatureConfig>> SPAWN_DRAGON_SKELETON = feature("spawn_dragon_skeleton", DragonSkeletonSpawnFeature::new);
    public static final RegistrySupplier<Feature<EntitySpawnFeatureConfig>> SPAWN_HIPPOCAMPUS = feature("spawn_hippocampus", HippocampusSpawnFeature::new);
    public static final RegistrySupplier<Feature<SeaSerpentFeatureConfig>> SPAWN_SEA_SERPENT = feature("spawn_sea_serpent", SeaSerpentSpawnFeature::new);
    public static final RegistrySupplier<Feature<StymphalianBirdFeatureConfig>> SPAWN_STYMPHALIAN_BIRD = feature("spawn_stymphalian_bird", StymphalianBirdSpawnFeature::new);
    public static final RegistrySupplier<Feature<WanderingCyclopsFeatureConfig>> SPAWN_WANDERING_CYCLOPS = feature("spawn_wandering_cyclops", WanderingCyclopsSpawnFeature::new);

    private static <F extends Feature<? extends FeatureConfig>> RegistrySupplier<F> feature(String name, Supplier<F> feature) {
        return REGISTRY.register(name, feature);
    }

    public static final RegistryKey<ConfiguredFeature<?, ?>> DREADWOOD = configuredFeature("dreadwood");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DREADWOOD_LARGE = configuredFeature("dreadwood_large");

    public static final RegistryKey<PlacedFeature> PLACED_SPAWN_DEATH_WORM = placeFeature("spawn_death_worm");
    public static final RegistryKey<PlacedFeature> PLACED_SPAWN_DRAGON_SKELETON_L = placeFeature("spawn_dragon_skeleton_lightning");
    public static final RegistryKey<PlacedFeature> PLACED_SPAWN_DRAGON_SKELETON_F = placeFeature("spawn_dragon_skeleton_fire");
    public static final RegistryKey<PlacedFeature> PLACED_SPAWN_DRAGON_SKELETON_I = placeFeature("spawn_dragon_skeleton_ice");
    public static final RegistryKey<PlacedFeature> PLACED_SPAWN_HIPPOCAMPUS = placeFeature("spawn_hippocampus");
    public static final RegistryKey<PlacedFeature> PLACED_SPAWN_SEA_SERPENT = placeFeature("spawn_sea_serpent");
    public static final RegistryKey<PlacedFeature> PLACED_SPAWN_STYMPHALIAN_BIRD = placeFeature("spawn_stymphalian_bird");
    public static final RegistryKey<PlacedFeature> PLACED_SPAWN_WANDERING_CYCLOPS = placeFeature("spawn_wandering_cyclops");
    public static final RegistryKey<PlacedFeature> PLACED_SILVER_ORE = placeFeature("silver_ore");
    public static final RegistryKey<PlacedFeature> PLACED_SAPPHIRE_ORE = placeFeature("sapphire_ore");
    public static final RegistryKey<PlacedFeature> PLACED_FIRE_LILY = placeFeature("fire_lily");
    public static final RegistryKey<PlacedFeature> PLACED_LIGHTNING_LILY = placeFeature("lightning_lily");
    public static final RegistryKey<PlacedFeature> PLACED_FROST_LILY = placeFeature("frost_lily");

    public static RegistryKey<ConfiguredFeature<?, ?>> configuredFeature(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(IceAndFire.MOD_ID, name));
    }

    public static RegistryKey<PlacedFeature> placeFeature(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(IceAndFire.MOD_ID, name));
    }

    public static void init() {
        if (Platform.isNeoForge()) return;

        addFeatureToBiome(IafBiomeTags.FIRE, PLACED_FIRE_LILY, GenerationStep.Feature.VEGETAL_DECORATION);
        addFeatureToBiome(IafBiomeTags.ICE, PLACED_FROST_LILY, GenerationStep.Feature.VEGETAL_DECORATION);
        addFeatureToBiome(IafBiomeTags.LIGHTNING, PLACED_LIGHTNING_LILY, GenerationStep.Feature.VEGETAL_DECORATION);
        addFeatureToBiome(IafBiomeTags.FIRE, PLACED_SPAWN_DRAGON_SKELETON_F, GenerationStep.Feature.SURFACE_STRUCTURES);
        addFeatureToBiome(IafBiomeTags.ICE, PLACED_SPAWN_DRAGON_SKELETON_I, GenerationStep.Feature.SURFACE_STRUCTURES);
        addFeatureToBiome(IafBiomeTags.LIGHTNING, PLACED_SPAWN_DRAGON_SKELETON_L, GenerationStep.Feature.SURFACE_STRUCTURES);

        addFeatureToBiome(IafBiomeTags.SILVER_ORE, PLACED_SILVER_ORE, GenerationStep.Feature.UNDERGROUND_ORES);
        addFeatureToBiome(IafBiomeTags.SAPPHIRE_ORE, PLACED_SAPPHIRE_ORE, GenerationStep.Feature.UNDERGROUND_ORES);

        addFeatureToBiome(IafBiomeTags.DEATHWORM, PLACED_SPAWN_DEATH_WORM, GenerationStep.Feature.SURFACE_STRUCTURES);
        addFeatureToBiome(IafBiomeTags.WANDERING_CYCLOPS, PLACED_SPAWN_WANDERING_CYCLOPS, GenerationStep.Feature.SURFACE_STRUCTURES);
        addFeatureToBiome(IafBiomeTags.HIPPOCAMPUS, PLACED_SPAWN_HIPPOCAMPUS, GenerationStep.Feature.SURFACE_STRUCTURES);
        addFeatureToBiome(IafBiomeTags.SEA_SERPENT, PLACED_SPAWN_SEA_SERPENT, GenerationStep.Feature.SURFACE_STRUCTURES);
        addFeatureToBiome(IafBiomeTags.STYMPHALIAN_BIRD, PLACED_SPAWN_STYMPHALIAN_BIRD, GenerationStep.Feature.SURFACE_STRUCTURES);
    }

    private static void addFeatureToBiome(TagKey<Biome> biomeTag, RegistryKey<PlacedFeature> featureResource, GenerationStep.Feature step) {
        BiomeModifications.addProperties(context -> context.hasTag(biomeTag), (context, mutable) -> mutable.getGenerationProperties().addFeature(step, featureResource));
    }
}
