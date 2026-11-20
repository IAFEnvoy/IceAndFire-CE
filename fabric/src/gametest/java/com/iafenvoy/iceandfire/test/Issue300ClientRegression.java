package com.iafenvoy.iceandfire.test;

import com.iafenvoy.iceandfire.entity.EntityDeathWorm;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Drives real LocalPlayer key mappings and checks the integrated server's vehicle position. */
public final class Issue300ClientRegression implements ClientModInitializer {
    private static final int FORWARD = 1, BACKWARD = 2, LEFT = 4, RIGHT = 8, JUMP = 16;
    private record Scenario(String name, float yaw, float pitch, int keys, float heading, double speedScale) {}
    private static final List<Scenario> SCENARIOS = List.of(
            new Scenario("forward", 0, 0, FORWARD, 0, 1),
            new Scenario("backward", 0, 0, BACKWARD, 180, 0.15),
            new Scenario("left", 0, 0, LEFT, -90, 0.25),
            new Scenario("right", 0, 0, RIGHT, 90, 0.25),
            new Scenario("turn", 90, 0, FORWARD, 90, 1),
            new Scenario("forward_left", 0, 0, FORWARD | LEFT, -90, 0.25),
            new Scenario("forward_right", 0, 0, FORWARD | RIGHT, 90, 0.25),
            new Scenario("backward_left", 0, 0, BACKWARD | LEFT, 180, 0.15 * 0.25),
            new Scenario("backward_right", 0, 0, BACKWARD | RIGHT, 180, 0.15 * 0.25),
            new Scenario("looking_up", 0, -90, FORWARD, Float.NaN, 1),
            new Scenario("looking_down", 0, 90, FORWARD, Float.NaN, 1),
            new Scenario("idle", 45, 0, 0, Float.NaN, 1),
            new Scenario("jump", 0, 0, JUMP, Float.NaN, 1),
            new Scenario("sand_forward", 0, 0, FORWARD, 0, 1));
    private static final List<String> passed = new ArrayList<>();
    private static boolean opening;
    private static boolean finished;
    private static int ticks;
    private static int worldTicks;
    private static int wormId;
    private static int scenario;
    private static int phase;
    private static int phaseTicks;
    private static Vec3d start;
    private static double highestY;
    private static float startYaw;
    private static float previousYaw;
    private static CompletableFuture<Integer> setup;
    private static CompletableFuture<?> terrain;
    private static CompletableFuture<Vec3d> serverPosition;
    private static CompletableFuture<Boolean> dismounted;

