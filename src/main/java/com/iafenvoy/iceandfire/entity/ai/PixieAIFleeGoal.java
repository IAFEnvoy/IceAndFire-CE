package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.PixieEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class PixieAIFleeGoal<T extends Entity> extends Goal {
    protected final PixieEntity pixie;
    private final float avoidDistance;
    private final Class<T> classToAvoid;
    protected T closestLivingEntity;
    private Vec3 hidePlace;

    private List<T> list = Collections.emptyList();

    public PixieAIFleeGoal(PixieEntity pixie, Class<T> classToAvoidIn, float avoidDistanceIn, Predicate<? super T> avoidTargetSelectorIn) {
        this.pixie = pixie;
        this.classToAvoid = classToAvoidIn;
        this.avoidDistance = avoidDistanceIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.pixie.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() || this.pixie.isTame()) {
            this.list = Collections.emptyList();
            return false;
        }

        if (this.pixie.level().getGameTime() % 4 == 0) // only update the list every 4 ticks
            this.list = this.pixie.level().getEntitiesOfClass(this.classToAvoid, this.pixie.getBoundingBox().inflate(this.avoidDistance, 3.0D, this.avoidDistance), EntitySelector.NO_SPECTATORS);

        if (this.list.isEmpty()) return false;

        this.closestLivingEntity = this.list.getFirst();
        if (this.closestLivingEntity != null) {
            Vec3 Vector3d = DefaultRandomPos.getPosAway(this.pixie, 16, 4, new Vec3(this.closestLivingEntity.getX(), this.closestLivingEntity.getY(), this.closestLivingEntity.getZ()));

            if (Vector3d == null) return false;
            else {
                Vector3d = Vector3d.add(0, 1, 0);
                this.pixie.getMoveControl().setWantedPosition(Vector3d.x, Vector3d.y, Vector3d.z, this.calculateRunSpeed());
                this.pixie.getLookControl().setLookAt(Vector3d.x, Vector3d.y, Vector3d.z, 180.0F, 20.0F);
                this.hidePlace = Vector3d;
                this.pixie.slowSpeed = true;
                return true;
            }
        }
        return false;
    }

    private double calculateRunSpeed() {
        if (this.pixie.ticksHeldItemFor > 6000) return 0.1D;
        if (this.pixie.ticksHeldItemFor > 1200) return 0.25D;
        if (this.pixie.ticksHeldItemFor > 600) return 0.25D;
        return 1D;
    }

    @Override
    public boolean canContinueToUse() {
        return this.hidePlace != null && this.pixie.distanceToSqr(this.hidePlace.add(0.5, 0.5, 0.5)) < 2;
    }

    @Override
    public void start() {
        this.pixie.getMoveControl().setWantedPosition(this.hidePlace.x, this.hidePlace.y, this.hidePlace.z, this.calculateRunSpeed());
        this.pixie.getLookControl().setLookAt(this.hidePlace.x, this.hidePlace.y, this.hidePlace.z, 180.0F, 20.0F);
    }

    @Override
    public void stop() {
        this.closestLivingEntity = null;
        this.pixie.slowSpeed = false;
    }
}