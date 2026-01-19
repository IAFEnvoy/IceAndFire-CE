package com.iafenvoy.iceandfire.data;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.armor.TrollArmorItem;
import com.iafenvoy.iceandfire.item.tool.TrollWeaponItem;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.uranus.util.RandomHelper;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TrollType {
    //FIXME:: Remove this
    public RegistrySupplier<Item> leather, helmet, chestplate, leggings, boots;
    private final String name;
    private final RegistryEntry<ArmorMaterial> material;
    private final TagKey<Biome> spawnBiomes;
    private final Identifier lootTable;
    private final List<ITrollWeapon> weapons;

    public TrollType(String name, RegistryEntry<ArmorMaterial> material, TagKey<Biome> spawnBiomes, ITrollWeapon... weapons) {
        this.name = name;
        this.weapons = List.of(weapons);
        this.material = material;
        this.spawnBiomes = spawnBiomes;
        this.lootTable = Identifier.of(IceAndFire.MOD_ID, "entities/troll_" + name);
    }

    public static TrollType getBiomeType(RegistryEntry<Biome> biome) {
        List<TrollType> types = IafRegistries.TROLL_TYPE.stream().filter(x -> x.allowSpawn(biome)).toList();
        return RandomHelper.randomOne(types.isEmpty() ? IafRegistries.TROLL_TYPE.stream().toList() : types);
    }

    public static ITrollWeapon getWeaponForType(TrollType troll) {
        return troll.weapons.get(ThreadLocalRandom.current().nextInt(troll.weapons.size()));
    }

    public static void initArmors() {
        for (TrollType troll : IafRegistries.TROLL_TYPE) {
            troll.leather = IafItems.registerItem(String.format(Locale.ROOT, "troll_leather_%s", troll.name.toLowerCase(Locale.ROOT)), () -> new Item(new Item.Settings()));
            troll.helmet = IafItems.registerArmor(TrollArmorItem.getName(troll, ArmorItem.Type.HELMET), () -> new TrollArmorItem(troll, ArmorItem.Type.HELMET));
            troll.chestplate = IafItems.registerArmor(TrollArmorItem.getName(troll, ArmorItem.Type.CHESTPLATE), () -> new TrollArmorItem(troll, ArmorItem.Type.CHESTPLATE));
            troll.leggings = IafItems.registerArmor(TrollArmorItem.getName(troll, ArmorItem.Type.LEGGINGS), () -> new TrollArmorItem(troll, ArmorItem.Type.LEGGINGS));
            troll.boots = IafItems.registerArmor(TrollArmorItem.getName(troll, ArmorItem.Type.BOOTS), () -> new TrollArmorItem(troll, ArmorItem.Type.BOOTS));
        }
    }

    public static List<TrollType> values() {
        return IafRegistries.TROLL_TYPE.stream().toList();
    }

    public static TrollType getByName(String name) {
        return IafRegistries.TROLL_TYPE.get(IceAndFire.id(name));
    }

    public String getName() {
        return this.name;
    }

    public Identifier getLootTable() {
        return this.lootTable;
    }

    public RegistryEntry<ArmorMaterial> getMaterial() {
        return this.material;
    }

    public Identifier getTexture() {
        return Identifier.of(IceAndFire.MOD_ID, "textures/entity/troll/troll_" + this.name + ".png");
    }

    public Identifier getStatueTexture() {
        return Identifier.of(IceAndFire.MOD_ID, "textures/entity/troll/troll_" + this.name + "_stone.png");
    }

    public Identifier getEyesTexture() {
        return Identifier.of(IceAndFire.MOD_ID, "textures/entity/troll/troll_" + this.name + "_eyes.png");
    }

    public boolean allowSpawn(RegistryEntry<Biome> biome) {
        return biome.isIn(this.spawnBiomes);
    }

    public enum BuiltinWeapon implements ITrollWeapon {
        AXE, COLUMN, COLUMN_FOREST, COLUMN_FROST, HAMMER, TRUNK, TRUNK_FROST;
        private final RegistrySupplier<Item> item;

        BuiltinWeapon() {
            this.item = IafItems.registerToolOrWeapon("troll_weapon_" + this.name().toLowerCase(Locale.ROOT), () -> new TrollWeaponItem(this));
            ITrollWeapon.addWeapons(this);
        }

        @Override
        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public Identifier getTexture() {
            return Identifier.of(IceAndFire.MOD_ID, "textures/entity/troll/weapon/weapon_" + this.name().toLowerCase(Locale.ROOT) + ".png");
        }

        @Override
        public Item getItem() {
            return this.item.get();
        }
    }

    public interface ITrollWeapon {
        @ApiStatus.Internal
        List<ITrollWeapon> WEAPONS = new ArrayList<>();
        @ApiStatus.Internal
        Map<String, ITrollWeapon> BY_NAME = new HashMap<>();

        static void addWeapons(ITrollWeapon... weapons) {
            for (ITrollWeapon weapon : weapons) {
                WEAPONS.add(weapon);
                BY_NAME.put(weapon.getName(), weapon);
            }
        }

        static List<ITrollWeapon> values() {
            return ImmutableList.copyOf(WEAPONS);
        }

        static ITrollWeapon getByName(String name) {
            return BY_NAME.getOrDefault(name, BuiltinWeapon.AXE);
        }

        String getName();

        Identifier getTexture();

        Item getItem();
    }
}
