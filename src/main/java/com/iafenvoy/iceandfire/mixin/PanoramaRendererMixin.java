package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.screen.TitleScreenRenderManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.PanoramaRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PanoramaRenderer.class, priority = 900)
public abstract class PanoramaRendererMixin {
    @Unique
    private int iceandfire$slowTick = 0;

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void onRenderBackground(GuiGraphics guiGraphics, int width, int height, float fade, float partialTick, CallbackInfo ci) {
        if (!IafClientConfig.INSTANCE.customMainMenu.getValue()) return;
        this.iceandfire$slowTick++;
        if (this.iceandfire$slowTick >= 3) {
            this.iceandfire$slowTick = 0;
            TitleScreenRenderManager.tick();
        }
        TitleScreenRenderManager.renderBackground(guiGraphics, width, height);
        ci.cancel();
    }
}
