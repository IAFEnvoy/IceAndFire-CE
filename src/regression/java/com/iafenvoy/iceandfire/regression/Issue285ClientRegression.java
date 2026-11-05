package com.iafenvoy.iceandfire.regression;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonEggEntity;
import com.iafenvoy.iceandfire.item.block.entity.EggInIceBlockEntity;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafDragonColors;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Checks actual block-entity and chunk packets in an isolated integrated world. */
@EventBusSubscriber(modid = "iceandfire", value = Dist.CLIENT)
public final class Issue285ClientRegression {
    private static boolean opening;
    private static boolean finished;
    private static int ticks;
    private static int worldTicks;
    private static int phase;
    private static int passed;
    private static CompletableFuture<List<Sample>> setup;
    private static CompletableFuture<?> resend;
    private static List<Sample> samples;

    private record Sample(BlockPos pos, DragonColor color, UUID owner) {}

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) throws Exception {
        if (!Boolean.getBoolean("iceandfire.issue285Regression") || finished) return;
        Minecraft client = Minecraft.getInstance();
        try {
            check(++ticks < 1800, "Timed out waiting for ice egg network regression");
            if (client.getOverlay() != null) return;
            if (!opening) {
                if (client.screen == null) return;
                opening = true;
                client.options.pauseOnLostFocus = false;
                Files.deleteIfExists(client.gameDirectory.toPath().resolve("issue285-client-result.json"));
                client.createWorldOpenFlows().createFreshLevel("issue285-" + System.currentTimeMillis(),
                        new LevelSettings("Issue 285 regression", GameType.CREATIVE,
                                LevelSettings.DifficultySettings.DEFAULT, true, WorldDataConfiguration.DEFAULT),
                        new WorldOptions(285, false, false),
                        registries -> registries.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT)
                                .value().createWorldDimensions(), client.screen);
                return;
            }
            if (client.level == null || client.player == null) {
                check(setup == null, "Client disconnected while receiving ice egg packets");
                return;
            }
            if (client.screen != null) client.setScreen(null);
            if (++worldTicks < 20) return;
            var server = client.getSingleplayerServer();
            check(server != null, "Integrated server missing");
            if (setup == null) {
                UUID playerId = client.player.getUUID();
                BlockPos origin = new BlockPos((client.player.blockPosition().getX() & ~15) + 3,
                        client.player.blockPosition().getY() + 1, (client.player.blockPosition().getZ() & ~15) + 3);
                setup = server.submit(() -> {
                    var level = server.overworld();
                    var result = new ArrayList<Sample>();
                    server.getCommands().performPrefixedCommand(server.createCommandSourceStack(),
                            "setblock " + origin.getX() + " " + origin.getY() + " " + origin.getZ() + " iceandfire:egginice");
                    check(level.getBlockEntity(origin) instanceof EggInIceBlockEntity, "setblock failed");
                    result.add(new Sample(origin, null, null));
                    var colors = List.of(IafDragonColors.BLUE, IafDragonColors.WHITE,
                            IafDragonColors.SAPPHIRE, IafDragonColors.SILVER);
                    for (int i = 0; i < colors.size(); i++) {
                        BlockPos pos = origin.offset(i * 3, 0, 3);
                        level.setBlockAndUpdate(pos.below(), Blocks.ICE.defaultBlockState());
                        level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                        var egg = new DragonEggEntity(IafEntities.DRAGON_EGG.get(), level);
                        egg.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        egg.setEggType(colors.get(i));
                        UUID owner = i == 0 ? null : playerId;
                        egg.setOwnerId(owner);
                        egg.getRandom().setSeed(285 + i);
                        // Accelerate only the random wait, using the production freezing path.
                        for (int attempt = 0; attempt < 10000 && !egg.isRemoved(); attempt++) egg.updateEggCondition();
                        check(egg.isRemoved() && level.getBlockEntity(pos) instanceof EggInIceBlockEntity, "Egg did not freeze");
                        result.add(new Sample(pos, colors.get(i), owner));
                    }
                    return List.copyOf(result);
                });
                return;
            }
            if (!setup.isDone()) return;
            samples = setup.join();
            if (phase == 0) {
                if (!received(client)) return;
                passed += samples.size();
                // Remove client copies so the second pass must reconstruct them from chunk data.
                for (Sample sample : samples) client.level.removeBlockEntity(sample.pos());
                UUID playerId = client.player.getUUID();
                resend = server.submit(() -> {
                    var level = server.overworld();
                    var player = server.getPlayerList().getPlayer(playerId);
                    check(player != null, "Server player missing");
                    player.connection.send(new ClientboundLevelChunkWithLightPacket(level.getChunkAt(samples.getFirst().pos()),
                            level.getChunkSource().getLightEngine(), null, null));
                });
                phase = 1;
                return;
            }
            if (!resend.isDone()) return;
            resend.join();
            if (!received(client)) return;
            passed += samples.size();
            Files.writeString(client.gameDirectory.toPath().resolve("issue285-client-result.json"),
                    "{\"passed\":" + passed + ",\"failed\":0,\"checks\":\"setblock,freeze,block_entity_packet,chunk_packet\"}");
            finished = true;
            System.out.println("IAF ISSUE 285 CLIENT: all " + passed + " ice egg network scenarios passed");
            client.stop();
        } catch (Throwable failure) {
            finished = true;
            Files.writeString(client.gameDirectory.toPath().resolve("issue285-client-result.json"),
                    "{\"passed\":" + passed + ",\"failed\":1}");
            throw new AssertionError("Issue 285 client regression failed", failure);
        }
    }

    private static boolean received(Minecraft client) {
        for (Sample sample : samples) {
            if (!(client.level.getBlockEntity(sample.pos()) instanceof EggInIceBlockEntity egg)) return false;
            if (egg.type != sample.color() || !java.util.Objects.equals(egg.ownerUUID, sample.owner())) return false;
        }
        return true;
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
