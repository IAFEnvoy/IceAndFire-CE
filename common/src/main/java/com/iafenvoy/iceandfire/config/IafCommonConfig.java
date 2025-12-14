package com.iafenvoy.iceandfire.config;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.config.entry.DoubleEntry;
import com.iafenvoy.jupiter.config.entry.IntegerEntry;
import com.iafenvoy.jupiter.config.entry.SeparatorEntry;
import net.minecraft.util.Identifier;

public class IafCommonConfig extends AutoInitConfigContainer {
    public static final IafCommonConfig INSTANCE = new IafCommonConfig();

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
        super(Identifier.of(IceAndFire.MOD_ID, "common"), "screen.iceandfire.common.title", "./config/iceandfire/iaf-common.json");
    }

    @Override
    public void init() {
        super.init();
        this.dataFixer.registerKeyRule("config.iceandfire.[a-zA-Z0-9.]+", s -> s.replace("config.iceandfire.", ""));
    }

    @SuppressWarnings("unused")
    public static class DragonConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.dragon.maxHealth", 500).min(1).key("maxHealth").build();
        public final IntegerEntry eggBornTime = IntegerEntry.builder("config.iceandfire.dragon.eggBornTime", 7200).min(0).key("eggBornTime").build();
        public final BooleanEntry villagersFear = BooleanEntry.builder("config.iceandfire.dragon.villagersFear", true).key("villagersFear").build();
        public final BooleanEntry animalsFear = BooleanEntry.builder("config.iceandfire.dragon.animalsFear", true).key("animalsFear").build();
        public final SeparatorEntry s1 = SeparatorEntry.builder().build();
        public final BooleanEntry generateSkeletons = BooleanEntry.builder("config.iceandfire.dragon.generate.skeletons", true).key("generate.skeletons").build();
        public final DoubleEntry generateSkeletonChance = DoubleEntry.builder("config.iceandfire.dragon.generate.skeletonChance", 1.0 / 300).range(0, 1).key("generate.skeletonChance").build();
        public final DoubleEntry generateDenGoldChance = DoubleEntry.builder("config.iceandfire.dragon.generate.denGoldAmount", 1.0 / 4).range(0, 1).key("generate.denGoldAmount").build();
        public final DoubleEntry generateOreRatio = DoubleEntry.builder("config.iceandfire.dragon.generate.oreRatio", 1.0 / 45).range(0, 1).key("generate.oreRatio").build();
        public final SeparatorEntry s2 = SeparatorEntry.builder().build();
        public final BooleanEntry griefing = BooleanEntry.builder("config.iceandfire.dragon.griefing", true).key("griefing").build();
        public final BooleanEntry tamedGriefing = BooleanEntry.builder("config.iceandfire.dragon.tamedGriefing", true).key("tamedGriefing").build();
        public final IntegerEntry flapNoiseDistance = IntegerEntry.builder("config.iceandfire.dragon.flapNoiseDistance", 4).range(0, 32).key("flapNoiseDistance").build();
        public final IntegerEntry fluteDistance = IntegerEntry.builder("config.iceandfire.dragon.fluteDistance", 8).range(0, 512).key("fluteDistance").build();
        public final IntegerEntry attackDamage = IntegerEntry.builder("config.iceandfire.dragon.attackDamage", 17).min(0).key("attackDamage").build();
        public final DoubleEntry attackDamageFire = DoubleEntry.builder("config.iceandfire.dragon.attackDamageFire", 2).min(0).key("attackDamageFire").build();
        public final DoubleEntry attackDamageIce = DoubleEntry.builder("config.iceandfire.dragon.attackDamageIce", 2.5).min(0).key("attackDamageIce").build();
        public final DoubleEntry attackDamageLightning = DoubleEntry.builder("config.iceandfire.dragon.attackDamageLightning", 3.5).min(0).key("attackDamageLightning").build();
        public final IntegerEntry maxFlight = IntegerEntry.builder("config.iceandfire.dragon.maxFlight", 256).range(-2302, 2302).key("maxFlight").build();
        public final IntegerEntry goldSearchLength = IntegerEntry.builder("config.iceandfire.dragon.goldSearchLength", 30).range(0, 256).key("goldSearchLength").build();
        public final BooleanEntry canHealFromBiting = BooleanEntry.builder("config.iceandfire.dragon.canHealFromBiting", false).key("canHealFromBiting").build();
        public final BooleanEntry canDespawn = BooleanEntry.builder("config.iceandfire.dragon.canDespawn", true).key("canDespawn").build();
        public final BooleanEntry sleep = BooleanEntry.builder("config.iceandfire.dragon.sleep", true).key("sleep").build();
        public final BooleanEntry digWhenStuck = BooleanEntry.builder("config.iceandfire.dragon.digWhenStuck", true).key("digWhenStuck").build();
        public final IntegerEntry breakBlockCooldown = IntegerEntry.builder("config.iceandfire.dragon.breakBlockCooldown", 5).min(0).key("breakBlockCooldown").build();
        public final IntegerEntry targetSearchLength = IntegerEntry.builder("config.iceandfire.dragon.targetSearchLength", 128).range(0, 1024).key("targetSearchLength").build();
        public final IntegerEntry wanderFromHomeDistance = IntegerEntry.builder("config.iceandfire.dragon.wanderFromHomeDistance", 40).range(0, 1024).key("wanderFromHomeDistance").build();
        public final IntegerEntry hungerTickRate = IntegerEntry.builder("config.iceandfire.dragon.hungerTickRate", 3000).min(1).key("hungerTickRate").build();
        public final DoubleEntry blockBreakingDropChance = DoubleEntry.builder("config.iceandfire.dragon.blockBreakingDropChance", 0.1).range(0, 1).key("blockBreakingDropChance").build();
        public final BooleanEntry explosiveBreath = BooleanEntry.builder("config.iceandfire.dragon.explosiveBreath", false).key("explosiveBreath").build();
        public final BooleanEntry chunkLoadSummonCrystal = BooleanEntry.builder("config.iceandfire.dragon.chunkLoadSummonCrystal", true).key("chunkLoadSummonCrystal").build();
        public final DoubleEntry dragonFlightSpeedMod = DoubleEntry.builder("config.iceandfire.dragon.dragonFlightSpeedMod", 1).range(0.0001, 50).key("dragonFlightSpeedMod").build();
        public final IntegerEntry maxTamedDragonAge = IntegerEntry.builder("config.iceandfire.dragon.maxTamedDragonAge", 128).range(0, 128).key("maxTamedDragonAge").build();
        public final DoubleEntry maxBreathTimeMul = DoubleEntry.builder("config.iceandfire.dragon.maxBreathTimeMul", 2).min(0).key("maxBreathTimeMul").build();
        public final SeparatorEntry s3 = SeparatorEntry.builder().build();
        public final BooleanEntry lootSkull = BooleanEntry.builder("config.iceandfire.dragon.loot.skull", true).key("loot.skull").build();
        public final BooleanEntry lootHeart = BooleanEntry.builder("config.iceandfire.dragon.loot.heart", true).key("loot.heart").build();
        public final BooleanEntry lootBlood = BooleanEntry.builder("config.iceandfire.dragon.loot.blood", true).key("loot.blood").build();

        public DragonConfig() {
            super("dragon", "config.iceandfire.category.dragon");
        }
    }

    public static class HippogryphsConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry spawn = BooleanEntry.builder("config.iceandfire.hippogryphs.spawn", true).key("spawn").build();
        public final IntegerEntry spawnWeight = IntegerEntry.builder("config.iceandfire.hippogryphs.spawnWeight", 2).range(0, 20).key("spawnWeight").build();
        public final DoubleEntry fightSpeedMod = DoubleEntry.builder("config.iceandfire.hippogryphs.fightSpeedMod", 1).range(0.0001, 50).key("fightSpeedMod").build();

        public HippogryphsConfig() {
            super("hippogryphs", "config.iceandfire.category.hippogryphs");
        }
    }

    public static class PixieConfig extends AutoInitConfigCategoryBase {
        public final IntegerEntry size = IntegerEntry.builder("config.iceandfire.pixie.size", 5).range(0, 100).key("size").build();
        public final BooleanEntry stealItems = BooleanEntry.builder("config.iceandfire.pixie.stealItems", false).key("stealItems").build();

        public PixieConfig() {
            super("pixie", "config.iceandfire.category.pixie");
        }
    }

    public static class CyclopsConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry spawnWanderingChance = DoubleEntry.builder("config.iceandfire.cyclops.spawnWanderingChance", 1.0 / 900).range(0, 1).key("spawnWanderingChance").build();
        public final IntegerEntry sheepSearchLength = IntegerEntry.builder("config.iceandfire.cyclops.sheepSearchLength", 17).range(0, 1024).key("sheepSearchLength").build();
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.cyclops.maxHealth", 150).min(1).key("maxHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.cyclops.attackDamage", 15).min(0).key("attackDamage").build();
        public final DoubleEntry biteDamage = DoubleEntry.builder("config.iceandfire.cyclops.biteDamage", 40).min(0).key("biteDamage").build();
        public final BooleanEntry griefing = BooleanEntry.builder("config.iceandfire.cyclops.griefing", true).key("griefing").build();

        public CyclopsConfig() {
            super("cyclops", "config.iceandfire.category.cyclops");
        }
    }

    public static class SirenConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.siren.maxHealth", 50).min(1).key("maxHealth").build();
        public final IntegerEntry maxSingTime = IntegerEntry.builder("config.iceandfire.siren.maxSingTime", 12000).min(0).key("maxSingTime").build();
        public final IntegerEntry timeBetweenSongs = IntegerEntry.builder("config.iceandfire.siren.timeBetweenSongs", 2000).min(0).key("timeBetweenSongs").build();

        public SirenConfig() {
            super("siren", "config.iceandfire.category.siren");
        }
    }

    public static class GorgonConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.gorgon.maxHealth", 100).min(1).key("maxHealth").build();

        public GorgonConfig() {
            super("gorgon", "config.iceandfire.category.gorgon");
        }
    }

    public static class DeathwormConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry spawnChance = DoubleEntry.builder("config.iceandfire.deathworm.spawnChance", 1.0 / 30).range(0, 1).key("spawnChance").build();
        public final IntegerEntry targetSearchLength = IntegerEntry.builder("config.iceandfire.deathworm.targetSearchLength", 48).range(0, 1024).key("targetSearchLength").build();
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.deathworm.maxHealth", 10).min(1).key("maxHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.deathworm.attackDamage", 3).range(0, 30).key("attackDamage").build();
        public final BooleanEntry attackMonsters = BooleanEntry.builder("config.iceandfire.deathworm.attackMonsters", true).key("attackMonsters").build();

        public DeathwormConfig() {
            super("deathworm", "config.iceandfire.category.deathworm");
        }
    }

    public static class CockatriceConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry spawn = BooleanEntry.builder("config.iceandfire.cockatrice.spawn", true).key("spawn").build();
        public final IntegerEntry spawnWeight = IntegerEntry.builder("config.iceandfire.cockatrice.spawnWeight", 4).range(0, 20).key("spawnWeight").build();
        public final IntegerEntry chickenSearchLength = IntegerEntry.builder("config.iceandfire.cockatrice.chickenSearchLength", 32).range(0, 1024).key("chickenSearchLength").build();
        public final DoubleEntry eggChance = DoubleEntry.builder("config.iceandfire.cockatrice.eggChance", 1.0 / 30).range(0, 1).key("eggChance").build();
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.cockatrice.maxHealth", 40).min(1).key("maxHealth").build();
        public final BooleanEntry chickensLayRottenEggs = BooleanEntry.builder("config.iceandfire.cockatrice.chickensLayRottenEggs", true).key("chickensLayRottenEggs").build();

        public CockatriceConfig() {
            super("cockatrice", "config.iceandfire.category.cockatrice");
        }
    }

    public static class StymphalianBirdConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry spawnChance = DoubleEntry.builder("config.iceandfire.bird.spawnChance", 1.0 / 80).range(0, 1).key("spawnChance").build();
        public final IntegerEntry targetSearchLength = IntegerEntry.builder("config.iceandfire.bird.targetSearchLength", 48).range(0, 1024).key("targetSearchLength").build();
        public final DoubleEntry featherDropChance = DoubleEntry.builder("config.iceandfire.bird.featherDropChance", 1.0 / 25).range(0, 1).key("featherDropChance").build();
        public final DoubleEntry featherAttackDamage = DoubleEntry.builder("config.iceandfire.bird.featherAttackDamage", 1).min(0).key("featherAttackDamage").build();
        public final IntegerEntry flockLength = IntegerEntry.builder("config.iceandfire.bird.flockLength", 40).range(0, 200).key("flockLength").build();
        public final IntegerEntry flightHeight = IntegerEntry.builder("config.iceandfire.bird.flightHeight", 80).range(-2032, 2032).key("flightHeight").build();
        public final BooleanEntry attackAnimals = BooleanEntry.builder("config.iceandfire.bird.attackAnimals", false).key("attackAnimals").build();

        public StymphalianBirdConfig() {
            super("bird", "config.iceandfire.category.bird");
        }
    }

    public static class TrollConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry spawn = BooleanEntry.builder("config.iceandfire.troll.spawn", true).key("spawn").build();
        public final IntegerEntry spawnWeight = IntegerEntry.builder("config.iceandfire.troll.spawnWeight", 60).range(0, 200).key("spawnWeight").build();
        public final BooleanEntry dropWeapon = BooleanEntry.builder("config.iceandfire.troll.dropWeapon", true).key("dropWeapon").build();
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.troll.maxHealth", 50).min(1).key("maxHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.troll.attackDamage", 10).min(0).key("attackDamage").build();

        public TrollConfig() {
            super("troll", "config.iceandfire.category.troll");
        }
    }

    public static class AmphithereConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry spawn = BooleanEntry.builder("config.iceandfire.amphithere.spawn", true).key("spawn").build();
        public final IntegerEntry spawnWeight = IntegerEntry.builder("config.iceandfire.amphithere.spawnWeight", 50).range(0, 400).key("spawnWeight").build();
        public final DoubleEntry villagerSearchLength = DoubleEntry.builder("config.iceandfire.amphithere.villagerSearchLength", 48).range(0, 1024).key("villagerSearchLength").build();
        public final IntegerEntry tameTime = IntegerEntry.builder("config.iceandfire.amphithere.tameTime", 400).min(0).key("tameTime").build();
        public final DoubleEntry flightSpeed = DoubleEntry.builder("config.iceandfire.amphithere.flightSpeed", 1.75).range(0, 20).key("flightSpeed").build();
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.amphithere.maxHealth", 50).min(1).key("maxHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.amphithere.attackDamage", 7).min(0).key("attackDamage").build();

        public AmphithereConfig() {
            super("amphithere", "config.iceandfire.category.amphithere");
        }
    }

    public static class SeaSerpentConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry spawnChance = DoubleEntry.builder("config.iceandfire.seaSerpent.spawnChance", 1.0 / 250).range(0, 1).key("spawnChance").build();
        public final BooleanEntry griefing = BooleanEntry.builder("config.iceandfire.seaSerpent.griefing", true).key("griefing").build();
        public final DoubleEntry baseHealth = DoubleEntry.builder("config.iceandfire.seaSerpent.baseHealth", 20).min(0).key("baseHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.seaSerpent.attackDamage", 4).min(0).key("attackDamage").build();

        public SeaSerpentConfig() {
            super("seaSerpent", "config.iceandfire.category.seaSerpent");
        }
    }

    public static class LichConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry spawn = BooleanEntry.builder("config.iceandfire.lich.spawn", true).key("spawn").build();
        public final IntegerEntry spawnWeight = IntegerEntry.builder("config.iceandfire.lich.spawnWeight", 4).range(0, 20).key("spawnWeight").build();
        public final DoubleEntry spawnChance = DoubleEntry.builder("config.iceandfire.lich.spawnChance", 1.0 / 30).range(0, 1).key("spawnChance").build();

        public LichConfig() {
            super("lich", "config.iceandfire.category.lich");
        }
    }

    public static class HydraConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.hydra.maxHealth", 250).min(1).key("maxHealth").build();

        public HydraConfig() {
            super("hydra", "config.iceandfire.category.hydra");
        }
    }

    public static class HippocampusConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry spawnChance = DoubleEntry.builder("config.iceandfire.hippocampus.spawnChance", 1.0 / 40).range(0, 1).key("spawnChance").build();
        public final DoubleEntry swimSpeedMod = DoubleEntry.builder("config.iceandfire.hippocampus.swimSpeedMod", 1).range(0.0001, 10).key("swimSpeedMod").build();

        public HippocampusConfig() {
            super("hippocampus", "config.iceandfire.category.hippocampus");
        }
    }

    public static class GhostConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.ghost.maxHealth", 30).min(1).key("maxHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.ghost.attackDamage", 3).min(0).key("attackDamage").build();
        public final BooleanEntry fromPlayerDeaths = BooleanEntry.builder("config.iceandfire.ghost.fromPlayerDeaths", true).key("fromPlayerDeaths").build();

        public GhostConfig() {
            super("ghost", "config.iceandfire.category.ghost");
        }
    }

    @SuppressWarnings("unused")
    public static class ArmorsConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry dragonFireAbility = BooleanEntry.builder("iceandfire.armors.dragonFireAbility", true).key("dragonFireAbility").build();
        public final BooleanEntry dragonIceAbility = BooleanEntry.builder("iceandfire.armors.dragonIceAbility", true).key("dragonIceAbility").build();
        public final BooleanEntry dragonLightningAbility = BooleanEntry.builder("iceandfire.armors.dragonLightningAbility", true).key("dragonLightningAbility").build();
        public final SeparatorEntry s1 = SeparatorEntry.builder().build();
        public final DoubleEntry dragonSteelBaseDamage = DoubleEntry.builder("iceandfire.armors.dragonSteelBaseDamage", 25).min(0).key("dragonSteelBaseDamage").build();
        public final IntegerEntry dragonSteelBaseArmor = IntegerEntry.builder("iceandfire.armors.dragonSteelBaseArmor", 12).min(0).key("dragonSteelBaseArmor").build();
        public final DoubleEntry dragonSteelBaseArmorToughness = DoubleEntry.builder("iceandfire.armors.dragonSteelBaseArmorToughness", 6).min(0).key("dragonSteelBaseArmorToughness").build();
        public final IntegerEntry dragonSteelBaseDurability = IntegerEntry.builder("iceandfire.armors.dragonSteelBaseDurability", 8000).min(0).key("dragonSteelBaseDurability").build();
        public final IntegerEntry dragonSteelBaseDurabilityEquipment = IntegerEntry.builder("iceandfire.armors.dragonSteelBaseDurabilityEquipment", 8000).min(0).key("dragonSteelBaseDurabilityEquipment").build();
        public final SeparatorEntry s2 = SeparatorEntry.builder().build();
        public final DoubleEntry dragonLightningSearchRange = DoubleEntry.builder("iceandfire.armors.dragonLightningSearchRange", 10).range(0, 128).key("dragonLightningSearchRange").build();
        public final DoubleEntry dragonLightningDamageReduction = DoubleEntry.builder("iceandfire.armors.dragonLightningDamageReduction", 0.5).range(0, 1).key("dragonLightningDamageReduction").build();
        public final IntegerEntry dragonLightningMaxSearchCount = IntegerEntry.builder("iceandfire.armors.dragonLightningMaxSearchCount", 10).range(0, 1024).key("dragonLightningMaxSearchCount").build();

        public ArmorsConfig() {
            super("armors", "iceandfire.category.armors");
        }
    }

    public static class WorldGenConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry dangerousDistanceLimit = DoubleEntry.builder("config.iceandfire.worldgen.dangerousDistanceLimit", 1000).min(0).key("dangerousDistanceLimit").build();
        public final DoubleEntry generateFireDragonCaveChance = DoubleEntry.builder("config.iceandfire.worldgen.generateFireDragonCaveChance", 1).range(0, 1).key("generateFireDragonCaveChance").build();
        public final DoubleEntry generateFireDragonRoostChance = DoubleEntry.builder("config.iceandfire.worldgen.generateFireDragonRoostChance", 1).range(0, 1).key("generateFireDragonRoostChance").build();
        public final DoubleEntry generateIceDragonCaveChance = DoubleEntry.builder("config.iceandfire.worldgen.generateIceDragonCaveChance", 1).range(0, 1).key("generateIceDragonCaveChance").build();
        public final DoubleEntry generateIceDragonRoostChance = DoubleEntry.builder("config.iceandfire.worldgen.generateIceDragonRoostChance", 1).range(0, 1).key("generateIceDragonRoostChance").build();
        public final DoubleEntry generateLightningDragonCaveChance = DoubleEntry.builder("config.iceandfire.worldgen.generateLightningDragonCaveChance", 1).range(0, 1).key("generateLightningDragonCaveChance").build();
        public final DoubleEntry generateLightningDragonRoostChance = DoubleEntry.builder("config.iceandfire.worldgen.generateLightningDragonRoostChance", 1).range(0, 1).key("generateLightningDragonRoostChance").build();
        public final DoubleEntry generateCyclopsCaveChance = DoubleEntry.builder("config.iceandfire.worldgen.generateCyclopsCaveChance", 1).range(0, 1).key("generateCyclopsCaveChance").build();
        public final DoubleEntry generateGorgonTempleChance = DoubleEntry.builder("config.iceandfire.worldgen.generateGorgonTempleChance", 1).range(0, 1).key("generateGorgonTempleChance").build();
        public final DoubleEntry generateGraveYardChance = DoubleEntry.builder("config.iceandfire.worldgen.generateGraveYardChance", 1).range(0, 1).key("generateGraveYardChance").build();
        public final DoubleEntry generateHydraCaveChance = DoubleEntry.builder("config.iceandfire.worldgen.generateHydraCaveChance", 1).range(0, 1).key("generateHydraCaveChance").build();
        public final DoubleEntry generateMausoleumChance = DoubleEntry.builder("config.iceandfire.worldgen.generateMausoleumChance", 1).range(0, 1).key("generateMausoleumChance").build();
        public final DoubleEntry generatePixieVillageChance = DoubleEntry.builder("config.iceandfire.worldgen.generatePixieVillageChance", 1).range(0, 1).key("generatePixieVillageChance").build();
        public final DoubleEntry generateSirenIslandChance = DoubleEntry.builder("config.iceandfire.worldgen.generateSirenIslandChance", 1).range(0, 1).key("generateSirenIslandChance").build();

        public WorldGenConfig() {
            super("worldgen", "config.iceandfire.category.worldgen");
        }
    }

    public static class Misc extends AutoInitConfigCategoryBase {
        public final BooleanEntry enableDragonSeeker = BooleanEntry.builder("config.iceandfire.misc.enableDragonSeeker", true).key("enableDragonSeeker").build();
        public final DoubleEntry dreadQueenMaxHealth = DoubleEntry.builder("config.iceandfire.misc.dreadQueenMaxHealth", 750).min(0).key("dreadQueenMaxHealth").build();

        public Misc() {
            super("misc", "config.iceandfire.category.misc");
        }
    }
}
