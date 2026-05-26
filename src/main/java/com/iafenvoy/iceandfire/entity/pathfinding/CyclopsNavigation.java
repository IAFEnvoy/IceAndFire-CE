package com.iafenvoy.iceandfire.entity.pathfinding;

import com.iafenvoy.iceandfire.entity.CyclopsEntity;
import com.iafenvoy.uranus.object.entity.collision.CustomCollisionsNavigator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.NotNull;

public class CyclopsNavigation extends CustomCollisionsNavigator {
    public CyclopsNavigation(CyclopsEntity LivingEntityIn, Level worldIn) {
        super(LivingEntityIn, worldIn);
    }

    @Override
    protected @NotNull PathFinder createPathFinder(int i) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanOpenDoors(true);
        this.nodeEvaluator.setCanFloat(true);
        return new PathFinder(this.nodeEvaluator, i);
    }
}