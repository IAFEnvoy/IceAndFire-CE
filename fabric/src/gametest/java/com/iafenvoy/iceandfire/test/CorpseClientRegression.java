package com.iafenvoy.iceandfire.test;

import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.util.hit.EntityHitResult;

import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Uses the real crosshair, use key and network packets, then checks server-side loot. */
public final class CorpseClientRegression implements net.fabricmc.api.ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (!Boolean.getBoolean("iceandfire.corpseRegression")) return;
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try { tick(); } catch (Exception failure) { throw new IllegalStateException(failure); }
        });
    }
    private static boolean opening, finished;
    private static int ticks, worldTicks, scenario, phase, phaseTicks, dragonId;
    private static net.minecraft.item.Item skullItem;
    private static CompletableFuture<Integer> setup;
    private static CompletableFuture<?> verification;

    private static void tick() throws Exception {
        if (!Boolean.getBoolean("iceandfire.corpseRegression") || finished) return;
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            check(++ticks < 3600, "Corpse interaction timed out: scenario=" + scenario + ", phase=" + phase);
            if (client.getOverlay() != null) return;
            if (!opening) {
                if (client.currentScreen == null) return;
                opening = true;
                client.options.pauseOnLostFocus = false;
                Files.deleteIfExists(client.runDirectory.toPath().resolve("corpse-client-result.json"));
                client.createIntegratedServerLoader().createAndStart("corpse-" + System.currentTimeMillis(),
                        new LevelInfo("Corpse regression", GameMode.CREATIVE,
                                false, net.minecraft.world.Difficulty.NORMAL, true, new net.minecraft.world.GameRules(), DataConfiguration.SAFE_MODE),
                        new GeneratorOptions(284, false, false),
                        registries -> registries.get(RegistryKeys.WORLD_PRESET).getOrThrow(WorldPresets.FLAT)
                                .createDimensionsRegistryHolder());
                return;
            }
            if (client.world == null || client.player == null) return;
            if (client.currentScreen != null) client.setScreen(null);
            if (++worldTicks < 30) return;
            var server = client.getServer();
            check(server != null, "Integrated server missing");
            if (scenario == 18) {
                finish(client, null);
                return;
            }
            int action = scenario % 6;
            UUID playerId = client.player.getUuid();
            if (phase == 0) {
                if (setup == null) {
                    int typeIndex = scenario / 6;
                    setup = server.submit(() -> {
                        var player = server.getPlayerManager().getPlayer(playerId);
                        var level = player.getServerWorld();
                        if (level.getEntityById(dragonId) != null) level.getEntityById(dragonId).discard();
                        player.changeGameMode(action == 5 ? GameMode.ADVENTURE : GameMode.SURVIVAL);
                        player.setInvulnerable(true);
                        player.getInventory().clear();
                        var type = List.of(IafEntities.FIRE_DRAGON.get(), IafEntities.ICE_DRAGON.get(),
                                IafEntities.LIGHTNING_DRAGON.get()).get(typeIndex);
                        EntityDragonBase dragon = (EntityDragonBase) type.create(level);
                        check(dragon != null, "Could not spawn dragon");
                        dragon.setAgeInDays(125);
                        dragon.setAiDisabled(true);
                        dragon.setNoGravity(true);
                        dragon.setPosition(0, -59, 0);
                        dragon.setHealth(0);
                        dragon.setModelDead(true);
                        dragon.setDeathStage(action == 3 ? 24 : 0);
                        skullItem = dragon.getSkull().getItem();
                        level.spawnEntity(dragon);
                        var box = dragon.getBoundingBox();
                        player.requestTeleport(box.getCenter().x, dragon.getY(), box.minZ - 2);
                        player.setStackInHand(Hand.MAIN_HAND,
                                action == 1 ? new ItemStack(Items.GLASS_BOTTLE)
                                        : action == 4 ? new ItemStack(Items.STICK) : ItemStack.EMPTY);
                        player.setStackInHand(Hand.OFF_HAND,
                                action == 2 ? new ItemStack(Items.GLASS_BOTTLE) : ItemStack.EMPTY);
                        return dragon.getId();
                    });
                    return;
                }
                if (!setup.isDone()) return;
                dragonId = setup.join();
                if (!(client.world.getEntityById(dragonId) instanceof EntityDragonBase dragon)) return;
                check(!dragon.isAlive() && dragon.canHit(), "Corpse lost death state or body selection");
                for (var part : client.world.getEntitiesByClass(com.iafenvoy.iceandfire.entity.EntityDragonPart.class,
                        dragon.getBoundingBox().expand(32), part -> part.getParent() == dragon))
                    check(!part.canHit(), "Corpse part is still selectable");
                if (++phaseTicks < 30) return;
                phase = 1;
                phaseTicks = 0;
            }
            if (phase == 1) {
                var dragon = client.world.getEntityById(dragonId);
                check(dragon != null, "Corpse disappeared before interaction");
                var delta = dragon.getBoundingBox().getCenter().subtract(client.player.getEyePos());
                client.player.setYaw((float) (MathHelper.atan2(delta.z, delta.x) * 180 / Math.PI) - 90);
                client.player.setPitch((float) (-MathHelper.atan2(delta.y, Math.hypot(delta.x, delta.z)) * 180 / Math.PI));
                if (++phaseTicks < 5) return;
                check(client.crosshairTarget instanceof EntityHitResult hit && hit.getEntity() == dragon,
                        "Crosshair did not select corpse body: " + client.crosshairTarget);
                client.options.useKey.setPressed(true);
                phase = 2;
                phaseTicks = 0;
                return;
            }
            client.options.useKey.setPressed(false);
            if (phase == 2) {
                if (++phaseTicks < 15) return;
                verification = server.submit(() -> {
                    var player = server.getPlayerManager().getPlayer(playerId);
                    var level = player.getServerWorld();
                    var entity = level.getEntityById(dragonId);
                    if (action == 3) {
                        check(entity == null || entity.isRemoved(), "Final harvest did not remove corpse");
                        check(!level.getEntitiesByClass(ItemEntity.class,
                                new net.minecraft.util.math.Box(-5, -60, -5, 5, -50, 5),
                                item -> item.getStack().isOf(skullItem)).isEmpty(),
                                "Final harvest did not drop skull");
                    } else {
                        check(entity instanceof EntityDragonBase, "Corpse disappeared");
                        var dragon = (EntityDragonBase) entity;
                        check(dragon.getDeathStage() == (action >= 4 ? 0 : 1),
                                "Harvest count incorrect: " + dragon.getDeathStage() + ", action=" + action);
                        check(!dragon.isAlive(), "Harvest revived corpse");
                        if (action == 2) check(player.getInventory().count(Items.GLASS_BOTTLE) == 1,
                                "Main-hand harvest also consumed off-hand bottle");
                        if (action == 1) {
                            check(player.getInventory().count(dragon.getBloodItem()) == 1, "No blood received");
                            check(player.getInventory().count(Items.GLASS_BOTTLE) == 0, "Bottle was not consumed");
                        }
                    }
                });
                phase = 3;
            }
            if (phase == 3 && verification.isDone()) {
                verification.join();
                System.out.println("IAF CORPSE CLIENT: passed " + scenario);
                scenario++;
                phase = 0;
                phaseTicks = 0;
                setup = null;
                verification = null;
            }
        } catch (Throwable failure) {
            finish(client, failure);
        }
    }

    private static void finish(MinecraftClient client, Throwable failure) throws Exception {
        finished = true;
        client.options.useKey.setPressed(false);
        Files.writeString(client.runDirectory.toPath().resolve("corpse-client-result.json"),
                "{\"passed\":" + scenario + ",\"failed\":" + (failure == null ? 0 : 1)
                        + ",\"failure\":\"" + (failure == null ? "" : failure.toString()
                        .replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ")) + "\"}");
        if (failure != null) failure.printStackTrace();
        else System.out.println("IAF CORPSE CLIENT: all 18 scenarios passed");
        client.scheduleStop();
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
