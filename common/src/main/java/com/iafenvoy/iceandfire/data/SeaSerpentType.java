package com.iafenvoy.iceandfire.data;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.SeaSerpentScaleItem;
import com.iafenvoy.iceandfire.item.armor.SeaSerpentArmorItem;
import com.iafenvoy.iceandfire.item.block.SeaSerpentScalesBlock;
import com.iafenvoy.iceandfire.registry.IafArmorMaterials;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.uranus.util.function.MemorizeSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Locale;

public class SeaSerpentType {
    private final String name;
    private final Formatting color;
    //FIXME:: Remove this
    public RegistrySupplier<Item> scale, helmet, chestplate, leggings, boots;

    public SeaSerpentType(String name, Formatting color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public Formatting getColor() {
        return this.color;
    }

    public Identifier getTexture(boolean blink) {
        return Identifier.of(IceAndFire.MOD_ID, String.format("textures/entity/seaserpent/seaserpent_%s%s.png", this.name, blink ? "_blink" : ""));
    }

    public static List<SeaSerpentType> values() {
        return IafRegistries.SEA_SERPENT_TYPE.stream().toList();
    }

    public static void initArmors() {
        for (SeaSerpentType type : SeaSerpentType.values()) {
            IafBlocks.register(String.format(Locale.ROOT, "sea_serpent_scale_block_%s", type.name), () -> new SeaSerpentScalesBlock(type.name, type.color));
            RegistrySupplier<ArmorMaterial> material = IafArmorMaterials.register(String.format(Locale.ROOT, "sea_serpent_scales_%s", type.name), new int[]{ 4, 7, 8, 4 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 2.5F, new MemorizeSupplier<>(() -> Ingredient.ofItems(type.scale.get())));
            type.scale = IafItems.registerItem(String.format(Locale.ROOT, "sea_serpent_scales_%s", type.name), () -> new SeaSerpentScaleItem(type));
            type.helmet = IafItems.registerArmor(String.format(Locale.ROOT, "tide_%s_helmet", type.name), () -> new SeaSerpentArmorItem(type, material, ArmorItem.Type.HELMET));
            type.chestplate = IafItems.registerArmor(String.format(Locale.ROOT, "tide_%s_chestplate", type.name), () -> new SeaSerpentArmorItem(type, material, ArmorItem.Type.CHESTPLATE));
            type.leggings = IafItems.registerArmor(String.format(Locale.ROOT, "tide_%s_leggings", type.name), () -> new SeaSerpentArmorItem(type, material, ArmorItem.Type.LEGGINGS));
            type.boots = IafItems.registerArmor(String.format(Locale.ROOT, "tide_%s_boots", type.name), () -> new SeaSerpentArmorItem(type, material, ArmorItem.Type.BOOTS));
        }
    }
}
