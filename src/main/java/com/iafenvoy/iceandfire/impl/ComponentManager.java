package com.iafenvoy.iceandfire.impl;

import com.iafenvoy.iceandfire.data.component.ChainData;
import com.iafenvoy.iceandfire.data.component.ChickenData;
import com.iafenvoy.iceandfire.data.component.MiscData;
import com.iafenvoy.iceandfire.data.component.PortalData;
import com.iafenvoy.iceandfire.registry.IafAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ComponentManager {
    public static ChainData getChainData(LivingEntity living) {
        return living.getData(IafAttachments.CHAIN_DATA.get());
    }

    public static ChickenData getChickenData(LivingEntity living) {
        return living.getData(IafAttachments.CHICKEN_DATA.get());
    }

    public static MiscData getMiscData(LivingEntity living) {
        return living.getData(IafAttachments.MISC_DATA.get());
    }

    public static PortalData getPortalData(Player player) {
        return player.getData(IafAttachments.PORTAL_DATA.get());
    }
}
