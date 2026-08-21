package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.effect.FrozenStatusEffect;
import com.iafenvoy.iceandfire.item.ability.BuiltinAbilities;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand hand);

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"))
    private void onSwingHand(InteractionHand hand, boolean updateSelf, CallbackInfo ci) {
        if (this.getItemInHand(hand).is(IafItemTags.SUMMON_GHOST_SWORD) && BuiltinAbilities.SUMMON_GHOST_SWORD.isEnable())
            BuiltinAbilities.SUMMON_GHOST_SWORD.active((LivingEntity) (Object) this);
    }

    @Inject(method = "onEffectsRemoved", at = @At("HEAD"))
    private void handleFrozenEffectRemove(Collection<MobEffectInstance> effects, CallbackInfo ci) {
        for (MobEffectInstance effect : effects) {
            if (effect.getEffect().value() instanceof FrozenStatusEffect frozen)
                frozen.onRemoved((LivingEntity) (Object) this);
        }
    }
}
