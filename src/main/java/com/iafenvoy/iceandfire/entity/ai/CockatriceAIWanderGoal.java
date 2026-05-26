package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.CockatriceEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CockatriceAIWanderGoal extends Goal {
    private final CockatriceEntity cockatrice;
    private final double speed;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private int executionChance;
    private boolean mustUpdate;

    public CockatriceAIWanderGoal(CockatriceEntity creatureIn, double speedIn) {
        this(creatureIn, speedIn, 20);
    }

    public CockatriceAIWanderGoal(CockatriceEntity creatureIn, double speedIn, int chance) {
        this.cockatrice = creatureIn;
        this.speed = speedIn;
        this.executionChance = chance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.cockatrice.canMove()) return false;
        if (this.cockatrice.getCommand() != 3 && this.cockatrice.getCommand() != 0)
            return false;
        if (!this.mustUpdate)
            if (this.cockatrice.getRandom().nextInt(this.executionChance) != 0)
                return false;
        Vec3 Vector3d = DefaultRandomPos.getPos(this.cockatrice, 10, 7);
        if (Vector3d == null) return false;
        else {
            this.xPosition = Vector3d.x;
            this.yPosition = Vector3d.y;
            this.zPosition = Vector3d.z;
            this.mustUpdate = false;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.cockatrice.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.cockatrice.getNavigation().moveTo(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    public void makeUpdate() {
        this.mustUpdate = true;
    }

    public void setExecutionChance(int newchance) {
        this.executionChance = newchance;
    }
}