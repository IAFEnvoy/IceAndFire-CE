package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.uranus.client.render.armor.ArmorModelBase;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.item.ItemStack;

public class BasicArmorRenderer implements IArmorRendererBase {
    private final Boolean2ObjectFunction<ArmorModelBase> modelProvider;

    public BasicArmorRenderer(Boolean2ObjectFunction<ArmorModelBase> modelProvider) {
        this.modelProvider = modelProvider;
    }

    @Override
    public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model defaultModel) {
        return this.modelProvider.get(layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS);
    }
}
