package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class DragonAIWatchClosestGoal extends LookAtPlayerGoal {
    public DragonAIWatchClosestGoal(PathfinderMob LivingEntityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance) {
        super(LivingEntityIn, watchTargetClass, maxDistance);
    }

    @Override
    public boolean canUse() {
        if (this.mob instanceof DragonBaseEntity && ((DragonBaseEntity) this.mob).getAnimation() == DragonBaseEntity.ANIMATION_SHAKEPREY)
            return false;
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob instanceof DragonBaseEntity && !((DragonBaseEntity) this.mob).canMove())
            return false;
        return super.canContinueToUse();
    }
}
