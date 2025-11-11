package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.registry.IafStructures;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.LakeFeature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

// Based on code from TelepathicGrunts RepurposedStructures
@Mixin(LakeFeature.class)
public class LakeFeatureMixin {
    @Inject(method = "generate", at = @At(value = "HEAD"), cancellable = true)
    private void iaf_noLakesInMausoleum(FeatureContext<SingleStateFeatureConfig> context, CallbackInfoReturnable<Boolean> cir) {
        if (!(context.getWorld() instanceof ChunkRegion))
            return;
        Registry<Structure> configuredStructureFeatureRegistry = context.getWorld().getRegistryManager().get(RegistryKeys.STRUCTURE);
        StructureAccessor structureManager = context.getWorld().toServerWorld().getStructureAccessor();
        List<Optional<Structure>> availableStructures = List.of(configuredStructureFeatureRegistry.getOrEmpty(IafStructures.MAUSOLEUM), configuredStructureFeatureRegistry.getOrEmpty(IafStructures.GRAVEYARD), configuredStructureFeatureRegistry.getOrEmpty(IafStructures.GORGON_TEMPLE));
        for (Optional<Structure> structure : availableStructures)
            if (structure.isPresent() && structureManager.getStructureAt(context.getOrigin(), structure.get()).hasChildren()) {
                cir.setReturnValue(false);
                return;
            }
    }
}