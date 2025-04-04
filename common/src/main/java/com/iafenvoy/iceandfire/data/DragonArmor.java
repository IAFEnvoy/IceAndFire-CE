package com.iafenvoy.iceandfire.data;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.armor.IafArmorMaterial;
import com.iafenvoy.iceandfire.item.armor.ItemScaleArmor;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.uranus.object.IdUtil;
import com.iafenvoy.uranus.object.item.CustomArmorMaterial;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class DragonArmor {
    private static final List<DragonArmor> ARMORS = new ArrayList<>();
    public static final DragonArmor RED = new DragonArmor(DragonColor.RED, IafItems.DRAGONSCALES_RED);
    public static final DragonArmor BRONZE = new DragonArmor(DragonColor.BRONZE, IafItems.DRAGONSCALES_BRONZE);
    public static final DragonArmor GREEN = new DragonArmor(DragonColor.GREEN, IafItems.DRAGONSCALES_GREEN);
    public static final DragonArmor GRAY = new DragonArmor(DragonColor.GRAY, IafItems.DRAGONSCALES_GRAY);
    public static final DragonArmor BLUE = new DragonArmor(DragonColor.BLUE, IafItems.DRAGONSCALES_BLUE);
    public static final DragonArmor WHITE = new DragonArmor(DragonColor.WHITE, IafItems.DRAGONSCALES_WHITE);
    public static final DragonArmor SAPPHIRE = new DragonArmor(DragonColor.SAPPHIRE, IafItems.DRAGONSCALES_SAPPHIRE);
    public static final DragonArmor SILVER = new DragonArmor(DragonColor.SILVER, IafItems.DRAGONSCALES_SILVER);
    public static final DragonArmor ELECTRIC = new DragonArmor(DragonColor.ELECTRIC, IafItems.DRAGONSCALES_ELECTRIC);
    public static final DragonArmor AMETHYST = new DragonArmor(DragonColor.AMETHYST, IafItems.DRAGONSCALES_AMETHYST);
    public static final DragonArmor COPPER = new DragonArmor(DragonColor.COPPER, IafItems.DRAGONSCALES_COPPER);
    public static final DragonArmor BLACK = new DragonArmor(DragonColor.BLACK, IafItems.DRAGONSCALES_BLACK);
    private final DragonColor color;
    private final Supplier<Item> repairItem;
    public CustomArmorMaterial material;
    public RegistrySupplier<Item> helmet, chestplate, leggings, boots;
    public CustomArmorMaterial armorMaterial;

    public DragonArmor(DragonColor color, Supplier<Item> repairItem) {
        this.color = color;
        this.repairItem = repairItem;
        ARMORS.add(this);
    }

    public static void initArmors() {
        for (int i = 0; i < ARMORS.size(); i++) {
            DragonArmor value = ARMORS.get(i);
            value.armorMaterial = new IafArmorMaterial(IdUtil.build(IceAndFire.MOD_ID, "armor_dragon_scales" + (i + 1)), 36, new int[]{5, 7, 9, 5}, 15, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 2);
            String sub = "armor_" + value.color.name().toLowerCase(Locale.ROOT);

            value.helmet = IafItems.register(sub + "_helmet", () -> new ItemScaleArmor(value.color, value, value.armorMaterial, ArmorItem.Type.HELMET));
            value.chestplate = IafItems.register(sub + "_chestplate", () -> new ItemScaleArmor(value.color, value, value.armorMaterial, ArmorItem.Type.CHESTPLATE));
            value.leggings = IafItems.register(sub + "_leggings", () -> new ItemScaleArmor(value.color, value, value.armorMaterial, ArmorItem.Type.LEGGINGS));
            value.boots = IafItems.register(sub + "_boots", () -> new ItemScaleArmor(value.color, value, value.armorMaterial, ArmorItem.Type.BOOTS));
        }
    }

    public static List<DragonArmor> values() {
        return ImmutableList.copyOf(ARMORS);
    }

    public Item getScaleItem() {
        return this.repairItem.get();
    }

    public DragonColor getColor() {
        return this.color;
    }
}
