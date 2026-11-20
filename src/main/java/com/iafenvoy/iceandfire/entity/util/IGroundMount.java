package com.iafenvoy.iceandfire.entity.util;

import net.minecraft.world.entity.player.Player;

public interface IGroundMount {
    Player getRidingPlayer();

    double getRideSpeedModifier();
}
