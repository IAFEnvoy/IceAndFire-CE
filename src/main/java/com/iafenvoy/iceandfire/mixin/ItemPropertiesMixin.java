package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.registry.IafRegistrationContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.Properties.class)
public abstract class ItemPropertiesMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void setIceAndFireItemId(CallbackInfo ci) {
        ResourceKey<Item> id = IafRegistrationContext.currentItemId();
        if (id != null) ((Item.Properties) (Object) this).setId(id);
    }
}
