package com.iafenvoy.iceandfire.render.item.armor;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.item.armor.DragonScaleArmorItem;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.render.model.armor.FireDragonScaleArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.IceDragonScaleArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.LightningDragonScaleArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.NetherDragonScaleArmorModel;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ScaleArmorRenderer implements IArmorRendererBase<LivingEntity> {
    @Override
    public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot armorSlot, BipedEntityModel<LivingEntity> bipedEntityModel) {
        boolean inner = armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.HEAD;
        if (itemStack.getItem() instanceof DragonScaleArmorItem scaleArmor) {
            DragonType dragonType = scaleArmor.getColor().getType();
            if (IafDragonTypes.FIRE == dragonType) return new FireDragonScaleArmorModel(inner);
            if (IafDragonTypes.ICE == dragonType) return new IceDragonScaleArmorModel(inner);
            if (IafDragonTypes.LIGHTNING == dragonType) return new LightningDragonScaleArmorModel(inner);
            if (IafDragonTypes.NETHER == dragonType) return new NetherDragonScaleArmorModel(inner);
        }
        return null;
    }
}
