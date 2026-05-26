package com.iafenvoy.iceandfire.event.handler;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.component.ChainData;
import com.iafenvoy.iceandfire.entity.*;
import com.iafenvoy.iceandfire.entity.ai.EntitySheepAIFollowCyclopsGoal;
import com.iafenvoy.iceandfire.entity.ai.VillagerAIFearUntamedGoal;
import com.iafenvoy.iceandfire.entity.util.IAnimalFear;
import com.iafenvoy.iceandfire.entity.util.IVillagerFear;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.item.ChainItem;
import com.iafenvoy.iceandfire.item.DragonHornItem;
import com.iafenvoy.iceandfire.item.armor.DragonScaleArmorItem;
import com.iafenvoy.iceandfire.item.armor.DragonSteelArmorItem;
import com.iafenvoy.iceandfire.item.armor.TrollArmorItem;
import com.iafenvoy.iceandfire.item.component.StoneStatusComponent;
import com.iafenvoy.iceandfire.network.payload.PlayerHitMultipartC2SPayload;
import com.iafenvoy.iceandfire.registry.*;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import com.iafenvoy.uranus.object.RegistryHelper;
import com.iafenvoy.uranus.util.RandomHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@EventBusSubscriber
public final class ServerEvents {
    public static final UUID ALEX_UUID = UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c");
    public static final String BOLT_DONT_DESTROY_LOOT = "iceandfire.bolt_skip_loot";
    private static final Predicate<LivingEntity> VILLAGER_FEAR = entity -> entity instanceof IVillagerFear fear && fear.shouldFear();

    private static void signalChickenAlarm(LivingEntity chicken, LivingEntity attacker) {
        final float d0 = IafCommonConfig.INSTANCE.cockatrice.chickenSearchLength.getValue();
        final List<CockatriceEntity> list = chicken.level().getEntitiesOfClass(CockatriceEntity.class, (new AABB(chicken.getX(), chicken.getY(), chicken.getZ(), chicken.getX() + 1.0D, chicken.getY() + 1.0D, chicken.getZ() + 1.0D)).inflate(d0, 10.0D, d0));
        if (list.isEmpty()) return;

        for (final CockatriceEntity cockatrice : list) {
            if (!(attacker instanceof CockatriceEntity)) {
                if (!DragonUtils.hasSameOwner(cockatrice, attacker)) {
                    if (attacker instanceof Player player) {
                        if (!player.isCreative() && !cockatrice.isOwnedBy(player))
                            cockatrice.setTarget(player);
                    } else cockatrice.setTarget(attacker);
                }
            }
        }
    }

    private static void signalAmphithereAlarm(LivingEntity villager, LivingEntity attacker) {
        final float d0 = IafCommonConfig.INSTANCE.amphithere.villagerSearchLength.getValue().floatValue();
        final List<AmphithereEntity> list = villager.level().getEntitiesOfClass(AmphithereEntity.class, (new AABB(villager.getX() - 1.0D, villager.getY() - 1.0D, villager.getZ() - 1.0D, villager.getX() + 1.0D, villager.getY() + 1.0D, villager.getZ() + 1.0D)).inflate(d0, d0, d0));
        if (list.isEmpty()) return;

        for (final Entity entity : list) {
            if (entity instanceof AmphithereEntity amphithere && !(attacker instanceof AmphithereEntity)) {
                if (!DragonUtils.hasSameOwner(amphithere, attacker)) {
                    if (attacker instanceof Player player) {
                        if (!player.isCreative() && !amphithere.isOwnedBy(player))
                            amphithere.setTarget(player);
                    } else amphithere.setTarget(attacker);
                }
            }
        }
    }

