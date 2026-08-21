package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.registry.IafRegistrationContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.Properties.class)
public abstract class BlockPropertiesMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void setIceAndFireBlockId(CallbackInfo ci) {
        ResourceKey<Block> id = IafRegistrationContext.currentBlockId();
        if (id != null) ((BlockBehaviour.Properties) (Object) this).setId(id);
    }
}
