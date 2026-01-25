package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.compat.delight.DelightFoodItem;
import com.iafenvoy.iceandfire.data.DragonArmorMaterial;
import com.iafenvoy.iceandfire.data.DragonArmorPart;
import com.iafenvoy.iceandfire.item.*;
import com.iafenvoy.iceandfire.item.ability.BuiltinAbilities;
import com.iafenvoy.iceandfire.item.armor.BlindfoldItem;
import com.iafenvoy.iceandfire.item.armor.DragonSteelArmorItem;
import com.iafenvoy.iceandfire.item.armor.EarPlugsArmorItem;
import com.iafenvoy.iceandfire.item.tool.*;
import com.iafenvoy.iceandfire.registry.tag.IafBannerPatternTags;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.CreativeTabRegistry;
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

    //Items
    public static final RegistrySupplier<Item> BESTIARY = registerItem("bestiary", BestiaryItem::new);
    public static final RegistrySupplier<Item> MANUSCRIPT = registerItem("manuscript", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> SAPPHIRE_GEM = registerItem("sapphire_gem", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> SILVER_INGOT = registerItem("silver_ingot", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> SILVER_NUGGET = registerItem("silver_nugget", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> RAW_SILVER = registerItem("raw_silver", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> COPPER_NUGGET = registerItem("copper_nugget", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> FIRE_STEW = registerItem("fire_stew", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> FROST_STEW = registerItem("frost_stew", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> LIGHTNING_STEW = registerItem("lightning_stew", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> DRAGONEGG_RED = registerItem("dragonegg_red", () -> new DragonEggItem(IafDragonColors.RED));
    public static final RegistrySupplier<Item> DRAGONEGG_GREEN = registerItem("dragonegg_green", () -> new DragonEggItem(IafDragonColors.GREEN));
    public static final RegistrySupplier<Item> DRAGONEGG_BRONZE = registerItem("dragonegg_bronze", () -> new DragonEggItem(IafDragonColors.BRONZE));
    public static final RegistrySupplier<Item> DRAGONEGG_GRAY = registerItem("dragonegg_gray", () -> new DragonEggItem(IafDragonColors.GRAY));
    public static final RegistrySupplier<Item> DRAGONEGG_BLUE = registerItem("dragonegg_blue", () -> new DragonEggItem(IafDragonColors.BLUE));
    public static final RegistrySupplier<Item> DRAGONEGG_WHITE = registerItem("dragonegg_white", () -> new DragonEggItem(IafDragonColors.WHITE));
    public static final RegistrySupplier<Item> DRAGONEGG_SAPPHIRE = registerItem("dragonegg_sapphire", () -> new DragonEggItem(IafDragonColors.SAPPHIRE));
    public static final RegistrySupplier<Item> DRAGONEGG_SILVER = registerItem("dragonegg_silver", () -> new DragonEggItem(IafDragonColors.SILVER));
    public static final RegistrySupplier<Item> DRAGONEGG_ELECTRIC = registerItem("dragonegg_electric", () -> new DragonEggItem(IafDragonColors.ELECTRIC));
    public static final RegistrySupplier<Item> DRAGONEGG_AMETHYST = registerItem("dragonegg_amethyst", () -> new DragonEggItem(IafDragonColors.AMETHYST));
    public static final RegistrySupplier<Item> DRAGONEGG_COPPER = registerItem("dragonegg_copper", () -> new DragonEggItem(IafDragonColors.COPPER));
    public static final RegistrySupplier<Item> DRAGONEGG_BLACK = registerItem("dragonegg_black", () -> new DragonEggItem(IafDragonColors.BLACK));
    public static final RegistrySupplier<Item> DRAGONSCALES_RED = registerItem("dragonscales_red", () -> new DragonScalesItem(IafDragonColors.RED));
    public static final RegistrySupplier<Item> DRAGONSCALES_GREEN = registerItem("dragonscales_green", () -> new DragonScalesItem(IafDragonColors.GREEN));
    public static final RegistrySupplier<Item> DRAGONSCALES_BRONZE = registerItem("dragonscales_bronze", () -> new DragonScalesItem(IafDragonColors.BRONZE));
    public static final RegistrySupplier<Item> DRAGONSCALES_GRAY = registerItem("dragonscales_gray", () -> new DragonScalesItem(IafDragonColors.GRAY));
    public static final RegistrySupplier<Item> DRAGONSCALES_BLUE = registerItem("dragonscales_blue", () -> new DragonScalesItem(IafDragonColors.BLUE));
    public static final RegistrySupplier<Item> DRAGONSCALES_WHITE = registerItem("dragonscales_white", () -> new DragonScalesItem(IafDragonColors.WHITE));
    public static final RegistrySupplier<Item> DRAGONSCALES_SAPPHIRE = registerItem("dragonscales_sapphire", () -> new DragonScalesItem(IafDragonColors.SAPPHIRE));
    public static final RegistrySupplier<Item> DRAGONSCALES_SILVER = registerItem("dragonscales_silver", () -> new DragonScalesItem(IafDragonColors.SILVER));
    public static final RegistrySupplier<Item> DRAGONSCALES_ELECTRIC = registerItem("dragonscales_electric", () -> new DragonScalesItem(IafDragonColors.ELECTRIC));
    public static final RegistrySupplier<Item> DRAGONSCALES_AMETHYST = registerItem("dragonscales_amethyst", () -> new DragonScalesItem(IafDragonColors.AMETHYST));
    public static final RegistrySupplier<Item> DRAGONSCALES_COPPER = registerItem("dragonscales_copper", () -> new DragonScalesItem(IafDragonColors.COPPER));
    public static final RegistrySupplier<Item> DRAGONSCALES_BLACK = registerItem("dragonscales_black", () -> new DragonScalesItem(IafDragonColors.BLACK));
    public static final RegistrySupplier<Item> DRAGON_BONE = registerItem("dragonbone", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> WITHERBONE = registerItem("witherbone", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> WITHER_SHARD = registerItem("wither_shard", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> DRAGON_SKULL_FIRE = registerItem("dragon_skull_fire", () -> new DragonSkullItem(IafDragonTypes.FIRE));
    public static final RegistrySupplier<Item> DRAGON_SKULL_ICE = registerItem("dragon_skull_ice", () -> new DragonSkullItem(IafDragonTypes.ICE));
    public static final RegistrySupplier<Item> DRAGON_SKULL_LIGHTNING = registerItem("dragon_skull_lightning", () -> new DragonSkullItem(IafDragonTypes.LIGHTNING));
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_IRON_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.IRON);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_IRON_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.IRON);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_IRON_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.IRON);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_IRON_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.IRON);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_COPPER_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.COPPER);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_COPPER_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.COPPER);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_COPPER_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.COPPER);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_COPPER_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.COPPER);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_SILVER_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.SILVER);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_SILVER_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.SILVER);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_SILVER_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.SILVER);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_SILVER_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.SILVER);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_GOLD_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.GOLD);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_GOLD_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.GOLD);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_GOLD_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.GOLD);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_GOLD_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.GOLD);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DIAMOND_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DIAMOND);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DIAMOND_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DIAMOND);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DIAMOND_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DIAMOND);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DIAMOND_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DIAMOND);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_NETHERITE_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.NETHERITE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_NETHERITE_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.NETHERITE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_NETHERITE_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.NETHERITE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_NETHERITE_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.NETHERITE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_FIRE_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_FIRE_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_FIRE_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_FIRE_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_ICE_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_ICE_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_ICE_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_ICE_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final RegistrySupplier<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final RegistrySupplier<Item> DRAGON_MEAL = registerItem("dragon_meal", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> SICKLY_DRAGON_MEAL = registerItem("sickly_dragon_meal", () -> new GenericItem(1));
    public static final RegistrySupplier<Item> CREATIVE_DRAGON_MEAL = registerItem("creative_dragon_meal", () -> new GenericItem(2));
    public static final RegistrySupplier<Item> FIRE_DRAGON_FLESH = registerItem("fire_dragon_flesh", () -> new DragonFleshItem(IafDragonTypes.FIRE));
    public static final RegistrySupplier<Item> ICE_DRAGON_FLESH = registerItem("ice_dragon_flesh", () -> new DragonFleshItem(IafDragonTypes.ICE));
    public static final RegistrySupplier<Item> LIGHTNING_DRAGON_FLESH = registerItem("lightning_dragon_flesh", () -> new DragonFleshItem(IafDragonTypes.LIGHTNING));
    public static final RegistrySupplier<Item> FIRE_DRAGON_HEART = registerItem("fire_dragon_heart", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> ICE_DRAGON_HEART = registerItem("ice_dragon_heart", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> LIGHTNING_DRAGON_HEART = registerItem("lightning_dragon_heart", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> FIRE_DRAGON_BLOOD = registerItem("fire_dragon_blood", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> ICE_DRAGON_BLOOD = registerItem("ice_dragon_blood", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> LIGHTNING_DRAGON_BLOOD = registerItem("lightning_dragon_blood", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> DRAGON_STAFF = registerItem("dragon_stick", () -> new Item(new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> DRAGON_HORN = registerItem("dragon_horn", DragonHornItem::new);
    public static final RegistrySupplier<Item> DRAGON_FLUTE = registerItem("dragon_flute", DragonFluteItem::new);
    public static final RegistrySupplier<Item> SUMMONING_CRYSTAL_FIRE = registerItem("summoning_crystal_fire", SummoningCrystalItem::new);
    public static final RegistrySupplier<Item> SUMMONING_CRYSTAL_ICE = registerItem("summoning_crystal_ice", SummoningCrystalItem::new);
    public static final RegistrySupplier<Item> SUMMONING_CRYSTAL_LIGHTNING = registerItem("summoning_crystal_lightning", SummoningCrystalItem::new);
    public static final RegistrySupplier<Item> HIPPOGRYPH_EGG = registerItem("hippogryph_egg", HippogryphEggItem::new);
    public static final RegistrySupplier<Item> IRON_HIPPOGRYPH_ARMOR = registerItem("iron_hippogryph_armor", () -> new Item(new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> GOLD_HIPPOGRYPH_ARMOR = registerItem("gold_hippogryph_armor", () -> new Item(new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> DIAMOND_HIPPOGRYPH_ARMOR = registerItem("diamond_hippogryph_armor", () -> new Item(new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<Item> NETHERITE_HIPPOGRYPH_ARMOR = registerItem("netherite_hippogryph_armor", () -> new Item(new Item.Settings().maxCount(1).fireproof()));
    public static final RegistrySupplier<Item> HIPPOGRYPH_TALON = registerItem("hippogryph_talon", () -> new GenericItem(1));
    public static final RegistrySupplier<Item> STONE_STATUE = registerItem("stone_statue", StoneStatueItem::new);
    public static final RegistrySupplier<Item> BLINDFOLD = registerItem("blindfold", BlindfoldItem::new);
    public static final RegistrySupplier<Item> PIXIE_DUST = registerItem("pixie_dust", () -> new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(1).saturationModifier(0.3F).alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 100, 1), 1).statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 1), 1).build())));
    public static final RegistrySupplier<Item> PIXIE_WINGS = registerItem("pixie_wings", () -> new GenericItem(1));
    public static final RegistrySupplier<Item> AMBROSIA = registerItem("ambrosia", () -> new Item(new Item.Settings().maxCount(1).food(new FoodComponent.Builder().nutrition(5).saturationModifier(0.6F).alwaysEdible().usingConvertsTo(Items.BOWL).statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 3600, 2), 1).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 3600, 2), 1).statusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 3600, 2), 1).statusEffect(new StatusEffectInstance(StatusEffects.LUCK, 3600, 2), 1).build())));
    public static final RegistrySupplier<Item> SHINY_SCALES = registerItem("shiny_scales", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> SIREN_TEAR = registerItem("siren_tear", () -> new GenericItem(1));
    public static final RegistrySupplier<Item> HIPPOCAMPUS_FIN = registerItem("hippocampus_fin", () -> new GenericItem(1));
    public static final RegistrySupplier<Item> EARPLUGS = registerItem("earplugs", EarPlugsArmorItem::new);
    public static final RegistrySupplier<Item> DEATH_WORM_CHITIN_YELLOW = registerItem("deathworm_chitin_yellow", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> DEATH_WORM_CHITIN_WHITE = registerItem("deathworm_chitin_white", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> DEATH_WORM_CHITIN_RED = registerItem("deathworm_chitin_red", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> DEATHWORM_EGG = registerItem("deathworm_egg", () -> new DeathwormEggItem(false));
    public static final RegistrySupplier<Item> DEATHWORM_EGG_GIGANTIC = registerItem("deathworm_egg_giant", () -> new DeathwormEggItem(true));
    public static final RegistrySupplier<Item> DEATHWORM_TOUNGE = registerItem("deathworm_tounge", () -> new GenericItem(1));
    public static final RegistrySupplier<Item> ROTTEN_EGG = registerItem("rotten_egg", RottenEggItem::new);
    public static final RegistrySupplier<Item> COCKATRICE_EYE = registerItem("cockatrice_eye", () -> new GenericItem(1));
    public static final RegistrySupplier<Item> STYMPHALIAN_BIRD_FEATHER = registerItem("stymphalian_bird_feather", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> TROLL_TUSK = registerItem("troll_tusk", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> AMPHITHERE_FEATHER = registerItem("amphithere_feather", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> AMPHITHERE_ARROW = registerItem("amphithere_arrow", AmphithereArrowItem::new);
    public static final RegistrySupplier<Item> SERPENT_FANG = registerItem("sea_serpent_fang", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> SEA_SERPENT_ARROW = registerItem("sea_serpent_arrow", SeaSerpentArrowItem::new);
    public static final RegistrySupplier<Item> CHAIN = registerItem("chain", () -> new ChainItem(false));
    public static final RegistrySupplier<Item> CHAIN_STICKY = registerItem("chain_sticky", () -> new ChainItem(true));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_INGOT = registerItem("dragonsteel_fire_ingot", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_INGOT = registerItem("dragonsteel_ice_ingot", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_INGOT = registerItem("dragonsteel_lightning_ingot", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> DREAD_SHARD = registerItem("dread_shard", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> DREAD_KEY = registerItem("dread_key", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> HYDRA_FANG = registerItem("hydra_fang", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> HYDRA_HEART = registerItem("hydra_heart", HydraHeartItem::new);
    public static final RegistrySupplier<Item> HYDRA_ARROW = registerItem("hydra_arrow", HydraArrowItem::new);
    public static final RegistrySupplier<Item> CANNOLI = registerItem("cannoli", CannoliItem::new);
    public static final RegistrySupplier<Item> ECTOPLASM = registerItem("ectoplasm", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> GHOST_INGOT = registerItem("ghost_ingot", () -> new GenericItem(1));
    public static final RegistrySupplier<Item> DRAGON_SEEKER = registerItem("dragon_seeker", () -> new DragonSeekerItem(DragonSeekerItem.SeekerType.NORMAL));
    public static final RegistrySupplier<Item> EPIC_DRAGON_SEEKER = registerItem("epic_dragon_seeker", () -> new DragonSeekerItem(DragonSeekerItem.SeekerType.EPIC));
    public static final RegistrySupplier<Item> LEGENDARY_DRAGON_SEEKER = registerItem("legendary_dragon_seeker", () -> new DragonSeekerItem(DragonSeekerItem.SeekerType.LEGENDARY));
    public static final RegistrySupplier<Item> GODLY_DRAGON_SEEKER = registerItem("godly_dragon_seeker", () -> new DragonSeekerItem(DragonSeekerItem.SeekerType.GODLY));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_FIRE = registerItem("banner_pattern_fire", () -> new BannerPatternItem(IafBannerPatternTags.FIRE_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_ICE = registerItem("banner_pattern_ice", () -> new BannerPatternItem(IafBannerPatternTags.ICE_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_LIGHTNING = registerItem("banner_pattern_lightning", () -> new BannerPatternItem(IafBannerPatternTags.LIGHTNING_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_FIRE_HEAD = registerItem("banner_pattern_fire_head", () -> new BannerPatternItem(IafBannerPatternTags.FIRE_HEAD_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_ICE_HEAD = registerItem("banner_pattern_ice_head", () -> new BannerPatternItem(IafBannerPatternTags.ICE_HEAD_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_LIGHTNING_HEAD = registerItem("banner_pattern_lightning_head", () -> new BannerPatternItem(IafBannerPatternTags.LIGHTNING_HEAD_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_AMPHITHERE = registerItem("banner_pattern_amphithere", () -> new BannerPatternItem(IafBannerPatternTags.AMPHITHERE_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_BIRD = registerItem("banner_pattern_bird", () -> new BannerPatternItem(IafBannerPatternTags.BIRD_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_EYE = registerItem("banner_pattern_eye", () -> new BannerPatternItem(IafBannerPatternTags.EYE_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_FAE = registerItem("banner_pattern_fae", () -> new BannerPatternItem(IafBannerPatternTags.FAE_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_FEATHER = registerItem("banner_pattern_feather", () -> new BannerPatternItem(IafBannerPatternTags.FEATHER_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_GORGON = registerItem("banner_pattern_gorgon", () -> new BannerPatternItem(IafBannerPatternTags.GORGON_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_HIPPOCAMPUS = registerItem("banner_pattern_hippocampus", () -> new BannerPatternItem(IafBannerPatternTags.HIPPOCAMPUS_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_HIPPOGRYPH_HEAD = registerItem("banner_pattern_hippogryph_head", () -> new BannerPatternItem(IafBannerPatternTags.HIPPOGRYPH_HEAD_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_MERMAID = registerItem("banner_pattern_mermaid", () -> new BannerPatternItem(IafBannerPatternTags.MERMAID_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_SEA_SERPENT = registerItem("banner_pattern_sea_serpent", () -> new BannerPatternItem(IafBannerPatternTags.SEA_SERPENT_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_TROLL = registerItem("banner_pattern_troll", () -> new BannerPatternItem(IafBannerPatternTags.TROLL_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_WEEZER = registerItem("banner_pattern_weezer", () -> new BannerPatternItem(IafBannerPatternTags.WEEZER_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<BannerPatternItem> PATTERN_DREAD = registerItem("banner_pattern_dread", () -> new BannerPatternItem(IafBannerPatternTags.DREAD_BANNER_PATTERN, new Item.Settings().maxCount(1)));
    public static final RegistrySupplier<DelightFoodItem> COOKED_RICE_WITH_FIRE_DRAGON_MEAT = registerItem("cooked_rice_with_fire_dragon_meat", () -> new DelightFoodItem(new Item.Settings().maxCount(1).food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.6f).usingConvertsTo(Items.BOWL).statusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 20 * 5), 1).statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 60 * 2), 1).build())));
    public static final RegistrySupplier<DelightFoodItem> COOKED_RICE_WITH_ICE_DRAGON_MEAT = registerItem("cooked_rice_with_ice_dragon_meat", () -> new DelightFoodItem(new Item.Settings().maxCount(1).food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.6f).usingConvertsTo(Items.BOWL).statusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 20 * 5), 1).statusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 60 * 2, 2), 1).build())));
    public static final RegistrySupplier<DelightFoodItem> COOKED_RICE_WITH_LIGHTNING_DRAGON_MEAT = registerItem("cooked_rice_with_lightning_dragon_meat", () -> new DelightFoodItem(new Item.Settings().maxCount(1).food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.6f).usingConvertsTo(Items.BOWL).statusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 20 * 5), 1).statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 60 * 2, 2), 1).build())));
    public static final RegistrySupplier<DelightFoodItem> GHOST_CREAM = registerItem("ghost_cream", () -> new DelightFoodItem(new Item.Settings().maxCount(1).food(new FoodComponent.Builder().snack().nutrition(4).saturationModifier(0.6f).usingConvertsTo(Items.GLASS_BOTTLE).statusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20 * 20), 1).build())));
    public static final RegistrySupplier<DelightFoodItem> PIXIE_DUST_MILKY_TEA = registerItem("pixie_dust_milky_tea", () -> new DelightFoodItem(new Item.Settings().maxCount(1).food(new FoodComponent.Builder().snack().nutrition(4).saturationModifier(0.6f).usingConvertsTo(Items.GLASS_BOTTLE).statusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 20 * 60 * 2), 1).build())));

    //spawn Eggs
    static {
        registerItem("spawn_egg_fire_dragon", () -> new ArchitecturySpawnEggItem(IafEntities.FIRE_DRAGON, 0X340000, 0XA52929, new Item.Settings()));
        registerItem("spawn_egg_ice_dragon", () -> new ArchitecturySpawnEggItem(IafEntities.ICE_DRAGON, 0XB5DDFB, 0X7EBAF0, new Item.Settings()));
        registerItem("spawn_egg_lightning_dragon", () -> new ArchitecturySpawnEggItem(IafEntities.LIGHTNING_DRAGON, 0X422367, 0X725691, new Item.Settings()));
        registerItem("spawn_egg_hippogryph", () -> new ArchitecturySpawnEggItem(IafEntities.HIPPOGRYPH, 0XD8D8D8, 0XD1B55D, new Item.Settings()));
        registerItem("spawn_egg_gorgon", () -> new ArchitecturySpawnEggItem(IafEntities.GORGON, 0XD0D99F, 0X684530, new Item.Settings()));
        registerItem("spawn_egg_pixie", () -> new ArchitecturySpawnEggItem(IafEntities.PIXIE, 0XFF7F89, 0XE2CCE2, new Item.Settings()));
        registerItem("spawn_egg_cyclops", () -> new ArchitecturySpawnEggItem(IafEntities.CYCLOPS, 0XB0826E, 0X3A1F0F, new Item.Settings()));
        registerItem("spawn_egg_siren", () -> new ArchitecturySpawnEggItem(IafEntities.SIREN, 0X8EE6CA, 0XF2DFC8, new Item.Settings()));
        registerItem("spawn_egg_hippocampus", () -> new ArchitecturySpawnEggItem(IafEntities.HIPPOCAMPUS, 0X4491C7, 0X4FC56B, new Item.Settings()));
        registerItem("spawn_egg_death_worm", () -> new ArchitecturySpawnEggItem(IafEntities.DEATH_WORM, 0XD1CDA3, 0X423A3A, new Item.Settings()));
        registerItem("spawn_egg_cockatrice", () -> new ArchitecturySpawnEggItem(IafEntities.COCKATRICE, 0X8F5005, 0X4F5A23, new Item.Settings()));
        registerItem("spawn_egg_stymphalian_bird", () -> new ArchitecturySpawnEggItem(IafEntities.STYMPHALIAN_BIRD, 0X744F37, 0X9E6C4B, new Item.Settings()));
        registerItem("spawn_egg_troll", () -> new ArchitecturySpawnEggItem(IafEntities.TROLL, 0X3D413D, 0X58433A, new Item.Settings()));
        registerItem("spawn_egg_amphithere", () -> new ArchitecturySpawnEggItem(IafEntities.AMPHITHERE, 0X597535, 0X00AA98, new Item.Settings()));
        registerItem("spawn_egg_sea_serpent", () -> new ArchitecturySpawnEggItem(IafEntities.SEA_SERPENT, 0X008299, 0XC5E6E7, new Item.Settings()));
        registerItem("spawn_egg_dread_thrall", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_THRALL, 0XE0E6E6, 0X00FFFF, new Item.Settings()));
        registerItem("spawn_egg_dread_ghoul", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_GHOUL, 0XE0E6E6, 0X7B838A, new Item.Settings()));
        registerItem("spawn_egg_dread_beast", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_BEAST, 0XE0E6E6, 0X38373C, new Item.Settings()));
        registerItem("spawn_egg_dread_scuttler", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_SCUTTLER, 0XE0E6E6, 0X4D5667, new Item.Settings()));
        registerItem("spawn_egg_lich", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_LICH, 0XE0E6E6, 0X274860, new Item.Settings()));
        registerItem("spawn_egg_dread_knight", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_KNIGHT, 0XE0E6E6, 0X4A6C6E, new Item.Settings()));
        registerItem("spawn_egg_dread_horse", () -> new ArchitecturySpawnEggItem(IafEntities.DREAD_HORSE, 0XE0E6E6, 0XACACAC, new Item.Settings()));
        registerItem("spawn_egg_hydra", () -> new ArchitecturySpawnEggItem(IafEntities.HYDRA, 0X8B8B78, 0X2E372B, new Item.Settings()));
        registerItem("spawn_egg_ghost", () -> new ArchitecturySpawnEggItem(IafEntities.GHOST, 0XB9EDB8, 0X73B276, new Item.Settings()));
    }

    //Hidden
    public static final RegistrySupplier<Item> TIDE_TRIDENT_INVENTORY = register("tide_trident_inventory", () -> new Item(new Item.Settings()));
    public static final RegistrySupplier<Item> WEEZER_BLUE_ALBUM = register("weezer_blue_album", () -> new GenericItem(1));
    public static final RegistrySupplier<Item> DRAGON_DEBUG_STICK = register("dragon_debug_stick", () -> new GenericItem(1));

    //Armors
    public static final RegistrySupplier<Item> SILVER_HELMET = registerArmor("armor_silver_metal_helmet", () -> new ArmorItem(IafArmorMaterials.SILVER, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> SILVER_CHESTPLATE = registerArmor("armor_silver_metal_chestplate", () -> new ArmorItem(IafArmorMaterials.SILVER, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(240)));
    public static final RegistrySupplier<Item> SILVER_LEGGINGS = registerArmor("armor_silver_metal_leggings", () -> new ArmorItem(IafArmorMaterials.SILVER, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(225)));
    public static final RegistrySupplier<Item> SILVER_BOOTS = registerArmor("armor_silver_metal_boots", () -> new ArmorItem(IafArmorMaterials.SILVER, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(195)));
    public static final RegistrySupplier<Item> COPPER_HELMET = registerArmor("armor_copper_metal_helmet", () -> new ArmorItem(IafArmorMaterials.COPPER, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(111)));
    public static final RegistrySupplier<Item> COPPER_CHESTPLATE = registerArmor("armor_copper_metal_chestplate", () -> new ArmorItem(IafArmorMaterials.COPPER, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(161)));
    public static final RegistrySupplier<Item> COPPER_LEGGINGS = registerArmor("armor_copper_metal_leggings", () -> new ArmorItem(IafArmorMaterials.COPPER, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(151)));
    public static final RegistrySupplier<Item> COPPER_BOOTS = registerArmor("armor_copper_metal_boots", () -> new ArmorItem(IafArmorMaterials.COPPER, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(131)));
    public static final RegistrySupplier<Item> SHEEP_HELMET = registerArmor("sheep_helmet", () -> new ArmorItem(IafArmorMaterials.SHEEP, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(55)));
    public static final RegistrySupplier<Item> SHEEP_CHESTPLATE = registerArmor("sheep_chestplate", () -> new ArmorItem(IafArmorMaterials.SHEEP, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(80)));
    public static final RegistrySupplier<Item> SHEEP_LEGGINGS = registerArmor("sheep_leggings", () -> new ArmorItem(IafArmorMaterials.SHEEP, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(75)));
    public static final RegistrySupplier<Item> SHEEP_BOOTS = registerArmor("sheep_boots", () -> new ArmorItem(IafArmorMaterials.SHEEP, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(65)));
    public static final RegistrySupplier<Item> DEATHWORM_YELLOW_HELMET = registerArmor("deathworm_yellow_helmet", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_YELLOW, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> DEATHWORM_YELLOW_CHESTPLATE = registerArmor("deathworm_yellow_chestplate", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_YELLOW, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(240)));
    public static final RegistrySupplier<Item> DEATHWORM_YELLOW_LEGGINGS = registerArmor("deathworm_yellow_leggings", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_YELLOW, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(225)));
    public static final RegistrySupplier<Item> DEATHWORM_YELLOW_BOOTS = registerArmor("deathworm_yellow_boots", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_YELLOW, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(195)));
    public static final RegistrySupplier<Item> DEATHWORM_WHITE_HELMET = registerArmor("deathworm_white_helmet", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_WHITE, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> DEATHWORM_WHITE_CHESTPLATE = registerArmor("deathworm_white_chestplate", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_WHITE, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(240)));
    public static final RegistrySupplier<Item> DEATHWORM_WHITE_LEGGINGS = registerArmor("deathworm_white_leggings", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_WHITE, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(225)));
    public static final RegistrySupplier<Item> DEATHWORM_WHITE_BOOTS = registerArmor("deathworm_white_boots", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_WHITE, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> DEATHWORM_RED_HELMET = registerArmor("deathworm_red_helmet", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_RED, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> DEATHWORM_RED_CHESTPLATE = registerArmor("deathworm_red_chestplate", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_RED, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(240)));
    public static final RegistrySupplier<Item> DEATHWORM_RED_LEGGINGS = registerArmor("deathworm_red_leggings", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_RED, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(225)));
    public static final RegistrySupplier<Item> DEATHWORM_RED_BOOTS = registerArmor("deathworm_red_boots", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_RED, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(165)));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_HELMET = registerArmor("dragonsteel_fire_helmet", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_FIRE, ArmorItem.Type.HELMET));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_CHESTPLATE = registerArmor("dragonsteel_fire_chestplate", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_FIRE, ArmorItem.Type.CHESTPLATE));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_LEGGINGS = registerArmor("dragonsteel_fire_leggings", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_FIRE, ArmorItem.Type.LEGGINGS));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_BOOTS = registerArmor("dragonsteel_fire_boots", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_FIRE, ArmorItem.Type.BOOTS));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_HELMET = registerArmor("dragonsteel_ice_helmet", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_ICE, ArmorItem.Type.HELMET));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_CHESTPLATE = registerArmor("dragonsteel_ice_chestplate", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_ICE, ArmorItem.Type.CHESTPLATE));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_LEGGINGS = registerArmor("dragonsteel_ice_leggings", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_ICE, ArmorItem.Type.LEGGINGS));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_BOOTS = registerArmor("dragonsteel_ice_boots", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_ICE, ArmorItem.Type.BOOTS));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_HELMET = registerArmor("dragonsteel_lightning_helmet", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_LIGHTNING, ArmorItem.Type.HELMET));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_CHESTPLATE = registerArmor("dragonsteel_lightning_chestplate", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_LIGHTNING, ArmorItem.Type.CHESTPLATE));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_LEGGINGS = registerArmor("dragonsteel_lightning_leggings", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_LIGHTNING, ArmorItem.Type.LEGGINGS));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_BOOTS = registerArmor("dragonsteel_lightning_boots", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_LIGHTNING, ArmorItem.Type.BOOTS));

    //Tools&Weapons
    public static final RegistrySupplier<Item> SILVER_SWORD = registerToolOrWeapon("silver_sword", () -> new ActivePostHitSwordItem(IafToolMaterials.SILVER_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.SILVER_TOOL_MATERIAL, 3.0F, -2.4F)), BuiltinAbilities.UNDEAD_DAMAGE_BONUS));
    public static final RegistrySupplier<Item> SILVER_SHOVEL = registerToolOrWeapon("silver_shovel", () -> new ActivePostHitShovelItem(IafToolMaterials.SILVER_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.SILVER_TOOL_MATERIAL, 1.5F, -3.0F)), BuiltinAbilities.UNDEAD_DAMAGE_BONUS));
    public static final RegistrySupplier<Item> SILVER_PICKAXE = registerToolOrWeapon("silver_pickaxe", () -> new ActivePostHitPickaxeItem(IafToolMaterials.SILVER_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.SILVER_TOOL_MATERIAL, 1.0F, -2.8F)), BuiltinAbilities.UNDEAD_DAMAGE_BONUS));
    public static final RegistrySupplier<Item> SILVER_AXE = registerToolOrWeapon("silver_axe", () -> new ActivePostHitAxeItem(IafToolMaterials.SILVER_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.SILVER_TOOL_MATERIAL, 6.0F, -3.0F)), BuiltinAbilities.UNDEAD_DAMAGE_BONUS));
    public static final RegistrySupplier<Item> SILVER_HOE = registerToolOrWeapon("silver_hoe", () -> new ActivePostHitHoeItem(IafToolMaterials.SILVER_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.SILVER_TOOL_MATERIAL, 0.0F, -3.0F)), BuiltinAbilities.UNDEAD_DAMAGE_BONUS));
    public static final RegistrySupplier<Item> COPPER_SWORD = registerToolOrWeapon("copper_sword", () -> new SwordItem(IafToolMaterials.COPPER_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.COPPER_TOOL_MATERIAL, 3.0F, -2.4F))));
    public static final RegistrySupplier<Item> COPPER_SHOVEL = registerToolOrWeapon("copper_shovel", () -> new ShovelItem(IafToolMaterials.COPPER_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.COPPER_TOOL_MATERIAL, 1.5F, -2.4F))));
    public static final RegistrySupplier<Item> COPPER_PICKAXE = registerToolOrWeapon("copper_pickaxe", () -> new PickaxeItem(IafToolMaterials.COPPER_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.COPPER_TOOL_MATERIAL, 1.0F, -2.8F))));
    public static final RegistrySupplier<Item> COPPER_AXE = registerToolOrWeapon("copper_axe", () -> new AxeItem(IafToolMaterials.COPPER_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.COPPER_TOOL_MATERIAL, 6.0F, -3.0F))));
    public static final RegistrySupplier<Item> COPPER_HOE = registerToolOrWeapon("copper_hoe", () -> new HoeItem(IafToolMaterials.COPPER_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.COPPER_TOOL_MATERIAL, 0.0F, -1.0F))));
    public static final RegistrySupplier<Item> FISHING_SPEAR = registerToolOrWeapon("fishing_spear", () -> new Item(new Item.Settings().maxDamage(64)));
    public static final RegistrySupplier<Item> DRAGONBONE_SWORD = registerToolOrWeapon("dragonbone_sword", () -> new SwordItem(IafToolMaterials.DRAGONBONE_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONBONE_TOOL_MATERIAL, 3.0F, -2.4F))));
    public static final RegistrySupplier<Item> DRAGONBONE_SHOVEL = registerToolOrWeapon("dragonbone_shovel", () -> new ShovelItem(IafToolMaterials.DRAGONBONE_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONBONE_TOOL_MATERIAL, 1.5F, -2.8F))));
    public static final RegistrySupplier<Item> DRAGONBONE_PICKAXE = registerToolOrWeapon("dragonbone_pickaxe", () -> new PickaxeItem(IafToolMaterials.DRAGONBONE_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONBONE_TOOL_MATERIAL, 1.0F, -2.8F))));
    public static final RegistrySupplier<Item> DRAGONBONE_AXE = registerToolOrWeapon("dragonbone_axe", () -> new AxeItem(IafToolMaterials.DRAGONBONE_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONBONE_TOOL_MATERIAL, 5.0F, -3.0F))));
    public static final RegistrySupplier<Item> DRAGONBONE_HOE = registerToolOrWeapon("dragonbone_hoe", () -> new HoeItem(IafToolMaterials.DRAGONBONE_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONBONE_TOOL_MATERIAL, -4.0F, 0.0F))));
    public static final RegistrySupplier<Item> DRAGONBONE_ARROW = registerToolOrWeapon("dragonbone_arrow", DragonArrowItem::new);
    public static final RegistrySupplier<Item> DRAGON_BOW = registerToolOrWeapon("dragonbone_bow", DragonBowItem::new);
    public static final RegistrySupplier<Item> STYMPHALIAN_ARROW = registerToolOrWeapon("stymphalian_arrow", StymphalianArrowItem::new);
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_SWORD = registerToolOrWeapon("dragonsteel_fire_sword", () -> new ActivePostHitSwordItem(IafToolMaterials.DRAGONSTEEL_FIRE, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_FIRE, 3.0F, -2.4F)), BuiltinAbilities.DRAGONSTEEL_FIRE_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_PICKAXE = registerToolOrWeapon("dragonsteel_fire_pickaxe", () -> new ActivePostHitPickaxeItem(IafToolMaterials.DRAGONSTEEL_FIRE, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_FIRE, 1.0F, -2.8F)), BuiltinAbilities.DRAGONSTEEL_FIRE_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_AXE = registerToolOrWeapon("dragonsteel_fire_axe", () -> new ActivePostHitAxeItem(IafToolMaterials.DRAGONSTEEL_FIRE, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_FIRE, 5.0F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_FIRE_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_SHOVEL = registerToolOrWeapon("dragonsteel_fire_shovel", () -> new ActivePostHitShovelItem(IafToolMaterials.DRAGONSTEEL_FIRE, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_FIRE, 1.5F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_FIRE_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_FIRE_HOE = registerToolOrWeapon("dragonsteel_fire_hoe", () -> new ActivePostHitHoeItem(IafToolMaterials.DRAGONSTEEL_FIRE, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_FIRE, -4.0F, 0.0F)), BuiltinAbilities.DRAGONSTEEL_FIRE_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_SWORD = registerToolOrWeapon("dragonsteel_ice_sword", () -> new ActivePostHitSwordItem(IafToolMaterials.DRAGONSTEEL_ICE, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_ICE, 3.0F, -2.4F)), BuiltinAbilities.DRAGONSTEEL_ICE_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_PICKAXE = registerToolOrWeapon("dragonsteel_ice_pickaxe", () -> new ActivePostHitPickaxeItem(IafToolMaterials.DRAGONSTEEL_ICE, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_ICE, 1.0F, -2.8F)), BuiltinAbilities.DRAGONSTEEL_ICE_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_AXE = registerToolOrWeapon("dragonsteel_ice_axe", () -> new ActivePostHitAxeItem(IafToolMaterials.DRAGONSTEEL_ICE, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_ICE, 5.0F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_ICE_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_SHOVEL = registerToolOrWeapon("dragonsteel_ice_shovel", () -> new ActivePostHitShovelItem(IafToolMaterials.DRAGONSTEEL_ICE, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_ICE, 1.5F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_ICE_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_ICE_HOE = registerToolOrWeapon("dragonsteel_ice_hoe", () -> new ActivePostHitHoeItem(IafToolMaterials.DRAGONSTEEL_ICE, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_ICE, -4.0F, 0.0F)), BuiltinAbilities.DRAGONSTEEL_ICE_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_SWORD = registerToolOrWeapon("dragonsteel_lightning_sword", () -> new ActivePostHitSwordItem(IafToolMaterials.DRAGONSTEEL_LIGHTNING, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_LIGHTNING, 3.0F, -2.4F)), BuiltinAbilities.DRAGONSTEEL_LIGHTNING_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_PICKAXE = registerToolOrWeapon("dragonsteel_lightning_pickaxe", () -> new ActivePostHitPickaxeItem(IafToolMaterials.DRAGONSTEEL_LIGHTNING, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_LIGHTNING, 1.0F, -2.8F)), BuiltinAbilities.DRAGONSTEEL_LIGHTNING_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_AXE = registerToolOrWeapon("dragonsteel_lightning_axe", () -> new ActivePostHitAxeItem(IafToolMaterials.DRAGONSTEEL_LIGHTNING, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_LIGHTNING, 5.0F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_LIGHTNING_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_SHOVEL = registerToolOrWeapon("dragonsteel_lightning_shovel", () -> new ActivePostHitShovelItem(IafToolMaterials.DRAGONSTEEL_LIGHTNING, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_LIGHTNING, 1.5F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_LIGHTNING_TOOL));
    public static final RegistrySupplier<Item> DRAGONSTEEL_LIGHTNING_HOE = registerToolOrWeapon("dragonsteel_lightning_hoe", () -> new ActivePostHitHoeItem(IafToolMaterials.DRAGONSTEEL_LIGHTNING, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DRAGONSTEEL_LIGHTNING, -4.0F, 0.0F)), BuiltinAbilities.DRAGONSTEEL_LIGHTNING_TOOL));
    public static final RegistrySupplier<Item> DREAD_SWORD = registerToolOrWeapon("dread_sword", () -> new SwordItem(IafToolMaterials.DREAD_SWORD_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DREAD_SWORD_TOOL_MATERIAL, 3.0F, -2.4F))));
    public static final RegistrySupplier<Item> DREAD_KNIGHT_SWORD = registerToolOrWeapon("dread_knight_sword", () -> new SwordItem(IafToolMaterials.DREAD_KNIGHT_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DREAD_KNIGHT_TOOL_MATERIAL, 3.0F, -2.4F))));
    public static final RegistrySupplier<Item> LICH_STAFF = registerToolOrWeapon("lich_staff", LichStaffItem::new);
    public static final RegistrySupplier<Item> DREAD_QUEEN_SWORD = registerToolOrWeapon("dread_queen_sword", () -> new SwordItem(IafToolMaterials.DREAD_QUEEN, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.DREAD_QUEEN, 3.0F, -2.4F))));
    public static final RegistrySupplier<Item> DREAD_QUEEN_STAFF = registerToolOrWeapon("dread_queen_staff", DreadQueenStaffItem::new);
    //--Legendary
    public static final RegistrySupplier<Item> DRAGONBONE_SWORD_FIRE = registerToolOrWeapon("dragonbone_sword_fire", () -> new ActivePostHitSwordItem(IafToolMaterials.BLOODED_DRAGONBONE_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.BLOODED_DRAGONBONE_TOOL_MATERIAL, 3.0F, -2.4F)), BuiltinAbilities.FIRE_DRAGON_BLOOD_TOOL));
    public static final RegistrySupplier<Item> DRAGONBONE_SWORD_ICE = registerToolOrWeapon("dragonbone_sword_ice", () -> new ActivePostHitSwordItem(IafToolMaterials.BLOODED_DRAGONBONE_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.BLOODED_DRAGONBONE_TOOL_MATERIAL, 3.0F, -2.4F)), BuiltinAbilities.ICE_DRAGON_BLOOD_TOOL));
    public static final RegistrySupplier<Item> DRAGONBONE_SWORD_LIGHTNING = registerToolOrWeapon("dragonbone_sword_lightning", () -> new ActivePostHitSwordItem(IafToolMaterials.BLOODED_DRAGONBONE_TOOL_MATERIAL, new Item.Settings().component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createAttributeModifiers(IafToolMaterials.BLOODED_DRAGONBONE_TOOL_MATERIAL, 3.0F, -2.4F)), BuiltinAbilities.LIGHTNING_DRAGON_BLOOD_TOOL));
    public static final RegistrySupplier<Item> HIPPOGRYPH_SWORD = registerToolOrWeapon("hippogryph_sword", HippogryphSwordItem::new);
    public static final RegistrySupplier<Item> GORGON_HEAD = registerToolOrWeapon("gorgon_head", GorgonHeadItem::new);
    public static final RegistrySupplier<Item> PIXIE_WAND = registerToolOrWeapon("pixie_wand", PixieWandItem::new);
    public static final RegistrySupplier<Item> CYCLOPS_EYE = registerToolOrWeapon("cyclops_eye", CyclopsEyeItem::new);
    public static final RegistrySupplier<Item> SIREN_FLUTE = registerToolOrWeapon("siren_flute", SirenFluteItem::new);
    public static final RegistrySupplier<Item> HIPPOCAMPUS_SLAPPER = registerToolOrWeapon("hippocampus_slapper", HippocampusSlapperItem::new);
    public static final RegistrySupplier<Item> DEATHWORM_GAUNTLET_YELLOW = registerToolOrWeapon("deathworm_gauntlet_yellow", DeathwormGauntletItem::new);
    public static final RegistrySupplier<Item> DEATHWORM_GAUNTLET_WHITE = registerToolOrWeapon("deathworm_gauntlet_white", DeathwormGauntletItem::new);
    public static final RegistrySupplier<Item> DEATHWORM_GAUNTLET_RED = registerToolOrWeapon("deathworm_gauntlet_red", DeathwormGauntletItem::new);
    public static final RegistrySupplier<Item> COCKATRICE_SCEPTER = registerToolOrWeapon("cockatrice_scepter", CockatriceScepterItem::new);
    public static final RegistrySupplier<Item> STYMPHALIAN_FEATHER_BUNDLE = registerToolOrWeapon("stymphalian_feather_bundle", StymphalianFeatherBundleItem::new);
    public static final RegistrySupplier<Item> STYMPHALIAN_DAGGER = registerToolOrWeapon("stymphalian_bird_dagger", StymphalianDaggerItem::new);
    public static final RegistrySupplier<Item> AMPHITHERE_MACUAHUITL = registerToolOrWeapon("amphithere_macuahuitl", AmphithereMacuahuitlItem::new);
    public static final RegistrySupplier<Item> TIDE_TRIDENT = registerToolOrWeapon("tide_trident", TideTridentItem::new);
    public static final RegistrySupplier<Item> GHOST_SWORD = registerToolOrWeapon("ghost_sword", GhostSwordItem::new);

    public static RegistrySupplier<DragonArmorItem> buildDragonArmor(DragonArmorPart type, DragonArmorMaterial material) {
        return registerItem(String.format("dragonarmor_%s_%s", material.name(), type.getId()), () -> new DragonArmorItem(material, type));
    }

    @SuppressWarnings("UnstableApiUsage")
    public static <T extends Item> RegistrySupplier<T> registerBlock(String name, Supplier<T> item) {
        RegistrySupplier<T> r = register(name, item);
        r.listen(o -> CreativeTabRegistry.append(IafItemGroups.BLOCKS, o));
        return r;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static <T extends Item> RegistrySupplier<T> registerItem(String name, Supplier<T> item) {
        RegistrySupplier<T> r = register(name, item);
        r.listen(o -> CreativeTabRegistry.append(IafItemGroups.ITEMS, o));
        return r;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static <T extends Item> RegistrySupplier<T> registerToolOrWeapon(String name, Supplier<T> item) {
        RegistrySupplier<T> r = register(name, item);
        r.listen(o -> CreativeTabRegistry.append(IafItemGroups.TOOLS_WEAPONS, o));
        return r;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static <T extends Item> RegistrySupplier<T> registerArmor(String name, Supplier<T> item) {
        RegistrySupplier<T> r = register(name, item);
        r.listen(o -> CreativeTabRegistry.append(IafItemGroups.ARMORS, o));
        return r;
    }

    static <T extends Item> RegistrySupplier<T> register(String name, Supplier<T> item) {
        return REGISTRY.register(name, item);
    }
}
