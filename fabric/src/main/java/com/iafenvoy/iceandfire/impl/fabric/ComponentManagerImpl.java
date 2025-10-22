package com.iafenvoy.iceandfire.impl.fabric;

import com.iafenvoy.iceandfire.data.component.*;
import com.iafenvoy.iceandfire.fabric.IafAttachments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@SuppressWarnings("UnstableApiUsage")
public class ComponentManagerImpl {
    public static ChainData getChainData(LivingEntity living) {
        return living.getAttachedOrCreate(IafAttachments.CHAIN_DATA);
    }

    public static ChickenData getChickenData(LivingEntity living) {
        return living.getAttachedOrCreate(IafAttachments.CHICKEN_DATA);
    }

    public static FrozenData getFrozenData(LivingEntity living) {
        return living.getAttachedOrCreate(IafAttachments.FROZEN_DATA);
    }

    public static MiscData getMiscData(LivingEntity living) {
        return living.getAttachedOrCreate(IafAttachments.MISC_DATA);
    }

    public static PortalData getPortalData(PlayerEntity player) {
        return player.getAttachedOrCreate(IafAttachments.PORTAL_DATA);
    }

    public static SirenData getSirenData(LivingEntity living) {
        return living.getAttachedOrCreate(IafAttachments.SIREN_DATA);
    }
}
