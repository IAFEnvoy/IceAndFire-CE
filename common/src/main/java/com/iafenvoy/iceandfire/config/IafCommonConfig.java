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
    public static final int CURRENT_VERSION = 2;
    public static final String backupPath = "./config/iceandfire/";

    public DragonConfig dragon = new DragonConfig();
    public HippogryphsConfig hippogryphs = new HippogryphsConfig();
    public PixieConfig pixie = new PixieConfig();
    public CyclopsConfig cyclops = new CyclopsConfig();
    public SirenConfig siren = new SirenConfig();
    public GorgonConfig gorgon = new GorgonConfig();
    public DeathwormConfig deathworm = new DeathwormConfig();
    public CockatriceConfig cockatrice = new CockatriceConfig();
    public StymphalianBirdConfig stymphalianBird = new StymphalianBirdConfig();
    public TrollConfig troll = new TrollConfig();
    public MyrmexConfig myrmex = new MyrmexConfig();
    public AmphithereConfig amphithere = new AmphithereConfig();
    public SeaSerpentConfig seaSerpent = new SeaSerpentConfig();
    public LichConfig lich = new LichConfig();
    public HydraConfig hydra = new HydraConfig();
    public HippocampusConfig hippocampus = new HippocampusConfig();
    public GhostConfig ghost = new GhostConfig();
    public ArmorsConfig armors = new ArmorsConfig();
    public WorldGenConfig worldGen = new WorldGenConfig();
    public Misc misc = new Misc();

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
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("iceandfire.dragon.maxHealth", 500, 1, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> eggBornTime = new IntegerEntry("iceandfire.dragon.eggBornTime", 7200, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> maxPathingNodes = new IntegerEntry("iceandfire.dragon.maxPathingNodes", 5000, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Boolean> villagersFear = new BooleanEntry("iceandfire.dragon.villagersFear", true);
        public final IConfigEntry<Boolean> animalsFear = new BooleanEntry("iceandfire.dragon.animalsFear", true);
        public final SeparatorEntry s1 = new SeparatorEntry();
        public final IConfigEntry<Boolean> generateSkeletons = new BooleanEntry("iceandfire.dragon.generate.skeletons", true);
        public final IConfigEntry<Double> generateSkeletonChance = new DoubleEntry("iceandfire.dragon.generate.skeletonChance", 1.0 / 300, 0, 1);
        public final IConfigEntry<Double> generateDenGoldChance = new DoubleEntry("iceandfire.dragon.generate.denGoldAmount", 1.0 / 4, 0, 1);
        public final IConfigEntry<Double> generateOreRatio = new DoubleEntry("iceandfire.dragon.generate.oreRatio", 1.0 / 45, 0, 1);
        public final SeparatorEntry s2 = new SeparatorEntry();
        public final IConfigEntry<Boolean> griefing = new BooleanEntry("iceandfire.dragon.griefing", true);
        public final IConfigEntry<Boolean> tamedGriefing = new BooleanEntry("iceandfire.dragon.tamedGriefing", true);
        public final IConfigEntry<Integer> flapNoiseDistance = new IntegerEntry("iceandfire.dragon.flapNoiseDistance", 4, 0, 32);
        public final IConfigEntry<Integer> fluteDistance = new IntegerEntry("iceandfire.dragon.fluteDistance", 8, 0, 512);
        public final IConfigEntry<Integer> attackDamage = new IntegerEntry("iceandfire.dragon.attackDamage", 17, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Double> attackDamageFire = new DoubleEntry("iceandfire.dragon.attackDamageFire", 2, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Double> attackDamageIce = new DoubleEntry("iceandfire.dragon.attackDamageIce", 2.5, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Double> attackDamageLightning = new DoubleEntry("iceandfire.dragon.attackDamageLightning", 3.5, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> maxFlight = new IntegerEntry("iceandfire.dragon.maxFlight", 256, 0, 384);
        public final IConfigEntry<Integer> goldSearchLength = new IntegerEntry("iceandfire.dragon.goldSearchLength", 30, 0, 256);
        public final IConfigEntry<Boolean> canHealFromBiting = new BooleanEntry("iceandfire.dragon.canHealFromBiting", false);
        public final IConfigEntry<Boolean> canDespawn = new BooleanEntry("iceandfire.dragon.canDespawn", true);
        public final IConfigEntry<Boolean> sleep = new BooleanEntry("iceandfire.dragon.sleep", true);
        public final IConfigEntry<Boolean> digWhenStuck = new BooleanEntry("iceandfire.dragon.digWhenStuck", true);
        public final IConfigEntry<Integer> breakBlockCooldown = new IntegerEntry("iceandfire.dragon.breakBlockCooldown", 5, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> targetSearchLength = new IntegerEntry("iceandfire.dragon.targetSearchLength", 128, 0, 1024);
        public final IConfigEntry<Integer> wanderFromHomeDistance = new IntegerEntry("iceandfire.dragon.wanderFromHomeDistance", 40, 0, 1024);
        public final IConfigEntry<Integer> hungerTickRate = new IntegerEntry("iceandfire.dragon.hungerTickRate", 3000, 1, Integer.MAX_VALUE);
        public final IConfigEntry<Double> blockBreakingDropChance = new DoubleEntry("iceandfire.dragon.blockBreakingDropChance", 0.1, 0, 1);
        public final IConfigEntry<Boolean> explosiveBreath = new BooleanEntry("iceandfire.dragon.explosiveBreath", false);
        public final IConfigEntry<Boolean> chunkLoadSummonCrystal = new BooleanEntry("iceandfire.dragon.chunkLoadSummonCrystal", true);
        public final IConfigEntry<Double> dragonFlightSpeedMod = new DoubleEntry("iceandfire.dragon.dragonFlightSpeedMod", 1, 0.0001, 50);
        public final IConfigEntry<Integer> maxTamedDragonAge = new IntegerEntry("iceandfire.dragon.maxTamedDragonAge", 128, 0, 128);
        public final IConfigEntry<Double> maxBreathTimeMul = new DoubleEntry("iceandfire.dragon.maxBreathTimeMul", 2, 0, Integer.MAX_VALUE);
        public final SeparatorEntry s3 = new SeparatorEntry();
        public final IConfigEntry<Boolean> lootSkull = new BooleanEntry("iceandfire.dragon.loot.skull", true);
        public final IConfigEntry<Boolean> lootHeart = new BooleanEntry("iceandfire.dragon.loot.heart", true);
        public final IConfigEntry<Boolean> lootBlood = new BooleanEntry("iceandfire.dragon.loot.blood", true);

        public DragonConfig() {
            super("dragon", "iceandfire.category.dragon");
        }
    }

    public static class HippogryphsConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> spawn = new BooleanEntry("iceandfire.hippogryphs.spawn", true);
        public final IConfigEntry<Integer> spawnWeight = new IntegerEntry("iceandfire.hippogryphs.spawnWeight", 2, 0, 20);
        public final IConfigEntry<Double> fightSpeedMod = new DoubleEntry("iceandfire.hippogryphs.fightSpeedMod", 1, 0.0001, 50);

        public HippogryphsConfig() {
            super("hippogryphs", "iceandfire.category.hippogryphs");
        }
    }

    public static class PixieConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Integer> size = new IntegerEntry("iceandfire.pixie.size", 5, 0, 100);
        public final IConfigEntry<Boolean> stealItems = new BooleanEntry("iceandfire.pixie.stealItems", true);

        public PixieConfig() {
            super("pixie", "iceandfire.category.pixie");
        }
    }

    public static class CyclopsConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> spawnWanderingChance = new DoubleEntry("iceandfire.cyclops.spawnWanderingChance", 1.0 / 900, 0, 1);
        public final IConfigEntry<Integer> sheepSearchLength = new IntegerEntry("iceandfire.cyclops.sheepSearchLength", 17, 0, 1024);
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("iceandfire.cyclops.maxHealth", 150, 1, Integer.MAX_VALUE);
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("iceandfire.cyclops.attackDamage", 15, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Double> biteDamage = new DoubleEntry("iceandfire.cyclops.biteDamage", 40, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Boolean> griefing = new BooleanEntry("iceandfire.cyclops.griefing", true);

        public CyclopsConfig() {
            super("cyclops", "iceandfire.category.cyclops");
        }
    }

    public static class SirenConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("iceandfire.siren.maxHealth", 50, 1, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> maxSingTime = new IntegerEntry("iceandfire.siren.maxSingTime", 12000, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> timeBetweenSongs = new IntegerEntry("iceandfire.siren.timeBetweenSongs", 2000, 0, Integer.MAX_VALUE);

        public SirenConfig() {
            super("siren", "iceandfire.category.siren");
        }
    }

    public static class GorgonConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("iceandfire.gorgon.maxHealth", 100, 1, Integer.MAX_VALUE);

        public GorgonConfig() {
            super("gorgon", "iceandfire.category.gorgon");
        }
    }

    public static class DeathwormConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> spawnChance = new DoubleEntry("iceandfire.deathworm.spawnChance", 1.0 / 30, 0, 1);
        public final IConfigEntry<Integer> targetSearchLength = new IntegerEntry("iceandfire.deathworm.targetSearchLength", 48, 0, 1024);
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("iceandfire.deathworm.maxHealth", 10, 1, Integer.MAX_VALUE);
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("iceandfire.deathworm.attackDamage", 3, 0, 30);
        public final IConfigEntry<Boolean> attackMonsters = new BooleanEntry("iceandfire.deathworm.attackMonsters", true);

        public DeathwormConfig() {
            super("deathworm", "iceandfire.category.deathworm");
        }
    }

    public static class CockatriceConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> spawn = new BooleanEntry("iceandfire.cockatrice.spawn", true);
        public final IConfigEntry<Integer> spawnWeight = new IntegerEntry("iceandfire.cockatrice.spawnWeight", 4, 0, 20);
        public final IConfigEntry<Integer> chickenSearchLength = new IntegerEntry("iceandfire.cockatrice.chickenSearchLength", 32, 0, 1024);
        public final IConfigEntry<Double> eggChance = new DoubleEntry("iceandfire.cockatrice.eggChance", 1.0 / 30, 0, 1);
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("iceandfire.cockatrice.maxHealth", 40, 1, Integer.MAX_VALUE);
        public final IConfigEntry<Boolean> chickensLayRottenEggs = new BooleanEntry("iceandfire.cockatrice.chickensLayRottenEggs", true);

        public CockatriceConfig() {
            super("cockatrice", "iceandfire.category.cockatrice");
        }
    }

    public static class StymphalianBirdConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> spawnChance = new DoubleEntry("iceandfire.bird.spawnChance", 1.0 / 80, 0, 1);
        public final IConfigEntry<Integer> targetSearchLength = new IntegerEntry("iceandfire.bird.targetSearchLength", 48, 0, 1024);
        public final IConfigEntry<Double> featherDropChance = new DoubleEntry("iceandfire.bird.featherDropChance", 1.0 / 25, 0, 1);
        public final IConfigEntry<Double> featherAttackDamage = new DoubleEntry("iceandfire.bird.featherAttackDamage", 1, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> flockLength = new IntegerEntry("iceandfire.bird.flockLength", 40, 0, 200);
        public final IConfigEntry<Integer> flightHeight = new IntegerEntry("iceandfire.bird.flightHeight", 80, 64, 384);
        public final IConfigEntry<Boolean> attackAnimals = new BooleanEntry("iceandfire.bird.attackAnimals", false);

        public StymphalianBirdConfig() {
            super("bird", "iceandfire.category.bird");
        }
    }

    public static class TrollConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> spawn = new BooleanEntry("iceandfire.troll.spawn", true);
        public final IConfigEntry<Integer> spawnWeight = new IntegerEntry("iceandfire.troll.spawnWeight", 60, 0, 200);
        public final IConfigEntry<Boolean> dropWeapon = new BooleanEntry("iceandfire.troll.dropWeapon", true);
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("iceandfire.troll.maxHealth", 50, 1, Integer.MAX_VALUE);
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("iceandfire.troll.attackDamage", 10, 0, Integer.MAX_VALUE);

        public TrollConfig() {
            super("troll", "iceandfire.category.troll");
        }
    }

    public static class MyrmexConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Integer> pregnantTicks = new IntegerEntry("iceandfire.myrmex.pregnantTicks", 2500, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> eggTicks = new IntegerEntry("iceandfire.myrmex.eggTicks", 3000, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> larvaTicks = new IntegerEntry("iceandfire.myrmex.larvaTicks", 35000, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> colonySize = new IntegerEntry("iceandfire.myrmex.colonySize", 80, 0, 400);
        public final IConfigEntry<Integer> maximumWanderRadius = new IntegerEntry("iceandfire.myrmex.maximumWanderRadius", 50, 0, 400);
        public final IConfigEntry<Boolean> hiveIgnoreDaytime = new BooleanEntry("iceandfire.myrmex.hiveIgnoreDaytime", false);
        public final IConfigEntry<Double> baseAttackDamage = new DoubleEntry("iceandfire.myrmex.baseAttackDamage", 3, 0, Integer.MAX_VALUE);

        public MyrmexConfig() {
            super("myrmex", "iceandfire.category.myrmex");
        }
    }

    public static class AmphithereConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> spawn = new BooleanEntry("iceandfire.amphithere.spawn", true);
        public final IConfigEntry<Integer> spawnWeight = new IntegerEntry("iceandfire.amphithere.spawnWeight", 50, 0, 400);
        public final IConfigEntry<Double> villagerSearchLength = new DoubleEntry("iceandfire.amphithere.villagerSearchLength", 48, 0, 1024);
        public final IConfigEntry<Integer> tameTime = new IntegerEntry("iceandfire.amphithere.tameTime", 400, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Double> flightSpeed = new DoubleEntry("iceandfire.amphithere.flightSpeed", 1.75, 0, 20);
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("iceandfire.amphithere.maxHealth", 50, 1, Integer.MAX_VALUE);
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("iceandfire.amphithere.attackDamage", 7, 0, Integer.MAX_VALUE);

        public AmphithereConfig() {
            super("amphithere", "iceandfire.category.amphithere");
        }
    }

    public static class SeaSerpentConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> spawnChance = new DoubleEntry("iceandfire.seaSerpent.spawnChance", 1.0 / 250, 0, 1);
        public final IConfigEntry<Boolean> griefing = new BooleanEntry("iceandfire.seaSerpent.griefing", true);
        public final IConfigEntry<Double> baseHealth = new DoubleEntry("iceandfire.seaSerpent.baseHealth", 20, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("iceandfire.seaSerpent.attackDamage", 4, 0, Integer.MAX_VALUE);

        public SeaSerpentConfig() {
            super("seaSerpent", "iceandfire.category.seaSerpent");
        }
    }

    public static class LichConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> spawn = new BooleanEntry("iceandfire.lich.spawn", true);
        public final IConfigEntry<Integer> spawnWeight = new IntegerEntry("iceandfire.lich.spawnWeight", 4, 0, 20);
        public final IConfigEntry<Double> spawnChance = new DoubleEntry("iceandfire.lich.spawnChance", 1.0 / 30, 0, 1);

        public LichConfig() {
            super("lich", "iceandfire.category.lich");
        }
    }

    public static class HydraConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("iceandfire.hydra.maxHealth", 250, 1, Integer.MAX_VALUE);

        public HydraConfig() {
            super("hydra", "iceandfire.category.hydra");
        }
    }

    public static class HippocampusConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> spawnChance = new DoubleEntry("iceandfire.hippocampus.spawnChance", 1.0 / 40, 0, 1);
        public final IConfigEntry<Double> swimSpeedMod = new DoubleEntry("iceandfire.hippocampus.swimSpeedMod", 1, 0.0001, 10);

        public HippocampusConfig() {
            super("hippocampus", "iceandfire.category.hippocampus");
        }
    }

    public static class GhostConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> maxHealth = new DoubleEntry("iceandfire.ghost.maxHealth", 30, 1, Integer.MAX_VALUE);
        public final IConfigEntry<Double> attackDamage = new DoubleEntry("iceandfire.ghost.attackDamage", 3, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Boolean> fromPlayerDeaths = new BooleanEntry("iceandfire.ghost.fromPlayerDeaths", true);

        public GhostConfig() {
            super("ghost", "iceandfire.category.ghost");
        }
    }

    @SuppressWarnings("unused")
    public static class ArmorsConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> dragonFireAbility = new BooleanEntry("iceandfire.armors.dragonFireAbility", true);
        public final IConfigEntry<Boolean> dragonIceAbility = new BooleanEntry("iceandfire.armors.dragonIceAbility", true);
        public final IConfigEntry<Boolean> dragonLightningAbility = new BooleanEntry("iceandfire.armors.dragonLightningAbility", true);
        public final SeparatorEntry s1 = new SeparatorEntry();
        public final IConfigEntry<Double> dragonSteelBaseDamage = new DoubleEntry("iceandfire.armors.dragonSteelBaseDamage", 25, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> dragonSteelBaseArmor = new IntegerEntry("iceandfire.armors.dragonSteelBaseArmor", 12, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Double> dragonSteelBaseArmorToughness = new DoubleEntry("iceandfire.armors.dragonSteelBaseArmorToughness", 6, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> dragonSteelBaseDurability = new IntegerEntry("iceandfire.armors.dragonSteelBaseDurability", 8000, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Integer> dragonSteelBaseDurabilityEquipment = new IntegerEntry("iceandfire.armors.dragonSteelBaseDurabilityEquipment", 8000, 0, Integer.MAX_VALUE);

        public ArmorsConfig() {
            super("armors", "iceandfire.category.armors");
        }
    }

    public static class WorldGenConfig extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Double> dangerousDistanceLimit = new DoubleEntry("iceandfire.worldgen.dangerousDistanceLimit", 1000, 0, Integer.MAX_VALUE);
        public final IConfigEntry<Double> generateFireDragonCaveChance = new DoubleEntry("iceandfire.worldgen.generateFireDragonCaveChance", 1, 0, 1);
        public final IConfigEntry<Double> generateFireDragonRoostChance = new DoubleEntry("iceandfire.worldgen.generateFireDragonRoostChance", 1, 0, 1);
        public final IConfigEntry<Double> generateIceDragonCaveChance = new DoubleEntry("iceandfire.worldgen.generateIceDragonCaveChance", 1, 0, 1);
        public final IConfigEntry<Double> generateIceDragonRoostChance = new DoubleEntry("iceandfire.worldgen.generateIceDragonRoostChance", 1, 0, 1);
        public final IConfigEntry<Double> generateLightningDragonCaveChance = new DoubleEntry("iceandfire.worldgen.generateLightningDragonCaveChance", 1, 0, 1);
        public final IConfigEntry<Double> generateLightningDragonRoostChance = new DoubleEntry("iceandfire.worldgen.generateLightningDragonRoostChance", 1, 0, 1);
        public final IConfigEntry<Double> generateCyclopsCaveChance = new DoubleEntry("iceandfire.worldgen.generateCyclopsCaveChance", 1, 0, 1);
        public final IConfigEntry<Double> generateGorgonTempleChance = new DoubleEntry("iceandfire.worldgen.generateGorgonTempleChance", 1, 0, 1);
        public final IConfigEntry<Double> generateGraveYardChance = new DoubleEntry("iceandfire.worldgen.generateGraveYardChance", 1, 0, 1);
        public final IConfigEntry<Double> generateHydraCaveChance = new DoubleEntry("iceandfire.worldgen.generateHydraCaveChance", 1, 0, 1);
        public final IConfigEntry<Double> generateMausoleumChance = new DoubleEntry("iceandfire.worldgen.generateMausoleumChance", 1, 0, 1);
        public final IConfigEntry<Double> generateMyemexHiveDesertChance = new DoubleEntry("iceandfire.worldgen.generateMyemexHiveDesertChance", 1, 0, 1);
        public final IConfigEntry<Double> generateMyemexHiveJungleChance = new DoubleEntry("iceandfire.worldgen.generateMyemexHiveJungleChance", 1, 0, 1);
        public final IConfigEntry<Double> generatePixieVillageChance = new DoubleEntry("iceandfire.worldgen.generatePixieVillageChance", 1, 0, 1);
        public final IConfigEntry<Double> generateSirenIslandChance = new DoubleEntry("iceandfire.worldgen.generateSirenIslandChance", 1, 0, 1);

        public WorldGenConfig() {
            super("worldgen", "iceandfire.category.worldgen");
        }
    }

    public static class Misc extends AutoInitConfigCategoryBase {
        public final IConfigEntry<Boolean> enableDragonSeeker = new BooleanEntry("iceandfire.misc.enableDragonSeeker", true);
        public final IConfigEntry<Double> dreadQueenMaxHealth = new DoubleEntry("iceandfire.misc.dreadQueenMaxHealth", 750, 0, Integer.MAX_VALUE);

        public Misc() {
            super("misc", "iceandfire.category.misc");
        }
    }
}
