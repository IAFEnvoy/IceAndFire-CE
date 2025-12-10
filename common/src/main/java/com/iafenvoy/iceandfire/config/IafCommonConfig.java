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
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.dragon.maxHealth", 500).min(1).json("maxHealth").build();
        public final IntegerEntry eggBornTime = IntegerEntry.builder("config.iceandfire.dragon.eggBornTime", 7200).min(0).json("eggBornTime").build();
        public final BooleanEntry villagersFear = BooleanEntry.builder("config.iceandfire.dragon.villagersFear", true).json("villagersFear").build();
        public final BooleanEntry animalsFear = BooleanEntry.builder("config.iceandfire.dragon.animalsFear", true).json("animalsFear").build();
        public final SeparatorEntry s1 = SeparatorEntry.builder().build();
        public final BooleanEntry generateSkeletons = BooleanEntry.builder("config.iceandfire.dragon.generate.skeletons", true).json("generate.skeletons").build();
        public final DoubleEntry generateSkeletonChance = DoubleEntry.builder("config.iceandfire.dragon.generate.skeletonChance", 1.0 / 300).min(0).max(1).json("generate.skeletonChance").build();
        public final DoubleEntry generateDenGoldChance = DoubleEntry.builder("config.iceandfire.dragon.generate.denGoldAmount", 1.0 / 4).min(0).max(1).json("generate.denGoldAmount").build();
        public final DoubleEntry generateOreRatio = DoubleEntry.builder("config.iceandfire.dragon.generate.oreRatio", 1.0 / 45).min(0).max(1).json("generate.oreRatio").build();
        public final SeparatorEntry s2 = SeparatorEntry.builder().build();
        public final BooleanEntry griefing = BooleanEntry.builder("config.iceandfire.dragon.griefing", true).json("griefing").build();
        public final BooleanEntry tamedGriefing = BooleanEntry.builder("config.iceandfire.dragon.tamedGriefing", true).json("tamedGriefing").build();
        public final IntegerEntry flapNoiseDistance = IntegerEntry.builder("config.iceandfire.dragon.flapNoiseDistance", 4).min(0).max(32).json("flapNoiseDistance").build();
        public final IntegerEntry fluteDistance = IntegerEntry.builder("config.iceandfire.dragon.fluteDistance", 8).min(0).max(512).json("fluteDistance").build();
        public final IntegerEntry attackDamage = IntegerEntry.builder("config.iceandfire.dragon.attackDamage", 17).min(0).json("attackDamage").build();
        public final DoubleEntry attackDamageFire = DoubleEntry.builder("config.iceandfire.dragon.attackDamageFire", 2).min(0).json("attackDamageFire").build();
        public final DoubleEntry attackDamageIce = DoubleEntry.builder("config.iceandfire.dragon.attackDamageIce", 2.5).min(0).json("attackDamageIce").build();
        public final DoubleEntry attackDamageLightning = DoubleEntry.builder("config.iceandfire.dragon.attackDamageLightning", 3.5).min(0).json("attackDamageLightning").build();
        public final IntegerEntry maxFlight = IntegerEntry.builder("config.iceandfire.dragon.maxFlight", 256).min(-2302).max(2302).json("maxFlight").build();
        public final IntegerEntry goldSearchLength = IntegerEntry.builder("config.iceandfire.dragon.goldSearchLength", 30).min(0).max(256).json("goldSearchLength").build();
        public final BooleanEntry canHealFromBiting = BooleanEntry.builder("config.iceandfire.dragon.canHealFromBiting", false).json("canHealFromBiting").build();
        public final BooleanEntry canDespawn = BooleanEntry.builder("config.iceandfire.dragon.canDespawn", true).json("canDespawn").build();
        public final BooleanEntry sleep = BooleanEntry.builder("config.iceandfire.dragon.sleep", true).json("sleep").build();
        public final BooleanEntry digWhenStuck = BooleanEntry.builder("config.iceandfire.dragon.digWhenStuck", true).json("digWhenStuck").build();
        public final IntegerEntry breakBlockCooldown = IntegerEntry.builder("config.iceandfire.dragon.breakBlockCooldown", 5).min(0).json("breakBlockCooldown").build();
        public final IntegerEntry targetSearchLength = IntegerEntry.builder("config.iceandfire.dragon.targetSearchLength", 128).min(0).max(1024).json("targetSearchLength").build();
        public final IntegerEntry wanderFromHomeDistance = IntegerEntry.builder("config.iceandfire.dragon.wanderFromHomeDistance", 40).min(0).max(1024).json("wanderFromHomeDistance").build();
        public final IntegerEntry hungerTickRate = IntegerEntry.builder("config.iceandfire.dragon.hungerTickRate", 3000).min(1).json("hungerTickRate").build();
        public final DoubleEntry blockBreakingDropChance = DoubleEntry.builder("config.iceandfire.dragon.blockBreakingDropChance", 0.1).min(0).max(1).json("blockBreakingDropChance").build();
        public final BooleanEntry explosiveBreath = BooleanEntry.builder("config.iceandfire.dragon.explosiveBreath", false).json("explosiveBreath").build();
        public final BooleanEntry chunkLoadSummonCrystal = BooleanEntry.builder("config.iceandfire.dragon.chunkLoadSummonCrystal", true).json("chunkLoadSummonCrystal").build();
        public final DoubleEntry dragonFlightSpeedMod = DoubleEntry.builder("config.iceandfire.dragon.dragonFlightSpeedMod", 1).min(0.0001).max(50).json("dragonFlightSpeedMod").build();
        public final IntegerEntry maxTamedDragonAge = IntegerEntry.builder("config.iceandfire.dragon.maxTamedDragonAge", 128).min(0).max(128).json("maxTamedDragonAge").build();
        public final DoubleEntry maxBreathTimeMul = DoubleEntry.builder("config.iceandfire.dragon.maxBreathTimeMul", 2).min(0).json("maxBreathTimeMul").build();
        public final SeparatorEntry s3 = SeparatorEntry.builder().build();
        public final BooleanEntry lootSkull = BooleanEntry.builder("config.iceandfire.dragon.loot.skull", true).json("loot.skull").build();
        public final BooleanEntry lootHeart = BooleanEntry.builder("config.iceandfire.dragon.loot.heart", true).json("loot.heart").build();
        public final BooleanEntry lootBlood = BooleanEntry.builder("config.iceandfire.dragon.loot.blood", true).json("loot.blood").build();

        public DragonConfig() {
            super("dragon", "config.iceandfire.category.dragon");
        }
    }

    public static class HippogryphsConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry spawn = BooleanEntry.builder("config.iceandfire.hippogryphs.spawn", true).json("spawn").build();
        public final IntegerEntry spawnWeight = IntegerEntry.builder("config.iceandfire.hippogryphs.spawnWeight", 2).min(0).max(20).json("spawnWeight").build();
        public final DoubleEntry fightSpeedMod = DoubleEntry.builder("config.iceandfire.hippogryphs.fightSpeedMod", 1).min(0.0001).max(50).json("fightSpeedMod").build();

        public HippogryphsConfig() {
            super("hippogryphs", "config.iceandfire.category.hippogryphs");
        }
    }

    public static class PixieConfig extends AutoInitConfigCategoryBase {
        public final IntegerEntry size = IntegerEntry.builder("config.iceandfire.pixie.size", 5).min(0).max(100).json("size").build();
        public final BooleanEntry stealItems = BooleanEntry.builder("config.iceandfire.pixie.stealItems", false).json("stealItems").build();

        public PixieConfig() {
            super("pixie", "config.iceandfire.category.pixie");
        }
    }

    public static class CyclopsConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry spawnWanderingChance = DoubleEntry.builder("config.iceandfire.cyclops.spawnWanderingChance", 1.0 / 900).min(0).max(1).json("spawnWanderingChance").build();
        public final IntegerEntry sheepSearchLength = IntegerEntry.builder("config.iceandfire.cyclops.sheepSearchLength", 17).min(0).max(1024).json("sheepSearchLength").build();
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.cyclops.maxHealth", 150).min(1).json("maxHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.cyclops.attackDamage", 15).min(0).json("attackDamage").build();
        public final DoubleEntry biteDamage = DoubleEntry.builder("config.iceandfire.cyclops.biteDamage", 40).min(0).json("biteDamage").build();
        public final BooleanEntry griefing = BooleanEntry.builder("config.iceandfire.cyclops.griefing", true).json("griefing").build();

        public CyclopsConfig() {
            super("cyclops", "config.iceandfire.category.cyclops");
        }
    }

    public static class SirenConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.siren.maxHealth", 50).min(1).json("maxHealth").build();
        public final IntegerEntry maxSingTime = IntegerEntry.builder("config.iceandfire.siren.maxSingTime", 12000).min(0).json("maxSingTime").build();
        public final IntegerEntry timeBetweenSongs = IntegerEntry.builder("config.iceandfire.siren.timeBetweenSongs", 2000).min(0).json("timeBetweenSongs").build();

        public SirenConfig() {
            super("siren", "config.iceandfire.category.siren");
        }
    }

    public static class GorgonConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.gorgon.maxHealth", 100).min(1).json("maxHealth").build();

        public GorgonConfig() {
            super("gorgon", "config.iceandfire.category.gorgon");
        }
    }

    public static class DeathwormConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry spawnChance = DoubleEntry.builder("config.iceandfire.deathworm.spawnChance", 1.0 / 30).min(0).max(1).json("spawnChance").build();
        public final IntegerEntry targetSearchLength = IntegerEntry.builder("config.iceandfire.deathworm.targetSearchLength", 48).min(0).max(1024).json("targetSearchLength").build();
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.deathworm.maxHealth", 10).min(1).json("maxHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.deathworm.attackDamage", 3).min(0).max(30).json("attackDamage").build();
        public final BooleanEntry attackMonsters = BooleanEntry.builder("config.iceandfire.deathworm.attackMonsters", true).json("attackMonsters").build();

        public DeathwormConfig() {
            super("deathworm", "config.iceandfire.category.deathworm");
        }
    }

    public static class CockatriceConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry spawn = BooleanEntry.builder("config.iceandfire.cockatrice.spawn", true).json("spawn").build();
        public final IntegerEntry spawnWeight = IntegerEntry.builder("config.iceandfire.cockatrice.spawnWeight", 4).min(0).max(20).json("spawnWeight").build();
        public final IntegerEntry chickenSearchLength = IntegerEntry.builder("config.iceandfire.cockatrice.chickenSearchLength", 32).min(0).max(1024).json("chickenSearchLength").build();
        public final DoubleEntry eggChance = DoubleEntry.builder("config.iceandfire.cockatrice.eggChance", 1.0 / 30).min(0).max(1).json("eggChance").build();
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.cockatrice.maxHealth", 40).min(1).json("maxHealth").build();
        public final BooleanEntry chickensLayRottenEggs = BooleanEntry.builder("config.iceandfire.cockatrice.chickensLayRottenEggs", true).json("chickensLayRottenEggs").build();

        public CockatriceConfig() {
            super("cockatrice", "config.iceandfire.category.cockatrice");
        }
    }

    public static class StymphalianBirdConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry spawnChance = DoubleEntry.builder("config.iceandfire.bird.spawnChance", 1.0 / 80).min(0).max(1).json("spawnChance").build();
        public final IntegerEntry targetSearchLength = IntegerEntry.builder("config.iceandfire.bird.targetSearchLength", 48).min(0).max(1024).json("targetSearchLength").build();
        public final DoubleEntry featherDropChance = DoubleEntry.builder("config.iceandfire.bird.featherDropChance", 1.0 / 25).min(0).max(1).json("featherDropChance").build();
        public final DoubleEntry featherAttackDamage = DoubleEntry.builder("config.iceandfire.bird.featherAttackDamage", 1).min(0).json("featherAttackDamage").build();
        public final IntegerEntry flockLength = IntegerEntry.builder("config.iceandfire.bird.flockLength", 40).min(0).max(200).json("flockLength").build();
        public final IntegerEntry flightHeight = IntegerEntry.builder("config.iceandfire.bird.flightHeight", 80).min(-2032).max(2032).json("flightHeight").build();
        public final BooleanEntry attackAnimals = BooleanEntry.builder("config.iceandfire.bird.attackAnimals", false).json("attackAnimals").build();

        public StymphalianBirdConfig() {
            super("bird", "config.iceandfire.category.bird");
        }
    }

    public static class TrollConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry spawn = BooleanEntry.builder("config.iceandfire.troll.spawn", true).json("spawn").build();
        public final IntegerEntry spawnWeight = IntegerEntry.builder("config.iceandfire.troll.spawnWeight", 60).min(0).max(200).json("spawnWeight").build();
        public final BooleanEntry dropWeapon = BooleanEntry.builder("config.iceandfire.troll.dropWeapon", true).json("dropWeapon").build();
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.troll.maxHealth", 50).min(1).json("maxHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.troll.attackDamage", 10).min(0).json("attackDamage").build();

        public TrollConfig() {
            super("troll", "config.iceandfire.category.troll");
        }
    }

    public static class AmphithereConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry spawn = BooleanEntry.builder("config.iceandfire.amphithere.spawn", true).json("spawn").build();
        public final IntegerEntry spawnWeight = IntegerEntry.builder("config.iceandfire.amphithere.spawnWeight", 50).min(0).max(400).json("spawnWeight").build();
        public final DoubleEntry villagerSearchLength = DoubleEntry.builder("config.iceandfire.amphithere.villagerSearchLength", 48).min(0).max(1024).json("villagerSearchLength").build();
        public final IntegerEntry tameTime = IntegerEntry.builder("config.iceandfire.amphithere.tameTime", 400).min(0).json("tameTime").build();
        public final DoubleEntry flightSpeed = DoubleEntry.builder("config.iceandfire.amphithere.flightSpeed", 1.75).min(0).max(20).json("flightSpeed").build();
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.amphithere.maxHealth", 50).min(1).json("maxHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.amphithere.attackDamage", 7).min(0).json("attackDamage").build();

        public AmphithereConfig() {
            super("amphithere", "config.iceandfire.category.amphithere");
        }
    }

    public static class SeaSerpentConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry spawnChance = DoubleEntry.builder("config.iceandfire.seaSerpent.spawnChance", 1.0 / 250).min(0).max(1).json("spawnChance").build();
        public final BooleanEntry griefing = BooleanEntry.builder("config.iceandfire.seaSerpent.griefing", true).json("griefing").build();
        public final DoubleEntry baseHealth = DoubleEntry.builder("config.iceandfire.seaSerpent.baseHealth", 20).min(0).json("baseHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.seaSerpent.attackDamage", 4).min(0).json("attackDamage").build();

        public SeaSerpentConfig() {
            super("seaSerpent", "config.iceandfire.category.seaSerpent");
        }
    }

    public static class LichConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry spawn = BooleanEntry.builder("config.iceandfire.lich.spawn", true).json("spawn").build();
        public final IntegerEntry spawnWeight = IntegerEntry.builder("config.iceandfire.lich.spawnWeight", 4).min(0).max(20).json("spawnWeight").build();
        public final DoubleEntry spawnChance = DoubleEntry.builder("config.iceandfire.lich.spawnChance", 1.0 / 30).min(0).max(1).json("spawnChance").build();

        public LichConfig() {
            super("lich", "config.iceandfire.category.lich");
        }
    }

    public static class HydraConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.hydra.maxHealth", 250).min(1).json("maxHealth").build();

        public HydraConfig() {
            super("hydra", "config.iceandfire.category.hydra");
        }
    }

    public static class HippocampusConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry spawnChance = DoubleEntry.builder("config.iceandfire.hippocampus.spawnChance", 1.0 / 40).min(0).max(1).json("spawnChance").build();
        public final DoubleEntry swimSpeedMod = DoubleEntry.builder("config.iceandfire.hippocampus.swimSpeedMod", 1).min(0.0001).max(10).json("swimSpeedMod").build();

        public HippocampusConfig() {
            super("hippocampus", "config.iceandfire.category.hippocampus");
        }
    }

    public static class GhostConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry maxHealth = DoubleEntry.builder("config.iceandfire.ghost.maxHealth", 30).min(1).json("maxHealth").build();
        public final DoubleEntry attackDamage = DoubleEntry.builder("config.iceandfire.ghost.attackDamage", 3).min(0).json("attackDamage").build();
        public final BooleanEntry fromPlayerDeaths = BooleanEntry.builder("config.iceandfire.ghost.fromPlayerDeaths", true).json("fromPlayerDeaths").build();

        public GhostConfig() {
            super("ghost", "config.iceandfire.category.ghost");
        }
    }

    @SuppressWarnings("unused")
    public static class ToolsConfig extends AutoInitConfigCategoryBase {
        public final BooleanEntry dragonFireAbility = BooleanEntry.builder("config.iceandfire.tools.dragonFireAbility", true).json("dragonFireAbility").build();
        public final BooleanEntry dragonIceAbility = BooleanEntry.builder("config.iceandfire.tools.dragonIceAbility", true).json("dragonIceAbility").build();
        public final BooleanEntry dragonLightningAbility = BooleanEntry.builder("config.iceandfire.tools.dragonLightningAbility", true).json("dragonLightningAbility").build();
        public final IntegerEntry dragonsteelFireDuration = IntegerEntry.builder("config.iceandfire.tools.dragonsteelFireDuration", 15).min(0).json("dragonsteelFireDuration").build();
        public final IntegerEntry dragonBloodFireDuration = IntegerEntry.builder("config.iceandfire.tools.dragonBloodFireDuration", 5).min(0).json("dragonBloodFireDuration").build();
        public final IntegerEntry dragonsteelFrozenDuration = IntegerEntry.builder("config.iceandfire.tools.dragonsteelFrozenDuration", 300).min(0).json("dragonsteelFrozenDuration").build();
        public final IntegerEntry dragonBloodFrozenDuration = IntegerEntry.builder("config.iceandfire.tools.dragonBloodFrozenDuration", 100).min(0).json("dragonBloodFrozenDuration").build();
        public final BooleanEntry phantasmalBladeAbility = BooleanEntry.builder("config.iceandfire.tools.phantasmalBladeAbility", true).json("phantasmalBladeAbility").build();
        public final SeparatorEntry s1 = SeparatorEntry.builder().build();
        public final DoubleEntry dragonLightningSearchRange = DoubleEntry.builder("config.iceandfire.tools.dragonLightningSearchRange", 10).min(0).max(128).json("dragonLightningSearchRange").build();
        public final DoubleEntry dragonLightningDamageReduction = DoubleEntry.builder("config.iceandfire.tools.dragonLightningDamageReduction", 0.5).min(0).max(1).json("dragonLightningDamageReduction").build();
        public final IntegerEntry dragonLightningMaxSearchCount = IntegerEntry.builder("config.iceandfire.tools.dragonLightningMaxSearchCount", 10).min(0).max(1024).json("dragonLightningMaxSearchCount").build();

        public ToolsConfig() {
            super("tools", "config.iceandfire.category.tools");
        }
    }

    @SuppressWarnings("unused")
    public static class ArmorsConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry dragonSteelBaseDamage = DoubleEntry.builder("config.iceandfire.armors.dragonSteelBaseDamage", 25).min(0).json("dragonSteelBaseDamage").build();
        public final IntegerEntry dragonSteelBaseDurability = IntegerEntry.builder("config.iceandfire.armors.dragonSteelBaseDurability", 8000).min(0).json("dragonSteelBaseDurability").build();
        public final DoubleEntry dragonsteelArmorToughness = DoubleEntry.builder("config.iceandfire.armors.dragonsteelArmorToughness", 6).min(0).json("dragonsteelArmorToughness").build();
        public final IntegerEntry dragonsteelHelmetArmor = IntegerEntry.builder("config.iceandfire.armors.dragonsteelHelmetArmor", 7).min(0).json("dragonsteelHelmetArmor").build();
        public final IntegerEntry dragonsteelHelmetDurability = IntegerEntry.builder("config.iceandfire.armors.dragonsteelHelmetDurability", 1760).min(0).json("dragonsteelHelmetDurability").build();
        public final IntegerEntry dragonsteelChestplateArmor = IntegerEntry.builder("config.iceandfire.armors.dragonsteelChestplateArmor", 12).min(0).json("dragonsteelChestplateArmor").build();
        public final IntegerEntry dragonsteelChestplateDurability = IntegerEntry.builder("config.iceandfire.armors.dragonsteelChestplateDurability", 2560).min(0).json("dragonsteelChestplateDurability").build();
        public final IntegerEntry dragonsteelLeggingsArmor = IntegerEntry.builder("config.iceandfire.armors.dragonsteelLeggingsArmor", 9).min(0).json("dragonsteelLeggingsArmor").build();
        public final IntegerEntry dragonsteelLeggingsDurability = IntegerEntry.builder("config.iceandfire.armors.dragonsteelLeggingsDurability", 2400).min(0).json("dragonsteelLeggingsDurability").build();
        public final IntegerEntry dragonsteelBootsArmor = IntegerEntry.builder("config.iceandfire.armors.dragonsteelBootsArmor", 6).min(0).json("dragonsteelBootsArmor").build();
        public final IntegerEntry dragonsteelBootsDurability = IntegerEntry.builder("config.iceandfire.armors.dragonsteelBootsDurability", 2080).min(0).json("dragonsteelBootsDurability").build();
        public final IntegerEntry dragonsteelArmorEnchantability = IntegerEntry.builder("config.iceandfire.armors.dragonsteelArmorEnchantability", 30).min(0).json("dragonsteelArmorEnchantability").build();
        public final DoubleEntry dragonsteelArmorKnockbackResistance = DoubleEntry.builder("config.iceandfire.armors.dragonsteelArmorKnockbackResistance", 0.1).min(0).max(0.25).json("dragonsteelArmorKnockbackResistance").build();

        public ArmorsConfig() {
            super("armors", "config.iceandfire.category.armors");
        }
    }

    public static class WorldGenConfig extends AutoInitConfigCategoryBase {
        public final DoubleEntry dangerousDistanceLimit = DoubleEntry.builder("config.iceandfire.worldgen.dangerousDistanceLimit", 1000).min(0).json("dangerousDistanceLimit").build();
        public final DoubleEntry generateFireDragonCaveChance = DoubleEntry.builder("config.iceandfire.worldgen.generateFireDragonCaveChance", 1).min(0).max(1).json("generateFireDragonCaveChance").build();
        public final DoubleEntry generateFireDragonRoostChance = DoubleEntry.builder("config.iceandfire.worldgen.generateFireDragonRoostChance", 1).min(0).max(1).json("generateFireDragonRoostChance").build();
        public final DoubleEntry generateIceDragonCaveChance = DoubleEntry.builder("config.iceandfire.worldgen.generateIceDragonCaveChance", 1).min(0).max(1).json("generateIceDragonCaveChance").build();
        public final DoubleEntry generateIceDragonRoostChance = DoubleEntry.builder("config.iceandfire.worldgen.generateIceDragonRoostChance", 1).min(0).max(1).json("generateIceDragonRoostChance").build();
        public final DoubleEntry generateLightningDragonCaveChance = DoubleEntry.builder("config.iceandfire.worldgen.generateLightningDragonCaveChance", 1).min(0).max(1).json("generateLightningDragonCaveChance").build();
        public final DoubleEntry generateLightningDragonRoostChance = DoubleEntry.builder("config.iceandfire.worldgen.generateLightningDragonRoostChance", 1).min(0).max(1).json("generateLightningDragonRoostChance").build();
        public final DoubleEntry generateCyclopsCaveChance = DoubleEntry.builder("config.iceandfire.worldgen.generateCyclopsCaveChance", 1).min(0).max(1).json("generateCyclopsCaveChance").build();
        public final DoubleEntry generateGorgonTempleChance = DoubleEntry.builder("config.iceandfire.worldgen.generateGorgonTempleChance", 1).min(0).max(1).json("generateGorgonTempleChance").build();
        public final DoubleEntry generateGraveYardChance = DoubleEntry.builder("config.iceandfire.worldgen.generateGraveYardChance", 1).min(0).max(1).json("generateGraveYardChance").build();
        public final DoubleEntry generateHydraCaveChance = DoubleEntry.builder("config.iceandfire.worldgen.generateHydraCaveChance", 1).min(0).max(1).json("generateHydraCaveChance").build();
        public final DoubleEntry generateMausoleumChance = DoubleEntry.builder("config.iceandfire.worldgen.generateMausoleumChance", 1).min(0).max(1).json("generateMausoleumChance").build();
        public final DoubleEntry generatePixieVillageChance = DoubleEntry.builder("config.iceandfire.worldgen.generatePixieVillageChance", 1).min(0).max(1).json("generatePixieVillageChance").build();
        public final DoubleEntry generateSirenIslandChance = DoubleEntry.builder("config.iceandfire.worldgen.generateSirenIslandChance", 1).min(0).max(1).json("generateSirenIslandChance").build();

        public WorldGenConfig() {
            super("worldgen", "config.iceandfire.category.worldgen");
        }
    }

    public static class Misc extends AutoInitConfigCategoryBase {
        public final BooleanEntry enableDragonSeeker = BooleanEntry.builder("config.iceandfire.misc.enableDragonSeeker", true).json("enableDragonSeeker").build();
        public final DoubleEntry dreadQueenMaxHealth = DoubleEntry.builder("config.iceandfire.misc.dreadQueenMaxHealth", 750).min(0).json("dreadQueenMaxHealth").build();

        public Misc() {
            super("misc", "config.iceandfire.category.misc");
        }
    }
}
