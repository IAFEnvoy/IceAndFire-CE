package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.compat.delight.DelightFoodItem;
import com.iafenvoy.iceandfire.data.DragonArmorMaterial;
import com.iafenvoy.iceandfire.data.DragonArmorPart;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.*;
import com.iafenvoy.iceandfire.item.ability.impl.AbilityImpls;
import com.iafenvoy.iceandfire.item.armor.ItemBlindfold;
import com.iafenvoy.iceandfire.item.armor.ItemDragonSteelArmor;
import com.iafenvoy.iceandfire.item.armor.ItemModArmor;
import com.iafenvoy.iceandfire.item.food.ItemAmbrosia;
import com.iafenvoy.iceandfire.item.food.ItemCannoli;
import com.iafenvoy.iceandfire.item.food.ItemDragonFlesh;
import com.iafenvoy.iceandfire.item.food.ItemPixieDust;
import com.iafenvoy.iceandfire.item.tool.*;
import com.iafenvoy.iceandfire.registry.tag.BannerPatternTags;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Supplier;

import static net.minecraft.item.MiningToolItem.createAttributeModifiers;

@SuppressWarnings("unused")
public final class IafItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.ITEM);

    public static final RegistrySupplier<Item> BESTIARY = register("bestiary", ItemBestiary::new);
    public static final RegistrySupplier<Item> MANUSCRIPT = register("manuscript", ItemGeneric::new);
    public static final RegistrySupplier<Item> SAPPHIRE_GEM = register("sapphire_gem", ItemGeneric::new);
    public static final RegistrySupplier<Item> SILVER_INGOT = register("silver_ingot", ItemGeneric::new);
    public static final RegistrySupplier<Item> SILVER_NUGGET = register("silver_nugget", ItemGeneric::new);
    public static final RegistrySupplier<Item> RAW_SILVER = register("raw_silver", ItemGeneric::new);
    public static final RegistrySupplier<Item> COPPER_NUGGET = register("copper_nugget", ItemGeneric::new);
    public static final RegistrySupplier<Item> SILVER_HELMET = register("armor_silver_metal_helmet", () -> new ArmorItem(IafArmorMaterials.SILVER_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> SILVER_CHESTPLATE = register("armor_silver_metal_chestplate", () -> new ArmorItem(IafArmorMaterials.SILVER_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(240)));
    public static final RegistrySupplier<Item> SILVER_LEGGINGS = register("armor_silver_metal_leggings", () -> new ArmorItem(IafArmorMaterials.SILVER_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(225)));
    public static final RegistrySupplier<Item> SILVER_BOOTS = register("armor_silver_metal_boots", () -> new ArmorItem(IafArmorMaterials.SILVER_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(195)));
    public static final RegistrySupplier<Item> SILVER_SWORD = register(
        "silver_sword",
        () -> new ActivePostHitSwordItem(
            IafToolMaterials.SILVER_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.SILVER_TOOL_MATERIAL,
                    3.0F, -2.4F
                )
            ),
            AbilityImpls.UNDEAD_DAMAGE_BONUS
        )
    );
    public static final RegistrySupplier<Item> SILVER_SHOVEL = register(
        "silver_shovel",
        () -> new ActivePostHitShovelItem(
            IafToolMaterials.SILVER_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.SILVER_TOOL_MATERIAL,
                    1.5F, -3.0F
                )
            ),
            AbilityImpls.UNDEAD_DAMAGE_BONUS
        )
    );
    public static final RegistrySupplier<Item> SILVER_PICKAXE = register(
        "silver_pickaxe",
        () -> new ActivePostHitPickaxeItem(
            IafToolMaterials.SILVER_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.SILVER_TOOL_MATERIAL,
                    1.0F, -2.8F
                )
            ),
            AbilityImpls.UNDEAD_DAMAGE_BONUS
        )
    );
    public static final RegistrySupplier<Item> SILVER_AXE = register(
        "silver_axe",
        () -> new ActivePostHitAxeItem(
            IafToolMaterials.SILVER_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.SILVER_TOOL_MATERIAL,
                    6.0F, -3.0F
                )
            ),
            AbilityImpls.UNDEAD_DAMAGE_BONUS
        )
    );
    public static final RegistrySupplier<Item> SILVER_HOE = register(
        "silver_hoe",
        () -> new ActivePostHitHoeItem(
            IafToolMaterials.SILVER_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.SILVER_TOOL_MATERIAL,
                    0.0F, -3.0F
                )
            ),
            AbilityImpls.UNDEAD_DAMAGE_BONUS
        )
    );
    public static final RegistrySupplier<Item> COPPER_HELMET = register("armor_copper_metal_helmet", () -> new ArmorItem(IafArmorMaterials.COPPER_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(111)));
    public static final RegistrySupplier<Item> COPPER_CHESTPLATE = register("armor_copper_metal_chestplate", () -> new ArmorItem(IafArmorMaterials.COPPER_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(161)));
    public static final RegistrySupplier<Item> COPPER_LEGGINGS = register("armor_copper_metal_leggings", () -> new ArmorItem(IafArmorMaterials.COPPER_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(151)));
    public static final RegistrySupplier<Item> COPPER_BOOTS = register("armor_copper_metal_boots", () -> new ArmorItem(IafArmorMaterials.COPPER_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(131)));
    public static final RegistrySupplier<Item> COPPER_SWORD = register(
        "copper_sword",
        () -> new SwordItem(
            IafToolMaterials.COPPER_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.COPPER_TOOL_MATERIAL,
                    3.0F, -2.4F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> COPPER_SHOVEL = register(
        "copper_shovel",
        () -> new ShovelItem(
            IafToolMaterials.COPPER_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.COPPER_TOOL_MATERIAL,
                    1.5F, -2.4F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> COPPER_PICKAXE = register(
        "copper_pickaxe",
        () -> new PickaxeItem(
            IafToolMaterials.COPPER_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.COPPER_TOOL_MATERIAL,
                    1.0F, -2.8F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> COPPER_AXE = register(
        "copper_axe",
        () -> new AxeItem(
            IafToolMaterials.COPPER_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.COPPER_TOOL_MATERIAL,
                    6.0F, -3.0F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> COPPER_HOE = register(
        "copper_hoe",
        () -> new HoeItem(
            IafToolMaterials.COPPER_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.COPPER_TOOL_MATERIAL,
                    0.0F, -1.0F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> FIRE_STEW = register("fire_stew", ItemGeneric::new);
    public static final RegistrySupplier<Item> FROST_STEW = register("frost_stew", ItemGeneric::new);
    public static final RegistrySupplier<Item> LIGHTNING_STEW = register("lightning_stew", ItemGeneric::new);
    public static final RegistrySupplier<Item> DRAGONEGG_RED = register("dragonegg_red", () -> new ItemDragonEgg(DragonColor.RED));
    public static final RegistrySupplier<Item> DRAGONEGG_GREEN = register("dragonegg_green", () -> new ItemDragonEgg(DragonColor.GREEN));
    public static final RegistrySupplier<Item> DRAGONEGG_BRONZE = register("dragonegg_bronze", () -> new ItemDragonEgg(DragonColor.BRONZE));
    public static final RegistrySupplier<Item> DRAGONEGG_GRAY = register("dragonegg_gray", () -> new ItemDragonEgg(DragonColor.GRAY));
    public static final RegistrySupplier<Item> DRAGONEGG_BLUE = register("dragonegg_blue", () -> new ItemDragonEgg(DragonColor.BLUE));
    public static final RegistrySupplier<Item> DRAGONEGG_WHITE = register("dragonegg_white", () -> new ItemDragonEgg(DragonColor.WHITE));
    public static final RegistrySupplier<Item> DRAGONEGG_SAPPHIRE = register("dragonegg_sapphire", () -> new ItemDragonEgg(DragonColor.SAPPHIRE));
    public static final RegistrySupplier<Item> DRAGONEGG_SILVER = register("dragonegg_silver", () -> new ItemDragonEgg(DragonColor.SILVER));
    public static final RegistrySupplier<Item> DRAGONEGG_ELECTRIC = register("dragonegg_electric", () -> new ItemDragonEgg(DragonColor.ELECTRIC));
    public static final RegistrySupplier<Item> DRAGONEGG_AMETHYST = register("dragonegg_amethyst", () -> new ItemDragonEgg(DragonColor.AMETHYST));
    public static final RegistrySupplier<Item> DRAGONEGG_COPPER = register("dragonegg_copper", () -> new ItemDragonEgg(DragonColor.COPPER));
    public static final RegistrySupplier<Item> DRAGONEGG_BLACK = register("dragonegg_black", () -> new ItemDragonEgg(DragonColor.BLACK));
    public static final RegistrySupplier<Item> DRAGONSCALES_RED = register("dragonscales_red", () -> new ItemDragonScales(DragonColor.RED));
    public static final RegistrySupplier<Item> DRAGONSCALES_GREEN = register("dragonscales_green", () -> new ItemDragonScales(DragonColor.GREEN));
    public static final RegistrySupplier<Item> DRAGONSCALES_BRONZE = register("dragonscales_bronze", () -> new ItemDragonScales(DragonColor.BRONZE));
    public static final RegistrySupplier<Item> DRAGONSCALES_GRAY = register("dragonscales_gray", () -> new ItemDragonScales(DragonColor.GRAY));
    public static final RegistrySupplier<Item> DRAGONSCALES_BLUE = register("dragonscales_blue", () -> new ItemDragonScales(DragonColor.BLUE));
    public static final RegistrySupplier<Item> DRAGONSCALES_WHITE = register("dragonscales_white", () -> new ItemDragonScales(DragonColor.WHITE));
    public static final RegistrySupplier<Item> DRAGONSCALES_SAPPHIRE = register("dragonscales_sapphire", () -> new ItemDragonScales(DragonColor.SAPPHIRE));
    public static final RegistrySupplier<Item> DRAGONSCALES_SILVER = register("dragonscales_silver", () -> new ItemDragonScales(DragonColor.SILVER));
    public static final RegistrySupplier<Item> DRAGONSCALES_ELECTRIC = register("dragonscales_electric", () -> new ItemDragonScales(DragonColor.ELECTRIC));
    public static final RegistrySupplier<Item> DRAGONSCALES_AMETHYST = register("dragonscales_amethyst", () -> new ItemDragonScales(DragonColor.AMETHYST));
    public static final RegistrySupplier<Item> DRAGONSCALES_COPPER = register("dragonscales_copper", () -> new ItemDragonScales(DragonColor.COPPER));
    public static final RegistrySupplier<Item> DRAGONSCALES_BLACK = register("dragonscales_black", () -> new ItemDragonScales(DragonColor.BLACK));
    public static final RegistrySupplier<Item> DRAGON_BONE = register("dragonbone", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> WITHERBONE = register("witherbone", ItemGeneric::new);
    public static final RegistrySupplier<Item> FISHING_SPEAR = register("fishing_spear", () -> new Item(new Item.Settings().maxDamage(64)));
    public static final RegistrySupplier<Item> WITHER_SHARD = register("wither_shard", ItemGeneric::new);
    public static final RegistrySupplier<Item> DRAGONBONE_SWORD = register(
        "dragonbone_sword",
        () -> new SwordItem(
            IafToolMaterials.DRAGONBONE_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONBONE_TOOL_MATERIAL,
                    3.0F, -2.4F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> DRAGONBONE_SHOVEL = register(
        "dragonbone_shovel",
        () -> new ShovelItem(
            IafToolMaterials.DRAGONBONE_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONBONE_TOOL_MATERIAL,
                    1.5F, -2.8F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> DRAGONBONE_PICKAXE = register(
        "dragonbone_pickaxe",
        () -> new PickaxeItem(
            IafToolMaterials.DRAGONBONE_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONBONE_TOOL_MATERIAL,
                    1.0F, -2.8F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> DRAGONBONE_AXE = register(
        "dragonbone_axe",
        () -> new AxeItem(
            IafToolMaterials.DRAGONBONE_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONBONE_TOOL_MATERIAL,
                    5.0F, -3.0F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> DRAGONBONE_HOE = register(
        "dragonbone_hoe",
        () -> new HoeItem(
            IafToolMaterials.DRAGONBONE_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONBONE_TOOL_MATERIAL,
                    -4.0F, 0.0F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> DRAGONBONE_SWORD_FIRE = register(
        "dragonbone_sword_fire",
        () -> new ActivePostHitSwordItem(
            IafToolMaterials.FIRE_DRAGONBONE_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.FIRE_DRAGONBONE_TOOL_MATERIAL,
                    3.0F, -2.4F
                )
            ),
            AbilityImpls.FIRE_DRAGON_BLOOD_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONBONE_SWORD_ICE = register(
        "dragonbone_sword_ice",
        () -> new ActivePostHitSwordItem(
            IafToolMaterials.ICE_DRAGONBONE_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.ICE_DRAGONBONE_TOOL_MATERIAL,
                    3.0F, -2.4F
                )
            ),
            AbilityImpls.ICE_DRAGON_BLOOD_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONBONE_SWORD_LIGHTNING = register(
        "dragonbone_sword_lightning",
        () -> new ActivePostHitSwordItem(
            IafToolMaterials.LIGHTNING_DRAGONBONE_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.LIGHTNING_DRAGONBONE_TOOL_MATERIAL,
                    3.0F, -2.4F
                )
            ),
            AbilityImpls.LIGHTNING_DRAGON_BLOOD_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONBONE_ARROW = register("dragonbone_arrow", ItemDragonArrow::new);
    public static final RegistrySupplier<Item> DRAGON_BOW = register("dragonbone_bow", ItemDragonBow::new);
    public static final RegistrySupplier<Item> DRAGON_SKULL_FIRE = register("dragon_skull_fire", () -> new ItemDragonSkull(DragonType.FIRE));
    public static final RegistrySupplier<Item> DRAGON_SKULL_ICE = register("dragon_skull_ice", () -> new ItemDragonSkull(DragonType.ICE));
    public static final RegistrySupplier<Item> DRAGON_SKULL_LIGHTNING = register("dragon_skull_lightning", () -> new ItemDragonSkull(DragonType.LIGHTNING));
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_IRON_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.IRON);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_IRON_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.IRON);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_IRON_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.IRON);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_IRON_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.IRON);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_COPPER_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.COPPER);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_COPPER_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.COPPER);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_COPPER_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.COPPER);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_COPPER_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.COPPER);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_SILVER_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.SILVER);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_SILVER_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.SILVER);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_SILVER_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.SILVER);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_SILVER_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.SILVER);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_GOLD_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.GOLD);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_GOLD_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.GOLD);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_GOLD_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.GOLD);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_GOLD_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.GOLD);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DIAMOND_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DIAMOND);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DIAMOND_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DIAMOND);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DIAMOND_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DIAMOND);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DIAMOND_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DIAMOND);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_NETHERITE_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.NETHERITE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_NETHERITE_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.NETHERITE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_NETHERITE_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.NETHERITE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_NETHERITE_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.NETHERITE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_FIRE_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_FIRE_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_FIRE_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_FIRE_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_ICE_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_ICE_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_ICE_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_ICE_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final RegistrySupplier<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final RegistrySupplier<Item> DRAGON_MEAL = register("dragon_meal", ItemGeneric::new);
    public static final RegistrySupplier<Item> SICKLY_DRAGON_MEAL = register("sickly_dragon_meal", () -> new ItemGeneric(1));
    public static final RegistrySupplier<Item> CREATIVE_DRAGON_MEAL = register("creative_dragon_meal", () -> new ItemGeneric(2));
    public static final RegistrySupplier<Item> FIRE_DRAGON_FLESH = register(ItemDragonFlesh.getNameForType(0), () -> new ItemDragonFlesh(0));
    public static final RegistrySupplier<Item> ICE_DRAGON_FLESH = register(ItemDragonFlesh.getNameForType(1), () -> new ItemDragonFlesh(1));
    public static final RegistrySupplier<Item> LIGHTNING_DRAGON_FLESH = register(ItemDragonFlesh.getNameForType(2), () -> new ItemDragonFlesh(2));
    public static final RegistrySupplier<Item> FIRE_DRAGON_HEART = register("fire_dragon_heart", ItemGeneric::new);
    public static final RegistrySupplier<Item> ICE_DRAGON_HEART = register("ice_dragon_heart", ItemGeneric::new);
    public static final RegistrySupplier<Item> LIGHTNING_DRAGON_HEART = register("lightning_dragon_heart", ItemGeneric::new);
    public static final RegistrySupplier<Item> FIRE_DRAGON_BLOOD = register("fire_dragon_blood", ItemGeneric::new);
    public static final RegistrySupplier<Item> ICE_DRAGON_BLOOD = register("ice_dragon_blood", ItemGeneric::new);
    public static final RegistrySupplier<Item> LIGHTNING_DRAGON_BLOOD = register("lightning_dragon_blood", ItemGeneric::new);
    public static final RegistrySupplier<Item> DRAGON_STAFF = register("dragon_stick", () -> new Item(new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> DRAGON_HORN = register("dragon_horn", ItemDragonHorn::new);
    public static final RegistrySupplier<Item> DRAGON_FLUTE = register("dragon_flute", ItemDragonFlute::new);
    public static final RegistrySupplier<Item> SUMMONING_CRYSTAL_FIRE = register("summoning_crystal_fire", ItemSummoningCrystal::new);
    public static final RegistrySupplier<Item> SUMMONING_CRYSTAL_ICE = register("summoning_crystal_ice", ItemSummoningCrystal::new);
    public static final RegistrySupplier<Item> SUMMONING_CRYSTAL_LIGHTNING = register("summoning_crystal_lightning", ItemSummoningCrystal::new);
    public static final RegistrySupplier<Item> HIPPOGRYPH_EGG = register("hippogryph_egg", ItemHippogryphEgg::new);
    public static final RegistrySupplier<Item> IRON_HIPPOGRYPH_ARMOR = register("iron_hippogryph_armor", () -> new ItemGeneric(0, 1));
    public static final RegistrySupplier<Item> GOLD_HIPPOGRYPH_ARMOR = register("gold_hippogryph_armor", () -> new ItemGeneric(0, 1));
    public static final RegistrySupplier<Item> DIAMOND_HIPPOGRYPH_ARMOR = register("diamond_hippogryph_armor", () -> new ItemGeneric(0, 1));
    public static final RegistrySupplier<Item> NETHERITE_HIPPOGRYPH_ARMOR = register("netherite_hippogryph_armor", () -> new ItemGeneric(0, 1, true));
    public static final RegistrySupplier<Item> HIPPOGRYPH_TALON = register("hippogryph_talon", () -> new ItemGeneric(1));
    public static final RegistrySupplier<Item> HIPPOGRYPH_SWORD = register("hippogryph_sword", ItemHippogryphSword::new);
    public static final RegistrySupplier<Item> GORGON_HEAD = register("gorgon_head", ItemGorgonHead::new);
    public static final RegistrySupplier<Item> STONE_STATUE = register("stone_statue", ItemStoneStatue::new);
    public static final RegistrySupplier<Item> BLINDFOLD = register("blindfold", ItemBlindfold::new);
    public static final RegistrySupplier<Item> PIXIE_DUST = register("pixie_dust", ItemPixieDust::new);
    public static final RegistrySupplier<Item> PIXIE_WINGS = register("pixie_wings", () -> new ItemGeneric(1));
    public static final RegistrySupplier<Item> PIXIE_WAND = register("pixie_wand", ItemPixieWand::new);
    public static final RegistrySupplier<Item> AMBROSIA = register("ambrosia", ItemAmbrosia::new);
    public static final RegistrySupplier<Item> CYCLOPS_EYE = register("cyclops_eye", ItemCyclopsEye::new);
    public static final RegistrySupplier<Item> SHEEP_HELMET = register("sheep_helmet", () -> new ItemModArmor(IafArmorMaterials.SHEEP_ARMOR_MATERIAL, ArmorItem.Type.HELMET, 55));
    public static final RegistrySupplier<Item> SHEEP_CHESTPLATE = register("sheep_chestplate", () -> new ItemModArmor(IafArmorMaterials.SHEEP_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, 80));
    public static final RegistrySupplier<Item> SHEEP_LEGGINGS = register("sheep_leggings", () -> new ItemModArmor(IafArmorMaterials.SHEEP_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, 75));
    public static final RegistrySupplier<Item> SHEEP_BOOTS = register("sheep_boots", () -> new ItemModArmor(IafArmorMaterials.SHEEP_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, 65));
    public static final RegistrySupplier<Item> SHINY_SCALES = register("shiny_scales", ItemGeneric::new);
    public static final RegistrySupplier<Item> SIREN_TEAR = register("siren_tear", () -> new ItemGeneric(1));
    public static final RegistrySupplier<Item> SIREN_FLUTE = register("siren_flute", ItemSirenFlute::new);
    public static final RegistrySupplier<Item> HIPPOCAMPUS_FIN = register("hippocampus_fin", () -> new ItemGeneric(1));
    public static final RegistrySupplier<Item> HIPPOCAMPUS_SLAPPER = register("hippocampus_slapper", ItemHippocampusSlapper::new);
    public static final RegistrySupplier<Item> EARPLUGS = register("earplugs", () -> new ItemModArmor(IafArmorMaterials.EARPLUGS_ARMOR_MATERIAL, ArmorItem.Type.HELMET, 55));
    public static final RegistrySupplier<Item> DEATH_WORM_CHITIN_YELLOW = register("deathworm_chitin_yellow", ItemGeneric::new);
    public static final RegistrySupplier<Item> DEATH_WORM_CHITIN_WHITE = register("deathworm_chitin_white", ItemGeneric::new);
    public static final RegistrySupplier<Item> DEATH_WORM_CHITIN_RED = register("deathworm_chitin_red", ItemGeneric::new);
    public static final RegistrySupplier<Item> DEATHWORM_YELLOW_HELMET = register("deathworm_yellow_helmet", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_0_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> DEATHWORM_YELLOW_CHESTPLATE = register("deathworm_yellow_chestplate", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_0_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(240)));
    public static final RegistrySupplier<Item> DEATHWORM_YELLOW_LEGGINGS = register("deathworm_yellow_leggings", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_0_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(225)));
    public static final RegistrySupplier<Item> DEATHWORM_YELLOW_BOOTS = register("deathworm_yellow_boots", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_0_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(195)));
    public static final RegistrySupplier<Item> DEATHWORM_WHITE_HELMET = register("deathworm_white_helmet", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_1_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> DEATHWORM_WHITE_CHESTPLATE = register("deathworm_white_chestplate", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_1_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(240)));
    public static final RegistrySupplier<Item> DEATHWORM_WHITE_LEGGINGS = register("deathworm_white_leggings", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_1_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(225)));
    public static final RegistrySupplier<Item> DEATHWORM_WHITE_BOOTS = register("deathworm_white_boots", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_1_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> DEATHWORM_RED_HELMET = register("deathworm_red_helmet", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_2_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> DEATHWORM_RED_CHESTPLATE = register("deathworm_red_chestplate", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_2_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(240)));
    public static final RegistrySupplier<Item> DEATHWORM_RED_LEGGINGS = register("deathworm_red_leggings", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_2_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(225)));
    public static final RegistrySupplier<Item> DEATHWORM_RED_BOOTS = register("deathworm_red_boots", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_2_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> DEATHWORM_EGG = register("deathworm_egg", () -> new ItemDeathwormEgg(false));
    public static final RegistrySupplier<Item> DEATHWORM_EGG_GIGANTIC = register("deathworm_egg_giant", () -> new ItemDeathwormEgg(true));
    public static final RegistrySupplier<Item> DEATHWORM_TOUNGE = register("deathworm_tounge", () -> new ItemGeneric(1));
    public static final RegistrySupplier<Item> DEATHWORM_GAUNTLET_YELLOW = register("deathworm_gauntlet_yellow", ItemDeathwormGauntlet::new);
    public static final RegistrySupplier<Item> DEATHWORM_GAUNTLET_WHITE = register("deathworm_gauntlet_white", ItemDeathwormGauntlet::new);
    public static final RegistrySupplier<Item> DEATHWORM_GAUNTLET_RED = register("deathworm_gauntlet_red", ItemDeathwormGauntlet::new);
    public static final RegistrySupplier<Item> ROTTEN_EGG = register("rotten_egg", ItemRottenEgg::new);
    public static final RegistrySupplier<Item> COCKATRICE_EYE = register("cockatrice_eye", () -> new ItemGeneric(1));
    public static final RegistrySupplier<Item> ITEM_COCKATRICE_SCEPTER = register("cockatrice_scepter", ItemCockatriceScepter::new);
    public static final RegistrySupplier<Item> STYMPHALIAN_BIRD_FEATHER = register("stymphalian_bird_feather", ItemGeneric::new);
    public static final RegistrySupplier<Item> STYMPHALIAN_ARROW = register("stymphalian_arrow", ItemStymphalianArrow::new);
    public static final RegistrySupplier<Item> STYMPHALIAN_FEATHER_BUNDLE = register("stymphalian_feather_bundle", ItemStymphalianFeatherBundle::new);
    public static final RegistrySupplier<Item> STYMPHALIAN_DAGGER = register("stymphalian_bird_dagger", ItemStymphalianDagger::new);
    public static final RegistrySupplier<Item> TROLL_TUSK = register("troll_tusk", ItemGeneric::new);
    public static final RegistrySupplier<Item> AMPHITHERE_FEATHER = register("amphithere_feather", ItemGeneric::new);
    public static final RegistrySupplier<Item> AMPHITHERE_ARROW = register("amphithere_arrow", ItemAmphithereArrow::new);
    public static final RegistrySupplier<Item> AMPHITHERE_MACUAHUITL = register("amphithere_macuahuitl", ItemAmphithereMacuahuitl::new);
    public static final RegistrySupplier<Item> SERPENT_FANG = register("sea_serpent_fang", ItemGeneric::new);
    public static final RegistrySupplier<Item> SEA_SERPENT_ARROW = register("sea_serpent_arrow", ItemSeaSerpentArrow::new);
    public static final RegistrySupplier<Item> TIDE_TRIDENT_INVENTORY = register("tide_trident_inventory", () -> new ItemGeneric(0, true), false);
    public static final RegistrySupplier<Item> TIDE_TRIDENT = register("tide_trident", ItemTideTrident::new);
    public static final RegistrySupplier<Item> CHAIN = register("chain", () -> new ItemChain(false));
    public static final RegistrySupplier<Item> CHAIN_STICKY = register("chain_sticky", () -> new ItemChain(true));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_INGOT = register("dragonsteel_fire_ingot", ItemGeneric::new);
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_SWORD = register(
        "dragonsteel_fire_sword",
        () -> new ActivePostHitSwordItem(
            IafToolMaterials.DRAGONSTEEL_FIRE,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_FIRE,
                    3.0F, -2.4F
                )
            ),
            AbilityImpls.DRAGONSTEEL_FIRE_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_PICKAXE = register(
        "dragonsteel_fire_pickaxe",
        () -> new ActivePostHitPickaxeItem(
            IafToolMaterials.DRAGONSTEEL_FIRE,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_FIRE,
                    1.0F, -2.8F
                )
            ),
            AbilityImpls.DRAGONSTEEL_FIRE_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_AXE = register(
        "dragonsteel_fire_axe",
        () -> new ActivePostHitAxeItem(
            IafToolMaterials.DRAGONSTEEL_FIRE,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_FIRE,
                    5.0F, -3.0F
                )
            ),
            AbilityImpls.DRAGONSTEEL_FIRE_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_SHOVEL = register(
        "dragonsteel_fire_shovel",
        () -> new ActivePostHitShovelItem(
            IafToolMaterials.DRAGONSTEEL_FIRE,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_FIRE,
                    1.5F, -3.0F
                )
            ),
            AbilityImpls.DRAGONSTEEL_FIRE_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_HOE = register(
        "dragonsteel_fire_hoe",
        () -> new ActivePostHitHoeItem(
            IafToolMaterials.DRAGONSTEEL_FIRE,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_FIRE,
                    -4.0F, 0.0F
                )
            ),
            AbilityImpls.DRAGONSTEEL_FIRE_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_HELMET = register("dragonsteel_fire_helmet", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_FIRE_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_CHESTPLATE = register("dragonsteel_fire_chestplate", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_FIRE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_LEGGINGS = register("dragonsteel_fire_leggings", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_FIRE_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_BOOTS = register("dragonsteel_fire_boots", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_FIRE_ARMOR_MATERIAL, ArmorItem.Type.BOOTS));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_INGOT = register("dragonsteel_ice_ingot", ItemGeneric::new);
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_SWORD = register(
        "dragonsteel_ice_sword",
        () -> new ActivePostHitSwordItem(
            IafToolMaterials.DRAGONSTEEL_ICE,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_ICE,
                    3.0F, -2.4F
                )
            ),
            AbilityImpls.DRAGONSTEEL_ICE_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_PICKAXE = register(
        "dragonsteel_ice_pickaxe",
        () -> new ActivePostHitPickaxeItem(
            IafToolMaterials.DRAGONSTEEL_ICE,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_ICE,
                    1.0F, -2.8F
                )
            ),
            AbilityImpls.DRAGONSTEEL_ICE_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_AXE = register(
        "dragonsteel_ice_axe",
        () -> new ActivePostHitAxeItem(
            IafToolMaterials.DRAGONSTEEL_ICE,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_ICE,
                    5.0F, -3.0F
                )
            ),
            AbilityImpls.DRAGONSTEEL_ICE_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_SHOVEL = register(
        "dragonsteel_ice_shovel",
        () -> new ActivePostHitShovelItem(
            IafToolMaterials.DRAGONSTEEL_ICE,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_ICE,
                    1.5F, -3.0F
                )
            ),
            AbilityImpls.DRAGONSTEEL_ICE_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_HOE = register(
        "dragonsteel_ice_hoe",
        () -> new ActivePostHitHoeItem(
            IafToolMaterials.DRAGONSTEEL_ICE,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_ICE,
                    -4.0F, 0.0F
                )
            ),
            AbilityImpls.DRAGONSTEEL_ICE_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_HELMET = register("dragonsteel_ice_helmet", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_ICE_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_CHESTPLATE = register("dragonsteel_ice_chestplate", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_ICE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_LEGGINGS = register("dragonsteel_ice_leggings", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_ICE_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_BOOTS = register("dragonsteel_ice_boots", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_ICE_ARMOR_MATERIAL, ArmorItem.Type.BOOTS));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_INGOT = register("dragonsteel_lightning_ingot", ItemGeneric::new);
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_SWORD = register(
        "dragonsteel_lightning_sword",
        () -> new ActivePostHitSwordItem(
            IafToolMaterials.DRAGONSTEEL_LIGHTNING,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_LIGHTNING,
                    3.0F, -2.4F
                )
            ),
            AbilityImpls.DRAGONSTEEL_LIGHTNING_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_PICKAXE = register(
        "dragonsteel_lightning_pickaxe",
        () -> new ActivePostHitPickaxeItem(
            IafToolMaterials.DRAGONSTEEL_LIGHTNING,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_LIGHTNING,
                    1.0F, -2.8F
                )
            ),
            AbilityImpls.DRAGONSTEEL_LIGHTNING_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_AXE = register(
        "dragonsteel_lightning_axe",
        () -> new ActivePostHitAxeItem(
            IafToolMaterials.DRAGONSTEEL_LIGHTNING,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_LIGHTNING,
                    5.0F, -3.0F
                )
            ),
            AbilityImpls.DRAGONSTEEL_LIGHTNING_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_SHOVEL = register(
        "dragonsteel_lightning_shovel",
        () -> new ActivePostHitShovelItem(
            IafToolMaterials.DRAGONSTEEL_LIGHTNING,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_LIGHTNING,
                    1.5F, -3.0F
                )
            ),
            AbilityImpls.DRAGONSTEEL_LIGHTNING_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_HOE = register(
        "dragonsteel_lightning_hoe",
        () -> new ActivePostHitHoeItem(
            IafToolMaterials.DRAGONSTEEL_LIGHTNING,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DRAGONSTEEL_LIGHTNING,
                    -4.0F, 0.0F
                )
            ),
            AbilityImpls.DRAGONSTEEL_LIGHTNING_TOOL
        )
    );
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_HELMET = register("dragonsteel_lightning_helmet", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, ArmorItem.Type.HELMET));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_CHESTPLATE = register("dragonsteel_lightning_chestplate", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_LEGGINGS = register("dragonsteel_lightning_leggings", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_BOOTS = register("dragonsteel_lightning_boots", () -> new ItemDragonSteelArmor(IafArmorMaterials.DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, ArmorItem.Type.BOOTS));
    public static final RegistrySupplier<Item> WEEZER_BLUE_ALBUM = register("weezer_blue_album", () -> new ItemGeneric(1, true));
    public static final RegistrySupplier<Item> DRAGON_DEBUG_STICK = register("dragon_debug_stick", () -> new ItemGeneric(1, true), false);
    public static final RegistrySupplier<Item> DREAD_SWORD = register(
        "dread_sword",
        () -> new SwordItem(
            IafToolMaterials.DREAD_SWORD_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DREAD_SWORD_TOOL_MATERIAL,
                    3.0F, -2.4F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> DREAD_KNIGHT_SWORD = register(
        "dread_knight_sword",
        () -> new SwordItem(
            IafToolMaterials.DREAD_KNIGHT_TOOL_MATERIAL,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DREAD_KNIGHT_TOOL_MATERIAL,
                    3.0F, -2.4F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> LICH_STAFF = register("lich_staff", ItemLichStaff::new);
    public static final RegistrySupplier<Item> DREAD_QUEEN_SWORD = register(
        "dread_queen_sword",
        () -> new SwordItem(
            IafToolMaterials.DREAD_QUEEN,
            new Item.Settings().component(
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(
                    IafToolMaterials.DREAD_QUEEN,
                    3.0F, -2.4F
                )
            )
        )
    );
    public static final RegistrySupplier<Item> DREAD_QUEEN_STAFF = register("dread_queen_staff", ItemDreadQueenStaff::new);
    public static final RegistrySupplier<Item> DREAD_SHARD = register("dread_shard", () -> new ItemGeneric(0));
    public static final RegistrySupplier<Item> DREAD_KEY = register("dread_key", () -> new ItemGeneric(0));
    public static final RegistrySupplier<Item> HYDRA_FANG = register("hydra_fang", () -> new ItemGeneric(0));
    public static final RegistrySupplier<Item> HYDRA_HEART = register("hydra_heart", ItemHydraHeart::new);
    public static final RegistrySupplier<Item> HYDRA_ARROW = register("hydra_arrow", ItemHydraArrow::new);
    public static final RegistrySupplier<Item> CANNOLI = register("cannoli", ItemCannoli::new, false);
    public static final RegistrySupplier<Item> ECTOPLASM = register("ectoplasm", ItemGeneric::new);
    public static final RegistrySupplier<Item> GHOST_INGOT = register("ghost_ingot", () -> new ItemGeneric(1));
    public static final RegistrySupplier<Item> GHOST_SWORD = register("ghost_sword", ItemGhostSword::new);
    public static final RegistrySupplier<Item> DRAGON_SEEKER = register("dragon_seeker", () -> new ItemDragonSeeker(ItemDragonSeeker.SeekerType.NORMAL));
    public static final RegistrySupplier<Item> EPIC_DRAGON_SEEKER = register("epic_dragon_seeker", () -> new ItemDragonSeeker(ItemDragonSeeker.SeekerType.EPIC));
    public static final RegistrySupplier<Item> LEGENDARY_DRAGON_SEEKER = register("legendary_dragon_seeker", () -> new ItemDragonSeeker(ItemDragonSeeker.SeekerType.LEGENDARY));
    public static final RegistrySupplier<Item> GODLY_DRAGON_SEEKER = register("godly_dragon_seeker", () -> new ItemDragonSeeker(ItemDragonSeeker.SeekerType.GODLY));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_FIRE = register("banner_pattern_fire", () -> new BannerPatternItem(BannerPatternTags.FIRE_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_ICE = register("banner_pattern_ice", () -> new BannerPatternItem(BannerPatternTags.ICE_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_LIGHTNING = register("banner_pattern_lightning", () -> new BannerPatternItem(BannerPatternTags.LIGHTNING_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_FIRE_HEAD = register("banner_pattern_fire_head", () -> new BannerPatternItem(BannerPatternTags.FIRE_HEAD_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_ICE_HEAD = register("banner_pattern_ice_head", () -> new BannerPatternItem(BannerPatternTags.ICE_HEAD_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_LIGHTNING_HEAD = register("banner_pattern_lightning_head", () -> new BannerPatternItem(BannerPatternTags.LIGHTNING_HEAD_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_AMPHITHERE = register("banner_pattern_amphithere", () -> new BannerPatternItem(BannerPatternTags.AMPHITHERE_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_BIRD = register("banner_pattern_bird", () -> new BannerPatternItem(BannerPatternTags.BIRD_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_EYE = register("banner_pattern_eye", () -> new BannerPatternItem(BannerPatternTags.EYE_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_FAE = register("banner_pattern_fae", () -> new BannerPatternItem(BannerPatternTags.FAE_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_FEATHER = register("banner_pattern_feather", () -> new BannerPatternItem(BannerPatternTags.FEATHER_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_GORGON = register("banner_pattern_gorgon", () -> new BannerPatternItem(BannerPatternTags.GORGON_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_HIPPOCAMPUS = register("banner_pattern_hippocampus", () -> new BannerPatternItem(BannerPatternTags.HIPPOCAMPUS_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_HIPPOGRYPH_HEAD = register("banner_pattern_hippogryph_head", () -> new BannerPatternItem(BannerPatternTags.HIPPOGRYPH_HEAD_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_MERMAID = register("banner_pattern_mermaid", () -> new BannerPatternItem(BannerPatternTags.MERMAID_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_SEA_SERPENT = register("banner_pattern_sea_serpent", () -> new BannerPatternItem(BannerPatternTags.SEA_SERPENT_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_TROLL = register("banner_pattern_troll", () -> new BannerPatternItem(BannerPatternTags.TROLL_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_WEEZER = register("banner_pattern_weezer", () -> new BannerPatternItem(BannerPatternTags.WEEZER_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_DREAD = register("banner_pattern_dread", () -> new BannerPatternItem(BannerPatternTags.DREAD_BANNER_PATTERN, new Item.Settings().maxCount(1)));

    public static final RegistrySupplier<DelightFoodItem> COOKED_RICE_WITH_FIRE_DRAGON_MEAT = register("cooked_rice_with_fire_dragon_meat", () -> new DelightFoodItem(new Item.Settings().maxCount(1).food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.6f).statusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 20 * 5), 1).statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 60 * 2), 1).build())));
    public static final RegistrySupplier<DelightFoodItem> COOKED_RICE_WITH_ICE_DRAGON_MEAT = register("cooked_rice_with_ice_dragon_meat", () -> new DelightFoodItem(new Item.Settings().maxCount(1).food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.6f).statusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 20 * 5), 1).statusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 60 * 2, 2), 1).build())));
    public static final RegistrySupplier<DelightFoodItem> COOKED_RICE_WITH_LIGHTNING_DRAGON_MEAT = register("cooked_rice_with_lightning_dragon_meat", () -> new DelightFoodItem(new Item.Settings().maxCount(1).food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.6f).statusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 20 * 5), 1).statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 60 * 2, 2), 1).build())));
    public static final RegistrySupplier<DelightFoodItem> GHOST_CREAM = register("ghost_cream", () -> new DelightFoodItem(new Item.Settings().maxCount(1).food(new FoodComponent.Builder().snack().nutrition(4).saturationModifier(0.6f).statusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20 * 20), 1).build())));
    public static final RegistrySupplier<DelightFoodItem> PIXIE_DUST_MILKY_TEA = register("pixie_dust_milky_tea", () -> new DelightFoodItem(new Item.Settings().maxCount(1).food(new FoodComponent.Builder().snack().nutrition(4).saturationModifier(0.6f).statusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 20 * 60 * 2), 1).build())));

    static {
        //spawn Eggs
        register("spawn_egg_fire_dragon", () -> new ArchitecturySpawnEggItem(IafEntities.FIRE_DRAGON, 0X340000, 0XA52929, new Item.Settings()));
        register("spawn_egg_ice_dragon", () -> new ArchitecturySpawnEggItem(IafEntities.ICE_DRAGON, 0XB5DDFB, 0X7EBAF0, new Item.Settings()));
        register("spawn_egg_lightning_dragon", () -> new ArchitecturySpawnEggItem(IafEntities.LIGHTNING_DRAGON, 0X422367, 0X725691, new Item.Settings()));
        register("spawn_egg_hippogryph", () -> new ArchitecturySpawnEggItem(IafEntities.HIPPOGRYPH, 0XD8D8D8, 0XD1B55D, new Item.Settings()));
        register("spawn_egg_gorgon", () -> new ArchitecturySpawnEggItem(IafEntities.GORGON, 0XD0D99F, 0X684530, new Item.Settings()));
        register("spawn_egg_pixie", () -> new ArchitecturySpawnEggItem(IafEntities.PIXIE, 0XFF7F89, 0XE2CCE2, new Item.Settings()));
        register("spawn_egg_cyclops", () -> new ArchitecturySpawnEggItem(IafEntities.CYCLOPS, 0XB0826E, 0X3A1F0F, new Item.Settings()));
        register("spawn_egg_siren", () -> new ArchitecturySpawnEggItem(IafEntities.SIREN, 0X8EE6CA, 0XF2DFC8, new Item.Settings()));
        register("spawn_egg_hippocampus", () -> new ArchitecturySpawnEggItem(IafEntities.HIPPOCAMPUS, 0X4491C7, 0X4FC56B, new Item.Settings()));
        register("spawn_egg_death_worm", () -> new ArchitecturySpawnEggItem(IafEntities.DEATH_WORM, 0XD1CDA3, 0X423A3A, new Item.Settings()));
        register("spawn_egg_cockatrice", () -> new ArchitecturySpawnEggItem(IafEntities.COCKATRICE, 0X8F5005, 0X4F5A23, new Item.Settings()));
        register("spawn_egg_stymphalian_bird", () -> new ArchitecturySpawnEggItem(IafEntities.STYMPHALIAN_BIRD, 0X744F37, 0X9E6C4B, new Item.Settings()));
        register("spawn_egg_troll", () -> new ArchitecturySpawnEggItem(IafEntities.TROLL, 0X3D413D, 0X58433A, new Item.Settings()));
        register("spawn_egg_amphithere", () -> new ArchitecturySpawnEggItem(IafEntities.AMPHITHERE, 0X597535, 0X00AA98, new Item.Settings()));
        register("spawn_egg_sea_serpent", () -> new ArchitecturySpawnEggItem(IafEntities.SEA_SERPENT, 0X008299, 0XC5E6E7, new Item.Settings()));
        register("spawn_egg_dread_thrall", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_THRALL, 0XE0E6E6, 0X00FFFF, new Item.Settings()));
        register("spawn_egg_dread_ghoul", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_GHOUL, 0XE0E6E6, 0X7B838A, new Item.Settings()));
        register("spawn_egg_dread_beast", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_BEAST, 0XE0E6E6, 0X38373C, new Item.Settings()));
        register("spawn_egg_dread_scuttler", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_SCUTTLER, 0XE0E6E6, 0X4D5667, new Item.Settings()));
        register("spawn_egg_lich", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_LICH, 0XE0E6E6, 0X274860, new Item.Settings()));
        register("spawn_egg_dread_knight", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_KNIGHT, 0XE0E6E6, 0X4A6C6E, new Item.Settings()));
        register("spawn_egg_dread_horse", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_HORSE, 0XE0E6E6, 0XACACAC, new Item.Settings()));
        register("spawn_egg_hydra", () -> new ArchitecturySpawnEggItem(IafEntities.HYDRA, 0X8B8B78, 0X2E372B, new Item.Settings()));
        register("spawn_egg_ghost", () -> new ArchitecturySpawnEggItem(IafEntities.GHOST, 0XB9EDB8, 0X73B276, new Item.Settings()));
    }

    public static RegistrySupplier<ItemDragonArmor> buildDragonArmor(DragonArmorPart type, DragonArmorMaterial material) {
        return register(String.format("dragonarmor_%s_%s", material.getId(), type.getId()), () -> new ItemDragonArmor(material, type));
    }

    public static <T extends Item> RegistrySupplier<T> register(String name, Supplier<T> item) {
        return register(name, item, true);
    }

    public static <T extends Item> RegistrySupplier<T> register(String name, Supplier<T> item, boolean putInTab) {
        RegistrySupplier<T> r = REGISTRY.register(name, item);
        if (putInTab) IafItemGroups.TAB_ITEMS_LIST.add(r::get);
        return r;
    }
}
