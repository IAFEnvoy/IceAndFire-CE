package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.registry.tag.IafBiomeTags;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.level.biome.Biome;

public final class IafTrollTypes {
    public static final TrollType FOREST = register("forest", IafArmorMaterials.TROLL_FOREST, IafBiomeTags.FOREST_TROLL, TrollType.BuiltinWeapon.TRUNK, TrollType.BuiltinWeapon.COLUMN_FOREST, TrollType.BuiltinWeapon.AXE, TrollType.BuiltinWeapon.HAMMER);
    public static final TrollType FROST = register("frost", IafArmorMaterials.TROLL_FROST, IafBiomeTags.SNOWY_TROLL, TrollType.BuiltinWeapon.COLUMN_FROST, TrollType.BuiltinWeapon.TRUNK_FROST, TrollType.BuiltinWeapon.AXE, TrollType.BuiltinWeapon.HAMMER);
    public static final TrollType MOUNTAIN = register("mountain", IafArmorMaterials.TROLL_MOUNTAIN, IafBiomeTags.MOUNTAIN_TROLL, TrollType.BuiltinWeapon.COLUMN, TrollType.BuiltinWeapon.AXE, TrollType.BuiltinWeapon.HAMMER);

    private static TrollType register(String name, Holder<ArmorMaterial> material, TagKey<Biome> spawnBiomes, TrollType.BuiltinWeapon... weapons) {
        return Registry.register(IafRegistries.TROLL_TYPE, ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, name), new TrollType(name, material, spawnBiomes, weapons));
    }

    public static void init() {
    }
}
