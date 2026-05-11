package com.iafenvoy.iceandfire.registry.tag;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public final class IafBiomeTags {
    public static final TagKey<Biome> EMPTY = create("empty");

    public static final TagKey<Biome> GORGON_TEMPLE = create("structure_gen/gorgon_temple");
    public static final TagKey<Biome> MAUSOLEUM = create("structure_gen/mausoleum");
    public static final TagKey<Biome> NO_GRAVEYARD = create("structure_gen/no_graveyard");
    public static final TagKey<Biome> FIRE = create("structure_gen/fire");
    public static final TagKey<Biome> ICE = create("structure_gen/ice");
    public static final TagKey<Biome> LIGHTNING = create("structure_gen/lightning");
    public static final TagKey<Biome> CYCLOPS_CAVE = create("structure_gen/cyclops_cave");
    public static final TagKey<Biome> HYDRA_CAVE = create("structure_gen/hydra_cave");
    public static final TagKey<Biome> PIXIE_VILLAGE = create("structure_gen/pixie_village");
    public static final TagKey<Biome> SIREN_ISLAND = create("structure_gen/siren_island");
    public static final TagKey<Biome> DREAD_RUIN = create("structure_gen/dread_ruin");
    public static final TagKey<Biome> DREAD_PORTAL = create("structure_gen/dread_portal");

    public static final TagKey<Biome> SILVER_ORE = create("ore_gen/silver");
    public static final TagKey<Biome> SAPPHIRE_ORE = create("ore_gen/sapphire");

    public static final TagKey<Biome> DEATHWORM = create("entity_gen/deathworm");
    public static final TagKey<Biome> WANDERING_CYCLOPS = create("entity_gen/wandering_cyclops");
    public static final TagKey<Biome> HIPPOCAMPUS = create("entity_gen/hippocampus");
    public static final TagKey<Biome> SEA_SERPENT = create("entity_gen/sea_serpent");
    public static final TagKey<Biome> STYMPHALIAN_BIRD = create("entity_gen/stymphalian_bird");
    public static final TagKey<Biome> HIPPOGRYPH = create("entity_gen/hippogryph");
    public static final TagKey<Biome> HIPPOGRYPH_BLACK = create("entity_gen/hippogryph_black");
    public static final TagKey<Biome> HIPPOGRYPH_BROWN = create("entity_gen/hippogryph_brown");
    public static final TagKey<Biome> HIPPOGRYPH_CHESTNUT = create("entity_gen/hippogryph_chestnut");
    public static final TagKey<Biome> HIPPOGRYPH_CREAMY = create("entity_gen/hippogryph_creamy");
    public static final TagKey<Biome> HIPPOGRYPH_DARK_BROWN = create("entity_gen/hippogryph_dark_brown");
    public static final TagKey<Biome> HIPPOGRYPH_GRAY = create("entity_gen/hippogryph_gray");
    public static final TagKey<Biome> HIPPOGRYPH_WHITE = create("entity_gen/hippogryph_white");
    public static final TagKey<Biome> COCKATRICE = create("entity_gen/cockatrice");
    public static final TagKey<Biome> AMPHITHERE = create("entity_gen/amphithere");
    public static final TagKey<Biome> TROLL = create("entity_gen/troll");
    public static final TagKey<Biome> FOREST_TROLL = create("entity_gen/troll_forest");
    public static final TagKey<Biome> MOUNTAIN_TROLL = create("entity_gen/troll_mountain");
    public static final TagKey<Biome> SNOWY_TROLL = create("entity_gen/troll_snowy");

    private static TagKey<Biome> create(final String name) {
        return TagKey.of(RegistryKeys.BIOME, Identifier.of(IceAndFire.MOD_ID, name));
    }
}
