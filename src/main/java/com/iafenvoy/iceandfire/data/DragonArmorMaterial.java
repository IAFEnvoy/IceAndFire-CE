package com.iafenvoy.iceandfire.data;

public record DragonArmorMaterial(String name, double protection, boolean fireProof, boolean dragonSteel) {
    public static final DragonArmorMaterial IRON = new DragonArmorMaterial("iron", 1.5, false, false);
    public static final DragonArmorMaterial COPPER = new DragonArmorMaterial("copper", 2, false, false);
    public static final DragonArmorMaterial SILVER = new DragonArmorMaterial("silver", 3, false, false);
    public static final DragonArmorMaterial GOLD = new DragonArmorMaterial("gold", 2, false, false);
    public static final DragonArmorMaterial DIAMOND = new DragonArmorMaterial("diamond", 5, false, false);
    public static final DragonArmorMaterial NETHERITE = new DragonArmorMaterial("netherite", 7, true, false);
    public static final DragonArmorMaterial DRAGON_STEEL_FIRE = new DragonArmorMaterial("dragon_steel_fire", 10, false, true);
    public static final DragonArmorMaterial DRAGON_STEEL_ICE = new DragonArmorMaterial("dragon_steel_ice", 10, false, true);
    public static final DragonArmorMaterial DRAGON_STEEL_LIGHTNING = new DragonArmorMaterial("dragon_steel_lightning", 10, false, true);
}
