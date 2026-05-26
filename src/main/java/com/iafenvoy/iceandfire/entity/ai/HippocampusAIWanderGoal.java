package com.iafenvoy.iceandfire.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

public class HippocampusAIWanderGoal extends RandomStrollGoal {
    public HippocampusAIWanderGoal(PathfinderMob creatureIn, double speedIn) {
        super(creatureIn, speedIn);
    }

    @Override
    public boolean canUse() {
        return !(this.mob instanceof TamableAnimal tameable && tameable.isOrderedToSit()) && !this.mob.isInWater() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !(this.mob instanceof TamableAnimal tameable && tameable.isOrderedToSit()) && !this.mob.isInWater() && super.canContinueToUse();
    }
}