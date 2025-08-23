package com.iafenvoy.iceandfire.render.model;

import com.iafenvoy.iceandfire.entity.EntityDreadGhoul;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.util.HideableModelRenderer;

public class ModelDreadGhoul extends ModelBipedBase<EntityDreadGhoul> {

    public final AdvancedModelBox head2;
    public final AdvancedModelBox clawsRight;
    public final AdvancedModelBox clawsLeft;

    public ModelDreadGhoul(float modelScale) {
        super();
        this.texWidth = 128;
        this.texHeight = 64;
        this.head = new HideableModelRenderer(this, 0, 0);
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -7.4F, -4.0F, 8, 8, 8, modelScale);
        this.setRotateAngle(this.head, 0.045553093477052F, 0.0F, 0.0F);
        this.legLeft = new HideableModelRenderer(this, 0, 16);
        this.legLeft.mirror = true;
        this.legLeft.setPos(1.9F, 12.0F, 0.1F);
        this.legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelScale);
        this.setRotateAngle(this.legLeft, -0.045553093477052F, 0.0F, 0.0F);
        this.armRight = new HideableModelRenderer(this, 40, 16);
        this.armRight.setPos(-4.0F, 2.0F, 0.0F);
        this.armRight.addBox(-3.0F, -2.0F, -2.0F, 3, 12, 4, modelScale);
        this.setRotateAngle(this.armRight, -0.136659280431156F, 0.091106186954104F, 0.22759093446006054F);
        this.armLeft = new HideableModelRenderer(this, 40, 16);
        this.armLeft.mirror = true;
        this.armLeft.setPos(4.0F, 2.0F, -0.0F);
        this.armLeft.addBox(0.0F, -2.0F, -2.0F, 3, 12, 4, modelScale);
        this.setRotateAngle(this.armLeft, -0.136659280431156F, 0.091106186954104F, -0.22759093446006054F);
        this.head2 = new AdvancedModelBox(this, 32, 0);
        this.head2.setPos(0.0F, 0.4F, 0.0F);
        this.head2.addBox(-4.5F, -6.4F, -4.1F, 9, 8, 8, modelScale);
        this.clawsLeft = new AdvancedModelBox(this, 56, 25);
        this.clawsLeft.mirror = true;
        this.clawsLeft.setPos(-0.5F, 11.0F, 0.0F);
        this.clawsLeft.addBox(-1.0F, -2.0F, -2.0F, 4, 3, 4, modelScale);
        this.setRotateAngle(this.clawsLeft, -0.0F, 0.0F, 0.2897246558310587F);
        this.body = new HideableModelRenderer(this, 16, 16);
        this.body.setPos(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelScale);
        this.setRotateAngle(this.body, 0.045553093477052F, 0.0F, 0.0F);
        this.legRight = new HideableModelRenderer(this, 0, 16);
        this.legRight.setPos(-1.9F, 12.0F, 0.1F);
        this.legRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelScale);
        this.setRotateAngle(this.legRight, -0.045553093477052F, 0.0F, 0.0F);
        this.clawsRight = new AdvancedModelBox(this, 56, 25);
        this.clawsRight.setPos(0.5F, 11.0F, 0.0F);
        this.clawsRight.addBox(-3.0F, -2.0F, -2.0F, 4, 3, 4, modelScale);
        this.setRotateAngle(this.clawsRight, 0.0F, 0.0F, -0.2897246558310587F);
        this.body.addChild(this.head);
        this.body.addChild(this.legRight);
        this.body.addChild(this.legLeft);
        this.body.addChild(this.armRight);
        this.body.addChild(this.armLeft);
        this.head.addChild(this.head2);
        this.armLeft.addChild(this.clawsLeft);
        this.armRight.addChild(this.clawsRight);
        this.animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public void setAngles(EntityDreadGhoul entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.resetToDefaultPose();
        this.faceTarget(headYaw, headPitch, 1.0F, this.head);
        this.animate(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 0f);
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 1F;
        if (entity.getAnimation() == EntityDreadGhoul.ANIMATION_SPAWN) {
            if (entity.getAnimationTick() < 30) {
                this.swing(this.armRight, 0.5F, 0.5F, false, 2, -0.7F, entity.age, 1);
                this.swing(this.armLeft, 0.5F, 0.5F, true, 2, -0.7F, entity.age, 1);
                this.flap(this.armRight, 0.5F, 0.5F, true, 1, 0, entity.age, 1);
                this.flap(this.armLeft, 0.5F, 0.5F, true, 1, 0, entity.age, 1);
            }
        }
        this.flap(this.armLeft, speed_idle, 0.15F, false, 2, -0.1F, entity.age, 1);
        this.flap(this.armRight, speed_idle, 0.15F, true, 2, -0.1F, entity.age, 1);
        this.walk(this.head, speed_idle, 0.1F, true, 1, -0.05F, entity.age, 1);

        this.walk(this.legRight, speed_walk, degree_walk, false, 0, 0, limbAngle, limbDistance);
        this.walk(this.legLeft, speed_walk, degree_walk, true, 0, 0, limbAngle, limbDistance);
        this.flap(this.legRight, speed_walk, degree_walk * 0.1F, true, 3, -0.05F, limbAngle, limbDistance);
        this.flap(this.legLeft, speed_walk, degree_walk * 0.1F, true, 3, 0.05F, limbAngle, limbDistance);
        this.flap(this.body, speed_walk, degree_walk * 0.1F, true, 1, 0, limbAngle, limbDistance);

        this.walk(this.armRight, speed_walk, degree_walk, true, -2, 0, limbAngle, limbDistance);
        this.walk(this.armLeft, speed_walk, degree_walk, false, -2, 0, limbAngle, limbDistance);
        this.flap(this.armRight, speed_walk, degree_walk * 0.8F, true, -2, -0.1F, limbAngle, limbDistance);
        this.flap(this.armLeft, speed_walk, degree_walk * 0.8F, true, -2, 0.1F, limbAngle, limbDistance);
        this.flap(this.head, speed_walk, degree_walk * 0.2F, false, 0, 0, limbAngle, limbDistance);

    }

    @Override
    void animate(EntityDreadGhoul entity, float limbSwing, float limbSwingAmount,
                 float ageInTicks, float netHeadYaw, float headPitch, float f) {
        this.resetToDefaultPose();
        this.animator.update(entity);
        if (this.animator.setAnimation(EntityDreadGhoul.ANIMATION_SLASH)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.armRight, 20, 45, 80);
            this.rotate(this.animator, this.body, 0, 30, 0);
            this.rotate(this.animator, this.head, 0, -20, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.armRight, -80, -15, 10);
            this.rotate(this.animator, this.body, 0, -70, 0);
            this.rotate(this.animator, this.head, 0, 60, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.armLeft, 20, -45, -80);
            this.rotate(this.animator, this.body, 0, -30, 0);
            this.rotate(this.animator, this.head, 0, 20, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.armLeft, -80, 15, -10);
            this.rotate(this.animator, this.body, 0, 70, 0);
            this.rotate(this.animator, this.head, 0, -60, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        if (this.animator.setAnimation(EntityDreadGhoul.ANIMATION_SPAWN)) {
            this.animator.startKeyframe(0);
            this.animator.move(this.body, 0, 35, 0);
            this.rotateMinus(this.animator, this.armLeft, -180, -90, 50);
            this.rotateMinus(this.animator, this.head, -60, 0, 0);
            this.rotateMinus(this.animator, this.armRight, -180, 90, -50);
            this.animator.endKeyframe();
            this.animator.startKeyframe(30);
            this.animator.move(this.body, 0, 0, 0);
            this.rotate(this.animator, this.armLeft, -30, -90, 0);
            this.rotate(this.animator, this.armRight, -30, 90, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        this.animator.update(null);
    }
}
