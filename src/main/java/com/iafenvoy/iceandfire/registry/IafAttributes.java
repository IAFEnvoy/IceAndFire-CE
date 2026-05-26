package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

//FIXME::Fix this f**king thing after port to NeoForge only
public class IafAttributes {
    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(Registries.ATTRIBUTE, IceAndFire.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> DRAGON_FORGE_SPEED = REGISTRY.register("generic.dragon_forge_speed", () -> new RangedAttribute("attribute.name.generic.dragon_forge_speed", 0.025, 0, 1024).setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
}
