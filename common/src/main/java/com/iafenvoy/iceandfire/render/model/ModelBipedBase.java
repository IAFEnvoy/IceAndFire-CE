package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import com.iafenvoy.uranus.client.model.util.HideableModelRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

public abstract class ModelBipedBase<T extends LivingEntity> extends AdvancedEntityModel<T> implements ICustomStatueModel, BasicHeadedModel, ModelWithArms {
    public HideableModelRenderer head;
    public HideableModelRenderer headware;
    public HideableModelRenderer body;
    public HideableModelRenderer armRight;
    public HideableModelRenderer armLeft;
    public HideableModelRenderer legRight;
    public HideableModelRenderer legLeft;
    public BipedEntityModel.ArmPose leftArmPose;
    public BipedEntityModel.ArmPose rightArmPose;
    public boolean isSneak;
    protected ModelAnimator animator;

    //Make sure we don't have any null boxes which would cause issues with getAllParts()
    protected ModelBipedBase() {
        this.head = new HideableModelRenderer(this, 0, 0);
        this.headware = new HideableModelRenderer(this, 0, 0);
        this.body = new HideableModelRenderer(this, 0, 0);
        this.armRight = new HideableModelRenderer(this, 0, 0);
        this.armLeft = new HideableModelRenderer(this, 0, 0);
        this.legRight = new HideableModelRenderer(this, 0, 0);
        this.legLeft = new HideableModelRenderer(this, 0, 0);
    }

    @Override
    public BasicModelPart getHead() {
        return this.head;
    }

    @Override
    public void setArmAngle(Arm sideIn, MatrixStack matrixStackIn) {
        this.getArmForSide(sideIn).translateAndRotate(matrixStackIn);
    }

    protected HideableModelRenderer getArmForSide(Arm side) {
        return side == Arm.LEFT ? this.armLeft : this.armRight;
    }

    protected Arm getMainHand(Entity entityIn) {
        if (entityIn instanceof LivingEntity LivingEntity) {
            Arm Handside = LivingEntity.getMainArm();
            return LivingEntity.preferredHand == Hand.MAIN_HAND ? Handside : Handside.getOpposite();
        } else return Arm.RIGHT;
    }

    public void progressRotationInterp(AdvancedModelBox model, float progress, float rotX, float rotY, float rotZ, float max) {
        model.rotateAngleX += progress * (rotX - model.defaultRotationX) / max;
        model.rotateAngleY += progress * (rotY - model.defaultRotationY) / max;
        model.rotateAngleZ += progress * (rotZ - model.defaultRotationZ) / max;
    }

    public void progresPositionInterp(AdvancedModelBox model, float progress, float x, float y, float z, float max) {
        model.rotationPointX += progress * (x) / max;
        model.rotationPointY += progress * (y) / max;
        model.rotationPointZ += progress * (z) / max;
    }

    public void progressRotation(AdvancedModelBox model, float progress, float rotX, float rotY, float rotZ) {
        model.rotateAngleX += progress * (rotX - model.defaultRotationX) / 20.0F;
        model.rotateAngleY += progress * (rotY - model.defaultRotationY) / 20.0F;
        model.rotateAngleZ += progress * (rotZ - model.defaultRotationZ) / 20.0F;
    }

    public void progressRotationPrev(AdvancedModelBox model, float progress, float rotX, float rotY, float rotZ) {
        model.rotateAngleX += progress * (rotX) / 20.0F;
        model.rotateAngleY += progress * (rotY) / 20.0F;
        model.rotateAngleZ += progress * (rotZ) / 20.0F;
    }

    public void progressPosition(AdvancedModelBox model, float progress, float x, float y, float z) {
        model.rotationPointX += progress * (x - model.defaultPositionX) / 20.0F;
        model.rotationPointY += progress * (y - model.defaultPositionY) / 20.0F;
        model.rotationPointZ += progress * (z - model.defaultPositionZ) / 20.0F;
    }

    public void progressPositionPrev(AdvancedModelBox model, float progress, float x, float y, float z) {
        model.rotationPointX += progress * x / 20.0F;
        model.rotationPointY += progress * y / 20.0F;
        model.rotationPointZ += progress * z / 20.0F;
    }

    public <U extends BasicModelPart> void copyFrom(U modelIn, U currentModel) {
        modelIn.copyModelAngles(currentModel);
        modelIn.rotationPointX = currentModel.rotationPointX;
        modelIn.rotationPointY = currentModel.rotationPointY;
        modelIn.rotationPointZ = currentModel.rotationPointZ;
    }

    public <M extends ModelPart, U extends BasicModelPart> void copyFrom(M modelIn, U currentModel) {
        modelIn.setAngles(currentModel.rotateAngleX, currentModel.rotateAngleY, currentModel.rotateAngleZ);
        modelIn.pivotX = currentModel.rotationPointX;
        modelIn.pivotY = currentModel.rotationPointY;
        modelIn.pivotZ = currentModel.rotationPointZ;
    }

    public void setModelAttributes(ModelBipedBase<T> modelIn) {
        super.copyStateTo(modelIn);
        modelIn.animator = this.animator;
        modelIn.leftArmPose = this.leftArmPose;
        modelIn.rightArmPose = this.rightArmPose;
        modelIn.isSneak = this.isSneak;
        this.copyFrom(modelIn.head, this.head);
        this.copyFrom(modelIn.headware, this.headware);
        this.copyFrom(modelIn.body, this.body);
        this.copyFrom(modelIn.armRight, this.armRight);
        this.copyFrom(modelIn.armLeft, this.armLeft);
        this.copyFrom(modelIn.legRight, this.legRight);
        this.copyFrom(modelIn.legLeft, this.legLeft);
    }

