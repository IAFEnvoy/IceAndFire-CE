package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.entity.EntityGorgon;
import com.iafenvoy.iceandfire.entity.EntityHydra;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelHydraHead extends ModelDragonBase<EntityHydra> {
    public final AdvancedModelBox Neck1;
    public final AdvancedModelBox Neck2;
    public final AdvancedModelBox Neck3;
    public final AdvancedModelBox Neck4;
    public final AdvancedModelBox Head1;
    public final AdvancedModelBox HeadPivot;
    public final AdvancedModelBox neckSpike1;
    public final AdvancedModelBox neckSpike2;
    public final AdvancedModelBox UpperJaw1;
    public final AdvancedModelBox LowerJaw1;
    public final AdvancedModelBox TeethTR1;
    public final AdvancedModelBox TeethL1;
    public final AdvancedModelBox TeethR1;
    public final AdvancedModelBox TeethTL1;
    private final ModelAnimator animator;
    private final int headIndex;

    public ModelHydraHead(int headIndex) {
        this.headIndex = headIndex;
        this.texWidth = 256;
        this.texHeight = 128;
        this.Neck3 = new AdvancedModelBox(this, 25, 90);
        this.Neck3.setPos(0.0F, -0.5F, -6.7F);
        this.Neck3.addBox(-1.92F, -2.0F, -8.0F, 4, 4, 9, 0.0F);
        this.setRotateAngle(this.Neck3, -0.12217304763960307F, -0.0F, 0.0F);
        this.TeethR1 = new AdvancedModelBox(this, 6, 44);
        this.TeethR1.mirror = true;
        this.TeethR1.setPos(0.0F, 0.0F, 0.0F);
        this.TeethR1.addBox(-0.2F, 0.8F, -4.1F, 2, 2, 6, 0.0F);
        this.neckSpike2 = new AdvancedModelBox(this, 0, 0);
        this.neckSpike2.setPos(0.0F, -0.9F, -3.7F);
        this.neckSpike2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(this.neckSpike2, 0.6829473363053812F, -0.0F, 0.0F);
        this.Head1 = new AdvancedModelBox(this, 6, 77);
        this.Head1.addBox(-3.0F, -1.3F, -2.5F, 6, 3, 4, 0.0F);
        this.setRotateAngle(this.Head1, 0.7853981633974483F, -0.0F, 0.0F);

        this.HeadPivot = new AdvancedModelBox(this, 6, 77);
        this.HeadPivot.setPos(0.0F, -0.2F, -8.3F);

        this.LowerJaw1 = new AdvancedModelBox(this, 6, 63);
        this.LowerJaw1.setPos(0.0F, 1.9F, -0.8F);
        this.LowerJaw1.addBox(-2.0F, -0.6F, -7.0F, 4, 1, 7, 0.0F);
        this.Neck2 = new AdvancedModelBox(this, 85, 72);
        this.Neck2.setPos(0.0F, 0.2F, -6.8F);
        this.Neck2.addBox(-2.0F, -2.8F, -7.0F, 4, 5, 8, 0.0F);
        this.setRotateAngle(this.Neck2, -0.5235987755982988F, 0.0F, 0.0F);
        this.Neck1 = new AdvancedModelBox(this, 56, 80);
        this.Neck1.setPos(-0.0F, 1.5F, -0.2F);
        this.Neck1.addBox(-2.5F, -3.0F, -7.0F, 5, 6, 9, 0.0F);
        this.setRotateAngle(this.Neck1, -0.3490658503988659F, 0.0F, 0.0F);
        this.TeethL1 = new AdvancedModelBox(this, 6, 44);
        this.TeethL1.setPos(0.0F, 0.9F, -2.8F);
        this.TeethL1.addBox(-1.9F, 0.8F, -4.1F, 2, 2, 6, 0.0F);
        this.setRotateAngle(this.TeethL1, 0.045553093477052F, -0.0F, -3.141592653589793F);
        this.Neck4 = new AdvancedModelBox(this, 35, 70);
        this.Neck4.mirror = true;
        this.Neck4.setPos(0.0F, 0.0F, -7.4F);
        this.Neck4.addBox(-2.0F, -2.0F, -8.2F, 4, 4, 9, 0.0F);
        this.setRotateAngle(this.Neck4, 0.47123889803846897F, 0.0F, 0.0F);
        this.neckSpike1 = new AdvancedModelBox(this, 40, 0);
        this.neckSpike1.setPos(0.0F, -1.2F, -6.0F);
        this.neckSpike1.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(this.neckSpike1, 0.7740535232594852F, 0.0F, 0.0F);
        this.TeethTL1 = new AdvancedModelBox(this, 6, 44);
        this.TeethTL1.mirror = true;
        this.TeethTL1.setPos(0.0F, 0.0F, 0.0F);
        this.TeethTL1.addBox(-0.2F, 0.8F, -4.1F, 2, 2, 6, 0.0F);
        this.UpperJaw1 = new AdvancedModelBox(this, 6, 54);
        this.UpperJaw1.setPos(0.0F, 0.0F, -2.4F);
        this.UpperJaw1.addBox(-2.5F, -1.7F, -5.8F, 5, 3, 6, 0.0F);
        this.setRotateAngle(this.UpperJaw1, 0.091106186954104F, -0.0F, 0.0F);

        this.TeethTR1 = new AdvancedModelBox(this, 6, 44);
        this.TeethTR1.setPos(0.0F, -0.4F, -3.6F);
        this.setRotateAngle(this.TeethTR1, -0.091106186954104F, -0.0F, 0.0F);
        this.TeethTR1.addBox(-1.9F, 0.8F, -4.1F, 2, 2, 6, 0.0F);

        this.Neck2.addChild(this.Neck3);
        this.TeethL1.addChild(this.TeethR1);
        this.Neck4.addChild(this.neckSpike2);
        this.Neck4.addChild(this.HeadPivot);
        this.HeadPivot.addChild(this.Head1);
        this.Head1.addChild(this.LowerJaw1);
        this.Neck1.addChild(this.Neck2);
        this.LowerJaw1.addChild(this.TeethL1);
        this.Neck3.addChild(this.Neck4);
        this.Head1.addChild(this.TeethTR1);
        this.Neck4.addChild(this.neckSpike1);
        this.TeethTR1.addChild(this.TeethTL1);
        this.Head1.addChild(this.UpperJaw1);
        this.animator = ModelAnimator.create();
        this.updateDefaultPose();
    }


    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.resetToDefaultPose();
        this.animator.update(entity);
    }

    @Override
    public void setAngles(EntityHydra entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.animate(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 1);
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 0.2F;
        float degree_idle = 0.5F;
        if (EntityGorgon.isStoneMob(entity)) {
            return;
        }
        float partialTicks = MinecraftClient.getInstance().getTickDelta();
        AdvancedModelBox[] ENTIRE_HEAD = new AdvancedModelBox[]{this.Neck1, this.Neck2, this.Neck3, this.Neck4};
        this.chainFlap(ENTIRE_HEAD, speed_idle, degree_idle * 0.15F, -3 + this.headIndex % 4, animationProgress, 1);
        this.chainSwing(ENTIRE_HEAD, speed_idle, degree_idle * 0.05F, -3 + this.headIndex % 3, animationProgress, 1);
        this.chainWave(ENTIRE_HEAD, speed_idle * 1.5F, degree_idle * 0.05F, -2 + this.headIndex % 3, animationProgress, 1);
        this.faceTarget(headYaw, headPitch, 1, this.Head1);
        this.walk(this.neckSpike1, speed_idle * 1.5F, degree_idle * 0.4F, false, 2, -0.1F, animationProgress, 1);
        this.walk(this.neckSpike2, speed_idle * 1.5F, degree_idle * 0.4F, false, 3, -0.1F, animationProgress, 1);
        this.chainSwing(ENTIRE_HEAD, speed_walk, degree_walk * 0.75F, -3, limbAngle, limbDistance);
        float speakProgress = entity.prevSpeakingProgress[this.headIndex] + partialTicks * (entity.speakingProgress[this.headIndex] - entity.prevSpeakingProgress[this.headIndex]);
        this.progressRotationInterp(this.LowerJaw1, MathHelper.sin((float) (speakProgress * Math.PI)) * 10F, (float) Math.toRadians(25), 0.0F, 0.0F, 10F);
        float strikeProgress = entity.prevStrikeProgress[this.headIndex] + partialTicks * (entity.strikingProgress[this.headIndex] - entity.prevStrikeProgress[this.headIndex]);
        this.progressRotationInterp(this.Neck2, strikeProgress, (float) Math.toRadians(5), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(this.Neck3, strikeProgress, (float) Math.toRadians(5), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(this.Neck4, strikeProgress, (float) Math.toRadians(5), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(this.Head1, strikeProgress, (float) Math.toRadians(-15), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(this.LowerJaw1, strikeProgress, (float) Math.toRadians(45), 0.0F, 0.0F, 10F);
        this.progressPositionInterp(this.TeethTR1, strikeProgress, 0.5F, 0.0F, 0.0F, 10F);
        float breathProgress = entity.prevBreathProgress[this.headIndex] + partialTicks * (entity.breathProgress[this.headIndex] - entity.prevBreathProgress[this.headIndex]);
        this.progressRotationInterp(this.Neck4, breathProgress, (float) Math.toRadians(15), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(this.Neck3, breathProgress, (float) Math.toRadians(15), 0.0F, 0.0F, 10F);
        this.progressPositionInterp(this.TeethTR1, breathProgress, 0.5F, 0.0F, 0.0F, 10F);
        this.progressRotationInterp(this.Head1, breathProgress, (float) Math.toRadians(15), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(this.UpperJaw1, breathProgress, (float) Math.toRadians(-10), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(this.LowerJaw1, breathProgress, (float) Math.toRadians(50), 0.0F, 0.0F, 10F);


        this.Neck2.showModel = entity.getSeveredHead() != this.headIndex && entity.isAlive();
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.Neck1);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.Neck1, this.Neck2, this.Neck3, this.Neck4, this.Head1, this.HeadPivot, this.neckSpike1, this.neckSpike2, this.UpperJaw1,
                this.LowerJaw1, this.TeethTR1, this.TeethL1, this.TeethR1, this.TeethTL1);
    }
}
