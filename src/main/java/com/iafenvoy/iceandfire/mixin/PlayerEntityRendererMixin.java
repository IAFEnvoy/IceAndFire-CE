package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.render.entity.feature.DragonRiderFeatureRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
    private void onPlayerRender(AbstractClientPlayer abstractClientPlayerEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
        if (abstractClientPlayerEntity.getVehicle() instanceof DragonBaseEntity && abstractClientPlayerEntity instanceof LocalPlayer && (Minecraft.getInstance().options.getCameraType().isFirstPerson() || !DragonRiderFeatureRenderer.RENDERING_RIDERS.contains(abstractClientPlayerEntity)))
            ci.cancel();
        if (abstractClientPlayerEntity instanceof RemotePlayer && abstractClientPlayerEntity.getVehicle() instanceof DragonBaseEntity && !DragonRiderFeatureRenderer.RENDERING_RIDERS.contains(abstractClientPlayerEntity))
            ci.cancel();
    }
}
