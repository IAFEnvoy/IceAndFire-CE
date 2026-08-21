package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.armor.DragonScaleArmorItem;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.render.model.armor.FireDragonScaleArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.IceDragonScaleArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.LightningDragonScaleArmorModel;
import com.iafenvoy.uranus.client.render.armor.ArmorModelBase;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScaleArmorRenderer implements IArmorRendererBase {
    private static final Map<DragonType, Boolean2ObjectFunction<ArmorModelBase>> MODEL_BY_TYPE = new LinkedHashMap<>();

    @Override
    public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model defaultModel) {
        return itemStack.getItem() instanceof DragonScaleArmorItem scaleArmor
                ? MODEL_BY_TYPE.getOrDefault(scaleArmor.getColor().getType(), b -> null).get(layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS)
                : defaultModel;
    }

    public static void register(DragonType type, Boolean2ObjectFunction<ArmorModelBase> model) {
        MODEL_BY_TYPE.put(type, model);
    }

    static {
        register(IafDragonTypes.FIRE, FireDragonScaleArmorModel::new);
        register(IafDragonTypes.ICE, IceDragonScaleArmorModel::new);
        register(IafDragonTypes.LIGHTNING, LightningDragonScaleArmorModel::new);
    }
}
