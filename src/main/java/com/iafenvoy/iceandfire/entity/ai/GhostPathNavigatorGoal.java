package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.GhostEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;

public class GhostPathNavigatorGoal extends FlyingPathNavigation {
    public final GhostEntity ghost;

    public GhostPathNavigatorGoal(GhostEntity entityIn, Level worldIn) {
        super(entityIn, worldIn);
        this.ghost = entityIn;
    }

    @Override
    public boolean moveTo(Entity entityIn, double speedIn) {
        this.ghost.getMoveControl().setWantedPosition(entityIn.getX(), entityIn.getY(), entityIn.getZ(), speedIn);
        return true;
    }

    @Override
    public boolean moveTo(double x, double y, double z, double speedIn) {
        this.ghost.getMoveControl().setWantedPosition(x, y, z, speedIn);
        return true;
    }
}
