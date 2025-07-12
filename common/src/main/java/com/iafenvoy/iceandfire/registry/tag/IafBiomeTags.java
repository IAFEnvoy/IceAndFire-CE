package com.iafenvoy.iceandfire.registry.tag;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public final class IafBiomeTags {
    public static final TagKey<Biome> GORGON_TEMPLE = createKey("structure_gen/gorgon_temple");
    public static final TagKey<Biome> MAUSOLEUM = createKey("structure_gen/mausoleum");
    public static final TagKey<Biome> NO_GRAVEYARD = createKey("structure_gen/no_graveyard");
    public static final TagKey<Biome> FIRE = createKey("structure_gen/fire");
    public static final TagKey<Biome> ICE = createKey("structure_gen/ice");
    public static final TagKey<Biome> LIGHTNING = createKey("structure_gen/lightning");
    public static final TagKey<Biome> CYCLOPS_CAVE = createKey("structure_gen/cyclops_cave");
    public static final TagKey<Biome> HYDRA_CAVE = createKey("structure_gen/hydra_cave");
    public static final TagKey<Biome> PIXIE_VILLAGE = createKey("structure_gen/pixie_village");
    public static final TagKey<Biome> SIREN_ISLAND = createKey("structure_gen/siren_island");

    public static final TagKey<Biome> SILVER_ORE = createKey("ore_gen/silver");
    public static final TagKey<Biome> SAPPHIRE_ORE = createKey("ore_gen/sapphire");

    public static final TagKey<Biome> DEATHWORM = createKey("entity_gen/deathworm");
    public static final TagKey<Biome> WANDERING_CYCLOPS = createKey("entity_gen/wandering_cyclops");
    public static final TagKey<Biome> HIPPOCAMPUS = createKey("entity_gen/hippocampus");
    public static final TagKey<Biome> SEA_SERPENT = createKey("entity_gen/sea_serpent");
    public static final TagKey<Biome> STYMPHALIAN_BIRD = createKey("entity_gen/stymphalian_bird");
    public static final TagKey<Biome> HIPPOGRYPH = createKey("entity_gen/hippogryph");
    public static final TagKey<Biome> HIPPOGRYPH_BLACK = createKey("entity_gen/hippogryph_black");
    public static final TagKey<Biome> HIPPOGRYPH_BROWN = createKey("entity_gen/hippogryph_brown");
    public static final TagKey<Biome> HIPPOGRYPH_CHESTNUT = createKey("entity_gen/hippogryph_chestnut");
    public static final TagKey<Biome> HIPPOGRYPH_CREAMY = createKey("entity_gen/hippogryph_creamy");
    public static final TagKey<Biome> HIPPOGRYPH_DARK_BROWN = createKey("entity_gen/hippogryph_dark_brown");
    public static final TagKey<Biome> HIPPOGRYPH_GRAY = createKey("entity_gen/hippogryph_gray");
    public static final TagKey<Biome> HIPPOGRYPH_WHITE = createKey("entity_gen/hippogryph_white");
    public static final TagKey<Biome> COCKATRICE = createKey("entity_gen/cockatrice");
    public static final TagKey<Biome> AMPHITHERE = createKey("entity_gen/amphithere");
    public static final TagKey<Biome> TROLL = createKey("entity_gen/troll");
    public static final TagKey<Biome> FOREST_TROLL = createKey("entity_gen/troll_forest");
    public static final TagKey<Biome> MOUNTAIN_TROLL = createKey("entity_gen/troll_mountain");
    public static final TagKey<Biome> SNOWY_TROLL = createKey("entity_gen/troll_snowy");

    private static TagKey<Biome> createKey(final String name) {
        return TagKey.of(RegistryKeys.BIOME, Identifier.of(IceAndFire.MOD_ID, name));
    }
}
