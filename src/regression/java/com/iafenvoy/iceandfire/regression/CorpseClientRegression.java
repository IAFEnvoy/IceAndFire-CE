package com.iafenvoy.iceandfire.regression;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Uses the real crosshair, use key and network packets, then checks server-side loot. */
@EventBusSubscriber(modid = "iceandfire", value = Dist.CLIENT)
public final class CorpseClientRegression {
    private static boolean opening, finished;
    private static int ticks, worldTicks, scenario, phase, phaseTicks, dragonId;
    private static net.minecraft.world.item.Item skullItem;
    private static CompletableFuture<Integer> setup;
    private static CompletableFuture<?> verification;
    private static CompletableFuture<?> placement;
    private static net.minecraft.world.entity.Entity aimed;
    private static final String[] TARGETS = {"body", "head", "wing", "tail"};

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) throws Exception {
        if (!Boolean.getBoolean("iceandfire.corpseRegression") || finished) return;
        Minecraft client = Minecraft.getInstance();
        try {
            check(++ticks < 12000, "Corpse interaction timed out: scenario=" + scenario + ", phase=" + phase);
            if (client.getOverlay() != null) return;
            if (!opening) {
                if (client.screen == null) return;
                opening = true;
                client.options.pauseOnLostFocus = false;
                Files.deleteIfExists(client.gameDirectory.toPath().resolve("corpse-client-result.json"));
                client.createWorldOpenFlows().createFreshLevel("corpse-" + System.currentTimeMillis(),
                        new LevelSettings("Corpse regression", GameType.CREATIVE,
                                LevelSettings.DifficultySettings.DEFAULT, true, WorldDataConfiguration.DEFAULT),
                        new WorldOptions(284, false, false),
                        registries -> registries.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT)
                                .value().createWorldDimensions(), client.screen);
                return;
            }
            if (client.level == null || client.player == null) return;
            if (client.screen != null) client.setScreen(null);
            if (++worldTicks < 30) return;
            var server = client.getSingleplayerServer();
            check(server != null, "Integrated server missing");
            if (scenario == 72) {
                finish(client, null);
                return;
            }
            int action = scenario % 6;
            int targetIndex = scenario / 6 % 4;
            UUID playerId = client.player.getUUID();
            if (phase == 0) {
                if (setup == null) {
                    int typeIndex = scenario / 24;
                    setup = server.submit(() -> {
                        var player = server.getPlayerList().getPlayer(playerId);
                        var level = player.level();
                        if (level.getEntity(dragonId) != null) level.getEntity(dragonId).discard();
                        player.setGameMode(action == 5 ? GameType.ADVENTURE : GameType.SURVIVAL);
                        player.setInvulnerable(true);
                        player.getInventory().clearContent();
                        var type = List.of(IafEntities.FIRE_DRAGON.get(), IafEntities.ICE_DRAGON.get(),
                                IafEntities.LIGHTNING_DRAGON.get()).get(typeIndex);
                        DragonBaseEntity dragon = (DragonBaseEntity) type.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
                        check(dragon != null, "Could not spawn dragon");
                        dragon.setAgeInDays(125);
                        dragon.setNoAi(true);
                        dragon.setNoGravity(true);
                        dragon.setPos(0, -59, 0);
                        dragon.setHealth(0);
                        dragon.setModelDead(true);
                        dragon.setDeathStage(action == 3 ? 24 : 0);
                        skullItem = dragon.getSkull();
                        level.addFreshEntity(dragon);
                        var box = dragon.getBoundingBox();
                        player.teleportTo(box.getCenter().x, dragon.getY(), box.minZ - 2);
                        player.setItemInHand(InteractionHand.MAIN_HAND,
                                action == 1 ? new ItemStack(Items.GLASS_BOTTLE)
                                        : action == 4 ? new ItemStack(Items.STICK) : ItemStack.EMPTY);
                        player.setItemInHand(InteractionHand.OFF_HAND,
                                action == 2 ? new ItemStack(Items.GLASS_BOTTLE) : ItemStack.EMPTY);
                        return dragon.getId();
                    });
                    return;
                }
                if (!setup.isDone()) return;
                dragonId = setup.join();
                if (!(client.level.getEntity(dragonId) instanceof DragonBaseEntity dragon)) return;
                check(dragon.isModelDead() && dragon.isAlive() && dragon.isPickable(), "Original corpse interaction state was not restored");
                for (var part : dragon.getParts()) check(part.isPickable(), "Corpse part is not selectable");
                if (++phaseTicks < 30) return;
                if (placement == null) {
                    placement = server.submit(() -> {
                        var sp = server.getPlayerList().getPlayer(playerId);
                        var sd = (DragonBaseEntity) sp.level().getEntity(dragonId);
                        var se = targetIndex == 0 ? sd : sd.getParts()[targetIndex == 1 ? 0 : targetIndex == 2 ? 3 : 9];
                        var center = se.getBoundingBox().getCenter();
                        var out = center.subtract(sd.position()).multiply(1, 0, 1).normalize();
                        if (out.lengthSqr() < 0.1) out = new net.minecraft.world.phys.Vec3(0, 0, -1);
                        double distance = se.getBbWidth() / 2 + 1;
                        sp.setNoGravity(true);
                        sp.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
                        if (targetIndex == 0)
                            sp.teleportTo(center.x, se.getBoundingBox().maxY + 1 - sp.getEyeHeight(), center.z);
                        else sp.teleportTo(center.x + out.x * distance, center.y - sp.getEyeHeight(), center.z + out.z * distance);
                    });
                    return;
                }
                if (!placement.isDone()) return;
                placement.join();
                aimed = targetIndex == 0 ? dragon : dragon.getParts()[targetIndex == 1 ? 0 : targetIndex == 2 ? 3 : 9];
                phase = 1;
                phaseTicks = 0;
            }
            if (phase == 1) {
                var dragon = client.level.getEntity(dragonId);
                check(dragon != null, "Corpse disappeared before interaction");
                var delta = aimed.getBoundingBox().getCenter().subtract(client.player.getEyePosition());
                client.player.setNoGravity(true);
                client.player.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
                client.player.setYRot((float) (Mth.atan2(delta.z, delta.x) * 180 / Math.PI) - 90);
                client.player.setXRot((float) (-Mth.atan2(delta.y, Math.hypot(delta.x, delta.z)) * 180 / Math.PI));
                if (++phaseTicks < 5) return;
                check(client.hitResult instanceof EntityHitResult hit && hit.getEntity() == aimed,
                        "Crosshair did not select corpse " + TARGETS[targetIndex] + ": " + client.hitResult);
                client.options.keyUse.setDown(true);
                phase = 2;
                phaseTicks = 0;
                return;
            }
            client.options.keyUse.setDown(false);
            if (phase == 2) {
                if (++phaseTicks < 15) return;
                verification = server.submit(() -> {
                    var player = server.getPlayerList().getPlayer(playerId);
                    var level = player.level();
                    var entity = level.getEntity(dragonId);
                    if (action == 3) {
                        check(entity == null || entity.isRemoved(), "Final harvest did not remove corpse");
                        check(!level.getEntitiesOfClass(ItemEntity.class,
                                new net.minecraft.world.phys.AABB(-5, -60, -5, 5, -50, 5),
                                item -> item.getItem().is(skullItem)).isEmpty(),
                                "Final harvest did not drop skull");
                    } else {
                        check(entity instanceof DragonBaseEntity, "Corpse disappeared");
                        var dragon = (DragonBaseEntity) entity;
                        check(dragon.getDeathStage() == (action >= 4 ? 0 : 1),
                                "Harvest count incorrect: " + dragon.getDeathStage() + ", action=" + action);
                        check(dragon.isModelDead() && dragon.isAlive(), "Harvest changed original corpse state");
                        if (action == 2) check(player.getInventory().countItem(Items.GLASS_BOTTLE) == 1,
                                "Main-hand harvest also consumed off-hand bottle");
                        if (action == 1) {
                            check(player.getInventory().countItem(dragon.getBloodItem()) == 1, "No blood received");
                            check(player.getInventory().countItem(Items.GLASS_BOTTLE) == 0, "Bottle was not consumed");
                        }
                    }
                });
                phase = 3;
            }
            if (phase == 3 && verification.isDone()) {
                verification.join();
                System.out.println("IAF CORPSE CLIENT: passed " + scenario + " " + TARGETS[targetIndex] + " action=" + action);
                scenario++;
                phase = 0;
                phaseTicks = 0;
                setup = null;
                verification = null;
                placement = null;
            }
        } catch (Throwable failure) {
            finish(client, failure);
        }
    }

    private static void finish(Minecraft client, Throwable failure) throws Exception {
        finished = true;
        client.options.keyUse.setDown(false);
        Files.writeString(client.gameDirectory.toPath().resolve("corpse-client-result.json"),
                "{\"passed\":" + scenario + ",\"failed\":" + (failure == null ? 0 : 1)
                        + ",\"failure\":\"" + (failure == null ? "" : failure.toString()
                        .replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ")) + "\"}");
        if (failure != null) failure.printStackTrace();
        else System.out.println("IAF CORPSE CLIENT: all 72 body/part scenarios passed");
        client.stop();
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
