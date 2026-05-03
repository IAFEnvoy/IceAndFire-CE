package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.RegistryKeys;

//FIXME::Fix this f**king thing after port to NeoForge only
public class IafAttributes {
    public static final DeferredRegister<EntityAttribute> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.ATTRIBUTE);

    public static final RegistrySupplier<EntityAttribute> DRAGON_FORGE_SPEED = REGISTRY.register("generic.dragon_forge_speed", () -> new ClampedEntityAttribute("attribute.name.generic.dragon_forge_speed", 0.025, 0, 1024).setTracked(true).setCategory(EntityAttribute.Category.NEUTRAL));
}
