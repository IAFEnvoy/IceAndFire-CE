package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.DeathWormEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.Predicate;

public class DeathWormAITargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final DeathWormEntity deathworm;

    public DeathWormAITargetGoal(DeathWormEntity entityIn, Class<T> classTarget, boolean checkSight, Predicate<LivingEntity> targetPredicate) {
        super(entityIn, classTarget, 20, checkSight, false, targetPredicate);
        this.deathworm = entityIn;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        boolean canUse = super.canUse();

        if (canUse && this.target != null && this.target.getType() != IafEntities.DEATH_WORM.get()) {
            if (this.target instanceof Player && !this.deathworm.isOwnedBy(this.target))
                return !this.deathworm.isTame();
            else if (this.deathworm.isOwnedBy(this.target)) return false;

            if (this.target instanceof Monster && this.deathworm.getWormAge() > 2) {
                if (this.target instanceof PathfinderMob)
                    return this.deathworm.getWormAge() > 3;
                return true;
            }
        }
        return false;
    }

    @Override
    protected @NotNull AABB getTargetSearchArea(double targetDistance) {
        // Increasing the y-range too much makes it target entities in caves etc., which will be unreachable (thus no target will be set)
        return this.deathworm.getBoundingBox().inflate(targetDistance, 6, targetDistance);
    }
}