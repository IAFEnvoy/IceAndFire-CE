package com.iafenvoy.iceandfire.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "prepareStartRegion", at = @At("HEAD"))
    private void beforeLoadingWorld(CallbackInfo ci) {
        //TODO
//        ServerEvents.addNewVillageBuilding((MinecraftServer) (Object) this);
    }
}
