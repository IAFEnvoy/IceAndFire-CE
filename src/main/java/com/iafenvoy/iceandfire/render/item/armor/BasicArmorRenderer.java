package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.iceandfire.render.model.armor.ArmorMeshParts;
import com.iafenvoy.uranus.client.render.armor.ArmorModelBase;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BasicArmorRenderer implements IArmorRendererBase {
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    );

    private final Map<EquipmentSlot, ArmorModelBase> outerModels = new EnumMap<>(EquipmentSlot.class);
    private final Map<EquipmentSlot, ArmorModelBase> innerModels = new EnumMap<>(EquipmentSlot.class);

    public BasicArmorRenderer(Boolean2ObjectFunction<MeshDefinition> meshProvider) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            this.outerModels.put(slot, ArmorMeshParts.bakeSlot(meshProvider.get(false), slot));
            this.innerModels.put(slot, ArmorMeshParts.bakeSlot(meshProvider.get(true), slot));
        }
    }

    @Override
    public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model defaultModel) {
        Map<EquipmentSlot, ArmorModelBase> models = layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS
                ? this.innerModels
                : this.outerModels;
        return models.getOrDefault(slotOf(itemStack), models.get(EquipmentSlot.CHEST));
    }

    static EquipmentSlot slotOf(ItemStack stack) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null ? equippable.slot() : EquipmentSlot.CHEST;
    }
}
