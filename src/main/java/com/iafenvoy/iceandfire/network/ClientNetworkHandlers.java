package com.iafenvoy.iceandfire.network;

import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.ISyncMount;
import com.iafenvoy.iceandfire.event.handler.ClientEvents;
import com.iafenvoy.iceandfire.item.block.entity.JarBlockEntity;
import com.iafenvoy.iceandfire.item.block.entity.PixieHouseBlockEntity;
import com.iafenvoy.iceandfire.item.block.entity.PodiumBlockEntity;
import com.iafenvoy.iceandfire.network.payload.*;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientNetworkHandlers {
    private static CameraType prev = CameraType.FIRST_PERSON;

    private ClientNetworkHandlers() {
    }

    public static void handleDragonSetBurnBlock(DragonSetBurnBlockS2CPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Entity entity = player.level().getEntity(payload.entityId());
            if (entity instanceof DragonBaseEntity dragon) {
                dragon.setBreathingFire(payload.breathing());
                dragon.burningTarget = new BlockPos(payload.target());
            }
        });
    }

    public static void handleLightningBolt(LightningBoltS2CPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientEvents.LIGHTNINGS.addAll(payload.lightnings()));
    }

    public static void handleStartRidingMob(StartRidingMobPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Options options = Minecraft.getInstance().options;
            Entity entity = player.level().getEntity(payload.dragonId());
            if (entity instanceof ISyncMount && entity instanceof TamableAnimal tamable && tamable.isOwnedBy(player) && tamable.distanceTo(player) < 14) {
                if (payload.ride()) {
                    if (payload.baby()) tamable.startRiding(player, true);
                    else {
                        player.startRiding(tamable, true);
                        if (IafClientConfig.INSTANCE.dragonAuto3rdPerson.getValue()) {
                            prev = options.getCameraType();
                            options.setCameraType(CameraType.THIRD_PERSON_BACK);
                        }
                    }
                } else {
                    if (payload.baby()) tamable.stopRiding();
                    else {
                        player.stopRiding();
                        if (IafClientConfig.INSTANCE.dragonAuto3rdPerson.getValue())
                            options.setCameraType(prev);
                    }
                }
            }
        });
    }

    public static void handleUpdatePixieHouse(UpdatePixieHouseS2CPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            BlockEntity blockEntity = player.level().getBlockEntity(payload.blockPos());
            if (blockEntity instanceof PixieHouseBlockEntity house) {
                house.hasPixie = payload.hasPixie();
                house.pixieType = payload.pixieType();
            } else if (blockEntity instanceof JarBlockEntity jar) {
                jar.hasPixie = payload.hasPixie();
                jar.pixieType = payload.pixieType();
            }
        });
    }

    public static void handleUpdatePixieJar(UpdatePixieJarS2CPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player.level().getBlockEntity(payload.blockPos()) instanceof JarBlockEntity jar)
                jar.hasProduced = payload.isProducing();
        });
    }

    public static void handleUpdatePodium(UpdatePodiumS2CPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player.level().getBlockEntity(payload.blockPos()) instanceof PodiumBlockEntity podium)
                podium.setItem(0, payload.heldStack());
        });
    }
}
