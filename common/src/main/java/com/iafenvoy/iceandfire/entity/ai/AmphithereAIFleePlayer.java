package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityAmphithere;
import com.iafenvoy.iceandfire.util.IafMath;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class AmphithereAIFleePlayer extends Goal {
    private final SearchCooldown searchCooldown = new SearchCooldown();
    protected final EntityAmphithere entity;
    private final double farSpeed;
    private final double nearSpeed;
    private final float avoidDistance;
    protected PlayerEntity closestLivingEntity;
    private Path path;

    private List<PlayerEntity> list = IafMath.emptyPlayerEntityList;

    public AmphithereAIFleePlayer(EntityAmphithere entityIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
        this.entity = entityIn;
        this.avoidDistance = avoidDistanceIn;
        this.farSpeed = farSpeedIn;
        this.nearSpeed = nearSpeedIn;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.entity.isFlying() && !this.entity.isTamed()) {
            if (!this.searchCooldown.shouldSearch(this.entity.getWorld().getTime(), this.entity.getId())) return false;
            this.list = this.entity.getWorld().getEntitiesByClass(PlayerEntity.class, this.entity.getBoundingBox().expand(this.avoidDistance, 6D, this.avoidDistance), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
            if (this.list.isEmpty()) return false;

            this.closestLivingEntity = this.list.get(0);
            Vec3d Vector3d = NoPenaltyTargeting.findFrom(this.entity, 20, 7, new Vec3d(this.closestLivingEntity.getX(), this.closestLivingEntity.getY(), this.closestLivingEntity.getZ()));

            if (Vector3d == null) return false;
            else if (this.closestLivingEntity.squaredDistanceTo(Vector3d) < this.closestLivingEntity.squaredDistanceTo(this.entity))
                return false;
            else {
                this.path = this.entity.getNavigation().findPathTo(Vector3d.x, Vector3d.y, Vector3d.z, 0);
                return this.path != null;
            }
        } else {
            this.list = IafMath.emptyPlayerEntityList;
            return false;
        }
    }

    @Override
    public boolean shouldContinue() {
        return !this.entity.getNavigation().isIdle();
    }

    @Override
    public void start() {
        this.entity.getNavigation().startMovingAlong(this.path, this.farSpeed);
    }

    @Override
    public void stop() {
        this.closestLivingEntity = null;
    }

    @Override
    public void tick() {
        this.entity.getNavigation().setSpeed(this.entity.squaredDistanceTo(this.closestLivingEntity) < 49.0D ? this.nearSpeed : this.farSpeed);
    }
}
