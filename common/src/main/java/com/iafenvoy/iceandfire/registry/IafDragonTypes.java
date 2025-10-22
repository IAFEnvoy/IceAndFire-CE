package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Function;
import java.util.function.Supplier;

public final class IafDragonTypes {
    public static final DragonType FIRE = register("fire", w -> IafEntities.FIRE_DRAGON.get().create(w), IafItems.DRAGON_SKULL_FIRE, IafItems.SUMMONING_CRYSTAL_FIRE, false);
    public static final DragonType ICE = register("ice", w -> IafEntities.ICE_DRAGON.get().create(w), IafItems.DRAGON_SKULL_ICE, IafItems.SUMMONING_CRYSTAL_ICE, true);
    public static final DragonType LIGHTNING = register("lightning", w -> IafEntities.LIGHTNING_DRAGON.get().create(w), IafItems.DRAGON_SKULL_LIGHTNING, IafItems.SUMMONING_CRYSTAL_LIGHTNING, false);
    public static final DragonType NETHER = register("nether", w -> IafEntities.NETHER_DRAGON.get().create(w), IafItems.DRAGON_SKULL_NETHER, IafItems.SUMMONING_CRYSTAL_NETHER, false);

    private static DragonType register(String name, Function<World, DragonBaseEntity> hatchEntityCreator, Supplier<Item> skullItem, Supplier<Item> crystalItem, boolean piscivore) {
        return Registry.register(IafRegistries.DRAGON_TYPE, Identifier.of(IceAndFire.MOD_ID, name), new DragonType(name, hatchEntityCreator, skullItem, crystalItem, piscivore));
    }

    public static void init() {
    }
}
