package com.iafenvoy.iceandfire.compat.jade;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonEggEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

public enum DragonEggEntityComponentProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public Identifier getUid() {
        return Identifier.of(IceAndFire.MOD_ID, "dragon_egg_entity");
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (entityAccessor.getEntity() instanceof DragonEggEntity egg)
            iTooltip.add(Text.translatable("dragon_egg.hatchTime", String.valueOf((IafCommonConfig.INSTANCE.dragon.eggBornTime.getValue() - egg.getDragonAge()) / 20)));
    }
}
