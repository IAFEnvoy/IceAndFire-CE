package com.iafenvoy.iceandfire.network;

import com.iafenvoy.iceandfire.StaticVariables;
import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.entity.block.BlockEntityJar;
import com.iafenvoy.iceandfire.entity.block.BlockEntityPixieHouse;
import com.iafenvoy.iceandfire.entity.block.BlockEntityPodium;
import com.iafenvoy.iceandfire.entity.util.ISyncMount;
import com.iafenvoy.iceandfire.event.ClientEvents;
import com.iafenvoy.uranus.network.PacketBufferUtils;
import dev.architectury.networking.NetworkManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.List;

public class ClientNetworkHelper {
    private static Perspective prev = Perspective.FIRST_PERSON;

    public static void registerReceivers() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StaticVariables.DRAGON_SET_BURN_BLOCK, (buf, ctx) -> {
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                Entity entity = player.getWorld().getEntityById(buf.readInt());
                if (entity instanceof EntityDragonBase dragon) {
                    dragon.setBreathingFire(buf.readBoolean());
                    dragon.burningTarget = new BlockPos(buf.readBlockPos());
                }
            }
        });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StaticVariables.START_RIDING_MOB_S2C, (buf, ctx) -> {
            GameOptions options = MinecraftClient.getInstance().options;
            int dragonId = buf.readInt();
            boolean ride = buf.readBoolean();
            boolean baby = buf.readBoolean();
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
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StaticVariables.UPDATE_PIXIE_HOUSE, (buf, ctx) -> {
            BlockPos blockPos = buf.readBlockPos();
            boolean hasPixie = buf.readBoolean();
            int pixieType = buf.readInt();
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                BlockEntity blockEntity = player.getWorld().getBlockEntity(blockPos);
                if (blockEntity instanceof BlockEntityPixieHouse house) {
                    house.hasPixie = hasPixie;
                    house.pixieType = pixieType;
                } else if (blockEntity instanceof BlockEntityJar jar) {
                    jar.hasPixie = hasPixie;
                    jar.pixieType = pixieType;
                }
            }
        });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StaticVariables.UPDATE_PIXIE_JAR, (buf, ctx) -> {
            BlockPos blockPos = buf.readBlockPos();
            boolean isProducing = buf.readBoolean();
            PlayerEntity player = ctx.getPlayer();
            if (player != null) {
                if (player.getWorld().getBlockEntity(blockPos) instanceof BlockEntityJar jar)
                    jar.hasProduced = isProducing;
            }
        });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, StaticVariables.UPDATE_PODIUM, (buf, ctx) -> {
            BlockPos blockPos = buf.readBlockPos();
            ItemStack heldStack = PacketBufferUtils.readItemStack(buf);
            PlayerEntity player = ctx.getPlayer();
            if (player != null)
                if (player.getWorld().getBlockEntity(blockPos) instanceof BlockEntityPodium podium)
                    podium.setStack(0, heldStack);
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
