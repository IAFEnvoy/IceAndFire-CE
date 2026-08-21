package com.iafenvoy.iceandfire.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLevel.class)
public interface ServerLevelMultipartAccessor {
    @Accessor("dragonParts")
    Int2ObjectMap<PartEntity<?>> iceandfire$getDragonParts();
}
