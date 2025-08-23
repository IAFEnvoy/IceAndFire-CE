package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.entity.EntityDreadScuttler;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ModelDreadScuttler extends ModelDragonBase<EntityDreadScuttler> {
    public final AdvancedModelBox Body2;
    public final AdvancedModelBox Body3;
    public final AdvancedModelBox Body1;
    public final AdvancedModelBox legTopR2;
    public final AdvancedModelBox legTopR2_1;
    public final AdvancedModelBox Body4;
    public final AdvancedModelBox legTopR3;
    public final AdvancedModelBox legTopR3_1;
    public final AdvancedModelBox Body5;
    public final AdvancedModelBox Tail1;
    public final AdvancedModelBox Tail2;
    public final AdvancedModelBox legMidR3;
    public final AdvancedModelBox legBottomR3;
    public final AdvancedModelBox legMidR3_1;
    public final AdvancedModelBox legBottomR3_1;
    public final AdvancedModelBox Neck1;
    public final AdvancedModelBox legTopR1;
    public final AdvancedModelBox legTopR1_1;
    public final AdvancedModelBox HeadBase;
    public final AdvancedModelBox palpTopL1;
    public final AdvancedModelBox palpTopR1;
    public final AdvancedModelBox palpMidL1;
    public final AdvancedModelBox palpBottomR1;
    public final AdvancedModelBox palpMidR1;
    public final AdvancedModelBox palpBottomR1_1;
    public final AdvancedModelBox legMidR1_1;
    public final AdvancedModelBox legBottomR1;
    public final AdvancedModelBox legMidR1_2;
    public final AdvancedModelBox legBottomR1_1;
    public final AdvancedModelBox legMidR2;
    public final AdvancedModelBox legBottomR2;
    public final AdvancedModelBox legMidR2_1;
    public final AdvancedModelBox legBottomR2_1;
    private final ModelAnimator animator;

    public ModelDreadScuttler() {
        this.texWidth = 128;
        this.texHeight = 128;
        this.legTopR1 = new AdvancedModelBox(this, 0, 54);
        this.legTopR1.mirror = true;
        this.legTopR1.setPos(-3.3F, 1.0F, 1.6F);
        this.legTopR1.addBox(-1.0F, 0.0F, -1.5F, 2, 7, 3, 0.0F);
        this.setRotateAngle(this.legTopR1, -0.45378560551852565F, -0.6981317007977318F, 0.9948376736367678F);
        this.legMidR2_1 = new AdvancedModelBox(this, 11, 50);
        this.legMidR2_1.setPos(0.0F, 6.4F, 0.1F);
        this.legMidR2_1.addBox(-1.5F, 0.0F, -1.0F, 3, 12, 2, 0.0F);
        this.setRotateAngle(this.legMidR2_1, 0.0F, 0.0F, 1.1383037381507017F);
        this.palpTopL1 = new AdvancedModelBox(this, 0, 73);
        this.palpTopL1.mirror = true;
        this.palpTopL1.setPos(2.4F, -2.1F, -9.4F);
        this.palpTopL1.addBox(-1.5F, 0.0F, -0.5F, 3, 7, 3, 0.0F);
        this.setRotateAngle(this.palpTopL1, -0.5918411493512771F, 0.0F, 0.0F);
        this.palpMidL1 = new AdvancedModelBox(this, 12, 74);
        this.palpMidL1.mirror = true;
        this.palpMidL1.setPos(0.0F, 6.4F, 0.1F);
        this.palpMidL1.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 3, 0.0F);
        this.setRotateAngle(this.palpMidL1, 1.2292353921796064F, 0.0F, 0.0F);
        this.Neck1 = new AdvancedModelBox(this, 33, 22);
        this.Neck1.setPos(0.0F, 3.0F, -6.0F);
        this.Neck1.addBox(-2.5F, -4.7F, -2.5F, 5, 6, 3, 0.0F);
        this.setRotateAngle(this.Neck1, -0.27314402793711257F, 0.0F, 0.0F);
        this.legBottomR2 = new AdvancedModelBox(this, 22, 51);
        this.legBottomR2.mirror = true;
        this.legBottomR2.setPos(0.0F, 10.4F, 0.0F);
        this.legBottomR2.addBox(-1.01F, 0.0F, -0.9F, 2, 18, 2, 0.0F);
        this.setRotateAngle(this.legBottomR2, 0.0F, 0.0F, 1.3203415791337103F);
        this.palpBottomR1_1 = new AdvancedModelBox(this, 22, 72);
        this.palpBottomR1_1.mirror = true;
        this.palpBottomR1_1.setPos(0.0F, 3.4F, 0.0F);
        this.palpBottomR1_1.addBox(-1.01F, 0.0F, -0.9F, 2, 5, 2, 0.0F);
        this.setRotateAngle(this.palpBottomR1_1, 0.7285004297824331F, 0.0F, 0.0F);
        this.HeadBase = new AdvancedModelBox(this, 0, 0);
        this.HeadBase.setPos(0.0F, -4.1F, 0.6F);
        this.HeadBase.addBox(-4.5F, -2.51F, -10.1F, 9, 8, 10, 0.0F);
        this.setRotateAngle(this.HeadBase, 1.1383037381507017F, 0.0F, 0.0F);
        this.legMidR3_1 = new AdvancedModelBox(this, 11, 50);
        this.legMidR3_1.mirror = true;
        this.legMidR3_1.setPos(0.0F, 6.4F, 0.1F);
        this.legMidR3_1.addBox(-1.5F, 0.0F, -1.0F, 3, 12, 2, 0.0F);
        this.setRotateAngle(this.legMidR3_1, 0.0F, 0.0F, -1.1383037381507017F);
        this.legMidR1_1 = new AdvancedModelBox(this, 11, 50);
        this.legMidR1_1.setPos(0.0F, 6.4F, 0.1F);
        this.legMidR1_1.addBox(-1.5F, 0.0F, -1.0F, 3, 12, 2, 0.0F);
        this.setRotateAngle(this.legMidR1_1, 0.0F, 0.0F, 1.1383037381507017F);
        this.legBottomR1_1 = new AdvancedModelBox(this, 22, 51);
        this.legBottomR1_1.mirror = true;
        this.legBottomR1_1.setPos(0.0F, 10.4F, 0.0F);
        this.legBottomR1_1.addBox(-1.01F, 0.0F, -0.9F, 2, 18, 2, 0.0F);
        this.setRotateAngle(this.legBottomR1_1, 0.0F, 0.0F, 1.3203415791337103F);
        this.palpTopR1 = new AdvancedModelBox(this, 0, 73);
        this.palpTopR1.setPos(-2.4F, -2.0F, -9.4F);
        this.palpTopR1.addBox(-1.5F, 0.0F, -0.5F, 3, 7, 3, 0.0F);
        this.setRotateAngle(this.palpTopR1, -0.5918411493512771F, 0.0F, 0.0F);
        this.legBottomR3 = new AdvancedModelBox(this, 22, 51);
        this.legBottomR3.setPos(0.0F, 10.4F, 0.0F);
        this.legBottomR3.addBox(-1.01F, 0.0F, -0.9F, 2, 18, 2, 0.0F);
        this.setRotateAngle(this.legBottomR3, 0.0F, 0.0F, -1.3203415791337103F);
        this.Body4 = new AdvancedModelBox(this, 58, 35);
        this.Body4.setPos(0.0F, -0.4F, 7.3F);
        this.Body4.addBox(-3.0F, -2.7F, -1.5F, 6, 7, 4, 0.0F);
        this.setRotateAngle(this.Body4, 0.40980330836826856F, 0.0F, 0.0F);
        this.palpMidR1 = new AdvancedModelBox(this, 12, 74);
        this.palpMidR1.mirror = true;
        this.palpMidR1.setPos(0.0F, 6.4F, 0.1F);
        this.palpMidR1.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 3, 0.0F);
        this.setRotateAngle(this.palpMidR1, 1.2292353921796064F, 0.0F, 0.0F);
        this.legMidR1_2 = new AdvancedModelBox(this, 11, 50);
        this.legMidR1_2.mirror = true;
        this.legMidR1_2.setPos(0.0F, 6.4F, 0.1F);
        this.legMidR1_2.addBox(-1.5F, 0.0F, -1.0F, 3, 12, 2, 0.0F);
        this.setRotateAngle(this.legMidR1_2, 0.0F, 0.0F, -1.1383037381507017F);
        this.Body5 = new AdvancedModelBox(this, 82, 35);
        this.Body5.setPos(0.0F, -0.4F, 4.2F);
        this.Body5.addBox(-3.5F, -3.7F, -4.0F, 7, 8, 9, 0.0F);
        this.setRotateAngle(this.Body5, 0.5009094953223726F, 0.0F, 0.0F);
        this.Body1 = new AdvancedModelBox(this, 34, 47);
        this.Body1.setPos(0.0F, -1.2F, 2.0F);
        this.Body1.addBox(-3.5F, -2.1F, -6.3F, 7, 8, 9, 0.0F);
        this.setRotateAngle(this.Body1, 0.045553093477052F, 0.0F, 0.0F);
        this.legTopR3_1 = new AdvancedModelBox(this, 0, 54);
        this.legTopR3_1.setPos(3.3F, -1.0F, 6.6F);
        this.legTopR3_1.addBox(-1.0F, 0.0F, -1.5F, 2, 7, 3, 0.0F);
        this.setRotateAngle(this.legTopR3_1, 0.5009094953223726F, -0.22759093446006054F, -0.7740535232594852F);
        this.Tail1 = new AdvancedModelBox(this, 64, 80);
        this.Tail1.setPos(0.0F, -2.4F, 2.2F);
        this.Tail1.addBox(-6.5F, -4.2F, -2.1F, 13, 13, 17, 0.0F);
        this.setRotateAngle(this.Tail1, 0.27314402793711257F, 0.0F, 0.0F);
        this.legTopR1_1 = new AdvancedModelBox(this, 0, 54);
        this.legTopR1_1.setPos(3.3F, 1.0F, 1.6F);
        this.legTopR1_1.addBox(-1.0F, 0.0F, -1.5F, 2, 7, 3, 0.0F);
        this.setRotateAngle(this.legTopR1_1, -0.45378560551852565F, 0.6981317007977318F, -0.9948376736367678F);
        this.legBottomR3_1 = new AdvancedModelBox(this, 22, 51);
        this.legBottomR3_1.mirror = true;
        this.legBottomR3_1.setPos(0.0F, 10.4F, 0.0F);
        this.legBottomR3_1.addBox(-1.01F, 0.0F, -0.9F, 2, 18, 2, 0.0F);
        this.setRotateAngle(this.legBottomR3_1, 0.0F, 0.0F, 1.3203415791337103F);
        this.legBottomR1 = new AdvancedModelBox(this, 22, 51);
        this.legBottomR1.setPos(0.0F, 10.4F, 0.0F);
        this.legBottomR1.addBox(-1.01F, 0.0F, -0.9F, 2, 18, 2, 0.0F);
        this.setRotateAngle(this.legBottomR1, 0.0F, 0.0F, -1.3203415791337103F);
        this.legMidR2 = new AdvancedModelBox(this, 11, 50);
        this.legMidR2.mirror = true;
        this.legMidR2.setPos(0.0F, 6.4F, 0.1F);
        this.legMidR2.addBox(-1.5F, 0.0F, -1.0F, 3, 12, 2, 0.0F);
        this.setRotateAngle(this.legMidR2, 0.0F, 0.0F, -1.1383037381507017F);
        this.Tail2 = new AdvancedModelBox(this, 60, 17);
        this.Tail2.setPos(0.0F, 0.6F, 12.0F);
        this.Tail2.addBox(-4.0F, -2.7F, -0.1F, 8, 8, 8, 0.0F);
        this.setRotateAngle(this.Tail2, 0.136659280431156F, 0.0F, 0.0F);
        this.legTopR2 = new AdvancedModelBox(this, 0, 54);
        this.legTopR2.setPos(3.3F, -1.0F, 7.6F);
        this.legTopR2.addBox(-1.0F, 0.0F, -1.5F, 2, 7, 3, 0.0F);
        this.setRotateAngle(this.legTopR2, 0.0F, 0.0F, -0.6981317007977318F);
        this.legMidR3 = new AdvancedModelBox(this, 11, 50);
        this.legMidR3.setPos(0.0F, 6.4F, 0.1F);
        this.legMidR3.addBox(-1.5F, 0.0F, -1.0F, 3, 12, 2, 0.0F);
        this.setRotateAngle(this.legMidR3, 0.0F, 0.0F, 1.1383037381507017F);
        this.Body2 = new AdvancedModelBox(this, 70, 53);
        this.Body2.setPos(0.0F, 8.6F, -6.0F);
        this.Body2.addBox(-3.0F, -2.7F, -0.1F, 6, 7, 4, 0.0F);
        this.setRotateAngle(this.Body2, -0.045553093477052F, 0.0F, 0.0F);
        this.Body3 = new AdvancedModelBox(this, 36, 73);
        this.Body3.setPos(0.0F, 0.2F, 4.1F);
        this.Body3.addBox(-4.5F, -3.4F, -1.4F, 9, 8, 10, 0.0F);
        this.legBottomR2_1 = new AdvancedModelBox(this, 22, 51);
        this.legBottomR2_1.setPos(0.0F, 10.4F, 0.0F);
        this.legBottomR2_1.addBox(-1.01F, 0.0F, -0.9F, 2, 18, 2, 0.0F);
        this.setRotateAngle(this.legBottomR2_1, 0.0F, 0.0F, -1.3203415791337103F);
        this.legTopR2_1 = new AdvancedModelBox(this, 0, 54);
        this.legTopR2_1.mirror = true;
        this.legTopR2_1.setPos(-3.3F, -1.0F, 7.6F);
        this.legTopR2_1.addBox(-1.0F, 0.0F, -1.5F, 2, 7, 3, 0.0F);
        this.setRotateAngle(this.legTopR2_1, 0.0F, 0.0F, 0.6981317007977318F);
        this.palpBottomR1 = new AdvancedModelBox(this, 22, 72);
        this.palpBottomR1.mirror = true;
        this.palpBottomR1.setPos(0.0F, 3.4F, 0.0F);
        this.palpBottomR1.addBox(-1.01F, 0.0F, -0.9F, 2, 5, 2, 0.0F);
        this.setRotateAngle(this.palpBottomR1, 0.7285004297824331F, 0.0F, 0.0F);
        this.legTopR3 = new AdvancedModelBox(this, 0, 54);
        this.legTopR3.mirror = true;
        this.legTopR3.setPos(-3.3F, -1.0F, 6.6F);
        this.legTopR3.addBox(-1.0F, 0.0F, -1.5F, 2, 7, 3, 0.0F);
        this.setRotateAngle(this.legTopR3, 0.5009094953223726F, 0.22759093446006054F, 0.7740535232594852F);
        this.Body1.addChild(this.legTopR1);
        this.legTopR2_1.addChild(this.legMidR2_1);
        this.HeadBase.addChild(this.palpTopL1);
        this.palpTopL1.addChild(this.palpMidL1);
        this.Body1.addChild(this.Neck1);
        this.legMidR2.addChild(this.legBottomR2);
        this.palpMidR1.addChild(this.palpBottomR1_1);
        this.Neck1.addChild(this.HeadBase);
        this.legTopR3_1.addChild(this.legMidR3_1);
        this.legTopR1.addChild(this.legMidR1_1);
        this.legMidR1_2.addChild(this.legBottomR1_1);
        this.HeadBase.addChild(this.palpTopR1);
        this.legMidR3.addChild(this.legBottomR3);
        this.Body3.addChild(this.Body4);
        this.palpTopR1.addChild(this.palpMidR1);
        this.legTopR1_1.addChild(this.legMidR1_2);
        this.Body4.addChild(this.Body5);
        this.Body2.addChild(this.Body1);
        this.Body3.addChild(this.legTopR3_1);
        this.Body5.addChild(this.Tail1);
        this.Body1.addChild(this.legTopR1_1);
        this.legMidR3_1.addChild(this.legBottomR3_1);
        this.legMidR1_1.addChild(this.legBottomR1);
        this.legTopR2.addChild(this.legMidR2);
        this.Tail1.addChild(this.Tail2);
        this.Body2.addChild(this.legTopR2);
        this.legTopR3.addChild(this.legMidR3);
        this.Body2.addChild(this.Body3);
        this.legMidR2_1.addChild(this.legBottomR2_1);
        this.Body2.addChild(this.legTopR2_1);
        this.palpMidL1.addChild(this.palpBottomR1);
        this.Body3.addChild(this.legTopR3);
        this.animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.resetToDefaultPose();
        this.animator.update(entity);
        if (this.animator.setAnimation(EntityDreadScuttler.ANIMATION_BITE)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Neck1, -30, 0, 0);
            this.rotate(this.animator, this.palpTopR1, -50, 0, 0);
            this.rotate(this.animator, this.palpTopL1, -50, 0, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Neck1, -70, 0, 0);
            this.rotate(this.animator, this.HeadBase, 20, 0, 10);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        if (this.animator.setAnimation(EntityDreadScuttler.ANIMATION_SPAWN)) {
            this.animator.startKeyframe(0);
            this.animator.move(this.Body2, 0, 35, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(30);
            this.animator.move(this.Body2, 0, 0, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        this.animator.update(null);
    }

    @Override
    public void setAngles(EntityDreadScuttler entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.animate(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 1);
        float speed_idle = 0.05F;
        float degree_idle = 0.5F;
        float speed_walk = 0.9F;
        float degree_walk = 0.3F;
        AdvancedModelBox[] GASTER = new AdvancedModelBox[]{this.Body4, this.Body5, this.Tail1, this.Tail2};
        AdvancedModelBox[] NECK = new AdvancedModelBox[]{this.Neck1, this.HeadBase};
        AdvancedModelBox[] LEGR1 = new AdvancedModelBox[]{this.legTopR1, this.legMidR1_2, this.legBottomR1};
        AdvancedModelBox[] LEGR2 = new AdvancedModelBox[]{this.legTopR2, this.legMidR2, this.legBottomR2};
        AdvancedModelBox[] LEGR3 = new AdvancedModelBox[]{this.legTopR3, this.legMidR3, this.legBottomR3};
        AdvancedModelBox[] LEGL1 = new AdvancedModelBox[]{this.legTopR1_1, this.legMidR1_1, this.legBottomR1_1};
        AdvancedModelBox[] LEGL2 = new AdvancedModelBox[]{this.legTopR2_1, this.legMidR2_1, this.legBottomR2_1};
        AdvancedModelBox[] LEGL3 = new AdvancedModelBox[]{this.legTopR3_1, this.legMidR3_1, this.legBottomR3_1};
        this.chainWave(GASTER, speed_idle, degree_idle * 0.25F, 0, animationProgress, 1);
        this.chainWave(NECK, speed_idle, degree_idle * -0.15F, 2, animationProgress, 1);
        this.swing(this.palpTopR1, speed_idle * 2F, degree_idle * -0.5F, false, 1, 0.2F, animationProgress, 1);
        this.swing(this.palpTopL1, speed_idle * 2F, degree_idle * -0.5F, true, 1, 0.2F, animationProgress, 1);

        this.walk(this.palpTopR1, speed_idle * 2F, degree_idle * -0.5F, true, 1, 0.2F, animationProgress, 1);
        this.walk(this.palpTopL1, speed_idle * 2F, degree_idle * -0.5F, true, 1, 0.2F, animationProgress, 1);

        this.walk(this.palpMidR1, speed_idle * 2F, degree_idle * -0.5F, true, 1, 0.2F, animationProgress, 1);
        this.walk(this.palpMidL1, speed_idle * 2F, degree_idle * -0.5F, true, 1, 0.2F, animationProgress, 1);

        if (entity.getAnimation() == EntityDreadScuttler.ANIMATION_SPAWN)
            if (entity.getAnimationTick() < 39) {
                limbAngle = animationProgress;
                limbDistance = 1;
            }

        this.animateLeg(LEGR1, speed_walk, degree_walk, false, 0, 1, limbAngle, limbDistance);
        this.animateLeg(LEGR3, speed_walk, degree_walk, false, 0, 1, limbAngle, limbDistance);
        this.animateLeg(LEGR2, speed_walk, degree_walk, true, 0, 1, limbAngle, limbDistance);

        this.animateLeg(LEGL1, speed_walk, degree_walk, false, 1, -1, limbAngle, limbDistance);
        this.animateLeg(LEGL3, speed_walk, degree_walk, false, 1, -1, limbAngle, limbDistance);
        this.animateLeg(LEGL2, speed_walk, degree_walk, true, 1, -1, limbAngle, limbDistance);
    }

    private void animateLeg(AdvancedModelBox[] models, float speed, float degree, boolean reverse, float offset, float weight, float f, float f1) {
        this.flap(models[0], speed, degree * 0.4F, reverse, offset, weight * 0.2F, f, f1);
        this.flap(models[1], speed, degree * 2, reverse, offset, weight * -0.4F, f, f1);
        this.flap(models[1], speed, -degree * 1.2F, reverse, offset, weight * 0.5F, f, f1);
        this.walk(models[0], speed, degree, reverse, offset, 0F, f, f1);

    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.Body2);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.Body2, this.Body3, this.Body1, this.legTopR2, this.legTopR2_1, this.Body4, this.legTopR3, this.legTopR3_1, this.Body5,
                this.Tail1, this.Tail2, this.legMidR3, this.legBottomR3, this.legMidR3_1, this.legBottomR3_1, this.Neck1, this.legTopR1, this.legTopR1_1, this.HeadBase,
                this.palpTopL1, this.palpTopR1, this.palpMidL1, this.palpBottomR1, this.palpMidR1, this.palpBottomR1_1, this.legMidR1_1, this.legBottomR1,
                this.legMidR1_2, this.legBottomR1_1, this.legMidR2, this.legBottomR2, this.legMidR2_1, this.legBottomR2_1);
    }
}
