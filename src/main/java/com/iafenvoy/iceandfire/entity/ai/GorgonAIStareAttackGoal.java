package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.GorgonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GorgonAIStareAttackGoal extends Goal {
    private final GorgonEntity entity;
    private final double moveSpeedAmp;
    private final float maxAttackDistance;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public GorgonAIStareAttackGoal(GorgonEntity gorgon, double speedAmplifier, int delay, float maxDistance) {
        this.entity = gorgon;
        this.moveSpeedAmp = speedAmplifier;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.entity.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !this.entity.getNavigation().isDone());
    }

    @Override
    public void stop() {
        super.stop();
        this.seeTime = 0;
        this.entity.stopUsingItem();
    }

    @Override
    public void tick() {
        LivingEntity LivingEntity = this.entity.getTarget();

        if (LivingEntity != null) {
            if (GorgonEntity.isStoneMob(LivingEntity)) {
                this.entity.setTarget(null);
                this.stop();
                return;
            }
            this.entity.getLookControl().setLookAt(LivingEntity.getX(), LivingEntity.getY() + LivingEntity.getEyeHeight(), LivingEntity.getZ(), this.entity.getMaxHeadYRot(), this.entity.getMaxHeadXRot());

            final double d0 = this.entity.distanceToSqr(LivingEntity.getX(), LivingEntity.getBoundingBox().minY, LivingEntity.getZ());
            final boolean flag = this.entity.getSensing().hasLineOfSight(LivingEntity);
            final boolean flag1 = this.seeTime > 0;

            if (flag != flag1) this.seeTime = 0;
            if (flag) ++this.seeTime;
            else --this.seeTime;

            if (d0 <= this.maxAttackDistance && this.seeTime >= 20) {
                this.entity.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.entity.getNavigation().moveTo(LivingEntity, this.moveSpeedAmp);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if (this.entity.getRandom().nextFloat() < 0.3D)
                    this.strafingClockwise = !this.strafingClockwise;
                if (this.entity.getRandom().nextFloat() < 0.3D)
                    this.strafingBackwards = !this.strafingBackwards;
                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (d0 > this.maxAttackDistance * 0.75F)
                    this.strafingBackwards = false;
                else if (d0 < this.maxAttackDistance * 0.25F)
                    this.strafingBackwards = true;

                this.entity.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.entity.getLookControl().setLookAt(LivingEntity.getX(), LivingEntity.getY() + LivingEntity.getEyeHeight(), LivingEntity.getZ(), this.entity.getMaxHeadYRot(), this.entity.getMaxHeadXRot());
                this.entity.forcePreyToLook(LivingEntity);
            } else {
                this.entity.getLookControl().setLookAt(LivingEntity, 30.0F, 30.0F);
                this.entity.forcePreyToLook(LivingEntity);
            }

        }
    }
}