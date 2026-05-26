package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.CockatriceEntity;
import com.iafenvoy.iceandfire.entity.GorgonEntity;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CockatriceAIStareAttackGoal extends Goal {
    private final CockatriceEntity entity;
    private final double moveSpeedAmp;
    private int seeTime;
    private BlockPos target = null;

    public CockatriceAIStareAttackGoal(CockatriceEntity cockatrice, double speedAmplifier, int delay, float maxDistance) {
        this.entity = cockatrice;
        this.moveSpeedAmp = speedAmplifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isEntityLookingAt(LivingEntity looker, LivingEntity seen, double degree) {
        Vec3 Vector3d = looker.getViewVector(1.0F).normalize();
        Vec3 Vector3d1 = new Vec3(seen.getX() - looker.getX(), seen.getBoundingBox().minY + seen.getEyeHeight() - (looker.getY() + looker.getEyeHeight()), seen.getZ() - looker.getZ());
        Vector3d1 = Vector3d1.normalize();
        final double d0 = Vector3d1.length();
        final double d1 = Vector3d.dot(Vector3d1);
        return d1 > 1.0D - degree / d0 && !looker.isSpectator();
    }

    @Override
    public boolean canUse() {
        return this.entity.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void stop() {
        super.stop();
        this.seeTime = 0;
        this.entity.stopUsingItem();
        this.entity.getNavigation().stop();
        this.target = null;
    }

    @Override
    public void tick() {
        LivingEntity LivingEntity = this.entity.getTarget();
        if (LivingEntity != null) {
            if (GorgonEntity.isStoneMob(LivingEntity) || !LivingEntity.isAlive()) {
                this.entity.setTarget(null);
                this.entity.setTargetedEntity(0);
                this.stop();
                return;
            }
            if (!isEntityLookingAt(LivingEntity, this.entity, CockatriceEntity.VIEW_RADIUS) || (LivingEntity.xo != this.entity.getX() || LivingEntity.yo != this.entity.getY() || LivingEntity.zo != this.entity.getZ())) {
                this.entity.getNavigation().stop();
                BlockPos pos = DragonUtils.getBlockInTargetsViewCockatrice(this.entity, LivingEntity);
                if (this.target == null || pos.distSqr(this.target) > 4)
                    this.target = pos;
            }
            this.entity.setTargetedEntity(LivingEntity.getId());

            this.entity.distanceToSqr(LivingEntity.getX(), LivingEntity.getBoundingBox().minY, LivingEntity.getZ());
            final boolean flag = this.entity.getSensing().hasLineOfSight(LivingEntity);
            final boolean flag1 = this.seeTime > 0;

            if (flag != flag1) this.seeTime = 0;
            if (flag) ++this.seeTime;
            else --this.seeTime;

            if (this.target != null)
                if (this.entity.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ()) > 16 && !isEntityLookingAt(LivingEntity, this.entity, CockatriceEntity.VIEW_RADIUS))
                    this.entity.getNavigation().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), this.moveSpeedAmp);
            this.entity.getLookControl().setLookAt(LivingEntity.getX(), LivingEntity.getY() + LivingEntity.getEyeHeight(), LivingEntity.getZ(), this.entity.getMaxHeadYRot(), this.entity.getMaxHeadXRot());
        }
    }

}