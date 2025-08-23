package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.entity.EntityHydra;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.ModelAnimator;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ModelHydraBody extends ModelDragonBase<EntityHydra> {
    public final AdvancedModelBox BodyUpper;
    public final AdvancedModelBox BodyLower;
    public final AdvancedModelBox BodySpike1;
    public final AdvancedModelBox BodySpike2;
    public final AdvancedModelBox Tail1;
    public final AdvancedModelBox BodySpike3;
    public final AdvancedModelBox Tail2;
    public final AdvancedModelBox Tail3;
    public final AdvancedModelBox Tail4;
    public final AdvancedModelBox Tail5;
    public final AdvancedModelBox TailSpike1;
    public final AdvancedModelBox TailSpike2;
    public final AdvancedModelBox TailSpike3;
    private final ModelAnimator animator;

    public ModelHydraBody() {
        this.texWidth = 256;
        this.texHeight = 128;
        this.BodySpike1 = new AdvancedModelBox(this, 0, 0);
        this.BodySpike1.setPos(0.0F, -1.2F, 3.0F);
        this.BodySpike1.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(this.BodySpike1, 0.6433283622851098F, -0.0F, 0.0F);
        this.Tail3 = new AdvancedModelBox(this, 70, 15);
        this.Tail3.setPos(0.0F, -0.1F, 7.7F);
        this.Tail3.addBox(-1.5F, -1.3F, 0.7F, 3, 4, 9, 0.0F);
        this.setRotateAngle(this.Tail3, 0.091106186954104F, 0.0F, 0.0F);
        this.Tail1 = new AdvancedModelBox(this, 69, 34);
        this.Tail1.setPos(0.0F, -1.2F, 7.3F);
        this.Tail1.addBox(-2.5F, -2.0F, 1.0F, 5, 5, 8, 0.0F);
        this.setRotateAngle(this.Tail1, 0.045553093477052F, 0.0F, 0.0F);
        this.TailSpike2 = new AdvancedModelBox(this, 0, 0);
        this.TailSpike2.setPos(0.0F, 0.0F, 3.0F);
        this.TailSpike2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(this.TailSpike2, 0.6433283622851098F, -0.0F, 0.0F);
        this.Tail4 = new AdvancedModelBox(this, 97, 16);
        this.Tail4.setPos(0.0F, 0.3F, 8.0F);
        this.Tail4.addBox(-1.52F, -1.3F, 0.8F, 3, 3, 9, 0.0F);
        this.Tail5 = new AdvancedModelBox(this, 42, 17);
        this.Tail5.setPos(0.0F, -0.4F, 7.5F);
        this.Tail5.addBox(-1.0F, -0.4F, 1.0F, 2, 2, 8, 0.0F);
        this.setRotateAngle(this.Tail5, 0.091106186954104F, 0.0F, 0.0F);
        this.TailSpike3 = new AdvancedModelBox(this, 40, 0);
        this.TailSpike3.setPos(0.0F, 0.0F, 7.0F);
        this.TailSpike3.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(this.TailSpike3, 0.6433283622851098F, -0.0F, 0.0F);
        this.BodySpike3 = new AdvancedModelBox(this, 0, 0);
        this.BodySpike3.setPos(0.0F, -3.1F, 2.5F);
        this.BodySpike3.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(this.BodySpike3, 0.6433283622851098F, -0.0F, 0.0F);
        this.BodySpike2 = new AdvancedModelBox(this, 40, 0);
        this.BodySpike2.setPos(0.0F, -1.2F, 7.0F);
        this.BodySpike2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(this.BodySpike2, 0.6433283622851098F, -0.0F, 0.0F);
        this.BodyLower = new AdvancedModelBox(this, 103, 47);
        this.BodyLower.setPos(0.0F, 2.2F, 8.1F);
        this.BodyLower.addBox(-3.5F, -3.5F, 0.0F, 7, 6, 9, 0.0F);
        this.setRotateAngle(this.BodyLower, -0.091106186954104F, -0.0F, 0.0F);
        this.TailSpike1 = new AdvancedModelBox(this, 40, 0);
        this.TailSpike1.setPos(0.0F, -0.6F, 4.2F);
        this.TailSpike1.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(this.TailSpike1, 0.6433283622851098F, -0.0F, 0.0F);
        this.Tail2 = new AdvancedModelBox(this, 95, 34);
        this.Tail2.setPos(0.0F, 0.5F, 7.4F);
        this.Tail2.addBox(-2.01F, -1.6F, 0.9F, 4, 4, 8, 0.0F);
        this.BodyUpper = new AdvancedModelBox(this, 67, 47);
        this.BodyUpper.setPos(0.0F, 19.1F, -9.7F);
        this.BodyUpper.addBox(-4.5F, -1.8F, 0.0F, 9, 7, 9, 0.0F);
        this.BodyUpper.addChild(this.BodySpike1);
        this.Tail2.addChild(this.Tail3);
        this.BodyLower.addChild(this.Tail1);
        this.Tail5.addChild(this.TailSpike2);
        this.Tail3.addChild(this.Tail4);
        this.Tail4.addChild(this.Tail5);
        this.Tail5.addChild(this.TailSpike3);
        this.BodyLower.addChild(this.BodySpike3);
        this.BodyUpper.addChild(this.BodySpike2);
        this.BodyUpper.addChild(this.BodyLower);
        this.Tail4.addChild(this.TailSpike1);
        this.Tail1.addChild(this.Tail2);
        this.animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.resetToDefaultPose();
        this.animator.update(entity);
        this.animator.update(null);
    }

    @Override
    public void setAngles(EntityHydra entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.animate(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 1);
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 1F;
        float degree_idle = 0.25F;
        AdvancedModelBox[] TAIL = new AdvancedModelBox[]{this.BodyLower, this.Tail1, this.Tail2, this.Tail3, this.Tail4, this.Tail5};
        this.chainSwing(TAIL, speed_walk, degree_walk * 0.75F, -3, limbAngle, limbDistance);
        this.swing(this.BodyUpper, speed_walk * 1.5F, degree_walk * 0.12F, true, 3, 0F, limbAngle, limbDistance);
        this.swing(this.Tail5, speed_idle * 1.5F, degree_idle * 0.2F, false, 3, 0F, animationProgress, 1);
        this.swing(this.Tail4, speed_idle * 1.5F, degree_idle * 0.2F, false, 2, 0F, animationProgress, 1);
        this.walk(this.BodySpike1, speed_idle * 1.5F, degree_idle * 0.4F, false, 2, -0.2F, animationProgress, 1);
        this.walk(this.BodySpike2, speed_idle * 1.5F, degree_idle * 0.4F, false, 3, -0.2F, animationProgress, 1);
        this.walk(this.BodySpike3, speed_idle * 1.5F, degree_idle * 0.4F, false, 4, -0.2F, animationProgress, 1);

        this.walk(this.TailSpike1, speed_idle * 1.5F, degree_idle * 0.4F, false, 2, -0.2F, animationProgress, 1);
        this.walk(this.TailSpike2, speed_idle * 1.5F, degree_idle * 0.4F, false, 3, -0.2F, animationProgress, 1);
        this.walk(this.TailSpike3, speed_idle * 1.5F, degree_idle * 0.4F, false, 4, -0.2F, animationProgress, 1);
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.resetToDefaultPose();
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

        this.resetToDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.BodyUpper);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.BodyUpper, this.BodyLower, this.BodySpike1, this.BodySpike2, this.Tail1, this.BodySpike3, this.Tail2, this.Tail3,
                this.Tail4, this.Tail5, this.TailSpike1, this.TailSpike2, this.TailSpike3);
    }
}
