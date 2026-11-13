package com.iafenvoy.iceandfire.regression;

import com.iafenvoy.iceandfire.entity.*;
import com.iafenvoy.iceandfire.entity.pathfinding.DeathWormNodeMaker;
import com.iafenvoy.iceandfire.entity.util.StymphalianBirdFlock;
import com.iafenvoy.iceandfire.item.block.DragonForgeCoreBlock;
import com.iafenvoy.iceandfire.item.block.ReturningStateBlock;
import com.iafenvoy.iceandfire.item.block.entity.*;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import com.iafenvoy.iceandfire.registry.*;
import com.iafenvoy.iceandfire.screen.menu.LecternMenu;
import com.iafenvoy.iceandfire.util.SearchCooldown;
import com.iafenvoy.iceandfire.world.DragonPosWorldData;
import com.iafenvoy.iceandfire.world.DragonSummoning;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@net.neoforged.fml.common.EventBusSubscriber
@GameTestHolder("iceandfire")
@PrefixGameTestTemplate(false)
public final class IssueRegressionTests {
    // GameTestServer deliberately skips wall-clock tick waits. Pace I/O tests at normal
    // server speed so their production 200-tick deadline is also ten real seconds.
    @net.neoforged.bus.api.SubscribeEvent
    public static void paceChunkIoTests(net.neoforged.neoforge.event.tick.ServerTickEvent.Pre event) {
        if (event.getServer() instanceof net.minecraft.gametest.framework.GameTestServer)
            java.util.concurrent.locks.LockSupport.parkNanos(50_000_000L);
    }

    private static final BlockPos POS = new BlockPos(3, 2, 3);

