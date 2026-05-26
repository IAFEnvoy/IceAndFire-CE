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
import com.iafenvoy.iceandfire.render.item.armor.BasicArmorRenderer;
import com.iafenvoy.iceandfire.render.item.armor.ScaleArmorRenderer;
import com.iafenvoy.iceandfire.render.model.animator.FireDragonTabulaModelAnimator;
import com.iafenvoy.iceandfire.render.model.animator.IceDragonTabulaModelAnimator;
import com.iafenvoy.iceandfire.render.model.animator.LightningTabulaDragonAnimator;
import com.iafenvoy.iceandfire.render.model.armor.*;
import com.iafenvoy.uranus.client.model.util.TabulaModelHandlerHelper;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import com.iafenvoy.uranus.util.function.MemorizeSupplier;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public final class IafRenderers {
    public static final ResourceLocation FIRE_DRAGON = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "firedragon/firedragon_ground");
    public static final ResourceLocation ICE_DRAGON = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "icedragon/icedragon_ground");
    public static final ResourceLocation LIGHTNING_DRAGON = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "lightningdragon/lightningdragon_ground");
    public static final ResourceLocation SEA_SERPENT = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "seaserpent/seaserpent_base");

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(IafEntities.FIRE_DRAGON.get(), x -> new DragonBaseEntityRenderer<>(x, TabulaModelHandlerHelper.getModel(FIRE_DRAGON, new MemorizeSupplier<>(FireDragonTabulaModelAnimator::new))));
        event.registerEntityRenderer(IafEntities.ICE_DRAGON.get(), manager -> new DragonBaseEntityRenderer<>(manager, TabulaModelHandlerHelper.getModel(ICE_DRAGON, new MemorizeSupplier<>(IceDragonTabulaModelAnimator::new))));
        event.registerEntityRenderer(IafEntities.LIGHTNING_DRAGON.get(), manager -> new LightningDragonEntityRenderer(manager, TabulaModelHandlerHelper.getModel(LIGHTNING_DRAGON, new MemorizeSupplier<>(LightningTabulaDragonAnimator::new))));
        event.registerEntityRenderer(IafEntities.DRAGON_EGG.get(), DragonEggEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DRAGON_ARROW.get(), DragonArrowEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DRAGON_SKULL.get(), DragonSkullEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.FIRE_DRAGON_CHARGE.get(), manager -> new DragonChargeEntityRenderer(manager, true));
        event.registerEntityRenderer(IafEntities.ICE_DRAGON_CHARGE.get(), manager -> new DragonChargeEntityRenderer(manager, false));
        event.registerEntityRenderer(IafEntities.LIGHTNING_DRAGON_CHARGE.get(), LightningDragonChargeEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.HIPPOGRYPH_EGG.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(IafEntities.HIPPOGRYPH.get(), HippogryphEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.STONE_STATUE.get(), StoneStatueEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.GORGON.get(), GorgonEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.PIXIE.get(), PixieEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.CYCLOPS.get(), CyclopsEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.SIREN.get(), SirenEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.HIPPOCAMPUS.get(), HippocampusEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DEATH_WORM.get(), DeathWormEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DEATH_WORM_EGG.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(IafEntities.COCKATRICE.get(), CockatriceEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.COCKATRICE_EGG.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(IafEntities.STYMPHALIAN_BIRD.get(), StymphalianBirdEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.STYMPHALIAN_FEATHER.get(), StymphalianFeatherEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.STYMPHALIAN_ARROW.get(), StymphalianArrowEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.TROLL.get(), TrollEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.AMPHITHERE.get(), AmphithereEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.AMPHITHERE_ARROW.get(), AmphithereArrowEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.SEA_SERPENT.get(), SeaSerpentEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.SEA_SERPENT_BUBBLES.get(), NothingEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.SEA_SERPENT_ARROW.get(), SeaSerpentArrowEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.CHAIN_TIE.get(), ChainTieEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.PIXIE_CHARGE.get(), NothingEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.TIDE_TRIDENT.get(), TideTridentEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.MOB_SKULL.get(), MobSkullEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DREAD_SCUTTLER.get(), DreadScuttlerEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DREAD_GHOUL.get(), DreadGhoulEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DREAD_BEAST.get(), DreadBeastEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DREAD_SCUTTLER.get(), DreadScuttlerEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DREAD_THRALL.get(), DreadThrallEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DREAD_LICH.get(), DreadLichEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DREAD_LICH_SKULL.get(), DreadLichSkullEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DREAD_KNIGHT.get(), DreadKnightEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DREAD_HORSE.get(), DreadHorseEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.HYDRA.get(), HydraEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.HYDRA_BREATH.get(), NothingEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.HYDRA_ARROW.get(), HydraArrowEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.SLOW_MULTIPART.get(), NothingEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.DRAGON_MULTIPART.get(), NothingEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.CYCLOPS_MULTIPART.get(), NothingEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.HYDRA_MULTIPART.get(), NothingEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.GHOST.get(), GhostEntityRenderer::new);
        event.registerEntityRenderer(IafEntities.GHOST_SWORD.get(), GhostSwordEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(IafBlockEntities.PODIUM.get(), PodiumBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(IafBlockEntities.IAF_LECTERN.get(), LecternBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(IafBlockEntities.EGG_IN_ICE.get(), EggInIceBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(IafBlockEntities.PIXIE_HOUSE.get(), PixieHouseBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(IafBlockEntities.PIXIE_JAR.get(), JarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(IafBlockEntities.DREAD_PORTAL.get(), DreadPortalBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(IafBlockEntities.DREAD_SPAWNER.get(), DreadSpawnerBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(IafBlockEntities.GHOST_CHEST.get(), ChestRenderer::new);
    }

    public static void registerParticleRenderers(Consumer<ParticleProviderHolder<?>> consumer) {
        consumer.accept(new ParticleProviderHolder<>(IafParticles.BLOOD.get(), BloodParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.DRAGON_FLAME.get(), DragonFlameParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.DRAGON_FROST.get(), DragonFrostParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.DREAD_PORTAL.get(), DreadPortalParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.DREAD_TORCH.get(), DreadTorchParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.GHOST_APPEARANCE.get(), GhostAppearanceParticle.factory()));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.HYDRA_BREATH.get(), HydraBreathParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.PIXIE_DUST.get(), PixieDustParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.SERPENT_BUBBLE.get(), SerpentBubbleParticle::factory));
        consumer.accept(new ParticleProviderHolder<>(IafParticles.SIREN_MUSIC.get(), SirenMusicParticle::factory));
    }

    public static void registerArmorRenderers() {
        IArmorRendererBase.register(new BasicArmorRenderer(CopperArmorModel::new), IafItems.COPPER_HELMET.get(), IafItems.COPPER_CHESTPLATE.get(), IafItems.COPPER_LEGGINGS.get(), IafItems.COPPER_BOOTS.get());
        IArmorRendererBase.register(new BasicArmorRenderer(DeathWormArmorModel::new), IafItems.DEATHWORM_WHITE_HELMET.get(), IafItems.DEATHWORM_WHITE_CHESTPLATE.get(), IafItems.DEATHWORM_WHITE_LEGGINGS.get(), IafItems.DEATHWORM_WHITE_BOOTS.get());
        IArmorRendererBase.register(new BasicArmorRenderer(DeathWormArmorModel::new), IafItems.DEATHWORM_YELLOW_HELMET.get(), IafItems.DEATHWORM_YELLOW_CHESTPLATE.get(), IafItems.DEATHWORM_YELLOW_LEGGINGS.get(), IafItems.DEATHWORM_YELLOW_BOOTS.get());
        IArmorRendererBase.register(new BasicArmorRenderer(DeathWormArmorModel::new), IafItems.DEATHWORM_RED_HELMET.get(), IafItems.DEATHWORM_RED_CHESTPLATE.get(), IafItems.DEATHWORM_RED_LEGGINGS.get(), IafItems.DEATHWORM_RED_BOOTS.get());
        IArmorRendererBase.register(new BasicArmorRenderer(DragonSteelFireArmorModel::new), IafItems.DRAGONSTEEL_FIRE_HELMET.get(), IafItems.DRAGONSTEEL_FIRE_CHESTPLATE.get(), IafItems.DRAGONSTEEL_FIRE_LEGGINGS.get(), IafItems.DRAGONSTEEL_FIRE_BOOTS.get());
        IArmorRendererBase.register(new BasicArmorRenderer(DragonSteelIceArmorModel::new), IafItems.DRAGONSTEEL_ICE_HELMET.get(), IafItems.DRAGONSTEEL_ICE_CHESTPLATE.get(), IafItems.DRAGONSTEEL_ICE_LEGGINGS.get(), IafItems.DRAGONSTEEL_ICE_BOOTS.get());
        IArmorRendererBase.register(new BasicArmorRenderer(DragonSteelLightningArmorModel::new), IafItems.DRAGONSTEEL_LIGHTNING_HELMET.get(), IafItems.DRAGONSTEEL_LIGHTNING_CHESTPLATE.get(), IafItems.DRAGONSTEEL_LIGHTNING_LEGGINGS.get(), IafItems.DRAGONSTEEL_LIGHTNING_BOOTS.get());
        IArmorRendererBase.register(new BasicArmorRenderer(SilverArmorModel::new), IafItems.SILVER_HELMET.get(), IafItems.SILVER_CHESTPLATE.get(), IafItems.SILVER_LEGGINGS.get(), IafItems.SILVER_BOOTS.get());
        for (DragonColor armor : IafRegistries.DRAGON_COLOR)
            IArmorRendererBase.register(new ScaleArmorRenderer(), armor.helmet.get(), armor.chestplate.get(), armor.leggings.get(), armor.boots.get());
        for (SeaSerpentType seaSerpent : IafRegistries.SEA_SERPENT_TYPE)
            IArmorRendererBase.register(new BasicArmorRenderer(SeaSerpentArmorModel::new), seaSerpent.helmet.get(), seaSerpent.chestplate.get(), seaSerpent.leggings.get(), seaSerpent.boots.get());
        for (TrollType troll : IafRegistries.TROLL_TYPE)
            IArmorRendererBase.register(new BasicArmorRenderer(TrollArmorModel::new), troll.helmet.get(), troll.chestplate.get(), troll.leggings.get(), troll.boots.get());
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


    public static void registerModelPredicates() {
        ItemProperties.register(IafItems.DRAGON_BOW.get(), ResourceLocation.withDefaultNamespace("pulling"), (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1 : 0);
        ItemProperties.register(IafItems.DRAGON_BOW.get(), ResourceLocation.withDefaultNamespace("pull"), (itemStack, clientWorld, livingEntity, seed) -> livingEntity == null ? 0 : livingEntity.getUseItem() != itemStack ? 0 : (float) (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20);

        ItemProperties.register(IafItems.DRAGON_HORN.get(), ResourceLocation.withDefaultNamespace("iceorfire"), (stack, level, entity, p) -> DragonHornItem.getDragonType(stack) * 0.25F);
        ItemProperties.register(IafItems.SUMMONING_CRYSTAL_FIRE.get(), ResourceLocation.withDefaultNamespace("has_dragon"), (stack, level, entity, p) -> SummoningCrystalItem.hasDragon(stack) ? 1.0F : 0.0F);
        ItemProperties.register(IafItems.SUMMONING_CRYSTAL_ICE.get(), ResourceLocation.withDefaultNamespace("has_dragon"), (stack, level, entity, p) -> SummoningCrystalItem.hasDragon(stack) ? 1.0F : 0.0F);
        ItemProperties.register(IafItems.SUMMONING_CRYSTAL_LIGHTNING.get(), ResourceLocation.withDefaultNamespace("has_dragon"), (stack, level, entity, p) -> SummoningCrystalItem.hasDragon(stack) ? 1.0F : 0.0F);
        ItemProperties.register(IafItems.TIDE_TRIDENT.get(), ResourceLocation.withDefaultNamespace("throwing"), (stack, level, entity, p) -> entity != null && entity.isUsingItem() && entity.getMainHandItem() == stack ? 1.0F : 0.0F);
    }
}
