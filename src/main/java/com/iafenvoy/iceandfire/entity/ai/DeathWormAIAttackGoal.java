package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.DeathWormEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DeathWormAIAttackGoal extends Goal {
    private final DeathWormEntity worm;
    private int jumpCooldown = 0;

    public DeathWormAIAttackGoal(DeathWormEntity worm) {
        this.worm = worm;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.jumpCooldown > 0)
            this.jumpCooldown--;
        return !(this.worm.getTarget() == null || this.worm.isVehicle() || !this.worm.onGround() && !this.worm.isInSandStrict() || this.jumpCooldown > 0);
    }

    @Override
    public boolean canContinueToUse() {
        return this.worm.getTarget() != null && this.worm.getTarget().isAlive();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        LivingEntity target = this.worm.getTarget();
        if (target != null) {
            if (this.worm.isInSand()) {
                BlockPos topSand = this.worm.blockPosition();
                while (this.worm.level().getBlockState(topSand.above()).is(BlockTags.SAND))
                    topSand = topSand.above();
                this.worm.setPos(this.worm.getX(), topSand.getY() + 0.5F, this.worm.getZ());
            }
            if (this.shouldJump()) this.jumpAttack();
            else this.worm.getNavigation().moveTo(target, 1.0F);
        }
    }

    public boolean shouldJump() {
        LivingEntity target = this.worm.getTarget();
        if (target != null) {
            final double distanceXZ = this.worm.distanceToSqr(target.getX(), this.worm.getY(), target.getZ());
            final float distanceXZSqrt = (float) Math.sqrt(distanceXZ);
            double d0 = this.worm.getDeltaMovement().y;
            if (distanceXZSqrt < 12 && distanceXZSqrt > 2)
                return this.jumpCooldown <= 0
                        && (d0 * d0 >= 0.03F || this.worm.getXRot() == 0.0F || Math.abs(this.worm.getXRot()) >= 10.0F
                        || !this.worm.isInWater())
                        && !this.worm.onGround();
        }
        return false;
    }

    public void jumpAttack() {
        LivingEntity target = this.worm.getTarget();
        if (target == null) return;
        this.worm.lookAt(target, 260, 30);
        final double smoothX = Mth.clamp(Math.abs(target.getX() - this.worm.getX()), 0, 1);
        //MathHelper.clamp(Math.abs(target.getPosY() - worm.getPosY()), 0, 1);
        final double smoothZ = Mth.clamp(Math.abs(target.getZ() - this.worm.getZ()), 0, 1);
        final double d0 = (target.getX() - this.worm.getX()) * 0.2 * smoothX;
        //Math.signum(target.getPosY() - this.worm.getPosY());
        final double d2 = (target.getZ() - this.worm.getZ()) * 0.2 * smoothZ;
        final float up = (this.worm.getAgeScale() > 3 ? 0.8F : 0.5F) + this.worm.getRandom().nextFloat() * 0.5F;
        this.worm.setDeltaMovement(this.worm.getDeltaMovement().add(d0 * 0.3D, up, d2 * 0.3D));
        this.worm.getNavigation().stop();
        this.worm.setWormJumping(20);
        this.jumpCooldown = this.worm.getRandom().nextInt(32) + 64;
    }

    @Override
    public void stop() {
        this.worm.setXRot(0.0F);
    }

    @Override
    public void tick() {
        if (this.jumpCooldown > 0) this.jumpCooldown--;
        LivingEntity target = this.worm.getTarget();
        if (target != null && this.worm.hasLineOfSight(target))
            if (this.worm.distanceTo(target) < 3F)
                this.worm.doHurtTarget(target);

        Vec3 vector3d = this.worm.getDeltaMovement();
        if (vector3d.y * vector3d.y < 0.1F && this.worm.getXRot() != 0.0F)
            this.worm.setXRot(Mth.rotLerp(this.worm.getXRot(), 0.0F, 0.2F));
        else {
            final double d0 = vector3d.horizontalDistance();
            final double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * (180F / (float) Math.PI);
            this.worm.setXRot((float) d1);
        }
        if (this.shouldJump()) this.jumpAttack();
        else if (this.worm.getNavigation().isDone())
            this.worm.getNavigation().moveTo(target, 1.0F);
    }
}
