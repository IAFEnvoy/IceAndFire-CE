package com.iafenvoy.iceandfire.data;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.armor.DragonScaleArmorItem;
import com.iafenvoy.iceandfire.registry.IafArmorMaterials;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.render.texture.DragonTextureProvider;
import com.iafenvoy.uranus.util.function.MemorizeSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class DragonColor {
    private final String name;
    private final ChatFormatting color;
    private final DragonType dragonType;
    private final DragonTextureProvider textureProvider;
    private final Supplier<Item> eggItem, scaleItem;
    //FIXME:: Remove this
    public DeferredHolder<Item, Item> helmet, chestplate, leggings, boots;
    //FIXME:: Remove this
    private DeferredHolder<ArmorMaterial, ArmorMaterial> material;

    public DragonColor(String name, ChatFormatting color, DragonType dragonType, Supplier<Item> eggItem, Supplier<Item> scaleItem) {
        this(name, color, dragonType, DragonTextureProvider::new, eggItem, scaleItem);
    }

    public DragonColor(String name, ChatFormatting color, DragonType dragonType, BiFunction<DragonType, String, DragonTextureProvider> textureProvider, Supplier<Item> eggItem, Supplier<Item> scaleItem) {
        this.name = name;
        this.color = color;
        this.dragonType = dragonType;
        this.dragonType.colors().add(this);
        this.textureProvider = textureProvider.apply(this.dragonType, this.name);
        this.eggItem = eggItem;
        this.scaleItem = scaleItem;
    }

    public static void initArmors() {
        for (DragonColor color : IafRegistries.DRAGON_COLOR.stream().toList()) {
            color.material = IafArmorMaterials.register("dragon_scales_" + color.name, new int[]{5, 7, 9, 5}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 2, new MemorizeSupplier<>(() -> Ingredient.of(color.scaleItem.get())));
            String sub = "armor_" + color.getName().toLowerCase(Locale.ROOT);
            color.helmet = IafItems.registerArmor(sub + "_helmet", () -> new DragonScaleArmorItem(color, ArmorItem.Type.HELMET));
            color.chestplate = IafItems.registerArmor(sub + "_chestplate", () -> new DragonScaleArmorItem(color, ArmorItem.Type.CHESTPLATE));
            color.leggings = IafItems.registerArmor(sub + "_leggings", () -> new DragonScaleArmorItem(color, ArmorItem.Type.LEGGINGS));
            color.boots = IafItems.registerArmor(sub + "_boots", () -> new DragonScaleArmorItem(color, ArmorItem.Type.BOOTS));
        }
    }

    public static DragonColor getById(String id) {
        return IafRegistries.DRAGON_COLOR.get(IceAndFire.id(id));
    }

    public DragonTextureProvider getTextureProvider() {
        return this.textureProvider;
    }

    public Item getEggItem() {
        return this.eggItem.get();
    }

    public Item getScaleItem() {
        return this.scaleItem.get();
    }

    public String getName() {
        return this.name;
    }

    public ChatFormatting getColorFormatting() {
        return this.color;
    }

    public DragonType getType() {
        return this.dragonType;
    }

    public DeferredHolder<ArmorMaterial, ArmorMaterial> getMaterial() {
        return this.material;
    }
}
