package com.iafenvoy.iceandfire.event;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.component.ChainData;
import com.iafenvoy.iceandfire.data.component.MiscData;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.ICustomMoveController;
import com.iafenvoy.iceandfire.network.payload.DragonControlC2SPayload;
import com.iafenvoy.iceandfire.registry.IafKeybindings;
import com.iafenvoy.iceandfire.registry.IafStatusEffects;
import com.iafenvoy.iceandfire.render.RenderVariables;
import com.iafenvoy.iceandfire.render.entity.feature.DragonRiderFeatureRenderer;
import com.iafenvoy.iceandfire.render.misc.ChainRenderer;
import com.iafenvoy.iceandfire.render.misc.CockatriceBeamRenderer;
import com.iafenvoy.iceandfire.render.misc.FrozenStateRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@OnlyIn(Dist.CLIENT)
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
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "rendertype_dread_portal"), DefaultVertexFormat.POSITION_COLOR), program -> RenderVariables.DREAD_PORTAL_PROGRAM = program);
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
                    PacketDistributor.sendToServer(new DragonControlC2SPayload(entity.getId(), controlState, entity.blockPosition()));
            }
        }
        if (entity instanceof Player player && player == Minecraft.getInstance().player && player.getVehicle() instanceof ICustomMoveController controller) {
            Entity vehicle = player.getVehicle();
            byte previousState = controller.getControlState();
            controller.up(mc.options.keyJump.isDown());
            controller.down(IafKeybindings.DRAGON_DOWN.isDown());
            controller.attack(IafKeybindings.DRAGON_STRIKE.isDown());
            controller.dismount(mc.options.keyShift.isDown());
            controller.strike(IafKeybindings.DRAGON_BREATH.isDown());
            byte controlState = controller.getControlState();
            if (controlState != previousState)
                PacketDistributor.sendToServer(new DragonControlC2SPayload(vehicle.getId(), controlState, vehicle.blockPosition()));
        }
    }

    @SubscribeEvent
    public static void onPostRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity entity = event.getEntity();
        float partialRenderTick = event.getPartialTick();
        PoseStack matrixStack = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();
        int light = event.getPackedLight();

        MiscData miscData = MiscData.get(entity);
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) return;
        miscData.checkScepterTarget(world.entityStorage.getEntityGetter()::get);
        //Cockatrice Beam
        for (Entity target : miscData.getTargetedByScepters().stream().filter(Objects::nonNull).map(x -> world.entityStorage.getEntityGetter().get(x)).filter(Objects::nonNull).toList())
            CockatriceBeamRenderer.render(entity, target, matrixStack, buffers, partialRenderTick);
        //Frozen
        MobEffectInstance effect = entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(IafStatusEffects.FROZEN.get()));
        if (effect != null) FrozenStateRenderer.render(entity, matrixStack, buffers, light, effect.getDuration());
        //Chain
        ChainData chainData = ChainData.get(entity);
        ChainRenderer.render(entity, matrixStack, buffers, light, chainData.getChainedTo());
    }

    @SubscribeEvent
    public static void disablePlayerRenderWhenNeed(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (player.getVehicle() instanceof DragonBaseEntity && player instanceof LocalPlayer && (Minecraft.getInstance().options.getCameraType().isFirstPerson() || !DragonRiderFeatureRenderer.RENDERING_RIDERS.contains(player)))
            event.setCanceled(true);
        if (player instanceof RemotePlayer && player.getVehicle() instanceof DragonBaseEntity && !DragonRiderFeatureRenderer.RENDERING_RIDERS.contains(player))
            event.setCanceled(true);
    }
}