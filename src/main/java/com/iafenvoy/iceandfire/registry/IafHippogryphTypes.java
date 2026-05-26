package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.HippogryphType;
import com.iafenvoy.iceandfire.registry.tag.IafBiomeTags;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class IafHippogryphTypes {
    public static final HippogryphType BLACK = register("black", false, IafBiomeTags.HIPPOGRYPH_BLACK);
    public static final HippogryphType BROWN = register("brown", false, IafBiomeTags.HIPPOGRYPH_BROWN);
    public static final HippogryphType GRAY = register("gray", false, IafBiomeTags.HIPPOGRYPH_GRAY);
    public static final HippogryphType CHESTNUT = register("chestnut", false, IafBiomeTags.HIPPOGRYPH_CHESTNUT);
    public static final HippogryphType CREAMY = register("creamy", false, IafBiomeTags.HIPPOGRYPH_CREAMY);
    public static final HippogryphType DARK_BROWN = register("dark_brown", false, IafBiomeTags.HIPPOGRYPH_DARK_BROWN);
    public static final HippogryphType WHITE = register("white", false, IafBiomeTags.HIPPOGRYPH_WHITE);
    public static final HippogryphType RAPTOR = register("raptor", true, IafBiomeTags.EMPTY);
    public static final HippogryphType ALEX = register("alex", true, IafBiomeTags.EMPTY);
    public static final HippogryphType DODO = register("dodo", true, IafBiomeTags.EMPTY);

    private static HippogryphType register(String name, boolean developer, TagKey<Biome> spawnBiomes) {
        return Registry.register(IafRegistries.HIPPOGRYPH_TYPE, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, name), new HippogryphType(name, developer, spawnBiomes));
    }

    public static void init() {
    }
}
