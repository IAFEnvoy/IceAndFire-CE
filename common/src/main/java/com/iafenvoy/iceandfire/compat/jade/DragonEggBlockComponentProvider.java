package com.iafenvoy.iceandfire.compat.jade;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.item.block.entity.EggInIceBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

public enum DragonEggBlockComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public Identifier getUid() {
        return Identifier.of(IceAndFire.MOD_ID, "dragon_egg_block");
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (entityAccessor.getBlockEntity() instanceof EggInIceBlockEntity egg)
            iTooltip.add(Text.translatable("dragon_egg.hatchTime", String.valueOf((IafCommonConfig.INSTANCE.dragon.eggBornTime.getValue() - egg.age) / 20)));
    }
}
