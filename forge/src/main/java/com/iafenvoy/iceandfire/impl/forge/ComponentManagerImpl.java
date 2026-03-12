package com.iafenvoy.iceandfire.impl.forge;

import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.iceandfire.data.component.PortalData;
import com.iafenvoy.iceandfire.forge.component.EntityDataProvider;
import com.iafenvoy.iceandfire.forge.component.EntityDataStorage;
import com.iafenvoy.iceandfire.forge.component.PortalDataProvider;
import com.iafenvoy.iceandfire.forge.component.PortalDataStorage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ComponentManagerImpl {
    public static IafEntityData getIafEntityData(LivingEntity living) {
        return living.getCapability(EntityDataProvider.CAPABILITY).orElse(new EntityDataStorage(living)).data();
    }

    public static PortalData getPortalData(PlayerEntity player) {
        return player.getCapability(PortalDataProvider.CAPABILITY).orElse(new PortalDataStorage(player)).data();
    }
}
