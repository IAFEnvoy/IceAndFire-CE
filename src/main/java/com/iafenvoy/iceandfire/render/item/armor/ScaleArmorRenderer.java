package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.armor.DragonScaleArmorItem;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.render.model.armor.FireDragonScaleArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.IceDragonScaleArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.LightningDragonScaleArmorModel;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScaleArmorRenderer implements IArmorRendererBase<LivingEntity> {
    private static final Map<DragonType, Boolean2ObjectFunction<HumanoidModel<LivingEntity>>> MODEL_BY_TYPE = new LinkedHashMap<>();

    @Override
    public HumanoidModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<LivingEntity> bipedEntityModel) {
        return itemStack.getItem() instanceof DragonScaleArmorItem scaleArmor ? MODEL_BY_TYPE.getOrDefault(scaleArmor.getColor().getType(), b -> null).get(armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.HEAD) : null;
    }

    public static void register(DragonType type, Boolean2ObjectFunction<HumanoidModel<LivingEntity>> model) {
        MODEL_BY_TYPE.put(type, model);
    }

    static {
        register(IafDragonTypes.FIRE, FireDragonScaleArmorModel::new);
        register(IafDragonTypes.ICE, IceDragonScaleArmorModel::new);
        register(IafDragonTypes.LIGHTNING, LightningDragonScaleArmorModel::new);
    }
}