    @GameTest(template = "regression_empty")
    public static void birdFlightSurvivesAnEmptyFlock(GameTestHelper helper) throws Exception {
        var bird = helper.spawnWithNoFreeWill(IafEntities.STYMPHALIAN_BIRD.get(), POS);
        var leader = helper.spawnWithNoFreeWill(IafEntities.STYMPHALIAN_BIRD.get(), POS.above());
        var flock = StymphalianBirdFlock.createFlock(leader);
        leader.flock = flock;
        leader.discard();
        bird.flock = flock;
        var speed = StymphalianBirdEntity.class.getDeclaredMethod("getFlySpeed", boolean.class);
        speed.setAccessible(true);
        helper.assertTrue((float) speed.invoke(bird, false) == 2F, "Leaderless flight did not use the normal speed");
        bird.setFlying(true);
        bird.airTarget = bird.blockPosition().above(5);
        bird.aiStep();
        helper.assertTrue(Double.isFinite(bird.getDeltaMovement().lengthSqr()), "Leaderless flight produced invalid motion");
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void dragonCorpsesAreDeadButRemainHarvestable(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        for (var type : java.util.List.of(IafEntities.FIRE_DRAGON.get(), IafEntities.ICE_DRAGON.get(), IafEntities.LIGHTNING_DRAGON.get())) {
            DragonBaseEntity dragon = (DragonBaseEntity) helper.spawnWithNoFreeWill(type, POS);
            dragon.setAgeInDays(75);
            dragon.setHealth(0);
            dragon.setModelDead(true);
            helper.assertTrue(!dragon.isAlive() && dragon.isDeadOrDying(), "Corpse still reports alive");
            for (var part : dragon.getParts())
                helper.assertTrue(!part.isPickable(), "Corpse part remains a damage-indicator target");
            helper.assertTrue(dragon.isPickable(), "Corpse body must remain selectable for harvesting");
            int stage = dragon.getDeathStage();
            dragon.mobInteract(player, InteractionHand.MAIN_HAND);
            helper.assertTrue(dragon.getDeathStage() == stage + 1 && !dragon.isRemoved(), "Corpse harvesting was broken");
            dragon.discard();
        }
        helper.succeed();
    }

    @GameTest(template = "regression_empty", timeoutTicks = 500)
    public static void hippogryphFliesWithinLocalAltitudeAndLands(GameTestHelper helper) {
        // The 26.x test world has no terrain outside individual templates.
        // Put the flight arena above the other fixtures so tests cannot obstruct each other.
        var floor = POS.above(32);
        for (int x = -40; x <= 40; x++) for (int z = -40; z <= 40; z++)
            helper.setBlock(floor.offset(x, 0, z), Blocks.STONE);
        var hippo = helper.spawn(IafEntities.HIPPOGRYPH.get(), floor.above(6));
        hippo.setNoAi(false);
        hippo.setTame(true, false);
        hippo.getRandom().setSeed(291);
        hippo.setHovering(true);
        double startY = hippo.getY(), startX = hippo.getX(), startZ = hippo.getZ();
        double[] distance = {0};
        helper.onEachTick(() -> {
            helper.assertTrue(hippo.getY() <= startY + 14, "Hippogryph climbed without an altitude target");
            helper.assertTrue(hippo.getDeltaMovement().y >= -0.4, "Hippogryph stopped flying before touchdown");
            distance[0] = Math.max(distance[0], Math.hypot(hippo.getX() - startX, hippo.getZ() - startZ));
            if (helper.getTick() > 40 && hippo.onGround() && !hippo.isFlying() && !hippo.isHovering()) {
                helper.assertTrue(distance[0] > 1, "Hippogryph hovered without selecting a flight destination");
                helper.assertTrue(!hippo.isNoGravity(), "Landing did not restore gravity");
                hippo.discard();
                helper.succeed();
            }
        });
    }

    @GameTest(template = "regression_empty")
    public static void forgeSelectsAnAwakeAdultAndRejectsBlockedRays(GameTestHelper helper) {
        var pos = POS.above(64);
        for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) for (int z = -1; z <= 1; z++) {
            var block = y != 0 && x != 0 && z != 0 ? IafBlocks.DRAGON_BONE_BLOCK.get() : IafBlocks.DRAGONFORGE_LIGHTNING_BRICK.get();
            helper.setBlock(pos.offset(x, y, z), block);
        }
        helper.setBlock(pos, IafBlocks.DRAGONFORGE_LIGHTNING_CORE_DISABLED.get());
        var inputPos = pos.north();
        helper.setBlock(inputPos, IafBlocks.DRAGONFORGE_LIGHTNING_INPUT.get());
        DragonForgeBlockEntity core = helper.getBlockEntity(pos);
        core.setItem(0, new ItemStack(Items.IRON_INGOT));
        core.setItem(1, new ItemStack(IafItems.LIGHTNING_DRAGON_BLOOD.get()));
        DragonForgeInputBlockEntity input = helper.getBlockEntity(inputPos);
        helper.assertTrue(input.isAssembled(), "Test lightning forge must be ready");
        var dragonPos = helper.absolutePos(inputPos).offset(0, 0, -16);
        var rejected = helper.spawnWithNoFreeWill(IafEntities.LIGHTNING_DRAGON.get(), inputPos);
        rejected.setPos(dragonPos.getX() + 0.5, dragonPos.getY(), dragonPos.getZ() + 0.5);
        rejected.setTame(true, false);
        rejected.setAgeInDays(0);
        var adult = helper.spawnWithNoFreeWill(IafEntities.LIGHTNING_DRAGON.get(), inputPos);
        adult.setAgeInDays(75);
        adult.setPos(dragonPos.getX() + 0.5, dragonPos.getY(), dragonPos.getZ() + 0.5);
        adult.setYRot(0);
        adult.setTame(true, false);
        helper.assertTrue(!adult.isChained(), "Unchained dragon incorrectly reports chained");
        helper.assertTrue(!input.canDragonFuel(rejected), "Baby was eligible to fuel forge");
        rejected.setAgeInDays(75);
        rejected.setInSittingPose(true);
        helper.assertTrue(!input.canDragonFuel(rejected), "Sleeping dragon was eligible to fuel forge");
        var ray = helper.getLevel().clip(new net.minecraft.world.level.ClipContext(adult.getHeadPosition(),
                net.minecraft.world.phys.Vec3.atCenterOf(input.getBlockPos()), net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE, adult));
        helper.assertTrue(input.canDragonFuel(adult), "Adult rejected: alive=" + adult.isAlive() + ", sleeping=" + adult.isSleeping()
                + ", baby=" + adult.isBaby() + ", head=" + adult.getHeadPosition() + ", ray=" + ray.getBlockPos() + ", input=" + input.getBlockPos());
        DragonForgeInputBlockEntity.tick(helper.getLevel(), input.getBlockPos(), input.getBlockState(), input);
        helper.assertTrue(input.getBlockPos().equals(adult.burningTarget), "Unavailable dragon prevented adult selection");
        adult.updateBurnTarget();
        helper.assertTrue(adult.isBreathingFire() && input.getBlockPos().equals(adult.burningTarget), "Lure and breath eligibility disagree");
        adult.fireBreathTicks = 40;
        adult.burnProgress = 40;
        adult.updateBurnTarget();
        helper.assertTrue(core.lastDragonFlameTimer > 0, "Lightning breath did not transfer power into the forge");
        rejected.setInSittingPose(false);
        rejected.setModelDead(true);
        helper.assertTrue(!input.canDragonFuel(rejected), "Corpse was eligible to fuel forge");
        adult.setTame(false, false);
        helper.assertTrue(!input.canDragonFuel(adult), "Wild unchained dragon was eligible to fuel forge");
        com.iafenvoy.iceandfire.data.component.ChainData.get(adult).attachChain(rejected.getUUID());
        helper.assertTrue(adult.isChained() && input.canDragonFuel(adult), "Chained adult cannot fuel forge");
        var middle = adult.getHeadPosition().lerp(net.minecraft.world.phys.Vec3.atCenterOf(input.getBlockPos()), 0.5);
        var obstruction = net.minecraft.core.BlockPos.containing(middle);
        var previous = helper.getLevel().getBlockState(obstruction);
        try {
            helper.getLevel().setBlockAndUpdate(obstruction, Blocks.STONE.defaultBlockState());
            helper.assertTrue(!input.canDragonFuel(adult), "Obstructed head-to-forge ray was accepted");
            adult.updateBurnTarget();
            helper.assertTrue(adult.burningTarget == null && !adult.isBreathingFire(), "Blocked input retained its burn target");
        } finally {
            helper.getLevel().setBlockAndUpdate(obstruction, previous);
            adult.discard();
            rejected.discard();
        }
        helper.succeed();
    }


