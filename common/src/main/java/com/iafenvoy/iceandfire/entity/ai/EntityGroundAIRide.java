package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.util.IGroundMount;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class EntityGroundAIRide<T extends MobEntity & IGroundMount> extends Goal {
    private final T dragon;

    public EntityGroundAIRide(T dragon) {
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
        this.dragon = dragon;
    }

    @Override
    public boolean canStart() {
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
        // LivingEntity's controlled movement handles input on the authoritative instance.
    }
}
