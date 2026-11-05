package com.iafenvoy.iceandfire.test;

import com.iafenvoy.iceandfire.data.component.MiscData;
import com.iafenvoy.iceandfire.data.component.PortalData;
import com.iafenvoy.iceandfire.entity.*;
import com.iafenvoy.iceandfire.entity.ai.CockatriceAITargetItems;
import com.iafenvoy.iceandfire.entity.ai.SearchCooldown;
import com.iafenvoy.iceandfire.entity.block.*;
import com.iafenvoy.iceandfire.entity.util.StymphalianBirdFlock;
import com.iafenvoy.iceandfire.item.ItemCockatriceScepter;
import com.iafenvoy.iceandfire.item.block.BlockDragonForgeCore;
import com.iafenvoy.iceandfire.item.tool.ItemGhostSword;
import com.iafenvoy.iceandfire.network.MultipartInteraction;
import com.iafenvoy.iceandfire.network.ServerNetworkHelper;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.screen.handler.LecternScreenHandler;
import com.iafenvoy.iceandfire.world.DragonPosWorldData;
import com.iafenvoy.iceandfire.world.SummoningCrystalManager;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class RegressionTests implements FabricGameTest {
    private static final BlockPos POS = new BlockPos(3, 2, 3);

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void forgeStateKeepsInventory(TestContext c) {
        c.setBlockState(POS, IafBlocks.DRAGONFORGE_FIRE_CORE_DISABLED.get());
        BlockPos pos = c.getAbsolutePos(POS);
        BlockEntityDragonForge forge = (BlockEntityDragonForge) c.getBlockEntity(POS);
        forge.setStack(0, new ItemStack(Items.IRON_INGOT, 12));
        for (boolean active : new boolean[]{true, false, true}) {
            BlockDragonForgeCore.setState(0, active, c.getWorld(), pos);
            c.assertTrue(c.getWorld().getBlockEntity(pos) == forge, "Core replacement lost BE identity");
            c.assertTrue(forge.getStack(0).getCount() == 12, "Core replacement changed inventory");
        }
        c.assertTrue(c.getWorld().getEntitiesByClass(ItemEntity.class, new Box(pos).expand(2), e -> true).isEmpty(),
                "Core replacement scattered items");
        c.complete();
    }

    private static class TestForge extends BlockEntityDragonForge {
        DragonForgeRecipe recipe;
        TestForge(BlockPos pos, BlockState state) { super(pos, state); }
        @Override public Optional<DragonForgeRecipe> getCurrentRecipe() { return Optional.ofNullable(this.recipe); }
        @Override public boolean assembled() { return true; }
    }

    private TestForge forge(TestContext c, ItemStack output) {
        c.setBlockState(POS, IafBlocks.DRAGONFORGE_FIRE_CORE.get());
        TestForge forge = new TestForge(c.getAbsolutePos(POS), c.getBlockState(POS));
        forge.setWorld(c.getWorld());
        forge.recipe = new DragonForgeRecipe(new Identifier("iceandfire_regressions", "test"),
                Ingredient.ofItems(Items.IRON_INGOT), Ingredient.ofItems(Items.REDSTONE), output, "fire", 100000);
        forge.setStack(0, new ItemStack(Items.IRON_INGOT, 10));
        forge.setStack(1, new ItemStack(Items.REDSTONE, 10));
        return forge;
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void forgeCoolingAndPersistence(TestContext c) {
        TestForge forge = forge(c, new ItemStack(Items.DIAMOND));
        forge.getPropertyDelegate().cookTime = 40000;
        forge.lastDragonFlameTimer = 20;
        NbtCompound saved = forge.createNbt();
        TestForge restored = forge(c, new ItemStack(Items.DIAMOND));
        restored.readNbt(saved);
        c.assertTrue(restored.getPropertyDelegate().cookTime == 40000 && restored.lastDragonFlameTimer == 20,
                "Progress was truncated or heat was lost");
        restored.lastDragonFlameTimer = 0;
        for (int i = 0; i < 100; i++) BlockEntityDragonForge.tick(c.getWorld(), restored.getPos(), restored.getCachedState(), restored);
        c.assertTrue(restored.getPropertyDelegate().cookTime == 39900, "No-flame progress did not cool down");
        restored.transferPower(Integer.MAX_VALUE);
        c.assertTrue(restored.getPropertyDelegate().cookTime == 100000, "Power exceeded recipe cap or overflowed");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void forgeOutputNbtAndCapacity(TestContext c) {
        ItemStack result = new ItemStack(Items.DIAMOND);
        result.setCustomName(Text.literal("A"));
        TestForge forge = forge(c, result);
        ItemStack incompatible = result.copy();
        incompatible.setCustomName(Text.literal("B"));
        forge.setStack(2, incompatible);
        c.assertFalse(forge.canSmelt(), "Different output NBT was merged");
        forge.setStack(2, result.copy());
        c.assertTrue(forge.canSmelt(), "Matching output NBT was rejected");
        forge.recipe = new DragonForgeRecipe(new Identifier("iceandfire_regressions", "oversized"),
                Ingredient.EMPTY, Ingredient.EMPTY, new ItemStack(Items.IRON_SWORD, 2), "fire", 20);
        forge.setStack(2, ItemStack.EMPTY);
        c.assertFalse(forge.canSmelt(), "Nonstackable output exceeded empty-slot capacity");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void recipeReturnsCopies(TestContext c) {
        TestForge forge = forge(c, new ItemStack(Items.DIAMOND, 2));
        DragonForgeRecipe recipe = forge.recipe;
        recipe.getResultItem().setCount(55);
        recipe.craft(forge, c.getWorld().getRegistryManager()).setCustomName(Text.literal("mutated"));
        recipe.getOutput(c.getWorld().getRegistryManager()).setCount(0);
        c.assertTrue(recipe.getResultItem().getCount() == 2 && !recipe.getResultItem().hasCustomName(), "Recipe template mutated");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void lecternBoundsAndLifetime(TestContext c) {
        c.setBlockState(POS, IafBlocks.LECTERN.get());
        BlockEntityLectern lectern = (BlockEntityLectern) c.getBlockEntity(POS);
        PlayerEntity player = c.createMockSurvivalPlayer();
        player.setPosition(Vec3d.ofCenter(lectern.getPos()));
        lectern.setStack(0, new ItemStack(IafItems.BESTIARY.get()));
        lectern.setStack(1, new ItemStack(IafItems.MANUSCRIPT.get(), 64));
        ArrayPropertyDelegate pages = new ArrayPropertyDelegate(3);
        pages.set(0, Integer.MAX_VALUE);
        pages.set(1, -2);
        LecternScreenHandler handler = new LecternScreenHandler(1, lectern, player.getInventory(), pages);
        c.assertFalse(handler.onButtonClick(player, -1), "Negative button accepted");
        c.assertFalse(handler.onButtonClick(player, Integer.MAX_VALUE), "Large button accepted");
        c.assertTrue(handler.getPossiblePages()[0] == null && handler.getPossiblePages()[1] == null, "Invalid page accepted");
        player.setPosition(player.getPos().add(9, 0, 0));
        c.assertFalse(handler.canUse(player), "Remote lectern menu stayed valid");
        c.removeBlock(POS);
        c.assertFalse(handler.canUse(player), "Removed lectern menu stayed valid");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void podiumInventoryContract(TestContext c) {
        c.setBlockState(POS, IafBlocks.PODIUM_OAK.get());
        BlockEntityPodium podium = (BlockEntityPodium) c.getBlockEntity(POS);
        podium.setStack(0, new ItemStack(Items.DIAMOND, 4));
        c.assertTrue(podium.removeStack(0, 2).getCount() == 2, "Counted removal failed");
        c.assertTrue(podium.removeStack(0).getCount() == 2 && podium.isEmpty(), "Whole-slot removal failed");
        podium.setStack(0, new ItemStack(Items.DIAMOND));
        BlockEntityPodium copy = new BlockEntityPodium(podium.getPos(), podium.getCachedState());
        copy.readNbt(podium.toInitialChunkDataNbt());
        c.assertTrue(copy.getStack(0).isOf(Items.DIAMOND), "Podium initial NBT incomplete");
        podium.clear();
        c.assertTrue(podium.isEmpty(), "Clear failed");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void jarInitialStateAndSingleRelease(TestContext c) {
        c.setBlockState(POS, IafBlocks.JAR_PIXIE_0.get());
        BlockEntityJar jar = (BlockEntityJar) c.getBlockEntity(POS);
        jar.hasProduced = true;
        jar.pixieItems.set(0, new ItemStack(Items.DIAMOND, 3));
        jar.pixieOwnerUUID = UUID.randomUUID();
        jar.tamedPixie = true;
        BlockEntityJar copy = new BlockEntityJar(jar.getPos(), jar.getCachedState());
        copy.readNbt(jar.toInitialChunkDataNbt());
        c.assertTrue(copy.hasPixie && copy.hasProduced && copy.pixieItems.get(0).getCount() == 3
                && copy.pixieOwnerUUID.equals(jar.pixieOwnerUUID), "Jar initial NBT incomplete");
        jar.releasePixie();
        jar.releasePixie();
        var pixies = c.getWorld().getEntitiesByClass(EntityPixie.class, new Box(jar.getPos()).expand(2), e -> true);
        c.assertTrue(pixies.size() == 1 && pixies.get(0).getMainHandStack().getCount() == 3, "Release duplicated/lost pixie items");
        c.assertTrue(!jar.hasPixie && jar.pixieItems.get(0).isEmpty() && jar.pixieOwnerUUID == null, "Release retained inventory/owner");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void multipartReachAndController(TestContext c) {
        var parent = c.spawnMob(EntityType.PIG, POS);
        EntitySlowPart part = new EntitySlowPart(parent, 0, 0, 0, 1, 1, 1);
        part.setPosition(parent.getPos());
        c.getWorld().spawnEntity(part);
        var owner = c.createMockCreativeServerPlayerInWorld();
        owner.setPosition(parent.getPos().add(0, 0, 2));
        c.assertTrue(MultipartInteraction.canInteract(owner, part), "Nearby valid part rejected");
        owner.setPosition(parent.getPos().add(0, 0, 20));
        c.assertFalse(MultipartInteraction.canInteract(owner, part), "Remote part accepted");
        EntityHippogryph mount = c.spawnMob(IafEntities.HIPPOGRYPH.get(), new BlockPos(2, 2, 2));
        mount.setTamed(true);
        mount.setOwner(owner);
        boolean mounted = owner.startRiding(mount, true);
        c.assertTrue(ServerNetworkHelper.canControl(owner, mount), "Owner controller rejected: mounted=" + mounted
                + ", owner=" + mount.isOwner(owner) + ", removed=" + mount.isRemoved()
                + ", passenger=" + mount.getControllingPassenger() + ", vehicle=" + owner.getVehicle());
        c.assertFalse(ServerNetworkHelper.canControl(c.createMockSurvivalPlayer(), mount), "Noncontroller accepted");
        owner.stopRiding();
        owner.discard();
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void hydraIndexDefense(TestContext c) {
        EntityHydra hydra = c.spawnMob(IafEntities.HYDRA.get(), POS);
        for (int index : new int[]{Integer.MIN_VALUE, -1, hydra.getHeadCount(), Integer.MAX_VALUE}) {
            hydra.triggerHeadFlags(index);
            hydra.timeUntilRegen = 0;
            hydra.damage(c.getWorld().getDamageSources().generic(), 1);
        }
        c.assertTrue(hydra.isAlive(), "Hydra died during small index-defense hits");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void searchCooldownEveryOtherTick(TestContext c) {
        for (int phase = 0; phase < 4; phase++) for (int parity = 0; parity < 2; parity++) {
            SearchCooldown cooldown = new SearchCooldown();
            int searches = 0;
            for (long time = parity; time < 40; time += 2) if (cooldown.shouldSearch(time, phase)) searches++;
            c.assertTrue(searches >= 9 && searches <= 10, "Search deadline starved or ran too often");
        }
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 40)
    public void itemGoalFindsFoodAfterSpawn(TestContext c) {
        EntityCockatrice bird = c.spawnMob(IafEntities.COCKATRICE.get(), POS);
        bird.setAiDisabled(true);
        bird.setHealth(bird.getMaxHealth() / 2);
        CockatriceAITargetItems<ItemEntity> goal = new CockatriceAITargetItems<>(bird, 0, false, false, null);
        goal.canStart();
        c.runAtTick(4, () -> c.spawnItem(Items.ROTTEN_FLESH, POS));
        c.runAtEveryTick(() -> {
            if (c.getTick() >= 6 && c.getTick() % 2 == 1 && goal.canStart()) c.complete();
        });
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void seaSerpentReusesPartsAndSaveIsPure(TestContext c) throws Exception {
        EntitySeaSerpent serpent = c.spawnMob(IafEntities.SEA_SERPENT.get(), POS);
        Field field = EntitySeaSerpent.class.getDeclaredField("segments");
        field.setAccessible(true);
        EntitySlowPart[] before = ((EntitySlowPart[]) field.get(serpent)).clone();
        c.assertTrue(Arrays.stream(before).allMatch(e -> e != null && !e.isRemoved()), "Missing initial parts");
        for (int i = 0; i < 10; i++) serpent.updateScale(1 + i / 10f);
        serpent.writeCustomDataToNbt(new NbtCompound());
        EntitySlowPart[] after = (EntitySlowPart[]) field.get(serpent);
        c.assertTrue(Arrays.equals(before, after) && Arrays.stream(after).noneMatch(e -> e.isRemoved()), "Scale/save replaced live parts");
        serpent.onUpdateParts();
        long count = c.getWorld().getEntitiesByClass(EntitySlowPart.class, new Box(serpent.getBlockPos()).expand(20),
                e -> e.getParent() == serpent).size();
        c.assertTrue(count == 9, "Orphan or duplicate serpent parts: " + count);
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void flockReelectsLiveLeader(TestContext c) {
        EntityStymphalianBird first = c.spawnMob(IafEntities.STYMPHALIAN_BIRD.get(), POS);
        EntityStymphalianBird second = c.spawnMob(IafEntities.STYMPHALIAN_BIRD.get(), POS.east());
        var flock = StymphalianBirdFlock.createFlock(first);
        first.flock = second.flock = flock;
        flock.addToFlock(second);
        flock.addToFlock(second);
        first.discard();
        flock.update();
        c.assertTrue(flock.getLeader() == second, "Dead leader retained");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void miscTargetsSerializeAndClear(TestContext c) {
        var target = c.spawnMob(EntityType.PIG, POS);
        MiscData data = new MiscData();
        data.addScepterTarget(target);
        data.tickMisc(target);
        c.assertTrue(data.getTargetedByScepter().contains(target), "First tick erased fresh server targets");
        NbtCompound tag = new NbtCompound();
        data.serialize(tag);
        c.assertTrue(tag.getCompound("miscData").getIntArray("targetedByScepterIds")[0] == target.getId(), "Target IDs in wrong NBT layer");
        data.isDirty();
        data.clearScepterTargets();
        c.assertTrue(data.isDirty() && data.getTargetedByScepter().isEmpty(), "Target clear did not synchronize");
        data.serialize(tag);
        c.assertTrue(tag.getCompound("miscData").getIntArray("targetedByScepterIds").length == 0, "Empty target update retained IDs");
        data.deserialize(tag);
        data.tickMisc(target);
        c.assertTrue(data.getTargetedByScepter().isEmpty(), "Deserialize retained old targets");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void ghostSwordOffhandOnlyOnce(TestContext c) {
        PlayerEntity player = c.createMockSurvivalPlayer();
        player.setPosition(c.getAbsolute(Vec3d.ofCenter(POS)));
        ItemStack sword = new ItemStack(IafItems.GHOST_SWORD.get());
        player.setStackInHand(Hand.OFF_HAND, sword);
        ItemGhostSword.spawnGhostSwordEntity(sword, player, Hand.OFF_HAND);
        ItemGhostSword.spawnGhostSwordEntity(sword, player, Hand.OFF_HAND);
        c.assertTrue(sword.getDamage() == 1, "Offhand sword did not lose exactly one durability");
        c.assertTrue(c.getWorld().getEntitiesByClass(EntityGhostSword.class, player.getBoundingBox().expand(3), e -> true).size() == 1,
                "Offhand swing spawned duplicate/missing projectile");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void portalSyncOnlyOnChange(TestContext c) {
        PlayerEntity player = c.createMockSurvivalPlayer();
        player.setPosition(c.getAbsolute(Vec3d.ofCenter(POS)));
        PortalData data = new PortalData(player);
        for (int i = 0; i < 50; i++) data.tick();
        c.assertFalse(data.consumeDirty(), "Idle portal data marked dirty");
        data.setTeleportTick(10);
        c.assertTrue(data.consumeDirty() && !data.consumeDirty(), "Portal change not consumed exactly once");
        data.setTeleportTick(10);
        c.assertFalse(data.consumeDirty(), "Identical portal assignment marked dirty");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void crystalValidatesOwnerAndPreservesOtherNbt(TestContext c) {
        var owner = c.createMockCreativeServerPlayerInWorld();
        owner.setPosition(c.getAbsolute(Vec3d.ofCenter(POS)));
        EntityFireDragon dragon = c.spawnMob(IafEntities.FIRE_DRAGON.get(), new BlockPos(1, 2, 1));
        dragon.setTamed(true);
        dragon.setOwner(owner);
        dragon.setCrystalBound(true);
        ItemStack crystal = new ItemStack(IafItems.SUMMONING_CRYSTAL_FIRE.get());
        NbtCompound binding = new NbtCompound();
        binding.putUuid("DragonUUID", dragon.getUuid());
        crystal.getOrCreateNbt().put("Dragon", binding);
        crystal.getOrCreateNbt().putString("Keep", "custom data");
        owner.setStackInHand(Hand.MAIN_HAND, crystal);
        var stranger = c.createMockCreativeServerPlayerInWorld();
        stranger.setStackInHand(Hand.MAIN_HAND, crystal.copy());
        BlockPos destination = c.getAbsolutePos(POS);
        Vec3d old = dragon.getPos();
        SummoningCrystalManager.request(stranger, Hand.MAIN_HAND, destination);
        c.assertTrue(dragon.getPos().equals(old) && dragon.isBoundToCrystal(), "Wrong owner summoned dragon");
        SummoningCrystalManager.request(owner, Hand.MAIN_HAND, destination);
        c.assertTrue(dragon.getPos().squaredDistanceTo(Vec3d.ofCenter(destination)) < 0.001 && !dragon.isBoundToCrystal(),
                "Owner summon failed or left bound entity");
        c.assertTrue(!crystal.getNbt().contains("Dragon") && crystal.getNbt().getString("Keep").equals("custom data"),
                "Summon erased unrelated item NBT");
        c.assertTrue(DragonPosWorldData.get(c.getWorld()).getDragonPos(dragon.getUuid()) == null, "Successful summon kept stale position");
        owner.discard();
        stranger.discard();
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void crystalMalformedUuidAndPositionDirtiness(TestContext c) {
        var player = c.createMockCreativeServerPlayerInWorld();
        ItemStack crystal = new ItemStack(IafItems.SUMMONING_CRYSTAL_FIRE.get());
        NbtCompound binding = new NbtCompound();
        binding.putString("DragonUUID", "invalid");
        crystal.getOrCreateNbt().put("Dragon", binding);
        player.setStackInHand(Hand.MAIN_HAND, crystal);
        SummoningCrystalManager.request(player, Hand.MAIN_HAND, c.getAbsolutePos(POS));
        c.assertTrue(crystal.getNbt().contains("Dragon"), "Failed summon cleared binding");
        DragonPosWorldData data = new DragonPosWorldData();
        UUID id = UUID.randomUUID();
        data.addDragon(id, POS);
        data.setDirty(false);
        data.addDragon(id, POS);
        c.assertFalse(data.isDirty(), "Unchanged dragon position dirtied persistent state");
        data.removeDragon(id);
        c.assertTrue(data.isDirty(), "Position removal not persisted");
        player.discard();
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void crystalCrossDimensionClearsReturnedEntity(TestContext c) {
        var owner = c.createMockCreativeServerPlayerInWorld();
        var source = c.getWorld().getServer().getWorld(World.NETHER);
        source.setChunkForced(0, 0, true);
        source.getChunk(0, 0);
        EntityFireDragon dragon = IafEntities.FIRE_DRAGON.get().create(source);
        dragon.setPosition(0.5, 80, 0.5);
        source.spawnEntity(dragon);
        dragon.setTamed(true);
        dragon.setOwner(owner);
        dragon.setCrystalBound(true);
        UUID id = dragon.getUuid();
        ItemStack crystal = new ItemStack(IafItems.SUMMONING_CRYSTAL_FIRE.get());
        NbtCompound binding = new NbtCompound();
        binding.putUuid("DragonUUID", id);
        crystal.getOrCreateNbt().put("Dragon", binding);
        owner.setStackInHand(Hand.MAIN_HAND, crystal);
        BlockPos destination = c.getAbsolutePos(POS);
        c.runAtEveryTick(() -> {
            if (source.getEntity(id) != dragon) return;
            SummoningCrystalManager.request(owner, Hand.MAIN_HAND, destination);
            var moved = c.getWorld().getEntity(id);
            c.assertTrue(moved instanceof EntityDragonBase && moved != dragon && dragon.isRemoved(), "Cross-dimension move failed");
            c.assertFalse(((EntityDragonBase) moved).isBoundToCrystal(), "Returned dragon clone kept crystal binding");
            c.assertTrue(moved.getPos().squaredDistanceTo(Vec3d.ofCenter(destination)) < 0.001, "Wrong cross-dimension destination");
            c.assertTrue(DragonPosWorldData.get(source).getDragonPos(id) == null, "Source dimension retained stale position");
            source.setChunkForced(0, 0, false);
            owner.discard();
            c.complete();
        });
        c.runAtTick(95, () -> source.setChunkForced(0, 0, false));
    }

    @SuppressWarnings("unchecked")
    private static java.util.Map<UUID, ?> requests() throws Exception {
        Field field = SummoningCrystalManager.class.getDeclaredField("PENDING");
        field.setAccessible(true);
        return (java.util.Map<UUID, ?>) field.get(null);
    }

    private static ItemStack missingDragonCrystal(UUID id) {
        ItemStack crystal = new ItemStack(IafItems.SUMMONING_CRYSTAL_FIRE.get());
        NbtCompound binding = new NbtCompound();
        binding.putUuid("DragonUUID", id);
        crystal.getOrCreateNbt().put("Dragon", binding);
        return crystal;
    }

    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 120)
    public void crystalTimeoutReleasesTickets(TestContext c) throws Exception {
        var owner = c.createMockCreativeServerPlayerInWorld();
        UUID id = UUID.randomUUID();
        BlockPos stored = c.getAbsolutePos(POS).add(80, 0, 0);
        DragonPosWorldData.get(c.getWorld()).addDragon(id, stored);
        ItemStack crystal = missingDragonCrystal(id);
        owner.setStackInHand(Hand.MAIN_HAND, crystal);
        SummoningCrystalManager.request(owner, Hand.MAIN_HAND, c.getAbsolutePos(POS));
        Object request = requests().get(owner.getUuid());
        c.assertTrue(request != null, "Missing dragon did not queue a chunk load");
        Field tickets = request.getClass().getDeclaredField("tickets");
        tickets.setAccessible(true);
        var heldTickets = (java.util.List<?>) tickets.get(request);
        c.assertFalse(heldTickets.isEmpty(), "No temporary ticket acquired");
        c.runAtTick(105, () -> {
            c.assertTrue(heldTickets.isEmpty(), "Timeout leaked tickets");
            c.assertTrue(crystal.getNbt().contains("Dragon"), "Timeout consumed crystal binding");
            DragonPosWorldData.get(c.getWorld()).removeDragon(id);
            owner.discard();
            c.complete();
        });
    }

    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 20)
    public void crystalChangingStackCancelsTickets(TestContext c) throws Exception {
        var owner = c.createMockCreativeServerPlayerInWorld();
        UUID id = UUID.randomUUID();
        DragonPosWorldData.get(c.getWorld()).addDragon(id, c.getAbsolutePos(POS).add(-80, 0, 0));
        ItemStack crystal = missingDragonCrystal(id);
        owner.setStackInHand(Hand.MAIN_HAND, crystal);
        SummoningCrystalManager.request(owner, Hand.MAIN_HAND, c.getAbsolutePos(POS));
        Object request = requests().get(owner.getUuid());
        c.assertTrue(request != null, "Request not queued");
        Field tickets = request.getClass().getDeclaredField("tickets");
        tickets.setAccessible(true);
        var heldTickets = (java.util.List<?>) tickets.get(request);
        owner.setStackInHand(Hand.MAIN_HAND, crystal.copy());
        c.runAtTick(3, () -> {
            c.assertTrue(heldTickets.isEmpty(), "Stack replacement failed to release tickets");
            DragonPosWorldData.get(c.getWorld()).removeDragon(id);
            owner.discard();
            c.complete();
        });
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void scepterDamagesOnlyActiveStackAndClears(TestContext c) throws Exception {
        PlayerEntity player = c.createMockSurvivalPlayer();
        player.setPosition(c.getAbsolute(Vec3d.ofCenter(POS)));
        player.setYaw(0);
        player.setPitch(0);
        var target = c.spawnMob(EntityType.PIG, POS.south(3));
        target.setPosition(target.getX(), player.getEyeY() - target.getStandingEyeHeight(), target.getZ());
        ItemCockatriceScepter item = (ItemCockatriceScepter) IafItems.ITEM_COCKATRICE_SCEPTER.get();
        ItemStack active = new ItemStack(item);
        ItemStack other = new ItemStack(item);
        player.setStackInHand(Hand.MAIN_HAND, active);
        player.setStackInHand(Hand.OFF_HAND, other);
        player.setCurrentHand(Hand.MAIN_HAND);
        player.age = 20;
        var data = com.iafenvoy.iceandfire.data.component.IafEntityData.get(player).miscData;
        data.addScepterTarget(target);
        var attack = ItemCockatriceScepter.class.getDeclaredMethod("attackTargets", net.minecraft.entity.LivingEntity.class);
        attack.setAccessible(true);
        attack.invoke(item, player);
        c.assertTrue(active.getDamage() == 1 && other.getDamage() == 0, "Scepter durability crossed item stacks");
        item.onStoppedUsing(active, c.getWorld(), player, 10);
        c.assertTrue(data.getTargetedByScepter().isEmpty(), "Stopped scepter retained targets");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void forgeInputRevalidatesCore(TestContext c) {
        c.setBlockState(POS, IafBlocks.DRAGONFORGE_FIRE_INPUT.get());
        c.setBlockState(POS.east(), IafBlocks.DRAGONFORGE_FIRE_CORE_DISABLED.get());
        BlockEntityDragonForgeInput input = (BlockEntityDragonForgeInput) c.getBlockEntity(POS);
        BlockEntityDragonForge old = (BlockEntityDragonForge) c.getBlockEntity(POS.east());
        input.onHitWithFlame();
        c.assertTrue(old.lastDragonFlameTimer == 40, "Valid fire core was not powered");
        old.lastDragonFlameTimer = 0;
        c.setBlockState(POS.east(), IafBlocks.DRAGONFORGE_ICE_CORE_DISABLED.get());
        BlockEntityDragonForge wrongType = (BlockEntityDragonForge) c.getBlockEntity(POS.east());
        input.onHitWithFlame();
        c.assertTrue(old.lastDragonFlameTimer == 0 && wrongType.lastDragonFlameTimer == 0, "Stale/wrong-type core powered");
        c.setBlockState(POS.east(), IafBlocks.DRAGONFORGE_FIRE_CORE_DISABLED.get());
        BlockEntityDragonForge replacement = (BlockEntityDragonForge) c.getBlockEntity(POS.east());
        input.onHitWithFlame();
        c.assertTrue(replacement.lastDragonFlameTimer == 40, "Replacement core was not reacquired");
        c.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void hydraSaveKeepsParts(TestContext c) throws Exception {
        EntityHydra hydra = c.spawnMob(IafEntities.HYDRA.get(), POS);
        hydra.resetParts();
        Field field = EntityHydra.class.getDeclaredField("headBoxes");
        field.setAccessible(true);
        EntityHydraHead[] before = ((EntityHydraHead[]) field.get(hydra)).clone();
        hydra.writeCustomDataToNbt(new NbtCompound());
        c.assertTrue(Arrays.stream(before).filter(e -> e != null).noneMatch(e -> e.isRemoved()), "Saving hydra removed heads");
        c.complete();
    }

    private static class HitCountingPig extends net.minecraft.entity.passive.PigEntity {
        int hits;
        HitCountingPig(World world) { super(EntityType.PIG, world); }
        @Override public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
            this.hits++;
            return super.damage(source, amount);
        }
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void vanillaMultipartAttackHitsParentOnce(TestContext c) {
        var player = c.createMockCreativeServerPlayerInWorld();
        HitCountingPig parent = new HitCountingPig(c.getWorld());
        parent.setPosition(c.getAbsolute(Vec3d.ofCenter(POS)));
        c.getWorld().spawnEntity(parent);
        EntitySlowPart part = new EntitySlowPart(parent, 0, 0, 0, 1, 1, 1);
        part.setPosition(parent.getPos());
        c.getWorld().spawnEntity(part);
        player.setPosition(parent.getPos().add(0, 0, 2));
        player.networkHandler.onPlayerInteractEntity(net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.attack(part, false));
        c.assertTrue(parent.hits == 1, "Vanilla multipart packet hit parent " + parent.hits + " times");
        player.discard();
        c.complete();
    }
}

