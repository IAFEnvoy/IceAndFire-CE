package com.iafenvoy.iceandfire.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class EntityAIWatchClosestIgnoreRiderGoal extends LookAtPlayerGoal {
    LivingEntity entity;

    public EntityAIWatchClosestIgnoreRiderGoal(Mob entity, Class<? extends LivingEntity> type, float dist) {
        super(entity, type, dist);
    }

    public static boolean isRidingOrBeingRiddenBy(Entity first, Entity entityIn) {
        for (Entity entity : first.getPassengers()) {
            if (entity.equals(entityIn)) return true;
            if (isRidingOrBeingRiddenBy(entity, entityIn)) return true;
        }
        return false;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.lookAt != null && isRidingOrBeingRiddenBy(this.lookAt, this.entity);
    }
}
