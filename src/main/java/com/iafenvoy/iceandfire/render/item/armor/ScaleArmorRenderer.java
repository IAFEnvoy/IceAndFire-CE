package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.armor.DragonScaleArmorItem;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.render.model.armor.ArmorMeshParts;
import com.iafenvoy.iceandfire.render.model.armor.FireDragonScaleArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.IceDragonScaleArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.LightningDragonScaleArmorModel;
import com.iafenvoy.uranus.client.render.armor.ArmorModelBase;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScaleArmorRenderer implements IArmorRendererBase {
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    );
    private static final Map<DragonType, Map<EquipmentSlot, ArmorModelBase>> OUTER_BY_TYPE = new LinkedHashMap<>();
    private static final Map<DragonType, Map<EquipmentSlot, ArmorModelBase>> INNER_BY_TYPE = new LinkedHashMap<>();

    @Override
    public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model defaultModel) {
        if (!(itemStack.getItem() instanceof DragonScaleArmorItem scaleArmor)) {
            return defaultModel;
        }
        Map<DragonType, Map<EquipmentSlot, ArmorModelBase>> byType =
                layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS ? INNER_BY_TYPE : OUTER_BY_TYPE;
        Map<EquipmentSlot, ArmorModelBase> models = byType.get(scaleArmor.getColor().getType());
        if (models == null) {
            return defaultModel;
        }
        ArmorModelBase model = models.get(BasicArmorRenderer.slotOf(itemStack));
        return model != null ? model : defaultModel;
    }

    public static void register(DragonType type, Boolean2ObjectFunction<MeshDefinition> meshProvider) {
        Map<EquipmentSlot, ArmorModelBase> outer = new EnumMap<>(EquipmentSlot.class);
        Map<EquipmentSlot, ArmorModelBase> inner = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            outer.put(slot, ArmorMeshParts.bakeSlot(meshProvider.get(false), slot));
            inner.put(slot, ArmorMeshParts.bakeSlot(meshProvider.get(true), slot));
        }
        OUTER_BY_TYPE.put(type, outer);
        INNER_BY_TYPE.put(type, inner);
    }

    static {
        register(IafDragonTypes.FIRE, inner -> FireDragonScaleArmorModel.createMesh(ArmorMeshParts.deformation(inner), 0.0F));
        register(IafDragonTypes.ICE, inner -> IceDragonScaleArmorModel.createMesh(ArmorMeshParts.deformation(inner), 0.0F));
        register(IafDragonTypes.LIGHTNING, inner -> LightningDragonScaleArmorModel.createMesh(ArmorMeshParts.deformation(inner), 0.0F));
    }
}
