package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.SirenEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AquaticAIGetInWaterGoal extends Goal {
    private final Mob creature;
    private final double movementSpeed;
    private final Level world;
    private double shelterX;
    private double shelterY;
    private double shelterZ;

    public AquaticAIGetInWaterGoal(Mob theCreatureIn, double movementSpeedIn) {
        this.creature = theCreatureIn;
        this.movementSpeed = movementSpeedIn;
        this.world = theCreatureIn.level();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    protected boolean isAttackerInWater() {
        return this.creature.getTarget() != null && !this.creature.getTarget().isInWater();
    }

    @Override
    public boolean canUse() {
        if (this.creature.isVehicle() || this.creature instanceof TamableAnimal tameable && tameable.isTame()
                || this.creature.isInWater() || this.isAttackerInWater() || this.creature instanceof SirenEntity siren
                && (siren.isSinging() || siren.wantsToSing()))
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

    public Vec3 findPossibleShelter() {
        return this.findPossibleShelter(10);
    }

    protected Vec3 findPossibleShelter(int xz) {
        RandomSource random = this.creature.getRandom();
        BlockPos blockpos = BlockPos.containing(this.creature.getBlockX(), this.creature.getBoundingBox().minY, this.creature.getBlockZ());
        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.offset(random.nextInt(xz * 2) - xz, random.nextInt(3 * 2) - 3, random.nextInt(xz * 2) - xz);
            if (this.world.getBlockState(blockpos1).is(Blocks.WATER))
                return new Vec3(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
        }
        return null;
    }
}
