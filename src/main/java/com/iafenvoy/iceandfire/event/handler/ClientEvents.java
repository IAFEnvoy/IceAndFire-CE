package com.iafenvoy.iceandfire.event.handler;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.ICustomMoveController;
import com.iafenvoy.iceandfire.network.payload.DragonControlC2SPayload;
import com.iafenvoy.iceandfire.registry.IafKeyMappings;
import com.iafenvoy.iceandfire.render.entity.feature.DragonRiderFeatureRenderer;
import com.iafenvoy.iceandfire.render.misc.LightningBoltData;
import com.iafenvoy.iceandfire.render.misc.LightningRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.concurrent.CopyOnWriteArrayList;

@EventBusSubscriber(Dist.CLIENT)
public final class ClientEvents {
    public static int currentView = 0;
    public static final CopyOnWriteArrayList<Tuple<Vec3, Vec3>> LIGHTNINGS = new CopyOnWriteArrayList<>();

    @SubscribeEvent
    public static void onCameraSetup(CalculateDetachedCameraDistanceEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof DragonBaseEntity) {
            float scale = ((DragonBaseEntity) player.getVehicle()).getRenderSize() / 3;
            if (Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_BACK ||
                    Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT) {
                if (currentView == 1) event.setDistance(scale * 1.2F);
                else if (currentView == 2) event.setDistance(scale * 3);
                else if (currentView == 3) event.setDistance(scale * 5);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Minecraft mc = Minecraft.getInstance();
        if (entity instanceof ICustomMoveController moveController) {
            if (entity.getVehicle() != null && entity.getVehicle() == mc.player) {
                byte previousState = moveController.getControlState();
                moveController.dismount(mc.options.keyShift.isDown());
                byte controlState = moveController.getControlState();
                if (controlState != previousState)
                    ClientPacketDistributor.sendToServer(new DragonControlC2SPayload(entity.getId(), controlState, entity.blockPosition()));
            }
        }
        if (entity instanceof Player player && player == Minecraft.getInstance().player && player.getVehicle() instanceof ICustomMoveController controller) {
            Entity vehicle = player.getVehicle();
            byte previousState = controller.getControlState();
            controller.up(mc.options.keyJump.isDown());
            controller.down(IafKeyMappings.DRAGON_DOWN.isDown());
            controller.attack(IafKeyMappings.DRAGON_STRIKE.isDown());
            controller.dismount(mc.options.keyShift.isDown());
            controller.strike(IafKeyMappings.DRAGON_BREATH.isDown());
            byte controlState = controller.getControlState();
            if (controlState != previousState)
                ClientPacketDistributor.sendToServer(new DragonControlC2SPayload(vehicle.getId(), controlState, vehicle.blockPosition()));
        }
    }

    @SubscribeEvent
    public static void disablePlayerRenderWhenNeed(RenderPlayerEvent.Pre<?> event) {
        if (Minecraft.getInstance().level == null) return;
        Entity entity = Minecraft.getInstance().level.getEntity(event.getRenderState().id);
        if (!(entity instanceof Player player)) return;
        if (player.getVehicle() instanceof DragonBaseEntity && player instanceof LocalPlayer && (Minecraft.getInstance().options.getCameraType().isFirstPerson() || !DragonRiderFeatureRenderer.RENDERING_RIDERS.contains(player)))
            event.setCanceled(true);
        if (player instanceof RemotePlayer && player.getVehicle() instanceof DragonBaseEntity && !DragonRiderFeatureRenderer.RENDERING_RIDERS.contains(player))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void renderLightningBolts(RenderLevelStageEvent.AfterOpaqueFeatures event) {
        if (LIGHTNINGS.isEmpty()) return;
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getLevelRenderState().cameraRenderState.pos;
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        for (Tuple<Vec3, Vec3> pair : LIGHTNINGS) {
            LightningBoltData bolt = new LightningBoltData(LightningBoltData.BoltRenderInfo.ELECTRICITY, pair.getA(), pair.getB(), 4)
                    .size(0.05F)
                    .lifespan(10)
                    .fade(LightningBoltData.FadeFunction.fade(0.1F))
                    .spawn(LightningBoltData.SpawnFunction.NO_DELAY);
            LIGHTNING_RENDERER.update(null, bolt, 0.0F);
        }
        LIGHTNINGS.clear();
        LIGHTNING_RENDERER.render(0.0F, poseStack, Minecraft.getInstance().renderBuffers().bufferSource());
        poseStack.popPose();
    }

    private static final LightningRenderer LIGHTNING_RENDERER = new LightningRenderer();
}
