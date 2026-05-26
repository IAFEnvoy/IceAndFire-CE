package com.iafenvoy.iceandfire.render.model;

import com.iafenvoy.iceandfire.entity.DreadLichEntity;
import com.iafenvoy.uranus.animation.Animation;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.util.HideableModelRenderer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;

public class DreadLichModel extends DreadBaseModel<DreadLichEntity> implements ArmedModel {
    public final HideableModelRenderer robe;
    public final HideableModelRenderer mask;
    public final HideableModelRenderer hood;
    public final HideableModelRenderer sleeveRight;
    public final HideableModelRenderer robeLowerRight;
    public final HideableModelRenderer robeLowerLeft;
    public final HideableModelRenderer sleeveLeft;

    public DreadLichModel(float modelScale) {
        this.texWidth = 128;
        this.texHeight = 64;
        this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        this.sleeveLeft = new HideableModelRenderer(this, 33, 35);
        this.sleeveLeft.mirror = true;
        this.sleeveLeft.setPos(0.0F, -0.1F, 0.0F);
        this.sleeveLeft.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelScale);
        this.robeLowerRight = new HideableModelRenderer(this, 48, 35);
        this.robeLowerRight.mirror = true;
        this.robeLowerRight.setPos(0.0F, 0.0F, 0.0F);
        this.robeLowerRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelScale);
        this.legLeft = new HideableModelRenderer(this, 0, 16);
        this.legLeft.mirror = true;
        this.legLeft.setPos(2.0F, 12.0F, 0.1F);
        this.legLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, modelScale);
        this.robe = new HideableModelRenderer(this, 4, 34);
        this.robe.setPos(0.0F, 0.1F, 0.0F);
        this.robe.addBox(-4.5F, 0.0F, -2.5F, 9, 12, 5, modelScale);
        this.legRight = new HideableModelRenderer(this, 0, 16);
        this.legRight.setPos(-2.0F, 12.0F, 0.1F);
        this.legRight.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, modelScale);
        this.sleeveRight = new HideableModelRenderer(this, 33, 35);
        this.sleeveRight.setPos(0.0F, -0.1F, 0.0F);
        this.sleeveRight.addBox(-2.2F, -2.0F, -2.0F, 3, 12, 4, modelScale);
        this.mask = new HideableModelRenderer(this, 40, 6);
        this.mask.setPos(0.0F, 0.0F, 0.0F);
        this.mask.addBox(-3.5F, -10F, -4.1F, 7, 8, 0, modelScale);
        this.armRight = new HideableModelRenderer(this, 40, 16);
        this.armRight.setPos(-5.0F, 2.0F, 0.0F);
        this.armRight.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, modelScale);
        this.setRotateAngle(this.armRight, 0.0F, -0.10000736613927509F, 0.10000736613927509F);
        this.hood = new HideableModelRenderer(this, 60, 0);
        this.hood.setPos(0.0F, 0.0F, 0.0F);
        this.hood.addBox(-4.5F, -8.6F, -4.5F, 9, 9, 9, modelScale);
        this.head = new HideableModelRenderer(this, 0, 0);
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelScale);
        this.body = new HideableModelRenderer(this, 16, 16);
        this.body.setPos(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelScale);
        this.armLeft = new HideableModelRenderer(this, 40, 16);
        this.armLeft.mirror = true;
        this.armLeft.setPos(5.0F, 2.0F, -0.0F);
        this.armLeft.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, modelScale);
        this.setRotateAngle(this.armLeft, 0.0F, 0.10000736613927509F, -0.10000736613927509F);
        this.robeLowerLeft = new HideableModelRenderer(this, 48, 35);
        this.robeLowerLeft.setPos(0.0F, 0.0F, 0.0F);
        this.robeLowerLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelScale);
        this.armLeft.addChild(this.sleeveLeft);
        this.legRight.addChild(this.robeLowerRight);
        this.body.addChild(this.legLeft);
        this.body.addChild(this.robe);
        this.body.addChild(this.legRight);
        this.armRight.addChild(this.sleeveRight);
        this.head.addChild(this.mask);
        this.body.addChild(this.armRight);
        this.head.addChild(this.hood);
        this.body.addChild(this.head);
        this.body.addChild(this.armLeft);
        this.legLeft.addChild(this.robeLowerLeft);
        this.updateDefaultPose();
        this.animator = ModelAnimator.create();
    }

    @Override
    public Animation getSpawnAnimation() {
        return DreadLichEntity.ANIMATION_SPAWN;
    }

    @Override
    public void setupAnim(DreadLichEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        super.setupAnim(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        if (entity.getAnimation() == DreadLichEntity.ANIMATION_SUMMON) {
            this.armRight.rotationPointZ = 0.0F;
            this.armRight.rotationPointX = -5.0F;
            this.armLeft.rotationPointZ = 0.0F;
            this.armLeft.rotationPointX = 5.0F;
            this.armRight.rotateAngleX = Mth.cos(animationProgress * 0.6662F) * 0.25F;
            this.armLeft.rotateAngleX = Mth.cos(animationProgress * 0.6662F) * 0.25F;
            this.armRight.rotateAngleZ = 2.3561945F;
            this.armLeft.rotateAngleZ = -2.3561945F;
            this.armRight.rotateAngleY = 0.0F;
            this.armLeft.rotateAngleY = 0.0F;
        }
    }
}
