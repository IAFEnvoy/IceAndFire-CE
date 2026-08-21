package com.iafenvoy.iceandfire.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("getBaseExperienceReward")
    int expReward(ServerLevel level);
}
