package com.iafenvoy.iceandfire.compat.jade;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.item.block.entity.EggInIceBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum DragonEggBlockComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "dragon_egg_block");
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (entityAccessor.getBlockEntity() instanceof EggInIceBlockEntity egg)
            iTooltip.add(Component.translatable("dragon_egg.hatchTime", String.valueOf((IafCommonConfig.INSTANCE.dragon.eggBornTime.getValue() - egg.age) / 20)));
    }
}
