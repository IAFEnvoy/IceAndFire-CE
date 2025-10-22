package com.iafenvoy.iceandfire.neoforge.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.iafenvoy.iceandfire.item.tool.ActivePostHitSwordItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(EntityMaid.class)
public class EntityMaidMixin {
    @Dynamic
    @Inject(method = "tryAttack", at = @At(value = "INVOKE", target = "Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;doSweepHurt(Lnet/minecraft/entity/Entity;)V"))
    private void handleWeaponAbility(Entity target, CallbackInfoReturnable<Boolean> cir) {
        EntityMaid maid = (EntityMaid) (Object) this;
        ItemStack stack = maid.getStackInHand(Hand.MAIN_HAND);
        if (stack.getItem() instanceof ActivePostHitSwordItem sword && target instanceof LivingEntity living)
            sword.postHit(stack, living, maid);
    }
}
