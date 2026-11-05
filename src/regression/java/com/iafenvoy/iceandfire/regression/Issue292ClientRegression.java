package com.iafenvoy.iceandfire.regression;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

/** Reproduces #292 against a real ClientLevel in an isolated integrated world. */
@EventBusSubscriber(modid = "iceandfire", value = Dist.CLIENT)
public final class Issue292ClientRegression {
    private static boolean opening;
    private static boolean finished;

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) throws Exception {
        if (!Boolean.getBoolean("iceandfire.issue292Regression") || finished) return;
        Minecraft client = Minecraft.getInstance();
        if (client.getOverlay() != null) return;
        if (!opening) {
            if (client.screen == null) return;
            opening = true;
            client.createWorldOpenFlows().createFreshLevel("issue292-" + System.currentTimeMillis(),
                    new LevelSettings("Issue 292 regression", GameType.SURVIVAL,
                            LevelSettings.DifficultySettings.DEFAULT, true, WorldDataConfiguration.DEFAULT),
                    new WorldOptions(292, false, false),
                    registries -> registries.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT)
                            .value().createWorldDimensions(), client.screen);
            return;
        }
        if (client.level == null || client.player == null) return;
        finished = true;
        int passed = 0;
        var result = client.gameDirectory.toPath().resolve("issue292-client-result.json");
        try {
            RemotePlayer observerPrey = new RemotePlayer(client.level, new GameProfile(UUID.randomUUID(), "issue292-prey"));
            for (var type : List.of(IafEntities.FIRE_DRAGON.get(), IafEntities.ICE_DRAGON.get(), IafEntities.LIGHTNING_DRAGON.get())) {
                for (int age : new int[]{75, 100}) {
                    for (Player prey : List.of(client.player, observerPrey)) {
                        DragonBaseEntity dragon = (DragonBaseEntity) type.create(client.level, EntitySpawnReason.COMMAND);
                        check(dragon != null, "Could not create dragon");
                        dragon.setAgeInDays(age);
                        dragon.setHunger(50);
                        dragon.setHealth(dragon.getMaxHealth() / 2);
                        dragon.setPos(client.player.position());
                        check(prey.startRiding(dragon, true, false), "Could not mount prey");
                        float preyHealth = prey.getHealth(), dragonHealth = dragon.getHealth();
                        try {
                            for (int tick : new int[]{25, 55, 56, 60, 64}) {
                                dragon.setAnimation(DragonBaseEntity.ANIMATION_SHAKEPREY);
                                dragon.setAnimationTick(tick);
                                dragon.setPos(dragon.position().add(2, 0, 0));
                                var before = prey.position();
                                // This is the callback used during client passenger ticking in the crash report.
                                dragon.positionRider(prey, Entity::setPos);
                                check(prey.getVehicle() == dragon, "Client released prey before server synchronization");
                                check(!prey.position().equals(before), "Client stopped positioning prey");
                                check(prey.getHealth() == preyHealth && dragon.getHealth() == dragonHealth
                                        && dragon.getHunger() == 50, "Client changed combat state");
                            }
                            passed++;
                        } finally {
                            prey.stopRiding();
                            dragon.discard();
                        }
                    }
                }
            }
            Files.writeString(result, "{\"passed\":" + passed + ",\"failed\":0,\"world\":\"ClientLevel\"}");
            System.out.println("IAF ISSUE 292 CLIENT: all " + passed + " player-prey scenarios passed");
            client.stop();
        } catch (Throwable failure) {
            Files.writeString(result, "{\"passed\":" + passed + ",\"failed\":1}");
            throw new AssertionError("Issue 292 client regression failed", failure);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
