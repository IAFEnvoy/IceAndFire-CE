package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.entity.EntityAmphithere;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ModelAmphithere extends ModelDragonBase<EntityAmphithere> {
    public final AdvancedModelBox BodyUpper;
    public final AdvancedModelBox BodyLower;
    public final AdvancedModelBox Neck1;
    public final AdvancedModelBox WingL;
    public final AdvancedModelBox WingR;
    public final AdvancedModelBox Tail1;
    public final AdvancedModelBox Tail2;
    public final AdvancedModelBox Tail3;
    public final AdvancedModelBox Tail4;
    public final AdvancedModelBox Club;
    public final AdvancedModelBox TailR1;
    public final AdvancedModelBox TailL1;
    public final AdvancedModelBox TailL2;
    public final AdvancedModelBox TailR2;
    public final AdvancedModelBox Neck2;
    public final AdvancedModelBox Neck3;
    public final AdvancedModelBox Head;
    public final AdvancedModelBox HeadFront;
    public final AdvancedModelBox Jaw;
    public final AdvancedModelBox CrestL1;
    public final AdvancedModelBox CrestL2;
    public final AdvancedModelBox CrestR2;
    public final AdvancedModelBox CrestR1;
    public final AdvancedModelBox CrestR3;
    public final AdvancedModelBox CrestL3;
    public final AdvancedModelBox CrestRB;
    public final AdvancedModelBox CrestLB;
    public final AdvancedModelBox Beak;
    public final AdvancedModelBox Teeth2;
    public final AdvancedModelBox Teeth1;
    public final AdvancedModelBox WingL2;
    public final AdvancedModelBox WingL3;
    public final AdvancedModelBox WingL21;
    public final AdvancedModelBox FingerL1;
    public final AdvancedModelBox FingerL2;
    public final AdvancedModelBox FingerL3;
    public final AdvancedModelBox FingerL4;
    public final AdvancedModelBox WingR2;
    public final AdvancedModelBox WingR3;
    public final AdvancedModelBox WingR21;
    public final AdvancedModelBox FingerR1;
    public final AdvancedModelBox FingerR2;
    public final AdvancedModelBox FingerR3;
    public final AdvancedModelBox FingerR4;
    private final ModelAnimator animator;

    public ModelAmphithere() {
        this.texWidth = 256;
        this.texHeight = 128;
        this.FingerR3 = new AdvancedModelBox(this, 40, 80);
        this.FingerR3.mirror = true;
        this.FingerR3.setPos(0.0F, 11.0F, 4.5F);
        this.FingerR3.addBox(-0.2F, -0.1F, -2.0F, 1, 16, 3, 0.0F);
        this.setRotateAngle(this.FingerR3, 0.13962634015954636F, 0.0F, 0.0F);
        this.TailR1 = new AdvancedModelBox(this, 120, 7);
        this.TailR1.mirror = true;
        this.TailR1.setPos(0.0F, 0.8F, 8.0F);
        this.TailR1.addBox(-0.5F, 0.0F, 0.0F, 3, 13, 1, 0.0F);
        this.setRotateAngle(this.TailR1, 1.3962634015954636F, 0.06981317007977318F, 0.0F);
        this.Teeth2 = new AdvancedModelBox(this, 6, 16);
        this.Teeth2.mirror = true;
        this.Teeth2.setPos(0.0F, -0.4F, -3.5F);
        this.Teeth2.addBox(-0.6F, 0.5F, -4.62F, 2, 1, 5, 0.0F);
        this.HeadFront = new AdvancedModelBox(this, 8, 32);
        this.HeadFront.setPos(0.0F, 0.7F, 0.0F);
        this.HeadFront.addBox(-1.5F, -2.8F, -8.8F, 3, 3, 5, 0.0F);
        this.setRotateAngle(this.HeadFront, -0.22759093446006054F, -0.0F, 0.0F);
        this.Head = new AdvancedModelBox(this, 6, 54);
        this.Head.setPos(0.0F, 1.7F, -5.5F);
        this.Head.addBox(-2.0F, -3.0F, -4.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(this.Head, 0.5462880558742251F, -0.0F, 0.0F);
        this.CrestL3 = new AdvancedModelBox(this, 134, 12);
        this.CrestL3.mirror = true;
        this.CrestL3.setPos(1.1F, -1.3F, -1.0F);
        this.CrestL3.addBox(-1.0F, -1.3F, 0.0F, 2, 5, 1, 0.0F);
        this.setRotateAngle(this.CrestL3, 1.7453292519943295F, 0.8726646259971648F, 0.7853981633974483F);
        this.TailL2 = new AdvancedModelBox(this, 100, 8);
        this.TailL2.mirror = true;
        this.TailL2.setPos(0.0F, 0.7F, 1.7F);
        this.TailL2.addBox(-1.0F, 0.0F, 0.0F, 4, 14, 1, 0.0F);
        this.setRotateAngle(this.TailL2, 1.3962634015954636F, 0.31869712141416456F, 0.0F);
        this.FingerL2 = new AdvancedModelBox(this, 50, 80);
        this.FingerL2.setPos(-0.1F, 11.0F, 2.0F);
        this.FingerL2.addBox(-0.8F, -0.1F, -2.0F, 1, 14, 3, 0.0F);
        this.setRotateAngle(this.FingerL2, 0.06981317007977318F, 0.0F, 0.0F);
        this.BodyUpper = new AdvancedModelBox(this, 67, 47);
        this.BodyUpper.setPos(0.0F, 15.8F, -10.0F);
        this.BodyUpper.addBox(-3.5F, -2.1F, 0.0F, 7, 8, 7, 0.0F);
        this.CrestLB = new AdvancedModelBox(this, 134, 12);
        this.CrestLB.mirror = true;
        this.CrestLB.setPos(1.1F, -1.4F, 1.2F);
        this.CrestLB.addBox(-1.0F, 0.0F, 0.0F, 2, 5, 1, 0.0F);
        this.setRotateAngle(this.CrestLB, 1.7453292519943295F, 0.27314402793711257F, 0.0F);
        this.TailL1 = new AdvancedModelBox(this, 120, 7);
        this.TailL1.setPos(0.0F, 0.8F, 8.0F);
        this.TailL1.addBox(-2.5F, 0.0F, 0.0F, 3, 13, 1, 0.0F);
        this.setRotateAngle(this.TailL1, 1.3962634015954636F, -0.06981317007977318F, 0.0F);
        this.CrestR3 = new AdvancedModelBox(this, 134, 12);
        this.CrestR3.setPos(-1.1F, -1.3F, -1.0F);
        this.CrestR3.addBox(-1.0F, -1.3F, 0.0F, 2, 5, 1, 0.0F);
        this.setRotateAngle(this.CrestR3, 1.7453292519943295F, -0.8726646259971648F, -0.7285004297824331F);
        this.Jaw = new AdvancedModelBox(this, 34, 56);
        this.Jaw.setPos(0.0F, 0.7F, -3.1F);
        this.Jaw.addBox(-1.5F, -0.5F, -5.5F, 3, 1, 5, 0.0F);
        this.setRotateAngle(this.Jaw, -0.3141592653589793F, -0.0F, 0.0F);
        this.CrestR2 = new AdvancedModelBox(this, 134, 12);
        this.CrestR2.setPos(-1.1F, -1.3F, -1.0F);
        this.CrestR2.addBox(-1.0F, 0.0F, 0.0F, 2, 5, 1, 0.0F);
        this.setRotateAngle(this.CrestR2, 1.7453292519943295F, -0.6108652381980153F, -0.3141592653589793F);
        this.Teeth1 = new AdvancedModelBox(this, 6, 16);
        this.Teeth1.setPos(0.0F, -0.4F, -3.5F);
        this.Teeth1.addBox(-1.2F, 0.5F, -4.62F, 2, 1, 5, 0.0F);
        this.WingL3 = new AdvancedModelBox(this, 96, 67);
        this.WingL3.mirror = true;
        this.WingL3.setPos(0.0F, 7.6F, 0.0F);
        this.WingL3.addBox(-0.7F, -0.1F, -2.0F, 1, 14, 10, 0.0F);
        this.setRotateAngle(this.WingL3, 0.5235987755982988F, 0.0F, 0.0F);
        this.WingR21 = new AdvancedModelBox(this, 80, 90);
        this.WingR21.setPos(0.5F, 0.0F, 0.0F);
        this.WingR21.addBox(-0.78F, -2.5F, -2.1F, 1, 11, 11, 0.0F);
        this.FingerR1 = new AdvancedModelBox(this, 60, 80);
        this.FingerR1.mirror = true;
        this.FingerR1.setPos(0.0F, 11.0F, 0.1F);
        this.FingerR1.addBox(-0.2F, -0.1F, -2.0F, 1, 11, 3, 0.0F);
        this.setRotateAngle(this.FingerR1, 0.03490658503988659F, 0.0F, 0.0F);
        this.BodyLower = new AdvancedModelBox(this, 95, 47);
        this.BodyLower.setPos(0.0F, 0.3F, 6.7F);
        this.BodyLower.addBox(-3.0F, -1.5F, -1.6F, 6, 6, 10, 0.0F);
        this.setRotateAngle(this.BodyLower, 0.045553093477052F, 0.0F, 0.0F);
        this.Tail1 = new AdvancedModelBox(this, 69, 34);
        this.Tail1.setPos(0.0F, 1.1F, 7.0F);
        this.Tail1.addBox(-2.0F, -2.1F, -0.4F, 4, 5, 8, 0.0F);
        this.setRotateAngle(this.Tail1, -0.045553093477052F, 0.0F, 0.0F);
        this.FingerR4 = new AdvancedModelBox(this, 30, 80);
        this.FingerR4.mirror = true;
        this.FingerR4.setPos(0.0F, 11.6F, 6.6F);
        this.FingerR4.addBox(-0.1F, -0.1F, -2.0F, 1, 11, 3, 0.0F);
        this.setRotateAngle(this.FingerR4, 0.2617993877991494F, 0.0F, 0.0F);
        this.WingR2 = new AdvancedModelBox(this, 80, 90);
        this.WingR2.setPos(-0.4F, 7.6F, -2.8F);
        this.WingR2.addBox(-0.4F, -2.5F, -2.1F, 1, 11, 11, 0.0F);
        this.setRotateAngle(this.WingR2, -0.3490658503988659F, 0.0F, 0.0F);
        this.WingR = new AdvancedModelBox(this, 100, 107);
        this.WingR.setPos(-3.0F, -0.5F, 5.0F);
        this.WingR.addBox(-0.9F, 0.0F, -5.0F, 1, 8, 12, 0.0F);
        this.setRotateAngle(this.WingR, 0.08726646259971647F, 0.0F, 1.3962634015954636F);
        this.WingL2 = new AdvancedModelBox(this, 80, 90);
        this.WingL2.mirror = true;
        this.WingL2.setPos(0.4F, 7.6F, -2.8F);
        this.WingL2.addBox(-0.6F, -2.5F, -2.1F, 1, 11, 11, 0.0F);
        this.setRotateAngle(this.WingL2, -0.3490658503988659F, 0.0F, 0.0F);
        this.CrestL2 = new AdvancedModelBox(this, 134, 12);
        this.CrestL2.mirror = true;
        this.CrestL2.setPos(1.1F, -1.3F, -1.0F);
        this.CrestL2.addBox(-1.0F, 0.0F, 0.0F, 2, 5, 1, 0.0F);
        this.setRotateAngle(this.CrestL2, 1.7453292519943295F, 0.6108652381980153F, 0.3141592653589793F);
        this.FingerL3 = new AdvancedModelBox(this, 40, 80);
        this.FingerL3.setPos(0.0F, 11.0F, 4.4F);
        this.FingerL3.addBox(-0.8F, -0.1F, -2.0F, 1, 16, 3, 0.0F);
        this.setRotateAngle(this.FingerL3, 0.13962634015954636F, 0.0F, 0.0F);
        this.FingerR2 = new AdvancedModelBox(this, 50, 80);
        this.FingerR2.mirror = true;
        this.FingerR2.setPos(0.1F, 11.0F, 2.0F);
        this.FingerR2.addBox(-0.2F, -0.1F, -2.0F, 1, 14, 3, 0.0F);
        this.setRotateAngle(this.FingerR2, 0.06981317007977318F, 0.0F, 0.0F);
        this.FingerL1 = new AdvancedModelBox(this, 60, 80);
        this.FingerL1.setPos(0.0F, 11.0F, 0.1F);
        this.FingerL1.addBox(-0.8F, -0.1F, -2.0F, 1, 11, 3, 0.0F);
        this.setRotateAngle(this.FingerL1, 0.03490658503988659F, 0.0F, 0.0F);
        this.Beak = new AdvancedModelBox(this, 7, 45);
        this.Beak.setPos(0.0F, -1.7F, -7.7F);
        this.Beak.addBox(-1.0F, -1.4F, -3.0F, 2, 3, 3, 0.0F);
        this.setRotateAngle(this.Beak, 0.6981317007977318F, -0.0F, 0.0F);
        this.WingL = new AdvancedModelBox(this, 100, 107);
        this.WingL.mirror = true;
        this.WingL.setPos(3.0F, -0.5F, 5.0F);
        this.WingL.addBox(-0.1F, 0.0F, -5.0F, 1, 8, 12, 0.0F);
        this.setRotateAngle(this.WingL, 0.08726646259971647F, 0.0F, -1.3962634015954636F);
        this.CrestL1 = new AdvancedModelBox(this, 134, 12);
        this.CrestL1.setPos(0.9F, -2.0F, -1.0F);
        this.CrestL1.addBox(-1.0F, 0.0F, 0.0F, 2, 5, 1, 0.0F);
        this.setRotateAngle(this.CrestL1, 1.7453292519943295F, 0.08726646259971647F, 0.0F);
        this.Tail3 = new AdvancedModelBox(this, 70, 21);
        this.Tail3.setPos(0.0F, -0.1F, 7.7F);
        this.Tail3.addBox(-1.0F, -1.3F, -0.2F, 2, 3, 9, 0.0F);
        this.setRotateAngle(this.Tail3, 0.091106186954104F, 0.0F, 0.0F);
        this.Club = new AdvancedModelBox(this, 42, 17);
        this.Club.setPos(0.0F, -0.7F, 7.5F);
        this.Club.addBox(-2.0F, -0.4F, 0.3F, 4, 2, 8, 0.0F);
        this.setRotateAngle(this.Club, 0.091106186954104F, 0.0F, 0.0F);
        this.Neck2 = new AdvancedModelBox(this, 37, 44);
        this.Neck2.setPos(0.0F, -0.1F, -3.8F);
        this.Neck2.addBox(-1.5F, -1.7F, -6.4F, 3, 5, 7, 0.0F);
        this.setRotateAngle(this.Neck2, -0.091106186954104F, -0.0F, 0.0F);
        this.CrestR1 = new AdvancedModelBox(this, 134, 12);
        this.CrestR1.mirror = true;
        this.CrestR1.setPos(-0.9F, -2.0F, -1.0F);
        this.CrestR1.addBox(-1.0F, 0.0F, 0.0F, 2, 5, 1, 0.0F);
        this.setRotateAngle(this.CrestR1, 1.7453292519943295F, -0.08726646259971647F, 0.0F);
        this.WingL21 = new AdvancedModelBox(this, 80, 90);
        this.WingL21.mirror = true;
        this.WingL21.setPos(-0.5F, 0.0F, 0.0F);
        this.WingL21.addBox(-0.22F, -2.5F, -2.1F, 1, 11, 11, 0.0F);
        this.Neck1 = new AdvancedModelBox(this, 57, 64);
        this.Neck1.setPos(0.0F, 0.6F, 1.8F);
        this.Neck1.addBox(-2.0F, -2.3F, -5.0F, 4, 6, 6, 0.0F);
        this.setRotateAngle(this.Neck1, 0.136659280431156F, -0.0F, -0.045553093477052F);
        this.FingerL4 = new AdvancedModelBox(this, 30, 80);
        this.FingerL4.setPos(0.0F, 11.0F, 6.6F);
        this.FingerL4.addBox(-0.9F, -0.1F, -2.0F, 1, 11, 3, 0.0F);
        this.setRotateAngle(this.FingerL4, 0.2617993877991494F, 0.0F, 0.0F);
        this.Tail2 = new AdvancedModelBox(this, 95, 34);
        this.Tail2.setPos(0.0F, -0.1F, 6.6F);
        this.Tail2.addBox(-1.51F, -1.7F, -0.1F, 3, 4, 8, 0.0F);
        this.setRotateAngle(this.Tail2, -0.091106186954104F, 0.0F, 0.0F);
        this.Neck3 = new AdvancedModelBox(this, 25, 64);
        this.Neck3.setPos(0.0F, 0.0F, -5.1F);
        this.Neck3.addBox(-1.5F, -1.1F, -7.0F, 3, 4, 8, 0.0F);
        this.setRotateAngle(this.Neck3, -0.18203784098300857F, -0.0F, 0.0F);
        this.Tail4 = new AdvancedModelBox(this, 70, 22);
        this.Tail4.setPos(0.0F, 0.3F, 9.0F);
        this.Tail4.addBox(-1.02F, -1.3F, -0.6F, 2, 2, 9, 0.0F);
        this.setRotateAngle(this.Tail4, -0.045553093477052F, 0.0F, 0.0F);
        this.TailR2 = new AdvancedModelBox(this, 100, 8);
        this.TailR2.setPos(-2.0F, 0.7F, 1.7F);
        this.TailR2.addBox(-1.0F, 0.0F, 0.0F, 4, 14, 1, 0.0F);
        this.setRotateAngle(this.TailR2, 1.3962634015954636F, -0.31869712141416456F, 0.0F);
        this.CrestRB = new AdvancedModelBox(this, 134, 12);
        this.CrestRB.setPos(-1.1F, -1.4F, 1.2F);
        this.CrestRB.addBox(-1.0F, 0.0F, 0.0F, 2, 5, 1, 0.0F);
        this.setRotateAngle(this.CrestRB, 1.7453292519943295F, -0.27314402793711257F, 0.0F);
        this.WingR3 = new AdvancedModelBox(this, 96, 67);
        this.WingR3.setPos(0.0F, 7.6F, 0.0F);
        this.WingR3.addBox(-0.3F, -0.1F, -2.0F, 1, 14, 10, 0.0F);
        this.setRotateAngle(this.WingR3, 0.5235987755982988F, 0.0F, 0.0F);
        this.WingR3.addChild(this.FingerR3);
        this.Club.addChild(this.TailR1);
        this.HeadFront.addChild(this.Teeth2);
        this.Head.addChild(this.HeadFront);
        this.Neck3.addChild(this.Head);
        this.Head.addChild(this.CrestL3);
        this.Club.addChild(this.TailL2);
        this.WingL3.addChild(this.FingerL2);
        this.Head.addChild(this.CrestLB);
        this.Club.addChild(this.TailL1);
        this.Head.addChild(this.CrestR3);
        this.Head.addChild(this.Jaw);
        this.Head.addChild(this.CrestR2);
        this.HeadFront.addChild(this.Teeth1);
        this.WingL2.addChild(this.WingL3);
        this.WingR2.addChild(this.WingR21);
        this.WingR3.addChild(this.FingerR1);
        this.BodyUpper.addChild(this.BodyLower);
        this.BodyLower.addChild(this.Tail1);
        this.WingR3.addChild(this.FingerR4);
        this.WingR.addChild(this.WingR2);
        this.BodyUpper.addChild(this.WingR);
        this.WingL.addChild(this.WingL2);
        this.Head.addChild(this.CrestL2);
        this.WingL3.addChild(this.FingerL3);
        this.WingR3.addChild(this.FingerR2);
        this.WingL3.addChild(this.FingerL1);
        this.HeadFront.addChild(this.Beak);
        this.BodyUpper.addChild(this.WingL);
        this.Head.addChild(this.CrestL1);
        this.Tail2.addChild(this.Tail3);
        this.Tail4.addChild(this.Club);
        this.Neck1.addChild(this.Neck2);
        this.Head.addChild(this.CrestR1);
        this.WingL2.addChild(this.WingL21);
        this.BodyUpper.addChild(this.Neck1);
        this.WingL3.addChild(this.FingerL4);
        this.Tail1.addChild(this.Tail2);
        this.Neck2.addChild(this.Neck3);
        this.Tail3.addChild(this.Tail4);
        this.Club.addChild(this.TailR2);
        this.Head.addChild(this.CrestRB);
        this.WingR2.addChild(this.WingR3);
        this.Jaw.setScale(0.99F, 0.99F, 0.99F);
        this.Neck3.setScale(0.99F, 0.99F, 0.99F);
        this.animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.BodyUpper);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.BodyUpper, this.BodyLower, this.Neck1, this.WingL, this.WingR, this.Tail1, this.Tail2, this.Tail3, this.Tail4,
                this.Club, this.TailR1, this.TailL1, this.TailL2, this.TailR2, this.Neck2, this.Neck3, this.Head, this.HeadFront, this.Jaw,
                this.CrestL1, this.CrestL2, this.CrestR2, this.CrestR1, this.CrestR3, this.CrestL3, this.CrestRB, this.CrestLB,
                this.Beak, this.Teeth2, this.Teeth1, this.WingL2, this.WingL3, this.WingL21, this.FingerL1, this.FingerL2, this.FingerL3,
                this.FingerL4, this.WingR2, this.WingR3, this.WingR21, this.FingerR1, this.FingerR2, this.FingerR3, this.FingerR4);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.animator.update(entity);
        if (this.animator.setAnimation(EntityAmphithere.ANIMATION_BITE)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Neck1, -39, 0, 0);
            this.rotate(this.animator, this.Neck2, -18, 0, 0);
            this.rotate(this.animator, this.Neck3, -11, 0, 0);
            this.rotate(this.animator, this.Head, 60, 0, 0);
            this.rotate(this.animator, this.Jaw, 41, 0, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Neck1, -19, 0, 0);
            this.rotate(this.animator, this.Neck2, 23, 0, 0);
            this.rotate(this.animator, this.Neck3, 7, 0, 0);
            this.rotate(this.animator, this.Head, 20, 0, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        if (this.animator.setAnimation(EntityAmphithere.ANIMATION_BITE_RIDER)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Neck1, 8, 0, 63);
            this.rotate(this.animator, this.Neck2, -5, 35, 13);
            this.rotate(this.animator, this.Neck3, -10, 70, 0);
            this.rotate(this.animator, this.Head, 20, 30, 95);
            this.rotate(this.animator, this.Jaw, 40, 0, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Neck1, 8, 0, 63);
            this.rotate(this.animator, this.Neck2, -5, 35, 13);
            this.rotate(this.animator, this.Neck3, -10, 70, 0);
            this.rotate(this.animator, this.Head, 50, 40, 95);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        if (this.animator.setAnimation(EntityAmphithere.ANIMATION_WING_BLAST)) {
            this.animator.startKeyframe(5);
            this.wingBlastPose();
            this.rotateMinus(this.animator, this.WingR, 32, 0, 170);
            this.rotateMinus(this.animator, this.WingL, 32, 0, -170);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.wingBlastPose();
            this.rotateMinus(this.animator, this.WingR, 15, 0, 45);
            this.rotateMinus(this.animator, this.WingL, 15, 0, -45);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.wingBlastPose();
            this.rotateMinus(this.animator, this.WingR, 32, 0, 170);
            this.rotateMinus(this.animator, this.WingL, 32, 0, -170);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.wingBlastPose();
            this.rotateMinus(this.animator, this.WingR, 15, 0, 45);
            this.rotateMinus(this.animator, this.WingL, 15, 0, -45);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(10);
        }
        if (this.animator.setAnimation(EntityAmphithere.ANIMATION_TAIL_WHIP)) {
            this.animator.startKeyframe(10);
            this.rotate(this.animator, this.BodyUpper, 0, 30, 0);
            this.rotate(this.animator, this.BodyLower, 0, 10, 0);
            this.rotate(this.animator, this.Tail1, 0, 15, 0);
            this.rotate(this.animator, this.Tail2, 0, 10, 0);
            this.rotate(this.animator, this.Tail2, 0, 10, 0);
            this.rotate(this.animator, this.Tail3, 0, 20, 0);
            this.rotate(this.animator, this.Tail4, 0, 15, -20);
            this.rotate(this.animator, this.Neck1, 0, -18, -20);
            this.rotate(this.animator, this.Neck2, 0, -29, 0);
            this.rotate(this.animator, this.Neck3, 0, -44, 0);
            this.rotate(this.animator, this.Head, 31, -28, 0);
            this.rotate(this.animator, this.WingL, 0, 18, 0);
            this.rotate(this.animator, this.WingR, 0, -18, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.BodyUpper, 0, -214, 0);
            this.rotate(this.animator, this.BodyLower, 0, -10, 0);
            this.rotate(this.animator, this.Tail1, 0, -15, 0);
            this.rotate(this.animator, this.Tail2, 0, -7, 0);
            this.rotate(this.animator, this.Tail2, 0, -7, 0);
            this.rotate(this.animator, this.Tail3, 0, -7, 0);
            this.rotate(this.animator, this.Tail4, 0, -15, -20);
            this.rotate(this.animator, this.Neck1, 0, 18, -20);
            this.rotate(this.animator, this.Neck2, 0, 29, 0);
            this.rotate(this.animator, this.Neck3, 0, 44, 0);
            this.rotate(this.animator, this.Head, 31, -28, 0);
            this.rotate(this.animator, this.WingL, 0, 18, 0);
            this.rotate(this.animator, this.WingR, 0, -18, 0);
            this.animator.endKeyframe();
            this.animator.setStaticKeyframe(3);
            this.animator.resetKeyframe(7);
            this.animator.endKeyframe();
        }
        if (this.animator.setAnimation(EntityAmphithere.ANIMATION_SPEAK)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Jaw, 31, 0, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        this.animator.update(null);
    }

    private void wingBlastPose() {
        this.animator.move(this.BodyUpper, 0, -36, 20);
        this.rotateMinus(this.animator, this.BodyUpper, -80, 0, 0);
        this.rotateMinus(this.animator, this.BodyLower, -10, 0, 0);
        this.rotateMinus(this.animator, this.Tail1, -10, 0, 0);
        this.rotateMinus(this.animator, this.Tail1, -10, 0, 0);
        this.rotateMinus(this.animator, this.Tail2, -10, 0, 0);
        this.rotateMinus(this.animator, this.Tail3, -10, 0, 0);
        this.rotateMinus(this.animator, this.Tail4, -10, 0, 0);
        this.rotateMinus(this.animator, this.Club, -25, 0, 0);
        this.rotate(this.animator, this.Neck1, 20, 0, 0);
        this.rotate(this.animator, this.Neck2, 32, 0, 0);
        this.rotate(this.animator, this.Neck3, 2, 0, 0);
        this.rotate(this.animator, this.Head, 80, 0, 0);
        this.rotateMinus(this.animator, this.WingR2, -20, 0, -20);
        this.rotateMinus(this.animator, this.WingL2, -20, 0, 20);
        this.rotateMinus(this.animator, this.WingR3, 16, 0, 0);
        this.rotateMinus(this.animator, this.WingL3, 16, 0, 0);
    }

    @Override
    public void setAngles(EntityAmphithere entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.resetToDefaultPose();
        this.animate(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 0);
        if (this.child) {
            this.BodyUpper.setShouldScaleChildren(true);
            this.HeadFront.setShouldScaleChildren(true);
            this.Jaw.setShouldScaleChildren(true);
            this.BodyUpper.setScale(0.5F, 0.5F, 0.5F);
            this.Head.setScale(1.5F, 1.5F, 1.5F);
            this.HeadFront.setScale(1, 1, 1);
            this.HeadFront.offsetZ = -2F;
            this.Jaw.offsetZ = -4.5F;
        } else {
            this.BodyUpper.setScale(1F, 1F, 1F);
            this.Head.setScale(1F, 1F, 1F);
            this.HeadFront.setScale(1F, 1F, 1F);
        }
        float speed_walk = 0.4F;
        float speed_idle = 0.05F;
        float speed_fly = 0.2F;
        float degree_walk = 0.5F;
        float degree_idle = 0.5F;
        float degree_flap = 0.5F * (entity.flapProgress / 10F);
        AdvancedModelBox[] TAIL = new AdvancedModelBox[]{this.Tail1, this.Tail2, this.Tail3, this.Tail4};
        AdvancedModelBox[] ENTIRE_BODY = new AdvancedModelBox[]{this.BodyUpper, this.BodyLower, this.Tail1, this.Tail2, this.Tail3, this.Tail4};
        AdvancedModelBox[] NECK = new AdvancedModelBox[]{this.Neck1, this.Neck2, this.Neck3};
        if (entity.groundProgress >= 10) {
            this.chainSwing(ENTIRE_BODY, speed_walk, 0.125F, 2, limbAngle, limbDistance);
            this.chainSwing(NECK, speed_walk, -degree_walk, 4, limbAngle, limbDistance);
        }
        this.chainWave(NECK, speed_idle, degree_idle * 0.15F, 4, animationProgress, 1);
        this.chainSwing(TAIL, speed_idle, degree_idle * 0.1F, 2, animationProgress, 1);
        this.flap(this.WingL, speed_fly, degree_flap, false, 0, 0, animationProgress, 1);
        this.flap(this.WingR, speed_fly, -degree_flap, false, 0, 0, animationProgress, 1);
        this.flap(this.WingL2, speed_fly, degree_flap, false, 0, 0, animationProgress, 1);
        this.flap(this.WingR2, speed_fly, -degree_flap, false, 0, 0, animationProgress, 1);
        {
            float sitProgress = entity.diveProgress;
            this.progressRotation(this.FingerR4, sitProgress, 0.2617993877991494F, 0.0F, 0.0F);
            this.progressRotation(this.WingL2, sitProgress, -0.3490658503988659F, 0.0F, 0.3490658503988659F);
            this.progressRotation(this.FingerR1, sitProgress, 0.03490658503988659F, 0.0F, 0.0F);
            this.progressRotation(this.FingerL3, sitProgress, 0.13962634015954636F, 0.0F, 0.0F);
            this.progressRotation(this.WingR21, sitProgress, 0.0F, 0.0F, 0.0F);
            this.progressRotation(this.FingerL1, sitProgress, 0.03490658503988659F, 0.0F, 0.0F);
            this.progressRotation(this.WingR, sitProgress, 0.5585053606381855F, 0.0F, 1.6580627893946132F);
            this.progressRotation(this.FingerL4, sitProgress, 0.2617993877991494F, 0.0F, 0.0F);
            this.progressRotation(this.WingR3, sitProgress, 1.48352986419518F, 0.0F, 0.0F);
            this.progressRotation(this.WingR2, sitProgress, -0.3490658503988659F, 0.0F, -0.3490658503988659F);
            this.progressRotation(this.WingL3, sitProgress, 1.48352986419518F, 0.0F, 0.0F);
            this.progressRotation(this.FingerL2, sitProgress, 0.06981317007977318F, 0.0F, 0.0F);
            this.progressRotation(this.FingerR3, sitProgress, 0.13962634015954636F, 0.0F, 0.0F);
            this.progressRotation(this.FingerR2, sitProgress, 0.06981317007977318F, 0.0F, 0.0F);
            this.progressRotation(this.WingL, sitProgress, 0.5585053606381855F, 0.0F, -1.6580627893946132F);
        }
        {
            float sitProgress = entity.groundProgress;
            this.progressRotation(this.Tail1, sitProgress, -0.045553093477052F, 0.0F, 0.0F);
            this.progressRotation(this.CrestR2, sitProgress, 1.7453292519943295F, -0.6108652381980153F, -0.3141592653589793F);
            this.progressRotation(this.FingerR4, sitProgress, -0.2617993877991494F, 0.0F, 0.0F);
            this.progressRotation(this.TailL1, sitProgress, 1.3962634015954636F, -0.06981317007977318F, 0.0F);
            this.progressRotation(this.Neck2, sitProgress, -0.4553564018453205F, -0.0F, 0.0F);
            this.progressRotation(this.FingerR3, sitProgress, -0.13962634015954636F, 0.0F, 0.0F);
            this.progressRotation(this.FingerL1, sitProgress, 0.13962634015954636F, 0.0F, 0.0F);
            this.progressRotation(this.WingR, sitProgress, 0.08726646259971647F, 0.0F, 1.1344640137963142F);
            this.progressRotation(this.FingerL4, sitProgress, -0.2617993877991494F, 0.0F, 0.0F);
            this.progressRotation(this.Teeth2, sitProgress, 0.0F, -0.0F, 0.0F);
            this.progressRotation(this.FingerR2, sitProgress, 0.0F, 0.0F, 0.0F);
            this.progressRotation(this.Beak, sitProgress, 0.6981317007977318F, -0.0F, 0.0F);
            this.progressRotation(this.BodyUpper, sitProgress, 0.0F, -0.0F, 0.0F);
            this.progressRotation(this.Club, sitProgress, 0.18203784098300857F, 0.0F, 0.0F);
            this.progressRotation(this.CrestLB, sitProgress, 1.7453292519943295F, 0.27314402793711257F, 0.0F);
            this.progressRotation(this.FingerR1, sitProgress, 0.13962634015954636F, 0.0F, 0.0F);
            this.progressRotation(this.HeadFront, sitProgress, -0.22759093446006054F, -0.0F, 0.0F);
            this.progressRotation(this.Neck1, sitProgress, -0.18203784098300857F, -0.0F, -0.045553093477052F);
            this.progressRotation(this.FingerL2, sitProgress, 0.0F, 0.0F, 0.0F);
            this.progressRotation(this.WingR3, sitProgress, 0.12217304763960307F, -0.17453292519943295F, -0.12217304763960307F);
            this.progressRotation(this.Teeth1, sitProgress, 0.0F, -0.0F, 0.0F);
            this.progressRotation(this.Tail4, sitProgress, -0.045553093477052F, 0.0F, 0.0F);
            this.progressRotation(this.WingL3, sitProgress, 0.12217304763960307F, 0.17453292519943295F, 0.12217304763960307F);
            this.progressRotation(this.Jaw, sitProgress, -0.31869712141416456F, -0.0F, 0.0F);
            this.progressRotation(this.Tail2, sitProgress, 0.091106186954104F, 0.0F, 0.0F);
            this.progressRotation(this.CrestRB, sitProgress, 1.7453292519943295F, -0.27314402793711257F, 0.0F);
            this.progressRotation(this.BodyLower, sitProgress, -0.136659280431156F, 0.0F, 0.0F);
            this.progressRotation(this.CrestR3, sitProgress, 1.7453292519943295F, -0.8726646259971648F, -0.7285004297824331F);
            this.progressRotation(this.WingL21, sitProgress, 0.0F, 0.0F, 0.0F);
            this.progressRotation(this.Neck3, sitProgress, 0.18203784098300857F, -0.0F, 0.0F);
            this.progressRotation(this.TailL2, sitProgress, 1.3962634015954636F, 0.31869712141416456F, 0.0F);
            this.progressRotation(this.FingerL3, sitProgress, -0.13962634015954636F, 0.0F, 0.0F);
            this.progressRotation(this.CrestL3, sitProgress, 1.7453292519943295F, 0.8726646259971648F, 0.7853981633974483F);
            this.progressRotation(this.CrestL2, sitProgress, 1.7453292519943295F, 0.6108652381980153F, 0.3141592653589793F);
            this.progressRotation(this.WingR21, sitProgress, 0.0F, 0.0F, 0.0F);
            this.progressRotation(this.TailR1, sitProgress, 1.3962634015954636F, 0.06981317007977318F, 0.0F);
            this.progressRotation(this.Head, sitProgress, 0.8196066167365371F, -0.0F, 0.0F);
            this.progressRotation(this.WingR2, sitProgress, 1.48352986419518F, 0.0F, 0.17453292519943295F);
            this.progressRotation(this.WingL, sitProgress, 0.08726646259971647F, 0.0F, -1.1344640137963142F);
            this.progressRotation(this.CrestR1, sitProgress, 1.7453292519943295F, -0.08726646259971647F, 0.0F);
            this.progressRotation(this.WingL2, sitProgress, 1.48352986419518F, 0.0F, -0.17453292519943295F);
            this.progressRotation(this.Tail3, sitProgress, 0.045553093477052F, 0.0F, 0.0F);
            this.progressRotation(this.TailR2, sitProgress, 1.3962634015954636F, -0.31869712141416456F, 0.0F);
            this.progressRotation(this.CrestL1, sitProgress, 1.7453292519943295F, 0.08726646259971647F, 0.0F);
            this.progressPosition(this.BodyUpper, sitProgress, 0, 18, 0);
        }
        {
            float sitProgress = entity.sitProgress;
            this.progressRotation(this.CrestLB, sitProgress, 1.7453292519943295F, 0.27314402793711257F, 0.0F);
            this.progressRotation(this.CrestR1, sitProgress, 1.7453292519943295F, -0.08726646259971647F, 0.0F);
            this.progressRotation(this.TailL1, sitProgress, 1.3962634015954636F, -0.06981317007977318F, 0.0F);
            this.progressRotation(this.HeadFront, sitProgress, -0.22759093446006054F, -0.0F, 0.0F);
            this.progressRotation(this.CrestL1, sitProgress, 1.7453292519943295F, 0.08726646259971647F, 0.0F);
            this.progressRotation(this.CrestR3, sitProgress, 1.7453292519943295F, -0.8726646259971648F, -0.7285004297824331F);
            this.progressRotation(this.TailR2, sitProgress, 1.3962634015954636F, -0.31869712141416456F, 0.0F);
            this.progressRotation(this.Neck2, sitProgress, -0.4553564018453205F, -0.0F, 0.0F);
            this.progressRotation(this.Head, sitProgress, 1.3658946726107624F, -0.0F, 0.0F);
            this.progressRotation(this.Tail1, sitProgress, 0.0F, 0.7285004297824331F, 0.045553093477052F);
            this.progressRotation(this.CrestRB, sitProgress, 1.7453292519943295F, -0.27314402793711257F, 0.0F);
            this.progressRotation(this.Jaw, sitProgress, -0.31869712141416456F, -0.0F, 0.0F);
            this.progressRotation(this.Tail2, sitProgress, 0.091106186954104F, 0.36425021489121656F, 0.0F);
            this.progressRotation(this.Tail3, sitProgress, 0.045553093477052F, 0.36425021489121656F, 0.0F);
            this.progressRotation(this.BodyLower, sitProgress, -0.136659280431156F, 0.0F, 0.136659280431156F);
            this.progressRotation(this.TailR1, sitProgress, 1.3962634015954636F, 0.06981317007977318F, 0.0F);
            this.progressRotation(this.Club, sitProgress, 0.2678394951282524F, 0.7285004297824331F, 0.08580165414524381F);
            this.progressRotation(this.TailL2, sitProgress, 1.3962634015954636F, 0.31869712141416456F, 0.0F);
            this.progressRotation(this.CrestL3, sitProgress, 1.7453292519943295F, 0.8726646259971648F, 0.7853981633974483F);
            this.progressRotation(this.CrestL2, sitProgress, 1.7453292519943295F, 0.6108652381980153F, 0.3141592653589793F);
            this.progressRotation(this.Neck1, sitProgress, -0.5462880558742251F, -0.0F, -0.045553093477052F);
            this.progressRotation(this.Tail4, sitProgress, -0.045553093477052F, 0.5009094953223726F, 0.0F);
            this.progressRotation(this.Neck3, sitProgress, 0.18203784098300857F, -0.0F, 0.0F);
        }

        if (entity.groundProgress <= 0 && entity.getAnimation() != EntityAmphithere.ANIMATION_WING_BLAST && !entity.isOnGround()) {
            entity.roll_buffer.applyChainFlapBuffer(this.BodyUpper);
            entity.pitch_buffer.applyChainWaveBuffer(this.BodyUpper);
            entity.tail_buffer.applyChainSwingBuffer(TAIL);
        }
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
