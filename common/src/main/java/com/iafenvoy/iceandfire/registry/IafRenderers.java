package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.data.SeaSerpentType;
import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.impl.ParticleProviderHolder;
import com.iafenvoy.iceandfire.item.DragonHornItem;
import com.iafenvoy.iceandfire.item.SummoningCrystalItem;
import com.iafenvoy.iceandfire.particle.*;
import com.iafenvoy.iceandfire.render.block.*;
import com.iafenvoy.iceandfire.render.entity.*;
import com.iafenvoy.iceandfire.render.item.*;
import com.iafenvoy.iceandfire.render.item.armor.*;
import com.iafenvoy.iceandfire.render.model.animator.FireDragonTabulaModelAnimator;
import com.iafenvoy.iceandfire.render.model.animator.IceDragonTabulaModelAnimator;
import com.iafenvoy.iceandfire.render.model.animator.NetherDragonTabulaModelAnimator;
import com.iafenvoy.iceandfire.render.model.animator.LightningTabulaDragonAnimator;
import com.iafenvoy.uranus.client.model.util.TabulaModelHandlerHelper;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import com.iafenvoy.uranus.util.function.MemorizeSupplier;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public final class IafRenderers {
    public static final Identifier FIRE_DRAGON = Identifier.of(IceAndFire.MOD_ID, "firedragon/firedragon_ground");
    public static final Identifier ICE_DRAGON = Identifier.of(IceAndFire.MOD_ID, "icedragon/icedragon_ground");
    public static final Identifier LIGHTNING_DRAGON = Identifier.of(IceAndFire.MOD_ID, "lightningdragon/lightningdragon_ground");
    public static final Identifier NETHER_DRAGON = Identifier.of(IceAndFire.MOD_ID, "firedragon/firedragon_ground"); // Using fire dragon model for now
    public static final Identifier SEA_SERPENT = Identifier.of(IceAndFire.MOD_ID, "seaserpent/seaserpent_base");

    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(IafEntities.FIRE_DRAGON, x -> new DragonBaseEntityRenderer<>(x, TabulaModelHandlerHelper.getModel(FIRE_DRAGON, new MemorizeSupplier<>(FireDragonTabulaModelAnimator::new))));
        EntityRendererRegistry.register(IafEntities.ICE_DRAGON, manager -> new DragonBaseEntityRenderer<>(manager, TabulaModelHandlerHelper.getModel(ICE_DRAGON, new MemorizeSupplier<>(IceDragonTabulaModelAnimator::new))));
        EntityRendererRegistry.register(IafEntities.LIGHTNING_DRAGON, manager -> new LightningDragonEntityRenderer(manager, TabulaModelHandlerHelper.getModel(LIGHTNING_DRAGON, new MemorizeSupplier<>(LightningTabulaDragonAnimator::new))));
        EntityRendererRegistry.register(IafEntities.NETHER_DRAGON, x -> new DragonBaseEntityRenderer<>(x, TabulaModelHandlerHelper.getModel(NETHER_DRAGON, new MemorizeSupplier<>(NetherDragonTabulaModelAnimator::new))));
        EntityRendererRegistry.register(IafEntities.DRAGON_EGG, DragonEggEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DRAGON_ARROW, DragonArrowEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DRAGON_SKULL, DragonSkullEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.FIRE_DRAGON_CHARGE, manager -> new DragonChargeEntityRenderer(manager, true));
        EntityRendererRegistry.register(IafEntities.ICE_DRAGON_CHARGE, manager -> new DragonChargeEntityRenderer(manager, false));
        EntityRendererRegistry.register(IafEntities.LIGHTNING_DRAGON_CHARGE, LightningDragonChargeEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.NETHER_DRAGON_CHARGE, manager -> new DragonChargeEntityRenderer(manager, true));
        EntityRendererRegistry.register(IafEntities.HIPPOGRYPH_EGG, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.HIPPOGRYPH, HippogryphEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.STONE_STATUE, StoneStatueEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.GORGON, GorgonEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.PIXIE, PixieEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.CYCLOPS, CyclopsEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.SIREN, SirenEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.HIPPOCAMPUS, HippocampusEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DEATH_WORM, DeathWormEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DEATH_WORM_EGG, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.COCKATRICE, CockatriceEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.COCKATRICE_EGG, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.STYMPHALIAN_BIRD, StymphalianBirdEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.STYMPHALIAN_FEATHER, StymphalianFeatherEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.STYMPHALIAN_ARROW, StymphalianArrowEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.TROLL, TrollEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.AMPHITHERE, AmphithereEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.AMPHITHERE_ARROW, AmphithereArrowEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.SEA_SERPENT, SeaSerpentEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.SEA_SERPENT_BUBBLES, NothingEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.SEA_SERPENT_ARROW, SeaSerpentArrowEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.CHAIN_TIE, ChainTieEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.PIXIE_CHARGE, NothingEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.TIDE_TRIDENT, TideTridentEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.MOB_SKULL, MobSkullEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DREAD_SCUTTLER, DreadScuttlerEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DREAD_GHOUL, DreadGhoulEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DREAD_BEAST, DreadBeastEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DREAD_SCUTTLER, DreadScuttlerEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DREAD_THRALL, DreadThrallEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DREAD_LICH, DreadLichEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DREAD_LICH_SKULL, DreadLichSkullEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DREAD_KNIGHT, DreadKnightEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DREAD_HORSE, DreadHorseEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.HYDRA, HydraEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.HYDRA_BREATH, NothingEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.HYDRA_ARROW, HydraArrowEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.SLOW_MULTIPART, NothingEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.DRAGON_MULTIPART, NothingEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.CYCLOPS_MULTIPART, NothingEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.HYDRA_MULTIPART, NothingEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.GHOST, GhostEntityRenderer::new);
        EntityRendererRegistry.register(IafEntities.GHOST_SWORD, GhostSwordEntityRenderer::new);
    }

    public static void registerParticleRenderers(Consumer<ParticleProviderHolder<?>> consumer) {
        consumer.accept(new ParticleProviderHolder<>(IafParticles.BLOOD.get(), BloodParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.DRAGON_FLAME.get(), DragonFlameParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.DRAGON_FROST.get(), DragonFrostParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.DRAGON_SOUL_FLAME.get(), DragonSoulFlameParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.DREAD_PORTAL.get(), DreadPortalParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.DREAD_TORCH.get(), DreadTorchParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.GHOST_APPEARANCE.get(), GhostAppearanceParticle.factory()));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.HYDRA_BREATH.get(), HydraBreathParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.PIXIE_DUST.get(), PixieDustParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.SERPENT_BUBBLE.get(), SerpentBubbleParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.SIREN_APPEARANCE.get(), SirenAppearanceParticle.factory()));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.SIREN_MUSIC.get(), SirenMusicParticle::factory));
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(IafBlockEntities.PODIUM.get(), PodiumBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(IafBlockEntities.IAF_LECTERN.get(), LecternBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(IafBlockEntities.EGG_IN_ICE.get(), EggInIceBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(IafBlockEntities.PIXIE_HOUSE.get(), PixieHouseBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(IafBlockEntities.PIXIE_JAR.get(), JarBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(IafBlockEntities.DREAD_PORTAL.get(), DreadPortalBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(IafBlockEntities.DREAD_SPAWNER.get(), DreadSpawnerBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(IafBlockEntities.GHOST_CHEST.get(), GhostChestBlockEntityRenderer::new);
    }

    public static void registerArmorRenderers() {
        IArmorRendererBase.register(new CopperArmorRenderer(), IafItems.COPPER_HELMET.get(), IafItems.COPPER_CHESTPLATE.get(), IafItems.COPPER_LEGGINGS.get(), IafItems.COPPER_BOOTS.get());
        IArmorRendererBase.register(new DeathWormArmorRenderer(), IafItems.DEATHWORM_WHITE_HELMET.get(), IafItems.DEATHWORM_WHITE_CHESTPLATE.get(), IafItems.DEATHWORM_WHITE_LEGGINGS.get(), IafItems.DEATHWORM_WHITE_BOOTS.get());
        IArmorRendererBase.register(new DeathWormArmorRenderer(), IafItems.DEATHWORM_YELLOW_HELMET.get(), IafItems.DEATHWORM_YELLOW_CHESTPLATE.get(), IafItems.DEATHWORM_YELLOW_LEGGINGS.get(), IafItems.DEATHWORM_YELLOW_BOOTS.get());
        IArmorRendererBase.register(new DeathWormArmorRenderer(), IafItems.DEATHWORM_RED_HELMET.get(), IafItems.DEATHWORM_RED_CHESTPLATE.get(), IafItems.DEATHWORM_RED_LEGGINGS.get(), IafItems.DEATHWORM_RED_BOOTS.get());
        IArmorRendererBase.register(new DragonSteelArmorRenderer(), IafItems.DRAGONSTEEL_FIRE_HELMET.get(), IafItems.DRAGONSTEEL_FIRE_CHESTPLATE.get(), IafItems.DRAGONSTEEL_FIRE_LEGGINGS.get(), IafItems.DRAGONSTEEL_FIRE_BOOTS.get());
        IArmorRendererBase.register(new DragonSteelArmorRenderer(), IafItems.DRAGONSTEEL_ICE_HELMET.get(), IafItems.DRAGONSTEEL_ICE_CHESTPLATE.get(), IafItems.DRAGONSTEEL_ICE_LEGGINGS.get(), IafItems.DRAGONSTEEL_ICE_BOOTS.get());
        IArmorRendererBase.register(new DragonSteelArmorRenderer(), IafItems.DRAGONSTEEL_LIGHTNING_HELMET.get(), IafItems.DRAGONSTEEL_LIGHTNING_CHESTPLATE.get(), IafItems.DRAGONSTEEL_LIGHTNING_LEGGINGS.get(), IafItems.DRAGONSTEEL_LIGHTNING_BOOTS.get());
        IArmorRendererBase.register(new SilverArmorRenderer(), IafItems.SILVER_HELMET.get(), IafItems.SILVER_CHESTPLATE.get(), IafItems.SILVER_LEGGINGS.get(), IafItems.SILVER_BOOTS.get());
        for (DragonColor armor : IafRegistries.DRAGON_COLOR)
            IArmorRendererBase.register(new ScaleArmorRenderer(), armor.helmet.get(), armor.chestplate.get(), armor.leggings.get(), armor.boots.get());
        for (SeaSerpentType seaSerpent : IafRegistries.SEA_SERPENT_TYPE)
            IArmorRendererBase.register(new SeaSerpentArmorRenderer(), seaSerpent.helmet.get(), seaSerpent.chestplate.get(), seaSerpent.leggings.get(), seaSerpent.boots.get());
        for (TrollType troll : IafRegistries.TROLL_TYPE)
            IArmorRendererBase.register(new TrollArmorRenderer(), troll.helmet.get(), troll.chestplate.get(), troll.leggings.get(), troll.boots.get());
    }

    public static void registerItemRenderers() {
        DynamicItemRenderer.RENDERERS.put(IafItems.DEATHWORM_GAUNTLET_RED.get(), new DeathwormGauntletRenderer());
        DynamicItemRenderer.RENDERERS.put(IafItems.DEATHWORM_GAUNTLET_YELLOW.get(), new DeathwormGauntletRenderer());
        DynamicItemRenderer.RENDERERS.put(IafItems.DEATHWORM_GAUNTLET_WHITE.get(), new DeathwormGauntletRenderer());
        DynamicItemRenderer.RENDERERS.put(IafItems.GORGON_HEAD.get(), new GorgonHeadRenderer());
        DynamicItemRenderer.RENDERERS.put(IafItems.TIDE_TRIDENT.get(), new TideTridentItemRenderer());
        DynamicItemRenderer.RENDERERS.put(IafBlocks.PIXIE_HOUSE_BIRCH.get().asItem(), new MiscItemRenderer());
        DynamicItemRenderer.RENDERERS.put(IafBlocks.PIXIE_HOUSE_OAK.get().asItem(), new MiscItemRenderer());
        DynamicItemRenderer.RENDERERS.put(IafBlocks.PIXIE_HOUSE_DARK_OAK.get().asItem(), new MiscItemRenderer());
        DynamicItemRenderer.RENDERERS.put(IafBlocks.PIXIE_HOUSE_SPRUCE.get().asItem(), new MiscItemRenderer());
        DynamicItemRenderer.RENDERERS.put(IafBlocks.PIXIE_HOUSE_MUSHROOM_RED.get().asItem(), new MiscItemRenderer());
        DynamicItemRenderer.RENDERERS.put(IafBlocks.PIXIE_HOUSE_MUSHROOM_BROWN.get().asItem(), new MiscItemRenderer());
        DynamicItemRenderer.RENDERERS.put(IafBlocks.DREAD_PORTAL.get().asItem(), new MiscItemRenderer());
        DynamicItemRenderer.RENDERERS.put(IafBlocks.GHOST_CHEST.get().asItem(), new MiscItemRenderer());
        for (TrollType.BuiltinWeapon weapon : TrollType.BuiltinWeapon.values())
            DynamicItemRenderer.RENDERERS.put(weapon.getItem(), new TrollWeaponRenderer());
    }

    public static void registerRenderLayers() {
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.GOLD_PILE.get(), IafBlocks.SILVER_PILE.get());
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.LECTERN.get());
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.PODIUM_OAK.get(), IafBlocks.PODIUM_BIRCH.get(), IafBlocks.PODIUM_SPRUCE.get(), IafBlocks.PODIUM_JUNGLE.get(), IafBlocks.PODIUM_ACACIA.get(), IafBlocks.PODIUM_DARK_OAK.get());
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.FIRE_LILY.get(), IafBlocks.FROST_LILY.get(), IafBlocks.LIGHTNING_LILY.get());
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.DRAGON_ICE_SPIKES.get());
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.DREAD_STONE_FACE.get());
        RenderTypeRegistry.register(RenderLayer.getTranslucent(), IafBlocks.EGG_IN_ICE.get());
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.JAR_EMPTY.get(), IafBlocks.JAR_PIXIE_0.get(), IafBlocks.JAR_PIXIE_1.get(), IafBlocks.JAR_PIXIE_2.get(), IafBlocks.JAR_PIXIE_3.get(), IafBlocks.JAR_PIXIE_4.get());
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.PIXIE_HOUSE_MUSHROOM_BROWN.get(), IafBlocks.PIXIE_HOUSE_MUSHROOM_RED.get(), IafBlocks.PIXIE_HOUSE_OAK.get(), IafBlocks.PIXIE_HOUSE_BIRCH.get(), IafBlocks.PIXIE_HOUSE_SPRUCE.get(), IafBlocks.PIXIE_HOUSE_DARK_OAK.get());
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.DREAD_SPAWNER.get());
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.DREAD_TORCH.get(), IafBlocks.BURNT_TORCH.get());
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.DREAD_TORCH_WALL.get(), IafBlocks.BURNT_TORCH_WALL.get());
        RenderTypeRegistry.register(RenderLayer.getCutout(), IafBlocks.DREADWOOD_LEAVES.get(), IafBlocks.DREADWOOD_SAPLING.get());
    }

    public static void registerModelPredicates() {
        ItemPropertiesRegistry.register(IafItems.DRAGON_BOW.get(), Identifier.ofVanilla("pulling"), (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1 : 0);
        ItemPropertiesRegistry.register(IafItems.DRAGON_BOW.get(), Identifier.ofVanilla("pull"), (itemStack, clientWorld, livingEntity, seed) -> livingEntity == null ? 0 : livingEntity.getActiveItem() != itemStack ? 0 : (float) (itemStack.getMaxUseTime(livingEntity) - livingEntity.getItemUseTimeLeft()) / 20);

        ItemPropertiesRegistry.register(IafItems.DRAGON_HORN.get(), Identifier.ofVanilla("iceorfire"), (stack, level, entity, p) -> DragonHornItem.getDragonType(stack) * 0.25F);
        ItemPropertiesRegistry.register(IafItems.SUMMONING_CRYSTAL_FIRE.get(), Identifier.ofVanilla("has_dragon"), (stack, level, entity, p) -> SummoningCrystalItem.hasDragon(stack) ? 1.0F : 0.0F);
        ItemPropertiesRegistry.register(IafItems.SUMMONING_CRYSTAL_ICE.get(), Identifier.ofVanilla("has_dragon"), (stack, level, entity, p) -> SummoningCrystalItem.hasDragon(stack) ? 1.0F : 0.0F);
        ItemPropertiesRegistry.register(IafItems.SUMMONING_CRYSTAL_LIGHTNING.get(), Identifier.ofVanilla("has_dragon"), (stack, level, entity, p) -> SummoningCrystalItem.hasDragon(stack) ? 1.0F : 0.0F);
        ItemPropertiesRegistry.register(IafItems.TIDE_TRIDENT.get(), Identifier.ofVanilla("throwing"), (stack, level, entity, p) -> entity != null && entity.isUsingItem() && entity.getMainHandStack() == stack ? 1.0F : 0.0F);
    }
}