    public void setModelAttributes(BipedEntityModel<T> modelIn) {
        super.copyStateTo(modelIn);
        modelIn.leftArmPose = this.leftArmPose;
        modelIn.rightArmPose = this.rightArmPose;
        modelIn.sneaking = this.isSneak;
        this.copyFrom(modelIn.head, this.head);
        this.copyFrom(modelIn.hat, this.headware);
        this.copyFrom(modelIn.body, this.body);
        this.copyFrom(modelIn.rightArm, this.armRight);
        this.copyFrom(modelIn.leftArm, this.armLeft);
        this.copyFrom(modelIn.rightLeg, this.legRight);
        this.copyFrom(modelIn.leftLeg, this.legLeft);
    }

    public void setVisible(boolean visible) {
        this.head.invisible = !visible;
        this.headware.invisible = !visible;
        this.body.invisible = !visible;
        this.armRight.invisible = !visible;
        this.armLeft.invisible = !visible;
        this.legRight.invisible = !visible;
        this.legLeft.invisible = !visible;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.resetToDefaultPose();
        this.animate(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 0);
        this.faceTarget(headYaw, headPitch, 1.0F, this.head);
        float f = 1.0F;
        this.armRight.rotateAngleX += MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 2.0F * limbDistance * 0.5F / f;
        this.armLeft.rotateAngleX += MathHelper.cos(limbAngle * 0.6662F) * 2.0F * limbDistance * 0.5F / f;
        this.legRight.rotateAngleX = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance / f;
        this.legLeft.rotateAngleX = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance / f;
        this.legRight.rotateAngleY = 0.0F;
        this.legLeft.rotateAngleY = 0.0F;
        this.legRight.rotateAngleZ = 0.0F;
        this.legLeft.rotateAngleZ = 0.0F;

        if (entity.hasVehicle()) {
            this.armRight.rotateAngleX -= ((float) Math.PI / 5F);
            this.armLeft.rotateAngleX -= ((float) Math.PI / 5F);
            this.legRight.rotateAngleX = -1.4137167F;
            this.legRight.rotateAngleY = ((float) Math.PI / 10F);
            this.legRight.rotateAngleZ = 0.07853982F;
            this.legLeft.rotateAngleX = -1.4137167F;
            this.legLeft.rotateAngleY = -((float) Math.PI / 10F);
            this.legLeft.rotateAngleZ = -0.07853982F;
        }
        if (this.handSwingProgress > 0.0F) {
            Arm handSide = this.getMainHand(entity);
            HideableModelRenderer modelrenderer = this.getArmForSide(handSide);
            float f1 = this.handSwingProgress;
            this.body.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;

            if (handSide == Arm.LEFT)
                this.body.rotateAngleY *= -1.0F;

            this.armRight.rotationPointZ = MathHelper.sin(this.body.rotateAngleY) * 5.0F;
            this.armRight.rotationPointX = -MathHelper.cos(this.body.rotateAngleY) * 5.0F;
            this.armLeft.rotationPointZ = -MathHelper.sin(this.body.rotateAngleY) * 5.0F;
            this.armLeft.rotationPointX = MathHelper.cos(this.body.rotateAngleY) * 5.0F;
            this.armRight.rotateAngleY += this.body.rotateAngleY;
            this.armLeft.rotateAngleY += this.body.rotateAngleY;
            this.armLeft.rotateAngleX += this.body.rotateAngleX;
            f1 = 1.0F - this.handSwingProgress;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float) Math.PI);
            float f3 = MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -(this.head.rotateAngleX - 0.7F) * 0.75F;
            modelrenderer.rotateAngleX = (float) ((double) modelrenderer.rotateAngleX - ((double) f2 * 1.2D + (double) f3));
            modelrenderer.rotateAngleY += this.body.rotateAngleY * 2.0F;
            modelrenderer.rotateAngleZ += MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -0.4F;
        }
        if (this.isSneak) {
            this.body.rotateAngleX = 0.5F;
            this.armRight.rotateAngleX += 0.4F;
            this.armLeft.rotateAngleX += 0.4F;
            this.legRight.rotationPointZ = 4.0F;
            this.legLeft.rotationPointZ = 4.0F;
            this.legRight.rotationPointY = 9.0F;
            this.legLeft.rotationPointY = 9.0F;
            this.head.rotationPointY = 1.0F;
        } else {
            this.body.rotateAngleX = 0.0F;
            this.legRight.rotationPointZ = 0.1F;
            this.legLeft.rotationPointZ = 0.1F;
            this.legRight.rotationPointY = 12.0F;
            this.legLeft.rotationPointY = 12.0F;
            this.head.rotationPointY = 0.0F;
        }

        this.armRight.rotateAngleZ += MathHelper.cos(animationProgress * 0.09F) * 0.05F + 0.05F;
        this.armLeft.rotateAngleZ -= MathHelper.cos(animationProgress * 0.09F) * 0.05F + 0.05F;
        this.armRight.rotateAngleX += MathHelper.sin(animationProgress * 0.067F) * 0.05F;
        this.armLeft.rotateAngleX -= MathHelper.sin(animationProgress * 0.067F) * 0.05F;

    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
    }

    abstract void animate(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float f);

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.head, this.headware, this.body, this.armRight, this.armLeft, this.legRight, this.legLeft);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.body);
    }
}
