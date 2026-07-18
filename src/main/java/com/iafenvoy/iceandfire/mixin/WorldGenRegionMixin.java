package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

/**
 * Avoid log spam for dragon caves (some blocks can exceed the writable worldgen area due to their size)
 */
@Mixin(WorldGenRegion.class)
public class WorldGenRegionMixin {
    @Shadow
    private Supplier<String> currentlyGenerating;

    @Inject(method = "ensureCanWrite", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;logAndPauseIfInIde(Ljava/lang/String;)V"), cancellable = true)
    private void skipLog(final BlockPos pos, final CallbackInfoReturnable<Boolean> callback) {
        if (this.currentlyGenerating != null && this.currentlyGenerating.get().contains(IceAndFire.MOD_ID))
            callback.setReturnValue(false);
    }
}
