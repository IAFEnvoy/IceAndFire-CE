package com.iafenvoy.iceandfire.render.model.animator;

import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.render.model.util.EnumDragonPoses;
import com.iafenvoy.iceandfire.render.model.util.LegArticulator;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ITabulaModelAnimator;
import com.iafenvoy.uranus.client.model.TabulaModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DragonTabulaModelAnimator extends IceAndFireTabulaModelAnimator<EntityDragonBase> implements ITabulaModelAnimator<EntityDragonBase> {
    //FIXME::Will not reload
    private final Map<EnumDragonPoses, TabulaModel<EntityDragonBase>> CACHES = new LinkedHashMap<>();
    protected TabulaModel<EntityDragonBase>[] walkPoses;
    protected TabulaModel<EntityDragonBase>[] flyPoses;
    protected TabulaModel<EntityDragonBase>[] swimPoses;
    protected AdvancedModelBox[] neckParts;
    protected AdvancedModelBox[] tailParts;
    protected AdvancedModelBox[] tailPartsWBody;
    protected AdvancedModelBox[] toesPartsL;
    protected AdvancedModelBox[] toesPartsR;
    protected AdvancedModelBox[] clawL;
    protected AdvancedModelBox[] clawR;

    public DragonTabulaModelAnimator(TabulaModel<EntityDragonBase> baseModel) {
        super(baseModel);
    }

    public void init(TabulaModel<EntityDragonBase> model) {
        this.neckParts = new AdvancedModelBox[]{model.getCube("Neck1"), model.getCube("Neck2"), model.getCube("Neck3"), model.getCube("Head")};
        this.tailParts = new AdvancedModelBox[]{model.getCube("Tail1"), model.getCube("Tail2"), model.getCube("Tail3"), model.getCube("Tail4")};
        this.tailPartsWBody = new AdvancedModelBox[]{model.getCube("BodyLower"), model.getCube("Tail1"), model.getCube("Tail2"), model.getCube("Tail3"), model.getCube("Tail4")};
        this.toesPartsL = new AdvancedModelBox[]{model.getCube("ToeL1"), model.getCube("ToeL2"), model.getCube("ToeL3")};
        this.toesPartsR = new AdvancedModelBox[]{model.getCube("ToeR1"), model.getCube("ToeR2"), model.getCube("ToeR3")};
        this.clawL = new AdvancedModelBox[]{model.getCube("ClawL")};
        this.clawR = new AdvancedModelBox[]{model.getCube("ClawR")};
    }

    @Override
    public void setRotationAngles(TabulaModel<EntityDragonBase> model, EntityDragonBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        model.resetToDefaultPose();
        if (this.neckParts == null)
            this.init(model);
        this.animate(model, entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale);

        boolean walking = !entity.isHovering() && !entity.isFlying() && entity.hoverProgress <= 0 && entity.flyProgress <= 0;
        boolean swimming = entity.isTouchingWater() && entity.swimProgress > 0;

        int currentIndex = walking ? (entity.walkCycle / 10) : (entity.flightCycle / 10);
        if (swimming) currentIndex = entity.swimCycle / 10;
        int prevIndex = currentIndex - 1;
        if (prevIndex < 0) prevIndex = swimming ? 4 : walking ? 3 : 5;

        TabulaModel<EntityDragonBase> currentPosition = swimming ? this.swimPoses[currentIndex] : walking ? this.walkPoses[currentIndex] : this.flyPoses[currentIndex];
        TabulaModel<EntityDragonBase> prevPosition = swimming ? this.swimPoses[prevIndex] : walking ? this.walkPoses[prevIndex] : this.flyPoses[prevIndex];
        float delta = ((walking ? entity.walkCycle : entity.flightCycle) / 10.0F) % 1.0F;
        if (swimming) delta = (entity.swimCycle / 10.0F) % 1.0F;
        float partialTick = MinecraftClient.getInstance().getTickDelta();
        float deltaTicks = delta + (partialTick / 10.0F);
        if (delta == 0) deltaTicks = 0;

        float speed_walk = 0.2F;
        float speed_idle = entity.isSleeping() ? 0.025F : 0.05F;
        float speed_fly = 0.2F;
        float degree_walk = 0.5F;
        float degree_idle = entity.isSleeping() ? 0.25F : 0.5F;
        float degree_fly = 0.5F;

        if (entity.isModelDead()) {
            for (AdvancedModelBox cube : model.getCubes().values())
                this.setRotationsLoopDeath(model, entity, limbSwingAmount, walking, currentPosition, prevPosition, partialTick, deltaTicks, cube);
            return;
        }

        if (entity.isAiDisabled()) return;

        for (AdvancedModelBox cube : model.getCubes().values())
            this.setRotationsLoop(model, entity, limbSwingAmount, walking, currentPosition, prevPosition, partialTick, deltaTicks, cube);

        if (entity.getAnimation() != EntityDragonBase.ANIMATION_SHAKEPREY || entity.getAnimation() != EntityDragonBase.ANIMATION_ROAR)
            model.faceTarget(rotationYaw, rotationPitch, 2, this.neckParts);
        if (!walking) {
            model.bob(model.getCube("BodyUpper"), -speed_fly, degree_fly * 5, false, ageInTicks, 1);
            model.walk(model.getCube("BodyUpper"), -speed_fly, degree_fly * 0.1F, false, 0, 0, ageInTicks, 1);
            model.chainWave(this.tailPartsWBody, speed_fly, degree_fly * -0.1F, 0, ageInTicks, 1);
            model.chainWave(this.neckParts, speed_fly, degree_fly * 0.2F, -4, ageInTicks, 1);
            model.chainWave(this.toesPartsL, speed_fly, degree_fly * 0.2F, -2, ageInTicks, 1);
            model.chainWave(this.toesPartsR, speed_fly, degree_fly * 0.2F, -2, ageInTicks, 1);
            model.walk(model.getCube("ThighR"), -speed_fly, degree_fly * 0.1F, false, 0, 0, ageInTicks, 1);
            model.walk(model.getCube("ThighL"), -speed_fly, degree_fly * 0.1F, true, 0, 0, ageInTicks, 1);
        } else {
            model.bob(model.getCube("BodyUpper"), speed_walk * 2, degree_walk * 1.7F, false, limbSwing, limbSwingAmount);
            model.bob(model.getCube("ThighR"), speed_walk, degree_walk * 1.7F, false, limbSwing, limbSwingAmount);
            model.bob(model.getCube("ThighL"), speed_walk, degree_walk * 1.7F, false, limbSwing, limbSwingAmount);
            model.chainSwing(this.tailParts, speed_walk, degree_walk * 0.25F, -2, limbSwing, limbSwingAmount);
            model.chainWave(this.tailParts, speed_walk, degree_walk * 0.15F, 2, limbSwing, limbSwingAmount);
            model.chainSwing(this.neckParts, speed_walk, degree_walk * 0.15F, 2, limbSwing, limbSwingAmount);
            model.chainWave(this.neckParts, speed_walk, degree_walk * 0.05F, -2, limbSwing, limbSwingAmount);
            model.chainSwing(this.tailParts, speed_idle, degree_idle * 0.25F, -2, ageInTicks, 1);
            model.chainWave(this.tailParts, speed_idle, degree_idle * 0.15F, -2, ageInTicks, 1);
            model.chainWave(this.neckParts, speed_idle, degree_idle * -0.15F, -3, ageInTicks, 1);
            model.walk(model.getCube("Neck1"), speed_idle, degree_idle * 0.05F, false, 0, 0, ageInTicks, 1);
        }
        model.bob(model.getCube("BodyUpper"), speed_idle, degree_idle * 1.3F, false, ageInTicks, 1);
        model.bob(model.getCube("ThighR"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);
        model.bob(model.getCube("ThighL"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);
        model.bob(model.getCube("armR1"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);
        model.bob(model.getCube("armL1"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);

        if (entity.isActuallyBreathingFire()) {
            float speed_shake = 0.7F;
            float degree_shake = 0.1F;
            model.chainFlap(this.neckParts, speed_shake, degree_shake, 2, ageInTicks, 1);
            model.chainSwing(this.neckParts, speed_shake * 0.65F, degree_shake * 0.1F, 1, ageInTicks, 1);
        }

        if (entity.turn_buffer != null && !entity.hasPassengers() && !entity.hasVehicle() && entity.isBreathingFire())
            entity.turn_buffer.applyChainSwingBuffer(this.neckParts);
        if (entity.tail_buffer != null && !entity.hasVehicle())
            entity.tail_buffer.applyChainSwingBuffer(this.tailPartsWBody);
        if (entity.roll_buffer != null && entity.pitch_buffer_body != null && entity.pitch_buffer != null)
            if (entity.flyProgress > 0 || entity.hoverProgress > 0) {
                entity.roll_buffer.applyChainFlapBuffer(model.getCube("BodyUpper"));
                entity.pitch_buffer_body.applyChainWaveBuffer(model.getCube("BodyUpper"));
                entity.pitch_buffer.applyChainWaveBufferReverse(this.tailPartsWBody);
            }
        if (entity.getWidth() >= 2 && entity.flyProgress == 0 && entity.hoverProgress == 0)
            LegArticulator.articulateQuadruped(entity, entity.legSolver, model.getCube("BodyUpper"), model.getCube("BodyLower"), model.getCube("Neck1"),
                    model.getCube("ThighL"), model.getCube("LegL"), this.toesPartsL,
                    model.getCube("ThighR"), model.getCube("LegR"), this.toesPartsR,
                    model.getCube("armL1"), model.getCube("armL2"), this.clawL,
                    model.getCube("armR1"), model.getCube("armR2"), this.clawR,
                    1.0F, 0.5F, 0.5F, -0.15F, -0.15F, 0F,
                    MinecraftClient.getInstance().getTickDelta()
            );
    }


    private void setRotationsLoop(TabulaModel<EntityDragonBase> model, EntityDragonBase entity, float limbSwingAmount, boolean walking, TabulaModel<EntityDragonBase> currentPosition, TabulaModel<EntityDragonBase> prevPosition, float partialTick, float deltaTicks, AdvancedModelBox cube) {
        this.genderMob(entity, cube);
        if (walking && entity.flyProgress <= 0.0F && entity.hoverProgress <= 0.0F && entity.modelDeadProgress <= 0.0F) {
            AdvancedModelBox walkPart = this.getModel(EnumDragonPoses.GROUND_POSE).getCube(cube.boxName);
            AdvancedModelBox prevPositionCube = prevPosition.getCube(cube.boxName);
            AdvancedModelBox currPositionCube = currentPosition.getCube(cube.boxName);

            if (prevPositionCube == null || currPositionCube == null) return;

            float prevX = prevPositionCube.rotateAngleX;
            float prevY = prevPositionCube.rotateAngleY;
            float prevZ = prevPositionCube.rotateAngleZ;
            float x = currPositionCube.rotateAngleX;
            float y = currPositionCube.rotateAngleY;
            float z = currPositionCube.rotateAngleZ;
            if (this.isHorn(cube) || this.isWing(model, cube) && (entity.getAnimation() == EntityDragonBase.ANIMATION_WINGBLAST || entity.getAnimation() == EntityDragonBase.ANIMATION_EPIC_ROAR))
                this.addToRotateAngle(cube, limbSwingAmount, walkPart.rotateAngleX, walkPart.rotateAngleY, walkPart.rotateAngleZ);
            else
                this.addToRotateAngle(cube, limbSwingAmount, prevX + deltaTicks * this.distance(prevX, x), prevY + deltaTicks * this.distance(prevY, y), prevZ + deltaTicks * this.distance(prevZ, z));
        }
        if (entity.sleepProgress > 0.0F)
            if (!this.isRotationEqual(cube, this.getModel(EnumDragonPoses.SLEEPING_POSE).getCube(cube.boxName)))
                this.transitionTo(cube, this.getModel(EnumDragonPoses.SLEEPING_POSE).getCube(cube.boxName), MathHelper.lerp(partialTick, entity.prevAnimationProgresses[1], entity.sleepProgress), 20, false);
        if (entity.hoverProgress > 0.0F)
            if (!this.isRotationEqual(cube, this.getModel(EnumDragonPoses.HOVERING_POSE).getCube(cube.boxName)) && !this.isWing(model, cube) && !cube.boxName.contains("Tail"))
                this.transitionTo(cube, this.getModel(EnumDragonPoses.HOVERING_POSE).getCube(cube.boxName), MathHelper.lerp(partialTick, entity.prevAnimationProgresses[2], entity.hoverProgress), 20, false);
        if (entity.flyProgress > 0.0F)
            if (!this.isRotationEqual(cube, this.getModel(EnumDragonPoses.FLYING_POSE).getCube(cube.boxName)))
                this.transitionTo(cube, this.getModel(EnumDragonPoses.FLYING_POSE).getCube(cube.boxName), MathHelper.lerp(partialTick, entity.prevAnimationProgresses[3], entity.flyProgress) - (MathHelper.lerp(partialTick, entity.prevDiveProgress, entity.diveProgress)) * 2, 20, false);
        if (entity.sitProgress > 0.0F)
            if (!entity.hasVehicle())
                if (!this.isRotationEqual(cube, this.getModel(EnumDragonPoses.SITTING_POSE).getCube(cube.boxName)))
                    this.transitionTo(cube, this.getModel(EnumDragonPoses.SITTING_POSE).getCube(cube.boxName), MathHelper.lerp(partialTick, entity.prevAnimationProgresses[0], entity.sitProgress), 20, false);
        if (entity.ridingProgress > 0.0F)
            if (!this.isHorn(cube) && !this.isRotationEqual(cube, this.getModel(EnumDragonPoses.SIT_ON_PLAYER_POSE).getCube(cube.boxName))) {
                this.transitionTo(cube, this.getModel(EnumDragonPoses.SIT_ON_PLAYER_POSE).getCube(cube.boxName), MathHelper.lerp(partialTick, entity.prevAnimationProgresses[5], entity.ridingProgress), 20, false);
                if (cube.boxName.equals("BodyUpper"))
                    cube.offsetZ += ((-12F - cube.offsetZ) / 20) * MathHelper.lerp(partialTick, entity.prevAnimationProgresses[5], entity.ridingProgress);
            }
        if (entity.tackleProgress > 0.0F)
            if (!this.isRotationEqual(this.getModel(EnumDragonPoses.TACKLE).getCube(cube.boxName), this.getModel(EnumDragonPoses.FLYING_POSE).getCube(cube.boxName)) && !this.isWing(model, cube))
                this.transitionTo(cube, this.getModel(EnumDragonPoses.TACKLE).getCube(cube.boxName), MathHelper.lerp(partialTick, entity.prevAnimationProgresses[6], entity.tackleProgress), 5, false);
        if (entity.diveProgress > 0.0F)
            if (!this.isRotationEqual(cube, this.getModel(EnumDragonPoses.DIVING_POSE).getCube(cube.boxName)))
                this.transitionTo(cube, this.getModel(EnumDragonPoses.DIVING_POSE).getCube(cube.boxName), MathHelper.lerp(partialTick, entity.prevDiveProgress, entity.diveProgress), 10, false);
        if (entity.fireBreathProgress > 0.0F)
            if (!this.isRotationEqual(cube, this.getModel(EnumDragonPoses.STREAM_BREATH).getCube(cube.boxName)) && !this.isWing(model, cube) && !cube.boxName.contains("Finger")) {
                if (entity.prevFireBreathProgress <= entity.fireBreathProgress)
                    this.transitionTo(cube, this.getModel(EnumDragonPoses.BLAST_CHARGE3).getCube(cube.boxName), MathHelper.clamp(MathHelper.lerp(partialTick, entity.prevFireBreathProgress, entity.fireBreathProgress), 0, 5), 5, false);
                this.transitionTo(cube, this.getModel(EnumDragonPoses.STREAM_BREATH).getCube(cube.boxName), MathHelper.clamp(MathHelper.lerp(partialTick, entity.prevFireBreathProgress, entity.fireBreathProgress) - 5, 0, 5), 5, false);
            }
        if (!walking) {
            AdvancedModelBox flightPart = this.getModel(EnumDragonPoses.FLYING_POSE).getCube(cube.boxName);
            AdvancedModelBox prevPositionCube = prevPosition.getCube(cube.boxName);
            AdvancedModelBox currPositionCube = currentPosition.getCube(cube.boxName);
            float prevX = prevPositionCube.rotateAngleX;
            float prevY = prevPositionCube.rotateAngleY;
            float prevZ = prevPositionCube.rotateAngleZ;
            float x = currPositionCube.rotateAngleX;
            float y = currPositionCube.rotateAngleY;
            float z = currPositionCube.rotateAngleZ;
            if (x != flightPart.rotateAngleX || y != flightPart.rotateAngleY || z != flightPart.rotateAngleZ)
                this.setRotateAngle(cube, 1F, prevX + deltaTicks * this.distance(prevX, x), prevY + deltaTicks * this.distance(prevY, y), prevZ + deltaTicks * this.distance(prevZ, z));
        }
    }

    public void setRotationsLoopDeath(TabulaModel<EntityDragonBase> model, EntityDragonBase entity, float limbSwingAmount, boolean walking, TabulaModel<EntityDragonBase> currentPosition, TabulaModel<EntityDragonBase> prevPosition, float partialTick, float deltaTicks, AdvancedModelBox cube) {
        if (entity.modelDeadProgress > 0.0F) {
            // TODO: Figure out what's up with custom poses
            // DON'T use this in it's current state since it heavily effects render performance due to the fact that
            // custom poses aren't being used right now
            // TabulaModel customPose = customPose(entity);
            TabulaModel<EntityDragonBase> pose = this.getModel(EnumDragonPoses.DEAD);
            if (!this.isRotationEqual(cube, pose.getCube(cube.boxName)))
                this.transitionTo(cube, pose.getCube(cube.boxName), entity.prevModelDeadProgress + (entity.modelDeadProgress - entity.prevModelDeadProgress) * MinecraftClient.getInstance().getTickDelta(), 20, cube.boxName.equals("ThighR") || cube.boxName.equals("ThighL"));
            //Ugly hack to make sure ice dragon models are touching the ground when dead
            if (this instanceof IceDragonTabulaModelAnimator)
                if (cube.boxName.equals("BodyUpper"))
                    cube.rotationPointY += 0.35F * MathHelper.lerp(partialTick, entity.prevModelDeadProgress, entity.modelDeadProgress);
        }
    }

    protected boolean isWing(TabulaModel<EntityDragonBase> model, AdvancedModelBox modelRenderer) {
        return model.getCube("armL1") == modelRenderer || model.getCube("armR1") == modelRenderer || model.getCube("armL1").childModels.contains(modelRenderer) || model.getCube("armR1").childModels.contains(modelRenderer);
    }

    protected boolean isHorn(AdvancedModelBox modelRenderer) {
        return modelRenderer.boxName.contains("Horn");
    }

    protected void genderMob(EntityDragonBase entity, AdvancedModelBox cube) {
        if (!entity.isMale()) {
            TabulaModel<EntityDragonBase> maleModel = this.getModel(EnumDragonPoses.MALE);
            TabulaModel<EntityDragonBase> femaleModel = this.getModel(EnumDragonPoses.FEMALE);
            AdvancedModelBox femaleModelCube = femaleModel.getCube(cube.boxName);
            AdvancedModelBox maleModelCube = maleModel.getCube(cube.boxName);
            if (maleModelCube == null || femaleModelCube == null) return;
            float x = femaleModelCube.rotateAngleX;
            float y = femaleModelCube.rotateAngleY;
            float z = femaleModelCube.rotateAngleZ;
            if (x != maleModelCube.rotateAngleX || y != maleModelCube.rotateAngleY || z != maleModelCube.rotateAngleZ)
                this.setRotateAngle(cube, 1F, x, y, z);
        }
    }

    protected TabulaModel<EntityDragonBase> getModel(EnumDragonPoses pose) {
        if (this.CACHES.containsKey(pose)) return this.CACHES.get(pose);
        TabulaModel<EntityDragonBase> model = this.getModelInternal(pose);
        this.CACHES.put(pose, model);
        return model;
    }

    protected abstract TabulaModel<EntityDragonBase> getModelInternal(EnumDragonPoses pose);

    protected void animate(TabulaModel<EntityDragonBase> model, EntityDragonBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        AdvancedModelBox modelCubeJaw = model.getCube("Jaw");
        AdvancedModelBox modelCubeBodyUpper = model.getCube("BodyUpper");
        model.animator.update(entity);
        //Firecharge
        if (model.animator.setAnimation(EntityDragonBase.ANIMATION_FIRECHARGE)) {
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.BLAST_CHARGE1));
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.BLAST_CHARGE2));
            model.animator.endKeyframe();
            model.animator.startKeyframe(5);
            this.moveToPose(model, this.getModel(EnumDragonPoses.BLAST_CHARGE3));
            model.animator.endKeyframe();
            model.animator.resetKeyframe(5);
        }
        //Speak
        if (model.animator.setAnimation(EntityDragonBase.ANIMATION_SPEAK)) {
            model.animator.startKeyframe(5);
            this.rotate(model.animator, modelCubeJaw, 18, 0, 0);
            model.animator.move(modelCubeJaw, 0, 0, 0.2F);
            model.animator.endKeyframe();
            model.animator.setStaticKeyframe(5);
            model.animator.startKeyframe(5);
            this.rotate(model.animator, modelCubeJaw, 18, 0, 0);
            model.animator.move(modelCubeJaw, 0, 0, 0.2F);
            model.animator.endKeyframe();
            model.animator.resetKeyframe(5);
        }
        //Bite
        if (model.animator.setAnimation(EntityDragonBase.ANIMATION_BITE)) {
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.BITE1));
            model.animator.endKeyframe();
            model.animator.startKeyframe(5);
            this.moveToPose(model, this.getModel(EnumDragonPoses.BITE2));
            model.animator.endKeyframe();
            model.animator.startKeyframe(5);
            this.moveToPose(model, this.getModel(EnumDragonPoses.BITE3));
            model.animator.endKeyframe();
            model.animator.resetKeyframe(10);
        }
        //Shakeprey
        if (model.animator.setAnimation(EntityDragonBase.ANIMATION_SHAKEPREY)) {
            model.animator.startKeyframe(15);
            this.moveToPose(model, this.getModel(EnumDragonPoses.GRAB1));
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.GRAB2));
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.GRAB_SHAKE1));
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.GRAB_SHAKE2));
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.GRAB_SHAKE3));
            model.animator.endKeyframe();
            model.animator.resetKeyframe(10);
        }
        //Tailwhack
        if (model.animator.setAnimation(EntityDragonBase.ANIMATION_TAILWHACK)) {
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.TAIL_WHIP1));
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.TAIL_WHIP2));
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.TAIL_WHIP3));
            model.animator.endKeyframe();
            model.animator.resetKeyframe(10);
        }
        //Wingblast
        if (model.animator.setAnimation(EntityDragonBase.ANIMATION_WINGBLAST)) {
            model.animator.startKeyframe(5);
            this.moveToPose(model, this.getModel(EnumDragonPoses.WING_BLAST1));
            model.animator.move(modelCubeBodyUpper, 0, 0, 0);
            model.animator.endKeyframe();
            model.animator.startKeyframe(5);
            this.moveToPose(model, this.getModel(EnumDragonPoses.WING_BLAST2));
            model.animator.move(modelCubeBodyUpper, 0, -2F, 0);
            model.animator.endKeyframe();
            model.animator.startKeyframe(5);
            this.moveToPose(model, this.getModel(EnumDragonPoses.WING_BLAST3));
            model.animator.move(modelCubeBodyUpper, 0, -4F, 0);
            model.animator.endKeyframe();
            model.animator.startKeyframe(5);
            this.moveToPose(model, this.getModel(EnumDragonPoses.WING_BLAST4));
            model.animator.move(modelCubeBodyUpper, 0, -4F, 0);
            model.animator.endKeyframe();
            model.animator.startKeyframe(5);
            this.moveToPose(model, this.getModel(EnumDragonPoses.WING_BLAST5));
            model.animator.move(modelCubeBodyUpper, 0, -4F, 0);
            model.animator.endKeyframe();
            model.animator.startKeyframe(5);
            this.moveToPose(model, this.getModel(EnumDragonPoses.WING_BLAST6));
            model.animator.move(modelCubeBodyUpper, 0, -4F, 0);
            model.animator.endKeyframe();
            model.animator.startKeyframe(5);
            this.moveToPose(model, this.getModel(EnumDragonPoses.WING_BLAST7));
            model.animator.move(modelCubeBodyUpper, 0, -2F, 0);
            model.animator.endKeyframe();
            model.animator.resetKeyframe(10);
        }
        //Roar
        if (model.animator.setAnimation(EntityDragonBase.ANIMATION_ROAR)) {
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.ROAR1));
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.ROAR2));
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.ROAR3));
            model.animator.endKeyframe();
            model.animator.resetKeyframe(10);
        }
        //Epicroar
        if (model.animator.setAnimation(EntityDragonBase.ANIMATION_EPIC_ROAR)) {
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.EPIC_ROAR1));
            model.animator.rotate(modelCubeBodyUpper, -0.1F, 0, 0);
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.EPIC_ROAR2));
            model.animator.rotate(modelCubeBodyUpper, -0.2F, 0, 0);
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.EPIC_ROAR3));
            model.animator.rotate(modelCubeBodyUpper, -0.2F, 0, 0);
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.EPIC_ROAR2));
            model.animator.rotate(modelCubeBodyUpper, -0.2F, 0, 0);
            model.animator.endKeyframe();
            model.animator.startKeyframe(10);
            this.moveToPose(model, this.getModel(EnumDragonPoses.EPIC_ROAR3));
            model.animator.rotate(modelCubeBodyUpper, -0.1F, 0, 0);
            model.animator.endKeyframe();
            model.animator.resetKeyframe(10);
        }
        // EATING
        if (model.animator.setAnimation(EntityDragonBase.ANIMATION_EAT)) {
            model.animator.startKeyframe(5);
            this.rotate(model.animator, model.getCube("Neck1"), 18, 0, 0);
            this.rotate(model.animator, model.getCube("Neck2"), 18, 0, 0);
            model.animator.endKeyframe();
            //CODE from speak
            model.animator.startKeyframe(5);
            this.rotate(model.animator, modelCubeJaw, 18, 0, 0);
            model.animator.move(modelCubeJaw, 0, 0, 0.2F);
            model.animator.endKeyframe();
            model.animator.setStaticKeyframe(5);
            model.animator.startKeyframe(5);
            this.rotate(model.animator, modelCubeJaw, 18, 0, 0);
            model.animator.move(modelCubeJaw, 0, 0, 0.2F);
            model.animator.endKeyframe();
            model.animator.startKeyframe(5);
            this.rotate(model.animator, model.getCube("Neck1"), -18, 0, 0);
            this.rotate(model.animator, model.getCube("Neck2"), -18, 0, 0);
            model.animator.endKeyframe();
        }
        model.animator.update(null);
    }
}
