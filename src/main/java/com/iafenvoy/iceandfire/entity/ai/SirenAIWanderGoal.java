package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.SirenEntity;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

public class SirenAIWanderGoal extends RandomStrollGoal {
    private final SirenEntity siren;

    public SirenAIWanderGoal(SirenEntity creatureIn, double speedIn) {
        super(creatureIn, speedIn);
        this.siren = creatureIn;
    }

    @Override
    public boolean canUse() {
        return !this.siren.isInWater() && !this.siren.isSinging() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.siren.isInWater() && !this.siren.isSinging() && super.canContinueToUse();
    }
}