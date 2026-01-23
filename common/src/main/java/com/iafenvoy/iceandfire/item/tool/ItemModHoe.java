package com.iafenvoy.iceandfire.item.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class ItemModHoe extends HoeItem implements DragonSteelOverrides<ItemModHoe> {
    private Multimap<EntityAttribute, EntityAttributeModifier> dragonsteelModifiers;

    public ItemModHoe(ToolMaterial toolmaterial) {
        super(toolmaterial, -(int) toolmaterial.getAttackDamage(), -3, new Settings());
    }

    @Override
    @Deprecated
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND && this.isDragonSteel(this.getMaterial()) ? this.bakeDragonsteel() : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    @Deprecated
    public Multimap<EntityAttribute, EntityAttributeModifier> bakeDragonsteel() {
        if (this.getMaterial().getAttackDamage() != IafCommonConfig.INSTANCE.armors.dragonSteelBaseDamage.getValue().floatValue() || this.dragonsteelModifiers == null) {
            ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 1F, EntityAttributeModifier.Operation.ADDITION));
            builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -3F, EntityAttributeModifier.Operation.ADDITION));
            this.dragonsteelModifiers = builder.build();
        }
        return this.dragonsteelModifiers;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        if (this.isDragonSteel(this.getMaterial())) {
            return IafCommonConfig.INSTANCE.armors.dragonSteelBaseDurability.getValue();
        } else {
            return this.getMaterial().getDurability();
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        this.hurtEnemy(this, stack, target, attacker);
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        super.appendTooltip(stack, worldIn, tooltip, flagIn);
        this.appendHoverText(this.getMaterial(), stack, worldIn, tooltip, flagIn);
    }
}
