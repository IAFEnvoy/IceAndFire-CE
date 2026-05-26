package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.BestiaryPage;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class IafBestiaryPages {
    public static final BestiaryPage INTRODUCTION = register("introduction", 2);
    public static final BestiaryPage FIRE_DRAGON = register("firedragon", 4);
    public static final BestiaryPage FIRE_DRAGON_EGG = register("firedragonegg", 1);
    public static final BestiaryPage ICE_DRAGON = register("icedragon", 4);
    public static final BestiaryPage ICE_DRAGON_EGG = register("icedragonegg", 1);
    public static final BestiaryPage LIGHTNING_DRAGON = register("lightningdragon", 5);
    public static final BestiaryPage LIGHTNING_DRAGON_EGG = register("lightningdragonegg", 1);
    public static final BestiaryPage TAMED_DRAGONS = register("tameddragons", 3);
    public static final BestiaryPage MATERIALS = register("materials", 2);
    public static final BestiaryPage ALCHEMY = register("alchemy", 1);
    public static final BestiaryPage DRAGON_FORGE = register("dragonforge", 3);
    public static final BestiaryPage HIPPOGRYPH = register("hippogryph", 1);
    public static final BestiaryPage GORGON = register("gorgon", 1);
    public static final BestiaryPage PIXIE = register("pixie", 1);
    public static final BestiaryPage CYCLOPS = register("cyclops", 2);
    public static final BestiaryPage SIREN = register("siren", 2);
    public static final BestiaryPage HIPPOCAMPUS = register("hippocampus", 2);
    public static final BestiaryPage DEATHWORM = register("deathworm", 3);
    public static final BestiaryPage COCKATRICE = register("cockatrice", 2);
    public static final BestiaryPage STYMPHALIAN_BIRD = register("stymphalianbird", 1);
    public static final BestiaryPage TROLL = register("troll", 2);
    public static final BestiaryPage AMPHITHERE = register("amphithere", 2);
    public static final BestiaryPage SEA_SERPENT = register("seaserpent", 2);
    public static final BestiaryPage DREAD_MOBS = register("dread_mobs", 1);
    public static final BestiaryPage GHOST = register("ghost", 1);

    private static BestiaryPage register(String name, int page) {
        return Registry.register(IafRegistries.BESTIARY_PAGE, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, name), new BestiaryPage(name, page));
    }

    public static void init() {
    }
}
