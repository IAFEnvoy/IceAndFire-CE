package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.SeaSerpentType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SeaSerpentArmorItem extends Item {
    public final SeaSerpentType armorType;
    private final ArmorType type;

    public SeaSerpentArmorItem(SeaSerpentType armorType, Holder<ArmorMaterial> material, ArmorType type) {
        super(new Properties().humanoidArmor(material.value(), type)
                .overrideDescription("item." + IceAndFire.MOD_ID + ".sea_serpent_" + type.getName()));
        this.armorType = armorType;
        this.type = type;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (entity instanceof Player player && slot == this.type.getSlot()) {
            int headMod = player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof SeaSerpentArmorItem ? 1 : 0;
            int chestMod = player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof SeaSerpentArmorItem ? 1 : 0;
            int legMod = player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof SeaSerpentArmorItem ? 1 : 0;
            int footMod = player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof SeaSerpentArmorItem ? 1 : 0;
            int modifier = headMod + chestMod + legMod + footMod - 1;
            if (modifier >= 0) {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 50, 0, false, false));
                if (player.isInWaterOrRain())
                    player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 50, modifier, false, false));
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        tooltip.accept(Component.translatable("sea_serpent." + this.armorType.getName()).withStyle(this.armorType.getColor()));
        tooltip.accept(Component.translatable("item.iceandfire.sea_serpent_armor.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.sea_serpent_armor.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
