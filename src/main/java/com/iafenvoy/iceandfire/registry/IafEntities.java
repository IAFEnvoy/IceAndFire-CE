package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber
public final class IafEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, IceAndFire.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<DragonPartEntity>> DRAGON_MULTIPART = build("dragon_multipart", DragonPartEntity::new, MobCategory.MISC, true, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<SlowPartEntity>> SLOW_MULTIPART = build("multipart", SlowPartEntity::new, MobCategory.MISC, true, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<HydraHeadEntity>> HYDRA_MULTIPART = build("hydra_multipart", HydraHeadEntity::new, MobCategory.MISC, true, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<CyclopsEyeEntity>> CYCLOPS_MULTIPART = build("cylcops_multipart", CyclopsEyeEntity::new, MobCategory.MISC, true, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<DragonEggEntity>> DRAGON_EGG = build("dragon_egg", DragonEggEntity::new, MobCategory.MISC, true, 0.45F, 0.55F);
    public static final DeferredHolder<EntityType<?>, EntityType<DragonArrowEntity>> DRAGON_ARROW = build("dragon_arrow", DragonArrowEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<DragonSkullEntity>> DRAGON_SKULL = build("dragon_skull", DragonSkullEntity::new, MobCategory.MISC, false, 0.9F, 0.65F);
    public static final DeferredHolder<EntityType<?>, EntityType<FireDragonEntity>> FIRE_DRAGON = build("fire_dragon", FireDragonEntity::new, MobCategory.CREATURE, true, 0.78F, 1.2F, 256);
    public static final DeferredHolder<EntityType<?>, EntityType<IceDragonEntity>> ICE_DRAGON = build("ice_dragon", IceDragonEntity::new, MobCategory.CREATURE, false, 0.78F, 1.2F, 256);
    public static final DeferredHolder<EntityType<?>, EntityType<LightningDragonEntity>> LIGHTNING_DRAGON = build("lightning_dragon", LightningDragonEntity::new, MobCategory.CREATURE, false, 0.78F, 1.2F, 256);
    public static final DeferredHolder<EntityType<?>, EntityType<FireDragonChargeEntity>> FIRE_DRAGON_CHARGE = build("fire_dragon_charge", FireDragonChargeEntity::new, MobCategory.MISC, false, 0.9F, 0.9F);
    public static final DeferredHolder<EntityType<?>, EntityType<IceDragonChargeEntity>> ICE_DRAGON_CHARGE = build("ice_dragon_charge", IceDragonChargeEntity::new, MobCategory.MISC, false, 0.9F, 0.9F);
    public static final DeferredHolder<EntityType<?>, EntityType<LightningDragonChargeEntity>> LIGHTNING_DRAGON_CHARGE = build("lightning_dragon_charge", LightningDragonChargeEntity::new, MobCategory.MISC, false, 0.9F, 0.9F);
    public static final DeferredHolder<EntityType<?>, EntityType<HippogryphEggEntity>> HIPPOGRYPH_EGG = build("hippogryph_egg", HippogryphEggEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<HippogryphEntity>> HIPPOGRYPH = build("hippogryph", HippogryphEntity::new, MobCategory.CREATURE, false, 1.7F, 1.6F, 128);
    public static final DeferredHolder<EntityType<?>, EntityType<StoneStatueEntity>> STONE_STATUE = build("stone_statue", StoneStatueEntity::new, MobCategory.CREATURE, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<GorgonEntity>> GORGON = build("gorgon", GorgonEntity::new, MobCategory.CREATURE, false, 0.8F, 1.99F);
    public static final DeferredHolder<EntityType<?>, EntityType<PixieEntity>> PIXIE = build("pixie", PixieEntity::new, MobCategory.CREATURE, false, 0.4F, 0.8F);
    public static final DeferredHolder<EntityType<?>, EntityType<CyclopsEntity>> CYCLOPS = build("cyclops", CyclopsEntity::new, MobCategory.CREATURE, false, 1.95F, 7.4F);
    public static final DeferredHolder<EntityType<?>, EntityType<SirenEntity>> SIREN = build("siren", SirenEntity::new, MobCategory.CREATURE, false, 1.6F, 0.9F);
    public static final DeferredHolder<EntityType<?>, EntityType<HippocampusEntity>> HIPPOCAMPUS = build("hippocampus", HippocampusEntity::new, MobCategory.WATER_CREATURE, false, 1.95F, 0.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<DeathWormEntity>> DEATH_WORM = build("deathworm", DeathWormEntity::new, MobCategory.CREATURE, false, 0.8F, 0.8F, 128);
    public static final DeferredHolder<EntityType<?>, EntityType<DeathWormEggEntity>> DEATH_WORM_EGG = build("deathworm_egg", DeathWormEggEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<CockatriceEntity>> COCKATRICE = build("cockatrice", CockatriceEntity::new, MobCategory.CREATURE, false, 1.1F, 1F);
    public static final DeferredHolder<EntityType<?>, EntityType<CockatriceEggEntity>> COCKATRICE_EGG = build("cockatrice_egg", CockatriceEggEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<StymphalianBirdEntity>> STYMPHALIAN_BIRD = build("stymphalian_bird", StymphalianBirdEntity::new, MobCategory.CREATURE, false, 1.3F, 1.2F, 128);
    public static final DeferredHolder<EntityType<?>, EntityType<StymphalianFeatherEntity>> STYMPHALIAN_FEATHER = build("stymphalian_feather", StymphalianFeatherEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<StymphalianArrowEntity>> STYMPHALIAN_ARROW = build("stymphalian_arrow", StymphalianArrowEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<TrollEntity>> TROLL = build("troll", TrollEntity::new, MobCategory.MONSTER, false, 1.2F, 3.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<AmphithereEntity>> AMPHITHERE = build("amphithere", AmphithereEntity::new, MobCategory.CREATURE, false, 2.5F, 1.25F, 128);
    public static final DeferredHolder<EntityType<?>, EntityType<AmphithereArrowEntity>> AMPHITHERE_ARROW = build("amphithere_arrow", AmphithereArrowEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<SeaSerpentEntity>> SEA_SERPENT = build("sea_serpent", SeaSerpentEntity::new, MobCategory.CREATURE, false, 0.5F, 0.5F, 256);
    public static final DeferredHolder<EntityType<?>, EntityType<SeaSerpentBubblesEntity>> SEA_SERPENT_BUBBLES = build("sea_serpent_bubbles", SeaSerpentBubblesEntity::new, MobCategory.MISC, false, 0.9F, 0.9F);
    public static final DeferredHolder<EntityType<?>, EntityType<SeaSerpentArrowEntity>> SEA_SERPENT_ARROW = build("sea_serpent_arrow", SeaSerpentArrowEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<ChainTieEntity>> CHAIN_TIE = build("chain_tie", ChainTieEntity::new, MobCategory.MISC, false, 0.8F, 0.9F);
    public static final DeferredHolder<EntityType<?>, EntityType<PixieChargeEntity>> PIXIE_CHARGE = build("pixie_charge", PixieChargeEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<TideTridentEntity>> TIDE_TRIDENT = build("tide_trident", TideTridentEntity::new, MobCategory.MISC, false, 0.85F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<MobSkullEntity>> MOB_SKULL = build("mob_skull", MobSkullEntity::new, MobCategory.MISC, false, 0.85F, 0.85F);
    public static final DeferredHolder<EntityType<?>, EntityType<DreadThrallEntity>> DREAD_THRALL = build("dread_thrall", DreadThrallEntity::new, MobCategory.MONSTER, false, 0.6F, 1.8F);
    public static final DeferredHolder<EntityType<?>, EntityType<DreadGhoulEntity>> DREAD_GHOUL = build("dread_ghoul", DreadGhoulEntity::new, MobCategory.MONSTER, false, 0.6F, 1.8F);
    public static final DeferredHolder<EntityType<?>, EntityType<DreadBeastEntity>> DREAD_BEAST = build("dread_beast", DreadBeastEntity::new, MobCategory.MONSTER, false, 1.2F, 0.9F);
    public static final DeferredHolder<EntityType<?>, EntityType<DreadScuttlerEntity>> DREAD_SCUTTLER = build("dread_scuttler", DreadScuttlerEntity::new, MobCategory.MONSTER, false, 1.5F, 1.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<DreadLichEntity>> DREAD_LICH = build("dread_lich", DreadLichEntity::new, MobCategory.MONSTER, false, 0.6F, 1.8F);
    public static final DeferredHolder<EntityType<?>, EntityType<DreadLichSkullEntity>> DREAD_LICH_SKULL = build("dread_lich_skull", DreadLichSkullEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<DreadKnightEntity>> DREAD_KNIGHT = build("dread_knight", DreadKnightEntity::new, MobCategory.MONSTER, false, 0.6F, 1.8F);
    public static final DeferredHolder<EntityType<?>, EntityType<DreadHorseEntity>> DREAD_HORSE = build("dread_horse", DreadHorseEntity::new, MobCategory.MONSTER, false, 1.3964844F, 1.6F);
    public static final DeferredHolder<EntityType<?>, EntityType<HydraEntity>> HYDRA = build("hydra", HydraEntity::new, MobCategory.CREATURE, false, 2.8F, 1.39F);
    public static final DeferredHolder<EntityType<?>, EntityType<HydraBreathEntity>> HYDRA_BREATH = build("hydra_breath", HydraBreathEntity::new, MobCategory.MISC, false, 0.9F, 0.9F);
    public static final DeferredHolder<EntityType<?>, EntityType<HydraArrowEntity>> HYDRA_ARROW = build("hydra_arrow", HydraArrowEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<GhostEntity>> GHOST = build("ghost", GhostEntity::new, MobCategory.MONSTER, true, 0.8F, 1.9F);
    public static final DeferredHolder<EntityType<?>, EntityType<GhostSwordEntity>> GHOST_SWORD = build("ghost_sword", GhostSwordEntity::new, MobCategory.MISC, false, 0.5F, 0.5F);

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> build(String entityName, EntityType.EntityFactory<T> constructor, MobCategory category, boolean fireImmune, float sizeX, float sizeY) {
        EntityType.Builder<T> builder = EntityType.Builder.of(constructor, category).sized(sizeX, sizeY);
        if (fireImmune) builder.fireImmune();
        return register(entityName, () -> builder.build(entityName));
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> build(String entityName, EntityType.EntityFactory<T> constructor, MobCategory category, boolean fireImmune, float sizeX, float sizeY, int trackingRange) {
        EntityType.Builder<T> builder = EntityType.Builder.of(constructor, category).sized(sizeX, sizeY).clientTrackingRange(trackingRange);
        if (fireImmune) builder.fireImmune();
        return register(entityName, () -> builder.build(entityName));
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String entityName, Supplier<EntityType<T>> builder) {
        return REGISTRY.register(entityName, builder);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(DRAGON_EGG.get(), DragonEggEntity.bakeAttributes().build());
        event.put(DRAGON_SKULL.get(), DragonSkullEntity.bakeAttributes().build());
        event.put(FIRE_DRAGON.get(), FireDragonEntity.bakeAttributes().build());
        event.put(ICE_DRAGON.get(), IceDragonEntity.bakeAttributes().build());
        event.put(LIGHTNING_DRAGON.get(), LightningDragonEntity.bakeAttributes().build());
        event.put(HIPPOGRYPH.get(), HippogryphEntity.bakeAttributes().build());
        event.put(GORGON.get(), GorgonEntity.bakeAttributes().build());
        event.put(STONE_STATUE.get(), StoneStatueEntity.bakeAttributes().build());
        event.put(PIXIE.get(), PixieEntity.bakeAttributes().build());
        event.put(CYCLOPS.get(), CyclopsEntity.bakeAttributes().build());
        event.put(SIREN.get(), SirenEntity.bakeAttributes().build());
        event.put(HIPPOCAMPUS.get(), HippocampusEntity.bakeAttributes().build());
        event.put(DEATH_WORM.get(), DeathWormEntity.bakeAttributes().build());
        event.put(COCKATRICE.get(), CockatriceEntity.bakeAttributes().build());
        event.put(STYMPHALIAN_BIRD.get(), StymphalianBirdEntity.bakeAttributes().build());
        event.put(TROLL.get(), TrollEntity.bakeAttributes().build());
        event.put(AMPHITHERE.get(), AmphithereEntity.bakeAttributes().build());
        event.put(SEA_SERPENT.get(), SeaSerpentEntity.bakeAttributes().build());
        event.put(MOB_SKULL.get(), MobSkullEntity.bakeAttributes().build());
        event.put(DREAD_THRALL.get(), DreadThrallEntity.bakeAttributes().build());
        event.put(DREAD_LICH.get(), DreadLichEntity.bakeAttributes().build());
        event.put(DREAD_BEAST.get(), DreadBeastEntity.bakeAttributes().build());
        event.put(DREAD_HORSE.get(), DreadHorseEntity.bakeAttributes().build());
        event.put(DREAD_GHOUL.get(), DreadGhoulEntity.bakeAttributes().build());
        event.put(DREAD_KNIGHT.get(), DreadKnightEntity.bakeAttributes().build());
        event.put(DREAD_SCUTTLER.get(), DreadScuttlerEntity.bakeAttributes().build());
        event.put(HYDRA.get(), HydraEntity.bakeAttributes().build());
        event.put(GHOST.get(), GhostEntity.bakeAttributes().build());
    }

    //Attribute Fix
    @SubscribeEvent
    public static void modifyDefaultAttributes(EntityAttributeModificationEvent event) {
        event.add(FIRE_DRAGON.get(), IafAttributes.DRAGON_FORGE_SPEED, 0.025);
        event.add(ICE_DRAGON.get(), IafAttributes.DRAGON_FORGE_SPEED, 0.025);
        event.add(LIGHTNING_DRAGON.get(), IafAttributes.DRAGON_FORGE_SPEED, 0.025);
    }

    @SubscribeEvent
    public static void registerPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(HIPPOGRYPH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HippogryphEntity::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(TROLL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TrollEntity::canTrollSpawnOn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(DREAD_LICH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DreadLichEntity::canLichSpawnOn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(COCKATRICE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CockatriceEntity::canCockatriceSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(AMPHITHERE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, AmphithereEntity::canAmphithereSpawnOn, RegisterSpawnPlacementsEvent.Operation.OR);
        //Patch in biome jsons
        event.register(DREAD_KNIGHT.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, (a, b, c, d, e) -> true, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(DREAD_BEAST.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, (a, b, c, d, e) -> true, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(DREAD_THRALL.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, (a, b, c, d, e) -> true, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(DREAD_GHOUL.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, (a, b, c, d, e) -> true, RegisterSpawnPlacementsEvent.Operation.OR);
    }
}
