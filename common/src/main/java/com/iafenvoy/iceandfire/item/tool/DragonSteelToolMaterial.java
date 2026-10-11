package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.uranus.object.item.ToolMaterialUtil;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ToolMaterial;

public class DragonSteelToolMaterial {
    public static ToolMaterial createMaterialWithRepairItem(ItemConvertible ingredient) {
        // ToolMaterialUtil.of(uses, speed, attackDamageBonus, harvestLevel, enchantmentLevel, repairIngredients)
        // harvestLevel 5 is one step above netherite (4), as in the original mod
        return ToolMaterialUtil.of(IafCommonConfig.INSTANCE.armors.dragonSteelBaseDurability.getValue(), 4F, 10F, 5, 10, ingredient);
    }
}
