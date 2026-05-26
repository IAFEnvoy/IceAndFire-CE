package com.iafenvoy.iceandfire.entity.util;

import net.minecraft.world.entity.player.Player;

//TODO: Fix death worm riding
public interface IGroundMount {
    Player getRidingPlayer();

    double getRideSpeedModifier();
}
