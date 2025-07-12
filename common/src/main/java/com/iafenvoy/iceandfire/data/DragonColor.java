package com.iafenvoy.iceandfire.data;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.item.Item;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record DragonColor(String name, Formatting color, DragonType dragonType, Supplier<Item> eggItem,
                          Supplier<Item> scaleItem) {
    private static final List<DragonColor> VALUES = new ArrayList<>();
    private static final Map<String, DragonColor> ID_MAP = new HashMap<>();
    private static final Map<DragonType, List<DragonColor>> BY_DRAGON_TYPE = new HashMap<>();
    public static final DragonColor RED = new DragonColor("red", Formatting.DARK_RED, DragonType.FIRE, IafItems.DRAGONEGG_RED, IafItems.DRAGONSCALES_RED);
    public static final DragonColor GREEN = new DragonColor("green", Formatting.DARK_GREEN, DragonType.FIRE, IafItems.DRAGONEGG_GREEN, IafItems.DRAGONSCALES_GREEN);
    public static final DragonColor BRONZE = new DragonColor("bronze", Formatting.GOLD, DragonType.FIRE, IafItems.DRAGONEGG_BRONZE, IafItems.DRAGONSCALES_BRONZE);
    public static final DragonColor GRAY = new DragonColor("gray", Formatting.GRAY, DragonType.FIRE, IafItems.DRAGONEGG_GRAY, IafItems.DRAGONSCALES_GRAY);
    public static final DragonColor BLUE = new DragonColor("blue", Formatting.AQUA, DragonType.ICE, IafItems.DRAGONEGG_BLUE, IafItems.DRAGONSCALES_BLUE);
    public static final DragonColor WHITE = new DragonColor("white", Formatting.WHITE, DragonType.ICE, IafItems.DRAGONEGG_WHITE, IafItems.DRAGONSCALES_WHITE);
    public static final DragonColor SAPPHIRE = new DragonColor("sapphire", Formatting.BLUE, DragonType.ICE, IafItems.DRAGONEGG_SAPPHIRE, IafItems.DRAGONSCALES_SAPPHIRE);
    public static final DragonColor SILVER = new DragonColor("silver", Formatting.DARK_GRAY, DragonType.ICE, IafItems.DRAGONEGG_SILVER, IafItems.DRAGONSCALES_SILVER);
    public static final DragonColor ELECTRIC = new DragonColor("electric", Formatting.DARK_BLUE, DragonType.LIGHTNING, IafItems.DRAGONEGG_ELECTRIC, IafItems.DRAGONSCALES_ELECTRIC);
    public static final DragonColor AMETHYST = new DragonColor("amethyst", Formatting.LIGHT_PURPLE, DragonType.LIGHTNING, IafItems.DRAGONEGG_AMETHYST, IafItems.DRAGONSCALES_AMETHYST);
    public static final DragonColor COPPER = new DragonColor("copper", Formatting.GOLD, DragonType.LIGHTNING, IafItems.DRAGONEGG_COPPER, IafItems.DRAGONSCALES_COPPER);
    public static final DragonColor BLACK = new DragonColor("black", Formatting.DARK_GRAY, DragonType.LIGHTNING, IafItems.DRAGONEGG_BLACK, IafItems.DRAGONSCALES_BLACK);

    public DragonColor(String name, Formatting color, DragonType dragonType, Supplier<Item> eggItem, Supplier<Item> scaleItem) {
        this.name = name;
        this.color = color;
        this.dragonType = dragonType;
        this.eggItem = eggItem;
        this.scaleItem = scaleItem;
        VALUES.add(this);
        ID_MAP.put(name, this);
        if (!BY_DRAGON_TYPE.containsKey(dragonType)) BY_DRAGON_TYPE.put(dragonType, new ArrayList<>());
        BY_DRAGON_TYPE.get(dragonType).add(this);
    }

    public static List<DragonColor> values() {
        return ImmutableList.copyOf(VALUES);
    }

    public static DragonColor byMetadata(int meta) {
        DragonColor i = values().get(meta);
        return i == null ? RED : i;
    }

    public static DragonColor getById(String id) {
        return ID_MAP.getOrDefault(id, RED);
    }

    public static List<DragonColor> getColorsByType(DragonType type) {
        return ImmutableList.copyOf(BY_DRAGON_TYPE.getOrDefault(type, new ArrayList<>()));
    }

    public Item getEggItem() {
        return this.eggItem.get();
    }

    public Item getScaleItem() {
        return this.scaleItem.get();
    }

    public Identifier getEggTexture() {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/models/%sdragon/egg_%s.png", this.dragonType.getName(), this.name));
    }

    public Identifier getTextureByEntity(EntityDragonBase dragon) {
        int stage = dragon.getDragonStage();
        if (dragon.isModelDead()) {
            if (dragon.getDeathStage() >= (dragon.getAgeInDays() / 5) / 2) return this.getSkeletonTexture(stage);
            else return this.getSleepTexture(stage);
        } else if (dragon.isSleeping() || dragon.isBlinking()) return this.getSleepTexture(stage);
        else return this.getBodyTexture(stage);
    }

    public Identifier getBodyTexture(int stage) {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/models/%sdragon/%s_%d.png", this.dragonType.getName(), this.name, stage));
    }

    public Identifier getSleepTexture(int stage) {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/models/%sdragon/%s_%d_sleeping.png", this.dragonType.getName(), this.name, stage));
    }

    public Identifier getEyesTexture(int stage) {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/models/%sdragon/%s_%d_eyes.png", this.dragonType.getName(), this.name, stage));
    }

    public Identifier getSkeletonTexture(int stage) {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/models/%sdragon/%s_skeleton_%d.png", this.dragonType.getName(), this.dragonType.getName(), stage));
    }

    public Identifier getMaleOverlay() {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/models/%sdragon/male_%s.png", this.dragonType.getName(), this.name));
    }
}
