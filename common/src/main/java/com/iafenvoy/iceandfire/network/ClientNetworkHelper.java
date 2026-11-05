package com.iafenvoy.iceandfire.network;

import com.iafenvoy.iceandfire.StaticVariables;
import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.entity.util.ISyncMount;
import com.iafenvoy.iceandfire.event.ClientEvents;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.List;

public class ClientNetworkHelper {
    private static Perspective prev = Perspective.FIRST_PERSON;

    public static void registerReceivers() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StaticVariables.DRAGON_SET_BURN_BLOCK, (buf, ctx) -> {
            int id = buf.readInt();
            boolean breathing = buf.readBoolean();
            BlockPos target = buf.readBlockPos();
            ctx.queue(() -> {
                PlayerEntity player = ctx.getPlayer();
                if (player != null && player.getWorld().getEntityById(id) instanceof EntityDragonBase dragon) {
                    dragon.setBreathingFire(breathing);
                    dragon.burningTarget = target;
                }
            });
        });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StaticVariables.START_RIDING_MOB_S2C, (buf, ctx) -> {
            int dragonId = buf.readInt();
            boolean ride = buf.readBoolean();
            boolean baby = buf.readBoolean();
            ctx.queue(() -> {
                GameOptions options = MinecraftClient.getInstance().options;
                PlayerEntity player = ctx.getPlayer();
                if (player != null) {
                    Entity entity = player.getWorld().getEntityById(dragonId);
                    if (entity instanceof ISyncMount && entity instanceof TameableEntity tamable) {
                        if (tamable.isOwner(player) && tamable.distanceTo(player) < 14) {
                            if (ride) {
                                if (baby) tamable.startRiding(player, true);
                                else {
                                    player.startRiding(tamable, true);
                                    if (IafClientConfig.INSTANCE.dragonAuto3rdPerson.getValue()) {
                                        prev = options.getPerspective();
                                        options.setPerspective(Perspective.THIRD_PERSON_BACK);
                                    }
                                }
                            } else {
                                if (baby) tamable.stopRiding();
                                else {
                                    player.stopRiding();
                                    if (IafClientConfig.INSTANCE.dragonAuto3rdPerson.getValue())
                                        options.setPerspective(prev);
                                }
                            }
                        }
                    }
                }
            });
        });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StaticVariables.LIGHTNING_BOLT_S2C, ((buf, ctx) -> {
            int size = buf.readInt();
            List<Pair<Vec3d, Vec3d>> lightnings = new LinkedList<>();
            for (int i = 0; i < size; i++)
                lightnings.add(new Pair<>(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()), new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble())));
            ctx.queue(() -> ClientEvents.LIGHTNINGS.addAll(lightnings));
        }));
    }
}
