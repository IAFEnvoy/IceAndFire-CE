package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.HippogryphEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.HitResult;
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
    private int flightTicks;

    public HippogryphAIWanderGoal(HippogryphEntity creatureIn, double speedIn) {
        this.hippo = creatureIn;
        this.speed = speedIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.hippo.canMove())
            return false;
        if (this.hippo.isHovering())
            return false;
        if (this.hippo.isFlying())
            return this.findFlightTarget();
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
        if (!this.hippo.canMove() || this.hippo.isHovering()) return false;
        if (this.hippo.isFlying())
            return !this.hippo.doesWantToLand() && this.flightTicks > 0 && !this.hippo.horizontalCollision
                    && this.hippo.distanceToSqr(this.xPosition, this.yPosition, this.zPosition) > 2;
        return !this.hippo.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.flightTicks = 100;
        if (!this.hippo.isFlying())
            this.hippo.getNavigation().moveTo(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    private boolean findFlightTarget() {
        if (this.hippo.doesWantToLand()) return false;
        for (int attempt = 0; attempt < 8; attempt++) {
            int x = this.hippo.getBlockX() + this.hippo.getRandom().nextInt(25) - 12;
            int z = this.hippo.getBlockZ() + this.hippo.getRandom().nextInt(25) - 12;
            var level = this.hippo.level();
            var column = new BlockPos(x, this.hippo.getBlockY(), z);
            if (!level.hasChunkAt(column)) continue;
            int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            double y = Mth.clamp(this.hippo.getY() + this.hippo.getRandom().nextInt(7) - 3, surface + 4, surface + 12);
            if (Math.abs(y - this.hippo.getY()) > 12 || y + this.hippo.getBbHeight() >= level.getMaxY()) continue;
            var target = new Vec3(x + 0.5D, y, z + 0.5D);
            var hit = level.clip(new ClipContext(
                    this.hippo.position(), target,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.hippo));
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
            this.hippo.getMoveControl().setWantedPosition(this.xPosition, this.yPosition, this.zPosition, this.speed);
        }
    }

    @Override
    public void stop() {
        this.hippo.getNavigation().stop();
        if (this.hippo.isFlying())
            this.hippo.getMoveControl().setWantedPosition(this.hippo.getX(), this.hippo.getY(), this.hippo.getZ(), 0);
    }
}
