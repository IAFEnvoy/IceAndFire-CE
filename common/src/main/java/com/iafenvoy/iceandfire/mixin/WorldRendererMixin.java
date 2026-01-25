package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.event.ClientEvents;
import com.iafenvoy.iceandfire.render.misc.LightningBoltData;
import com.iafenvoy.iceandfire.render.misc.LightningRenderer;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    @Final
    private BufferBuilderStorage bufferBuilders;
    @Unique
    private final LightningRenderer iceandfire$lightningRenderer = new LightningRenderer();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntities()Ljava/lang/Iterable;"))
    private void renderBolts(CallbackInfo ci, @Local(argsOnly = true) RenderTickCounter tickCounter, @Local(argsOnly = true) Camera camera, @Local MatrixStack matrices) {
        float tickDelta = tickCounter.getTickDelta(false);
        matrices.push();
        Vec3d pos = camera.getPos();
        matrices.translate(-pos.x, -pos.y, -pos.z);
        for (Pair<Vec3d, Vec3d> pair : ClientEvents.LIGHTNINGS) {
            LightningBoltData bolt = new LightningBoltData(LightningBoltData.BoltRenderInfo.ELECTRICITY, pair.getLeft(), pair.getRight(), 4)
                    .size(0.05F)
                    .lifespan(10)
                    .fade(LightningBoltData.FadeFunction.fade(0.1F))
                    .spawn(LightningBoltData.SpawnFunction.NO_DELAY);
            this.iceandfire$lightningRenderer.update(null, bolt, tickDelta);
        }
        ClientEvents.LIGHTNINGS.clear();
        this.iceandfire$lightningRenderer.render(tickDelta, matrices, this.bufferBuilders.getEntityVertexConsumers());
        matrices.pop();
    }
}
