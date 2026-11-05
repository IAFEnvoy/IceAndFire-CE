package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.TicketType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IafTicketTypes {
    public static final DeferredRegister<TicketType> REGISTRY = DeferredRegister.create(Registries.TICKET_TYPE, IceAndFire.MOD_ID);
    public static final DeferredHolder<TicketType, TicketType> DRAGON_SUMMON = REGISTRY.register("dragon_summon",
            () -> new TicketType(220, TicketType.FLAG_LOADING | TicketType.FLAG_SIMULATION | TicketType.FLAG_KEEP_DIMENSION_ACTIVE));

    private IafTicketTypes() { }
}
