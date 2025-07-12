package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.entity.EntityTroll;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ModelTroll extends ModelDragonBase<EntityTroll> {
    public final AdvancedModelBox body;
    public final AdvancedModelBox upperBody;
    public final AdvancedModelBox loin;
    public final AdvancedModelBox rightleg;
    public final AdvancedModelBox leftleg;
    public final AdvancedModelBox head;
    public final AdvancedModelBox rightarm;
    public final AdvancedModelBox leftarm;
    public final AdvancedModelBox chest;
    public final AdvancedModelBox jaw;
    public final AdvancedModelBox mouth;
    public final AdvancedModelBox nose;
    public final AdvancedModelBox teeth;
    public final AdvancedModelBox hornL;
    public final AdvancedModelBox hornR;
    public final AdvancedModelBox hornL2;
    public final AdvancedModelBox hornR2;
    public final AdvancedModelBox nose2;
    public final AdvancedModelBox rightarm2;
    public final AdvancedModelBox log1;
    public final AdvancedModelBox log2;
    public final AdvancedModelBox handle;
    public final AdvancedModelBox column;
    public final AdvancedModelBox blade1;
    public final AdvancedModelBox blade2;
    public final AdvancedModelBox blade2_1;
    public final AdvancedModelBox block;
    public final AdvancedModelBox blade2_2;
    public final AdvancedModelBox bottom;
    public final AdvancedModelBox top;
    public final AdvancedModelBox leftarm2;
    public final AdvancedModelBox rightleg2;
    public final AdvancedModelBox leftleg2;
    private final ModelAnimator animator;

    public ModelTroll() {
        this.texWidth = 256;
        this.texHeight = 128;
        this.log2 = new AdvancedModelBox(this, 10, 70);
        this.log2.setPos(0.0F, 9.0F, -1.0F);
        this.log2.addBox(-2.0F, -6.9F, -3.0F, 7, 24, 6, 0.0F);
        this.setRotateAngle(this.log2, -0.045553093477052F, 0.0F, 0.0F);
        this.top = new AdvancedModelBox(this, 177, 0);
        this.top.setPos(-1.0F, 0.0F, 0.0F);
        this.top.addBox(-1.5F, 20.0F, -3.5F, 10, 4, 10, 0.0F);
        this.hornL = new AdvancedModelBox(this, 60, 101);
        this.hornL.mirror = true;
        this.hornL.setPos(1.3F, -1.5F, -1.0F);
        this.hornL.addBox(-1.5F, -0.5F, 0.0F, 2, 2, 7, 0.0F);
        this.setRotateAngle(this.hornL, -0.5918411493512771F, 2.6406831582674206F, -0.17453292519943295F);
        this.body = new AdvancedModelBox(this, 88, 46);
        this.body.setPos(0.0F, -2.4F, 0.0F);
        this.body.addBox(-5.0F, -7.0F, -3.0F, 10, 9, 8, 0.0F);
        this.leftleg2 = new AdvancedModelBox(this, 0, 15);
        this.leftleg2.mirror = true;
        this.leftleg2.setPos(0.0F, 9.6F, -0.2F);
        this.leftleg2.addBox(-3.0F, 1.0F, -2.0F, 6, 15, 6, 0.0F);
        this.setRotateAngle(this.leftleg2, 0.3490658503988659F, 0.0F, 0.08726646259971647F);
        this.head = new AdvancedModelBox(this, 90, 0);
        this.head.setPos(0.0F, -15.8F, -1.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 10, 8, 0.0F);
        this.setRotateAngle(this.head, -0.18203784098300857F, 0.0F, 0.0F);
        this.blade1 = new AdvancedModelBox(this, 186, 84);
        this.blade1.setPos(0.0F, 0.0F, -2.0F);
        this.blade1.addBox(-1.0F, 12.0F, 0.0F, 2, 10, 10, 0.0F);
        this.hornR = new AdvancedModelBox(this, 60, 101);
        this.hornR.mirror = true;
        this.hornR.setPos(-1.3F, -1.5F, -1.0F);
        this.hornR.addBox(-0.5F, -0.5F, 0.0F, 2, 2, 7, 0.0F);
        this.setRotateAngle(this.hornR, -0.7740535232594852F, -2.9595548126067843F, 0.27314402793711257F);
        this.log1 = new AdvancedModelBox(this, 10, 100);
        this.log1.setPos(-0.5F, 8.0F, -0.4F);
        this.log1.addBox(-1.5F, -17.9F, -3.5F, 6, 20, 6, 0.0F);
        this.setRotateAngle(this.log1, -1.593485607070823F, 0.0F, 0.0F);
        this.bottom = new AdvancedModelBox(this, 177, 0);
        this.bottom.setPos(-1.0F, 0.0F, 0.0F);
        this.bottom.addBox(-1.5F, -21.0F, -3.5F, 10, 4, 10, 0.0F);
        this.nose2 = new AdvancedModelBox(this, 114, 120);
        this.nose2.setPos(0.0F, 5.1F, -3.0F);
        this.nose2.addBox(-1.5F, -1.8F, -1.6F, 3, 4, 2, 0.0F);
        this.setRotateAngle(this.nose2, -1.2747884856566583F, 0.0F, 0.0F);
        this.blade2_1 = new AdvancedModelBox(this, 189, 69);
        this.blade2_1.setPos(0.0F, 2.0F, -1.0F);
        this.blade2_1.addBox(-1.0F, 2.0F, -1.0F, 2, 2, 6, 0.0F);
        this.hornR2 = new AdvancedModelBox(this, 46, 90);
        this.hornR2.mirror = true;
        this.hornR2.setPos(-0.5F, 1.3F, 6.6F);
        this.hornR2.addBox(-0.01F, -0.8F, -0.0F, 2, 2, 7, 0.0F);
        this.setRotateAngle(this.hornR2, 1.2747884856566583F, 0.0F, 0.0F);
        this.leftleg = new AdvancedModelBox(this, 0, 45);
        this.leftleg.mirror = true;
        this.leftleg.setPos(4.0F, 1.2F, 1.0F);
        this.leftleg.addBox(-3.0F, 1.0F, -2.0F, 6, 13, 6, 0.0F);
        this.setRotateAngle(this.leftleg, -0.2617993877991494F, 0.0F, -0.08726646259971647F);
        this.upperBody = new AdvancedModelBox(this, 85, 21);
        this.upperBody.setPos(0.0F, -6.0F, 1.3F);
        this.upperBody.addBox(-6.0F, -13.9F, -4.1F, 12, 14, 9, 0.0F);
        this.setRotateAngle(this.upperBody, 0.31869712141416456F, 0.0F, 0.0F);
        this.rightleg2 = new AdvancedModelBox(this, 0, 15);
        this.rightleg2.setPos(0.0F, 9.6F, -0.2F);
        this.rightleg2.addBox(-3.0F, 1.0F, -2.0F, 6, 15, 6, 0.0F);
        this.setRotateAngle(this.rightleg2, 0.3490658503988659F, 0.0F, -0.08726646259971647F);
        this.leftarm = new AdvancedModelBox(this, 64, 0);
        this.leftarm.mirror = true;
        this.leftarm.setPos(5.0F, -11.2F, -0.4F);
        this.leftarm.addBox(0.0F, -2.0F, -2.0F, 6, 13, 7, 0.0F);
        this.setRotateAngle(this.leftarm, -0.136659280431156F, 0.0F, -0.17453292519943295F);
        this.mouth = new AdvancedModelBox(this, 114, 0);
        this.mouth.setPos(0.0F, 0.3F, -1.0F);
        this.mouth.addBox(-2.5F, -0.6F, -4.6F, 5, 3, 2, 0.0F);
        this.setRotateAngle(this.mouth, -0.18203784098300857F, 0.0F, 0.0F);
        this.jaw = new AdvancedModelBox(this, 40, 11);
        this.jaw.setPos(0.0F, 2.3F, -2.0F);
        this.jaw.addBox(-2.0F, -0.6F, -4.6F, 4, 2, 6, 0.0F);
        this.setRotateAngle(this.jaw, -0.091106186954104F, 0.0F, 0.0F);
        this.hornL2 = new AdvancedModelBox(this, 46, 90);
        this.hornL2.mirror = true;
        this.hornL2.setPos(-0.5F, 1.3F, 6.6F);
        this.hornL2.addBox(-1.01F, -0.8F, -0.0F, 2, 2, 7, 0.0F);
        this.setRotateAngle(this.hornL2, 1.4114477660878142F, 0.0F, 0.0F);
        this.rightleg = new AdvancedModelBox(this, 0, 45);
        this.rightleg.setPos(-4.0F, 1.2F, 1.0F);
        this.rightleg.addBox(-3.0F, 1.0F, -2.0F, 6, 13, 6, 0.0F);
        this.setRotateAngle(this.rightleg, -0.2617993877991494F, 0.0F, 0.08726646259971647F);
        this.column = new AdvancedModelBox(this, 220, 28);
        this.column.setPos(0.0F, 0.0F, -3.0F);
        this.column.addBox(-1.5F, -20.0F, -2.5F, 8, 42, 8, 0.0F);
        this.setRotateAngle(this.column, -0.045553093477052F, 0.0F, 0.0F);
        this.leftarm2 = new AdvancedModelBox(this, 60, 24);
        this.leftarm2.mirror = true;
        this.leftarm2.setPos(2.1F, 10.0F, -0.1F);
        this.leftarm2.addBox(-2.0F, -5.0F, -1.7F, 5, 16, 7, 0.0F);
        this.setRotateAngle(this.leftarm2, -0.31869712141416456F, 0.0F, 0.0F);
        this.rightarm = new AdvancedModelBox(this, 64, 0);
        this.rightarm.setPos(-5.0F, -11.2F, -0.4F);
        this.rightarm.addBox(-6.0F, -2.0F, -2.0F, 6, 13, 7, 0.0F);
        this.setRotateAngle(this.rightarm, -0.045553093477052F, 0.27314402793711257F, 0.0F);
        this.handle = new AdvancedModelBox(this, 232, 80);
        this.handle.setPos(0.3F, 0.0F, 1.0F);
        this.handle.addBox(-1.5F, -20.0F, -1.5F, 3, 42, 3, 0.0F);
        this.setRotateAngle(this.handle, -0.045553093477052F, 0.0F, 0.0F);
        this.block = new AdvancedModelBox(this, 182, 19);
        this.block.setPos(0.0F, 0.0F, 0.0F);
        this.block.addBox(-2.0F, 11.0F, -8.0F, 4, 10, 15, 0.0F);
        this.blade2_2 = new AdvancedModelBox(this, 161, 99);
        this.blade2_2.setPos(0.0F, 17.0F, 9.0F);
        this.blade2_2.addBox(-1.0F, -5.0F, -6.0F, 2, 5, 5, 0.0F);
        this.setRotateAngle(this.blade2_2, 3.141592653589793F, 0.0F, 0.0F);
        this.loin = new AdvancedModelBox(this, 50, 56);
        this.loin.setPos(0.0F, 0.0F, 0.0F);
        this.loin.addBox(-5.5F, 0.0F, -3.5F, 11, 13, 9, 0.0F);
        this.nose = new AdvancedModelBox(this, 114, 100);
        this.nose.setPos(0.0F, -4.5F, -0.2F);
        this.nose.addBox(-1.0F, -0.6F, -4.6F, 2, 5, 2, 0.0F);
        this.setRotateAngle(this.nose, -0.5462880558742251F, 0.0F, 0.0F);
        this.teeth = new AdvancedModelBox(this, 39, 0);
        this.teeth.setPos(0.0F, 2.0F, -1.0F);
        this.teeth.addBox(-2.1F, -3.6F, -3.6F, 4, 2, 6, 0.0F);
        this.chest = new AdvancedModelBox(this, 93, 29);
        this.chest.setPos(0.0F, -8.9F, 0.5F);
        this.chest.addBox(-5.0F, -2.0F, -4.8F, 10, 6, 2, 0.0F);
        this.setRotateAngle(this.chest, -0.22759093446006054F, 0.0F, 0.0F);
        this.blade2 = new AdvancedModelBox(this, 186, 66);
        this.blade2.setPos(0.0F, 7.0F, -1.0F);
        this.blade2.addBox(-1.0F, 1.0F, -1.0F, 2, 2, 9, 0.0F);
        this.rightarm2 = new AdvancedModelBox(this, 60, 24);
        this.rightarm2.setPos(-2.1F, 10.0F, 0.1F);
        this.rightarm2.addBox(-3.0F, -5.0F, -1.7F, 5, 16, 7, 0.0F);
        this.setRotateAngle(this.rightarm2, -2.1399481958702475F, 0.22759093446006054F, -0.136659280431156F);
        this.log1.addChild(this.log2);
        this.column.addChild(this.top);
        this.jaw.addChild(this.hornL);
        this.leftleg.addChild(this.leftleg2);
        this.upperBody.addChild(this.head);
        this.handle.addChild(this.blade1);
        this.jaw.addChild(this.hornR);
        this.rightarm2.addChild(this.log1);
        this.column.addChild(this.bottom);
        this.nose.addChild(this.nose2);
        this.handle.addChild(this.blade2_1);
        this.hornR.addChild(this.hornR2);
        this.body.addChild(this.leftleg);
        this.body.addChild(this.upperBody);
        this.rightleg.addChild(this.rightleg2);
        this.upperBody.addChild(this.leftarm);
        this.head.addChild(this.mouth);
        this.head.addChild(this.jaw);
        this.hornL.addChild(this.hornL2);
        this.body.addChild(this.rightleg);
        this.log1.addChild(this.column);
        this.leftarm.addChild(this.leftarm2);
        this.upperBody.addChild(this.rightarm);
        this.log1.addChild(this.handle);
        this.handle.addChild(this.block);
        this.blade1.addChild(this.blade2_2);
        this.body.addChild(this.loin);
        this.head.addChild(this.nose);
        this.jaw.addChild(this.teeth);
        this.upperBody.addChild(this.chest);
        this.handle.addChild(this.blade2);
        this.rightarm.addChild(this.rightarm2);
        this.animator = ModelAnimator.create();
        this.updateDefaultPose();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.body, this.upperBody, this.loin, this.rightleg, this.leftleg, this.head, this.rightarm,
                this.leftarm, this.chest, this.jaw, this.mouth, this.nose, this.teeth, this.hornL, this.hornR, this.hornL2, this.hornR2, this.nose2,
                this.rightarm2, this.log1, this.log2, this.handle, this.column, this.blade1, this.blade2, this.blade2_1, this.block, this.blade2_2,
                this.bottom, this.top, this.leftarm2, this.rightleg2, this.leftleg2);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.log1.showModel = true;
        this.resetToDefaultPose();
        this.animator.update(entity);
        if (this.animator.setAnimation(EntityTroll.ANIMATION_SPEAK)) {
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.jaw, 25, 0, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.jaw, 0, 0, 0);
            this.animator.endKeyframe();
        }
        if (this.animator.setAnimation(EntityTroll.ANIMATION_ROAR)) {
            this.animator.startKeyframe(5);
            this.rotateMinus(this.animator, this.leftleg, -31, -26, -5);
            this.rotateMinus(this.animator, this.rightleg, -31, 26, 5);
            this.rotate(this.animator, this.upperBody, 18, 0, 5);
            this.rotateMinus(this.animator, this.leftleg2, 41, 0, 5);
            this.rotateMinus(this.animator, this.rightleg2, 41, 0, -5);
            this.rotateMinus(this.animator, this.leftarm, -26, -44, -2);
            this.rotateMinus(this.animator, this.leftarm2, -60, 0, 0);
            this.rotateMinus(this.animator, this.rightarm, -39, 57, 0);
            this.rotateMinus(this.animator, this.rightarm2, -73, 13, -7);
            this.rotate(this.animator, this.head, -57, 0, 0);
            this.rotate(this.animator, this.jaw, 60, 0, 0);
            this.animator.move(this.body, 0, 2, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotateMinus(this.animator, this.leftleg, -31, -26, -5);
            this.rotateMinus(this.animator, this.rightleg, -31, 26, 5);
            this.rotate(this.animator, this.upperBody, 18, 0, 5);
            this.rotateMinus(this.animator, this.leftleg2, 41, 0, 5);
            this.rotateMinus(this.animator, this.rightleg2, 41, 0, -5);
            this.rotateMinus(this.animator, this.leftarm, -26, -44, -2);
            this.rotateMinus(this.animator, this.leftarm2, -60, 0, 0);
            this.rotateMinus(this.animator, this.rightarm, -39, 57, 0);
            this.rotateMinus(this.animator, this.rightarm2, -73, 13, -7);
            this.rotate(this.animator, this.head, -57, -28, 0);
            this.rotate(this.animator, this.jaw, 60, 0, 0);
            this.animator.move(this.body, 0, 2, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotateMinus(this.animator, this.leftleg, -31, -26, -5);
            this.rotateMinus(this.animator, this.rightleg, -31, 26, 5);
            this.rotate(this.animator, this.upperBody, 18, 0, 5);
            this.rotateMinus(this.animator, this.leftleg2, 41, 0, 5);
            this.rotateMinus(this.animator, this.rightleg2, 41, 0, -5);
            this.rotateMinus(this.animator, this.leftarm, -26, -44, -2);
            this.rotateMinus(this.animator, this.leftarm2, -60, 0, 0);
            this.rotateMinus(this.animator, this.rightarm, -39, 57, 0);
            this.rotateMinus(this.animator, this.rightarm2, -73, 13, -7);
            this.rotate(this.animator, this.head, -57, 28, 0);
            this.rotate(this.animator, this.jaw, 60, 0, 0);
            this.animator.move(this.body, 0, 2, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(10);
        }
        if (this.animator.setAnimation(EntityTroll.ANIMATION_STRIKE_HORIZONTAL)) {
            this.animator.startKeyframe(10);
            this.rotate(this.animator, this.body, 0, 31, 0);
            this.rotate(this.animator, this.upperBody, 18, 39, 0);
            this.rotate(this.animator, this.leftarm, 18, 0, -10);
            this.rotate(this.animator, this.rightarm, 41, 57, 65);
            this.rotate(this.animator, this.rightarm2, 50, 0, 0);
            this.rotate(this.animator, this.rightleg, -15, 57, 5);
            this.rotate(this.animator, this.leftleg, -13, -44, -5);
            this.animator.move(this.body, 0, 3, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.body, 0, 31, 0);
            this.rotate(this.animator, this.upperBody, 18, -39, 0);
            this.rotate(this.animator, this.leftarm, 18, 0, -10);
            this.rotate(this.animator, this.rightarm, -60, -40, -35);
            this.rotate(this.animator, this.rightarm2, 80, -45, 40);
            this.rotate(this.animator, this.rightleg, -15, 57, 5);
            this.rotate(this.animator, this.leftleg, -13, -44, -5);
            this.rotate(this.animator, this.log1, 15, 0, 0);
            this.animator.move(this.body, 0, 3, 0);
            this.animator.endKeyframe();
            this.animator.resetKeyframe(5);
        }
        if (this.animator.setAnimation(EntityTroll.ANIMATION_STRIKE_VERTICAL)) {
            this.animator.startKeyframe(7);
            this.rotate(this.animator, this.upperBody, -30, 0, 0);
            this.rotate(this.animator, this.rightleg, -15, 57, 5);
            this.rotate(this.animator, this.leftleg, -13, -44, -5);
            this.rotate(this.animator, this.leftarm, -203, 35, -15);
            this.rotate(this.animator, this.rightarm, -212, -40, 25);
            this.rotate(this.animator, this.leftarm2, 18, 0, 0);
            this.rotate(this.animator, this.rightarm2, 122, -13, 7);
            this.rotate(this.animator, this.log1, 0, -40, 0);
            this.animator.move(this.body, 0, 3, 0);
            this.animator.move(this.log1, 5, 0, 0);
            this.animator.endKeyframe();
            this.animator.startKeyframe(5);
            this.rotate(this.animator, this.body, 5, 0, 0);
            this.rotate(this.animator, this.upperBody, 35, 0, 0);
            this.rotate(this.animator, this.rightleg, -15, 57, 5);
            this.rotate(this.animator, this.leftleg, -13, -44, -5);
            this.rotate(this.animator, this.leftarm, -103, 20, -15);
            this.rotate(this.animator, this.rightarm, -112, -20, 25);
            this.rotate(this.animator, this.leftarm2, 18, 0, 0);
            this.rotate(this.animator, this.rightarm2, 122, -13, 7);
            this.rotate(this.animator, this.log1, 90, 25, 20);
            this.animator.move(this.body, 0, 3, 0);
            this.animator.move(this.log1, 2, 0, 0);
            this.animator.endKeyframe();
            this.animator.setStaticKeyframe(3);
            this.animator.resetKeyframe(5);
            this.animator.endKeyframe();
        }
    }

    @Override
    public void setAngles(EntityTroll entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.resetToDefaultPose();
        this.log1.showModel = true;

        this.animate(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 1);

        this.progressRotation(this.head, entity.stoneProgress, (float) Math.toRadians(-31), 0.0F, 0.0F);
        this.progressRotation(this.jaw, entity.stoneProgress, (float) Math.toRadians(54), 0.0F, 0.0F);
        this.progressRotation(this.leftarm, entity.stoneProgress, (float) Math.toRadians(10), (float) Math.toRadians(-73), (float) Math.toRadians(-60));
        this.progressRotation(this.leftarm2, entity.stoneProgress, (float) Math.toRadians(-80), 0.0F, 0.0F);
        this.progressRotation(this.rightarm, entity.stoneProgress, (float) Math.toRadians(-101), (float) Math.toRadians(70), 0);
        this.progressRotation(this.rightarm2, entity.stoneProgress, (float) Math.toRadians(-40), 0.0F, 0.0F);

        float speed_walk = 0.2F;
        float speed_idle = 0.05F;
        float degree_walk = 0.75F;
        float degree_idle = 0.5F;
        this.walk(this.rightleg, speed_walk, degree_walk * -0.75F, true, 0, 0F, limbAngle, limbDistance);
        this.walk(this.leftleg, speed_walk, degree_walk * -0.75F, false, 0, 0F, limbAngle, limbDistance);
        this.walk(this.rightleg2, speed_walk, degree_walk * -0.5F, true, 1, -0.3F, limbAngle, limbDistance);
        this.walk(this.leftleg2, speed_walk, degree_walk * -0.5F, false, 1, 0.3F, limbAngle, limbDistance);
        this.walk(this.leftarm, speed_walk, degree_walk * -0.75F, true, 0, 0F, limbAngle, limbDistance);
        this.walk(this.leftarm2, speed_walk, degree_walk * -0.5F, true, 1, 0.3F, limbAngle, limbDistance);
        this.swing(this.body, speed_walk, degree_walk * -0.5F, false, 0, 0F, limbAngle, limbDistance);
        this.swing(this.upperBody, speed_walk, degree_walk * -0.25F, true, 0, 0F, limbAngle, limbDistance);
        this.walk(this.rightarm, speed_walk, degree_walk * -0.25F, false, 0, 0F, limbAngle, limbDistance);
        this.walk(this.rightarm2, speed_walk, degree_walk * -0.125F, false, 1, -0.3F, limbAngle, limbDistance);
        this.walk(this.body, speed_idle, degree_idle * -0.1F, true, 0F, -0.1F, animationProgress, 1);
        this.walk(this.rightleg, speed_idle, degree_idle * 0.1F, true, 0F, 0.1F, animationProgress, 1);
        this.walk(this.leftleg, speed_idle, degree_idle * 0.1F, true, 0F, 0.1F, animationProgress, 1);

        this.walk(this.jaw, speed_idle, degree_idle * -0.15F, true, 0F, -0.1F, animationProgress, 1);
        this.walk(this.mouth, speed_idle, degree_idle * -0.15F, false, 0F, -0.1F, animationProgress, 1);
        this.faceTarget(headYaw, headPitch, 1, this.head);

    }

    public void animateStatue(EntityTroll troll) {
        this.progressRotation(this.head, 20, (float) Math.toRadians(-31), 0.0F, 0.0F);
        this.progressRotation(this.jaw, 20, (float) Math.toRadians(54), 0.0F, 0.0F);
        this.progressRotation(this.leftarm, 20, (float) Math.toRadians(10), (float) Math.toRadians(-73), (float) Math.toRadians(-60));
        this.progressRotation(this.leftarm2, 20, (float) Math.toRadians(-80), 0.0F, 0.0F);
        this.progressRotation(this.rightarm, 20, (float) Math.toRadians(-101), (float) Math.toRadians(70), 0);
        this.progressRotation(this.rightarm2, 20, (float) Math.toRadians(-40), 0.0F, 0.0F);
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.animateStatue((EntityTroll) living);
        this.log1.showModel = false;
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
