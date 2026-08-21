package com.iafenvoy.iceandfire.event;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.uranus.client.model.ITabulaModelAnimator;
import com.iafenvoy.uranus.util.function.MemorizeSupplier;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;

import java.util.Map;

public class CollectDragonSkullModelEvent extends Event {
    private final Map<DragonType, Pair<Identifier, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>>> models;

    public CollectDragonSkullModelEvent(Map<DragonType, Pair<Identifier, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>>>> models) {
        this.models = models;
    }

    public void register(DragonType type, Identifier modelId, MemorizeSupplier<ITabulaModelAnimator<? extends DragonBaseEntity>> animator) {
        this.models.put(type, Pair.of(modelId, animator));
    }
}
