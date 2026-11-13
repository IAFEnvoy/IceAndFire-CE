package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityHippogryph;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.Heightmap;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class HippogryphAIWander extends Goal {
    private final EntityHippogryph hippo;
    private final double speed;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private int flightTicks;

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
        if (this.hippo.isFlying())
            return this.findFlightTarget();
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
        if (!this.hippo.canMove() || this.hippo.isHovering()) return false;
        if (this.hippo.isFlying())
            return !this.hippo.doesWantToLand() && this.flightTicks > 0 && !this.hippo.horizontalCollision
                    && this.hippo.squaredDistanceTo(this.xPosition, this.yPosition, this.zPosition) > 2;
        return !this.hippo.getNavigation().isIdle();
    }

    @Override
    public void start() {
        this.flightTicks = 100;
        if (!this.hippo.isFlying())
            this.hippo.getNavigation().startMovingTo(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    private boolean findFlightTarget() {
        if (this.hippo.doesWantToLand()) return false;
        for (int attempt = 0; attempt < 8; attempt++) {
            int x = this.hippo.getBlockX() + this.hippo.getRandom().nextInt(25) - 12;
            int z = this.hippo.getBlockZ() + this.hippo.getRandom().nextInt(25) - 12;
            var level = this.hippo.getWorld();
            var column = new BlockPos(x, this.hippo.getBlockY(), z);
            if (!level.isChunkLoaded(column)) continue;
            int surface = level.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            double y = MathHelper.clamp(this.hippo.getY() + this.hippo.getRandom().nextInt(7) - 3, surface + 4, surface + 12);
            if (Math.abs(y - this.hippo.getY()) > 12 || y + this.hippo.getHeight() >= level.getTopY()) continue;
            var target = new Vec3d(x + 0.5D, y, z + 0.5D);
            var hit = level.raycast(new RaycastContext(
                    this.hippo.getPos(), target,
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this.hippo));
            if (hit.getType() != HitResult.Type.MISS) continue;
            this.xPosition = target.x;
            this.yPosition = target.y;
            this.zPosition = target.z;
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        if (this.hippo.isFlying() && !this.hippo.doesWantToLand()) {
            this.flightTicks--;
            this.hippo.getMoveControl().moveTo(this.xPosition, this.yPosition, this.zPosition, this.speed);
        }
    }

    @Override
    public void stop() {
        this.hippo.getNavigation().stop();
        if (this.hippo.isFlying())
            this.hippo.getMoveControl().moveTo(this.hippo.getX(), this.hippo.getY(), this.hippo.getZ(), 0);
    }
}
