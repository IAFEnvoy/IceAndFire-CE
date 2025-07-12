package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.*;
import com.iafenvoy.iceandfire.registry.tag.IafBiomeTags;
import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.SpawnSettings;

import java.util.function.Supplier;

public final class IafEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<EntityDragonPart>> DRAGON_MULTIPART = build("dragon_multipart", EntityDragonPart::new, SpawnGroup.MISC, true, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntitySlowPart>> SLOW_MULTIPART = build("multipart", EntitySlowPart::new, SpawnGroup.MISC, true, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityHydraHead>> HYDRA_MULTIPART = build("hydra_multipart", EntityHydraHead::new, SpawnGroup.MISC, true, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityCyclopsEye>> CYCLOPS_MULTIPART = build("cylcops_multipart", EntityCyclopsEye::new, SpawnGroup.MISC, true, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityDragonEgg>> DRAGON_EGG = build("dragon_egg", EntityDragonEgg::new, SpawnGroup.MISC, true, 0.45F, 0.55F);
    public static final RegistrySupplier<EntityType<EntityDragonArrow>> DRAGON_ARROW = build("dragon_arrow", EntityDragonArrow::new, SpawnGroup.MISC, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityDragonSkull>> DRAGON_SKULL = build("dragon_skull", EntityDragonSkull::new, SpawnGroup.MISC, false, 0.9F, 0.65F);
    public static final RegistrySupplier<EntityType<EntityFireDragon>> FIRE_DRAGON = build("fire_dragon", EntityFireDragon::new, SpawnGroup.CREATURE, true, 0.78F, 1.2F, 256);
    public static final RegistrySupplier<EntityType<EntityIceDragon>> ICE_DRAGON = build("ice_dragon", EntityIceDragon::new, SpawnGroup.CREATURE, false, 0.78F, 1.2F, 256);
    public static final RegistrySupplier<EntityType<EntityLightningDragon>> LIGHTNING_DRAGON = build("lightning_dragon", EntityLightningDragon::new, SpawnGroup.CREATURE, false, 0.78F, 1.2F, 256);
    public static final RegistrySupplier<EntityType<EntityDragonFireCharge>> FIRE_DRAGON_CHARGE = build("fire_dragon_charge", EntityDragonFireCharge::new, SpawnGroup.MISC, false, 0.9F, 0.9F);
    public static final RegistrySupplier<EntityType<EntityDragonIceCharge>> ICE_DRAGON_CHARGE = build("ice_dragon_charge", EntityDragonIceCharge::new, SpawnGroup.MISC, false, 0.9F, 0.9F);
    public static final RegistrySupplier<EntityType<EntityDragonLightningCharge>> LIGHTNING_DRAGON_CHARGE = build("lightning_dragon_charge", EntityDragonLightningCharge::new, SpawnGroup.MISC, false, 0.9F, 0.9F);
    public static final RegistrySupplier<EntityType<EntityHippogryphEgg>> HIPPOGRYPH_EGG = build("hippogryph_egg", EntityHippogryphEgg::new, SpawnGroup.MISC, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityHippogryph>> HIPPOGRYPH = build("hippogryph", EntityHippogryph::new, SpawnGroup.CREATURE, false, 1.7F, 1.6F, 128);
    public static final RegistrySupplier<EntityType<EntityStoneStatue>> STONE_STATUE = build("stone_statue", EntityStoneStatue::new, SpawnGroup.CREATURE, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityGorgon>> GORGON = build("gorgon", EntityGorgon::new, SpawnGroup.CREATURE, false, 0.8F, 1.99F);
    public static final RegistrySupplier<EntityType<EntityPixie>> PIXIE = build("pixie", EntityPixie::new, SpawnGroup.CREATURE, false, 0.4F, 0.8F);
    public static final RegistrySupplier<EntityType<EntityCyclops>> CYCLOPS = build("cyclops", EntityCyclops::new, SpawnGroup.CREATURE, false, 1.95F, 7.4F);
    public static final RegistrySupplier<EntityType<EntitySiren>> SIREN = build("siren", EntitySiren::new, SpawnGroup.CREATURE, false, 1.6F, 0.9F);
    public static final RegistrySupplier<EntityType<EntityHippocampus>> HIPPOCAMPUS = build("hippocampus", EntityHippocampus::new, SpawnGroup.WATER_CREATURE, false, 1.95F, 0.95F);
    public static final RegistrySupplier<EntityType<EntityDeathWorm>> DEATH_WORM = build("deathworm", EntityDeathWorm::new, SpawnGroup.CREATURE, false, 0.8F, 0.8F, 128);
    public static final RegistrySupplier<EntityType<EntityDeathWormEgg>> DEATH_WORM_EGG = build("deathworm_egg", EntityDeathWormEgg::new, SpawnGroup.MISC, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityCockatrice>> COCKATRICE = build("cockatrice", EntityCockatrice::new, SpawnGroup.CREATURE, false, 1.1F, 1F);
    public static final RegistrySupplier<EntityType<EntityCockatriceEgg>> COCKATRICE_EGG = build("cockatrice_egg", EntityCockatriceEgg::new, SpawnGroup.MISC, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityStymphalianBird>> STYMPHALIAN_BIRD = build("stymphalian_bird", EntityStymphalianBird::new, SpawnGroup.CREATURE, false, 1.3F, 1.2F, 128);
    public static final RegistrySupplier<EntityType<EntityStymphalianFeather>> STYMPHALIAN_FEATHER = build("stymphalian_feather", EntityStymphalianFeather::new, SpawnGroup.MISC, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityStymphalianArrow>> STYMPHALIAN_ARROW = build("stymphalian_arrow", EntityStymphalianArrow::new, SpawnGroup.MISC, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityTroll>> TROLL = build("troll", EntityTroll::new, SpawnGroup.MONSTER, false, 1.2F, 3.5F);
    public static final RegistrySupplier<EntityType<EntityAmphithere>> AMPHITHERE = build("amphithere", EntityAmphithere::new, SpawnGroup.CREATURE, false, 2.5F, 1.25F, 128);
    public static final RegistrySupplier<EntityType<EntityAmphithereArrow>> AMPHITHERE_ARROW = build("amphithere_arrow", EntityAmphithereArrow::new, SpawnGroup.MISC, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntitySeaSerpent>> SEA_SERPENT = build("sea_serpent", EntitySeaSerpent::new, SpawnGroup.CREATURE, false, 0.5F, 0.5F, 256);
    public static final RegistrySupplier<EntityType<EntitySeaSerpentBubbles>> SEA_SERPENT_BUBBLES = build("sea_serpent_bubbles", EntitySeaSerpentBubbles::new, SpawnGroup.MISC, false, 0.9F, 0.9F);
    public static final RegistrySupplier<EntityType<EntitySeaSerpentArrow>> SEA_SERPENT_ARROW = build("sea_serpent_arrow", EntitySeaSerpentArrow::new, SpawnGroup.MISC, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityChainTie>> CHAIN_TIE = build("chain_tie", EntityChainTie::new, SpawnGroup.MISC, false, 0.8F, 0.9F);
    public static final RegistrySupplier<EntityType<EntityPixieCharge>> PIXIE_CHARGE = build("pixie_charge", EntityPixieCharge::new, SpawnGroup.MISC, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityTideTrident>> TIDE_TRIDENT = build("tide_trident", EntityTideTrident::new, SpawnGroup.MISC, false, 0.85F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityMobSkull>> MOB_SKULL = build("mob_skull", EntityMobSkull::new, SpawnGroup.MISC, false, 0.85F, 0.85F);
    public static final RegistrySupplier<EntityType<EntityDreadThrall>> DREAD_THRALL = build("dread_thrall", EntityDreadThrall::new, SpawnGroup.MONSTER, false, 0.6F, 1.8F);
    public static final RegistrySupplier<EntityType<EntityDreadGhoul>> DREAD_GHOUL = build("dread_ghoul", EntityDreadGhoul::new, SpawnGroup.MONSTER, false, 0.6F, 1.8F);
    public static final RegistrySupplier<EntityType<EntityDreadBeast>> DREAD_BEAST = build("dread_beast", EntityDreadBeast::new, SpawnGroup.MONSTER, false, 1.2F, 0.9F);
    public static final RegistrySupplier<EntityType<EntityDreadScuttler>> DREAD_SCUTTLER = build("dread_scuttler", EntityDreadScuttler::new, SpawnGroup.MONSTER, false, 1.5F, 1.3F);
    public static final RegistrySupplier<EntityType<EntityDreadLich>> DREAD_LICH = build("dread_lich", EntityDreadLich::new, SpawnGroup.MONSTER, false, 0.6F, 1.8F);
    public static final RegistrySupplier<EntityType<EntityDreadLichSkull>> DREAD_LICH_SKULL = build("dread_lich_skull", EntityDreadLichSkull::new, SpawnGroup.MISC, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityDreadKnight>> DREAD_KNIGHT = build("dread_knight", EntityDreadKnight::new, SpawnGroup.MONSTER, false, 0.6F, 1.8F);
    public static final RegistrySupplier<EntityType<EntityDreadHorse>> DREAD_HORSE = build("dread_horse", EntityDreadHorse::new, SpawnGroup.MONSTER, false, 1.3964844F, 1.6F);
    public static final RegistrySupplier<EntityType<EntityHydra>> HYDRA = build("hydra", EntityHydra::new, SpawnGroup.CREATURE, false, 2.8F, 1.39F);
    public static final RegistrySupplier<EntityType<EntityHydraBreath>> HYDRA_BREATH = build("hydra_breath", EntityHydraBreath::new, SpawnGroup.MISC, false, 0.9F, 0.9F);
    public static final RegistrySupplier<EntityType<EntityHydraArrow>> HYDRA_ARROW = build("hydra_arrow", EntityHydraArrow::new, SpawnGroup.MISC, false, 0.5F, 0.5F);
    public static final RegistrySupplier<EntityType<EntityGhost>> GHOST = build("ghost", EntityGhost::new, SpawnGroup.MONSTER, true, 0.8F, 1.9F);
    public static final RegistrySupplier<EntityType<EntityGhostSword>> GHOST_SWORD = build("ghost_sword", EntityGhostSword::new, SpawnGroup.MISC, false, 0.5F, 0.5F);

    private static <T extends Entity> RegistrySupplier<EntityType<T>> build(String entityName, EntityType.EntityFactory<T> constructor, SpawnGroup category, boolean fireImmune, float sizeX, float sizeY) {
        EntityType.Builder<T> builder = EntityType.Builder.create(constructor, category).setDimensions(sizeX, sizeY);
        if (fireImmune) builder.makeFireImmune();
        return register(entityName, () -> builder.build(entityName));
    }

    private static <T extends Entity> RegistrySupplier<EntityType<T>> build(String entityName, EntityType.EntityFactory<T> constructor, SpawnGroup category, boolean fireImmune, float sizeX, float sizeY, int trackingRange) {
        EntityType.Builder<T> builder = EntityType.Builder.create(constructor, category).setDimensions(sizeX, sizeY).maxTrackingRange(trackingRange);
        if (fireImmune) builder.makeFireImmune();
        return register(entityName, () -> builder.build(entityName));
    }

    private static <T extends Entity> RegistrySupplier<EntityType<T>> register(String entityName, Supplier<EntityType<T>> builder) {
        return REGISTRY.register(entityName, builder);
    }

    public static void init() {
        addSpawners();
        commonSetup();
    }

    public static void bakeAttributes() {
        EntityAttributeRegistry.register(DRAGON_EGG, EntityDragonEgg::bakeAttributes);
        EntityAttributeRegistry.register(DRAGON_SKULL, EntityDragonSkull::bakeAttributes);
        EntityAttributeRegistry.register(FIRE_DRAGON, EntityFireDragon::bakeAttributes);
        EntityAttributeRegistry.register(ICE_DRAGON, EntityIceDragon::bakeAttributes);
        EntityAttributeRegistry.register(LIGHTNING_DRAGON, EntityLightningDragon::bakeAttributes);
        EntityAttributeRegistry.register(HIPPOGRYPH, EntityHippogryph::bakeAttributes);
        EntityAttributeRegistry.register(GORGON, EntityGorgon::bakeAttributes);
        EntityAttributeRegistry.register(STONE_STATUE, EntityStoneStatue::bakeAttributes);
        EntityAttributeRegistry.register(PIXIE, EntityPixie::bakeAttributes);
        EntityAttributeRegistry.register(CYCLOPS, EntityCyclops::bakeAttributes);
        EntityAttributeRegistry.register(SIREN, EntitySiren::bakeAttributes);
        EntityAttributeRegistry.register(HIPPOCAMPUS, EntityHippocampus::bakeAttributes);
        EntityAttributeRegistry.register(DEATH_WORM, EntityDeathWorm::bakeAttributes);
        EntityAttributeRegistry.register(COCKATRICE, EntityCockatrice::bakeAttributes);
        EntityAttributeRegistry.register(STYMPHALIAN_BIRD, EntityStymphalianBird::bakeAttributes);
        EntityAttributeRegistry.register(TROLL, EntityTroll::bakeAttributes);
        EntityAttributeRegistry.register(AMPHITHERE, EntityAmphithere::bakeAttributes);
        EntityAttributeRegistry.register(SEA_SERPENT, EntitySeaSerpent::bakeAttributes);
        EntityAttributeRegistry.register(MOB_SKULL, EntityMobSkull::bakeAttributes);
        EntityAttributeRegistry.register(DREAD_THRALL, EntityDreadThrall::bakeAttributes);
        EntityAttributeRegistry.register(DREAD_LICH, EntityDreadLich::bakeAttributes);
        EntityAttributeRegistry.register(DREAD_BEAST, EntityDreadBeast::bakeAttributes);
        EntityAttributeRegistry.register(DREAD_HORSE, EntityDreadHorse::bakeAttributes);
        EntityAttributeRegistry.register(DREAD_GHOUL, EntityDreadGhoul::bakeAttributes);
        EntityAttributeRegistry.register(DREAD_KNIGHT, EntityDreadKnight::bakeAttributes);
        EntityAttributeRegistry.register(DREAD_SCUTTLER, EntityDreadScuttler::bakeAttributes);
        EntityAttributeRegistry.register(HYDRA, EntityHydra::bakeAttributes);
        EntityAttributeRegistry.register(GHOST, EntityGhost::bakeAttributes);
    }

    public static void commonSetup() {
        SpawnRestriction.register(HIPPOGRYPH.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityHippogryph::canMobSpawn);
        SpawnRestriction.register(TROLL.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityTroll::canTrollSpawnOn);
        SpawnRestriction.register(DREAD_LICH.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityDreadLich::canLichSpawnOn);
        SpawnRestriction.register(COCKATRICE.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityCockatrice::canMobSpawn);
        SpawnRestriction.register(AMPHITHERE.get(), SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING, EntityAmphithere::canAmphithereSpawnOn);
    }

    public static void addSpawners() {
        if (IafCommonConfig.INSTANCE.hippogryphs.spawn.getValue())
            BiomeModifications.addProperties(context -> context.hasTag(IafBiomeTags.HIPPOGRYPH), (context, mutable) -> mutable.getSpawnProperties().addSpawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(IafEntities.HIPPOGRYPH.get(), IafCommonConfig.INSTANCE.hippogryphs.spawnWeight.getValue(), 1, 1)));
        if (IafCommonConfig.INSTANCE.lich.spawn.getValue())
            BiomeModifications.addProperties(context -> context.hasTag(IafBiomeTags.MAUSOLEUM), (context, mutable) -> mutable.getSpawnProperties().addSpawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(IafEntities.DREAD_LICH.get(), IafCommonConfig.INSTANCE.lich.spawnWeight.getValue(), 1, 1)));
        if (IafCommonConfig.INSTANCE.cockatrice.spawn.getValue())
            BiomeModifications.addProperties(context -> context.hasTag(IafBiomeTags.COCKATRICE), (context, mutable) -> mutable.getSpawnProperties().addSpawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(IafEntities.COCKATRICE.get(), IafCommonConfig.INSTANCE.cockatrice.spawnWeight.getValue(), 1, 2)));
        if (IafCommonConfig.INSTANCE.amphithere.spawn.getValue())
            BiomeModifications.addProperties(context -> context.hasTag(IafBiomeTags.AMPHITHERE), (context, mutable) -> mutable.getSpawnProperties().addSpawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(IafEntities.AMPHITHERE.get(), IafCommonConfig.INSTANCE.amphithere.spawnWeight.getValue(), 1, 3)));
        if (IafCommonConfig.INSTANCE.troll.spawn.getValue())
            BiomeModifications.addProperties(context -> context.hasTag(IafBiomeTags.TROLL), (context, mutable) -> mutable.getSpawnProperties().addSpawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(IafEntities.TROLL.get(), IafCommonConfig.INSTANCE.troll.spawnWeight.getValue(), 1, 3)));
    }
}
