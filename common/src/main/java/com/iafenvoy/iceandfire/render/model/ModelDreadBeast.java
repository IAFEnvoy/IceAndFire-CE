package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.entity.EntityDreadBeast;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ModelDreadBeast extends ModelDragonBase<EntityDreadBeast> {
    public final AdvancedModelBox Body;
    public final AdvancedModelBox LegL1;
    public final AdvancedModelBox LowerBody;
    public final AdvancedModelBox Neck1;
    public final AdvancedModelBox LegR1;
    public final AdvancedModelBox pelt;
    public final AdvancedModelBox pelt_1;
    public final AdvancedModelBox pelt_2;
    public final AdvancedModelBox LegL2;
    public final AdvancedModelBox Tail;
    public final AdvancedModelBox BackLegR1;
    public final AdvancedModelBox BackLegL1;
    public final AdvancedModelBox pelt_3;
    public final AdvancedModelBox Tail2;
    public final AdvancedModelBox Tail3;
    public final AdvancedModelBox BackLegR2;
    public final AdvancedModelBox BackLegL2;
    public final AdvancedModelBox HeadBase;
    public final AdvancedModelBox HeadFront;
    public final AdvancedModelBox Jaw;
    public final AdvancedModelBox ChopsR;
    public final AdvancedModelBox ChopsL;
    public final AdvancedModelBox EarR;
    public final AdvancedModelBox EarL;
    public final AdvancedModelBox pelt_4;
    public final AdvancedModelBox EarR2;
    public final AdvancedModelBox EarL2;
    public final AdvancedModelBox LegR2;
    private final ModelAnimator animator;

    public ModelDreadBeast() {
        this.texWidth = 256;
        this.texHeight = 128;
        this.HeadBase = new AdvancedModelBox(this, 0, 15);
        this.HeadBase.setPos(0.0F, 1.4F, -8.6F);
        this.HeadBase.addBox(-3.5F, -3.51F, -4.6F, 7, 6, 6, 0.0F);
        this.setRotateAngle(this.HeadBase, -0.12217304763960307F, 0.0F, 0.0F);
        this.LegL1 = new AdvancedModelBox(this, 0, 54);
        this.LegL1.mirror = true;
        this.LegL1.setPos(3.3F, 2.0F, -3.5F);
        this.LegL1.addBox(-1.0F, 0.0F, -1.5F, 2, 7, 3, 0.0F);
        this.setRotateAngle(this.LegL1, -0.091106186954104F, 0.0F, 0.0F);
        this.EarR = new AdvancedModelBox(this, 3, 0);
        this.EarR.setPos(-3.3F, -1.5F, 0.9F);
        this.EarR.addBox(-1.5F, -1.1F, -3.1F, 3, 1, 4, 0.0F);
        this.setRotateAngle(this.EarR, -2.6406831582674206F, 0.5009094953223726F, -1.5025539530419183F);
        this.BackLegR1 = new AdvancedModelBox(this, 20, 52);
        this.BackLegR1.mirror = true;
        this.BackLegR1.setPos(2.5F, -0.5F, 9.3F);
        this.BackLegR1.addBox(-1.0F, 0.0F, -1.5F, 2, 8, 5, 0.0F);
        this.setRotateAngle(this.BackLegR1, 0.045553093477052F, 0.0F, 0.0F);
        this.LegR1 = new AdvancedModelBox(this, 0, 54);
        this.LegR1.setPos(-3.3F, 2.0F, -3.5F);
        this.LegR1.addBox(-1.0F, 0.0F, -1.5F, 2, 7, 3, 0.0F);
        this.setRotateAngle(this.LegR1, -0.091106186954104F, 0.0F, 0.0F);
        this.Tail = new AdvancedModelBox(this, 50, 14);
        this.Tail.setPos(0.0F, -1.6F, 13.2F);
        this.Tail.addBox(-1.5F, -1.0F, 0.0F, 3, 4, 7, 0.0F);
        this.setRotateAngle(this.Tail, -0.9560913642424937F, 0.0F, 0.0F);
        this.ChopsL = new AdvancedModelBox(this, 37, 0);
        this.ChopsL.mirror = true;
        this.ChopsL.setPos(2.2F, 1.6F, -1.0F);
        this.ChopsL.addBox(-1.0F, -0.5F, -3.6F, 4, 2, 5, 0.0F);
        this.setRotateAngle(this.ChopsL, -2.5953045977155678F, -0.8196066167365371F, 0.9560913642424937F);
        this.LegR2 = new AdvancedModelBox(this, 11, 54);
        this.LegR2.setPos(0.0F, 6.4F, 0.1F);
        this.LegR2.addBox(-1.01F, 0.0F, -1.6F, 2, 6, 2, 0.0F);
        this.pelt_2 = new AdvancedModelBox(this, 91, 49);
        this.pelt_2.setPos(0.0F, -0.8F, 3.6F);
        this.pelt_2.addBox(-4.0F, -3.4F, 0.0F, 8, 4, 9, 0.0F);
        this.setRotateAngle(this.pelt_2, 0.091106186954104F, 0.0F, 0.0F);
        this.EarL = new AdvancedModelBox(this, 3, 0);
        this.EarL.mirror = true;
        this.EarL.setPos(3.3F, -1.5F, 0.9F);
        this.EarL.addBox(-1.5F, -1.1F, -3.1F, 3, 1, 4, 0.0F);
        this.setRotateAngle(this.EarL, -2.5497515042385164F, -0.36425021489121656F, 1.5025539530419183F);
        this.BackLegR2 = new AdvancedModelBox(this, 0, 44);
        this.BackLegR2.mirror = true;
        this.BackLegR2.setPos(0.0F, 7.5F, 1.4F);
        this.BackLegR2.addBox(-1.01F, 0.0F, -0.3F, 2, 8, 2, 0.0F);
        this.pelt_3 = new AdvancedModelBox(this, 91, 49);
        this.pelt_3.setPos(0.0F, 0.4F, 5.6F);
        this.pelt_3.addBox(-4.0F, -3.4F, 0.0F, 8, 4, 9, 0.0F);
        this.setRotateAngle(this.pelt_3, 0.31869712141416456F, 0.0F, 0.0F);
        this.BackLegL2 = new AdvancedModelBox(this, 0, 44);
        this.BackLegL2.setPos(0.0F, 7.5F, 1.4F);
        this.BackLegL2.addBox(-1.01F, 0.0F, -0.3F, 2, 8, 2, 0.0F);
        this.HeadFront = new AdvancedModelBox(this, 14, 2);
        this.HeadFront.setPos(0.0F, -1.2F, -5.1F);
        this.HeadFront.addBox(-1.5F, -2.2F, -5.4F, 3, 4, 8, 0.0F);
        this.setRotateAngle(this.HeadFront, -0.27314402793711257F, 0.0F, 0.0F);
        this.Jaw = new AdvancedModelBox(this, 28, 9);
        this.Jaw.setPos(0.0F, 1.5F, -5.7F);
        this.Jaw.addBox(-2.0F, -0.7F, -5.9F, 4, 3, 7, 0.0F);
        this.setRotateAngle(this.Jaw, 0.27314402793711257F, 0.0F, -3.141592653589793F);
        this.Neck1 = new AdvancedModelBox(this, 0, 28);
        this.Neck1.setPos(0.0F, -0.8F, -4.5F);
        this.Neck1.addBox(-2.5F, -2.0F, -8.2F, 5, 6, 8, 0.0F);
        this.setRotateAngle(this.Neck1, 0.36425021489121656F, 0.0F, 0.0F);
        this.Tail2 = new AdvancedModelBox(this, 81, 22);
        this.Tail2.setPos(0.0F, 0.4F, 6.0F);
        this.Tail2.addBox(-2.01F, -1.7F, -0.8F, 4, 5, 8, 0.0F);
        this.setRotateAngle(this.Tail2, 0.5462880558742251F, 0.0F, -0.091106186954104F);
        this.LegL2 = new AdvancedModelBox(this, 11, 54);
        this.LegL2.mirror = true;
        this.LegL2.setPos(0.0F, 6.4F, 0.1F);
        this.LegL2.addBox(-1.01F, 0.0F, -1.6F, 2, 6, 2, 0.0F);
        this.Body = new AdvancedModelBox(this, 38, 45);
        this.Body.setPos(0.0F, 9.3F, -6.0F);
        this.Body.addBox(-4.0F, -3.9F, -6.5F, 8, 10, 10, 0.0F);
        this.setRotateAngle(this.Body, 0.091106186954104F, 0.0F, 0.0F);
        this.pelt = new AdvancedModelBox(this, 90, 49);
        this.pelt.setPos(0.0F, 0.2F, -5.4F);
        this.pelt.addBox(-5.0F, -3.4F, 0.0F, 10, 4, 9, 0.0F);
        this.setRotateAngle(this.pelt, 0.4553564018453205F, 0.0F, 0.0F);
        this.EarL2 = new AdvancedModelBox(this, 5, 2);
        this.EarL2.setPos(0.3F, 0.1F, -2.9F);
        this.EarL2.addBox(-1.0F, -1.1F, -1.5F, 2, 1, 2, 0.0F);
        this.setRotateAngle(this.EarL2, 0.091106186954104F, 0.0F, 0.0F);
        this.Tail3 = new AdvancedModelBox(this, 69, 10);
        this.Tail3.setPos(0.0F, 0.7F, 5.7F);
        this.Tail3.addBox(-1.01F, -2.1F, 0.0F, 2, 4, 7, 0.0F);
        this.setRotateAngle(this.Tail3, -0.136659280431156F, 0.0F, 0.0F);
        this.BackLegL1 = new AdvancedModelBox(this, 20, 52);
        this.BackLegL1.setPos(-2.5F, -0.5F, 9.3F);
        this.BackLegL1.addBox(-1.0F, 0.0F, -1.5F, 2, 8, 5, 0.0F);
        this.setRotateAngle(this.BackLegL1, 0.045553093477052F, 0.0F, 0.0F);
        this.ChopsR = new AdvancedModelBox(this, 37, 0);
        this.ChopsR.setPos(-2.2F, 1.6F, -1.0F);
        this.ChopsR.addBox(-3.0F, -0.5F, -3.6F, 4, 2, 5, 0.0F);
        this.setRotateAngle(this.ChopsR, -2.5953045977155678F, 0.8196066167365371F, -0.9560913642424937F);
        this.EarR2 = new AdvancedModelBox(this, 5, 2);
        this.EarR2.mirror = true;
        this.EarR2.setPos(-0.3F, 0.1F, -2.9F);
        this.EarR2.addBox(-1.0F, -1.1F, -1.5F, 2, 1, 2, 0.0F);
        this.setRotateAngle(this.EarR2, 0.091106186954104F, 0.0F, 0.0F);
        this.LowerBody = new AdvancedModelBox(this, 4, 69);
        this.LowerBody.setPos(0.0F, -0.5F, 3.0F);
        this.LowerBody.addBox(-3.0F, -2.7F, -0.1F, 6, 8, 14, 0.0F);
        this.setRotateAngle(this.LowerBody, -0.136659280431156F, 0.0F, 0.0F);
        this.pelt_4 = new AdvancedModelBox(this, 92, 49);
        this.pelt_4.setPos(0.0F, 1.4F, -3.4F);
        this.pelt_4.addBox(-3.0F, -3.4F, 0.0F, 6, 4, 9, 0.0F);
        this.setRotateAngle(this.pelt_4, 0.31869712141416456F, 0.0F, 0.0F);
        this.pelt_1 = new AdvancedModelBox(this, 90, 49);
        this.pelt_1.setPos(0.0F, -0.8F, 0.6F);
        this.pelt_1.addBox(-5.0F, -3.4F, 0.0F, 10, 4, 9, 0.0F);
        this.setRotateAngle(this.pelt_1, 0.5462880558742251F, 0.0F, 0.0F);
        this.Neck1.addChild(this.HeadBase);
        this.Body.addChild(this.LegL1);
        this.HeadBase.addChild(this.EarR);
        this.LowerBody.addChild(this.BackLegR1);
        this.Body.addChild(this.LegR1);
        this.LowerBody.addChild(this.Tail);
        this.HeadBase.addChild(this.ChopsL);
        this.LegR1.addChild(this.LegR2);
        this.Body.addChild(this.pelt_2);
        this.HeadBase.addChild(this.EarL);
        this.BackLegR1.addChild(this.BackLegR2);
        this.LowerBody.addChild(this.pelt_3);
        this.BackLegL1.addChild(this.BackLegL2);
        this.HeadBase.addChild(this.HeadFront);
        this.HeadBase.addChild(this.Jaw);
        this.Body.addChild(this.Neck1);
        this.Tail.addChild(this.Tail2);
        this.LegL1.addChild(this.LegL2);
        this.Body.addChild(this.pelt);
        this.EarL.addChild(this.EarL2);
        this.Tail2.addChild(this.Tail3);
        this.LowerBody.addChild(this.BackLegL1);
        this.HeadBase.addChild(this.ChopsR);
        this.EarR.addChild(this.EarR2);
        this.Body.addChild(this.LowerBody);
        this.HeadBase.addChild(this.pelt_4);
        this.Body.addChild(this.pelt_1);
        this.animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.Body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.Body, this.LegL1, this.LowerBody, this.Neck1, this.LegR1, this.pelt, this.pelt_1, this.pelt_2, this.LegL2, this.Tail, this.BackLegR1,
                this.BackLegL1, this.pelt_3, this.Tail2, this.Tail3, this.BackLegR2, this.BackLegL2, this.HeadBase, this.HeadFront, this.Jaw, this.ChopsR, this.ChopsL, this.EarR,
                this.EarL, this.pelt_4, this.EarR2, this.EarL2, this.LegR2);
    }

    @Override
    public void setAngles(EntityDreadBeast entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.animate(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 0);
        float speed_walk = 0.45F;
        float speed_idle = 0.05F;
        float degree_walk = 1F;
        float degree_idle = 0.5F;
        AdvancedModelBox[] NECK = new AdvancedModelBox[]{this.Neck1, this.HeadBase};
        AdvancedModelBox[] TAIL = new AdvancedModelBox[]{this.Tail, this.Tail2, this.Tail3};
        this.chainWave(NECK, speed_idle, degree_idle * 0.15F, -1, animationProgress, 1);
        this.walk(this.Jaw, speed_idle, degree_idle * 0.35F, true, 1, -0.1F, animationProgress, 1);
        this.walk(this.pelt, speed_idle, degree_idle * 0.2F, false, 3, -0.1F, animationProgress, 1);
        this.walk(this.pelt_1, speed_idle, degree_idle * 0.2F, false, 3, -0.1F, animationProgress, 1);
        this.walk(this.pelt_2, speed_idle, degree_idle * 0.2F, false, 3, -0.1F, animationProgress, 1);
        this.walk(this.pelt_3, speed_idle, degree_idle * 0.2F, false, 3, -0.1F, animationProgress, 1);
        this.walk(this.pelt_4, speed_idle, degree_idle * 0.2F, false, 3, -0.1F, animationProgress, 1);
        this.chainSwing(TAIL, speed_idle, degree_idle * 0.75F, -2, animationProgress, 1);
        this.chainWave(TAIL, speed_idle, degree_idle * 0.15F, 0, animationProgress, 1);
        this.chainWave(NECK, speed_walk, degree_walk * 0.15F, -1, limbAngle, limbDistance);
        this.chainWave(TAIL, speed_walk, degree_walk * 0.15F, 2, limbAngle, limbDistance);
        this.bob(this.Body, speed_walk, degree_walk * 1.15F, false, limbAngle, limbDistance);
        this.walk(this.BackLegR1, speed_walk, degree_walk * -0.75F, true, 0, 0F, limbAngle, limbDistance);
        this.walk(this.BackLegL1, speed_walk, degree_walk * -0.75F, true, 0, 0F, limbAngle, limbDistance);
        this.walk(this.BackLegR2, speed_walk, degree_walk * -0.5F, true, 1, 0.3F, limbAngle, limbDistance);
        this.walk(this.BackLegL2, speed_walk, degree_walk * -0.5F, true, 1, 0.3F, limbAngle, limbDistance);
        this.walk(this.LegR1, speed_walk, degree_walk * -0.75F, false, 0, 0F, limbAngle, limbDistance);
        this.walk(this.LegL1, speed_walk, degree_walk * -0.75F, false, 0, 0F, limbAngle, limbDistance);
        this.walk(this.LegR2, speed_walk, degree_walk * -0.5F, false, -1, 0.3F, limbAngle, limbDistance);
        this.walk(this.LegL2, speed_walk, degree_walk * -0.5F, false, -1, 0.3F, limbAngle, limbDistance);
        float f12 = -0.9560913642424937F + limbDistance;
        if (f12 > Math.toRadians(-20))
            f12 = (float) Math.toRadians(-20);
        this.Tail.rotateAngleX = f12;
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.resetToDefaultPose();
        this.animator.update(entity);
        if (this.animator.setAnimation(EntityDreadBeast.ANIMATION_BITE)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Neck1, -39, 0, 0);
            this.rotate(this.animator, this.HeadBase, 40, 0, -15);
            this.rotate(this.animator, this.Jaw, -50, 0, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Neck1, -19, 0, 0);
            this.rotate(this.animator, this.HeadBase, 20, 0, 10);
            this.rotate(this.animator, this.Jaw, 10, 0, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        if (this.animator.setAnimation(EntityDreadBeast.ANIMATION_SPAWN)) {
            this.animator.startKeyframe(0);
            this.animator.move(this.Body, 0, 35, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(30);
            this.animator.move(this.Body, 0, 0, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        this.animator.update(null);
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
