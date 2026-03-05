package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntitySeaSerpent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SeaSerpentAIMeleeJump extends DiveJumpingGoal {
    private final EntitySeaSerpent dolphin;
    private int attackCooldown = 0;
    private boolean inWater;

    public SeaSerpentAIMeleeJump(EntitySeaSerpent dolphin) {
        this.dolphin = dolphin;
    }

    @Override
    public boolean canStart() {
        return this.dolphin.getTarget() != null && this.dolphin.shouldUseJumpAttack(this.dolphin.getTarget()) && !this.dolphin.isOnGround();
    }

    @Override
    public boolean shouldContinue() {
        final double d0 = this.dolphin.getVelocity().y;
        return this.dolphin.getTarget() != null && this.dolphin.jumpCooldown > 0
                && (d0 * d0 >= 0.03F || this.dolphin.getPitch() == 0.0F
                || Math.abs(this.dolphin.getPitch()) >= 10.0F || !this.dolphin.isTouchingWater())
                && !this.dolphin.isOnGround();
    }

    @Override
    public boolean canStop() {
        return false;
    }

    @Override
    public void start() {
        LivingEntity target = this.dolphin.getTarget();
        if (target != null) {
            final double distanceXZ = this.dolphin.squaredDistanceTo(target.getX(), this.dolphin.getY(), target.getZ());
            if (distanceXZ < 300) {
                this.dolphin.lookAtEntity(target, 260, 30);
                final double smoothX = MathHelper.clamp(Math.abs(target.getX() - this.dolphin.getX()), 0, 1);
                final double smoothZ = MathHelper.clamp(Math.abs(target.getZ() - this.dolphin.getZ()), 0, 1);
                final double d0 = (target.getX() - this.dolphin.getX()) * 0.3 * smoothX;
                final double d2 = (target.getZ() - this.dolphin.getZ()) * 0.3 * smoothZ;
                final float up = 1F + this.dolphin.getRandom().nextFloat() * 0.8F;
                this.dolphin.setVelocity(this.dolphin.getVelocity().add(d0 * 0.3D, up, d2 * 0.3D));
                this.dolphin.getNavigation().stop();
                this.dolphin.jumpCooldown = this.dolphin.getRandom().nextInt(32) + 32;
            } else
                this.dolphin.getNavigation().startMovingTo(target, 1.0F);
        }
    }

    @Override
    public void stop() {
        this.dolphin.setPitch(0.0F);
        this.attackCooldown = 0;
    }

    @Override
    public void tick() {
        final boolean flag = this.inWater;
        if (!flag) {
            FluidState fluidstate = this.dolphin.getWorld().getFluidState(this.dolphin.getBlockPos());
            this.inWater = fluidstate.isIn(FluidTags.WATER);
        }
        if (this.attackCooldown > 0) this.attackCooldown--;
        if (this.inWater && !flag)
            this.dolphin.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
        LivingEntity target = this.dolphin.getTarget();
        if (target != null) {
            if (this.dolphin.distanceTo(target) < 3F && this.attackCooldown <= 0) {
                this.attackCooldown = 20;
            } else if (this.dolphin.distanceTo(target) < 5F) {
                this.dolphin.setAnimation(EntitySeaSerpent.ANIMATION_BITE);
                this.dolphin.setAnimationTick(0);
            }
        }

        Vec3d vector3d = this.dolphin.getVelocity();
        if (vector3d.y * vector3d.y < 0.1F && this.dolphin.getPitch() != 0.0F || vector3d.length() == 0)
            this.dolphin.setPitch(MathHelper.lerpAngleDegrees(this.dolphin.getPitch(), 0.0F, 0.2F));
        else {
            final double d0 = vector3d.horizontalLength();
            final double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * (180F / (float) Math.PI);
            this.dolphin.setPitch((float) d1);
        }
    }
}
