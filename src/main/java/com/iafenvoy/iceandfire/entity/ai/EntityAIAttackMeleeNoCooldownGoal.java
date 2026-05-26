package com.iafenvoy.iceandfire.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class EntityAIAttackMeleeNoCooldownGoal extends MeleeAttackGoal {
    public EntityAIAttackMeleeNoCooldownGoal(PathfinderMob creature, double speed, boolean memory) {
        super(creature, speed, memory);
    }

    @Override
    public void tick() {
        // TODO: investigate why the goal is even running when the attack target is null
        // Probably has something to do with the goal switching
        if (this.mob.getTarget() != null)
            super.tick();
    }
}
