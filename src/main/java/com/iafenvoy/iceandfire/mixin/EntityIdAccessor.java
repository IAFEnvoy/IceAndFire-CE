package com.iafenvoy.iceandfire.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Entity.class)
public interface EntityIdAccessor {
    @Accessor("ENTITY_COUNTER")
    static AtomicInteger iceandfire$getEntityCounter() {
        throw new AssertionError();
    }
}
