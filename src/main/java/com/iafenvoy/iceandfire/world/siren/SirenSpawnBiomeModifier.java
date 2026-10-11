package com.iafenvoy.iceandfire.world.siren;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public record SirenSpawnBiomeModifier(HolderSet<Biome> biomes) implements BiomeModifier {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> REGISTRY =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, IceAndFire.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<SirenSpawnBiomeModifier>> SIREN_SPAWNS =
            REGISTRY.register("siren_spawns", () -> SirenSpawnBiomeModifier.CODEC);

    public static final MapCodec<SirenSpawnBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(SirenSpawnBiomeModifier::biomes)
    ).apply(instance, SirenSpawnBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD || !this.biomes.contains(biome)) return;
        if (!IafCommonConfig.INSTANCE.siren.beachSpawns.getValue()) return;
        int weight = IafCommonConfig.INSTANCE.siren.beachSpawnWeight.getValue();
        if (weight <= 0) return;
        int min = Math.max(1, IafCommonConfig.INSTANCE.siren.beachMinGroupSize.getValue());
        int max = Math.max(min, IafCommonConfig.INSTANCE.siren.beachMaxGroupSize.getValue());
        EntityType<?> type = IafEntities.SIREN.get();
        builder.getMobSpawnSettings().addSpawn(type.getCategory(), weight, new MobSpawnSettings.SpawnerData(type, min, max));
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return SIREN_SPAWNS.get();
    }
}
