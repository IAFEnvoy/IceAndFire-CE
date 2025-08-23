package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.entity.EntitySiren;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ModelSiren extends ModelDragonBase<EntitySiren> {
    public final AdvancedModelBox Tail_1;
    public final AdvancedModelBox Tail_2;
    public final AdvancedModelBox Body;
    public final AdvancedModelBox Fin1;
    public final AdvancedModelBox Tail_3;
    public final AdvancedModelBox Fin2;
    public final AdvancedModelBox FlukeL;
    public final AdvancedModelBox FlukeR;
    public final AdvancedModelBox Fin3;
    public final AdvancedModelBox Left_Arm;
    public final AdvancedModelBox Head;
    public final AdvancedModelBox Right_Arm;
    public final AdvancedModelBox Neck;
    public final AdvancedModelBox Hair1;
    public final AdvancedModelBox HairR;
    public final AdvancedModelBox HairL;
    public final AdvancedModelBox Mouth;
    public final AdvancedModelBox Jaw;
    public final AdvancedModelBox Hair2;
    private final ModelAnimator animator;

    public ModelSiren() {
        this.texWidth = 128;
        this.texHeight = 64;
        this.Left_Arm = new AdvancedModelBox(this, 40, 16);
        this.Left_Arm.mirror = true;
        this.Left_Arm.setPos(5.0F, -10.0F, 0.0F);
        this.Left_Arm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.setRotateAngle(this.Left_Arm, -0.6981317007977318F, 0.0F, 0.0F);
        this.HairR = new AdvancedModelBox(this, 80, 16);
        this.HairR.setPos(-1.8F, -7.8F, 3.2F);
        this.HairR.addBox(-1.9F, -10.7F, -0.3F, 2, 11, 4, 0.0F);
        this.setRotateAngle(this.HairR, -2.5830872929516078F, 0.0F, 0.08726646259971647F);
        this.Mouth = new AdvancedModelBox(this, 38, 0);
        this.Mouth.setPos(0.5F, -1.3F, 0.0F);
        this.Mouth.addBox(-2.5F, -0.6F, -4.6F, 4, 3, 2, 0.0F);
        this.setRotateAngle(this.Mouth, -0.36425021489121656F, 0.0F, 0.0F);
        this.Fin2 = new AdvancedModelBox(this, 72, 34);
        this.Fin2.setPos(0.0F, 5.8F, 1.9F);
        this.Fin2.addBox(-1.0F, -5.5F, 0.8F, 1, 11, 4, 0.0F);
        this.Tail_3 = new AdvancedModelBox(this, 52, 34);
        this.Tail_3.setPos(0.0F, 10.4F, 0.1F);
        this.Tail_3.addBox(-3.0F, 0.0F, -1.9F, 6, 13, 4, 0.0F);
        this.Neck = new AdvancedModelBox(this, 40, 8);
        this.Neck.setPos(0.0F, -12.0F, 0.0F);
        this.Neck.addBox(-3.0F, -3.7F, -1.0F, 6, 4, 1, 0.0F);
        this.Hair2 = new AdvancedModelBox(this, 81, 16);
        this.Hair2.setPos(0.0F, -1.5F, 2.9F);
        this.Hair2.addBox(-3.5F, -11.9F, 0.2F, 7, 11, 3, 0.0F);
        this.setRotateAngle(this.Hair2, -0.22759093446006054F, 0.0F, 0.0F);
        this.Fin3 = new AdvancedModelBox(this, 72, 15);
        this.Fin3.setPos(0.0F, 6.1F, 1.9F);
        this.Fin3.addBox(-0.9F, -5.5F, 0.3F, 1, 13, 3, 0.0F);
        this.Fin1 = new AdvancedModelBox(this, 84, 34);
        this.Fin1.setPos(0.0F, 6.1F, 1.9F);
        this.Fin1.addBox(-1.0F, -5.4F, 0.8F, 1, 11, 3, 0.0F);
        this.Tail_1 = new AdvancedModelBox(this, 0, 35);
        this.Tail_1.setPos(0.0F, 22.2F, -0.2F);
        this.Tail_1.addBox(-4.0F, -0.1F, -1.8F, 8, 11, 5, 0.1F);
        this.setRotateAngle(this.Tail_1, 1.5707963267948966F, 0.0F, 0.0F);
        this.Head = new AdvancedModelBox(this, 0, 0);
        this.Head.setPos(0.0F, -12.0F, 0.0F);
        this.Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.setRotateAngle(this.Head, -0.4553564018453205F, 0.0F, 0.0F);
        this.FlukeL = new AdvancedModelBox(this, 106, 34);
        this.FlukeL.setPos(0.0F, 12.3F, 0.1F);
        this.FlukeL.addBox(-3.5F, -0.1F, -0.5F, 7, 11, 1, 0.0F);
        this.setRotateAngle(this.FlukeL, -0.03490658503988659F, -0.08726646259971647F, -0.5235987755982988F);
        this.Tail_2 = new AdvancedModelBox(this, 27, 34);
        this.Tail_2.setPos(0.0F, 10.4F, 0.1F);
        this.Tail_2.addBox(-3.5F, 0.0F, -1.9F, 7, 11, 5, 0.0F);
        this.FlukeR = new AdvancedModelBox(this, 106, 34);
        this.FlukeR.mirror = true;
        this.FlukeR.setPos(0.0F, 12.3F, 0.1F);
        this.FlukeR.addBox(-3.5F, -0.1F, -0.5F, 7, 11, 1, 0.0F);
        this.setRotateAngle(this.FlukeR, -0.03490658503988659F, 0.08726646259971647F, 0.5235987755982988F);
        this.Right_Arm = new AdvancedModelBox(this, 40, 16);
        this.Right_Arm.setPos(-5.0F, -10.0F, 0.0F);
        this.Right_Arm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.setRotateAngle(this.Right_Arm, -0.6981317007977318F, 0.045553093477052F, 0.0F);
        this.Hair1 = new AdvancedModelBox(this, 80, 16);
        this.Hair1.setPos(0.0F, -7.8F, 3.2F);
        this.Hair1.addBox(-3.5F, -10.7F, -0.3F, 7, 11, 4, 0.0F);
        this.setRotateAngle(this.Hair1, -2.1855012893472994F, 0.0F, 0.0F);
        this.Jaw = new AdvancedModelBox(this, 24, 0);
        this.Jaw.setPos(0.0F, 0.0F, 0.0F);
        this.Jaw.addBox(-2.0F, -0.6F, -4.6F, 4, 1, 3, 0.0F);
        this.setRotateAngle(this.Jaw, 0.045553093477052F, 0.0F, 0.0F);
        this.Body = new AdvancedModelBox(this, 16, 16);
        this.Body.setPos(0.0F, 0.9F, 1.0F);
        this.Body.addBox(-4.0F, -12.0F, -2.0F, 8, 12, 4, 0.0F);
        this.setRotateAngle(this.Body, -0.8196066167365371F, 0.0F, 0.0F);
        this.HairL = new AdvancedModelBox(this, 80, 16);
        this.HairL.mirror = true;
        this.HairL.setPos(1.8F, -7.3F, 3.2F);
        this.HairL.addBox(0.1F, -10.7F, -0.3F, 2, 11, 4, 0.0F);
        this.setRotateAngle(this.HairL, -2.5830872929516078F, 0.0F, -0.08726646259971647F);
        this.Body.addChild(this.Left_Arm);
        this.Head.addChild(this.HairR);
        this.Head.addChild(this.Mouth);
        this.Tail_2.addChild(this.Fin2);
        this.Tail_2.addChild(this.Tail_3);
        this.Body.addChild(this.Neck);
        this.Hair1.addChild(this.Hair2);
        this.Tail_3.addChild(this.Fin3);
        this.Tail_1.addChild(this.Fin1);
        this.Body.addChild(this.Head);
        this.Tail_3.addChild(this.FlukeL);
        this.Tail_1.addChild(this.Tail_2);
        this.Tail_3.addChild(this.FlukeR);
        this.Body.addChild(this.Right_Arm);
        this.Head.addChild(this.Hair1);
        this.Head.addChild(this.Jaw);
        this.Tail_1.addChild(this.Body);
        this.Head.addChild(this.HairL);
        this.animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.Tail_1);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.Tail_1, this.Tail_2, this.Body, this.Fin1, this.Tail_3, this.Fin2, this.FlukeL, this.FlukeR, this.Fin3,
                this.Left_Arm, this.Head, this.Right_Arm, this.Neck, this.Hair1, this.HairR, this.HairL, this.Mouth, this.Jaw, this.Hair2);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.resetToDefaultPose();
        this.animator.update(entity);
        if (this.animator.setAnimation(EntitySiren.ANIMATION_BITE)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Mouth, -28, 0, 0);
            this.rotate(this.animator, this.Jaw, 7, 0, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
            this.animator.endKeyframe();
        }
        if (this.animator.setAnimation(EntitySiren.ANIMATION_PULL)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Left_Arm, -103, 5, 0);
            this.rotate(this.animator, this.Right_Arm, -103, -5, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.Left_Arm, 103, 5, 0);
            this.rotate(this.animator, this.Right_Arm, 103, -5, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
            this.animator.endKeyframe();
        }
        this.animator.update(null);
    }

    @Override
    public void setAngles(EntitySiren entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.animate(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 1);
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 1F;
        float degree_idle = 0.5F;
        AdvancedModelBox[] TAIL_NO_BASE = {this.Tail_2, this.Tail_3};
        this.walk(this.Hair1, speed_idle, degree_idle * 0.3F, false, 2, 0F, animationProgress, 1);
        this.walk(this.Hair2, speed_idle, degree_idle * 0.2F, false, 2, 0F, animationProgress, 1);
        this.swing(this.HairL, speed_idle, degree_idle * 0.4F, true, 0F, -0.4F, animationProgress, 1);
        this.swing(this.HairR, speed_idle, degree_idle * 0.4F, false, 0F, -0.4F, animationProgress, 1);
        this.walk(this.Body, speed_idle, degree_idle * 0.3F, false, 2, 0F, animationProgress, 1);
        this.walk(this.Right_Arm, speed_idle, degree_idle * 0.2F, true, 0, 0.1F, animationProgress, 1);
        this.walk(this.Left_Arm, speed_idle, degree_idle * 0.2F, true, 0, 0.1F, animationProgress, 1);
        this.walk(this.Body, speed_idle, degree_idle * 0.2F, false, 0, -0.1F, animationProgress, 1);
        this.progressRotation(this.Body, entity.swimProgress, (float) Math.toRadians(-2F), 0.0F, 0.0F);
        this.progressRotation(this.Head, entity.swimProgress, (float) Math.toRadians(-70), 0.0F, 0.0F);
        this.progressRotation(this.Left_Arm, entity.swimProgress, (float) Math.toRadians(-15), 0.0F, 0.0F);
        this.progressRotation(this.Right_Arm, entity.swimProgress, (float) Math.toRadians(-15), 0.0F, 0.0F);
        if (entity.isSwimming()) {
            this.flap(this.Right_Arm, speed_walk, degree_walk * 1.2F, false, 0, 1.2F, limbAngle, limbDistance);
            this.flap(this.Left_Arm, speed_walk, degree_walk * 1.2F, true, 0, 1.2F, limbAngle, limbDistance);
            this.chainWave(TAIL_NO_BASE, speed_walk, degree_walk * 0.4F, 0, limbAngle, limbDistance);
            this.walk(this.Tail_1, speed_walk, degree_walk * 0.2F, true, 0, 0F, limbAngle, limbDistance);
        } else {
            this.walk(this.Right_Arm, speed_walk, degree_walk * 0.4F, false, 0, 0F, limbAngle, limbDistance);
            this.walk(this.Left_Arm, speed_walk, degree_walk * 0.4F, true, 0, 0F, limbAngle, limbDistance);
            this.chainFlap(TAIL_NO_BASE, speed_walk, degree_walk * 0.6F, 1, limbAngle, limbDistance);
            this.swing(this.Tail_1, speed_walk, degree_walk * 0.2F, true, 0, 0F, limbAngle, limbDistance);
        }
        if (entity.isSinging())
            switch (entity.getSingingPose()) {
                case 2 -> {
                    this.progressRotation(this.Body, entity.singProgress, (float) Math.toRadians(-46F), 0.0F, 0.0F);
                    this.progressRotation(this.Tail_1, entity.singProgress, (float) Math.toRadians(90F), 0.0F, (float) Math.toRadians(20F));
                    this.progressRotation(this.Tail_2, entity.singProgress, 0.0F, (float) Math.toRadians(-13F), 0.0F);
                    this.progressRotation(this.Tail_3, entity.singProgress, 0.0F, (float) Math.toRadians(-7F), 0.0F);
                    this.progressRotation(this.Head, entity.singProgress, (float) Math.toRadians(-52F), (float) Math.toRadians(2F), (float) Math.toRadians(-26F));
                    this.progressRotation(this.Left_Arm, entity.singProgress, (float) Math.toRadians(-40F), (float) Math.toRadians(-28F), (float) Math.toRadians(-26F));
                    this.progressRotation(this.Right_Arm, entity.singProgress, (float) Math.toRadians(13F), (float) Math.toRadians(73F), (float) Math.toRadians(130F));
                    this.progressPosition(this.Head, entity.singProgress, 0, -12.0F, -0.5F);
                    this.walk(this.Right_Arm, speed_idle * 1.5F, degree_idle * 0.6F, false, 2, 0F, animationProgress, 1);
                    this.flap(this.Right_Arm, speed_idle * 1.5F, degree_idle * 0.6F, false, 2, 0F, animationProgress, 1);
                    if (entity.isOnGround()) {
                        this.chainFlap(TAIL_NO_BASE, speed_idle, degree_idle, 0, animationProgress, 1);
                        this.swing(this.Tail_2, speed_idle, degree_idle * 0.4F, false, 0F, -0.4F, animationProgress, 1);
                        this.swing(this.Tail_3, speed_idle, degree_idle * 0.4F, false, 0F, 0.6F, animationProgress, 1);
                    }
                }
                case 1 -> {
                    this.progressRotation(this.Body, entity.singProgress, (float) Math.toRadians(-57F), 0.0F, 0.0F);
                    this.progressRotation(this.Head, entity.singProgress, (float) Math.toRadians(-13F), 0.0F, 0.0F);
                    this.progressRotation(this.Left_Arm, entity.singProgress, (float) Math.toRadians(-200F), (float) Math.toRadians(-60F), (float) Math.toRadians(70F));
                    this.progressRotation(this.Right_Arm, entity.singProgress, (float) Math.toRadians(-200F), (float) Math.toRadians(60F), (float) Math.toRadians(-70F));
                    this.progressRotation(this.Tail_1, entity.singProgress, (float) Math.toRadians(70F), 0.0F, 0.0F);
                    this.progressRotation(this.Tail_2, entity.singProgress, (float) Math.toRadians(20F), 0.0F, (float) Math.toRadians(25F));
                    this.progressRotation(this.Tail_3, entity.singProgress, 0.0F, 0.0F, (float) Math.toRadians(18F));
                    this.progressPosition(this.Tail_1, entity.singProgress, 0.0F, 18.9F, -0.2F);
                    this.walk(this.Right_Arm, speed_idle * 1.5F, degree_idle * 0.6F, false, 2, 0F, animationProgress, 1);
                    this.walk(this.Left_Arm, speed_idle * 1.5F, degree_idle * 0.6F, true, 2, 0F, animationProgress, 1);
                    if (entity.isOnGround())
                        this.chainFlap(TAIL_NO_BASE, speed_idle, degree_idle, 0, animationProgress, 1);
                }
                default -> {
                    this.progressRotation(this.Body, entity.singProgress, (float) Math.toRadians(-46F), 0.0F, (float) Math.toRadians(20.87F));
                    this.progressPosition(this.Head, entity.singProgress, 0, -12.0F, -0.5F);
                    this.progressRotation(this.Head, entity.singProgress, (float) Math.toRadians(-54F), 0.0F, (float) Math.toRadians(20.87F));
                    this.progressRotation(this.Tail_1, entity.singProgress, (float) Math.toRadians(90F), (float) Math.toRadians(20.87F), 0.0F);
                    this.progressRotation(this.Tail_2, entity.singProgress, 0.0F, 0.0F, (float) Math.toRadians(-33));
                    this.progressRotation(this.Tail_2, entity.singProgress, 0.0F, 0.0F, (float) Math.toRadians(-15));
                    this.progressRotation(this.Right_Arm, entity.singProgress, (float) Math.toRadians(-40F), (float) Math.toRadians(2F), (float) Math.toRadians(53F));
                    this.progressRotation(this.Left_Arm, entity.singProgress, (float) Math.toRadians(-80F), (float) Math.toRadians(-70F), 0.0F);
                    this.walk(this.Right_Arm, speed_idle * 1.5F, degree_idle * 0.6F, false, 2, 0F, animationProgress, 1);
                    this.walk(this.Left_Arm, speed_idle * 1.5F, degree_idle * 0.6F, true, 2, 0F, animationProgress, 1);
                    this.flap(this.Right_Arm, speed_idle * 1.5F, degree_idle * 0.6F, false, 2, 0F, animationProgress, 1);
                    this.flap(this.Left_Arm, speed_idle * 1.5F, degree_idle * 0.6F, true, 2, 0F, animationProgress, 1);
                    if (entity.isOnGround())
                        this.chainFlap(TAIL_NO_BASE, speed_idle, degree_idle * 0.5F, -1, animationProgress, 1);
                }
            }
        else
            this.faceTarget(headYaw, headPitch, 2, this.Neck, this.Head);
        if (entity.tail_buffer != null)
            entity.tail_buffer.applyChainSwingBuffer(TAIL_NO_BASE);
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
