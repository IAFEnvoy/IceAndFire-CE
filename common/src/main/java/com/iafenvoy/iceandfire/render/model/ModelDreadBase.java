package com.iafenvoy.iceandfire.render.model;

import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.entity.LivingEntity;

abstract class ModelDreadBase<T extends LivingEntity & IAnimatedEntity> extends ModelBipedBase<T> {
    ModelDreadBase() {
        super();
    }

    public abstract Animation getSpawnAnimation();

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        this.setRotationAnglesSpawn(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
    }

    public void setRotationAnglesSpawn(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entityIn.getAnimation() == this.getSpawnAnimation())
            if (entityIn.getAnimationTick() < 30) {
                this.flap(this.armRight, 0.5F, 0.5F, false, 2, -0.7F, entityIn.age, 1);
                this.flap(this.armLeft, 0.5F, 0.5F, true, 2, -0.7F, entityIn.age, 1);
                this.walk(this.armRight, 0.5F, 0.5F, true, 1, 0, entityIn.age, 1);
                this.walk(this.armLeft, 0.5F, 0.5F, true, 1, 0, entityIn.age, 1);
            }
    }

    @Override
    public void animate(T entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.animator.update(entity);
        if (this.animator.setAnimation(this.getSpawnAnimation())) {
            this.animator.startKeyframe(0);
            this.animator.move(this.body, 0, 35, 0);
            this.rotate(this.animator, this.armLeft, -180, 0, 0);
            this.rotate(this.animator, this.armRight, -180, 0, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(30);
            this.animator.move(this.body, 0, 0, 0);
            this.rotate(this.animator, this.armLeft, -180, 0, 0);
            this.rotate(this.animator, this.armRight, -180, 0, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        this.animator.update(null);
    }
}
