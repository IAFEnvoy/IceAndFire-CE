package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.world.structure.IceDragonCaveStructure;
import com.iafenvoy.iceandfire.world.structure.IceDragonRoostStructure;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StructureManager.class)
public class StructureManagerMixin {
    @Shadow
    @Final
    private LevelAccessor level;

    @ModifyVariable(method = "setStartForStructure", at = @At("HEAD"), argsOnly = true)
    private StructureStart iceandfire$blockIceDragonStructuresInEnd(StructureStart start) {
        // Reject the start before it is stored and any pieces are placed. The manager owns the
        // actual world; a structure generation context can instead contain only a protochunk.
        if ((start.getStructure() instanceof IceDragonCaveStructure || start.getStructure() instanceof IceDragonRoostStructure)
                && IafCommonConfig.INSTANCE.worldGen.preventIceDragonStructuresInEnd.getValue()
                && this.level instanceof ServerLevelAccessor serverLevel
                && Level.END.equals(serverLevel.getLevel().dimension()))
            return StructureStart.INVALID_START;
        return start;
    }
}
