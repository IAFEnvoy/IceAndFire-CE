package com.iafenvoy.iceandfire.render.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.entity.EntityPixie;
import com.iafenvoy.iceandfire.entity.block.BlockEntityJar;
import com.iafenvoy.iceandfire.entity.block.BlockEntityPixieHouse;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

public class ModelPixie extends ModelDragonBase<EntityPixie> {
    public final AdvancedModelBox Body;
    public final AdvancedModelBox Left_Arm;
    public final AdvancedModelBox Head;
    public final AdvancedModelBox Right_Arm;
    public final AdvancedModelBox Neck;
    public final AdvancedModelBox Left_Leg;
    public final AdvancedModelBox Right_Leg;
    public final AdvancedModelBox Left_Wing;
    public final AdvancedModelBox Left_Wing2;
    public final AdvancedModelBox Right_Wing;
    public final AdvancedModelBox Right_Wing2;
    public final AdvancedModelBox Dress;

    public ModelPixie() {
        this.texWidth = 32;
        this.texHeight = 32;
        this.Neck = new AdvancedModelBox(this, 40, 25);
        this.Neck.setPos(0.0F, -8.2F, 0.0F);
        this.Neck.addBox(-1.5F, -1.1F, -1.0F, 3, 1, 1, 0.0F);
        this.Right_Arm = new AdvancedModelBox(this, 0, 17);
        this.Right_Arm.setPos(-1.8F, -7.0F, 0.0F);
        this.Right_Arm.addBox(-0.6F, -0.5F, -1.0F, 1, 6, 1, 0.0F);
        this.setRotateAngle(this.Right_Arm, 0.0F, 0.0F, 0.17453292519943295F);
        this.Right_Wing2 = new AdvancedModelBox(this, 24, 10);
        this.Right_Wing2.setPos(-1.4F, -5.0F, -0.1F);
        this.Right_Wing2.addBox(-1.2F, -0.5F, 0.5F, 3, 10, 1, 0.0F);
        this.setRotateAngle(this.Right_Wing2, 0.5235987755982988F, -0.01832595714594046F, 1.0471975511965976F);
        this.Right_Wing = new AdvancedModelBox(this, 14, 10);
        this.Right_Wing.setPos(-1.2F, -6.3F, 0.4F);
        this.Right_Wing.addBox(-1.2F, -0.5F, 0.5F, 3, 12, 1, 0.0F);
        this.setRotateAngle(this.Right_Wing, 0.5235987755982988F, -0.2617993877991494F, 1.7453292519943295F);
        this.Body = new AdvancedModelBox(this, 0, 8);
        this.Body.setPos(0.0F, 16.9F, 0.5F);
        this.Body.addBox(-1.5F, -7.9F, -1.4F, 3, 5, 2, 0.0F);
        this.Right_Leg = new AdvancedModelBox(this, 5, 17);
        this.Right_Leg.mirror = true;
        this.Right_Leg.setPos(-0.8F, -1.5F, 0.0F);
        this.Right_Leg.addBox(-0.6F, -0.5F, -0.9F, 1, 6, 1, 0.0F);
        this.Dress = new AdvancedModelBox(this, 0, 24);
        this.Dress.setPos(0.0F, -2.5F, 0.1F);
        this.Dress.addBox(-2.0F, -0.4F, -1.5F, 4, 3, 2, 0.0F);
        this.Head = new AdvancedModelBox(this, 0, 0);
        this.Head.setPos(0.0F, -8.0F, -0.8F);
        this.Head.addBox(-2.0F, -3.8F, -1.6F, 4, 4, 4, 0.0F);
        this.Left_Wing = new AdvancedModelBox(this, 14, 10);
        this.Left_Wing.mirror = true;
        this.Left_Wing.setPos(1.2F, -6.3F, 0.4F);
        this.Left_Wing.addBox(-1.8F, -0.5F, 0.5F, 3, 12, 1, 0.0F);
        this.setRotateAngle(this.Left_Wing, 0.5235987755982988F, 0.2617993877991494F, -1.7453292519943295F);
        this.Left_Leg = new AdvancedModelBox(this, 5, 17);
        this.Left_Leg.setPos(0.8F, -1.5F, 0.0F);
        this.Left_Leg.addBox(-0.6F, -0.5F, -0.9F, 1, 6, 1, 0.0F);
        this.Left_Wing2 = new AdvancedModelBox(this, 24, 10);
        this.Left_Wing2.mirror = true;
        this.Left_Wing2.setPos(1.4F, -5.0F, -0.1F);
        this.Left_Wing2.addBox(-1.8F, -0.5F, 0.5F, 3, 10, 1, 0.0F);
        this.setRotateAngle(this.Left_Wing2, 0.5235987755982988F, 0.01832595714594046F, -1.0471975511965976F);
        this.Left_Arm = new AdvancedModelBox(this, 0, 17);
        this.Left_Arm.setPos(1.8F, -7.0F, 0.0F);
        this.Left_Arm.addBox(-0.6F, -0.5F, -0.9F, 1, 6, 1, 0.0F);
        this.setRotateAngle(this.Left_Arm, 0.0F, 0.0F, -0.17453292519943295F);
        this.Body.addChild(this.Neck);
        this.Body.addChild(this.Right_Arm);
        this.Body.addChild(this.Right_Wing2);
        this.Body.addChild(this.Right_Wing);
        this.Body.addChild(this.Right_Leg);
        this.Body.addChild(this.Dress);
        this.Body.addChild(this.Head);
        this.Body.addChild(this.Left_Wing);
        this.Body.addChild(this.Left_Leg);
        this.Body.addChild(this.Left_Wing2);
        this.Body.addChild(this.Left_Arm);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.Body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.Body, this.Left_Arm, this.Head, this.Right_Arm, this.Neck, this.Left_Leg, this.Right_Leg, this.Left_Wing,
                this.Left_Wing2, this.Right_Wing, this.Right_Wing2, this.Dress);
    }

    @Override
    public void setAngles(EntityPixie entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.resetToDefaultPose();
        float speed_fly = 1.1F;
        float speed_idle = 0.05F;
        float degree_fly = 1F;
        float degree_idle = 0.5F;
        AdvancedModelBox[] LEFT_WINGS = new AdvancedModelBox[]{this.Left_Wing, this.Left_Wing2};
        AdvancedModelBox[] RIGHT_WINGS = new AdvancedModelBox[]{this.Right_Wing, this.Right_Wing2};

        this.Left_Leg.rotateAngleX = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.0F * limbDistance * 0.5F;
        this.Right_Leg.rotateAngleX = MathHelper.cos(limbAngle * 0.6662F) * 1.0F * limbDistance * 0.5F;

        float f12 = limbDistance;
        if (f12 < 0.0F) {
            f12 = 0.0F;
        }
        if (f12 > Math.toRadians(20)) {
            f12 = (float) Math.toRadians(20);
        }
        this.Body.rotateAngleX = f12;
        this.Head.rotateAngleX -= f12;
        ItemStack itemstack = entity.getStackInHand(Hand.MAIN_HAND);
        if (!itemstack.isEmpty()) {

            this.faceTarget(headYaw, headPitch, 1, this.Head);
            this.Left_Arm.rotateAngleX += (float) Math.toRadians(-35);
            this.Right_Arm.rotateAngleX += (float) Math.toRadians(-35);
            this.Body.rotateAngleX += (float) Math.toRadians(10);
            this.Left_Leg.rotateAngleX += (float) Math.toRadians(-10);
            this.Right_Leg.rotateAngleX += (float) Math.toRadians(-10);
            this.Head.rotateAngleX += (float) Math.toRadians(-10);
        } else {
            this.Right_Arm.rotateAngleX = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.0F * limbDistance * 0.5F;
            this.Left_Arm.rotateAngleX = MathHelper.cos(limbAngle * 0.6662F) * 1.0F * limbDistance * 0.5F;
        }

        if (entity.isPixieSitting()) {
            this.Right_Arm.rotateAngleX -= ((float) Math.PI / 5F);
            this.Left_Arm.rotateAngleX -= ((float) Math.PI / 5F);
            this.Right_Leg.rotateAngleX = -1.4137167F;
            this.Right_Leg.rotateAngleY = ((float) Math.PI / 10F);
            this.Right_Leg.rotateAngleZ = 0.07853982F;
            this.Left_Leg.rotateAngleX = -1.4137167F;
            this.Left_Leg.rotateAngleY = -((float) Math.PI / 10F);
            this.Left_Leg.rotateAngleZ = -0.07853982F;
            this.Dress.rotateAngleX += (float) Math.toRadians(-50);
            this.Dress.rotationPointZ += 0.25F;
            this.Dress.rotationPointY += 0.35F;
            this.Left_Wing.rotateAngleZ = (float) Math.toRadians(-28);
            this.Right_Wing.rotateAngleZ = (float) Math.toRadians(28);
            this.Left_Wing2.rotateAngleZ = (float) Math.toRadians(-8);
            this.Right_Wing2.rotateAngleZ = (float) Math.toRadians(8);
        } else {
            this.chainWave(LEFT_WINGS, speed_fly, degree_fly * 0.75F, 1, animationProgress, 1);
            this.chainWave(RIGHT_WINGS, speed_fly, degree_fly * 0.75F, 1, animationProgress, 1);
        }

    }

    public void animateInHouse(BlockEntityPixieHouse house) {
        this.resetToDefaultPose();
        float speed_fly = 1.1F;
        float speed_idle = 0.05F;
        float degree_fly = 1F;
        float degree_idle = 0.5F;
        AdvancedModelBox[] LEFT_WINGS = new AdvancedModelBox[]{this.Left_Wing, this.Left_Wing2};
        AdvancedModelBox[] RIGHT_WINGS = new AdvancedModelBox[]{this.Right_Wing, this.Right_Wing2};

        this.Right_Arm.rotateAngleX -= ((float) Math.PI / 5F);
        this.Left_Arm.rotateAngleX -= ((float) Math.PI / 5F);
        this.Right_Leg.rotateAngleX = -1.4137167F;
        this.Right_Leg.rotateAngleY = ((float) Math.PI / 10F);
        this.Right_Leg.rotateAngleZ = 0.07853982F;
        this.Left_Leg.rotateAngleX = -1.4137167F;
        this.Left_Leg.rotateAngleY = -((float) Math.PI / 10F);
        this.Left_Leg.rotateAngleZ = -0.07853982F;
        this.Dress.rotateAngleX += (float) Math.toRadians(-50);
        this.Dress.rotationPointZ += 0.25F;
        this.Dress.rotationPointY += 0.35F;
        this.Left_Wing.rotateAngleZ = (float) Math.toRadians(-28);
        this.Right_Wing.rotateAngleZ = (float) Math.toRadians(28);
        this.Left_Wing2.rotateAngleZ = (float) Math.toRadians(-8);
        this.Right_Wing2.rotateAngleZ = (float) Math.toRadians(8);
    }

    public void animateInJar(boolean sitting, BlockEntityJar jar, float headRot) {
        this.resetToDefaultPose();
        float speed_fly = 1.1F;
        float speed_idle = 0.05F;
        float degree_fly = 1F;
        float degree_idle = 0.5F;
        AdvancedModelBox[] LEFT_WINGS = new AdvancedModelBox[]{this.Left_Wing, this.Left_Wing2};
        AdvancedModelBox[] RIGHT_WINGS = new AdvancedModelBox[]{this.Right_Wing, this.Right_Wing2};
        if (sitting) {
            this.Right_Arm.rotateAngleX -= ((float) Math.PI / 5F);
            this.Left_Arm.rotateAngleX -= ((float) Math.PI / 5F);
            this.Right_Leg.rotateAngleX = -1.4137167F;
            this.Right_Leg.rotateAngleY = ((float) Math.PI / 10F);
            this.Right_Leg.rotateAngleZ = 0.07853982F;
            this.Left_Leg.rotateAngleX = -1.4137167F;
            this.Left_Leg.rotateAngleY = -((float) Math.PI / 10F);
            this.Left_Leg.rotateAngleZ = -0.07853982F;
            this.Dress.rotateAngleX += (float) Math.toRadians(-50);
            this.Dress.rotationPointZ += 0.25F;
            this.Dress.rotationPointY += 0.35F;
            this.Left_Wing.rotateAngleZ = (float) Math.toRadians(-28);
            this.Right_Wing.rotateAngleZ = (float) Math.toRadians(28);
            this.Left_Wing2.rotateAngleZ = (float) Math.toRadians(-8);
            this.Right_Wing2.rotateAngleZ = (float) Math.toRadians(8);
        } else if (jar != null) {
            float partialTicks = MinecraftClient.getInstance().getTickDelta();
            this.chainWave(LEFT_WINGS, speed_fly, degree_fly * 0.75F, 1, jar.ticksExisted + partialTicks, 1);
            this.chainWave(RIGHT_WINGS, speed_fly, degree_fly * 0.75F, 1, jar.ticksExisted + partialTicks, 1);
        }
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
