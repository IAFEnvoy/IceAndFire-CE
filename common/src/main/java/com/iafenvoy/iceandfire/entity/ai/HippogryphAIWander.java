package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityHippogryph;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class HippogryphAIWander extends Goal {
    private final EntityHippogryph hippo;
    private final double speed;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    public HippogryphAIWander(EntityHippogryph creatureIn, double speedIn) {
        this.hippo = creatureIn;
        this.speed = speedIn;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.hippo.canMove())
            return false;
        if (this.hippo.isHovering())
            return false;
        Vec3d Vector3d = NoPenaltyTargeting.find(this.hippo, 10, 0);
        if (Vector3d == null)
            return false;
        else {
            this.xPosition = Vector3d.x;
            this.yPosition = Vector3d.y + this.hippo.getRandom().nextBetween(-4, 2);
            this.zPosition = Vector3d.z;
            return true;
        }
    }

    @Override
    public boolean shouldContinue() {
        return !this.hippo.getNavigation().isIdle();
    }

    @Override
    public void start() {
        this.hippo.getNavigation().startMovingTo(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }
}