package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.HippogryphEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class HippogryphAIWanderGoal extends Goal {
    private final HippogryphEntity hippo;
    private final double speed;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    public HippogryphAIWanderGoal(HippogryphEntity creatureIn, double speedIn) {
        this.hippo = creatureIn;
        this.speed = speedIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.hippo.canMove())
            return false;
        if (this.hippo.isFlying() || this.hippo.isHovering())
            return false;
        Vec3 Vector3d = DefaultRandomPos.getPos(this.hippo, 10, 0);
        if (Vector3d == null)
            return false;
        else {
            this.xPosition = Vector3d.x;
            this.yPosition = Vector3d.y + this.hippo.getRandom().nextIntBetweenInclusive(-4, 2);
            this.zPosition = Vector3d.z;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.hippo.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.hippo.getNavigation().moveTo(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }
}