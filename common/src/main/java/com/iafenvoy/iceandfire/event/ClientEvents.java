package com.iafenvoy.iceandfire.event;

import com.iafenvoy.iceandfire.data.component.ChainData;
import com.iafenvoy.iceandfire.data.component.FrozenData;
import com.iafenvoy.iceandfire.data.component.MiscData;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.ICustomMoveController;
import com.iafenvoy.iceandfire.network.payload.DragonControlC2SPayload;
import com.iafenvoy.iceandfire.registry.IafKeybindings;
import com.iafenvoy.iceandfire.render.misc.ChainRenderer;
import com.iafenvoy.iceandfire.render.misc.CockatriceBeamRenderer;
import com.iafenvoy.iceandfire.render.misc.FrozenStateRenderer;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public final class ClientEvents {
    public static int currentView = 0;
    public static final CopyOnWriteArrayList<Pair<Vec3d, Vec3d>> LIGHTNINGS = new CopyOnWriteArrayList<>();

    public static void onCameraSetup(Camera camera) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.getVehicle() instanceof DragonBaseEntity) {
            float scale = ((DragonBaseEntity) player.getVehicle()).getRenderSize() / 3;
            if (MinecraftClient.getInstance().options.getPerspective() == Perspective.THIRD_PERSON_BACK ||
                    MinecraftClient.getInstance().options.getPerspective() == Perspective.THIRD_PERSON_FRONT) {
                if (currentView == 1) camera.moveBy(-camera.clipToSpace(scale * 1.2F), 0F, 0);
                else if (currentView == 2) camera.moveBy(-camera.clipToSpace(scale * 3F), 0F, 0);
                else if (currentView == 3) camera.moveBy(-camera.clipToSpace(scale * 5F), 0F, 0);
            }
        }
    }

    public static void onLivingUpdate(LivingEntity entity) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (entity instanceof ICustomMoveController moveController) {
            if (entity.getVehicle() != null && entity.getVehicle() == mc.player) {
                byte previousState = moveController.getControlState();
                moveController.dismount(mc.options.sneakKey.isPressed());
                byte controlState = moveController.getControlState();
                if (controlState != previousState)
                    NetworkManager.sendToServer(new DragonControlC2SPayload(entity.getId(), controlState, entity.getBlockPos()));
            }
        }
        if (entity instanceof PlayerEntity player && player == MinecraftClient.getInstance().player && player.getVehicle() instanceof ICustomMoveController controller) {
            Entity vehicle = player.getVehicle();
            byte previousState = controller.getControlState();
            controller.up(mc.options.jumpKey.isPressed());
            controller.down(IafKeybindings.DRAGON_DOWN.isPressed());
            controller.attack(IafKeybindings.DRAGON_STRIKE.isPressed());
            controller.dismount(mc.options.sneakKey.isPressed());
            controller.strike(IafKeybindings.DRAGON_BREATH.isPressed());
            byte controlState = controller.getControlState();
            if (controlState != previousState)
                NetworkManager.sendToServer(new DragonControlC2SPayload(vehicle.getId(), controlState, vehicle.getBlockPos()));
        }
    }

    public static void onPostRenderLiving(LivingEntity entity, float partialRenderTick, MatrixStack matrixStack, VertexConsumerProvider buffers, int light) {
        MiscData miscData = MiscData.get(entity);
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;
        miscData.checkScepterTarget(world.entityManager.getLookup()::get);
        for (UUID target : miscData.getTargetedByScepters())
            CockatriceBeamRenderer.render(entity, world.entityManager.getLookup().get(target), matrixStack, buffers, partialRenderTick);
        FrozenData frozenData = FrozenData.get(entity);
        if (frozenData.isFrozen)
            FrozenStateRenderer.render(entity, matrixStack, buffers, light, frozenData.frozenTicks);
        ChainData chainData = ChainData.get(entity);
        ChainRenderer.render(entity, matrixStack, buffers, light, chainData.getChainedTo());
    }
}