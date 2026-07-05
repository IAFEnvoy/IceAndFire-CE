package com.iafenvoy.iceandfire.data;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Supplier;

public class DragonType {
    private static final List<DragonType> TYPES = new ArrayList<>();
    private static final Map<String, DragonType> BY_NAME = new HashMap<>();
    public static final DragonType FIRE = new DragonType("fire", IafEntities.FIRE_DRAGON::get, IafItems.DRAGON_SKULL_FIRE, IafItems.SUMMONING_CRYSTAL_FIRE);
    public static final DragonType ICE = new DragonType("ice", IafEntities.ICE_DRAGON::get, IafItems.DRAGON_SKULL_ICE, IafItems.SUMMONING_CRYSTAL_ICE).setPiscivore();
    public static final DragonType LIGHTNING = new DragonType("lightning", IafEntities.LIGHTNING_DRAGON::get, IafItems.DRAGON_SKULL_LIGHTNING, IafItems.SUMMONING_CRYSTAL_LIGHTNING);
    private final String name;
    private final Supplier<EntityType<? extends EntityDragonBase>> entityType;
    private final Supplier<Item> skullItem, crystalItem;
    private boolean piscivore;

    public DragonType(String name, Supplier<EntityType<? extends EntityDragonBase>> entityType, Supplier<Item> skullItem, Supplier<Item> crystalItem) {
        this.name = name;
        this.entityType = entityType;
        this.skullItem = skullItem;
        this.crystalItem = crystalItem;
        TYPES.add(this);
        BY_NAME.put(name, this);
    }

    public static String getNameFromInt(int type) {
        return TYPES.get(type).name;
    }

    public static DragonType getTypeById(String id) {
        return BY_NAME.getOrDefault(id, FIRE);
    }

    public static DragonType getTypeByEntityType(EntityType<?> type) {
        return TYPES.stream().filter(x -> x.entityType.get() == type).findFirst().orElse(FIRE);
    }

    public int getIntFromType() {
        return TYPES.indexOf(this);
    }

    public EntityType<? extends EntityDragonBase> getEntity() {
        return this.entityType.get();
    }

    public String getName() {
        return this.name;
    }

    public boolean isPiscivore() {
        return this.piscivore;
    }

    public DragonType setPiscivore() {
        this.piscivore = true;
        return this;
    }

    public Identifier getSkeletonTexture(int stage) {
        return Identifier.of(IceAndFire.MOD_ID, String.format(Locale.ROOT, "textures/models/%sdragon/%s_skeleton_%d.png", this.name, this.name, stage));
    }

    public Item getSkullItem() {
        return this.skullItem.get();
    }

    public Item getCrystalItem() {
        return this.crystalItem.get();
    }
}
