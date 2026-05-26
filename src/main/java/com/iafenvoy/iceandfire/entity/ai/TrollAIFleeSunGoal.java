package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.TrollEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TrollAIFleeSunGoal extends Goal {
    private final TrollEntity troll;
    private final double movementSpeed;
    private final Level world;
    private double shelterX;
    private double shelterY;
    private double shelterZ;

    public TrollAIFleeSunGoal(TrollEntity theCreatureIn, double movementSpeedIn) {
        this.troll = theCreatureIn;
        this.movementSpeed = movementSpeedIn;
        this.world = theCreatureIn.level();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.world.isDay()) return false;
        else if (!this.world.canSeeSky(BlockPos.containing(this.troll.getBlockX(), this.troll.getBoundingBox().minY, this.troll.getBlockZ())))
            return false;
        else {
            Vec3 Vector3d = this.findPossibleShelter();
            if (Vector3d == null) return false;
            else {
                this.shelterX = Vector3d.x;
                this.shelterY = Vector3d.y;
                this.shelterZ = Vector3d.z;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress Goal should continue executing
     */
    @Override
    public boolean canContinueToUse() {
        return !this.troll.getNavigation().isDone();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void start() {
        this.troll.getNavigation().moveTo(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    private Vec3 findPossibleShelter() {
        RandomSource random = this.troll.getRandom();
        BlockPos blockpos = BlockPos.containing(this.troll.getBlockX(), this.troll.getBoundingBox().minY, this.troll.getBlockZ());
        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
            if (!this.world.canSeeSky(blockpos1) && this.troll.getWalkTargetValue(blockpos1) < 0.0F)
                return new Vec3(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
        }
        return null;
    }
}