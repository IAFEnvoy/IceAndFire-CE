package com.iafenvoy.iceandfire.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ItemDragonSteelArmor extends ArmorItem implements IProtectAgainstDragonItem {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    private final ArmorMaterial material;
    private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifierMultimap;

    public ItemDragonSteelArmor(ArmorMaterial material, int renderIndex, Type slot) {
        super(material, slot, new Settings());
        this.material = material;
        this.attributeModifierMultimap = this.createAttributeMap();
    }

    //Workaround for armor attributes being registered before the config gets loaded
    private Multimap<EntityAttribute, EntityAttributeModifier> createAttributeMap() {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[this.type.getEquipmentSlot().getEntitySlotId()];
        builder.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uuid, "Armor modifier", this.material.getProtection(this.type), EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(uuid, "Armor toughness", this.material.getToughness(), EntityAttributeModifier.Operation.ADDITION));
        if (this.knockbackResistance > 0)
            builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(uuid, "Armor knockback resistance", this.knockbackResistance, EntityAttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    private Multimap<EntityAttribute, EntityAttributeModifier> getOrUpdateAttributeMap() {
        //If the armor values have changed recreate the map
        //There might be a prettier way of accomplishing this but it works
        if (this.attributeModifierMultimap.containsKey(EntityAttributes.GENERIC_ARMOR)
                && !this.attributeModifierMultimap.get(EntityAttributes.GENERIC_ARMOR).isEmpty()
                && this.attributeModifierMultimap.get(EntityAttributes.GENERIC_ARMOR).toArray()[0] instanceof EntityAttributeModifier
                && ((EntityAttributeModifier) this.attributeModifierMultimap.get(EntityAttributes.GENERIC_ARMOR).toArray()[0]).getValue() != this.getProtection()
        )
            this.attributeModifierMultimap = this.createAttributeMap();
        return this.attributeModifierMultimap;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        if (this.type != null)
            return this.getMaterial().getDurability(this.type);
        return super.getMaxUseTime(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        if (IafCommonConfig.INSTANCE.armors.dragonSteelBreathReduction.getValue())
            tooltip.add(Text.translatable("item.dragonscales_armor.desc").formatted(Formatting.GRAY));
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == this.type.getEquipmentSlot() ? this.getOrUpdateAttributeMap() : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public int getProtection() {
        if (this.material != null)
            return this.material.getProtection(this.getType());
        return super.getProtection();
    }
}
