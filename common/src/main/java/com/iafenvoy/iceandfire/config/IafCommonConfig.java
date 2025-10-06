package com.iafenvoy.iceandfire.config;

import com.google.gson.JsonObject;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.config.entry.DoubleEntry;
import com.iafenvoy.jupiter.config.entry.IntegerEntry;
import com.iafenvoy.jupiter.config.entry.SeparatorEntry;
import com.iafenvoy.jupiter.interfaces.IConfigEntry;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IafCommonConfig extends AutoInitConfigContainer {
    public static final IafCommonConfig INSTANCE = new IafCommonConfig();
    public static final int CURRENT_VERSION = 3;
    public static final String backupPath = "./config/iceandfire/";

    public final DragonConfig dragon = new DragonConfig();
    public final HippogryphsConfig hippogryphs = new HippogryphsConfig();
    public final PixieConfig pixie = new PixieConfig();
    public final CyclopsConfig cyclops = new CyclopsConfig();
    public final SirenConfig siren = new SirenConfig();
    public final GorgonConfig gorgon = new GorgonConfig();
    public final DeathwormConfig deathworm = new DeathwormConfig();
    public final CockatriceConfig cockatrice = new CockatriceConfig();
    public final StymphalianBirdConfig stymphalianBird = new StymphalianBirdConfig();
    public final TrollConfig troll = new TrollConfig();
    public final AmphithereConfig amphithere = new AmphithereConfig();
    public final SeaSerpentConfig seaSerpent = new SeaSerpentConfig();
    public final LichConfig lich = new LichConfig();
    public final HydraConfig hydra = new HydraConfig();
    public final HippocampusConfig hippocampus = new HippocampusConfig();
    public final GhostConfig ghost = new GhostConfig();
    public final ToolsConfig tools = new ToolsConfig();
    public final ArmorsConfig armors = new ArmorsConfig();
    public final WorldGenConfig worldGen = new WorldGenConfig();
    public final Misc misc = new Misc();

    public IafCommonConfig() {
        super(Identifier.of(IceAndFire.MOD_ID, "config.iceandfire.common"), "screen.iceandfire.common.title", "./config/iceandfire/iaf-common.json");
    }

    @Override
    protected boolean shouldLoad(JsonObject obj) {
        if (!obj.has("version")) return true;
        int version = obj.get("version").getAsInt();
        if (version != CURRENT_VERSION) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                FileUtils.copyFile(new File(this.path), new File(IafCommonConfig.backupPath + "iceandfire_common_" + sdf.format(new Date()) + ".json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.save();
            IceAndFire.LOGGER.info("Wrong common config version {} for mod {}! Automatically use version {} and backup old one.", version, IceAndFire.MOD_NAME, CURRENT_VERSION);
            return false;
        } else IceAndFire.LOGGER.info("{} common config version match.", IceAndFire.MOD_NAME);
        return true;
    }

    @Override
    protected void writeCustomData(JsonObject obj) {
        obj.addProperty("version", CURRENT_VERSION);
    }

    @SuppressWarnings("unused")
    public static class DragonConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("config.iceandfire.dragon.maxHealth", 500, 1, Integer.MAX_VALUE).json("maxHealth");
        public final IConfigEntry<Double> netherDragonMaxHealth = new DoubleEntry("config.iceandfire.dragon.netherDragonMaxHealth", 750, 1, Integer.MAX_VALUE).json("netherDragonMaxHealth");
        public final IConfigEntry<Integer> eggBornTime = new IntegerEntry("config.iceandfire.dragon.eggBornTime", 7200, 0, Integer.MAX_VALUE).json("eggBornTime");
        public final IConfigEntry<Integer> maxPathingNodes = new IntegerEntry("config.iceandfire.dragon.maxPathingNodes", 5000, 0, Integer.MAX_VALUE).json("maxPathingNodes");
        public final IConfigEntry<Boolean> villagersFear = new BooleanEntry("config.iceandfire.dragon.villagersFear", true).json("villagersFear");
        public final IConfigEntry<Boolean> animalsFear = new BooleanEntry("config.iceandfire.dragon.animalsFear", true).json("animalsFear");
        public final SeparatorEntry s1 = new SeparatorEntry();
        public final IConfigEntry<Boolean> generateSkeletons = new BooleanEntry("config.iceandfire.dragon.generate.skeletons", true).json("generate.skeletons");
        public final IConfigEntry<Double> generateSkeletonChance = new DoubleEntry("config.iceandfire.dragon.generate.skeletonChance", 1.0 / 300, 0, 1).json("generate.skeletonChance");
        public final IConfigEntry<Double> generateDenGoldChance = new DoubleEntry("config.iceandfire.dragon.generate.denGoldAmount", 1.0 / 4, 0, 1).json("generate.denGoldAmount");
        public final IConfigEntry<Double> generateOreRatio = new DoubleEntry("config.iceandfire.dragon.generate.oreRatio", 1.0 / 45, 0, 1).json("generate.oreRatio");
        public final SeparatorEntry s2 = new SeparatorEntry();
        public final IConfigEntry<Boolean> griefing = new BooleanEntry("config.iceandfire.dragon.griefing", true).json("griefing");
        public final IConfigEntry<Boolean> tamedGriefing = new BooleanEntry("config.iceandfire.dragon.tamedGriefing", true).json("tamedGriefing");
        public final IConfigEntry<Integer> flapNoiseDistance = new IntegerEntry("config.iceandfire.dragon.flapNoiseDistance", 4, 0, 32).json("flapNoiseDistance");
        public final IConfigEntry<Integer> fluteDistance = new IntegerEntry("config.iceandfire.dragon.fluteDistance", 8, 0, 512).json("fluteDistance");
        public final IConfigEntry<Integer> attackDamage = new IntegerEntry("config.iceandfire.dragon.attackDamage", 17, 0, Integer.MAX_VALUE).json("attackDamage");
        public final IConfigEntry<Double> attackDamageFire = new DoubleEntry("config.iceandfire.dragon.attackDamageFire", 2, 0, Integer.MAX_VALUE).json("attackDamageFire");
        public final IConfigEntry<Double> attackDamageIce = new DoubleEntry("config.iceandfire.dragon.attackDamageIce", 2.5, 0, Integer.MAX_VALUE).json("attackDamageIce");
        public final IConfigEntry<Double> attackDamageLightning = new DoubleEntry("config.iceandfire.dragon.attackDamageLightning", 3.5, 0, Integer.MAX_VALUE).json("attackDamageLightning");
        public final IConfigEntry<Double> netherDragonAttackDamage = new DoubleEntry("config.iceandfire.dragon.netherDragonAttackDamage", 4.0, 0, Integer.MAX_VALUE).json("netherDragonAttackDamage");
        public final IConfigEntry<Integer> maxFlight = new IntegerEntry("config.iceandfire.dragon.maxFlight", 256, -2032, 2032).json("maxFlight");
        public final IConfigEntry<Integer> goldSearchLength = new IntegerEntry("config.iceandfire.dragon.goldSearchLength", 30, 0, 256).json("goldSearchLength");
        public final IConfigEntry<Boolean> canHealFromBiting = new BooleanEntry("config.iceandfire.dragon.canHealFromBiting", false).json("canHealFromBiting");
        public final IConfigEntry<Boolean> canDespawn = new BooleanEntry("config.iceandfire.dragon.canDespawn", true).json("canDespawn");
        public final IConfigEntry<Boolean> sleep = new BooleanEntry("config.iceandfire.dragon.sleep", true).json("sleep");
        public final IConfigEntry<Boolean> digWhenStuck = new BooleanEntry("config.iceandfire.dragon.digWhenStuck", true).json("digWhenStuck");
        public final IConfigEntry<Integer> breakBlockCooldown = new IntegerEntry("config.iceandfire.dragon.breakBlockCooldown", 5, 0, Integer.MAX_VALUE).json("breakBlockCooldown");
        public final IConfigEntry<Integer> targetSearchLength = new IntegerEntry("config.iceandfire.dragon.targetSearchLength", 128, 0, 1024).json("targetSearchLength");
        public final IConfigEntry<Integer> wanderFromHomeDistance = new IntegerEntry("config.iceandfire.dragon.wanderFromHomeDistance", 40, 0, 1024).json("wanderFromHomeDistance");
        public final IConfigEntry<Integer> hungerTickRate = new IntegerEntry("config.iceandfire.dragon.hungerTickRate", 3000, 1, Integer.MAX_VALUE).json("hungerTickRate");
        public final IConfigEntry<Double> blockBreakingDropChance = new DoubleEntry("config.iceandfire.dragon.blockBreakingDropChance", 0.1, 0, 1).json("blockBreakingDropChance");
        public final IConfigEntry<Boolean> explosiveBreath = new BooleanEntry("config.iceandfire.dragon.explosiveBreath", false).json("explosiveBreath");
        public final IConfigEntry<Boolean> chunkLoadSummonCrystal = new BooleanEntry("config.iceandfire.dragon.chunkLoadSummonCrystal", true).json("chunkLoadSummonCrystal");
        public final IConfigEntry<Double> dragonFlightSpeedMod = new DoubleEntry("config.iceandfire.dragon.dragonFlightSpeedMod", 1, 0.0001, 50).json("dragonFlightSpeedMod");
        public final IConfigEntry<Integer> maxTamedDragonAge = new IntegerEntry("config.iceandfire.dragon.maxTamedDragonAge", 128, 0, 128).json("maxTamedDragonAge");
        public final IConfigEntry<Double> maxBreathTimeMul = new DoubleEntry("config.iceandfire.dragon.maxBreathTimeMul", 2, 0, Integer.MAX_VALUE).json("maxBreathTimeMul");
        public final IConfigEntry<Boolean> neutralToPlayer = new BooleanEntry("config.iceandfire.dragon.neutralToPlayer", false).json("neutralToPlayer");
        public final IConfigEntry<Boolean> enableBrushDragonScales = new BooleanEntry("config.iceandfire.dragon.enableBrushDragonScales", true).json("enableBrushDragonScales");
        public final IConfigEntry<Integer> maxBrushScalesDropPerTime = new IntegerEntry("config.iceandfire.dragon.maxBrushScalesDropPerTime", 2, 1, Integer.MAX_VALUE).json("maxBrushScalesDropPerTime");
        public final IConfigEntry<Double> brushTimesMul = new DoubleEntry("config.iceandfire.dragon.brushTimesMul", 1, 0, Integer.MAX_VALUE).json("brushTimesMul");
        public final SeparatorEntry s3 = new SeparatorEntry();
        public final IConfigEntry<Boolean> lootSkull = new BooleanEntry("config.iceandfire.dragon.loot.skull", true).json("loot.skull");
        public final IConfigEntry<Boolean> lootHeart = new BooleanEntry("config.iceandfire.dragon.loot.heart", true).json("loot.heart");
        public final IConfigEntry<Boolean> lootBlood = new BooleanEntry("config.iceandfire.dragon.loot.blood", true).json("loot.blood");

        public DragonConfig() {
            super("dragon", "config.iceandfire.category.dragon");
        }
    }

    public static class HippogryphsConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> spawn = new BooleanEntry("config.iceandfire.hippogryphs.spawn", true).json("spawn");
        public final IConfigEntry<Integer> spawnWeight = new IntegerEntry("config.iceandfire.hippogryphs.spawnWeight", 2, 0, 20).json("spawnWeight");
        public final IConfigEntry<Double> fightSpeedMod = new DoubleEntry("config.iceandfire.hippogryphs.fightSpeedMod", 1, 0.0001, 50).json("fightSpeedMod");

        public HippogryphsConfig() {
            super("hippogryphs", "config.iceandfire.category.hippogryphs");
        }
    }

    public static class PixieConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Integer> size = new IntegerEntry("config.iceandfire.pixie.size", 5, 0, 100).json("size");
        public final IConfigEntry<Boolean> stealItems = new BooleanEntry("config.iceandfire.pixie.stealItems", false).json("stealItems");

        public PixieConfig() {
            super("pixie", "config.iceandfire.category.pixie");
        }
    }

    public static class CyclopsConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> spawnWanderingChance = new DoubleEntry("config.iceandfire.cyclops.spawnWanderingChance", 1.0 / 900, 0, 1).json("spawnWanderingChance");
        public final IConfigEntry<Integer> sheepSearchLength = new IntegerEntry("config.iceandfire.cyclops.sheepSearchLength", 17, 0, 1024).json("sheepSearchLength");
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("config.iceandfire.cyclops.maxHealth", 150, 1, Integer.MAX_VALUE).json("maxHealth");
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("config.iceandfire.cyclops.attackDamage", 15, 0, Integer.MAX_VALUE).json("attackDamage");
        public final IConfigEntry<Double> biteDamage = new DoubleEntry("config.iceandfire.cyclops.biteDamage", 40, 0, Integer.MAX_VALUE).json("biteDamage");
        public final IConfigEntry<Boolean> griefing = new BooleanEntry("config.iceandfire.cyclops.griefing", true).json("griefing");

        public CyclopsConfig() {
            super("cyclops", "config.iceandfire.category.cyclops");
        }
    }

    public static class SirenConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("config.iceandfire.siren.maxHealth", 50, 1, Integer.MAX_VALUE).json("maxHealth");
        public final IConfigEntry<Integer> maxSingTime = new IntegerEntry("config.iceandfire.siren.maxSingTime", 12000, 0, Integer.MAX_VALUE).json("maxSingTime");
        public final IConfigEntry<Integer> timeBetweenSongs = new IntegerEntry("config.iceandfire.siren.timeBetweenSongs", 2000, 0, Integer.MAX_VALUE).json("timeBetweenSongs");

        public SirenConfig() {
            super("siren", "config.iceandfire.category.siren");
        }
    }

    public static class GorgonConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("config.iceandfire.gorgon.maxHealth", 100, 1, Integer.MAX_VALUE).json("maxHealth");

        public GorgonConfig() {
            super("gorgon", "config.iceandfire.category.gorgon");
        }
    }

    public static class DeathwormConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> spawnChance = new DoubleEntry("config.iceandfire.deathworm.spawnChance", 1.0 / 30, 0, 1).json("spawnChance");
        public final IConfigEntry<Integer> targetSearchLength = new IntegerEntry("config.iceandfire.deathworm.targetSearchLength", 48, 0, 1024).json("targetSearchLength");
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("config.iceandfire.deathworm.maxHealth", 10, 1, Integer.MAX_VALUE).json("maxHealth");
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("config.iceandfire.deathworm.attackDamage", 3, 0, 30).json("attackDamage");
        public final IConfigEntry<Boolean> attackMonsters = new BooleanEntry("config.iceandfire.deathworm.attackMonsters", true).json("attackMonsters");

        public DeathwormConfig() {
            super("deathworm", "config.iceandfire.category.deathworm");
        }
    }

    public static class CockatriceConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> spawn = new BooleanEntry("config.iceandfire.cockatrice.spawn", true).json("spawn");
        public final IConfigEntry<Integer> spawnWeight = new IntegerEntry("config.iceandfire.cockatrice.spawnWeight", 4, 0, 20).json("spawnWeight");
        public final IConfigEntry<Integer> chickenSearchLength = new IntegerEntry("config.iceandfire.cockatrice.chickenSearchLength", 32, 0, 1024).json("chickenSearchLength");
        public final IConfigEntry<Double> eggChance = new DoubleEntry("config.iceandfire.cockatrice.eggChance", 1.0 / 30, 0, 1).json("eggChance");
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("config.iceandfire.cockatrice.maxHealth", 40, 1, Integer.MAX_VALUE).json("maxHealth");
        public final IConfigEntry<Boolean> chickensLayRottenEggs = new BooleanEntry("config.iceandfire.cockatrice.chickensLayRottenEggs", true).json("chickensLayRottenEggs");

        public CockatriceConfig() {
            super("cockatrice", "config.iceandfire.category.cockatrice");
        }
    }

    public static class StymphalianBirdConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> spawnChance = new DoubleEntry("config.iceandfire.bird.spawnChance", 1.0 / 80, 0, 1).json("spawnChance");
        public final IConfigEntry<Integer> targetSearchLength = new IntegerEntry("config.iceandfire.bird.targetSearchLength", 48, 0, 1024).json("targetSearchLength");
        public final IConfigEntry<Double> featherDropChance = new DoubleEntry("config.iceandfire.bird.featherDropChance", 1.0 / 25, 0, 1).json("featherDropChance");
        public final IConfigEntry<Double> featherAttackDamage = new DoubleEntry("config.iceandfire.bird.featherAttackDamage", 1, 0, Integer.MAX_VALUE).json("featherAttackDamage");
        public final IConfigEntry<Integer> flockLength = new IntegerEntry("config.iceandfire.bird.flockLength", 40, 0, 200).json("flockLength");
        public final IConfigEntry<Integer> flightHeight = new IntegerEntry("config.iceandfire.bird.flightHeight", 80, -2032, 2032).json("flightHeight");
        public final IConfigEntry<Boolean> attackAnimals = new BooleanEntry("config.iceandfire.bird.attackAnimals", false).json("attackAnimals");

        public StymphalianBirdConfig() {
            super("bird", "config.iceandfire.category.bird");
        }
    }

    public static class TrollConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> spawn = new BooleanEntry("config.iceandfire.troll.spawn", true).json("spawn");
        public final IConfigEntry<Integer> spawnWeight = new IntegerEntry("config.iceandfire.troll.spawnWeight", 60, 0, 200).json("spawnWeight");
        public final IConfigEntry<Boolean> dropWeapon = new BooleanEntry("config.iceandfire.troll.dropWeapon", true).json("dropWeapon");
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("config.iceandfire.troll.maxHealth", 50, 1, Integer.MAX_VALUE).json("maxHealth");
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("config.iceandfire.troll.attackDamage", 10, 0, Integer.MAX_VALUE).json("attackDamage");

        public TrollConfig() {
            super("troll", "config.iceandfire.category.troll");
        }
    }

    public static class AmphithereConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> spawn = new BooleanEntry("config.iceandfire.amphithere.spawn", true).json("spawn");
        public final IConfigEntry<Integer> spawnWeight = new IntegerEntry("config.iceandfire.amphithere.spawnWeight", 50, 0, 400).json("spawnWeight");
        public final IConfigEntry<Double> villagerSearchLength = new DoubleEntry("config.iceandfire.amphithere.villagerSearchLength", 48, 0, 1024).json("villagerSearchLength");
        public final IConfigEntry<Integer> tameTime = new IntegerEntry("config.iceandfire.amphithere.tameTime", 400, 0, Integer.MAX_VALUE).json("tameTime");
        public final IConfigEntry<Double> flightSpeed = new DoubleEntry("config.iceandfire.amphithere.flightSpeed", 1.75, 0, 20).json("flightSpeed");
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("config.iceandfire.amphithere.maxHealth", 50, 1, Integer.MAX_VALUE).json("maxHealth");
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("config.iceandfire.amphithere.attackDamage", 7, 0, Integer.MAX_VALUE).json("attackDamage");

        public AmphithereConfig() {
            super("amphithere", "config.iceandfire.category.amphithere");
        }
    }

    public static class SeaSerpentConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> spawnChance = new DoubleEntry("config.iceandfire.seaSerpent.spawnChance", 1.0 / 250, 0, 1).json("spawnChance");
        public final IConfigEntry<Boolean> griefing = new BooleanEntry("config.iceandfire.seaSerpent.griefing", true).json("griefing");
        public final IConfigEntry<Double> baseHealth = new DoubleEntry("config.iceandfire.seaSerpent.baseHealth", 20, 0, Integer.MAX_VALUE).json("baseHealth");
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("config.iceandfire.seaSerpent.attackDamage", 4, 0, Integer.MAX_VALUE).json("attackDamage");

        public SeaSerpentConfig() {
            super("seaSerpent", "config.iceandfire.category.seaSerpent");
        }
    }

    public static class LichConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> spawn = new BooleanEntry("config.iceandfire.lich.spawn", true).json("spawn");
        public final IConfigEntry<Integer> spawnWeight = new IntegerEntry("config.iceandfire.lich.spawnWeight", 4, 0, 20).json("spawnWeight");
        public final IConfigEntry<Double> spawnChance = new DoubleEntry("config.iceandfire.lich.spawnChance", 1.0 / 30, 0, 1).json("spawnChance");

        public LichConfig() {
            super("lich", "config.iceandfire.category.lich");
        }
    }

    public static class HydraConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("config.iceandfire.hydra.maxHealth", 250, 1, Integer.MAX_VALUE).json("maxHealth");

        public HydraConfig() {
            super("hydra", "config.iceandfire.category.hydra");
        }
    }

    public static class HippocampusConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> spawnChance = new DoubleEntry("config.iceandfire.hippocampus.spawnChance", 1.0 / 40, 0, 1).json("spawnChance");
        public final IConfigEntry<Double> swimSpeedMod = new DoubleEntry("config.iceandfire.hippocampus.swimSpeedMod", 1, 0.0001, 10).json("swimSpeedMod");

        public HippocampusConfig() {
            super("hippocampus", "config.iceandfire.category.hippocampus");
        }
    }

    public static class GhostConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("config.iceandfire.ghost.maxHealth", 30, 1, Integer.MAX_VALUE).json("maxHealth");
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("config.iceandfire.ghost.attackDamage", 3, 0, Integer.MAX_VALUE).json("attackDamage");
        public final IConfigEntry<Boolean> fromPlayerDeaths = new BooleanEntry("config.iceandfire.ghost.fromPlayerDeaths", true).json("fromPlayerDeaths");

        public GhostConfig() {
            super("ghost", "config.iceandfire.category.ghost");
        }
    }

    public static class ToolsConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> dragonFireAbility = new BooleanEntry("config.iceandfire.tools.dragonFireAbility", true).json("dragonFireAbility");
        public final IConfigEntry<Boolean> dragonIceAbility = new BooleanEntry("config.iceandfire.tools.dragonIceAbility", true).json("dragonIceAbility");
        public final IConfigEntry<Boolean> dragonLightningAbility = new BooleanEntry("config.iceandfire.tools.dragonLightningAbility", true).json("dragonLightningAbility");
        public final IConfigEntry<Integer> dragonsteelFireDuration = new IntegerEntry("config.iceandfire.tools.dragonsteelFireDuration", 15, 0, Integer.MAX_VALUE).json("dragonsteelFireDuration");
        public final IConfigEntry<Integer> dragonBloodFireDuration = new IntegerEntry("config.iceandfire.tools.dragonBloodFireDuration", 5, 0, Integer.MAX_VALUE).json("dragonBloodFireDuration");
        public final IConfigEntry<Integer> dragonsteelFrozenDuration = new IntegerEntry("config.iceandfire.tools.dragonsteelFrozenDuration", 300, 0, Integer.MAX_VALUE).json("dragonsteelFrozenDuration");
        public final IConfigEntry<Integer> dragonBloodFrozenDuration = new IntegerEntry("config.iceandfire.tools.dragonBloodFrozenDuration", 100, 0, Integer.MAX_VALUE).json("dragonBloodFrozenDuration");
        public final IConfigEntry<Boolean> phantasmalBladeAbility = new BooleanEntry("config.iceandfire.tools.phantasmalBladeAbility", true).json("phantasmalBladeAbility");
        public final SeparatorEntry s2 = new SeparatorEntry();
        public final IConfigEntry<Double> dragonLightningSearchRange = new DoubleEntry("config.iceandfire.tools.dragonLightningSearchRange", 10, 0, 128).json("dragonLightningSearchRange");
        public final IConfigEntry<Double> dragonLightningDamageReduction = new DoubleEntry("config.iceandfire.tools.dragonLightningDamageReduction", 0.5, 0, 1).json("dragonLightningDamageReduction");
        public final IConfigEntry<Integer> dragonLightningMaxSearchCount = new IntegerEntry("config.iceandfire.tools.dragonLightningMaxSearchCount", 10, 0, 1024).json("dragonLightningMaxSearchCount");

        public ToolsConfig() {
            super("tools", "config.iceandfire.category.tools");
        }
    }

    @SuppressWarnings("unused")
    public static class ArmorsConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> dragonSteelBaseDamage = new DoubleEntry("config.iceandfire.armors.dragonSteelBaseDamage", 25, 0, Integer.MAX_VALUE).json("dragonSteelBaseDamage");
        public final IConfigEntry<Integer> dragonSteelBaseDurability = new IntegerEntry("config.iceandfire.armors.dragonSteelBaseDurability", 8000, 0, Integer.MAX_VALUE).json("dragonSteelBaseDurability");
        public final IConfigEntry<Double> dragonsteelArmorToughness = new DoubleEntry("config.iceandfire.armors.dragonsteelArmorToughness", 6, 0, Integer.MAX_VALUE).json("dragonsteelArmorToughness");
        public final IConfigEntry<Integer> dragonsteelHelmetArmor = new IntegerEntry("config.iceandfire.armors.dragonsteelHelmetArmor", 7, 0, Integer.MAX_VALUE).json("dragonsteelHelmetArmor");
        public final IConfigEntry<Integer> dragonsteelHelmetDurability = new IntegerEntry("config.iceandfire.armors.dragonsteelHelmetDurability", 1760, 0, Integer.MAX_VALUE).json("dragonsteelHelmetDurability");
        public final IConfigEntry<Integer> dragonsteelChestplateArmor = new IntegerEntry("config.iceandfire.armors.dragonsteelChestplateArmor", 12, 0, Integer.MAX_VALUE).json("dragonsteelChestplateArmor");
        public final IConfigEntry<Integer> dragonsteelChestplateDurability = new IntegerEntry("config.iceandfire.armors.dragonsteelChestplateDurability", 2560, 0, Integer.MAX_VALUE).json("dragonsteelChestplateDurability");
        public final IConfigEntry<Integer> dragonsteelLeggingsArmor = new IntegerEntry("config.iceandfire.armors.dragonsteelLeggingsArmor", 9, 0, Integer.MAX_VALUE).json("dragonsteelLeggingsArmor");
        public final IConfigEntry<Integer> dragonsteelLeggingsDurability = new IntegerEntry("config.iceandfire.armors.dragonsteelLeggingsDurability", 2400, 0, Integer.MAX_VALUE).json("dragonsteelLeggingsDurability");
        public final IConfigEntry<Integer> dragonsteelBootsArmor = new IntegerEntry("config.iceandfire.armors.dragonsteelBootsArmor", 6, 0, Integer.MAX_VALUE).json("dragonsteelBootsArmor");
        public final IConfigEntry<Integer> dragonsteelBootsDurability = new IntegerEntry("config.iceandfire.armors.dragonsteelBootsDurability", 2080, 0, Integer.MAX_VALUE).json("dragonsteelBootsDurability");
        public final IConfigEntry<Integer> dragonsteelArmorEnchantability = new IntegerEntry("config.iceandfire.armors.dragonsteelArmorEnchantability", 30, 0, Integer.MAX_VALUE).json("dragonsteelArmorEnchantability");
        public final IConfigEntry<Double> dragonsteelArmorKnockbackResistance = new DoubleEntry("config.iceandfire.armors.dragonsteelArmorKnockbackResistance", 0.1, 0.0, 0.25).json("dragonsteelArmorKnockbackResistance");

        public ArmorsConfig() {
            super("armors", "config.iceandfire.category.armors");
        }
    }

    public static class WorldGenConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> dangerousDistanceLimit = new DoubleEntry("config.iceandfire.worldgen.dangerousDistanceLimit", 1000, 0, Integer.MAX_VALUE).json("dangerousDistanceLimit");
        public final IConfigEntry<Double> generateFireDragonCaveChance = new DoubleEntry("config.iceandfire.worldgen.generateFireDragonCaveChance", 1, 0, 1).json("generateFireDragonCaveChance");
        public final IConfigEntry<Double> generateFireDragonRoostChance = new DoubleEntry("config.iceandfire.worldgen.generateFireDragonRoostChance", 1, 0, 1).json("generateFireDragonRoostChance");
        public final IConfigEntry<Double> generateIceDragonCaveChance = new DoubleEntry("config.iceandfire.worldgen.generateIceDragonCaveChance", 1, 0, 1).json("generateIceDragonCaveChance");
        public final IConfigEntry<Double> generateIceDragonRoostChance = new DoubleEntry("config.iceandfire.worldgen.generateIceDragonRoostChance", 1, 0, 1).json("generateIceDragonRoostChance");
        public final IConfigEntry<Double> generateLightningDragonCaveChance = new DoubleEntry("config.iceandfire.worldgen.generateLightningDragonCaveChance", 1, 0, 1).json("generateLightningDragonCaveChance");
        public final IConfigEntry<Double> generateLightningDragonRoostChance = new DoubleEntry("config.iceandfire.worldgen.generateLightningDragonRoostChance", 1, 0, 1).json("generateLightningDragonRoostChance");
        public final IConfigEntry<Double> generateCyclopsCaveChance = new DoubleEntry("config.iceandfire.worldgen.generateCyclopsCaveChance", 1, 0, 1).json("generateCyclopsCaveChance");
        public final IConfigEntry<Double> generateGorgonTempleChance = new DoubleEntry("config.iceandfire.worldgen.generateGorgonTempleChance", 1, 0, 1).json("generateGorgonTempleChance");
        public final IConfigEntry<Double> generateGraveYardChance = new DoubleEntry("config.iceandfire.worldgen.generateGraveYardChance", 1, 0, 1).json("generateGraveYardChance");
        public final IConfigEntry<Double> generateHydraCaveChance = new DoubleEntry("config.iceandfire.worldgen.generateHydraCaveChance", 1, 0, 1).json("generateHydraCaveChance");
        public final IConfigEntry<Double> generateMausoleumChance = new DoubleEntry("config.iceandfire.worldgen.generateMausoleumChance", 1, 0, 1).json("generateMausoleumChance");
        public final IConfigEntry<Double> generatePixieVillageChance = new DoubleEntry("config.iceandfire.worldgen.generatePixieVillageChance", 1, 0, 1).json("generatePixieVillageChance");
        public final IConfigEntry<Double> generateSirenIslandChance = new DoubleEntry("config.iceandfire.worldgen.generateSirenIslandChance", 1, 0, 1).json("generateSirenIslandChance");

        public WorldGenConfig() {
            super("worldgen", "config.iceandfire.category.worldgen");
        }
    }

    public static class Misc extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> enableDragonSeeker = new BooleanEntry("config.iceandfire.misc.enableDragonSeeker", true).json("enableDragonSeeker");
        public final IConfigEntry<Double> dreadQueenMaxHealth = new DoubleEntry("config.iceandfire.misc.dreadQueenMaxHealth", 750, 0, Integer.MAX_VALUE).json("dreadQueenMaxHealth");

        public Misc() {
            super("misc", "config.iceandfire.category.misc");
        }
    }
}
