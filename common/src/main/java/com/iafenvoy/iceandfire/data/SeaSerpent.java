package com.iafenvoy.iceandfire.data;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.ItemSeaSerpentScales;
import com.iafenvoy.iceandfire.item.armor.IafArmorMaterial;
import com.iafenvoy.iceandfire.item.armor.ItemSeaSerpentArmor;
import com.iafenvoy.iceandfire.item.block.BlockSeaSerpentScales;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.uranus.object.IdUtil;
import com.iafenvoy.uranus.object.item.CustomArmorMaterial;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;

public class SeaSerpent {
    private static final List<SeaSerpent> TYPES = new ArrayList<>();
    private static final Map<String, SeaSerpent> BY_NAME = new HashMap<>();
    public static final SeaSerpent BLUE = new SeaSerpent("blue", Formatting.BLUE);
    public static final SeaSerpent BRONZE = new SeaSerpent("bronze", Formatting.GOLD);
    public static final SeaSerpent DEEPBLUE = new SeaSerpent("deepblue", Formatting.DARK_BLUE);
    public static final SeaSerpent GREEN = new SeaSerpent("green", Formatting.DARK_GREEN);
    public static final SeaSerpent PURPLE = new SeaSerpent("purple", Formatting.DARK_PURPLE);
    public static final SeaSerpent RED = new SeaSerpent("red", Formatting.DARK_RED);
    public static final SeaSerpent TEAL = new SeaSerpent("teal", Formatting.AQUA);
    private final String name;
    private final Formatting color;
    public CustomArmorMaterial armorMaterial;
    public RegistrySupplier<Item> scale, helmet, chestplate, leggings, boots;
    public RegistrySupplier<Block> scaleBlock;

    public SeaSerpent(String name, Formatting color) {
        this.name = name;
        this.color = color;
        TYPES.add(this);
        BY_NAME.put(name, this);
    }

    public String getName() {
        return this.name;
    }

    public Formatting getColor() {
        return this.color;
    }

    public Identifier getTexture(boolean blink) {
        return Identifier.of(IceAndFire.MOD_ID, String.format(Locale.ROOT, "textures/models/seaserpent/seaserpent_%s%s.png", this.name, blink ? "_blink" : ""));
    }

    public static List<SeaSerpent> values() {
        return ImmutableList.copyOf(TYPES);
    }

    public static void initArmors() {
        for (SeaSerpent color : SeaSerpent.values()) {
            color.armorMaterial = new IafArmorMaterial(IdUtil.build(IceAndFire.MOD_ID, "sea_serpent_scales_") + color.name, 30, new int[]{4, 8, 7, 4}, 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 2.5F);
            color.scaleBlock = IafBlocks.register("sea_serpent_scale_block_" + color.name, () -> new BlockSeaSerpentScales(color.name, color.color));
            color.scale = IafItems.register("sea_serpent_scales_" + color.name, () -> new ItemSeaSerpentScales(color.name, color.color));
            color.helmet = IafItems.register("tide_" + color.name + "_helmet", () -> new ItemSeaSerpentArmor(color, color.armorMaterial, ArmorItem.Type.HELMET));
            color.chestplate = IafItems.register("tide_" + color.name + "_chestplate", () -> new ItemSeaSerpentArmor(color, color.armorMaterial, ArmorItem.Type.CHESTPLATE));
            color.leggings = IafItems.register("tide_" + color.name + "_leggings", () -> new ItemSeaSerpentArmor(color, color.armorMaterial, ArmorItem.Type.LEGGINGS));
            color.boots = IafItems.register("tide_" + color.name + "_boots", () -> new ItemSeaSerpentArmor(color, color.armorMaterial, ArmorItem.Type.BOOTS));
        }
    }

    public static SeaSerpent getByName(String name) {
        return BY_NAME.getOrDefault(name, BLUE);
    }
}