    @GameTest(template = "regression_empty")
    public static void iceDragonEndRestrictionHonorsConfigAndDimension(GameTestHelper helper) {
        var option = com.iafenvoy.iceandfire.config.IafCommonConfig.INSTANCE.worldGen.preventIceDragonStructuresInEnd;
        helper.assertTrue(option.getDefaultValue(), "End restriction must default to enabled");
        boolean previous = option.getValue();
        var chunk = helper.getLevel().getChunkAt(helper.absolutePos(POS));
        var savedStarts = new java.util.HashMap<>(chunk.getAllStarts());
        var registry = helper.getLevel().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE);
        try {
            for (boolean enabled : new boolean[]{true, false}) {
                option.setValue(enabled);
                for (var dimension : java.util.List.of(net.minecraft.world.level.Level.END,
                        net.minecraft.world.level.Level.OVERWORLD, net.minecraft.world.level.Level.NETHER)) {
                    var level = helper.getLevel().getServer().getLevel(dimension);
                    helper.assertTrue(level != null, "Missing test dimension: " + dimension);
                    for (String type : java.util.List.of("ice", "fire", "lightning")) {
                        for (String kind : java.util.List.of("cave", "roost")) {
                            var structure = registry.get(com.iafenvoy.iceandfire.IceAndFire.id(type + "_dragon_" + kind));
                            helper.assertTrue(structure != null, "Missing dragon structure");
                            var pieces = new net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer(java.util.List.of(
                                    new net.minecraft.world.level.levelgen.structure.structures.BuriedTreasurePieces.BuriedTreasurePiece(BlockPos.ZERO)));
                            var start = new net.minecraft.world.level.levelgen.structure.StructureStart(structure, chunk.getPos(), 0, pieces);
                            helper.assertTrue(start.isValid(), "Test structure start must be valid");
                            level.structureManager().setStartForStructure(net.minecraft.core.SectionPos.of(chunk.getPos(), 0), structure, start, chunk);
                            boolean blocked = enabled && dimension.equals(net.minecraft.world.level.Level.END) && type.equals("ice");
                            helper.assertTrue(chunk.getStartForStructure(structure) == (blocked
                                            ? net.minecraft.world.level.levelgen.structure.StructureStart.INVALID_START : start),
                                    "Wrong start stored for " + dimension + "/" + type + "/" + kind + ", enabled=" + enabled);
                        }
                    }
                }
            }
        } finally {
            option.setValue(previous);
            chunk.setAllStarts(savedStarts);
        }
        helper.succeed();
    }


    @GameTest(template = "regression_empty")
    public static void searchCooldownDoesNotMissEitherSchedulerPhase(GameTestHelper helper) {
        for (int phase = 0; phase < 4; phase++) {
            SearchCooldown cooldown = new SearchCooldown(4);
            int searches = 0;
            for (int time = phase; time < 400 + phase; time += 2)
                if (cooldown.isReady(time)) searches++;
            helper.assertTrue(searches == 100, "searches must progress in every scheduler phase");
            helper.assertTrue(cooldown.isReady(0), "world clock rollback must not stall searches");
        }
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void coreStateChangesPreserveInventoryAndIdentity(GameTestHelper helper) {
        helper.setBlock(POS, IafBlocks.DRAGONFORGE_FIRE_CORE_DISABLED.get());
        DragonForgeBlockEntity forge = helper.getBlockEntity(POS);
        forge.setItem(0, new ItemStack(Items.IRON_INGOT, 7));
        BlockPos absolute = helper.absolutePos(POS);
        for (int i = 0; i < 6; i++) {
            DragonForgeCoreBlock.setState(IafDragonTypes.FIRE, helper.getLevel(), absolute, i % 2 == 0);
            helper.assertTrue(helper.getBlockEntity(POS) == forge, "core transition replaced the block entity");
            helper.assertTrue(!forge.isRemoved() && forge.getItem(0).getCount() == 7, "core transition lost inventory");
            helper.assertTrue(forge.getBlockState() == helper.getBlockState(POS), "core retained a stale block state");
        }
        helper.assertTrue(helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                new net.minecraft.world.phys.AABB(absolute).inflate(2)).isEmpty(), "transition dropped inventory");
        helper.setBlock(POS, Blocks.AIR);
        int count = helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                new net.minecraft.world.phys.AABB(absolute).inflate(2)).stream().mapToInt(e -> e.getItem().getCount()).sum();
        helper.assertTrue(count == 7, "actual removal must drop inventory once");
        helper.succeed();
    }

    private static DragonForgeBlockEntity assembledForge(GameTestHelper helper) {
        for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) for (int z = -1; z <= 1; z++) {
            var block = y != 0 && x != 0 && z != 0 ? IafBlocks.DRAGON_BONE_BLOCK.get() : IafBlocks.DRAGONFORGE_FIRE_BRICK.get();
            helper.setBlock(POS.offset(x, y, z), block);
        }
        helper.setBlock(POS, IafBlocks.DRAGONFORGE_FIRE_CORE_DISABLED.get());
        return helper.getBlockEntity(POS);
    }

    @GameTest(template = "regression_empty")
    public static void forgeHeatExpiresAndLongProgressRoundTrips(GameTestHelper helper) {
        DragonForgeBlockEntity forge = assembledForge(helper);
        helper.assertTrue(forge.assembled(), "test forge must be assembled");
        forge.setItem(0, new ItemStack(Items.COBBLESTONE, 8));
        forge.transferPower(10);
        for (int i = 0; i < 45; i++) DragonForgeBlockEntity.tick(helper.getLevel(), helper.absolutePos(POS), forge.getBlockState(), forge);
        var registries = helper.getLevel().registryAccess();
        double progress = forge.saveWithoutMetadata(registries).getDouble("CookTime");
        helper.assertTrue(forge.lastDragonFlameTimer == 0 && progress > 0, "test must reach the cooling phase");
        DragonForgeBlockEntity.tick(helper.getLevel(), helper.absolutePos(POS), forge.getBlockState(), forge);
        helper.assertTrue(forge.saveWithoutMetadata(registries).getDouble("CookTime") == progress - 1, "progress did not decay without heat");
        CompoundTag tag = forge.saveWithoutMetadata(registries);
        tag.putDouble("CookTime", 40000.25);
        forge.loadWithComponents(tag, registries);
        helper.assertTrue(forge.saveWithoutMetadata(registries).getDouble("CookTime") == 40000.25, "long fractional progress was truncated");
        tag.putInt("CookTime", 123);
        forge.loadWithComponents(tag, registries);
        helper.assertTrue(forge.saveWithoutMetadata(registries).getDouble("CookTime") == 123, "legacy integer progress was not loaded");
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void forgeRespectsOutputComponentsAndRecipeCopies(GameTestHelper helper) {
        DragonForgeBlockEntity forge = assembledForge(helper);
        forge.setItem(0, new ItemStack(Items.COBBLESTONE));
        ItemStack output = new ItemStack(IafBlocks.ASH.get());
        output.set(DataComponents.CUSTOM_NAME, Component.literal("different components"));
        forge.setItem(2, output);
        helper.assertTrue(!forge.canSmelt(), "different output components must not merge");
        DragonForgeRecipe recipe = new DragonForgeRecipe(Ingredient.of(Items.IRON_INGOT), Ingredient.EMPTY,
                new ItemStack(Items.DIAMOND, 2), "fire", 100);
        recipe.assemble(new DragonForgeBlockEntity.DragonForgeRecipeInput(forge), helper.getLevel().registryAccess()).setCount(19);
        helper.assertTrue(recipe.assemble(new DragonForgeBlockEntity.DragonForgeRecipeInput(forge), helper.getLevel().registryAccess()).getCount() == 2,
                "assemble exposed the shared result template");
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void lecternRejectsInvalidButtonsAndStaleMenus(GameTestHelper helper) {
        helper.setBlock(POS, IafBlocks.LECTERN.get());
        LecternBlockEntity lectern = helper.getBlockEntity(POS);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(helper.absolutePos(POS).getCenter());
        lectern.setItem(0, new ItemStack(IafItems.BESTIARY.get()));
        lectern.setItem(1, new ItemStack(IafItems.MANUSCRIPT.get(), 3));
        LecternMenu menu = new LecternMenu(1, lectern, player.getInventory(), new SimpleContainerData(3));
        for (int button : new int[]{-1, 3, Integer.MAX_VALUE, Integer.MIN_VALUE})
            helper.assertTrue(!menu.clickMenuButton(player, button), "invalid button accepted");
        helper.assertTrue(lectern.getItem(1).getCount() == 3, "invalid buttons consumed manuscripts");
        helper.assertTrue(menu.stillValid(player), "nearby menu should be valid");
        player.setPos(helper.absolutePos(POS).getCenter().add(30, 0, 0));
        helper.assertTrue(!menu.stillValid(player), "distant menu stayed valid");
        player.setPos(helper.absolutePos(POS).getCenter());
        helper.setBlock(POS, Blocks.AIR);
        helper.assertTrue(!menu.stillValid(player), "removed lectern stayed valid");
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void jarSendsInitialStateAndReleasesOnce(GameTestHelper helper) {
        helper.setBlock(POS, IafBlocks.JAR_EMPTY.get());
        JarBlockEntity jar = helper.getBlockEntity(POS);
        jar.hasPixie = true;
        jar.pixieType = 3;
        jar.hasProduced = true;
        jar.pixieItems.set(0, new ItemStack(Items.DIAMOND));
        var registries = helper.getLevel().registryAccess();
        CompoundTag initial = jar.getUpdateTag(registries);
        helper.assertTrue(initial.getBoolean("HasPixie") && initial.getInt("PixieType") == 3 && initial.getBoolean("HasProduced"), "initial jar sync omitted state");
        jar.releasePixie();
        jar.releasePixie();
        helper.assertTrue(!jar.hasPixie && !jar.hasProduced && jar.pixieItems.getFirst().isEmpty(), "jar release retained saved contents");
        var pixies = helper.getLevel().getEntitiesOfClass(PixieEntity.class, new net.minecraft.world.phys.AABB(helper.absolutePos(POS)).inflate(3));
        helper.assertTrue(pixies.size() == 1 && pixies.getFirst().getMainHandItem().is(Items.DIAMOND), "release must create exactly one pixie with its item");
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void multipartScaleKeepsRegisteredParts(GameTestHelper helper) {
        SeaSerpentEntity serpent = helper.spawnWithNoFreeWill(IafEntities.SEA_SERPENT.get(), POS);
        var parts = serpent.getParts().clone();
        serpent.updateScale(2);
        serpent.updateScale(4);
        for (int i = 0; i < parts.length; i++) {
            helper.assertTrue(parts[i] == serpent.getParts()[i], "scale replaced multipart identity");
            helper.assertTrue(helper.getLevel().getPartEntities().contains(parts[i]), "current part is absent from the server registry");
        }
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void wormChecksTheWholeVolume(GameTestHelper helper) throws Exception {
        var mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 2, 1));
        BlockPos origin = helper.absolutePos(POS);
        for (int x = 0; x < 2; x++) for (int y = 0; y < 2; y++) for (int z = 0; z < 2; z++)
            helper.setBlock(POS.offset(x, y, z), Blocks.SAND);
        helper.setBlock(POS, Blocks.STONE); // The last voxel is passable; the first is blocked.
        DeathWormNodeMaker evaluator = new DeathWormNodeMaker();
        evaluator.prepare(new PathNavigationRegion(helper.getLevel(), origin.offset(-2, -2, -2), origin.offset(4, 4, 4)), mob);
        for (String field : new String[]{"entityWidth", "entityHeight", "entityDepth"}) {
            var size = net.minecraft.world.level.pathfinder.NodeEvaluator.class.getDeclaredField(field);
            size.setAccessible(true);
            size.setInt(evaluator, 2);
        }
        var isFree = DeathWormNodeMaker.class.getDeclaredMethod("isFree", int.class, int.class, int.class);
        isFree.setAccessible(true);
        helper.assertTrue(isFree.invoke(evaluator, origin.getX(), origin.getY(), origin.getZ()) == PathType.BLOCKED, "solid voxel inside worm volume was ignored");
        evaluator.done();
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void flockElectsALiveLeaderAfterRemoval(GameTestHelper helper) {
        StymphalianBirdEntity first = helper.spawnWithNoFreeWill(IafEntities.STYMPHALIAN_BIRD.get(), POS);
        StymphalianBirdEntity second = helper.spawnWithNoFreeWill(IafEntities.STYMPHALIAN_BIRD.get(), POS.above());
        var flock = StymphalianBirdFlock.createFlock(first);
        first.flock = flock;
        second.flock = flock;
        flock.addToFlock(second);
        flock.addToFlock(second);
        first.discard();
        helper.assertTrue(flock.getLeader() == second, "removed leader was retained or reselected");
        second.discard();
        helper.assertTrue(flock.getLeader() == null, "empty flock retained a leader");
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void transformedBlocksReceiveRandomTicks(GameTestHelper helper) {
        var block = (ReturningStateBlock) IafBlocks.CHARRED_DIRT.get();
        helper.setBlock(POS, block.defaultBlockState().setValue(ReturningStateBlock.REVERTS, true));
        RandomSource random = RandomSource.create(42);
        for (int i = 0; i < 40 && helper.getBlockState(POS).is(block); i++)
            helper.getBlockState(POS).randomTick(helper.getLevel(), helper.absolutePos(POS), random);
        helper.assertTrue(helper.getBlockState(POS).is(Blocks.DIRT), "random ticks did not restore the terrain");
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void crystalChecksOwnershipAndClearsSuccessfulBinding(GameTestHelper helper) {
        var owner = new net.neoforged.neoforge.common.util.FakePlayer(helper.getLevel(), new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "crystal-test"));
        var dragon = helper.spawnWithNoFreeWill(IafEntities.FIRE_DRAGON.get(), POS);
        dragon.setTame(true, false);
        dragon.setOwnerUUID(owner.getUUID());
        dragon.setCrystalBound(true);
        ItemStack crystal = new ItemStack(IafItems.SUMMONING_CRYSTAL_FIRE.get());
        CompoundTag data = new CompoundTag(), binding = new CompoundTag();
        binding.putUUID("DragonUUID", dragon.getUUID());
        data.put("Dragon", binding);
        crystal.set(IafDataComponents.CRYSTAL_DRAGON_DATA.get(), data);
        owner.setItemInHand(InteractionHand.MAIN_HAND, crystal);
        DragonPosWorldData positions = DragonPosWorldData.get(helper.getLevel());
        positions.addDragon(dragon.getUUID(), dragon.blockPosition());
        BlockPos destination = helper.absolutePos(POS.offset(2, 0, 0));
        dragon.setOwnerUUID(java.util.UUID.randomUUID());
        DragonSummoning.request(owner, InteractionHand.MAIN_HAND, destination);
        helper.assertTrue(dragon.isBoundToCrystal() && crystal.has(IafDataComponents.CRYSTAL_DRAGON_DATA.get()), "non-owner summoned the dragon");
        dragon.setOwnerUUID(owner.getUUID());
        DragonSummoning.request(owner, InteractionHand.MAIN_HAND, destination);
        helper.assertTrue(!dragon.isBoundToCrystal() && !crystal.has(IafDataComponents.CRYSTAL_DRAGON_DATA.get()), "successful summon retained binding");
        helper.assertTrue(dragon.blockPosition().equals(destination) && positions.getDragonPos(dragon.getUUID()) == null, "summon did not move or clean saved location");
        helper.succeed();
    }

    @GameTest(template = "regression_empty", timeoutTicks = 400)
    public static void crystalLoadsAnUnloadedDragonAndReleasesItsTicket(GameTestHelper helper) {
        var level = helper.getLevel();
        var owner = new net.neoforged.neoforge.common.util.FakePlayer(level,
                new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "unloaded-crystal-test"));
        BlockPos far = helper.absolutePos(POS).offset(1024, 0, 1024);
        level.getChunkAt(far);
        var dragon = IafEntities.FIRE_DRAGON.get().create(level);
        dragon.setPos(far.getCenter());
        dragon.setNoAi(true);
        dragon.setNoGravity(true);
        dragon.setTame(true, false);
        dragon.setOwnerUUID(owner.getUUID());
        dragon.setCrystalBound(true);
        level.addFreshEntity(dragon);
        java.util.UUID id = dragon.getUUID();
        ItemStack crystal = new ItemStack(IafItems.SUMMONING_CRYSTAL_FIRE.get());
        CompoundTag data = new CompoundTag(), binding = new CompoundTag();
        binding.putUUID("DragonUUID", id);
        data.put("Dragon", binding);
        crystal.set(IafDataComponents.CRYSTAL_DRAGON_DATA.get(), data);
        owner.setItemInHand(InteractionHand.MAIN_HAND, crystal);
        BlockPos destination = helper.absolutePos(POS);
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(dragon.isRemoved() && dragon.getRemovalReason() == net.minecraft.world.entity.Entity.RemovalReason.UNLOADED_TO_CHUNK && level.getEntity(id) == null, "source chunk has not unloaded"))
                .thenExecute(() -> {
                    helper.assertTrue(DragonPosWorldData.get(level).getDragonPos(id) != null, "unloading did not save the dragon location");
                    DragonSummoning.request(owner, InteractionHand.MAIN_HAND, destination);
                })
                .thenWaitUntil(() -> helper.assertTrue(!crystal.has(IafDataComponents.CRYSTAL_DRAGON_DATA.get()), "unloaded dragon was not summoned; pending=" + pendingInfo() + ", ownerAlive=" + owner.isAlive() + ", source=" + level.getEntity(id) + ", saved=" + DragonPosWorldData.get(level).getDragonPos(id)))
                .thenExecute(() -> {
                    helper.assertTrue(level.getEntity(id) instanceof DragonBaseEntity summoned && summoned.blockPosition().equals(destination), "loaded dragon arrived at the wrong destination");
                    helper.assertTrue(!level.getForcedChunks().contains(new net.minecraft.world.level.ChunkPos(far).toLong()), "summon persisted a forced chunk");
                    try {
                        var field = DragonSummoning.class.getDeclaredField("PENDING");
                        field.setAccessible(true);
                        helper.assertTrue(!((java.util.Map<?, ?>) field.get(null)).containsKey(id), "summon retained its pending request");
                    } catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
                })
                .thenWaitUntil(() -> helper.assertTrue(!level.getChunkSource().hasChunk(far.getX() >> 4, far.getZ() >> 4), "source ticket was not released"))
                .thenSucceed();
    }

    private static Object pendingInfo() {
        try {
            var field = DragonSummoning.class.getDeclaredField("PENDING");
            field.setAccessible(true);
            return field.get(null).toString();
        } catch (Exception e) { return e; }
    }

    private static final class CountingZombie extends net.minecraft.world.entity.monster.Zombie {
        int hits;
        CountingZombie(net.minecraft.world.level.Level level) { super(EntityType.ZOMBIE, level); }
        @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
            this.hits++;
            return true;
        }
    }

    @GameTest(template = "regression_empty")
    public static void lightningSelectsEachMobOnce(GameTestHelper helper) {
        var attacker = helper.makeMockPlayer(GameType.SURVIVAL);
        attacker.setPos(helper.absolutePos(POS).getCenter());
        CountingZombie[] targets = new CountingZombie[4];
        for (int i = 0; i < targets.length; i++) {
            targets[i] = new CountingZombie(helper.getLevel());
            targets[i].setPos(helper.absolutePos(POS).getCenter().add(i + 1, 0, 0));
            targets[i].setNoAi(true);
            helper.getLevel().addFreshEntity(targets[i]);
        }
        new com.iafenvoy.iceandfire.item.ability.LightningMultihitAbility().active(new ItemStack(Items.DIAMOND_SWORD), targets[0], attacker);
        helper.assertTrue(targets[0].hits == 0, "chain hit its initial target again");
        for (int i = 1; i < targets.length; i++) helper.assertTrue(targets[i].hits == 1, "chain repeated or skipped a target");
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void sceptersChargeTheCorrectStackAndSyncRemoval(GameTestHelper helper) throws Exception {
        var first = helper.makeMockPlayer(GameType.SURVIVAL);
        var second = helper.makeMockPlayer(GameType.SURVIVAL);
        var target = helper.spawnWithNoFreeWill(EntityType.COW, POS);
        first.setPos(target.position().add(0, 0, -3));
        second.setPos(target.position().add(0, 0, -3));
        first.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
        second.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
        var scepter = IafItems.COCKATRICE_SCEPTER.get();
        ItemStack firstStack = new ItemStack(scepter), secondStack = new ItemStack(scepter);
        first.setItemInHand(InteractionHand.MAIN_HAND, firstStack);
        second.setItemInHand(InteractionHand.MAIN_HAND, secondStack);
        var firstData = com.iafenvoy.iceandfire.data.component.MiscData.get(first);
        firstData.addScepterTarget(target);
        firstData.isDirty();
        first.tickCount = 20;
        scepter.onUseTick(helper.getLevel(), first, firstStack, 100);
        helper.assertTrue(firstStack.getDamageValue() == 1 && secondStack.getDamageValue() == 0, "scepter durability crossed users");
        scepter.releaseUsing(secondStack, helper.getLevel(), second, 100);
        helper.assertTrue(secondStack.getDamageValue() == 0, "another player paid the first user's durability");
        scepter.releaseUsing(firstStack, helper.getLevel(), first, 100);
        helper.assertTrue(firstData.getTargetedByScepters().isEmpty() && firstData.isDirty(), "release did not synchronize target removal");
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void mountControlsIgnoreClientCoordinates(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var worm = helper.spawnWithNoFreeWill(IafEntities.DEATH_WORM.get(), POS);
        player.startRiding(worm, true);
        var position = worm.position();
        var context = (net.neoforged.neoforge.network.handling.IPayloadContext) java.lang.reflect.Proxy.newProxyInstance(
                IssueRegressionTests.class.getClassLoader(), new Class[]{net.neoforged.neoforge.network.handling.IPayloadContext.class},
                (proxy, method, arguments) -> switch (method.getName()) {
                    case "player" -> player;
                    case "enqueueWork" -> {
                        ((Runnable) arguments[0]).run();
                        yield java.util.concurrent.CompletableFuture.completedFuture(null);
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        com.iafenvoy.iceandfire.network.ServerNetworkHandlers.handleDragonControl(
                new com.iafenvoy.iceandfire.network.payload.DragonControlC2SPayload(worm.getId(), (byte) 1, new BlockPos(29000000, 100, 29000000)), context);
        helper.assertTrue(worm.position().equals(position) && worm.getControlState() == 1, "control packet moved the mount or rejected its rider");
        player.stopRiding();
        com.iafenvoy.iceandfire.network.ServerNetworkHandlers.handleDragonControl(
                new com.iafenvoy.iceandfire.network.payload.DragonControlC2SPayload(worm.getId(), (byte) 2, BlockPos.ZERO), context);
        helper.assertTrue(worm.getControlState() == 1, "non-rider changed the control state");
        helper.succeed();
    }

    @GameTest(template = "regression_empty")
    public static void offhandGhostSwordUsesTheOffhandStack(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(helper.absolutePos(POS).getCenter());
        ItemStack main = new ItemStack(Items.DIAMOND_PICKAXE), offhand = new ItemStack(IafItems.GHOST_SWORD.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, main);
        player.setItemInHand(InteractionHand.OFF_HAND, offhand);
        player.swing(InteractionHand.OFF_HAND);
        helper.assertTrue(main.getDamageValue() == 0 && offhand.getDamageValue() == 1, "ghost sword used the wrong hand's durability");
        var shots = helper.getLevel().getEntitiesOfClass(GhostSwordEntity.class,
                new net.minecraft.world.phys.AABB(helper.absolutePos(POS)).inflate(4));
        helper.assertTrue(shots.size() == 1 && shots.getFirst().getBaseDamage() > 0, "offhand swing failed to spawn a damaging projectile");
        helper.succeed();
    }

    @GameTest(template = "regression_empty", timeoutTicks = 100)
    public static void crystalCanSummonAnOwnedDragonAcrossDimensions(GameTestHelper helper) {
        var level = helper.getLevel();
        var other = level.getServer().getLevel(net.minecraft.world.level.Level.NETHER);
        var owner = new net.neoforged.neoforge.common.util.FakePlayer(level,
                new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "dimension-crystal-test"));
        BlockPos source = new BlockPos(0, 100, 0);
        other.getChunkAt(source);
        var dragon = IafEntities.FIRE_DRAGON.get().create(other);
        dragon.setPos(source.getCenter());
        dragon.setNoAi(true);
        dragon.setTame(true, false);
        dragon.setOwnerUUID(owner.getUUID());
        dragon.setCrystalBound(true);
        other.addFreshEntity(dragon);
        java.util.UUID id = dragon.getUUID();
        ItemStack crystal = new ItemStack(IafItems.SUMMONING_CRYSTAL_FIRE.get());
        CompoundTag data = new CompoundTag(), binding = new CompoundTag();
        binding.putUUID("DragonUUID", id);
        data.put("Dragon", binding);
        crystal.set(IafDataComponents.CRYSTAL_DRAGON_DATA.get(), data);
        owner.setItemInHand(InteractionHand.MAIN_HAND, crystal);
        BlockPos destination = helper.absolutePos(POS);
        DragonSummoning.request(owner, InteractionHand.MAIN_HAND, destination);
        helper.succeedWhen(() -> {
            helper.assertTrue(!crystal.has(IafDataComponents.CRYSTAL_DRAGON_DATA.get()), "cross-dimension summon did not complete; pending=" + pendingInfo() + ", ownerAlive=" + owner.isAlive() + ", held=" + (owner.getMainHandItem() == crystal) + ", source=" + other.getEntity(id) + ", saved=" + DragonPosWorldData.get(other).getDragonPos(id) + ", removed=" + dragon.getRemovalReason() + ", alive=" + dragon.isAlive() + ", sourceChunk=" + other.getChunkSource().hasChunk(0, 0));
            helper.assertTrue(level.getEntity(id) instanceof DragonBaseEntity moved && moved.blockPosition().equals(destination)
                    && owner.getUUID().equals(moved.getOwnerUUID()) && !moved.isBoundToCrystal(), "cross-dimension summon lost position, owner or binding state");
            helper.assertTrue(other.getEntity(id) == null, "cross-dimension summon duplicated the dragon");
        });
    }
}
