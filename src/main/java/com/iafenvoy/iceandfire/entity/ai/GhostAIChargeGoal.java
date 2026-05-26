package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.GhostEntity;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GhostAIChargeGoal extends Goal {
    private final GhostEntity ghost;
    public boolean firstPhase = true;
    public Vec3 moveToPos = null;
    public Vec3 offsetOf = Vec3.ZERO;

    public GhostAIChargeGoal(GhostEntity ghost) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.ghost = ghost;
    }

    @Override
    public boolean canUse() {
        return this.ghost.getTarget() != null && !this.ghost.isCharging();
    }

    @Override
    public boolean canContinueToUse() {
        return this.ghost.getTarget() != null && this.ghost.getTarget().isAlive();
    }

    @Override
    public void start() {
        this.ghost.setCharging(true);
    }

    @Override
    public void stop() {
        this.firstPhase = true;
        this.moveToPos = null;
        this.ghost.setCharging(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.ghost.getTarget();
        if (target != null) {
            if (this.ghost.getAnimation() == IAnimatedEntity.NO_ANIMATION && this.ghost.distanceTo(target) < 1.4D)
                this.ghost.setAnimation(GhostEntity.ANIMATION_HIT);
            if (this.firstPhase) {
                if (this.moveToPos == null) {
                    BlockPos moveToPos = DragonUtils.getBlockInTargetsViewGhost(this.ghost, target);
                    this.moveToPos = Vec3.atCenterOf(moveToPos);
                } else {
                    this.ghost.getNavigation().moveTo(this.moveToPos.x + 0.5D, this.moveToPos.y + 0.5D, this.moveToPos.z + 0.5D, 1F);
                    if (this.ghost.distanceToSqr(this.moveToPos.add(0.5D, 0.5D, 0.5D)) < 9D) {
                        if (this.ghost.getAnimation() == IAnimatedEntity.NO_ANIMATION)
                            this.ghost.setAnimation(GhostEntity.ANIMATION_SCARE);
                        this.firstPhase = false;
                        this.moveToPos = null;
                        this.offsetOf = target.position().subtract(this.ghost.position()).normalize();
                    }
                }
            } else {
                Vec3 fin = target.position();
                this.moveToPos = new Vec3(fin.x, target.getY() + target.getEyeHeight() / 2, fin.z);
                this.ghost.getNavigation().moveTo(target, 1.2F);
                if (this.ghost.distanceToSqr(this.moveToPos.add(0.5D, 0.5D, 0.5D)) < 3D)
                    this.stop();
            }
        }

    }
}
