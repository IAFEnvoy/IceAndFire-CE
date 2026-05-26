package com.iafenvoy.iceandfire.data;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record DragonType(String name, List<DragonColor> colors, Function<Level, DragonBaseEntity> hatchEntityCreator,
                         Supplier<Item> skullItem, Supplier<Item> crystalItem, boolean piscivore) {
    public DragonType(String name, Function<Level, DragonBaseEntity> hatchEntityCreator, Supplier<Item> skullItem, Supplier<Item> crystalItem, boolean piscivore) {
        this(name, new LinkedList<>(), hatchEntityCreator, skullItem, crystalItem, piscivore);
    }

    public DragonBaseEntity createEntity(Level world) {
        return this.hatchEntityCreator.apply(world);
    }

    public Item getSkullItem() {
        return this.skullItem.get();
    }

    public Item getCrystalItem() {
        return this.crystalItem.get();
    }

    public ResourceLocation getSkeletonTexture(int stage) {
        return ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, String.format("textures/entity/%sdragon/%s_skeleton_%d.png", this.name, this.name, stage));
    }
}
