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
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static net.minecraft.world.item.DiggerItem.createAttributes;

//FIXME::Specific vararg and remove .get()
@SuppressWarnings("unused")
public final class IafItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(IceAndFire.MOD_ID);

    //Items
    public static final DeferredItem<Item> BESTIARY = registerItem("bestiary", BestiaryItem::new);
    public static final DeferredItem<Item> MANUSCRIPT = registerItem("manuscript", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SAPPHIRE_GEM = registerItem("sapphire_gem", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SILVER_INGOT = registerItem("silver_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SILVER_NUGGET = registerItem("silver_nugget", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_SILVER = registerItem("raw_silver", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_NUGGET = registerItem("copper_nugget", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRE_STEW = registerItem("fire_stew", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FROST_STEW = registerItem("frost_stew", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LIGHTNING_STEW = registerItem("lightning_stew", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DRAGONEGG_RED = registerItem("dragonegg_red", () -> new DragonEggItem(IafDragonColors.RED));
    public static final DeferredItem<Item> DRAGONEGG_GREEN = registerItem("dragonegg_green", () -> new DragonEggItem(IafDragonColors.GREEN));
    public static final DeferredItem<Item> DRAGONEGG_BRONZE = registerItem("dragonegg_bronze", () -> new DragonEggItem(IafDragonColors.BRONZE));
    public static final DeferredItem<Item> DRAGONEGG_GRAY = registerItem("dragonegg_gray", () -> new DragonEggItem(IafDragonColors.GRAY));
    public static final DeferredItem<Item> DRAGONEGG_BLUE = registerItem("dragonegg_blue", () -> new DragonEggItem(IafDragonColors.BLUE));
    public static final DeferredItem<Item> DRAGONEGG_WHITE = registerItem("dragonegg_white", () -> new DragonEggItem(IafDragonColors.WHITE));
    public static final DeferredItem<Item> DRAGONEGG_SAPPHIRE = registerItem("dragonegg_sapphire", () -> new DragonEggItem(IafDragonColors.SAPPHIRE));
    public static final DeferredItem<Item> DRAGONEGG_SILVER = registerItem("dragonegg_silver", () -> new DragonEggItem(IafDragonColors.SILVER));
    public static final DeferredItem<Item> DRAGONEGG_ELECTRIC = registerItem("dragonegg_electric", () -> new DragonEggItem(IafDragonColors.ELECTRIC));
    public static final DeferredItem<Item> DRAGONEGG_AMETHYST = registerItem("dragonegg_amethyst", () -> new DragonEggItem(IafDragonColors.AMETHYST));
    public static final DeferredItem<Item> DRAGONEGG_COPPER = registerItem("dragonegg_copper", () -> new DragonEggItem(IafDragonColors.COPPER));
    public static final DeferredItem<Item> DRAGONEGG_BLACK = registerItem("dragonegg_black", () -> new DragonEggItem(IafDragonColors.BLACK));
    public static final DeferredItem<Item> DRAGONSCALES_RED = registerItem("dragonscales_red", () -> new DragonScalesItem(IafDragonColors.RED));
    public static final DeferredItem<Item> DRAGONSCALES_GREEN = registerItem("dragonscales_green", () -> new DragonScalesItem(IafDragonColors.GREEN));
    public static final DeferredItem<Item> DRAGONSCALES_BRONZE = registerItem("dragonscales_bronze", () -> new DragonScalesItem(IafDragonColors.BRONZE));
    public static final DeferredItem<Item> DRAGONSCALES_GRAY = registerItem("dragonscales_gray", () -> new DragonScalesItem(IafDragonColors.GRAY));
    public static final DeferredItem<Item> DRAGONSCALES_BLUE = registerItem("dragonscales_blue", () -> new DragonScalesItem(IafDragonColors.BLUE));
    public static final DeferredItem<Item> DRAGONSCALES_WHITE = registerItem("dragonscales_white", () -> new DragonScalesItem(IafDragonColors.WHITE));
    public static final DeferredItem<Item> DRAGONSCALES_SAPPHIRE = registerItem("dragonscales_sapphire", () -> new DragonScalesItem(IafDragonColors.SAPPHIRE));
    public static final DeferredItem<Item> DRAGONSCALES_SILVER = registerItem("dragonscales_silver", () -> new DragonScalesItem(IafDragonColors.SILVER));
    public static final DeferredItem<Item> DRAGONSCALES_ELECTRIC = registerItem("dragonscales_electric", () -> new DragonScalesItem(IafDragonColors.ELECTRIC));
    public static final DeferredItem<Item> DRAGONSCALES_AMETHYST = registerItem("dragonscales_amethyst", () -> new DragonScalesItem(IafDragonColors.AMETHYST));
    public static final DeferredItem<Item> DRAGONSCALES_COPPER = registerItem("dragonscales_copper", () -> new DragonScalesItem(IafDragonColors.COPPER));
    public static final DeferredItem<Item> DRAGONSCALES_BLACK = registerItem("dragonscales_black", () -> new DragonScalesItem(IafDragonColors.BLACK));
    public static final DeferredItem<Item> DRAGON_BONE = registerItem("dragonbone", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WITHERBONE = registerItem("witherbone", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WITHER_SHARD = registerItem("wither_shard", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DRAGON_SKULL_FIRE = registerItem("dragon_skull_fire", () -> new DragonSkullItem(IafDragonTypes.FIRE));
    public static final DeferredItem<Item> DRAGON_SKULL_ICE = registerItem("dragon_skull_ice", () -> new DragonSkullItem(IafDragonTypes.ICE));
    public static final DeferredItem<Item> DRAGON_SKULL_LIGHTNING = registerItem("dragon_skull_lightning", () -> new DragonSkullItem(IafDragonTypes.LIGHTNING));
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_IRON_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.IRON);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_IRON_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.IRON);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_IRON_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.IRON);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_IRON_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.IRON);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_COPPER_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.COPPER);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_COPPER_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.COPPER);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_COPPER_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.COPPER);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_COPPER_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.COPPER);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_SILVER_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.SILVER);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_SILVER_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.SILVER);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_SILVER_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.SILVER);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_SILVER_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.SILVER);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_GOLD_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.GOLD);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_GOLD_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.GOLD);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_GOLD_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.GOLD);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_GOLD_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.GOLD);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DIAMOND_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DIAMOND);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DIAMOND_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DIAMOND);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DIAMOND_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DIAMOND);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DIAMOND_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DIAMOND);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_NETHERITE_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.NETHERITE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_NETHERITE_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.NETHERITE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_NETHERITE_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.NETHERITE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_NETHERITE_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.NETHERITE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_FIRE_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_FIRE_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_FIRE_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_FIRE_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DRAGON_STEEL_FIRE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_ICE_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_ICE_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_ICE_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_ICE_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DRAGON_STEEL_ICE);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_HEAD = buildDragonArmor(DragonArmorPart.HEAD, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_NECK = buildDragonArmor(DragonArmorPart.NECK, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_BODY = buildDragonArmor(DragonArmorPart.BODY, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final DeferredItem<DragonArmorItem> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_TAIL = buildDragonArmor(DragonArmorPart.TAIL, DragonArmorMaterial.DRAGON_STEEL_LIGHTNING);
    public static final DeferredItem<Item> DRAGON_MEAL = registerItem("dragon_meal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SICKLY_DRAGON_MEAL = registerItem("sickly_dragon_meal", () -> new GenericItem(1));
    public static final DeferredItem<Item> CREATIVE_DRAGON_MEAL = registerItem("creative_dragon_meal", () -> new GenericItem(2));
    public static final DeferredItem<Item> FIRE_DRAGON_FLESH = registerItem("fire_dragon_flesh", () -> new DragonFleshItem(IafDragonTypes.FIRE));
    public static final DeferredItem<Item> ICE_DRAGON_FLESH = registerItem("ice_dragon_flesh", () -> new DragonFleshItem(IafDragonTypes.ICE));
    public static final DeferredItem<Item> LIGHTNING_DRAGON_FLESH = registerItem("lightning_dragon_flesh", () -> new DragonFleshItem(IafDragonTypes.LIGHTNING));
    public static final DeferredItem<Item> FIRE_DRAGON_HEART = registerItem("fire_dragon_heart", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ICE_DRAGON_HEART = registerItem("ice_dragon_heart", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LIGHTNING_DRAGON_HEART = registerItem("lightning_dragon_heart", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FIRE_DRAGON_BLOOD = registerItem("fire_dragon_blood", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ICE_DRAGON_BLOOD = registerItem("ice_dragon_blood", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LIGHTNING_DRAGON_BLOOD = registerItem("lightning_dragon_blood", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DRAGON_STAFF = registerItem("dragon_stick", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> DRAGON_HORN = registerItem("dragon_horn", DragonHornItem::new);
    public static final DeferredItem<Item> DRAGON_FLUTE = registerItem("dragon_flute", DragonFluteItem::new);
    public static final DeferredItem<Item> SUMMONING_CRYSTAL_FIRE = registerItem("summoning_crystal_fire", SummoningCrystalItem::new);
    public static final DeferredItem<Item> SUMMONING_CRYSTAL_ICE = registerItem("summoning_crystal_ice", SummoningCrystalItem::new);
    public static final DeferredItem<Item> SUMMONING_CRYSTAL_LIGHTNING = registerItem("summoning_crystal_lightning", SummoningCrystalItem::new);
    public static final DeferredItem<Item> HIPPOGRYPH_EGG = registerItem("hippogryph_egg", HippogryphEggItem::new);
    public static final DeferredItem<Item> IRON_HIPPOGRYPH_ARMOR = registerItem("iron_hippogryph_armor", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> GOLD_HIPPOGRYPH_ARMOR = registerItem("gold_hippogryph_armor", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> DIAMOND_HIPPOGRYPH_ARMOR = registerItem("diamond_hippogryph_armor", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> NETHERITE_HIPPOGRYPH_ARMOR = registerItem("netherite_hippogryph_armor", () -> new Item(new Item.Properties().stacksTo(1).fireResistant()));
    public static final DeferredItem<Item> HIPPOGRYPH_TALON = registerItem("hippogryph_talon", () -> new GenericItem(1));
    public static final DeferredItem<Item> STONE_STATUE = registerItem("stone_statue", StoneStatueItem::new);
    public static final DeferredItem<Item> BLINDFOLD = registerItem("blindfold", BlindfoldItem::new);
    public static final DeferredItem<Item> PIXIE_DUST = registerItem("pixie_dust", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.3F).alwaysEdible().effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 100, 1), 1).effect(() -> new MobEffectInstance(MobEffects.GLOWING, 100, 1), 1).build())));
    public static final DeferredItem<Item> PIXIE_WINGS = registerItem("pixie_wings", () -> new GenericItem(1));
    public static final DeferredItem<Item> AMBROSIA = registerItem("ambrosia", () -> new Item(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F).alwaysEdible().usingConvertsTo(Items.BOWL).effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 2), 1).effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 3600, 2), 1).effect(() -> new MobEffectInstance(MobEffects.JUMP, 3600, 2), 1).effect(() -> new MobEffectInstance(MobEffects.LUCK, 3600, 2), 1).build())));
    public static final DeferredItem<Item> SHINY_SCALES = registerItem("shiny_scales", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SIREN_TEAR = registerItem("siren_tear", () -> new GenericItem(1));
    public static final DeferredItem<Item> HIPPOCAMPUS_FIN = registerItem("hippocampus_fin", () -> new GenericItem(1));
    public static final DeferredItem<Item> EARPLUGS = registerItem("earplugs", EarPlugsArmorItem::new);
    public static final DeferredItem<Item> DEATH_WORM_CHITIN_YELLOW = registerItem("deathworm_chitin_yellow", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DEATH_WORM_CHITIN_WHITE = registerItem("deathworm_chitin_white", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DEATH_WORM_CHITIN_RED = registerItem("deathworm_chitin_red", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DEATHWORM_EGG = registerItem("deathworm_egg", () -> new DeathwormEggItem(false));
    public static final DeferredItem<Item> DEATHWORM_EGG_GIGANTIC = registerItem("deathworm_egg_giant", () -> new DeathwormEggItem(true));
    public static final DeferredItem<Item> DEATHWORM_TOUNGE = registerItem("deathworm_tounge", () -> new GenericItem(1));
    public static final DeferredItem<Item> ROTTEN_EGG = registerItem("rotten_egg", RottenEggItem::new);
    public static final DeferredItem<Item> COCKATRICE_EYE = registerItem("cockatrice_eye", () -> new GenericItem(1));
    public static final DeferredItem<Item> STYMPHALIAN_BIRD_FEATHER = registerItem("stymphalian_bird_feather", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TROLL_TUSK = registerItem("troll_tusk", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> AMPHITHERE_FEATHER = registerItem("amphithere_feather", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> AMPHITHERE_ARROW = registerItem("amphithere_arrow", AmphithereArrowItem::new);
    public static final DeferredItem<Item> SERPENT_FANG = registerItem("sea_serpent_fang", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SEA_SERPENT_ARROW = registerItem("sea_serpent_arrow", SeaSerpentArrowItem::new);
    public static final DeferredItem<Item> CHAIN = registerItem("chain", () -> new ChainItem(false));
    public static final DeferredItem<Item> CHAIN_STICKY = registerItem("chain_sticky", () -> new ChainItem(true));
    public static final DeferredItem<Item> DRAGONSTEEL_FIRE_INGOT = registerItem("dragonsteel_fire_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DRAGONSTEEL_ICE_INGOT = registerItem("dragonsteel_ice_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DRAGONSTEEL_LIGHTNING_INGOT = registerItem("dragonsteel_lightning_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DREAD_SHARD = registerItem("dread_shard", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DREAD_KEY = registerItem("dread_key", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HYDRA_FANG = registerItem("hydra_fang", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HYDRA_HEART = registerItem("hydra_heart", HydraHeartItem::new);
    public static final DeferredItem<Item> HYDRA_ARROW = registerItem("hydra_arrow", HydraArrowItem::new);
    public static final DeferredItem<Item> CANNOLI = registerItem("cannoli", CannoliItem::new);
    public static final DeferredItem<Item> ECTOPLASM = registerItem("ectoplasm", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GHOST_INGOT = registerItem("ghost_ingot", () -> new GenericItem(1));
    public static final DeferredItem<Item> DRAGON_SEEKER = registerItem("dragon_seeker", () -> new DragonSeekerItem(DragonSeekerItem.SeekerType.NORMAL));
    public static final DeferredItem<Item> EPIC_DRAGON_SEEKER = registerItem("epic_dragon_seeker", () -> new DragonSeekerItem(DragonSeekerItem.SeekerType.EPIC));
    public static final DeferredItem<Item> LEGENDARY_DRAGON_SEEKER = registerItem("legendary_dragon_seeker", () -> new DragonSeekerItem(DragonSeekerItem.SeekerType.LEGENDARY));
    public static final DeferredItem<Item> GODLY_DRAGON_SEEKER = registerItem("godly_dragon_seeker", () -> new DragonSeekerItem(DragonSeekerItem.SeekerType.GODLY));
    public static final DeferredItem<BannerPatternItem> PATTERN_FIRE = registerItem("banner_pattern_fire", () -> new BannerPatternItem(IafBannerPatternTags.FIRE_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_ICE = registerItem("banner_pattern_ice", () -> new BannerPatternItem(IafBannerPatternTags.ICE_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_LIGHTNING = registerItem("banner_pattern_lightning", () -> new BannerPatternItem(IafBannerPatternTags.LIGHTNING_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_FIRE_HEAD = registerItem("banner_pattern_fire_head", () -> new BannerPatternItem(IafBannerPatternTags.FIRE_HEAD_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_ICE_HEAD = registerItem("banner_pattern_ice_head", () -> new BannerPatternItem(IafBannerPatternTags.ICE_HEAD_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_LIGHTNING_HEAD = registerItem("banner_pattern_lightning_head", () -> new BannerPatternItem(IafBannerPatternTags.LIGHTNING_HEAD_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_AMPHITHERE = registerItem("banner_pattern_amphithere", () -> new BannerPatternItem(IafBannerPatternTags.AMPHITHERE_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_BIRD = registerItem("banner_pattern_bird", () -> new BannerPatternItem(IafBannerPatternTags.BIRD_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_EYE = registerItem("banner_pattern_eye", () -> new BannerPatternItem(IafBannerPatternTags.EYE_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_FAE = registerItem("banner_pattern_fae", () -> new BannerPatternItem(IafBannerPatternTags.FAE_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_FEATHER = registerItem("banner_pattern_feather", () -> new BannerPatternItem(IafBannerPatternTags.FEATHER_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_GORGON = registerItem("banner_pattern_gorgon", () -> new BannerPatternItem(IafBannerPatternTags.GORGON_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_HIPPOCAMPUS = registerItem("banner_pattern_hippocampus", () -> new BannerPatternItem(IafBannerPatternTags.HIPPOCAMPUS_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_HIPPOGRYPH_HEAD = registerItem("banner_pattern_hippogryph_head", () -> new BannerPatternItem(IafBannerPatternTags.HIPPOGRYPH_HEAD_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_MERMAID = registerItem("banner_pattern_mermaid", () -> new BannerPatternItem(IafBannerPatternTags.MERMAID_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_SEA_SERPENT = registerItem("banner_pattern_sea_serpent", () -> new BannerPatternItem(IafBannerPatternTags.SEA_SERPENT_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_TROLL = registerItem("banner_pattern_troll", () -> new BannerPatternItem(IafBannerPatternTags.TROLL_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_WEEZER = registerItem("banner_pattern_weezer", () -> new BannerPatternItem(IafBannerPatternTags.WEEZER_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BannerPatternItem> PATTERN_DREAD = registerItem("banner_pattern_dread", () -> new BannerPatternItem(IafBannerPatternTags.DREAD_BANNER_PATTERN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<DelightFoodItem> COOKED_RICE_WITH_FIRE_DRAGON_MEAT = registerItem("cooked_rice_with_fire_dragon_meat", () -> new DelightFoodItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.6f).usingConvertsTo(Items.BOWL).effect(() -> new MobEffectInstance(MobEffects.SATURATION, 20 * 5), 1).effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20 * 60 * 2), 1).build())));
    public static final DeferredItem<DelightFoodItem> COOKED_RICE_WITH_ICE_DRAGON_MEAT = registerItem("cooked_rice_with_ice_dragon_meat", () -> new DelightFoodItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.6f).usingConvertsTo(Items.BOWL).effect(() -> new MobEffectInstance(MobEffects.SATURATION, 20 * 5), 1).effect(() -> new MobEffectInstance(MobEffects.JUMP, 20 * 60 * 2, 2), 1).build())));
    public static final DeferredItem<DelightFoodItem> COOKED_RICE_WITH_LIGHTNING_DRAGON_MEAT = registerItem("cooked_rice_with_lightning_dragon_meat", () -> new DelightFoodItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.6f).usingConvertsTo(Items.BOWL).effect(() -> new MobEffectInstance(MobEffects.SATURATION, 20 * 5), 1).effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 60 * 2, 2), 1).build())));
    public static final DeferredItem<DelightFoodItem> GHOST_CREAM = registerItem("ghost_cream", () -> new DelightFoodItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().fast().nutrition(4).saturationModifier(0.6f).usingConvertsTo(Items.GLASS_BOTTLE).effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 20 * 20), 1).build())));
    public static final DeferredItem<DelightFoodItem> PIXIE_DUST_MILKY_TEA = registerItem("pixie_dust_milky_tea", () -> new DelightFoodItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().fast().nutrition(4).saturationModifier(0.6f).usingConvertsTo(Items.GLASS_BOTTLE).effect(() -> new MobEffectInstance(MobEffects.INVISIBILITY, 20 * 60 * 2), 1).build())));

    //spawn Eggs
    static {
        registerItem("spawn_egg_fire_dragon", () -> new DeferredSpawnEggItem(IafEntities.FIRE_DRAGON, 0X340000, 0XA52929, new Item.Properties()));
        registerItem("spawn_egg_ice_dragon", () -> new DeferredSpawnEggItem(IafEntities.ICE_DRAGON, 0XB5DDFB, 0X7EBAF0, new Item.Properties()));
        registerItem("spawn_egg_lightning_dragon", () -> new DeferredSpawnEggItem(IafEntities.LIGHTNING_DRAGON, 0X422367, 0X725691, new Item.Properties()));
        registerItem("spawn_egg_hippogryph", () -> new DeferredSpawnEggItem(IafEntities.HIPPOGRYPH, 0XD8D8D8, 0XD1B55D, new Item.Properties()));
        registerItem("spawn_egg_gorgon", () -> new DeferredSpawnEggItem(IafEntities.GORGON, 0XD0D99F, 0X684530, new Item.Properties()));
        registerItem("spawn_egg_pixie", () -> new DeferredSpawnEggItem(IafEntities.PIXIE, 0XFF7F89, 0XE2CCE2, new Item.Properties()));
        registerItem("spawn_egg_cyclops", () -> new DeferredSpawnEggItem(IafEntities.CYCLOPS, 0XB0826E, 0X3A1F0F, new Item.Properties()));
        registerItem("spawn_egg_siren", () -> new DeferredSpawnEggItem(IafEntities.SIREN, 0X8EE6CA, 0XF2DFC8, new Item.Properties()));
        registerItem("spawn_egg_hippocampus", () -> new DeferredSpawnEggItem(IafEntities.HIPPOCAMPUS, 0X4491C7, 0X4FC56B, new Item.Properties()));
        registerItem("spawn_egg_death_worm", () -> new DeferredSpawnEggItem(IafEntities.DEATH_WORM, 0XD1CDA3, 0X423A3A, new Item.Properties()));
        registerItem("spawn_egg_cockatrice", () -> new DeferredSpawnEggItem(IafEntities.COCKATRICE, 0X8F5005, 0X4F5A23, new Item.Properties()));
        registerItem("spawn_egg_stymphalian_bird", () -> new DeferredSpawnEggItem(IafEntities.STYMPHALIAN_BIRD, 0X744F37, 0X9E6C4B, new Item.Properties()));
        registerItem("spawn_egg_troll", () -> new DeferredSpawnEggItem(IafEntities.TROLL, 0X3D413D, 0X58433A, new Item.Properties()));
        registerItem("spawn_egg_amphithere", () -> new DeferredSpawnEggItem(IafEntities.AMPHITHERE, 0X597535, 0X00AA98, new Item.Properties()));
        registerItem("spawn_egg_sea_serpent", () -> new DeferredSpawnEggItem(IafEntities.SEA_SERPENT, 0X008299, 0XC5E6E7, new Item.Properties()));
        registerItem("spawn_egg_dread_thrall", () -> new DeferredSpawnEggItem(IafEntities.DREAD_THRALL, 0XE0E6E6, 0X00FFFF, new Item.Properties()));
        registerItem("spawn_egg_dread_ghoul", () -> new DeferredSpawnEggItem(IafEntities.DREAD_GHOUL, 0XE0E6E6, 0X7B838A, new Item.Properties()));
        registerItem("spawn_egg_dread_beast", () -> new DeferredSpawnEggItem(IafEntities.DREAD_BEAST, 0XE0E6E6, 0X38373C, new Item.Properties()));
        registerItem("spawn_egg_dread_scuttler", () -> new DeferredSpawnEggItem(IafEntities.DREAD_SCUTTLER, 0XE0E6E6, 0X4D5667, new Item.Properties()));
        registerItem("spawn_egg_lich", () -> new DeferredSpawnEggItem(IafEntities.DREAD_LICH, 0XE0E6E6, 0X274860, new Item.Properties()));
        registerItem("spawn_egg_dread_knight", () -> new DeferredSpawnEggItem(IafEntities.DREAD_KNIGHT, 0XE0E6E6, 0X4A6C6E, new Item.Properties()));
        registerItem("spawn_egg_dread_horse", () -> new DeferredSpawnEggItem(IafEntities.DREAD_HORSE, 0XE0E6E6, 0XACACAC, new Item.Properties()));
        registerItem("spawn_egg_hydra", () -> new DeferredSpawnEggItem(IafEntities.HYDRA, 0X8B8B78, 0X2E372B, new Item.Properties()));
        registerItem("spawn_egg_ghost", () -> new DeferredSpawnEggItem(IafEntities.GHOST, 0XB9EDB8, 0X73B276, new Item.Properties()));
    }

    //Hidden
    public static final DeferredItem<Item> TIDE_TRIDENT_INVENTORY = register("tide_trident_inventory", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WEEZER_BLUE_ALBUM = register("weezer_blue_album", () -> new GenericItem(1));
    public static final DeferredItem<Item> DRAGON_DEBUG_STICK = register("dragon_debug_stick", () -> new GenericItem(1));

    //Armors
    public static final DeferredItem<Item> SILVER_HELMET = registerArmor("armor_silver_metal_helmet", () -> new ArmorItem(IafArmorMaterials.SILVER, ArmorItem.Type.HELMET, new Item.Properties().durability(165)));
    public static final DeferredItem<Item> SILVER_CHESTPLATE = registerArmor("armor_silver_metal_chestplate", () -> new ArmorItem(IafArmorMaterials.SILVER, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(240)));
    public static final DeferredItem<Item> SILVER_LEGGINGS = registerArmor("armor_silver_metal_leggings", () -> new ArmorItem(IafArmorMaterials.SILVER, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(225)));
    public static final DeferredItem<Item> SILVER_BOOTS = registerArmor("armor_silver_metal_boots", () -> new ArmorItem(IafArmorMaterials.SILVER, ArmorItem.Type.BOOTS, new Item.Properties().durability(195)));
    public static final DeferredItem<Item> COPPER_HELMET = registerArmor("armor_copper_metal_helmet", () -> new ArmorItem(IafArmorMaterials.COPPER, ArmorItem.Type.HELMET, new Item.Properties().durability(111)));
    public static final DeferredItem<Item> COPPER_CHESTPLATE = registerArmor("armor_copper_metal_chestplate", () -> new ArmorItem(IafArmorMaterials.COPPER, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(161)));
    public static final DeferredItem<Item> COPPER_LEGGINGS = registerArmor("armor_copper_metal_leggings", () -> new ArmorItem(IafArmorMaterials.COPPER, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(151)));
    public static final DeferredItem<Item> COPPER_BOOTS = registerArmor("armor_copper_metal_boots", () -> new ArmorItem(IafArmorMaterials.COPPER, ArmorItem.Type.BOOTS, new Item.Properties().durability(131)));
    public static final DeferredItem<Item> SHEEP_HELMET = registerArmor("sheep_helmet", () -> new ArmorItem(IafArmorMaterials.SHEEP, ArmorItem.Type.HELMET, new Item.Properties().durability(55)));
    public static final DeferredItem<Item> SHEEP_CHESTPLATE = registerArmor("sheep_chestplate", () -> new ArmorItem(IafArmorMaterials.SHEEP, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(80)));
    public static final DeferredItem<Item> SHEEP_LEGGINGS = registerArmor("sheep_leggings", () -> new ArmorItem(IafArmorMaterials.SHEEP, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(75)));
    public static final DeferredItem<Item> SHEEP_BOOTS = registerArmor("sheep_boots", () -> new ArmorItem(IafArmorMaterials.SHEEP, ArmorItem.Type.BOOTS, new Item.Properties().durability(65)));
    public static final DeferredItem<Item> DEATHWORM_YELLOW_HELMET = registerArmor("deathworm_yellow_helmet", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_YELLOW, ArmorItem.Type.HELMET, new Item.Properties().durability(165)));
    public static final DeferredItem<Item> DEATHWORM_YELLOW_CHESTPLATE = registerArmor("deathworm_yellow_chestplate", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_YELLOW, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(240)));
    public static final DeferredItem<Item> DEATHWORM_YELLOW_LEGGINGS = registerArmor("deathworm_yellow_leggings", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_YELLOW, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(225)));
    public static final DeferredItem<Item> DEATHWORM_YELLOW_BOOTS = registerArmor("deathworm_yellow_boots", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_YELLOW, ArmorItem.Type.BOOTS, new Item.Properties().durability(195)));
    public static final DeferredItem<Item> DEATHWORM_WHITE_HELMET = registerArmor("deathworm_white_helmet", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_WHITE, ArmorItem.Type.HELMET, new Item.Properties().durability(165)));
    public static final DeferredItem<Item> DEATHWORM_WHITE_CHESTPLATE = registerArmor("deathworm_white_chestplate", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_WHITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(240)));
    public static final DeferredItem<Item> DEATHWORM_WHITE_LEGGINGS = registerArmor("deathworm_white_leggings", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_WHITE, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(225)));
    public static final DeferredItem<Item> DEATHWORM_WHITE_BOOTS = registerArmor("deathworm_white_boots", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_WHITE, ArmorItem.Type.BOOTS, new Item.Properties().durability(165)));
    public static final DeferredItem<Item> DEATHWORM_RED_HELMET = registerArmor("deathworm_red_helmet", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_RED, ArmorItem.Type.HELMET, new Item.Properties().durability(165)));
    public static final DeferredItem<Item> DEATHWORM_RED_CHESTPLATE = registerArmor("deathworm_red_chestplate", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_RED, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(240)));
    public static final DeferredItem<Item> DEATHWORM_RED_LEGGINGS = registerArmor("deathworm_red_leggings", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_RED, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(225)));
    public static final DeferredItem<Item> DEATHWORM_RED_BOOTS = registerArmor("deathworm_red_boots", () -> new ArmorItem(IafArmorMaterials.DEATHWORM_RED, ArmorItem.Type.BOOTS, new Item.Properties().durability(165)));
    public static final DeferredItem<Item> DRAGONSTEEL_FIRE_HELMET = registerArmor("dragonsteel_fire_helmet", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_FIRE, ArmorItem.Type.HELMET));
    public static final DeferredItem<Item> DRAGONSTEEL_FIRE_CHESTPLATE = registerArmor("dragonsteel_fire_chestplate", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_FIRE, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<Item> DRAGONSTEEL_FIRE_LEGGINGS = registerArmor("dragonsteel_fire_leggings", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_FIRE, ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<Item> DRAGONSTEEL_FIRE_BOOTS = registerArmor("dragonsteel_fire_boots", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_FIRE, ArmorItem.Type.BOOTS));
    public static final DeferredItem<Item> DRAGONSTEEL_ICE_HELMET = registerArmor("dragonsteel_ice_helmet", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_ICE, ArmorItem.Type.HELMET));
    public static final DeferredItem<Item> DRAGONSTEEL_ICE_CHESTPLATE = registerArmor("dragonsteel_ice_chestplate", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_ICE, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<Item> DRAGONSTEEL_ICE_LEGGINGS = registerArmor("dragonsteel_ice_leggings", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_ICE, ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<Item> DRAGONSTEEL_ICE_BOOTS = registerArmor("dragonsteel_ice_boots", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_ICE, ArmorItem.Type.BOOTS));
    public static final DeferredItem<Item> DRAGONSTEEL_LIGHTNING_HELMET = registerArmor("dragonsteel_lightning_helmet", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_LIGHTNING, ArmorItem.Type.HELMET));
    public static final DeferredItem<Item> DRAGONSTEEL_LIGHTNING_CHESTPLATE = registerArmor("dragonsteel_lightning_chestplate", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_LIGHTNING, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<Item> DRAGONSTEEL_LIGHTNING_LEGGINGS = registerArmor("dragonsteel_lightning_leggings", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_LIGHTNING, ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<Item> DRAGONSTEEL_LIGHTNING_BOOTS = registerArmor("dragonsteel_lightning_boots", () -> new DragonSteelArmorItem(IafArmorMaterials.DRAGONSTEEL_LIGHTNING, ArmorItem.Type.BOOTS));

    //Tools&Weapons
    public static final DeferredItem<Item> SILVER_SWORD = registerToolOrWeapon("silver_sword", () -> new ActivePostHitSwordItem(IafTiers.SILVER_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.SILVER_TOOL_MATERIAL, 3.0F, -2.4F)), BuiltinAbilities.UNDEAD_DAMAGE_BONUS));
    public static final DeferredItem<Item> SILVER_SHOVEL = registerToolOrWeapon("silver_shovel", () -> new ActivePostHitShovelItem(IafTiers.SILVER_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.SILVER_TOOL_MATERIAL, 1.5F, -3.0F)), BuiltinAbilities.UNDEAD_DAMAGE_BONUS));
    public static final DeferredItem<Item> SILVER_PICKAXE = registerToolOrWeapon("silver_pickaxe", () -> new ActivePostHitPickaxeItem(IafTiers.SILVER_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.SILVER_TOOL_MATERIAL, 1.0F, -2.8F)), BuiltinAbilities.UNDEAD_DAMAGE_BONUS));
    public static final DeferredItem<Item> SILVER_AXE = registerToolOrWeapon("silver_axe", () -> new ActivePostHitAxeItem(IafTiers.SILVER_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.SILVER_TOOL_MATERIAL, 6.0F, -3.0F)), BuiltinAbilities.UNDEAD_DAMAGE_BONUS));
    public static final DeferredItem<Item> SILVER_HOE = registerToolOrWeapon("silver_hoe", () -> new ActivePostHitHoeItem(IafTiers.SILVER_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.SILVER_TOOL_MATERIAL, 0.0F, -3.0F)), BuiltinAbilities.UNDEAD_DAMAGE_BONUS));
    public static final DeferredItem<Item> COPPER_SWORD = registerToolOrWeapon("copper_sword", () -> new SwordItem(IafTiers.COPPER_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.COPPER_TOOL_MATERIAL, 3.0F, -2.4F))));
    public static final DeferredItem<Item> COPPER_SHOVEL = registerToolOrWeapon("copper_shovel", () -> new ShovelItem(IafTiers.COPPER_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.COPPER_TOOL_MATERIAL, 1.5F, -2.4F))));
    public static final DeferredItem<Item> COPPER_PICKAXE = registerToolOrWeapon("copper_pickaxe", () -> new PickaxeItem(IafTiers.COPPER_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.COPPER_TOOL_MATERIAL, 1.0F, -2.8F))));
    public static final DeferredItem<Item> COPPER_AXE = registerToolOrWeapon("copper_axe", () -> new AxeItem(IafTiers.COPPER_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.COPPER_TOOL_MATERIAL, 6.0F, -3.0F))));
    public static final DeferredItem<Item> COPPER_HOE = registerToolOrWeapon("copper_hoe", () -> new HoeItem(IafTiers.COPPER_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.COPPER_TOOL_MATERIAL, 0.0F, -1.0F))));
    public static final DeferredItem<Item> FISHING_SPEAR = registerToolOrWeapon("fishing_spear", () -> new Item(new Item.Properties().durability(64)));
    public static final DeferredItem<Item> DRAGONBONE_SWORD = registerToolOrWeapon("dragonbone_sword", () -> new SwordItem(IafTiers.DRAGONBONE_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONBONE_TOOL_MATERIAL, 3.0F, -2.4F))));
    public static final DeferredItem<Item> DRAGONBONE_SHOVEL = registerToolOrWeapon("dragonbone_shovel", () -> new ShovelItem(IafTiers.DRAGONBONE_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONBONE_TOOL_MATERIAL, 1.5F, -2.8F))));
    public static final DeferredItem<Item> DRAGONBONE_PICKAXE = registerToolOrWeapon("dragonbone_pickaxe", () -> new PickaxeItem(IafTiers.DRAGONBONE_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONBONE_TOOL_MATERIAL, 1.0F, -2.8F))));
    public static final DeferredItem<Item> DRAGONBONE_AXE = registerToolOrWeapon("dragonbone_axe", () -> new AxeItem(IafTiers.DRAGONBONE_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONBONE_TOOL_MATERIAL, 5.0F, -3.0F))));
    public static final DeferredItem<Item> DRAGONBONE_HOE = registerToolOrWeapon("dragonbone_hoe", () -> new HoeItem(IafTiers.DRAGONBONE_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONBONE_TOOL_MATERIAL, -4.0F, 0.0F))));
    public static final DeferredItem<Item> DRAGONBONE_ARROW = registerToolOrWeapon("dragonbone_arrow", DragonArrowItem::new);
    public static final DeferredItem<Item> DRAGON_BOW = registerToolOrWeapon("dragonbone_bow", DragonBowItem::new);
    public static final DeferredItem<Item> STYMPHALIAN_ARROW = registerToolOrWeapon("stymphalian_arrow", StymphalianArrowItem::new);
    public static final DeferredItem<Item> DRAGONSTEEL_FIRE_SWORD = registerToolOrWeapon("dragonsteel_fire_sword", () -> new ActivePostHitSwordItem(IafTiers.DRAGONSTEEL_FIRE, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_FIRE, 3.0F, -2.4F)), BuiltinAbilities.DRAGONSTEEL_FIRE_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_FIRE_PICKAXE = registerToolOrWeapon("dragonsteel_fire_pickaxe", () -> new ActivePostHitPickaxeItem(IafTiers.DRAGONSTEEL_FIRE, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_FIRE, 1.0F, -2.8F)), BuiltinAbilities.DRAGONSTEEL_FIRE_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_FIRE_AXE = registerToolOrWeapon("dragonsteel_fire_axe", () -> new ActivePostHitAxeItem(IafTiers.DRAGONSTEEL_FIRE, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_FIRE, 5.0F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_FIRE_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_FIRE_SHOVEL = registerToolOrWeapon("dragonsteel_fire_shovel", () -> new ActivePostHitShovelItem(IafTiers.DRAGONSTEEL_FIRE, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_FIRE, 1.5F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_FIRE_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_FIRE_HOE = registerToolOrWeapon("dragonsteel_fire_hoe", () -> new ActivePostHitHoeItem(IafTiers.DRAGONSTEEL_FIRE, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_FIRE, -4.0F, 0.0F)), BuiltinAbilities.DRAGONSTEEL_FIRE_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_ICE_SWORD = registerToolOrWeapon("dragonsteel_ice_sword", () -> new ActivePostHitSwordItem(IafTiers.DRAGONSTEEL_ICE, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_ICE, 3.0F, -2.4F)), BuiltinAbilities.DRAGONSTEEL_ICE_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_ICE_PICKAXE = registerToolOrWeapon("dragonsteel_ice_pickaxe", () -> new ActivePostHitPickaxeItem(IafTiers.DRAGONSTEEL_ICE, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_ICE, 1.0F, -2.8F)), BuiltinAbilities.DRAGONSTEEL_ICE_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_ICE_AXE = registerToolOrWeapon("dragonsteel_ice_axe", () -> new ActivePostHitAxeItem(IafTiers.DRAGONSTEEL_ICE, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_ICE, 5.0F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_ICE_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_ICE_SHOVEL = registerToolOrWeapon("dragonsteel_ice_shovel", () -> new ActivePostHitShovelItem(IafTiers.DRAGONSTEEL_ICE, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_ICE, 1.5F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_ICE_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_ICE_HOE = registerToolOrWeapon("dragonsteel_ice_hoe", () -> new ActivePostHitHoeItem(IafTiers.DRAGONSTEEL_ICE, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_ICE, -4.0F, 0.0F)), BuiltinAbilities.DRAGONSTEEL_ICE_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_LIGHTNING_SWORD = registerToolOrWeapon("dragonsteel_lightning_sword", () -> new ActivePostHitSwordItem(IafTiers.DRAGONSTEEL_LIGHTNING, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_LIGHTNING, 3.0F, -2.4F)), BuiltinAbilities.DRAGONSTEEL_LIGHTNING_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_LIGHTNING_PICKAXE = registerToolOrWeapon("dragonsteel_lightning_pickaxe", () -> new ActivePostHitPickaxeItem(IafTiers.DRAGONSTEEL_LIGHTNING, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_LIGHTNING, 1.0F, -2.8F)), BuiltinAbilities.DRAGONSTEEL_LIGHTNING_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_LIGHTNING_AXE = registerToolOrWeapon("dragonsteel_lightning_axe", () -> new ActivePostHitAxeItem(IafTiers.DRAGONSTEEL_LIGHTNING, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_LIGHTNING, 5.0F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_LIGHTNING_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_LIGHTNING_SHOVEL = registerToolOrWeapon("dragonsteel_lightning_shovel", () -> new ActivePostHitShovelItem(IafTiers.DRAGONSTEEL_LIGHTNING, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_LIGHTNING, 1.5F, -3.0F)), BuiltinAbilities.DRAGONSTEEL_LIGHTNING_TOOL));
    public static final DeferredItem<Item> DRAGONSTEEL_LIGHTNING_HOE = registerToolOrWeapon("dragonsteel_lightning_hoe", () -> new ActivePostHitHoeItem(IafTiers.DRAGONSTEEL_LIGHTNING, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DRAGONSTEEL_LIGHTNING, -4.0F, 0.0F)), BuiltinAbilities.DRAGONSTEEL_LIGHTNING_TOOL));
    public static final DeferredItem<Item> DREAD_SWORD = registerToolOrWeapon("dread_sword", () -> new SwordItem(IafTiers.DREAD_SWORD_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DREAD_SWORD_TOOL_MATERIAL, 3.0F, -2.4F))));
    public static final DeferredItem<Item> DREAD_KNIGHT_SWORD = registerToolOrWeapon("dread_knight_sword", () -> new SwordItem(IafTiers.DREAD_KNIGHT_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DREAD_KNIGHT_TOOL_MATERIAL, 3.0F, -2.4F))));
    public static final DeferredItem<Item> LICH_STAFF = registerToolOrWeapon("lich_staff", LichStaffItem::new);
    public static final DeferredItem<Item> DREAD_QUEEN_SWORD = registerToolOrWeapon("dread_queen_sword", () -> new SwordItem(IafTiers.DREAD_QUEEN, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.DREAD_QUEEN, 3.0F, -2.4F))));
    public static final DeferredItem<Item> DREAD_QUEEN_STAFF = registerToolOrWeapon("dread_queen_staff", DreadQueenStaffItem::new);
    //--Legendary
    public static final DeferredItem<Item> DRAGONBONE_SWORD_FIRE = registerToolOrWeapon("dragonbone_sword_fire", () -> new ActivePostHitSwordItem(IafTiers.BLOODED_DRAGONBONE_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.BLOODED_DRAGONBONE_TOOL_MATERIAL, 3.0F, -2.4F)), BuiltinAbilities.FIRE_DRAGON_BLOOD_TOOL));
    public static final DeferredItem<Item> DRAGONBONE_SWORD_ICE = registerToolOrWeapon("dragonbone_sword_ice", () -> new ActivePostHitSwordItem(IafTiers.BLOODED_DRAGONBONE_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.BLOODED_DRAGONBONE_TOOL_MATERIAL, 3.0F, -2.4F)), BuiltinAbilities.ICE_DRAGON_BLOOD_TOOL));
    public static final DeferredItem<Item> DRAGONBONE_SWORD_LIGHTNING = registerToolOrWeapon("dragonbone_sword_lightning", () -> new ActivePostHitSwordItem(IafTiers.BLOODED_DRAGONBONE_TOOL_MATERIAL, new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(IafTiers.BLOODED_DRAGONBONE_TOOL_MATERIAL, 3.0F, -2.4F)), BuiltinAbilities.LIGHTNING_DRAGON_BLOOD_TOOL));
    public static final DeferredItem<Item> HIPPOGRYPH_SWORD = registerToolOrWeapon("hippogryph_sword", HippogryphSwordItem::new);
    public static final DeferredItem<Item> GORGON_HEAD = registerToolOrWeapon("gorgon_head", GorgonHeadItem::new);
    public static final DeferredItem<Item> PIXIE_WAND = registerToolOrWeapon("pixie_wand", PixieWandItem::new);
    public static final DeferredItem<Item> CYCLOPS_EYE = registerToolOrWeapon("cyclops_eye", CyclopsEyeItem::new);
    public static final DeferredItem<Item> SIREN_FLUTE = registerToolOrWeapon("siren_flute", SirenFluteItem::new);
    public static final DeferredItem<Item> HIPPOCAMPUS_SLAPPER = registerToolOrWeapon("hippocampus_slapper", HippocampusSlapperItem::new);
    public static final DeferredItem<Item> DEATHWORM_GAUNTLET_YELLOW = registerToolOrWeapon("deathworm_gauntlet_yellow", DeathwormGauntletItem::new);
    public static final DeferredItem<Item> DEATHWORM_GAUNTLET_WHITE = registerToolOrWeapon("deathworm_gauntlet_white", DeathwormGauntletItem::new);
    public static final DeferredItem<Item> DEATHWORM_GAUNTLET_RED = registerToolOrWeapon("deathworm_gauntlet_red", DeathwormGauntletItem::new);
    public static final DeferredItem<Item> COCKATRICE_SCEPTER = registerToolOrWeapon("cockatrice_scepter", CockatriceScepterItem::new);
    public static final DeferredItem<Item> STYMPHALIAN_FEATHER_BUNDLE = registerToolOrWeapon("stymphalian_feather_bundle", StymphalianFeatherBundleItem::new);
    public static final DeferredItem<Item> STYMPHALIAN_DAGGER = registerToolOrWeapon("stymphalian_bird_dagger", StymphalianDaggerItem::new);
    public static final DeferredItem<Item> AMPHITHERE_MACUAHUITL = registerToolOrWeapon("amphithere_macuahuitl", AmphithereMacuahuitlItem::new);
    public static final DeferredItem<Item> TIDE_TRIDENT = registerToolOrWeapon("tide_trident", TideTridentItem::new);
    public static final DeferredItem<Item> GHOST_SWORD = registerToolOrWeapon("ghost_sword", GhostSwordItem::new);

    public static DeferredItem<DragonArmorItem> buildDragonArmor(DragonArmorPart type, DragonArmorMaterial material) {
        return registerItem(String.format("dragonarmor_%s_%s", material.name(), type.getId()), () -> new DragonArmorItem(material, type));
    }

    public static <T extends Item> DeferredItem<T> registerBlock(String name, Supplier<T> item) {
        DeferredItem<T> r = register(name, item);
        IafCreativeModeTabs.BLOCKS_LIST.add(r);
        return r;
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item) {
        DeferredItem<T> r = register(name, item);
        IafCreativeModeTabs.ITEMS_LIST.add(r);
        return r;
    }

    public static <T extends Item> DeferredItem<T> registerToolOrWeapon(String name, Supplier<T> item) {
        DeferredItem<T> r = register(name, item);
        IafCreativeModeTabs.TOOLS_WEAPONS_LIST.add(r);
        return r;
    }

    public static <T extends Item> DeferredItem<T> registerArmor(String name, Supplier<T> item) {
        DeferredItem<T> r = register(name, item);
        IafCreativeModeTabs.ARMORS_LIST.add(r);
        return r;
    }

    static <T extends Item> DeferredItem<T> register(String name, Supplier<T> item) {
        return REGISTRY.register(name, item);
    }
}
