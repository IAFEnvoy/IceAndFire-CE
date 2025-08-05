package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.uranus.object.item.ToolMaterialUtil;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ToolMaterial;

public class DragonSteelToolMaterial {
    public static ToolMaterial createMaterialWithRepairItem(ItemConvertible ingredient) {
        return ToolMaterialUtil.of(IafCommonConfig.INSTANCE.armors.dragonSteelBaseDurability.getValue(), 4F, 10F, 21, 10, ingredient);
    }
}
