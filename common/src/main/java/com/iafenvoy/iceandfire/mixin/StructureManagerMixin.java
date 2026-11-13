package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.world.structure.IceDragonCaveStructure;
import com.iafenvoy.iceandfire.world.structure.IceDragonRoostStructure;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StructureAccessor.class)
public class StructureManagerMixin {
    @Shadow
    @Final
    private WorldAccess world;

    @ModifyVariable(method = "setStructureStart", at = @At("HEAD"), argsOnly = true)
    private StructureStart iceandfire$blockIceDragonStructuresInEnd(StructureStart start) {
        // Reject the start before it is stored and any pieces are placed. The manager owns the
        // actual world; a structure generation context can instead contain only a protochunk.
        if ((start.getStructure() instanceof IceDragonCaveStructure || start.getStructure() instanceof IceDragonRoostStructure)
                && IafCommonConfig.INSTANCE.worldGen.preventIceDragonStructuresInEnd.getValue()
                && this.world instanceof ServerWorldAccess serverWorld
                && World.END.equals(serverWorld.toServerWorld().getRegistryKey()))
            return StructureStart.DEFAULT;
        return start;
    }
}
