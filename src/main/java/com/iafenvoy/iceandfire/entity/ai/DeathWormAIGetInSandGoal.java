package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.DeathWormEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DeathWormAIGetInSandGoal extends Goal {
    private final DeathWormEntity creature;
    private final double movementSpeed;
    private final Level world;
    private double shelterX;
    private double shelterY;
    private double shelterZ;

    public DeathWormAIGetInSandGoal(DeathWormEntity theCreatureIn, double movementSpeedIn) {
        this.creature = theCreatureIn;
        this.movementSpeed = movementSpeedIn;
        this.world = theCreatureIn.level();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.creature.isVehicle() || this.creature.isInSand() || this.creature.getTarget() != null && !this.creature.getTarget().isInWater() || this.creature.targetItemsGoal.targetEntity != null)
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
        return !this.creature.getNavigation().isDone();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void start() {
        this.creature.getNavigation().moveTo(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    private Vec3 findPossibleShelter() {
        RandomSource random = this.creature.getRandom();
        BlockPos blockpos = BlockPos.containing(this.creature.getBlockX(), this.creature.getBoundingBox().minY, this.creature.getBlockZ());

        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
            if (this.world.getBlockState(blockpos1).is(BlockTags.SAND))
                return new Vec3(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
        }

        return null;
    }
}