    public static boolean isRidingOrBeingRiddenBy(final Entity first, final Entity entityIn) {
        if (first == null || entityIn == null) return false;
        for (final Entity entity : first.getPassengers())
            if (entity.equals(entityIn) || isRidingOrBeingRiddenBy(entity, entityIn))
                return true;
        return false;
    }

    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        float amount = event.getNewDamage();
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            float multi = 1;
            if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof TrollArmorItem)
                multi -= 0.1f;
            if (entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof TrollArmorItem)
                multi -= 0.3f;
            if (entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof TrollArmorItem)
                multi -= 0.2f;
            if (entity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof TrollArmorItem)
                multi -= 0.1f;
            amount *= multi;
        }
        if (source.is(IafDamageTypes.DRAGON_FIRE_TYPE) || source.is(IafDamageTypes.DRAGON_ICE_TYPE) || source.is(IafDamageTypes.DRAGON_LIGHTNING_TYPE)) {
            float multi = 1;
            if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof DragonScaleArmorItem ||
                    entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof DragonSteelArmorItem)
                multi -= 0.1f;
            if (entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof DragonScaleArmorItem ||
                    entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof DragonSteelArmorItem)
                multi -= 0.3f;
            if (entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof DragonScaleArmorItem ||
                    entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof DragonSteelArmorItem)
                multi -= 0.2f;
            if (entity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof DragonScaleArmorItem ||
                    entity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof DragonSteelArmorItem)
                multi -= 0.1f;
            amount *= multi;
        }
        event.setNewDamage(amount);
    }

    @SubscribeEvent
    public static void onLivingSetTarget(PlayerEvent.StartTracking event) {
        Player player = event.getEntity();
        if (event.getTarget() instanceof LivingEntity target) {
            if (target.getType().is(IafEntityTags.CHICKENS)) signalChickenAlarm(target, player);
            else if (DragonUtils.isVillager(target)) signalAmphithereAlarm(target, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        Level world = event.getEntity().level();
        Entity entity = event.getTarget();
        if (entity.getType().is(IafEntityTags.SHEEP)) {
            float dist = IafCommonConfig.INSTANCE.cyclops.sheepSearchLength.getValue();
            final List<Entity> list = entity.level().getEntities(entity, entity.getBoundingBox().inflate(dist, dist, dist));
            if (!list.isEmpty())
                for (final Entity e : list)
                    if (e instanceof CyclopsEntity cyclops)
                        if (!cyclops.isBlinded() && !player.isCreative())
                            cyclops.setTarget(player);
        }
        if (entity instanceof StoneStatueEntity statue) {
            statue.setHealth(statue.getMaxHealth());
            ItemStack stack = player.getMainHandItem();
            entity.playSound(SoundEvents.STONE_BREAK, 2, 0.5F + (float) (RandomHelper.nextDouble(-1, 1) * 0.2 + 0.5));

            if (stack.is(ItemTags.PICKAXES)) {
                statue.setCrackAmount(statue.getCrackAmount() + 1);

                if (statue.getCrackAmount() > 9) {
                    CompoundTag writtenTag = new CompoundTag();
                    entity.saveWithoutId(writtenTag);
                    entity.playSound(SoundEvents.STONE_BREAK, 2F, (float) (RandomHelper.nextDouble(-1, 1) * 0.2 + 0.5));
                    entity.remove(Entity.RemovalReason.KILLED);

                    if (EnchantmentHelper.getItemEnchantmentLevel(RegistryHelper.getEnchantment(world.registryAccess(), Enchantments.SILK_TOUCH), stack) > 0) {
                        ItemStack statuette = new ItemStack(IafItems.STONE_STATUE.get());
                        statuette.set(IafDataComponents.STONE_STATUS.get(), new StoneStatusComponent(statue.getTrappedEntityTypeString().equalsIgnoreCase("minecraft:player"), statue.getTrappedEntityTypeString(), writtenTag));
                        if (!statue.level().isClientSide)
                            statue.spawnAtLocation(statuette, 1);
                    } else if (!statue.level().isClientSide)
                        statue.spawnAtLocation(Blocks.COBBLESTONE, 2 + player.getRandom().nextInt(4));

                    statue.remove(Entity.RemovalReason.KILLED);
                }
            }
            event.setCanceled(true);
            return;
        }
        if (entity instanceof MultipartPartEntity mutlipartPart) {
            Entity parent = mutlipartPart.getParent();
            try {
                //If the attacked entity is the parent itself parent will be null and also doesn't have to be attacked
                if (parent != null)
                    player.attack(parent);
            } catch (Exception e) {
                IceAndFire.LOGGER.warn("Exception thrown while interacting with entity.", e);
            }
            int extraData = 0;
            if (mutlipartPart instanceof HydraHeadEntity hydraHead && parent instanceof HydraEntity hydra) {
                extraData = hydraHead.headIndex;
                hydra.triggerHeadFlags(extraData);
            }
            if (mutlipartPart.level().isClientSide && parent != null)
                PacketDistributor.sendToServer(new PlayerHitMultipartC2SPayload(parent.getId(), extraData));
        }
        if (entity instanceof LivingEntity livingEntity) {
            if (entity.getType().is(IafEntityTags.CHICKENS)) signalChickenAlarm(livingEntity, player);
            else if (DragonUtils.isVillager(entity)) signalAmphithereAlarm(livingEntity, player);
        }
    }

    @SubscribeEvent
    public static void onEntityDie(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        ChainData chainData = ChainData.get(entity);
        if (!chainData.getChainedTo().isEmpty()) {
            ItemEntity entityitem = new ItemEntity(entity.level(),
                    entity.getX(),
                    entity.getY() + 1,
                    entity.getZ(),
                    new ItemStack(IafItems.CHAIN.get(), chainData.getChainedTo().size()));
            entityitem.setDefaultPickUpDelay();
            entity.level().addFreshEntity(entityitem);

            chainData.clearChains();
        }

        if (entity.getUUID().equals(ServerEvents.ALEX_UUID))
            entity.spawnAtLocation(new ItemStack(IafItems.WEEZER_BLUE_ALBUM.get()), 1);

        if (entity instanceof Player) {
            if (IafCommonConfig.INSTANCE.ghost.fromPlayerDeaths.getValue()) {
                Entity attacker = entity.getLastHurtByMob();
                if (attacker instanceof Player && entity.getRandom().nextInt(3) == 0) {
                    CombatTracker combat = entity.getCombatTracker();
                    CombatEntry entry = combat.getMostSignificantFall();
                    boolean flag = entry != null && (entry.source().is(DamageTypes.FALL) || entry.source().is(DamageTypes.DROWN) || entry.source().is(DamageTypes.LAVA));
                    if (entity.hasEffect(MobEffects.POISON))
                        flag = true;
                    if (flag) {
                        Level world = entity.level();
                        GhostEntity ghost = IafEntities.GHOST.get().create(world);
                        assert ghost != null;
                        ghost.copyPosition(entity);
                        if (world instanceof ServerLevelAccessor serverWorldAccess) {
                            ghost.finalizeSpawn(serverWorldAccess, world.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, null);
                            world.addFreshEntity(ghost);
                        }
                        ghost.setDaytimeMode(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        Entity entity = event.getTarget();
        InteractionHand hand = event.getHand();
        // Handle chain removal
        if (entity instanceof LivingEntity target && !player.isSpectator()) {
            ChainData chainData = ChainData.get(target);
            if (chainData.isChainedTo(entity.getUUID())) {
                chainData.removeChain(entity.getUUID());
                if (!player.level().isClientSide)
                    entity.spawnAtLocation(IafItems.CHAIN.get(), 1);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }
        }
        // Handle multipart
        if (entity instanceof MultipartPartEntity multipart) {
            //FIXME::Don't run in item class?
            // Handle some dragon items
            if (player.getItemInHand(hand).getItem() instanceof DragonHornItem horn && multipart.getParent() instanceof LivingEntity living)
                horn.interactLivingEntity(player.getItemInHand(hand), player, living, hand);
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        Level world = player.level();
        if (world.getBlockState(pos).getBlock() instanceof AbstractChestBlock && !player.isCreative()) {
            float dist = IafCommonConfig.INSTANCE.dragon.goldSearchLength.getValue();
            final List<Entity> list = world.getEntities(player, player.getBoundingBox().inflate(dist, dist, dist));
            if (!list.isEmpty())
                for (final Entity entity : list)
                    if (entity instanceof DragonBaseEntity dragon)
                        if (!dragon.isTame() && !dragon.isModelDead() && !dragon.isOwnedBy(player)) {
                            dragon.setInSittingPose(false);
                            dragon.setOrderedToSit(false);
                            dragon.setTarget(player);
                        }
        }
        if (world.getBlockState(pos).getBlock() instanceof WallBlock)
            ChainItem.attachToFence(player, world, pos);
    }

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        LevelAccessor world = event.getLevel();
        BlockState state = event.getState();
        Player player = event.getPlayer();
        if (state.getBlock() instanceof AbstractChestBlock || state.is(IafBlocks.GOLD_PILE.get()) || state.is(IafBlocks.SILVER_PILE.get()) || state.is(IafBlocks.COPPER_PILE.get())) {
            final float dist = IafCommonConfig.INSTANCE.dragon.goldSearchLength.getValue();
            List<Entity> list = world.getEntities(player, player.getBoundingBox().inflate(dist, dist, dist));
            if (list.isEmpty()) return;

            for (Entity entity : list)
                if (entity instanceof DragonBaseEntity dragon)
                    if (!dragon.isTame() && !dragon.isModelDead() && !dragon.isOwnedBy(player) && !player.isCreative()) {
                        dragon.setInSittingPose(false);
                        dragon.setOrderedToSit(false);
                        dragon.setTarget(player);
                    }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeaveEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (!player.getPassengers().isEmpty())
            for (Entity entity : player.getPassengers())
                entity.stopRiding();
    }

    @SubscribeEvent
    public static boolean onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Mob mob)
            try {
                if (mob.getType().is(IafEntityTags.SHEEP) && mob instanceof Animal animal)
                    animal.goalSelector.addGoal(8, new EntitySheepAIFollowCyclopsGoal(animal, 1.2D));
                if (mob.getType().is(IafEntityTags.VILLAGERS))
                    if (IafCommonConfig.INSTANCE.dragon.villagersFear.getValue())
                        mob.goalSelector.addGoal(1, new VillagerAIFearUntamedGoal((PathfinderMob) mob, LivingEntity.class, 8.0F, 0.8D, 0.8D, VILLAGER_FEAR));
                if (mob.getType().is(IafEntityTags.FEAR_DRAGONS))
                    if (IafCommonConfig.INSTANCE.dragon.animalsFear.getValue())
                        mob.goalSelector.addGoal(1, new VillagerAIFearUntamedGoal((PathfinderMob) mob, LivingEntity.class, 30, 1.0D, 0.5D, e -> e instanceof IAnimalFear fear && fear.shouldAnimalsFear(mob)));
            } catch (Exception e) {
                IceAndFire.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
            }
        return true;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (source.is(DamageTypes.LIGHTNING_BOLT) &&
                entity.getItemBySlot(EquipmentSlot.HEAD).is(IafItems.DRAGONSTEEL_LIGHTNING_HELMET.get()) &&
                entity.getItemBySlot(EquipmentSlot.CHEST).is(IafItems.DRAGONSTEEL_LIGHTNING_CHESTPLATE.get()) &&
                entity.getItemBySlot(EquipmentSlot.LEGS).is(IafItems.DRAGONSTEEL_LIGHTNING_LEGGINGS.get()) &&
                entity.getItemBySlot(EquipmentSlot.FEET).is(IafItems.DRAGONSTEEL_LIGHTNING_BOOTS.get()))
            event.setNewDamage(0);
    }
}
