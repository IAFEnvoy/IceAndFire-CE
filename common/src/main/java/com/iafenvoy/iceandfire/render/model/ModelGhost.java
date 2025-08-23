package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.entity.EntityGhost;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.util.HideableModelRenderer;

public class ModelGhost extends ModelBipedBase<EntityGhost> {
    public final AdvancedModelBox robe;
    public final AdvancedModelBox mask;
    public final AdvancedModelBox hood;
    public final AdvancedModelBox jaw;
    public final AdvancedModelBox sleeveRight;
    public final AdvancedModelBox robeLowerRight;
    public final AdvancedModelBox robeLowerLeft;
    public final AdvancedModelBox sleeveLeft;

    public ModelGhost(float modelScale) {
        super();
        this.texWidth = 128;
        this.texHeight = 64;
        this.sleeveRight = new AdvancedModelBox(this, 33, 35);
        this.sleeveRight.setPos(0.0F, -0.1F, 0.0F);
        this.sleeveRight.addBox(-2.2F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, 0.0F, 0.0F, modelScale);
        this.body = new HideableModelRenderer(this, 16, 16);
        this.body.setPos(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, 0.0F, modelScale);
        this.robeLowerRight = new AdvancedModelBox(this, 48, 35);
        this.robeLowerRight.mirror = true;
        this.robeLowerRight.setPos(0.0F, 0.0F, 0.0F);
        this.robeLowerRight.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, 0.0F, modelScale);
        this.armLeft = new HideableModelRenderer(this, 40, 16);
        this.armLeft.mirror = true;
        this.armLeft.setPos(5.0F, 2.0F, -0.0F);
        this.armLeft.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, 0.0F, modelScale);
        this.setRotateAngle(this.armLeft, -1.4570009181544104F, 0.10000736647217022F, -0.10000736647217022F);
        this.jaw = new AdvancedModelBox(this, 98, 8);
        this.jaw.setPos(0.0F, -1.4F, 0.2F);
        this.jaw.addBox(-2.0F, 0.0F, -4.5F, 4.0F, 2.0F, 8.0F, 0.0F, 0.0F, modelScale);
        this.setRotateAngle(this.jaw, 0.13665927909957545F, 0.0F, 0.0F);
        this.hood = new AdvancedModelBox(this, 60, 0);
        this.hood.setPos(0.0F, 0.0F, 0.0F);
        this.hood.addBox(-4.5F, -8.6F, -4.5F, 9.0F, 9.0F, 9.0F, 0.0F, 0.0F, modelScale);
        this.legLeft = new HideableModelRenderer(this, 0, 16);
        this.legLeft.mirror = true;
        this.legLeft.setPos(2.2F, 12.0F, 0.1F);
        this.legLeft.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, 0.0F, modelScale);
        this.head = new HideableModelRenderer(this, 0, 0);
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, 0.0F, modelScale);
        this.mask = new AdvancedModelBox(this, 40, 8);
        this.mask.setPos(0.0F, 0.0F, 0.0F);
        this.mask.addBox(-4.0F, -8.6F, -4.4F, 8.0F, 8.0F, 0.0F, 0.0F, 0.0F, modelScale);
        this.armRight = new HideableModelRenderer(this, 40, 16);
        this.armRight.setPos(-5.0F, 2.0F, 0.0F);
        this.armRight.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, 0.0F, modelScale);
        this.setRotateAngle(this.armRight, -1.4570009181544104F, -0.10000736647217022F, 0.10000736647217022F);
        this.robe = new AdvancedModelBox(this, 4, 34);
        this.robe.setPos(0.0F, 0.1F, 0.0F);
        this.robe.addBox(-4.5F, 0.0F, -2.5F, 9.0F, 12.0F, 5.0F, 0.0F, 0.0F, modelScale);
        this.legRight = new HideableModelRenderer(this, 0, 16);
        this.legRight.setPos(-2.3F, 12.0F, 0.1F);
        this.legRight.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, 0.0F, modelScale);
        this.robeLowerLeft = new AdvancedModelBox(this, 48, 35);
        this.robeLowerLeft.setPos(0.0F, 0.0F, 0.0F);
        this.robeLowerLeft.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, 0.0F, modelScale);
        this.sleeveLeft = new AdvancedModelBox(this, 33, 35);
        this.sleeveLeft.mirror = true;
        this.sleeveLeft.setPos(0.0F, -0.1F, 0.0F);
        this.sleeveLeft.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, 0.0F, 0.0F, modelScale);
        this.armRight.addChild(this.sleeveRight);
        this.legRight.addChild(this.robeLowerRight);
        this.body.addChild(this.armLeft);
        this.mask.addChild(this.jaw);
        this.head.addChild(this.hood);
        this.body.addChild(this.legLeft);
        this.body.addChild(this.head);
        this.head.addChild(this.mask);
        this.body.addChild(this.armRight);
        this.body.addChild(this.robe);
        this.body.addChild(this.legRight);
        this.legLeft.addChild(this.robeLowerLeft);
        this.armLeft.addChild(this.sleeveLeft);
        this.animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public void setAngles(EntityGhost entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.resetToDefaultPose();
        this.animate(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 1);
        this.faceTarget(headYaw, headPitch, 1, this.head);
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 1F;
        float degree_idle = 0.25F;

        float f12 = (float) Math.toRadians(-1.29f) + limbDistance;
        if (f12 < 0.0F) {
            f12 = 0.0F;
        }
        if (f12 > Math.toRadians(20)) {
            f12 = (float) Math.toRadians(20);
        }
        this.body.rotateAngleX = f12;
        this.head.rotateAngleX = this.head.rotateAngleX - f12;
        this.armRight.rotateAngleX = this.armRight.rotateAngleX - f12;
        this.armLeft.rotateAngleX = this.armLeft.rotateAngleX - f12;

        this.walk(this.jaw, speed_idle * 2F, degree_idle * 0.5F, false, 0, 0.1F, animationProgress, 1);
        this.walk(this.armRight, speed_idle * 1.5F, degree_idle * 0.4F, false, 2, 0.0F, animationProgress, 1);
        this.walk(this.armLeft, speed_idle * 1.5F, degree_idle * 0.4F, true, 2, 0.0F, animationProgress, 1);
        this.flap(this.armRight, speed_idle * 1.5F, degree_idle * 0.2F, false, 2, 0.2F, animationProgress, 1);
        this.flap(this.armRight, speed_idle * 1.5F, degree_idle * 0.2F, true, 2, 0.2F, animationProgress, 1);
        this.walk(this.legLeft, speed_idle * 1.5F, degree_idle * 0.4F, false, 2, 0.2F, animationProgress, 1);
        this.walk(this.legRight, speed_idle * 1.5F, degree_idle * 0.4F, false, 2, 0.2F, animationProgress, 1);
        this.flap(this.body, speed_idle, degree_idle * 0.1F, true, 3, 0, animationProgress, 1);
        this.bob(this.body, speed_idle * 0.5F, degree_idle * 4.1F, false, animationProgress, 1);
        this.bob(this.body, speed_walk * 0.75F, degree_walk * 2.1F, false, limbAngle, limbDistance);

    }

    @Override
    public void animate(EntityGhost entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.animator.update(entity);
        if (this.animator.setAnimation(EntityGhost.ANIMATION_SCARE)) {
            this.animator.startKeyframe(5);
            this.animator.move(this.head, 0, -2, 0);
            this.animator.move(this.armLeft, 0, 1, 0);
            this.animator.move(this.armRight, 0, 1, 0);
            this.rotateMinus(this.animator, this.head, 0, 20, 0);
            this.rotateMinus(this.animator, this.jaw, 20, 10, 0);
            this.rotateMinus(this.animator, this.armLeft, 30, -12.5F, -70F);
            this.rotateMinus(this.animator, this.armRight, -30, 12.5F, 70F);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.animator.move(this.head, 0, -1, 0);
            this.animator.move(this.armLeft, 0, -1, 0);
            this.animator.move(this.armRight, 0, -1, 0);
            this.rotateMinus(this.animator, this.head, 0, -20, 0);
            this.rotateMinus(this.animator, this.jaw, 20, -10, 0);
            this.rotateMinus(this.animator, this.armLeft, -20, -25, -140);
            this.rotateMinus(this.animator, this.armRight, 20, 25, 140);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.animator.move(this.head, 0, -2, 0);
            this.animator.move(this.armLeft, 0, 1, 0);
            this.animator.move(this.armRight, 0, 1, 0);
            this.rotateMinus(this.animator, this.head, 0, 20, 0);
            this.rotateMinus(this.animator, this.jaw, 20, 10, 0);
            this.rotateMinus(this.animator, this.armLeft, 30, -25, -140);
            this.rotateMinus(this.animator, this.armRight, -30, 25, 140);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.animator.move(this.head, 0, -1, 0);
            this.animator.move(this.armLeft, 0, -1, 0);
            this.animator.move(this.armRight, 0, -1, 0);
            this.rotateMinus(this.animator, this.head, 0, -20, 0);
            this.rotateMinus(this.animator, this.jaw, 20, -10, 0);
            this.rotateMinus(this.animator, this.armLeft, -20, -25, -140);
            this.rotateMinus(this.animator, this.armRight, 20, 25, 140);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(10);
        }
        if (this.animator.setAnimation(EntityGhost.ANIMATION_HIT)) {
            this.animator.startKeyframe(5);
            this.animator.move(this.head, 0, -1, 0);
            this.rotateMinus(this.animator, this.body, 0, 0F, 0F);
            this.rotateMinus(this.animator, this.head, -20, 0F, 0F);
            this.rotateMinus(this.animator, this.jaw, 70, 0F, 0F);
            this.rotateMinus(this.animator, this.armRight, -180F, 0F, -30F);
            this.rotateMinus(this.animator, this.armLeft, -180F, 0F, 30F);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        this.animator.update(null);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.head, this.headware, this.body, this.armRight, this.armLeft, this.legRight, this.legLeft, this.robe, this.mask, this.hood, this.jaw,
                this.sleeveRight, this.robeLowerRight, this.robeLowerLeft, this.sleeveLeft);
    }

}
