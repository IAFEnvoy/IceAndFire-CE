package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.render.PortalRenderHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @Shadow
    protected abstract void renderTextureOverlay(GuiGraphics context, ResourceLocation texture, float opacity);

    @Shadow
    @Final
    private static ResourceLocation POWDER_SNOW_OUTLINE_LOCATION;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "renderCameraOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getTicksFrozen()I"))
    private void renderDreadPortalOverlay(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (this.minecraft.player == null) return;
        int renderTick = PortalRenderHelper.getTick(), i = this.minecraft.player.getTicksRequiredToFreeze();
        if (renderTick > 0) this.renderTextureOverlay(context, POWDER_SNOW_OUTLINE_LOCATION, (float) Math.min(renderTick, i) / i);
    }
}
