package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.uranus.object.item.CustomArmorMaterial;
import net.minecraft.item.ArmorItem;
import net.minecraft.sound.SoundEvent;

public class IafArmorMaterial extends CustomArmorMaterial {
    protected static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    private final int maxDamageFactor;
    private final boolean cosmetic;

    public IafArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness) {
        this(name, durability, damageReduction, encantability, sound, toughness, false);
    }

    public IafArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness, boolean cosmetic) {
        super(name, durability, damageReduction, encantability, sound, toughness, 0);
        this.maxDamageFactor = durability;
        this.cosmetic = cosmetic;
    }

    //1.20.1 has no unbreakable flag on Item.Settings and Item#getMaxDamage is final, so the only way to make an
    //armour item unbreakable is to give its material 0 durability: ArmorItem then stores 0 max damage, and vanilla
    //refuses to damage a stack whose max damage is 0 (the stack size stays 1 either way).
    protected boolean isCosmeticUnbreakable() {
        return this.cosmetic && IafCommonConfig.INSTANCE.armors.cosmeticArmorUnbreakable.getValue();
    }

    @Override
    public int getDurability(ArmorItem.Type slotIn) {
        if (this.isCosmeticUnbreakable()) return 0;
        return MAX_DAMAGE_ARRAY[slotIn.getEquipmentSlot().getEntitySlotId()] * this.maxDamageFactor;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}
