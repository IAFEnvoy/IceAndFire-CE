package com.iafenvoy.iceandfire.render.model.animator;

import com.iafenvoy.iceandfire.entity.EntitySeaSerpent;
import com.iafenvoy.iceandfire.render.model.util.EnumSeaSerpentAnimations;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ITabulaModelAnimator;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.iafenvoy.uranus.client.model.util.TabulaModelHandlerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class SeaSerpentTabulaModelAnimator extends IceAndFireTabulaModelAnimator<EntitySeaSerpent> implements ITabulaModelAnimator<EntitySeaSerpent> {
    public final EnumSeaSerpentAnimations[] swimPose = {EnumSeaSerpentAnimations.SWIM1, EnumSeaSerpentAnimations.SWIM3, EnumSeaSerpentAnimations.SWIM4, EnumSeaSerpentAnimations.SWIM6};

    public SeaSerpentTabulaModelAnimator() {
        super(resolve(EnumSeaSerpentAnimations.T_POSE.getModelId()));
    }

    @Override
    public void setRotationAngles(TabulaModel<EntitySeaSerpent> model, EntitySeaSerpent entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        model.resetToDefaultPose();
        model.getCube("BodyUpper").rotationPointY += 9;//model was made too high
        model.animator.update(entity);
        this.animate(model, entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale);
        int currentIndex = entity.swimCycle / 10;
        int prevIndex = currentIndex - 1;
        if (prevIndex < 0) prevIndex = 3;
        TabulaModel<EntitySeaSerpent> prevPosition = resolve(this.swimPose[prevIndex].getModelId());
        TabulaModel<EntitySeaSerpent> currentPosition = resolve(this.swimPose[currentIndex].getModelId());
        if (prevPosition == null || currentPosition == null) return;
        float partialTicks = MinecraftClient.getInstance().getTickDelta();
        float delta = ((entity.swimCycle) / 10.0F) % 1.0F + (partialTicks / 10.0F);

        for (AdvancedModelBox cube : model.getCubes().values()) {
            if (entity.jumpProgress > 0.0F)
                if (!this.isRotationEqual(cube, resolve(EnumSeaSerpentAnimations.JUMPING2.getModelId()).getCube(cube.boxName)))
                    this.transitionTo(cube, resolve(EnumSeaSerpentAnimations.JUMPING2.getModelId()).getCube(cube.boxName), entity.jumpProgress, 5, false);
            if (entity.wantJumpProgress > 0.0F)
                if (!this.isRotationEqual(cube, resolve(EnumSeaSerpentAnimations.JUMPING1.getModelId()).getCube(cube.boxName)))
                    this.transitionTo(cube, resolve(EnumSeaSerpentAnimations.JUMPING1.getModelId()).getCube(cube.boxName), entity.wantJumpProgress, 10, false);
            AdvancedModelBox prevPositionCube = prevPosition.getCube(cube.boxName);
            AdvancedModelBox currPositionCube = currentPosition.getCube(cube.boxName);
            float prevX = prevPositionCube.rotateAngleX;
            float prevY = prevPositionCube.rotateAngleY;
            float prevZ = prevPositionCube.rotateAngleZ;
            float x = currPositionCube.rotateAngleX;
            float y = currPositionCube.rotateAngleY;
            float z = currPositionCube.rotateAngleZ;
            this.addToRotateAngle(cube, limbSwingAmount, prevX + delta * this.distance(prevX, x), prevY + delta * this.distance(prevY, y), prevZ + delta * this.distance(prevZ, z));

        }
        if (entity.breathProgress > 0.0F) {
            this.progressRotation(model.getCube("Head"), entity.breathProgress, (float) Math.toRadians(-15F), 0, 0);
            this.progressRotation(model.getCube("HeadFront"), entity.breathProgress, (float) Math.toRadians(-20F), 0, 0);
            this.progressRotation(model.getCube("Jaw"), entity.breathProgress, (float) Math.toRadians(60F), 0, 0);
        }
        if (entity.jumpRot > 0.0F) {
            float jumpRot = entity.prevJumpRot + (entity.jumpRot - entity.prevJumpRot) * partialTicks;
            float turn = (float) entity.getVelocity().y * -4F;
            model.getCube("BodyUpper").rotateAngleX += (float) Math.toRadians(22.5F * turn) * jumpRot;
            model.getCube("Tail1").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
            model.getCube("Tail2").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
            model.getCube("Tail3").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
            model.getCube("Tail4").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
        }
        float prevRenderOffset = entity.prevBodyYaw + (entity.bodyYaw - entity.prevBodyYaw) * partialTicks;

        model.getCube("Tail1").rotateAngleY += (entity.getPieceYaw(1, partialTicks) - prevRenderOffset) * ((float) Math.PI / 180F);
        model.getCube("Tail2").rotateAngleY += (entity.getPieceYaw(2, partialTicks) - prevRenderOffset) * ((float) Math.PI / 180F);
        model.getCube("Tail3").rotateAngleY += (entity.getPieceYaw(3, partialTicks) - prevRenderOffset) * ((float) Math.PI / 180F);
        model.getCube("Tail4").rotateAngleY += (entity.getPieceYaw(4, partialTicks) - prevRenderOffset) * ((float) Math.PI / 180F);
        model.getCube("BodyUpper").rotateAngleX -= rotationPitch * ((float) Math.PI / 180F);
        if (!entity.isJumpingOutOfWater() || entity.isTouchingWater()) {
            model.getCube("Tail1").rotateAngleX -= (entity.getPiecePitch(1, partialTicks) - 0) * ((float) Math.PI / 180F);
            model.getCube("Tail2").rotateAngleX -= (entity.getPiecePitch(2, partialTicks) - 0) * ((float) Math.PI / 180F);
            model.getCube("Tail3").rotateAngleX -= (entity.getPiecePitch(3, partialTicks) - 0) * ((float) Math.PI / 180F);
            model.getCube("Tail4").rotateAngleX -= (entity.getPiecePitch(4, partialTicks) - 0) * ((float) Math.PI / 180F);
        }
        model.animator.update(null);
    }

    public void progressRotation(AdvancedModelBox model, float progress, float rotX, float rotY, float rotZ) {
        model.rotateAngleX += progress * (rotX - model.defaultRotationX) / 20.0F;
        model.rotateAngleY += progress * (rotY - model.defaultRotationY) / 20.0F;
        model.rotateAngleZ += progress * (rotZ - model.defaultRotationZ) / 20.0F;
    }

    private void animate(TabulaModel<EntitySeaSerpent> model, EntitySeaSerpent entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        if (model.animator.setAnimation(EntitySeaSerpent.ANIMATION_SPEAK)) {
            model.animator.startKeyframe(5);
            this.rotate(model.animator, model.getCube("Jaw"), 25, 0, 0);
            model.animator.endKeyframe();
            model.animator.setStaticKeyframe(5);
            model.animator.resetKeyframe(5);
        }
        if (model.animator.setAnimation(EntitySeaSerpent.ANIMATION_BITE)) {
            model.animator.startKeyframe(5);
            this.moveToPose(model, resolve(EnumSeaSerpentAnimations.BITE1.getModelId()));
            model.animator.endKeyframe();
            model.animator.startKeyframe(5);
            this.moveToPose(model, resolve(EnumSeaSerpentAnimations.BITE2.getModelId()));
            model.animator.endKeyframe();
            model.animator.setStaticKeyframe(2);
            model.animator.resetKeyframe(3);
        }
        if (model.animator.setAnimation(EntitySeaSerpent.ANIMATION_ROAR)) {
            model.animator.startKeyframe(10);
            this.moveToPose(model, resolve(EnumSeaSerpentAnimations.ROAR1.getModelId()));
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, resolve(EnumSeaSerpentAnimations.ROAR2.getModelId()));
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, resolve(EnumSeaSerpentAnimations.ROAR3.getModelId()));
            model.animator.endKeyframe();
            model.animator.resetKeyframe(10);
        }
    }

    private static TabulaModel<EntitySeaSerpent> resolve(Identifier id) {
        return TabulaModelHandlerHelper.getModel(id);
    }
}
