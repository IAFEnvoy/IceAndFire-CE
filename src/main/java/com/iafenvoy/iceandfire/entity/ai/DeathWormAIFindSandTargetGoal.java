package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.DeathWormEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

import java.util.ArrayList;
import java.util.List;

public class DeathWormAIFindSandTargetGoal extends Goal {
    private final DeathWormEntity mob;
    private int range;

    public DeathWormAIFindSandTargetGoal(DeathWormEntity mob, int range) {
        this.mob = mob;
        this.range = range;
    }

    @Override
    public boolean canUse() {
        if (this.mob.getTarget() != null) return false;

        if (!this.mob.isInSand() || this.mob.isPassenger() || this.mob.isVehicle())
            return false;
        if (this.mob.getRandom().nextFloat() < 0.5F) {
            final Path path = this.mob.getNavigation().getPath();
            if (path != null /*
             * || !this.mob.getNavigator().noPath() && !isDirectPathBetweenPoints(this.mob,
             * this.mob.getPositionVec(), new Vector3d(path.getFinalPathPoint().x,
             * path.getFinalPathPoint().y, path.getFinalPathPoint().z))
             */) this.mob.getNavigation().stop();
            if (this.mob.getNavigation().isDone() /* && !this.mob.getMoveControl().hasWanted()*/) {
                BlockPos vec3 = this.findSandTarget();
                if (vec3 != null) {
                    this.mob.getMoveControl().setWantedPosition(vec3.getX(), vec3.getY(), vec3.getZ(), 1.0);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    public BlockPos findSandTarget() {
        if (this.mob.getTarget() == null || !this.mob.getTarget().isAlive()) {
            List<BlockPos> sand = new ArrayList<>();
            if (this.mob.isTame() && this.mob.getWormHome() != null) {
                this.range = 25;
                for (int x = this.mob.getWormHome().getX() - this.range; x < this.mob.getWormHome().getX() + this.range; x++)
                    for (int y = this.mob.getWormHome().getY() - this.range; y < this.mob.getWormHome().getY() + this.range; y++)
                        for (int z = this.mob.getWormHome().getZ() - this.range; z < this.mob.getWormHome().getZ() + this.range; z++)
                            if (this.mob.level().getBlockState(new BlockPos(x, y, z)).is(BlockTags.SAND))
                                sand.add(new BlockPos(x, y, z));
            } else
                for (int x = (int) this.mob.getX() - this.range; x < (int) this.mob.getX() + this.range; x++)
                    for (int y = (int) this.mob.getY() - this.range; y < (int) this.mob.getY() + this.range; y++)
                        for (int z = (int) this.mob.getZ() - this.range; z < (int) this.mob.getZ() + this.range; z++)
                            if (this.mob.level().getBlockState(new BlockPos(x, y, z)).is(BlockTags.SAND))
                                sand.add(new BlockPos(x, y, z));
            if (!sand.isEmpty()) return sand.get(this.mob.getRandom().nextInt(sand.size()));
        } else {
            BlockPos blockpos1 = this.mob.getTarget().blockPosition();
            return new BlockPos(blockpos1.getX(), blockpos1.getY() - 1, blockpos1.getZ());
        }
        return null;
    }
}