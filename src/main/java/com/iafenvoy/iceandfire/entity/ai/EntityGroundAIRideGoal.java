package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.util.IGroundMount;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class EntityGroundAIRideGoal<T extends Mob & IGroundMount> extends Goal {
    private final T dragon;

    public EntityGroundAIRideGoal(T dragon) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        this.dragon = dragon;
    }

    @Override
    public boolean canUse() {
        return this.dragon.getRidingPlayer() != null;
    }

    @Override
    public void start() {
        this.dragon.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.dragon.getNavigation().stop();
        this.dragon.setTarget(null);
        // LivingEntity's ridden movement handles input on the authoritative instance.
    }
}
