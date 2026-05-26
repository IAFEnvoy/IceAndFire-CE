package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DragonAIReturnToRoostGoal extends Goal {
    private final DragonBaseEntity dragon;

    public DragonAIReturnToRoostGoal(DragonBaseEntity entityIn, double movementSpeedIn) {
        this.dragon = entityIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.dragon.canMove() || !this.dragon.lookingForRoostAIFlag
                || (this.dragon.getTarget() != null && this.dragon.getTarget().isAlive())) return false;
        this.dragon.getRestrictCenter();
        return DragonUtils.isInHomeDimension(this.dragon) && this.dragon.getDistanceSquared(Vec3.atCenterOf(this.dragon.getRestrictCenter())) > this.dragon.getBbWidth()
                * this.dragon.getBbWidth();
    }

    @Override
    public void tick() {
        this.dragon.getRestrictCenter();
        final double dist = Math.sqrt(this.dragon.getDistanceSquared(Vec3.atCenterOf(this.dragon.getRestrictCenter())));
        final double xDist = Math.abs(this.dragon.getX() - this.dragon.getRestrictCenter().getX() - 0.5F);
        final double zDist = Math.abs(this.dragon.getZ() - this.dragon.getRestrictCenter().getZ() - 0.5F);
        final double xzDist = Math.sqrt(xDist * xDist + zDist * zDist);

        if (dist < this.dragon.getBbWidth()) {
            this.dragon.setFlying(false);
            this.dragon.setHovering(false);
            this.dragon.getNavigation().moveTo(this.dragon.getRestrictCenter().getX(),
                    this.dragon.getRestrictCenter().getY(), this.dragon.getRestrictCenter().getZ(), 1.0F);
        } else {
            double yAddition = 15 + this.dragon.getRandom().nextInt(3);
            if (xzDist < 40) {
                yAddition = 0;
                if (this.dragon.onGround()) {
                    this.dragon.setFlying(false);
                    this.dragon.setHovering(false);
                    this.dragon.flightManager.setFlightTarget(Vec3.upFromBottomCenterOf(this.dragon.getRestrictCenter(), yAddition));
                    this.dragon.getNavigation().moveTo(this.dragon.getRestrictCenter().getX(), this.dragon.getRestrictCenter().getY(), this.dragon.getRestrictCenter().getZ(), 1.0F);
                    return;
                }
            }
            if (!this.dragon.isFlying() && !this.dragon.isHovering() && xzDist > 40)
                this.dragon.setHovering(true);
            if (this.dragon.isFlying()) {
                this.dragon.flightManager.setFlightTarget(Vec3.upFromBottomCenterOf(this.dragon.getRestrictCenter(), yAddition));
                this.dragon.getNavigation().moveTo(this.dragon.getRestrictCenter().getX(), yAddition + this.dragon.getRestrictCenter().getY(), this.dragon.getRestrictCenter().getZ(), 1F);
            }
            this.dragon.flyTicks = 0;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }
}
