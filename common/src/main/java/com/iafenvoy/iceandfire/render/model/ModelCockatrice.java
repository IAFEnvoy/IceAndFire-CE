package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.entity.EntityCockatrice;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ModelCockatrice extends ModelDragonBase<EntityCockatrice> {
    public final AdvancedModelBox lowerBody;
    public final AdvancedModelBox leftThigh;
    public final AdvancedModelBox rightThigh;
    public final AdvancedModelBox upperBody;
    public final AdvancedModelBox tail1;
    public final AdvancedModelBox neck;
    public final AdvancedModelBox leftUpperArm;
    public final AdvancedModelBox RightUpperArm;
    public final AdvancedModelBox neck2;
    public final AdvancedModelBox head;
    public final AdvancedModelBox upperJaw;
    public final AdvancedModelBox lowerJaw;
    public final AdvancedModelBox Comb1;
    public final AdvancedModelBox Comb2;
    public final AdvancedModelBox Wattle;
    public final AdvancedModelBox upperJaw_1;
    public final AdvancedModelBox leftLowerArm;
    public final AdvancedModelBox leftUpperArmWing;
    public final AdvancedModelBox leftLowerArmWing;
    public final AdvancedModelBox RightLowerArm;
    public final AdvancedModelBox RightUpperArmWing;
    public final AdvancedModelBox RightLowerArmWing;
    public final AdvancedModelBox tail2;
    public final AdvancedModelBox tail3;
    public final AdvancedModelBox rightToeClaw2;
    public final AdvancedModelBox tail4;
    public final AdvancedModelBox TailPlume;
    public final AdvancedModelBox TailPlume_1;
    public final AdvancedModelBox leftLeg;
    public final AdvancedModelBox leftFoot;
    public final AdvancedModelBox rightLeg;
    public final AdvancedModelBox rightFoot;
    private final ModelAnimator animator;

    public ModelCockatrice() {
        this.texWidth = 128;
        this.texHeight = 64;
        this.lowerBody = new AdvancedModelBox(this, 65, 32);
        this.lowerBody.setPos(0.0F, 7.9F, -2.5F);
        this.lowerBody.addBox(-4.0F, -1.0F, 0.0F, 8, 8, 9, 0.0F);
        this.setRotateAngle(this.lowerBody, -0.15550883635269477F, 0.0F, 0.0F);
        this.Wattle = new AdvancedModelBox(this, 47, 9);
        this.Wattle.mirror = true;
        this.Wattle.setPos(-0.0F, -2.1F, -2.03F);
        this.Wattle.addBox(-0.5F, -1.5F, 0.6F, 1, 4, 5, 0.0F);
        this.setRotateAngle(this.Wattle, -1.593485607070823F, -0.0F, 0.0F);
        this.TailPlume = new AdvancedModelBox(this, 37, 31);
        this.TailPlume.setPos(0.0F, 0.9F, 7.3F);
        this.TailPlume.addBox(-0.5F, -9.1F, -0.2F, 1, 7, 5, 0.0F);
        this.setRotateAngle(this.TailPlume, -0.7740535232594852F, -0.0F, 0.0F);
        this.upperBody = new AdvancedModelBox(this, 67, 5);
        this.upperBody.setPos(0.0F, 1.1F, -2.0F);
        this.upperBody.addBox(-3.0F, -2.0F, -5.0F, 6, 7, 8, 0.0F);
        this.setRotateAngle(this.upperBody, 0.091106186954104F, -0.0F, 0.0F);
        this.leftLowerArmWing = new AdvancedModelBox(this, 20, 9);
        this.leftLowerArmWing.setPos(0.4F, 0.8F, 2.4F);
        this.leftLowerArmWing.addBox(-0.5F, 0.0F, -8.0F, 1, 12, 8, 0.0F);
        this.setRotateAngle(this.leftLowerArmWing, 0.01361356816555577F, -0.0F, 0.0F);
        this.upperJaw = new AdvancedModelBox(this, 28, 0);
        this.upperJaw.setPos(0.0F, -1.0F, -4.93F);
        this.upperJaw.addBox(-1.5F, -2.4F, -4.0F, 3, 3, 4, 0.0F);
        this.setRotateAngle(this.upperJaw, -0.0017453292519943296F, -0.0F, 0.0F);
        this.rightFoot = new AdvancedModelBox(this, 0, 36);
        this.rightFoot.setPos(0.0F, 0.9F, -5.7F);
        this.rightFoot.addBox(-1.5F, 0.0F, -3.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(this.rightFoot, -1.3089969389957472F, -0.0F, 0.0F);
        this.leftUpperArm = new AdvancedModelBox(this, 0, 20);
        this.leftUpperArm.mirror = true;
        this.leftUpperArm.setPos(2.5F, 1.3F, -3.3F);
        this.leftUpperArm.addBox(0.0F, -1.0F, -1.0F, 2, 5, 3, 0.0F);
        this.setRotateAngle(this.leftUpperArm, 0.0F, 0.22689280275926282F, -0.8726646259971648F);
        this.leftThigh = new AdvancedModelBox(this, 14, 35);
        this.leftThigh.mirror = true;
        this.leftThigh.setPos(3.0F, 14.0F, 3.0F);
        this.leftThigh.addBox(0.0F, -2.5F, -2.0F, 3, 6, 5, 0.0F);
        this.neck = new AdvancedModelBox(this, 29, 51);
        this.neck.setPos(0.0F, 0.8F, -3.5F);
        this.neck.addBox(-2.0F, -2.0F, -6.5F, 4, 5, 7, 0.0F);
        this.setRotateAngle(this.neck, -0.4553564018453205F, -0.0F, 0.0F);
        this.neck2 = new AdvancedModelBox(this, 0, 47);
        this.neck2.setPos(0.0F, 0.3F, -5.6F);
        this.neck2.addBox(-2.02F, -2.0F, -8.0F, 4, 4, 8, 0.0F);
        this.setRotateAngle(this.neck2, -0.31869712141416456F, -0.0F, 0.0F);
        this.RightUpperArm = new AdvancedModelBox(this, 0, 20);
        this.RightUpperArm.mirror = true;
        this.RightUpperArm.setPos(-2.5F, 1.3F, -3.3F);
        this.RightUpperArm.addBox(-2.0F, -1.0F, -1.0F, 2, 5, 3, 0.0F);
        this.setRotateAngle(this.RightUpperArm, 0.0F, -0.22689280275926282F, 0.8726646259971648F);
        this.rightThigh = new AdvancedModelBox(this, 14, 35);
        this.rightThigh.setPos(-3.0F, 14.0F, 3.0F);
        this.rightThigh.addBox(-3.0F, -2.5F, -2.0F, 3, 6, 5, 0.0F);
        this.Comb1 = new AdvancedModelBox(this, 38, 15);
        this.Comb1.mirror = true;
        this.Comb1.setPos(-0.0F, -2.5F, -2.03F);
        this.Comb1.addBox(-0.5F, -1.5F, 0.6F, 1, 4, 5, 0.0F);
        this.setRotateAngle(this.Comb1, 1.2292353921796064F, -0.0F, 0.0F);
        this.rightToeClaw2 = new AdvancedModelBox(this, 0, 40);
        this.rightToeClaw2.setPos(0.0F, 0.2F, -2.5F);
        this.rightToeClaw2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 1, 0.0F);
        this.setRotateAngle(this.rightToeClaw2, -1.7627825445142729F, -0.0F, 0.0F);
        this.RightLowerArm = new AdvancedModelBox(this, 0, 12);
        this.RightLowerArm.setPos(-0.9F, 3.0F, 0.0F);
        this.RightLowerArm.addBox(-1.0F, 0.0F, -6.0F, 2, 2, 6, 0.0F);
        this.setRotateAngle(this.RightLowerArm, 0.704240353179712F, -0.0F, 0.0F);
        this.RightUpperArmWing = new AdvancedModelBox(this, 22, 10);
        this.RightUpperArmWing.mirror = true;
        this.RightUpperArmWing.setPos(-1.0F, 2.7F, 1.1F);
        this.RightUpperArmWing.addBox(-0.5F, 0.0F, -3.5F, 1, 4, 7, 0.0F);
        this.setRotateAngle(this.RightUpperArmWing, 1.5184364492350666F, -0.0F, 0.0F);
        this.head = new AdvancedModelBox(this, 0, 0);
        this.head.setPos(0.0F, 0.8F, -6.03F);
        this.head.addBox(-2.5F, -4.0F, -5.0F, 5, 5, 6, 0.0F);
        this.setRotateAngle(this.head, 1.1383037381507017F, 0.0F, 0.0F);
        this.rightLeg = new AdvancedModelBox(this, 2, 25);
        this.rightLeg.setPos(-1.2F, 2.9F, 1.1F);
        this.rightLeg.addBox(-1.0F, 0.4F, -6.7F, 2, 2, 8, 0.0F);
        this.setRotateAngle(this.rightLeg, 1.3089969389957472F, -0.0F, 0.0F);
        this.RightLowerArmWing = new AdvancedModelBox(this, 20, 9);
        this.RightLowerArmWing.mirror = true;
        this.RightLowerArmWing.setPos(0.4F, 0.8F, 2.4F);
        this.RightLowerArmWing.addBox(-0.5F, 0.0F, -8.0F, 1, 12, 8, 0.0F);
        this.setRotateAngle(this.RightLowerArmWing, 0.01361356816555577F, -0.0F, 0.0F);
        this.tail3 = new AdvancedModelBox(this, 49, 16);
        this.tail3.setPos(0.0F, 0.3F, 7.2F);
        this.tail3.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 12, 0.0F);
        this.setRotateAngle(this.tail3, -0.05253441048502932F, -0.0F, 0.0F);
        this.tail4 = new AdvancedModelBox(this, 47, 40);
        this.tail4.setPos(0.0F, 0.6F, 10.0F);
        this.tail4.addBox(-1.02F, -0.2F, 0.1F, 2, 2, 12, 0.0F);
        this.setRotateAngle(this.tail4, -0.05253441048502932F, -0.0F, 0.0F);
        this.tail2 = new AdvancedModelBox(this, 90, 13);
        this.tail2.setPos(0.0F, 0.2F, 6.7F);
        this.tail2.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 8, 0.0F);
        this.leftLowerArm = new AdvancedModelBox(this, 0, 12);
        this.leftLowerArm.mirror = true;
        this.leftLowerArm.setPos(0.9F, 3.0F, 0.0F);
        this.leftLowerArm.addBox(-1.0F, 0.0F, -6.0F, 2, 2, 6, 0.0F);
        this.setRotateAngle(this.leftLowerArm, 0.704240353179712F, -0.0F, 0.0F);
        this.tail1 = new AdvancedModelBox(this, 91, 0);
        this.tail1.setPos(0.0F, -1.0F, 8.5F);
        this.tail1.addBox(-2.0F, 0.0F, 0.0F, 4, 5, 7, 0.0F);
        this.setRotateAngle(this.tail1, 0.11903145498601327F, -0.0F, 0.0F);
        this.TailPlume_1 = new AdvancedModelBox(this, 35, 29);
        this.TailPlume_1.setPos(0.0F, 1.2F, 3.5F);
        this.TailPlume_1.addBox(-0.5F, -11.5F, -0.4F, 1, 10, 6, 0.0F);
        this.setRotateAngle(this.TailPlume_1, -0.5009094953223726F, -0.0F, 0.0F);
        this.Comb2 = new AdvancedModelBox(this, 38, 15);
        this.Comb2.setPos(-0.0F, -0.7F, -1.03F);
        this.Comb2.addBox(-0.52F, -1.5F, 0.6F, 1, 4, 5, 0.0F);
        this.setRotateAngle(this.Comb2, 1.1383037381507017F, 0.0F, 0.0F);
        this.leftUpperArmWing = new AdvancedModelBox(this, 22, 10);
        this.leftUpperArmWing.setPos(1.4F, 2.7F, 1.1F);
        this.leftUpperArmWing.addBox(-0.5F, 0.0F, -3.5F, 1, 4, 7, 0.0F);
        this.setRotateAngle(this.leftUpperArmWing, 1.5184364492350666F, -0.0F, 0.0F);
        this.leftFoot = new AdvancedModelBox(this, 0, 36);
        this.leftFoot.setPos(0.0F, 0.9F, -5.7F);
        this.leftFoot.addBox(-1.5F, 0.0F, -3.1F, 3, 2, 4, 0.0F);
        this.setRotateAngle(this.leftFoot, -1.3089969389957472F, -0.0F, 0.0F);
        this.lowerJaw = new AdvancedModelBox(this, 49, 0);
        this.lowerJaw.setPos(0.0F, 0.1F, -4.53F);
        this.lowerJaw.setScale(0.99F, 0.99F, 0.99F);
        this.lowerJaw.addBox(-1.5F, -0.5F, -4.3F, 3, 1, 4, 0.0F);
        this.setRotateAngle(this.lowerJaw, -0.091106186954104F, 0.0F, 0.0F);
        this.leftLeg = new AdvancedModelBox(this, 2, 25);
        this.leftLeg.mirror = true;
        this.leftLeg.setPos(1.2F, 2.9F, 1.1F);
        this.leftLeg.addBox(-1.0F, 0.4F, -6.7F, 2, 2, 8, 0.0F);
        this.setRotateAngle(this.leftLeg, 1.3089969389957472F, -0.0F, 0.0F);
        this.upperJaw_1 = new AdvancedModelBox(this, 20, 0);
        this.upperJaw_1.setPos(-0.0F, -1.0F, -3.83F);
        this.upperJaw_1.addBox(-0.5F, -0.5F, -2.9F, 1, 1, 3, 0.0F);
        this.setRotateAngle(this.upperJaw_1, 1.3658946726107624F, -0.0F, 0.0F);
        this.head.addChild(this.Wattle);
        this.tail4.addChild(this.TailPlume);
        this.lowerBody.addChild(this.upperBody);
        this.leftLowerArm.addChild(this.leftLowerArmWing);
        this.head.addChild(this.upperJaw);
        this.rightLeg.addChild(this.rightFoot);
        this.upperBody.addChild(this.leftUpperArm);
        this.upperBody.addChild(this.neck);
        this.neck.addChild(this.neck2);
        this.upperBody.addChild(this.RightUpperArm);
        this.head.addChild(this.Comb1);
        this.tail2.addChild(this.rightToeClaw2);
        this.RightUpperArm.addChild(this.RightLowerArm);
        this.RightUpperArm.addChild(this.RightUpperArmWing);
        this.neck2.addChild(this.head);
        this.rightThigh.addChild(this.rightLeg);
        this.RightLowerArm.addChild(this.RightLowerArmWing);
        this.tail2.addChild(this.tail3);
        this.tail3.addChild(this.tail4);
        this.tail1.addChild(this.tail2);
        this.leftUpperArm.addChild(this.leftLowerArm);
        this.lowerBody.addChild(this.tail1);
        this.tail4.addChild(this.TailPlume_1);
        this.head.addChild(this.Comb2);
        this.leftUpperArm.addChild(this.leftUpperArmWing);
        this.leftLeg.addChild(this.leftFoot);
        this.head.addChild(this.lowerJaw);
        this.leftThigh.addChild(this.leftLeg);
        this.upperJaw.addChild(this.upperJaw_1);
        this.animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.lowerBody, this.leftThigh, this.rightThigh);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.lowerBody, this.leftThigh, this.rightThigh, this.upperBody, this.tail1, this.neck, this.leftUpperArm,
                this.RightUpperArm, this.neck2, this.head, this.upperJaw, this.lowerJaw, this.Comb1, this.Comb2, this.Wattle, this.upperJaw_1, this.leftLowerArm,
                this.leftUpperArmWing, this.leftLowerArmWing, this.RightLowerArm, this.RightUpperArmWing, this.RightLowerArmWing,
                this.tail2, this.tail3, this.rightToeClaw2, this.tail4, this.TailPlume, this.TailPlume_1, this.leftLeg, this.leftFoot, this.rightLeg, this.rightFoot);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        this.animator.update(entity);
        if (this.animator.setAnimation(EntityCockatrice.ANIMATION_EAT)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.neck2, 7, 0, 0);
            this.rotate(this.animator, this.head, 45, 0, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.upperBody, 15, 0, 0);
            this.rotate(this.animator, this.neck, 31, 0, 0);
            this.rotate(this.animator, this.neck2, 7, 0, 0);
            this.rotate(this.animator, this.head, 45, 0, 0);
            this.rotate(this.animator, this.lowerJaw, 15, 0, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.upperBody, 5, 0, 0);
            this.rotate(this.animator, this.neck, -10, 0, 0);
            this.rotate(this.animator, this.neck2, 1, 0, 0);
            this.rotate(this.animator, this.head, 45, 0, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        if (this.animator.setAnimation(EntityCockatrice.ANIMATION_JUMPAT)) {
            this.animator.startKeyframe(10);
            this.rotate(this.animator, this.lowerBody, 18, 0, 0);
            this.rotate(this.animator, this.upperBody, 5, 0, 0);
            this.rotate(this.animator, this.neck, -23, 0, 0);
            this.rotate(this.animator, this.neck2, 18, 0, 0);
            this.rotate(this.animator, this.head, -27, 0, 0);
            this.rotate(this.animator, this.leftUpperArm, 0, 30, -50);
            this.rotate(this.animator, this.RightUpperArm, 0, -30, 50);
            this.animator.move(this.lowerBody, 0, 1.7F, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.jumpPos();
            this.animator.endKeyframe();
            this.animator.startKeyframe(2);
            this.jumpPos();
            this.rotate(this.animator, this.leftUpperArm, 0, 30, -75);
            this.rotate(this.animator, this.RightUpperArm, 0, -30, 75);
            this.animator.endKeyframe();
            this.animator.startKeyframe(2);
            this.jumpPos();
            this.rotate(this.animator, this.leftUpperArm, 0, 30, -19);
            this.rotate(this.animator, this.RightUpperArm, 0, -30, 19);
            this.animator.endKeyframe();
            this.animator.startKeyframe(2);
            this.jumpPos();
            this.rotate(this.animator, this.leftUpperArm, 0, 30, -75);
            this.rotate(this.animator, this.RightUpperArm, 0, -30, 75);
            this.animator.endKeyframe();
            this.animator.startKeyframe(2);
            this.jumpPos();
            this.rotate(this.animator, this.leftUpperArm, 0, 30, -19);
            this.rotate(this.animator, this.RightUpperArm, 0, -30, 19);
            this.animator.endKeyframe();
            this.animator.startKeyframe(2);
            this.jumpPos();
            this.rotate(this.animator, this.leftUpperArm, 0, 30, -75);
            this.rotate(this.animator, this.RightUpperArm, 0, -30, 75);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        if (this.animator.setAnimation(EntityCockatrice.ANIMATION_WATTLESHAKE)) {
            this.animator.startKeyframe(3);
            this.rotate(this.animator, this.neck, 0, 0, -23);
            this.rotate(this.animator, this.neck2, 0, 0, -13);
            this.rotate(this.animator, this.head, 5, 0, -15);
            this.rotate(this.animator, this.Comb1, 0, 0, -23);
            this.rotate(this.animator, this.Comb2, 0, 0, -27);
            this.rotate(this.animator, this.Wattle, 0, 0, -23);
            this.animator.endKeyframe();
            this.animator.startKeyframe(2);
            this.rotate(this.animator, this.neck, 0, 0, -23);
            this.rotate(this.animator, this.neck2, 0, 0, -13);
            this.rotate(this.animator, this.head, 5, 0, -15);
            this.rotate(this.animator, this.Comb1, 0, 0, -33);
            this.rotate(this.animator, this.Comb2, 0, 0, -37);
            this.rotate(this.animator, this.Wattle, 0, 0, -33);
            this.animator.endKeyframe();
            this.animator.startKeyframe(3);
            this.rotate(this.animator, this.neck, 0, 0, 23);
            this.rotate(this.animator, this.neck2, 0, 0, 13);
            this.rotate(this.animator, this.head, 5, 0, 15);
            this.rotate(this.animator, this.Comb1, 0, 0, 23);
            this.rotate(this.animator, this.Comb2, 0, 0, 27);
            this.rotate(this.animator, this.Wattle, 0, 0, 23);
            this.animator.endKeyframe();
            this.animator.startKeyframe(2);
            this.rotate(this.animator, this.neck, 0, 0, 23);
            this.rotate(this.animator, this.neck2, 0, 0, 13);
            this.rotate(this.animator, this.head, 5, 0, 15);
            this.rotate(this.animator, this.Comb1, 0, 0, 33);
            this.rotate(this.animator, this.Comb2, 0, 0, 37);
            this.rotate(this.animator, this.Wattle, 0, 0, 33);
            this.animator.endKeyframe();
            this.animator.startKeyframe(3);
            this.rotate(this.animator, this.neck, 0, 0, -23);
            this.rotate(this.animator, this.neck2, 0, 0, -13);
            this.rotate(this.animator, this.head, 5, 0, -15);
            this.rotate(this.animator, this.Comb1, 0, 0, -23);
            this.rotate(this.animator, this.Comb2, 0, 0, -27);
            this.rotate(this.animator, this.Wattle, 0, 0, -23);
            this.animator.endKeyframe();
            this.animator.startKeyframe(2);
            this.rotate(this.animator, this.neck, 0, 0, -23);
            this.rotate(this.animator, this.neck2, 0, 0, -13);
            this.rotate(this.animator, this.head, 5, 0, -15);
            this.rotate(this.animator, this.Comb1, 0, 0, -33);
            this.rotate(this.animator, this.Comb2, 0, 0, -37);
            this.rotate(this.animator, this.Wattle, 0, 0, -33);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        if (this.animator.setAnimation(EntityCockatrice.ANIMATION_BITE)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.neck, -47, 0, 0);
            this.rotate(this.animator, this.neck2, 17, 0, 0);
            this.rotate(this.animator, this.head, 46, 0, 0);
            this.rotate(this.animator, this.lowerJaw, 10, 0, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.neck, 26, 0, 0);
            this.rotate(this.animator, this.neck2, -18, 0, 0);
            this.rotate(this.animator, this.head, 2, 0, 0);
            this.rotate(this.animator, this.lowerJaw, 33, 0, 0);
            this.rotate(this.animator, this.upperJaw, -20, 0, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        if (this.animator.setAnimation(EntityCockatrice.ANIMATION_SPEAK)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.lowerJaw, 25, 0, 0);
            this.animator.resetKeyframe(5);
        }
        this.animator.update(null);
    }

    private void jumpPos() {
        this.rotate(this.animator, this.lowerBody, -29, 0, 0);
        this.rotate(this.animator, this.upperBody, 10, 0, 0);
        this.rotate(this.animator, this.neck, 7, 0, 0);
        this.rotate(this.animator, this.neck2, 32, 0, 0);
        this.rotate(this.animator, this.head, 36, 0, 0);
        this.rotate(this.animator, this.lowerJaw, 28, 0, 0);
        this.rotate(this.animator, this.tail1, -18, 0, 0);
        this.animator.move(this.lowerBody, 0, -1.9F, 2.5F);
        this.rotate(this.animator, this.rightThigh, -74, 0, 10);
        this.rotate(this.animator, this.rightLeg, 0, 39, 0);
        this.rotate(this.animator, this.rightFoot, 50, -10, 0);
        this.rotate(this.animator, this.leftThigh, -74, 0, -10);
        this.rotate(this.animator, this.leftLeg, 0, -39, 0);
        this.rotate(this.animator, this.leftFoot, 50, 10, 0);
    }

    @Override
    public void setAngles(EntityCockatrice entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.animate(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 0.5F;
        float degree_idle = 0.5F;
        AdvancedModelBox[] NECK = new AdvancedModelBox[]{this.neck, this.neck2, this.head};
        AdvancedModelBox[] TAIL = new AdvancedModelBox[]{this.tail1, this.tail2, this.tail3, this.tail4};
        this.chainWave(NECK, speed_idle, degree_idle * 0.1F, 4, animationProgress, 1);
        this.chainSwing(TAIL, speed_idle, degree_idle * 0.5F, 0, animationProgress, 1);
        this.walk(this.lowerBody, speed_idle, degree_idle * 0.1F, false, 0, 0.1F, animationProgress, 1);
        this.walk(this.upperBody, speed_idle, degree_idle * 0.05F, true, 1, 0F, animationProgress, 1);

        this.walk(this.leftUpperArm, speed_idle, degree_idle * 0.2F, false, 1, 0.1F, animationProgress, 1);
        this.walk(this.RightUpperArm, speed_idle, degree_idle * 0.2F, false, 1, 0.1F, animationProgress, 1);
        this.walk(this.leftLowerArm, speed_idle, degree_idle * 0.2F, false, 1, 0.1F, animationProgress, 1);
        this.walk(this.RightLowerArm, speed_idle, degree_idle * 0.2F, false, 1, 0.1F, animationProgress, 1);

        this.flap(this.leftUpperArm, speed_idle, degree_idle * 0.2F, true, 2, -0.3F, animationProgress, 1);
        this.flap(this.RightUpperArm, speed_idle, degree_idle * 0.2F, false, 2, -0.3F, animationProgress, 1);
        this.faceTarget(headYaw, headPitch, 2, this.head);
        this.faceTarget(headYaw, headPitch, 2, this.neck);

        this.chainWave(NECK, speed_walk, degree_walk * 0.5F, 4, limbAngle, limbDistance);
        this.walk(this.lowerBody, speed_walk, degree_walk * 0.1F, false, 0, 0F, limbAngle, limbDistance);
        this.walk(this.upperBody, speed_walk, degree_walk * 0.05F, true, 1, 0F, limbAngle, limbDistance);
        this.walk(this.leftThigh, speed_walk, degree_walk, true, 1, 0.1F, limbAngle, limbDistance);
        this.walk(this.rightThigh, speed_walk, degree_walk, false, 1, 0.1F, limbAngle, limbDistance);
        this.walk(this.leftLeg, speed_walk, degree_walk, true, 1, 0.1F, limbAngle, limbDistance);
        this.walk(this.rightLeg, speed_walk, degree_walk, false, 1, 0.1F, limbAngle, limbDistance);
        this.walk(this.leftFoot, speed_walk, degree_walk * -1.75F, true, 1, -0.1F, limbAngle, limbDistance);
        this.walk(this.rightFoot, speed_walk, degree_walk * -1.75F, false, 1, -0.1F, limbAngle, limbDistance);
        this.progressRotation(this.neck, entity.stareProgress, (float) Math.toRadians(10), 0.0F, 0.0F);
        this.progressRotation(this.neck2, entity.stareProgress, (float) Math.toRadians(-18), 0.0F, 0.0F);
        this.progressRotation(this.head, entity.stareProgress, (float) Math.toRadians(18), 0.0F, 0.0F);

        this.progressRotation(this.rightThigh, entity.sitProgress, (float) Math.toRadians(-15), 0.0F, 0.0F);
        this.progressRotation(this.leftThigh, entity.sitProgress, (float) Math.toRadians(-15), 0.0F, 0.0F);
        this.progressRotation(this.rightLeg, entity.sitProgress, (float) Math.toRadians(13), 0.0F, 0.0F);
        this.progressRotation(this.leftLeg, entity.sitProgress, (float) Math.toRadians(13), 0.0F, 0.0F);
        this.progressRotation(this.rightFoot, entity.sitProgress, 0.0F, 0.0F, 0.0F);
        this.progressRotation(this.leftFoot, entity.sitProgress, 0.0F, 0.0F, 0.0F);
        this.progressPosition(this.rightThigh, entity.sitProgress, -3.0F, 19F, 3.0F);
        this.progressPosition(this.leftThigh, entity.sitProgress, 3.0F, 19F, 3.0F);
        this.progressPosition(this.lowerBody, entity.sitProgress, 0.0F, 12.9F, -2.5F);
    }


    @Override
    public void renderStatue(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