    @Override
    public void onInitializeClient() {
        if (!Boolean.getBoolean("iceandfire.issue300Regression")) return;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try { tick(); }
            catch (Exception failure) { throw new IllegalStateException(failure); }
        });
    }

    private static void tick() throws Exception {
        if (!Boolean.getBoolean("iceandfire.issue300Regression") || finished) return;
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            check(++ticks < 2400, "Timed out waiting for death worm riding regression");
            if (client.getOverlay() != null) return;
            if (!opening) {
                if (client.currentScreen == null) return;
                opening = true;
                client.options.pauseOnLostFocus = false;
                Files.deleteIfExists(client.runDirectory.toPath().resolve("issue300-client-result.json"));
                client.createIntegratedServerLoader().createAndStart("issue300-" + System.currentTimeMillis(),
                        new LevelInfo("Issue 300 regression", GameMode.CREATIVE,
                                false, Difficulty.NORMAL, true, new GameRules(), DataConfiguration.SAFE_MODE),
                        new GeneratorOptions(300, false, false),
                        registries -> registries.get(RegistryKeys.WORLD_PRESET).getOrThrow(WorldPresets.FLAT)
                                .createDimensionsRegistryHolder());
                return;
            }
            if (client.world == null || client.player == null) return;
            if (client.currentScreen != null) client.setScreen(null);
            if (++worldTicks < 30) return;
            var server = client.getServer();
            check(server != null, "Integrated server missing");
            if (setup == null) {
                UUID playerId = client.player.getUuid();
                setup = server.submit(() -> {
                    var player = server.getPlayerManager().getPlayer(playerId);
                    check(player != null, "Server player missing");
                    var level = player.getServerWorld();
                    var worm = IafEntities.DEATH_WORM.get().create(level);
                    check(worm != null, "Could not create death worm");
                    worm.setWormAge(5);
                    worm.setDeathWormScale(1);
                    worm.setInvulnerable(true);
                    worm.setPersistent();
                    worm.setPosition(player.getX() + 2, player.getY(), player.getZ());
                    level.spawnEntity(worm);
                    player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.FISHING_ROD));
                    player.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.FISHING_ROD));
                    worm.interactMob(player, Hand.MAIN_HAND);
                    check(player.getVehicle() == worm, "Double fishing rods did not mount the server worm");
                    return worm.getId();
                });
                return;
            }
            if (!setup.isDone()) return;
            wormId = setup.join();
            if (!(client.world.getEntityById(wormId) instanceof EntityDeathWorm worm)) return;
            if (phase == 0) {
                if (client.player.getVehicle() != worm) return;
                releaseKeys(client);
                phase = 1;
            }
            if (scenario == SCENARIOS.size()) {
                releaseKeys(client);
                client.options.sneakKey.setPressed(true);
                if (++phaseTicks < 10) return;
                check(client.player.getVehicle() == null, "Shift did not dismount the client player");
                if (dismounted == null) {
                    UUID playerId = client.player.getUuid();
                    dismounted = server.submit(() -> server.getPlayerManager().getPlayer(playerId).getVehicle() == null);
                    return;
                }
                if (!dismounted.isDone()) return;
                check(dismounted.join(), "Server still considers the player mounted");
                passed.add("dismount");
                finish(client, null);
                return;
            }
            check(client.player.getVehicle() == worm, "Lost the worm during " + SCENARIOS.get(scenario).name());
            var sample = SCENARIOS.get(scenario);
            client.player.setYaw(sample.yaw());
            client.player.setPitch(sample.pitch());
            if (phase == 1) {
                releaseKeys(client);
                if (sample.name().equals("sand_forward")) {
                    if (terrain == null) {
                        terrain = server.submit(() -> {
                            var serverWorm = server.getOverworld().getEntityById(wormId);
                            check(serverWorm != null, "Server worm disappeared");
                            BlockPos center = serverWorm.getBlockPos().down();
                            for (int x = -16; x <= 16; x++)
                                for (int z = -16; z <= 16; z++)
                                    server.getOverworld().setBlockState(center.add(x, 0, z), Blocks.SAND.getDefaultState());
                        });
                        return;
                    }
                    if (!terrain.isDone()) return;
                    terrain.join();
                }
                if (++phaseTicks < 15 || !worm.isOnGround()) return;
                start = worm.getPos();
                highestY = worm.getY();
                startYaw = previousYaw = worm.getYaw();
                phaseTicks = 0;
                phase = 2;
            }
            if (phase == 2) {
                pressKeys(client, sample.keys());
                check(Math.abs(MathHelper.wrapDegrees(worm.getYaw() - previousYaw)) <= 90.01F,
                        "Steering exceeded the original turn limit during " + sample.name());
                previousYaw = worm.getYaw();
                highestY = Math.max(highestY, worm.getY());
                if (++phaseTicks < 25) return;
                Vec3d delta = worm.getPos().subtract(start);
                switch (sample.name()) {
                    case "forward", "sand_forward" -> check(delta.z > 0.5, sample.name() + " did not move forward: " + delta);
                    case "backward" -> check(delta.z < -0.01, "Backward input did not move the worm: " + delta);
                    case "left", "forward_left" -> {
                        check(delta.x > 0.05, sample.name() + " did not turn and walk left: " + delta);
                        check(Math.abs(delta.z) < 0.1, sample.name() + " unexpectedly combined axes: " + delta);
                    }
                    case "right", "forward_right", "turn" -> {
                        check(delta.x < -0.05, sample.name() + " did not move in the expected direction: " + delta);
                        check(Math.abs(delta.z) < 0.1, sample.name() + " unexpectedly combined axes: " + delta);
                    }
                    case "backward_left", "backward_right" -> check(Math.abs(delta.x) < 0.1 && delta.z <= 0,
                            "Backward input did not take priority over sideways input: " + delta);
                    case "idle", "looking_up", "looking_down" -> check(delta.horizontalLength() < 0.1,
                            sample.name() + " received unwanted movement: " + delta);
                    case "jump" -> check(highestY > start.y + 0.25, "Jump key did not lift the worm");
                }
                float expectedYaw = Float.isNaN(sample.heading()) ? startYaw : sample.heading();
                check(Math.abs(MathHelper.wrapDegrees(worm.getYaw() - expectedYaw)) < 1,
                        "Worm did not retain the original steering heading during " + sample.name() + ": " + worm.getYaw());
                check(Math.abs(worm.getMoveControl().getSpeed() - 1.8F * sample.speedScale()) < 0.00001,
                        "Original riding speed multiplier changed during " + sample.name());
                if (!Float.isNaN(sample.heading()))
                    check(Math.abs(worm.getMovementSpeed() - worm.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                            * 1.8F * sample.speedScale()) < 0.00001,
                            "Move controller did not supply the original walking speed during " + sample.name());
                System.out.println("IAF ISSUE 300 CLIENT: " + sample.name() + " displacement=" + delta
                        + " yaw=" + worm.getYaw() + " speed=" + worm.getMovementSpeed());
                releaseKeys(client);
                phase = 3;
                phaseTicks = 0;
            }
            if (phase == 3) {
                releaseKeys(client);
                if (++phaseTicks < 15) return;
                if (serverPosition == null) {
                    serverPosition = server.submit(() -> server.getOverworld().getEntityById(wormId).getPos());
                    return;
                }
                if (!serverPosition.isDone()) return;
                check(serverPosition.join().distanceTo(worm.getPos()) < 1,
                        "Server position did not follow client riding in " + sample.name());
                passed.add(sample.name());
                System.out.println("IAF ISSUE 300 CLIENT: passed " + sample.name());
                scenario++;
                phase = 1;
                phaseTicks = 0;
                serverPosition = null;
            }
        } catch (Throwable failure) {
            finish(client, failure);
        }
    }

    private static void pressKeys(MinecraftClient client, int keys) {
        releaseKeys(client);
        client.options.forwardKey.setPressed((keys & FORWARD) != 0);
        client.options.backKey.setPressed((keys & BACKWARD) != 0);
        client.options.leftKey.setPressed((keys & LEFT) != 0);
        client.options.rightKey.setPressed((keys & RIGHT) != 0);
        client.options.jumpKey.setPressed((keys & JUMP) != 0);
    }

    private static void releaseKeys(MinecraftClient client) {
        client.options.forwardKey.setPressed(false);
        client.options.backKey.setPressed(false);
        client.options.leftKey.setPressed(false);
        client.options.rightKey.setPressed(false);
        client.options.jumpKey.setPressed(false);
        client.options.sneakKey.setPressed(false);
    }

    private static void finish(MinecraftClient client, Throwable failure) throws Exception {
        finished = true;
        releaseKeys(client);
        Files.writeString(client.runDirectory.toPath().resolve("issue300-client-result.json"),
                "{\"passed\":" + passed.size() + ",\"failed\":" + (failure == null ? 0 : 1)
                        + ",\"scenarios\":\"" + String.join(",", passed) + "\",\"failure\":\""
                        + (failure == null ? "" : failure.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ")) + "\"}");
        if (failure == null) System.out.println("IAF ISSUE 300 CLIENT: all " + passed.size() + " riding scenarios passed");
        else failure.printStackTrace();
        client.scheduleStop();
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
