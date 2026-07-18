package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.world.processor.DreadRuinProcessor;
import com.iafenvoy.iceandfire.world.processor.GraveyardProcessor;
import com.iafenvoy.iceandfire.world.processor.VillageHouseProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class IafProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> REGISTRY = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, IceAndFire.MOD_ID);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<GraveyardProcessor>> GRAVEYARD_PROCESSOR = registerProcessor("graveyard_processor", () -> () -> GraveyardProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<VillageHouseProcessor>> VILLAGE_HOUSE_PROCESSOR = registerProcessor("village_house_processor", () -> () -> VillageHouseProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<DreadRuinProcessor>> DREAD_MAUSOLEUM_PROCESSOR = registerProcessor("dread_mausoleum_processor", () -> () -> DreadRuinProcessor.CODEC);

    private static <P extends StructureProcessor> DeferredHolder<StructureProcessorType<?>, StructureProcessorType<P>> registerProcessor(String name, Supplier<StructureProcessorType<P>> processor) {
        return REGISTRY.register(name, processor);
    }
}
