package com.iafenvoy.iceandfire.regression;

import com.iafenvoy.iceandfire.entity.DeathWormEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Drives real LocalPlayer key mappings and checks the integrated server's vehicle position. */
@EventBusSubscriber(modid = "iceandfire", value = Dist.CLIENT)
public final class Issue300ClientRegression {
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
    private static Vec3 start;
    private static double highestY;
    private static float startYaw;
    private static float previousYaw;
    private static CompletableFuture<Integer> setup;
    private static CompletableFuture<?> terrain;
    private static CompletableFuture<Vec3> serverPosition;
    private static CompletableFuture<Boolean> dismounted;

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) throws Exception {
        if (!Boolean.getBoolean("iceandfire.issue300Regression") || finished) return;
        Minecraft client = Minecraft.getInstance();
        try {
            check(++ticks < 2400, "Timed out waiting for death worm riding regression");
            if (client.getOverlay() != null) return;
            if (!opening) {
                if (client.screen == null) return;
                opening = true;
                client.options.pauseOnLostFocus = false;
                Files.deleteIfExists(client.gameDirectory.toPath().resolve("issue300-client-result.json"));
                client.createWorldOpenFlows().createFreshLevel("issue300-" + System.currentTimeMillis(),
                        new LevelSettings("Issue 300 regression", GameType.CREATIVE,
                                LevelSettings.DifficultySettings.DEFAULT, true, WorldDataConfiguration.DEFAULT),
                        new WorldOptions(300, false, false),
                        registries -> registries.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT)
                                .value().createWorldDimensions(), client.screen);
                return;
            }
            if (client.level == null || client.player == null) return;
            if (client.screen != null) client.setScreen(null);
            if (++worldTicks < 30) return;
            var server = client.getSingleplayerServer();
            check(server != null, "Integrated server missing");
            if (setup == null) {
                UUID playerId = client.player.getUUID();
                setup = server.submit(() -> {
                    var player = server.getPlayerList().getPlayer(playerId);
                    check(player != null, "Server player missing");
                    var level = player.level();
                    var worm = IafEntities.DEATH_WORM.get().create(level, EntitySpawnReason.COMMAND);
                    check(worm != null, "Could not create death worm");
                    worm.setWormAge(5);
                    worm.setDeathWormScale(1);
                    worm.setInvulnerable(true);
                    worm.setPersistenceRequired();
                    worm.setPos(player.getX() + 2, player.getY(), player.getZ());
                    level.addFreshEntity(worm);
                    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.FISHING_ROD));
                    player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.FISHING_ROD));
                    worm.mobInteract(player, InteractionHand.MAIN_HAND);
                    check(player.getVehicle() == worm, "Double fishing rods did not mount the server worm");
                    return worm.getId();
                });
                return;
            }
            if (!setup.isDone()) return;
            wormId = setup.join();
            if (!(client.level.getEntity(wormId) instanceof DeathWormEntity worm)) return;
            if (phase == 0) {
                if (client.player.getVehicle() != worm) return;
                releaseKeys(client);
                phase = 1;
            }
            if (scenario == SCENARIOS.size()) {
                releaseKeys(client);
                client.options.keyShift.setDown(true);
                if (++phaseTicks < 10) return;
                check(client.player.getVehicle() == null, "Shift did not dismount the client player");
                if (dismounted == null) {
                    UUID playerId = client.player.getUUID();
                    dismounted = server.submit(() -> server.getPlayerList().getPlayer(playerId).getVehicle() == null);
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
            client.player.setYRot(sample.yaw());
            client.player.setXRot(sample.pitch());
            if (phase == 1) {
                releaseKeys(client);
                if (sample.name().equals("sand_forward")) {
                    if (terrain == null) {
                        terrain = server.submit(() -> {
                            var serverWorm = server.overworld().getEntity(wormId);
                            check(serverWorm != null, "Server worm disappeared");
                            BlockPos center = serverWorm.blockPosition().below();
                            for (int x = -16; x <= 16; x++)
                                for (int z = -16; z <= 16; z++)
                                    server.overworld().setBlockAndUpdate(center.offset(x, 0, z), Blocks.SAND.defaultBlockState());
                        });
                        return;
                    }
                    if (!terrain.isDone()) return;
                    terrain.join();
                }
                if (++phaseTicks < 15 || !worm.onGround()) return;
                start = worm.position();
                highestY = worm.getY();
                startYaw = previousYaw = worm.getYRot();
                phaseTicks = 0;
                phase = 2;
            }
            if (phase == 2) {
                pressKeys(client, sample.keys());
                check(Math.abs(Mth.wrapDegrees(worm.getYRot() - previousYaw)) <= 90.01F,
                        "Steering exceeded the original turn limit during " + sample.name());
                previousYaw = worm.getYRot();
                highestY = Math.max(highestY, worm.getY());
                if (++phaseTicks < 25) return;
                Vec3 delta = worm.position().subtract(start);
                switch (sample.name()) {
                    case "forward", "sand_forward" -> check(delta.z > 0.5, sample.name() + " did not move forward: " + delta);
                    case "backward" -> check(delta.z < -0.01, "Backward input did not move the worm: " + delta);
                    case "left", "forward_left" -> {
                        check(delta.x > 0.1, sample.name() + " did not turn and walk left: " + delta);
                        check(Math.abs(delta.z) < 0.1, sample.name() + " unexpectedly combined axes: " + delta);
                    }
                    case "right", "forward_right", "turn" -> {
                        check(delta.x < -0.1, sample.name() + " did not move in the expected direction: " + delta);
                        check(Math.abs(delta.z) < 0.1, sample.name() + " unexpectedly combined axes: " + delta);
                    }
                    case "backward_left", "backward_right" -> check(Math.abs(delta.x) < 0.1 && delta.z <= 0,
                            "Backward input did not take priority over sideways input: " + delta);
                    case "idle", "looking_up", "looking_down" -> check(delta.horizontalDistance() < 0.1,
                            sample.name() + " received unwanted movement: " + delta);
                    case "jump" -> check(highestY > start.y + 0.25, "Jump key did not lift the worm");
                }
                float expectedYaw = Float.isNaN(sample.heading()) ? startYaw : sample.heading();
                check(Math.abs(Mth.wrapDegrees(worm.getYRot() - expectedYaw)) < 1,
                        "Worm did not retain the original steering heading during " + sample.name() + ": " + worm.getYRot());
                check(Math.abs(worm.getMoveControl().getSpeedModifier() - 1.8F * sample.speedScale()) < 0.00001,
                        "Original riding speed multiplier changed during " + sample.name());
                if (!Float.isNaN(sample.heading()))
                    check(Math.abs(worm.getSpeed() - worm.getAttributeValue(Attributes.MOVEMENT_SPEED)
                            * 1.8F * sample.speedScale()) < 0.00001,
                            "Move controller did not supply the original walking speed during " + sample.name());
                System.out.println("IAF ISSUE 300 CLIENT: " + sample.name() + " displacement=" + delta
                        + " yaw=" + worm.getYRot() + " speed=" + worm.getSpeed());
                releaseKeys(client);
                phase = 3;
                phaseTicks = 0;
            }
            if (phase == 3) {
                releaseKeys(client);
                if (++phaseTicks < 15) return;
                if (serverPosition == null) {
                    serverPosition = server.submit(() -> server.overworld().getEntity(wormId).position());
                    return;
                }
                if (!serverPosition.isDone()) return;
                check(serverPosition.join().distanceTo(worm.position()) < 1,
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

    private static void pressKeys(Minecraft client, int keys) {
        releaseKeys(client);
        client.options.keyUp.setDown((keys & FORWARD) != 0);
        client.options.keyDown.setDown((keys & BACKWARD) != 0);
        client.options.keyLeft.setDown((keys & LEFT) != 0);
        client.options.keyRight.setDown((keys & RIGHT) != 0);
        client.options.keyJump.setDown((keys & JUMP) != 0);
    }

    private static void releaseKeys(Minecraft client) {
        client.options.keyUp.setDown(false);
        client.options.keyDown.setDown(false);
        client.options.keyLeft.setDown(false);
        client.options.keyRight.setDown(false);
        client.options.keyJump.setDown(false);
        client.options.keyShift.setDown(false);
    }

    private static void finish(Minecraft client, Throwable failure) throws Exception {
        finished = true;
        releaseKeys(client);
        Files.writeString(client.gameDirectory.toPath().resolve("issue300-client-result.json"),
                "{\"passed\":" + passed.size() + ",\"failed\":" + (failure == null ? 0 : 1)
                        + ",\"scenarios\":\"" + String.join(",", passed) + "\",\"failure\":\""
                        + (failure == null ? "" : failure.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ")) + "\"}");
        if (failure == null) System.out.println("IAF ISSUE 300 CLIENT: all " + passed.size() + " riding scenarios passed");
        else failure.printStackTrace();
        client.stop();
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
