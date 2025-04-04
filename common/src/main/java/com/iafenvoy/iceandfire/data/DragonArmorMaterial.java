package com.iafenvoy.iceandfire.data;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.armor.ItemDragonArmor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DragonArmorMaterial {
    private static final List<DragonArmorMaterial> MATERIALS = new ArrayList<>();
    public static final DragonArmorMaterial IRON = new DragonArmorMaterial("iron", 1.5);
    public static final DragonArmorMaterial COPPER = new DragonArmorMaterial("copper", 2);
    public static final DragonArmorMaterial SILVER = new DragonArmorMaterial("silver", 3);
    public static final DragonArmorMaterial GOLD = new DragonArmorMaterial("gold", 2);
    public static final DragonArmorMaterial DIAMOND = new DragonArmorMaterial("diamond", 5);
    public static final DragonArmorMaterial NETHERITE = new DragonArmorMaterial("netherite", 7);
    public static final DragonArmorMaterial DRAGON_STEEL_FIRE = new DragonArmorMaterial("dragon_steel_fire", 10);
    public static final DragonArmorMaterial DRAGON_STEEL_ICE = new DragonArmorMaterial("dragon_steel_ice", 10);
    public static final DragonArmorMaterial DRAGON_STEEL_LIGHTNING = new DragonArmorMaterial("dragon_steel_lightning", 10);
    private final String name;
    private final double protection;

    public DragonArmorMaterial(String name, double protection) {
        this.name = name;
        this.protection = protection;
        MATERIALS.add(this);
    }

    public static Identifier getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemDragonArmor armorItem)
            return armorItem.type.getTexture(slot);
        else return Identifier.of(Identifier.DEFAULT_NAMESPACE, "missing");
    }

    public static List<DragonArmorMaterial> values() {
        return ImmutableList.copyOf(MATERIALS);
    }

    public String getId() {
        return this.name.toLowerCase(Locale.ROOT);
    }

    public Identifier getTexture(EquipmentSlot slot) {
        return switch (slot) {
            case MAINHAND, OFFHAND -> null;
            case FEET ->
                    Identifier.of(IceAndFire.MOD_ID, "textures/models/dragon_armor/armor_tail_" + this.name + ".png");
            case LEGS ->
                    Identifier.of(IceAndFire.MOD_ID, "textures/models/dragon_armor/armor_body_" + this.name + ".png");
            case CHEST ->
                    Identifier.of(IceAndFire.MOD_ID, "textures/models/dragon_armor/armor_neck_" + this.name + ".png");
            case HEAD ->
                    Identifier.of(IceAndFire.MOD_ID, "textures/models/dragon_armor/armor_head_" + this.name + ".png");
        };
    }

    public double getProtection() {
        return this.protection;
    }
}
