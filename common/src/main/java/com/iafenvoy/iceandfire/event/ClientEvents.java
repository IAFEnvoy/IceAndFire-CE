package com.iafenvoy.iceandfire.event;

import com.iafenvoy.iceandfire.StaticVariables;
import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.entity.EntityMultipartPart;
import com.iafenvoy.iceandfire.entity.util.ICustomMoveController;
import com.iafenvoy.iceandfire.particle.CockatriceBeamRender;
import com.iafenvoy.iceandfire.particle.LightningRender;
import com.iafenvoy.iceandfire.registry.IafKeybindings;
import com.iafenvoy.iceandfire.registry.IafParticles;
import com.iafenvoy.iceandfire.render.block.RenderFrozenState;
import com.iafenvoy.iceandfire.render.entity.RenderChain;
import com.iafenvoy.uranus.network.PacketBufferUtils;
import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public class ClientEvents {
    private static final Identifier SIREN_SHADER = Identifier.of("iceandfire", "shaders/post/siren.json");
    public static int currentView = 0;
    public static final CopyOnWriteArrayList<Pair<Vec3d, Vec3d>> LIGHTNINGS = new CopyOnWriteArrayList<>();

    public static void onCameraSetup(Camera camera) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.getVehicle() instanceof EntityDragonBase) {
            float scale = ((EntityDragonBase) player.getVehicle()).getRenderSize() / 3;
            if (MinecraftClient.getInstance().options.getPerspective() == Perspective.THIRD_PERSON_BACK ||
                    MinecraftClient.getInstance().options.getPerspective() == Perspective.THIRD_PERSON_FRONT) {
                if (currentView == 1) {
                    camera.moveBy(-camera.clipToSpace(scale * 1.2F), 0F, 0);
                } else if (currentView == 2) {
                    camera.moveBy(-camera.clipToSpace(scale * 3F), 0F, 0);
                } else if (currentView == 3) {
                    camera.moveBy(-camera.clipToSpace(scale * 5F), 0F, 0);
                }
            }
        }
    }

    public static EventResult onEntityInteract(PlayerEntity player, Entity entity, Hand hand) {
        // Hook multipart
        if (entity instanceof EntityMultipartPart) return EventResult.interruptTrue();
        return EventResult.pass();
    }

    public static void onLivingUpdate(LivingEntity entity) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (entity instanceof ICustomMoveController moveController) {
            if (entity.getVehicle() != null && entity.getVehicle() == mc.player) {
                byte previousState = moveController.getControlState();
                moveController.dismount(mc.options.sneakKey.isPressed());
                byte controlState = moveController.getControlState();
                if (controlState != previousState) {
                    PacketByteBuf buf = PacketBufferUtils.create();
                    buf.writeInt(entity.getId()).writeByte(controlState);
                    buf.writeBlockPos(entity.getBlockPos());
                    NetworkManager.sendToServer(StaticVariables.DRAGON_CONTROL, buf);
                }
            }
        }
        if (entity instanceof PlayerEntity player && player.getWorld().isClient) {
            if (player.getVehicle() instanceof ICustomMoveController controller) {
                Entity vehicle = player.getVehicle();
                byte previousState = controller.getControlState();
                controller.up(mc.options.jumpKey.isPressed());
                controller.down(IafKeybindings.DRAGON_DOWN.isPressed());
                controller.attack(IafKeybindings.DRAGON_STRIKE.isPressed());
                controller.dismount(mc.options.sneakKey.isPressed());
                controller.strike(IafKeybindings.DRAGON_BREATH.isPressed());
                byte controlState = controller.getControlState();
                if (controlState != previousState) {
                    PacketByteBuf buf = PacketBufferUtils.create();
                    buf.writeInt(vehicle.getId()).writeByte(controlState);
                    buf.writeBlockPos(vehicle.getBlockPos());
                    NetworkManager.sendToServer(StaticVariables.DRAGON_CONTROL, buf);
                }
            }
            GameRenderer renderer = MinecraftClient.getInstance().gameRenderer;
            IafEntityData data = IafEntityData.get(player);
            if (IafClientConfig.INSTANCE.sirenShader.getValue() && data.sirenData.charmedBy == null && renderer.getPostProcessor() != null)
                if (SIREN_SHADER.toString().equals(renderer.getPostProcessor().getName()))
                    renderer.disablePostProcessor();
            if (data.sirenData.charmedBy == null) return;
            if (IafClientConfig.INSTANCE.sirenShader.getValue() && !data.sirenData.isCharmed && renderer.getPostProcessor() != null && SIREN_SHADER.toString().equals(renderer.getPostProcessor().getName()))
                renderer.disablePostProcessor();
            if (data.sirenData.isCharmed) {
                if (entity.getRandom().nextInt(40) == 0)
                    entity.getWorld().addParticle(IafParticles.SIREN_APPEARANCE.get(), player.getX(), player.getY(), player.getZ(), data.sirenData.charmedBy.getHairColor(), 0, 0);
                if (IafClientConfig.INSTANCE.sirenShader.getValue() && renderer.getPostProcessor() == null)
                    renderer.loadPostProcessor(SIREN_SHADER);
            }
        }
    }

    public static void onPostRenderLiving(LivingEntity entity, float partialRenderTick, MatrixStack matrixStack, VertexConsumerProvider buffers, int light) {
        IafEntityData data = IafEntityData.get(entity);
        data.miscData.checkScepterTarget();
        for (LivingEntity target : data.miscData.getTargetedByScepter())
            CockatriceBeamRender.render(entity, target, matrixStack, buffers, partialRenderTick);
        if (data.frozenData.isFrozen)
            RenderFrozenState.render(entity, matrixStack, buffers, light, data.frozenData.frozenTicks);
        RenderChain.render(entity, matrixStack, buffers, light, data.chainData.getChainedTo());
    }
}