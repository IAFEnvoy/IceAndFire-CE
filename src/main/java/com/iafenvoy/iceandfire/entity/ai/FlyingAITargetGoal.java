package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.SeaSerpentEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class FlyingAITargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    public FlyingAITargetGoal(Mob creature, Class<T> classTarget, boolean checkSight) {
        super(creature, classTarget, checkSight);
    }

    public FlyingAITargetGoal(Mob creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
        super(creature, classTarget, checkSight, onlyNearby);
    }

    public FlyingAITargetGoal(Mob creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, final Predicate<LivingEntity> targetSelector) {
        super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
    }

    @Override
    protected @NotNull AABB getTargetSearchArea(double targetDistance) {
        return this.mob.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
    }

    @Override
    public boolean canUse() {
        if (this.mob instanceof SeaSerpentEntity seaSerpent && (seaSerpent.isJumpingOutOfWater() || !this.mob.isInWater()))
            return false;
        return super.canUse();
    }
}
