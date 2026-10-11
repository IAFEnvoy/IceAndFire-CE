package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.EntityAmphithere;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.render.entity.layer.LayerAmphithereRider;
import com.iafenvoy.iceandfire.render.entity.layer.LayerDragonRider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void onPlayerRender(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (abstractClientPlayerEntity.getVehicle() instanceof EntityDragonBase && abstractClientPlayerEntity instanceof ClientPlayerEntity && (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() || !LayerDragonRider.renderingRiders.contains(abstractClientPlayerEntity)))
            ci.cancel();
        if (abstractClientPlayerEntity instanceof OtherClientPlayerEntity && abstractClientPlayerEntity.getVehicle() instanceof EntityDragonBase && !LayerDragonRider.renderingRiders.contains(abstractClientPlayerEntity))
            ci.cancel();
        //Flight rework: same handling for the amphithere rider, which LayerAmphithereRider draws as part of its mount
        if (IafCommonConfig.INSTANCE.amphithere.flightRework.getValue() && abstractClientPlayerEntity.getVehicle() instanceof EntityAmphithere) {
            if (abstractClientPlayerEntity instanceof ClientPlayerEntity && (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() || !LayerAmphithereRider.renderingRiders.contains(abstractClientPlayerEntity)))
                ci.cancel();
            if (abstractClientPlayerEntity instanceof OtherClientPlayerEntity && !LayerAmphithereRider.renderingRiders.contains(abstractClientPlayerEntity))
                ci.cancel();
        }
    }
}
