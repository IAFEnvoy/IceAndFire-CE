package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.event.handler.ClientEvents;
import com.iafenvoy.iceandfire.render.misc.LightningBoltData;
import com.iafenvoy.iceandfire.render.misc.LightningRenderer;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    @Final
    private RenderBuffers renderBuffers;
    @Unique
    private final LightningRenderer iceandfire$lightningRenderer = new LightningRenderer();

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"))
    private void renderBolts(CallbackInfo ci, @Local(argsOnly = true) DeltaTracker tickCounter, @Local(argsOnly = true) Camera camera, @Local PoseStack matrices) {
        float tickDelta = tickCounter.getGameTimeDeltaPartialTick(false);
        matrices.pushPose();
        Vec3 pos = camera.getPosition();
        matrices.translate(-pos.x, -pos.y, -pos.z);
        for (Tuple<Vec3, Vec3> pair : ClientEvents.LIGHTNINGS) {
            LightningBoltData bolt = new LightningBoltData(LightningBoltData.BoltRenderInfo.ELECTRICITY, pair.getA(), pair.getB(), 4)
                    .size(0.05F)
                    .lifespan(10)
                    .fade(LightningBoltData.FadeFunction.fade(0.1F))
                    .spawn(LightningBoltData.SpawnFunction.NO_DELAY);
            this.iceandfire$lightningRenderer.update(null, bolt, tickDelta);
        }
        ClientEvents.LIGHTNINGS.clear();
        this.iceandfire$lightningRenderer.render(tickDelta, matrices, this.renderBuffers.bufferSource());
        matrices.popPose();
    }
}
